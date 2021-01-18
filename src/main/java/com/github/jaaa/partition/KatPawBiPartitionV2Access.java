package com.github.jaaa.partition;

import com.github.jaaa.PredicateSwapAccess;
import com.github.jaaa.misc.BlockSwapAccess;
import com.github.jaaa.misc.RotateAccess;
import com.github.jaaa.util.Hex16;
import com.github.jaaa.util.IntBiConsumer;

import java.util.function.IntFunction;

import static com.github.jaaa.util.IMath.log2Ceil;
import static java.lang.Math.*;
import static java.lang.Math.max;

// OVERVIEW
// --------
//   m: Number of A-elements
//   n: Number of B-elements
//
// The KatPaw bi-partitioning algorithm builds on top of the ideas of the two
// MuRaSaBi algorithms. Since the second MuRaSaBi algorithm requires O( (m+n)*log(m+n) ),
// the idea of KatPaw is to reduce the problem size by a factor of O(log(m+n)) by first
// grouping A- and B-elements into blocks of size ~log(m+n), similar to how the MuRaSaBi
// algorithms themselves to. In the original sketch of KatPaw, these blocks are formed
// by recursively applying a variant of KatPaw. In this implementation, however, we
// decided to apply two sweeps of Hex(Rec)BiPartition to build blocks of size 256 (=16*16),
// which is significantly larger than log(m+n). The HexRecBiPartition is more concise, simpler
// and faster.
//
// Once blocks of size log(n+m) are formed, we now have l = (m+n) / log(m+n) blocks to
// bi-partition. If we no apply the second MuRaSaBi, it will take
// O( l*log(l) ) = O( (m+n) / log(m+n) * ( log(m+n) - loglog(m+n) ) ) = O(m+n) comparisons,
// making it linear.
//
// REFERENCES
// ----------
// .. [1] "STABLE MINIMUM SPACE PARTITIONING IN LINEAR TIME"
//         JYRKI KATAJAINEN and TOMI PASANEN

