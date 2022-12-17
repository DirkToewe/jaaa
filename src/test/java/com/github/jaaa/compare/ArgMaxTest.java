package com.github.jaaa.compare;

import com.github.jaaa.ArrayProviderTemplate;
import com.github.jaaa.WithRange;
import net.jqwik.api.ForAll;
import net.jqwik.api.Property;

import java.lang.reflect.Array;
import java.util.Comparator;
import java.util.function.ToIntFunction;

import static com.github.jaaa.compare.ArgMax.argMaxL;
import static com.github.jaaa.compare.ArgMax.argMaxR;
import static com.github.jaaa.Boxing.boxed;
import static java.lang.System.arraycopy;
import static java.util.Comparator.naturalOrder;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;


public class ArgMaxTest implements ArrayProviderTemplate
{
  @Override public int maxArraySize      () { return 16*1024; }
  @Override public int maxArraySizeString() { return  2*1024; }

  @SuppressWarnings({"rawtypes", "unchecked", "SuspiciousSystemArraycopy"})
  private <A> void argMaxLArrays( A array, int from, int until, Comparator cmp, ToIntFunction<? super A> argMax ) {
    assertThat(from).isGreaterThanOrEqualTo(0);
    assertThat(from).isLessThanOrEqualTo(until);

    int len = Array.getLength(array);

    A tst = (A) Array.newInstance(array.getClass().getComponentType(), len);
    arraycopy(array,0, tst,0, len);

    if( from >= until )
      assertThatThrownBy( () -> argMax.applyAsInt(tst) ).isInstanceOf(IllegalArgumentException.class);
    else {
      int        iMin = argMax.applyAsInt(tst);
      assertThat(iMin).isBetween(from,until-1);
      assertThat(tst).isEqualTo(array);

      Object min = Array.get(array,iMin);

      for( int i=from; i < until; i++ ) {
        int c = cmp.compare(min, Array.get(array,i));
        assertThat(c).isGreaterThan( i < iMin ? 0 : -1 );
      }
    }
  }
  @Property void argMaxLArraysByte                        ( @ForAll("arraysByte"           )             byte[]  ref                                            ) {                                               argMaxLArrays(       ref          ,    0,ref.length, naturalOrder(), ArgMax::argMaxL); }
  @Property void argMaxLArraysShort                       ( @ForAll("arraysShort"          )            short[]  ref                                            ) {                                               argMaxLArrays(       ref          ,    0,ref.length, naturalOrder(), ArgMax::argMaxL); }
  @Property void argMaxLArraysInt                         ( @ForAll("arraysInt"            )              int[]  ref                                            ) {                                               argMaxLArrays(       ref          ,    0,ref.length, naturalOrder(), ArgMax::argMaxL); }
  @Property void argMaxLArraysLong                        ( @ForAll("arraysLong"           )             long[]  ref                                            ) {                                               argMaxLArrays(       ref          ,    0,ref.length, naturalOrder(), ArgMax::argMaxL); }
  @Property void argMaxLArraysChar                        ( @ForAll("arraysChar"           )             char[]  ref                                            ) {                                               argMaxLArrays(       ref          ,    0,ref.length, naturalOrder(), ArgMax::argMaxL); }
  @Property void argMaxLArraysFloat                       ( @ForAll("arraysFloat"          )            float[]  ref                                            ) {                                               argMaxLArrays(       ref          ,    0,ref.length, naturalOrder(), ArgMax::argMaxL); }
  @Property void argMaxLArraysDouble                      ( @ForAll("arraysDouble"         )           double[]  ref                                            ) {                                               argMaxLArrays(       ref          ,    0,ref.length, naturalOrder(), ArgMax::argMaxL); }
  @Property void argMaxLArraysObjByte                     ( @ForAll("arraysByte"           )             byte[]  ref                                            ) {                                               argMaxLArrays( boxed(ref)         ,    0,ref.length, naturalOrder(), ArgMax::argMaxL); }
  @Property void argMaxLArraysObjShort                    ( @ForAll("arraysShort"          )            short[]  ref                                            ) {                                               argMaxLArrays( boxed(ref)         ,    0,ref.length, naturalOrder(), ArgMax::argMaxL); }
  @Property void argMaxLArraysObjInt                      ( @ForAll("arraysInt"            )              int[]  ref                                            ) {                                               argMaxLArrays( boxed(ref)         ,    0,ref.length, naturalOrder(), ArgMax::argMaxL); }
  @Property void argMaxLArraysObjLong                     ( @ForAll("arraysLong"           )             long[]  ref                                            ) {                                               argMaxLArrays( boxed(ref)         ,    0,ref.length, naturalOrder(), ArgMax::argMaxL); }
  @Property void argMaxLArraysObjChar                     ( @ForAll("arraysChar"           )             char[]  ref                                            ) {                                               argMaxLArrays( boxed(ref)         ,    0,ref.length, naturalOrder(), ArgMax::argMaxL); }
  @Property void argMaxLArraysObjFloat                    ( @ForAll("arraysFloat"          )            float[]  ref                                            ) {                                               argMaxLArrays( boxed(ref)         ,    0,ref.length, naturalOrder(), ArgMax::argMaxL); }
  @Property void argMaxLArraysObjDouble                   ( @ForAll("arraysDouble"         )           double[]  ref                                            ) {                                               argMaxLArrays( boxed(ref)         ,    0,ref.length, naturalOrder(), ArgMax::argMaxL); }
  @Property void argMaxLArraysWithRangeByte               ( @ForAll("arraysWithRangeByte"  ) WithRange<  byte[]> ref                                            ) { int from=ref.getFrom(), until=ref.getUntil(); argMaxLArrays(       ref.getData(), from,until,      naturalOrder(), x -> argMaxL(x,from,until) ); }
  @Property void argMaxLArraysWithRangeShort              ( @ForAll("arraysWithRangeShort" ) WithRange< short[]> ref                                            ) { int from=ref.getFrom(), until=ref.getUntil(); argMaxLArrays(       ref.getData(), from,until,      naturalOrder(), x -> argMaxL(x,from,until) ); }
  @Property void argMaxLArraysWithRangeInt                ( @ForAll("arraysWithRangeInt"   ) WithRange<   int[]> ref                                            ) { int from=ref.getFrom(), until=ref.getUntil(); argMaxLArrays(       ref.getData(), from,until,      naturalOrder(), x -> argMaxL(x,from,until) ); }
  @Property void argMaxLArraysWithRangeLong               ( @ForAll("arraysWithRangeLong"  ) WithRange<  long[]> ref                                            ) { int from=ref.getFrom(), until=ref.getUntil(); argMaxLArrays(       ref.getData(), from,until,      naturalOrder(), x -> argMaxL(x,from,until) ); }
  @Property void argMaxLArraysWithRangeChar               ( @ForAll("arraysWithRangeChar"  ) WithRange<  char[]> ref                                            ) { int from=ref.getFrom(), until=ref.getUntil(); argMaxLArrays(       ref.getData(), from,until,      naturalOrder(), x -> argMaxL(x,from,until) ); }
  @Property void argMaxLArraysWithRangeFloat              ( @ForAll("arraysWithRangeFloat" ) WithRange< float[]> ref                                            ) { int from=ref.getFrom(), until=ref.getUntil(); argMaxLArrays(       ref.getData(), from,until,      naturalOrder(), x -> argMaxL(x,from,until) ); }
  @Property void argMaxLArraysWithRangeDouble             ( @ForAll("arraysWithRangeDouble") WithRange<double[]> ref                                            ) { int from=ref.getFrom(), until=ref.getUntil(); argMaxLArrays(       ref.getData(), from,until,      naturalOrder(), x -> argMaxL(x,from,until) ); }
  @Property void argMaxLArraysWithRangeObjByte            ( @ForAll("arraysWithRangeByte"  ) WithRange<  byte[]> ref                                            ) { int from=ref.getFrom(), until=ref.getUntil(); argMaxLArrays( boxed(ref.getData()),from,until,      naturalOrder(), x -> argMaxL(x,from,until) ); }
  @Property void argMaxLArraysWithRangeObjShort           ( @ForAll("arraysWithRangeShort" ) WithRange< short[]> ref                                            ) { int from=ref.getFrom(), until=ref.getUntil(); argMaxLArrays( boxed(ref.getData()),from,until,      naturalOrder(), x -> argMaxL(x,from,until) ); }
  @Property void argMaxLArraysWithRangeObjInt             ( @ForAll("arraysWithRangeInt"   ) WithRange<   int[]> ref                                            ) { int from=ref.getFrom(), until=ref.getUntil(); argMaxLArrays( boxed(ref.getData()),from,until,      naturalOrder(), x -> argMaxL(x,from,until) ); }
  @Property void argMaxLArraysWithRangeObjLong            ( @ForAll("arraysWithRangeLong"  ) WithRange<  long[]> ref                                            ) { int from=ref.getFrom(), until=ref.getUntil(); argMaxLArrays( boxed(ref.getData()),from,until,      naturalOrder(), x -> argMaxL(x,from,until) ); }
  @Property void argMaxLArraysWithRangeObjChar            ( @ForAll("arraysWithRangeChar"  ) WithRange<  char[]> ref                                            ) { int from=ref.getFrom(), until=ref.getUntil(); argMaxLArrays( boxed(ref.getData()),from,until,      naturalOrder(), x -> argMaxL(x,from,until) ); }
  @Property void argMaxLArraysWithRangeObjFloat           ( @ForAll("arraysWithRangeFloat" ) WithRange< float[]> ref                                            ) { int from=ref.getFrom(), until=ref.getUntil(); argMaxLArrays( boxed(ref.getData()),from,until,      naturalOrder(), x -> argMaxL(x,from,until) ); }
  @Property void argMaxLArraysWithRangeObjDouble          ( @ForAll("arraysWithRangeDouble") WithRange<double[]> ref                                            ) { int from=ref.getFrom(), until=ref.getUntil(); argMaxLArrays( boxed(ref.getData()),from,until,      naturalOrder(), x -> argMaxL(x,from,until) ); }
  @Property void argMaxLArraysComparatorByte              ( @ForAll("arraysByte"           )             byte[]  ref, @ForAll Comparator<? super Byte     > cmp ) {                                               argMaxLArrays(       ref          ,    0,ref.length, cmp,            x -> argMaxL(x           ,cmp::compare) ); }
  @Property void argMaxLArraysComparatorShort             ( @ForAll("arraysShort"          )            short[]  ref, @ForAll Comparator<? super Short    > cmp ) {                                               argMaxLArrays(       ref          ,    0,ref.length, cmp,            x -> argMaxL(x           ,cmp::compare) ); }
  @Property void argMaxLArraysComparatorInt               ( @ForAll("arraysInt"            )              int[]  ref, @ForAll Comparator<? super Integer  > cmp ) {                                               argMaxLArrays(       ref          ,    0,ref.length, cmp,            x -> argMaxL(x           ,cmp::compare) ); }
  @Property void argMaxLArraysComparatorLong              ( @ForAll("arraysLong"           )             long[]  ref, @ForAll Comparator<? super Long     > cmp ) {                                               argMaxLArrays(       ref          ,    0,ref.length, cmp,            x -> argMaxL(x           ,cmp::compare) ); }
  @Property void argMaxLArraysComparatorChar              ( @ForAll("arraysChar"           )             char[]  ref, @ForAll Comparator<? super Character> cmp ) {                                               argMaxLArrays(       ref          ,    0,ref.length, cmp,            x -> argMaxL(x           ,cmp::compare) ); }
  @Property void argMaxLArraysComparatorFloat             ( @ForAll("arraysFloat"          )            float[]  ref, @ForAll Comparator<? super Float    > cmp ) {                                               argMaxLArrays(       ref          ,    0,ref.length, cmp,            x -> argMaxL(x           ,cmp::compare) ); }
  @Property void argMaxLArraysComparatorDouble            ( @ForAll("arraysDouble"         )           double[]  ref, @ForAll Comparator<? super Double   > cmp ) {                                               argMaxLArrays(       ref          ,    0,ref.length, cmp,            x -> argMaxL(x           ,cmp::compare) ); }
  @Property void argMaxLArraysComparatorObjByte           ( @ForAll("arraysByte"           )             byte[]  ref, @ForAll Comparator<? super Byte     > cmp ) {                                               argMaxLArrays( boxed(ref)         ,    0,ref.length, cmp,            x -> argMaxL(x           ,cmp) ); }
  @Property void argMaxLArraysComparatorObjShort          ( @ForAll("arraysShort"          )            short[]  ref, @ForAll Comparator<? super Short    > cmp ) {                                               argMaxLArrays( boxed(ref)         ,    0,ref.length, cmp,            x -> argMaxL(x           ,cmp) ); }
  @Property void argMaxLArraysComparatorObjInt            ( @ForAll("arraysInt"            )              int[]  ref, @ForAll Comparator<? super Integer  > cmp ) {                                               argMaxLArrays( boxed(ref)         ,    0,ref.length, cmp,            x -> argMaxL(x           ,cmp) ); }
  @Property void argMaxLArraysComparatorObjLong           ( @ForAll("arraysLong"           )             long[]  ref, @ForAll Comparator<? super Long     > cmp ) {                                               argMaxLArrays( boxed(ref)         ,    0,ref.length, cmp,            x -> argMaxL(x           ,cmp) ); }
  @Property void argMaxLArraysComparatorObjChar           ( @ForAll("arraysChar"           )             char[]  ref, @ForAll Comparator<? super Character> cmp ) {                                               argMaxLArrays( boxed(ref)         ,    0,ref.length, cmp,            x -> argMaxL(x           ,cmp) ); }
  @Property void argMaxLArraysComparatorObjFloat          ( @ForAll("arraysFloat"          )            float[]  ref, @ForAll Comparator<? super Float    > cmp ) {                                               argMaxLArrays( boxed(ref)         ,    0,ref.length, cmp,            x -> argMaxL(x           ,cmp) ); }
  @Property void argMaxLArraysComparatorObjDouble         ( @ForAll("arraysDouble"         )           double[]  ref, @ForAll Comparator<? super Double   > cmp ) {                                               argMaxLArrays( boxed(ref)         ,    0,ref.length, cmp,            x -> argMaxL(x           ,cmp) ); }
  @Property void argMaxLArraysWithRangeComparatorByte     ( @ForAll("arraysWithRangeByte"  ) WithRange<  byte[]> ref, @ForAll Comparator<? super Byte     > cmp ) { int from=ref.getFrom(), until=ref.getUntil(); argMaxLArrays(       ref.getData(), from,until,      cmp,            x -> argMaxL(x,from,until,cmp::compare) ); }
  @Property void argMaxLArraysWithRangeComparatorShort    ( @ForAll("arraysWithRangeShort" ) WithRange< short[]> ref, @ForAll Comparator<? super Short    > cmp ) { int from=ref.getFrom(), until=ref.getUntil(); argMaxLArrays(       ref.getData(), from,until,      cmp,            x -> argMaxL(x,from,until,cmp::compare) ); }
  @Property void argMaxLArraysWithRangeComparatorInt      ( @ForAll("arraysWithRangeInt"   ) WithRange<   int[]> ref, @ForAll Comparator<? super Integer  > cmp ) { int from=ref.getFrom(), until=ref.getUntil(); argMaxLArrays(       ref.getData(), from,until,      cmp,            x -> argMaxL(x,from,until,cmp::compare) ); }
  @Property void argMaxLArraysWithRangeComparatorLong     ( @ForAll("arraysWithRangeLong"  ) WithRange<  long[]> ref, @ForAll Comparator<? super Long     > cmp ) { int from=ref.getFrom(), until=ref.getUntil(); argMaxLArrays(       ref.getData(), from,until,      cmp,            x -> argMaxL(x,from,until,cmp::compare) ); }
  @Property void argMaxLArraysWithRangeComparatorChar     ( @ForAll("arraysWithRangeChar"  ) WithRange<  char[]> ref, @ForAll Comparator<? super Character> cmp ) { int from=ref.getFrom(), until=ref.getUntil(); argMaxLArrays(       ref.getData(), from,until,      cmp,            x -> argMaxL(x,from,until,cmp::compare) ); }
  @Property void argMaxLArraysWithRangeComparatorFloat    ( @ForAll("arraysWithRangeFloat" ) WithRange< float[]> ref, @ForAll Comparator<? super Float    > cmp ) { int from=ref.getFrom(), until=ref.getUntil(); argMaxLArrays(       ref.getData(), from,until,      cmp,            x -> argMaxL(x,from,until,cmp::compare) ); }
  @Property void argMaxLArraysWithRangeComparatorDouble   ( @ForAll("arraysWithRangeDouble") WithRange<double[]> ref, @ForAll Comparator<? super Double   > cmp ) { int from=ref.getFrom(), until=ref.getUntil(); argMaxLArrays(       ref.getData(), from,until,      cmp,            x -> argMaxL(x,from,until,cmp::compare) ); }
  @Property void argMaxLArraysWithRangeComparatorObjByte  ( @ForAll("arraysWithRangeByte"  ) WithRange<  byte[]> ref, @ForAll Comparator<? super Byte     > cmp ) { int from=ref.getFrom(), until=ref.getUntil(); argMaxLArrays( boxed(ref.getData()),from,until,      cmp,            x -> argMaxL(x,from,until,cmp) ); }
  @Property void argMaxLArraysWithRangeComparatorObjShort ( @ForAll("arraysWithRangeShort" ) WithRange< short[]> ref, @ForAll Comparator<? super Short    > cmp ) { int from=ref.getFrom(), until=ref.getUntil(); argMaxLArrays( boxed(ref.getData()),from,until,      cmp,            x -> argMaxL(x,from,until,cmp) ); }
  @Property void argMaxLArraysWithRangeComparatorObjInt   ( @ForAll("arraysWithRangeInt"   ) WithRange<   int[]> ref, @ForAll Comparator<? super Integer  > cmp ) { int from=ref.getFrom(), until=ref.getUntil(); argMaxLArrays( boxed(ref.getData()),from,until,      cmp,            x -> argMaxL(x,from,until,cmp) ); }
  @Property void argMaxLArraysWithRangeComparatorObjLong  ( @ForAll("arraysWithRangeLong"  ) WithRange<  long[]> ref, @ForAll Comparator<? super Long     > cmp ) { int from=ref.getFrom(), until=ref.getUntil(); argMaxLArrays( boxed(ref.getData()),from,until,      cmp,            x -> argMaxL(x,from,until,cmp) ); }
  @Property void argMaxLArraysWithRangeComparatorObjChar  ( @ForAll("arraysWithRangeChar"  ) WithRange<  char[]> ref, @ForAll Comparator<? super Character> cmp ) { int from=ref.getFrom(), until=ref.getUntil(); argMaxLArrays( boxed(ref.getData()),from,until,      cmp,            x -> argMaxL(x,from,until,cmp) ); }
  @Property void argMaxLArraysWithRangeComparatorObjFloat ( @ForAll("arraysWithRangeFloat" ) WithRange< float[]> ref, @ForAll Comparator<? super Float    > cmp ) { int from=ref.getFrom(), until=ref.getUntil(); argMaxLArrays( boxed(ref.getData()),from,until,      cmp,            x -> argMaxL(x,from,until,cmp) ); }
  @Property void argMaxLArraysWithRangeComparatorObjDouble( @ForAll("arraysWithRangeDouble") WithRange<double[]> ref, @ForAll Comparator<? super Double   > cmp ) { int from=ref.getFrom(), until=ref.getUntil(); argMaxLArrays( boxed(ref.getData()),from,until,      cmp,            x -> argMaxL(x,from,until,cmp) ); }



