package com.github.jaaa.compare;

import com.github.jaaa.ArrayProviderTemplate;
import com.github.jaaa.WithRange;
import net.jqwik.api.ForAll;
import net.jqwik.api.Property;

import java.lang.reflect.Array;
import java.util.Comparator;
import java.util.function.ToIntFunction;

import static com.github.jaaa.compare.ArgMin.argMinL;
import static com.github.jaaa.compare.ArgMin.argMinR;
import static com.github.jaaa.Boxing.boxed;
import static java.lang.System.arraycopy;
import static java.util.Comparator.naturalOrder;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;


public class ArgMinTest implements ArrayProviderTemplate
{
  @Override public int maxArraySize      () { return 16*1024; }
  @Override public int maxArraySizeString() { return  2*1024; }

  @SuppressWarnings({"rawtypes", "unchecked", "SuspiciousSystemArraycopy"})
  private <A> void argMinLArrays( A array, int from, int until, Comparator cmp, ToIntFunction<? super A> argMin ) {
    assertThat(from).isGreaterThanOrEqualTo(0);
    assertThat(from).isLessThanOrEqualTo(until);

    int len = Array.getLength(array);

    A tst = (A) Array.newInstance(array.getClass().getComponentType(), len);
    arraycopy(array,0, tst,0, len);

    if( from >= until )
      assertThatThrownBy( () -> argMin.applyAsInt(tst) ).isInstanceOf(IllegalArgumentException.class);
    else {
      int        iMin = argMin.applyAsInt(tst);
      assertThat(iMin).isBetween(from,until-1);
      assertThat(tst).isEqualTo(array);

      var min = Array.get(array,iMin);

      for( int i=from; i < until; i++ ) {
        var c = cmp.compare(min, Array.get(array,i));
        assertThat(c).isLessThan( i < iMin ? 0 : 1 );
      }
    }
  }
  @Property void argMinLArraysByte                        ( @ForAll("arraysByte"           )             byte[]  ref                                            ) {                                               argMinLArrays(       ref          ,    0,ref.length, naturalOrder(), ArgMin::argMinL); }
  @Property void argMinLArraysShort                       ( @ForAll("arraysShort"          )            short[]  ref                                            ) {                                               argMinLArrays(       ref          ,    0,ref.length, naturalOrder(), ArgMin::argMinL); }
  @Property void argMinLArraysInt                         ( @ForAll("arraysInt"            )              int[]  ref                                            ) {                                               argMinLArrays(       ref          ,    0,ref.length, naturalOrder(), ArgMin::argMinL); }
  @Property void argMinLArraysLong                        ( @ForAll("arraysLong"           )             long[]  ref                                            ) {                                               argMinLArrays(       ref          ,    0,ref.length, naturalOrder(), ArgMin::argMinL); }
  @Property void argMinLArraysChar                        ( @ForAll("arraysChar"           )             char[]  ref                                            ) {                                               argMinLArrays(       ref          ,    0,ref.length, naturalOrder(), ArgMin::argMinL); }
  @Property void argMinLArraysFloat                       ( @ForAll("arraysFloat"          )            float[]  ref                                            ) {                                               argMinLArrays(       ref          ,    0,ref.length, naturalOrder(), ArgMin::argMinL); }
  @Property void argMinLArraysDouble                      ( @ForAll("arraysDouble"         )           double[]  ref                                            ) {                                               argMinLArrays(       ref          ,    0,ref.length, naturalOrder(), ArgMin::argMinL); }
  @Property void argMinLArraysObjByte                     ( @ForAll("arraysByte"           )             byte[]  ref                                            ) {                                               argMinLArrays( boxed(ref)         ,    0,ref.length, naturalOrder(), ArgMin::argMinL); }
  @Property void argMinLArraysObjShort                    ( @ForAll("arraysShort"          )            short[]  ref                                            ) {                                               argMinLArrays( boxed(ref)         ,    0,ref.length, naturalOrder(), ArgMin::argMinL); }
  @Property void argMinLArraysObjInt                      ( @ForAll("arraysInt"            )              int[]  ref                                            ) {                                               argMinLArrays( boxed(ref)         ,    0,ref.length, naturalOrder(), ArgMin::argMinL); }
  @Property void argMinLArraysObjLong                     ( @ForAll("arraysLong"           )             long[]  ref                                            ) {                                               argMinLArrays( boxed(ref)         ,    0,ref.length, naturalOrder(), ArgMin::argMinL); }
  @Property void argMinLArraysObjChar                     ( @ForAll("arraysChar"           )             char[]  ref                                            ) {                                               argMinLArrays( boxed(ref)         ,    0,ref.length, naturalOrder(), ArgMin::argMinL); }
  @Property void argMinLArraysObjFloat                    ( @ForAll("arraysFloat"          )            float[]  ref                                            ) {                                               argMinLArrays( boxed(ref)         ,    0,ref.length, naturalOrder(), ArgMin::argMinL); }
  @Property void argMinLArraysObjDouble                   ( @ForAll("arraysDouble"         )           double[]  ref                                            ) {                                               argMinLArrays( boxed(ref)         ,    0,ref.length, naturalOrder(), ArgMin::argMinL); }
  @Property void argMinLArraysWithRangeByte               ( @ForAll("arraysWithRangeByte"  ) WithRange<  byte[]> ref                                            ) { int from=ref.getFrom(), until=ref.getUntil(); argMinLArrays(       ref.getData(), from,until,      naturalOrder(), x -> argMinL(x,from,until) ); }
  @Property void argMinLArraysWithRangeShort              ( @ForAll("arraysWithRangeShort" ) WithRange< short[]> ref                                            ) { int from=ref.getFrom(), until=ref.getUntil(); argMinLArrays(       ref.getData(), from,until,      naturalOrder(), x -> argMinL(x,from,until) ); }
  @Property void argMinLArraysWithRangeInt                ( @ForAll("arraysWithRangeInt"   ) WithRange<   int[]> ref                                            ) { int from=ref.getFrom(), until=ref.getUntil(); argMinLArrays(       ref.getData(), from,until,      naturalOrder(), x -> argMinL(x,from,until) ); }
  @Property void argMinLArraysWithRangeLong               ( @ForAll("arraysWithRangeLong"  ) WithRange<  long[]> ref                                            ) { int from=ref.getFrom(), until=ref.getUntil(); argMinLArrays(       ref.getData(), from,until,      naturalOrder(), x -> argMinL(x,from,until) ); }
  @Property void argMinLArraysWithRangeChar               ( @ForAll("arraysWithRangeChar"  ) WithRange<  char[]> ref                                            ) { int from=ref.getFrom(), until=ref.getUntil(); argMinLArrays(       ref.getData(), from,until,      naturalOrder(), x -> argMinL(x,from,until) ); }
  @Property void argMinLArraysWithRangeFloat              ( @ForAll("arraysWithRangeFloat" ) WithRange< float[]> ref                                            ) { int from=ref.getFrom(), until=ref.getUntil(); argMinLArrays(       ref.getData(), from,until,      naturalOrder(), x -> argMinL(x,from,until) ); }
  @Property void argMinLArraysWithRangeDouble             ( @ForAll("arraysWithRangeDouble") WithRange<double[]> ref                                            ) { int from=ref.getFrom(), until=ref.getUntil(); argMinLArrays(       ref.getData(), from,until,      naturalOrder(), x -> argMinL(x,from,until) ); }
  @Property void argMinLArraysWithRangeObjByte            ( @ForAll("arraysWithRangeByte"  ) WithRange<  byte[]> ref                                            ) { int from=ref.getFrom(), until=ref.getUntil(); argMinLArrays( boxed(ref.getData()),from,until,      naturalOrder(), x -> argMinL(x,from,until) ); }
  @Property void argMinLArraysWithRangeObjShort           ( @ForAll("arraysWithRangeShort" ) WithRange< short[]> ref                                            ) { int from=ref.getFrom(), until=ref.getUntil(); argMinLArrays( boxed(ref.getData()),from,until,      naturalOrder(), x -> argMinL(x,from,until) ); }
  @Property void argMinLArraysWithRangeObjInt             ( @ForAll("arraysWithRangeInt"   ) WithRange<   int[]> ref                                            ) { int from=ref.getFrom(), until=ref.getUntil(); argMinLArrays( boxed(ref.getData()),from,until,      naturalOrder(), x -> argMinL(x,from,until) ); }
  @Property void argMinLArraysWithRangeObjLong            ( @ForAll("arraysWithRangeLong"  ) WithRange<  long[]> ref                                            ) { int from=ref.getFrom(), until=ref.getUntil(); argMinLArrays( boxed(ref.getData()),from,until,      naturalOrder(), x -> argMinL(x,from,until) ); }
  @Property void argMinLArraysWithRangeObjChar            ( @ForAll("arraysWithRangeChar"  ) WithRange<  char[]> ref                                            ) { int from=ref.getFrom(), until=ref.getUntil(); argMinLArrays( boxed(ref.getData()),from,until,      naturalOrder(), x -> argMinL(x,from,until) ); }
  @Property void argMinLArraysWithRangeObjFloat           ( @ForAll("arraysWithRangeFloat" ) WithRange< float[]> ref                                            ) { int from=ref.getFrom(), until=ref.getUntil(); argMinLArrays( boxed(ref.getData()),from,until,      naturalOrder(), x -> argMinL(x,from,until) ); }
  @Property void argMinLArraysWithRangeObjDouble          ( @ForAll("arraysWithRangeDouble") WithRange<double[]> ref                                            ) { int from=ref.getFrom(), until=ref.getUntil(); argMinLArrays( boxed(ref.getData()),from,until,      naturalOrder(), x -> argMinL(x,from,until) ); }
  @Property void argMinLArraysComparatorByte              ( @ForAll("arraysByte"           )             byte[]  ref, @ForAll Comparator<? super Byte     > cmp ) {                                               argMinLArrays(       ref          ,    0,ref.length, cmp,            x -> argMinL(x           ,cmp::compare) ); }
  @Property void argMinLArraysComparatorShort             ( @ForAll("arraysShort"          )            short[]  ref, @ForAll Comparator<? super Short    > cmp ) {                                               argMinLArrays(       ref          ,    0,ref.length, cmp,            x -> argMinL(x           ,cmp::compare) ); }
  @Property void argMinLArraysComparatorInt               ( @ForAll("arraysInt"            )              int[]  ref, @ForAll Comparator<? super Integer  > cmp ) {                                               argMinLArrays(       ref          ,    0,ref.length, cmp,            x -> argMinL(x           ,cmp::compare) ); }
  @Property void argMinLArraysComparatorLong              ( @ForAll("arraysLong"           )             long[]  ref, @ForAll Comparator<? super Long     > cmp ) {                                               argMinLArrays(       ref          ,    0,ref.length, cmp,            x -> argMinL(x           ,cmp::compare) ); }
  @Property void argMinLArraysComparatorChar              ( @ForAll("arraysChar"           )             char[]  ref, @ForAll Comparator<? super Character> cmp ) {                                               argMinLArrays(       ref          ,    0,ref.length, cmp,            x -> argMinL(x           ,cmp::compare) ); }
  @Property void argMinLArraysComparatorFloat             ( @ForAll("arraysFloat"          )            float[]  ref, @ForAll Comparator<? super Float    > cmp ) {                                               argMinLArrays(       ref          ,    0,ref.length, cmp,            x -> argMinL(x           ,cmp::compare) ); }
  @Property void argMinLArraysComparatorDouble            ( @ForAll("arraysDouble"         )           double[]  ref, @ForAll Comparator<? super Double   > cmp ) {                                               argMinLArrays(       ref          ,    0,ref.length, cmp,            x -> argMinL(x           ,cmp::compare) ); }
  @Property void argMinLArraysComparatorObjByte           ( @ForAll("arraysByte"           )             byte[]  ref, @ForAll Comparator<? super Byte     > cmp ) {                                               argMinLArrays( boxed(ref)         ,    0,ref.length, cmp,            x -> argMinL(x           ,cmp) ); }
  @Property void argMinLArraysComparatorObjShort          ( @ForAll("arraysShort"          )            short[]  ref, @ForAll Comparator<? super Short    > cmp ) {                                               argMinLArrays( boxed(ref)         ,    0,ref.length, cmp,            x -> argMinL(x           ,cmp) ); }
  @Property void argMinLArraysComparatorObjInt            ( @ForAll("arraysInt"            )              int[]  ref, @ForAll Comparator<? super Integer  > cmp ) {                                               argMinLArrays( boxed(ref)         ,    0,ref.length, cmp,            x -> argMinL(x           ,cmp) ); }
  @Property void argMinLArraysComparatorObjLong           ( @ForAll("arraysLong"           )             long[]  ref, @ForAll Comparator<? super Long     > cmp ) {                                               argMinLArrays( boxed(ref)         ,    0,ref.length, cmp,            x -> argMinL(x           ,cmp) ); }
  @Property void argMinLArraysComparatorObjChar           ( @ForAll("arraysChar"           )             char[]  ref, @ForAll Comparator<? super Character> cmp ) {                                               argMinLArrays( boxed(ref)         ,    0,ref.length, cmp,            x -> argMinL(x           ,cmp) ); }
  @Property void argMinLArraysComparatorObjFloat          ( @ForAll("arraysFloat"          )            float[]  ref, @ForAll Comparator<? super Float    > cmp ) {                                               argMinLArrays( boxed(ref)         ,    0,ref.length, cmp,            x -> argMinL(x           ,cmp) ); }
  @Property void argMinLArraysComparatorObjDouble         ( @ForAll("arraysDouble"         )           double[]  ref, @ForAll Comparator<? super Double   > cmp ) {                                               argMinLArrays( boxed(ref)         ,    0,ref.length, cmp,            x -> argMinL(x           ,cmp) ); }
  @Property void argMinLArraysWithRangeComparatorByte     ( @ForAll("arraysWithRangeByte"  ) WithRange<  byte[]> ref, @ForAll Comparator<? super Byte     > cmp ) { int from=ref.getFrom(), until=ref.getUntil(); argMinLArrays(       ref.getData(), from,until,      cmp,            x -> argMinL(x,from,until,cmp::compare) ); }
  @Property void argMinLArraysWithRangeComparatorShort    ( @ForAll("arraysWithRangeShort" ) WithRange< short[]> ref, @ForAll Comparator<? super Short    > cmp ) { int from=ref.getFrom(), until=ref.getUntil(); argMinLArrays(       ref.getData(), from,until,      cmp,            x -> argMinL(x,from,until,cmp::compare) ); }
  @Property void argMinLArraysWithRangeComparatorInt      ( @ForAll("arraysWithRangeInt"   ) WithRange<   int[]> ref, @ForAll Comparator<? super Integer  > cmp ) { int from=ref.getFrom(), until=ref.getUntil(); argMinLArrays(       ref.getData(), from,until,      cmp,            x -> argMinL(x,from,until,cmp::compare) ); }
  @Property void argMinLArraysWithRangeComparatorLong     ( @ForAll("arraysWithRangeLong"  ) WithRange<  long[]> ref, @ForAll Comparator<? super Long     > cmp ) { int from=ref.getFrom(), until=ref.getUntil(); argMinLArrays(       ref.getData(), from,until,      cmp,            x -> argMinL(x,from,until,cmp::compare) ); }
  @Property void argMinLArraysWithRangeComparatorChar     ( @ForAll("arraysWithRangeChar"  ) WithRange<  char[]> ref, @ForAll Comparator<? super Character> cmp ) { int from=ref.getFrom(), until=ref.getUntil(); argMinLArrays(       ref.getData(), from,until,      cmp,            x -> argMinL(x,from,until,cmp::compare) ); }
  @Property void argMinLArraysWithRangeComparatorFloat    ( @ForAll("arraysWithRangeFloat" ) WithRange< float[]> ref, @ForAll Comparator<? super Float    > cmp ) { int from=ref.getFrom(), until=ref.getUntil(); argMinLArrays(       ref.getData(), from,until,      cmp,            x -> argMinL(x,from,until,cmp::compare) ); }
  @Property void argMinLArraysWithRangeComparatorDouble   ( @ForAll("arraysWithRangeDouble") WithRange<double[]> ref, @ForAll Comparator<? super Double   > cmp ) { int from=ref.getFrom(), until=ref.getUntil(); argMinLArrays(       ref.getData(), from,until,      cmp,            x -> argMinL(x,from,until,cmp::compare) ); }
  @Property void argMinLArraysWithRangeComparatorObjByte  ( @ForAll("arraysWithRangeByte"  ) WithRange<  byte[]> ref, @ForAll Comparator<? super Byte     > cmp ) { int from=ref.getFrom(), until=ref.getUntil(); argMinLArrays( boxed(ref.getData()),from,until,      cmp,            x -> argMinL(x,from,until,cmp) ); }
  @Property void argMinLArraysWithRangeComparatorObjShort ( @ForAll("arraysWithRangeShort" ) WithRange< short[]> ref, @ForAll Comparator<? super Short    > cmp ) { int from=ref.getFrom(), until=ref.getUntil(); argMinLArrays( boxed(ref.getData()),from,until,      cmp,            x -> argMinL(x,from,until,cmp) ); }
  @Property void argMinLArraysWithRangeComparatorObjInt   ( @ForAll("arraysWithRangeInt"   ) WithRange<   int[]> ref, @ForAll Comparator<? super Integer  > cmp ) { int from=ref.getFrom(), until=ref.getUntil(); argMinLArrays( boxed(ref.getData()),from,until,      cmp,            x -> argMinL(x,from,until,cmp) ); }
  @Property void argMinLArraysWithRangeComparatorObjLong  ( @ForAll("arraysWithRangeLong"  ) WithRange<  long[]> ref, @ForAll Comparator<? super Long     > cmp ) { int from=ref.getFrom(), until=ref.getUntil(); argMinLArrays( boxed(ref.getData()),from,until,      cmp,            x -> argMinL(x,from,until,cmp) ); }
  @Property void argMinLArraysWithRangeComparatorObjChar  ( @ForAll("arraysWithRangeChar"  ) WithRange<  char[]> ref, @ForAll Comparator<? super Character> cmp ) { int from=ref.getFrom(), until=ref.getUntil(); argMinLArrays( boxed(ref.getData()),from,until,      cmp,            x -> argMinL(x,from,until,cmp) ); }
  @Property void argMinLArraysWithRangeComparatorObjFloat ( @ForAll("arraysWithRangeFloat" ) WithRange< float[]> ref, @ForAll Comparator<? super Float    > cmp ) { int from=ref.getFrom(), until=ref.getUntil(); argMinLArrays( boxed(ref.getData()),from,until,      cmp,            x -> argMinL(x,from,until,cmp) ); }
  @Property void argMinLArraysWithRangeComparatorObjDouble( @ForAll("arraysWithRangeDouble") WithRange<double[]> ref, @ForAll Comparator<? super Double   > cmp ) { int from=ref.getFrom(), until=ref.getUntil(); argMinLArrays( boxed(ref.getData()),from,until,      cmp,            x -> argMinL(x,from,until,cmp) ); }



