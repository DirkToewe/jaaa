package com.github.jaaa.merge;

import com.github.jaaa.ArgMinAccess;
import com.github.jaaa.fn.Int4Consumer;
import com.github.jaaa.misc.BlockSwapAccess;
import com.github.jaaa.search.ExpL2RSearch;
import com.github.jaaa.sort.HeapSortFastAccess;

import static java.lang.Math.*;


// Like WikiMergeV4 but with the kiwiMerge fallback copy-pasted into this interface as `wikiMergeV5_kiwiMergeL2R_usingBuffer`.
public interface WikiMergeV5Access extends ArgMinAccess, BlockRotationMergeAccess, BlockSwapAccess, ExtractMergeBufOrdinalAccess, HeapSortFastAccess
{
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

  default void wikiMergeV5_mergeInPlace ( int from, int mid, int until ) { blockRotationMerge(from,mid,until); }
  default void wikiMergeV5_mergeBuffered( int a0, int aLen,
                                          int b0, int bLen,
                                          int c0 )
  {
    new TimMergeAccessor<Void>() {
      @Override public Void malloc( int len ) { throw new AssertionError(); }
      @Override public void   swap( Void a, int i, Void b, int j ) { throw new AssertionError(); }
      @Override public void   copy( Void a, int i, Void b, int j ) {        WikiMergeV5Access.this.   swap(i,j); }
      @Override public int compare( Void a, int i, Void b, int j ) { return WikiMergeV5Access.this.compare(i,j); }
    }.timMerge(null,a0,aLen, null,b0,bLen, null,c0);
  }

  default void wikiMergeV5( int from, int mid, int until )
  {
    if( mid-from <= until-mid ) wikiMergeV5_L2R(from,mid,until);
    else                        wikiMergeV5_R2L(from,mid,until);
  }

  default void wikiMergeV5_L2R( int from, int mid, int until )
  {
    if( from < 0 || from > mid || until < mid ) throw new IllegalArgumentException();
    if( mid == until || mid == from ) return;

    int lenL =   mid-from,
        len  = until-from;

    if( lenL < 7 || lenL < sqrt(len) )
      wikiMergeV5_mergeInPlace(from,mid,until);
    else {
      // STEP 1: Extract BUFFER
      // ======================
      // The buffer is used both as movement-imitation buffer (MIB) and
      // merge buffer.
      int bufLen = extractMergeBuf_ordinal_l_min_unsorted(from,mid, minBufLen(lenL), from),
          merLen = merBufLen(lenL-bufLen, bufLen);

      heapSortFast(from+merLen, from+bufLen);

      wikiMergeV5_L2R_usingBuffer(from+bufLen, mid, until, from, bufLen);

      if( merLen == 0 )
          merLen = bufLen;
      else {
        // USE BUFFER TO MERGE MIB
        int  i = from+merLen,
          dest = expL2RSearchGapL(from+bufLen,until, i),
        mibLen = bufLen-merLen;

        blockSwap(from,i, mibLen);

        // roll up to pos
        for( ;   i+mibLen < dest; i++ )
          swap(i,i+mibLen);

        swap(from,i); // <- insert 1st elem.
        wikiMergeV5_mergeBuffered(from+1,mibLen-1,  dest,until-dest, i+1);

        // SORT MERGE BUFFER
        heapSortFast(from, from+merLen);
      }
      wikiMergeV5_mergeInPlace(from, from+merLen, until);
    }
  }

