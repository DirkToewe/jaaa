package com.github.jaaa.merge;

import com.github.jaaa.ArgMinAccess;
import com.github.jaaa.fn.Int4Consumer;
import com.github.jaaa.misc.BlockSwapAccess;
import com.github.jaaa.sort.HeapSortFastAccess;

import static java.lang.Math.*;


// WikiMerge is a standalone version of the stable in-place merging algorithm
// used in WikiSort [1]. It is an optimized variant of StableOptimalBlockMerge,
// which was originally presented in [2].
//
// WikiMerge replaces the "Block Distribution Storage" (BDS) of the original
// algorithm with an ad-hoc local merge buffer. The local merges are performed
// right away whenever a block has been rearranged to its destination. Unlike
// StableOptimalBlockMerge there are no two separate phases for block rearrangement
// and local merges. Rearrangement and local merges are performed intermittently.
//
// References
// ----------
// .. [2] "Ratio Based Stable In-Place Merging"
//         Pok-Son Kim & Arne Kutzner
//         Theory and Applications of Models of Computation, pp. 246-257, 2008
public interface WikiMergeV1Access extends ArgMinAccess, BlockRotationMergeAccess, BlockSwapAccess, ExtractMergeBufOrdinalAccess, HeapSortFastAccess
{
  private static int minBufLen( int len )
  {
    // x: minBufLen/2
    // l: len
    // l-2*x = x²  =>  l = x² + 2*x = (x+1) - 1  =>  x = √(l+1) - 1
    if( len < 0 ) throw new IllegalArgumentException();
    return (int) ceil( sqrt(len+1d)*2 - 2 );
  }

  static int minBufLenMIB( int len )
  {
    if( len < 0 ) throw new IllegalArgumentException();
    // x*(b-x) = l   =>  x = (b - √(b² - 4l)) / 2
    int         bufLen = minBufLen(len);
    double  b = bufLen,
        delta = b*b - 4d*(len-bufLen);
    if( delta < 0 ) return 0;
    int x = (int)( (b + sqrt(delta)) / 2 ),
        y = bufLen - x;
    return min(x,y);
  }

  static int minBufLenMER( int len )
  {
    if( len < 0 ) throw new IllegalArgumentException();
    // x*(b-x) = l   =>  x = (b - √(b² - 4l)) / 2
    int         bufLen = minBufLen(len);
    double  b = bufLen,
        delta = b*b - 4d*(len-bufLen);
    if( delta < 0 ) return 0;
    int x = (int)( (b + sqrt(delta)) / 2 ),
        y = bufLen - x;
    return max(x,y);
  }

  default void wikiMergeV1_mergeInPlace ( int from, int mid, int until ) { blockRotationMerge(from,mid,until); }
  default void wikiMergeV1_mergeBuffered( int a0, int aLen,
                                          int b0, int bLen,
                                          int c0 )
  {
    new TimMergeAccessor<Void>() {
      @Override public Void malloc( int len ) { throw new AssertionError(); }
      @Override public void   swap( Void a, int i, Void b, int j ) { throw new AssertionError(); }
      @Override public void   copy( Void a, int i, Void b, int j ) {        WikiMergeV1Access.this.   swap(i,j); }
      @Override public int compare( Void a, int i, Void b, int j ) { return WikiMergeV1Access.this.compare(i,j); }
    }.timMerge(null,a0,aLen, null,b0,bLen, null,c0);
  }

  default void wikiMergeV1( int from, int mid, int until )
  {
    if( mid-from <= until-mid ) wikiMergeV1_L2R(from,mid,until);
    else                        wikiMergeV1_R2L(from,mid,until);
  }

