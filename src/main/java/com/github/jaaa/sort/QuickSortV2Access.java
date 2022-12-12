package com.github.jaaa.sort;

import com.github.jaaa.fn.Int2Consumer;
import com.github.jaaa.select.HeapSelectAccess;

import java.util.SplittableRandom;
import java.util.function.IntBinaryOperator;

import static com.github.jaaa.util.IMath.sign;


public interface QuickSortV2Access extends InsertionAdaptiveSortAccess, HeapSelectAccess
{
  default              void quickSortV2_sortRun( int from, int until ) { insertionAdaptiveSort(from,until); }
  default IntBinaryOperator quickSortV2_newPivotChooser() {
    var rng = new SplittableRandom();
    return (from,until) -> {
      if( from > until-3 )
        throw new AssertionError();
      final int a = from,
                b = until + from >>> 1,
                c = until-1;
      swap(a, rng.nextInt(from++,until  ));
      swap(c, rng.nextInt(from,  until--));
      swap(b, rng.nextInt(from,  until  ));
      int ab = sign( compare(a,b) ),
          bc = sign( compare(b,c) );
      if( ab*bc >= 0 ) {
        if( (ab|bc) > 0 ) swap(a,c);
      }
      else {
        if( compare(a,c) > 0 ) {
          swap(a,c);
          ab = -bc;
        }
        swap(b, ab > 0 ? a : c);
      }
//      assert compare(a,b) <= 0;
//      assert compare(b,c) <= 0;
      return b;
    };
  }

  default void quickSortV2( int from, int until )
  {
    if( from < 0 || from > until )
      throw new IllegalArgumentException();
    new Int2Consumer()
    {
      private final IntBinaryOperator choosePivot = quickSortV2_newPivotChooser();
      @Override public void accept( int from, int until )
      {
        if( until-from <= 16 )
          quickSortV2_sortRun(from,until);
        else {
          // SELECT RANDOM PIVOT
          // -------------------
          int l = choosePivot.applyAsInt(from,until),
              r = l+1;

          // PARTITION BY PIVOT
          // ------------------
          partition: {
            int i=l, j=r;
            loop: while( from < i-- ) {
              // Skip ahead in left elements
              // ---------------------------
              int il = compare(i,l);
              if( il <= 0 ) {
                if( il == 0 )
                  swap(i,--l); // <- add to pivots
                continue;
              }
              // Skip ahead in right elements
              // ----------------------------
              for( ; j < until; j++ ) {
                int lj = compare(l,j);
                if( lj > 0 ) {
                  swap(i,j++);
                  continue loop;
                }
                if( lj == 0 )
                  swap(r++,j); // <- add to pivots
              }
              // Handle remaining left elements
              // ------------------------------
              int k = l;
              swap(i,--k);
              while( from < i-- ) {
                il = compare(i,l);
                if( il >= 0 ) { swap(i,--k);
                  if( il == 0 )   swap(k,--l); } // <- add to pivots
              }
              // roll pivots over right-hand side elements
              while( k < l )
                swap(--l,--r);
              break partition;
            }
            // Handle remaining right elements
            // -------------------------------
            int k = r;
            for( ; j < until; j++) {
              int lj = compare(l,j);
              if( lj >= 0 ) { swap(j,k);
                if( lj == 0 )   swap(k,r++); k++; }
            }
            // roll pivots over left-hand side elements
            while( r < k )
              swap(l++,r++);
          }
          // sort left an right of the pivots
          accept(from,l);
          accept(r,until);
        }
      }
    }.accept(from,until);
  }
}
