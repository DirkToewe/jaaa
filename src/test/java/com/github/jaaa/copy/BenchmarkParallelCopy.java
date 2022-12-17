package com.github.jaaa.copy;

import com.github.jaaa.util.PlotlyUtils;
import com.github.jaaa.util.Progress;

import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;
import java.util.SplittableRandom;

import static com.github.jaaa.permute.RandomShuffle.randomShuffle;
import static java.lang.String.format;
import static java.time.Instant.now;
import static java.util.Arrays.stream;
import static java.util.stream.IntStream.range;


public class BenchmarkParallelCopy
{
  public static void main( String... args ) throws IOException
  {
    SplittableRandom rng = new SplittableRandom();

    int              N_RUNS = 1_000,
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
      "{\n" +
      "  type: 'scattergl',\n" +
      "  name: 'sequential',\n" +
      "  x: %1$s,\n" +
      "  y:\n%2$s\n" +
      "},{\n" +
      "  type: 'scattergl',\n" +
      "  name: 'parallel',\n" +
      "  x: %1$s,\n" +
      "  y: %3$s\n" +
      "}\n",
      Arrays.toString(x),
      Arrays.toString(y_sys),
      Arrays.toString(y_par)
    );

    String layout =
      "{\n" +
      "  title: 'Parallel Copy Benchmark',\n" +
      "  xaxis: {title: 'Array Length'},\n" +
      "  yaxis: {title: 'Time [msec.]'}\n" +
      "}\n";

    PlotlyUtils.plot(layout, data);
  }
}
