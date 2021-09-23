package com.github.jaaa;

import net.jqwik.api.ForAll;
import net.jqwik.api.Property;
import net.jqwik.api.PropertyDefaults;
import net.jqwik.api.constraints.Size;

import java.nio.*;

import static com.github.jaaa.Swap.swap;
import static org.assertj.core.api.Assertions.assertThat;


@PropertyDefaults( tries = 10_000 )
public class SwapTest
{
  private static final int SIZE = 1024;

  @Property void swapWithinArray1Boolean( @ForAll WithIndex<WithIndex<@Size(min=1, max=SIZE) boolean[]>> bitsIJ )
  {
    int   i = bitsIJ.getIndex(),
          j = bitsIJ.getData().getIndex();
    var ref = bitsIJ.getData().getData().clone();

    var  tst = ref.clone();
    swap(tst,i,j);

    for( int k=0; k < ref.length; k++ )
      assertThat( tst[k] ).isEqualTo(
        k==i ? ref[j] :
        k==j ? ref[i] :
               ref[k]
      );
  }
  @Property void swapWithinArray1Byte( @ForAll WithIndex<WithIndex<@Size(min=1, max=SIZE) byte[]>> bitsIJ )
  {
    int   i = bitsIJ.getIndex(),
          j = bitsIJ.getData().getIndex();
    var ref = bitsIJ.getData().getData().clone();

    var  tst = ref.clone();
    swap(tst,i,j);

    for( int k=0; k < ref.length; k++ )
      assertThat( tst[k] ).isEqualTo(
         k==i ? ref[j] :
         k==j ? ref[i] :
                ref[k]
      );
  }
  @Property void swapWithinArray1Short( @ForAll WithIndex<WithIndex<@Size(min=1, max=SIZE) short[]>> bitsIJ )
  {
    int   i = bitsIJ.getIndex(),
          j = bitsIJ.getData().getIndex();
    var ref = bitsIJ.getData().getData().clone();

    var  tst = ref.clone();
    swap(tst,i,j);

    for( int k=0; k < ref.length; k++ )
      assertThat( tst[k] ).isEqualTo(
         k==i ? ref[j] :
         k==j ? ref[i] :
                ref[k]
      );
  }
  @Property void swapWithinArray1Int( @ForAll WithIndex<WithIndex<@Size(min=1, max=SIZE) int[]>> bitsIJ )
  {
    int   i = bitsIJ.getIndex(),
          j = bitsIJ.getData().getIndex();
    var ref = bitsIJ.getData().getData().clone();

    var  tst = ref.clone();
    swap(tst,i,j);

    for( int k=0; k < ref.length; k++ )
      assertThat( tst[k] ).isEqualTo(
        k==i ? ref[j] :
        k==j ? ref[i] :
               ref[k]
      );
  }
  @Property void swapWithinArray1Long( @ForAll WithIndex<WithIndex<@Size(min=1, max=SIZE) long[]>> bitsIJ )
  {
    int   i = bitsIJ.getIndex(),
          j = bitsIJ.getData().getIndex();
    var ref = bitsIJ.getData().getData().clone();

    var  tst = ref.clone();
    swap(tst,i,j);

    for( int k=0; k < ref.length; k++ )
      assertThat( tst[k] ).isEqualTo(
        k==i ? ref[j] :
        k==j ? ref[i] :
               ref[k]
      );
  }
  @Property void swapWithinArray1Char( @ForAll WithIndex<WithIndex<@Size(min=1, max=SIZE) char[]>> bitsIJ )
  {
    int   i = bitsIJ.getIndex(),
          j = bitsIJ.getData().getIndex();
    var ref = bitsIJ.getData().getData().clone();

    var  tst = ref.clone();
    swap(tst,i,j);

    for( int k=0; k < ref.length; k++ )
      assertThat( tst[k] ).isEqualTo(
        k==i ? ref[j] :
        k==j ? ref[i] :
               ref[k]
      );
  }
  @Property void swapWithinArray1Float( @ForAll WithIndex<WithIndex<@Size(min=1, max=SIZE) float[]>> bitsIJ )
  {
    int   i = bitsIJ.getIndex(),
          j = bitsIJ.getData().getIndex();
    var ref = bitsIJ.getData().getData().clone();

    var  tst = ref.clone();
    swap(tst,i,j);

    for( int k=0; k < ref.length; k++ )
      assertThat( tst[k] ).isEqualTo(
        k==i ? ref[j] :
        k==j ? ref[i] :
               ref[k]
      );
  }
  @Property void swapWithinArray1Double( @ForAll WithIndex<WithIndex<@Size(min=1, max=SIZE) double[]>> bitsIJ )
  {
    int   i = bitsIJ.getIndex(),
          j = bitsIJ.getData().getIndex();
    var ref = bitsIJ.getData().getData().clone();

    var  tst = ref.clone();
    swap(tst,i,j);

    for( int k=0; k < ref.length; k++ )
      assertThat( tst[k] ).isEqualTo(
        k==i ? ref[j] :
        k==j ? ref[i] :
               ref[k]
      );
  }
  @Property void swapWithinArray1Generic( @ForAll WithIndex<WithIndex<@Size(min=1, max=SIZE) Integer[]>> bitsIJ )
  {
    int   i = bitsIJ.getIndex(),
          j = bitsIJ.getData().getIndex();
    var ref = bitsIJ.getData().getData().clone();

    var  tst = ref.clone();
    swap(tst,i,j);

    for( int k=0; k < ref.length; k++ )
      assertThat( tst[k] ).isEqualTo(
        k==i ? ref[j] :
        k==j ? ref[i] :
               ref[k]
      );
  }



