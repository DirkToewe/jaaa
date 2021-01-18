package com.github.jaaa.merge;

import com.github.jaaa.RandomAccessor;

import static java.lang.Math.min;

class CheckArgsMerge
{
// STATIC FIELDS

// STATIC CONSTRUCTOR

// STATIC METHODS
  public static void checkArgs_mergeOffset(
    int a0, int aLen,
    int b0, int bLen, int nSkip
  )
  {
    if( a0   < 0 ) throw new IndexOutOfBoundsException("mergeOffset(a,a0,aLen, b,b0,bLen, nSkip): a0 must not be negative.");
    if( b0   < 0 ) throw new IndexOutOfBoundsException("mergeOffset(a,a0,aLen, b,b0,bLen, nSkip): b0 must not be negative.");
    if( aLen < 0 ) throw new IndexOutOfBoundsException("mergeOffset(a,a0,aLen, b,b0,bLen, nSkip): aLen must not be negative.");
    if( bLen < 0 ) throw new IndexOutOfBoundsException("mergeOffset(a,a0,aLen, b,b0,bLen, nSkip): bLen must not be negative.");
    if( nSkip< 0 ) throw new IndexOutOfBoundsException("mergeOffset(a,a0,aLen, b,b0,bLen, nSkip): nSkip must not be negative.");

    if( nSkip-bLen > aLen ) throw new IllegalArgumentException("mergePart(a,a0,aLen, b,b0,bLen, nSkip): nSkip cannot be larger than .");
  }


  public static <T> void checkArgs_mergeL2R(
    RandomAccessor<? super T> acc,
    T a, int a0, int aLen,
    T b, int b0, int bLen,
    T c, int c0
  )
  {
    if( a0   < 0 ) throw new IndexOutOfBoundsException("mergePart(a,a0,aLen, b,b0,bLen, c,c0,cLen): a0 must not be negative.");
    if( b0   < 0 ) throw new IndexOutOfBoundsException("mergePart(a,a0,aLen, b,b0,bLen, c,c0,cLen): b0 must not be negative.");
    if( c0   < 0 ) throw new IndexOutOfBoundsException("mergePart(a,a0,aLen, b,b0,bLen, c,c0,cLen): c0 must not be negative.");
    if( aLen < 0 ) throw new IndexOutOfBoundsException("mergePart(a,a0,aLen, b,b0,bLen, c,c0,cLen): aLen must not be negative.");
    if( bLen < 0 ) throw new IndexOutOfBoundsException("mergePart(a,a0,aLen, b,b0,bLen, c,c0,cLen): bLen must not be negative.");

    int aLength = acc.len(a);
    if( aLength < 0 ) throw new IndexOutOfBoundsException("mergePart(a,a0,aLen, b,b0,bLen, c,c0,cLen): a.length must not be negative.");
    if( aLength - aLen < a0 ) throw new IndexOutOfBoundsException("mergePart(a,a0,aLen, b,b0,bLen, c,c0,cLen): a0+aLen-1 out of bounds of a.");

    int bLength = acc.len(b);
    if( bLength < 0 ) throw new IndexOutOfBoundsException("mergePart(a,a0,aLen, b,b0,bLen, c,c0,cLen): b.length must not be negative.");
    if( bLength - bLen < b0 ) throw new IndexOutOfBoundsException("mergePart(a,a0,aLen, b,b0,bLen, c,c0,cLen): b0+bLen-1 out of bounds of b.");

    int cLength = acc.len(c);
    if( cLength < c0 ) throw new IndexOutOfBoundsException("mergePart(a,a0,aLen, b,b0,bLen, c,c0,cLen): c0 must not be greater than c.length.");
    if( cLength-c0-bLen < aLen ) throw new IndexOutOfBoundsException("mergePart(a,a0,aLen, b,b0,bLen, c,c0,cLen): merged sequence does not fit into c[c0:].");

    if( a==c && ! (a0 <= c0-aLen || c0 <= a0-bLen) ) throw new IllegalArgumentException("mergePart(a,a0,aLen, b,b0,bLen, c,c0,cLen): invalid overlap of a and c.");
    if( b==c && ! (b0 <= c0-bLen || c0 <= b0-aLen) ) throw new IllegalArgumentException("mergePart(a,a0,aLen, b,b0,bLen, c,c0,cLen): invalid overlap of b and c.");
  }

