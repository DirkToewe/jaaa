package com.github.jaaa.compare;

import com.github.jaaa.ArrayProviderTemplate;
import com.github.jaaa.WithRange;
import net.jqwik.api.ForAll;
import net.jqwik.api.Property;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;


public class ArgMinAccessorTest implements ArrayProviderTemplate
{
  @Override public int maxArraySize      () { return 16*1024; }
  @Override public int maxArraySizeString() { return  8*1024; }

  private <T> void test_argMinL( T arr, int from, int until, ArgMinAccessor<T> acc ) {
    if( from >= until )
      assertThatThrownBy( () -> acc.argMinL(arr,from,until) ).isInstanceOf(IllegalArgumentException.class);
    else {
      int min = acc.argMinL(arr,from,until);
      assertThat(min).isBetween(from,until-1);
      for( int i=from; i < min  ; i++ ) assertThat(acc.compare(arr,min, arr,i)).isLessThan         (0);
      for( int i= min; i < until; i++ ) assertThat(acc.compare(arr,min, arr,i)).isLessThanOrEqualTo(0);
    }
  }

  @Property void argMinLArraysByte  ( @ForAll("arraysByte"  )   byte[] ref ) { test_argMinL( ref, 0, ref.length, (a,i, b,j) ->      Byte.compare(a[i], b[j]) ); }
  @Property void argMinLArraysShort ( @ForAll("arraysShort" )  short[] ref ) { test_argMinL( ref, 0, ref.length, (a,i, b,j) ->     Short.compare(a[i], b[j]) ); }
  @Property void argMinLArraysInt   ( @ForAll("arraysInt"   )    int[] ref ) { test_argMinL( ref, 0, ref.length, (a,i, b,j) ->   Integer.compare(a[i], b[j]) ); }
  @Property void argMinLArraysLong  ( @ForAll("arraysLong"  )   long[] ref ) { test_argMinL( ref, 0, ref.length, (a,i, b,j) ->      Long.compare(a[i], b[j]) ); }
  @Property void argMinLArraysChar  ( @ForAll("arraysChar"  )   char[] ref ) { test_argMinL( ref, 0, ref.length, (a,i, b,j) -> Character.compare(a[i], b[j]) ); }
  @Property void argMinLArraysFloat ( @ForAll("arraysFloat" )  float[] ref ) { test_argMinL( ref, 0, ref.length, (a,i, b,j) ->     Float.compare(a[i], b[j]) ); }
  @Property void argMinLArraysDouble( @ForAll("arraysDouble") double[] ref ) { test_argMinL( ref, 0, ref.length, (a,i, b,j) ->    Double.compare(a[i], b[j]) ); }

  @Property void argMinLArraysWithRangeByte  ( @ForAll("arraysWithRangeByte"  ) WithRange<  byte[]> x ) { test_argMinL( x.getData(), x.getFrom(), x.getUntil(), (a, i, b, j) ->      Byte.compare(a[i], b[j]) ); }
  @Property void argMinLArraysWithRangeShort ( @ForAll("arraysWithRangeShort" ) WithRange< short[]> x ) { test_argMinL( x.getData(), x.getFrom(), x.getUntil(), (a,i, b,j) ->     Short.compare(a[i], b[j]) ); }
  @Property void argMinLArraysWithRangeInt   ( @ForAll("arraysWithRangeInt"   ) WithRange<   int[]> x ) { test_argMinL( x.getData(), x.getFrom(), x.getUntil(), (a,i, b,j) ->   Integer.compare(a[i], b[j]) ); }
  @Property void argMinLArraysWithRangeLong  ( @ForAll("arraysWithRangeLong"  ) WithRange<  long[]> x ) { test_argMinL( x.getData(), x.getFrom(), x.getUntil(), (a,i, b,j) ->      Long.compare(a[i], b[j]) ); }
  @Property void argMinLArraysWithRangeChar  ( @ForAll("arraysWithRangeChar"  ) WithRange<  char[]> x ) { test_argMinL( x.getData(), x.getFrom(), x.getUntil(), (a,i, b,j) -> Character.compare(a[i], b[j]) ); }
  @Property void argMinLArraysWithRangeFloat ( @ForAll("arraysWithRangeFloat" ) WithRange< float[]> x ) { test_argMinL( x.getData(), x.getFrom(), x.getUntil(), (a,i, b,j) ->     Float.compare(a[i], b[j]) ); }
  @Property void argMinLArraysWithRangeDouble( @ForAll("arraysWithRangeDouble") WithRange<double[]> x ) { test_argMinL( x.getData(), x.getFrom(), x.getUntil(), (a,i, b,j) ->    Double.compare(a[i], b[j]) ); }

