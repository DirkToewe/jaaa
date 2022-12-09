package com.github.jaaa.select;

import com.github.jaaa.fn.EntryConsumer;
import com.github.jaaa.fn.EntryFn;
import com.github.jaaa.select.SelectComparisonOverSplit.CountingAccessor;
import com.github.jaaa.select.SelectComparisonOverSplit.SelectFn;
import com.github.jaaa.util.Progress;

import java.io.IOException;
import java.nio.file.Files;
import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.atomic.AtomicInteger;

import static com.github.jaaa.permute.RandomShuffle.randomShuffle;
import static com.github.jaaa.select.SelectComparisonOverSplit.selectors;
import static java.lang.Math.round;
import static java.lang.Runtime.getRuntime;
import static java.lang.String.format;
import static java.lang.System.nanoTime;
import static java.lang.System.out;
import static java.util.Arrays.stream;
import static java.util.stream.Collectors.joining;
import static java.util.stream.IntStream.range;


// -ea -Xmx16g -XX:MaxInlineLevel=15
public class SelectComparisonOverLength
{
  private static final int N_SAMPLES_DEFAULT = 1<<14;

  static {
    boolean ea = false;
    assert  ea = true;
      if( ! ea ) throw new AssertionError("Assertions not enabled.");
  }

  public static void main( String... args ) throws IOException
  {
    out.println( System.getProperty("java.vm.name") + " " + System.getProperty("java.vm.version") );
    out.println( "Java " + System.getProperty("java.version") );

    for( var split: new double[]{ 0.5, 0.25, 0.75 } )
      compare_over_length(1_000_000, split);
//      compare_over_length(40_000_000, split);
  }

  /** Compares merging algorithms using merge sequences of constant split but varying combined
   *  lengths. In other words: Given two merge sequences a and b of combined length len, then
   *  the sequence length are |a| = split*len and |b| = (1-split)*len.
   */
  private static void compare_over_length( final int max_length, final double splitRatio ) throws IOException
  {
    int N_SAMPLES = N_SAMPLES_DEFAULT;
    var rng = new SplittableRandom();

    var x = rng.ints(N_SAMPLES, 0, max_length+1).asDoubleStream().toArray();
    Arrays.sort(x);

    var acc = new CountingAccessor();
    var selectors = selectors(acc);

    Map<String,double[]>
      resultsComps = new TreeMap<>(),
      resultsSwaps = new TreeMap<>(),
      resultsTimes = new TreeMap<>();
    selectors.forEach( (k,v) -> {
      resultsComps.put(k, new double[N_SAMPLES]);
      resultsSwaps.put(k, new double[N_SAMPLES]);
      resultsTimes.put(k, new double[N_SAMPLES]);
    });

    @SuppressWarnings("unchecked")
    Entry<String,SelectFn<int[]>>[] selectorsArr = selectors.entrySet().toArray(Entry[]::new);

    var      order = range(0,N_SAMPLES).toArray();
    randomShuffle(order,rng::nextInt);
    Progress.print( stream(order) ).forEach( i -> {
      final int length = (int) x[i],
                 split = (int) round(length*splitRatio);

      int[] ref = new int[length];
      for( int j=0; ++j < length; )
        ref[j] = ref[j-1] + rng.nextInt(2);
      randomShuffle(ref, rng::nextInt);

//      int[] ref = new int[length];
//      for( int j=0; ++j < length; )
//        ref[j] = ref[j-1] + rng.nextInt(2);

//      int[] ref = new int[length];
//      for( int j=0; ++j < length; )
//        ref[j] = ref[j-1] - rng.nextInt(2);

      randomShuffle(selectorsArr, rng::nextInt);
      stream(selectorsArr).forEach( EntryConsumer.of( (k,v) -> {
        acc.nComps = 0;
        acc.nSwaps = 0;
        var tst = ref.clone();

        long t0 = nanoTime();
        v.select(tst, 0, split, length);
        long dt = nanoTime() - t0;

        resultsComps.get(k)[i] = acc.nComps;
        resultsSwaps.get(k)[i] = acc.nSwaps;
        resultsTimes.get(k)[i] = dt / 1e3;

        assert stream(tst,0,split       ).max().orElse(Integer.MIN_VALUE)
            <= stream(tst,  split,length).min().orElse(Integer.MAX_VALUE);
      }));
    });

    plot2d(format("Select comparisons Benchmark (split = %.2f)", splitRatio), "Length", splitRatio, x, "#comparisons", resultsComps);
    plot2d(format(       "Select swap Benchmark (split = %.2f)", splitRatio), "Length", splitRatio, x, "#swaps",       resultsSwaps);
    plot2d(format(    "Select timings Benchmark (split = %.2f)", splitRatio), "Length", splitRatio, x, "Time [msec.]", resultsTimes);
  }

