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
    var xy = lhs(8192, 2);
    IntFunction<String> getCoord = i -> stream(xy).map( row -> Double.toString(row[i]) ).collect( joining(", ","[","]"));

    PlotlyUtils.plot(
      """
      {
        title: 'Latin Hypercube Sampling 2D',
        yaxis: { scaleanchor: 'x' },
        matches: 'x',
      }
      """,
      format(
        """
        {
          type: 'scattergl',
          mode: 'markers',
          marker: {
            size: 6,
            opacity: 0.6
          },
          x: %s,
          y: %s
        }
        """,
        getCoord.apply(0),
        getCoord.apply(1)
      )
    );
  }

  private static void plot3d() throws IOException
  {
    var xyz = lhs(8192, 3);
    IntFunction<String> getCoord = i -> stream(xyz).map( row -> Double.toString(row[i]) ).collect( joining(", ","[","]"));

    PlotlyUtils.plot(
      """
      {
        title: 'Latin Hypercube Sampling 2D',
        yaxis: { scaleanchor: 'x' },
        matches: 'x',
      }
      """,
      format(
        """
        {
          type: 'scatter3d',
          mode: 'markers',
          marker: {
            size: 2,
            opacity: 0.4
          },
          x: %s,
          y: %s,
          z: %s
        }
        """,
        getCoord.apply(0),
        getCoord.apply(1),
        getCoord.apply(2)
      )
    );
  }
}
