package com.github.jaaa.select;

import com.github.jaaa.fn.EntryConsumer;
import com.github.jaaa.fn.EntryFn;
import com.github.jaaa.select.BenchmarkOverSplit.CountingAccessor;
import com.github.jaaa.select.BenchmarkOverSplit.SelectFn;
import com.github.jaaa.util.Progress;

import java.io.IOException;
import java.io.Writer;
import java.nio.file.Path;
import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.atomic.AtomicInteger;

import static com.github.jaaa.permute.RandomShuffle.randomShuffle;
import static com.github.jaaa.select.BenchmarkOverSplit.selectors;
import static java.lang.Math.round;
import static java.lang.Runtime.getRuntime;
import static java.lang.String.format;
import static java.lang.System.nanoTime;
import static java.lang.System.out;
import static java.nio.charset.StandardCharsets.UTF_8;
import static java.nio.file.Files.createTempFile;
import static java.nio.file.Files.newBufferedWriter;
import static java.util.Arrays.asList;
import static java.util.Arrays.stream;
import static java.util.stream.Collectors.joining;
import static java.util.stream.IntStream.range;


// -ea -Xmx16g -XX:MaxInlineLevel=15
public class BenchmarkOverLength
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

    for( double split: new double[]{ 0.5, 0.25, 0.75 } )
      compare_over_length(100_000, split);
//      compare_over_length(40_000_000, split);
  }

  /** Compares merging algorithms using merge sequences of constant split but varying combined
   *  lengths. In other words: Given two merge sequences a and b of combined length len, then
   *  the sequence length are |a| = split*len and |b| = (1-split)*len.
   */
  private static void compare_over_length( final int max_length, final double splitRatio ) throws IOException
  {
    int N_SAMPLES = N_SAMPLES_DEFAULT;
    SplittableRandom rng = new SplittableRandom();

    double[] x = rng.ints(N_SAMPLES, 0, max_length+1).asDoubleStream().toArray();
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
    Entry<String,SelectFn<int[]>>[] selectorsArr = selectors.entrySet().stream().toArray(Entry[]::new);

    int[]         order = range(0,N_SAMPLES).toArray();
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

    plot2d(format("Select comparisons Benchmark (split = %.2f)", splitRatio), "Length", splitRatio, x, "#comparisons", resultsComps);
    plot2d(format(       "Select swap Benchmark (split = %.2f)", splitRatio), "Length", splitRatio, x, "#swaps",       resultsSwaps);
    plot2d(format(    "Select timings Benchmark (split = %.2f)", splitRatio), "Length", splitRatio, x, "Time [msec.]", resultsTimes);
  }

  private static void plot2d( String title, String x_label, double split, double[] x, String y_label, Map<String,double[]> ys ) throws IOException
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
      "      const splitRatio = %3$f;\n" +
      "\n" +
      "      const worstCase   = (m,n) => Math.log2(m+1) * n;\n" +
      "      const averageCase = (m,n) => Math.log((m+n)/m) * Math.log2(m+1)*m;\n" +
      "\n" +
      "      data = data.flatMap( data => {\n" +
      "        let fitFns = function(){\n" +
      "          switch(data.name) {\n" +
      "            default:\n" +
      "              throw new Error(data.name);\n" +
      "            case 'MoM3':\n" +
      "            case 'MoM5':\n" +
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
      "           train_X = data.x.map( x => {\n" +
      "             const split = Math.round(splitRatio*x);\n" +
      "             return [split, x-split];\n" +
      "           }),\n" +
      "           train_y = data.y;\n" +
      "\n" +
      "        const fit = nd.opt.fit_lin(train_X, train_y, fitFns);\n" +
      "\n" +
      "        console.log({[data.name]: fit.coeffs})\n" +
      "\n" +
      "        const x = [...nd.iter.linspace(2,xMax-2,1000)]\n" +
      "        const y = x.map( x => {\n" +
      "          const split = Math.round(splitRatio*x);\n" +
      "          return fit([split, x-split]);\n" +
      "        })\n" +
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
      out.write( format(PLOT_TEMPLATE, layout, dat, split) );
    }
    String[] cmd = {"xdg-open", tmp.toString()};
    getRuntime().exec(cmd);
  }
}
