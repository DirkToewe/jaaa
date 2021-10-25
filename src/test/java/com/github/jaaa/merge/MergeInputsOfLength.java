package com.github.jaaa.merge;

import java.util.AbstractList;

import static com.github.jaaa.misc.Revert.revert;
import static java.lang.Math.addExact;
import static java.lang.Math.multiplyExact;


class MergeInputsOfLength extends AbstractList<MergeInput<byte[]>>
{
// STATIC FIELDS
  private static final int[] LOOKUP = new int[17*2];

// STATIC CONSTRUCTOR
  static {
    assert LOOKUP.length % 2 == 0;
    for( int nA=1, nB=1, i=LOOKUP.length; (i-=2) >= 0; )
    {
      LOOKUP[i  ] = nA;
      LOOKUP[i+1] = nB;
      int a2 = multiplyExact(2,nA),
          b2 = multiplyExact(2,nB);
      nB = addExact(nA, b2);
      nA = addExact(a2, b2);
    }
  }

// STATIC METHODS
  public static int maxLength() { return LOOKUP.length >>> 1; }
  public static int nSamples( int len )
  {
    if( len==0 )
      return 1;
    int i = LOOKUP.length - 2*len;
    return LOOKUP[i] + LOOKUP[i+1];
  }
  public static MergeInput<byte[]> sample( int len, int index )
  {
    if( index < 0 )
      throw new IndexOutOfBoundsException();
    if( 0 == len )
      return MergeInput.EMPTY_BYTE;

    int i = LOOKUP.length - 2*len;

    boolean wasB;
    if( index < LOOKUP[i] )
      wasB = false;
    else {
      wasB = true;
          index -= LOOKUP[i];
      if( index >= LOOKUP[i+1] )
        throw new IndexOutOfBoundsException();
    }

    int l=0,
        r=len;
    var result = new byte[len];
    if(wasB) --r;
    else     ++l;

    for( byte val=0; (i+=2) < LOOKUP.length; )
    {
      int nA = LOOKUP[i],
          nB = LOOKUP[i+1];

      block: {
        if( !wasB ) {
          if(  index <  nA ) break block;
          else index -= nA;
        }
        if( index < nA ) { wasB = false; ++val; }
        else {
          wasB = true;
              index -= nA;
          if( index >= nB ) {
              index -= nB;
            ++val;
          }
        }
      }
      result[wasB ? --r : l++] = val;
    }

    revert(result, l,len);
    return new MergeInput<>(result, 0,l,len);
  }

// FIELDS
  private final int len;

// CONSTRUCTORS
  public MergeInputsOfLength(int _len )
  {
    if(          0 > _len ) throw new IllegalArgumentException();
    if(maxLength() < _len ) throw new ArithmeticException();
    len =_len;
  }

// METHODS
  @Override public int size() { return nSamples(len); }
  @Override public MergeInput<byte[]> get( int index ) { return sample(len,index); }
}
