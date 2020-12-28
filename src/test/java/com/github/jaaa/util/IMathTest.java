package com.github.jaaa.util;

import net.jqwik.api.Example;
import net.jqwik.api.ForAll;
import net.jqwik.api.Property;
import net.jqwik.api.constraints.IntRange;
import net.jqwik.api.constraints.Negative;
import net.jqwik.api.constraints.Positive;

import static com.github.jaaa.util.IMath.*;
import static java.lang.Integer.compareUnsigned;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

public class IMathTest
{
  private static final int N_TRIES = 100_000;

  @Property( tries = N_TRIES )
  void log2FloorTest1( @ForAll @Positive int n )
  {
    int           l = log2Floor(n);
    assertThat(1<<l).isLessThanOrEqualTo(n);
    assertThat( compareUnsigned(1<<l+1, n) ).isGreaterThan(0);
  }

  @Property( tries = N_TRIES )
  void log2FloorTest2( @ForAll @IntRange(max=0) int n )
  {
    assertThatCode( () -> log2Floor(n) ).isInstanceOf(ArithmeticException.class);
  }

  @Property( tries = N_TRIES )
  void log2CeilTestPos( @ForAll @Positive int n )
  {
    int            l = log2Ceil(n);
    assertThat(1L<<l).isGreaterThanOrEqualTo(n);
    assertThat(l).satisfiesAnyOf(
      x -> assertThat(n).isEqualTo(1),
      x -> assertThat( compareUnsigned(1<<x-1, n) ).isLessThan(0)
    );
  }

  @Property( tries = N_TRIES )
  void log2CeilTestNeg( @ForAll @IntRange(max=0) int n )
  {
    assertThatCode( () -> log2Ceil(n) ).isInstanceOf(ArithmeticException.class);
  }

  @Property( tries = N_TRIES ) void signTestNeg( @ForAll @Negative int n ) { assertThat( sign(n) ).isEqualTo(-1); }
  @Example                     void signTestNul()                          { assertThat( sign(0) ).isEqualTo( 0); }
  @Property( tries = N_TRIES ) void signTestPos( @ForAll @Positive int n ) { assertThat( sign(n) ).isEqualTo(+1); }

  @Property( tries = N_TRIES )
  void sqrtCeilTestPos( @ForAll @IntRange(min=0) int n )
  {
    int r = sqrtCeil(n);
                 assertThat(r).isNotNegative();
                 assertThat( compareUnsigned(r*r,n) ).isGreaterThanOrEqualTo(0);
    if( n != 0 ) assertThat( (r-1)*(r-1) ).isLessThan(n);
  }

  @Property( tries = N_TRIES )
  void sqrtCeilTestNeg( @ForAll @Negative int n )
  {
    assertThatCode( () -> sqrtCeil(n) ).isInstanceOf(ArithmeticException.class);
  }

  @Property( tries = N_TRIES )
  void sqrtFloorTestPos( @ForAll @IntRange(min=0) int n )
  {
    int         r = sqrtFloor(n);
    assertThat(r).isNotNegative();
    assertThat(r*r).isLessThanOrEqualTo(n);
    assertThat( compareUnsigned((r+1)*(r+1), n) ).isGreaterThan(0);
  }

  @Property( tries = N_TRIES )
  void sqrtFloorTestNeg( @ForAll @Negative int n )
  {
    assertThatCode( () -> sqrtFloor(n) ).isInstanceOf(ArithmeticException.class);
  }
}
