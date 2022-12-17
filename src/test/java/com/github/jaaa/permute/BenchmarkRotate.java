package com.github.jaaa.permute;

import com.github.jaaa.fn.EntryConsumer;
import com.github.jaaa.fn.EntryFn;
import com.github.jaaa.util.IMath;
import com.github.jaaa.util.PlotlyUtils;
import com.github.jaaa.util.Progress;

import java.io.IOException;
import java.util.*;
import java.util.Map.Entry;

import static com.github.jaaa.permute.RandomShuffle.randomShuffle;
import static com.github.jaaa.permute.RandomShuffle.shuffled;
import static java.lang.String.format;
import static java.lang.System.nanoTime;
import static java.util.Arrays.stream;
import static java.util.stream.IntStream.range;


// -ea -Xmx16g -XX:MaxInlineLevel=15
public class BenchmarkRotate
{
// STATIC FIELDS

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
    int N_SAMPLES = 10_000;
    int LEN = 1_000_000;
    SplittableRandom rng = new SplittableRandom(1337);

    int[] x = rng.ints(N_SAMPLES, 1,1+LEN).toArray();
    Arrays.sort(x);

    Map<String,RotFn> rotFns = new LinkedHashMap<>();
    rotFns.put("reverts", (acc, from,until, rot) -> {
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
    });
    rotFns.put("juggling", (acc, from,until, rot) -> {
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
    });
    rotFns.put("rolls", (acc, from,until, rot) -> {
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
    });
    rotFns.put("roll_L2R", (acc, from,until, rot) -> {
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
    });
    rotFns.put("roll_R2L", (acc, from,until, rot) -> {
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
    });

    Map<String,double[]>
      resultsSwaps = new TreeMap<>(),
      resultsTimes = new TreeMap<>();
    rotFns.forEach( (k,v) -> {
      resultsSwaps.put(k, new double[N_SAMPLES]);
      resultsTimes.put(k, new double[N_SAMPLES]);
    });

    @SuppressWarnings("unchecked")
    Entry<String,RotFn>[] rotFnsArr = rotFns.entrySet().stream().toArray(Entry[]::new);

    Progress.print( stream( shuffled( range(0,N_SAMPLES).toArray(), rng::nextInt ) ) ).forEach(i -> {
      int length = x[i];

      int[] sample = rng.ints(length).toArray();
      int      rot = rng.nextInt();

      randomShuffle(rotFnsArr, rng::nextInt);
      stream(rotFnsArr).forEach( EntryConsumer.of( (k,v) -> {
        int[] test = sample.clone();
        CountingSwapAccess acc = new CountingSwapAccess(test);

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
    String[] data = ys.entrySet().stream().map( EntryFn.of(
      (method,y) -> format(
        "{\n" +
        "  type: 'scattergl',\n" +
        "  mode: 'markers',\n" +
        "  marker: {\n" +
        "    size: 2,\n" +
        "    opacity: 0.4\n" +
        "  },\n" +
        "  name: '%s',\n" +
        "  x: %s,\n" +
        "  y: %s\n" +
        "}\n",
        method,
        Arrays.toString(x),
        Arrays.toString(y)
      )
    )).toArray(String[]::new);

    String layout = format(
      "{ title: '%s', xaxis: {title: '%s'}, yaxis: {title: '%s'} }",
      title,
      x_label,
      y_label
    );

    PlotlyUtils.plot(layout, data);
  }
}
