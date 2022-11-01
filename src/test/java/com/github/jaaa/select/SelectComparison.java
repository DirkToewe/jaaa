package com.github.jaaa.select;

import com.github.jaaa.CompareSwapAccess;
import com.github.jaaa.Swap;
import com.github.jaaa.fn.EntryConsumer;
import com.github.jaaa.fn.EntryFn;
import com.github.jaaa.util.PlotlyUtils;
import com.github.jaaa.util.Progress;
import smile.math.kernel.GaussianKernel;
import smile.regression.GaussianProcessRegression;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.stream.Stream;

import static com.github.jaaa.misc.RandomShuffle.shuffle;
import static com.github.jaaa.util.Sampling.lhs;
import static java.awt.Desktop.getDesktop;
import static java.lang.Math.round;
import static java.lang.String.format;
import static java.lang.System.nanoTime;
import static java.lang.System.out;
import static java.util.Arrays.stream;
import static java.util.Map.entry;
import static java.util.stream.Collectors.joining;
import static java.util.stream.IntStream.range;


// -ea -Xmx16g -XX:MaxInlineLevel=15
public class SelectComparison
{
// STATIC FIELDS

// STATIC CONSTRUCTOR
  static {
    boolean ea = false;
    assert  ea = true;
      if( ! ea ) throw new AssertionError("Assertions not enabled.");
  }

  private interface SelectFn
  {
    void select( int l, int m, int r, CompareSwapAccess acc );
  }

  private static final class CountingAccess implements CompareSwapAccess
  {
  // FIELDS
    private final int[] arr;
    public long nComps = 0,
                nSwaps = 0;
  // CONSTRUCTORS
    public CountingAccess( int[] _arr ) { arr=_arr; }
  // METHODS
    @Override public int     compare( int i, int j ) { nComps++; return Integer.compare(arr[i],arr[j]); }
    @Override public void       swap( int i, int j ) { nSwaps++; Swap.swap(arr,i,j);    }
  }

// STATIC METHODS
  public static void main( String... args ) throws IOException
  {
    out.println( System.getProperty("java.vm.name") + " " + System.getProperty("java.vm.version") );
    out.println( "Java " + System.getProperty("java.version") );

    Map<String,SelectFn> selectors = Map.ofEntries(
      entry("HeapV1Acc", (l,m,r,acc) ->
        new HeapSelectV1V2Access() {
          @Override public void   swap( int i, int j ) {        acc.   swap(i,j); }
          @Override public int compare( int i, int j ) { return acc.compare(i,j); }
        }.heapSelectV1(l,m,r)
      ),
      entry("HeapV2Acc", (l,m,r,acc) ->
        new HeapSelectV1V2Access() {
          @Override public void   swap( int i, int j ) {        acc.   swap(i,j); }
          @Override public int compare( int i, int j ) { return acc.compare(i,j); }
        }.heapSelectV2(l,m,r)
      ),
      entry("HeapV3Acc", (l,m,r,acc) ->
        new HeapSelectV3V4Access() {
          @Override public void   swap( int i, int j ) {        acc.   swap(i,j); }
          @Override public int compare( int i, int j ) { return acc.compare(i,j); }
        }.heapSelectV3(l,m,r)
      ),
      entry("HeapV4Acc", (l,m,r,acc) ->
        new HeapSelectV3V4Access() {
          @Override public void   swap( int i, int j ) {        acc.   swap(i,j); }
          @Override public int compare( int i, int j ) { return acc.compare(i,j); }
        }.heapSelectV4(l,m,r)
      ),
      entry("QuickV1Acc", (l,m,r,acc) ->
        new QuickSelectV1Access() {
          @Override public void   swap( int i, int j ) {        acc.   swap(i,j); }
          @Override public int compare( int i, int j ) { return acc.compare(i,j); }
        }.quickSelectV1(l,m,r)
      )
      , entry("QuickV2Acc", (l,m,r,acc) ->
        new QuickSelectV2Access() {
          @Override public void   swap( int i, int j ) {        acc.   swap(i,j); }
          @Override public int compare( int i, int j ) { return acc.compare(i,j); }
        }.quickSelectV2(l,m,r)
      )
    );

    for( int len: new int[]{100_000, 1_000_000} )
    {
      out.printf("len: %d\n", len);
      compare_over_split(selectors, len);
      compare_over_both (selectors, len);
    }
//    for( var split: new double[]{ 0.5 } )//, 0.25, 0.75 } )
//      compare_over_length(selectors, 1_000_000, split);
  }

