package com.github.jaaa.permute;


public interface RevertAccessor<T> extends SwapAccessor<T>
{
  default void revert( T arr, int from, int until )
  {
    assert from >= 0;
    assert from <= until;
    for(;--until>from; ++from)
      swap(arr,from, arr,until);
  }
}
