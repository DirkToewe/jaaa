package com.github.jaaa.sort;

import com.github.jaaa.CompareSwapAccess;


// REFERENCES
// ----------
// .. [1] "Building Heaps Fast"
//         C. J. H. McDiarmid, B. A. Reed
//         JOURNAL OF ALGORITHMS 10, 352-365 (1989)
public interface HeapSortAccess extends CompareSwapAccess
{
  default void heapSort( int from, int until )
  {
    if( from < 0 || from > until )
      throw new IllegalArgumentException();

    // Implementation Details
    // ----------------------
    // Builds a min-heap with the root on the right-hand side.
    //
    // Elements are added to the heap by first trickling each
    // element down from root to a leaf and then sifting the
    // new element up again.
    //
    // During the trickle-down process only one comparison is
    // required per tree level to find the lesser child. Since
    // there are more children than parents, most elements are
    // going to end up in leaf nodes anyway. That means that
    // sifting up from the leaves is for the most part cheap.
    //
    // There is however one exception worth mentioning: Fast
    // heap building will not be efficient when the input is
    // in (mostly) descending. During heap building such cases,
    // (almost) every element added to the heap is going to end
    // up as new root of the heap. This will make heap building
    // significantly slower than traditional heap building.
    // During the minimum-extraction-phase, however, the fast
    // heap building will be efficient since every new root is
    // going to end up as leaf.
    final int     firstParent = from + until+1 >>> 1;
    for( int root=firstParent;  from < until-1; )
    {
      int parent=root;

      // TRICKLE DOWN
      for(;;) {
        int child = (parent<<1) - until;
        if( child < from ) break;
        if( child > from )
            child -= compare(child-1,child)>>>31;
        swap(parent,parent=child);
      }

      // BUBBLE UP
      for( int child=parent; root != child; )
      {
        parent = child+until+1 >>> 1;
        if( compare(child,parent) < 0 )
          swap(child,child=parent);
        else break;
      }

      if( root < until-1 )
        ++root; // <- heap building phase
      else
        swap(from++,root); // <- extract min. phase
    }
  }

//  default void heapSortFast( int from, int until )
//  {
//    if( from < 0 || from > until )
//      throw new IllegalArgumentException();
//
//    final int                    lastParent = -1 + (from+until-- >>> 1);
//    heap_building: for( int root=lastParent; from < until; )
//    {
//      int parent=root;
//
//      // TRICKLE DOWN
//      for(;;) {
//        int child = (parent<<1) + 1 - from;
//        if( child > until ) break;
//        if( child < until )
//            child += compare(child,child+1)>>>31;
//        swap(parent,parent=child);
//      }
//
//      // BUBBLE UP
//      for( int child=parent; root != child; )
//      {
//        parent = child+from-1 >>> 1;
//        if( compare(child,parent) > 0 )
//          swap(child,child=parent);
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
