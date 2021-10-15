package com.github.jaaa.sort;

import com.github.jaaa.CompareSwapAccess;
import com.github.jaaa.search.AkimboSearchAccess;
import com.github.jaaa.search.ExpR2LSearchAccess;

import static java.lang.Math.subtractExact;


public interface InsertionAkimboSortAccess extends AkimboSearchAccess, CompareSwapAccess
{
  default void insertionAkimboSort( int from, int until )
  {
    if( from > until )
      throw new IllegalArgumentException();
    for( int i=from; ++i < until; )
      for( int j = akimboSearchGapR(from,i,i), k = i; k > j; )
        swap(k,--k);
  }
}
