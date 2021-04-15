package com.github.jaaa.sort;

import com.github.jaaa.sort.datagen.RandomSortDataGenerator;
import org.openjdk.jmh.annotations.*;

import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import static java.util.stream.IntStream.range;

@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)

@Warmup     (iterations = 16, time = 16/*sec*/)
@Measurement(iterations = 16, time = 16/*sec*/)
@Fork( value=4, jvmArgsAppend={"-ea", "-XX:MaxInlineLevel=15", "-Xmx16g"} )

@State(Scope.Benchmark)
public abstract class BenchmarkSortTemplate
{
  ;{
    boolean ea = false;
    assert  ea = true;
       if( !ea ) throw new IllegalStateException();
  }

  abstract protected int len();

  private final Random rng = new Random();
  private final RandomSortDataGenerator gen = new RandomSortDataGenerator(rng);

  private     int[]  ref = null;
          Integer[] data = null;

  @Setup(Level.Invocation)
  public void setup() {
    int len = len();

    if( ref == null ) {
      assert data == null;
      ref = new     int[len];
      data= new Integer[len];
    }

    assert data.length == len;
    assert  ref.length == len;

    gen.nextMixed(ref);
    for( int i=0; i < len; i++ ) data[i] = ref[i];
  }

  @TearDown(Level.Invocation)
  public void tearDown() {
    Arrays.parallelSort(ref);
    assert ref.length == data.length;
    assert range(0,ref.length).allMatch( i -> ref[i] == data[i] ) : Arrays.toString(data) + " != " + Arrays.toString(ref);
  }
}
