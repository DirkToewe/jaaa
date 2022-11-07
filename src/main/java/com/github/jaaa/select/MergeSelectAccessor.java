package com.github.jaaa.select;

import com.github.jaaa.ArgMaxAccessor;
import com.github.jaaa.ArgMinAccessor;
import com.github.jaaa.merge.ExpMergeOffsetAccessor;
import com.github.jaaa.merge.TapeMergeAccessor;
import com.github.jaaa.sort.InsertionSortAccessor;
import com.github.jaaa.sort.TimSort;

import static java.lang.Integer.highestOneBit;
import static java.lang.Integer.numberOfTrailingZeros;
import static java.lang.Math.min;


public interface MergeSelectAccessor<T> extends ArgMaxAccessor<T>,
                                                ArgMinAccessor<T>,
                                        ExpMergeOffsetAccessor<T>,
                                         InsertionSortAccessor<T>,
                                             TapeMergeAccessor<T>
{
  default void mergeSelect_sortRun( T a, int from, int until ) { insertionSort(a,from,until); }
  default  int mergeSelect_runLen( int len, int nSelect ) {
    if( nSelect < 0 || nSelect > len )
      throw new IllegalArgumentException();
    int runLen = TimSort.optimalRunLength(16, len);
    return min(runLen, nSelect);
  }

  default int mergeSelect_mergeOffset( T ab, int a0, int aLen, int b0, int bLen, int nSkip ) {
    return expMergeOffset(ab,a0,aLen, ab,b0,bLen, nSkip);
  }

  default void mergeSelect_mergeL2R(
    T a, int a0, int aLen,
    T b, int b0, int bLen,
    T c, int c0
  ) {
    tapeMergeL2R(a,a0,aLen, b,b0,bLen, c,c0);
  }

  default void mergeSelect_mergeR2L(
    T a, int a0, int aLen,
    T b, int b0, int bLen,
    T c, int c0
  ) {
    tapeMergeR2L(a,a0,aLen, b,b0,bLen, c,c0);
  }

  default void mergeSelect( T arr, int from, int mid, int until, T buf, int buf0, int buf1 ) {
    if( mid-from <= until-mid )
      mergeSelectL(arr, from,mid,until, buf,buf0,buf1);
    else
      mergeSelectR(arr, from,mid,until, buf,buf0,buf1);
  }

  default void mergeSelectL( T arr, int from, int mid, int until, T buff, int buff0, int buff1 )
  {
    if( from < 0 || from > mid || mid > until || buff0 < 0 || buff0 > buff1 )
      throw new IndexOutOfBoundsException();

    if( from >= mid-1 ) {
      if( from < mid )
        for( int i = argMinL(arr,from,until); from < i; )
          swap(arr,i, arr,--i);
      return;
    }

    final int N_SELECT = mid - from;

    new Object() {
      T   buf    = buff;
      int buf0   = buff0,
          bufLen = buff1 - buff0;

      void merge( int j, int lenL, int lenR ) {
        assert lenL >= lenR;
        int   i = j - lenL,
        nSelect = min(lenL+lenR, N_SELECT),
             nL = mergeSelect_mergeOffset(arr, i,lenL, j,lenR, nSelect),
             nR = nSelect - nL;
        // ensure buffer capacity
        if( 0 < nR ) {
          if( bufLen < nR ) {
              bufLen = highestOneBit(nR*2 - 1); // <- next power of 2
              if( bufLen > N_SELECT || bufLen < 0 ) // <- (bufLen < 0) to prevent overflow
                  bufLen = N_SELECT;
              buf = malloc(bufLen);
              buf0 = 0;
          }
          copyRange(arr,j, buf,buf0, nR);
          int n = min(lenL-nL, nR);
          copyRange(arr,i+nL, arr,j+nR-n, n);
          mergeSelect_mergeR2L(arr,i,nL, buf,buf0,nR, arr,i);
        }
      }

      {
        final int RUN_LEN = mergeSelect_runLen(until-from, N_SELECT);

        int lenR=0,
           stack=0;

        for( int i=from; i < until; ) {
          int nxt = i + RUN_LEN;
          if( nxt > until || nxt < 0 ) // <- (nxt < 0) to prevent overflow
              nxt = until;

          mergeSelect_sortRun(arr, i,nxt);

          // merge runs of equal length
          lenR = nxt-i;
          for( int s=++stack, lenL = RUN_LEN; (s & 1) == 0;  s>>>=1, lenL<<=1 ) {
            merge(i,lenL,lenR);
            i    -= lenL;
            lenR += lenL;
          }

          i = nxt;
        }

        int n0 = numberOfTrailingZeros(stack);
        stack >>>= n0;

        // merge rest
        int              lenL = RUN_LEN << n0;
        for( int i=until-lenR;; ) {
          stack >>>= 1; if( 0 == stack   ) break;
          lenL   <<= 1; if( 0 ==(stack&1)) continue;
          merge(i,lenL,lenR);
          i    -= lenL;
          lenR += lenL;
        }
      }
    };
  }

  default void mergeSelectR( T arr, int from, int mid, int until, T buff, int buff0, int buff1 )
  {
    if( from < 0 || from > mid || mid > until || buff0 < 0 || buff0 > buff1 )
      throw new IndexOutOfBoundsException();

    if( mid >= until-1 ) {
      if( mid < until )
        for( int i = argMaxR(arr,from,until); i < until-1; )
          swap(arr,i, arr,++i);
      return;
    }

    final int N_SELECT = until - mid;

    new Object() {
      T   buf    = buff;
      int buf0   = buff0,
          bufLen = buff1 - buff0;

      void merge( int j, int lenL, int lenR ) {
        assert lenL <= lenR;
        int len = lenL + lenR,
              i = j - lenL,
        nSelect = min(len, N_SELECT),
          skip  = len - nSelect,
          skipL = mergeSelect_mergeOffset(arr, i,lenL, j,lenR, skip),
          skipR = skip - skipL,
             nL = lenL - skipL,
             nR = lenR - skipR;
        if( 0 < nL ) {
          // ensure buffer capacity
          if( bufLen < nL ) {
              bufLen = highestOneBit(nL*2 - 1); // <- next power of 2
              if( bufLen > N_SELECT || bufLen < 0 ) // <- (bufLen < 0) to prevent overflow
                  bufLen = N_SELECT;
              buf = malloc(bufLen);
              buf0 = 0;
          }
          copyRange(arr,i+skipL, buf,buf0, nL);
          int n = min(lenR-nR, nL);
          copyRange(arr,j+skipR-n, arr,i+skipL, n);
          mergeSelect_mergeL2R(buf,buf0,nL, arr,j+skipR,nR, arr,i+skip);
        }
      }

      {
        final int RUN_LEN = mergeSelect_runLen(until-from, N_SELECT);

        int lenL=0,
           stack=0;

        for( int i=until; i > from; ) {
          int nxt = i - RUN_LEN;
          if( nxt < from )
            nxt = from;

          mergeSelect_sortRun(arr,nxt,i);

          // merge runs of equal length
          lenL = i-nxt;
          for( int s=++stack, lenR = RUN_LEN; (s & 1) == 0;  s>>>=1, lenR<<=1 ) {
            merge(i,lenL,lenR);
            i    += lenR;
            lenL += lenR;
          }

          i = nxt;
        }

        int n0 = numberOfTrailingZeros(stack);
        stack >>>= n0;

        // merge rest
        int             lenR = RUN_LEN << n0;
        for( int i=from+lenL;; ) {
          stack >>>= 1; if( 0 == stack   ) break;
          lenR   <<= 1; if( 0 ==(stack&1)) continue;
          merge(i,lenL,lenR);
          i    += lenR;
          lenL += lenR;
        }
      }
    };
  }
}
