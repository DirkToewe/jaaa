package com.github.jaaa.merge;

import com.github.jaaa.CompareRandomAccessor;
import com.github.jaaa.search.ExpL2RSearchAccessor;
import com.github.jaaa.search.ExpR2LSearchAccessor;

import static com.github.jaaa.merge.CheckArgsMerge.checkArgs_mergePartL2R;
import static com.github.jaaa.merge.CheckArgsMerge.checkArgs_mergePartR2L;
import static java.lang.Math.min;


public interface TimMergePartAccessor<T> extends CompareRandomAccessor<T>,
                                                  ExpL2RSearchAccessor<T>,
                                                  ExpR2LSearchAccessor<T>
{
  public default void timMergePartL2R(
    T a, int a0, int aLen,
    T b, int b0, int bLen,
    T c, int c0, int cLen
  ) {
    checkArgs_mergePartL2R(
      this,a,a0,aLen,
           b,b0,bLen,
           c,c0,cLen
    );

    aLen = min(aLen,cLen);
    bLen = min(bLen,cLen);

    int           MIN_GALLOP = 7,
      minGallop = MIN_GALLOP;

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
            if(--cLen <= 0 ) return; bLen = min(bLen,cLen);
            if(--aLen <= 0 ) break outer;
          } else {
            copy(b,b0++, c,c0++);
            count1++;
            count2 = 0;
            if(--cLen <= 0 ) return; aLen = min(aLen,cLen);
            if(--bLen <= 0 ) break outer;
          }
        }
        while( (count1 | count2) < minGallop );

        // GALLOPING MERGE
        do {
                   count1 = -a0 + expL2RSearchGapR(a,a0,a0+aLen, b,b0);
          if( 0 != count1 ) { copyRange(a,a0, c,c0, count1);
             a0 += count1;
             c0 += count1;
           cLen -= count1; if( cLen <= 0 ) return; bLen = min(bLen,cLen);
           aLen -= count1; if( aLen <= 0 ) break outer;
          }
          copy(b,b0++, c,c0++);
          if(--cLen <= 0 ) return; aLen = min(aLen,cLen);
          if(--bLen <= 0 ) break outer;

                   count2 = -b0 + expL2RSearchGapL(b,b0,b0+bLen, a,a0);
          if( 0 != count2 ) { copyRange(b,b0, c,c0, count2);
             b0 += count2;
             c0 += count2;
           cLen -= count2; if( cLen <= 0 ) return; aLen = min(aLen,cLen);
           bLen -= count2; if( bLen <= 0 ) break outer;
          }
          copy(a,a0++, c,c0++);
          if(--cLen <= 0 ) return; bLen = min(bLen,cLen);
          if(--aLen <= 0 ) break outer;

          minGallop--;
        } while( count1 >= MIN_GALLOP | count2 >= MIN_GALLOP );

        if(minGallop < 0)
           minGallop = 0;
           minGallop+= 2;  // Penalize for leaving gallop mode
      }

         if( aLen!=0 ) copyRange(a,a0, c,c0, aLen);
    else if( bLen!=0 ) copyRange(b,b0, c,c0, bLen);
  }

  public default void timMergePartR2L(
    T a, int a0, int aLen,
    T b, int b0, int bLen,
    T c, int c0, int cLen
  ) {
    checkArgs_mergePartR2L(
      this,a,a0,aLen,
           b,b0,bLen,
           c,c0,cLen
    );

    a0 += aLen;
    b0 += bLen;
    c0 += cLen;

    aLen = min(aLen,cLen);
    bLen = min(bLen,cLen);

    int           MIN_GALLOP = 7,
      minGallop = MIN_GALLOP;

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
          if( compare(a,--a0, b,--b0) > 0 ) {
            copy(a,a0, c,c0); b0++;
            count2++;
            count1 = 0;
            if(--cLen <= 0 ) return; bLen = min(bLen,cLen);
            if(--aLen <= 0 ) break outer;
          } else {
            copy(b,b0, c,c0); a0++;
            count1++;
            count2 = 0;
            if(--cLen <= 0 ) return; aLen = min(aLen,cLen);
            if(--bLen <= 0 ) break outer;
          }
        }
        while( (count1 | count2) < minGallop );

        // GALLOPING MERGE
        do {
                   count1 = a0 - expR2LSearchGapR(a,a0-aLen,a0, b,b0-1);
          if( 0 != count1 ) {
             a0 -= count1;
             c0 -= count1; copyRange(a,a0, c,c0, count1);
           cLen -= count1; if( cLen <= 0 ) return; bLen = min(bLen,cLen);
           aLen -= count1; if( aLen <= 0 ) break outer;
          }
          copy(b,--b0, c,--c0);
          if(--cLen <= 0 ) return; aLen = min(aLen,cLen);
          if(--bLen <= 0 ) break outer;

                   count2 = b0 - expR2LSearchGapL(b,b0-bLen,b0, a,a0-1);
          if( 0 != count2 ) {
             b0 -= count2;
             c0 -= count2; copyRange(b,b0, c,c0, count2);
           cLen -= count2; if( cLen <= 0 ) return; aLen = min(aLen,cLen);
           bLen -= count2; if( bLen <= 0 ) break outer;
          }
          copy(a,--a0, c,--c0);
          if(--cLen <= 0 ) return; bLen = min(bLen,cLen);
          if(--aLen <= 0 ) break outer;

          minGallop--;
        } while( count1 >= MIN_GALLOP | count2 >= MIN_GALLOP );

        if(minGallop < 0)
           minGallop = 0;
           minGallop+= 2;  // Penalize for leaving gallop mode
      }

         if( aLen!=0 ) copyRange(a,a0-aLen, c,c0-aLen, aLen);
    else if( bLen!=0 ) copyRange(b,b0-bLen, c,c0-bLen, bLen);
  }
}
