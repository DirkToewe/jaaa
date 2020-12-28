package com.github.jaaa.sort.tiny;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.infra.Blackhole;

public class TinySortBenchmark8 extends TinySortBenchmarkTemplate
{
  @Param({"8", "7", "6", "5", "4", "3", "2", "1", "0"})
  private int len;

  @Override protected int len() { return len; }

  @Benchmark
  public void netSortV1(  Blackhole blackhole ) {
    NetSortV1.sort(output);
    blackhole.consume(output);
  }
}
