package com.github.jaaa.copy;

import com.github.jaaa.util.Progress;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;
import java.util.Random;
import java.util.SplittableRandom;

import static java.awt.Desktop.getDesktop;
import static java.lang.String.format;
import static java.lang.System.nanoTime;
import static java.util.stream.IntStream.range;
import static com.github.jaaa.permute.RandomShuffle.randomShuffle;
import static java.util.Arrays.stream;
import static java.time.Instant.now;


public class ParallelCopyBenchmark
{
  private static final String PLOT_TEMPLATE = """
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

  public static void main( String... args ) throws IOException
  {
    var rng = new SplittableRandom();

    int              N_RUNS = 10_000,
      x[] = rng.ints(N_RUNS,0,10_000_000).parallel().sorted().toArray();
    double[]
      y_sys = new double[N_RUNS],
      y_par = new double[N_RUNS];

    int[] order = range(0,N_RUNS).toArray();
    randomShuffle(order);
    Progress.print( stream(order) ).forEach(run -> {
      int len = x[run];

      Integer[] src = rng.ints(len).boxed().toArray(Integer[]::new),
            dst_sys = new Integer[len],
            dst_par = new Integer[len];

      System.gc();

      Duration dt_sys, dt_par;
      Instant t0;
      if( rng.nextDouble() < 0.5 ) {
        t0 = now(); ParallelCopy.arraycopy(src,0, dst_sys,0, len); dt_par = Duration.between(t0, now());
        t0 = now();       System.arraycopy(src,0, dst_par,0, len); dt_sys = Duration.between(t0, now());
      }
      else {
        t0 = now();       System.arraycopy(src,0, dst_par,0, len); dt_sys = Duration.between(t0, now());
        t0 = now(); ParallelCopy.arraycopy(src,0, dst_sys,0, len); dt_par = Duration.between(t0, now());
      }

      if( ! Arrays.equals(dst_sys,dst_par) )
        throw new AssertionError();

      y_sys[run] = dt_sys.toNanos() / 1e6;
      y_par[run] = dt_par.toNanos() / 1e6;
    });

    String data = format(
      "[{\ntype: 'scattergl',\nname: 'sequential',\nx: %1$s,\ny:\n%2$s\n},{\ntype: 'scattergl',\nname: 'parallel',\nx: %1$s,\ny: %3$s\n}]",
      Arrays.toString(x),
      Arrays.toString(y_sys),
      Arrays.toString(y_par)
    );

    String layout = "{ title: 'Parallel Copy Benchmark', xaxis: {title: 'Array Length'}, yaxis: {title: 'Time [msec.]'} }";

    Path tmp = Files.createTempFile("parallel_copy_benchmark_", ".html");
    Files.writeString(  tmp, format(PLOT_TEMPLATE, layout, data) );
    getDesktop().browse(tmp.toUri());
  }
}