  /** Compares merging algorithms using merge sequences of constant combined length but
   *  with a different splits into left and right merge sqeuence length. In other words:
   *  given two merge sequences a and b, the sequence lengths |a| and |b| must always add
   *  up to length, i.e. |a|+|b| = length.
   *
   *  @param length The combined length of the tested merge sequences.
   */
  private static void compare_over_split( Map<String,SelectFn> selectors, final int length ) throws IOException
  {
    int N_SAMPLES = 2048;
    var rng = new SplittableRandom();

    double[] x = rng.ints(N_SAMPLES, 0,length+1).asDoubleStream().toArray();
    Arrays.sort(x);

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
    Entry<String,SelectFn>[] selectorsArr = selectors.entrySet().toArray(Entry[]::new);

    var      order = range(0,N_SAMPLES).toArray();
    shuffle(order,rng::nextInt);
    Progress.print( stream(order) ).forEach(i -> {
      int split = (int) x[i];

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
      stream(selectorsArr).forEach( EntryConsumer.of( (k, v) -> {
        var tst = ref.clone();
        var acc = new CountingAccess(tst);

        long t0 = nanoTime();
        v.select(0,split,length, acc);
        long dt = nanoTime() - t0;

        resultsComps.get(k)[i] = acc.nComps;
        resultsSwaps.get(k)[i] = acc.nSwaps;
        resultsTimes.get(k)[i] = dt / 1e3;

        assert stream(tst,0,split       ).max().orElse(Integer.MIN_VALUE)
            <= stream(tst,  split,length).min().orElse(Integer.MAX_VALUE);
      }));
    });

    plot2d(format("Select comparisons Benchmark (L = %d)", length), "Split", x, "#comparisons", resultsComps);
    plot2d(format(      "Select swaps Benchmark (L = %d)", length), "Split", x, "#swaps",       resultsSwaps);
    plot2d(format(    "Select timings Benchmark (L = %d)", length), "Split", x, "Time [msec.]", resultsTimes);
  }

  /** Compares merging algorithms using merge sequences of constant split but varying combined
   *  lengths. In other words: Given two merge sequences a and b of combined length len, then
   *  the sequence length are |a| = split*len and |b| = (1-split)*len.
   */
  private static void compare_over_length( Map<String,SelectFn> selectors, final int max_length, final double splitRatio ) throws IOException
  {
    int N_SAMPLES = 10_000;
    var rng = new SplittableRandom();

    var x = rng.ints(N_SAMPLES, 0,max_length+1).asDoubleStream().toArray();
    Arrays.sort(x);

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
    Entry<String,SelectFn>[] selectorsArr = selectors.entrySet().toArray(Entry[]::new);

    var      order = range(0,N_SAMPLES).toArray();
    shuffle(order,rng::nextInt);
    Progress.print( stream(order) ).forEach( i -> {
      final int length = (int) x[i],
                 split = (int) round(length*splitRatio);

      int[] ref = new int[length];
      for( int j=0; ++j < length; )
        ref[j] = ref[j-1] + rng.nextInt(2);
      shuffle(ref, rng::nextInt);

//      int[] ref = new int[length];
//      for( int j=0; ++j < length; )
//        ref[j] = ref[j-1] + rng.nextInt(2);

//      int[] ref = new int[length];
//      for( int j=0; ++j < length; )
//        ref[j] = ref[j-1] - rng.nextInt(2);

      shuffle(selectorsArr, rng::nextInt);
      stream(selectorsArr).forEach( EntryConsumer.of( (k,v) -> {
        var tst = ref.clone();
        var acc = new CountingAccess(tst);

        long t0 = nanoTime();
        v.select(0,split,length, acc);
        long dt = nanoTime() - t0;

        resultsComps.get(k)[i] = acc.nComps;
        resultsSwaps.get(k)[i] = acc.nSwaps;
        resultsTimes.get(k)[i] = dt / 1e3;

        assert stream(tst,0,split       ).max().orElse(Integer.MIN_VALUE)
            <= stream(tst,  split,length).min().orElse(Integer.MAX_VALUE);
      }));
    });

    plot2d(format("Select comparisons Benchmark (split = %.2f)", splitRatio), "Length", x, "#comparisons", resultsComps);
    plot2d(format(       "Select swap Benchmark (split = %.2f)", splitRatio), "Length", x, "#swaps",       resultsSwaps);
    plot2d(format(    "Select timings Benchmark (split = %.2f)", splitRatio), "Length", x, "Time [msec.]", resultsTimes);
  }

