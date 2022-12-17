package com.github.jaaa.merge.datagen;

import com.github.jaaa.search.ExpR2LSearch;
import static java.lang.Math.addExact;

import java.util.AbstractList;


public class MergeInputsUpToLength extends AbstractList<MergeInput<byte[]>>
{
// STATIC FIELDS
  private static final int[] LOOKUP = new int[MergeInputsOfLength.maxLength() + 1];

// STATIC CONSTRUCTOR
  static {
    LOOKUP[0] = 1;
    for( int i=0; ++i < LOOKUP.length; )
      LOOKUP[i] = addExact( LOOKUP[i-1], MergeInputsOfLength.nSamples(i) );
  }

// STATIC METHODS

// FIELDS
  private final int maxLen, hashCode;

// CONSTRUCTORS
  public MergeInputsUpToLength( int _maxLen ) {
    if( _maxLen < 0 || _maxLen >= LOOKUP.length )
      throw new IllegalArgumentException();
    maxLen = _maxLen;
    hashCode = super.hashCode();
  }

// METHODS
  @Override public MergeInput<byte[]> get( int index ) {
    if( index < 0 || index > size() )
      throw new IndexOutOfBoundsException();
    int len = ExpR2LSearch.searchGapR(LOOKUP,0,maxLen, index);
    if( len == 0 )
      return MergeInput.EMPTY_BYTE;
    return MergeInputsOfLength.sample(len, index - LOOKUP[len-1]);
  }
  @Override public int size() {
    return LOOKUP[maxLen];
  }
  @Override public int hashCode() { return hashCode; }
  @Override public boolean equals( Object that ) {
    if( that == this ) return true;
    if( that instanceof MergeInputsUpToLength && that.hashCode() != hashCode )
      return false;
    return super.equals(that);
  }
}
