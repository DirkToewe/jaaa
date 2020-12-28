package com.github.jaaa.sort.tiny;

import com.github.jaaa.CompareSwapAccess;
import com.github.jaaa.util.Hex16;

import static java.lang.Math.subtractExact;

public interface HexInSortV2Access extends CompareSwapAccess
{

  public default void hexInSortV2(int from, int until )
  {
    if( from > until ) throw new IllegalArgumentException();
    int len = subtractExact(until,from);
    if( len  > 16    ) throw new IllegalArgumentException();

    Hex16 order = new Hex16();
    order.append(0);

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
          from + order.get(mid)
        );
        if( c < 0 )  hi = mid-1;
        else         lo = mid+1;

        if( lo > hi ) break;
      }

      // insert
      order.insert(lo,i);
    }

    // apply order
    order.unsortAndClear( (i,j) -> swap(from+i,from+j) );
  }
}
