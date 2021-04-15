package com.github.jaaa;

import java.util.function.Function;

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

  @Override public WithInsertIndex<T> clone() {
    return new WithInsertIndex<>(index, getData());
  }

  public <U> WithInsertIndex<U> map( Function<? super T,? extends U> mapFn ) {
    return new WithInsertIndex<>( index, mapFn.apply(getData()) );
  }
}