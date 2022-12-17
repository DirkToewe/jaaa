package com.github.jaaa.util;

import com.github.jaaa.ArrayProviderTemplate;
import net.jqwik.api.ForAll;
import net.jqwik.api.Property;
import net.jqwik.api.PropertyDefaults;
import net.jqwik.api.ShrinkingMode;

import java.util.List;
import java.util.Random;
import java.util.Spliterator;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.function.DoubleConsumer;
import java.util.function.IntConsumer;
import java.util.function.LongConsumer;
import java.util.stream.IntStream;
import java.util.stream.StreamSupport;

import static com.github.jaaa.Boxing.boxed;
import static java.util.Arrays.asList;
import static java.util.Arrays.stream;
import static java.util.Spliterator.*;
import static java.util.stream.Collectors.toList;
import static java.util.stream.StreamSupport.*;
import static org.assertj.core.api.Assertions.assertThat;


@PropertyDefaults( tries = 1_000, shrinking = ShrinkingMode.OFF )
public class ProgressTest implements ArrayProviderTemplate
{
  @Override public int maxArraySize() { return 100_000; }

  private static final class SpliterComparator
  {
    private static final int[] chars = { SUBSIZED, SORTED, ORDERED, NONNULL, IMMUTABLE, DISTINCT, CONCURRENT, SIZED };

    private final Random rng;
    public SpliterComparator( Random _rng ) {
      rng = _rng;
    }

    private IntStream randomCharacteristics() {
      return IntStream.generate( () -> {
        int result = 0;
        for( int c: chars )
          if( rng.nextDouble() < 0.25 )
            result |= c;
        return result;
      }).limit(16);
    }

    public void compare( Spliterator.OfInt test, Spliterator.OfInt reference )
    {
      assertThat( test.characteristics() ).isEqualTo( reference.characteristics() );
      assertThat( randomCharacteristics() ).allSatisfy( c ->
        assertThat( test.hasCharacteristics(c) ).isEqualTo( reference.hasCharacteristics(c) )
      );

      assertThat( test.estimateSize()        ).isEqualTo( reference.estimateSize()        );
      assertThat( test.getExactSizeIfKnown() ).isEqualTo( reference.getExactSizeIfKnown() );

      if( rng.nextDouble() < 1/3d )
      {
        Spliterator.OfInt
          leftTst =      test.trySplit(),
          leftRef = reference.trySplit();

        assertThat( null==leftTst ).isEqualTo( null==leftRef );

        if( null != leftTst ) {
          if( rng.nextBoolean() ) {
            compare(leftTst, leftRef);
            compare(test, reference);
          }
          else {
            compare(test, reference);
            compare(leftTst, leftRef);
          }
          return;
        }
      }

      int[] ref = intStream(reference, false).toArray();

      AtomicInteger i = new AtomicInteger();

      IntConsumer       actionUnboxed = x -> assertThat(x).isEqualTo( ref[i.getAndIncrement()] );
      Consumer<Integer> actionBoxed   = x -> assertThat(x).isEqualTo( ref[i.getAndIncrement()] );

      while( rng.nextDouble() < 0.75 ) {
        boolean unfinished = rng.nextBoolean()
          ? test.tryAdvance(actionUnboxed)
          : test.tryAdvance(actionBoxed);
        if( ! unfinished ) {
          assertThat(i.get()).isEqualTo(ref.length);
          break;
        }
      }

      if( rng.nextBoolean() ) test.forEachRemaining(actionUnboxed);
      else                    test.forEachRemaining(actionBoxed);
      assertThat(i.get()).isEqualTo(ref.length);
    }

