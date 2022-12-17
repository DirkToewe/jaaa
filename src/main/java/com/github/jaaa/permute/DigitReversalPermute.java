package com.github.jaaa.permute;

public final class DigitReversalPermute
{
  static int digitReverseIncrement( int radix, int reverseInt, int addedDigit ) {
    while( addedDigit != 0 ) {
      int x = reverseInt / addedDigit;
      if( x % radix != radix-1 ) break;
      reverseInt -= addedDigit * (radix-1);
      addedDigit /= radix;
    }
    return reverseInt + addedDigit;
  }
  private DigitReversalPermute() {
    throw new UnsupportedOperationException("Cannot instantiate static class.");
  }
}