  default void wikiMergeV1_L2R( int from, int mid, int until )
  {
    if( from < 0 || from > mid || until < mid ) throw new IllegalArgumentException();
    if( mid == until || mid == from ) return;

    int lenL =   mid-from,
        len  = until-from;

    if( lenL < 7 || lenL < sqrt(len) ) {
      wikiMergeV1_mergeInPlace(from,mid,until);
      return;
    }

    int MIB_desiredLen = minBufLenMIB(lenL),
        MER_desiredLen = minBufLenMER(lenL);
    assert MIB_desiredLen > 0;
    assert MIB_desiredLen <= MER_desiredLen;

    // STEP 1: Extract BUFFER
    // ======================
    // The buffer is use both as movement-imitation buffer (MIB) and
    // merge buffer (MER).
    final int MIB, MIB_len,
              MER, MER_len;
    {
      int mib_len=0, mer=from,                          mer_len = extractMergeBuf_ordinal_l_min_unsorted(mer,mid, MER_desiredLen, mer), mib = mer+mer_len;
      if( mer_len < MER_desiredLen || MIB_desiredLen > (mib_len = extractMergeBuf_ordinal_l_min_unsorted(mib,mid, MIB_desiredLen, mib)) ) {
        int buf_len = mib_len;
                      mib_len = mer_len;
                                mer_len = buf_len;
        mib = from;
        mer = mib+mib_len;
      }
      MIB=mib; MIB_len=mib_len;
      MER=mer; MER_len=mer_len;
    }
    heapSortFast(MIB,MIB+MIB_len);

    final int B = MER_len < MER_desiredLen ? (lenL-MER_len+1) / (MIB_len+1) : MER_len,
       nBlocksL = (lenL-MIB_len-MER_len) / B,
        block0  = mid - nBlocksL*B;

    assert MER_len == MER_desiredLen || (
         (lenL-MIB_len-MER_len) /  B   <= MIB_len
      && (lenL-MIB_len-MER_len) / (B-1) > MIB_len
    );

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
      wikiMergeV1_mergeBuffered(MER+1,n-1,  dest,r-dest, l+1);
    };

    Int4Consumer merge = MER_len < MER_desiredLen
      ? (l, r, n, dest) -> {
        // INPLACE MERGE
        rotate(l,dest, -n);
        wikiMergeV1_mergeInPlace(dest-n+1, dest, r);
      }
      : bufMerge;

    int prevDest = -1,
       firstDest = until,
             pos = block0; // <- position of the remaining rolling blocks

    loop: for(
      int min = 0,
      nRemain = nBlocksL;; // <- number of blocks not yet in place
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

      // move min. block to the very right so it is in place
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
      firstDest -= nBlocksL*B;
      merge.accept( buf_end,firstDest, block0-buf_end, expL2RSearchGap(block0,firstDest, buf_end, false) );
    }

    revert(MIB, MIB+nBlocksL); // <- during block rearrangement, MIB was reverted

    if( MER_len == MER_desiredLen )
      bufMerge.accept( MIB,until, MIB_len, expL2RSearchGap(MIB+MIB_len,until, MIB, false) );

    heapSortFast(MER,MER+MER_len);
    wikiMergeV1_mergeInPlace(MER, MER+MER_len, until);

    if( MER_len < MER_desiredLen )
      wikiMergeV1_mergeInPlace(MIB, MIB+MIB_len, until);
  }

  default void wikiMergeV1_R2L( int from, int mid, int until )
  {
    if( from  < 0   ) throw new IllegalArgumentException();
    if( from  > mid ) throw new IllegalArgumentException();
    if( until < mid ) throw new IllegalArgumentException();

    int last = until-1;
    new WikiMergeV1Access()
    {
      @Override public void wikiMergeV1_mergeInPlace  ( int frm, int md, int ntl ) { WikiMergeV1Access.this.wikiMergeV1_mergeInPlace(until-ntl, until-md, until-frm); }
      @Override public void wikiMergeV1_mergeBuffered ( int a0, int aLen, int b0, int bLen, int c0 ) {
        WikiMergeV1Access.this.wikiMergeV1_mergeBuffered(
          until-b0-bLen,bLen,
          until-a0-aLen,aLen,
          until-c0-aLen-bLen
        );
      }
      @Override public void   swap( int i, int j ) {        WikiMergeV1Access.this.   swap(last-j, last-i); }
      @Override public int compare( int i, int j ) { return WikiMergeV1Access.this.compare(last-j, last-i); }
    }.wikiMergeV1_L2R(0, until-mid, until-from);
  }
}
