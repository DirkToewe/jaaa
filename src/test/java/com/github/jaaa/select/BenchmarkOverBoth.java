package com.github.jaaa.select;

import com.github.jaaa.fn.EntryConsumer;
import com.github.jaaa.fn.EntryFn;
import com.github.jaaa.select.BenchmarkOverSplit.CountingAccessor;
import com.github.jaaa.select.BenchmarkOverSplit.SelectFn;
import com.github.jaaa.util.Progress;

import java.io.IOException;
import java.io.Writer;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Map;
import java.util.Map.Entry;
import java.util.SplittableRandom;
import java.util.TreeMap;

import static com.github.jaaa.permute.RandomShuffle.randomShuffle;
import static com.github.jaaa.select.BenchmarkOverSplit.selectors;
import static com.github.jaaa.util.Sampling.lhs;
import static java.lang.Math.round;
import static java.lang.Runtime.getRuntime;
import static java.lang.String.format;
import static java.lang.System.nanoTime;
import static java.lang.System.out;
import static java.nio.charset.StandardCharsets.UTF_8;
import static java.nio.file.Files.createTempFile;
import static java.nio.file.Files.newBufferedWriter;
import static java.util.Arrays.stream;
import static java.util.stream.Collectors.joining;
import static java.util.stream.IntStream.range;


