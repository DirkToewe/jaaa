package com.github.jaaa.partition;

import com.github.jaaa.fn.EntryConsumer;
import com.github.jaaa.fn.EntryFn;
import com.github.jaaa.fn.PredicateSwapAccess;
import com.github.jaaa.permute.Swap;
import com.github.jaaa.util.PlotlyUtils;
import com.github.jaaa.util.Progress;
import net.jqwik.api.Tuple;
import net.jqwik.api.Tuple.Tuple2;

import java.io.IOException;
import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.atomic.LongAdder;
import java.util.function.BiConsumer;
import java.util.function.IntFunction;

import static com.github.jaaa.permute.RandomShuffle.randomShuffle;
import static com.github.jaaa.permute.Revert.revert;
import static java.lang.String.format;
import static java.lang.System.nanoTime;
import static java.lang.System.out;
import static java.util.Arrays.stream;
import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.toMap;
import static java.util.stream.IntStream.range;


public class BenchmarkBiPartition
{
// STATIC FIELDS
  private interface BiPartitioner {
    void biPartition( int from, int until, PredicateSwapAccess access );
  }

  private static final class CountingBiPartitionAccess implements PredicateSwapAccess
  {
    public final LongAdder
      nRead = new LongAdder(),
      nSwap = new LongAdder();
    private final                     Tuple2<Boolean,Integer>[]  array;
    public CountingBiPartitionAccess( Tuple2<Boolean,Integer>[] _array ) { array =_array; }
    @Override public boolean predicate( int i ) { nRead.increment(); return array[i].get1(); }
    @Override public void  swap( int i, int j ) { nSwap.increment(); Swap.swap(array, i,j); }
  }

// STATIC CONSTRUCTOR

// STATIC METHODS
  public static void main( String... args )
  {
    out.println( System.getProperty("java.vm.name") + " " + System.getProperty("java.vm.version") );
    out.println( "Java " + System.getProperty("java.version") );

    Map<String,BiPartitioner> algorithms = new LinkedHashMap<>();
    algorithms.put("Railway", RailwayBiPartition::biPartition);
    algorithms.put("KatPawV5", KatPawBiPartition::biPartition);
    algorithms.put("PermPartition", (from,until,access) -> PermPartitionStable.partition(from, until, new PartitionAccess() {
        @Override public int  key(int i) { return access.predicate(i) ? 1 : 0; }
        @Override public int nKeys() { return 2; }
        @Override public void swap(int i, int j) { access.swap(i,j); }
      })
    );
    @SuppressWarnings("unchecked")
    Entry<String,BiPartitioner>[] algos = algorithms.entrySet().stream().toArray(Entry[]::new);

    boolean ea = false;
    assert  ea = true;
       if( !ea ) throw new ExceptionInInitializerError("Assertions must be enabled.");

    SplittableRandom rng = new SplittableRandom();
    IntFunction<boolean[]> randBits = len -> {
      boolean[] result = new boolean[len];
      for( int i=0; i < len; ) {
        long bits = rng.nextLong();
        for( int j=0; i < len && j < 64; i++, j++ )
          result[i] = (bits>>>j & 1) == 1;
      }
      return result;
    };

    int[] lens = rng.ints(1_000, 0, 1_000_000).sorted().toArray();
    revert(lens);
    Map<String,double[]>
      times = algorithms.entrySet().stream().collect( toMap(Entry::getKey, e -> new double[lens.length]) ),
      reads = algorithms.entrySet().stream().collect( toMap(Entry::getKey, e -> new double[lens.length]) ),
      swaps = algorithms.entrySet().stream().collect( toMap(Entry::getKey, e -> new double[lens.length]) );

    int[] order = range(0,lens.length).toArray();
    randomShuffle(order, rng::nextInt);
    Progress.print( stream(order) ).forEach(i -> {
      int len = lens[i];

      boolean[]   bits = randBits.apply(len);
      assert len==bits.length;

      @SuppressWarnings("unchecked")
      Tuple2<Boolean,Integer>[] prototype = range(0,len).mapToObj( j -> Tuple.of(bits[j],j) ).toArray(Tuple2[]::new),
                  reference  =  prototype.clone();
      Arrays.sort(reference, comparing(Tuple2::get1));

      randomShuffle(algos, rng::nextInt);
      stream(algos).forEach( EntryConsumer.of(
        (name,alg) -> {
          Tuple2<Boolean,Integer>[] instance = prototype.clone();

          CountingBiPartitionAccess access = new CountingBiPartitionAccess(instance);

          long t0 = nanoTime();
          alg.biPartition(0, instance.length, access);
          long dt = nanoTime() - t0;

          reads.get(name)[i] = access.nRead.sum();
          swaps.get(name)[i] = access.nSwap.sum();
          times.get(name)[i] = dt / 1e9;

          assert Arrays.equals(instance, reference);
        }
      ));
    });

    String xString = Arrays.toString(lens);

    BiConsumer<String, Map<String,double[]>> plot = (dataType, records) -> {
      String[] data = records.entrySet().stream().map( EntryFn.of(
        (name, record) -> format(
          "{\n" +
          "  type:'scattergl',\n" +
          "  name: '%s',\n" +
          "  x: %s,\n" +
          "  y: %s\n" +
          "}\n",
          name,
          xString,
          Arrays.toString(record))
      )).toArray(String[]::new);

      String layout = format(
        "{\n" +
        "  title: 'BiPartition Benchmark %1$s',\n" +
        "  xaxis: {title: 'Array Length'},\n" +
        "  yaxis: {title: '%1$s'}\n" +
        "}\n",
        dataType
      );

      try {
        PlotlyUtils.plot(layout, data);
      }
      catch( IOException ioe ) {
        throw new Error(ioe);
      }
    };
    plot.accept("reads", reads);
    plot.accept("swaps", swaps);
    plot.accept("times", times);
  }
}
