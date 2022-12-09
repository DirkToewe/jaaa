package com.github.jaaa.compare;

import com.github.jaaa.ArrayProviderTemplate;
import com.github.jaaa.WithRange;
import net.jqwik.api.ForAll;
import net.jqwik.api.Property;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;


public class ArgMaxAccessTest implements ArrayProviderTemplate
{
  @Override public int maxArraySize      () { return 16*1024; }
  @Override public int maxArraySizeString() { return  8*1024; }

  private void test_argMaxL( int from, int until, ArgMaxAccess acc ) {
    if( from >= until )
      assertThatThrownBy( () -> acc.argMaxL(from,until) ).isInstanceOf(IllegalArgumentException.class);
    else {
      int min = acc.argMaxL(from,until);
      assertThat(min).isBetween(from,until-1);
      for( int i=from; i < min  ; i++ ) assertThat(acc.compare(min,i)).isGreaterThan( 0);
      for( int i= min; i < until; i++ ) assertThat(acc.compare(min,i)).isGreaterThan(-1);
    }
  }

  @Property void argMaxLArraysByte  ( @ForAll("arraysByte"  )   byte[] ref ) { test_argMaxL( 0,ref.length, (i,j) ->      Byte.compare(ref[i],ref[j]) ); }
  @Property void argMaxLArraysShort ( @ForAll("arraysShort" )  short[] ref ) { test_argMaxL( 0,ref.length, (i,j) ->     Short.compare(ref[i],ref[j]) ); }
  @Property void argMaxLArraysInt   ( @ForAll("arraysInt"   )    int[] ref ) { test_argMaxL( 0,ref.length, (i,j) ->   Integer.compare(ref[i],ref[j]) ); }
  @Property void argMaxLArraysLong  ( @ForAll("arraysLong"  )   long[] ref ) { test_argMaxL( 0,ref.length, (i,j) ->      Long.compare(ref[i],ref[j]) ); }
  @Property void argMaxLArraysChar  ( @ForAll("arraysChar"  )   char[] ref ) { test_argMaxL( 0,ref.length, (i,j) -> Character.compare(ref[i],ref[j]) ); }
  @Property void argMaxLArraysFloat ( @ForAll("arraysFloat" )  float[] ref ) { test_argMaxL( 0,ref.length, (i,j) ->     Float.compare(ref[i],ref[j]) ); }
  @Property void argMaxLArraysDouble( @ForAll("arraysDouble") double[] ref ) { test_argMaxL( 0,ref.length, (i,j) ->    Double.compare(ref[i],ref[j]) ); }

  @Property void argMaxLArraysWithRangeByte  ( @ForAll("arraysWithRangeByte"  ) WithRange<  byte[]> x ) { var ref=x.getData(); test_argMaxL( x.getFrom(), x.getUntil(), (i, j) ->      Byte.compare(ref[i],ref[j]) ); }
  @Property void argMaxLArraysWithRangeShort ( @ForAll("arraysWithRangeShort" ) WithRange< short[]> x ) { var ref=x.getData(); test_argMaxL( x.getFrom(), x.getUntil(), (i,j) ->     Short.compare(ref[i],ref[j]) ); }
  @Property void argMaxLArraysWithRangeInt   ( @ForAll("arraysWithRangeInt"   ) WithRange<   int[]> x ) { var ref=x.getData(); test_argMaxL( x.getFrom(), x.getUntil(), (i,j) ->   Integer.compare(ref[i],ref[j]) ); }
  @Property void argMaxLArraysWithRangeLong  ( @ForAll("arraysWithRangeLong"  ) WithRange<  long[]> x ) { var ref=x.getData(); test_argMaxL( x.getFrom(), x.getUntil(), (i,j) ->      Long.compare(ref[i],ref[j]) ); }
  @Property void argMaxLArraysWithRangeChar  ( @ForAll("arraysWithRangeChar"  ) WithRange<  char[]> x ) { var ref=x.getData(); test_argMaxL( x.getFrom(), x.getUntil(), (i,j) -> Character.compare(ref[i],ref[j]) ); }
  @Property void argMaxLArraysWithRangeFloat ( @ForAll("arraysWithRangeFloat" ) WithRange< float[]> x ) { var ref=x.getData(); test_argMaxL( x.getFrom(), x.getUntil(), (i,j) ->     Float.compare(ref[i],ref[j]) ); }
  @Property void argMaxLArraysWithRangeDouble( @ForAll("arraysWithRangeDouble") WithRange<double[]> x ) { var ref=x.getData(); test_argMaxL( x.getFrom(), x.getUntil(), (i,j) ->    Double.compare(ref[i],ref[j]) ); }

