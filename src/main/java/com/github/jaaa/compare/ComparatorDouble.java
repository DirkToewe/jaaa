package com.github.jaaa.compare;

public interface ComparatorDouble
{
  final class ReverseComparatorDouble implements ComparatorDouble
  {
    private final ComparatorDouble cmp;

    public ReverseComparatorDouble( ComparatorDouble _cmp ) { cmp = _cmp; }

    @Override public ComparatorDouble reversed() { return cmp; }
    @Override public int compare( double x, double y ) { return cmp.compare(y,x); }
    @Override public int hashCode() {
      return Integer.MIN_VALUE ^ cmp.hashCode();
    }
  }

  default ComparatorDouble reversed() { return new ComparatorDouble.ReverseComparatorDouble(this); }

  int compare( double x, double y );
}
