package com.github.jaaa.sort;

import com.github.jaaa.compare.ArgMaxAccess;
import com.github.jaaa.compare.ArgMinAccess;
import com.github.jaaa.fn.Int4Consumer;
import com.github.jaaa.merge.BlockRotationMergeAccess;
import com.github.jaaa.merge.TimMergeAccessor;
import com.github.jaaa.permute.BlockSwapAccess;
import com.github.jaaa.search.ExpL2RSearch;

import static java.lang.Math.*;


// Like KiwiSortV5 but with a power of 2 buffer size and run length. In most situation this should avoid
// oddly-sized single blocks that have to be merged separately.
public interface KiwiSortAccess extends ArgMaxAccess, ArgMinAccess, BlockRotationMergeAccess, BlockSwapAccess, ExtractSortBufOrdinalAccess, HeapSortAccess, InsertionAdaptiveSortAccess
{
  int MIN_RUN_LEN = 16;

  TimMergeAccessor<KiwiSortAccess> TIM_MERGE_ACCESSOR = new TimMergeAccessor<KiwiSortAccess>()
  {
    @Override public KiwiSortAccess malloc( int len ) { throw new UnsupportedOperationException(); }
    @Override public int compare( KiwiSortAccess a, int i, KiwiSortAccess b, int j ) { return a.compare(i,j); }
    @Override public void   swap( KiwiSortAccess a, int i, KiwiSortAccess b, int j ) {        a.   swap(i,j); }
    @Override public void   copy( KiwiSortAccess a, int i, KiwiSortAccess b, int j ) {        a.   swap(i,j); }
  };

  static int bufLen( int len )
  {
    // bufLen == (len-bufLen) / bufLen;
    if( len <  0 ) throw new IllegalArgumentException();
    if( len == 0 ) return 0;
    double bufLenMin = nextDown( ceil( (sqrt(len*8d + 1) - 5) / 4 ) );
    int                   exp = getExponent( max(0.5, bufLenMin) ),
            bufLen = 1 << exp+1,
    l = len-bufLen;
    l-= l+1>>>1;
    if(1 > bufLen || l/bufLen > bufLen)
           bufLen <<= 1;
    return bufLen;
  }

  default  int kiwiSort_runLen( int n ) { return TimSort.optimalRunLength(MIN_RUN_LEN,n); }
  default void kiwiSort_sortRun      ( int from,          int until ) { insertionAdaptiveSort(from,until); }
  default void kiwiSort_mergeInPlace ( int from, int mid, int until ) { blockRotationMerge(from,mid,until); }
  default void kiwiSort_mergeBuffered( int a0, int aLen,
                                       int b0, int bLen,
                                       int c0 )
  {
    TIM_MERGE_ACCESSOR.timMergeBiasedL2R(TimMergeAccessor.MIN_GALLOP, this,a0,aLen, this,b0,bLen, this,c0);
  }