  private void test_argMaxR( int from, int until, ArgMaxAccess acc ) {
    if( from >= until ) {
      assertThat(from).isEqualTo(until);
      assertThatThrownBy( () -> acc.argMaxR(from,until) ).isInstanceOf(IllegalArgumentException.class);
    }
    else {
      int min = acc.argMaxR(from,until);
      assertThat(min).isBetween(from,until-1);
      for( int i=from;   i <= min ; i++ ) assertThat(acc.compare(min,i)).isGreaterThan(-1);
      for( int i= min; ++i < until;     ) assertThat(acc.compare(min,i)).isGreaterThan( 0);
    }
  }

  @Property void argMaxRArraysByte  ( @ForAll("arraysByte"  )   byte[] ref ) { test_argMaxR( 0,ref.length, (i,j) ->      Byte.compare(ref[i],ref[j]) ); }
  @Property void argMaxRArraysShort ( @ForAll("arraysShort" )  short[] ref ) { test_argMaxR( 0,ref.length, (i,j) ->     Short.compare(ref[i],ref[j]) ); }
  @Property void argMaxRArraysInt   ( @ForAll("arraysInt"   )    int[] ref ) { test_argMaxR( 0,ref.length, (i,j) ->   Integer.compare(ref[i],ref[j]) ); }
  @Property void argMaxRArraysLong  ( @ForAll("arraysLong"  )   long[] ref ) { test_argMaxR( 0,ref.length, (i,j) ->      Long.compare(ref[i],ref[j]) ); }
  @Property void argMaxRArraysChar  ( @ForAll("arraysChar"  )   char[] ref ) { test_argMaxR( 0,ref.length, (i,j) -> Character.compare(ref[i],ref[j]) ); }
  @Property void argMaxRArraysFloat ( @ForAll("arraysFloat" )  float[] ref ) { test_argMaxR( 0,ref.length, (i,j) ->     Float.compare(ref[i],ref[j]) ); }
  @Property void argMaxRArraysDouble( @ForAll("arraysDouble") double[] ref ) { test_argMaxR( 0,ref.length, (i,j) ->    Double.compare(ref[i],ref[j]) ); }

  @Property void argMaxRArraysWithRangeByte  ( @ForAll("arraysWithRangeByte"  ) WithRange<  byte[]> x ) { var ref=x.getData(); test_argMaxR( x.getFrom(), x.getUntil(), (i,j) ->      Byte.compare(ref[i],ref[j]) ); }
  @Property void argMaxRArraysWithRangeShort ( @ForAll("arraysWithRangeShort" ) WithRange< short[]> x ) { var ref=x.getData(); test_argMaxR( x.getFrom(), x.getUntil(), (i,j) ->     Short.compare(ref[i],ref[j]) ); }
  @Property void argMaxRArraysWithRangeInt   ( @ForAll("arraysWithRangeInt"   ) WithRange<   int[]> x ) { var ref=x.getData(); test_argMaxR( x.getFrom(), x.getUntil(), (i,j) ->   Integer.compare(ref[i],ref[j]) ); }
  @Property void argMaxRArraysWithRangeLong  ( @ForAll("arraysWithRangeLong"  ) WithRange<  long[]> x ) { var ref=x.getData(); test_argMaxR( x.getFrom(), x.getUntil(), (i,j) ->      Long.compare(ref[i],ref[j]) ); }
  @Property void argMaxRArraysWithRangeChar  ( @ForAll("arraysWithRangeChar"  ) WithRange<  char[]> x ) { var ref=x.getData(); test_argMaxR( x.getFrom(), x.getUntil(), (i,j) -> Character.compare(ref[i],ref[j]) ); }
  @Property void argMaxRArraysWithRangeFloat ( @ForAll("arraysWithRangeFloat" ) WithRange< float[]> x ) { var ref=x.getData(); test_argMaxR( x.getFrom(), x.getUntil(), (i,j) ->     Float.compare(ref[i],ref[j]) ); }
  @Property void argMaxRArraysWithRangeDouble( @ForAll("arraysWithRangeDouble") WithRange<double[]> x ) { var ref=x.getData(); test_argMaxR( x.getFrom(), x.getUntil(), (i,j) ->    Double.compare(ref[i],ref[j]) ); }
}
