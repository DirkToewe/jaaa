package com.github.jaaa;

import net.jqwik.api.ForAll;
import net.jqwik.api.Property;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;


public class ArgMinAccessTest implements ArrayProviderTemplate
{
  @Override public int maxArraySize      () { return 16*1024; }
  @Override public int maxArraySizeString() { return  8*1024; }

  private void test_argMinL( int from, int until, ArgMinAccess acc ) {
    if( from >= until )
      assertThatThrownBy( () -> acc.argMinL(from,until) ).isInstanceOf(IllegalArgumentException.class);
    else {
      int min = acc.argMinL(from,until);
      assertThat(min).isBetween(from,until-1);
      for( int i=from; i < min  ; i++ ) assertThat(acc.compare(min,i)).isLessThan         (0);
      for( int i= min; i < until; i++ ) assertThat(acc.compare(min,i)).isLessThanOrEqualTo(0);
    }
  }

  @Property void argMinLArraysByte  ( @ForAll("arraysByte"  )   byte[] ref ) { test_argMinL( 0,ref.length, (i,j) ->      Byte.compare(ref[i],ref[j]) ); }
  @Property void argMinLArraysShort ( @ForAll("arraysShort" )  short[] ref ) { test_argMinL( 0,ref.length, (i,j) ->     Short.compare(ref[i],ref[j]) ); }
  @Property void argMinLArraysInt   ( @ForAll("arraysInt"   )    int[] ref ) { test_argMinL( 0,ref.length, (i,j) ->   Integer.compare(ref[i],ref[j]) ); }
  @Property void argMinLArraysLong  ( @ForAll("arraysLong"  )   long[] ref ) { test_argMinL( 0,ref.length, (i,j) ->      Long.compare(ref[i],ref[j]) ); }
  @Property void argMinLArraysChar  ( @ForAll("arraysChar"  )   char[] ref ) { test_argMinL( 0,ref.length, (i,j) -> Character.compare(ref[i],ref[j]) ); }
  @Property void argMinLArraysFloat ( @ForAll("arraysFloat" )  float[] ref ) { test_argMinL( 0,ref.length, (i,j) ->     Float.compare(ref[i],ref[j]) ); }
  @Property void argMinLArraysDouble( @ForAll("arraysDouble") double[] ref ) { test_argMinL( 0,ref.length, (i,j) ->    Double.compare(ref[i],ref[j]) ); }

  @Property void argMinLArraysWithRangeByte  ( @ForAll("arraysWithRangeByte"  ) WithRange<  byte[]> x ) { var ref=x.getData(); test_argMinL( x.getFrom(), x.getUntil(), (i,j) ->      Byte.compare(ref[i],ref[j]) ); }
  @Property void argMinLArraysWithRangeShort ( @ForAll("arraysWithRangeShort" ) WithRange< short[]> x ) { var ref=x.getData(); test_argMinL( x.getFrom(), x.getUntil(), (i,j) ->     Short.compare(ref[i],ref[j]) ); }
  @Property void argMinLArraysWithRangeInt   ( @ForAll("arraysWithRangeInt"   ) WithRange<   int[]> x ) { var ref=x.getData(); test_argMinL( x.getFrom(), x.getUntil(), (i,j) ->   Integer.compare(ref[i],ref[j]) ); }
  @Property void argMinLArraysWithRangeLong  ( @ForAll("arraysWithRangeLong"  ) WithRange<  long[]> x ) { var ref=x.getData(); test_argMinL( x.getFrom(), x.getUntil(), (i,j) ->      Long.compare(ref[i],ref[j]) ); }
  @Property void argMinLArraysWithRangeChar  ( @ForAll("arraysWithRangeChar"  ) WithRange<  char[]> x ) { var ref=x.getData(); test_argMinL( x.getFrom(), x.getUntil(), (i,j) -> Character.compare(ref[i],ref[j]) ); }
  @Property void argMinLArraysWithRangeFloat ( @ForAll("arraysWithRangeFloat" ) WithRange< float[]> x ) { var ref=x.getData(); test_argMinL( x.getFrom(), x.getUntil(), (i,j) ->     Float.compare(ref[i],ref[j]) ); }
  @Property void argMinLArraysWithRangeDouble( @ForAll("arraysWithRangeDouble") WithRange<double[]> x ) { var ref=x.getData(); test_argMinL( x.getFrom(), x.getUntil(), (i,j) ->    Double.compare(ref[i],ref[j]) ); }

