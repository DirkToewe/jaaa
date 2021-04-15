package com.github.jaaa.partition;

import com.github.jaaa.PredicateSwapAccess;


final class InplaceBufferBool
{
// STATIC FIELDS

// STATIC CONSTRUCTOR

// STATIC METHODS

// FIELDS
  private final int a, b, size;
  private final PredicateSwapAccess acc;
  private boolean tru = true;

// CONSTRUCTORS
  public InplaceBufferBool( PredicateSwapAccess _acc, int _a, int _b, int _size )
  {
    assert      _size >= 0;
    assert _a         >= 0;
    assert _b         >= 0;
    assert _a + _size >= 0 : "Integer overflow.";
    assert _b + _size >= 0 : "Integer overflow.";
    assert _acc != null;

//    assert range(_a,_a+_size).noneMatch(_acc::predicate);
//    assert range(_b,_b+_size). allMatch(_acc::predicate);

    acc = _acc;
    a = _a;
    b = _b;
    size = _size;
  }

// METHODS
  public boolean get( int i ) {
    assert i >= 0;
    assert i <  size;
    return acc.predicate(a+i) == tru;
  }

  public void flip( int i ) {
    assert i >= 0;
    assert i <  size;
    acc.swap(a+i, b+i);
  }

  public void flipRange( int from, int until ) {
    assert 0 <= from;
    assert      from <= until;
    assert              until <= size;
    while( until-- > from )
      acc.swap(a+until, b+until);
  }

  public boolean isTruthInverted() {
    return tru == false;
  }

  public void flipTruth() {
    tru = !tru; // "Today's not opposite day!!!" - "So it IS opposite day!"
  }
}
