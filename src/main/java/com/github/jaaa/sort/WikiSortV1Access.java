package com.github.jaaa.sort;

import com.github.jaaa.compare.ArgMinAccess;
import com.github.jaaa.fn.Int4Consumer;
import com.github.jaaa.merge.BlockRotationMergeAccess;
import com.github.jaaa.merge.TimMergeAccessor;
import com.github.jaaa.permute.BlockSwapAccess;
import com.github.jaaa.search.ExpL2RSearch;

import static java.lang.Math.*;


public interface WikiSortV1Access extends ArgMinAccess, BlockRotationMergeAccess, BlockSwapAccess, ExtractSortBufOrdinalAccess, HeapSortFastAccess, InsertionAdaptiveSortAccess
{
  int MIN_RUN_LEN = 16;

  static int minBufLen( int len )
  {
    // x: minBufLen/2
    // l: len
    // l-2*x = x²  =>  l = x² + 2*x = (x+1) - 1  =>  x = √(l+1) - 1
    if( len < 0 ) throw new IllegalArgumentException();
    return (int) ceil( sqrt(len+1d)*2 - 2 );
  }

  static int merBufLen( int len, int bufLen )
  {
    if( len < 0 || bufLen < 0 )
      throw new IllegalArgumentException();
    // x*(b-x) = l   =>  x = (b - √(b² - 4l)) / 2
    double      b = bufLen,
        delta = b*b - 4d*len;
    if( delta < 0 ) return 0;
    int x = (int)( (b + sqrt(delta)) / 2 ),
        y = bufLen - x;
    return max(x,y);
  }

  default int  wikiSortV1_runLen( int n ) { return TimSort.optimalRunLength(MIN_RUN_LEN,n); }
  default void wikiSortV1_sortRun( int from, int until ) { insertionAdaptiveSort(from,until); }
  default void wikiSortV1_mergeInPlace ( int from, int mid, int until ) { blockRotationMerge(from,mid,until); }
  default void wikiSortV1_mergeBuffered( int a0, int aLen,
                                         int b0, int bLen,
                                         int c0 )
  {
    new TimMergeAccessor<Void>() {
      @Override public Void malloc( int len ) { throw new AssertionError(); }
      @Override public int compare( Void a, int i, Void b, int j ) { return WikiSortV1Access.this.compare(i,j); }
      @Override public void   swap( Void a, int i, Void b, int j ) {        WikiSortV1Access.this.   swap(i,j); }
      @Override public void   copy( Void a, int i, Void b, int j ) {        WikiSortV1Access.this.   swap(i,j); }
    }._timMergeL2R(TimMergeAccessor.MIN_GALLOP, null,a0,aLen, null,b0,bLen, null,c0);
  }

