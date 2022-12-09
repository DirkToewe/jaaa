package com.github.jaaa.compare;

public interface ComparatorLong
{
  final class ReverseComparatorLong implements ComparatorLong
  {
    private final ComparatorLong cmp;

    public ReverseComparatorLong( ComparatorLong _cmp ) { cmp = _cmp; }

    @Override public ComparatorLong reversed() { return cmp; }
    @Override public int compare( long x, long y ) { return cmp.compare(y,x); }
    @Override public int hashCode() {
      return Integer.MIN_VALUE ^ cmp.hashCode();
    }
  }

  default ComparatorLong reversed() { return new ComparatorLong.ReverseComparatorLong(this); }

  int compare( long x, long y );
}
