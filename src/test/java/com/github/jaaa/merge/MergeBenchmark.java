package com.github.jaaa.merge;

import com.github.jaaa.permute.Swap;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Param;

public class MergeBenchmark extends MergeBenchmarkTemplate
{
  @Param({"10","100","1000"}) private int lenA;
  @Param({"10","100","1000"}) private int lenB;

  @Override protected int lenA() { return lenA; }
  @Override protected int lenB() { return lenB; }


  private static final HwangLinMergeAccessor<Double[]> HWANG_LIN_COMPARABLE_DOUBLE = new HwangLinMergeAccessor<>() {
    @Override public Double[] malloc( int len ) { return new Double[len]; }
    @Override public void copy     ( Double[] a, int i, Double[] b, int j ) { b[j] = a[i]; }
    @Override public void copyRange( Double[] a, int i, Double[] b, int j, int len ) { System.arraycopy(a,i, b,j, len); }
    @Override public void      swap( Double[] a, int i, Double[] b, int j ) { Swap.swap(a,i, b,j); }
    @Override public int    compare( Double[] a, int i, Double[] b, int j ) { return Double.compare(a[i], b[j]); }
  };

//  @Benchmark
//  public void bench_hwangLin()
//  {
//    assert testA.length == lenA;
//    assert testB.length == lenB;
//
//    HwangLinMerge.merge(testA,0,testA.length, testB,0,testB.length, testC,0, Double::compare);
//  }
//
//  @Benchmark
//  public void bench_hwangLinAccessor()
//  {
//    assert testA.length == lenA;
//    assert testB.length == lenB;
//
//    HWANG_LIN_COMPARABLE_DOUBLE.hwangLinMerge(testA,0,testA.length, testB,0,testB.length, testC,0);
//  }

  @Benchmark
  public void bench_hwangLinAccessorL2R()
  {
    assert testA.length == lenA;
    assert testB.length == lenB;

    HWANG_LIN_COMPARABLE_DOUBLE.hwangLinStaticMergeL2R(testA,0,testA.length, testB,0,testB.length, testC,0);
  }

  @Benchmark
  public void bench_hwangLinAccessorR2L()
  {
    assert testA.length == lenA;
    assert testB.length == lenB;

    HWANG_LIN_COMPARABLE_DOUBLE.hwangLinStaticMergeR2L(testA,0,testA.length, testB,0,testB.length, testC,0);
  }


  private static final TapeMergeAccessor<Double[]> TAPE_COMPARABLE_DOUBLE = new TapeMergeAccessor<>() {
    @Override public Double[] malloc( int len ) { return new Double[len]; }
    @Override public void copy     ( Double[] a, int i, Double[] b, int j ) { b[j] = a[i]; }
    @Override public void copyRange( Double[] a, int i, Double[] b, int j, int len ) { System.arraycopy(a,i, b,j, len); }
    @Override public void      swap( Double[] a, int i, Double[] b, int j ) { Swap.swap(a,i, b,j); }
    @Override public int    compare( Double[] a, int i, Double[] b, int j ) { return Double.compare(a[i], b[j]); }
  };

//  @Benchmark
//  public void bench_tape()
//  {
//    assert testA.length == lenA;
//    assert testB.length == lenB;
//
//    TapeMerge.merge(testA,0,testA.length, testB,0,testB.length, testC,0, Double::compare);
//  }
//
//  @Benchmark
//  public void bench_tapeAccessor()
//  {
//    assert testA.length == lenA;
//    assert testB.length == lenB;
//
//    TAPE_COMPARABLE_DOUBLE.tapeMerge(testA,0,testA.length, testB,0,testB.length, testC,0);
//  }

  @Benchmark
  public void bench_tapeAccessorL2R()
  {
    assert testA.length == lenA;
    assert testB.length == lenB;

    TAPE_COMPARABLE_DOUBLE.tapeMergeL2R(testA,0,testA.length, testB,0,testB.length, testC,0);
  }

  @Benchmark
  public void bench_tapeAccessorR2L()
  {
    assert testA.length == lenA;
    assert testB.length == lenB;

    TAPE_COMPARABLE_DOUBLE.tapeMergeR2L(testA,0,testA.length, testB,0,testB.length, testC,0);
  }


  private static final ExpMergeAccessor<Double[]> EXP_COMPARABLE_DOUBLE = new ExpMergeAccessor<>() {
    @Override public Double[] malloc( int len ) { return new Double[len]; }
    @Override public void copy     ( Double[] a, int i, Double[] b, int j ) { b[j] = a[i]; }
    @Override public void copyRange( Double[] a, int i, Double[] b, int j, int len ) { System.arraycopy(a,i, b,j, len); }
    @Override public void      swap( Double[] a, int i, Double[] b, int j ) { Swap.swap(a,i, b,j); }
    @Override public int    compare( Double[] a, int i, Double[] b, int j ) { return Double.compare(a[i], b[j]); }
  };

  @Benchmark
  public void bench_expAccessorL2R()
  {
    assert testA.length == lenA;
    assert testB.length == lenB;

    EXP_COMPARABLE_DOUBLE.expMerge_L2R(testA,0,testA.length, testB,0,testB.length, testC,0);
  }

  @Benchmark
  public void bench_expAccessorR2L()
  {
    assert testA.length == lenA;
    assert testB.length == lenB;

    EXP_COMPARABLE_DOUBLE.expMerge_R2L(testA,0,testA.length, testB,0,testB.length, testC,0);
  }
}
