package com.github.jaaa.sort;

import com.github.jaaa.RandomAccessorArrObj;
import com.github.jaaa.misc.Revert;

import static java.lang.Math.min;
import static java.lang.System.arraycopy;


public interface TimSortAccessorArrObj<T> extends TimSortAccessor<T[]>,
                                             RandomAccessorArrObj<T>
{
  default int timSort_prepareNextRun(T[] arr, int from, int until, int minRunLen )
  {
    // TODO: improve adaptive inplace sorting of the runs
    int start  = from+1;
    if( start >= until ) return start;

    boolean ascending = compare(arr,start++, arr,from) >= 0;

    while( start < until && compare(arr,start, arr,start-1) >= 0 == ascending )
         ++start;

    if( ! ascending )
      Revert.revert(arr, from,start);

        until = min(from+minRunLen, until);
    if( until <= start) return start;
    for(;;)
    {
      int lo = from,
          hi = start;
      while( lo < hi ) {       int mid = lo+hi >>> 1;
        if( compare(arr,start, arr,mid) < 0 )
                              hi = mid;
        else                  lo = mid+1;
      }

      T piv = arr[start];
      arraycopy(arr,lo, arr,lo+1, start-lo);
      arr[lo] = piv;

      if( until <= ++start )
        return until;
    }
  }
}
