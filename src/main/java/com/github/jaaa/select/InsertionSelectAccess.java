package com.github.jaaa.select;

import com.github.jaaa.CompareSwapAccess;


public interface InsertionSelectAccess extends CompareSwapAccess
{
  default void insertionSelect( int from, int mid, int until ) {
    if( mid-from <= until-mid )
      insertionSelectL(from,mid,until);
    else
      insertionSelectR(from,mid,until);
  }

  default void insertionSelectL( int from, int mid, int until )
  {
    if( from < 0 || from > mid || mid > until )
      throw new IllegalArgumentException();

    if( from == mid ) return;

    int      i = from;
    while( ++i < mid )
    {
           int lo = from;
      for( int hi = i;; )
      {
        int               j = lo+hi >>> 1;
        int c = compare(i,j);
        if( c < 0 )  hi = j;
        else         lo = j+1;

        if( lo >= hi ) break;
      }

      for( int k=i; k > lo; )
        swap(k,--k);
    }

    for(; i < until; i++)
    {
           int lo = from;
      for( int hi = mid;; )
      {
        int               j = lo+hi >>> 1;
        int c = compare(i,j);
        if( c < 0 )  hi = j;
        else         lo = j+1;

        if( lo >= hi ) break;
      }

      for( int j=i, k=mid-1; lo <= k; j = k-- )
        swap(j,k);
    }
  }

  default void insertionSelectR( int from, int mid, int until )
  {
    if( from < 0 || from > mid || mid > until )
      throw new IllegalArgumentException();

    if( mid == until ) return;

    int    i = until-1;
    while( i > mid )
    {
           int lo = i--;
      for( int hi = until;; )
      {
        int               j = lo+hi >>> 1;
        int c = compare(i,j);
        if( c < 0 )  hi = j;
        else         lo = j+1;

        if( lo >= hi ) break;
      }

      for( int k=i; k < lo-1; )
        swap(k,++k);
    }

    while( i-- > from )
    {
           int lo = mid;
      for( int hi = until;; )
      {
        int               j = lo+hi >>> 1;
        int c = compare(i,j);
        if( c < 0 )  hi = j;
        else         lo = j+1;

        if( lo >= hi ) break;
      }

      for( int j=i, k=mid; k < lo; j = k++ )
        swap(j,k);
    }

//    for(; i < until; i++)
//    {
//           int lo = mid;
//      for( int hi = until;; )
//      {
//        int               j = lo+hi >>> 1;
//        int c = compare(i,j);
//        if( c < 0 )  hi = j;
//        else         lo = j+1;
//
//        if( lo >= hi ) break;
//      }
//
//      for( int k=i; k < lo-1; )
//        swap(k,++k);
//
//      if( mid < lo ) {
//        swap(i,mid);
//        for( int k=mid; k < lo-1; )
//          swap(k,++k);
//      }
//    }
  }
}