public interface KatPawBiPartitionV2Access extends BlockSwapAccess,
                                       ExtractBufBiPartitionAccess
{
  public default void katPawBiPartitionV2( int from, int until )
  {
    if( from > until ) throw new IllegalArgumentException();

    // BLOCK SIZES
    // -----------
    //   n: number of blocks (blockNum)
    //   b: length of a block (blockLen)
    //   B: size of an element in the block (16*16).
    // len: number of elements in the sequence that is to be partitioned
    //
    // We want to divide the B-chunked sequence into roughly √(len/B) blocks.
    // But for every block, 2A+2B elements are required as buffer. This leads to
    // the following equation for the block size:
    //
    // b*(b*B+4) <= len   =>   b = (√(B*len + 4) - 2) / B;
    //
    final int B = 16*16,
            len = subtractExact(until,from),
       blockLen = (int) ( ( sqrt(1d*len*B + 4) - 2 ) / B ), // <- number B-sized chunks per block
       blockNum = blockLen + (len - blockLen*blockLen*B - 4*blockLen) / (B*blockLen+4); // <- number of (B*blockLen)-sized blocks

    // TODO: only nRest%(2B) buffer elements have to be extracted before the "blockification step", the rest could be extracted block-wise

    buffer_extraction: {
      int    nRest = len - blockNum*blockLen*B,
        nL = nRest >>> 1,
        nR = nRest - nL;
      assert nRest >= 0;
      if( extractBufB(from,until,nR) < nR ) return; until -= nR;
      if( extractBufA(from,until,nL) < nL ) return;  from += nL;
    }
    if( from == until ) return;

    assert (until-from)%B == 0;

    // BLOCKIFY
    // --------
    // Group up the B-elements into blocks of size B while maintaining the order.
    Hex16 ord = new Hex16(),
          ORD = new Hex16();
    int   pos = from, nA = 0,
          POS = from, nB = 0;

    IntFunction<IntBiConsumer>
      swapper = off -> (i,j) ->      swap(off+i,    off+j),
      SWAPPER = OFF -> (i,j) -> blockSwap(OFF+i*16, OFF+j*16, 16);

    for( int l=from; l < until; l++ )
      if( ! predicate(l) ) nA++;
      else {               nB++;
        if( ord.isFull() )
        {
          move_up:{
            int        target = from + (l-16-from)/16*16;
            ord.rotate(target-pos);

            for( ; pos < target; pos++) swap(  pos, pos+16);
            for( ; pos > target;      ) swap(--pos, pos+16);
            assert pos ==target;
          }

          // unravel collected elements
          ord.sortAndClear( swapper.apply(pos) );

          if( ORD.isFull() )
          {
            // dump off collectd blocks at multiple of B
            int          TARGET = from + (pos-B-from)/B*B;
            ORD.rotate( (TARGET-POS) / 16 );

            // move collected blocks
            for( ; POS < TARGET; POS+=16 ) blockSwap(POS,     POS+B, 16);
            for( ; POS > TARGET;         ) blockSwap(POS-=16, POS+B, 16);
            assert POS ==TARGET;

            // unravel collected blocks
            ORD.sortAndClear( SWAPPER.apply(POS) );
          }
          else
          { // move up collected blocks
            int     SIZE = ORD.size()*16;
            if( 0 < SIZE ) {
              ORD.rotate( (pos-SIZE-POS) / 16 );
              for( ; POS < pos-SIZE;POS+=16 ) blockSwap(POS,POS+SIZE, 16);
            }
          }

          POS = pos - ORD.size()*16;
          ORD.append(ORD.size());
        }
        else
        { // move up collected elements
          int     size = ord.size();
          if( 0 < size ) {
            ord.rotate(l-size-pos);
            for( ; pos < l-size; pos++ ) swap(pos,pos+size);
          }
        }

        pos = l-ord.size();
        ord.append(ord.size());
      }

    // move remaining collected elements into place
    int                SIZE = ORD.size()*16,
                       size = ord.size();
    ord.rotate(  until-size-pos);
    for( ; pos < until-size; pos++) swap(pos, pos+size);

    // unravel remaining collected elements and blocks
    ord.sortAndClear( swapper.apply(pos) );
    ORD.sortAndClear( SWAPPER.apply(POS) );

    // move remaining collected blocks into place
    rotate(POS,until-size, -SIZE);

    // BI-PARTITION REMAINING BLOCKS
    // =============================
    assert blockNum >= blockLen;

    int lg2    =  blockNum<2 ? 1 : log2Ceil(blockNum),
        lg2len = (blockNum-1)/lg2*lg2; // <- FIXME

    // starting of positions of the left and right sides of the two bit buffers.
    int finL = from-blockNum,  lg2L = finL - lg2len,
        finR = until,          lg2R = finR + blockNum;

    // SORT & REARRANGE BLOCK BY BLOCK
    // -------------------------------
    // Perform a `PermSortStable` on each block, using nL and nR as memory bits.
    int nA_prev = 0;
    sort_each_block: {
      boolean tru = true;
      for( int off=from; off < until; off += blockLen*B )
      {
        // INIT LG2-BUFFER
        // ---------------
        int nA_curr=0; // number of A-elements in current block
        for( int i=0; i < blockLen; i++ )
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
          if( ! predicate(off + B*i) )
            ++nA_curr;
        }

        // SORT BLOCK
        // ----------
        for( int k=0; k < blockLen; k++ )
          if( tru != predicate(finL+k) ) // <- if the element is not yet in position
          {
            int i=k;
            do {
              // FIND POSITION AMONG UNFINISHED ELEMENTS
              boolean pi = predicate(off+B*k);
              int s = i/lg2*lg2,
                iTo = 0;

              if( 0 < s ) {
                for( int j=lg2; j-- > 0; )
                  if( predicate(lg2L + s-lg2 + j) )
                    iTo+= 1<<j;
                if(pi) iTo = s - iTo;
              } if(pi) iTo+= nA_curr;

              int p = 0;
              for( int j=max(k+1, s); j < i; j++ )
                if( tru != predicate(finL+j) && predicate(off+B*j) == pi )
                  ++p;

              // FIND FINAL POSITION
              for( ;; ++iTo )
                if( tru != predicate(finL+iTo) && --p < 0 )
                  break;

              blockSwap(off+B*iTo,  off+B*k, B);
              swap(finL+iTo, finR+iTo); // <- flip bit to mark that `iTo` is now in position
              i = iTo;
            }
            while( i != k );
          }

        tru = !tru;

        // REGROUP BLOCK
        // -------------
        if( 0 != nA_prev && nA_prev != blockLen )
        {
          if( nA_prev + nA_curr < blockLen ) {
            // MERGE PREVIOUS BLOCKS A-ELEMENTS INTO CURRENT BLOCK
            rotate(off-B*blockLen, off,                    -nA_prev*B);
            rotate(off-B*nA_prev,  off+B*(nA_prev+nA_curr),+nA_prev*B);
            nA_curr  +=  nA_prev;
          }
          else {
            // FILL UP PREVIOUS BLOCK
            int          nFill = blockLen-nA_prev;
            rotate(off-B*nFill, off+B*nA_curr, -nFill*B);
            nA_curr  -=  nFill;
          }
        }
        nA_prev = nA_curr;
      }

      if( !tru ) blockSwap(finL,finR, blockLen);
    }

    // REARRANGE BLOCKS
    // ----------------
    // Perform a `PermSortStable` on the blocks, comparing the first entries of each block.
    rearrange_blocks: {
      int mid=0;
      for( int i=0; i < blockNum; i++ )
      {
        if( 0 < i && i%lg2 == 0 )
          for( int j=0; j < lg2; j++ )
          {
            int    k = (i/lg2 - 1)*lg2 + j;
            assert k >= 0;
            assert k < lg2len;
            if( (mid>>>j & 1)==1 != predicate(lg2L+k) )
              swap(lg2L+k, lg2R+k);
          }
        if( ! predicate(from + blockLen*B*i) )
          ++mid;
      }
      assert mid == (nA + blockLen*B-1) / (blockLen*B);

      for( int k=0; k < blockNum; k++ )
        if( ! predicate(finL+k) ) // <- if the element is not yet in position
        {
          int i=k;
          do {
            // FIND POSITION OF 1st AMONG EQUALS
            boolean pi = predicate(from + blockLen*B*k);
            int s = i/lg2*lg2,
                    iTo = 0;

            if( 0 < s ) {
              for( int j=lg2; j-- > 0; )
                if( predicate(lg2L + s-lg2 + j) )
                  iTo+= 1<<j;
              if(pi) iTo = s - iTo;
            } if(pi) iTo+= mid;

            int p = 0;
            for( int j=max(k+1, s); j < i; j++ )
              if( ! predicate(finL+j) && predicate(from + blockLen*B*j) == pi )
                ++p;

            // FIND FINAL POSITION OF CURRENT ELEMENT
            for( ;; ++iTo )
              if( ! predicate(finL+iTo) && --p < 0 )
                break;

            blockSwap(
              from + blockLen*B*k,
              from + blockLen*B*iTo,
                     blockLen*B
            );
            swap( // <- flip bit to mark that `iTo` is now in position
              finL + iTo,
              finR + iTo
            );
            i = iTo;
          }
          while( i != k );
        }

      // reset FIN-buffer
      blockSwap(finL,finR, blockNum);
    }

    // reset LG2-buffer
    for( int i=0; i < lg2len; i++ )
      if( predicate(lg2L+i) )
        swap(lg2L+i, lg2R+i);

    int rest = size+SIZE,
        REST = nA_prev==0 ? 0 : blockLen-nA_prev;

    rotate(until - nB/B*B, until, -REST*B);

    if( rest >= B ) {
        rest -= B;
       until -= B;
    }

    rotate(from+nA, until, -rest);
  }
}
