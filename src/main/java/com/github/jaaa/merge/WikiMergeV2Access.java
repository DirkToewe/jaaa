package com.github.jaaa.merge;

import com.github.jaaa.ArgMinAccess;
import com.github.jaaa.fn.Int4Consumer;
import com.github.jaaa.misc.BlockSwapAccess;
import com.github.jaaa.sort.HeapSortFastAccess;

import static java.lang.Math.ceil;
import static java.lang.Math.sqrt;


public interface WikiMergeV2Access extends ArgMinAccess, BlockRotationMergeAccess, BlockSwapAccess, ExtractMergeBufOrdinalAccess, HeapSortFastAccess
{
  static int minBufLenMIB( int len )
  {
    if( len < 0 ) throw new IllegalArgumentException();
    int           MER_len = minBufLenMER(len);
    return len / (MER_len+1);
  }

  static int minBufLenMER( int len )
  {
    if( len < 0 ) throw new IllegalArgumentException();
    return (int) ceil(sqrt(len+1d)) - 1;
  }

  default void wikiMergeV2_mergeInPlace ( int from, int mid, int until ) { blockRotationMerge(from,mid,until); }
  default void wikiMergeV2_mergeBuffered( int a0, int aLen,
                                          int b0, int bLen,
                                          int c0 )
  {
    new TimMergeAccessor<Void>() {
      @Override public Void malloc( int len ) { throw new AssertionError(); }
      @Override public void   swap( Void a, int i, Void b, int j ) { throw new AssertionError(); }
      @Override public void   copy( Void a, int i, Void b, int j ) {        WikiMergeV2Access.this.   swap(i,j); }
      @Override public int compare( Void a, int i, Void b, int j ) { return WikiMergeV2Access.this.compare(i,j); }
    }.timMerge(null,a0,aLen, null,b0,bLen, null,c0);
  }

  default void wikiMergeV2( int from, int mid, int until )
  {
    if( mid-from <= until-mid ) wikiMergeV2_L2R(from,mid,until);
    else                        wikiMergeV2_R2L(from,mid,until);
  }

