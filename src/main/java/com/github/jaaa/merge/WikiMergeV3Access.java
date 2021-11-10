package com.github.jaaa.merge;

import com.github.jaaa.fn.Int4Consumer;

import static java.lang.Math.*;

public interface WikiMergeV3Access extends KiwiMergeAccess
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
    double  b = bufLen,
        delta = b*b - 4d*len;
    if( delta < 0 ) return 0;
    int x = (int)( (b + sqrt(delta)) / 2 ),
        y = bufLen - x;
    return max(x,y);
  }

  default void wikiMergeV3_mergeInPlace ( int from, int mid, int until ) { blockRotationMerge(from,mid,until); }
  default void wikiMergeV3_mergeBuffered( int a0, int aLen,
                                          int b0, int bLen,
                                          int c0 )
  {
    new TimMergeAccessor<Void>() {
      @Override public Void malloc( int len ) { throw new AssertionError(); }
      @Override public void   swap( Void a, int i, Void b, int j ) { throw new AssertionError(); }
      @Override public void   copy( Void a, int i, Void b, int j ) {        WikiMergeV3Access.this.   swap(i,j); }
      @Override public int compare( Void a, int i, Void b, int j ) { return WikiMergeV3Access.this.compare(i,j); }
    }.timMerge(null,a0,aLen, null,b0,bLen, null,c0);
  }

//  private void wikiMergeV3_copyToBufferAndMerge( int l, int m, int r, int buf, int nL )
//  {
//
//  }

  default void wikiMergeV3( int from, int mid, int until )
  {
    if( mid-from <= until-mid ) wikiMergeV3_L2R(from,mid,until);
    else                        wikiMergeV3_R2L(from,mid,until);
  }

  default void wikiMergeV3_L2R( int from, int mid, int until )
  {
    if( from < 0 || from > mid || until < mid ) throw new IllegalArgumentException();
    if( mid == until || mid == from ) return;

    int lenL =   mid-from,
        len  = until-from;

    if( lenL < 7 || lenL < sqrt(len) )
      wikiMergeV3_mergeInPlace(from,mid,until);
    else {
      // STEP 1: Extract BUFFER
      // ======================
      // The buffer is used both as movement-imitation buffer (MIB) and
      // merge buffer.
      int bufLen = extractMergeBuf_ordinal_l_min_unsorted(from,mid, minBufLen(lenL), from),
          merLen = merBufLen(lenL-bufLen, bufLen);

      heapSortFast(from+merLen, from+bufLen);

      wikiMergeV3_L2R_usingBuffer(from+bufLen, mid, until, from, bufLen);

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
        wikiMergeV3_mergeBuffered(from+1,mibLen-1,  dest,until-dest, i+1);

        // SORT MERGE BUFFER
        heapSortFast(from, from+merLen);
      }
      wikiMergeV3_mergeInPlace(from, from+merLen, until);
    }
  }

  default void wikiMergeV3_L2R_usingBuffer( int from, int mid, int until, int buf, int bufLen )
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
      kiwiMergeL2R_usingBuffer(from,mid,until, buf,bufLen);
      return;
    }

    final int MER = buf,
              MIB = MER + B,
         nBlocksL = lenL / B,
          block0  = mid - nBlocksL*B;

    // STEP 2: Block Rearrangement
    // ===========================
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
      wikiMergeV3_mergeBuffered(MER+1,n-1,  dest,r-dest, l+1);
    };

    int prevDest = -1,
       firstDest = until,
             pos = block0; // <- position of the remaining rolling blocks

    loop: for( int min = 0, nRemain = nBlocksL;;) // <- number of blocks not yet in place
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
    if( from < block0 ) {
      firstDest -= nBlocksL*B;
      merge.accept( from,firstDest, block0-from, expL2RSearchGap(block0,firstDest, from, false) );
    }

    revert(MIB, MIB+nBlocksL); // <- during block rearrangement, MIB was reverted
  }

  default void wikiMergeV3_R2L( int from, int mid, int until )
  {
    if( from < 0 || from > mid || until < mid ) throw new IllegalArgumentException();

    int last = until-1;
    new WikiMergeV3Access()
    {
      @Override public void kiwiMerge_mergeInPlace ( int frm, int md, int ntl ) { WikiMergeV3Access.this.kiwiMerge_mergeInPlace(until-ntl, until-md, until-frm); }
      @Override public void kiwiMerge_mergeBuffered( int a0, int aLen, int b0, int bLen, int c0 ) {
        WikiMergeV3Access.this.kiwiMerge_mergeBuffered(
          until-b0-bLen,bLen,
          until-a0-aLen,aLen,
          until-c0-aLen-bLen
        );
      }

      @Override public void wikiMergeV3_mergeInPlace ( int frm, int md, int ntl ) { WikiMergeV3Access.this.wikiMergeV3_mergeInPlace(until-ntl, until-md, until-frm); }
      @Override public void wikiMergeV3_mergeBuffered( int a0, int aLen, int b0, int bLen, int c0 ) {
        WikiMergeV3Access.this.wikiMergeV3_mergeBuffered(
          until-b0-bLen,bLen,
          until-a0-aLen,aLen,
          until-c0-aLen-bLen
        );
      }

      @Override public void   swap( int i, int j ) {        WikiMergeV3Access.this.   swap(last-j, last-i); }
      @Override public int compare( int i, int j ) { return WikiMergeV3Access.this.compare(last-j, last-i); }
    }.wikiMergeV3_L2R(0, until-mid, until-from);
  }
}
