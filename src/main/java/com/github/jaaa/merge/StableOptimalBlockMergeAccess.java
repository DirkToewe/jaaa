package com.github.jaaa.merge;

import com.github.jaaa.compare.ArgMinAccess;
import com.github.jaaa.fn.Int4Consumer;
import com.github.jaaa.permute.BlockSwapAccess;
import com.github.jaaa.search.BinarySearchAccess;
import com.github.jaaa.search.ExpL2RSearch;
import com.github.jaaa.sort.InsertionSortAccess;

import static java.lang.Math.*;


public interface StableOptimalBlockMergeAccess extends BinarySearchAccess, BlockRotationMergeAccess, ExtractMergeBufOrdinalAccess, BlockSwapAccess, ArgMinAccess, InsertionSortAccess
{
  default void stableOptimalBlockMerge_mergeInPlace (int from, int mid, int until )  { blockRotationMerge(from,mid,until); }
  default void stableOptimalBlockMerge_mergeBuffered( int a0, int aLen,
                                                      int b0, int bLen,
                                                      int c0 )
  {
    new TimMergeAccessor<Void>() {
      @Override public Void malloc( int len ) { throw new AssertionError(); }
      @Override public void   swap( Void a, int i, Void b, int j ) { throw new AssertionError(); }
      @Override public void   copy( Void a, int i, Void b, int j ) {        StableOptimalBlockMergeAccess.this.   swap(i,j); }
      @Override public int compare( Void a, int i, Void b, int j ) { return StableOptimalBlockMergeAccess.this.compare(i,j); }
    }.timMerge(null,a0,aLen, null,b0,bLen, null,c0);
  }

  default void stableOptimalBlockMerge( int from, int mid, int until )
  {
    if( mid-from <= until-mid ) stableOptimalBlockMerge_L2R(from,mid,until);
    else                        stableOptimalBlockMerge_R2L(from,mid,until);
  }

