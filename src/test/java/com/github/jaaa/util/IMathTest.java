package com.github.jaaa.util;

import net.jqwik.api.*;
import net.jqwik.api.constraints.IntRange;
import net.jqwik.api.constraints.LongRange;
import net.jqwik.api.constraints.Negative;
import net.jqwik.api.constraints.Positive;

import java.math.BigInteger;
import java.util.HashSet;
import java.util.Set;
import java.util.function.IntConsumer;
import java.util.stream.IntStream;
import java.util.stream.LongStream;

import static com.github.jaaa.util.IMath.*;
import static java.lang.Math.abs;
import static java.lang.Math.multiplyExact;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;


@PropertyDefaults( tries = 100_000 )
public class IMathTest
{
  @Property void bitReverseIncrement_int( @ForAll int reverseInt ) {
    IntConsumer testImpl = addedBit -> {
      int tst = bitReverseIncrement(reverseInt, addedBit);
      int ref = Integer.reverse(
        Integer.reverse(reverseInt) +
        Integer.reverse(addedBit)
      );
      assertThat(tst).isEqualTo(ref);
    };

    for( int i=0; i < 32; i++ )
      testImpl.accept(1<<i);
    testImpl.accept(0);
  }

  @Group
  class IsPowerOf {
    @Example void isPowerOf_2_int() { isPowerOf_int(2); }
    @Example void isPowerOf_3_int() { isPowerOf_int(3); }
    @Example void isPowerOf_4_int() { isPowerOf_int(4); }
    @Example void isPowerOf_5_int() { isPowerOf_int(5); }
    @Example void isPowerOf_6_int() { isPowerOf_int(6); }
    @Example void isPowerOf_7_int() { isPowerOf_int(7); }
    @Example void isPowerOf_8_int() { isPowerOf_int(8); }
    @Example void isPowerOf_9_int() { isPowerOf_int(9); }
    private  void isPowerOf_int( int base ) {
      assertThat(base).isGreaterThan(1);
      Set<Integer> powers = new HashSet<>();
      try {
        for( int p=1;; p = multiplyExact(p,base) )
          powers.add(p);
      }
      catch( ArithmeticException ignored ) {}

      IntStream.rangeClosed(Integer.MIN_VALUE, Integer.MAX_VALUE).forEach(
        x -> assertThat( isPowerOf(base,x) ).isEqualTo( powers.contains(x) )
      );
    }
  }

  @Example void isPowerOf3_int() {
    Set<Integer> powers = new HashSet<>();
    try {
      for( int p=1;; p = multiplyExact(p,3) )
        powers.add(p);
    }
    catch( ArithmeticException ignored ) {}

    IntStream.rangeClosed(Integer.MIN_VALUE, Integer.MAX_VALUE).forEach(
      x -> assertThat( isPowerOf3(x) ).isEqualTo( powers.contains(x) )
    );
  }

  @Example void isPowerOf2_int() {
    IntStream.rangeClosed(Integer.MIN_VALUE, Integer.MAX_VALUE).forEach(
      x -> assertThat( hasOneBit(x) ).isEqualTo( Integer.bitCount(x) == 1 )
    );
  }
  @Example void isPowerOf2_orZero_int() {
    IntStream.rangeClosed(Integer.MIN_VALUE, Integer.MAX_VALUE).forEach(
      x -> assertThat( hasOneBitOrNone(x) ).isEqualTo( Integer.bitCount(x) <= 1 )
    );
  }

  @Example void isPowerOf2_long() {
    LongStream.rangeClosed(Integer.MIN_VALUE, Integer.MAX_VALUE).forEach(this::isPowerOf2_long);
  }
  @Example void isPowerOf2_orZero_long() {
    LongStream.rangeClosed(Integer.MIN_VALUE, Integer.MAX_VALUE).forEach(this::isPowerOf2_orZero_long);
  }

  @Property( tries = Integer.MAX_VALUE ) void isPowerOf2_long( @ForAll long x ) {
    assertThat( hasOneBit(x) ).isEqualTo( Long.bitCount(x) == 1 );
  }
  @Property( tries = Integer.MAX_VALUE ) void isPowerOf2_orZero_long( @ForAll long x ) {
    assertThat( hasOneBitOrNone(x) ).isEqualTo( Long.bitCount(x) <= 1 );
  }

  @Property void gcd_int( @ForAll int x, @ForAll int y )
  {
    BigInteger
      X = BigInteger.valueOf(x),
      Y = BigInteger.valueOf(y);
    assertThat( gcd(x,y) ).isEqualTo( X.gcd(Y).intValue() );
  }

  @Property void gcd_long( @ForAll long x, @ForAll long y )
  {
    BigInteger
      X = BigInteger.valueOf(x),
      Y = BigInteger.valueOf(y);
    assertThat( gcd(x,y) ).isEqualTo( X.gcd(Y).longValue() );
  }

