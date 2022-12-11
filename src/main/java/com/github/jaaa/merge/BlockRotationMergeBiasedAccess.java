package com.github.jaaa.merge;

import com.github.jaaa.CompareSwapAccess;
import com.github.jaaa.permute.RotateAccess;
import com.github.jaaa.search.ExpSearchAccess;

import static com.github.jaaa.util.IMath.sqrtFloor;


// REFERENCES
// ----------
// .. [1] "Ratio based stable in-place merging"
//         POK-SON KIM and ARNE KUTZNER


public interface BlockRotationMergeBiasedAccess extends CompareSwapAccess, ExpSearchAccess, RotateAccess
{
          int blockRotationMergeBiased_localMerge( int bias, int from, int mid, int until );
  default int blockRotationMergeBiased           ( int bias, int from, int mid, int until )
  {
    if( 0 > from || from > mid || mid > until )
      throw new IllegalArgumentException();
    if( from == mid || mid == until )
      return bias;
    int lenL =   mid - from,
        lenR = until - mid;
    return lenL < lenR
      ? blockRotationMergeBiasedL2R(bias, from,mid,until, sqrtFloor(lenL))
      : blockRotationMergeBiasedR2L(bias, from,mid,until, sqrtFloor(lenR));
  }

  default int blockRotationMergeBiasedL2R( int bias, int from, int mid, int until, int blockSize )
  {
    if( 0 > from || from > mid || mid > until )
      throw new IllegalArgumentException();
    if( from == mid || mid == until )
      return bias;

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
      bias = blockRotationMergeBiased_localMerge(bias, k+1-blockSize, k, i-n);
    }

    return blockRotationMergeBiased_localMerge(bias, k+1, i, until);
  }

  default int blockRotationMergeBiasedR2L( int bias, int from, int mid, int until, int blockSize )
  {
    if( 0 > from || from > mid || mid > until )
      throw new IllegalArgumentException();
    if( from == mid || mid == until )
      return bias;

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
      bias = blockRotationMergeBiased_localMerge(bias, i+n, k, k-1+blockSize);
    }

    return blockRotationMergeBiased_localMerge(bias, from,i,k-1);
  }
}
