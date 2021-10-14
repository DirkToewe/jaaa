package com.github.jaaa.util;

import static java.lang.Math.*;


public final class IMath
{
//  public static int gcd( int x, int y )
//  {
//    x = abs(x);
//    y = abs(y);
//
//    for( int s=0;; ) {
//      if( x==0 || x==y || y==0 )
//        return (x|y) << s;
//
//      int l = x & 1 ^ 1,
//          r = y & 1 ^ 1;
//      x >>>= l;
//      y >>>= r;
//      s += l & r;
//
//      if( (l|r) == 0 ) {
//        if( x > y ) x = x-y >>> 1;
//        else        y = y-x >>> 1;
//      }
//    }
//  }

  public static int gcd( int x, int y )
  {
    x = abs(x);
    y = abs(y);
    for( int shift=0;;) {
      int l = Integer.numberOfTrailingZeros(x); x >>>= l;
      int r = Integer.numberOfTrailingZeros(y); y >>>= r;
      shift += min(l,r);

      if( x==0 || x==y || y==0 )
        return (x|y) << shift;

      int z = abs(x-y) >>> 1;
      x = min(x,y);
      y = z;
    }
  }

  public static long gcd( long x, long y )
  {
    x = abs(x);
    y = abs(y);
    for( int shift=0;;) {
      int l = Long.numberOfTrailingZeros(x); x >>>= l;
      int r = Long.numberOfTrailingZeros(y); y >>>= r;
      shift += min(l,r);

      if( x==0 || x==y || y==0 )
        return (x|y) << shift;

      long z = abs(x-y) >>> 1;
      x = min(x,y);
      y = z;
    }
  }

  public static int sqrtCeil( int n )
  {
    if( n < 0 ) throw new ArithmeticException();
    return (int) ceil( sqrt(n) );
  }

  public static int sqrtFloor( int n )
  {
    if( n < 0 ) throw new ArithmeticException();
    return (int) Math.sqrt(n);
  }

  public static long sqrtCeil( long n )
  {
    if( n < 0 ) throw new ArithmeticException();
    if( n < 2 ) return n;

    int           nBits = log2Floor(n)+1  >>>  1;
    long r = (1L<<nBits+1) - 1;
    for( int  i = nBits+1; i-- > 0; ) {
      long R = r  ^  1L << i,
         err = R*R - n;
      if(err >= 0) r = R;
      if(err == 0) break;
    }
    return r;
  }

  public static long sqrtFloor( long n )
  {
    if( n < 0 ) throw new ArithmeticException();
    if( n < 2 ) return n;

    int          nBits = log2Floor(n)  >>>  1;
    long r = 1L<<nBits;
    for( int i = nBits; i-- > 0; ) {
      long R = r  |  1L << i,
         err = R*R - n;
      if(err <= 0) r = R;
      if(err == 0) break;
    }
    return r;
  }

  public static int sign( int n ) {
    return (n >> 31) | (-n >>> 31);
  }
  public static int sign( long n ) {
    return (int) (n >> 63) | (int) (-n >>> 63);
  }

  public static int log2Ceil( int n )
  {
    if( n <= 0 ) throw new ArithmeticException();
    return 32 - Integer.numberOfLeadingZeros(n-1);
  }

  public static int log2Floor( int n )
  {
    if( n <= 0 ) throw new ArithmeticException();
    return 31 - Integer.numberOfLeadingZeros(n);
  }

  public static int log2Ceil( long n )
  {
    if( n <= 0 ) throw new ArithmeticException();
    return 64 - Long.numberOfLeadingZeros(n-1);
  }

  public static int log2Floor( long n )
  {
    if( n <= 0 ) throw new ArithmeticException();
    return 63 - Long.numberOfLeadingZeros(n);
  }

  public static  int maxUnsigned( int u, int v ) { return Integer.compareUnsigned(u,v) > 0 ? u : v; }
  public static long maxUnsigned(long u,long v ) { return    Long.compareUnsigned(u,v) > 0 ? u : v; }

  public static  int minUnsigned( int u, int v ) { return Integer.compareUnsigned(u,v) < 0 ? u : v; }
  public static long minUnsigned(long u,long v ) { return    Long.compareUnsigned(u,v) < 0 ? u : v; }

  private IMath() {
    throw new UnsupportedOperationException();
  }
}
