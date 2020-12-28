package com.github.jaaa.partition;

import com.github.jaaa.PredicateSwapAccess;
import com.github.jaaa.Swap;
import com.github.jaaa.util.RNG;
import net.jqwik.api.Tuple;
import net.jqwik.api.Tuple.Tuple2;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.IntFunction;
import java.util.stream.IntStream;

import static java.awt.Desktop.getDesktop;
import static java.lang.Math.max;
import static java.lang.String.format;
import static java.lang.System.nanoTime;
import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.toMap;
import static java.util.stream.IntStream.range;

public class BiPartitionComparison
{
// STATIC FIELDS
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

  private static interface BiPartitioner {
    public void biPartition( int from, int until, PredicateSwapAccess access );
  }

  private static final class CountingBiPartitionAccess implements PredicateSwapAccess
  {
    public long nRead = 0,
                nSwap = 0;
    private final                     Tuple2<Boolean,Integer>[]  array;
    public CountingBiPartitionAccess( Tuple2<Boolean,Integer>[] _array ) { array =_array; }
    @Override public boolean predicate( int i ) { ++nRead; return array[i].get1(); }
    @Override public void  swap( int i, int j ) { ++nSwap; Swap.swap(array, i,j); }
  }

// STATIC CONSTRUCTOR

// STATIC METHODS
  public static void main( String... args )
  {
    Map<String,BiPartitioner> algorithms = Map.of(
//            "Sweep",   SweepBiPartition  ::biPartition,
            "Railway", RailwayBiPartition  ::biPartition,
                "Rec",     RecBiPartition  ::biPartition,
         "MuRaSaBiV1",  MuRaSaBiPartitionV1::biPartition,
         "MuRaSaBiV2",  MuRaSaBiPartitionV2::biPartition,
           "HexRecV1",  HexRecBiPartitionV1::biPartition,
           "HexRecV2",  HexRecBiPartitionV2::biPartition,
           "KatPawV1",  KatPawBiPartitionV1::biPartition,
           "KatPawV2",  KatPawBiPartitionV2::biPartition,
      "PermPartition", (from,until,access) -> PermPartitionStable.partition(from, until, new PartitionAccess() {
        @Override public int  key(int i) { return access.predicate(i) ? 1 : 0; }
        @Override public int nKeys() { return 2; }
        @Override public void swap(int i, int j) { access.swap(i,j); }
      })
    );

    boolean ea = false;
    assert  ea = true;
       if( !ea ) throw new ExceptionInInitializerError("Assertions must be enabled.");

    RNG rng = new RNG(1337);
    IntFunction<boolean[]> randBits = len -> {
      boolean[] result = new boolean[len];
      for( int i=0; i < len; ) {
        long bits = rng.nextLong();
        for( int j=0; i < len && j < 64; i++, j++ )
          result[i] = (bits>>>j & 1) == 1;
      }
      return result;
    };

    int[] lens = IntStream.iterate(1, l -> l < 100_000_000, l -> max( l+1, (int) (l*1.05) ) ).toArray();
    Map<String,double[]>
      times = algorithms.entrySet().stream().collect( toMap(Entry::getKey, e -> new double[lens.length]) ),
      reads = algorithms.entrySet().stream().collect( toMap(Entry::getKey, e -> new double[lens.length]) ),
      swaps = algorithms.entrySet().stream().collect( toMap(Entry::getKey, e -> new double[lens.length]) );

    range(0,lens.length).forEach( i -> {
      int len = lens[i];
      System.out.printf("len:%8d\n", len);

      boolean[]   bits = randBits.apply(len);
      assert len==bits.length;

      Tuple2<Boolean,Integer>[] prototype = range(0,len).mapToObj( j -> Tuple.of(bits[j],j) ).toArray(Tuple2[]::new),
                  reference  =  prototype.clone();
      Arrays.sort(reference, comparing(Tuple2::get1));

      algorithms.forEach( (name, alg) -> {
        Tuple2<Boolean,Integer>[] instance = prototype.clone();

        var access = new CountingBiPartitionAccess(instance);

        long t0 = nanoTime();
        alg.biPartition(0, instance.length, access);
        long dt = nanoTime() - t0;

        reads.get(name)[i] = access.nRead;
        swaps.get(name)[i] = access.nSwap;
        times.get(name)[i] = dt / 1e9;

        assert Arrays.equals(instance, reference);
      });
    });

    Map.of(
      "reads", reads,
      "swaps", swaps,
      "times", times
    ).forEach( (dataType, records) -> {
      StringBuilder data = new StringBuilder("[");

      records.forEach( (name, record) ->
        data.append( format("{\n  type:'scattergl',\n  name: '%s',\n  x: %s,\n  y: %s\n},\n", name, Arrays.toString(lens), Arrays.toString(record)) )
      );

      data.append("\n]");

      String layout = format("{ title: 'BiPartition Benchmark %1$s', xaxis: {title: 'Array Length'}, yaxis: {title: '%1$s'} }", dataType);

      try
      {
        Path tmp = Files.createTempFile("plot_"+dataType+"_",".html");
        Files.writeString(  tmp, format(PLOT_TEMPLATE, layout, data) );
        getDesktop().browse(tmp.toUri());
      }
      catch( IOException ioe ) {
        throw new Error(ioe);
      }
    });
  }
}
