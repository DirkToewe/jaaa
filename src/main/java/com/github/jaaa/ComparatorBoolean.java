package com.github.jaaa;

public interface ComparatorBoolean
{
  static final class ReverseComparatorBoolean implements ComparatorBoolean
  {
    private final ComparatorBoolean cmp;

    public ReverseComparatorBoolean( ComparatorBoolean _cmp ) { cmp = _cmp; }

    @Override public ComparatorBoolean reversed() { return cmp; }
    @Override public int compare( boolean x, boolean y ) { return cmp.compare(y,x); }
    @Override public int hashCode() {
      return Integer.MIN_VALUE ^ cmp.hashCode();
    }
  }

  public default ComparatorBoolean reversed() { return new ReverseComparatorBoolean(this); }

  public int compare( boolean x, boolean y );
}
