package com.github.jaaa.util;

import net.jqwik.api.ForAll;
import net.jqwik.api.Property;
import net.jqwik.api.constraints.IntRange;
import org.assertj.core.data.Offset;

import java.util.Random;
import java.util.Spliterator;
import java.util.concurrent.atomic.AtomicInteger;

import static java.lang.Math.*;
import static java.util.Spliterator.*;
import static java.util.stream.IntStream.range;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.data.Offset.offset;
import static java.util.stream.Collectors.toList;


public class LinSpaceTest
{
  @Property( tries = 100_000 ) void is_evenly_spaced( @ForAll double start, @ForAll double end, @ForAll @IntRange(min=2, max=100_000) int size )
  {
    LinSpace linSpace = new LinSpace(start,end,size);
    assertThat( linSpace.size() ).isEqualTo(size);
    assertThat( linSpace.getAsDouble(0) ).isEqualTo(start);
    assertThat( linSpace.getAsDouble(size-1) ).isEqualTo(end);
    double delta = linSpace.getAsDouble(1) - linSpace.getAsDouble(0);
    Offset<Double> tolerance = offset( max(1e-8, abs(delta)/1e5) );
    for( int i=1; ++i < size; )
      assertThat( linSpace.getAsDouble(i) - linSpace.getAsDouble(i-1) ).isCloseTo(delta, tolerance);
  }

  @Property( tries = 100_000 ) void get_isEqualTo_getAsDouble( @ForAll double start, @ForAll double end, @ForAll @IntRange(min=2, max=100_000) int size )
  {
    LinSpace linSpace = new LinSpace(start,end, size);
    for( int i=0; i < size; i++ )
      assertThat( linSpace.get(i) ).isEqualTo( linSpace.getAsDouble(i) );
  }

  @Property( tries = 100_000 ) void forEach_statement( @ForAll double start, @ForAll double end, @ForAll @IntRange(min=2, max=100_000) int size )
  {
    LinSpace linSpace = new LinSpace(start,end, size);
    int i=0;
    for( double x: linSpace )
      assertThat(x).isEqualTo( linSpace.getAsDouble(i++) );
    assertThat(i).isEqualTo(size);
  }

  @Property( tries = 100_000 ) void forEach_unboxed( @ForAll double start, @ForAll double end, @ForAll @IntRange(min=2, max=100_000) int size )
  {
    LinSpace linSpace = new LinSpace(start,end, size);
    AtomicInteger i = new AtomicInteger();
    linSpace.forEach( (double x) -> assertThat(x).isEqualTo( linSpace.getAsDouble(i.getAndIncrement()) ) );
    assertThat( i.get() ).isEqualTo(size);
  }

  @Property( tries = 100_000 ) void forEach_boxed( @ForAll double start, @ForAll double end, @ForAll @IntRange(min=2, max=100_000) int size )
  {
    LinSpace linSpace = new LinSpace(start,end, size);
    AtomicInteger i = new AtomicInteger();
    linSpace.forEach( (Double x) -> assertThat(x).isEqualTo( linSpace.getAsDouble(i.getAndIncrement()) ) );
    assertThat( i.get() ).isEqualTo(size);
  }

  @Property( tries = 100_000 ) void stream( @ForAll double start, @ForAll double end, @ForAll @IntRange(min=2, max=100_000) int size )
  {
    LinSpace linSpace = new LinSpace(start,end, size);
    assertThat( linSpace.stream().collect(toList()) ).isEqualTo(linSpace);
  }

  @Property( tries = 100_000 ) void stream_parallel( @ForAll double start, @ForAll double end, @ForAll @IntRange(min=2, max=100_000) int size )
  {
    LinSpace linSpace = new LinSpace(start,end, size);
    assertThat( linSpace.stream().parallel().collect(toList()) ).isEqualTo(linSpace);
  }

  @Property( tries = 100_000 ) void parallelStream( @ForAll double start, @ForAll double end, @ForAll @IntRange(min=2, max=100_000) int size )
  {
    LinSpace linSpace = new LinSpace(start,end, size);
    assertThat( linSpace.parallelStream().collect(toList()) ).isEqualTo(linSpace);
  }

