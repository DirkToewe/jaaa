package com.github.jaaa.compare;

public interface ComparatorInt
{
  final class ReverseComparatorInt implements ComparatorInt
  {
    private final ComparatorInt cmp;

    public ReverseComparatorInt( ComparatorInt _cmp ) { cmp = _cmp; }

    @Override public ComparatorInt reversed() { return cmp; }
    @Override public int compare( int x, int y ) { return cmp.compare(y,x); }
    @Override public int hashCode() {
      return Integer.MIN_VALUE ^ cmp.hashCode();
    }
  }

  int compare( int x, int y );

  default ComparatorInt reversed() { return new ReverseComparatorInt(this); }
}
