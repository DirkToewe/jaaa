package com.github.jaaa.sort;

public interface QuickSortAccess extends InsertionSortV2Access
{
  public int randInt( int from, int until );

  public default void quickSort( int from, int until )
  {
    if( until-from <= 32 )
      insertionSortV2(from,until);
    else {
      // move random element up front as pivot
      swap( from, randInt(from,until) );
      // partition using pivot, elements equal to pivot are kept in growing range [l,r)
      int l=from,
          r=from+1;
      for( int i=from; ++i < until; )
      {
        int il = compare(i,l);
        if( il <= 0 ) { swap(r,i);   // <- move elem. to mid
        if( il <  0 )   swap(r,l++); // <- move elem. to left
                               r++; }
      }
      // sort left an right of the pivots
      quickSort(from,l);
      quickSort(r,until);
    }
  }
}
