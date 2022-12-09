package com.github.jaaa.compare;

public interface ComparatorShort
{
  final class ReverseComparatorShort implements ComparatorShort
  {
    private final ComparatorShort cmp;

    public ReverseComparatorShort( ComparatorShort _cmp ) { cmp = _cmp; }

    @Override public ComparatorShort reversed() { return cmp; }
    @Override public int compare( short x, short y ) { return cmp.compare(y,x); }
    @Override public int hashCode() {
      return Integer.MIN_VALUE ^ cmp.hashCode();
    }
  }

  default ComparatorShort reversed() { return new ReverseComparatorShort(this); }

  int compare( short x, short y );
}
