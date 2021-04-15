package com.github.jaaa;

import static java.lang.String.format;

public class WithIndex<T> extends With<T>
{
  private final int index;

  public WithIndex( int _index, T _data )
  {
    super(_data);
    index =_index;
    if( index < 0               ) throw new IllegalArgumentException();
    if( index >= contentLength()) throw new IllegalArgumentException();
  }

  public int getIndex() { return index; }

  @Override public String toString() {
    return format( "WithIndex{ index: %d, data: %s }", index, dataString() );
  }

  @Override public WithIndex<T> clone() {
    return new WithIndex<>(index, getData());
  }
}
