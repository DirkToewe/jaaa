package com.github.jaaa.partition;

import com.github.jaaa.permute.BlockSwapAccess;

import static com.github.jaaa.util.IMath.sqrtFloor;
import static java.lang.Math.subtractExact;

// COMPLEXITY
// ----------
// Comparisons: O( (m+n)^(3/2) )
//       Swaps: O( m+n )
//      Memory: O(1)
//
// REFERENCES
// ----------
// .. [1] "STABLE IN SITU SORTING AND MINIMUM DATA MOVEMENT"
//         J. IAN MUNRO, VENKATESH RAMAN and JEFFREY S. SALOWE

public interface MuRaSaBiPartitionV1Access extends BlockSwapAccess,
        ExtractBiPartitionBufAccess
{
  private static int blockSizes( int len )
  {
    // The sequence is split into (n+2) blocks of size n, where the +2
    // blocks may be a little larger to make up the difference.
    int       n = sqrtFloor(1 + len) - 1;
    assert (2+n)*n     <= len;
    assert (3+n)*(1+n) >  len;
    return    n;
  }

  default int binarySearchCountA( int from, int until )
  {
    if( from > until ) throw new IllegalArgumentException();
    int off = from;
                 --until;
    while( from <= until )
    { int           mid = (int) ( (long) from+until >>> 1 );
      if( predicate(mid) ) until = mid-1;
      else                  from = mid+1;
    }
    int    result = from - off;
    return result;
  }

  default void muRaSaBiPartitionV1( int from, int until )
  {
    if( from > until ) throw new IllegalArgumentException();

    int              len = subtractExact(until,from),
      n = blockSizes(len);

    final int nL = (len - n*n) >>> 1,
              nR = (len - n*n) - nL;

    // buffer extraction
    if( extractBiPartitionBufB_R(from,until,nR) < nR ) return; until -= nR;
    if( extractBiPartitionBufA_L(from,until,nL) < nL ) return;

    if( from+nL == until ) return;

    // SORT BLOCKS INDIVIDUALLY
    // ------------------------
    // Perform a `PermSortStable` on each block, using nL and nR as memory bits.
    int nA = nL, // <- count number of A-elements
        nB = nR; // <- count number of B-elements
    boolean tru = true;
    for( int off = from+nL; off < until; off += n )
    {
      int mid = 0;
      for( int k=0; k < n; k++ )
        if( predicate(off+k) ) ++nB;
        else          { ++mid; ++nA; }

      for( int k=0; k < n; k++ )
        if( tru != predicate(from+k) ) // <- if the element is not yet in position
        {
          int i=k;
          do {
            // FIND POSITION OF 1st AMONG EQUALS
            boolean   pi = predicate(off+k);
            int iTo = pi ? mid : 0;

            // FIND POSITION OF CURRENT ELEMENT AMONG EQUALS NOT YET IN POSITION
            int pos = 0;
            for( int j=k; ++j < i; )
              if( tru != predicate(from+j) && predicate(off+j) == pi )
                ++pos;

            // FIND FINAL POSITION OF CURRENT ELEMENT
            for( ;; ++iTo )
              if( tru != predicate(from+iTo) && --pos < 0 )
                break;

            swap( off+iTo,  off + k );
            swap(from+iTo, until+iTo); // <- flip bit to mark that `iTo` is now in position
            i = iTo;
          }
          while( i != k );
        }

      tru = !tru;
    }

    // REGROUP ITEMS
    // -------------
    // Swap elements between blocks such there is at most one block that contains both A- and B-elements.
    int nPrev = 0;
    for( int off=from+nL; off < until; off += n )
    {
      int nCurr = binarySearchCountA(off,off+n);
      if( 0 != nPrev && nPrev != n )
      {
        if( nPrev + nCurr < n ) {
          // MERGE PREVIOUS BLOCKS A-ELEMENTS INTO CURRENT BLOCK
          // [ A | B ][ A | B ] -> [  A  ][ A | B ]
          rotate(off-n,    off,            -nPrev);
          rotate(off-nPrev,off+nPrev+nCurr,+nPrev);
          nCurr  +=  nPrev;
        }
        else {
          // FILL UP PREVIOUS BLOCK
          // [ A | B ][ A | B ] -> [  B  ][ A | B ]
          int        nFill = n-nPrev;
          rotate(off-nFill, off+nCurr, -nFill);
          nCurr  -=  nFill;
        }
      }
      nPrev = nCurr;
    }

    // REARRANGE BLOCKS
    // ----------------
    // Perform a `PermSortStable` on the blocks, comparing the first entries of each block.
    rearrange_blocks: {
      int mid = (nA-nL + n-1) / n;

      for( int k=0; k < n; k++ )
        if( tru != predicate(from+k) ) // <- if the element is not yet in position
        {
          int i=k;
          do {
            // FIND POSITION OF 1st AMONG EQUALS
            boolean   pi = predicate(from+nL + n*k);
            int iTo = pi ? mid : 0;

            // FIND POSITION OF CURRENT ELEMENT AMONG EQUALS NOT YET IN POSITION
            int pos = 0;
            for( int j=k; ++j < i; )
              if( tru != predicate(from+j) && predicate(from+nL + n*j) == pi )
                ++pos;

            // FIND FINAL POSITION OF CURRENT ELEMENT
            for( ;; ++iTo )
              if( tru != predicate(from+iTo) && --pos < 0 )
                break;

            blockSwap(
              from+nL + n*k,
              from+nL + n*iTo,
                        n
            );
            swap( // <- flip bit to mark that `iTo` is now in position
               from+iTo,
              until+iTo
            );
            i = iTo;
          }
          while( i != k );
        }

      tru = !tru;
    }

    if( !tru ) blockSwap(from,until, n);

    // B-elements of the mixed block belong all the way to the end
    if( 0 != nPrev && nPrev != n ) // <- unless there is no mixed block
      rotate( from+nA, until, -(n-nPrev) );
  }
}
