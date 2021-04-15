package com.github.jaaa.merge;

import com.github.jaaa.Swap;
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

    HWANG_LIN_COMPARABLE_DOUBLE.hwangLinMergeL2R(testA,0,testA.length, testB,0,testB.length, testC,0);
  }

  @Benchmark
  public void bench_hwangLinAccessorR2L()
  {
    assert testA.length == lenA;
    assert testB.length == lenB;

    HWANG_LIN_COMPARABLE_DOUBLE.hwangLinMergeR2L(testA,0,testA.length, testB,0,testB.length, testC,0);
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


  private static final RecMergeAccessor<Double[]> REC_COMPARABLE_DOUBLE = new RecMergeAccessor<>() {
    @Override public Double[] malloc( int len ) { return new Double[len]; }
    @Override public void copy     ( Double[] a, int i, Double[] b, int j ) { b[j] = a[i]; }
    @Override public void copyRange( Double[] a, int i, Double[] b, int j, int len ) { System.arraycopy(a,i, b,j, len); }
    @Override public void      swap( Double[] a, int i, Double[] b, int j ) { Swap.swap(a,i, b,j); }
    @Override public int    compare( Double[] a, int i, Double[] b, int j ) { return Double.compare(a[i], b[j]); }
  };

//  @Benchmark
//  public void bench_rec()
//  {
//    assert testA.length == lenA;
//    assert testB.length == lenB;
//
//    RecMerge.merge(testA,0,testA.length, testB,0,testB.length, testC,0, Double::compare);
//  }
//
//  @Benchmark
//  public void bench_recAccessor()
//  {
//    assert testA.length == lenA;
//    assert testB.length == lenB;
//
//    REC_COMPARABLE_DOUBLE.recMerge(testA,0,testA.length, testB,0,testB.length, testC,0);
//  }

  @Benchmark
  public void bench_recAccessorL2R()
  {
    assert testA.length == lenA;
    assert testB.length == lenB;

    REC_COMPARABLE_DOUBLE.recMergeL2R(testA,0,testA.length, testB,0,testB.length, testC,0);
  }

  @Benchmark
  public void bench_recAccessorR2L()
  {
    assert testA.length == lenA;
    assert testB.length == lenB;

    REC_COMPARABLE_DOUBLE.recMergeR2L(testA,0,testA.length, testB,0,testB.length, testC,0);
  }


  private static final ExpMergeV1Accessor<Double[]> EXP_V1_COMPARABLE_DOUBLE = new ExpMergeV1Accessor<>() {
    @Override public Double[] malloc( int len ) { return new Double[len]; }
    @Override public void copy     ( Double[] a, int i, Double[] b, int j ) { b[j] = a[i]; }
    @Override public void copyRange( Double[] a, int i, Double[] b, int j, int len ) { System.arraycopy(a,i, b,j, len); }
    @Override public void      swap( Double[] a, int i, Double[] b, int j ) { Swap.swap(a,i, b,j); }
    @Override public int    compare( Double[] a, int i, Double[] b, int j ) { return Double.compare(a[i], b[j]); }
  };

//  @Benchmark
//  public void bench_expV1()
//  {
//    assert testA.length == lenA;
//    assert testB.length == lenB;
//
//    ExpMergeV1.merge(testA,0,testA.length, testB,0,testB.length, testC,0, Double::compare);
//  }
//
//  @Benchmark
//  public void bench_expV1Accessor()
//  {
//    assert testA.length == lenA;
//    assert testB.length == lenB;
//
//    EXP_V1_COMPARABLE_DOUBLE.expMergeV1(testA,0,testA.length, testB,0,testB.length, testC,0);
//  }

  @Benchmark
  public void bench_expV1AccessorL2R()
  {
    assert testA.length == lenA;
    assert testB.length == lenB;

    EXP_V1_COMPARABLE_DOUBLE.expMergeV1_L2R(testA,0,testA.length, testB,0,testB.length, testC,0);
  }

  @Benchmark
  public void bench_expV1AccessorR2L()
  {
    assert testA.length == lenA;
    assert testB.length == lenB;

    EXP_V1_COMPARABLE_DOUBLE.expMergeV1_R2L(testA,0,testA.length, testB,0,testB.length, testC,0);
  }


  private static final ExpMergeV2Accessor<Double[]> EXP_V2_COMPARABLE_DOUBLE = new ExpMergeV2Accessor<>() {
    @Override public Double[] malloc( int len ) { return new Double[len]; }
    @Override public void copy     ( Double[] a, int i, Double[] b, int j ) { b[j] = a[i]; }
    @Override public void copyRange( Double[] a, int i, Double[] b, int j, int len ) { System.arraycopy(a,i, b,j, len); }
    @Override public void      swap( Double[] a, int i, Double[] b, int j ) { Swap.swap(a,i, b,j); }
    @Override public int    compare( Double[] a, int i, Double[] b, int j ) { return Double.compare(a[i], b[j]); }
  };

//  @Benchmark
//  public void bench_expV2()
//  {
//    assert testA.length == lenA;
//    assert testB.length == lenB;
//
//    ExpMergeV2.merge(testA,0,testA.length, testB,0,testB.length, testC,0, Double::compare);
//  }
//
//  @Benchmark
//  public void bench_expV2Accessor()
//  {
//    assert testA.length == lenA;
//    assert testB.length == lenB;
//
//    EXP_V2_COMPARABLE_DOUBLE.expMergeV2(testA,0,testA.length, testB,0,testB.length, testC,0);
//  }

  @Benchmark
  public void bench_expV2AccessorL2R()
  {
    assert testA.length == lenA;
    assert testB.length == lenB;

    EXP_V2_COMPARABLE_DOUBLE.expMergeV2_L2R(testA,0,testA.length, testB,0,testB.length, testC,0);
  }

  @Benchmark
  public void bench_expV2AccessorR2L()
  {
    assert testA.length == lenA;
    assert testB.length == lenB;

    EXP_V2_COMPARABLE_DOUBLE.expMergeV2_R2L(testA,0,testA.length, testB,0,testB.length, testC,0);
  }


  private static final BinaryMergeAccessor<Double[]> BINARY_COMPARABLE_DOUBLE = new BinaryMergeAccessor<>() {
    @Override public Double[] malloc( int len ) { return new Double[len]; }
    @Override public void copy     ( Double[] a, int i, Double[] b, int j ) { b[j] = a[i]; }
    @Override public void copyRange( Double[] a, int i, Double[] b, int j, int len ) { System.arraycopy(a,i, b,j, len); }
    @Override public void      swap( Double[] a, int i, Double[] b, int j ) { Swap.swap(a,i, b,j); }
    @Override public int    compare( Double[] a, int i, Double[] b, int j ) { return Double.compare(a[i], b[j]); }
  };

//  @Benchmark
//  public void bench_binary()
//  {
//    assert testA.length == lenA;
//    assert testB.length == lenB;
//
//    BinaryMerge.merge(testA,0,testA.length, testB,0,testB.length, testC,0, Double::compare);
//  }
//
//  @Benchmark
//  public void bench_binaryAccessor()
//  {
//    assert testA.length == lenA;
//    assert testB.length == lenB;
//
//    EXP_COMPARABLE_DOUBLE.binaryMerge(testA,0,testA.length, testB,0,testB.length, testC,0);
//  }

  @Benchmark
  public void bench_binaryAccessorL2R()
  {
    assert testA.length == lenA;
    assert testB.length == lenB;

    BINARY_COMPARABLE_DOUBLE.binaryMergeL2R(testA,0,testA.length, testB,0,testB.length, testC,0);
  }

  @Benchmark
  public void bench_binaryAccessorR2L()
  {
    assert testA.length == lenA;
    assert testB.length == lenB;

    BINARY_COMPARABLE_DOUBLE.binaryMergeR2L(testA,0,testA.length, testB,0,testB.length, testC,0);
  }
}
