package com.github.jaaa.util;

import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;

import static java.lang.Runtime.getRuntime;
import static java.lang.String.format;
import static java.nio.charset.StandardCharsets.UTF_8;
import static java.nio.file.Files.newBufferedWriter;
import static java.util.Arrays.stream;
import static java.util.stream.Collectors.joining;


public final class PlotlyUtils
{
// STATIC FIELDS
  private static final String PLOT_TEMPLATE =
    "<!DOCTYPE html>\n" +
    "<html lang=\"en\">\n" +
    "  <head>\n" +
    "    <meta charset=\"utf-8\">\n" +
    "    <script src=\"https://cdn.plot.ly/plotly-latest.js\"></script>\n" +
    "  </head>\n" +
    "  <body>\n" +
    "    <script>\n" +
    "      'use strict';\n" +
    "\n" +
    "      const plot = document.createElement('div');\n" +
    "      plot.style = 'width: 100%%; height: 95vh;';\n" +
    "      document.body.appendChild(plot);\n" +
    "\n" +
    "      const layout = %1$s;\n" +
    "      if( 'title' in layout )\n" +
    "        document.title = layout.title;\n" +
    "\n" +
    "      if( 'paper_bgcolor' in layout )\n" +
    "        document.body.style.background = layout.paper_bgcolor;\n" +
    "\n" +
    "      Plotly.plot(plot, {layout, data: %2$s});\n" +
    "    </script>\n" +
    "  </body>\n" +
    "</html>\n";

// STATIC CONSTRUCTOR

// STATIC METHODS
  public static Path plot( String layout, String... data ) throws IOException
  {
    Path tmp = Files.createTempFile("plot_",".html");
    plot(tmp, layout, data);
    String[] cmd = { "xdg-open", tmp.toString() };
    getRuntime().exec(cmd);
    return tmp;
  }
  public static void plot( Path path, String layout, String... data ) throws IOException
  {
    String dat = stream(data).collect( joining(",\n", "[", "]") );
    try( Writer out = newBufferedWriter(path, UTF_8) ) {
      out.write( format(PLOT_TEMPLATE, layout, dat) );
    }
  }

// FIELDS
// CONSTRUCTORS
  private PlotlyUtils() {
    throw new UnsupportedOperationException("Static class may not be instantiated.");
  }
// METHODS
}
