package com.github.jaaa.sort.tiny;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import static java.lang.System.arraycopy;

@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)

@Warmup     (iterations = 4, time = 4/*sec*/)
@Measurement(iterations = 4, time = 4/*sec*/)
@Fork( value=5, jvmArgsAppend={"-ea", "-XX:MaxInlineLevel=15"} )

@State(Scope.Benchmark)
public abstract class TinySortBenchmarkTemplate
{
  ;{
    boolean ea = false;
    assert  ea = true;
       if( !ea ) throw new IllegalStateException();
  }

  protected abstract int len();

  private Integer[]  input = null,
                 reference = null;
          Integer[] output = null;

  @Setup(Level.Invocation)
  public void setup() {
    final int len = len();

    if(   input == null ) {
          input = new Integer[len]; Arrays.fill(input,0);
         output = input.clone();
      reference = input.clone();
    }
    assert len ==     input.length;
    assert len ==    output.length;
    assert len == reference.length;

    for( int i=0; i  < len; i++ )
      if( ++input[i] < len ) break;
      else  input[i] = 0;

    arraycopy(input,0,    output,0, len);
    arraycopy(input,0, reference,0, len);
  }

  @TearDown(Level.Invocation)
  public void tearDown() {
    Arrays.sort(reference);
    assert Arrays.equals(output,reference) : Arrays.toString(output) + " != " + Arrays.toString(reference);
  }
}
