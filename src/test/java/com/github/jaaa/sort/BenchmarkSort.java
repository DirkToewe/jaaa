package com.github.jaaa.sort;

import com.github.jaaa.fn.EntryConsumer;
import com.github.jaaa.fn.EntryFn;
import com.github.jaaa.sort.datagen.RandomSortDataGenerator;
import com.github.jaaa.util.PlotlyUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

import static com.github.jaaa.misc.Shuffle.shuffled;
import static java.awt.Desktop.getDesktop;
import static java.lang.String.format;
import static java.lang.System.nanoTime;
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
    public long nComps = 0;
  }

// STATIC METHODS
  public static void main( String... args ) throws IOException
  {
//    System.out.println("WAIT");
//    new java.util.Scanner(System.in).next();
//    System.out.println("GO");

    Map<String,SortFn> mergers = Map.ofEntries(
      entry("TimSort",                      TimSort::sort),
//      entry("JDK",                           Arrays::sort),
//      entry("HeapSort",                    HeapSort::sort),
//      entry("HeapSortFast",            HeapSortFast::sort),
//      entry("QuickSort",                  QuickSort::sort),
//      entry("MergeSort",                  MergeSort::sort),
      entry("KiwiSortV1",                KiwiSortV1::sort),
      entry("KiwiSortV2",                KiwiSortV2::sort),
      entry("KiwiSortV3",                KiwiSortV3::sort),
      entry("KiwiSortV4",                KiwiSortV4::sort),
      entry("ComparatorWikiSort", new SortFn() {
        @Override public <T> void sort( T[] arr, Comparator<? super T> cmp ) {
          new ComparatorWikiSort<T>(null).Sort(arr,cmp);
        }
      })
    );

    int     LEN = 1_000_000,
      N_SAMPLES =    10_000;

    var rng = new Random();
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
    var progressCounter = new AtomicLong();

    stream( shuffled( range(0,N_SAMPLES).toArray() ) ).forEach( i -> {
      var progress = progressCounter.incrementAndGet();
      if( progress % 10 == 0  )
        System.out.printf("%5d / %d\n", progress, N_SAMPLES);

      int len = x[i];
//      int[] data = gen.nextMixed(len);
      int[] data = gen.nextUniform(len);
      assert len == data.length;

      int sign = rng.nextBoolean() ? -1 : +1;
      var cmp = new CountingComparator() {
        @Override public int compare( Integer x, Integer y ) {
          ++nComps;
          return sign*Integer.compare(x,y);
        }
      };

      var ref = stream(data).boxed().sorted(cmp).toArray(Integer[]::new);

      Collections.shuffle(mergersEntries);

      mergersEntries.forEach( EntryConsumer.of( (k, v) -> {
        cmp.nComps = 0;

        var test = stream(data).boxed().toArray(Integer[]::new);

        long t0 = nanoTime();
        v.sort(test, cmp);
        long dt = nanoTime() - t0;

        resultsComps.get(k)[i] = cmp.nComps;
        resultsTimes.get(k)[i] = dt / 1e3;

        assert Arrays.equals(test,ref) : Arrays.toString(test) + "\n != \n" + Arrays.toString(ref);
      }));
    });

    plot_results("comparisons", x, resultsComps);
    plot_results("timings",     x, resultsTimes);
  }

  private static void plot_results( String type, int[] x, Map<String,double[]> results ) throws IOException
  {
    var data = results.entrySet().stream().map( EntryFn.of(
      (method,y) -> format(
        """
        {
          type: 'scatter2d',
          mode: 'markers',
          marker: {
            size: 4,
            opacity: 0.5
          },
          name: '%s',
          x: %s,
          y: %s
        }
        """,
        method,
        Arrays.toString(x),
        Arrays.toString(y)
      )
    )).toArray(String[]::new);

    String layout = format(
      """
      {
        title: 'Merge %s Benchmark (L = %d)',
        xaxis: {title: 'Split Position'},
        yaxis: {title: 'Time [msec.]'  }
      }
      """,
      type, x.length
    );

    PlotlyUtils.plot(layout, data);
  }
}
