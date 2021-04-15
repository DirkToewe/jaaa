package com.github.jaaa.misc;

import com.github.jaaa.RandomAccessor;

public interface RevertAccessor<T> extends RandomAccessor<T>
{
  default void revert( T arr, int from, int until )
  {
    assert from >= 0;
    assert from <= until;
    for(;--until>from; ++from)
      swap(arr,from, arr,until);
  }
}
