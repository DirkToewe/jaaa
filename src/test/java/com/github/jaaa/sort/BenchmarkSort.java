package com.github.jaaa.sort;

import com.github.jaaa.fn.EntryConsumer;
import com.github.jaaa.fn.EntryFn;
import com.github.jaaa.sort.datagen.RandomSortDataGenerator;
import com.github.jaaa.util.Progress;

import java.io.IOException;
import java.io.Writer;
import java.nio.file.Path;
import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.atomic.LongAdder;

import static com.github.jaaa.Boxing.boxed;
import static com.github.jaaa.permute.RandomShuffle.shuffled;
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
public class BenchmarkSort
{
// STATIC FIELDS

// STATIC CONSTRUCTOR
  static {
    boolean ea = false;
    assert  ea = true;
      if( ! ea ) throw new AssertionError("Assertions not enabled.");
  }

  private interface SortFn {
    <T> void sort( T[] arr, Comparator<? super T> cmp );
  }

  private static abstract class CountingComparator implements Comparator<Integer>
  {
    public final LongAdder nComps = new LongAdder();
  }

// STATIC METHODS
  public static void main( String... args ) throws IOException
  {
    out.println( System.getProperty("java.vm.name") + " " + System.getProperty("java.vm.version") );
    out.println( "Java " + System.getProperty("java.version") );

//    System.out.println("WAIT");
//    new java.util.Scanner(System.in).next();
//    System.out.println("GO");

    Map<String,SortFn> sorters = new LinkedHashMap<>();
//    sorters.put("ParSkip", ParallelSkipMergeSort::sort);
//    sorters.put("ParZen",  ParallelZenMergeSort ::sort);
//    sorters.put("JDK (parallel)", Arrays::parallelSort);

//    sorters.put("HeapSort",             HeapSort::sort);
//    sorters.put("QuickSort",           QuickSort::sort);
//    sorters.put("MergeSort",           MergeSort::sort);

    sorters.put("KiwiSort",                     KiwiSort::sort);
    sorters.put("WikiSort", new ComparatorWikiSort(null)::sort);
//    sorters.put("KiwiSortBiased",         KiwiSortBiased::sort);

//    sorters.put("TimSort",               TimSort::sort);
    sorters.put("JDK",                    Arrays::sort);

    int     LEN = 1_000_000,
      N_SAMPLES =     1_000;

    SplittableRandom        rng = new SplittableRandom();
    RandomSortDataGenerator gen = new RandomSortDataGenerator(rng);

//    int[] x = rng.doubles(N_SAMPLES, 0,Math.log(LEN+1)).mapToInt( y -> (int) Math.round(Math.exp(y)) ).toArray();
    int[] x = rng.ints(N_SAMPLES, 0,LEN+1).sorted().toArray();
    assert x.length == N_SAMPLES;

    Map<String,double[]>
      resultsComps = new TreeMap<>(),
      resultsTimes = new TreeMap<>();
    sorters.forEach( (k,v) -> {
      resultsComps.put(k, new double[N_SAMPLES]);
      resultsTimes.put(k, new double[N_SAMPLES]);
    });

    List<Entry<String,SortFn>> mergersEntries  = new ArrayList<>( sorters.entrySet() );

    Progress.print( stream( shuffled(range(0,N_SAMPLES).toArray(), rng::nextInt) ) ).forEach( i -> {
      int len = x[i];
//      int[] data = gen.nextMixed(len);
      int[] data = gen.nextShuffled(len);
      assert len == data.length;

      int mask = rng.nextInt();
      CountingComparator cmp = new CountingComparator() {
        @Override public int compare( Integer x, Integer y ) {
          nComps.increment();
          return Integer.compare(x^mask,y^mask);
        }
      };

      Integer[] ref = stream(data).boxed().toArray(Integer[]::new);
      Arrays.parallelSort(ref,cmp);

      Collections.shuffle(mergersEntries);

      mergersEntries.forEach( EntryConsumer.of( (k, v) -> {
        System.gc();
        cmp.nComps.reset();

        Integer[] test = boxed(data);

        long t0 = nanoTime();
        v.sort(test, cmp);
        long dt = nanoTime() - t0;

        out.printf("\n%14s: %.3fsec.", k, dt/1e9);
        resultsComps.get(k)[i] = cmp.nComps.doubleValue();
        resultsTimes.get(k)[i] = dt / 1e3;

        assert Arrays.equals(test,ref) : Arrays.toString(test) + "\n != \n" + Arrays.toString(ref);
      }));
      out.println();
    });

    plot_results("comparisons", x, resultsComps);
    plot_results("timings",     x, resultsTimes);
  }

  private static void plot_results( String type, int[] x, Map<String,double[]> results ) throws IOException
  {
    String vm = System.getProperty("java.vm.name") + " " + System.getProperty("java.vm.version"),
           jv = "Java " + System.getProperty("java.version");

    Iterator<String> colors = asList(
      "#e41a1c", "#377eb8", "#4daf4a", "#984ea3", "#ff7f00", "#ffff33", "#a65628", "#f781bf", "#999999"
    ).iterator();

    String[] data = results.entrySet().stream().map( EntryFn.of(
      (method,y) -> format(
        "{\n" +
        "  type: 'scattergl',\n" +
        "  mode: 'markers',\n" +
        "  marker: {\n" +
        "    size: 4,\n" +
        "    opacity: 0.5,\n" +
        "    color: '%s'\n" +
        "  },\n" +
        "  name: '%s',\n" +
        "  x: %s,\n" +
        "  y: %s\n" +
        "}\n",
        colors.next(),
        method,
        Arrays.toString(x),
        Arrays.toString(y)
      )
    )).toArray(String[]::new);

    String layout = format(
      "{\n" +
      "  title: 'Sort %s Benchmark (L_max = %d)<br>%s<br>%s',\n" +
      "  xaxis: {title: 'Split Position'},\n" +
      "  yaxis: {title: 'Time [msec.]'  }\n" +
      "}\n",
      type, stream(x).max().getAsInt(), vm, jv
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
      "        const fitFns = [\n" +
      "          x => 1,\n" +
      "          x => x,\n" +
      "          x => Math.log(x)*x,\n" +
      "//          x => x*x\n" +
      "        ];\n" +
      "\n" +
      "        const xMax = data.x.reduce((x,y) => Math.max(x,y)),\n" +
      "              yMax = data.y.reduce((x,y) => Math.max(x,y))\n" +
      "\n" +
      "        const fit = nd.opt.fit_lin(data.x, data.y, fitFns);\n" +
      "\n" +
      "        console.log({[data.name]: fit.coeffs})\n" +
      "\n" +
      "        const x = [...nd.iter.linspace(1, xMax, 1000)]\n" +
      "        const y = x.map(fit);\n" +
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
