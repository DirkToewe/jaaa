package com.github.jaaa.permute;

public final class TritReversalPermute
{
  static int tritReverseIncrement( int reverseInt, int addedTrit ) {
    while( addedTrit != 0 ) {
      int x = reverseInt / addedTrit;
      if( x % 3 != 2 ) break;
      reverseInt -= addedTrit<<1;
      addedTrit /= 3;
    }
    return reverseInt + addedTrit;
  }

  private TritReversalPermute() {
    throw new UnsupportedOperationException("Cannot instantiate static class.");
  }
}
