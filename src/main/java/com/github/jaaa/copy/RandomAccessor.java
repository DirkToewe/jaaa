package com.github.jaaa.copy;


import com.github.jaaa.permute.SwapAccessor;

/** Instances of this class allow access to array-like objects of generic type
 *
 *  @param <T>
 */
public interface RandomAccessor<T> extends CopyAccessor<T>, SwapAccessor<T>
{
  T malloc( int len );
}
