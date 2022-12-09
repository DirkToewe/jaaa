package com.github.jaaa.util;

import com.github.jaaa.ArrayProviderTemplate;
import net.jqwik.api.ForAll;
import net.jqwik.api.Property;

import java.util.Map.Entry;

import static com.github.jaaa.Boxing.boxed;
import static com.github.jaaa.util.ZipWithIndex.zipWithIndex;
import static org.assertj.core.api.Assertions.assertThat;


public class ZipWithIndexTest implements ArrayProviderTemplate
{
  @Override public int maxArraySize() { return 8192; }


  @Property                         void boxed_boolean( @ForAll("arraysBoolean") boolean[] arr ) { boxed_impl(boxed(arr)); }
  @Property                         void boxed_byte   ( @ForAll("arraysByte"   )    byte[] arr ) { boxed_impl(boxed(arr)); }
  @Property                         void boxed_short  ( @ForAll("arraysShort"  )   short[] arr ) { boxed_impl(boxed(arr)); }
  @Property                         void boxed_int    ( @ForAll("arraysInt"    )     int[] arr ) { boxed_impl(boxed(arr)); }
  @Property                         void boxed_long   ( @ForAll("arraysLong"   )    long[] arr ) { boxed_impl(boxed(arr)); }
  @Property                         void boxed_char   ( @ForAll("arraysChar"   )    char[] arr ) { boxed_impl(boxed(arr)); }
  @Property                         void boxed_float  ( @ForAll("arraysFloat"  )   float[] arr ) { boxed_impl(boxed(arr)); }
  @Property                         void boxed_double ( @ForAll("arraysDouble" )  double[] arr ) { boxed_impl(boxed(arr)); }
  <T extends Comparable<? super T>> void boxed_impl( T[] arr )
  {
    var        zip = zipWithIndex(arr);
    for( int i=zip.length; i-- > 0; )
    {
      var k = zip[i].getKey();
      int v = zip[i].getValue();
      assertThat(v).isEqualTo(i);
      assertThat(k).isSameAs(arr[i]);
    }
  }


  @Property                         void unboxed_boolean( @ForAll("arraysBoolean") boolean[] arr ) { unboxed_impl(zipWithIndex(arr), boxed(arr)); }
  @Property                         void unboxed_byte   ( @ForAll("arraysByte"   )    byte[] arr ) { unboxed_impl(zipWithIndex(arr), boxed(arr)); }
  @Property                         void unboxed_short  ( @ForAll("arraysShort"  )   short[] arr ) { unboxed_impl(zipWithIndex(arr), boxed(arr)); }
  @Property                         void unboxed_int    ( @ForAll("arraysInt"    )     int[] arr ) { unboxed_impl(zipWithIndex(arr), boxed(arr)); }
  @Property                         void unboxed_long   ( @ForAll("arraysLong"   )    long[] arr ) { unboxed_impl(zipWithIndex(arr), boxed(arr)); }
  @Property                         void unboxed_char   ( @ForAll("arraysChar"   )    char[] arr ) { unboxed_impl(zipWithIndex(arr), boxed(arr)); }
  @Property                         void unboxed_float  ( @ForAll("arraysFloat"  )   float[] arr ) { unboxed_impl(zipWithIndex(arr), boxed(arr)); }
  @Property                         void unboxed_double ( @ForAll("arraysDouble" )  double[] arr ) { unboxed_impl(zipWithIndex(arr), boxed(arr)); }
  <C extends Comparable<? super C>> void unboxed_impl( Entry<C,Integer>[] zip, C[] boxed )
  {
    assertThat(zip).isEqualTo( zipWithIndex(boxed) );
  }
}