  @Group
  class GCDX
  {
    @Property void int_gcd( @ForAll @IntRange(min=0) int x, @ForAll @IntRange(min=0) int y )
    {
      BigInteger
        X = BigInteger.valueOf(x),
        Y = BigInteger.valueOf(y);
      assertThat( gcdx(x,y).gcd ).isEqualTo( X.gcd(Y).intValue() );
    }

    @Property void int_bezout( @ForAll @IntRange(min=0) int x, @ForAll @IntRange(min=0) int y )
    {
      GcdxInt gcdx = gcdx(x,y);
      assertThat( gcdx.gcd ).isEqualTo( gcdx.bezoutL*x + gcdx.bezoutR*y );
    }

    @Property void int_minimality( @ForAll @IntRange(min=0) int a, @ForAll @IntRange(min=0) int b )
    {
      GcdxInt gcdx = gcdx(a,b);
      int d = gcdx.gcd,
          x = gcdx.bezoutL,
          y = gcdx.bezoutR;
      if( d == 0 ) {
        assertThat( gcdx.bezoutL ).isEqualTo(0);
        assertThat( gcdx.bezoutR ).isEqualTo(0);
      }
      else if( a == b )
        assertThat( Tuple.of(x,y) ).isIn( Tuple.of(1,0), Tuple.of(0,1) );
      else if( d == a ) {
        assertThat(x).isEqualTo(1);
        assertThat(y).isEqualTo(0);
      }
      else if( d == b ) {
        assertThat(x).isEqualTo(0);
        assertThat(y).isEqualTo(1);
      }
      else {
        assertThat( abs(x) ).isLessThanOrEqualTo(b/d >>> 1);
        assertThat( abs(y) ).isLessThanOrEqualTo(a/d >>> 1);
      }
    }

    @Property void long_gcd( @ForAll @LongRange(min=0) long x, @ForAll @LongRange(min=0) long y )
    {
      BigInteger
        X = BigInteger.valueOf(x),
        Y = BigInteger.valueOf(y);
      assertThat( gcdx(x,y).gcd ).isEqualTo( X.gcd(Y).longValue() );
    }

    @Property void long_bezout( @ForAll @LongRange(min=0) long x, @ForAll @LongRange(min=0) long y )
    {
      GcdxLong gcdx = gcdx(x,y);
      assertThat( gcdx.gcd ).isEqualTo( gcdx.bezoutL*x + gcdx.bezoutR*y );
    }

    @Property void long_minimality( @ForAll @LongRange(min=0) long a, @ForAll @LongRange(min=0) long b )
    {
      GcdxLong gcdx = gcdx(a,b);
      long d = gcdx.gcd,
           x = gcdx.bezoutL,
           y = gcdx.bezoutR;
      if( d == 0 ) {
        assertThat( gcdx.bezoutL ).isEqualTo(0L);
        assertThat( gcdx.bezoutR ).isEqualTo(0L);
      }
      else if( a == b )
        assertThat( Tuple.of(x,y) ).isIn( Tuple.of(1L,0L), Tuple.of(0L,1L) );
      else if( d == a ) {
        assertThat(x).isEqualTo(1);
        assertThat(y).isEqualTo(0);
      }
      else if( d == b ) {
        assertThat(x).isEqualTo(0);
        assertThat(y).isEqualTo(1);
      }
      else {
        assertThat( abs(x) ).isLessThanOrEqualTo(b/d >>> 1);
        assertThat( abs(y) ).isLessThanOrEqualTo(a/d >>> 1);
      }
    }
  }

  @Property void log2FloorIntTestPos( @ForAll @Positive int n )
  {
    int           l = log2Floor(n);
    assertThat(1<<l).isLessThanOrEqualTo(n);
    assertThat( Integer.compareUnsigned(1<<l+1, n) ).isGreaterThan(0);
  }

  @Property void log2FloorIntTestNeg( @ForAll @IntRange(min=Integer.MIN_VALUE, max=0) int n )
  {
    assertThatCode( () -> log2Floor(n) ).isInstanceOf(ArithmeticException.class);
  }

  @Property void log2FloorLongTestPos( @ForAll @Positive long n )
  {
    int            l = log2Floor(n);
    assertThat(1L<<l).isLessThanOrEqualTo(n);
    assertThat( Long.compareUnsigned(1L<<l+1, n) ).isGreaterThan(0);
  }

  @Property void log2FloorLongTestNeg( @ForAll @LongRange(min=Long.MIN_VALUE, max=0) long n )
  {
    assertThatCode( () -> log2Floor(n) ).isInstanceOf(ArithmeticException.class);
  }

  @Property void log2CeilIntTestPos( @ForAll @Positive int n )
  {
    int            l = log2Ceil(n);
    assertThat(1L<<l).isGreaterThanOrEqualTo(n);
    assertThat(l).satisfiesAnyOf(
      x -> assertThat(n).isEqualTo(1),
      x -> assertThat( Integer.compareUnsigned(1<<x-1, n) ).isLessThan(0)
    );
  }

