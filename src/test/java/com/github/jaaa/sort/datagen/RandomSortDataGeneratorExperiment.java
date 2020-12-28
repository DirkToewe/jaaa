package com.github.jaaa.sort.datagen;

import javax.swing.*;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.concurrent.Executors;
import java.util.concurrent.ForkJoinPool;

import static java.awt.Desktop.getDesktop;
import static java.lang.String.format;
import static java.util.stream.IntStream.range;

public class RandomSortDataGeneratorExperiment
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
    Path tmpDir = Files.createTempDirectory("random_sort_data_generator");

    var gen = new RandomSortDataGenerator();

    for( int i=0; i++ < 16; )
    {
      int[] sample = gen.nextMixed(1280);
      System.out.println( Arrays.toString(sample) );

      plot( sample, tmpDir.resolve( format("sample%d.html",i) ) );
    }
  }

  private static void plot( int[] dist, Path path ) throws IOException
  {
    var pool = ForkJoinPool.commonPool();

    String data = format(
      "[{\n  type: 'bar',\n  x: %s,\n  y: %s\n}]",
      Arrays.toString( range(0,dist.length).mapToObj(i -> "'" + i + "'").toArray() ),
      Arrays.toString(dist)
    );

    String layout = format( "{ title: '%s' }", path.getFileName() );

    Files.writeString( path, format(PLOT_TEMPLATE, layout, data) );
    SwingUtilities.invokeLater( () -> {
      try {
        getDesktop().browse(path.toUri());
      }
      catch( IOException ioe ) {
        throw new Error(ioe);
      }
    });
  }
}
