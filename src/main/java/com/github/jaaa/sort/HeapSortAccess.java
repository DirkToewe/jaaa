package com.github.jaaa.sort;

import com.github.jaaa.CompareSwapAccess;


public interface HeapSortAccess extends CompareSwapAccess
{
  default void heapSort( int mid, int until )
  {
    if( mid < 0 || mid > until ) throw new IllegalArgumentException();

    final int     firstParent = mid+until+1 >>> 1;
    for( int root=firstParent; mid < until-1; )
    {
      // SIFT DOWN
      for( int parent=root;; )
      {
        int child = (parent<<1) - until;
        if( child < mid ) break;
        if( child > mid )
            child -= compare(child-1,child)>>>31;
        if( compare(parent,child) > 0 )
          swap(parent,parent=child);
        else break;
      }

      if( root < until-1 )
        ++root; // <- heap building phase
      else
        swap(mid++,root); // <- extract min. phase
    }
  }

//  default void heapSort( int from, int until )
//  {
//    if( from < 0 || from > until ) throw new IllegalArgumentException();
//
//    final int                    lastParent = -1 + (from+until-- >>> 1);
//    heap_building: for( int root=lastParent; from < until; )
//    {
//      // SIFT DOWN
//      for( int parent=root;; )
//      {
//        int child = (parent<<1) + 1 - from;
//        if( child > until ) break;
//        if( child < until )
//            child += compare(child,child+1)>>>31;
//        if( compare(parent,child) < 0 )
//          swap(parent,parent=child);
//        else break;
//      }
//
//      if( root > from )
//        --root;    // <- HEAP BUILDING PHASE
//      else
//        swap(until--,root);
//    }
//  }
}