  @Property void swapWithinBuffer1Byte( @ForAll WithIndex<WithIndex<@Size(min=1, max=SIZE) byte[]>> bitsIJ )
  {
    int   i = bitsIJ.getIndex(),
          j = bitsIJ.getData().getIndex();
    var ref = bitsIJ.getData().getData().clone();

    var  buf = ByteBuffer.wrap( ref.clone() );
    swap(buf,i,j);

    for( int k=0; k < ref.length; k++ )
      assertThat( buf.get(k) ).isEqualTo(
        k==i ? ref[j] :
        k==j ? ref[i] :
               ref[k]
      );
  }
  @Property void swapWithinBuffer1Short( @ForAll WithIndex<WithIndex<@Size(min=1, max=SIZE) short[]>> bitsIJ )
  {
    int   i = bitsIJ.getIndex(),
          j = bitsIJ.getData().getIndex();
    var ref = bitsIJ.getData().getData().clone();

    var  buf = ShortBuffer.wrap( ref.clone() );
    swap(buf,i,j);

    for( int k=0; k < ref.length; k++ )
      assertThat( buf.get(k) ).isEqualTo(
        k==i ? ref[j] :
        k==j ? ref[i] :
               ref[k]
      );
  }
  @Property void swapWithinBuffer1Int( @ForAll WithIndex<WithIndex<@Size(min=1, max=SIZE) int[]>> bitsIJ )
  {
    int   i = bitsIJ.getIndex(),
          j = bitsIJ.getData().getIndex();
    var ref = bitsIJ.getData().getData().clone();

    var  buf = IntBuffer.wrap( ref.clone() );
    swap(buf,i,j);

    for( int k=0; k < ref.length; k++ )
      assertThat( buf.get(k) ).isEqualTo(
        k==i ? ref[j] :
        k==j ? ref[i] :
               ref[k]
      );
  }
  @Property void swapWithinBuffer1Long( @ForAll WithIndex<WithIndex<@Size(min=1, max=SIZE) long[]>> bitsIJ )
  {
    int   i = bitsIJ.getIndex(),
          j = bitsIJ.getData().getIndex();
    var ref = bitsIJ.getData().getData().clone();

    var  buf = LongBuffer.wrap( ref.clone() );
    swap(buf,i,j);

    for( int k=0; k < ref.length; k++ )
      assertThat( buf.get(k) ).isEqualTo(
        k==i ? ref[j] :
        k==j ? ref[i] :
               ref[k]
      );
  }
  @Property void swapWithinBuffer1Char( @ForAll WithIndex<WithIndex<@Size(min=1, max=SIZE) char[]>> bitsIJ )
  {
    int   i = bitsIJ.getIndex(),
          j = bitsIJ.getData().getIndex();
    var ref = bitsIJ.getData().getData().clone();

    var  buf = CharBuffer.wrap( ref.clone() );
    swap(buf,i,j);

    for( int k=0; k < ref.length; k++ )
      assertThat( buf.get(k) ).isEqualTo(
        k==i ? ref[j] :
        k==j ? ref[i] :
               ref[k]
      );
  }
  @Property void swapWithinBuffer1Float( @ForAll WithIndex<WithIndex<@Size(min=1, max=SIZE) float[]>> bitsIJ )
  {
    int   i = bitsIJ.getIndex(),
          j = bitsIJ.getData().getIndex();
    var ref = bitsIJ.getData().getData().clone();

    var  buf = FloatBuffer.wrap( ref.clone() );
    swap(buf,i,j);

    for( int k=0; k < ref.length; k++ )
      assertThat( buf.get(k) ).isEqualTo(
        k==i ? ref[j] :
        k==j ? ref[i] :
               ref[k]
      );
  }
  @Property void swapWithinBuffer1Double( @ForAll WithIndex<WithIndex<@Size(min=1, max=SIZE) double[]>> bitsIJ )
  {
    int   i = bitsIJ.getIndex(),
          j = bitsIJ.getData().getIndex();
    var ref = bitsIJ.getData().getData().clone();

    var  buf = DoubleBuffer.wrap( ref.clone() );
    swap(buf,i,j);

    for( int k=0; k < ref.length; k++ )
      assertThat( buf.get(k) ).isEqualTo(
        k==i ? ref[j] :
        k==j ? ref[i] :
               ref[k]
      );
  }




