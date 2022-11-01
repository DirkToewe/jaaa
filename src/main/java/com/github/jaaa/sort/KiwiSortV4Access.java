package com.github.jaaa.sort;

import com.github.jaaa.ArgMinAccess;
import com.github.jaaa.fn.Int4Consumer;
import com.github.jaaa.fn.Int4Op;
import com.github.jaaa.merge.BlockRotationMergeAccess;
import com.github.jaaa.merge.TimMergeAccessor;
import com.github.jaaa.misc.BlockSwapAccess;
import com.github.jaaa.search.ExpL2RSearch;
import com.github.jaaa.select.QuickSelectV1Access;

import static java.lang.Integer.numberOfTrailingZeros;
import static java.lang.Math.*;


// KiwiSort implementation that uses recursive merge order instead of bottom-up.
public interface KiwiSortV4Access extends ArgMinAccess, BlockRotationMergeAccess, BlockSwapAccess, ExtractSortBufOrdinalAccess, HeapSortFastAccess, InsertionAdaptiveSortAccess, QuickSelectV1Access
{
  int MIN_RUN_LEN = 16;

  TimMergeAccessor<KiwiSortV4Access> _TIM_MERGE_ACCESSOR = new TimMergeAccessor<>()
  {
    @Override public KiwiSortV4Access malloc( int len ) { throw new AssertionError(); }
    @Override public int compare( KiwiSortV4Access a, int i, KiwiSortV4Access b, int j ) { return a.compare(i,j); }
    @Override public void   swap( KiwiSortV4Access a, int i, KiwiSortV4Access b, int j ) {        a.   swap(i,j); }
    @Override public void   copy( KiwiSortV4Access a, int i, KiwiSortV4Access b, int j ) {        a.   swap(i,j); }
  };

  static int minBufLen( int len )
  {
    // bufLen == (len-bufLen) / bufLen;
    if( len < 0 ) throw new IllegalArgumentException();
    int         bufLen = (int) ceil( ( sqrt(len*8d + 1) - 5 ) / 4 );
    int l = len-bufLen;
        l-= l+1>>>1;
    if(1 > bufLen || l/bufLen > bufLen)
         ++bufLen;
    return bufLen;
  }

  default  int kiwiSortV4_runLen( int n ) { return TimSort.optimalRunLength(MIN_RUN_LEN,n); }
  default void kiwiSortV4_sortRun      ( int from,          int until ) { insertionAdaptiveSort(from,until); }
  default void kiwiSortV4_mergeInPlace ( int from, int mid, int until ) { blockRotationMerge(from,mid,until); }
  default void kiwiSortV4_mergeBuffered( int a0, int aLen,
                                         int b0, int bLen,
                                         int c0 )
  {
//    new TimMergeAccessor<Void>() {
//      @Override public Void malloc( int len ) { throw new AssertionError(); }
//      @Override public int compare( Void a, int i, Void b, int j ) { return KiwiSortV4Access.this.compare(i,j); }
//      @Override public void   swap( Void a, int i, Void b, int j ) {        KiwiSortV4Access.this.   swap(i,j); }
//      @Override public void   copy( Void a, int i, Void b, int j ) {        KiwiSortV4Access.this.   swap(i,j); }
//    }._timMergeL2R(TimMergeAccessor.MIN_GALLOP, null,a0,aLen, null,b0,bLen, null,c0);
    _TIM_MERGE_ACCESSOR._timMergeL2R(TimMergeAccessor.MIN_GALLOP, this,a0,aLen, this,b0,bLen, this,c0);
  }

