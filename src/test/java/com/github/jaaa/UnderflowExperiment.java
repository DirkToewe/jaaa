package com.github.jaaa;

import java.util.function.DoubleToIntFunction;

public class UnderflowExperiment
{
  public static double binarySearchGapL( double from, double until, DoubleToIntFunction compass )
  {
    if( from < 0 || from > until ) throw new IllegalArgumentException();

    while( from < until ) { double mid = from + (until-from)/2;
      if( mid == until )
          mid = Math.nextDown(until);
      int c = compass.applyAsInt(mid);
      if( c <= 0 )       until = mid;
      else                from = Math.nextUp(mid);
    }
    return from;
  }

  public static void main( String... args )
  {
//    var small = binarySearchGapL(0, Math.sqrt(Double.MIN_VALUE), x -> x*x == 0 ? +1 : -1);
//    System.out.println(small);
//    System.out.println(small*small); small = Math.nextDown(small);
//    System.out.println(small);
//    System.out.println(small*small);
//
//    System.out.println("\n\n");
//    var large = binarySearchGapL(0, Double.MAX_VALUE, x -> Double.isInfinite(x*x) ? -1 : +1);
//    System.out.println(large);
//    System.out.println(large*large); large = Math.nextDown(large);
//    System.out.println(large);
//    System.out.println(large*large);

    var tinyF32 = (float) binarySearchGapL(0, 1, x -> Float.isInfinite(1f / (float) x) ? +1 : -1);
    System.out.println(tinyF32);
    System.out.println( 1f / tinyF32 ); tinyF32 = Math.nextDown(tinyF32);
    System.out.println(tinyF32);
    System.out.println( 1f / tinyF32 );
  }
}
