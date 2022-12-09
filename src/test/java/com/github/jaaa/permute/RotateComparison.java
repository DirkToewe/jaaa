package com.github.jaaa.permute;

import com.github.jaaa.fn.EntryConsumer;
import com.github.jaaa.fn.EntryFn;
import com.github.jaaa.util.IMath;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Map;
import java.util.Map.Entry;
import java.util.SplittableRandom;
import java.util.TreeMap;
import java.util.concurrent.atomic.AtomicInteger;

import static com.github.jaaa.permute.RandomShuffle.randomShuffle;
import static com.github.jaaa.permute.RandomShuffle.shuffled;
import static java.awt.Desktop.getDesktop;
import static java.lang.String.format;
import static java.lang.System.nanoTime;
import static java.util.Arrays.stream;
import static java.util.stream.Collectors.joining;
import static java.util.stream.IntStream.range;


// -ea -Xmx16g -XX:MaxInlineLevel=15
public class RotateComparison
{
  // STATIC FIELDS
  private static final String PLOT_TEMPLATE = """
    <!DOCTYPE html>
    <html lang="en">
      <head>
        <meta charset="utf-8">
        <script src="https://cdn.plot.ly/plotly-latest.js"></script>
      </head>
      <body>
        <script>
          'use strict';

          const plot = document.createElement('div');
          plot.style = 'width: 100%%; height: 95vh;';
          document.body.appendChild(plot);

          const layout = %1$s;
          document.title = layout.title;

          Plotly.plot(plot, {layout, data: %2$s});
        </script>
      </body>
    </html>
  """;

  // STATIC CONSTRUCTOR
  static {
    boolean ea = false;
    assert  ea = true;
      if( ! ea ) throw new AssertionError("Assertions not enabled.");
  }

  private interface RotFn {
    void rotate( SwapAccess acc, int from, int until, int rot );
  }

  private static final class CountingSwapAccess implements SwapAccess
  {
    long nSwaps = 0;
    private final int[] arr;
    public CountingSwapAccess( int[] _arr ) { arr =_arr; }
    @Override public void swap( int i, int j ) {
      nSwaps++;
      Swap.swap(arr,i,j);
    }
  }

