package com.github.jaaa.partition;

import com.github.jaaa.misc.BlockSwapAccess;
import com.github.jaaa.util.Hex16;
import com.github.jaaa.util.IntBiConsumer;
import com.github.jaaa.util.IntTriOp;

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

public interface KatPawBiPartitionV4Access extends BlockSwapAccess,
                                       ExtractBufBiPartitionAccess
{
  default void katPawBiPartitionV4( int from, int until )
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
    final int B = 16*16,
            len = subtractExact(until,from),
       blockLen = (int) ( ( sqrt(1d*len*B + 4) - 2 ) / B ), // <- number B-sized chunks per block
             BB = blockLen*B,
       blockNum = blockLen + (len - blockLen*BB - 4*blockLen) / (BB+4); // <- number of (B*blockLen)-sized blocks, may be slightly greater than blockLen

    // TODO: only nRest%(2B) buffer elements have to be extracted before the "blockification step", the rest could be extracted block-wise

    final int nL,nR; { // <- sizes of left and right buffer
    int    nRest = len - blockNum*BB;
      nL = nRest >>> 1; // <- num. of elements to be moved to the left  as buffer
      nR = nRest - nL;  // <- num. of elements to be moved to the right as buffer
    assert nRest >= 0;
  }

    // CHUNKIFY & EXTRACT BUFFERS
    // ==========================
    // Group up elements into chunks of size B while maintaining the order.
    Hex16 ord = new Hex16(),
          ORD = new Hex16();
    int   pos = from, nA = 0,
          POS = from, nB = 0,
          p1  = from, // <- position of remainder elements to be used as left buffer, i.e. elements that cannot be collected as B-sized chunks
          p256= -1; // <- position of last B-sized chunk to be collected as left buffer
    final int n1 = nL % B; // <- remainder of elements to be used as left buffer,  i.e. elements that cannot be collected as B-sized chunks

    IntFunction<IntBiConsumer>
      swapper = off -> (i,j) ->      swap(off+i,    off+j),
      SWAPPER = OFF -> (i,j) -> blockSwap(OFF+i*16, OFF+j*16, 16);

    for( int l=from; l < until; l++ )
      if( ! predicate(l) )
      {
        int size = ord.size(),
            SIZE = ORD.size()*16;
        if( ++nA == n1 ) {
          // IF ALL REMAINDER ELEMENTS FOR THE LEFT BUFFER WERE COLLECTED, MARK THEIR POSITION
          rotate(POS,      pos, -SIZE);
          rotate(pos-SIZE, l+1, -SIZE-size); // <- TODO: Hex16-ROTATE INSTEAD?
          pos = l+1 - size;
          POS = pos - SIZE;
          p1  = POS - n1;
          from += n1;
        }
        else if( nA <= nL && (nA-n1) % B == 0 ) {
          // IF ANOTHER CHUNK FOR THE LEFT BUFFER WAS COLLECTED, MARK ITS POSITION
          p256 = l+1 - B - size - SIZE;
          assert (p256-from) % B == 0;
        }
      }
      else
      {
        ++nB;
        if( ord.isFull() )
        {
          // move up collected elements
          int        target = from + (l-16-from)/16 * 16;
          ord.rotate(target-pos);
          for( ; pos < target; pos++) swap(  pos, pos+16);
          for( ; pos > target;      ) swap(--pos, pos+16);
          assert pos ==target;

          // unravel collected elements
          ord.sortAndClear( swapper.apply(pos) );

          if( ORD.isFull() )
          { // dump off collectd blocks at multiple of B
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

    int rest; {
    // move remaining collected elements to the very right
    int                SIZE = ORD.size()*16,
            size = ord.size();
    ord.rotate(  until-size-pos);
    for( ; pos < until-size; pos++) swap(pos, pos+size);

    // unravel remaining collected elements and blocks
    ord.sortAndClear( swapper.apply(pos) );
    ORD.sortAndClear( SWAPPER.apply(POS) );

    // move remaining collected blocks into place
    rotate(POS,until-size, -SIZE);

    rest = size+SIZE;
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
          if( 0 < nCollected ) {
            assert nA < nL;
            p256 = until - nCollected - rest;
          }
          else {
            assert 0 <= p256;
            nCollected = B;
          }
          assert (p256-from-n1) % B == 0;

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

      if( rest < nBuf ) {
        // COLLECT CHUNKS
        int first = -1,
                end = from + (until-from-rest-B)/B*B;
        assert (end-from) % B == 0;

        int nCollected = 0;
        // find first chunk
        for( int p=end; p >= from; p -= B )
          if( predicate(p) )
          {
            assert p+B <= until-rest;
            nCollected += B;
            if( nCollected >= nBuf-rest  ) {
              first = p;
              break;
            }
          }
        assert nCollected > 0;
        assert nBuf <= rest + nCollected;
        assert nBuf >  rest + nCollected - B;

        assert from <= first;

        // move up chunks
        nCollected=B;
        for( int p=first; (p+=B) <= end; )
          if( predicate(p) ) {
            rotate(first,  p,-nCollected);
            first = p -nCollected;
            nCollected += B;
          }
        assert nCollected > 0;
        assert nBuf <= rest + nCollected;
        assert nBuf >  rest + nCollected - B;

        rotate(first, until-rest, -nCollected);

        rest += nCollected;
      }

      if( nB <= nR ) return;
          nB -= nR;
       until -= nR;
        rest -= nR;
    }

    assert from < until;
    assert (until-from)%B == 0;

    // BI-PARTITION REMAINING BLOCKS
    // =============================
    assert blockNum >= blockLen;

    int lg2 =  blockNum<2 ? 1 : log2Ceil(blockNum),
      nInts = (blockNum-1)/lg2;

    var finishedEntries = new InplaceBufferBool(this, from-blockNum,           until,            blockNum);
    var         offsets = new InplaceBufferInt (this, from-blockNum-nInts*lg2, until+blockNum, lg2, nInts);

    IntTriOp partitionBlocks = (off,nBlocks,blockSize) ->
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
    int nA_prev = 0;
    for( int off=from; off < until; off += BB )
    {
      int nA_curr = partitionBlocks.applyAsInt(off, blockLen, B);

      finishedEntries.flipTruth();

      // REGROUP BLOCK
      if( 0 != nA_prev && nA_prev != blockLen )
      {
        if( nA_prev + nA_curr < blockLen ) {
          // MERGE PREVIOUS BLOCKS A-ELEMENTS INTO CURRENT BLOCK
          rotate(off-BB,        off,                    -nA_prev*B);
          rotate(off-B*nA_prev, off+B*(nA_prev+nA_curr),+nA_prev*B);
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

    // adjust unused buffer region
    if( finishedEntries.isTruthInverted() )
        finishedEntries.flipRange(blockLen, blockNum);

    // REARRANGE BLOCKS
    // ----------------
    // Perform a `PermSortStable` on the blocks, comparing the first entries of each block.
    int    nA_total = partitionBlocks.applyAsInt(from, blockNum, BB);
    assert nA_total == (nA + BB-1) / BB;

    // reset buffers
    if( ! finishedEntries.isTruthInverted() )
          finishedEntries.flipRange(0,blockNum);
    offsets.reset();

    // RESOLVE MIXED BLOCK
    // -------------------
    // There may be a single block left over that contains both A and B elements.
    // The A elements in said block have to moved all the way back, due to how
    // the blocks were formed.
    int REM = nB % BB;
    if( REM <= B ) {
      if( rest > B )
        until -= B;
    }
    else {
      int mid = from+nA + REM;
      if( rest >= B )
           mid -= B;
      rotate(from+nA, mid, -nB%B);
    }
    rotate(from+nA, until, -REM);
  }
}
