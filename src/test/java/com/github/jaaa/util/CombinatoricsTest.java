package com.github.jaaa.util;

import net.jqwik.api.Example;
import net.jqwik.api.ForAll;
import net.jqwik.api.Property;
import net.jqwik.api.PropertyDefaults;
import net.jqwik.api.constraints.IntRange;

import java.util.*;
import java.util.function.Consumer;

import static com.github.jaaa.misc.Boxing.boxed;
import static com.github.jaaa.util.Combinatorics.factorial;
import static com.github.jaaa.util.Combinatorics.permutations;
import static java.util.Arrays.asList;
import static java.util.Collections.newSetFromMap;
import static org.assertj.core.api.Assertions.assertThat;


@PropertyDefaults( tries = 100_000 )
public class CombinatoricsTest
{
// STATIC FIELDS

// STATIC CONSTRUCTOR

// STATIC METHODS
  @Example void test_factorial0() {
    assertThat( factorial(0) ).isEqualTo(1);
  }
  @Property( tries = Integer.MAX_VALUE ) void test_factorial( @ForAll @IntRange(min=1, max=20) int n )
  {
    assertThat( factorial(n) ).isEqualTo( factorial(n-1)*n );
  }

  @Property void test_permutations_tryAdvance( @ForAll @IntRange(min=0, max=10) int n ) {
    var spltr = permutations(n).spliterator();
    var perms = new ArrayList<byte[]>( (int) factorial(n) );
    Consumer<byte[]>               perms_add = perms::add;
    do {} while ( spltr.tryAdvance(perms_add) );
    test_permutations(n,perms);
  }
  @Property void test_permutations_forEachRemaining( @ForAll @IntRange(min=0, max=10) int n ) {
    var perms = new ArrayList<byte[]>( (int) factorial(n) );
    permutations(n).spliterator().forEachRemaining(perms::add);
    test_permutations(n,perms);
  }
  @Property void test_permutations_split( @ForAll @IntRange(min=0, max=7) int n, @ForAll Random rng ) {
    new Object()
    {
      private List<byte[]> perms = new ArrayList<>( (int) factorial(n) );
      private Consumer<byte[]> perms_add = perms::add;

      private void collect( Spliterator<byte[]> rest )
      {
        if( rng.nextDouble() < 1/3d )
        {
          var left = rest.trySplit();
          if( left != null ) {
            if( rng.nextBoolean() ) {
              collect(left);
              collect(rest);
            }
            else {
              collect(rest);
              collect(left);
            }
            return;
          }
        }
        var        size = rest.estimateSize();
        assertThat(size).isBetween(0L, (long) Integer.MAX_VALUE);
        int nAdvance = rng.nextInt( (int) size );
        for( int i=0; i < nAdvance; i++ ) {
          assertThat( rest.estimateSize() ).isEqualTo(size);
          assertThat( rest.tryAdvance(perms_add) ).isTrue();
          assertThat( rest.estimateSize() ).isEqualTo(--size);
        }
        rest.forEachRemaining(perms_add);
        assertThat( rest.estimateSize() ).isEqualTo(0);
      }

      {
        collect( permutations(n).spliterator() );
        test_permutations(n, perms);
      }
    };
  }
  @Property     void test_permutations         ( @ForAll @IntRange(min=0, max=10) int n ) { test_permutations( n, permutations(n)           .toList() ); }
  @Property     void test_permutations_parallel( @ForAll @IntRange(min=0, max=10) int n ) { test_permutations( n, permutations(n).parallel().toList() ); }
  public static void test_permutations( int n, List<byte[]> permutations )
  {
    assertThat(n).isBetween(0, (int) Byte.MAX_VALUE);
    assertThat( permutations.size() ).isEqualTo( factorial(n) );

    var idSet = newSetFromMap( new IdentityHashMap<>() );
    var set = new HashSet<List<Byte>>();

    var ref = new byte[n];
    for( byte i=0; ++i < n; )
      ref[i] = i;

    for( var x: permutations ) {
      assertThat(x.length).isEqualTo(n);
      assertThat( idSet.add(x) ).isTrue();

      var y = asList(boxed(x));
      assertThat( set.add(y) ).isTrue();

      Arrays.sort(x);
      assertThat(x).isEqualTo(ref);
    }
  }

// FIELDS

// CONSTRUCTORS

// METHODS
}