    public void compare( Spliterator.OfLong test, Spliterator.OfLong reference )
    {
      assertThat( test.characteristics() ).isEqualTo( reference.characteristics() );
      assertThat( randomCharacteristics() ).allSatisfy( c ->
        assertThat( test.hasCharacteristics(c) ).isEqualTo( reference.hasCharacteristics(c) )
      );

      assertThat( test.estimateSize()        ).isEqualTo( reference.estimateSize()        );
      assertThat( test.getExactSizeIfKnown() ).isEqualTo( reference.getExactSizeIfKnown() );

      if( rng.nextDouble() < 1/3d )
      {
        Spliterator.OfLong
          leftTst =      test.trySplit(),
          leftRef = reference.trySplit();

        assertThat( null==leftTst ).isEqualTo( null==leftRef );

        if( null != leftTst ) {
          if( rng.nextBoolean() ) {
            compare(leftTst, leftRef);
            compare(test, reference);
          }
          else {
            compare(test, reference);
            compare(leftTst, leftRef);
          }
          return;
        }
      }

      long[] ref = longStream(reference, false).toArray();

      AtomicInteger i = new AtomicInteger();

      LongConsumer   actionUnboxed = x -> assertThat(x).isEqualTo( ref[i.getAndIncrement()] );
      Consumer<Long> actionBoxed   = x -> assertThat(x).isEqualTo( ref[i.getAndIncrement()] );

      while( rng.nextDouble() < 0.75 ) {
        boolean unfinished = rng.nextBoolean()
          ? test.tryAdvance(actionUnboxed)
          : test.tryAdvance(actionBoxed);
        if( ! unfinished ) {
          assertThat(i.get()).isEqualTo(ref.length);
          break;
        }
      }

      if( rng.nextBoolean() ) test.forEachRemaining(actionUnboxed);
      else                    test.forEachRemaining(actionBoxed);
      assertThat(i.get()).isEqualTo(ref.length);
    }

    public void compare( Spliterator.OfDouble test, Spliterator.OfDouble reference )
    {
      assertThat( test.characteristics() ).isEqualTo( reference.characteristics() );
      assertThat( randomCharacteristics() ).allSatisfy( c ->
        assertThat( test.hasCharacteristics(c) ).isEqualTo( reference.hasCharacteristics(c) )
      );

      assertThat( test.estimateSize()        ).isEqualTo( reference.estimateSize()        );
      assertThat( test.getExactSizeIfKnown() ).isEqualTo( reference.getExactSizeIfKnown() );

      if( rng.nextDouble() < 1/3d )
      {
        Spliterator.OfDouble
          leftTst =      test.trySplit(),
          leftRef = reference.trySplit();

        assertThat( null==leftTst ).isEqualTo( null==leftRef );

        if( null != leftTst ) {
          if( rng.nextBoolean() ) {
            compare(leftTst, leftRef);
            compare(test, reference);
          }
          else {
            compare(test, reference);
            compare(leftTst, leftRef);
          }
          return;
        }
      }

      double[] ref = doubleStream(reference, false).toArray();

      AtomicInteger i = new AtomicInteger();

      DoubleConsumer   actionUnboxed = x -> assertThat(x).isEqualTo( ref[i.getAndIncrement()] );
      Consumer<Double> actionBoxed   = x -> assertThat(x).isEqualTo( ref[i.getAndIncrement()] );

      while( rng.nextDouble() < 0.75 ) {
        boolean unfinished = rng.nextBoolean()
          ? test.tryAdvance(actionUnboxed)
          : test.tryAdvance(actionBoxed);
        if( ! unfinished ) {
          assertThat(i.get()).isEqualTo(ref.length);
          break;
        }
      }

      if( rng.nextBoolean() ) test.forEachRemaining(actionUnboxed);
      else                    test.forEachRemaining(actionBoxed);
      assertThat(i.get()).isEqualTo(ref.length);
    }