  private <T> void test_argMinR( T arr, int from, int until, ArgMinAccessor<T> acc ) {
    if( from >= until ) {
      assertThat(from).isEqualTo(until);
      assertThatThrownBy( () -> acc.argMinR(arr,from,until) ).isInstanceOf(IllegalArgumentException.class);
    }
    else {
      int min = acc.argMinR(arr,from,until);
      assertThat(min).isBetween(from,until-1);
      for( int i=from;   i <= min ; i++ ) assertThat(acc.compare(arr,min, arr,i)).isLessThanOrEqualTo(0);
      for( int i= min; ++i < until;     ) assertThat(acc.compare(arr,min, arr,i)).isLessThan         (0);
    }
  }

  @Property void argMinRArraysByte  ( @ForAll("arraysByte"  )   byte[] ref ) { test_argMinR( ref, 0, ref.length, (a,i, b,j) ->      Byte.compare(a[i], b[j]) ); }
  @Property void argMinRArraysShort ( @ForAll("arraysShort" )  short[] ref ) { test_argMinR( ref, 0, ref.length, (a,i, b,j) ->     Short.compare(a[i], b[j]) ); }
  @Property void argMinRArraysInt   ( @ForAll("arraysInt"   )    int[] ref ) { test_argMinR( ref, 0, ref.length, (a,i, b,j) ->   Integer.compare(a[i], b[j]) ); }
  @Property void argMinRArraysLong  ( @ForAll("arraysLong"  )   long[] ref ) { test_argMinR( ref, 0, ref.length, (a,i, b,j) ->      Long.compare(a[i], b[j]) ); }
  @Property void argMinRArraysChar  ( @ForAll("arraysChar"  )   char[] ref ) { test_argMinR( ref, 0, ref.length, (a,i, b,j) -> Character.compare(a[i], b[j]) ); }
  @Property void argMinRArraysFloat ( @ForAll("arraysFloat" )  float[] ref ) { test_argMinR( ref, 0, ref.length, (a,i, b,j) ->     Float.compare(a[i], b[j]) ); }
  @Property void argMinRArraysDouble( @ForAll("arraysDouble") double[] ref ) { test_argMinR( ref, 0, ref.length, (a,i, b,j) ->    Double.compare(a[i], b[j]) ); }

  @Property void argMinRArraysWithRangeByte  ( @ForAll("arraysWithRangeByte"  ) WithRange<  byte[]> x ) { test_argMinR( x.getData(), x.getFrom(), x.getUntil(), (a,i, b,j) ->      Byte.compare(a[i], b[j]) ); }
  @Property void argMinRArraysWithRangeShort ( @ForAll("arraysWithRangeShort" ) WithRange< short[]> x ) { test_argMinR( x.getData(), x.getFrom(), x.getUntil(), (a,i, b,j) ->     Short.compare(a[i], b[j]) ); }
  @Property void argMinRArraysWithRangeInt   ( @ForAll("arraysWithRangeInt"   ) WithRange<   int[]> x ) { test_argMinR( x.getData(), x.getFrom(), x.getUntil(), (a,i, b,j) ->   Integer.compare(a[i], b[j]) ); }
  @Property void argMinRArraysWithRangeLong  ( @ForAll("arraysWithRangeLong"  ) WithRange<  long[]> x ) { test_argMinR( x.getData(), x.getFrom(), x.getUntil(), (a,i, b,j) ->      Long.compare(a[i], b[j]) ); }
  @Property void argMinRArraysWithRangeChar  ( @ForAll("arraysWithRangeChar"  ) WithRange<  char[]> x ) { test_argMinR( x.getData(), x.getFrom(), x.getUntil(), (a,i, b,j) -> Character.compare(a[i], b[j]) ); }
  @Property void argMinRArraysWithRangeFloat ( @ForAll("arraysWithRangeFloat" ) WithRange< float[]> x ) { test_argMinR( x.getData(), x.getFrom(), x.getUntil(), (a,i, b,j) ->     Float.compare(a[i], b[j]) ); }
  @Property void argMinRArraysWithRangeDouble( @ForAll("arraysWithRangeDouble") WithRange<double[]> x ) { test_argMinR( x.getData(), x.getFrom(), x.getUntil(), (a,i, b,j) ->    Double.compare(a[i], b[j]) ); }
}
