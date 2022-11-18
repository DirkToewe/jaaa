package com.github.jaaa.heap;

import com.github.jaaa.CompareSwapAccess;


public interface MaxHeapLAccess extends CompareSwapAccess
{
  default void maxHeapL_build( int from, int until )
  {
    if( from < 0 || from > until ) throw new IllegalArgumentException();

    final int     firstChild = from+until-- >>> 1;
    for( int root=firstChild; from < root--; )
      for( int parent=root;; )
      {
        // SIFT DOWN
        int child = (parent<<1) + 1 - from;
        if( child > until ) break;
        if( child < until )
            child += compare(child,child+1)>>>31;
        if( compare(parent,child) < 0 )
          swap(parent,parent=child);
        else break;
      }
  }

  default void maxHeapL_buildFast( int from, int until )
  {
    // REFERENCES
    // ----------
    // .. [1] "Building Heaps Fast"
    //         C. J. H. McDiarmid, B. A. Reed
    //         JOURNAL OF ALGORITHMS 10, 352-365 (1989)
    if( from < 0 || from > until ) throw new IllegalArgumentException();

    final int     firstChild = from+until-- >>> 1;
    for( int root=firstChild; from < root--; )
    {
      int parent=root;

      // TRICKLE DOWN
      for(;;) {
        int child = (parent<<1) + 1 - from;
        if( child > until ) break;
        if( child < until )
            child += compare(child,child+1)>>>31;
        swap(parent,parent=child);
      }

      // BUBBLE UP
      for( int child=parent; root != child; )
      {
        parent = child+from-1 >>> 1;
        if( compare(child,parent) > 0 )
          swap(child,child=parent);
        else break;
      }
    }
  }

  default int maxHeapL_child( int from, int until, int parent )
  {
    if( from < 0 || from > parent || parent >= until )
      throw new IllegalArgumentException();
    return (parent<<1) + 1 - from;
  }

  default int maxHeapL_parent( int from, int until, int child )
  {
    if( from < 0 || from > child || child >= until )
      throw new IllegalArgumentException();
    return child+from-1 >>> 1;
  }

  default void maxHeapL_siftDown( int from, int until, int parent )
  {
    if( from < 0 || from > parent || parent >= until )
      throw new IllegalArgumentException();
    --until;
    for(;;) {
      // SIFT DOWN
      int child = (parent<<1) + 1 - from;
      if( child > until ) break;
      if( child < until )
          child += compare(child,child+1)>>>31;
      if( compare(parent,child) < 0 )
        swap(parent,parent=child);
      else break;
    }
  }

  default void maxHeapL_siftDownFast( int from, int until, int parent )
  {
    if( from < 0 || from > parent || parent >= until )
      throw new IllegalArgumentException();
    --until;
    int p = parent;
    // TRICKLE DOWN
    for(;;) {
      int child = (p<<1) + 1 - from;
      if( child > until ) break;
      if( child < until )
          child += compare(child,child+1)>>>31;
      swap(p,p=child);
    }

    // BUBBLE UP
    for( int child=p; parent != child; )
    {
      p = child+from-1 >>> 1;
      if( compare(child,p) > 0 )
        swap(child,child=p);
      else break;
    }
  }

  default void maxHeapL_bubbleUp( int from, int until, int child )
  {
    if( from < 0 || from > child || child >= until )
      throw new IllegalArgumentException();
    // BUBBLE UP
    while( from < child )
    {
      int parent = child+from-1 >>> 1;
      if( compare(child,parent) > 0 )
        swap(child,child=parent);
      else break;
    }
  }
}