  default void wikiMergeV5_L2R_usingBuffer( int from, int mid, int until, int buf, int bufLen )
  {
    if( from < 0 || buf < 0 || bufLen <= 0 || mid < from || mid > until )
      throw new IllegalArgumentException();
    if( mid == from || mid == until ) return;

    int lenL = mid-from;

    // STEP 1: Extract BUFFER
    // ======================
    // The buffer is use both as movement-imitation buffer (MIB) and
    // merge buffer (MER).
    final int B = merBufLen(lenL, bufLen);
    if(  0 == B ) {
      // fallback to kiwiMerge
      wikiMergeV5_kiwiMergeL2R_usingBuffer(from,mid,until, buf,bufLen);
      return;
    }

    int  MER = buf,
         MIB = MER + B,
    nBlocksL = lenL / B,
     block0  = mid - nBlocksL*B;

    // STEP 2: Block Rearrangement and Local Merges
    // ============================================
    // The full blocks of the left sequence are rolled in place. Each block is
    // moved as far right as possible, without having to move elements to the
    // left during local merges. This can be achieved by binary searching the
    // first entry of a left block in the last entries of each remaining right
    // block to find the insertion point.

    Int4Consumer merge = (l,r, n, dest) -> {
      // BUFFERED MERGE
      blockSwap(MER,l, n);

      // roll up to pos
      for(; l+n < dest; l++ )
        swap(l,l+n);

      swap(MER,l); // <- insert 1st elem.
      wikiMergeV5_mergeBuffered(MER+1,n-1,  dest,r-dest, l+1);
    };

    int firstDest = expL2RSearchGap(mid,until, block0, false);

    for( int min=0, pos=mid, dest=firstDest, nRemain = nBlocksL; nRemain > 0; ) // <- number of blocks not yet in place
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

        dest = expL2RSearchGap(prevDest,until, pos + (min-nRemain)*B, false);
      }
      else
        dest = until;

      merge.accept(pos, dest, B, prevDest);
    }

    // STEP 4: Merge up left odd end
    // =============================
    if( from < block0 ) {
      firstDest -= nBlocksL*B;
      merge.accept( from,firstDest, block0-from, expL2RSearchGap(block0,firstDest, from, false) );
    }

    revert(MIB, MIB+nBlocksL); // <- during block rearrangement, MIB was reverted
  }

  private void wikiMergeV5_kiwiMergeL2R_usingBuffer( int from, int mid, int until, int buf, int bufLen )
  {
    int lenL =   mid-from,
        lenR = until-mid;

    boolean useBufferedMerge = lenL / bufLen <= bufLen;
    int B = useBufferedMerge ? min(lenL,bufLen) : 1 + lenL/(bufLen+1);

    Int4Consumer merge = useBufferedMerge
      ? (l,r, n, pos) -> {
        // BUFFERED MERGE
        blockSwap(buf,l, n);

        // roll up to pos
        for(; l+n < pos; l++ )
          swap(l,l+n);

        swap(buf,l); // <- insert 1st elem.
        wikiMergeV5_mergeBuffered(buf+1,n-1,  pos,r-pos, l+1);
      }
      : (l,r, n, pos) -> {
        // IN-PLACE MERGE
        rotate(l,pos, -n);
        wikiMergeV5_mergeInPlace(pos-n+1, pos, r);
      };

    int mergeEnd = until,
        nBlocksL = lenL/B,
    firstBlock   = mid - B*nBlocksL;

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
      for( int i=firstBlock + nBlocks*B < until ? nBlocks : nBlocks-1; i > 0; i-- )
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
    if( from < firstBlock )
      merge.accept( from,mergeEnd, firstBlock-from, expL2RSearchGap(firstBlock,mergeEnd, from, false) );

    // if we used MIB as merge buffer, it might be out of order
    if( useBufferedMerge )
      heapSortFast(buf, buf+B);
  }

  default void wikiMergeV5_R2L( int from, int mid, int until )
  {
    if( from < 0 || from > mid || until < mid ) throw new IllegalArgumentException();

    int last = until-1;
    new WikiMergeV5Access()
    {
      @Override public void wikiMergeV5_mergeInPlace ( int frm, int md, int ntl ) { WikiMergeV5Access.this.wikiMergeV5_mergeInPlace(until-ntl, until-md, until-frm); }
      @Override public void wikiMergeV5_mergeBuffered( int a0, int aLen, int b0, int bLen, int c0 ) {
        WikiMergeV5Access.this.wikiMergeV5_mergeBuffered(
          until-b0-bLen,bLen,
          until-a0-aLen,aLen,
          until-c0-aLen-bLen
        );
      }
      @Override public void   swap( int i, int j ) {        WikiMergeV5Access.this.   swap(last-j, last-i); }
      @Override public int compare( int i, int j ) { return WikiMergeV5Access.this.compare(last-j, last-i); }
    }.wikiMergeV5_L2R(0, until-mid, until-from);
  }
}
