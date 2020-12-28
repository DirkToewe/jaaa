package com.github.jaaa.sort.tiny;

import com.github.jaaa.CompareSwapAccess;
import com.github.jaaa.util.IntBiConsumer;

import static java.lang.Math.subtractExact;
import com.github.jaaa.Swap;

public interface NetSortV1Access extends CompareSwapAccess
{
  public default void netSortV1( int from, int until )
  {
    if( from > until ) throw new IllegalArgumentException();
    int len = subtractExact(until,from);
    if( len  > 8     ) throw new IllegalArgumentException();

    if( len <= 1 ) return;
    if( len <  4 ) {
      // hard-coded insertion sort
      if(           compare(from+1, from+0) < 0 )   swap(from+0,from+1);
      if( len==3 && compare(from+2, from+1) < 0 ) { swap(from+1,from+2);
      if(           compare(from+1, from+0) < 0 )   swap(from+0,from+1); }
      return;
    }

    byte[] order = { 0, 1, 2, 3, 4, 5, 6, 7 };

    IntBiConsumer sort2 = (i,j) -> {
      int c = compare(from+i, from+j);
      if( c > 0 || c==0 && order[i] > order[j] ) {
        Swap.swap(order,i,j);
        swap(from+i, from+j);
      }
    };

    switch(len)
    {
      case 8:
        sort2.accept(0, 1); sort2.accept(2, 3);
        sort2.accept(4, 5); sort2.accept(6, 7);
        sort2.accept(0, 2); sort2.accept(1, 3);
        sort2.accept(4, 6); sort2.accept(5, 7);
        sort2.accept(1, 2); sort2.accept(5, 6);
        sort2.accept(0, 4); sort2.accept(3, 7);
        sort2.accept(1, 5); sort2.accept(2, 6);
        sort2.accept(1, 4); sort2.accept(3, 6);
        sort2.accept(2, 4); sort2.accept(3, 5);
        sort2.accept(3, 4);
        break;
      case 7:
        sort2.accept(1, 2); sort2.accept(3, 4); sort2.accept(5, 6);
        sort2.accept(0, 2); sort2.accept(3, 5); sort2.accept(4, 6);
        sort2.accept(0, 1); sort2.accept(4, 5); sort2.accept(2, 6);
        sort2.accept(0, 4); sort2.accept(1, 5);
        sort2.accept(0, 3); sort2.accept(2, 5);
        sort2.accept(1, 3); sort2.accept(2, 4);
        sort2.accept(2, 3);
        break;
      case 6:
        sort2.accept(1, 2); sort2.accept(4, 5);
        sort2.accept(0, 2); sort2.accept(3, 5);
        sort2.accept(0, 1); sort2.accept(3, 4); sort2.accept(2, 5);
        sort2.accept(0, 3); sort2.accept(1, 4);
        sort2.accept(2, 4); sort2.accept(1, 3);
        sort2.accept(2, 3);
        break;
      case 5:
        sort2.accept(0, 1); sort2.accept(3, 4);
        sort2.accept(2, 4);
        sort2.accept(2, 3); sort2.accept(1, 4);
        sort2.accept(0, 3);
        sort2.accept(0, 2); sort2.accept(1, 3);
        sort2.accept(1, 2);
        break;
      case 4:
        sort2.accept(0, 1); sort2.accept(2, 3);
        sort2.accept(0, 2); sort2.accept(1, 3);
        sort2.accept(1, 2);
        break;
      default: new AssertionError();
    }
  }
}
