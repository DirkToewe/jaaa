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
        ++root;
      else
        swap(from++,root);
    }
  }

//  default void heapSort( int from, int until )
//  {
//    binHeapMinR_build(from,until);
//
//    int           root = until-1;
//    while( from < root )
//    { // EXTRACT MIN
//      swap(from++,root);
//      // SIFT DOWN
//      for( int parent=root;; ) {
//        int child = (parent<<1) - until;
//        if( child < from ) break;
//        if( child > from )
//            child -= compare(child-1,child)>>>31;
//        if( compare(parent,child) > 0 )
//          swap(parent,parent=child);
//        else break;
//      }
//    }
//  }
}
