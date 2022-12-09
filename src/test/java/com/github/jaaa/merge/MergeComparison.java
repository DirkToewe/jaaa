package com.github.jaaa.merge;

import com.github.jaaa.compare.CompareRandomAccessor;
import com.github.jaaa.permute.Swap;
import com.github.jaaa.fn.EntryConsumer;
import com.github.jaaa.fn.EntryFn;
import com.github.jaaa.util.PlotlyUtils;
import com.github.jaaa.util.Progress;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Map;
import java.util.Map.Entry;
import java.util.SplittableRandom;
import java.util.TreeMap;
import java.util.function.DoubleSupplier;

import static com.github.jaaa.permute.RandomShuffle.randomShuffle;
import static java.awt.Desktop.getDesktop;
import static java.lang.Math.round;
import static java.lang.String.format;
import static java.lang.System.*;
import static java.util.Arrays.stream;
import static java.util.Map.entry;
import static java.util.stream.IntStream.range;


// -ea -Xmx16g -XX:MaxInlineLevel=15
// -ea -XX:+PrintCompilation -XX:+UnlockDiagnosticVMOptions -XX:+PrintInlining
public class MergeComparison
{
// STATIC FIELDS

// STATIC CONSTRUCTOR
  static {
    var    ea = false;
    assert ea = true;
      if( !ea ) throw new AssertionError("Assertions not enabled.");
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
    long nComps = 0,
         nWrite = 0;
    @Override public int[] malloc(int len ) { return new int[len]; }
    @Override public int     compare( int[] a, int i, int[] b, int j ) { int c = Integer.compare(a[i],b[j]);    nComps+=1; return c; }
    @Override public void       swap( int[] a, int i, int[] b, int j ) {               Swap.swap(a,i, b,j);     nWrite+=2; }
    @Override public void  copy     ( int[] a, int i, int[] b, int j ) {                         b[j]=a[i];     nWrite+=1; }
    @Override public void  copyRange( int[] a, int i, int[] b, int j, int len ){       arraycopy(a,i, b,j, len);nWrite+=len; }
  }

// STATIC METHODS
  public static void main( String... args ) throws IOException
  {
    out.println( System.getProperty("java.vm.name") + " " + System.getProperty("java.vm.version") );
    out.println( "Java " + System.getProperty("java.version") );

    Map<String,MergeFn> mergers = Map.ofEntries(
      entry("ExpV1",              ExpMergeV1::merge),
      entry("ExpV2",              ExpMergeV2::merge),
      entry("ExpV4",              ExpMergeV4::merge),
      entry("TapeMerge",           TapeMerge::merge),
      entry("TimMerge",             TimMerge::merge),
      entry("HwangLinV1",HwangLinMerge      ::merge),
      entry("HwangLinV2",HwangLinStaticMerge::merge),
      entry("BlockRot",   BlockRotationMerge::merge),
      entry("ExpV1Acc", new MergeFn() {
        @Override public <T> void merge(
          T a, int i, int m,
          T b, int j, int n,
          T c, int k, CompareRandomAccessor<T> acc
        ) {
          acc.copyRange(a,i, c,k,   m);
          acc.copyRange(b,j, c,k+m, n);
          new ExpMergeV1Access() {
            @Override public void   swap( int i, int j ) {        acc.   swap(c,i, c,j); }
            @Override public int compare( int i, int j ) { return acc.compare(c,i, c,j); }
          }.expMergeV1(k, k+m, k+m+n);
        }
      }),
      entry("ExpV2Acc", new MergeFn() {
        @Override public <T> void merge(
          T a, int i, int m,
          T b, int j, int n,
          T c, int k, CompareRandomAccessor<T> acc
        ) {
          acc.copyRange(a,i, c,k,   m);
          acc.copyRange(b,j, c,k+m, n);
          new ExpMergeV2Access() {
            @Override public void   swap( int i, int j ) {        acc.   swap(c,i, c,j); }
            @Override public int compare( int i, int j ) { return acc.compare(c,i, c,j); }
          }.expMergeV2(k, k+m, k+m+n);
        }
      }),
      entry("ExpV4Acc", new MergeFn() {
        @Override public <T> void merge(
          T a, int i, int m,
          T b, int j, int n,
          T c, int k, CompareRandomAccessor<T> acc
        ) {
          acc.copyRange(a,i, c,k,   m);
          acc.copyRange(b,j, c,k+m, n);
          new ExpMergeV4Access() {
            @Override public void   swap( int i, int j ) {        acc.   swap(c,i, c,j); }
            @Override public int compare( int i, int j ) { return acc.compare(c,i, c,j); }
          }.expMergeV4(k, k+m, k+m+n);
        }
      }),
      entry("TapeAcc", new MergeFn() {
        @Override public <T> void merge(
          T a, int i, int m,
          T b, int j, int n,
          T c, int k, CompareRandomAccessor<T> acc
        ) {
          acc.copyRange(a,i, c,k,   m);
          acc.copyRange(b,j, c,k+m, n);
          new TapeMergeAccess() {
            @Override public void   swap( int i, int j ) {        acc.   swap(c,i, c,j); }
            @Override public int compare( int i, int j ) { return acc.compare(c,i, c,j); }
          }.tapeMerge(k, k+m, k+m+n);
        }
      }),
      entry("TimAcc", new MergeFn() {
        @Override public <T> void merge(
                T a, int i, int m,
                T b, int j, int n,
                T c, int k, CompareRandomAccessor<T> acc
        ) {
          acc.copyRange(a,i, c,k,   m);
          acc.copyRange(b,j, c,k+m, n);
          new TimMergeAccess() {
            @Override public void   swap( int i, int j ) {        acc.   swap(c,i, c,j); }
            @Override public int compare( int i, int j ) { return acc.compare(c,i, c,j); }
          }.timMerge(k, k+m, k+m+n);
        }
      }),
      entry("StableOptBlockAcc", new MergeFn() {
        @Override public <T> void merge(
          T a, int i, int m,
          T b, int j, int n,
          T c, int k, CompareRandomAccessor<T> acc
        ) {
          acc.copyRange(a,i, c,k,   m);
          acc.copyRange(b,j, c,k+m, n);
          new StableOptimalBlockMergeAccess() {
            @Override public int compare( int i, int j ) { return acc.compare(c,i, c,j); }
            @Override public void   swap( int i, int j ) {        acc.   swap(c,i, c,j); }
          }.stableOptimalBlockMerge(k, k+m, k+m+n);
        }
      }),
      entry("WikiV1Acc", new MergeFn() {
        @Override public <T> void merge(
          T a, int i, int m,
          T b, int j, int n,
          T c, int k, CompareRandomAccessor<T> acc
        ) {
          acc.copyRange(a,i, c,k,   m);
          acc.copyRange(b,j, c,k+m, n);
          new WikiMergeV1Access() {
            @Override public int compare( int i, int j ) { return acc.compare(c,i, c,j); }
            @Override public void   swap( int i, int j ) {        acc.   swap(c,i, c,j); }
          }.wikiMergeV1(k, k+m, k+m+n);
        }
      }),
      entry("WikiV2Acc", new MergeFn() {
        @Override public <T> void merge(
          T a, int i, int m,
          T b, int j, int n,
          T c, int k, CompareRandomAccessor<T> acc
        ) {
          acc.copyRange(a,i, c,k,   m);
          acc.copyRange(b,j, c,k+m, n);
          new WikiMergeV2Access() {
            @Override public int compare( int i, int j ) { return acc.compare(c,i, c,j); }
            @Override public void   swap( int i, int j ) {        acc.   swap(c,i, c,j); }
          }.wikiMergeV2(k, k+m, k+m+n);
        }
      }),
      entry("WikiV3Acc", new MergeFn() {
        @Override public <T> void merge(
          T a, int i, int m,
          T b, int j, int n,
          T c, int k, CompareRandomAccessor<T> acc
        ) {
          acc.copyRange(a,i, c,k,   m);
          acc.copyRange(b,j, c,k+m, n);
          new WikiMergeV3Access() {
            @Override public int compare( int i, int j ) { return acc.compare(c,i, c,j); }
            @Override public void   swap( int i, int j ) {        acc.   swap(c,i, c,j); }
          }.wikiMergeV3(k, k+m, k+m+n);
        }
      }),
      entry("WikiV4Acc", new MergeFn() {
        @Override public <T> void merge(
          T a, int i, int m,
          T b, int j, int n,
          T c, int k, CompareRandomAccessor<T> acc
        ) {
          acc.copyRange(a,i, c,k,   m);
          acc.copyRange(b,j, c,k+m, n);
          new WikiMergeV4Access() {
            @Override public int compare( int i, int j ) { return acc.compare(c,i, c,j); }
            @Override public void   swap( int i, int j ) {        acc.   swap(c,i, c,j); }
          }.wikiMergeV4(k, k+m, k+m+n);
        }
      }),
      entry("WikiV5Acc", new MergeFn() {
        @Override public <T> void merge(
          T a, int i, int m,
          T b, int j, int n,
          T c, int k, CompareRandomAccessor<T> acc
        ) {
          acc.copyRange(a,i, c,k,   m);
          acc.copyRange(b,j, c,k+m, n);
          new WikiMergeV5Access() {
            @Override public int compare( int i, int j ) { return acc.compare(c,i, c,j); }
            @Override public void   swap( int i, int j ) {        acc.   swap(c,i, c,j); }
          }.wikiMergeV5(k, k+m, k+m+n);
        }
      }),
      entry("KiwiAcc", new MergeFn() {
        @Override public <T> void merge(
          T a, int i, int m,
          T b, int j, int n,
          T c, int k, CompareRandomAccessor<T> acc
        ) {
          acc.copyRange(a,i, c,k,   m);
          acc.copyRange(b,j, c,k+m, n);
          new KiwiMergeAccess() {
            @Override public int compare( int i, int j ) { return acc.compare(c,i, c,j); }
            @Override public void   swap( int i, int j ) { acc.swap(c,i, c,j); }
          }.kiwiMerge(k, k+m, k+m+n);
        }
      })
    );

    var rng = new SplittableRandom();

    DoubleSupplier[] splits = {
      new DoubleSupplier() { @Override public double getAsDouble() { return rng.nextDouble(); } @Override public String toString() { return "random"; } },
      new DoubleSupplier() { @Override public double getAsDouble() { return 0.5;              } @Override public String toString() { return format("%.2f", getAsDouble()); } },
      new DoubleSupplier() { @Override public double getAsDouble() { return 0.25;             } @Override public String toString() { return format("%.2f", getAsDouble()); } },
      new DoubleSupplier() { @Override public double getAsDouble() { return 0.75;             } @Override public String toString() { return format("%.2f", getAsDouble()); } }
    };


    for( var len: new int[]{ /*1_000,*/ 10_000, 100_000 } )
    {
      compare_over_length(mergers, len, splits[0]);
      compare_over_split(mergers, len);
    }
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
    var random = new SplittableRandom();
    var rng = new RandomMergeInputGenerator(random);

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
    Entry<String,MergeFn>[] mergersArr = mergers.entrySet().toArray(Entry[]::new);

    var acc = new CountingAccessor();

    var order = range(0,N_SAMPLES).toArray();
    randomShuffle(order,random::nextInt);
    Progress.print( stream(order) ).forEach( i -> {
      int lenA = x[i],
          lenB = length - lenA;

      var sample = rng.next(lenA,lenB);
      int[] aRef = sample.get1(),
            bRef = sample.get2(),
            cRef = new int[lenA + lenB];

      assert aRef.length == lenA;
      assert bRef.length == lenB;
      System.arraycopy(aRef,0, cRef,0,    lenA);
      System.arraycopy(bRef,0, cRef,lenA, lenB);
      Arrays.sort(cRef);

      randomShuffle(mergersArr, random::nextInt);
      stream(mergersArr).forEach( EntryConsumer.of( (k, v) -> {
        acc.nComps = 0;
        acc.nWrite = 0;

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

        resultsComps.get(k)[i] = acc.nComps;
        resultsWrite.get(k)[i] = acc.nWrite;
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
    var random = new SplittableRandom(1337);
    var rng = new RandomMergeInputGenerator(random);

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
    Entry<String,MergeFn>[] mergersArr = mergers.entrySet().toArray(Entry[]::new);

    var acc = new CountingAccessor();

    var order = range(0,N_SAMPLES).toArray();
    randomShuffle(order,random::nextInt);
    Progress.print( stream(order) ).forEach( i -> {
      int length = x[i],
          lenA = (int) round(nextSplit.getAsDouble()*length),
          lenB = length - lenA;

      var sample = rng.next(lenA,lenB);
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
        acc.nComps = 0;
        acc.nWrite = 0;

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

        resultsComps.get(k)[i] = acc.nComps;
        resultsWrite.get(k)[i] = acc.nWrite;
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
    var data = ys.entrySet().stream().map( EntryFn.of(
      (method,y) -> format(
        """
        {
          type: 'scattergl',
          mode: 'markers',
          name: '%s',
          marker: {
            size: 4,
            opacity: 0.6
          },
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
      "{ title: '%s', xaxis: {title: '%s'}, yaxis: {title: '%s'} }",
      title,
      x_label,
      y_label
    );

    Path tmp = Files.createTempFile(title+" ", ".html");
    PlotlyUtils.plot(tmp, layout, data);
    getDesktop().browse(tmp.toUri());
  }
}
