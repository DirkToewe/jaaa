package com.github.jaaa.sort.tiny;

import com.github.jaaa.sort.InsertionSort;
import com.github.jaaa.sort.InsertionAdaptiveSort;
import com.github.jaaa.sort.InsertionExpSort;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.infra.Blackhole;

import java.util.Arrays;


public class BenchmarkTinySort32 extends BenchmarkTinySortTemplate
{
  @Param({/*"31", "30", "29", "28", "27", "26", "25", "24", "23", "22", "21", "20", "19", "18", "17",*/ "16", "15", "14", "13", "12", "11", "10", "9", "8", "7", "6", "5", "4", "3", "2", "1", "0"})
  private int len;

  @Override protected int len() { return len; }

  @Benchmark
  public void jdk(  Blackhole blackhole ) {
    Arrays.sort(output);
    blackhole.consume(output);
  }

//  @Benchmark
//  public void insertionSortV1( Blackhole blackhole ) {
//    InsertionSort.sort(output);
//    blackhole.consume(output);
//  }

  @Benchmark
  public void insertionSortV2( Blackhole blackhole ) {
    InsertionAdaptiveSort.sort(output);
    blackhole.consume(output);
  }

//  @Benchmark
//  public void insertionSortV3( Blackhole blackhole ) {
//    InsertionExpSort.sort(output);
//    blackhole.consume(output);
//  }
}