  private void test_argMinR( int from, int until, ArgMinAccess acc ) {
    if( from >= until ) {
      assertThat(from).isEqualTo(until);
      assertThatThrownBy( () -> acc.argMinR(from,until) ).isInstanceOf(IllegalArgumentException.class);
    }
    else {
      int min = acc.argMinR(from,until);
      assertThat(min).isBetween(from,until-1);
      for( int i=from;   i <= min ; i++ ) assertThat(acc.compare(min,i)).isLessThanOrEqualTo(0);
      for( int i= min; ++i < until;     ) assertThat(acc.compare(min,i)).isLessThan         (0);
    }
  }

  @Property void argMinRArraysByte  ( @ForAll("arraysByte"  )   byte[] ref ) { test_argMinR( 0,ref.length, (i,j) ->      Byte.compare(ref[i],ref[j]) ); }
  @Property void argMinRArraysShort ( @ForAll("arraysShort" )  short[] ref ) { test_argMinR( 0,ref.length, (i,j) ->     Short.compare(ref[i],ref[j]) ); }
  @Property void argMinRArraysInt   ( @ForAll("arraysInt"   )    int[] ref ) { test_argMinR( 0,ref.length, (i,j) ->   Integer.compare(ref[i],ref[j]) ); }
  @Property void argMinRArraysLong  ( @ForAll("arraysLong"  )   long[] ref ) { test_argMinR( 0,ref.length, (i,j) ->      Long.compare(ref[i],ref[j]) ); }
  @Property void argMinRArraysChar  ( @ForAll("arraysChar"  )   char[] ref ) { test_argMinR( 0,ref.length, (i,j) -> Character.compare(ref[i],ref[j]) ); }
  @Property void argMinRArraysFloat ( @ForAll("arraysFloat" )  float[] ref ) { test_argMinR( 0,ref.length, (i,j) ->     Float.compare(ref[i],ref[j]) ); }
  @Property void argMinRArraysDouble( @ForAll("arraysDouble") double[] ref ) { test_argMinR( 0,ref.length, (i,j) ->    Double.compare(ref[i],ref[j]) ); }

  @Property void argMinRArraysWithRangeByte  ( @ForAll("arraysWithRangeByte"  ) WithRange<  byte[]> x ) { var ref=x.getData(); test_argMinR( x.getFrom(), x.getUntil(), (i,j) ->      Byte.compare(ref[i],ref[j]) ); }
  @Property void argMinRArraysWithRangeShort ( @ForAll("arraysWithRangeShort" ) WithRange< short[]> x ) { var ref=x.getData(); test_argMinR( x.getFrom(), x.getUntil(), (i,j) ->     Short.compare(ref[i],ref[j]) ); }
  @Property void argMinRArraysWithRangeInt   ( @ForAll("arraysWithRangeInt"   ) WithRange<   int[]> x ) { var ref=x.getData(); test_argMinR( x.getFrom(), x.getUntil(), (i,j) ->   Integer.compare(ref[i],ref[j]) ); }
  @Property void argMinRArraysWithRangeLong  ( @ForAll("arraysWithRangeLong"  ) WithRange<  long[]> x ) { var ref=x.getData(); test_argMinR( x.getFrom(), x.getUntil(), (i,j) ->      Long.compare(ref[i],ref[j]) ); }
  @Property void argMinRArraysWithRangeChar  ( @ForAll("arraysWithRangeChar"  ) WithRange<  char[]> x ) { var ref=x.getData(); test_argMinR( x.getFrom(), x.getUntil(), (i,j) -> Character.compare(ref[i],ref[j]) ); }
  @Property void argMinRArraysWithRangeFloat ( @ForAll("arraysWithRangeFloat" ) WithRange< float[]> x ) { var ref=x.getData(); test_argMinR( x.getFrom(), x.getUntil(), (i,j) ->     Float.compare(ref[i],ref[j]) ); }
  @Property void argMinRArraysWithRangeDouble( @ForAll("arraysWithRangeDouble") WithRange<double[]> x ) { var ref=x.getData(); test_argMinR( x.getFrom(), x.getUntil(), (i,j) ->    Double.compare(ref[i],ref[j]) ); }
}
