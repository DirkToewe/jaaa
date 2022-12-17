package com.github.jaaa.merge;

import com.github.jaaa.compare.CompareRandomAccessor;
import com.github.jaaa.fn.EntryConsumer;
import com.github.jaaa.fn.EntryFn;
import com.github.jaaa.merge.datagen.RandomMergeInputGenerator;
import com.github.jaaa.permute.Swap;
import com.github.jaaa.util.PlotlyUtils;
import com.github.jaaa.util.Progress;
import net.jqwik.api.Tuple.Tuple2;

import java.io.IOException;
import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.atomic.LongAdder;
import java.util.function.DoubleSupplier;

import static com.github.jaaa.permute.RandomShuffle.randomShuffle;
import static java.lang.Math.round;
import static java.lang.String.format;
import static java.lang.System.*;
import static java.util.Arrays.stream;
import static java.util.stream.IntStream.range;


// -ea -Xmx16g -XX:MaxInlineLevel=15
// -ea -XX:+PrintCompilation -XX:+UnlockDiagnosticVMOptions -XX:+PrintInlining
public class BenchmarkMerge
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

  private static final class CountingAccessor implements CompareRandomAccessor<int[]>
  {
    final LongAdder
      nComps = new LongAdder(),
      nWrite = new LongAdder();
    @Override public int[] malloc(int len ) { return new int[len]; }
    @Override public int     compare( int[] a, int i, int[] b, int j ) { int c = Integer.compare(a[i],b[j]);    nComps.add(1); return c; }
    @Override public void       swap( int[] a, int i, int[] b, int j ) {               Swap.swap(a,i, b,j);     nWrite.add(2); }
    @Override public void  copy     ( int[] a, int i, int[] b, int j ) {                         b[j]=a[i];     nWrite.add(1); }
    @Override public void  copyRange( int[] a, int i, int[] b, int j, int len ){       arraycopy(a,i, b,j, len);nWrite.add(len); }
  }