  @Property void swapWithinArray2Boolean( @ForAll WithIndex<WithIndex<@Size(min=1, max=SIZE) boolean[]>> bitsIJ )
  {
    int   i = bitsIJ.getIndex(),
          j = bitsIJ.getData().getIndex();
    var ref = bitsIJ.getData().getData().clone();

    var  tst = ref.clone();
    swap(tst,i, tst,j);

    for( int k=0; k < ref.length; k++ )
      assertThat( tst[k] ).isEqualTo(
        k==i ? ref[j] :
        k==j ? ref[i] :
               ref[k]
      );
  }
  @Property void swapWithinArray2Byte( @ForAll WithIndex<WithIndex<@Size(min=1, max=SIZE) byte[]>> bitsIJ )
  {
    int   i = bitsIJ.getIndex(),
          j = bitsIJ.getData().getIndex();
    var ref = bitsIJ.getData().getData().clone();

    var  tst = ref.clone();
    swap(tst,i, tst,j);

    for( int k=0; k < ref.length; k++ )
      assertThat( tst[k] ).isEqualTo(
        k==i ? ref[j] :
        k==j ? ref[i] :
               ref[k]
      );
  }
  @Property void swapWithinArray2Short( @ForAll WithIndex<WithIndex<@Size(min=1, max=SIZE) short[]>> bitsIJ )
  {
    int   i = bitsIJ.getIndex(),
          j = bitsIJ.getData().getIndex();
    var ref = bitsIJ.getData().getData().clone();

    var  tst = ref.clone();
    swap(tst,i, tst,j);

    for( int k=0; k < ref.length; k++ )
      assertThat( tst[k] ).isEqualTo(
        k==i ? ref[j] :
        k==j ? ref[i] :
               ref[k]
      );
  }
  @Property void swapWithinArray2Int( @ForAll WithIndex<WithIndex<@Size(min=1, max=SIZE) int[]>> bitsIJ )
  {
    int   i = bitsIJ.getIndex(),
          j = bitsIJ.getData().getIndex();
    var ref = bitsIJ.getData().getData().clone();

    var  tst = ref.clone();
    swap(tst,i, tst,j);

    for( int k=0; k < ref.length; k++ )
      assertThat( tst[k] ).isEqualTo(
        k==i ? ref[j] :
        k==j ? ref[i] :
               ref[k]
      );
  }
  @Property void swapWithinArray2Long( @ForAll WithIndex<WithIndex<@Size(min=1, max=SIZE) long[]>> bitsIJ )
  {
    int   i = bitsIJ.getIndex(),
          j = bitsIJ.getData().getIndex();
    var ref = bitsIJ.getData().getData().clone();

    var  tst = ref.clone();
    swap(tst,i, tst,j);

    for( int k=0; k < ref.length; k++ )
      assertThat( tst[k] ).isEqualTo(
        k==i ? ref[j] :
        k==j ? ref[i] :
               ref[k]
      );
  }
  @Property void swapWithinArray2Char( @ForAll WithIndex<WithIndex<@Size(min=1, max=SIZE) char[]>> bitsIJ )
  {
    int   i = bitsIJ.getIndex(),
          j = bitsIJ.getData().getIndex();
    var ref = bitsIJ.getData().getData().clone();

    var  tst = ref.clone();
    swap(tst,i, tst,j);

    for( int k=0; k < ref.length; k++ )
      assertThat( tst[k] ).isEqualTo(
        k==i ? ref[j] :
        k==j ? ref[i] :
               ref[k]
      );
  }
  @Property void swapWithinArray2Float( @ForAll WithIndex<WithIndex<@Size(min=1, max=SIZE) float[]>> bitsIJ )
  {
    int   i = bitsIJ.getIndex(),
          j = bitsIJ.getData().getIndex();
    var ref = bitsIJ.getData().getData().clone();

    var  tst = ref.clone();
    swap(tst,i, tst,j);

    for( int k=0; k < ref.length; k++ )
      assertThat( tst[k] ).isEqualTo(
        k==i ? ref[j] :
        k==j ? ref[i] :
               ref[k]
      );
  }
  @Property void swapWithinArray2Double( @ForAll WithIndex<WithIndex<@Size(min=1, max=SIZE) double[]>> bitsIJ )
  {
    int   i = bitsIJ.getIndex(),
          j = bitsIJ.getData().getIndex();
    var ref = bitsIJ.getData().getData().clone();

    var  tst = ref.clone();
    swap(tst,i, tst,j);

    for( int k=0; k < ref.length; k++ )
      assertThat( tst[k] ).isEqualTo(
        k==i ? ref[j] :
        k==j ? ref[i] :
               ref[k]
      );
  }
  @Property void swapWithinArray2Generic( @ForAll WithIndex<WithIndex<@Size(min=1, max=SIZE) Integer[]>> bitsIJ )
  {
    int   i = bitsIJ.getIndex(),
          j = bitsIJ.getData().getIndex();
    var ref = bitsIJ.getData().getData().clone();

    var  tst = ref.clone();
    swap(tst,i, tst,j);

    for( int k=0; k < ref.length; k++ )
      assertThat( tst[k] ).isEqualTo(
        k==i ? ref[j] :
        k==j ? ref[i] :
               ref[k]
      );
  }



