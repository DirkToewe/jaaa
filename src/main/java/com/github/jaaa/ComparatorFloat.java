package com.github.jaaa;

public interface ComparatorFloat
{
  static final class ReverseComparatorFloat implements ComparatorFloat
  {
    private final ComparatorFloat cmp;

    public ReverseComparatorFloat( ComparatorFloat _cmp ) { cmp = _cmp; }

    @Override public ComparatorFloat reversed() { return cmp; }
    @Override public int compare( float x, float y ) { return cmp.compare(y,x); }
    @Override public int hashCode() {
      return Integer.MIN_VALUE ^ cmp.hashCode();
    }
  }

  public default ComparatorFloat reversed() { return new ComparatorFloat.ReverseComparatorFloat(this); }

  public int compare( float x, float y );
}