  default void wikiMergeV2_L2R( int from, int mid, int until )
  {
    if( from < 0   ) throw new IllegalArgumentException();
    if( from > mid ) throw new IllegalArgumentException(); if( mid == until) return;
    if(until < mid ) throw new IllegalArgumentException(); if( mid == from ) return;

    int lenL =   mid-from,
        lenR = until-mid;

    if( lenL < 7 || lenL < sqrt(lenR) ) {
      wikiMergeV2_mergeInPlace(from,mid,until);
      return;
    }

    int MIB_desiredLen = minBufLenMIB(lenL),
        MER_desiredLen = minBufLenMER(lenL),
            desiredLen = MER_desiredLen + MIB_desiredLen;
    assert MIB_desiredLen > 0;
    assert MIB_desiredLen <= MER_desiredLen;

    // STEP 1: Extract BUFFER
    // ======================
    // The buffer is use both as movement-imitation buffer (MIB) and
    // merge buffer (MER).
    final int MIB, MIB_len,
              MER, MER_len;
    {
      int len = extractMergeBuf_ordinal_l_min_unsorted(from,mid, desiredLen, from);
      MER = from;        MER_len = len < desiredLen ? 0 : MER_desiredLen;
      MIB = MER+MER_len; MIB_len = len-MER_len;
    }
    heapSortFast(MIB,MIB+MIB_len);

    final int B = MER_len==0 ? (lenL+1) / (MIB_len+1) : MER_len,
        blocksL = (lenL-MIB_len-MER_len) / B,
         block0 = mid - blocksL*B;

    assert MER_len == MER_desiredLen
     || (lenL-MIB_len-MER_len) /  B   <= MIB_len
     && (lenL-MIB_len-MER_len) / (B-1) > MIB_len;

    // STEP 2: Block Rearrangement
    // ===========================
    // The full blocks of the left sequence are rolled in place. Each block is
    // moved as far right as possible, without having to move elements to the
    // left during local merges. This can be achieved by binary searching the
    // first entry of a left block in the last entries of each remaining right
    // block to find the insertion point.

    Int4Consumer bufMerge = (l,r, n, dest) -> {
      // BUFFERED MERGE
      blockSwap(MER,l, n);

      // roll up to pos
      for(; l+n < dest; l++ )
        swap(l,l+n);

      swap(MER,l); // <- insert 1st elem.
      wikiMergeV2_mergeBuffered(MER+1,n-1,  dest,r-dest, l+1);
    };

    Int4Consumer merge = MER_len < MER_desiredLen
      ? (l, r, n, dest) -> {
        // INPLACE MERGE
        rotate(l,dest, -n);
        wikiMergeV2_mergeInPlace(dest-n+1, dest, r);
      }
      : bufMerge;

    int prevDest = -1,
       firstDest = until,
             pos = block0; // <- position of the remaining rolling blocks

    loop: for(
      int min = 0,
      nRemain = blocksL;; // <- number of blocks not yet in place
    )
    {
      int end = pos + nRemain*B;

      int dest = expL2RSearchGap(
        0 <= prevDest ? prevDest : end,
        until,
        pos + min*B,
        false
      );

      if( 0 <= prevDest )
        merge.accept(end,dest, B, prevDest);
      else
        firstDest = dest;
      prevDest = dest;

      int roll = (end - dest) / B;

      // block roll
      for( ; pos + nRemain*B + B <= dest; pos += B )
        blockSwap(pos, pos + nRemain*B, B);

      // adjust min index and MIB to account for the block rolling
          min += roll % nRemain;
      if( min < 0 )
          min += nRemain;
      rotate(MIB, MIB+nRemain, roll);

      --nRemain;

      // move min. block to the very left so it is in place
      blockSwap( pos + nRemain*B, pos + min*B, B );
      swap(MIB+nRemain, MIB+min); // <- as a side effect this sorts the MIB

      if( nRemain <= 0 )
        break loop;

      // find next smallest block with the help of the MIB
      min = argMinL(MIB,MIB+nRemain) - MIB;
    }

    assert prevDest >= 0;
    merge.accept(pos,until, B, prevDest);

    // STEP 4: Merge up left odd end
    // =============================
    int buf_end = from+MER_len+MIB_len;
    if( buf_end < block0 ) {
      firstDest -= blocksL*B;
      merge.accept( buf_end,firstDest, block0-buf_end, expL2RSearchGap(block0,firstDest, buf_end, false) );
    }

    revert(MIB, MIB+blocksL); // <- during block rearrangement, MIB was reverted

    if( MER_len==0 )
      wikiMergeV2_mergeInPlace(MIB, MIB+MIB_len, until);
    else {
      bufMerge.accept( MIB,until, MIB_len, expL2RSearchGap(MIB+MIB_len,until, MIB, false) );

      heapSortFast(MER,MER+MER_len);
      wikiMergeV2_mergeInPlace(MER, MER+MER_len, until);
    }
  }

  default void wikiMergeV2_R2L( int from, int mid, int until )
  {
    if( from  < 0   ) throw new IllegalArgumentException();
    if( from  > mid ) throw new IllegalArgumentException();
    if( until < mid ) throw new IllegalArgumentException();

    int last = until-1;
    new WikiMergeV2Access()
    {
      @Override public void wikiMergeV2_mergeInPlace  ( int frm, int md, int ntl ) { WikiMergeV2Access.this.wikiMergeV2_mergeInPlace(until-ntl, until-md, until-frm); }
      @Override public void wikiMergeV2_mergeBuffered ( int a0, int aLen, int b0, int bLen, int c0 ) {
        WikiMergeV2Access.this.wikiMergeV2_mergeBuffered(
          until-b0-bLen,bLen,
          until-a0-aLen,aLen,
          until-c0-aLen-bLen
        );
      }
      @Override public void   swap( int i, int j ) {        WikiMergeV2Access.this.   swap(last-j, last-i); }
      @Override public int compare( int i, int j ) { return WikiMergeV2Access.this.compare(last-j, last-i); }
    }.wikiMergeV2_L2R(0, until-mid, until-from);
  }
}
