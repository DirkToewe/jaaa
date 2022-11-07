package com.github.jaaa.select;

import com.github.jaaa.ArgMaxAccess;
import com.github.jaaa.ArgMinAccess;
import com.github.jaaa.CompareSwapAccess;

import static com.github.jaaa.util.IMath.log2Ceil;
import static java.lang.Math.max;
import static java.lang.Math.min;


public interface HeapSelectV1V2Access extends CompareSwapAccess, ArgMaxAccess, ArgMinAccess
{
  default void heapSelectV1( int from, int mid, int until )
  {
    if( from < 0 || from > mid || mid > until ) throw new IllegalArgumentException();
    if( from == mid || mid == until ) return;

         if( from ==   mid-1 ) swap(from, argMinL(from,until));
    else if( mid  == until-1 ) swap(mid,  argMaxL(from,until));
    else if( mid-from >= until-mid ) _heapSelectV3V4_l(from,mid,until);
    else                             _heapSelectV3V4_r(from,mid,until);
  }

  default void heapSelectV2( int from, int mid, int until )
  {
    if( from < 0 || from > mid || mid > until ) throw new IllegalArgumentException();
    if( from == mid || mid == until ) return;

         if( from ==   mid-1 ) swap(from, argMinL(from,until));
    else if( mid  == until-1 ) swap(mid,  argMaxL(from,until));
    else if( mid-from <= until-mid ) _heapSelectV3V4_l(from,mid,until);
    else                             _heapSelectV3V4_r(from,mid,until);
  }

  /** Returns a rough estimate of the worst case performance of {@link #heapSelectV1(int,int,int)}.
   */
  default long heapSelectV1_worstCasePerformance( int from, int mid, int until )
  {
    if( from < 0 || from > mid || mid > until ) throw new IllegalArgumentException();
    long s,l; {
      int m =   mid-from,
          n = until-mid;
      s = min(m,n);
      l = max(m,n);
    }
    long heapify = l,
       selection = log2Ceil(l)*s;
    return (heapify + selection) << 1;
  }

  default void _heapSelectV3V4_l( int from, int mid, int until )
  {
    //   1) Build max heap in the left range
    //   2) For each element in the right range that
    //      is less than the top of the heap, swap
    //      it with the top of the heap. Reinstate
    //      heap property afterwards.
    //
    // The root of the heap is on the right side (mid-1),
    // such that only O(n) comparisons and 0 swaps are
    // required if input is already in ascending order.
    final int                    firstParent = from+mid+1 >>> 1;
    heap_building: for( int root=firstParent, i = mid-1;; )
    {
      // SIFT DOWN
      for( int parent=root;; )
      {
        int child = (parent<<1) - mid;
        if( child < from ) break;
        if( child > from )
            child -= compare(child,child-1)>>>31;
        if( compare(parent,child) < 0 )
          swap(parent,parent=child);
        else break;
      }

      int r = root+1;
      if( r < mid )
        root = r;    // <- HEAP BUILDING PHASE
      else for(;;) // <- SELECTION PHASE
        if( ++i >= until ) return;
        else if( compare(root,i) > 0 ) {
                    swap(root,i);
          continue heap_building;
        }
    }
  }

  default void _heapSelectV3V4_r( int from, int mid, int until )
  {
    // 1) Build min heap in the right range
    // 2) For each element in the left range that
    //    is greater than the top of the heap, swap
    //    it with the top of the heap. Reinstate
    //    heap property afterwards.
    //
    // The root of the heap is on the left side (mid-1),
    // such that only O(n) comparisons and 0 swaps are
    // required if input is already in ascending order.
    int i=mid--;
        until--;
    final int                    firstParent = mid+until >> 1;
    heap_building: for( int root=firstParent;; )
    {
      // SIFT DOWN
      for( int parent=root;; )
      {
        int child = (parent<<1) - mid;
        if( child > until ) break;
        if( child < until )
            child += compare(child+1,child)>>>31;
        if( compare(parent,child) > 0 )
          swap(parent,parent=child);
        else break;
      }

      int r = root-1;
      if( r > mid )
        root = r; // <- HEAP BUILDING PHASE
      else for(;;) // <- SELECTION PHASE
        if( --i < from ) return;
        else if( compare(i,root) > 0 ) {
                    swap(i,root);
          continue heap_building;
        }
    }
  }
}