  private static void plot2d( String title, String x_label, double split, double[] x, String y_label, Map<String,double[]> ys ) throws IOException
  {
    var colorList = List.of(
      "#e41a1c", "#377eb8", "#4daf4a", "#984ea3", "#ff7f00", "#ffff33", "#a65628", "#f781bf", "#999999"
    );
    var col = new AtomicInteger();

    String[] data = ys.entrySet().stream().map( EntryFn.of(
      (method,y) -> {
        var color = colorList.get( col.getAndIncrement() );
        return format(
          """
          {
            type: 'scattergl',
            mode: 'markers',
            name: '%s',
            marker: {
              color: '%s',
              size: 2,
              opacity: 0.6
            },
            x: %s,
            y: %s
          }
          """,
          method,
          color,
          Arrays.toString(x),
          Arrays.toString(y)
        );
      }
    )).toArray(String[]::new);

    String layout = format(
      """
      {
        title: '%s',
        xaxis: {
          title: '%s',
          color: 'lightgray',
          gridcolor: '#333'
        },
        yaxis: {
          title: '%s',
          color: 'lightgray',
          gridcolor: '#333'
        },
        paper_bgcolor: 'black',
         plot_bgcolor: 'black',
        legend: {
          font: { color: 'lightgray' }
        },
        font: { color: 'lightgray' }
      }
      """,
      title,
      x_label,
      y_label
    );

    final String PLOT_TEMPLATE = """
    <!DOCTYPE html>
    <html lang="en">
      <head>
        <meta charset="utf-8">
        <script src="https://cdn.plot.ly/plotly-latest.js"></script>
        <script src="./nd.min.js"></script>
      </head>
      <body>
        <script>
          'use strict';

          const plot = document.createElement('div');
          plot.style = 'width: 100%%; height: 95vh;';
          document.body.appendChild(plot);

          const layout = %1$s;
          if( 'title' in layout )
            document.title = layout.title;

          if( 'paper_bgcolor' in layout )
            document.body.style.background = 'black';

          let data = %2$s;
          const splitRatio = %3$f;

          const worstCase   = (m,n) => Math.log2(m+1) * n;
          const averageCase = (m,n) => Math.log((m+n)/m) * Math.log2(m+1)*m;

          data = data.flatMap( data => {
            let fitFns = function(){
              switch(data.name) {
                default:
                  throw new Error(data.name);
                case 'MoM3 V1':
                case 'MoM3 V2':
                case 'MoM5 V1':
                case 'MoM5 V2':
                case 'MergeSelect':
                case 'QuickV1':
                case 'QuickV2':
                  return [(m,n) => m+n];
                case 'Heap':
                case 'HeapRandom':
                  return [(m,n) => 0];
                case 'HeapMajor':
                  return [
                    (m,n) => m,
                    (m,n) => n,
                    (m,n) =>   worstCase(n,m),
                    (m,n) => averageCase(n,m),
                  ];
                case 'HeapMinor':
                case 'HeapHybridV2':
                  return [
                    (m,n) => m,
                    (m,n) => n,
                    (m,n) =>   worstCase(m,n),
                    (m,n) => averageCase(m,n),
                  ];
              }
            }();

            fitFns = fitFns.map(
              f => ([x,y]) => {
                const m = Math.min(x,y),
                      n = Math.max(x,y);
                return m < 2 ? 0 : f(m,n)
              }
            );

            const xMax = data.x.reduce((x,y) => Math.max(x,y)),
                  yMax = data.y.reduce((x,y) => Math.max(x,y)),
               train_X = data.x.map( x => {
                 const split = Math.round(splitRatio*x);
                 return [split, x-split];
               }),
               train_y = data.y;

            const fit = nd.opt.fit_lin(train_X, train_y, fitFns);

            console.log({[data.name]: fit.coeffs})

            const x = [...nd.iter.linspace(2,xMax-2,1000)]
            const y = x.map( x => {
              const split = Math.round(splitRatio*x);
              return fit([split, x-split]);
            })

            const fitData = {
              x,y,
              name: data.name + " (fit)",
              mode: 'lines',
              type: 'scattergl',
              line: {
                color: data.marker.color
              }
            };
            return [data, fitData];
          });

          Plotly.plot(plot, {layout, data});
        </script>
      </body>
    </html>
    """;

    var tmp = Files.createTempFile("plot_",".html");
    var dat = stream(data).collect( joining(",\n", "[", "]") );
    Files.writeString( tmp, format(PLOT_TEMPLATE, layout, dat, split) );
    var cmd = format("xdg-open %s", tmp);
    getRuntime().exec(cmd);
  }
}