  default void kiwiSortV4( int from, int until )
  {
    if( from < 0 || from > until ) throw new IllegalArgumentException();
    if( from > until - MIN_RUN_LEN*3 ) {
      kiwiSortV4_sortRun(from,until);
      return;
    }

    // STEP 1: Buffer Extraction
    // -------------------------
    int        len = until-from,
     desiredBufLen = minBufLen(len),
            bufLen = extractSortBuf_ordinal_l_sorted(from,until, desiredBufLen, from),
            buf    = from;
    from += bufLen;
    len  -= bufLen;

    /* A broadcast version of @StableOptimalBlockMergeV2Access,
     * which allows to first rearrange all blocks and then merge
     * them in a consecutive step. This avoids
     */
    Int4Op merger = (nUnsorted, lenL,mid,lenR) ->
    {
      boolean useBufferedMerge = lenL / bufLen <= bufLen;
      int B = useBufferedMerge ? min(lenL,bufLen) : 1 + lenL/(bufLen+1);
      if(  !  useBufferedMerge ) {
        assert lenL /  B   <= bufLen;
        assert lenL / (B-1) > bufLen;
      }

      Int4Consumer merge = useBufferedMerge
        ? (l,r, n, pos) -> {
          // BUFFERED MERGE
          blockSwap(buf, l, n);

          // roll up to pos
          for(; l+n < pos; l++ )
            swap(l,l+n);

          swap(buf,l); // <- insert 1st elem.
          kiwiSortV4_mergeBuffered(buf+1,n-1,  pos,r-pos, l+1);
        }
        : (l,r, n, pos) -> {
          // IN-PLACE MERGE
          rotate(l,pos, -n);
          kiwiSortV4_mergeInPlace(pos-n+1, pos, r);
        };

      int mergeEnd = mid + lenR,
          nBlocksL = lenL/B;
      if( nBlocksL*B == lenL && nBlocksL <= 3 ) // <- if there's no rest, we can rearrange with one block less, to avoid a partialSort
        --nBlocksL;
      if( nBlocksL > 2 ) {
        int nUnsortedMax = bufLen - nBlocksL;
        if( nUnsortedMax < nUnsorted ) {
          partialSort(buf, buf+nUnsortedMax, buf+nUnsorted);
          nUnsorted = nUnsortedMax;
        }
      }
      if( useBufferedMerge )
        nUnsorted = max(nUnsorted, B);

      int firstBlock = mid - B*nBlocksL;

      if( nBlocksL > 0 )
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
              mib = buf+bufLen-nBlocksL, // <- movement imitation buffer
          nRemain = nBlocksL;; // <- number of blocks not yet in place
        )
        {
          int key = firstBlock + B*(pos+min),
              idx = ExpL2RSearch.searchGapL( pos+nRemain,nBlocks, i -> compare(key, firstBlock + i*B + B-1) );
              idx -= nRemain;

          // adjust min index and MIB to account for the block rolling
              min += (pos-idx) % nRemain;
          if( min < 0 )
              min += nRemain;
          rotate(mib, mib+nRemain, pos-idx);

          // block roll forward
          for( ; pos < idx; pos++ )
            blockSwap( firstBlock + B*pos, firstBlock + B*(pos+nRemain), B );

          // move min. block to the very left to its final destination
          blockSwap( firstBlock + B*pos, firstBlock + B*(pos+min), B );
          swap(mib, mib+min); // <- as a side effect this sorts the MIB

          if( --nRemain <= 0 )
            break;
          ++pos;
          ++mib;

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
        // So all we have to go _from right to left through all blocks,
        // find such neighbors and merge them with the right side.
        for( int i=firstBlock + nBlocks*B < mergeEnd ? nBlocks : nBlocks-1; i > 0; i-- )
        {
          int m = firstBlock + i*B;
          if( compare(m-1,m) > 0 ) {
            int          l = m-B,       pos = expL2RSearchGap(m,min(m+B,mergeEnd), l, false); // <- find pos. of 1st elem.
            merge.accept(l,mergeEnd, B, pos);
            mergeEnd = pos-B;
          }
        }
      }

      // STEP 4: Merge up left odd ends
      // =============================
      int mergeStart = mid - lenL;
      if( mergeStart < firstBlock )
        merge.accept( mergeStart,mergeEnd, firstBlock-mergeStart, expL2RSearchGap(firstBlock,mergeEnd, mergeStart, false) );

      return nUnsorted;
    };

    // STEP 2: Sort & Merge
    // --------------------
    final int RUN_LEN = kiwiSortV4_runLen(len);

    int  lenL = 0,
        stack = 0,
    nUnsorted = 0;

    for( int mid=until; mid > from; )
    {
      int                nxt = max(mid-RUN_LEN, from);
      kiwiSortV4_sortRun(nxt,mid);

      // merge runs of equal length
               lenL = mid - nxt;
      for( int lenR = RUN_LEN, s=++stack; (s & 1) == 0;  s>>>=1, lenR<<=1 )
      {
        nUnsorted = merger.applyAsInt(nUnsorted, lenL,mid,lenR);
        mid += lenR;
        lenL+= lenR;
      }

      mid = nxt;
    }

    int n0 = numberOfTrailingZeros(stack);
    stack >>>= n0;

    // merge rest
    int               lenR = RUN_LEN << n0;
    for( int mid=from+lenL;; )
    {
      stack >>>= 1; if( 0 == stack   ) break;
      lenR   <<= 1; if( 0 ==(stack&1)) continue;

      nUnsorted = merger.applyAsInt(nUnsorted, lenL,mid,lenR);
      mid += lenR;
      lenL+= lenR;
    }

    // STEP 3: Merge Up Buffer
    // -----------------------
    if( 0 < nUnsorted )
      heapSortFast(buf, buf+nUnsorted);
    kiwiSortV4_mergeInPlace(buf,from,until);
  }

  /** Partially sorts a range <code>[from,until)</code> such
   *  that the beginning of the range <code>[from,mid)</code>
   *  contains the smallest <code>mid-from</code> elements
   *  int sorted order.
   */
  private void partialSort( int from, int mid, int until )
  {
    quickSelectV1(from,mid,until);
    heapSortFast(mid,until);
  }
}