  @Property void swapWithinBuffer2Byte( @ForAll WithIndex<WithIndex<@Size(min=1, max=SIZE) byte[]>> bitsIJ )
  {
    int   i = bitsIJ.getIndex(),
          j = bitsIJ.getData().getIndex();
    var ref = bitsIJ.getData().getData().clone();

    var  buf = ByteBuffer.wrap( ref.clone() );
    swap(buf,i, buf,j);

    for( int k=0; k < ref.length; k++ )
      assertThat( buf.get(k) ).isEqualTo(
        k==i ? ref[j] :
        k==j ? ref[i] :
               ref[k]
      );
  }
  @Property void swapWithinBuffer2Short( @ForAll WithIndex<WithIndex<@Size(min=1, max=SIZE) short[]>> bitsIJ )
  {
    int   i = bitsIJ.getIndex(),
          j = bitsIJ.getData().getIndex();
    var ref = bitsIJ.getData().getData().clone();

    var  buf = ShortBuffer.wrap( ref.clone() );
    swap(buf,i, buf,j);

    for( int k=0; k < ref.length; k++ )
      assertThat( buf.get(k) ).isEqualTo(
        k==i ? ref[j] :
        k==j ? ref[i] :
               ref[k]
      );
  }
  @Property void swapWithinBuffer2Int( @ForAll WithIndex<WithIndex<@Size(min=1, max=SIZE) int[]>> bitsIJ )
  {
    int   i = bitsIJ.getIndex(),
          j = bitsIJ.getData().getIndex();
    var ref = bitsIJ.getData().getData().clone();

    var  buf = IntBuffer.wrap( ref.clone() );
    swap(buf,i, buf,j);

    for( int k=0; k < ref.length; k++ )
      assertThat( buf.get(k) ).isEqualTo(
        k==i ? ref[j] :
        k==j ? ref[i] :
               ref[k]
      );
  }
  @Property void swapWithinBuffer2Long( @ForAll WithIndex<WithIndex<@Size(min=1, max=SIZE) long[]>> bitsIJ )
  {
    int   i = bitsIJ.getIndex(),
            j = bitsIJ.getData().getIndex();
    var ref = bitsIJ.getData().getData().clone();

    var  buf = LongBuffer.wrap( ref.clone() );
    swap(buf,i, buf,j);

    for( int k=0; k < ref.length; k++ )
      assertThat( buf.get(k) ).isEqualTo(
        k==i ? ref[j] :
        k==j ? ref[i] :
               ref[k]
      );
  }
  @Property void swapWithinBuffer2Char( @ForAll WithIndex<WithIndex<@Size(min=1, max=SIZE) char[]>> bitsIJ )
  {
    int   i = bitsIJ.getIndex(),
          j = bitsIJ.getData().getIndex();
    var ref = bitsIJ.getData().getData().clone();

    var  buf = CharBuffer.wrap( ref.clone() );
    swap(buf,i, buf,j);

    for( int k=0; k < ref.length; k++ )
      assertThat( buf.get(k) ).isEqualTo(
        k==i ? ref[j] :
        k==j ? ref[i] :
               ref[k]
      );
  }
  @Property void swapWithinBuffer2Float( @ForAll WithIndex<WithIndex<@Size(min=1, max=SIZE) float[]>> bitsIJ )
  {
    int   i = bitsIJ.getIndex(),
          j = bitsIJ.getData().getIndex();
    var ref = bitsIJ.getData().getData().clone();

    var  buf = FloatBuffer.wrap( ref.clone() );
    swap(buf,i, buf,j);

    for( int k=0; k < ref.length; k++ )
      assertThat( buf.get(k) ).isEqualTo(
        k==i ? ref[j] :
        k==j ? ref[i] :
               ref[k]
      );
  }
  @Property void swapWithinBuffer2Double( @ForAll WithIndex<WithIndex<@Size(min=1, max=SIZE) double[]>> bitsIJ )
  {
    int   i = bitsIJ.getIndex(),
          j = bitsIJ.getData().getIndex();
    var ref = bitsIJ.getData().getData().clone();

    var  buf = DoubleBuffer.wrap( ref.clone() );
    swap(buf,i, buf,j);

    for( int k=0; k < ref.length; k++ )
      assertThat( buf.get(k) ).isEqualTo(
        k==i ? ref[j] :
        k==j ? ref[i] :
               ref[k]
      );
  }



