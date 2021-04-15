package com.github.jaaa.partition;

import com.github.jaaa.PredicateSwapAccess;
import com.github.jaaa.SwapAccess;
import com.github.jaaa.misc.BlockSwapAccess;
import com.github.jaaa.util.Hex16;

import java.util.function.IntFunction;

import static java.lang.Math.min;
import static java.lang.Math.sqrt;
import static java.lang.Math.subtractExact;

// REFERENCES
// ----------
// .. [1] "STABLE MINIMUM SPACE PARTITIONING IN LINEAR TIME"
//         JYRKI KATAJAINEN and TOMI PASANEN

public interface KatPawBiPartitionV1Access extends BlockSwapAccess,
        ExtractBiPartitionBufAccess
{
  public default void katPawBiPartitionV1( int from, int until )
  {
    if( from > until ) throw new IllegalArgumentException();

    final int B = 16*16,
            len = subtractExact(until,from),
       blockLen = (int) ( ( sqrt(1d*len*B + 4) - 2 ) / B ), // <- number B-sized chunks per block
       blockNum = blockLen + (len - blockLen*blockLen*B - 4*blockLen) / (B*blockLen+4); // <- number of (B*blockLen)-sized blocks

    // only nRest%(2B) buffer elements have to be extracted before the "blockification step"
//    int    nRest = len - blockNum*blockLen*B,
//      nL = nRest >>> 1,
//      nR = nRest - nL;
    final int nL = len%B >>> 1,
              nR = len%B - nL;

    // buffer extraction
    if( extractBiPartitionBufB_R(from,until,nR) < nR ) return; until -= nR;
    if( extractBiPartitionBufA_L(from,until,nL) < nL ) return;  from += nL;

    assert (until-from)%B == 0;

    // BLOCKIFY
    // --------
    // Group up the B-elements into blocks of size B while maintaining the order.
    Hex16 ord = new Hex16(),
          ORD = new Hex16();
    int   pos = from,
          POS = from;

    IntFunction<SwapAccess>
      swapper = off -> (i,j) ->      swap(off+i,    off+j),
      SWAPPER = OFF -> (i,j) -> blockSwap(OFF+i*16, OFF+j*16, 16);

    int nA = 0,
        nB = 0;

    for( int l=from; l < until; l++ )
      if( ! predicate(l) ) nA++;
      else {               nB++;
        if( ord.isFull() )
        {
          move_up:{
            int        target = from + (l-16-from)/16*16;
            ord.rotate(target-pos);

            for( ; pos < target; pos++) swap(  pos, pos+16);
            for( ; pos > target;      ) swap(--pos, pos+16);
            assert pos ==target;
          }

          // unravel collected elements
          ord.sortAndClear( swapper.apply(pos) );

          if( ORD.isFull() )
          {
            // dump off collectd blocks at multiple of B
            int          TARGET = from + (pos-B-from)/B * B;
            ORD.rotate( (TARGET-POS) / 16 );

            // move collected blocks
            for( ; POS < TARGET; POS+=16 ) blockSwap(POS,     POS+B, 16);
            for( ; POS > TARGET;         ) blockSwap(POS-=16, POS+B, 16);
            assert POS ==TARGET;

            // unravel collected blocks
            ORD.sortAndClear( SWAPPER.apply(POS) );
          }
          else
          { // move up collected blocks
            int     SIZE = ORD.size()*16;
            if( 0 < SIZE ) {
              ORD.rotate( (pos-SIZE-POS) / 16 );
              for( ; POS < pos-SIZE; POS+=16 ) blockSwap(POS,POS+SIZE, 16);
            }
          }

          POS = pos - ORD.size()*16;
          ORD.append(ORD.size());
        }
        else
        { // move up collected elements
          int     size = ord.size();
          if( 0 < size ) {
            ord.rotate(l-size-pos);
            for( ; pos < l-size; pos++ ) swap(pos,pos+size);
          }
        }

        pos = l-ord.size();
        ord.append(ord.size());
      }

    // move remaining collected elements into place
    int               SIZE = ORD.size()*16,
                      size = ord.size();
    ord.rotate( until-size-pos);
    for(; pos < until-size;pos++) swap(pos, pos+size);

    // unravel remaining collected elements and blocks
    ord.sortAndClear( swapper.apply(pos) );
    ORD.sortAndClear( SWAPPER.apply(POS) );

    // move remaining collected blocks into place
    rotate(POS,until-size, -SIZE);

    // BI-PARTITION REMAINING BLOCKS
    // -----------------------------
    assert 0 == (until-from) % B;
    final int FROM = from;
    MuRaSaBiPartitionV2.biPartition(0, (until-from) / B, new PredicateSwapAccess(){
      @Override public void swap( int i, int j ) { blockSwap(FROM+B*i, FROM+B*j, B); }
      @Override public boolean predicate( int i ) { return KatPawBiPartitionV1Access.this.predicate(FROM+B*i); }
    });

    // B-elements of the mixed block belong all the way to the end
    rotate( from+nA, SIZE==B ? until-B : until, -(size+SIZE)%B );
  }
}