  public static <T> void checkArgs_mergeR2L(
    RandomAccessor<? super T> acc,
    T a, int a0, int aLen,
    T b, int b0, int bLen,
    T c, int c0
  )
  {
    if( a0   < 0 ) throw new IndexOutOfBoundsException("mergePart(a,a0,aLen, b,b0,bLen, c,c0,cLen): a0 must not be negative.");
    if( b0   < 0 ) throw new IndexOutOfBoundsException("mergePart(a,a0,aLen, b,b0,bLen, c,c0,cLen): b0 must not be negative.");
    if( c0   < 0 ) throw new IndexOutOfBoundsException("mergePart(a,a0,aLen, b,b0,bLen, c,c0,cLen): c0 must not be negative.");
    if( aLen < 0 ) throw new IndexOutOfBoundsException("mergePart(a,a0,aLen, b,b0,bLen, c,c0,cLen): aLen must not be negative.");
    if( bLen < 0 ) throw new IndexOutOfBoundsException("mergePart(a,a0,aLen, b,b0,bLen, c,c0,cLen): bLen must not be negative.");

    int aLength = acc.len(a);
    if( aLength < 0 ) throw new IndexOutOfBoundsException("mergePart(a,a0,aLen, b,b0,bLen, c,c0,cLen): a.length must not be negative.");
    if( aLength - aLen < a0 ) throw new IndexOutOfBoundsException("mergePart(a,a0,aLen, b,b0,bLen, c,c0,cLen): a0+aLen-1 out of bounds.");

    int bLength = acc.len(b);
    if( bLength < 0 ) throw new IndexOutOfBoundsException("mergePart(a,a0,aLen, b,b0,bLen, c,c0,cLen): b.length must not be negative.");
    if( bLength - bLen < b0 ) throw new IndexOutOfBoundsException("mergePart(a,a0,aLen, b,b0,bLen, c,c0,cLen): b0+bLen-1 out of bounds.");

    int cLength = acc.len(c);
    if( cLength < c0 ) throw new IndexOutOfBoundsException("mergePart(a,a0,aLen, b,b0,bLen, c,c0,cLen): c0 must not be greater than c.length.");
    if( cLength-c0-bLen < aLen ) throw new IndexOutOfBoundsException("mergePart(a,a0,aLen, b,b0,bLen, c,c0,cLen): merged sequence does not fit into c[c0:].");

    if( a==c && ! (a0 <= c0 || c0 <= a0-aLen-bLen) ) throw new IllegalArgumentException("mergePart(a,a0,aLen, b,b0,bLen, c,c0,cLen): invalid overlap of a and c.");
    if( b==c && ! (b0 <= c0 || c0 <= b0-aLen-bLen) ) throw new IllegalArgumentException("mergePart(a,a0,aLen, b,b0,bLen, c,c0,cLen): invalid overlap of b and c.");
  }



  public static <T> void checkArgs_mergePartL2R(
    RandomAccessor<? super T> acc,
    T a, int a0, int aLen,
    T b, int b0, int bLen,
    T c, int c0, int cLen
  )
  {
    if( a0   < 0 ) throw new IndexOutOfBoundsException("mergePartL2R(a,a0,aLen, b,b0,bLen, c,c0,cLen): a0 must not be negative.");
    if( b0   < 0 ) throw new IndexOutOfBoundsException("mergePartL2R(a,a0,aLen, b,b0,bLen, c,c0,cLen): b0 must not be negative.");
    if( c0   < 0 ) throw new IndexOutOfBoundsException("mergePartL2R(a,a0,aLen, b,b0,bLen, c,c0,cLen): c0 must not be negative.");
    if( aLen < 0 ) throw new IndexOutOfBoundsException("mergePartL2R(a,a0,aLen, b,b0,bLen, c,c0,cLen): aLen must not be negative.");
    if( bLen < 0 ) throw new IndexOutOfBoundsException("mergePartL2R(a,a0,aLen, b,b0,bLen, c,c0,cLen): bLen must not be negative.");
    if( cLen < 0 ) throw new IndexOutOfBoundsException("mergePartL2R(a,a0,aLen, b,b0,bLen, c,c0,cLen): cLen must not be negative.");

    int aLength = acc.len(a);
    if( aLength < 0 ) throw new IndexOutOfBoundsException("mergePartL2R(a,a0,aLen, b,b0,bLen, c,c0,cLen): a.length must not be negative.");
    if( aLength - aLen < a0 ) throw new IndexOutOfBoundsException("mergePartL2R(a,a0,aLen, b,b0,bLen, c,c0,cLen): a0+aLen-1 out of bounds.");

    int bLength = acc.len(b);
    if( bLength < 0 ) throw new IndexOutOfBoundsException("mergePartL2R(a,a0,aLen, b,b0,bLen, c,c0,cLen): b.length must not be negative.");
    if( bLength - bLen < b0 ) throw new IndexOutOfBoundsException("mergePartL2R(a,a0,aLen, b,b0,bLen, c,c0,cLen): b0+bLen-1 out of bounds.");

    int cLength = acc.len(c);
    if( cLength < 0 ) throw new IndexOutOfBoundsException("mergePartL2R(a,a0,aLen, b,b0,bLen, c,c0,cLen): c.length must not be negative.");
    if( cLength - cLen < c0 ) throw new IndexOutOfBoundsException("mergePartL2R(a,a0,aLen, b,b0,bLen, c,c0,cLen): c0+cLen-1 out of bounds.");

    if( cLen-bLen > aLen ) throw new IllegalArgumentException("mergePartL2R(a,a0,aLen, b,b0,bLen, c,c0,cLen): cLen cannot be greater than (aLen+bLen).");

    if( a==c  &&  ! ( a0 <= c0-aLen || c0 <= a0 - min(bLen,cLen) ) ) throw new IllegalArgumentException("mergePartL2R(a,a0,aLen, b,b0,bLen, c,c0,cLen): invalid overlap of a and c.");
    if( b==c  &&  ! ( b0 <= c0-bLen || c0 <= b0 - min(aLen,cLen) ) ) throw new IllegalArgumentException("mergePartL2R(a,a0,aLen, b,b0,bLen, c,c0,cLen): invalid overlap of b and c.");
  }