  @SuppressWarnings({"rawtypes", "unchecked", "SuspiciousSystemArraycopy"})
  private <A> void argMaxRArrays( A array, int from, int until, Comparator cmp, ToIntFunction<? super A> argMax ) {
    assertThat(from).isGreaterThanOrEqualTo(0);
    assertThat(from).isLessThanOrEqualTo(until);

    int len = Array.getLength(array);

    A tst = (A) Array.newInstance(array.getClass().getComponentType(), len);
    arraycopy(array,0, tst,0, len);

    if( from >= until )
      assertThatThrownBy( () -> argMax.applyAsInt(tst) ).isInstanceOf(IllegalArgumentException.class);
    else {
      int        iMin = argMax.applyAsInt(tst);
      assertThat(iMin).isBetween(from,until-1);
      assertThat(tst).isEqualTo(array);

      Object min = Array.get(array,iMin);

      for( int i=from; i < until; i++ ) {
        int c = cmp.compare(min, Array.get(array,i));
        assertThat(c).isGreaterThan( i > iMin ? 0 : -1 );
      }
    }
  }
  @Property void argMaxRArraysByte                        ( @ForAll("arraysByte"           )             byte[]  ref                                            ) {                                               argMaxRArrays(       ref          ,    0,ref.length, naturalOrder(), ArgMax::argMaxR); }
  @Property void argMaxRArraysShort                       ( @ForAll("arraysShort"          )            short[]  ref                                            ) {                                               argMaxRArrays(       ref          ,    0,ref.length, naturalOrder(), ArgMax::argMaxR); }
  @Property void argMaxRArraysInt                         ( @ForAll("arraysInt"            )              int[]  ref                                            ) {                                               argMaxRArrays(       ref          ,    0,ref.length, naturalOrder(), ArgMax::argMaxR); }
  @Property void argMaxRArraysLong                        ( @ForAll("arraysLong"           )             long[]  ref                                            ) {                                               argMaxRArrays(       ref          ,    0,ref.length, naturalOrder(), ArgMax::argMaxR); }
  @Property void argMaxRArraysChar                        ( @ForAll("arraysChar"           )             char[]  ref                                            ) {                                               argMaxRArrays(       ref          ,    0,ref.length, naturalOrder(), ArgMax::argMaxR); }
  @Property void argMaxRArraysFloat                       ( @ForAll("arraysFloat"          )            float[]  ref                                            ) {                                               argMaxRArrays(       ref          ,    0,ref.length, naturalOrder(), ArgMax::argMaxR); }
  @Property void argMaxRArraysDouble                      ( @ForAll("arraysDouble"         )           double[]  ref                                            ) {                                               argMaxRArrays(       ref          ,    0,ref.length, naturalOrder(), ArgMax::argMaxR); }
  @Property void argMaxRArraysObjByte                     ( @ForAll("arraysByte"           )             byte[]  ref                                            ) {                                               argMaxRArrays( boxed(ref)         ,    0,ref.length, naturalOrder(), ArgMax::argMaxR); }
  @Property void argMaxRArraysObjShort                    ( @ForAll("arraysShort"          )            short[]  ref                                            ) {                                               argMaxRArrays( boxed(ref)         ,    0,ref.length, naturalOrder(), ArgMax::argMaxR); }
  @Property void argMaxRArraysObjInt                      ( @ForAll("arraysInt"            )              int[]  ref                                            ) {                                               argMaxRArrays( boxed(ref)         ,    0,ref.length, naturalOrder(), ArgMax::argMaxR); }
  @Property void argMaxRArraysObjLong                     ( @ForAll("arraysLong"           )             long[]  ref                                            ) {                                               argMaxRArrays( boxed(ref)         ,    0,ref.length, naturalOrder(), ArgMax::argMaxR); }
  @Property void argMaxRArraysObjChar                     ( @ForAll("arraysChar"           )             char[]  ref                                            ) {                                               argMaxRArrays( boxed(ref)         ,    0,ref.length, naturalOrder(), ArgMax::argMaxR); }
  @Property void argMaxRArraysObjFloat                    ( @ForAll("arraysFloat"          )            float[]  ref                                            ) {                                               argMaxRArrays( boxed(ref)         ,    0,ref.length, naturalOrder(), ArgMax::argMaxR); }
  @Property void argMaxRArraysObjDouble                   ( @ForAll("arraysDouble"         )           double[]  ref                                            ) {                                               argMaxRArrays( boxed(ref)         ,    0,ref.length, naturalOrder(), ArgMax::argMaxR); }
  @Property void argMaxRArraysWithRangeByte               ( @ForAll("arraysWithRangeByte"  ) WithRange<  byte[]> ref                                            ) { int from=ref.getFrom(), until=ref.getUntil(); argMaxRArrays(       ref.getData(), from,until,      naturalOrder(), x -> argMaxR(x,from,until) ); }
  @Property void argMaxRArraysWithRangeShort              ( @ForAll("arraysWithRangeShort" ) WithRange< short[]> ref                                            ) { int from=ref.getFrom(), until=ref.getUntil(); argMaxRArrays(       ref.getData(), from,until,      naturalOrder(), x -> argMaxR(x,from,until) ); }
  @Property void argMaxRArraysWithRangeInt                ( @ForAll("arraysWithRangeInt"   ) WithRange<   int[]> ref                                            ) { int from=ref.getFrom(), until=ref.getUntil(); argMaxRArrays(       ref.getData(), from,until,      naturalOrder(), x -> argMaxR(x,from,until) ); }
  @Property void argMaxRArraysWithRangeLong               ( @ForAll("arraysWithRangeLong"  ) WithRange<  long[]> ref                                            ) { int from=ref.getFrom(), until=ref.getUntil(); argMaxRArrays(       ref.getData(), from,until,      naturalOrder(), x -> argMaxR(x,from,until) ); }
  @Property void argMaxRArraysWithRangeChar               ( @ForAll("arraysWithRangeChar"  ) WithRange<  char[]> ref                                            ) { int from=ref.getFrom(), until=ref.getUntil(); argMaxRArrays(       ref.getData(), from,until,      naturalOrder(), x -> argMaxR(x,from,until) ); }
  @Property void argMaxRArraysWithRangeFloat              ( @ForAll("arraysWithRangeFloat" ) WithRange< float[]> ref                                            ) { int from=ref.getFrom(), until=ref.getUntil(); argMaxRArrays(       ref.getData(), from,until,      naturalOrder(), x -> argMaxR(x,from,until) ); }
  @Property void argMaxRArraysWithRangeDouble             ( @ForAll("arraysWithRangeDouble") WithRange<double[]> ref                                            ) { int from=ref.getFrom(), until=ref.getUntil(); argMaxRArrays(       ref.getData(), from,until,      naturalOrder(), x -> argMaxR(x,from,until) ); }
  @Property void argMaxRArraysWithRangeObjByte            ( @ForAll("arraysWithRangeByte"  ) WithRange<  byte[]> ref                                            ) { int from=ref.getFrom(), until=ref.getUntil(); argMaxRArrays( boxed(ref.getData()),from,until,      naturalOrder(), x -> argMaxR(x,from,until) ); }
  @Property void argMaxRArraysWithRangeObjShort           ( @ForAll("arraysWithRangeShort" ) WithRange< short[]> ref                                            ) { int from=ref.getFrom(), until=ref.getUntil(); argMaxRArrays( boxed(ref.getData()),from,until,      naturalOrder(), x -> argMaxR(x,from,until) ); }
  @Property void argMaxRArraysWithRangeObjInt             ( @ForAll("arraysWithRangeInt"   ) WithRange<   int[]> ref                                            ) { int from=ref.getFrom(), until=ref.getUntil(); argMaxRArrays( boxed(ref.getData()),from,until,      naturalOrder(), x -> argMaxR(x,from,until) ); }
  @Property void argMaxRArraysWithRangeObjLong            ( @ForAll("arraysWithRangeLong"  ) WithRange<  long[]> ref                                            ) { int from=ref.getFrom(), until=ref.getUntil(); argMaxRArrays( boxed(ref.getData()),from,until,      naturalOrder(), x -> argMaxR(x,from,until) ); }
  @Property void argMaxRArraysWithRangeObjChar            ( @ForAll("arraysWithRangeChar"  ) WithRange<  char[]> ref                                            ) { int from=ref.getFrom(), until=ref.getUntil(); argMaxRArrays( boxed(ref.getData()),from,until,      naturalOrder(), x -> argMaxR(x,from,until) ); }
  @Property void argMaxRArraysWithRangeObjFloat           ( @ForAll("arraysWithRangeFloat" ) WithRange< float[]> ref                                            ) { int from=ref.getFrom(), until=ref.getUntil(); argMaxRArrays( boxed(ref.getData()),from,until,      naturalOrder(), x -> argMaxR(x,from,until) ); }
  @Property void argMaxRArraysWithRangeObjDouble          ( @ForAll("arraysWithRangeDouble") WithRange<double[]> ref                                            ) { int from=ref.getFrom(), until=ref.getUntil(); argMaxRArrays( boxed(ref.getData()),from,until,      naturalOrder(), x -> argMaxR(x,from,until) ); }
  @Property void argMaxRArraysComparatorByte              ( @ForAll("arraysByte"           )             byte[]  ref, @ForAll Comparator<? super Byte     > cmp ) {                                               argMaxRArrays(       ref          ,    0,ref.length, cmp,            x -> argMaxR(x           ,cmp::compare) ); }
  @Property void argMaxRArraysComparatorShort             ( @ForAll("arraysShort"          )            short[]  ref, @ForAll Comparator<? super Short    > cmp ) {                                               argMaxRArrays(       ref          ,    0,ref.length, cmp,            x -> argMaxR(x           ,cmp::compare) ); }
  @Property void argMaxRArraysComparatorInt               ( @ForAll("arraysInt"            )              int[]  ref, @ForAll Comparator<? super Integer  > cmp ) {                                               argMaxRArrays(       ref          ,    0,ref.length, cmp,            x -> argMaxR(x           ,cmp::compare) ); }
  @Property void argMaxRArraysComparatorLong              ( @ForAll("arraysLong"           )             long[]  ref, @ForAll Comparator<? super Long     > cmp ) {                                               argMaxRArrays(       ref          ,    0,ref.length, cmp,            x -> argMaxR(x           ,cmp::compare) ); }
  @Property void argMaxRArraysComparatorChar              ( @ForAll("arraysChar"           )             char[]  ref, @ForAll Comparator<? super Character> cmp ) {                                               argMaxRArrays(       ref          ,    0,ref.length, cmp,            x -> argMaxR(x           ,cmp::compare) ); }
  @Property void argMaxRArraysComparatorFloat             ( @ForAll("arraysFloat"          )            float[]  ref, @ForAll Comparator<? super Float    > cmp ) {                                               argMaxRArrays(       ref          ,    0,ref.length, cmp,            x -> argMaxR(x           ,cmp::compare) ); }
  @Property void argMaxRArraysComparatorDouble            ( @ForAll("arraysDouble"         )           double[]  ref, @ForAll Comparator<? super Double   > cmp ) {                                               argMaxRArrays(       ref          ,    0,ref.length, cmp,            x -> argMaxR(x           ,cmp::compare) ); }
  @Property void argMaxRArraysComparatorObjByte           ( @ForAll("arraysByte"           )             byte[]  ref, @ForAll Comparator<? super Byte     > cmp ) {                                               argMaxRArrays( boxed(ref)         ,    0,ref.length, cmp,            x -> argMaxR(x           ,cmp) ); }
  @Property void argMaxRArraysComparatorObjShort          ( @ForAll("arraysShort"          )            short[]  ref, @ForAll Comparator<? super Short    > cmp ) {                                               argMaxRArrays( boxed(ref)         ,    0,ref.length, cmp,            x -> argMaxR(x           ,cmp) ); }
  @Property void argMaxRArraysComparatorObjInt            ( @ForAll("arraysInt"            )              int[]  ref, @ForAll Comparator<? super Integer  > cmp ) {                                               argMaxRArrays( boxed(ref)         ,    0,ref.length, cmp,            x -> argMaxR(x           ,cmp) ); }
  @Property void argMaxRArraysComparatorObjLong           ( @ForAll("arraysLong"           )             long[]  ref, @ForAll Comparator<? super Long     > cmp ) {                                               argMaxRArrays( boxed(ref)         ,    0,ref.length, cmp,            x -> argMaxR(x           ,cmp) ); }
  @Property void argMaxRArraysComparatorObjChar           ( @ForAll("arraysChar"           )             char[]  ref, @ForAll Comparator<? super Character> cmp ) {                                               argMaxRArrays( boxed(ref)         ,    0,ref.length, cmp,            x -> argMaxR(x           ,cmp) ); }
  @Property void argMaxRArraysComparatorObjFloat          ( @ForAll("arraysFloat"          )            float[]  ref, @ForAll Comparator<? super Float    > cmp ) {                                               argMaxRArrays( boxed(ref)         ,    0,ref.length, cmp,            x -> argMaxR(x           ,cmp) ); }
  @Property void argMaxRArraysComparatorObjDouble         ( @ForAll("arraysDouble"         )           double[]  ref, @ForAll Comparator<? super Double   > cmp ) {                                               argMaxRArrays( boxed(ref)         ,    0,ref.length, cmp,            x -> argMaxR(x           ,cmp) ); }
  @Property void argMaxRArraysWithRangeComparatorByte     ( @ForAll("arraysWithRangeByte"  ) WithRange<  byte[]> ref, @ForAll Comparator<? super Byte     > cmp ) { int from=ref.getFrom(), until=ref.getUntil(); argMaxRArrays(       ref.getData(), from,until,      cmp,            x -> argMaxR(x,from,until,cmp::compare) ); }
  @Property void argMaxRArraysWithRangeComparatorShort    ( @ForAll("arraysWithRangeShort" ) WithRange< short[]> ref, @ForAll Comparator<? super Short    > cmp ) { int from=ref.getFrom(), until=ref.getUntil(); argMaxRArrays(       ref.getData(), from,until,      cmp,            x -> argMaxR(x,from,until,cmp::compare) ); }
  @Property void argMaxRArraysWithRangeComparatorInt      ( @ForAll("arraysWithRangeInt"   ) WithRange<   int[]> ref, @ForAll Comparator<? super Integer  > cmp ) { int from=ref.getFrom(), until=ref.getUntil(); argMaxRArrays(       ref.getData(), from,until,      cmp,            x -> argMaxR(x,from,until,cmp::compare) ); }
  @Property void argMaxRArraysWithRangeComparatorLong     ( @ForAll("arraysWithRangeLong"  ) WithRange<  long[]> ref, @ForAll Comparator<? super Long     > cmp ) { int from=ref.getFrom(), until=ref.getUntil(); argMaxRArrays(       ref.getData(), from,until,      cmp,            x -> argMaxR(x,from,until,cmp::compare) ); }
  @Property void argMaxRArraysWithRangeComparatorChar     ( @ForAll("arraysWithRangeChar"  ) WithRange<  char[]> ref, @ForAll Comparator<? super Character> cmp ) { int from=ref.getFrom(), until=ref.getUntil(); argMaxRArrays(       ref.getData(), from,until,      cmp,            x -> argMaxR(x,from,until,cmp::compare) ); }
  @Property void argMaxRArraysWithRangeComparatorFloat    ( @ForAll("arraysWithRangeFloat" ) WithRange< float[]> ref, @ForAll Comparator<? super Float    > cmp ) { int from=ref.getFrom(), until=ref.getUntil(); argMaxRArrays(       ref.getData(), from,until,      cmp,            x -> argMaxR(x,from,until,cmp::compare) ); }
  @Property void argMaxRArraysWithRangeComparatorDouble   ( @ForAll("arraysWithRangeDouble") WithRange<double[]> ref, @ForAll Comparator<? super Double   > cmp ) { int from=ref.getFrom(), until=ref.getUntil(); argMaxRArrays(       ref.getData(), from,until,      cmp,            x -> argMaxR(x,from,until,cmp::compare) ); }
  @Property void argMaxRArraysWithRangeComparatorObjByte  ( @ForAll("arraysWithRangeByte"  ) WithRange<  byte[]> ref, @ForAll Comparator<? super Byte     > cmp ) { int from=ref.getFrom(), until=ref.getUntil(); argMaxRArrays( boxed(ref.getData()),from,until,      cmp,            x -> argMaxR(x,from,until,cmp) ); }
  @Property void argMaxRArraysWithRangeComparatorObjShort ( @ForAll("arraysWithRangeShort" ) WithRange< short[]> ref, @ForAll Comparator<? super Short    > cmp ) { int from=ref.getFrom(), until=ref.getUntil(); argMaxRArrays( boxed(ref.getData()),from,until,      cmp,            x -> argMaxR(x,from,until,cmp) ); }
  @Property void argMaxRArraysWithRangeComparatorObjInt   ( @ForAll("arraysWithRangeInt"   ) WithRange<   int[]> ref, @ForAll Comparator<? super Integer  > cmp ) { int from=ref.getFrom(), until=ref.getUntil(); argMaxRArrays( boxed(ref.getData()),from,until,      cmp,            x -> argMaxR(x,from,until,cmp) ); }
  @Property void argMaxRArraysWithRangeComparatorObjLong  ( @ForAll("arraysWithRangeLong"  ) WithRange<  long[]> ref, @ForAll Comparator<? super Long     > cmp ) { int from=ref.getFrom(), until=ref.getUntil(); argMaxRArrays( boxed(ref.getData()),from,until,      cmp,            x -> argMaxR(x,from,until,cmp) ); }
  @Property void argMaxRArraysWithRangeComparatorObjChar  ( @ForAll("arraysWithRangeChar"  ) WithRange<  char[]> ref, @ForAll Comparator<? super Character> cmp ) { int from=ref.getFrom(), until=ref.getUntil(); argMaxRArrays( boxed(ref.getData()),from,until,      cmp,            x -> argMaxR(x,from,until,cmp) ); }
  @Property void argMaxRArraysWithRangeComparatorObjFloat ( @ForAll("arraysWithRangeFloat" ) WithRange< float[]> ref, @ForAll Comparator<? super Float    > cmp ) { int from=ref.getFrom(), until=ref.getUntil(); argMaxRArrays( boxed(ref.getData()),from,until,      cmp,            x -> argMaxR(x,from,until,cmp) ); }
  @Property void argMaxRArraysWithRangeComparatorObjDouble( @ForAll("arraysWithRangeDouble") WithRange<double[]> ref, @ForAll Comparator<? super Double   > cmp ) { int from=ref.getFrom(), until=ref.getUntil(); argMaxRArrays( boxed(ref.getData()),from,until,      cmp,            x -> argMaxR(x,from,until,cmp) ); }
}
