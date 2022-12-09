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
    int l = Integer.numberOfTrailingZeros(x); x >>>= l;
    int r = Integer.numberOfTrailingZeros(y); y >>>= r;
    int shift = min(l,r);
    if( x > y ) {
      int z = x; x = y; y = z;
    }
    while( x != 0 ) {
      // Interleave euclidean and binary GCD.
      // Ensures that y halves each iteration.
      int z = x > (y>>>1) ? y-x : y%x;
      y = x;
      x = z;
      // Invariant: x <= y && (y is odd)
      x >>>= Integer.numberOfTrailingZeros(x);
    }
    return y << shift;
  }

  public static long gcd( long x, long y )
  {
    x = abs(x);
    y = abs(y);
    int l = Long.numberOfTrailingZeros(x); x >>>= l;
    int r = Long.numberOfTrailingZeros(y); y >>>= r;
    int shift = min(l,r);
    if( x > y ) {
      long z = x; x = y; y = z;
    }
    while( x != 0 ) {
      // Interleave euclidean and binary GCD.
      // Ensures that y halves each iteration.
      long z = x > (y>>>1) ? y-x : y%x;
      y = x;
      x = z;
      // Invariant: x <= y && (y is odd)
      x >>>= Long.numberOfTrailingZeros(x);
    }
    return y << shift;
  }

  public record       GcdxInt( int gcd, int bezoutL, int bezoutR ) {}
  public static final GcdxInt GCDX_INT_ZERO = new GcdxInt(0,0,0);
  public static       GcdxInt gcdx( int a, int b )
  {
    if( a == 0 && b == 0 ) return GCDX_INT_ZERO;
    if( a <  0 || b <  0 ) throw new IllegalArgumentException();
    // https://en.wikipedia.org/wiki/Extended_Euclidean_algorithm
    // use negative a and b because -Int.MinValue == Int.MinValue
    int r = a, rNew = b,
        s = 1, sNew = 0;

    while( rNew != 0 ) {
      int q = r / rNew;
      int rNext = r - q * rNew; r = rNew; rNew = rNext;
      int sNext = s - q * sNew; s = sNew; sNew = sNext;
    }

    int t = b==0 ? 0 : (int) ((r - (long)s*a) / b);
    return new GcdxInt(r,s,t);
  }

  public record       GcdxLong( long gcd, long bezoutL, long bezoutR ) {}
  public static final GcdxLong GCDX_LONG_ZERO = new GcdxLong(0,0,0);
  public static       GcdxLong gcdx( long a, long b )
  {
    if( a == 0 && b == 0 ) return GCDX_LONG_ZERO;
    if( a <  0 || b <  0 ) throw new IllegalArgumentException();
    // https://en.wikipedia.org/wiki/Extended_Euclidean_algorithm
    // use negative a and b because -Int.MinValue == Int.MinValue
    long r = a,  rNew = b,
         s = 1L, sNew = 0L,
         t = 0L, tNew = 1L;

    while( rNew != 0 ) {
      var             q = r / rNew;
      var rNext = r - q*rNew; r = rNew; rNew = rNext;
      var sNext = s - q*sNew; s = sNew; sNew = sNext;
      var tNext = t - q*tNew; t = tNew; tNew = tNext;
    }

    return new GcdxLong(r,s,t);
  }

  public static int pow( int base, long exp )
  {
    if( exp < 0 )
      throw new ArithmeticException();
    if( exp == 0 || base == 1 ) return 1;
    if( exp == 1 || base == 0 ) return base;
    if( hasOneBitOrNone(base) ) {
      int e = Integer.numberOfTrailingZeros(base) * (int) min(exp,32);
      return e < 32 ? 1<<e : 0;
    }
    if( hasOneBitOrNone(-base) ) {
      int e = Integer.numberOfTrailingZeros(-base) * (int) min(exp,32);
      if( e >= 32 )
        return 0;
      int sgn = (int) exp & 1;
      return (1<<e ^ -sgn) + sgn; // <- if isEven(exp) then pow else -pow
    }
    // https://en.wikipedia.org/wiki/Exponentiation_by_squaring
    int pow = 1;
    while(exp > 0) {
      if((exp & 1) == 1)
        pow *= base;
      base *= base;
      exp >>>= 1;
    }
    return pow;

//    require( 0 <= exponent )
//    if exponent == 0 || base == 1 then
//      1
//    else if exponent == 1 || base == 0 then
//      base
//    else if hasOneBitOrNone(base) then
//      val exp = Integer.numberOfTrailingZeros(base) * min(exponent,32).toInt
//      if  exp < 32 then 1<<exp else 0
//    else if hasOneBitOrNone(-base) then
//      val exp = Integer.numberOfTrailingZeros(-base) * min(exponent,32).toInt
//      if  exp >= 32 then
//        0
//      else
//        val sgn = exponent.toInt & 1
//        (1<<exp ^ -sgn) + sgn // <- if isEven(exponent) then pow else -pow
//    else
//      // https://en.wikipedia.org/wiki/Exponentiation_by_squaring
//      var   bas = base
//      var   pow = 1
//      var   exp = exponent
//      while exp > 0 do
//        if (exp & 1) == 1 then
//          pow *= bas
//        bas *= bas
//        exp >>>= 1
//      end while
//      pow
//    end if
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

  public static int floorPow3( int num ) {
    if( num < 1 )
      throw new ArithmeticException();
    var pow = 43046721L;
    if( pow > num )
        pow = 1;
    long
    p = pow * 6561; if( p <= num ) pow = p;
    p = pow * 81;   if( p <= num ) pow = p;
    p = pow * 9;    if( p <= num ) pow = p;
    p = pow * 3;    if( p <= num ) pow = p;
    return (int) pow;
  }

  public static boolean isPowerOf3( int num ) {
    if( num <= 1 )
      return num == 1;
    return floorPow3(num) == num;
  }

  public static int floorPow( int base, int num ) {
    if( base < 2 )
      throw new ArithmeticException();
    long bb = (long) base * base,
     p = bb > num ? 1L : floorPow((int) bb, num),
     q = p * base;
    return (int) (q <= num ? q : p);
  }

  public static boolean isPowerOf( int base, int num ) {
    return floorPow(base,num) == num;
  }

  public static boolean hasOneBit( int n ) { return (n & n-1) == 0 && n != 0; }
  public static boolean hasOneBit(long n ) { return (n & n-1) == 0 && n != 0; }
  public static boolean hasOneBitOrNone( int n ) { return (n & n-1) == 0; }
  public static boolean hasOneBitOrNone(long n ) { return (n & n-1) == 0; }

  public static int bitReverseIncrement( int reverseInt, int addedBit ) {
    if( ! hasOneBitOrNone(addedBit) )
      throw new IllegalArgumentException();
    for(;;) {
      int bit = reverseInt & addedBit;
      reverseInt ^= addedBit;
      if( bit == 0 )
        return reverseInt;
      addedBit >>>= 1;
    }
  }

  public static  int maxUnsigned( int u, int v ) { return Integer.compareUnsigned(u,v) > 0 ? u : v; }
  public static long maxUnsigned(long u,long v ) { return    Long.compareUnsigned(u,v) > 0 ? u : v; }

  public static  int minUnsigned( int u, int v ) { return Integer.compareUnsigned(u,v) < 0 ? u : v; }
  public static long minUnsigned(long u,long v ) { return    Long.compareUnsigned(u,v) < 0 ? u : v; }

  private IMath() {
    throw new UnsupportedOperationException();
  }
}
