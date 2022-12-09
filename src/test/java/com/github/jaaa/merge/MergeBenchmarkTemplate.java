package com.github.jaaa.merge;

import com.github.jaaa.permute.Swap;
import org.openjdk.jmh.annotations.*;

import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import static java.lang.Math.max;
import static java.lang.System.arraycopy;
import static java.util.stream.IntStream.range;

@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)

@Warmup     (iterations = 8, time = 8/*sec*/)
@Measurement(iterations = 8, time = 8/*sec*/)
@Fork( value=4, jvmArgsAppend={"-ea"} )

@State(Scope.Benchmark)
public abstract class MergeBenchmarkTemplate
{
  {
    boolean ea = false;
    assert  ea = true;
      if( ! ea ) throw new IllegalStateException();
  }

  protected abstract int lenA();
  protected abstract int lenB();

  private Double[]
    refA = null,
    refB = null,
    refC = null;
  protected Double[]
    testA = null,
    testB = null,
    testC = null;
  private int indices[];

  private final Random rng = new Random();

  private void randomize( Double[] refA, Double[] refB )
  {
    assert    refA.length == refB.length;
    int len = refA.length;
    if( len > 0 ) {
      refA[0] =
      refB[0] = 1.0;
      switch( rng.nextInt(3) ) {
        default: throw new AssertionError();
        case 2 : refA[0] = 2.0; break;
        case 1 : refB[0] = 2.0; break;
        case 0 :                break;
      }

      for( int i=1; i < len; i++ )
      {
        double a = refA[i-1],
               b = refB[i-1];
        if( a==b )
          switch( rng.nextInt(6) )
          {
            default: throw new AssertionError();
            case 0:             break;
            case 1: a+=1;       break;
            case 2:       b+=1; break;
            case 3: a+=1; b+=1; break;
            case 4: a+=1; b+=2; break;
            case 5: a+=2; b+=1; break;
          }
        else
        {
          var swap = a > b;
          if( swap ) { Double c=a; a=b; b=c; }

          switch( rng.nextInt(8) )
          {
            default: throw new AssertionError();
            case 0:              break;
            case 1: a = (a+b)/2; break;
            case 2: a =    b   ; break;
            case 3: a =  1+b   ; break;
            case 4:          b = b+1; break;
            case 5: a = a+1; b = b+1; break;
            case 6: a = b+1; b = b+1; break;
            case 7: a = b+2; b = b+1; break;
          }

          if( swap ) { Double c=a; a=b; b=c; }
        }

        refA[i] = a;
        refB[i] = b;
      }
    }
  }

  private void subSample( Double[] src, Double[] dest )
  {
    assert        dest.length <= src.length;
    assert     indices.length == src.length;
    for( int i=indices.length; i-- > 0; )
      indices[i] = i;

    for( int i=indices.length,
             j=dest.length; j-- > 0; )
    {
      Swap.swap(indices, rng.nextInt(i), --i);
      dest[j] = src[indices[i]];
    }

    Arrays.sort(dest);
  }

  @Setup(Level.Invocation)
  public void setup() {
    final int
      lenA = lenA(),
      lenB = lenB();

    if( null == refA ) {
      refA = new Double[lenA]; Arrays.fill(refA, 0.0);
      refB = new Double[lenB]; Arrays.fill(refB, 0.0);
      refC = new Double[lenA + lenB];
      testA = refA.clone();
      testB = refB.clone();
      testC = refC.clone();
      indices = new int[max(lenA,lenB)];
    }

    if( lenA >= lenB ) { randomize(refA,testA); subSample(testA,refB); }
    else               { randomize(refB,testB); subSample(testB,refA); }

    assert range(1,lenA).allMatch( i -> refA[i-1] <= refA[i] );
    assert range(1,lenB).allMatch( i -> refB[i-1] <= refB[i] );

    arraycopy(refA,0, testA,0, lenA);
    arraycopy(refB,0, testB,0, lenB);
  }

  @TearDown(Level.Invocation)
  public void tearDown() {
    arraycopy(refA,0, refC,0,           refA.length);
    arraycopy(refB,0, refC,refA.length, refB.length);
    Arrays.sort(refC);

    assert Arrays.equals(testA,refA);
    assert Arrays.equals(testB,refB);
    assert Arrays.equals(testC,refC);
  }

//  public abstract void bench( Blackhole blackhole );
}
