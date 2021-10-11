package com.github.jaaa.sort.tiny;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.infra.Blackhole;

public class BenchmarkTinySort16 extends BenchmarkTinySortTemplate
{
  @Param({"16", "15", "14", "13", "12", "11", "10", "9", "8", "7", "6", "5", "4", "3", "2", "1", "0"})
  private int len;

  @Override protected int len() { return len; }

  @Benchmark
  public void hexInSortV1(  Blackhole blackhole ) {
    HexInSortV1.sort(output);
    blackhole.consume(output);
  }

  @Benchmark
  public void hexInSortV2(  Blackhole blackhole ) {
    HexInSortV2.sort(output);
    blackhole.consume(output);
  }

  @Benchmark
  public void netSortV2( Blackhole blackhole ) {
    NetSortV2.sort(output);
    blackhole.consume(output);
  }

  @Benchmark
  public void netSortV3( Blackhole blackhole ) {
    NetSortV3.sort(output);
    blackhole.consume(output);
  }
}
