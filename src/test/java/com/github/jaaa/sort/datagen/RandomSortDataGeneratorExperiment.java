package com.github.jaaa.sort.datagen;

import com.github.jaaa.util.PlotlyUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;

import static java.lang.String.format;
import static java.util.stream.IntStream.range;

public class RandomSortDataGeneratorExperiment
{
  public static void main( String... args ) throws IOException
  {
    Path tmpDir = Files.createTempDirectory("random_sort_data_generator");

    RandomSortDataGenerator gen = new RandomSortDataGenerator();

    for( int i=0; i++ < 16; )
    {
      int[] sample = gen.nextMixed(1280);
      System.out.println( Arrays.toString(sample) );

      plot( sample, tmpDir.resolve( format("sample%d.html",i) ) );
    }
  }

  private static void plot( int[] dist, Path path ) throws IOException
  {
    String data = format(
      "{\n" +
      "  type: 'bar',\n" +
      "  x: %s,\n" +
      "  y: %s\n" +
      "}\n",
      Arrays.toString( range(0,dist.length).mapToObj(i -> "'" + i + "'").toArray() ),
      Arrays.toString(dist)
    );

    String layout = format( "{ title: '%s' }", path.getFileName() );

    PlotlyUtils.plot(layout, data);
  }
}
