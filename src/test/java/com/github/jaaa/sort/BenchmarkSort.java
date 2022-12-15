package com.github.jaaa.sort;

import com.github.jaaa.fn.EntryConsumer;
import com.github.jaaa.fn.EntryFn;
import com.github.jaaa.sort.datagen.RandomSortDataGenerator;
import com.github.jaaa.util.Progress;

import java.io.IOException;
import java.nio.file.Files;
import java.util.*;
import java.util.concurrent.atomic.LongAdder;

import static com.github.jaaa.permute.RandomShuffle.shuffled;
import static java.lang.Runtime.getRuntime;
import static java.lang.String.format;
import static java.lang.System.nanoTime;
import static java.lang.System.out;
import static java.util.Arrays.stream;
import static java.util.Map.entry;
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

    Map<String,SortFn> mergers = Map.ofEntries(
      entry("ParRebel", ParallelRebelMergeSort::sort),
//      entry("ParRec",   ParallelRecMergeSort  ::sort),
//      entry("ParSkip",  ParallelSkipMergeSort ::sort),
//      entry("ParZen",   ParallelZenMergeSort  ::sort),
//      entry("HeapSort",                               HeapSort::sort),
//      entry("HeapSortFast",                       HeapSortFast::sort),
//      entry("QuickSort",                             QuickSort::sort),
//      entry("MergeSort",                             MergeSort::sort),
//      entry("KiwiSortV1",                           KiwiSortV1::sort),
//      entry("KiwiSort",                             KiwiSort::sort),
//      entry("KiwiSortBiased",                       KiwiSortBiased::sort),
//      entry("WikiSortV1",                           WikiSortV1::sort),
//      entry("ComparatorWikiSort", new ComparatorWikiSort(null)::sort),
//      entry("TimSort",                                 TimSort::sort),
//      entry("RebelSort",                ParallelRebelMergeSort::sort),
//      entry(  "RecSort",                ParallelRecMergeSort  ::sort),
//      entry( "SkipSort",                ParallelSkipMergeSort ::sort),
//      entry(  "ZenSort",                ParallelZenMergeSort  ::sort),
//      entry("JDK",                                      Arrays::sort)
      entry("JDK (parallel)",                            Arrays::parallelSort)
    );

    int     LEN = 10_000_000,
      N_SAMPLES =      1_000;

    var rng = new SplittableRandom();
    var gen = new RandomSortDataGenerator(rng);

//    int[] x = rng.doubles(N_SAMPLES, 0,Math.log(LEN+1)).mapToInt( y -> (int) Math.round(Math.exp(y)) ).toArray();
    int[] x = rng.ints(N_SAMPLES, 0,LEN+1).sorted().toArray();
    assert x.length == N_SAMPLES;

    Map<String,double[]>
      resultsComps = new TreeMap<>(),
      resultsTimes = new TreeMap<>();
    mergers.forEach( (k,v) -> {
      resultsComps.put(k, new double[N_SAMPLES]);
      resultsTimes.put(k, new double[N_SAMPLES]);
    });

    var mergersEntries  = new ArrayList<>( mergers.entrySet() );

    Progress.print( stream( shuffled(range(0,N_SAMPLES).toArray(), rng::nextInt) ) ).forEach( i -> {
      int len = x[i];
//      int[] data = gen.nextMixed(len);
      int[] data = gen.nextShuffled(len);
      assert len == data.length;

      int sign = rng.nextBoolean() ? -1 : +1;
      var cmp = new CountingComparator() {
        @Override public int compare( Integer x, Integer y ) {
          nComps.increment();
          return sign*Integer.compare(x,y);
        }
      };

      var ref = stream(data).boxed().sorted(cmp).toArray(Integer[]::new);

      Collections.shuffle(mergersEntries);

      mergersEntries.forEach( EntryConsumer.of( (k, v) -> {
        System.gc();
        cmp.nComps.reset();

        var test = stream(data).boxed().toArray(Integer[]::new);

        long t0 = nanoTime();
        v.sort(test, cmp);
        long dt = nanoTime() - t0;

        resultsComps.get(k)[i] = cmp.nComps.doubleValue();
        resultsTimes.get(k)[i] = dt / 1e3;

        assert Arrays.equals(test,ref) : Arrays.toString(test) + "\n != \n" + Arrays.toString(ref);
      }));
    });

    plot_results("comparisons", x, resultsComps);
    plot_results("timings",     x, resultsTimes);
  }

  private static void plot_results( String type, int[] x, Map<String,double[]> results ) throws IOException
  {
    var vm = System.getProperty("java.vm.name") + " " + System.getProperty("java.vm.version");
    var jv = "Java " + System.getProperty("java.version");

    var colors = List.of(
      "#e41a1c", "#377eb8", "#4daf4a", "#984ea3", "#ff7f00", "#ffff33", "#a65628", "#f781bf", "#999999"
    ).iterator();

    var data = results.entrySet().stream().map( EntryFn.of(
      (method,y) -> format(
        """
        {
          type: 'scattergl',
          mode: 'markers',
          marker: {
            size: 4,
            opacity: 0.5,
            color: '%s'
          },
          name: '%s',
          x: %s,
          y: %s
        }
        """,
        colors.next(),
        method,
        Arrays.toString(x),
        Arrays.toString(y)
      )
    )).toArray(String[]::new);

    String layout = format(
      """
      {
        title: 'Sort %s Benchmark (L_max = %d)<br>%s<br>%s',
        xaxis: {title: 'Split Position'},
        yaxis: {title: 'Time [msec.]'  }
      }
      """,
      type, stream(x).max().getAsInt(), vm, jv
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
            const fitFns = [
              x => 1,
              x => x,
              x => Math.log(x)*x,
              x => x*x
            ];

            const xMax = data.x.reduce((x,y) => Math.max(x,y)),
                  yMax = data.y.reduce((x,y) => Math.max(x,y))

            const fit = nd.opt.fit_lin(data.x, data.y, fitFns);

            console.log({[data.name]: fit.coeffs})

            const x = [...nd.iter.linspace(1, xMax, 1000)]
            const y = x.map(fit);

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
    String[] cmd = {"xdg-open", tmp.toString()};
    getRuntime().exec(cmd);
  }
}
