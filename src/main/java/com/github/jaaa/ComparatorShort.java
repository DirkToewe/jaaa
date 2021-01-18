package com.github.jaaa;

public interface ComparatorShort
{
  static final class ReverseComparatorShort implements ComparatorShort
  {
    private final ComparatorShort cmp;

    public ReverseComparatorShort( ComparatorShort _cmp ) { cmp = _cmp; }

    @Override public ComparatorShort reversed() { return cmp; }
    @Override public int compare( short x, short y ) { return cmp.compare(y,x); }
    @Override public int hashCode() {
      return Integer.MIN_VALUE ^ cmp.hashCode();
    }
  }

  public default ComparatorShort reversed() { return new ReverseComparatorShort(this); }

  public int compare( short x, short y );
}