  @Property void swapBetweenArraysBoolean(
    @ForAll WithIndex<@Size(min=1, max=SIZE) boolean[]> sample1,
    @ForAll WithIndex<@Size(min=1, max=SIZE) boolean[]> sample2
  )
  {
    int  i1 = sample1.getIndex(),
         i2 = sample2.getIndex();
    var ref1 = sample1.getData(); var tst1 = ref1.clone();
    var ref2 = sample2.getData(); var tst2 = ref2.clone();

    swap(tst1,i1, tst2,i2);

    for( int i=0; i < ref1.length; i++ ) assertThat(tst1[i]).isEqualTo( i==i1 ? ref2[i2] : ref1[i] );
    for( int i=0; i < ref2.length; i++ ) assertThat(tst2[i]).isEqualTo( i==i2 ? ref1[i1] : ref2[i] );
  }
  @Property void swapBetweenArraysByte(
    @ForAll WithIndex<@Size(min=1, max=SIZE) byte[]> sample1,
    @ForAll WithIndex<@Size(min=1, max=SIZE) byte[]> sample2
  )
  {
    int  i1 = sample1.getIndex(),
         i2 = sample2.getIndex();
    var ref1 = sample1.getData(); var tst1 = ref1.clone();
    var ref2 = sample2.getData(); var tst2 = ref2.clone();

    swap(tst1,i1, tst2,i2);

    for( int i=0; i < ref1.length; i++ ) assertThat(tst1[i]).isEqualTo( i==i1 ? ref2[i2] : ref1[i] );
    for( int i=0; i < ref2.length; i++ ) assertThat(tst2[i]).isEqualTo( i==i2 ? ref1[i1] : ref2[i] );
  }
  @Property void swapBetweenArraysShort(
    @ForAll WithIndex<@Size(min=1, max=SIZE) short[]> sample1,
    @ForAll WithIndex<@Size(min=1, max=SIZE) short[]> sample2
  )
  {
    int  i1 = sample1.getIndex(),
         i2 = sample2.getIndex();
    var ref1 = sample1.getData(); var tst1 = ref1.clone();
    var ref2 = sample2.getData(); var tst2 = ref2.clone();

    swap(tst1,i1, tst2,i2);

    for( int i=0; i < ref1.length; i++ ) assertThat(tst1[i]).isEqualTo( i==i1 ? ref2[i2] : ref1[i] );
    for( int i=0; i < ref2.length; i++ ) assertThat(tst2[i]).isEqualTo( i==i2 ? ref1[i1] : ref2[i] );
  }
  @Property void swapBetweenArraysInt(
    @ForAll WithIndex<@Size(min=1, max=SIZE) int[]> sample1,
    @ForAll WithIndex<@Size(min=1, max=SIZE) int[]> sample2
  )
  {
    int  i1 = sample1.getIndex(),
         i2 = sample2.getIndex();
    var ref1 = sample1.getData(); var tst1 = ref1.clone();
    var ref2 = sample2.getData(); var tst2 = ref2.clone();

    swap(tst1,i1, tst2,i2);

    for( int i=0; i < ref1.length; i++ ) assertThat(tst1[i]).isEqualTo( i==i1 ? ref2[i2] : ref1[i] );
    for( int i=0; i < ref2.length; i++ ) assertThat(tst2[i]).isEqualTo( i==i2 ? ref1[i1] : ref2[i] );
  }
  @Property void swapBetweenArraysLong(
    @ForAll WithIndex<@Size(min=1, max=SIZE) long[]> sample1,
    @ForAll WithIndex<@Size(min=1, max=SIZE) long[]> sample2
  )
  {
    int  i1 = sample1.getIndex(),
         i2 = sample2.getIndex();
    var ref1 = sample1.getData(); var tst1 = ref1.clone();
    var ref2 = sample2.getData(); var tst2 = ref2.clone();

    swap(tst1,i1, tst2,i2);

    for( int i=0; i < ref1.length; i++ ) assertThat(tst1[i]).isEqualTo( i==i1 ? ref2[i2] : ref1[i] );
    for( int i=0; i < ref2.length; i++ ) assertThat(tst2[i]).isEqualTo( i==i2 ? ref1[i1] : ref2[i] );
  }
  @Property void swapBetweenArraysChar(
    @ForAll WithIndex<@Size(min=1, max=SIZE) char[]> sample1,
    @ForAll WithIndex<@Size(min=1, max=SIZE) char[]> sample2
  )
  {
    int  i1 = sample1.getIndex(),
         i2 = sample2.getIndex();
    var ref1 = sample1.getData(); var tst1 = ref1.clone();
    var ref2 = sample2.getData(); var tst2 = ref2.clone();

    swap(tst1,i1, tst2,i2);

    for( int i=0; i < ref1.length; i++ ) assertThat(tst1[i]).isEqualTo( i==i1 ? ref2[i2] : ref1[i] );
    for( int i=0; i < ref2.length; i++ ) assertThat(tst2[i]).isEqualTo( i==i2 ? ref1[i1] : ref2[i] );
  }
  @Property void swapBetweenArraysFloat(
    @ForAll WithIndex<@Size(min=1, max=SIZE) float[]> sample1,
    @ForAll WithIndex<@Size(min=1, max=SIZE) float[]> sample2
  )
  {
    int  i1 = sample1.getIndex(),
         i2 = sample2.getIndex();
    var ref1 = sample1.getData(); var tst1 = ref1.clone();
    var ref2 = sample2.getData(); var tst2 = ref2.clone();

    swap(tst1,i1, tst2,i2);

    for( int i=0; i < ref1.length; i++ ) assertThat(tst1[i]).isEqualTo( i==i1 ? ref2[i2] : ref1[i] );
    for( int i=0; i < ref2.length; i++ ) assertThat(tst2[i]).isEqualTo( i==i2 ? ref1[i1] : ref2[i] );
  }
  @Property void swapBetweenArraysDouble(
    @ForAll WithIndex<@Size(min=1, max=SIZE) float[]> sample1,
    @ForAll WithIndex<@Size(min=1, max=SIZE) float[]> sample2
  )
  {
    int  i1 = sample1.getIndex(),
         i2 = sample2.getIndex();
    var ref1 = sample1.getData(); var tst1 = ref1.clone();
    var ref2 = sample2.getData(); var tst2 = ref2.clone();

    swap(tst1,i1, tst2,i2);

    for( int i=0; i < ref1.length; i++ ) assertThat(tst1[i]).isEqualTo( i==i1 ? ref2[i2] : ref1[i] );
    for( int i=0; i < ref2.length; i++ ) assertThat(tst2[i]).isEqualTo( i==i2 ? ref1[i1] : ref2[i] );
  }
  @Property void swapBetweenArraysGeneric(
    @ForAll WithIndex<@Size(min=1, max=SIZE) Integer[]> sample1,
    @ForAll WithIndex<@Size(min=1, max=SIZE) Integer[]> sample2
  )
  {
    int  i1 = sample1.getIndex(),
         i2 = sample2.getIndex();
    var ref1 = sample1.getData(); var tst1 = ref1.clone();
    var ref2 = sample2.getData(); var tst2 = ref2.clone();

    swap(tst1,i1, tst2,i2);

    for( int i=0; i < ref1.length; i++ ) assertThat(tst1[i]).isEqualTo( i==i1 ? ref2[i2] : ref1[i] );
    for( int i=0; i < ref2.length; i++ ) assertThat(tst2[i]).isEqualTo( i==i2 ? ref1[i1] : ref2[i] );
  }



