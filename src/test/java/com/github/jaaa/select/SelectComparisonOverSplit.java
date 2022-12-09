package com.github.jaaa.select;

import com.github.jaaa.compare.CompareRandomAccessor;
import com.github.jaaa.fn.EntryConsumer;
import com.github.jaaa.fn.EntryFn;
import com.github.jaaa.util.Progress;

import java.io.IOException;
import java.nio.file.Files;
import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.atomic.AtomicInteger;

import static com.github.jaaa.permute.RandomShuffle.randomShuffle;
import static java.lang.Runtime.getRuntime;
import static java.lang.String.format;
import static java.lang.System.*;
import static java.util.Arrays.stream;
import static java.util.Map.entry;
import static java.util.stream.Collectors.joining;
import static java.util.stream.IntStream.range;


// -ea -Xmx16g -XX:MaxInlineLevel=15
public class SelectComparisonOverSplit
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

    for( int len: new int[]{ 10_000, 100_000, 1_000_000, 10_000_000, 100_000_000 } )
    {
      out.printf("len: %d\n", len);
      compare_over_split(len);
    }
  }

  interface SelectFn<T> {
    void select( T arr, int l, int m, int r );
  }

  static final class CountingAccessor implements CompareRandomAccessor<int[]>
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

  static <T> Map<String, SelectFn<T>> selectors( CompareRandomAccessor<T> acc ) {
    return Map.ofEntries(
//      entry("HeapMajor", (arr,l,m,r) ->
//        new HeapSelectAccess() {
//          @Override public void   swap( int i, int j ) {        acc.   swap(arr,i, arr,j); }
//          @Override public int compare( int i, int j ) { return acc.compare(arr,i, arr,j); }
//        }.heapSelectMajor(l,m,r)
//      ),
//      entry("HeapMinor", (arr,l,m,r) ->
//        new HeapSelectAccess() {
//          @Override public void   swap( int i, int j ) {        acc.   swap(arr,i, arr,j); }
//          @Override public int compare( int i, int j ) { return acc.compare(arr,i, arr,j); }
//        }.heapSelectMinor(l,m,r)
//      ),
//      entry("HeapRandom", (arr,l,m,r) ->
//        new HeapSelectRandomAccess() {
//          @Override public void   swap( int i, int j ) {        acc.   swap(arr,i, arr,j); }
//          @Override public int compare( int i, int j ) { return acc.compare(arr,i, arr,j); }
//        }.heapSelectRandom(l,m,r)
//      ),
//      entry("MergeSelect", (arr,l,m,r) ->
//        new MergeSelectAccessor<T>() {
//          @Override public T malloc( int len ) { return acc.malloc(len); }
//          @Override public void   copy( T a, int i, T b, int j ) {        acc.   copy(a,i, b,j); }
//          @Override public void   swap( T a, int i, T b, int j ) {        acc.   swap(a,i, b,j); }
//          @Override public int compare( T a, int i, T b, int j ) { return acc.compare(a,i, b,j); }
//        }.mergeSelect(arr,l,m,r, null,0,0)
//      ),
//      entry("Heap", (arr,l,m,r) ->
//        new HeapSelectAccess() {
//          @Override public void   swap( int i, int j ) {        acc.   swap(arr,i, arr,j); }
//          @Override public int compare( int i, int j ) { return acc.compare(arr,i, arr,j); }
//        }.heapSelect(l,m,r)
//      ),
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
      ),
      entry("MoM5 V1", (arr,l,m,r) ->
        new Mom5SelectV1Access() {
          @Override public void   swap( int i, int j ) {        acc.   swap(arr,i, arr,j); }
          @Override public int compare( int i, int j ) { return acc.compare(arr,i, arr,j); }
        }.mom5SelectV1(l,m,r)
      ),
      entry("MoM5 V2", (arr,l,m,r) ->
        new Mom5SelectV2Access() {
          @Override public void   swap( int i, int j ) {        acc.   swap(arr,i, arr,j); }
          @Override public int compare( int i, int j ) { return acc.compare(arr,i, arr,j); }
        }.mom5SelectV2(l,m,r)
      ),
      entry("MoM3 V1", (arr,l,m,r) ->
        new Mom3SelectV1Access() {
          @Override public void   swap( int i, int j ) {        acc.   swap(arr,i, arr,j); }
          @Override public int compare( int i, int j ) { return acc.compare(arr,i, arr,j); }
        }.mom3SelectV1(l,m,r)
      ),
      entry("MoM3 V2", (arr,l,m,r) ->
        new Mom3SelectV2Access() {
          @Override public void   swap( int i, int j ) {        acc.   swap(arr,i, arr,j); }
          @Override public int compare( int i, int j ) { return acc.compare(arr,i, arr,j); }
        }.mom3SelectV2(l,m,r)
      )
    );
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

    var order = range(0,N_SAMPLES).toArray();
    randomShuffle(order,rng::nextInt);
    Progress.print( stream(order) ).forEach(i -> {
      int split = (int) x[i];

      int[] ref = new int[length];
      for( int j=0; ++j < length; )
        ref[j] = ref[j-1] + rng.nextInt(2);
      randomShuffle(ref, rng::nextInt);
//      Revert.revert(ref);

//      int[] ref = new int[length];
//      for( int j=0; ++j < length; )
//        ref[j] = ref[j-1] + rng.nextInt(2);

//      int[] ref = new int[length];
//      for( int j=0; ++j < length; )
//        ref[j] = ref[j-1] - 1;//rng.nextInt(2);

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

    plot2d(format("Select comparisons Benchmark (L = %d)", length), "Split", x, "#comparisons", resultsComps);
    plot2d(format(      "Select swaps Benchmark (L = %d)", length), "Split", x, "#swaps",       resultsSwaps);
    plot2d(format(    "Select timings Benchmark (L = %d)", length), "Split", x, "Time [msec.]", resultsTimes);
  }

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

          const worstCase   = (m,n) => Math.log2(m+1) * n;
          const averageCase = (m,n) => Math.log((m+n)/m) * Math.log2(m+1)*m;

          data = data.flatMap( data => {
            let fitFns = function(){
              switch(data.name) {
                default:
                  throw new Error(data.name);
                case 'MoM5 V1':
                case 'MoM5 V2':
                case 'MoM3 V1':
                case 'MoM3 V2':
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
               train_X = data.x.map( x => [x,xMax-x] ),
               train_y = data.y;

            const fit = nd.opt.fit_lin(train_X, train_y, fitFns);

            console.log({[data.name]: fit.coeffs})

            const x = [...nd.iter.linspace(2,xMax-2,1000)]
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
}