// -ea -Xmx16g -XX:MaxInlineLevel=15
public class BenchmarkOverBoth
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

    for( int len: new int[]{ 100_000 } )
    {
      out.printf("len: %d\n", len);
      compare_over_both(len);
    }
  }

  private static void compare_over_both( final int max_length ) throws IOException
  {
    int N_SAMPLES = N_SAMPLES_DEFAULT;
    SplittableRandom rng = new SplittableRandom();

    double[][] samples = lhs(N_SAMPLES, 2);
    double[]
      x = stream(samples).mapToDouble( xy -> round(2 + xy[0]*(max_length-1)) ).toArray(),
      y = stream(samples).mapToDouble( xy -> round(2 + xy[1]*(max_length-1)) ).toArray();

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

    int[] order = range(0,N_SAMPLES).toArray();
    randomShuffle(order,rng::nextInt);
    Progress.print( stream(order) ).forEach( i -> {
      final int split = (int) x[i],
               length = (int)(x[i] + y[i]);

      int[] ref = new int[length];
      for( int j=0; ++j < length; )
        ref[j] = ref[j-1] + rng.nextInt(2);
      randomShuffle(ref, rng::nextInt);

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

    plot3d("Select comparisons Benchmark", "Left Length", x, "Right Length", y, "#comparisons", resultsComps);
    plot3d(       "Select swap Benchmark", "Left Length", x, "Right Length", y, "#swaps",       resultsSwaps);
    plot3d(    "Select timings Benchmark", "Left Length", x, "Right Length", y, "Time [msec.]", resultsTimes);
  }

  private static void plot3d( String title, String x_label, double[] x, String y_label, double[] y, String z_label, Map<String,double[]> zs ) throws IOException
  {
    String data = zs.entrySet().stream().map( EntryFn.of(
      (method,z) -> format(
        "{\n" +
        "  type: 'scatter3d',\n" +
        "  mode: 'markers',\n" +
        "  name: '%s',\n" +
        "  marker: {\n" +
        "    size: 1.5,\n" +
        "    opacity: 0.8,\n" +
        "    line: {\n" +
        "      width: 0.5,\n" +
        "      color: 'black'\n" +
        "    }\n" +
        "  },\n" +
        "  x: %s,\n" +
        "  y: %s,\n" +
        "  z: %s\n" +
        "}\n",
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
      "{\n" +
      "  title: '%s',\n" +
      "  paper_bgcolor: 'black',\n" +
      "   plot_bgcolor: 'black',\n" +
      "  legend: {\n" +
      "    font: { color: 'lightgray' }\n" +
      "  },\n" +
      "  font: { color: 'lightgray' },\n" +
      "  scene: {\n" +
      "    xaxis: {title: '%s', color: 'lightgray', gridcolor: '#333' },\n" +
      "    yaxis: {title: '%s', color: 'lightgray', gridcolor: '#333' },\n" +
      "    zaxis: {title: '%s', color: 'lightgray', gridcolor: '#333' }\n" +
      "  }\n" +
      "}\n",
      title,
      x_label,
      y_label,
      z_label
    );

    String PLOT_TEMPLATE =
      "<!DOCTYPE html>\n" +
      "<html lang=\"en\">\n" +
      "  <head>\n" +
      "    <meta charset=\"utf-8\">\n" +
      "    <script src=\"https://cdn.plot.ly/plotly-latest.js\"></script>\n" +
      "    <script type=\"text/javascript\" src=\"./nd.min.js\"></script>\n" +
      "  </head>\n" +
      "  <body>\n" +
      "    <script>\n" +
      "    'use strict';\n" +
      "\n" +
      "    (async () => {\n" +
      "      const plot = document.createElement('div');\n" +
      "      plot.style = 'width: 100%%; height: 95vh;';\n" +
      "      document.body.appendChild(plot);\n" +
      "\n" +
      "      const dataRaw = %2$s,\n" +
      "             layout = %1$s;\n" +
      "      if( 'title' in layout )\n" +
      "        document.title = layout.title;\n" +
      "      if( 'paper_bgcolor' in layout )\n" +
      "        document.body.style.background = layout.paper_bgcolor;\n" +
      "\n" +
      "      const colors = [\n" +
      "        '#e41a1c',\n" +
      "        '#377eb8',\n" +
      "        '#4daf4a',\n" +
      "        '#984ea3',\n" +
      "        '#ff7f00',\n" +
      "        '#ffff33',\n" +
      "        '#a65628',\n" +
      "        '#f781bf',\n" +
      "        '#999999'\n" +
      "      ];\n" +
      "\n" +
      "      const sleep = dt => new Promise(resolve => setTimeout(resolve,dt));\n" +
      "\n" +
      "      const data = [];\n" +
      "      let col = 0;\n" +
      "      for( const pts of dataRaw )\n" +
      "      {\n" +
      "        const color = colors[col++ %% colors.length];\n" +
      "        pts.marker.color = color;\n" +
      "\n" +
      "        const     log2 = x => Math.ceil( Math.log2(x+1) ),\n" +
      "          sortedInputs = fn => ([x,y]) => fn( Math.min(x,y), Math.max(x,y) );\n" +
      "\n" +
      "        const   worstCase = (m,n) => Math.log2(m+1) * n;\n" +
      "        const averageCase = (m,n) => Math.log((m+n)/m) * Math.log2(m+1)*m;\n" +
      "\n" +
      "        let fit_fns = function(){\n" +
      "          switch(pts.name) {\n" +
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
      "        fit_fns = fit_fns.map(\n" +
      "          f => ([x,y]) => {\n" +
      "            const m = Math.min(x,y),\n" +
      "                  n = Math.max(x,y);\n" +
      "            return m < 2 ? 0 : f(m,n)\n" +
      "          }\n" +
      "        );\n" +
      "\n" +
      "        const fit_fn = nd.opt.fit_lin(\n" +
      "          [...nd.iter.zip(pts.x, pts.y)],\n" +
      "          pts.z,\n" +
      "          fit_fns\n" +
      "        );\n" +
      "\n" +
      "        console.log({name: pts.name, fit_fn})\n" +
      "\n" +
      "        const xMin = pts.x.reduce( (x,y) => Math.min(x,y) ), xMax = pts.x.reduce( (x,y) => Math.max(x,y) ),\n" +
      "              yMin = pts.y.reduce( (x,y) => Math.min(x,y) ), yMax = pts.y.reduce( (x,y) => Math.max(x,y) );\n" +
      "\n" +
      "        const x = [],\n" +
      "              y = [],\n" +
      "              z = [];\n" +
      "\n" +
      "        const N = 17,\n" +
      "            LOD = 10;\n" +
      "\n" +
      "        for( const xi of nd.iter.linspace(xMin,xMax,N) )\n" +
      "        {\n" +
      "          for( const yi of nd.iter.linspace(yMin,yMax,N*LOD) )\n" +
      "          {\n" +
      "            x.push(xi);\n" +
      "            y.push(yi);\n" +
      "            z.push( fit_fn([xi,yi]) );\n" +
      "          }\n" +
      "          [x,y,z].forEach( arr => arr.push(NaN) );\n" +
      "        }\n" +
      "        for( const yi of nd.iter.linspace(yMin,yMax,N) )\n" +
      "        {\n" +
      "          for( const xi of nd.iter.linspace(xMin,xMax,N*LOD) )\n" +
      "          {\n" +
      "            x.push(xi);\n" +
      "            y.push(yi);\n" +
      "            z.push( fit_fn([xi,yi]) );\n" +
      "          }\n" +
      "          [x,y,z].forEach( arr => arr.push(NaN) );\n" +
      "        }\n" +
      "\n" +
      "        const fit = {\n" +
      "          type: 'scatter3d',\n" +
      "          mode: 'lines',\n" +
      "          name: pts.name + ' (fit)',\n" +
      "          line: { color, width: 4 },\n" +
      "          x,y,z\n" +
      "        };\n" +
      "\n" +
      "        await sleep();\n" +
      "\n" +
      "        data.push(pts,fit);\n" +
      "      }\n" +
      "\n" +
      "      await Plotly.plot(plot, {layout, data});\n" +
      "    })()\n" +
      "    </script>\n" +
      "  </body>\n" +
      "</html>\n";

    Path tmp = createTempFile("plot_",".html");
    try( Writer out = newBufferedWriter(tmp,UTF_8) ) {
      out.write( format(PLOT_TEMPLATE, layout, data) );
    }
    String[] cmd = {"xdg-open", tmp.toString()};
    getRuntime().exec(cmd);
  }
}
