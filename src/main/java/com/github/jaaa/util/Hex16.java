package com.github.jaaa.util;

import static java.util.stream.Collectors.joining;
import static java.util.stream.IntStream.range;

public final class Hex16
{
  private long bits;
  private byte size;

  public int size() {
    return size;
  }

  public boolean isFull() { return size==16; }

  public void clear() { bits=size=0; }

  public void rotate( int rot ) // <- FIXME should be rotating the other way around
  {
    if( 0 == size ) return;
        rot %=   size;
    if( rot < 0 )
        rot +=   size;
        rot *= 4;
    int len  = 4*size;
    bits =                  bits  >>>    rot
         | ((1L << rot)-1 & bits) << len-rot;
  }

  public int get( int i )
  {
    assert i >=   0;
    assert i < size;
    int    result  = 15;
           result &= bits >>> i*4;
    return result;
  }

  public void set( int i, int hex )
  {
    assert hex >=  0;
    assert hex <= 16;
    assert   i >=  0;
    assert   i <= 16;
             i *=  4;
    bits = bits &  ~(15L << i)
                | hex*1L << i;
  }

  public void swap( int i, int j )
  {
    assert i >= 0;
    assert j >= 0;
    assert i < size;
    assert j < size;

    if( i == j ) return;
    if( i >  j ) {
      int k=i;
            i=j;
              j=k;
    }
    i <<= 2;
    j <<= 2;
    long mi = 15L << i,
         mj = 15L << j;
    int d = j-i;

    bits = bits & ~(mi|mj)
       | ((bits & mi) << d)
       | ((bits & mj) >>>d);
  }

  public void insert( int pos, int hex )
  {
    assert  pos >= 0;
    assert  pos <= size;
    assert         size < 16;
    assert  hex >= 0;
    assert  hex < 16;
    ++size;              pos *= 4;
    long     mask = (1L<<pos) - 1;
    bits =   mask & bits
         | (~mask & bits) << 4
         | hex*1L << pos;
  }

  public void append( int hex )
  {
    assert size <  16;
    assert  hex >=  0;
    assert  hex <= 16;
    bits |= hex*1L << 4*size++;
  }

  public String toString()
  {
    return range(0,size)
      .mapToObj( i -> String.valueOf(15 & bits >>> 4*i) )
      .collect( joining(", ","[","]") );
  }

  /** Sorts this Hex16 in ascending order and then clears it. The swap operations
   *  that are applied during sorting process are also applied to the given swap
   *  method.
   *
   *  @param swap The swap method that is called for every swap operation that occurs
   *              during sorting.
   */
  public void sortAndClear( IntBiConsumer swap )
  {
    for( int i=0; i < size; i++ )
    for(
      int  j = get(i);
      i != j;
    )
    { int k = get(j);
              set(j,j);
      swap.accept(i,j);
      j = k;
    }
    bits = size = 0;
  }

  public void unsortAndClear( IntBiConsumer swap )
  {
    for( int i=0; i < size-1; i++ )
    for( int j=i;; )
    {
      int k = get(j);
              set(j,j); if( k==i ) break;
      swap.accept(j,k);
      j=k;
    }
    bits = size = 0;
  }
}