  @Property void swapBetweenBuffersByte(
    @ForAll WithIndex<@Size(min=1, max=SIZE) byte[]> sample1,
    @ForAll WithIndex<@Size(min=1, max=SIZE) byte[]> sample2
  )
  {
    int  i1 = sample1.getIndex(),
         i2 = sample2.getIndex();
    var ref1 = sample1.getData(); var tst1 = ByteBuffer.wrap(ref1.clone());
    var ref2 = sample2.getData(); var tst2 = ByteBuffer.wrap(ref2.clone());

    swap(tst1,i1, tst2,i2);

    for( int i=0; i < ref1.length; i++ ) assertThat(tst1.get(i)).isEqualTo( i==i1 ? ref2[i2] : ref1[i] );
    for( int i=0; i < ref2.length; i++ ) assertThat(tst2.get(i)).isEqualTo( i==i2 ? ref1[i1] : ref2[i] );
  }
  @Property void swapBetweenBuffersShort(
    @ForAll WithIndex<@Size(min=1, max=SIZE) short[]> sample1,
    @ForAll WithIndex<@Size(min=1, max=SIZE) short[]> sample2
  )
  {
    int  i1 = sample1.getIndex(),
         i2 = sample2.getIndex();
    var ref1 = sample1.getData(); var tst1 = ShortBuffer.wrap(ref1.clone());
    var ref2 = sample2.getData(); var tst2 = ShortBuffer.wrap(ref2.clone());

    swap(tst1,i1, tst2,i2);

    for( int i=0; i < ref1.length; i++ ) assertThat(tst1.get(i)).isEqualTo( i==i1 ? ref2[i2] : ref1[i] );
    for( int i=0; i < ref2.length; i++ ) assertThat(tst2.get(i)).isEqualTo( i==i2 ? ref1[i1] : ref2[i] );
  }
  @Property void swapBetweenBuffersInt(
    @ForAll WithIndex<@Size(min=1, max=SIZE) int[]> sample1,
    @ForAll WithIndex<@Size(min=1, max=SIZE) int[]> sample2
  )
  {
    int  i1 = sample1.getIndex(),
         i2 = sample2.getIndex();
    var ref1 = sample1.getData(); var tst1 = IntBuffer.wrap(ref1.clone());
    var ref2 = sample2.getData(); var tst2 = IntBuffer.wrap(ref2.clone());

    swap(tst1,i1, tst2,i2);

    for( int i=0; i < ref1.length; i++ ) assertThat(tst1.get(i)).isEqualTo( i==i1 ? ref2[i2] : ref1[i] );
    for( int i=0; i < ref2.length; i++ ) assertThat(tst2.get(i)).isEqualTo( i==i2 ? ref1[i1] : ref2[i] );
  }
  @Property void swapBetweenBuffersLong(
    @ForAll WithIndex<@Size(min=1, max=SIZE) long[]> sample1,
    @ForAll WithIndex<@Size(min=1, max=SIZE) long[]> sample2
  )
  {
    int  i1 = sample1.getIndex(),
         i2 = sample2.getIndex();
    var ref1 = sample1.getData(); var tst1 = LongBuffer.wrap(ref1.clone());
    var ref2 = sample2.getData(); var tst2 = LongBuffer.wrap(ref2.clone());

    swap(tst1,i1, tst2,i2);

    for( int i=0; i < ref1.length; i++ ) assertThat(tst1.get(i)).isEqualTo( i==i1 ? ref2[i2] : ref1[i] );
    for( int i=0; i < ref2.length; i++ ) assertThat(tst2.get(i)).isEqualTo( i==i2 ? ref1[i1] : ref2[i] );
  }
  @Property void swapBetweenBuffersChar(
    @ForAll WithIndex<@Size(min=1, max=SIZE) char[]> sample1,
    @ForAll WithIndex<@Size(min=1, max=SIZE) char[]> sample2
  )
  {
    int  i1 = sample1.getIndex(),
         i2 = sample2.getIndex();
    var ref1 = sample1.getData(); var tst1 = CharBuffer.wrap(ref1.clone());
    var ref2 = sample2.getData(); var tst2 = CharBuffer.wrap(ref2.clone());

    swap(tst1,i1, tst2,i2);

    for( int i=0; i < ref1.length; i++ ) assertThat(tst1.get(i)).isEqualTo( i==i1 ? ref2[i2] : ref1[i] );
    for( int i=0; i < ref2.length; i++ ) assertThat(tst2.get(i)).isEqualTo( i==i2 ? ref1[i1] : ref2[i] );
  }
  @Property void swapBetweenBuffersFloat(
    @ForAll WithIndex<@Size(min=1, max=SIZE) float[]> sample1,
    @ForAll WithIndex<@Size(min=1, max=SIZE) float[]> sample2
  )
  {
    int  i1 = sample1.getIndex(),
         i2 = sample2.getIndex();
    var ref1 = sample1.getData(); var tst1 = FloatBuffer.wrap(ref1.clone());
    var ref2 = sample2.getData(); var tst2 = FloatBuffer.wrap(ref2.clone());

    swap(tst1,i1, tst2,i2);

    for( int i=0; i < ref1.length; i++ ) assertThat(tst1.get(i)).isEqualTo( i==i1 ? ref2[i2] : ref1[i] );
    for( int i=0; i < ref2.length; i++ ) assertThat(tst2.get(i)).isEqualTo( i==i2 ? ref1[i1] : ref2[i] );
  }
  @Property void swapBetweenBuffersDouble(
    @ForAll WithIndex<@Size(min=1, max=SIZE) double[]> sample1,
    @ForAll WithIndex<@Size(min=1, max=SIZE) double[]> sample2
  )
  {
    int  i1 = sample1.getIndex(),
         i2 = sample2.getIndex();
    var ref1 = sample1.getData(); var tst1 = DoubleBuffer.wrap(ref1.clone());
    var ref2 = sample2.getData(); var tst2 = DoubleBuffer.wrap(ref2.clone());

    swap(tst1,i1, tst2,i2);

    for( int i=0; i < ref1.length; i++ ) assertThat(tst1.get(i)).isEqualTo( i==i1 ? ref2[i2] : ref1[i] );
    for( int i=0; i < ref2.length; i++ ) assertThat(tst2.get(i)).isEqualTo( i==i2 ? ref1[i1] : ref2[i] );
  }
}
