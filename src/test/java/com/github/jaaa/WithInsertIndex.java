package com.github.jaaa;

import static java.lang.String.format;

public class WithInsertIndex<T> extends With<T>
{
  private final int index;

  public WithInsertIndex( int _index, T _data )
  {
    super(_data);
        index =_index;
    if( index < 0              ) throw new IllegalArgumentException();
    if( index > contentLength()) throw new IllegalArgumentException();
  }

  public int getIndex() { return index; }

  @Override public String toString() {
    return format( "WithInsertIndex{ index: %d, data: %s }", index, dataString() );
  }
}