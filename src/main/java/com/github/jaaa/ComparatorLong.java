package com.github.jaaa;

public interface ComparatorLong
{
  static final class ReverseComparatorLong implements ComparatorLong
  {
    private final ComparatorLong cmp;

    public ReverseComparatorLong( ComparatorLong _cmp ) { cmp = _cmp; }

    @Override public ComparatorLong reversed() { return cmp; }
    @Override public int compare( long x, long y ) { return cmp.compare(y,x); }
    @Override public int hashCode() {
      return Integer.MIN_VALUE ^ cmp.hashCode();
    }
  }

  public default ComparatorLong reversed() { return new ComparatorLong.ReverseComparatorLong(this); }

  public int compare( long x, long y );
}
