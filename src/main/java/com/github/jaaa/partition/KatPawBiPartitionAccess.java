package com.github.jaaa.partition;

import com.github.jaaa.fn.PredicateSwapAccess;
import com.github.jaaa.permute.SwapAccess;
import com.github.jaaa.permute.BlockSwapAccess;
import com.github.jaaa.permute.RotateAccess;
import com.github.jaaa.util.Byte256;
import com.github.jaaa.fn.Int3Op;

import java.util.function.IntFunction;

import static com.github.jaaa.util.IMath.log2Ceil;
import static java.lang.Math.*;

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

public interface KatPawBiPartitionAccess extends PredicateSwapAccess, BlockSwapAccess, RotateAccess
{
  default void katPawBiPartitionV5( int from, int until )
  {
    if( from < 0     ) throw new IllegalArgumentException();
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
    int nL,nR,B = 16*16,
            len = subtractExact(until,from),
       blockLen = (int) ( ( sqrt(1d*len*B + 4) - 2 ) / B ), // <- number B-sized chunks per block
             BB = blockLen*B,
       blockNum = blockLen + (len - blockLen*BB - 4*blockLen) / (BB+4);

    // TODO: only nRest%(2B) buffer elements have to be extracted before the "blockification step", the rest could be extracted block-wise
    compute_buf_len: { // <- sizes of left and right buffer
      int    nRest = len - blockNum*BB;
        nL = nRest >>> 1; // <- num. of elements to be moved to the left  as buffer
        nR = nRest - nL;  // <- num. of elements to be moved to the right as buffer
      assert nRest >= 0;
    }

    // CHUNKIFY & EXTRACT BUFFERS
    // ==========================
    // Group up elements into chunks of size B while maintaining the order.
    Byte256 ord = new Byte256();
    int nA = 0, pos = from,
        nB = 0, p1  = from,
                p256= -1; // <- position of last B-sized chunk to be collected as left buffer
    final int n1 = nL % B; // <- remainder of elements to be used as left buffer,  i.e. elements that cannot be collected as B-sized chunks

    IntFunction<SwapAccess> swapper = off -> (i, j) -> swap(off+i, off+j);

    for( int l=from; l < until; l++ )
    {
      final int size = ord.size();

      if( ! predicate(l) )
      {
        if( ++nA == n1 )
        { // IF ALL REMAINDER ELEMENTS FOR THE LEFT BUFFER WERE COLLECTED, MARK THEIR POSITION
          int            target = l+1 - size;
          ord.rotate(pos-target);
          for(;pos < target; pos++) swap(pos, pos+size);
          p1 = pos - n1;
          from += n1;
        }
        else if( nA <= nL && (nA-n1) % B == 0 ) {
          // IF ANOTHER CHUNK FOR THE LEFT BUFFER WAS COLLECTED, MARK ITS POSITION
          p256 = l+1 - B - size;
          assert (p256-from) % B == 0;
        }
      }
      else
      {
        ++nB;
        if( 0 < size )
        {
          int target = l-size;
          // if block is full, let's dump it
          if( size == B ) {       target = from + (target-from >>> 8 << 8);
            for( int p=pos; p-- > target; ) swap(p, p+B); // <- roll back block if necessary
          }

          // roll up block
          ord.rotate(pos-target);
          for(; pos < target; pos++) swap(pos, pos+size);

          // unravel block and dump it
          if( size == B )
            ord.sortAndClear( swapper.apply(target) );
        }
        pos = l-ord.size();
        ord.append(ord.size());
      }
    }

    int rest = ord.size(); {
      // move remaining collected elements to the very right
      int            target = until-rest;
      ord.rotate(pos-target);
      for( ; pos  <  target; pos++) swap(pos, pos+rest);
      assert pos  == target;

      // unravel remaining collected elements and blocks
      ord.sortAndClear( swapper.apply(pos) );
    }

    // EXTRACT LEFT BUFFER
    // -------------------
    extract_left: {
      int nBuf = min(nA,nL);

      if( nA < n1 )
        p1 = until - nA - rest;
      else
      {
        from -= n1;

        // COLLECT CHUNKS
        if( n1 < nBuf )
        {
          int     nCollected = (nBuf-n1) % B;
          if( 0 < nCollected )
            p256 = until - nCollected - rest;
          else
            nCollected = B;

          for( int p=p256; (p-=B) >= p1; )
            if( ! predicate(p) ) {
              rotate(p+B, p256+nCollected, nCollected);
              p256 = p;        nCollected += B;
            }
          assert nCollected == nBuf - n1;
          rotate(p1+n1, p256+nCollected, nCollected);
        }
      }

      // move extracted buffer to the left
      rotate(from, p1+nBuf, nBuf);
      if( nA <= nL ) return;
          nA -= nL;
        from += nL;
    }

    // EXTRACT RIGHT BUFFER
    // --------------------
    extract_right: {
      int nBuf = min(nB,nR);

      if( rest < nBuf )
      { // COLLECT CHUNKS
        int first = -1,
              end = from + (until-from-rest-B)/B*B;
        assert (end-from) % B == 0;

        int nCollected = 0;
        // find first chunk
        for( int p=end; p >= from; p -= B )
          if( predicate(p) )
          {     nCollected += B;
            if( nCollected >= nBuf-rest  ) {
              first = p;
              break;
            }
          }

        // move up chunks
        nCollected=B;
        for( int p=first; (p+=B) <= end; )
          if( predicate(p) ) {
            rotate(first,  p,-nCollected);
                   first = p -nCollected;
                              nCollected += B;
          }

        rotate(first, until-rest, -nCollected);
      }

      if( nB <= nR ) return;
          nB -= nR;
       until -= nR;
    }

    // BI-PARTITION REMAINING BLOCKS
    // =============================
    assert     blockNum >= blockLen;
    int lg2 =  blockNum<2 ? 1 : log2Ceil(blockNum),
      nInts = (blockNum-1) / lg2;

    InPlacePartitionBufferBool finishedEntries = new InPlacePartitionBufferBool(this, from-blockNum,           until,blockNum);
    InPlacePartitionBufferInt          offsets = new InPlacePartitionBufferInt (this, from-blockNum-nInts*lg2, until+blockNum, lg2, nInts);

    Int3Op permPartitionStableBlocks = (off, nBlocks, blockSize) ->
    {
      // INIT OFFSETS
      int mid=0; // number of A-elements in current block
      for( int i=0; i < nBlocks; i++ )
      {
        if(  0 < i && i%lg2 == 0 )
          offsets.set(i/lg2 - 1, mid);
        if( ! predicate(off + blockSize*i) )
          ++mid;
      }
      // SORT BLOCKS
      for( int k=0; k < nBlocks; k++ )
        if( ! finishedEntries.get(k) ) // <- if the element is not yet in position
        {
          int i=k;
          do {
            // FIND POSITION AMONG UNFINISHED ELEMENTS
            boolean pi = predicate(off+blockSize*k);
            int s = i/lg2,
              iTo = s==0 ? 0 : offsets.get(s-1);
            if(pi)
              iTo = s*lg2 - iTo + mid;

            int p = 0;
            for( int j=max(k+1, s*lg2); j < i; j++ )
              if( ! finishedEntries.get(j) && predicate(off+blockSize*j) == pi )
                ++p;

            // FIND FINAL POSITION
            for( ;; ++iTo )
              if( ! finishedEntries.get(iTo) && --p < 0 )
                break;

            blockSwap(off+blockSize*iTo,  off+blockSize*k, blockSize);
            finishedEntries.flip(iTo); // <- flip bit to mark that `iTo` now contains final value
            i = iTo;
          }
          while( i != k );
        }
      return mid;
    };

    // SORT & REARRANGE BLOCK BY BLOCK
    // -------------------------------
    // Perform a `PermSortStable` on each block, using nL and nR as memory bits.
    for( int nA_prev=0, off=from; off < until; off += BB )
    {
      int nA_curr = permPartitionStableBlocks.applyAsInt(off, blockLen, B);

      finishedEntries.flipTruth();

      // REGROUP BLOCK
      if( 0 != nA_prev && nA_prev != blockLen )
      {
        if( nA_prev + nA_curr < blockLen ) {
          // MERGE PREVIOUS BLOCKS A-ELEMENTS INTO CURRENT BLOCK
          int l1 = off - BB;                    revert(l1,off);
          int l2 = off - B* nA_prev;            revert(l1, l2);
          int r2 = off + B*(nA_prev + nA_curr); revert(off,r2);
          int r1 = off + B* nA_prev;            revert(r1, r2);
                                                revert(l2, r1);
          nA_curr += nA_prev;
        }
        else {
          // FILL UP PREVIOUS BLOCK
          int        nFill = blockLen-nA_prev;
          rotate(off-nFill*B, off+nA_curr*B, -nFill*B);
          nA_curr -= nFill;
          nA_prev  = blockLen;
        }
      }
      // FULL BLOCKS ON VERY LEFT ARE ALREADY IN RIGHT PLACE
      if( from+BB == off && nA_prev == blockLen && !predicate(off-1) ) {
         nA -= BB;
       from += BB; --blockNum;
      }
      nA_prev = nA_curr;
    }

    int REM = nB % BB;

    resolve_right_side: {
      int end = until,
          rem = nB % B;
      while( from <= until-BB && predicate(until-BB) ) {
        until -= BB;
           nB -= BB; --blockNum;
      }
      // resolve mixed chunk
      rotate(until-REM, end, -rem);
    }

    assert until-from == blockNum*BB;

    if( REM != 0 ) --blockNum;

    // adjust buffer for size change from blockLen to blockNum
    if( finishedEntries.isTruthInverted() )
      finishedEntries.flipRange(
        min(blockNum, blockLen),
        max(blockNum, blockLen)
      );

    // REARRANGE BLOCKS
    // ----------------
    // Perform a `PermSortStable` on the blocks, comparing the first entries of each block.
    permPartitionStableBlocks.applyAsInt(from, blockNum, BB);

    // reset buffers
    if( ! finishedEntries.isTruthInverted() )
      finishedEntries.flipRange(0,blockNum);
    offsets.reset();

    // RESOLVE MIXED BLOCK
    // -------------------
    // There may be a single block left over that contains both A and B elements.
    if( 0 != REM ) {
      int mid = until-REM;      REM = BB-REM;
      rotate( from+nA-REM, mid, REM );
    }
  }
}
