package com.github.jaaa.select;

import com.github.jaaa.compare.CompareRandomAccessor;
import com.github.jaaa.fn.EntryConsumer;
import com.github.jaaa.fn.EntryFn;
import com.github.jaaa.util.LinSpace;
import com.github.jaaa.util.PlotlyUtils;
import com.github.jaaa.util.Progress;

import java.io.IOException;
import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.stream.Stream;

import static com.github.jaaa.permute.RandomShuffle.randomShuffle;
import static java.lang.Math.min;
import static java.lang.String.format;
import static java.lang.System.arraycopy;
import static java.util.Arrays.asList;
import static java.util.Arrays.stream;
import static java.util.Collections.unmodifiableMap;
import static java.util.stream.Collectors.joining;
import static java.util.stream.IntStream.range;
import static java.util.stream.IntStream.rangeClosed;


/**
 * Checks whether the performance estimates are reasonable.
 */
public class CheckHeapSelect
{
  public static void main( String... args ) throws IOException, InterruptedException
  {
//   runCheck(
//     "Worst Case",
//     x -> x::performance_worstCase,
//     rng -> len -> {
//       int[] arr = new int[len];
//       for( int j=len; --j > 0; )
//         arr[j-1] = arr[j] + rng.nextInt(2);
//       return arr;
//     }
//   );
    runCheck(
      "Average Case",
      x -> x::performance_average,
      rng -> len -> {
        int[] arr = new int[len];
        for( int j=0; ++j < len; )
          arr[j] = arr[j-1] + rng.nextInt(2);
        randomShuffle(arr, rng::nextInt);
        return arr;
      }
    );
  }

  private static void runCheck( String caseName, Function<SelectAccess,PerformanceFn> perf, Function<SplittableRandom, IntFunction<int[]>> inputGenerator ) throws IOException, InterruptedException {
    int N_SAMPLES = 1 << 16;
    SplittableRandom rng = new SplittableRandom();
    IntFunction<int[]> gen = inputGenerator.apply(rng);

    CountingAccessor acc = new CountingAccessor();
    Map<String, Function<int[],SelectAccess>> selectors = selectors(acc);

    for( int _len=10; _len < 100_000_000; _len *= 10 )
    {
      int length = _len;
      System.out.printf("length: %d\n", length);

      double[] x = rng.ints(N_SAMPLES, 0,length+1).asDoubleStream().toArray();
      Arrays.sort(x);

      Map<String,double[]> resultsComps = new TreeMap<>();
      selectors.forEach( (k,v) ->
        resultsComps.put(k, new double[N_SAMPLES])
      );

      @SuppressWarnings("unchecked")
      Entry<String, Function<int[],SelectAccess>>[] selectorsArr = selectors.entrySet().stream().toArray(Entry[]::new);

      int[]         order = range(0,N_SAMPLES).toArray();
      randomShuffle(order,rng::nextInt);
      Progress.print( stream(order) ).forEach(i -> {
        int mid = (int) x[i];

        int[] ref = gen.apply(length);

        randomShuffle(selectorsArr, rng::nextInt);
        stream(selectorsArr).forEach( EntryConsumer.of( (k,v) -> {
          acc.nComps = 0;
          acc.nSwaps = 0;

          int[]   tst = ref.clone();
          v.apply(tst).select(0, mid, length);

          resultsComps.get(k)[i] = acc.nComps;

          assert stream(tst,0,  mid                 ).allMatch( z -> z <= tst[mid] );
          assert stream(tst,min(mid+1,length),length).allMatch( z -> z >= tst[mid] );
        }));
      });

      List<String> colorList = asList(
        "#e41a1c", "#377eb8", "#4daf4a", "#984ea3", "#ff7f00", "#ffff33", "#a65628", "#f781bf", "#999999"
      );
      AtomicInteger col = new AtomicInteger();

      String[] data = selectors.entrySet().stream().flatMap(
        EntryFn.of( (method, sel) -> {
          double[]   y = resultsComps.get(method);
          String color = colorList.get( col.getAndIncrement() );

          String result = format(
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

          int[] estRange; {
            int l = min(1024, (length>>>1) - 2),
                r = length - l;
            estRange = Stream.of(
              rangeClosed(0,l),
              new LinSpace(l+1, r-1, min(1024, r-l-1)).stream().mapToInt(Double::intValue),
              rangeClosed(r,length)
            ).flatMapToInt( i -> i ).sorted().toArray();
          }

          String estimate = format(
            "{\n" +
            "  type: 'scattergl',\n" +
            "  mode: 'line',\n" +
            "  name: '%s (estimate)',\n" +
            "  line: {\n" +
            "    color: '%s'\n" +
            "  },\n" +
            "  x: %s,\n" +
            "  y: %s\n" +
            "}\n",
            method,
            color,
            Arrays.toString(estRange),
            stream(estRange).mapToLong( mid -> perf.apply(sel.apply(null)).performance(0, mid, length) ).mapToObj(Long::toString).collect(joining(", ", "[", "]"))
          );

          return Stream.of(result, estimate);
        })
      ).toArray(String[]::new);



      String layout = format(
        "{\n" +
        "  title: 'Heap Select Performance Estimation<br>(length=%d, %s)',\n" +
        "  xaxis: {\n" +
        "    title: 'Split',\n" +
        "    color: 'lightgray',\n" +
        "    gridcolor: '#333'\n" +
        "  },\n" +
        "  yaxis: {\n" +
        "    title: 'Number of Comparisons',\n" +
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
        length,
        caseName
      );

      PlotlyUtils.plot(layout, data);
      Thread.sleep(2_000);
    }
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
      int ai = a[i];
        a[i] = b[j];
        b[j] = ai;
      ++nSwaps;
    }
  }

