package com.github.jaaa.merge;

import com.github.jaaa.search.BinarySearchAccessor;
import com.github.jaaa.CompareRandomAccessor;

public interface RecMergeV4Accessor<T> extends CompareRandomAccessor<T>, BinarySearchAccessor<T>
{
  static abstract class RecMergeFn
  {
    public int k;
    public RecMergeFn( int _k ) { k =_k; }
    public abstract void merge( int a1, int a2, int b1, int b2 );
  }

  public default void recMergeV4(
          T a, int i, int m,
          T b, int j, int n,
          T c, int k
  ) {
    if(  a==c && k < i+m && i <= k
            || b==c && k < j+n && j <= k )
      recMergeV4_R2L(a,i,m, b,j,n, c,k); // <- depending on overlap, merge from right to left
    else
      recMergeV4_L2R(a,i,m, b,j,n, c,k);
  }

  public default void recMergeV4_L2R(
          T a, int i, int m,
          T b, int j, int n,
          T c, int k
  ) {
    if( m < 0 ) throw new IllegalArgumentException();
    if( n < 0 ) throw new IllegalArgumentException();
    if( 0 < i   && i+m   < 0 ) throw new ArithmeticException("Integer overflow.");
    if( 0 < j   && j+n   < 0 ) throw new ArithmeticException("Integer overflow.");
    if( 0 < k   && k+m   < 0 ) throw new ArithmeticException("Integer overflow.");
    if( 0 < k+m && k+m+n < 0 ) throw new ArithmeticException("Integer overflow.");

//    if( i > len(a) - m ) throw new IllegalArgumentException();
//    if( j > len(b) - n ) throw new IllegalArgumentException();
//    if( k > len(c)-m-n ) throw new IllegalArgumentException();

    // check invalid overlap
    if( a==c && ! (i+m <= k || k+n <= i) ) throw new IllegalArgumentException();
    if( b==c && ! (j+n <= k || k+m <= j) ) throw new IllegalArgumentException();

    new RecMergeFn(k) {
      @Override public final void merge( int a1, int a2, int b1, int b2 )
      {
        int aLen = a2-a1,
            bLen = b2-b1;
             if( a1==a2 ) { copyRange(b,b1, c,k, bLen); k += bLen; }
        else if( b1==b2 ) { copyRange(a,a1, c,k, aLen); k += aLen; }
        else if( aLen > bLen ) {
          int am = a1 + (aLen>>>1),
              bm = binarySearchGapL(b,b1,b2, a,am);
          merge(a1,am, b1,bm   );       copy(a,am, c,k++);
          merge( 1+am,a2, bm,b2);
        }
        else {
          int bm = b1 + (bLen>>>1),
              am = binarySearchGapR(a,a1,a2, b,bm);
          merge(a1,am,   b1,bm   );     copy(b,bm, c,k++);
          merge(   am,a2, 1+bm,b2);
        }
      }
    }.merge(i,i+m, j,j+n);
  }

  public default void recMergeV4_R2L(
          T a, int i, int m,
          T b, int j, int n,
          T c, int k
  ) {
    if( m < 0 ) throw new IllegalArgumentException();
    if( n < 0 ) throw new IllegalArgumentException();
    if( 0 < i   && i+m   < 0 ) throw new ArithmeticException("Integer overflow.");
    if( 0 < j   && j+n   < 0 ) throw new ArithmeticException("Integer overflow.");
    if( 0 < k   && k+m   < 0 ) throw new ArithmeticException("Integer overflow.");
    if( 0 < k+m && k+m+n < 0 ) throw new ArithmeticException("Integer overflow.");

//    if( i > len(a) - m ) throw new IllegalArgumentException();
//    if( j > len(b) - n ) throw new IllegalArgumentException();
//    if( k > len(c)-m-n ) throw new IllegalArgumentException();

    // check invalid overlap
    if( a==c && ! (i <= k || k+m+n <= i) ) throw new IllegalArgumentException();
    if( b==c && ! (j <= k || k+m+n <= j) ) throw new IllegalArgumentException();

    new RecMergeFn(k+m+n) {
      @Override public final void merge( int a1, int a2, int b1, int b2 )
      {
        int aLen = a2-a1,
            bLen = b2-b1;
             if( a1==a2 ) copyRange(b,b1, c,k-=bLen, bLen);
        else if( b1==b2 ) copyRange(a,a1, c,k-=aLen, aLen);
        else if( aLen > bLen ) {
          int am = a1 + (aLen>>>1),
              bm = binarySearchGapL(b,b1,b2, a,am);
          merge( 1+am,a2, bm,b2);       copy(a,am, c,--k);
          merge(a1,am, b1,bm   );

        }
        else {
          int bm = b1 + (bLen>>>1),
              am = binarySearchGapR(a,a1,a2, b,bm);
          merge(   am,a2, 1+bm,b2);     copy(b,bm, c,--k);
          merge(a1,am,   b1,bm   );
        }
      }
    }.merge(i,i+m, j,j+n);
  }
}
