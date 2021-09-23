package com.github.jaaa.sort;

import com.github.jaaa.CompareSwapAccess;

public interface HeapSortAccess extends CompareSwapAccess
{
  default void heapSort( int from, int until )
  {
    if( from < 0     ) throw new IllegalArgumentException();
    if( from > until ) throw new IllegalArgumentException();

    new Object() {
      int end = until; // <- end of remaining heap

      int parent( int  child ) { return from + (child-from-1) / 2; }
      int  child( int parent ) { return 1 + 2*parent - from; }

      void siftDown( int p ) {
        for(;;) {
          int c = child(p);
          if( c < end-1 ) {
            if( compare(c,c+1) <= 0 ) ++c;
            if( compare(p,c)   <  0 ) { swap(p,p=c); continue; }
            return;
          }
          if( c < end && compare(p,c) < 0 ) swap(p,c);
          return;
        }
      }

      {
        // HEAPIFY (build max-heap)
        for( int i=parent(end-1); i >= from; i-- )
          siftDown(i);

        // EXTRACT MAXIMA
        while( --end > from ) {
          swap(from,end);
          siftDown(from); // <- reinstate heap property
        }
      }
    };
  }
}
