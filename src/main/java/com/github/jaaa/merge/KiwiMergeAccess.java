package com.github.jaaa.merge;

import com.github.jaaa.compare.ArgMinAccess;
import com.github.jaaa.fn.Int4Consumer;
import com.github.jaaa.permute.BlockSwapAccess;
import com.github.jaaa.search.ExpL2RSearch;
import com.github.jaaa.sort.HeapSortFastAccess;

import static java.lang.Math.*;


// KiwiMerge is closely related to WikiMerge. Both are optimized variants of
// StableOptimalBlockMerge, which was originally presented in [1]. The two
// variants find different ways to get rid of the "block distribution storage"
// (BDS) used in the original algorithm.
//
// KiwiMerge gets rid of the BDS by simply realizing that the block rearrangement
// of StableOptimalBlockMerge is stable. A BDS is therefore not necessary to
// ensure stability during local merges. KiwiMerge is therefore able to get rid
// of the BDS all together. It therefore requires only (roughly) half the merge
// buffer size of WikiMerge.
//
// References
// ----------
// .. [1] "Ratio Based Stable In-Place Merging"
//         Pok-Son Kim & Arne Kutzner
//         Theory and Applications of Models of Computation, pp. 246-257, 2008
public interface KiwiMergeAccess extends ArgMinAccess, BlockRotationMergeAccess, BlockSwapAccess, ExtractMergeBufOrdinalAccess, HeapSortFastAccess
{
  /** Returns the minimum buffer size required for buffered merging.
    *
    * @param len Length of on of the two merge sequences.
    * @return The minimum number of buffer elements that have to be extracted
    *         from the subsequence to allow buffered merging.
    */
  static int minBufLen( int len )
  {
    if( len < 0 ) throw new IllegalArgumentException();
    int    buf_len = -1 + (int) ceil( sqrt(len+0.25) - 0.5 );
    if( 0==buf_len || (len-buf_len) / buf_len > buf_len )
         ++buf_len;
    return buf_len;
  }

  default void kiwiMerge_mergeInPlace ( int from, int mid, int until ) { blockRotationMerge(from,mid,until); }
  default void kiwiMerge_mergeBuffered( int a0, int aLen,
                                        int b0, int bLen,
                                        int c0 )
  {
    new TimMergeAccessor<Void>() {
      @Override public Void malloc( int len ) { throw new AssertionError(); }
      @Override public void   swap( Void a, int i, Void b, int j ) { throw new AssertionError(); }
      @Override public void   copy( Void a, int i, Void b, int j ) {        KiwiMergeAccess.this.   swap(i,j); }
      @Override public int compare( Void a, int i, Void b, int j ) { return KiwiMergeAccess.this.compare(i,j); }
    }.timMerge(null,a0,aLen, null,b0,bLen, null,c0);
  }

  default void kiwiMerge( int from, int mid, int until )
  {
    if( mid-from <= until-mid ) kiwiMergeL2R(from,mid,until);
    else                        kiwiMergeR2L(from,mid,until);
  }

  default void kiwiMergeL2R(int from, int mid, int until )
  {
    if( from < 0 || from > mid || until < mid ) throw new IllegalArgumentException();
    if( mid == until || mid == from ) return;

    int lenL =   mid-from,
        len  = until-from;

    if( lenL < 7 || lenL < sqrt(len*2) )
      kiwiMerge_mergeInPlace(from,mid,until);
    else {
      // STEP 1: Extract BUFFER
      // ======================
      // The buffer is used both as movement-imitation buffer (MIB) and
      // merge buffer.
      int bufLen = extractMergeBuf_ordinal_l_min_sorted(from,mid, minBufLen(lenL), from);
      kiwiMergeL2R_usingBuffer(from+bufLen, mid, until, from, bufLen);
      kiwiMerge_mergeInPlace(from, from+bufLen, until);
    }
  }

