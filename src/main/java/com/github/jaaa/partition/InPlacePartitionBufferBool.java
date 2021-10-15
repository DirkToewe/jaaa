package com.github.jaaa.partition;

import com.github.jaaa.PredicateSwapAccess;
import com.github.jaaa.buf.BufferBool;


final class InPlacePartitionBufferBool implements BufferBool
{
// STATIC FIELDS

// STATIC CONSTRUCTOR

// STATIC METHODS

// FIELDS
  private final int a, b, len;
  private final PredicateSwapAccess acc;
  private boolean tru = true;

// CONSTRUCTORS
  public InPlacePartitionBufferBool( PredicateSwapAccess _acc, int _a, int _b, int _len )
  {
    assert      _len >= 0;
    assert _a         >= 0;
    assert _b         >= 0;
    assert _a + _len >= 0 : "Integer overflow.";
    assert _b + _len >= 0 : "Integer overflow.";
    assert _acc != null;

//    assert range(_a,_a+_len).noneMatch(_acc::predicate);
//    assert range(_b,_b+_len). allMatch(_acc::predicate);

    acc = _acc;
    a = _a;
    b = _b;
    len = _len;
  }

// METHODS
  public int len() { return len; }

  public boolean get( int i ) {
    assert i >= 0;
    assert i < len;
    return acc.predicate(a+i) == tru;
  }

  public void flip( int i ) {
    assert i >= 0;
    assert i < len;
    acc.swap(a+i, b+i);
  }

  public void flipRange( int from, int until ) {
    assert 0 <= from;
    assert      from <= until;
    assert              until <= len;
    while( until-- > from )
      acc.swap(a+until, b+until);
  }

  public boolean isTruthInverted() {
    return !tru;
  }

  public void flipTruth() {
    tru = !tru; // "Today's not opposite day!!!" - "So it IS opposite day!"
  }
}
