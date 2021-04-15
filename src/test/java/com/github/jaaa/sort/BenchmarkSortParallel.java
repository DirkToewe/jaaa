package com.github.jaaa.sort;

import com.github.jaaa.util.EntryFn;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.infra.BenchmarkParams;
import org.openjdk.jmh.infra.Blackhole;
import org.openjdk.jmh.results.AverageTimeResult;
import org.openjdk.jmh.results.RunResult;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

import static java.awt.Desktop.getDesktop;
import static java.lang.String.format;
import static java.util.stream.Collectors.joining;
import static com.github.jaaa.misc.Revert.revert;

public class BenchmarkSortParallel extends BenchmarkSortTemplate
{
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
    + "\n" + ""
    + "\n" + "      const plot = document.createElement('div');"
    + "\n" + "      plot.style = 'width: 100%%; height: 95vh;';"
    + "\n" + "      document.body.appendChild(plot);"
    + "\n" + ""
    + "\n" + "      const layout = %1$s;"
    + "\n" + "      document.title = layout.title;"
    + "\n" + ""
    + "\n" + "      Plotly.plot(plot, {layout, data: %2$s});"
    + "\n" + "    </script>"
    + "\n" + "  </body>"
    + "\n" + "</html>"
    + "\n";

  public static void main( String... args ) throws RunnerException, IOException
  {
    var rng = new Random();

    var lens = rng.ints(16, 0,10_000_000).sorted().mapToObj(Objects::toString).toArray(String[]::new);
    revert(lens);

    var opt = new OptionsBuilder()
      .include( BenchmarkSortParallel.class.getCanonicalName() )
      .param( "len", lens )
      .build();

    Collection<RunResult> results = new Runner(opt).run();

    SortedMap<String, SortedMap<Integer,Double>> resultMap = new TreeMap<>();

    for( var result: results )
    {
      var   avgTime = (AverageTimeResult) result.getPrimaryResult();
      if( ! avgTime.getScoreUnit().equalsIgnoreCase("ms/op") )
        throw new AssertionError();

      String method_path[] = result.getParams().getBenchmark().split("\\."),
             method        = method_path[method_path.length-1];

      BenchmarkParams params = result.getParams();

      if( ! result.getParams().getParamsKeys().equals( Set.of("len") ) )
        throw new AssertionError();

      int len = Integer.valueOf( params.getParam("len") );
      resultMap
        .computeIfAbsent(method, x -> new TreeMap<>())
        .put( len, avgTime.getScore() );
    }

    String data = resultMap.entrySet().stream().map(
      EntryFn.of( (key, val) -> format(
        "{\n  type: 'scatter2d',\n  name: '%s',\n  x: [%s],\n  y: [%s]\n}",
        key,
        val.keySet().stream().map(Objects::toString).collect( joining(",") ),
        val.values().stream().map(Objects::toString).collect( joining(",") )
      ) )
    ).collect( joining(",", "[", "]") );

    String layout = format("{ title: 'Parallel Sort Benchmark', xaxis: {title: 'Array Length'}, yaxis: {title: 'Time [msec.]'} }");

    Path tmp = Files.createTempFile("plot_", ".html");
    Files.writeString(  tmp, format(PLOT_TEMPLATE, layout, data) );
    getDesktop().browse(tmp.toUri());
  }

  @Param({
    "10000000",
     "1000000",
      "100000",
       "10000",
        "1000"
  })
  private int len;

  @Override protected int len() { return len; }

  @Benchmark public void parallelRebelMergeSort( Blackhole blackhole ) {
    ParallelRebelMergeSort.sort(data);
    blackhole.consume(data);
  }

  @Benchmark public void parallelRecMergeSort( Blackhole blackhole ) {
    ParallelRecMergeSort.sort(data);
    blackhole.consume(data);
  }

  @Benchmark public void parallelSkipMergeSort( Blackhole blackhole ) {
    ParallelSkipMergeSort.sort(data);
    blackhole.consume(data);
  }

  @Benchmark public void parallelZenMergeSort( Blackhole blackhole ) {
    ParallelZenMergeSort.sort(data);
    blackhole.consume(data);
  }

  @Benchmark public void jdk_parallelSort( Blackhole blackhole ) {
    Arrays.parallelSort(data);
    blackhole.consume(data);
  }

//  @Benchmark public void jdk_sort( Blackhole blackhole ) {
//    Arrays.sort(data);
//    blackhole.consume(data);
//  }
}