package com.github.jaaa.misc;

public class CheckArgsMisc
{
// STATIC FIELDS

// STATIC CONSTRUCTOR

// STATIC METHODS
  public static void checkArgs_fromUntil( int from, int until, int len )
  {
    if( from < 0     ) throw new IllegalArgumentException();
    if( from > until ) throw new IllegalArgumentException();
    if(  len < until ) throw new IllegalArgumentException();
  }

// FIELDS

// CONSTRUCTORS
  private CheckArgsMisc() { throw new UnsupportedOperationException("Static class cannot be instantiated."); }

// METHODS
}