  @Property void log2CeilIntTestNeg( @ForAll @IntRange(min=Integer.MIN_VALUE, max=0) int n )
  {
    assertThatCode( () -> log2Ceil(n) ).isInstanceOf(ArithmeticException.class);
  }

  @Property void log2CeilLongTestPos( @ForAll @Positive long n )
  {
    int            l = log2Ceil(n);
    assertThat( Long.compareUnsigned(1L<<l, n) ).isGreaterThanOrEqualTo(0);
    assertThat(l).satisfiesAnyOf(
      x -> assertThat(n).isEqualTo(1),
      x -> assertThat( Long.compareUnsigned(1L<<x-1, n) ).isLessThan(0)
    );
  }

  @Property void log2CeilLongTestNeg( @ForAll @LongRange(min=Long.MIN_VALUE, max=0) long n )
  {
    assertThatCode( () -> log2Ceil(n) ).isInstanceOf(ArithmeticException.class);
  }

  @Group class Pow {
    private int ref( int base, long exp ) {
      return BigInteger.valueOf(base).modPow(
        BigInteger.valueOf(exp),
        BigInteger.valueOf(1L<<32)
      ).intValue();
    }
    @Property void test1( @ForAll int base, @ForAll @LongRange(min=0) long exp ) {
      int tst = pow(base,exp),
          ref = ref(base,exp);
      assertThat(tst).isEqualTo(ref);
    }
    @Property void test2( @ForAll @IntRange(min=0, max=31) int baseShift, @ForAll @LongRange(min=0) long exp ) {
      int base = 1 << baseShift;
      int tst = pow(base,exp),
          ref = ref(base,exp);
      assertThat(tst).isEqualTo(ref);
    }
    @Property void test3( @ForAll @IntRange(min=0, max=31) int baseShift, @ForAll @LongRange(min=0) long exp ) {
      int base = -(1 << baseShift);
      int tst = pow(base,exp),
          ref = ref(base,exp);
      assertThat(tst).isEqualTo(ref);
    }
  }

  @Property void signIntTestNeg( @ForAll @Negative int n ) { assertThat( sign(n) ).isEqualTo(-1); }
  @Example  void signIntTestNul()                          { assertThat( sign(0) ).isEqualTo( 0); }
  @Property void signIntTestPos( @ForAll @Positive int n ) { assertThat( sign(n) ).isEqualTo(+1); }

  @Property void signLongTestNeg( @ForAll @Negative long n ) { assertThat( sign(n) ).isEqualTo(-1); }
  @Example  void signLongTestNul()                           { assertThat( sign(0) ).isEqualTo( 0); }
  @Property void signLongTestPos( @ForAll @Positive long n ) { assertThat( sign(n) ).isEqualTo(+1); }

  @Property void sqrtCeilIntTestPos( @ForAll @IntRange(min=0) int n )
  {
    int r = sqrtCeil(n);
                 assertThat(r).isNotNegative();
                 assertThat( Integer.compareUnsigned(r*r,n) ).isGreaterThanOrEqualTo(0);
    if( n != 0 ) assertThat( (r-1)*(r-1) ).isLessThan(n);
  }

  @Property void sqrtCeilIntTestNeg( @ForAll @Negative int n )
  {
    assertThatCode( () -> sqrtCeil(n) ).isInstanceOf(ArithmeticException.class);
  }

  @Property void sqrtFloorIntTestPos( @ForAll @IntRange(min=0) int n )
  {
    int         r = sqrtFloor(n);
    assertThat(r).isNotNegative();
    assertThat(r*r).isLessThanOrEqualTo(n);
    assertThat( Integer.compareUnsigned((r+1)*(r+1), n) ).isGreaterThan(0);
  }

  @Property void sqrtFloorIntTestNeg( @ForAll @Negative int n )
  {
    assertThatCode( () -> sqrtFloor(n) ).isInstanceOf(ArithmeticException.class);
  }

  @Property void sqrtCeilLongTestPos( @ForAll @LongRange(min=0) long n )
  {
    long       r = sqrtCeil(n);
    assertThat(r).isNotNegative();
    assertThat( Long.compareUnsigned(r*r,n) ).isGreaterThanOrEqualTo(0);
    if( n != 0 ) assertThat( (r-1)*(r-1) ).isLessThan(n);
  }

  @Property void sqrtCeilLongTestNeg( @ForAll @Negative long n )
  {
    assertThatCode( () -> sqrtCeil(n) ).isInstanceOf(ArithmeticException.class);
  }

  @Property void sqrtFloorLongTestPos( @ForAll @LongRange(min=0) long n )
  {
    long       r = sqrtFloor(n);
    assertThat(r).isNotNegative();
    assertThat(r*r).isLessThanOrEqualTo(n);
    assertThat( Long.compareUnsigned((r+1)*(r+1), n) ).isGreaterThan(0);
  }

  @Property void sqrtFloorLongTestNeg( @ForAll @Negative long n )
  {
    assertThatCode( () -> sqrtFloor(n) ).isInstanceOf(ArithmeticException.class);
  }
}
