package com.github.jaaa.sort;

import com.github.jaaa.fn.Int2Consumer;
import com.github.jaaa.select.HeapSelectAccess;

import java.util.SplittableRandom;
import java.util.function.IntBinaryOperator;


public interface QuickSortAccess extends InsertionAdaptiveSortAccess, HeapSelectAccess
{
  default              void quickSort_sortRun(int from, int until ) { insertionAdaptiveSort(from,until); }
  default IntBinaryOperator quickSort_newPivotChooser() {
    var rng = new SplittableRandom();
    return (from,until) -> {
      if( from > until-5 )
        throw new AssertionError();
      int a = from,
          b = from + 1,
          c = from + until >>> 1,
          d = until - 1,
          e = until - 2;
      swap(a, rng.nextInt(from++,until  ));
      swap(b, rng.nextInt(from++,until  ));
      swap(d, rng.nextInt(from,  until--));
      swap(e, rng.nextInt(from,  until--));
      swap(c, rng.nextInt(from,  until  ));
      if( compare(c,a) < 0 )   swap(c,a);
      if( compare(d,b) < 0 )   swap(d,b);
      if( compare(d,c) < 0 ) { swap(d,c); swap(a,b); }
      if( compare(e,b) < 0 )   swap(e,b);
      if( compare(e,c) < 0 ) { swap(e,c); b = a; }
      if( compare(c,b) < 0 )   swap(c,b);
//      assert compare(a,c) <= 0;
//      assert compare(b,c) <= 0;
//      assert compare(c,d) <= 0;
//      assert compare(c,e) <= 0;
      return c;
    };
  }

  default void quickSort(int from, int until )
  {
    if( from < 0 || from > until )
      throw new IllegalArgumentException();
    new Int2Consumer()
    {
      private final IntBinaryOperator choosePivot = quickSort_newPivotChooser();
      @Override public void accept( int from, int until )
      {
        if( until-from <= 16 )
          quickSort_sortRun(from,until);
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
