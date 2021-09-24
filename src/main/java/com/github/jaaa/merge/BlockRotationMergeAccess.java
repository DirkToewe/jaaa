package com.github.jaaa.merge;


import com.github.jaaa.CompareSwapAccess;
import com.github.jaaa.misc.RotateAccess;

import static com.github.jaaa.util.IMath.sqrtFloor;


// REFERENCES
// ----------
// .. [1] "Ratio based stable in-place merging"
//         POK-SON KIM and ARNE KUTZNER


public interface BlockRotationMergeAccess extends CompareSwapAccess, RotateAccess, ExpMergeV2Access
{
  default void blockRotationMerge_localMerge( int from, int mid, int until ) { expMergeV2(from,mid,until); }

  default void blockRotationMerge( int from, int mid, int until )
  {
    if(  0  > from ) throw new IllegalArgumentException();
    if( mid < from ) throw new IllegalArgumentException(); if( mid == until) return;
    if( mid > until) throw new IllegalArgumentException(); if( mid == from ) return;
    int lenL =   mid - from,
        lenR = until - mid;
    if( lenL < lenR ) blockRotationMergeL2R( from,mid,until, sqrtFloor(lenL) );
    else              blockRotationMergeR2L( from,mid,until, sqrtFloor(lenR) );
  }

  default void blockRotationMergeL2R( int from, int mid, int until, int blockSize )
  {
    if(  0  > from ) throw new IllegalArgumentException();
    if( mid < from ) throw new IllegalArgumentException(); if( mid == until) return;
    if( mid > until) throw new IllegalArgumentException(); if( mid == from ) return;

    if( blockSize <= 0 ) throw new IllegalArgumentException();

    int i = expL2RSearchGap(mid,until, from, /*rightBias=*/false),
        n = mid-from;
    rotate(from,i, -n);
    int k = i-n;

    while( 0 < (n-=blockSize) )
    {
      k = i-n;
      i = expL2RSearchGap(i,until, k, /*rightBias=*/false);
      rotate(k,i, -n);
      blockRotationMerge_localMerge(k+1-blockSize, k, i-n);
    }

    blockRotationMerge_localMerge(k+1, i, until);
  }

  default void blockRotationMergeR2L( int from, int mid, int until, int blockSize )
  {
    if(  0  > from ) throw new IllegalArgumentException();
    if( mid < from ) throw new IllegalArgumentException(); if( mid == until) return;
    if( mid > until) throw new IllegalArgumentException(); if( mid == from ) return;

    if( blockSize <= 0 ) throw new IllegalArgumentException();

    int i = expR2LSearchGap(from,mid, until-1, true),
        n = until-mid;
    rotate(i,until, n);
    int k = i+n;

    while( 0 < (n-=blockSize) )
    {
      k = i+n;
      i = expR2LSearchGap(from,i, k-1, true);
      rotate(i,k, n);
      blockRotationMerge_localMerge(i+n, k, k-1+blockSize);
    }

    blockRotationMerge_localMerge(from,i,k-1);
  }
}
