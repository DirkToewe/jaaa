package com.github.jaaa.util;

import static java.lang.Math.ceil;
import static java.lang.Math.sqrt;

public final class IMath
{
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

//    int        nBits = 31 - numberOfLeadingZeros(n)  >>>  1,
//        r = 1<<nBits;
//    for( int i=nBits;; ) {
//      int R = r | 1<<--i,
//          c = R*R - n;
//      if( c <= 0 ) r = R;
//      if( c == 0 ||
//          i == 0 ) return r;
//    }

    return (int) Math.sqrt(n);
  }

  public static int sign(  int n ) {
    return n < 0 ? -1
         : n > 0 ? +1 : 0;
  }
  public static int sign( long n ) {
    return n < 0 ? -1
         : n > 0 ? +1 : 0;
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

  public static int log2Ceil( int n )
  {
    if(  n <= 0  ) throw new ArithmeticException();
    int                      l =31;
    if( (1 << (l-16)) >= n ) l-=16;
    if( (1 << (l- 8)) >= n ) l-= 8;
    if( (1 << (l- 4)) >= n ) l-= 4;
    if( (1 << (l- 2)) >= n ) l-= 2;
    if( (1 << (l- 1)) >= n ) l-= 1;
    return                   l;
  }

  public static  int maxUnsigned( int u, int v ) { return Integer.compareUnsigned(u,v) > 0 ? u : v; }
  public static long maxUnsigned(long u,long v ) { return    Long.compareUnsigned(u,v) > 0 ? u : v; }

  public static  int minUnsigned( int u, int v ) { return Integer.compareUnsigned(u,v) < 0 ? u : v; }
  public static long minUnsigned(long u,long v ) { return    Long.compareUnsigned(u,v) < 0 ? u : v; }

  private IMath() {
    throw new UnsupportedOperationException();
  }
}
