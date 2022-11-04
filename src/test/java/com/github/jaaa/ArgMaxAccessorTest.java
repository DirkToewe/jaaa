package com.github.jaaa;

import net.jqwik.api.ForAll;
import net.jqwik.api.Property;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class ArgMaxAccessorTest implements ArrayProviderTemplate
{
  @Override public int maxArraySize      () { return 16*1024; }
  @Override public int maxArraySizeString() { return  8*1024; }

  private <T> void test_argMaxL( T arr, int from, int until, ArgMaxAccessor<? super T> acc ) {
    if( from >= until )
      assertThatThrownBy( () -> acc.argMaxL(arr,from,until) ).isInstanceOf(IllegalArgumentException.class);
    else {
      int        min = acc.argMaxL(arr,from,until);
      assertThat(min).isBetween(from,until-1);
      for( int i=from; i < min  ; i++ ) assertThat(acc.compare(arr,min, arr,i)).isGreaterThan( 0);
      for( int i= min; i < until; i++ ) assertThat(acc.compare(arr,min, arr,i)).isGreaterThan(-1);
    }
  }

  @Property void argMaxLArraysByte  ( @ForAll("arraysByte"  )   byte[] ref ) { test_argMaxL( ref, 0, ref.length, (a,i, b,j) ->      Byte.compare(a[i], b[j]) ); }
  @Property void argMaxLArraysShort ( @ForAll("arraysShort" )  short[] ref ) { test_argMaxL( ref, 0, ref.length, (a,i, b,j) ->     Short.compare(a[i], b[j]) ); }
  @Property void argMaxLArraysInt   ( @ForAll("arraysInt"   )    int[] ref ) { test_argMaxL( ref, 0, ref.length, (a,i, b,j) ->   Integer.compare(a[i], b[j]) ); }
  @Property void argMaxLArraysLong  ( @ForAll("arraysLong"  )   long[] ref ) { test_argMaxL( ref, 0, ref.length, (a,i, b,j) ->      Long.compare(a[i], b[j]) ); }
  @Property void argMaxLArraysChar  ( @ForAll("arraysChar"  )   char[] ref ) { test_argMaxL( ref, 0, ref.length, (a,i, b,j) -> Character.compare(a[i], b[j]) ); }
  @Property void argMaxLArraysFloat ( @ForAll("arraysFloat" )  float[] ref ) { test_argMaxL( ref, 0, ref.length, (a,i, b,j) ->     Float.compare(a[i], b[j]) ); }
  @Property void argMaxLArraysDouble( @ForAll("arraysDouble") double[] ref ) { test_argMaxL( ref, 0, ref.length, (a,i, b,j) ->    Double.compare(a[i], b[j]) ); }

  @Property void argMaxLArraysWithRangeByte  ( @ForAll("arraysWithRangeByte"  ) WithRange<  byte[]> x ) { test_argMaxL( x.getData(), x.getFrom(), x.getUntil(), (a,i, b,j) ->      Byte.compare(a[i],b[j]) ); }
  @Property void argMaxLArraysWithRangeShort ( @ForAll("arraysWithRangeShort" ) WithRange< short[]> x ) { test_argMaxL( x.getData(), x.getFrom(), x.getUntil(), (a,i, b,j) ->     Short.compare(a[i],b[j]) ); }
  @Property void argMaxLArraysWithRangeInt   ( @ForAll("arraysWithRangeInt"   ) WithRange<   int[]> x ) { test_argMaxL( x.getData(), x.getFrom(), x.getUntil(), (a,i, b,j) ->   Integer.compare(a[i],b[j]) ); }
  @Property void argMaxLArraysWithRangeLong  ( @ForAll("arraysWithRangeLong"  ) WithRange<  long[]> x ) { test_argMaxL( x.getData(), x.getFrom(), x.getUntil(), (a,i, b,j) ->      Long.compare(a[i],b[j]) ); }
  @Property void argMaxLArraysWithRangeChar  ( @ForAll("arraysWithRangeChar"  ) WithRange<  char[]> x ) { test_argMaxL( x.getData(), x.getFrom(), x.getUntil(), (a,i, b,j) -> Character.compare(a[i],b[j]) ); }
  @Property void argMaxLArraysWithRangeFloat ( @ForAll("arraysWithRangeFloat" ) WithRange< float[]> x ) { test_argMaxL( x.getData(), x.getFrom(), x.getUntil(), (a,i, b,j) ->     Float.compare(a[i],b[j]) ); }
  @Property void argMaxLArraysWithRangeDouble( @ForAll("arraysWithRangeDouble") WithRange<double[]> x ) { test_argMaxL( x.getData(), x.getFrom(), x.getUntil(), (a,i, b,j) ->    Double.compare(a[i],b[j]) ); }

  private <T> void test_argMaxR( T arr, int from, int until, ArgMaxAccessor<? super T> acc ) {
    if( from >= until ) {
      assertThat(from).isEqualTo(until);
      assertThatThrownBy( () -> acc.argMaxR(arr,from,until) ).isInstanceOf(IllegalArgumentException.class);
    }
    else {
      int        min = acc.argMaxR(arr,from,until);
      assertThat(min).isBetween(from,until-1);
      for( int i=from;   i <= min ; i++ ) assertThat(acc.compare(arr,min, arr,i)).isGreaterThan(-1);
      for( int i= min; ++i < until;     ) assertThat(acc.compare(arr,min, arr,i)).isGreaterThan( 0);
    }
  }

  @Property void argMaxRArraysByte  ( @ForAll("arraysByte"  )   byte[] ref ) { test_argMaxR( ref, 0, ref.length, (a,i, b,j) ->      Byte.compare(a[i], b[j]) ); }
  @Property void argMaxRArraysShort ( @ForAll("arraysShort" )  short[] ref ) { test_argMaxR( ref, 0, ref.length, (a,i, b,j) ->     Short.compare(a[i], b[j]) ); }
  @Property void argMaxRArraysInt   ( @ForAll("arraysInt"   )    int[] ref ) { test_argMaxR( ref, 0, ref.length, (a,i, b,j) ->   Integer.compare(a[i], b[j]) ); }
  @Property void argMaxRArraysLong  ( @ForAll("arraysLong"  )   long[] ref ) { test_argMaxR( ref, 0, ref.length, (a,i, b,j) ->      Long.compare(a[i], b[j]) ); }
  @Property void argMaxRArraysChar  ( @ForAll("arraysChar"  )   char[] ref ) { test_argMaxR( ref, 0, ref.length, (a,i, b,j) -> Character.compare(a[i], b[j]) ); }
  @Property void argMaxRArraysFloat ( @ForAll("arraysFloat" )  float[] ref ) { test_argMaxR( ref, 0, ref.length, (a,i, b,j) ->     Float.compare(a[i], b[j]) ); }
  @Property void argMaxRArraysDouble( @ForAll("arraysDouble") double[] ref ) { test_argMaxR( ref, 0, ref.length, (a,i, b,j) ->    Double.compare(a[i], b[j]) ); }

  @Property void argMaxRArraysWithRangeByte  ( @ForAll("arraysWithRangeByte"  ) WithRange<  byte[]> x ) { test_argMaxR( x.getData(), x.getFrom(), x.getUntil(), (a,i, b,j) ->      Byte.compare(a[i],b[j]) ); }
  @Property void argMaxRArraysWithRangeShort ( @ForAll("arraysWithRangeShort" ) WithRange< short[]> x ) { test_argMaxR( x.getData(), x.getFrom(), x.getUntil(), (a,i, b,j) ->     Short.compare(a[i],b[j]) ); }
  @Property void argMaxRArraysWithRangeInt   ( @ForAll("arraysWithRangeInt"   ) WithRange<   int[]> x ) { test_argMaxR( x.getData(), x.getFrom(), x.getUntil(), (a,i, b,j) ->   Integer.compare(a[i],b[j]) ); }
  @Property void argMaxRArraysWithRangeLong  ( @ForAll("arraysWithRangeLong"  ) WithRange<  long[]> x ) { test_argMaxR( x.getData(), x.getFrom(), x.getUntil(), (a,i, b,j) ->      Long.compare(a[i],b[j]) ); }
  @Property void argMaxRArraysWithRangeChar  ( @ForAll("arraysWithRangeChar"  ) WithRange<  char[]> x ) { test_argMaxR( x.getData(), x.getFrom(), x.getUntil(), (a,i, b,j) -> Character.compare(a[i],b[j]) ); }
  @Property void argMaxRArraysWithRangeFloat ( @ForAll("arraysWithRangeFloat" ) WithRange< float[]> x ) { test_argMaxR( x.getData(), x.getFrom(), x.getUntil(), (a,i, b,j) ->     Float.compare(a[i],b[j]) ); }
  @Property void argMaxRArraysWithRangeDouble( @ForAll("arraysWithRangeDouble") WithRange<double[]> x ) { test_argMaxR( x.getData(), x.getFrom(), x.getUntil(), (a,i, b,j) ->    Double.compare(a[i],b[j]) ); }
}
