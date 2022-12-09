package com.github.jaaa.compare;


public interface ComparatorByte
{
  final class ReverseComparatorByte implements ComparatorByte
  {
    private final ComparatorByte cmp;

    public ReverseComparatorByte( ComparatorByte _cmp ) { cmp = _cmp; }

    @Override public ComparatorByte reversed() { return cmp; }
    @Override public int compare( byte x, byte y ) { return cmp.compare(y,x); }
    @Override public int hashCode() {
      return Integer.MIN_VALUE ^ cmp.hashCode();
    }
  }

  default ComparatorByte reversed() { return new ReverseComparatorByte(this); }

  int compare( byte x, byte y );
}
