package com.github.jaaa.select;

import com.github.jaaa.CompareRandomAccessor;
import com.github.jaaa.fn.EntryConsumer;
import com.github.jaaa.fn.EntryFn;
import com.github.jaaa.util.Progress;

import java.io.IOException;
import java.nio.file.Files;
import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.atomic.AtomicInteger;

import static com.github.jaaa.misc.RandomShuffle.shuffle;
import static com.github.jaaa.util.Sampling.lhs;
import static java.lang.Math.round;
import static java.lang.Runtime.getRuntime;
import static java.lang.String.format;
import static java.lang.System.*;
import static java.util.Arrays.stream;
import static java.util.Map.entry;
import static java.util.stream.Collectors.joining;
import static java.util.stream.IntStream.range;


// -ea -Xmx48g -XX:MaxInlineLevel=15
public class SelectComparison
{
// STATIC FIELDS
  private static final int N_SAMPLES_DEFAULT = 8192;

// STATIC CONSTRUCTOR
  static {
    boolean ea = false;
    assert  ea = true;
      if( ! ea ) throw new AssertionError("Assertions not enabled.");
  }

  private interface SelectFn<T> {
    void select( T arr, int l, int m, int r );
  }

  private static final class CountingAccessor implements CompareRandomAccessor<int[]>
  {
    public long nComps = 0,
                nSwaps = 0;
    @Override public int compare( int[] a, int i, int[] b, int j ) {
      ++nComps;
      return Integer.compare(a[i], b[j]);
    }
    @Override public void copy( int[] a, int i, int[] b, int j ) {
      b[j] = a[i];
      ++nSwaps;
    }
    @Override
    public void copyRange( int[] a, int i, int[] b, int j, int len ) {
      arraycopy(a,i, b,j, len);
      nSwaps += len;
    }
    @Override public int[] malloc( int len ) { return new int[len]; }
    @Override public void swap( int[] a, int i, int[] b, int j ) {
      var ai = a[i];
               a[i] = b[j];
                      b[j] = ai;
      ++nSwaps;
    }
  }