// STATIC METHODS
  public static void main( String... args ) throws IOException
  {
    out.println( System.getProperty("java.vm.name") + " " + System.getProperty("java.vm.version") );
    out.println( "Java " + System.getProperty("java.version") );

    Map<String,MergeFn> mergers = new LinkedHashMap<>();
    mergers.put("ParSkip", ParallelSkipMerge::merge);
    mergers.put("ParZen",  ParallelZenMerge ::merge);
//    mergers.put("ExpV2",           ExpMerge::merge);
//    mergers.put("TapeMerge",      TapeMerge::merge);
//    mergers.put("TimMerge",        TimMerge::merge);
//    mergers.put("HwangLin",   HwangLinMerge::merge);
//    mergers.put("HwangLinPart", new MergeFn() {
//      @Override public <T> void merge(
//        T a, int i, int m,
//        T b, int j, int n,
//        T c, int k, CompareRandomAccessor<T> acc
//      ) {
//        new HwangLinMergePartAccessor<T>() {
//          @Override public T malloc( int len ) { return acc.malloc(len); }
//          @Override public void   copy( T a, int i, T b, int j ) {        acc.   copy(a,i, b,j); }
//          @Override public void   swap( T a, int i, T b, int j ) {        acc.   swap(a,i, b,j); }
//          @Override public int compare( T a, int i, T b, int j ) { return acc.compare(a,i, b,j); }
//        }.hwangLinMergePartV1_L2R(a,i,m, b,j,n, c,k,m+n);
//      }
//    });
//    mergers.put("BlockRot",   BlockRotationMerge::merge);
//    mergers.put("BlockRotBias", new MergeFn() {
//      @Override public <T> void merge(
//        T a, int i, int m,
//        T b, int j, int n,
//        T c, int k, CompareRandomAccessor<T> acc ) {
//        acc.copyRange(a,i, c,k,   m);
//        acc.copyRange(b,j, c,k+m, n);
//        class Acc implements BlockRotationMergeBiasedAccess, TimMergeAccess {
//          @Override public void   swap( int i, int j ) {        acc.   swap(c,i, c,j); }
//          @Override public int compare( int i, int j ) { return acc.compare(c,i, c,j); }
//          @Override public int blockRotationMergeBiased_localMerge(int bias, int from, int mid, int until) {
//            return timMergeBiased(bias, from, mid, until);
//          }
//        }
//        new Acc().blockRotationMergeBiased(TimMergeAccess.MIN_GALLOP, k, k+m, k+m+n);
//      }
//    });
//    mergers.put("ExpV2Acc", new MergeFn() {
//      @Override public <T> void merge(
//        T a, int i, int m,
//        T b, int j, int n,
//        T c, int k, CompareRandomAccessor<T> acc
//      ) {
//        acc.copyRange(a,i, c,k,   m);
//        acc.copyRange(b,j, c,k+m, n);
//        new ExpMergeAccess() {
//          @Override public void   swap( int i, int j ) {        acc.   swap(c,i, c,j); }
//          @Override public int compare( int i, int j ) { return acc.compare(c,i, c,j); }
//        }.expMerge(k, k+m, k+m+n);
//      }
//    });
//    mergers.put("TapeAcc", new MergeFn() {
//      @Override public <T> void merge(
//        T a, int i, int m,
//        T b, int j, int n,
//        T c, int k, CompareRandomAccessor<T> acc
//      ) {
//        acc.copyRange(a,i, c,k,   m);
//        acc.copyRange(b,j, c,k+m, n);
//        new TapeMergeAccess() {
//          @Override public void   swap( int i, int j ) {        acc.   swap(c,i, c,j); }
//          @Override public int compare( int i, int j ) { return acc.compare(c,i, c,j); }
//        }.tapeMerge(k, k+m, k+m+n);
//      }
//    });
//    mergers.put("TimAcc", new MergeFn() {
//      @Override public <T> void merge(
//        T a, int i, int m,
//        T b, int j, int n,
//        T c, int k, CompareRandomAccessor<T> acc
//      ) {
//        acc.copyRange(a,i, c,k,   m);
//        acc.copyRange(b,j, c,k+m, n);
//        new TimMergeAccess() {
//          @Override public void   swap( int i, int j ) {        acc.   swap(c,i, c,j); }
//          @Override public int compare( int i, int j ) { return acc.compare(c,i, c,j); }
//        }.timMerge(k, k+m, k+m+n);
//      }
//    });
//    mergers.put("KiwiAcc", new MergeFn() {
//      @Override public <T> void merge(
//        T a, int i, int m,
//        T b, int j, int n,
//        T c, int k, CompareRandomAccessor<T> acc
//      ) {
//        acc.copyRange(a,i, c,k,   m);
//        acc.copyRange(b,j, c,k+m, n);
//        new KiwiMergeAccess() {
//          @Override public int compare( int i, int j ) { return acc.compare(c,i, c,j); }
//          @Override public void   swap( int i, int j ) { acc.swap(c,i, c,j); }
//        }.kiwiMerge(k, k+m, k+m+n);
//      }
//    });

    SplittableRandom rng = new SplittableRandom();

    DoubleSupplier[] splits = {
      new DoubleSupplier() { @Override public double getAsDouble() { return rng.nextDouble(); } @Override public String toString() { return "random"; } },
      new DoubleSupplier() { @Override public double getAsDouble() { return 0.5;              } @Override public String toString() { return format("%.2f", getAsDouble()); } },
      new DoubleSupplier() { @Override public double getAsDouble() { return 0.25;             } @Override public String toString() { return format("%.2f", getAsDouble()); } },
      new DoubleSupplier() { @Override public double getAsDouble() { return 0.75;             } @Override public String toString() { return format("%.2f", getAsDouble()); } }
    };

    for( int len = 100_000_000; len > 100; len /= 10 )
      compare_over_split(mergers, len);
//    for( var len: new int[]{ 1_000, 10_000, 100_000, 1_000_000 } ) compare_over_split (mergers, len);
//    for( var len: new int[]{ 1_000, 10_000, 100_000, 1_000_000 } ) compare_over_length(mergers, len, splits[0]);
  }

  /** Compares merging algorithms using merge sequences of constant combined length but
   *  with a different splits into left and right merge sqeuence length. In other words:
   *  given two merge sequences a and b, the sequence lengths |a| and |b| must always add
   *  up to length, i.e. |a|+|b| = length.
   *
   *  @param length The combined length of the tested merge sequences.
   */
  private static void compare_over_split( Map<String,MergeFn> mergers, final int length ) throws IOException
  {
    int N_SAMPLES = 10_000;
    SplittableRandom random = new SplittableRandom();
    RandomMergeInputGenerator rng = new RandomMergeInputGenerator(random);

    int[] x = random.ints(N_SAMPLES, 0,length+1).toArray();
    Arrays.sort(x);

    Map<String,double[]>
      resultsComps = new TreeMap<>(),
      resultsWrite = new TreeMap<>(),
      resultsTimes = new TreeMap<>();
    mergers.forEach( (k,v) -> {
      resultsComps.put(k, new double[N_SAMPLES]);
      resultsWrite.put(k, new double[N_SAMPLES]);
      resultsTimes.put(k, new double[N_SAMPLES]);
    });

    @SuppressWarnings("unchecked")
    Entry<String,MergeFn>[] mergersArr = mergers.entrySet().stream().toArray(Entry[]::new);

    CountingAccessor acc = new CountingAccessor();

    int[] order = range(0,N_SAMPLES).toArray();
    randomShuffle(order,random::nextInt);
    Progress.print( stream(order) ).forEach( i -> {
      int lenA = x[i],
          lenB = length - lenA;

      Tuple2<int[],int[]> sample = rng.next(lenA,lenB);
      int[] aRef = sample.get1(),
            bRef = sample.get2(),
            cRef = new int[lenA + lenB];

      assert aRef.length == lenA;
      assert bRef.length == lenB;
      System.arraycopy(aRef,0, cRef,0,    lenA);
      System.arraycopy(bRef,0, cRef,lenA, lenB);
      Arrays.sort(cRef);

      randomShuffle(mergersArr, random::nextInt);
      stream(mergersArr).forEach( EntryConsumer.of( (k,v) -> {
        System.gc();
        acc.nComps.reset();
        acc.nWrite.reset();

        int[] aTest = aRef.clone(),
              bTest = bRef.clone(),
              cTest = new int[lenA + lenB];

        long t0 = nanoTime();
        v.merge(
          aTest,0,aTest.length,
          bTest,0,bTest.length,
          cTest,0, acc
        );
        long dt = nanoTime() - t0;

        resultsComps.get(k)[i] = acc.nComps.sum();
        resultsWrite.get(k)[i] = acc.nWrite.sum();
        resultsTimes.get(k)[i] = dt / 1e3;

        assert Arrays.equals(aRef,aTest);
        assert Arrays.equals(bRef,bTest);
        assert Arrays.equals(cRef,cTest);
      }));
    });

    plot_results(format("Merge comparisons Benchmark (L = %d)", length), "Split", "#comparisons", x, resultsComps);
    plot_results(format(      "Merge write Benchmark (L = %d)", length), "Split", "#writes",      x, resultsWrite);
    plot_results(format(    "Merge timings Benchmark (L = %d)", length), "Split", "Time [msec.]", x, resultsTimes);
  }

  /** Compares merging algorithms using merge sequences of constant split but varying combined
   *  lengths. In other words: Given two merge sequences a and b of combined length len, then
   *  the sequence length are |a| = split*len and |b| = (1-split)*len.
   */
  private static void compare_over_length( Map<String,MergeFn> mergers, final int max_length, final DoubleSupplier nextSplit ) throws IOException
  {
    int N_SAMPLES = 10_000;
    SplittableRandom random = new SplittableRandom(1337);
    RandomMergeInputGenerator rng = new RandomMergeInputGenerator(random);

    int[] x = random.ints(N_SAMPLES, 0,max_length).toArray();
    Arrays.sort(x);

    Map<String,double[]>
      resultsComps = new TreeMap<>(),
      resultsWrite = new TreeMap<>(),
      resultsTimes = new TreeMap<>();
    mergers.forEach( (k,v) -> {
      resultsComps.put(k, new double[N_SAMPLES]);
      resultsWrite.put(k, new double[N_SAMPLES]);
      resultsTimes.put(k, new double[N_SAMPLES]);
    });

    @SuppressWarnings("unchecked")
    Entry<String,MergeFn>[] mergersArr = mergers.entrySet().stream().toArray(Entry[]::new);

    CountingAccessor acc = new CountingAccessor();

    int[] order = range(0,N_SAMPLES).toArray();
    randomShuffle(order,random::nextInt);
    Progress.print( stream(order) ).forEach( i -> {
      int length = x[i],
          lenA = (int) round(nextSplit.getAsDouble()*length),
          lenB = length - lenA;

      Tuple2<int[],int[]> sample = rng.next(lenA,lenB);
      int[] aRef = sample.get1(),
            bRef = sample.get2(),
            cRef = new int[lenA + lenB];

      assert aRef.length == lenA;
      assert bRef.length == lenB;
      System.arraycopy(aRef,0, cRef,0,    lenA);
      System.arraycopy(bRef,0, cRef,lenA, lenB);
      Arrays.sort(cRef);

      randomShuffle(mergersArr, random::nextInt);
      stream(mergersArr).forEach( EntryConsumer.of( (k,v) -> {
        acc.nComps.reset();
        acc.nWrite.reset();

        int[] aTest = aRef.clone(),
              bTest = bRef.clone(),
              cTest = new int[lenA + lenB];

        long t0 = nanoTime();
        v.merge(
          aTest,0,aTest.length,
          bTest,0,bTest.length,
          cTest,0, acc
        );
        long dt = nanoTime() - t0;

        resultsComps.get(k)[i] = acc.nComps.sum();
        resultsWrite.get(k)[i] = acc.nWrite.sum();
        resultsTimes.get(k)[i] = dt / 1e3;

        assert Arrays.equals(aRef,aTest);
        assert Arrays.equals(bRef,bTest);
        assert Arrays.equals(cRef,cTest);
      }));
    });

    plot_results(format("Merge comparisons Benchmark (split = %s)", nextSplit), "length", "#comparisons", x, resultsComps);
    plot_results(format(      "Merge write Benchmark (split = %s)", nextSplit), "length", "#writes",      x, resultsWrite);
    plot_results(format(    "Merge timings Benchmark (split = %s)", nextSplit), "length", "Time [msec.]", x, resultsTimes);
  }

  private static void plot_results( String title, String x_label, String y_label, int[] x, Map<String,double[]> ys ) throws IOException
  {
    String[] data = ys.entrySet().stream().map( EntryFn.of(
      (method,y) -> format(
        "{\n" +
        "  type: 'scattergl',\n" +
        "  mode: 'markers',\n" +
        "  name: '%s',\n" +
        "  marker: {\n" +
        "    size: 4,\n" +
        "    opacity: 0.6\n" +
        "  },\n" +
        "  x: %s,\n" +
        "  y: %s\n" +
        "}\n",
        method,
        Arrays.toString(x),
        Arrays.toString(y)
      )
    )).toArray(String[]::new);

    String layout = format(
      "{ title: '%s', xaxis: {title: '%s'}, yaxis: {title: '%s'} }",
      title,
      x_label,
      y_label
    );

    PlotlyUtils.plot(layout, data);
  }
}
