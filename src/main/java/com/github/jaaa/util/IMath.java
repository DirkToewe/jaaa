package com.github.jaaa.util;

import static java.lang.Math.ceil;
import static java.lang.Math.sqrt;
import static java.lang.Math.abs;


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
    int sx = -(x>>>31); x = (x^sx) - sx; // <- x = abs(x)
    int sy = -(y>>>31); y = (y^sy) - sy; // <- y = abs(y)

    for( int shift=0;; ) {
      if( x==0 || x==y || y==0 )
        return (x|y) << shift;

      int l = x & 1 ^ 1; x >>>= l;
      int r = y & 1 ^ 1; y >>>= r;
      shift += l & r;

      // xy = |y-x| / 2
      int xy = y-x; int z = -(xy>>>31);
          xy = (xy^z) - z; // <- xy = abs(xy)
          xy >>>= 1;
      int  a = -(l|r);
      z |= a;
      // <- x,y = |x-y|/2, min(x,y)
      y = (y & z | ~z & x );
      x = (x & a | ~a & xy);
    }
  }

  public static long gcd( long x, long y )
  {
    long sx = -(x>>>63); x = (x^sx) - sx; // <- x = abs(x)
    long sy = -(y>>>63); y = (y^sy) - sy; // <- y = abs(y)

    for( int shift=0;; ) {
      if( x==0 || x==y || y==0 )
        return (x|y) << shift;

      long l = x & 1 ^ 1; x >>>= l;
      long r = y & 1 ^ 1; y >>>= r;
      shift += l & r;

      // xy = |y-x| / 2
      long xy = y-x;long z = -(xy>>>63);
           xy = (xy^z) - z; // <- xy = abs(xy)
           xy >>>= 1;
      long a = -(l|r);
      z |= a;
      // <- x,y = |x-y|/2, min(x,y)
      y = (y & z | ~z & x );
      x = (x & a | ~a & xy);
    }
  }

  public static int sqrtCeil( int n )
  {
    if( n < 0 )
      throw new ArithmeticException();
    return (int) ceil( sqrt(n) );
  }

  public static int sqrtFloor( int n )
  {
    if( n < 0 )
      throw new ArithmeticException();

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
    int                      l =31;
    if( (1 << (l-16)) >= n ) l-=16;
    if( (1 << (l- 8)) >= n ) l-= 8;
    if( (1 << (l- 4)) >= n ) l-= 4;
    if( (1 << (l- 2)) >= n ) l-= 2;
    if( (1 << (l- 1)) >= n ) l-= 1;
    return                   l;
  }

  public static int log2Floor( int n )
  {
    if(  n <= 0  ) throw new ArithmeticException();
    int                      l = 0;
    if( (n >>> ( 16)) != 0 ) l+=16;
    if( (n >>> (l+8)) != 0 ) l+= 8;
    if( (n >>> (l+4)) != 0 ) l+= 4;
    if( (n >>> (l+2)) != 0 ) l+= 2;
    if( (n >>> (l+1)) != 0 ) l+= 1;
    return                   l;
  }

  public static int log2Ceil( long n )
  {
    if( n <= 0 ) throw new ArithmeticException();
    int                       l =63;
    if( (1L << (l-32)) >= n ) l-=32;
    if( (1L << (l-16)) >= n ) l-=16;
    if( (1L << (l- 8)) >= n ) l-= 8;
    if( (1L << (l- 4)) >= n ) l-= 4;
    if( (1L << (l- 2)) >= n ) l-= 2;
    if( (1L << (l- 1)) >= n ) l-= 1;
    return                   l;
  }

  public static int log2Floor( long n )
  {
    if(  n <= 0  ) throw new ArithmeticException();
    int                       l = 0;
    if( (n >>> (  32)) != 0 ) l+=32;
    if( (n >>> (l+16)) != 0 ) l+=16;
    if( (n >>> (l+ 8)) != 0 ) l+= 8;
    if( (n >>> (l+ 4)) != 0 ) l+= 4;
    if( (n >>> (l+ 2)) != 0 ) l+= 2;
    if( (n >>> (l+ 1)) != 0 ) l+= 1;
    return                    l;
  }

  public static  int maxUnsigned( int u, int v ) { return Integer.compareUnsigned(u,v) > 0 ? u : v; }
  public static long maxUnsigned(long u,long v ) { return    Long.compareUnsigned(u,v) > 0 ? u : v; }

  public static  int minUnsigned( int u, int v ) { return Integer.compareUnsigned(u,v) < 0 ? u : v; }
  public static long minUnsigned(long u,long v ) { return    Long.compareUnsigned(u,v) < 0 ? u : v; }

  private IMath() {
    throw new UnsupportedOperationException();
  }
}