  private static <T> Map<String, SelectFn<T>> selectors( CompareRandomAccessor<T> acc ) {
    return Map.ofEntries(
      entry("HeapV1", (arr,l,m,r) ->
        new HeapSelectV1V2Access() {
          @Override public void   swap( int i, int j ) {        acc.   swap(arr,i, arr,j); }
          @Override public int compare( int i, int j ) { return acc.compare(arr,i, arr,j); }
        }.heapSelectV1(l,m,r)
      ),
      entry("HeapV2", (arr,l,m,r) ->
        new HeapSelectV1V2Access() {
          @Override public void   swap( int i, int j ) {        acc.   swap(arr,i, arr,j); }
          @Override public int compare( int i, int j ) { return acc.compare(arr,i, arr,j); }
        }.heapSelectV2(l,m,r)
      ),
      entry("HeapV3", (arr,l,m,r) ->
        new HeapSelectV3V4Access() {
          @Override public void   swap( int i, int j ) {        acc.   swap(arr,i, arr,j); }
          @Override public int compare( int i, int j ) { return acc.compare(arr,i, arr,j); }
        }.heapSelectV3(l,m,r)
      ),
      entry("HeapV4", (arr,l,m,r) ->
        new HeapSelectV3V4Access() {
          @Override public void   swap( int i, int j ) {        acc.   swap(arr,i, arr,j); }
          @Override public int compare( int i, int j ) { return acc.compare(arr,i, arr,j); }
        }.heapSelectV4(l,m,r)
      ),
      entry("QuickV1", (arr,l,m,r) ->
        new QuickSelectV1Access() {
          @Override public void   swap( int i, int j ) {        acc.   swap(arr,i, arr,j); }
          @Override public int compare( int i, int j ) { return acc.compare(arr,i, arr,j); }
        }.quickSelectV1(l,m,r)
      ),
      entry("QuickV2", (arr,l,m,r) ->
        new QuickSelectV2Access() {
          @Override public void   swap( int i, int j ) {        acc.   swap(arr,i, arr,j); }
          @Override public int compare( int i, int j ) { return acc.compare(arr,i, arr,j); }
        }.quickSelectV2(l,m,r)
      )
//      entry("InsertionSelect", (arr,l,m,r) ->
//        new InsertionSelectAccess() {
//          @Override public void   swap( int i, int j ) {        acc.   swap(arr,i, arr,j); }
//          @Override public int compare( int i, int j ) { return acc.compare(arr,i, arr,j); }
//        }.insertionSelect(l,m,r)
//      ),
//     entry("MergeSelect", (arr,l,m,r) ->
//       new MergeSelectAccessor<T>() {
//         @Override public T malloc( int len ) { return acc.malloc(len); }
//         @Override public void swap( T a, int i, T b, int j ) { acc.swap(a,i, b,j); }
//         @Override public void copy( T a, int i, T b, int j ) { acc.swap(a,i, b,j); }
//         @Override public void copyRange( T a, int i, T b, int j, int len ) { acc.copyRange(a,i, b,j, len); }
//         @Override public int compare( T a, int i, T b, int j ) { return acc.compare(a,i, b,j); }
//       }.mergeSelect(arr,l,m,r, null,0,0)
//     ),
//      entry("MergeSelectStable", (arr,l,m,r) ->
//        new MergeSelectStableAccessor<T>() {
//          @Override public T malloc( int len ) { return acc.malloc(len); }
//          @Override public void swap( T a, int i, T b, int j ) { acc.swap(a,i, b,j); }
//          @Override public void copy( T a, int i, T b, int j ) { acc.swap(a,i, b,j); }
//          @Override public void copyRange( T a, int i, T b, int j, int len ) { acc.copyRange(a,i, b,j, len); }
//          @Override public int compare( T a, int i, T b, int j ) { return acc.compare(a,i, b,j); }
//        }.mergeSelectStable(arr,l,m,r, null,0,0)
//      ),
//      entry("MergeSort", (arr,l,m,r) ->
//        new MergeSortAccessor<T>() {
//          @Override public T malloc( int len ) { return acc.malloc(len); }
//          @Override public void swap( T a, int i, T b, int j ) { acc.swap(a,i, b,j); }
//          @Override public void copy( T a, int i, T b, int j ) { acc.swap(a,i, b,j); }
//          @Override public void copyRange( T a, int i, T b, int j, int len ) { acc.copyRange(a,i, b,j, len); }
//          @Override public int compare( T a, int i, T b, int j ) { return acc.compare(a,i, b,j); }
//        }.mergeSort(arr,l,r, null,0,0)
//      )
    );
  }

// STATIC METHODS
  public static void main( String... args ) throws IOException
  {
    out.println( System.getProperty("java.vm.name") + " " + System.getProperty("java.vm.version") );
    out.println( "Java " + System.getProperty("java.version") );

    for( int len: new int[]{10_000, 100_000, 1_000_000, 10_000_000, 100_000_000} )
    {
      out.printf("len: %d\n", len);
      compare_over_split(len);
//      compare_over_both (len);
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
  private static void compare_over_split( final int length ) throws IOException
  {
    int N_SAMPLES = N_SAMPLES_DEFAULT;
    var rng = new SplittableRandom();

    double[] x = rng.ints(N_SAMPLES, 0,length+1).asDoubleStream().toArray();
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

    plot2d(format("Select comparisons Benchmark (L = %d)", length), "Split", x, "#comparisons", resultsComps);
    plot2d(format(      "Select swaps Benchmark (L = %d)", length), "Split", x, "#swaps",       resultsSwaps);
    plot2d(format(    "Select timings Benchmark (L = %d)", length), "Split", x, "Time [msec.]", resultsTimes);
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

    plot2d(format("Select comparisons Benchmark (split = %.2f)", splitRatio), "Length", x, "#comparisons", resultsComps);
    plot2d(format(       "Select swap Benchmark (split = %.2f)", splitRatio), "Length", x, "#swaps",       resultsSwaps);
    plot2d(format(    "Select timings Benchmark (split = %.2f)", splitRatio), "Length", x, "Time [msec.]", resultsTimes);
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

//  private static void plot2d( String title, String x_label, double[] x, String y_label, Map<String,double[]> ys ) throws IOException
//  {
//    var colorList = List.of(
//      "#e41a1c", "#377eb8", "#4daf4a", "#984ea3", "#ff7f00", "#ffff33", "#a65628", "#f781bf", "#999999"
//    );
//    var col = new AtomicInteger();
//
//    String[] data = ys.entrySet().stream().map( EntryFn.of(
//      (method,y) -> {
//        var color = colorList.get( col.getAndIncrement() );
//        return format(
//          """
//          {
//            type: 'scattergl',
//            mode: 'markers',
//            name: '%s',
//            marker: {
//              color: '%s',
//              size: 4,
//              opacity: 0.25
//            },
//            x: %s,
//            y: %s
//          }
//          """,
//          method,
//          color,
//          Arrays.toString(x),
//          Arrays.toString(y)
//        );
//      }
//    )).toArray(String[]::new);
//
//    String layout = format(
//      """
//      {
//        title: '%s',
//        xaxis: {title: '%s'},
//        yaxis: {title: '%s'},
//        paper_bgcolor: 'black',
//        plot_bgcolor: 'black'
//      }
//      """,
//      title,
//      x_label,
//      y_label
//    );
//
//    PlotlyUtils.plot(layout, data);
//  }

  private static void plot2d( String title, String x_label, double[] x, String y_label, Map<String,double[]> ys ) throws IOException
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

          data = data.flatMap( data => {
            let fitFns = function(){
              switch(data.name) {
                default:
                  throw new Error(data.name);
                case 'QuickV1':
                case 'QuickV2':
                  return [(m,n) => 1];
                case 'HeapV1':
                case 'HeapV3':
                  return [
                    (m,n) => m,
                    (m,n) => n,
                    (m,n) => Math.log2(m+1) * m*n / (m+n),
                  ];
                case 'HeapV2':
                case 'HeapV4':
                  return [
                    (m,n) => m,
                    (m,n) => n,
                    (m,n) => Math.log2(n+1) * m*n / (m+n),
                  ];
              }
            }();

            fitFns = fitFns.map(
              f => ([x,y]) => f(Math.min(x,y),Math.max(x,y))
            );

            const xMax = data.x.reduce((x,y) => Math.max(x,y)),
                  yMax = data.y.reduce((x,y) => Math.max(x,y)),
               train_X = data.x.map( x => [x,xMax-x] ),
               train_y = data.y;

            const fit = nd.opt.fit_lin(train_X, train_y, fitFns);

            console.log({[data.name]: fit.coeffs})

            const x = [...nd.iter.linspace(0,xMax,1000)]
            const y = x.map( x => fit([x,xMax-x]) )

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
    Files.writeString( tmp, format(PLOT_TEMPLATE, layout, dat) );
    var cmd = format("xdg-open %s", tmp);
    getRuntime().exec(cmd);
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
            size: 4,
            opacity: 0.25,
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
    getRuntime().exec( format("xdg-open %s", tmp) );
  }
}