  public static <T> void checkArgs_mergePartR2L(
    RandomAccessor<? super T> acc,
    T a, int a0, int aLen,
    T b, int b0, int bLen,
    T c, int c0, int cLen
  )
  {
    if( a0   < 0 ) throw new IndexOutOfBoundsException("mergePartR2L(a,a0,aLen, b,b0,bLen, c,c0,cLen): a0 must not be negative.");
    if( b0   < 0 ) throw new IndexOutOfBoundsException("mergePartR2L(a,a0,aLen, b,b0,bLen, c,c0,cLen): b0 must not be negative.");
    if( c0   < 0 ) throw new IndexOutOfBoundsException("mergePartR2L(a,a0,aLen, b,b0,bLen, c,c0,cLen): c0 must not be negative.");
    if( aLen < 0 ) throw new IndexOutOfBoundsException("mergePartR2L(a,a0,aLen, b,b0,bLen, c,c0,cLen): aLen must not be negative.");
    if( bLen < 0 ) throw new IndexOutOfBoundsException("mergePartR2L(a,a0,aLen, b,b0,bLen, c,c0,cLen): bLen must not be negative.");
    if( cLen < 0 ) throw new IndexOutOfBoundsException("mergePartR2L(a,a0,aLen, b,b0,bLen, c,c0,cLen): cLen must not be negative.");

    int aLength = acc.len(a);
    if( aLength < 0 ) throw new IndexOutOfBoundsException("mergePartR2L(a,a0,aLen, b,b0,bLen, c,c0,cLen): a.length must not be negative.");
    if( aLength - aLen < a0 ) throw new IndexOutOfBoundsException("mergePartR2L(a,a0,aLen, b,b0,bLen, c,c0,cLen): a0+aLen-1 out of bounds.");

    int bLength = acc.len(b);
    if( bLength < 0 ) throw new IndexOutOfBoundsException("mergePartR2L(a,a0,aLen, b,b0,bLen, c,c0,cLen): b.length must not be negative.");
    if( bLength - bLen < b0 ) throw new IndexOutOfBoundsException("mergePartR2L(a,a0,aLen, b,b0,bLen, c,c0,cLen): b0+bLen-1 out of bounds.");

    int cLength = acc.len(c);
    if( cLength < 0 ) throw new IndexOutOfBoundsException("mergePartR2L(a,a0,aLen, b,b0,bLen, c,c0,cLen): c.length must not be negative.");
    if( cLength - cLen < c0 ) throw new IndexOutOfBoundsException("mergePartR2L(a,a0,aLen, b,b0,bLen, c,c0,cLen): c0+cLen-1 out of bounds.");

    if( cLen-bLen > aLen ) throw new IllegalArgumentException("mergePartR2L(a,a0,aLen, b,b0,bLen, c,c0,cLen): cLen cannot be greater than (aLen+bLen).");

    if( a==c && ! (a0 <= c0 || c0 <= a0-aLen-bLen) ) throw new IllegalArgumentException("mergePartR2L(a,a0,aLen, b,b0,bLen, c,c0,cLen): invalid overlap of a and c.");
    if( b==c && ! (b0 <= c0 || c0 <= b0-bLen-aLen) ) throw new IllegalArgumentException("mergePartR2L(a,a0,aLen, b,b0,bLen, c,c0,cLen): invalid overlap of b and c.");
  }

// FIELDS

// CONSTRUCTORS
  private CheckArgsMerge() { throw new UnsupportedOperationException("Static class cannot be instantiated."); }

// METHODS
}
