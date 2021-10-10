package com.github.jaaa.util;

import net.jqwik.api.Example;
import net.jqwik.api.ForAll;
import net.jqwik.api.Property;
import net.jqwik.api.PropertyDefaults;
import net.jqwik.api.constraints.IntRange;
import net.jqwik.api.constraints.LongRange;
import net.jqwik.api.constraints.Negative;
import net.jqwik.api.constraints.Positive;

import java.math.BigInteger;

import static com.github.jaaa.util.IMath.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;


@PropertyDefaults( tries = 100_000 )
public class IMathTest
{
  @Property void gcd_int( @ForAll int x, @ForAll int y )
  {
    var X = BigInteger.valueOf(x);
    var Y = BigInteger.valueOf(y);
    assertThat( gcd(x,y) ).isEqualTo( X.gcd(Y).intValue() );
  }

  @Property void gcd_long( @ForAll long x, @ForAll long y )
  {
    var X = BigInteger.valueOf(x);
    var Y = BigInteger.valueOf(y);
    assertThat( gcd(x,y) ).isEqualTo( X.gcd(Y).longValue() );
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
