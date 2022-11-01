package com.github.jaaa.sort;

import com.github.jaaa.heap.BinaryHeapAccess;


public interface HeapSortAccess extends BinaryHeapAccess
{
  default void heapSort( int from, int until )
  {
    if( from < 0 || from > until ) throw new IllegalArgumentException();

    final int     firstParent = from+until+1 >>> 1;
    for( int root=firstParent; from < until-1; )
    {
      // SIFT DOWN
      for( int parent=root;; )
      {
        int child = (parent<<1) - until;
        if( child < from ) break;
        if( child > from )
            child -= compare(child-1,child)>>>31;
        if( compare(parent,child) > 0 )
          swap(parent,parent=child);
        else break;
      }

      if( root < until-1 )
        ++root; // <- heap building phase
      else
        swap(from++,root); // <- extract min. phase
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
