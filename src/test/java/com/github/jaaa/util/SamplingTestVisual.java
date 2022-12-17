package com.github.jaaa.util;

import java.io.IOException;
import java.util.function.IntFunction;

import static com.github.jaaa.util.Sampling.lhs;
import static java.lang.String.format;
import static java.util.Arrays.stream;
import static java.util.stream.Collectors.joining;


public class SamplingTestVisual
{
  public static void main( String... args ) throws IOException
  {
    plot2d();
    plot3d();
  }

  private static void plot2d() throws IOException
  {
    double[][] xy = lhs(8192, new double[]{-1,3}, new double[]{2,7});
    IntFunction<String> getCoord = i -> stream(xy).map( row -> Double.toString(row[i]) ).collect( joining(", ","[","]"));

    PlotlyUtils.plot(
      "{\n" +
      "  title: 'Latin Hypercube Sampling 2D',\n" +
      "  yaxis: { scaleanchor: 'x' },\n" +
      "  matches: 'x',\n" +
      "}\n",
      format(
        "{\n" +
        "  type: 'scattergl',\n" +
        "  mode: 'markers',\n" +
        "  marker: {\n" +
        "    size: 6,\n" +
        "    opacity: 0.6\n" +
        "  },\n" +
        "  x: %s,\n" +
        "  y: %s\n" +
        "}\n",
        getCoord.apply(0),
        getCoord.apply(1)
      )
    );
  }

  private static void plot3d() throws IOException
  {
    double[][] xyz = lhs(8192, 3);
    IntFunction<String> getCoord = i -> stream(xyz).map( row -> Double.toString(row[i]) ).collect( joining(", ","[","]"));

    PlotlyUtils.plot(
      "{\n" +
      "  title: 'Latin Hypercube Sampling 2D',\n" +
      "  yaxis: { scaleanchor: 'x' },\n" +
      "  matches: 'x',\n" +
      "}\n",
      format(
        "{\n" +
        "  type: 'scatter3d',\n" +
        "  mode: 'markers',\n" +
        "  marker: {\n" +
        "    size: 2,\n" +
        "    opacity: 0.4\n" +
        "  },\n" +
        "  x: %s,\n" +
        "  y: %s,\n" +
        "  z: %s\n" +
        "}\n",
        getCoord.apply(0),
        getCoord.apply(1),
        getCoord.apply(2)
      )
    );
  }
}