  // STATIC METHODS
  public static void main( String... args ) throws IOException
  {
    int N_SAMPLES = 1_000;
    int LEN = 100_000_000;
    var rng = new SplittableRandom(1337);

    int[] x = rng.ints(N_SAMPLES, 1,1+LEN).toArray();
    Arrays.sort(x);

    Map<String,RotFn> rotFns = Map.of(
      "reverts", (acc, from,until, rot) -> {
        if( from < 0     ) throw new IllegalArgumentException();
        if( from > until ) throw new IllegalArgumentException();

        int len = until - from;
        if( len <= 1 ) return;
            rot %= len;
        if( rot == 0 ) return;
            rot += len & -(rot>>>31);

        for( int i=from,     j=until   ; i < --j; i++ ) acc.swap(i,j);
        for( int i=from,     j=from+rot; i < --j; i++ ) acc.swap(i,j);
        for( int i=from+rot, j=until   ; i < --j; i++ ) acc.swap(i,j);
      },
      "juggling", (acc, from,until, rot) -> {
        if( from < 0    ) throw new IllegalArgumentException();
        if( from > until) throw new IllegalArgumentException();

        int len = until - from;
        if( len <= 1 ) return;
            rot %= len;
        if( rot == 0 ) return;
            rot += len & -(rot>>>31);

        // Juggling Algorithm
        // ------------------
        until = from + IMath.gcd(rot,len);
        for( int i=from; i < until; i++ )
          for( int j=i;; ) { // start swap cycle
            int k = j-rot;
                k += len & -(k-from>>>31);
            if( k == i ) break;
            acc.swap(j,j=k);
          }
      },
      "rolls", (acc, from,until, rot) -> {
        if( from <  0     ) throw new IllegalArgumentException();
        if( from >  until ) throw new IllegalArgumentException();
        if( from == until ) return;

        for(;;){ int len = until - from;
              rot %= len;
          if( rot == 0 ) return;
              rot += len & -(rot>>>31); // <- handles negative rot
          if( rot <= (len>>>1) )
          {
            for(;;) {
              int i = --until - rot;
              acc.swap(i,until);
              if( i == from ) break;
            }
            rot = -len;
          }
          else {
            rot = len-rot;
            for(;;) {
              int i = from + rot;
              if( i >= until ) break;
              acc.swap(from++,i);
            }
            rot = len;
          }
        }
      },
      "roll_L2R", (acc, from,until, rot) -> {
        if( from <  0     ) throw new IllegalArgumentException();
        if( from >  until ) throw new IllegalArgumentException();

        int    len = until - from;
        if( 0==len ) return;
        rot %= len;
        rot += len & -(rot>>>31); // <- handles negative rot

        while( rot != 0 ){
          rot = len-rot;
          for(;;) {
            int i = from + rot;
            if( i >= until ) break;
            acc.swap(from++,i);
          }
          rot  = len;
          rot %= len = until - from;
        }
      },
      "roll_R2L", (acc, from,until, rot) -> {
        if( from <  0     ) throw new IllegalArgumentException();
        if( from >  until ) throw new IllegalArgumentException();
        if( from == until ) return;

        for(;;){ int len = until - from;
              rot %= len;
          if( rot == 0 ) return;
              rot += len & -(rot>>>31); // <- handles negative rot
          for(;;) {
            int i = --until - rot;
            acc.swap(i,until);
            if( i == from ) break;
          }
          rot = -len;
        }
      }
    );

    Map<String,double[]>
      resultsSwaps = new TreeMap<>(),
      resultsTimes = new TreeMap<>();
    rotFns.forEach( (k,v) -> {
      resultsSwaps.put(k, new double[N_SAMPLES]);
      resultsTimes.put(k, new double[N_SAMPLES]);
    });

    @SuppressWarnings("unchecked")
    Entry<String,RotFn>[] rotFnsArr = rotFns.entrySet().toArray(Entry[]::new);

    var progress = new AtomicInteger(0);

    stream( shuffled( range(0,N_SAMPLES).toArray(), rng::nextInt ) ).forEach( i -> {
      int prog = progress.incrementAndGet();
      if( prog % 100 == 0 )
        System.out.printf("%5d / %d\n", prog, N_SAMPLES);

      int length = x[i];

      var sample = rng.ints(length).toArray();
      var rot = rng.nextInt();

      randomShuffle(rotFnsArr, rng::nextInt);
      stream(rotFnsArr).forEach( EntryConsumer.of( (k,v) -> {
        var test = sample.clone();
        var acc = new CountingSwapAccess(test);

        long t0 = nanoTime();
        v.rotate(acc, 0,test.length, rot);
        long dt = nanoTime() - t0;

        assert range(0,test.length).allMatch( h -> {
          int j = (h - rot % test.length) % test.length;
          if( j < 0 )
              j += test.length;
          return test[h] == sample[j];
        }): format("len: %d; rot: %d;", length, rot);

        resultsSwaps.get(k)[i] = acc.nSwaps;
        resultsTimes.get(k)[i] = dt / 1e3;
      }));
    });

    plot_results("Rotate Swaps Benchmark",   "length", "#swaps",       x, resultsSwaps);
    plot_results("Rotate Timings Benchmark", "length", "Time [msec.]", x, resultsTimes);
  }

  private static void plot_results( String title, String x_label, String y_label, int[] x, Map<String,double[]> ys ) throws IOException
  {
    String data = ys.entrySet().stream().map( EntryFn.of(
      (method,y) -> format(
        "{\n  type: 'scatter2d',\n  name: '%s',\n  x: %s,\n  y: %s\n}",
        method,
        Arrays.toString(x),
        Arrays.toString(y)
      )
    )).collect( joining(",", "[", "]") );

    String layout = format(
      "{ title: '%s', xaxis: {title: '%s'}, yaxis: {title: '%s'} }",
      title,
      x_label,
      y_label
    );

    Path tmp = Files.createTempFile(title+" ", ".html");
    Files.writeString(  tmp, format(PLOT_TEMPLATE, layout, data) );
    getDesktop().browse(tmp.toUri());
  }
}
