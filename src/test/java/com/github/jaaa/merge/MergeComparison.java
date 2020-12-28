package com.github.jaaa.merge;

import com.github.jaaa.Swap;
import com.github.jaaa.CompareRandomAccessor;
import com.github.jaaa.util.EntryFn;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Map;
import java.util.Random;
import java.util.TreeMap;

import static java.awt.Desktop.getDesktop;
import static java.lang.String.format;
import static java.lang.System.arraycopy;
import static java.lang.System.nanoTime;
import static java.util.stream.Collectors.joining;
import static java.util.stream.IntStream.range;
import static java.util.Map.entry;

public class MergeComparison
{
// STATIC FIELDS
  private static String PLOT_TEMPLATE
    =        "<!DOCTYPE html>"
    + "\n" + "<html lang=\"en\">"
    + "\n" + "  <head>"
    + "\n" + "    <meta charset=\"utf-8\">"
    + "\n" + "    <script src=\"https://cdn.plot.ly/plotly-latest.js\"></script>"
    + "\n" + "  </head>"
    + "\n" + "  <body>"
    + "\n" + "    <script>"
    + "\n" + "      'use strict';"
    + "\n" + "\n"
    + "\n" + "      const plot = document.createElement('div');"
    + "\n" + "      plot.style = 'width: 100%%; height: 95vh;';"
    + "\n" + "      document.body.appendChild(plot);"
    + "\n" + "\n"
    + "\n" + "      const layout = %1$s;"
    + "\n" + "      document.title = layout.title;"
    + "\n" + "\n"
    + "\n" + "      Plotly.plot(plot, {layout, data: %2$s});"
    + "\n" + "    </script>"
    + "\n" + "  </body>"
    + "\n" + "</html>"
    + "\n";

// STATIC CONSTRUCTOR
  static {
    boolean ea = false;
    assert  ea = true;
      if( ! ea ) throw new AssertionError("Assertions not enabled.");
  }

  private static interface MergeFn
  {
    public <T> void merge(
      T a, int i, int m,
      T b, int j, int n,
      T c, int k, CompareRandomAccessor<T> cmp
    );
  }

  private static final class CountingSortAccessor implements CompareRandomAccessor<double[]>
  {
    long nComps = 0,
         nWrite = 0;
    @Override public int         len( double[] buf ) { return buf.length; }
    @Override public int     compare( double[] a, int i, double[] b, int j ) { int c = Double.compare(a[i],b[j]); nComps+=1; return c; }
    @Override public void       swap( double[] a, int i, double[] b, int j ) {              Swap.swap(a,i, b,j);  nWrite+=2; }
    @Override public void  copy     ( double[] a, int i, double[] b, int j ) {                        b[j]=a[i];  nWrite+=1; }
    @Override public void  copyRange( double[] a, int i, double[] b, int j, int len ){ arraycopy(a,i, b,j, len);  nWrite+=len; }
  }

// STATIC METHODS
  public static void main( String... args ) throws IOException
  {
    var rng = new RandomMergeInputGenerator(1337);

    Map<String,MergeFn> mergers = Map.ofEntries(
      entry("Binary",         BinaryMerge::merge),
      entry("ExpV1",          ExpMergeV1::merge),
      entry("ExpV2",          ExpMergeV2::merge),
      entry("ExpV3",          ExpMergeV3::merge),
      entry("ExpV4",          ExpMergeV4::merge),
      entry("HwangLinV1",HwangLinMergeV1::merge),
      entry("HwangLinV2",HwangLinMergeV2::merge),
      entry("RecMergeV1",     RecMergeV1::merge),
      entry("RecMergeV2",     RecMergeV2::merge),
      entry("RecMergeV3",     RecMergeV3::merge),
      entry("RecMergeV4",     RecMergeV4::merge),
      entry("TapeMerge",       TapeMerge::merge),
      entry("TimMerge",         TimMerge::merge)
    );

    int N_SAMPLES =  10_000,
              LEN = 100_000;

    int[] x; {                    var _rng = new Random();
      x = range(0,N_SAMPLES).map(i -> _rng.nextInt(LEN+1) ).toArray();
      Arrays.sort(x);
    };
    Map<String,double[]>
      resultsComps = new TreeMap<>(),
      resultsWrite = new TreeMap<>(),
      resultsTimes = new TreeMap<>();
    mergers.forEach( (k,v) -> {
      resultsComps.put(k, new double[N_SAMPLES]);
      resultsWrite.put(k, new double[N_SAMPLES]);
      resultsTimes.put(k, new double[N_SAMPLES]);
    });

    var acc = new CountingSortAccessor();

    range(0,N_SAMPLES).forEach( i -> {
      if( (i+1) % 10 == 0 )
        System.out.printf("%5d / %d\n", i+1, N_SAMPLES);

      int lenA = x[i],
          lenB = LEN - lenA;

      var sample = rng.next(lenA,lenB);
      double[] aRef = sample.get1(),
               bRef = sample.get2(),
               cRef = new double[lenA + lenB];

//      double[] aRef = range(   0,     lenA).mapToDouble(z->z).toArray(),
//               bRef = range(lenA,lenA+lenB).mapToDouble(z->z).toArray(),
//               cRef = new double[lenA + lenB];

//      double[] aRef = range(lenB,lenB+lenA).mapToDouble(z->z).toArray(),
//               bRef = range(   0,lenB     ).mapToDouble(z->z).toArray(),
//               cRef = new double[lenA + lenB];

      assert aRef.length == lenA;
      assert bRef.length == lenB;
      System.arraycopy(aRef,0, cRef,0,    lenA);
      System.arraycopy(bRef,0, cRef,lenA, lenB);
      Arrays.sort(cRef);

      mergers.forEach( (k,v) -> {
        acc.nComps = 0;
        acc.nWrite = 0;

        double[] aTest = aRef.clone(),
                 bTest = bRef.clone(),
                 cTest = new double[lenA + lenB];

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
      });
    });

    plot_results("comparisons", x, resultsComps);
//    plot_results("write",       x, resultsWrite);
    plot_results("timings",     x, resultsTimes);
  }

  private static void plot_results( String type, int[] x, Map<String,double[]> results ) throws IOException
  {
    String data = results.entrySet().stream().map( EntryFn.of(
      (method,y) -> format(
        "{\n  type: 'scatter2d',\n  name: '%s',\n  x: %s,\n  y: %s\n}",
        method,
        Arrays.toString(x),
        Arrays.toString(y)
      )
    )).collect( joining(",", "[", "]") );

    String layout = format(
      "{ title: 'Merge %s Benchmark (L = %d)', xaxis: {title: 'Split Position'}, yaxis: {title: 'Time [msec.]'} }",
      type,
      x.length
    );

    Path tmp = Files.createTempFile("merge_"+type+"_", ".html");
    Files.writeString(  tmp, format(PLOT_TEMPLATE, layout, data) );
    getDesktop().browse(tmp.toUri());
  }
}
