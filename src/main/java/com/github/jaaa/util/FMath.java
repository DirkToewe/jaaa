package com.github.jaaa.util;

import static java.lang.Math.log;


/**
 * The "F" stands for floating-point and not whatever you were thinking...
 */
public final class FMath
{
  private static final double LN2 = log(2);

  public static double log2( double x ) {
    return log(x) / LN2;
  }

  private FMath() {
    throw new UnsupportedOperationException("Cannot instantiate static class.");
  }
}
