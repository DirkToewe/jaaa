package com.github.jaaa.util;

import com.github.jaaa.SwapAccess;

import static com.github.jaaa.util.IMath.sign;
import static java.lang.Byte.toUnsignedInt;
import static java.lang.Math.abs;
import static java.util.stream.Collectors.joining;
import static java.util.stream.IntStream.range;


public class Byte256
{
  private static final int LEN = 256;

  private final byte[] bytes = new byte[LEN];
  private int start=0, size=0;

  public int size() { return size; }

  public void append( int val ) {
    assert  val >= 0;
    assert  val < LEN;
    assert size < LEN;

    assert start >= 0;
    assert start < LEN;
    assert  size >= 0;
    assert  size <= LEN;

    bytes[(size++ + start) % LEN] = (byte) val;
  }

  public int get( int i ) {
    assert     i >= 0;
    assert     i <  size;
    assert start >= 0;
    assert start <  LEN;
    assert  size >= 0;
    assert  size <= LEN;

    return toUnsignedInt( bytes[(start+i) % LEN] );
  }

  public void set( int i, int val ) {
    assert     i >= 0;
    assert     i <  size;
    assert   val >= 0;
    assert   val <  LEN;
    assert start >= 0;
    assert start <  LEN;
    assert  size >= 0;
    assert  size <= LEN;

    bytes[(start+i) % LEN] = (byte) val;
  }

  public void rotate( int rot )
  {
    assert start >= 0;
    assert start < LEN;
    assert  size >= 0;
    assert  size <= LEN;

    if(size == 0 ) return; rot %= size;
    if( rot == 0 ) return;

    if( abs(rot) > size/2 )
      rot -= sign(rot) * size;
    assert abs(rot) <= size/2;

    if( rot < 0 )
      while( rot++ < 0 )
      {
        bytes[(start+size) % LEN] = bytes[start];
        start = (start+1) % LEN;
      }
    else
      while( rot-- > 0 )
      {
        if( --start < 0 )
              start += LEN;
        bytes[start] = bytes[(start+size) % LEN];
      }
  }

  public boolean isFull() { return size == LEN; }

  /** Sorts this Byte256 in ascending order and then clears it. The swap operations
   *  that are applied during sorting process are also applied to the given swap
   *  method.
   *
   *  @param acc The swap method that is called for every swap operation that occurs
   *             during sorting.
   */
  public void sortAndClear( SwapAccess acc )
  {
    for( int i=0; i < size; i++ )
      for(
        int  j = get(i);
        i != j;
      )
      { int k=get(j);
             set(j,j);
        acc.swap(i,j);
        j = k;
      }
    start = size = 0;
  }

  public String toString()
  {
    return range(0,size)
      .mapToObj( i -> String.valueOf( get(i) ) )
      .collect( joining(", ","[","]") );
  }
}
