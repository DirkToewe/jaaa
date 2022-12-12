package com.github.jaaa.sort.tiny;

import com.github.jaaa.CompareSwapAccess;

import static java.lang.Math.subtractExact;

public interface HexInSortV1Access extends CompareSwapAccess
{
  private static long hexInsert( long hex, int i, int val )
  {
    assert val >= 0;
    assert val < 16;
    assert   i >= 0;
    assert   i < 16;   i *= 4;
    long   mask = (1L<<i) - 1;
    return mask & hex
       | (~mask & hex) << 4
       | val*1L << i;
  }
  private static long hexSet( long hex, int i, int val )
  {
    assert val >= 0;
    assert val < 16;
    assert   i >= 0;
    assert   i < 16;    i *=  4;
    return hex & ~(15L<<i)
         | val*1L << i;
  }
  private static int hexGet( long hex, int i )
  {
    assert i >= 0;
    assert i < 16;
    int    result  = 15;
           result &= hex >>> i*4;
    return result;
  }

  public default void hexInSortV1(int from, int until )
  {
    if( from > until ) throw new IllegalArgumentException();
    int len = subtractExact(until,from);
    if( len  > 16    ) throw new IllegalArgumentException();

    long order = 0;

    // compute order
    for( int i=0; ++i < len; )
    {
      // binary search
           int lo = 0;
      for( int hi = i-1,
              mid = i-1;;
              mid = lo+hi >>> 1 )
      {
        int c = compare(
          from + i,
          from + hexGet(order,mid)
        );
        if( c < 0 )  hi = mid-1;
        else         lo = mid+1;

        if( lo > hi ) break;
      }

      // insert
      order = hexInsert(order, lo, i);
    }

    // apply order
    for( int i=0; i < len-1; i++ )
    for( int j=i;; )
    {
      int k = hexGet(order,j);
      order = hexSet(order,j,j); if( k==i ) break;
      swap(from+j, from+k);
      j=k;
    }
  }
}
