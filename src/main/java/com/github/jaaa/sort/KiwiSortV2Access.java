package com.github.jaaa.sort;

import com.github.jaaa.ArgMinAccess;
import com.github.jaaa.merge.BlockRotationMergeAccess;
import com.github.jaaa.merge.TimMergeAccessor;
import com.github.jaaa.misc.BlockSwapAccess;
import com.github.jaaa.search.ExpL2RSearch;

import static java.lang.Math.*;


public interface KiwiSortV2Access extends ArgMinAccess, BlockRotationMergeAccess, BlockSwapAccess, ExtractSortBufOrdinalAccess, HeapSortFastAccess, InsertionAdaptiveSortAccess
{
  int MIN_RUN_LEN = 16;

  static int minBufLen( int len )
  {
    if( len < 0 ) throw new IllegalArgumentException();
    int         bufLen = (int) ceil( ( sqrt(len*8d + 1) - 5 ) / 4 );
    int l = len-bufLen;
        l -= l+1>>>1;
    if(1 > bufLen || l/bufLen > bufLen )
         ++bufLen;
    return bufLen;
  }

  default  int kiwiSortV2_runLen( int n ) { return TimSort.optimalRunLength(MIN_RUN_LEN,n); }
  default void kiwiSortV2_sortRun      ( int from,          int until ) { insertionAdaptiveSort(from,until); }
  default void kiwiSortV2_mergeInPlace ( int from, int mid, int until ) { blockRotationMerge(from,mid,until); }
  default void kiwiSortV2_mergeBuffered( int a0, int aLen,
                                         int b0, int bLen,
                                         int c0 )
  {
    new TimMergeAccessor<Void>() {
      @Override public Void malloc( int len ) { throw new AssertionError(); }
      @Override public int compare( Void a, int i, Void b, int j ) { return KiwiSortV2Access.this.compare(i,j); }
      @Override public void   swap( Void a, int i, Void b, int j ) {        KiwiSortV2Access.this.   swap(i,j); }
      @Override public void   copy( Void a, int i, Void b, int j ) {        KiwiSortV2Access.this.   swap(i,j); }
    }._timMergeL2R(TimMergeAccessor.MIN_GALLOP, null,a0,aLen, null,b0,bLen, null,c0);
  }

