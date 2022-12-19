package com.github.jaaa.select;

import com.github.jaaa.compare.CompareRandomAccessor;
import com.github.jaaa.fn.EntryConsumer;
import com.github.jaaa.fn.EntryFn;
import com.github.jaaa.sort.TimSortAccessor;
import com.github.jaaa.util.Progress;

import java.io.IOException;
import java.io.Writer;
import java.nio.file.Path;
import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.atomic.AtomicInteger;

import static com.github.jaaa.permute.RandomShuffle.randomShuffle;
import static java.lang.Runtime.getRuntime;
import static java.lang.String.format;
import static java.lang.System.*;
import static java.nio.charset.StandardCharsets.UTF_8;
import static java.nio.file.Files.createTempFile;
import static java.nio.file.Files.newBufferedWriter;
import static java.util.Arrays.stream;
import static java.util.Collections.unmodifiableMap;
import static java.util.stream.Collectors.joining;
import static java.util.stream.IntStream.range;
import static java.util.Arrays.asList;


// -ea -Xmx16g -XX:MaxInlineLevel=15
public class BenchmarkOverSplit
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
      int ai = a[i];
               a[i] = b[j];
                      b[j] = ai;
      ++nSwaps;
    }
  }

  static <T> Map<String, SelectFn<T>> selectors( CompareRandomAccessor<T> acc ) {
    Map<String, SelectFn<T>> selectors = new LinkedHashMap<>();
    selectors.put("Heap", (arr,l,m,r) ->
      new HeapSelectAccess() {
        @Override public void   swap( int i, int j ) {        acc.   swap(arr,i, arr,j); }
        @Override public int compare( int i, int j ) { return acc.compare(arr,i, arr,j); }
      }.heapSelect(l,m,r)
    );
    selectors.put("HeapRandom", (arr,l,m,r) ->
      new HeapSelectRandomAccess() {
        @Override public void   swap( int i, int j ) {        acc.   swap(arr,i, arr,j); }
        @Override public int compare( int i, int j ) { return acc.compare(arr,i, arr,j); }
      }.heapSelectRandom(l,m,r)
    );
    selectors.put("MergeSelect", (arr,l,m,r) ->
      new MergeSelectAccessor<T>() {
        @Override public T malloc( int len ) { return acc.malloc(len); }
        @Override public void   copy( T a, int i, T b, int j ) {        acc.   copy(a,i, b,j); }
        @Override public void   swap( T a, int i, T b, int j ) {        acc.   swap(a,i, b,j); }
        @Override public int compare( T a, int i, T b, int j ) { return acc.compare(a,i, b,j); }
      }.mergeSelect(arr,l,m,r, null,0,0)
    );
    selectors.put("Quick", (arr,l,m,r) ->
      new QuickSelectAccess() {
        @Override public void   swap( int i, int j ) {        acc.   swap(arr,i, arr,j); }
        @Override public int compare( int i, int j ) { return acc.compare(arr,i, arr,j); }
      }.quickSelect(l,m,r)
    );
    selectors.put("MoM5", (arr,l,m,r) ->
      new Mom5SelectAccess() {
        @Override public void   swap( int i, int j ) {        acc.   swap(arr,i, arr,j); }
        @Override public int compare( int i, int j ) { return acc.compare(arr,i, arr,j); }
      }.mom5Select(l,m,r)
    );
    selectors.put("MoM3", (arr,l,m,r) ->
      new Mom3SelectAccess() {
        @Override public void   swap( int i, int j ) {        acc.   swap(arr,i, arr,j); }
        @Override public int compare( int i, int j ) { return acc.compare(arr,i, arr,j); }
      }.mom3Select(l,m,r)
    );
    selectors.put("TimSort", (arr,l,m,r) -> {
      TimSortAccessor<T> accessor = new TimSortAccessor<T>() {
        @Override public T malloc( int len ) { return acc.malloc(len); }
        @Override public int    compare( T a, int i, T b, int j ) { return acc.compare(a,i, b,j); }
        @Override public void      swap( T a, int i, T b, int j ) { acc.swap(a,i, b,j);}
        @Override public void copy     ( T a, int i, T b, int j ) { acc.copy(a,i, b,j); }
        @Override public void copyRange( T a, int i, T b, int j, int len ) { acc.copyRange(a,i, b,j, len); }
      };
      accessor.timSort(arr,l,r, null,0,0);
    });
    return unmodifiableMap(selectors);
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
    SplittableRandom rng = new SplittableRandom();

    double[] x = rng.ints(N_SAMPLES, 0,length+1).asDoubleStream().toArray();
    Arrays.sort(x);

    CountingAccessor acc = new CountingAccessor();
    Map<String, SelectFn<int[]>> selectors = selectors(acc);

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
    Entry<String,SelectFn<int[]>>[] selectorsArr = selectors.entrySet().toArray(new Entry[0]);

    int[] order = range(0,N_SAMPLES).toArray();
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
        int[] tst = ref.clone();

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
    List<String> colorList = asList(
      "#e41a1c", "#377eb8", "#4daf4a", "#984ea3", "#ff7f00", "#ffff33", "#a65628", "#f781bf", "#999999"
    );
    AtomicInteger col = new AtomicInteger();

    String[] data = ys.entrySet().stream().map( EntryFn.of(
      (method,y) -> {
        String color = colorList.get( col.getAndIncrement() );
        return format(
          "{\n" +
          "  type: 'scattergl',\n" +
          "  mode: 'markers',\n" +
          "  name: '%s',\n" +
          "  marker: {\n" +
          "    color: '%s',\n" +
          "    size: 2,\n" +
          "    opacity: 0.6\n" +
          "  },\n" +
          "  x: %s,\n" +
          "  y: %s\n" +
          "}\n",
          method,
          color,
          Arrays.toString(x),
          Arrays.toString(y)
        );
      }
    )).toArray(String[]::new);

    String layout = format(
      "{\n" +
      "  title: '%s',\n" +
      "  xaxis: {\n" +
      "    title: '%s',\n" +
      "    color: 'lightgray',\n" +
      "    gridcolor: '#333'\n" +
      "  },\n" +
      "  yaxis: {\n" +
      "    title: '%s',\n" +
      "    color: 'lightgray',\n" +
      "    gridcolor: '#333'\n" +
      "  },\n" +
      "  paper_bgcolor: 'black',\n" +
      "   plot_bgcolor: 'black',\n" +
      "  legend: {\n" +
      "    font: { color: 'lightgray' }\n" +
      "  },\n" +
      "  font: { color: 'lightgray' }\n" +
      "}\n",
      title,
      x_label,
      y_label
    );

    final String PLOT_TEMPLATE =
      "<!DOCTYPE html>\n" +
      "<html lang=\"en\">\n" +
      "  <head>\n" +
      "    <meta charset=\"utf-8\">\n" +
      "    <script src=\"https://cdn.plot.ly/plotly-latest.js\"></script>\n" +
      "    <script src=\"./nd.min.js\"></script>\n" +
      "  </head>\n" +
      "  <body>\n" +
      "    <script>\n" +
      "      'use strict';\n" +
      "\n" +
      "      const plot = document.createElement('div');\n" +
      "      plot.style = 'width: 100%%; height: 95vh;';\n" +
      "      document.body.appendChild(plot);\n" +
      "\n" +
      "      const layout = %1$s;\n" +
      "      if( 'title' in layout )\n" +
      "        document.title = layout.title;\n" +
      "\n" +
      "      if( 'paper_bgcolor' in layout )\n" +
      "        document.body.style.background = 'black';\n" +
      "\n" +
      "      let data = %2$s;\n" +
      "\n" +
      "      const worstCase   = (m,n) => Math.log2(m+1) * n;\n" +
      "      const averageCase = (m,n) => Math.log((m+n)/m) * Math.log2(m+1)*m;\n" +
      "\n" +
      "      data = data.flatMap( data => {\n" +
      "        let fitFns = function(){\n" +
      "          switch(data.name) {\n" +
      "            default:\n" +
      "              throw new Error(data.name);\n" +
      "            case 'MoM5':\n" +
      "            case 'MoM3':\n" +
      "            case 'TimSort':\n" +
      "            case 'MergeSelect':\n" +
      "            case 'Quick':\n" +
      "              return [(m,n) => m+n];\n" +
      "            case 'Heap':\n" +
      "            case 'HeapRandom':\n" +
      "              return [(m,n) => 0];\n" +
      "            case 'HeapMajor':\n" +
      "              return [\n" +
      "                (m,n) => m,\n" +
      "                (m,n) => n,\n" +
      "                (m,n) =>   worstCase(n,m),\n" +
      "                (m,n) => averageCase(n,m),\n" +
      "              ];\n" +
      "            case 'HeapMinor':\n" +
      "            case 'HeapHybridV2':\n" +
      "              return [\n" +
      "                (m,n) => m,\n" +
      "                (m,n) => n,\n" +
      "                (m,n) =>   worstCase(m,n),\n" +
      "                (m,n) => averageCase(m,n),\n" +
      "              ];\n" +
      "          }\n" +
      "        }();\n" +
      "\n" +
      "        fitFns = fitFns.map(\n" +
      "          f => ([x,y]) => {\n" +
      "            const m = Math.min(x,y),\n" +
      "                  n = Math.max(x,y);\n" +
      "            return m < 2 ? 0 : f(m,n)\n" +
      "          }\n" +
      "        );\n" +
      "\n" +
      "        const xMax = data.x.reduce((x,y) => Math.max(x,y)),\n" +
      "              yMax = data.y.reduce((x,y) => Math.max(x,y)),\n" +
      "           train_X = data.x.map( x => [x,xMax-x] ),\n" +
      "           train_y = data.y;\n" +
      "\n" +
      "        const fit = nd.opt.fit_lin(train_X, train_y, fitFns);\n" +
      "\n" +
      "        console.log({[data.name]: fit.coeffs})\n" +
      "\n" +
      "        const x = [...nd.iter.linspace(2,xMax-2,1000)]\n" +
      "        const y = x.map( x => fit([x,xMax-x]) )\n" +
      "\n" +
      "        const fitData = {\n" +
      "          x,y,\n" +
      "          name: data.name + ' (fit)',\n" +
      "          mode: 'lines',\n" +
      "          type: 'scattergl',\n" +
      "          line: {\n" +
      "            color: data.marker.color\n" +
      "          }\n" +
      "        };\n" +
      "        return [data, fitData];\n" +
      "      });\n" +
      "\n" +
      "      Plotly.plot(plot, {layout, data});\n" +
      "    </script>\n" +
      "  </body>\n" +
      "</html>\n";

    Path tmp = createTempFile("plot_",".html");
    String dat = stream(data).collect( joining(",\n", "[", "]") );
    try( Writer out = newBufferedWriter(tmp,UTF_8) ) {
      out.write( format(PLOT_TEMPLATE, layout, dat) );
    }
    String[] cmd = {"xdg-open", tmp.toString()};
    getRuntime().exec(cmd);
  }
}
