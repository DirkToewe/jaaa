package com.github.jaaa.partition;

import com.github.jaaa.permute.BlockSwapAccess;

import static com.github.jaaa.util.IMath.log2Ceil;
import static com.github.jaaa.util.IMath.sqrtFloor;
import static java.lang.Math.*;

// COMPLEXITY
// ----------
// Comparisons: O( (m+n)*log(m+n) )
//       Swaps: O( m+n )
//      Memory: O(1)
//
// REFERENCES
// ----------
// .. [1] "Stable In Situ Sorting and Minimum Data Movement"
//         J. Ian Munro, Venkatesh Raman and Jeffrey S. Salowe

public interface MuRaSaBiPartitionV2Access extends BlockSwapAccess,
        ExtractBiPartitionBufAccess
{
  private static int blockSizes( int len )
  {
    // BLOCK SIZES
    // -----------
    // As so many other in-place algorithms, the idea is to break
    // the array down into blocks of sqrt(len). And as so many other
    // in-place algorithms, additional parts of the array are set
    // aside as bits of information. These bits are used to apply
    // the `PermPartitionStable` algorithm on the other sets of bits.
    // Said algorithm requires two buffer of O(n) bits each:
    //
    //   1) FIN-buffer: n bits are required to memoize which entries
    //      have already been moved into their sorted position.
    //   2) LG2-buffer: ~n bits are required to memorize, for every
    //      ⎣log2(n)⎦-th index, how many A-elements are intially left
    //      of that index.
    int n = sqrtFloor(4 + len) - 2, // <- find largest n such that: `(4+n)*n = len`
        m = n+1,  lg2 = m<2 ? 1 : log2Ceil(m),
        s = n/lg2*lg2;
    return 2*s + (2+m)*m <= len ? m : n;
  }

  boolean predicate( int i );

  default void muRaSaBiPartitionV2( int from, int until )
  {
    if( from >  until ) throw new IllegalArgumentException();

    int  len = subtractExact(until,from),
               n = blockSizes(len),
      lg2    = n<2 ? 1 : log2Ceil(n),
      lg2len =(n-1)/lg2 * lg2; // <- FIXME

    final int nL = (len - n*n) >>> 1,
              nR = (len - n*n) - nL;

    // buffer extraction
    if( extractBiPartitionBufB_R(from,until,nR) < nR ) return; until -= nR;
    if( extractBiPartitionBufA_L(from,until,nL) < nL ) return;

    // starting of positions of the left and right sides of the two bit buffers.
    int finL = from,  lg2L = finL + n,
        finR = until, lg2R = finR + n;

    from += nL;

    if( from == until ) return;

    // SORT & REARRANGE BLOCK BY BLOCK
    // -------------------------------
    // Perform a `PermSortStable` on each block, using nL and nR as memory bits.
    int    nA = nL,
           nB = nR,
      nA_prev = 0; // <- number of A-elements in previous block
    boolean tru = true;
    for( int off=from; off < until; off += n )
    {
      // INIT LG2-BUFFER
      // ---------------
      int nA_curr=0; // number of A-elements in current block
      for( int i=0; i < n; i++ )
      {
        if( 0 < i && i%lg2 == 0 )
          for( int j=0; j < lg2; j++ )
          {
            int    k = (i/lg2 - 1)*lg2 + j;
            assert k >= 0;
            assert k < lg2len;
            if( (nA_curr>>>j & 1)==1 != predicate(lg2L+k) )
              swap(lg2L+k, lg2R+k );
          }
        if( predicate(off+i) ) ++nB;
        else      { ++nA_curr; ++nA; }
      }

      // SORT BLOCK
      // ----------
      for( int k=0; k < n; k++ )
        if( tru != predicate(finL+k) ) // <- if the element is not yet in position
        {
          int i=k;
          do {
            // FIND POSITION AMONG UNFINISHED ELEMENTS
            boolean pi = predicate(off+k);
            int s = i/lg2*lg2,
              iTo = 0;

            if( 0 < s ) {
              for( int j=lg2; j-- > 0; )
                if( predicate(lg2L + s-lg2 + j) )
                     iTo+= 1<<j;
              if(pi) iTo = s - iTo;
            } if(pi) iTo+= nA_curr;

            int pos = 0;
            for( int j=max(k+1, s); j < i; j++ )
              if( tru != predicate(finL+j) && predicate(off+j) == pi )
                ++pos;

            // FIND FINAL POSITION
            for( ;; ++iTo )
              if( tru != predicate(finL+iTo) && --pos < 0 )
                break;

            swap( off+iTo,  off+k  );
            swap(finL+iTo, finR+iTo); // <- flip bit to mark that `iTo` is now in position
            i = iTo;
          }
          while( i != k );
        }

      tru = !tru;

      // REGROUP BLOCK
      // -------------
      if( 0 != nA_prev && nA_prev != n )
      {
        if( nA_prev + nA_curr < n ) {
          // MERGE PREVIOUS BLOCKS A-ELEMENTS INTO CURRENT BLOCK
          rotate(off-n,      off,                -nA_prev);
          rotate(off-nA_prev,off+nA_prev+nA_curr,+nA_prev);
          nA_curr += nA_prev;
        }
        else {
          // FILL UP PREVIOUS BLOCK
          int        nFill =  n-nA_prev;
          rotate(off-nFill, off+nA_curr, -nFill);
          nA_curr -= nFill;
        }
      }
      nA_prev = nA_curr;
    }

    // REARRANGE BLOCKS
    // ----------------
    // Perform a `PermSortStable` on the blocks, comparing the first entries of each block.
    rearrange_blocks: {
      int mid=0;
      for( int i=0; i < n; i++ )
      {
        if( 0 < i && i%lg2 == 0 )
          for( int j=0; j < lg2; j++ )
          {
            int    k = (i/lg2 - 1)*lg2 + j;
            assert k >= 0;
            assert k < lg2len;
            if( (mid>>>j & 1)==1 != predicate(lg2L+k) )
              swap(lg2L+k, lg2R+k );
          }
        if( ! predicate(from + n*i) )
          ++mid;
      }
      assert mid == (nA-nL + n-1) / n;

      for( int k=0; k < n; k++ )
        if( tru != predicate(finL+k) ) // <- if the element is not yet in position
        {
          int i=k;
          do {
            // FIND POSITION OF 1st AMONG EQUALS
            boolean pi = predicate(from + n*k);
            int s = i/lg2*lg2,
              iTo = 0;

            if( 0 < s ) {
              for( int j=lg2; j-- > 0; )
                if( predicate(lg2L + s-lg2 + j) )
                     iTo+= 1<<j;
              if(pi) iTo = s - iTo;
            } if(pi) iTo+= mid;

            int pos = 0;
            for( int j=max(k+1, s); j < i; j++ )
              if( tru != predicate(finL+j) && predicate(from + n*j) == pi )
                ++pos;

            // FIND FINAL POSITION OF CURRENT ELEMENT
            for( ;; ++iTo )
              if( tru != predicate(finL+iTo) && --pos < 0 )
                break;

            blockSwap(
              from + n*k,
              from + n*iTo, n
            );
            swap( // <- flip bit to mark that `iTo` is now in position
              finL + iTo,
              finR + iTo
            );
            i = iTo;
          }
          while( i != k );
        }

      tru = !tru;
    }

    // reset FIN-buffer if necessary
    if( !tru ) blockSwap(finL,finR, n);

    // reset LG2-buffer
    for( int i=0; i < lg2len; i++ )
      if( predicate(lg2L+i) )
        swap(lg2L+i, lg2R+i);

    // B-elements of the mixed block belong all the way to the end
    if( 0 != nA_prev && nA_prev != n ) // <- unless there is no mixed block
      rotate( from-nL+nA, until, -(n-nA_prev) );
  }
}