  default void wikiSortV1( int from, int until )
  {
    if( from < 0 || from > until ) throw new IllegalArgumentException();
    if( from > until - MIN_RUN_LEN*3 ) {
      wikiSortV1_sortRun(from,until);
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

    int run = wikiSortV1_runLen(len);

    // STEP 2: Sort Runs
    // -----------------
    for( int r = until, l = r - len%run;; )
    {
      wikiSortV1_sortRun(l,r);
      if( l <= from ) break;
      r = l;
      l-= run;
    }

    // STEP 3: Merging
    // ---------------
    int nUnsorted = 0;
    for(; run-len < 0; run<<=1 )
      nUnsorted = wikiSortV1_mergeLvl_wiki(from,until, run, buf,bufLen, nUnsorted);

    // STEP 4: Merge Up Buffer
    // -----------------------
    if( nUnsorted > 0 )
      heapSortFast(buf, buf+nUnsorted);
    wikiSortV1_mergeInPlace(buf,from,until);
  }

  private int wikiSortV1_mergeLvl_wiki( int _from, int _until, int run, int buf, int bufLen, int nUnsorted )
  {
    // STEP 1: Extract BUFFER
    // ======================
    // The buffer is use both as movement-imitation buffer (MIB) and
    // merge buffer (MER).
    int B = merBufLen(run, bufLen);
    if( B == 0 ) // kiwiMerge fallback
      return wikiSortV1_mergeLvl_kiwi(_from,_until, run, buf,bufLen, nUnsorted);

    final int MER = buf,
        MIB = MER + B;

    int nBlocksL = run/B;
    if( nBlocksL <= 3 && nBlocksL*B == run ) // <- if there's no rest, we can rearrange with one block less
      --nBlocksL;
    if( nBlocksL > 2 ) {
      int nUnsortedMax = bufLen - nBlocksL;
      if( nUnsortedMax < nUnsorted ) {
        partialSort(buf, buf+nUnsortedMax, buf+nUnsorted);
        nUnsorted = nUnsortedMax;
      }
    }
    nUnsorted = max(nUnsorted, B);

    Int4Consumer merge = (l,r, n, dest) -> {
      // BUFFERED MERGE
      blockSwap(MER,l, n);

      // roll up to pos
      for(; l+n < dest; l++ )
        swap(l,l+n);

      swap(MER,l); // <- insert 1st elem.
      wikiSortV1_mergeBuffered(MER+1,n-1,  dest,r-dest, l+1);
    };

    // STEP 3.1: Block Rearrangements
    // ------------------------------
    for( int m=_from+run;;)
    {
      int r = m + run;
      if( r - _until > 0 ) // <- checks for overflow
          r = _until;
      int l = m - run;

      int block0 = m - B*nBlocksL,
       firstDest = r;

      if( nBlocksL > 0 )
        for( int min=0, pos=m, dest=firstDest=expL2RSearchGap(m,r, block0, false), nRemain=nBlocksL; nRemain > 0; ) // <- number of blocks not yet in place
        {
          // adjust min index and MIB to account for the block rolling
          int        roll = (pos - dest) / B;
              min += roll % nRemain;
          if( min < 0 )
              min += nRemain;
          rotate(MIB, MIB+nRemain, roll);

          // roll blocks forward to destination
          for( ; pos <= dest-B; pos += B )
            blockSwap(pos, pos - nRemain*B, B);

          int prevDest = dest;

          pos -= B;
          if( --nRemain > 0 )
          {
            // move min. block to the very right, so it is in place
            blockSwap( pos, pos + (min-nRemain)*B, B );
            swap(MIB+nRemain, MIB+min); // <- as a side effect this sorts the MIB

            // find next smallest block with the help of the MIB
            min = argMinL(MIB, MIB+nRemain) - MIB;

            dest = expL2RSearchGap(prevDest,r, pos + (min-nRemain)*B, false);
          }
          else
            dest = r;

          merge.accept(pos, dest, B, prevDest);
        }

      // STEP 4: Merge up left odd end
      // =============================
      if( l < block0 ) {
        firstDest -= nBlocksL*B;
        merge.accept( l,firstDest, block0-l, expL2RSearchGap(block0,firstDest, l, false) );
      }

      revert(MIB, MIB+nBlocksL); // <- during block rearrangement, MIB was reverted

      if( r >= _until-run ) return nUnsorted;
      m = r+run;
    }
  }

  private int wikiSortV1_mergeLvl_kiwi( int from, int until, int run, int buf, int bufLen, int nUnsorted )
  {
    boolean useBufferedMerge = run/bufLen <= bufLen;
    int B = useBufferedMerge ? min(run,bufLen) : 1 + run/(bufLen+1),
        nBlocksL = run/B;
    if( nBlocksL <= 3 && nBlocksL*B == run ) // <- if there's no rest, we can rearrange with one block less
      --nBlocksL;
    if( nBlocksL > 2 ) {
      int nUnsortedMax = bufLen - nBlocksL;
      if( nUnsortedMax < nUnsorted ) {
        partialSort(buf, buf+nUnsortedMax, buf+nUnsorted);
        nUnsorted = nUnsortedMax;
      }
    }

    int m,r;

    // STEP 3.1: Block Rearrangements
    // ------------------------------
    if( nBlocksL <= 0 ) {
      m = until;
      m -= (until-from) % (run*2L);
      if( m < until-run ) m += run;
      else                m -= run;
      r = m < until-run ? m +  run : until;
    }
    else for( m=from+run;;)
    {
      if((r = m+run) - until > 0 ) // <- checks for overflow
          r = until;

      int  nR = r - m,
      nBlocks = nR/B + nBlocksL,
       block0 = m - B*nBlocksL;

      for(
        int pos = 0, // <- position of the remaining rolling blocks
            min = 0,
            mib = buf+bufLen-nBlocksL, // <- movement imitation buffer
        nRemain = nBlocksL;; // <- number of blocks not yet in place
      )
      {
        int key = block0 + B*(pos+min),
            idx = ExpL2RSearch.searchGapL( pos+nRemain,nBlocks, i -> compare(key, block0 + i*B + B-1) );
            idx-= nRemain;

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
        ++pos;
        ++mib;

        // find next smallest block with the help of the MIB
        min = argMinL(mib,mib+nRemain) - mib;
      }

      if( r >= until-run ) break;
      m = r+run;
    }

    // STEP 3.2: Local Merges
    // ----------------------
    Int4Consumer merge = useBufferedMerge
      ? (k,mergeEnd, n, pos) -> {
        // BUFFERED MERGE
        blockSwap(buf,k, n);

        // roll up to pos
        for(; k+n < pos; k++ )
          swap(k,k+n);

        swap(buf,k); // <- insert 1st elem.
        wikiSortV1_mergeBuffered(buf+1,n-1,  pos,mergeEnd-pos, k+1);
      }
      : (k,mergeEnd, n, pos) -> {
        // IN-PLACE MERGE
        rotate(k,pos, -n);
        wikiSortV1_mergeInPlace(pos-n+1, pos, mergeEnd);
      };

    for(; m > from; r = m-run, m = r-run )
    {
      int lenR = r-m,
        nBlocks = lenR/B + nBlocksL,
         block0 = m - B*nBlocksL,
       mergeEnd = r;

      for( int i = block0 + B*nBlocks < r ? nBlocks : nBlocks-1; i > 0; i-- )
      {    int j = block0 + B*i;
        if( compare(j-1,j) > 0 ) {
          int          k = j-B,       pos = expL2RSearchGap(j,min(j+B,mergeEnd), k, /*rightBias=*/false); // <- find pos. of 1st elem.
          merge.accept(k,mergeEnd, B, pos);
          mergeEnd = pos-B;
        }
      }

      int l = m-run;
      if( l < block0 )
        merge.accept( l,mergeEnd, block0-l, expL2RSearchGap(block0,mergeEnd, l, /*rightBias=*/false) );
    }

    // STEP 3.3: Clean Up Merge Buffer
    // -------------------------------
    if(useBufferedMerge) nUnsorted = max(nUnsorted, B);
    return nUnsorted;
  }

  /** Partially sorts a range <code>[from,until)</code> such
   *  that the beginning of the range <code>[mid,until)</code>
   *  contains the largest <code>until-mid</code> elements from
   *  range <code>[from,until)</code> in sorted order.
   */
  private void partialSort( int from, int mid, int until )
  {
    if( from < 0 || from > mid || mid >= until ) throw new IllegalArgumentException();

    // uses a partial variant of HeapSortFast
    final int     lastParent = -1 + (from+until-- >>> 1);
    for( int root=lastParent; mid <= until; )
    {
      int parent=root;

      // TRICKLE DOWN
      for(;;) {
        int child = (parent<<1) + 1 - from;
        if( child > until ) break;
        if( child < until )
            child += compare(child,child+1)>>>31;
        swap(parent,parent=child);
      }

      // BUBBLE UP
      for( int child=parent; root != child; )
      {
        parent = child+from-1 >>> 1;
        if( compare(child,parent) > 0 )
          swap(child,child=parent);
        else break;
      }

      if( root > from )
        --root; // <- HEAP BUILDING PHASE
      else
        swap(until--,root); // <- MAX. EXTRACTION PHASE
    }
  }
}
