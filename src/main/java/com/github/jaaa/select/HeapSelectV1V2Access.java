package com.github.jaaa.select;

import com.github.jaaa.ArgMaxAccess;
import com.github.jaaa.ArgMinAccess;
import com.github.jaaa.CompareSwapAccess;


public interface HeapSelectV1V2Access extends CompareSwapAccess, ArgMaxAccess, ArgMinAccess
{
  default void heapSelectV1( int from, int mid, int until )
  {
    if( from < 0 || from > mid || mid > until ) throw new IllegalArgumentException();
    if( from == mid || mid == until ) return;

         if( from ==   mid-1 ) swap(from, argMinL(from,until));
    else if( mid  == until-1 ) swap(mid,  argMaxL(from,until));
    else if( mid-from >= until-mid ) heapSelectV1V2_l(from,mid,until);
    else                             heapSelectV1V2_r(from,mid,until);
  }

  default void heapSelectV2( int from, int mid, int until )
  {
    if( from < 0 || from > mid || mid > until ) throw new IllegalArgumentException();
    if( from == mid || mid == until ) return;

         if( from ==   mid-1 ) swap(from, argMinL(from,until));
    else if( mid  == until-1 ) swap(mid,  argMaxL(from,until));
    else if( mid-from <= until-mid ) heapSelectV1V2_l(from,mid,until);
    else                             heapSelectV1V2_r(from,mid,until);
  }

  private void heapSelectV1V2_l( int from, int mid, int until )
  {
    //   1) Build max heap in the left range
    //   2) For each element in the right range that
    //      is less than the top of the heap, swap
    //      it with the top of the heap. Reinstate
    //      heap property afterwards.
    final int                    lastParent = -1 + (from+mid >>> 1);
    heap_building: for( int root=lastParent, i = --mid;; )
    {
      // SIFT DOWN
      for( int parent=root;; )
      {
        int child = (parent<<1) + 1 - from;
        if( child > mid ) break;
        if( child < mid )
            child += compare(child,child+1)>>>31;
        if( compare(parent,child) < 0 )
               swap(parent,parent=child);
        else break;
      }

      if( root > from )
        --root;    // <- HEAP BUILDING PHASE
      else for(;;) // <- SELECTION PHASE
        if( ++i >= until ) return;
        else if( compare(root,i) > 0 ) {
                    swap(root,i);
          continue heap_building;
        }
    }
  }

  private void heapSelectV1V2_r( int from, int mid, int until )
  {
    // 1) Build min heap in the right range
    // 2) For each element in the left range that
    //    is greater than the top of the heap, swap
    //    it with the top of the heap. Reinstate
    //    heap property afterwards.
    final int                    firstParent = mid+until+1 >>> 1;
    heap_building: for( int root=firstParent, i=mid;; )
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
        ++root; // <- HEAP BUILDING PHASE
      else for(;;) // <- SELECTION PHASE
        if( --i < from ) return;
        else if( compare(i,root) > 0 ) {
                    swap(i,root);
          continue heap_building;
        }
    }
  }
}
