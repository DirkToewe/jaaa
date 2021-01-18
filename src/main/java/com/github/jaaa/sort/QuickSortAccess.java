package com.github.jaaa.sort;

import com.github.jaaa.util.IntBiConsumer;

import java.util.function.IntBinaryOperator;

public interface QuickSortAccess extends InsertionAdaptiveSortAccess
{
  IntBinaryOperator newRNG();

  default void quickSort( int from, int until )
  {
    var rng = newRNG();
    new IntBiConsumer()
    {
      @Override public void accept( int from, int until )
      {
        if( until-from <= 32 )
          insertionAdaptiveSort(from,until);
        else {
          // move random element up front as pivot
          swap( from, rng.applyAsInt(from,until) );
          // partition using pivot, elements equal to pivot are kept in growing range [l,r)
          int      l=from,
                   r=from+1;
          for( int i=from; ++i < until; )
          {
            int il = compare(i,l);
            if( il <= 0 ) { swap(r,i);   // <- move elem. to mid
            if( il <  0 )   swap(r,l++); // <- move elem. to left
                                   r++; }
          }
          // sort left an right of the pivots
          accept(from,l);
          accept(r,until);
        }
      }
    }.accept(from,until);
  }
}
