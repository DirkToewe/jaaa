package com.github.jaaa.examples;

import com.github.jaaa.search.ExpL2RSearch;
import java.util.stream.IntStream;
import static java.lang.Math.*;

public class Example6 {
  private static int sqrtFloor( int x ) {
    if( x < 0 ) throw new IllegalArgumentException();
    return ExpL2RSearch.searchGapL(0, Integer.MAX_VALUE, y -> Long.signum((long)y*y - x) );
  }
  public static void main( String... args ) {
    IntStream.rangeClosed(0, Integer.MAX_VALUE).parallel().forEach( x -> {
      assert sqrtFloor(x) == floor( sqrt(x) );
    });
  }
}
