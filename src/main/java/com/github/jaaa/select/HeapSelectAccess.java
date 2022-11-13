package com.github.jaaa.select;

import com.github.jaaa.ArgMaxAccess;
import com.github.jaaa.ArgMinAccess;
import com.github.jaaa.CompareSwapAccess;

import static com.github.jaaa.select.HeapSelect.performance_average;
import static java.lang.Math.min;


/**
 * A selection algorithm strongly inspired by heap sort.
 * Builds a binary heap on one side of the selection and
 * uses it to compare it to the elements of the other side,
 * swapping and enqueueing elements into the heap whenever
 * necessary to instate the selection property.
 *
 * There are different strategies for deciding on which side
 * the heap is built:
 *
 * <dl>
 *   <dt>heapSelectL</dt><dd>Always builds the heap on the left-hand side.</dd>
 *   <dt>heapSelectR</dt><dd>Always builds the heap on the right-hand side.</dd>
 *   <dt>heapSelectMajor</dt><dd>Always builds the heap on the larger side.</dd>
 *   <dt>heapSelectMinor</dt><dd>Always builds the heap on the smaller side.</dd>
 *   <dt>heapSelect</dt><dd>Chooses the side which is optimal for randomly shuffled inputs.</dd>
 * </dl>
 *
 *
 */
public interface HeapSelectAccess extends CompareSwapAccess, ArgMaxAccess, ArgMinAccess
{
  default void heapSelect( int from, int mid, int until )
  {
    if( from < 0 || from > mid || mid > until )
      throw new IllegalArgumentException();
    int m =   mid - from,
        n = until - mid;
    if( 0 < n && performance_average(m+1,n-1) < performance_average(n,m) )
      heapSelectL(from,mid,until);
    else
      heapSelectR(from,mid,until);
  }


  default void heapSelectMajor( int from, int mid, int until )
  {
    if( mid-from > until-mid-2 ) heapSelectL(from,mid,until);
    else                         heapSelectR(from,mid,until);
  }


  default void heapSelectMinor( int from, int mid, int until )
  {
    if( mid-from < until-mid-2 ) heapSelectL(from,mid,until);
    else                         heapSelectR(from,mid,until);
  }


  /**
   * Returns the estimated number of comparisons required by
   * `heapSelect` in order to select from a randomly shuffled
   * input of the given dimensions.
   *
   * @param from  Start (inclusive) of the selection range.
   * @param mid   Index of the selected element.
   * @param until End (exclusive) fo the selection range.
   * @return Expected number of comparisons required by
   *         `heapSelect(from,mid,until` given a randomly
   *         shuffled input.
   */
  default long heapSelect_performance( int from, int mid, int until ) {
    if( from < 0 || from > mid || mid > until )
      throw new IllegalArgumentException();
    if( mid == until )
      return 0;
    int m =   mid - from,
        n = until - mid;
    var p = performance_average(m+1,n-1);
    var q = performance_average(n,m);
    return min(p,q);
  }


  default long heapSelectMajor_performance( int from, int mid, int until ) {
    if( from < 0 || from > mid || mid > until )
      throw new IllegalArgumentException();
    if( mid == until )
      return 0;
    int m =   mid - from,
        n = until - mid;
    return m > n-2
      ? performance_average(m+1,n-1)
      : performance_average(n,m);
  }


  default void heapSelectL( int from, int mid, int until )
  {
    if( from < 0 || from > mid || mid > until )
      throw new IllegalArgumentException();

    if( mid == until ) return;
    if( mid == from    ) { swap(from, argMinL(from,until)); return; }
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
    mid++;
    final int      firstParent = from+mid+1 >>> 1;
    for(  int root=firstParent; root < mid; root++ )
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
    }

    // Swap Phase
    // ----------
    final int root = mid-1;
    for( int i=mid; i < until; i++ )
    {
      if( compare(root,i) <= 0 )
        continue;
      swap(root,i);
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
    }
  }


  default void heapSelectR( int from, int mid, int until )
  {
    if( from < 0 || from > mid || mid > until )
      throw new IllegalArgumentException();

    if( mid == until ) return;
    if( mid == from    ) { swap(from, argMinL(from,until)); return; }
    if( mid == until-1 ) { swap(mid,  argMaxR(from,until)); return; }

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
    }

    // Swap Phase
    // ----------
    int root = mid+1;
    for( int i=mid; i >= from; i-- )
    {
      if( compare(i,root) <= 0 )
        continue;
      swap(i,root);
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
    }
  }
}