  private static void compare_over_both( Map<String,SelectFn> selectors, final int max_length ) throws IOException
  {
    int N_SAMPLES = 10_000;
    var rng = new SplittableRandom();

    var samples = lhs(N_SAMPLES, 2);
    var x = stream(samples).mapToDouble( xy -> round(2 + xy[0]*(max_length-1)) ).toArray();
    var y = stream(samples).mapToDouble( xy -> round(2 + xy[1]*(max_length-1)) ).toArray();

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
    Entry<String,SelectFn>[] selectorsArr = selectors.entrySet().toArray(Entry[]::new);

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
        var tst = ref.clone();
        var acc = new CountingAccess(tst);

        long t0 = nanoTime();
        v.select(0,split,length, acc);
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

  private static void plot2d( String title, String x_label, double[] x, String y_label, Map<String,double[]> ys ) throws IOException
  {
    var colorList = List.of(
      "#e41a1c", "#377eb8", "#4daf4a", "#984ea3", "#ff7f00", "#ffff33", "#a65628", "#f781bf", "#999999"
    );
    var col = new AtomicInteger();

    String[] data = Progress.print( ys.entrySet().stream() ).flatMap(
      new Function<Entry<String, double[]>, Stream<String>>() {
        public  Stream<String> apply( Entry<String, double[]> entry ) { return apply(entry.getKey(), entry.getValue()); }
        private Stream<String> apply( String method, double[] y )
        {
          var X = stream(x).mapToObj( x -> new double[]{x} ).toArray(double[][]::new);
          var color = colorList.get( col.getAndIncrement() );

          var scatter2d = format(
            """
            {
              type: 'scattergl',
              mode: 'markers',
              name: '%s',
              marker: {
                color: '%s',
                size: 5,
                opacity: 0.2
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

          var avg = stream(y).average().getAsDouble();
          var var = stream(y).map( x -> { x-=avg; return x*x; } ).average().getAsDouble();

          var gpr = GaussianProcessRegression.fit(
            X,y.clone(), new GaussianKernel(8),
            /*noise=*/var / 1e9, /*normalize=*/true, /*tol=*/1e-8, /*maxIter=*/1024
          );

          var fit = format(
            """
            {
              type: 'scattergl',
              mode: 'lines',
              name: '%s (fit)',
              line: {
                color: '%s',
                width: 2
              },
              x: %s,
              y: %s
            }
            """,
            method,
            color,
            Arrays.toString(x),
            stream(X).mapToDouble(gpr::predict).mapToObj(Double::toString).collect( joining(", ", "[", "]") )
          );

          return Stream.of(scatter2d, fit);
        }
      }
    ).toArray(String[]::new);

//    String[] data = ys.entrySet().stream().map( EntryFn.of(
//      (method,y) -> format(
//        """
//        {
//          type: 'scattergl',
//          mode: 'markers',
//          name: '%s',
//          marker: {
//            size: 4,
//            opacity: 0.6
//          },
//          x: %s,
//          y: %s
//        }
//        """,
//        method,
//        Arrays.toString(x),
//        Arrays.toString(y)
//      )
//    )).toArray(String[]::new);

    String layout = format(
      """
      {
        title: '%s',
        xaxis: {title: '%s'},
        yaxis: {title: '%s'},
        paper_bgcolor: 'black',
        plot_bgcolor: 'black'
      }
      """,
      title,
      x_label,
      y_label
    );

    Path tmp = Files.createTempFile(title+" ", ".html");
    PlotlyUtils.plot(tmp, layout, data);
    getDesktop().browse(tmp.toUri());
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
            size: 2,
            opacity: 0.2,
            line: {
              width: 0.5,
              color: 'darkgray'
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

    String layout = format(
      """
      {
        title: '%s',
        scene: {
          xaxis: {title: '%s'},
          yaxis: {title: '%s'},
          zaxis: {title: '%s'}
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
              pts.marker.size = 3;
              pts.marker.color = color;

              const     log2 = x => Math.ceil( Math.log2(x+1) ),
                sortedInputs = fn => ([x,y]) => fn( Math.min(x,y), Math.max(x,y) );

              const fit_fn = nd.opt.fit_lin(
                [...nd.iter.zip(pts.x, pts.y)],
                pts.z,
                [
                  sortedInputs( (x,y) => x ),
                  sortedInputs( (x,y) => y ),
                  sortedInputs( (x,y) => log2(x)*y ),
                  sortedInputs( (x,y) => log2(y)*x )
                ]
              );

              console.log({name: pts.name, fit_fn})

              const xMin = pts.x.reduce( (x,y) => Math.min(x,y) ), xMax = pts.x.reduce( (x,y) => Math.max(x,y) ),
                    yMin = pts.y.reduce( (x,y) => Math.min(x,y) ), yMax = pts.y.reduce( (x,y) => Math.max(x,y) );

              const x = [],
                    y = [],
                    z = [];

              for( const xi of nd.iter.linspace(xMin,xMax,20) )
              {
                for( const yi of nd.iter.linspace(yMin,yMax,1000) )
                {
                  x.push(xi);
                  y.push(yi);
                  z.push( fit_fn([xi,yi]) );
                }
                [x,y,z].forEach( arr => arr.push(NaN) );
              }
              for( const yi of nd.iter.linspace(yMin,yMax,20) )
              {
                for( const xi of nd.iter.linspace(xMin,xMax,1000) )
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

              //const nx = 100,
              //      ny = 100;
              //for( const xi of nd.iter.linspace(xMin,xMax,nx) )
              //for( const yi of nd.iter.linspace(yMin,yMax,ny) )
              //{
              //  x.push(xi);
              //  y.push(yi);
              //  z.push( fit_fn([xi,yi]) );
              //}
              //
              //const i = [],
              //      j = [],
              //      k = [];
              //
              //for( let a=nx; --a > 0; )
              //for( let b=ny; --b > 0; )
              //{
              //  i.push( ny* a   + b   , ny*(a-1) +(b-1) )
              //  j.push( ny*(a-1)+ b   , ny* a    +(b-1) )
              //  k.push( ny*(a-1)+(b-1), ny* a    + b    )
              //}
              //
              //const fit = {
              //  type: 'mesh3d',
              //  name: pts.name + ' (fit)',
              //  color,
              //  opacity: 0.4,
              //  showlegend: true,
              //  usecolormap: false,
              //  x,y,z,
              //  i,j,k
              //};

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
    getDesktop().browse(tmp.toUri());
  }
}