  private interface PerformanceFn {
    long performance( int l, int m, int r );
  }

  private interface SelectAccess {
    void select( int l, int m, int r );
    long performance_worstCase( int l, int m, int r );
    long performance_average  ( int l, int m, int r );
  }

  private static <T> Map<String, Function<T,SelectAccess>> selectors( CompareRandomAccessor<T> acc ) {
    Map<String, Function<T,SelectAccess>> selectors = new LinkedHashMap<>();
    selectors.put("Heap", arr -> {
      class Acc implements HeapSelectAccess, SelectAccess {
        @Override public void   swap( int i, int j ) {        acc.   swap(arr,i, arr,j); }
        @Override public int compare( int i, int j ) { return acc.compare(arr,i, arr,j); }
        @Override public void select( int l, int m, int r ) { heapSelect(l,m,r); }
        @Override public long performance_average  ( int l, int m, int r ) { return heapSelect_performance(l,m,r); }
        @Override public long performance_worstCase( int from, int mid, int until ) {
          if( from < 0 || from > mid || mid > until )
            throw new IllegalArgumentException();
          if( mid == until )
            return 0;
          int m =   mid - from,
              n = until - mid;
          return m > n-2
            ? HeapSelect.performance_worstCase(m+1,n-1)
            : HeapSelect.performance_worstCase(n,m);
        }
      }
      return new Acc();
    });
    selectors.put("HeapRandom", arr -> {
      class Acc implements HeapSelectRandomAccess, SelectAccess {
        @Override public void   swap( int i, int j ) {        acc.   swap(arr,i, arr,j); }
        @Override public int compare( int i, int j ) { return acc.compare(arr,i, arr,j); }
        @Override public void select( int l, int m, int r ) { heapSelectRandom(l,m,r); }
        @Override public long performance_average  ( int l, int m, int r ) { return heapSelectRandom_performance(l,m,r); }
        @Override public long performance_worstCase( int l, int m, int r ) { return heapSelectRandom_performance(l,m,r); }
      }
      return new Acc();
    });
    selectors.put("MergeSelect", arr -> {
      class Acc implements MergeSelectAccessor<T>, SelectAccess {
        @Override public T malloc( int len ) { return acc.malloc(len); }
        @Override public void   copy( T a, int i, T b, int j ) {        acc.   copy(a,i, b,j); }
        @Override public void   swap( T a, int i, T b, int j ) {        acc.   swap(a,i, b,j); }
        @Override public int compare( T a, int i, T b, int j ) { return acc.compare(a,i, b,j); }
        @Override public void select( int l, int m, int r ) { mergeSelect(arr,l,m,r, null,0,0); }
        @Override public long performance_average  ( int l, int m, int r ) { return mergeSelect_performance(l,m,r); }
        @Override public long performance_worstCase( int l, int m, int r ) { return mergeSelect_performance(l,m,r); }
      }
      return new Acc();
    });
    return unmodifiableMap(selectors);
  }
}