  default void kiwiMergeL2R_usingBuffer( int from, int mid, int until, int buf, int bufLen )
  {
    if( from < 0 || buf < 0 || bufLen <= 0 || from > mid )
      throw new IllegalArgumentException();
    if( mid == until) return; if( until < mid ) throw new IllegalArgumentException();
    if( mid == from ) return;

    int lenL =   mid-from,
        lenR = until-mid;

    boolean useBufferedMerge = lenL / bufLen <= bufLen;
    int B = useBufferedMerge ? min(lenL,bufLen) : 1 + lenL/(bufLen+1);
    if(  !  useBufferedMerge ) {
      assert lenL /  B   <= bufLen;
      assert lenL / (B-1) > bufLen;
    }

    Int4Consumer merge = useBufferedMerge
      ? (l,r, n, pos) -> {
        // BUFFERED MERGE
        blockSwap(buf,l, n);

        // roll up to pos
        for(; l+n < pos; l++ )
          swap(l,l+n);

        swap(buf,l); // <- insert 1st elem.
        kiwiMerge_mergeBuffered(buf+1,n-1,  pos,r-pos, l+1);
      }
      : (l,r, n, pos) -> {
        // IN-PLACE MERGE
        rotate(l,pos, -n);
        kiwiMerge_mergeInPlace(pos-n+1, pos, r);
      };

    int mergeEnd = until,
        nBlocksL = lenL/B;
    if( B == lenL ) // <- merge single blocks without block rearrangement
       nBlocksL--;

    int block0 = mid - B*nBlocksL;

    if( 0 < nBlocksL )
    {
      int nBlocks  = lenR/B + nBlocksL;

      // STEP 2: Block Rearrangement
      // ===========================
      // The full blocks of the A sequence are rolled in place. Each block is
      // moved as far right as possible, without having to move elements back to
      // the left during local merges. This can be achieved by binary searching
      // the first entry of an A-block in the last entries of each remaining
      // B-block to find the insertion point.
      for(
        int pos = 0, // <- position of the remaining rolling blocks
            min = 0,
            mib = buf, // <- movement imitation buffer
        nRemain = nBlocksL;; // <- number of blocks not yet in place
      )
      {
        int key = block0 + B*(pos+min),
            idx = ExpL2RSearch.searchGapL( pos+nRemain,nBlocks, i -> compare(key, block0 + i*B + B-1) );
            idx -= nRemain;

        // adjust min index and MIB to account for the block rolling
            min += (pos-idx) % nRemain;
        if( min < 0 )
            min += nRemain;
        rotate(mib, mib+nRemain, pos-idx);

        // block roll forward
        for( ; pos < idx; pos++ )
          blockSwap( block0 + B*pos, block0 + B*(pos+nRemain), B );

        // move min. block to the very left to its final destination
        blockSwap( block0 + B*pos, block0 + B*(pos+min), B );
        swap(mib, mib+min); // <- as a side effect this sorts the MIB

        if( --nRemain <= 0 )
          break;
        pos += 1;
        mib += 1;

        // find next smallest block with the help of the MIB
        min = argMinL(mib,mib+nRemain) - mib;
      }

      // STEP 3: Local Merges
      // ====================
      // We do not need a block distribution storage (BDS) to merge
      // the blocks, as originally proposed in [1]. All we have to
      // do is to find neighboring blocks that still have to be
      // merged. Block rearrangement moved A-blocks as far to the
      // right as possible without having to move any element of
      // an A-block back left during local merges. That means
      // if we have to neighboring blocks which are not yet sorted,
      // we can be sure that:
      //   1) The left neighbor is an A-block
      //   2) The right neighbor starts with an B-element
      //   3) The first element of the left neighbor's final
      //      destination is within the right neighbor block.
      //   4) The left neighbor only has merged with the
      //      right rest of the sequence
      //
      // So all we have to go from right to left through all blocks,
      // find such neighbors and merge them with the right side.
      for( int i=block0 + nBlocks*B < until ? nBlocks : nBlocks-1; i > 0; i-- )
      {
        int m = block0 + i*B;
        if( compare(m-1,m) > 0 ) {
          int          l = m-B,       pos = expL2RSearchGap(m,min(m+B,mergeEnd), l, false); // <- find pos. of 1st elem.
          merge.accept(l,mergeEnd, B, pos);
          mergeEnd = pos-B;
        }
      }
    }

    // STEP 4: Merge up left odd ends
    // =============================
    if( from < block0 )
      merge.accept( from,mergeEnd, block0-from, expL2RSearchGap(block0,mergeEnd, from, false) );

    // if we used MIB as merge buffer, it might be out of order
    if( useBufferedMerge )
      heapSortFast(buf, buf+B);
  }

  default void kiwiMergeR2L(int from, int mid, int until )
  {
    if( 0 > from || from > mid || mid > until )
      throw new IllegalArgumentException();

    int last = until-1;
    new KiwiMergeAccess()
    {
      @Override public void kiwiMerge_mergeBuffered(int a0, int aLen, int b0, int bLen, int c0 ) {
        KiwiMergeAccess.this.kiwiMerge_mergeBuffered(
          until-b0-bLen,bLen,
          until-a0-aLen,aLen,
          until-c0-aLen-bLen
        );
      }
      @Override public void kiwiMerge_mergeInPlace(int frm, int md, int ntl ) { KiwiMergeAccess.this.kiwiMerge_mergeInPlace(until-ntl, until-md, until-frm); }
      @Override public void   swap( int i, int j ) {        KiwiMergeAccess.this.   swap(last-j, last-i); }
      @Override public int compare( int i, int j ) { return KiwiMergeAccess.this.compare(last-j, last-i); }
    }.kiwiMergeL2R(0, until-mid, until-from);
  }
}
