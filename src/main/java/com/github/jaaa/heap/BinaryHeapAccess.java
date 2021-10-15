package com.github.jaaa.heap;

import com.github.jaaa.CompareSwapAccess;

public interface BinaryHeapAccess extends CompareSwapAccess
{
  default void binHeapMinR_build( int from, int until )
  {
    if( from < 0 || from > until ) throw new IllegalArgumentException();

    final int  firstParent = from+until+1 >>> 1;
    for( int i=firstParent; i < until; i++ )
      for( int parent=i;; )
      {
        // SIFT DOWN
        int child = (parent<<1) - until;
        if( child < from ) break;
        if( child > from )
            child -= compare(child-1,child)>>>31;
        if( compare(parent,child) > 0 )
          swap(parent,parent=child);
        else break;
      }
  }
}
