package com.github.jaaa.merge;

import com.github.jaaa.CompareRandomAccessor;
import com.github.jaaa.Swap;
import com.github.jaaa.fn.EntryFn;
import com.github.jaaa.util.PlotlyUtils;
import com.github.jaaa.util.Progress;
import net.jqwik.api.Tuple;
import net.jqwik.api.Tuple.Tuple3;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

import static com.github.jaaa.merge.ParallelRebelMerge.PARALLEL_REBEL_MERGER;
import static com.github.jaaa.merge.ParallelSkipMerge.PARALLEL_SKIP_MERGER;
import static com.github.jaaa.merge.ParallelZenMerge.PARALLEL_ZEN_MERGER;
import static com.github.jaaa.misc.Shuffle.shuffled;
import static java.awt.Desktop.getDesktop;
import static java.lang.String.format;
import static java.lang.System.arraycopy;
import static java.lang.System.nanoTime;
import static java.util.Arrays.stream;
import static java.util.Comparator.comparingInt;
import static java.util.Map.entry;
import static java.util.stream.IntStream.range;

public class ParallelMergeComparison
{
// STATIC FIELDS

// STATIC CONSTRUCTOR
  static {
    boolean ea = false;
    assert  ea = true;
      if( ! ea ) throw new AssertionError("Assertions not enabled.");
  }

  private interface MergeFn
  {
    <T> void merge(
      T a, int i, int m,
      T b, int j, int n,
      T c, int k, CompareRandomAccessor<T> cmp
    );
  }

  private interface CompRandAccessor<T> extends CompareRandomAccessor<T[]>
  {
    @Override default T[] malloc( int len ) { throw new UnsupportedOperationException(); }
    @Override default void       swap( T[] a, int i, T[] b, int j ) { Swap.swap(a,i, b,j); }
    @Override default void  copy     ( T[] a, int i, T[] b, int j ) { b[j]=a[i]; }
    @Override default void  copyRange( T[] a, int i, T[] b, int j, int len ){ arraycopy(a,i, b,j, len); }
  }

  private static final class CompRandccessorDouble implements CompareRandomAccessor<double[]>
  {
    @Override public double[] malloc(int len ) { return new double[len]; }
    @Override public int     compare( double[] a, int i, double[] b, int j ) { int c = Double.compare(a[i],b[j]); /*nComps+=1;  */ return c; }
    @Override public void       swap( double[] a, int i, double[] b, int j ) {              Swap.swap(a,i, b,j);  /*nWrite+=2;  */ }
    @Override public void  copy     ( double[] a, int i, double[] b, int j ) {                        b[j]=a[i];  /*nWrite+=1;  */ }
    @Override public void  copyRange( double[] a, int i, double[] b, int j, int len ){ arraycopy(a,i, b,j, len);  /*nWrite+=len;*/ }
  }

