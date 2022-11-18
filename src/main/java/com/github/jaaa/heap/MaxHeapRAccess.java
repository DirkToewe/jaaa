package com.github.jaaa.heap;

import com.github.jaaa.CompareSwapAccess;


public interface MaxHeapRAccess extends CompareSwapAccess
{
  default void maxHeapR_build( int from, int until )
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
            child -= compare(child,child-1)>>>31;
        if( compare(parent,child) < 0 )
          swap(parent,parent=child);
        else break;
      }
  }

  default void maxHeapR_buildFast( int from, int until )
  {
    // REFERENCES
    // ----------
    // .. [1] "Building Heaps Fast"
    //         C. J. H. McDiarmid, B. A. Reed
    //         JOURNAL OF ALGORITHMS 10, 352-365 (1989)
    if( from < 0 || from > until ) throw new IllegalArgumentException();

    final int     firstParent = from+until+1 >>> 1;
    for( int root=firstParent; root < until; root++ )
    {
      int parent=root;

      // TRICKLE DOWN
      for(;;) {
        int child = (parent<<1) - until;
        if( child < from ) break;
        if( child > from )
            child -= compare(child,child-1)>>>31;
        swap(parent,parent=child);
      }

      // BUBBLE UP
      for( int child=parent; root != child; )
      {
        parent = child+until+1 >>> 1;
        if( compare(child,parent) > 0 )
          swap(child,child=parent);
        else break;
      }
    }
  }

  default int maxHeapR_child( int from, int until, int parent )
  {
    if( from < 0 || from > parent || parent >= until )
      throw new IllegalArgumentException();
    return (parent<<1) - until;
  }

  default int maxHeapR_parent( int from, int until, int child )
  {
    if( from < 0 || from > child || child >= until )
      throw new IllegalArgumentException();
    return child+until+1 >>> 1;
  }

  default void maxHeapR_siftDown( int from, int until, int parent )
  {
    if( from < 0 || from > parent || parent >= until )
      throw new IllegalArgumentException();
    for(;;) {
      // SIFT DOWN
      int child = (parent<<1) - until;
      if( child < from ) break;
      if( child > from )
          child -= compare(child,child-1)>>>31;
      if( compare(parent,child) < 0 )
        swap(parent,parent=child);
      else break;
    }
  }

  default void maxHeapR_siftDownFast( int from, int until, int parent )
  {
    if( from < 0 || from > parent || parent >= until )
      throw new IllegalArgumentException();
    int p = parent;
    // TRICKLE DOWN
    for(;;) {
      int child = (p<<1) - until;
      if( child < from ) break;
      if( child > from )
          child -= compare(child,child-1)>>>31;
      swap(p,p=child);
    }
    // BUBBLE UP
    for( int child=p; parent != child; )
    {
      p = child+until+1 >>> 1;
      if( compare(child,p) > 0 )
        swap(child,child=p);
      else break;
    }
  }

  default void maxHeapR_bubbleUp( int from, int until, int child )
  {
    if( from < 0 || from > child || child >= until )
      throw new IllegalArgumentException();
    // BUBBLE UP
    while( child < until-1 )
    {
      int parent = child+until+1 >>> 1;
      if( compare(child,parent) > 0 )
        swap(child,child=parent);
      else break;
    }
  }
}
