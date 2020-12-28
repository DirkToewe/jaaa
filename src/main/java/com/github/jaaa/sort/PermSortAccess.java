package com.github.jaaa.sort;

import com.github.jaaa.CompareSwapAccess;

// REFERENCES
// ----------
// .. [1] "STABLE IN SITU SORTING AND MINIMUM DATA MOVEMENT"
//         J. IAN MUNRO, VENKATESH RAMAN and JEFFREY S. SALOWE

public interface PermSortAccess extends CompareSwapAccess
{
  public default void permSort( int from, int until )
  {
    if( from > until )
      throw new IllegalArgumentException();

    // see `insitu_permsort` [1, Fig. 1b]
    outer_loop:for( int k=from; k < until; k++ )
      for(;;)
      {
        // find correct position of element i by counting elements less than it
        int l=k;
        for( int j=k; ++j < until; )
          if( compare(j,k) < 0 )
            ++l;

        // if element already in right place, continue with next i
        if( k == l )
          continue outer_loop;

        // avoid swapping with elements that are already in place
        while( compare(k,l) == 0 )
          ++l;

        swap(k,l);
      }
  }
}