  // STATIC METHODS
  public static void main( String... args ) throws IOException
  {
    var rng = new SplittableRandom();
    var gen = new RandomMergeInputGenerator(rng);

    Map<String,MergeFn> mergers = Map.ofEntries(
      entry("RecV1", PARALLEL_REBEL_MERGER::merge),
      entry("RecV2", PARALLEL_ZEN_MERGER  ::merge),
      entry("Skip",  PARALLEL_SKIP_MERGER ::merge)
    );

    int N_SAMPLES = 1_000,
              LEN = 100_000_000;

    int[] x = rng.ints(N_SAMPLES, 0,LEN+1).sorted().toArray();//range(0,N_SAMPLES).map(i -> _rng.nextInt(LEN+1) ).toArray();

    Map<String,double[]>
      resultsComps = new TreeMap<>(),
      resultsWrite = new TreeMap<>(),
      resultsTimes = new TreeMap<>();
    mergers.forEach( (k,v) -> {
      resultsComps.put(k, new double[N_SAMPLES]);
      resultsWrite.put(k, new double[N_SAMPLES]);
      resultsTimes.put(k, new double[N_SAMPLES]);
    });

    Comparator<Tuple3<Integer,Integer,Integer>>  cmp; {
    Comparator<Tuple3<Integer,Integer,Integer>>
      _cmp =          comparingInt(Tuple3::get1);
      _cmp = _cmp.thenComparingInt(Tuple3::get2);
      _cmp = _cmp.thenComparingInt(Tuple3::get3);
       cmp = _cmp;
    }

    CompRandAccessor<Tuple3<Integer,Integer,Integer>> acc = (a,i, b,j) -> cmp.compare(a[i],b[j]);

    Progress.print( stream( shuffled(range(0,N_SAMPLES).toArray()) ) ).forEach(i -> {
      int lenA = x[i],
          lenB = LEN - lenA;

      @SuppressWarnings("unchecked")
      Tuple3<Integer,Integer,Integer>[]
         aRef = range(0,lenA).mapToObj( z -> Tuple.of( rng.nextInt(256), rng.nextInt(256), rng.nextInt() ) ).toArray(Tuple3[]::new),
         bRef = range(0,lenB).mapToObj( z -> Tuple.of( rng.nextInt(256), rng.nextInt(256), rng.nextInt() ) ).toArray(Tuple3[]::new);

//      var sample = gen.next(lenA,lenB);
//      double[] aRef = sample.get1(),
//               bRef = sample.get2();
//               cRef = new double[lenA + lenB];

//      double[] aRef = range(   0,     lenA).mapToDouble(z->z).toArray(),
//               bRef = range(lenA,lenA+lenB).mapToDouble(z->z).toArray(),
//               cRef = new double[lenA + lenB];

//      double[] aRef = range(lenB,lenB+lenA).mapToDouble(z->z).toArray(),
//               bRef = range(   0,lenB     ).mapToDouble(z->z).toArray(),
//               cRef = new double[lenA + lenB];

      assert aRef.length == lenA;
      assert bRef.length == lenB;
//      System.arraycopy(aRef,0, cRef,0,    lenA);
//      System.arraycopy(bRef,0, cRef,lenA, lenB);
//      Arrays.sort(cRef);

      mergers.forEach( (k,v) -> {
//        acc.nComps = 0;
//        acc.nWrite = 0;
//        System.out.println("  "+k);

        @SuppressWarnings("unchecked")
        Tuple3<Integer,Integer,Integer>[]
          aTest = aRef.clone(),
          bTest = bRef.clone(),
          cTest = new Tuple3[lenA + lenB];

        long t0 = nanoTime();
        v.merge(
          aTest,0,aTest.length,
          bTest,0,bTest.length,
          cTest,0, acc
        );
        long dt = nanoTime() - t0;

//        resultsComps.get(k)[i] = acc.nComps;
//        resultsWrite.get(k)[i] = acc.nWrite;
        resultsTimes.get(k)[i] = dt / 1e3;

        assert Arrays.equals(aRef,aTest);
        assert Arrays.equals(bRef,bTest);
//        assert Arrays.equals(cRef,cTest);
      });
    });

    plot_results("comparisons", x, resultsComps);
    plot_results("write",       x, resultsWrite);
    plot_results("timings",     x, resultsTimes);
  }

  private static void plot_results( String type, int[] x, Map<String,double[]> results ) throws IOException
  {
    var data = results.entrySet().stream().map( EntryFn.of(
      (method,y) -> format(
        "{\n  type: 'scatter2d',\n  name: '%s',\n  x: %s,\n  y: %s\n}",
        method,
        Arrays.toString(x),
        Arrays.toString(y)
      )
    )).toArray(String[]::new);

    String layout = format(
      "{ title: 'Merge %s Benchmark (L = %d)', xaxis: {title: 'Split Position'}, yaxis: {title: 'Time [msec.]'} }",
      type,
      x.length
    );

    Path tmp = Files.createTempFile("merge_"+type+"_", ".html");
    PlotlyUtils.plot(layout,data);
    getDesktop().browse(tmp.toUri());
  }
}
