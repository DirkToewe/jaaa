package com.github.jaaa.sort;

import com.github.jaaa.CompareRandomAccessor;
import com.github.jaaa.merge.TapeMergeAccessor;

import static java.lang.Integer.numberOfTrailingZeros;


public interface MergeSortAccessor<T> extends CompareRandomAccessor<T>,
        InsertionSortAccessor<T>,
                                                  TapeMergeAccessor<T>
{
  default int mergeSort_runLen( int n )
  {
    assert 0 <= n;
    int            MIN_RUN_LEN=16,s = 0;
    if( n>>> 16 >= MIN_RUN_LEN )  s =16;
    if( n>>>s+8 >= MIN_RUN_LEN )  s+= 8;
    if( n>>>s+4 >= MIN_RUN_LEN )  s+= 4;
    if( n>>>s+2 >= MIN_RUN_LEN )  s+= 2;
    if( n>>>s+1 >= MIN_RUN_LEN )  s+= 1;
    int    l = n>>>s;
    return l<<s != n ? l+1 : l;
  }

  default void mergeSort_mergeR2L( T ac, int a0, int aLen,
                                   T b,  int b0, int bLen,
                                         int c0 )
  {
    tapeMergeR2L(ac,a0,aLen, b,b0,bLen, ac,c0);
  }

  default void mergeSort_sortRun( T a, int from, int until )
  {
    insertionSort(a,from,until);
  }

  default void mergeSort( T arr, int arr0, int arr1, T buf, int buf0, int buf1 )
  {
    if( arr0 < 0    ) throw new IndexOutOfBoundsException();
    if( arr0 > arr1 ) throw new IndexOutOfBoundsException();
    if( buf0 < 0    ) throw new IndexOutOfBoundsException();
    if( buf0 > buf1 ) throw new IndexOutOfBoundsException();

    int bufLen = buf1 - buf0,
        arrLen = arr1 - arr0;
    if( arrLen>>>1 > bufLen ) {
      buf = malloc(arrLen>>>1);
      buf0 = 0;
    }

    final int RUN_LEN = mergeSort_runLen(arr1-arr0);

    int lenR=0,
       stack=0;

    for( int i=arr0; i < arr1; )
    {
      int nxt = i+RUN_LEN;
      if( nxt > arr1 || nxt < 0 )
          nxt = arr1;

      mergeSort_sortRun(arr, i,nxt);

      // merge runs of equal length
      lenR = nxt-i;
      for( int s=++stack, lenL=RUN_LEN; (s & 1) == 0;  s>>>=1, lenL<<=1 )
      {
        copyRange(arr,i,buf,buf0, lenR);
                               i-=lenL;
        mergeSort_mergeR2L(arr,i, lenL, buf,buf0,lenR, i);
        lenR += lenL;
      }

      i = nxt;
    }

    int n0 = numberOfTrailingZeros(stack);
    stack >>>= n0;

    // merge rest
    int               lenL = RUN_LEN << n0;
    for( int mid=arr1-lenR;; )
    {
      stack >>>= 1; if( 0 == stack   ) break;
      lenL   <<= 1; if( 0 ==(stack&1)) continue;

      copyRange(arr,mid,buf,buf0, lenR);
                             mid-=lenL;
      mergeSort_mergeR2L(arr,mid, lenL, buf,buf0,lenR, mid);
      lenR += lenL;
    }
  }
}