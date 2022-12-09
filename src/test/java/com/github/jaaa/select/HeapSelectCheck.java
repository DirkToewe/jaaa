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
import java.util.random.RandomGenerator;
import java.util.stream.Stream;

import static com.github.jaaa.permute.RandomShuffle.randomShuffle;
import static java.lang.Math.min;
import static java.lang.String.format;
import static java.lang.System.arraycopy;
import static java.util.Arrays.stream;
import static java.util.Map.entry;
import static java.util.stream.Collectors.joining;
import static java.util.stream.IntStream.range;
import static java.util.stream.IntStream.rangeClosed;


/**
 * Checks whether the performance estimates are reasonable.
 */
public class HeapSelectCheck
{
  public static void main( String... args ) throws IOException, InterruptedException
  {
//    runCheck(
//      "Worst Case",
//      x -> x::performance_worstCase,
//      rng -> len -> {
//        int[] arr = new int[len];
//        for( int j=len; --j > 0; )
//          arr[j-1] = arr[j] + rng.nextInt(2);
//        return arr;
//      }
//    );
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

  private static void runCheck( String caseName, Function<SelectAccess,PerformanceFn> perf, Function<RandomGenerator, IntFunction<int[]>> inputGenerator ) throws IOException, InterruptedException {
    int N_SAMPLES = 1 << 16;
    var rng = new SplittableRandom();
    var gen = inputGenerator.apply(rng);

    var acc = new CountingAccessor();
    var selectors = selectors(acc);

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
      Entry<String, Function<int[],SelectAccess>>[] selectorsArr = selectors.entrySet().toArray(Entry[]::new);

      var     order = range(0,N_SAMPLES).toArray();
      randomShuffle(order,rng::nextInt);
      Progress.print( stream(order) ).forEach(i -> {
        int mid = (int) x[i];

        int[] ref = gen.apply(length);

        randomShuffle(selectorsArr, rng::nextInt);
        stream(selectorsArr).forEach( EntryConsumer.of( (k,v) -> {
          acc.nComps = 0;
          acc.nSwaps = 0;

          var tst = ref.clone();
          v.apply(tst).select(0, mid, length);

          resultsComps.get(k)[i] = acc.nComps;

          assert stream(tst,0,  mid                 ).allMatch( z -> z <= tst[mid] );
          assert stream(tst,min(mid+1,length),length).allMatch( z -> z >= tst[mid] );
        }));
      });

      var colorList = List.of(
        "#e41a1c", "#377eb8", "#4daf4a", "#984ea3", "#ff7f00", "#ffff33", "#a65628", "#f781bf", "#999999"
      );
      var col = new AtomicInteger();

      var data = selectors.entrySet().stream().flatMap(
        EntryFn.of( (method, sel) -> {
          var y = resultsComps.get(method);
          var color = colorList.get( col.getAndIncrement() );

          var result = format(
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

          int[] estRange; {
            int l = min(1024, (length>>>1) - 2),
                r = length - l;
            estRange = Stream.of(
              rangeClosed(0,l),
              new LinSpace(l+1, r-1, min(1024, r-l-1)).stream().mapToInt(Double::intValue),
              rangeClosed(r,length)
            ).flatMapToInt( i -> i ).sorted().toArray();
          }

          var estimate = format(
            """
            {
              type: 'scattergl',
              mode: 'line',
              name: '%s (estimate)',
              line: {
                color: '%s'
              },
              x: %s,
              y: %s
            }
            """,
            method,
            color,
            Arrays.toString(estRange),
            stream(estRange).mapToLong( mid -> perf.apply(sel.apply(null)).performance(0, mid, length) ).mapToObj(Long::toString).collect(joining(", ", "[", "]"))
          );

          return Stream.of(result, estimate);
        })
      ).toArray(String[]::new);



      String layout = format(
        """
        {
          title: 'Heap Select Performance Estimation<br>(length=%d, %s)',
          xaxis: {
            title: 'Split',
            color: 'lightgray',
            gridcolor: '#333'
          },
          yaxis: {
            title: 'Number of Comparisons',
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
      var ai = a[i];
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
    return Map.ofEntries(
      entry("Heap", arr -> {
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
            if( HeapSelect.performance_average(m+1,n-1) < HeapSelect.performance_average(n,m) )
              return HeapSelect.performance_worstCase(m+1,n-1);
            else
              return HeapSelect.performance_worstCase(n,m);
          }
        }
        return new Acc();
      }),
      entry("HeapMajor", arr -> {
        class Acc implements HeapSelectAccess, SelectAccess {
          @Override public void   swap( int i, int j ) {        acc.   swap(arr,i, arr,j); }
          @Override public int compare( int i, int j ) { return acc.compare(arr,i, arr,j); }
          @Override public void select( int l, int m, int r ) { heapSelectMajor(l,m,r); }
          @Override public long performance_average  ( int l, int m, int r ) { return heapSelectMajor_performance(l,m,r); }
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
      }),
      entry("HeapMinor", arr -> {
        class Acc implements HeapSelectAccess, SelectAccess {
          @Override public void   swap( int i, int j ) {        acc.   swap(arr,i, arr,j); }
          @Override public int compare( int i, int j ) { return acc.compare(arr,i, arr,j); }
          @Override public void select( int l, int m, int r ) { heapSelectMinor(l,m,r); }
          @Override public long performance_average  ( int l, int m, int r ) { return heapSelectMinor_performance(l,m,r); }
          @Override public long performance_worstCase( int from, int mid, int until ) {
            if( from < 0 || from > mid || mid > until )
              throw new IllegalArgumentException();
            if( mid == until )
              return 0;
            int m =   mid - from,
                n = until - mid;
            return m < n-2
              ? HeapSelect.performance_worstCase(m+1,n-1)
              : HeapSelect.performance_worstCase(n,m);
          }
        }
        return new Acc();
      }),
      entry("HeapRandom", arr -> {
        class Acc implements HeapSelectRandomAccess, SelectAccess {
          @Override public void   swap( int i, int j ) {        acc.   swap(arr,i, arr,j); }
          @Override public int compare( int i, int j ) { return acc.compare(arr,i, arr,j); }
          @Override public void select( int l, int m, int r ) { heapSelectRandom(l,m,r); }
          @Override public long performance_average  ( int l, int m, int r ) { return heapSelect_performance(l,m,r); }
          @Override public long performance_worstCase( int l, int m, int r ) { return heapSelect_performance(l,m,r); }
        }
        return new Acc();
      }),
      entry("MergeSelect", arr -> {
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
      })
    );
  }
}
