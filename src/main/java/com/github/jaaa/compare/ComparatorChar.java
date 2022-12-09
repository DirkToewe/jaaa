package com.github.jaaa.compare;

public interface ComparatorChar
{
  final class ReverseComparatorChar implements ComparatorChar
  {
    private final ComparatorChar cmp;

    public ReverseComparatorChar( ComparatorChar _cmp ) { cmp = _cmp; }

    @Override public ComparatorChar reversed() { return cmp; }
    @Override public int compare( char x, char y ) { return cmp.compare(y,x); }
    @Override public int hashCode() {
      return Integer.MIN_VALUE ^ cmp.hashCode();
    }
  }

  default ComparatorChar reversed() { return new ComparatorChar.ReverseComparatorChar(this); }

  int compare( char x, char y );
}
