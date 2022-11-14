package com.github.jaaa.select;

import com.github.jaaa.fn.EntryConsumer;
import com.github.jaaa.fn.EntryFn;
import com.github.jaaa.select.SelectComparisonOverSplit.CountingAccessor;
import com.github.jaaa.select.SelectComparisonOverSplit.SelectFn;
import com.github.jaaa.util.Progress;

import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.Map;
import java.util.Map.Entry;
import java.util.SplittableRandom;
import java.util.TreeMap;

import static com.github.jaaa.misc.RandomShuffle.shuffle;
import static com.github.jaaa.select.SelectComparisonOverSplit.selectors;
import static com.github.jaaa.util.Sampling.lhs;
import static java.lang.Math.round;
import static java.lang.Runtime.getRuntime;
import static java.lang.String.format;
import static java.lang.System.nanoTime;
import static java.lang.System.out;
import static java.util.Arrays.stream;
import static java.util.stream.Collectors.joining;
import static java.util.stream.IntStream.range;


// -ea -Xmx16g -XX:MaxInlineLevel=15
public class SelectComparisonOverBoth
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

    for( int len: new int[]{ 1_000_000 } )
    {
      out.printf("len: %d\n", len);
      compare_over_both(len);
    }
  }

  private static void compare_over_both( final int max_length ) throws IOException
  {
    int N_SAMPLES = N_SAMPLES_DEFAULT;
    var rng = new SplittableRandom();

    var samples = lhs(N_SAMPLES, 2);
    var x = stream(samples).mapToDouble( xy -> round(2 + xy[0]*(max_length-1)) ).toArray();
    var y = stream(samples).mapToDouble( xy -> round(2 + xy[1]*(max_length-1)) ).toArray();

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
    shuffle(order,rng::nextInt);
    Progress.print( stream(order) ).forEach( i -> {
      final int split = (int) x[i],
               length = (int)(x[i] + y[i]);

      int[] ref = new int[length];
      for( int j=0; ++j < length; )
        ref[j] = ref[j-1] + rng.nextInt(2);
      shuffle(ref, rng::nextInt);

//      int[] ref = new int[length];
//      for( int j=0; ++j < length; )
//        ref[j] = ref[j-1] + rng.nextInt(2);

//      int[] ref = new int[length];
//      for( int j=0; ++j < length; )
//        ref[j] = ref[j-1] - 1;//rng.nextInt(2);

      shuffle(selectorsArr, rng::nextInt);
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

    plot3d("Select comparisons Benchmark", "Left Length", x, "Right Length", y, "#comparisons", resultsComps);
    plot3d(       "Select swap Benchmark", "Left Length", x, "Right Length", y, "#swaps",       resultsSwaps);
    plot3d(    "Select timings Benchmark", "Left Length", x, "Right Length", y, "Time [msec.]", resultsTimes);
  }

  private static void plot3d( String title, String x_label, double[] x, String y_label, double[] y, String z_label, Map<String,double[]> zs ) throws IOException
  {
    String data = zs.entrySet().stream().map( EntryFn.of(
      (method,z) -> format(
        """
        {
          type: 'scatter3d',
          mode: 'markers',
          name: '%s',
          marker: {
            size: 1.5,
            opacity: 0.8,
            line: {
              width: 0.5,
              color: 'black'
            }
          },
          x: %s,
          y: %s,
          z: %s
        }
        """,
        method,
        Arrays.toString(x),
        Arrays.toString(y),
        Arrays.toString(z)
      )
    )).collect( joining(",\n", "[", "]") );

/*
        title: '%s',
        xaxis: {
          title: '%s',
          color: 'lightgray',
          gridcolor: '#333'
        },
        yaxis: {
          title: '%s',
          color: 'lightgray', gridcolor: '#333'
        },
        paper_bgcolor: 'black',
         plot_bgcolor: 'black',
        legend: {
          font: { color: 'lightgray' }
        },
        font: { color: 'lightgray' }
        */

    String layout = format(
      """
      {
        title: '%s',
        paper_bgcolor: 'black',
         plot_bgcolor: 'black',
        legend: {
          font: { color: 'lightgray' }
        },
        font: { color: 'lightgray' },
        scene: {
          xaxis: {title: '%s', color: 'lightgray', gridcolor: '#333' },
          yaxis: {title: '%s', color: 'lightgray', gridcolor: '#333' },
          zaxis: {title: '%s', color: 'lightgray', gridcolor: '#333' }
        }
      }
      """,
      title,
      x_label,
      y_label,
      z_label
    );

    String PLOT_TEMPLATE = """
      <!DOCTYPE html>
      <html lang="en">
        <head>
          <meta charset="utf-8">
          <script src="https://cdn.plot.ly/plotly-latest.js"></script>
          <script type="text/javascript" src="./nd.min.js"></script>
        </head>
        <body>
          <script>
          'use strict';

          (async () => {
            const plot = document.createElement('div');
            plot.style = 'width: 100%%; height: 95vh;';
            document.body.appendChild(plot);

            const dataRaw = %2$s,
                   layout = %1$s;
            if( 'title' in layout )
              document.title = layout.title;
            if( 'paper_bgcolor' in layout )
              document.body.style.background = layout.paper_bgcolor;

            const colors = [
              "#e41a1c",
              "#377eb8",
              "#4daf4a",
              "#984ea3",
              "#ff7f00",
              "#ffff33",
              "#a65628",
              "#f781bf",
              "#999999"
            ];

            const sleep = dt => new Promise(resolve => setTimeout(resolve,dt));

            const data = [];
            let col = 0;
            for( const pts of dataRaw )
            {
              const color = colors[col++ %% colors.length];
              pts.marker.color = color;

              const     log2 = x => Math.ceil( Math.log2(x+1) ),
                sortedInputs = fn => ([x,y]) => fn( Math.min(x,y), Math.max(x,y) );

              const   worstCase = (m,n) => Math.log2(m+1) * n;
              const averageCase = (m,n) => Math.log((m+n)/m) * Math.log2(m+1)*m;

              let fit_fns = function(){
                switch(pts.name) {
                  default:
                    throw new Error(data.name);
                  case 'MoM3 V1':
                  case 'MoM3 V2':
                  case 'MoM5 V1':
                  case 'MoM5 V2':
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

              fit_fns = fit_fns.map(
                f => ([x,y]) => {
                  const m = Math.min(x,y),
                        n = Math.max(x,y);
                  return m < 2 ? 0 : f(m,n)
                }
              );

              const fit_fn = nd.opt.fit_lin(
                [...nd.iter.zip(pts.x, pts.y)],
                pts.z,
                fit_fns
              );

              console.log({name: pts.name, fit_fn})

              const xMin = pts.x.reduce( (x,y) => Math.min(x,y) ), xMax = pts.x.reduce( (x,y) => Math.max(x,y) ),
                    yMin = pts.y.reduce( (x,y) => Math.min(x,y) ), yMax = pts.y.reduce( (x,y) => Math.max(x,y) );

              const x = [],
                    y = [],
                    z = [];

              const N = 17,
                  LOD = 10;

              for( const xi of nd.iter.linspace(xMin,xMax,N) )
              {
                for( const yi of nd.iter.linspace(yMin,yMax,N*LOD) )
                {
                  x.push(xi);
                  y.push(yi);
                  z.push( fit_fn([xi,yi]) );
                }
                [x,y,z].forEach( arr => arr.push(NaN) );
              }
              for( const yi of nd.iter.linspace(yMin,yMax,N) )
              {
                for( const xi of nd.iter.linspace(xMin,xMax,N*LOD) )
                {
                  x.push(xi);
                  y.push(yi);
                  z.push( fit_fn([xi,yi]) );
                }
                [x,y,z].forEach( arr => arr.push(NaN) );
              }

              const fit = {
                type: 'scatter3d',
                mode: 'lines',
                name: pts.name + ' (fit)',
                line: { color, width: 4 },
                x,y,z
              };

              await sleep();

              data.push(pts,fit);
            }
    
            await Plotly.plot(plot, {layout, data});
          })()
          </script>
        </body>
      </html>
    """;

    var tmp = Files.createTempFile("plot_",".html");
    Files.writeString( tmp, format(PLOT_TEMPLATE, layout, data) );
    getRuntime().exec( format("xdg-open %s", tmp) );
  }
}
