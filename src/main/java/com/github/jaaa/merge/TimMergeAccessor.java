package com.github.jaaa.merge;

import com.github.jaaa.CompareRandomAccessor;
import com.github.jaaa.search.ExpL2RSearchAccessor;
import com.github.jaaa.search.ExpR2LSearchAccessor;

import static com.github.jaaa.merge.CheckArgsMerge.checkArgs_mergeL2R;
import static com.github.jaaa.merge.CheckArgsMerge.checkArgs_mergeR2L;
import static java.lang.Math.max;
import static java.lang.Math.min;


//  REFERENCES
//  ----------
//  .. [1] OpenJDK 11, java.util.TimSort.java
//          https://github.com/openjdk/jdk11/blob/master/src/java.base/share/classes/java/util/TimSort.java
//          http://openjdk.java.net/projects/jdk/11/
//  .. [2] "[Python-Dev] Sorting"
//          Tim Peters
//          https://mail.python.org/pipermail/python-dev/2002-July/026837.html
//  .. [3]  https://en.wikipedia.org/wiki/Timsort
//
//  IMPLEMENTATION DETAILS
//  ----------------------
//  Tim merge is a standalone version of the merging algorithm used in the Tim sort algorithm.
//  It is a hybrid between tape merge (linear merge) and an exponential merge algorithm. The
//  algorithm starts with tape merge. When it heuristically detects that the tape merge is too
//  one-sided, it switches over to exponential merge. The heuristic switches back and forth between
//  exponential and linear merge whenever one is performing poorly. Depending on how successful
//  the most recent exponential merge has been, it is incentivised or penalized, making the
//  algorithm more or less likely to swap from tape merge back to exponential merge.
//

