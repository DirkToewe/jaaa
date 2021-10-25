package com.github.jaaa.util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static java.awt.Desktop.getDesktop;
import static java.lang.String.format;
import static java.util.Arrays.stream;
import static java.util.stream.Collectors.joining;


public final class PlotlyUtils
{
// STATIC FIELDS
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
          if( 'title' in layout )
            document.title = layout.title;

          if( 'paper_bgcolor' in layout )
            document.body.style.background = 'black';

          Plotly.plot(plot, {layout, data: %2$s});
        </script>
      </body>
    </html>
  """;

// STATIC CONSTRUCTOR

// STATIC METHODS
  public static Path plot( String layout, String... data ) throws IOException
  {
    var  tmp = Files.createTempFile("plot_",".html");
    plot(tmp, layout, data);
    getDesktop().browse(tmp.toUri());
    return tmp;
  }
  public static void plot( Path path, String layout, String... data ) throws IOException
  {
    var dat = stream(data).collect( joining(",\n", "[", "]") );
    Files.writeString( path, format(PLOT_TEMPLATE, layout, dat) );
  }

// FIELDS
// CONSTRUCTORS
  private PlotlyUtils() {
    throw new UnsupportedOperationException("Static class may not be instantiated.");
  }
// METHODS
}
