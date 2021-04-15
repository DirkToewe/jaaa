package com.github.jaaa;


/** Instances of this class allow access to array-like objects of generic type
 *
 *  @param <T>
 */
public interface RandomAccessor<T> extends CopyAccessor<T>
{
  T malloc( int len );
  void swap( T a, int i, T b, int j );
}