    public <T> void compare( Spliterator<T> test, Spliterator<T> reference )
    {
      assertThat( test.characteristics() ).isEqualTo( reference.characteristics() );
      assertThat( randomCharacteristics() ).allSatisfy( c ->
        assertThat( test.hasCharacteristics(c) ).isEqualTo( reference.hasCharacteristics(c) )
      );

      assertThat( test.estimateSize()        ).isEqualTo( reference.estimateSize()        );
      assertThat( test.getExactSizeIfKnown() ).isEqualTo( reference.getExactSizeIfKnown() );

      if( rng.nextDouble() < 1/3d )
      {
        Spliterator<T>
          leftTst =      test.trySplit(),
          leftRef = reference.trySplit();

        assertThat( null==leftTst ).isEqualTo( null==leftRef );

        if( null != leftTst ) {
          if( rng.nextBoolean() ) {
            compare(leftTst, leftRef);
            compare(test, reference);
          }
          else {
            compare(test, reference);
            compare(leftTst, leftRef);
          }
          return;
        }
      }

      List<T> ref = StreamSupport.stream(reference, false).collect(toList());

      AtomicInteger i = new AtomicInteger();

      Consumer<? super T> actionBoxed = x -> assertThat(x).isSameAs( ref.get( i.getAndIncrement() ) );

      while( rng.nextDouble() < 0.75 ) {
        boolean unfinished = test.tryAdvance(actionBoxed);
        if( ! unfinished ) {
          assertThat(i.get()).isEqualTo( ref.size() );
          break;
        }
      }

      test.forEachRemaining(actionBoxed);
      assertThat(i.get()).isEqualTo( ref.size() );
    }
  }



  @Property void print_stream_int( @ForAll("arraysInt") int[] arr, @ForAll Random rng ) {
    new SpliterComparator(rng).compare(
      Progress.print(stream(arr)).spliterator(),
                     stream(arr) .spliterator()
    );
  }

  @Property void print_stream_long( @ForAll("arraysLong") long[] arr, @ForAll Random rng ) {
    new SpliterComparator(rng).compare(
      Progress.print(stream(arr)).spliterator(),
                     stream(arr) .spliterator()
    );
  }

  @Property void print_stream_double( @ForAll("arraysDouble") double[] arr, @ForAll Random rng ) {
    new SpliterComparator(rng).compare(
      Progress.print(stream(arr)).spliterator(),
                     stream(arr) .spliterator()
    );
  }

  @Property void print_stream( @ForAll("arraysByte") byte[] unboxed, @ForAll Random rng )
  {
    Byte[] arr = boxed(unboxed);
    new SpliterComparator(rng).compare(
      Progress.print(stream(arr)).spliterator(),
                     stream(arr) .spliterator()
    );
  }



  @Property void print_spliterator_int( @ForAll("arraysInt") int[] arr, @ForAll Random rng ) {
    new SpliterComparator(rng).compare(
      Progress.print(stream(arr).spliterator()),
                     stream(arr).spliterator()
    );
  }

  @Property void print_spliterator_long( @ForAll("arraysLong") long[] arr, @ForAll Random rng ) {
    new SpliterComparator(rng).compare(
      Progress.print(stream(arr).spliterator()),
                     stream(arr).spliterator()
    );
  }

  @Property void print_spliterator_double( @ForAll("arraysDouble") double[] arr, @ForAll Random rng ) {
    new SpliterComparator(rng).compare(
      Progress.print(stream(arr).spliterator()),
                     stream(arr).spliterator()
    );
  }

  @Property void print_spliterator( @ForAll("arraysInt") int[] unboxed, @ForAll Random rng )
  {
    Integer[] arr = boxed(unboxed);
    new SpliterComparator(rng).compare(
      Progress.print(stream(arr).spliterator()),
                     stream(arr).spliterator()
    );
  }



  @Property void print_iterable_iterator( @ForAll("arraysInt") int[] unboxed ) {
    Integer[] arr = boxed(unboxed);
    int i = 0;
    for( int x: Progress.print( asList(arr) ) )
      assertThat(x).isSameAs(arr[i++]);
    assertThat(i).isEqualTo(unboxed.length);
  }

  @Property void print_iterable_forEach( @ForAll("arraysInt") int[] unboxed ) {
    Integer[] arr = boxed(unboxed);
    AtomicInteger i = new AtomicInteger();
    Progress.print( asList(arr) ).forEach(
      x -> assertThat(x).isSameAs( arr[i.getAndIncrement()] )
    );
    assertThat( i.get() ).isEqualTo(unboxed.length);
  }

  @Property void print_iterable_spliterator( @ForAll("arraysInt") int[] unboxed, @ForAll Random rng ) {
    List<Integer> arr = asList( boxed(unboxed) );
    new SpliterComparator(rng).compare( Progress.print( arr.spliterator() ), arr.spliterator() );
  }
}