  default void kiwiSort( int from, int until )
  {
    if( from < 0 || from > until ) throw new IllegalArgumentException();
    if( from > until - MIN_RUN_LEN*3 ) {
      kiwiSort_sortRun(from,until);
      return;
    }

    // STEP 1: Buffer Extraction
    // -------------------------
    int        len = until-from,
     desiredBufLen = bufLen(len),
            bufLen = extractSortBuf_ordinal_l_sorted(from,until, desiredBufLen, from),
            buf    = from;
    from += bufLen;
    len  -= bufLen;

    int run = kiwiSort_runLen(len);

    // STEP 2: Sort Runs
    // -----------------
    for( int r = until, l = r - len%run;; )
    {
      kiwiSort_sortRun(l,r);
      if( l <= from ) break;
      r = l;
      l-= run;
    }

    // STEP 3: Merging
    // ---------------
    int nUnsorted = 0;
    for( int RUN; run < len; run=RUN )
    {
      if((RUN = 2*run) < 0 ) // <- avoid underflow
          RUN = Integer.MAX_VALUE;

      boolean useBufferedMerge = run/bufLen <= bufLen;
      int B = useBufferedMerge ? min(run,bufLen) : 1 + run/(bufLen+1),
          nBlocksL = run/B;
      if( nBlocksL <= 3 && nBlocksL*B == run ) // <- if there's no rest, we can rearrange with one block less
        --nBlocksL;
      if( nBlocksL > 2 ) {
        int nUnsortedMax = bufLen - nBlocksL;
        if( nUnsortedMax < nUnsorted ) {
          kiwiSort_selectAndSortR(buf, buf+nUnsortedMax, buf+nUnsorted);
          nUnsorted = nUnsortedMax;
        }
      }

      int lastM  = until - len%RUN;
      if( lastM >= until-run ) lastM -= run;
      else                     lastM += run;

      // STEP 3.1: Block Rearrangements
      // ------------------------------
      if( nBlocksL > 0 )
        for( int m = from+run;; )
        {
          int r = m+run;
          if( r - until > 0 ) // <- checks for overflow
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

          if( m == lastM ) break;
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
          kiwiSort_mergeBuffered(buf+1,n-1,  pos,mergeEnd-pos, k+1);
        }
        : (k,mergeEnd, n, pos) -> {
          // IN-PLACE MERGE
          rotate(k,pos, -n);
          kiwiSort_mergeInPlace(pos-n+1, pos, mergeEnd);
        };

      for( int m = lastM, r = lastM <= until-run ? lastM+run : until;  m > from;  r = m-run, m = r-run )
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
    }

    // STEP 4: Merge Up Buffer
    // -----------------------
    if( nUnsorted > 0 )
      heapSort(buf, buf+nUnsorted);
    kiwiSort_mergeInPlace(buf,from,until);
  }

  /** Partially (unstably) sorts a range <code>[from,until)</code>
   *  such that the beginning of the range <code>[mid,until)</code>
   *  contains the largest <code>until-mid</code> elements from
   *  range <code>[from,until)</code> in sorted order.
   */
  default void kiwiSort_selectAndSortR( int from, int mid, int until )
  {
    if( from < 0 || from > mid | mid > until ) throw new IllegalArgumentException();
    if( mid == until ) return;
    if( mid == until-1 ) { swap(mid,  argMaxR(from,until)); return; }

    // PHASE 1: HEAP SELECTION
    // =======================

    // Build Min-Heap with Right-Hand-Side Root
    // ----------------------------------------
    final int      firstParent = mid + until +1 >>> 1;
    for(int root = firstParent; root < until; root++ )
    {
      int parent=root;

      // TRICKLE DOWN
      for(;;) {
        int child = (parent<<1) - until;
        if( child < mid) break;
        if( child > mid)
            child -= compare(child-1,child)>>>31;
        swap(parent,parent=child);
      }

      // BUBBLE UP
      for( int child=parent; root != child; )
      {
        parent = child+ until +1 >>> 1;
        if( compare(child,parent) < 0 )
          swap(child,child=parent);
        else break;
      }
    }

    // Perform selection
    // -----------------
    int root = until-1;
    for( int i=from; i < mid; i++ )
    {
      if( compare(i,root) <= 0 )
        continue;
      swap(i,root);
      for( int parent = root;; ) {
        // SIFT DOWN
        int child = (parent<<1) - until;
        if( child < mid) break;
        if( child > mid)
            child -= compare(child-1,child)>>>31;
        if( compare(parent,child) > 0 )
          swap(parent,parent=child);
        else break;
      }
    }

    // PHASE 2: HEAP SORT
    // ==================
    // We already have a heap, so all we have to do is extract minima from it.
    for( int i=mid; i < root; )
    {
      swap(i++,root);
      int p = root;
      // TRICKLE DOWN
      for(;;) {
        int child = (p<<1) - until;
        if( child < i ) break;
        if( child > i )
            child -= compare(child-1,child)>>>31;
        swap(p,p=child);
      }
      // BUBBLE UP
      for( int child=p; root != child; )
      {
        p = child+until+1 >>> 1;
        if( compare(child,p) < 0 )
          swap(child,child=p);
        else break;
      }
    }
  }
}