  default void stableOptimalBlockMerge_L2R( int from, int mid, int until )
  {
    if( from < 0   ) throw new IllegalArgumentException();
    if( from > mid ) throw new IllegalArgumentException(); if( mid == until) return;
    if(until < mid ) throw new IllegalArgumentException(); if( mid == from ) return;

    int  nRight = until-mid, nLeft = mid-from,
      blockSize = (int) sqrt(nLeft),
       rightEnd = until; // <- TODO: this could probably still optimized

    // MIB: Movement imitation buffer
    // BDS: Block distribution storage
    final int   MIB_desiredLen = nLeft /blockSize, // <- Movement imitation buffer size
      BDS_len = MIB_desiredLen + nRight/blockSize; // <- Block distribution storage size

    if( nLeft <= BDS_len ) {
      blockRotationMerge(from,mid,until);
      return;
    }
    // STEP 1: Block distribution storage (BDS) assignment
    // ===================================================
    // The block distribution storage is an in-place boolean buffer. It consists
    // of a left and a right part. Each element on the lefts sequence has to be
    // strictly less than its corresponding element in the right sequence. In
    // order to achieve that there may have to me a gap between the two sequences
    // containing zero or more equal entries. Here is an example of what the BDS
    // might look like:
    //  |<--left-->|            |<--right-->|
    // [ 1, 2, 2, 4, 4, 4, 4, 4, 4, 4, 4, 9 ]
    //           |<---------mid-------->|
    class BDS {
      final int midL,
        r, end, midR;
      BDS() {
        int     lEnd = from+BDS_len,
          key = lEnd-1;
        midR = expL2RSearchGapR(lEnd,mid, key);
        midL = expR2LSearchGapL(from,key, key);
              r = max(lEnd, midR - (midL-from));
        end = r > mid-BDS_len ? mid : r+BDS_len;
      }
      boolean isLeftBlock( int i ) {
        assert r <= end-BDS_len;
        assert i >= 0;
        assert i < BDS_len;
        int c = compare(from+i, r+i);
        assert 0 != c;
        return c > 0;
      }
      void flipBit( int i ) {
        assert r <= end-BDS_len;
        assert i >= 0;
        assert i < BDS_len;
        swap(from+i, r+i);
      }
      void mergeUp( int rightEnd ) {
        // In order to sweep up the BDS, we binary search the insertion point of
        // the middle. We move the middle and right part to said insertion point.
        // The left and right are then block rotation merged with the subsequences
        // left and right of the insertion point respectively.
        int pos = expL2RSearchGapL(end,rightEnd, /*key=*/midL);
        rotate(midL,pos, -(end-midL));
        blockRotationMerge(from, midL, pos-(end-midL));
        blockRotationMerge(pos-(end-r), pos, rightEnd);
      }
    }

    var bds = new BDS();

    int MIB = bds.end;
    if( MIB < mid )
    {
      // STEP 2: Extract Movement-Imitation Buffer (MIB)
      // ===============================================
      int MIB_len = extractMergeBuf_ordinal_l_min_sorted(MIB,mid, MIB_desiredLen, MIB),
               nL = mid - MIB - MIB_len;
      if( MIB_len < MIB_desiredLen )
        blockSize = max(blockSize, (nL+MIB_len-1) / MIB_len);

      Int4Consumer merge = MIB_len < MIB_desiredLen
        ? (l,r, n, pos) -> {
          // IN-PLACE MERGE
          rotate(l,pos, -n);
          stableOptimalBlockMerge_mergeInPlace(pos-n+1, pos, r);
        }
        : (l,r, n, pos) -> {
          // BUFFERED MERGE
          blockSwap(MIB,l, n);

          // move up to pos
          for(; l+n < pos; l++ )
            swap(l,l+n);

          swap(MIB,l);
          stableOptimalBlockMerge_mergeBuffered(MIB+1,n-1,  pos,r-pos, l+1);
        };

      final int B = blockSize,
          blocksL = 0==B ? 0 : nL / B,
           block0 = mid - blocksL*B;
      if( blocksL > 0 )
      {
        final int blocksTotal = blocksL + nRight/B;
        assert    blocksTotal <= BDS_len;

        // STEP 3: Block Rearrangement
        // ===========================
        // The full blocks of the left sequence are rolled in place. Each block is
        // moved as far right as possible, without having to move elements to the
        // left during local merges. This can be achieved by binary searching the
        // first entry of a left block in the last entries of each remaining right
        // block to find the insertion point.
        loop: for(
          int   pos = 0, // <- position of the remaining rolling blocks
                min = 0,
                mib = MIB,
            nRemain = blocksL;; // <- number of blocks not yet in place
        )
        {
          int key = block0 + B*(pos+min),
              idx = ExpL2RSearch.searchGapL( pos+nRemain,blocksTotal, i -> compare(key, block0 + i*B + B-1) );
          idx -= nRemain;
          bds.flipBit(idx);

          // adjust min index and MIB to account for the block rolling
          min -= (idx-pos) % nRemain;
          if( min < 0 )
              min += nRemain;
          rotate(mib, mib+nRemain, -(idx-pos));

          // block roll
          for( ; pos < idx; pos++ )
            blockSwap( block0 + B*pos, block0 + B*(pos+nRemain), B );

          // move min. block to the very left so it is in place
          blockSwap( block0 + B*pos, block0 + B*(pos+min), B );
          swap(mib, mib+min); // <- as a side effect this sorts the MIB

          if( --nRemain <= 0 )
            break loop;
          pos += 1;
          mib += 1;

          // find next smallest block with the help of the MIB
          min = argMinL(mib,mib+nRemain) - mib;
        }

        // STEP 4: Local Merges
        // ====================
        for( int i=blocksTotal; i-- > 0; )
          if( bds.isLeftBlock(i) ) {
            bds.flipBit(i);
            int l = block0 + i*B,    m=l+B,
              pos = binarySearchGapL(m, min(m+B, until), l);
            merge.accept(l,rightEnd, B, pos);
            rightEnd = pos-B;
          }
      }

      // STEP 5: Merge up left odd end
      // =============================
      int MIB_end = MIB+MIB_len;
      if( MIB_end < block0 ) {
        int n = block0 - MIB_end,
          pos = expL2RSearchGapL(block0,rightEnd, MIB_end);
        merge.accept(MIB_end,rightEnd, n, pos);
        rightEnd = pos - n;
      }

      // if we use MIB as buffer for merging it might be out of order
      if( MIB_len == MIB_desiredLen )
        insertionSort(MIB, MIB+MIB_len);

      stableOptimalBlockMerge_mergeInPlace(MIB, MIB+MIB_len, until);
    }

    // STEP 6: Sweep up BDS
    // ====================
    bds.mergeUp(rightEnd);
  }

  default void stableOptimalBlockMerge_R2L( int from, int mid, int until )
  {
    if( from  < 0   ) throw new IllegalArgumentException();
    if( from  > mid ) throw new IllegalArgumentException();
    if( until < mid ) throw new IllegalArgumentException();

    int last = until-1;
    new StableOptimalBlockMergeAccess()
    {
      @Override public void stableOptimalBlockMerge_mergeInPlace ( int frm, int md, int ntl ) { StableOptimalBlockMergeAccess.this.stableOptimalBlockMerge_mergeInPlace(until-ntl, until-md, until-frm); }
      @Override public void stableOptimalBlockMerge_mergeBuffered( int a0, int aLen, int b0, int bLen, int c0 ) {
        StableOptimalBlockMergeAccess.this.stableOptimalBlockMerge_mergeBuffered(
          until-b0-bLen,bLen,
          until-a0-aLen,aLen,
          until-c0-aLen-bLen
        );
      }
      @Override public void   swap( int i, int j ) {        StableOptimalBlockMergeAccess.this.   swap(last-j, last-i); }
      @Override public int compare( int i, int j ) { return StableOptimalBlockMergeAccess.this.compare(last-j, last-i); }
    }.stableOptimalBlockMerge_L2R(0, until-mid, until-from);
  }
}
