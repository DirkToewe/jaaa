package com.github.jaaa.permute;

/** A wrapper around an array-like object, supporting a single manipulation operations: swapping two elements.
 *  The interface is intended as generic access point for in-place sorting, merging, selection algorithms and
 *  the like.
 *
 *  @author Dirk Toewe
 */
public interface SwapAccess
{
  /** Swaps two elements of the wrapped, array-like object.
   *
   *  @param i Index of the first element.
   *  @param j Index of the second element.
   */
  void swap( int i, int j );
}
