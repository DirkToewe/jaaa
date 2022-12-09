package com.github.jaaa.partition;

import com.github.jaaa.fn.PredicateSwapAccess;


final class InPlacePartitionBufferInt
{
// STATIC FIELDS

// STATIC CONSTRUCTOR

// STATIC METHODS

// FIELDS
  private final PredicateSwapAccess acc;
  private final int a,b, nBits, nInts;

// CONSTRUCTORS
  public InPlacePartitionBufferInt(PredicateSwapAccess _acc, int _a, int _b, int _nBits, int _nInts )
  {
    assert _a >= 0;
    assert _b >= 0;
    assert _nBits >= 0;
    assert _nBits < 32;
    assert _nInts >= 0;
    assert _nBits * _nBits      >= 0 : "Integer overflow.";
    assert _nBits * _nBits + _a >= 0 : "Integer overflow.";
    assert _nBits * _nBits + _b >= 0 : "Integer overflow.";
    assert _acc != null;

//    assert range(_a, _a + _nBits*_nInts).noneMatch(_acc::predicate);
//    assert range(_b, _b + _nBits*_nInts). allMatch(_acc::predicate);

    a = _a; nBits = _nBits;
    b = _b; nInts = _nInts; acc = _acc;
  }

// METHODS
  public int get( int i ) {
    assert i >= 0;
    assert i <  nInts;
           i *= nBits;
    int result = 0;
    for( int j=nBits; j-- > 0; )
      result = result<<1 | (acc.predicate(a+i+j) ? 1 : 0);
    return result;
  }

  public void set( int i, int val ) {
    assert i >= 0;
    assert i <  nInts;
    assert val >= 0;
    assert val < (1L<<nBits);

    i *= nBits;
    for( int j=nBits; j-- > 0; )
      if( (val>>>j & 1) == 1 != acc.predicate(a+i+j) )
        acc.swap(a+i+j, b+i+j);
  }

  public void reset() {
    for( int i=nBits*nInts; i-- > 0; )
      if( acc.predicate(a+i) )
        acc.swap(a+i, b+i);
  }
}
