package com.github.jaaa.partition;

import com.github.jaaa.misc.BlockSwapAccess;
import com.github.jaaa.misc.RotateAccess;

import static java.lang.Integer.compareUnsigned;

// An in-place {A,B}-sort method conceived by Dirk Toewe.
//
// Overview
// --------
// The algorithm traverses the list collecting all B-elements along the
// way. The elements are collected in form a "train", made up of wagons.
// The length of each wagon is unique and a power of 2. Ever wagon is larger
// than the wagon to its right. Whether or not the train contains a wagon
// of length 2^i can be stored in the i-th bit of an integer. Each wagon
// of the train is rolled forward independently. Since all wagons travel
// the same distance, a single integer is sufficient to keep track of
// the rotation.
//
// Complexity
// ----------
// Comparisons: O(m+n)
//       Swaps: O( min{ (m+n)*log(n), n²log(n) + m } )
//      Memory: O(1)
//
// Where `m` is the number of A-elements and `n` is number of B-elements.

public interface RailwayBiPartitionAccess extends RotateAccess, BlockSwapAccess
{
  public boolean predicate( int i );

  public default void railwayBiPartition( int from, int until )
  {
    if( from > until )
      throw new IllegalArgumentException();

    int trainLen = 0,    // <- number of right hand elements collected so far
        trainPos = from, // <- position of the collected elements
        trainRot = 0;    // <- number of times collected elements were rolled/rotated

    loop:for( int i=from;; i++ )
      if( i == until || predicate(i) )
      {
        // MOVE TRAIN UP TO NEXT ELEMENT
        // -----------------------------
        // First let's do "bunny hops"
        if( 0 != trainLen )
          while( trainPos+trainLen <= i-trainLen )
            blockSwap(trainPos, trainPos+=trainLen, trainLen);

        trainRot += i - (trainPos+trainLen);

        // If we can't "bunny hop" anymore, let's move the train wagon by wagon
        wagon_by_wagon: {
          int wagonPos  = trainPos + trainLen,
              wagonStop = i;
          for( int wagonLen=1; compareUnsigned(wagonLen,trainLen) <= 0; wagonLen <<= 1 )
            if( (wagonLen & trainLen) != 0 )
            {
              for(  int j=wagonPos; j < wagonStop; j++ )
                swap(j, j-wagonLen);
              wagonPos -= wagonLen;
              wagonStop-= wagonLen;
            }
          trainPos  = i - trainLen;
        }

        if( i == until )
          break loop;

        // INSERT NEXT ELEMENT INTO TRAIN
        // merge wagons of equal size
             int wagonPos = i;
        for( int wagonLen = 1; (wagonLen & trainLen) != 0; wagonLen <<= 1 )
          // un-rotate wagon
          rotate(
            wagonPos -=wagonLen,
            wagonPos + wagonLen,
            trainRot
          );
        // re-rotate last wagon that now contains a new B-element
        trainLen += 1;
        rotate(wagonPos, i+1, -trainRot);
      }

    // UN-ROTATE TRAIN AT ITS LAST STOP
         int wagonPos = until;
    for( int wagonLen = 1; compareUnsigned(wagonLen,trainLen) <= 0; wagonLen <<= 1 )
      if( (wagonLen & trainLen) != 0 )
        rotate(
          wagonPos -=wagonLen,
          wagonPos + wagonLen,
          trainRot
        );
  }
}