public interface TimMergeAccessor<T> extends CompareRandomAccessor<T>,
//                                           GallopL2RSearchAccessor<T>,
//                                           GallopR2LSearchAccessor<T>
                                              ExpR2LSearchAccessor<T>,
                                              ExpL2RSearchAccessor<T>
{
  int MIN_GALLOP = 7;

  default void timMerge(
    T a, int i, int m,
    T b, int j, int n,
    T c, int k
  ) {
    if(  a==c && k < i+m && i <= k
      || b==c && k < j+n && j <= k )
      timMergeR2L(a,i,m, b,j,n, c,k); // <- depending on overlap, merge from right to left
    else
      timMergeL2R(a,i,m, b,j,n, c,k);
  }

  default void timMergeL2R(
    T a, int a0, int aLen,
    T b, int b0, int bLen,
    T c, int c0
  ) {
    _timMergeL2R(
      MIN_GALLOP,a,a0,aLen,
                 b,b0,bLen,
                 c,c0
    );
  }

  default void timMergeR2L(
    T a, int a0, int aLen,
    T b, int b0, int bLen,
    T c, int c0
  ) {
    _timMergeR2L(
      MIN_GALLOP,a,a0,aLen,
                 b,b0,bLen,
                 c,c0
    );
  }

  default int _timMergeL2R(
    int minGallop,
    T a, int a0, int aLen,
    T b, int b0, int bLen,
    T c, int c0
  ) {
    checkArgs_mergeL2R(
      a,a0,aLen,
      b,b0,bLen,
      c,c0
    );
    assert 1 <= minGallop;

    // MERGE LOOP
    // ----------
    if( min(aLen,bLen) > 0 )
      outer: for(;;)
      {
        int count1 = 0, // Number of times in a row that first run won
            count2 = 0; // Number of times in a row that second run won

        // TAPE MERGE
        do {
          if( compare(a,a0, b,b0) <= 0 ) {
            copy(a,a0++, c,c0++);
            count2++;
            count1 = 0;
            if(--aLen <= 0 ) break outer;
          } else {
            copy(b,b0++, c,c0++);
            count1++;
            count2 = 0;
            if(--bLen <= 0 ) break outer;
          }
        }
        while( (count1 | count2) < minGallop );

        // GALLOPING MERGE
        do {
                   count1 = expL2RSearchGap(a,a0,a0+aLen, b,b0, /*rightBias=*/true) - a0;
          if( 0 != count1 ) { copyRange(a,a0, c,c0, count1);
             a0 += count1;
             c0 += count1;
           aLen -= count1;
           if( aLen <= 0 ) break outer;
          }
          copy(b,b0++, c,c0++);
          if(--bLen <= 0 ) break outer;

                   count2 = expL2RSearchGap(b,b0,b0+bLen, a,a0, /*rightBias=*/false) - b0;
          if( 0 != count2 ) { copyRange(b,b0, c,c0, count2);
             b0 += count2;
             c0 += count2;
           bLen -= count2;
           if( bLen <= 0 ) break outer;
          }
          copy(a,a0++, c,c0++);
          if(--aLen <= 0 ) break outer;

          --minGallop;
        } while( count1 >= MIN_GALLOP | count2 >= MIN_GALLOP );

        if(minGallop < 0)
           minGallop = 0;
           minGallop+= 2;  // Penalize for leaving gallop mode
      }

    assert ! (aLen != 0 && bLen != 0);
         if( aLen!=0 ) copyRange(a,a0, c,c0, aLen);
    else if( bLen!=0 ) copyRange(b,b0, c,c0, bLen);

    return max(1,minGallop);
  }

  default int _timMergeR2L(
    int minGallop,
    T a, int a0, int aLen,
    T b, int b0, int bLen,
    T c, int c0
  ) {
    checkArgs_mergeR2L(
      a,a0,aLen,
      b,b0,bLen,
      c,c0
    );
    assert 1 <= minGallop;

    a0 += aLen;
    b0 +=      bLen;
    c0 += aLen+bLen;

    // MERGE LOOP
    // ----------
    if( min(aLen,bLen) > 0 )
      outer: for(;;)
      {
        int count1 = 0, // Number of times in a row that first run won
            count2 = 0; // Number of times in a row that second run won

        // TAPE MERGE
        do {
          --c0;
          if( compare(a,a0-1, b,b0-1) > 0 ) {
            copy(a,--a0, c,c0);
            count2++;
            count1 = 0;
            if(--aLen <= 0 ) break outer;
          } else {
            copy(b,--b0, c,c0);
            count1++;
            count2 = 0;
            if(--bLen <= 0 ) break outer;
          }
        }
        while( (count1 | count2) < minGallop );

        // GALLOPING MERGE
        do {
                   count2 = b0 - expR2LSearchGap(b,b0-bLen,b0, a,a0-1, /*rightBias=*/false);
          if( 0 != count2 ) {
             b0 -= count2;
             c0 -= count2; copyRange(b,b0, c,c0, count2);
           bLen -= count2;
           if( bLen <= 0 ) break outer;
          }
          copy(a,--a0, c,--c0);
          if(--aLen <= 0 ) break outer;

                   count1 = a0 - expR2LSearchGap(a,a0-aLen,a0, b,b0-1, /*rightBias=*/true);
          if( 0 != count1 ) {
             a0 -= count1;
             c0 -= count1; copyRange(a,a0, c,c0, count1);
           aLen -= count1;
           if( aLen <= 0 ) break outer;
          }
          copy(b,--b0, c,--c0);
          if(--bLen <= 0 ) break outer;

          --minGallop;
        } while( count1 >= MIN_GALLOP | count2 >= MIN_GALLOP );

        if(minGallop < 0)
           minGallop = 0;
           minGallop+= 2;  // Penalize for leaving gallop mode
      }

    assert ! (aLen != 0 && bLen != 0);
         if( aLen!=0 ) copyRange(a,a0-aLen, c,c0-aLen, aLen);
    else if( bLen!=0 ) copyRange(b,b0-bLen, c,c0-bLen, bLen);

    return max(1,minGallop);
  }
}