  @SuppressWarnings({"rawtypes", "unchecked", "SuspiciousSystemArraycopy"})
  private <A> void argMinRArrays( A array, int from, int until, Comparator cmp, ToIntFunction<? super A> argMin ) {
    assertThat(from).isGreaterThanOrEqualTo(0);
    assertThat(from).isLessThanOrEqualTo(until);

    int len = Array.getLength(array);

    A tst = (A) Array.newInstance(array.getClass().getComponentType(), len);
    arraycopy(array,0, tst,0, len);

    if( from >= until )
      assertThatThrownBy( () -> argMin.applyAsInt(tst) ).isInstanceOf(IllegalArgumentException.class);
    else {
      int        iMin = argMin.applyAsInt(tst);
      assertThat(iMin).isBetween(from,until-1);
      assertThat(tst).isEqualTo(array);

      var min = Array.get(array,iMin);

      for( int i=from; i < until; i++ ) {
        var c = cmp.compare(min, Array.get(array,i));
        assertThat(c).isLessThan( i > iMin ? 0 : 1 );
      }
    }
  }
  @Property void argMinRArraysByte                        ( @ForAll("arraysByte"           )             byte[]  ref                                            ) {                                               argMinRArrays(       ref          ,    0,ref.length, naturalOrder(), ArgMin::argMinR); }
  @Property void argMinRArraysShort                       ( @ForAll("arraysShort"          )            short[]  ref                                            ) {                                               argMinRArrays(       ref          ,    0,ref.length, naturalOrder(), ArgMin::argMinR); }
  @Property void argMinRArraysInt                         ( @ForAll("arraysInt"            )              int[]  ref                                            ) {                                               argMinRArrays(       ref          ,    0,ref.length, naturalOrder(), ArgMin::argMinR); }
  @Property void argMinRArraysLong                        ( @ForAll("arraysLong"           )             long[]  ref                                            ) {                                               argMinRArrays(       ref          ,    0,ref.length, naturalOrder(), ArgMin::argMinR); }
  @Property void argMinRArraysChar                        ( @ForAll("arraysChar"           )             char[]  ref                                            ) {                                               argMinRArrays(       ref          ,    0,ref.length, naturalOrder(), ArgMin::argMinR); }
  @Property void argMinRArraysFloat                       ( @ForAll("arraysFloat"          )            float[]  ref                                            ) {                                               argMinRArrays(       ref          ,    0,ref.length, naturalOrder(), ArgMin::argMinR); }
  @Property void argMinRArraysDouble                      ( @ForAll("arraysDouble"         )           double[]  ref                                            ) {                                               argMinRArrays(       ref          ,    0,ref.length, naturalOrder(), ArgMin::argMinR); }
  @Property void argMinRArraysObjByte                     ( @ForAll("arraysByte"           )             byte[]  ref                                            ) {                                               argMinRArrays( boxed(ref)         ,    0,ref.length, naturalOrder(), ArgMin::argMinR); }
  @Property void argMinRArraysObjShort                    ( @ForAll("arraysShort"          )            short[]  ref                                            ) {                                               argMinRArrays( boxed(ref)         ,    0,ref.length, naturalOrder(), ArgMin::argMinR); }
  @Property void argMinRArraysObjInt                      ( @ForAll("arraysInt"            )              int[]  ref                                            ) {                                               argMinRArrays( boxed(ref)         ,    0,ref.length, naturalOrder(), ArgMin::argMinR); }
  @Property void argMinRArraysObjLong                     ( @ForAll("arraysLong"           )             long[]  ref                                            ) {                                               argMinRArrays( boxed(ref)         ,    0,ref.length, naturalOrder(), ArgMin::argMinR); }
  @Property void argMinRArraysObjChar                     ( @ForAll("arraysChar"           )             char[]  ref                                            ) {                                               argMinRArrays( boxed(ref)         ,    0,ref.length, naturalOrder(), ArgMin::argMinR); }
  @Property void argMinRArraysObjFloat                    ( @ForAll("arraysFloat"          )            float[]  ref                                            ) {                                               argMinRArrays( boxed(ref)         ,    0,ref.length, naturalOrder(), ArgMin::argMinR); }
  @Property void argMinRArraysObjDouble                   ( @ForAll("arraysDouble"         )           double[]  ref                                            ) {                                               argMinRArrays( boxed(ref)         ,    0,ref.length, naturalOrder(), ArgMin::argMinR); }
  @Property void argMinRArraysWithRangeByte               ( @ForAll("arraysWithRangeByte"  ) WithRange<  byte[]> ref                                            ) { int from=ref.getFrom(), until=ref.getUntil(); argMinRArrays(       ref.getData(), from,until,      naturalOrder(), x -> argMinR(x,from,until) ); }
  @Property void argMinRArraysWithRangeShort              ( @ForAll("arraysWithRangeShort" ) WithRange< short[]> ref                                            ) { int from=ref.getFrom(), until=ref.getUntil(); argMinRArrays(       ref.getData(), from,until,      naturalOrder(), x -> argMinR(x,from,until) ); }
  @Property void argMinRArraysWithRangeInt                ( @ForAll("arraysWithRangeInt"   ) WithRange<   int[]> ref                                            ) { int from=ref.getFrom(), until=ref.getUntil(); argMinRArrays(       ref.getData(), from,until,      naturalOrder(), x -> argMinR(x,from,until) ); }
  @Property void argMinRArraysWithRangeLong               ( @ForAll("arraysWithRangeLong"  ) WithRange<  long[]> ref                                            ) { int from=ref.getFrom(), until=ref.getUntil(); argMinRArrays(       ref.getData(), from,until,      naturalOrder(), x -> argMinR(x,from,until) ); }
  @Property void argMinRArraysWithRangeChar               ( @ForAll("arraysWithRangeChar"  ) WithRange<  char[]> ref                                            ) { int from=ref.getFrom(), until=ref.getUntil(); argMinRArrays(       ref.getData(), from,until,      naturalOrder(), x -> argMinR(x,from,until) ); }
  @Property void argMinRArraysWithRangeFloat              ( @ForAll("arraysWithRangeFloat" ) WithRange< float[]> ref                                            ) { int from=ref.getFrom(), until=ref.getUntil(); argMinRArrays(       ref.getData(), from,until,      naturalOrder(), x -> argMinR(x,from,until) ); }
  @Property void argMinRArraysWithRangeDouble             ( @ForAll("arraysWithRangeDouble") WithRange<double[]> ref                                            ) { int from=ref.getFrom(), until=ref.getUntil(); argMinRArrays(       ref.getData(), from,until,      naturalOrder(), x -> argMinR(x,from,until) ); }
  @Property void argMinRArraysWithRangeObjByte            ( @ForAll("arraysWithRangeByte"  ) WithRange<  byte[]> ref                                            ) { int from=ref.getFrom(), until=ref.getUntil(); argMinRArrays( boxed(ref.getData()),from,until,      naturalOrder(), x -> argMinR(x,from,until) ); }
  @Property void argMinRArraysWithRangeObjShort           ( @ForAll("arraysWithRangeShort" ) WithRange< short[]> ref                                            ) { int from=ref.getFrom(), until=ref.getUntil(); argMinRArrays( boxed(ref.getData()),from,until,      naturalOrder(), x -> argMinR(x,from,until) ); }
  @Property void argMinRArraysWithRangeObjInt             ( @ForAll("arraysWithRangeInt"   ) WithRange<   int[]> ref                                            ) { int from=ref.getFrom(), until=ref.getUntil(); argMinRArrays( boxed(ref.getData()),from,until,      naturalOrder(), x -> argMinR(x,from,until) ); }
  @Property void argMinRArraysWithRangeObjLong            ( @ForAll("arraysWithRangeLong"  ) WithRange<  long[]> ref                                            ) { int from=ref.getFrom(), until=ref.getUntil(); argMinRArrays( boxed(ref.getData()),from,until,      naturalOrder(), x -> argMinR(x,from,until) ); }
  @Property void argMinRArraysWithRangeObjChar            ( @ForAll("arraysWithRangeChar"  ) WithRange<  char[]> ref                                            ) { int from=ref.getFrom(), until=ref.getUntil(); argMinRArrays( boxed(ref.getData()),from,until,      naturalOrder(), x -> argMinR(x,from,until) ); }
  @Property void argMinRArraysWithRangeObjFloat           ( @ForAll("arraysWithRangeFloat" ) WithRange< float[]> ref                                            ) { int from=ref.getFrom(), until=ref.getUntil(); argMinRArrays( boxed(ref.getData()),from,until,      naturalOrder(), x -> argMinR(x,from,until) ); }
  @Property void argMinRArraysWithRangeObjDouble          ( @ForAll("arraysWithRangeDouble") WithRange<double[]> ref                                            ) { int from=ref.getFrom(), until=ref.getUntil(); argMinRArrays( boxed(ref.getData()),from,until,      naturalOrder(), x -> argMinR(x,from,until) ); }
  @Property void argMinRArraysComparatorByte              ( @ForAll("arraysByte"           )             byte[]  ref, @ForAll Comparator<? super Byte     > cmp ) {                                               argMinRArrays(       ref          ,    0,ref.length, cmp,            x -> argMinR(x           ,cmp::compare) ); }
  @Property void argMinRArraysComparatorShort             ( @ForAll("arraysShort"          )            short[]  ref, @ForAll Comparator<? super Short    > cmp ) {                                               argMinRArrays(       ref          ,    0,ref.length, cmp,            x -> argMinR(x           ,cmp::compare) ); }
  @Property void argMinRArraysComparatorInt               ( @ForAll("arraysInt"            )              int[]  ref, @ForAll Comparator<? super Integer  > cmp ) {                                               argMinRArrays(       ref          ,    0,ref.length, cmp,            x -> argMinR(x           ,cmp::compare) ); }
  @Property void argMinRArraysComparatorLong              ( @ForAll("arraysLong"           )             long[]  ref, @ForAll Comparator<? super Long     > cmp ) {                                               argMinRArrays(       ref          ,    0,ref.length, cmp,            x -> argMinR(x           ,cmp::compare) ); }
  @Property void argMinRArraysComparatorChar              ( @ForAll("arraysChar"           )             char[]  ref, @ForAll Comparator<? super Character> cmp ) {                                               argMinRArrays(       ref          ,    0,ref.length, cmp,            x -> argMinR(x           ,cmp::compare) ); }
  @Property void argMinRArraysComparatorFloat             ( @ForAll("arraysFloat"          )            float[]  ref, @ForAll Comparator<? super Float    > cmp ) {                                               argMinRArrays(       ref          ,    0,ref.length, cmp,            x -> argMinR(x           ,cmp::compare) ); }
  @Property void argMinRArraysComparatorDouble            ( @ForAll("arraysDouble"         )           double[]  ref, @ForAll Comparator<? super Double   > cmp ) {                                               argMinRArrays(       ref          ,    0,ref.length, cmp,            x -> argMinR(x           ,cmp::compare) ); }
  @Property void argMinRArraysComparatorObjByte           ( @ForAll("arraysByte"           )             byte[]  ref, @ForAll Comparator<? super Byte     > cmp ) {                                               argMinRArrays( boxed(ref)         ,    0,ref.length, cmp,            x -> argMinR(x           ,cmp) ); }
  @Property void argMinRArraysComparatorObjShort          ( @ForAll("arraysShort"          )            short[]  ref, @ForAll Comparator<? super Short    > cmp ) {                                               argMinRArrays( boxed(ref)         ,    0,ref.length, cmp,            x -> argMinR(x           ,cmp) ); }
  @Property void argMinRArraysComparatorObjInt            ( @ForAll("arraysInt"            )              int[]  ref, @ForAll Comparator<? super Integer  > cmp ) {                                               argMinRArrays( boxed(ref)         ,    0,ref.length, cmp,            x -> argMinR(x           ,cmp) ); }
  @Property void argMinRArraysComparatorObjLong           ( @ForAll("arraysLong"           )             long[]  ref, @ForAll Comparator<? super Long     > cmp ) {                                               argMinRArrays( boxed(ref)         ,    0,ref.length, cmp,            x -> argMinR(x           ,cmp) ); }
  @Property void argMinRArraysComparatorObjChar           ( @ForAll("arraysChar"           )             char[]  ref, @ForAll Comparator<? super Character> cmp ) {                                               argMinRArrays( boxed(ref)         ,    0,ref.length, cmp,            x -> argMinR(x           ,cmp) ); }
  @Property void argMinRArraysComparatorObjFloat          ( @ForAll("arraysFloat"          )            float[]  ref, @ForAll Comparator<? super Float    > cmp ) {                                               argMinRArrays( boxed(ref)         ,    0,ref.length, cmp,            x -> argMinR(x           ,cmp) ); }
  @Property void argMinRArraysComparatorObjDouble         ( @ForAll("arraysDouble"         )           double[]  ref, @ForAll Comparator<? super Double   > cmp ) {                                               argMinRArrays( boxed(ref)         ,    0,ref.length, cmp,            x -> argMinR(x           ,cmp) ); }
  @Property void argMinRArraysWithRangeComparatorByte     ( @ForAll("arraysWithRangeByte"  ) WithRange<  byte[]> ref, @ForAll Comparator<? super Byte     > cmp ) { int from=ref.getFrom(), until=ref.getUntil(); argMinRArrays(       ref.getData(), from,until,      cmp,            x -> argMinR(x,from,until,cmp::compare) ); }
  @Property void argMinRArraysWithRangeComparatorShort    ( @ForAll("arraysWithRangeShort" ) WithRange< short[]> ref, @ForAll Comparator<? super Short    > cmp ) { int from=ref.getFrom(), until=ref.getUntil(); argMinRArrays(       ref.getData(), from,until,      cmp,            x -> argMinR(x,from,until,cmp::compare) ); }
  @Property void argMinRArraysWithRangeComparatorInt      ( @ForAll("arraysWithRangeInt"   ) WithRange<   int[]> ref, @ForAll Comparator<? super Integer  > cmp ) { int from=ref.getFrom(), until=ref.getUntil(); argMinRArrays(       ref.getData(), from,until,      cmp,            x -> argMinR(x,from,until,cmp::compare) ); }
  @Property void argMinRArraysWithRangeComparatorLong     ( @ForAll("arraysWithRangeLong"  ) WithRange<  long[]> ref, @ForAll Comparator<? super Long     > cmp ) { int from=ref.getFrom(), until=ref.getUntil(); argMinRArrays(       ref.getData(), from,until,      cmp,            x -> argMinR(x,from,until,cmp::compare) ); }
  @Property void argMinRArraysWithRangeComparatorChar     ( @ForAll("arraysWithRangeChar"  ) WithRange<  char[]> ref, @ForAll Comparator<? super Character> cmp ) { int from=ref.getFrom(), until=ref.getUntil(); argMinRArrays(       ref.getData(), from,until,      cmp,            x -> argMinR(x,from,until,cmp::compare) ); }
  @Property void argMinRArraysWithRangeComparatorFloat    ( @ForAll("arraysWithRangeFloat" ) WithRange< float[]> ref, @ForAll Comparator<? super Float    > cmp ) { int from=ref.getFrom(), until=ref.getUntil(); argMinRArrays(       ref.getData(), from,until,      cmp,            x -> argMinR(x,from,until,cmp::compare) ); }
  @Property void argMinRArraysWithRangeComparatorDouble   ( @ForAll("arraysWithRangeDouble") WithRange<double[]> ref, @ForAll Comparator<? super Double   > cmp ) { int from=ref.getFrom(), until=ref.getUntil(); argMinRArrays(       ref.getData(), from,until,      cmp,            x -> argMinR(x,from,until,cmp::compare) ); }
  @Property void argMinRArraysWithRangeComparatorObjByte  ( @ForAll("arraysWithRangeByte"  ) WithRange<  byte[]> ref, @ForAll Comparator<? super Byte     > cmp ) { int from=ref.getFrom(), until=ref.getUntil(); argMinRArrays( boxed(ref.getData()),from,until,      cmp,            x -> argMinR(x,from,until,cmp) ); }
  @Property void argMinRArraysWithRangeComparatorObjShort ( @ForAll("arraysWithRangeShort" ) WithRange< short[]> ref, @ForAll Comparator<? super Short    > cmp ) { int from=ref.getFrom(), until=ref.getUntil(); argMinRArrays( boxed(ref.getData()),from,until,      cmp,            x -> argMinR(x,from,until,cmp) ); }
  @Property void argMinRArraysWithRangeComparatorObjInt   ( @ForAll("arraysWithRangeInt"   ) WithRange<   int[]> ref, @ForAll Comparator<? super Integer  > cmp ) { int from=ref.getFrom(), until=ref.getUntil(); argMinRArrays( boxed(ref.getData()),from,until,      cmp,            x -> argMinR(x,from,until,cmp) ); }
  @Property void argMinRArraysWithRangeComparatorObjLong  ( @ForAll("arraysWithRangeLong"  ) WithRange<  long[]> ref, @ForAll Comparator<? super Long     > cmp ) { int from=ref.getFrom(), until=ref.getUntil(); argMinRArrays( boxed(ref.getData()),from,until,      cmp,            x -> argMinR(x,from,until,cmp) ); }
  @Property void argMinRArraysWithRangeComparatorObjChar  ( @ForAll("arraysWithRangeChar"  ) WithRange<  char[]> ref, @ForAll Comparator<? super Character> cmp ) { int from=ref.getFrom(), until=ref.getUntil(); argMinRArrays( boxed(ref.getData()),from,until,      cmp,            x -> argMinR(x,from,until,cmp) ); }
  @Property void argMinRArraysWithRangeComparatorObjFloat ( @ForAll("arraysWithRangeFloat" ) WithRange< float[]> ref, @ForAll Comparator<? super Float    > cmp ) { int from=ref.getFrom(), until=ref.getUntil(); argMinRArrays( boxed(ref.getData()),from,until,      cmp,            x -> argMinR(x,from,until,cmp) ); }
  @Property void argMinRArraysWithRangeComparatorObjDouble( @ForAll("arraysWithRangeDouble") WithRange<double[]> ref, @ForAll Comparator<? super Double   > cmp ) { int from=ref.getFrom(), until=ref.getUntil(); argMinRArrays( boxed(ref.getData()),from,until,      cmp,            x -> argMinR(x,from,until,cmp) ); }
}