  default void kiwiSortV2( int from, int until )
  {
    if( from < 0 || from > until ) throw new IllegalArgumentException();
    if( from > until - MIN_RUN_LEN*3 ) {
      kiwiSortV2_sortRun(from,until);
      return;
    }

    // STEP 1: Buffer Extraction
    // -------------------------
    int               len = until-from,
            desiredBufLen = minBufLen(len),
            bufLen = extractSortBuf_ordinal_l_sorted(from,until, desiredBufLen, from),
            buf    = from;
    from += bufLen;
    len  -= bufLen;

    int run = kiwiSortV2_runLen(len);

    // STEP 2: Sort Runs
    // -----------------
    for( int l = from, r = l + len%run;; )
    {
      kiwiSortV2_sortRun(l,r);
      if( r==until ) break;
      l = r;
      r+= run;
    }

    // STEP 3: Merging
    // ---------------

    /* A broadcast version of @StableOptimalBlockMergeV2Access,
     * which allows to first rearrange all blocks and then merge
     * them in a consecutive step. This avoids
     */
    final class KiwiMerger
    {
      public boolean usesBufferedMerge=false;
      public int lenL = -1, nBlocksL = -1, B = -1,
                 lenR = -1, nBlocks  = -1;

      public void init( int _lenL, int _lenR ) {
//        assert _lenL <= _lenR;
//        assert _lenL >= 0;
        lenL = _lenL;
        lenR = _lenR;
            usesBufferedMerge =     lenL/bufLen <= bufLen;
        B = usesBufferedMerge ? min(lenL,bufLen) : 1 + lenL/(bufLen+1);
            nBlocksL = lenL/B;
        if( nBlocksL*B == lenL ) // <- if there's no rest, we can rearrange with one block less
            nBlocksL--;
            nBlocks  = lenR/B + nBlocksL;
      }

      public void mergeBlock( int l, int r, int n, int pos )
      {
        if( usesBufferedMerge ) {
          // BUFFERED MERGE
          blockSwap(buf,l, n);

          // roll up to pos
          for(; l+n < pos; l++ )
            swap(l,l+n);

          swap(buf,l); // <- insert 1st elem.
          kiwiSortV2_mergeBuffered(buf+1,n-1,  pos,r-pos, l+1);
        }
        else {
          // IN-PLACE MERGE
          rotate(l,pos, -n);
          kiwiSortV2_mergeInPlace(pos-n+1, pos, r);
        }
      }

      public void rearrangeBlocks( int mid )
      {
        if( 0==nBlocksL )
          return;

        int firstBlock = mid - B*nBlocksL;

        for(
          int pos = 0, // <- position of the remaining rolling blocks
              min = 0,
              mib = buf, // <- movement imitation buffer
          nRemain = nBlocksL;; // <- number of blocks not yet in place
        )
        {
          int key = firstBlock + B*(pos+min),
              idx = ExpL2RSearch.searchGapL( pos+nRemain,nBlocks, i -> compare(key, firstBlock + i*B + B-1) );
              idx-= nRemain;

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
          pos += 1;
          mib += 1;

          // find next smallest block with the help of the MIB
          min = argMinL(mib,mib+nRemain) - mib;
        }
      }

      public void mergeLocally( int mid )
      {
        int   from = mid-lenL,
             until = mid+lenR,
          mergeEnd = until,
        firstBlock = mid - B*nBlocksL;

        for( int i=firstBlock + nBlocks*B < until ? nBlocks : nBlocks-1; i > 0; i-- )
        {
          int m = firstBlock + i*B;
          if( compare(m-1,m) > 0 ) {
            int        l = m-B,       pos = expL2RSearchGap(m,min(m+B,mergeEnd), l, /*rightBias=*/false); // <- find pos. of 1st elem.
            mergeBlock(l,mergeEnd, B, pos);
            mergeEnd = pos-B;
          }
        }

        if( from < firstBlock )
          mergeBlock( from,mergeEnd, firstBlock-from, expL2RSearchGap(firstBlock,mergeEnd, from, /*rightBias=*/false) );
      }
    }

    var merger0 = new KiwiMerger();
    var merger  = new KiwiMerger();

    for( int RUN; run < len; run=RUN )
    {
      if((RUN = 2*run) < 0 ) // <- avoid underflow
          RUN = Integer.MAX_VALUE;

      // STEP 3.1: Block Rearrangements
      // ------------------------------

      // rearrange left odd pair of neighbors
      int m0 = from + len%RUN - run;
      if( m0 > from ) {
        merger0.init(m0-from,run);
        merger0.rearrangeBlocks(m0);
      }

      // rerrange remaining pairs of neighbors
      merger.init(run,run);
      for( int m=m0; (m+=run) < until; )
        merger.rearrangeBlocks(m+=run);

      // STEP 3.2: Local Merges
      // ----------------------

      // merge remaining pairs of neighbors
      for(  int l=until;; ) {
        int m = l-run;
        if((l = m-run) < from ) break;
        merger.mergeLocally(m);
      }

      // merge left off pair of neighbors
      if( m0 > from )
        merger0.mergeLocally(m0);

      // STEP 3.3: Clean Up Merge Buffer
      // -------------------------------
      // TODO: Instead of cleaning up the entire buffer after a sweep
      //       of merges, we could only clean up only nBlocksL entries
      //       before block rearrangement and only do so if that region
      //       is dirty from merging and if 2 < nBlocksL.
      int                           nSort = 0;
      if(merger0.usesBufferedMerge) nSort =     merger0.B;
      if(merger. usesBufferedMerge) nSort = max(merger .B,nSort);
      heapSortFast(buf,buf+nSort);
    }

    // STEP 4: Merge Up Buffer
    // -----------------------
    kiwiSortV2_mergeInPlace(buf,from,until);
  }
}