  @Property( tries = 100_000 ) void doubleStream( @ForAll double start, @ForAll double end, @ForAll @IntRange(min=2, max=100_000) int size )
  {
    LinSpace linSpace = new LinSpace(start,end, size);
    assertThat( linSpace.doubleStream().boxed().collect(toList()) ).isEqualTo(linSpace);
  }

  @Property( tries = 100_000 ) void doubleStream_parallel( @ForAll double start, @ForAll double end, @ForAll @IntRange(min=2, max=100_000) int size )
  {
    LinSpace linSpace = new LinSpace(start,end, size);
    assertThat( linSpace.doubleStream().parallel().boxed().collect(toList()) ).isEqualTo(linSpace);
  }

  @Property( tries = 100_000 ) void spliterator( @ForAll double start, @ForAll double end, @ForAll @IntRange(min=2, max=100_000) int size, @ForAll Random rng )
  {
    LinSpace linSpace = new LinSpace(start,end, size);

    new Object() {
      private void test( Spliterator.OfDouble rest, int offset, int size )
      {
        assertThat( rest.hasCharacteristics(IMMUTABLE | NONNULL | SIZED | SUBSIZED) ).isTrue();

        assertThat(size).isBetween(0,linSpace.size());
        assertThat(size).isNotNegative();
        assertThat( rest.estimateSize() ).isEqualTo( rest.getExactSizeIfKnown() );
        assertThat( rest.estimateSize() ).isEqualTo(size);

        if( rng.nextDouble() < 1/3d )
        {
          Spliterator.OfDouble left = rest.trySplit();
          if( left != null ) {
            int lSize = toIntExact( left.estimateSize() );
            int rSize = toIntExact( rest.estimateSize() );
            assertThat(lSize).isBetween(0,size);
            assertThat( addExact(lSize,rSize) ).isEqualTo(size);

            if( rng.nextBoolean() ) {
              test(left, offset,lSize);
              test(rest, offset+lSize, rSize);
            }
            else {
              test(rest, offset+lSize, rSize);
              test(left, offset,lSize);
            }

            return;
          }
        }

        assertThat( rest.estimateSize() ).isEqualTo( rest.getExactSizeIfKnown() );
        assertThat( rest.estimateSize() ).isEqualTo(size);

        int n = rng.nextInt(size+1);
        range(0,n).forEach( i -> {
          boolean adv = rng.nextBoolean()
            ? rest.tryAdvance( (double x) -> assertThat(x).isEqualTo( linSpace.getAsDouble(offset+i) ) )
            : rest.tryAdvance( (Double x) -> assertThat(x).isEqualTo( linSpace.getAsDouble(offset+i) ) );
          assertThat(adv).isTrue();
          assertThat( rest.estimateSize() ).isEqualTo( rest.getExactSizeIfKnown() );
          assertThat( rest.estimateSize() ).isEqualTo(size-i-1);
        });

        assertThat( rest.estimateSize() ).isEqualTo( rest.getExactSizeIfKnown() );
        assertThat( rest.estimateSize() ).isEqualTo(size-n);

        AtomicInteger i = new AtomicInteger(n);

        if( rng.nextBoolean() ) rest.forEachRemaining( (double x) -> assertThat(x).isEqualTo( linSpace.getAsDouble(offset+i.getAndIncrement()) ) );
        else                    rest.forEachRemaining( (Double x) -> assertThat(x).isEqualTo( linSpace.getAsDouble(offset+i.getAndIncrement()) ) );

        assertThat(i.get()).isEqualTo(size);

        assertThat( rest.estimateSize() ).isEqualTo( rest.getExactSizeIfKnown() );
        assertThat( rest.estimateSize() ).isEqualTo(0);

        assertThat( rng.nextBoolean()
          ? rest.tryAdvance( (double x) -> { throw new AssertionError(); } )
          : rest.tryAdvance( (Double x) -> { throw new AssertionError(); } )
        ).isFalse();
      }

      {
        test(linSpace.spliterator(), 0,linSpace.size());
      }
    };
  }
}
