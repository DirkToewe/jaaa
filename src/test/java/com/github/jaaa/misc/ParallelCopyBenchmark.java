package com.github.jaaa.misc;

import com.github.jaaa.util.EntryFn;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Random;

import static java.awt.Desktop.getDesktop;
import static java.lang.String.format;
import static java.lang.System.nanoTime;
import static java.util.stream.Collectors.joining;

public class ParallelCopyBenchmark
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

  public static void main( String... args ) throws IOException
  {
    var rng = new Random();

    int              N_RUNS = 10_000,
      x[] = rng.ints(N_RUNS,0,10_000_000).parallel().sorted().toArray();
    double[]
      y_sys = new double[N_RUNS],
      y_par = new double[N_RUNS];

    for( int run=0; run < N_RUNS; run++ )
    {
      System.out.println(run);

      int len = x[run];

      Integer[] src = rng.ints(len).boxed().toArray(Integer[]::new),
            dst_sys = new Integer[len],
            dst_par = new Integer[len];

      System.gc();

      long t0, dt_sys, dt_par;
      if( rng.nextDouble() < 0.5 ) {
        t0 = nanoTime(); ParallelCopy.arraycopy(src,0, dst_sys,0, len); dt_par = nanoTime() - t0;
        t0 = nanoTime();       System.arraycopy(src,0, dst_par,0, len); dt_sys = nanoTime() - t0;
      }
      else {
        t0 = nanoTime();       System.arraycopy(src,0, dst_par,0, len); dt_sys = nanoTime() - t0;
        t0 = nanoTime(); ParallelCopy.arraycopy(src,0, dst_sys,0, len); dt_par = nanoTime() - t0;
      }

      if( ! Arrays.equals(dst_sys,dst_par) )
        throw new AssertionError();

      y_sys[run] = dt_sys / 1e6;
      y_par[run] = dt_par / 1e6;
    }

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
