package com.github.jaaa.select;

import com.github.jaaa.ArgMaxAccess;
import com.github.jaaa.ArgMinAccess;
import com.github.jaaa.CompareSwapAccess;


public interface HeapSelectFastAccess extends CompareSwapAccess, ArgMaxAccess, ArgMinAccess
{
  default void heapSelectFastV1( int from, int mid, int until )
  {
    if( mid-from >= until-mid ) heapSelectFastL(from,mid,until);
    else                        heapSelectFastR(from,mid,until);
  }

  default void heapSelectFastV2( int from, int mid, int until )
  {
    if( mid-from <= until-mid ) heapSelectFastL(from,mid,until);
    else                        heapSelectFastR(from,mid,until);
  }

  default void heapSelectFastL( int from, int mid, int until )
  {
    if( from < 0 || from > mid || mid > until )
      throw new IllegalArgumentException();

    if( from == mid || mid == until ) return;
    if( from == mid-1  ) { swap(from, argMinL(from,until)); return; }
    if( mid == until-1 ) { swap(mid,  argMaxR(from,until)); return; }

    //   1) Build max heap in the left range
    //   2) For each element in the right range that
    //      is less than the top of the heap, swap
    //      it with the top of the heap. Reinstate
    //      heap property afterwards.
    //
    // The root of the heap is on the right side (mid-1),
    // such that only O(n) comparisons and 0 swaps are
    // required if input is already in ascending order.

    // Heap Building Phase
    // -------------------
    final int      firstParent = from+mid+1 >>> 1;
    for(  int root=firstParent; root < mid; root++ )
    {
      int parent=root;

      // TRICKLE DOWN
      for(;;) {
        int child = (parent<<1) - mid;
        if( child < from ) break;
        if( child > from )
            child -= compare(child,child-1)>>>31;
        swap(parent,parent=child);
      }

      // BUBBLE UP
      for( int child=parent; root != child; )
      {
        parent = child+mid+1 >>> 1;
        if( compare(child,parent) > 0 )
          swap(child,child=parent);
        else break;
      }
    }

    // Swap Phase
    // ----------
    final int root = mid-1;
    for( int i=mid; i < until; i++ )
    {
      if( compare(root,i) <= 0 )
        continue;
      swap(root,i);
      int parent=root;
      // TRICKLE DOWN
      for(;;) {
        int child = (parent<<1) - mid;
        if( child < from ) break;
        if( child > from )
            child -= compare(child,child-1)>>>31;
        swap(parent,parent=child);
      }

      // BUBBLE UP
      for( int child=parent; root != child; )
      {
        parent = child+mid+1 >>> 1;
        if( compare(child,parent) > 0 )
          swap(child,child=parent);
        else break;
      }
    }
  }

  default void heapSelectFastR( int from, int mid, int until )
  {
    if( from < 0 || from > mid || mid > until )
      throw new IllegalArgumentException();

    if( from == mid || mid == until ) return;
    if( from == mid-1 ) {
      swap(from, argMinL(from,until));
      return;
    }
    if( mid == until-1 ) {
      swap(mid, argMaxR(from,until));
      return;
    }

    // 1) Build min heap in the right range
    // 2) For each element in the left range that
    //    is greater than the top of the heap, swap
    //    it with the top of the heap. Reinstate
    //    heap property afterwards.
    //
    // The root of the heap is on the left side (mid-1),
    // such that only O(n) comparisons and 0 swaps are
    // required if input is already in ascending order.
    --mid;
    --until;
    // Heap Building Phase
    // -------------------
    final int      firstParent = mid+until >> 1;
    for(  int root=firstParent; root > mid; root-- )
    {
      int parent=root;

      // TRICKLE DOWN
      for(;;) {
        int child = (parent<<1) - mid;
        if( child > until ) break;
        if( child < until )
            child += compare(child+1,child)>>>31;
        swap(parent,parent=child);
      }

      // BUBBLE UP
      for( int child=parent; root != child; )
      {
        parent = child+mid >>> 1;
        if( compare(child,parent) < 0 )
          swap(child,child=parent);
        else break;
      }
    }

    // Swap Phase
    // ----------
    int root = mid+1;
    for( int i=mid; i >= from; i-- )
    {
      if( compare(i,root) <= 0 )
        continue;
      swap(i,root);
      int parent=root;
      // TRICKLE DOWN
      for(;;) {
        int child = (parent<<1) - mid;
        if( child > until ) break;
        if( child < until )
            child += compare(child+1,child)>>>31;
        swap(parent,parent=child);
      }
      // BUBBLE UP
      for( int child=parent; root != child; )
      {
        parent = child+mid >>> 1;
        if( compare(child,parent) < 0 )
          swap(child,child=parent);
        else break;
      }
    }
  }
}
