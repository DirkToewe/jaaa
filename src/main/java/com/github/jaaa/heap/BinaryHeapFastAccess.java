package com.github.jaaa.heap;

import com.github.jaaa.CompareSwapAccess;


// REFERENCES
// ----------
// .. [1] "Building Heaps Fast"
//         C. J. H. McDiarmid, B. A. Reed
//         JOURNAL OF ALGORITHMS 10, 352-365 (1989)
public interface BinaryHeapFastAccess extends CompareSwapAccess
{
  default void binHeapMinR_buildFast( int from, int until )
  {
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
    }
  }
}
