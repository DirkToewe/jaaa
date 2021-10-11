package com.github.jaaa.sort.tiny;

import com.github.jaaa.fn.EntryFn;
import org.openjdk.jmh.infra.BenchmarkParams;
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
import static java.lang.Integer.parseInt;
import static java.lang.String.format;
import static java.util.stream.Collectors.joining;


// -ea -Xmx16g -XX:MaxInlineLevel=15
public class BenchmarkTinySortMain
{
  private static String PLOT_TEMPLATE = """
    <!DOCTYPE html>
    <html lang="en">
      <head>
        <meta charset="utf-8">
        <script src="https://cdn.plot.ly/plotly-latest.js"></script>
      </head>
      <body>
        <script>
          'use strict';

          const plot = document.createElement('div');
          plot.style = 'width: 100%%; height: 95vh;';
          document.body.appendChild(plot);

          const layout = %1$s;
          document.title = layout.title;

          Plotly.plot(plot, {layout, data: %2$s});
        </script>
      </body>
    </html>
    
  """;

  public static void main( String... args ) throws RunnerException, IOException
  {
    var resultMap = runBenchmarks();

    String data = resultMap.entrySet().stream().map(
      EntryFn.of( (key,val) -> format(
        "{\n  type: 'scatter2d',\n  name: '%s',\n  x: [%s],\n  y: [%s]\n}",
        key,
        val.keySet().stream().map(Objects::toString).collect( joining(",") ),
        val.values().stream().map(Objects::toString).collect( joining(",") )
      ) )
    ).collect( joining(",", "[", "]") );

    String layout = """
      {
        title: 'Tiny Sort Benchmark',
        xaxis: {title: 'Array Length'},
        yaxis: {title: 'Time [nsec.]'}
      }
    """;

    Path tmp = Files.createTempFile("plot_", ".html");
    Files.writeString(  tmp, format(PLOT_TEMPLATE, layout, data) );
    getDesktop().browse(tmp.toUri());
  }

  private static SortedMap<String, SortedMap<Integer,Double>> runBenchmarks() throws RunnerException
  {
    var opt = new OptionsBuilder()
//      .include( BenchmarkTinySort8 .class.getCanonicalName() )
//      .include( BenchmarkTinySort16.class.getCanonicalName() )
      .include( BenchmarkTinySort32.class.getCanonicalName() )
      .build();

    Collection<RunResult> results = new Runner(opt).run();

    SortedMap<String, SortedMap<Integer,Double>> resultMap = new TreeMap<>();

    for( var result: results )
    {
      var   avgTime = (AverageTimeResult) result.getPrimaryResult();
      if( ! avgTime.getScoreUnit().equalsIgnoreCase("ns/op") )
        throw new AssertionError();

      String[] method_path = result.getParams().getBenchmark().split("\\.");
      String   method = method_path[method_path.length-1];

      BenchmarkParams params = result.getParams();

      if( ! result.getParams().getParamsKeys().equals( Set.of("len") ) )
        throw new AssertionError();

      int len = parseInt( params.getParam("len") );
      resultMap
        .computeIfAbsent(method, x -> new TreeMap<>())
        .put( len, avgTime.getScore() );
    }

    return resultMap;
  }
}
