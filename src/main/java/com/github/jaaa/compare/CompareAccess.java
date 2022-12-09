package com.github.jaaa.compare;

/** A wrapper around an array-like object, supporting a single access operations: comparing two elements.
 *  The interface is intended as generic access point for in-place algorithms.
 *
 * @author Dirk Toewe
 */
public interface CompareAccess
{
  /** Swaps two elements of the wrapped, array-like object.
   *
   *  @param i Index of the first element.
   *  @param j Index of the second element.
   *  @return A negative value if element i is less than element j, a positive value if element i is greater
   *          than element j, and <c>0</c> if both element are equal (under the given order).
   */
  public int compare( int i, int j );
}
