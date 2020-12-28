package com.github.jaaa.sort.tiny;

import com.github.jaaa.sort.InsertionSortV1;
import com.github.jaaa.sort.InsertionSortV2;
import com.github.jaaa.sort.InsertionSortV3;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.infra.Blackhole;

import java.util.Arrays;

public class TinySortBenchmark32 extends TinySortBenchmarkTemplate
{
  @Param({"31", "30", "29", "28", "27", "26", "25", "24", "23", "22", "21", "20", "19", "18", "17", "16", "15", "14", "13", "12", "11", "10", "9", "8", "7", "6", "5", "4", "3", "2", "1", "0"})
  private int len;

  @Override protected int len() { return len; }

  @Benchmark
  public void jdk(  Blackhole blackhole ) {
    Arrays.sort(output);
    blackhole.consume(output);
  }

  @Benchmark
  public void insertionSortV1( Blackhole blackhole ) {
    InsertionSortV1.sort(output);
    blackhole.consume(output);
  }

  @Benchmark
  public void insertionSortV2( Blackhole blackhole ) {
    InsertionSortV2.sort(output);
    blackhole.consume(output);
  }

  @Benchmark
  public void insertionSortV3( Blackhole blackhole ) {
    InsertionSortV3.sort(output);
    blackhole.consume(output);
  }
}
