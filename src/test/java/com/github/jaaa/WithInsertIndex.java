package com.github.jaaa;

import java.util.function.Function;

import static java.lang.String.format;

public record WithInsertIndex<T>( int index, T data ) implements With<T>
{
  public WithInsertIndex {
    if( index < 0                       ) throw new IllegalArgumentException();
    if( index > With.contentLength(data)) throw new IllegalArgumentException();
    With.checkData(data);
  }

  @Override
  public  T  getData () { return data; }
  public int getIndex() { return index; }

  @Override public String toString() {
    return format( "WithInsertIndex{ index: %d, data: %s }", index, With.toString(data) );
  }

  @Override public WithInsertIndex<T> clone() {
    return new WithInsertIndex<>(index, With.clone(data));
  }

  @Override
  public <U> WithInsertIndex<U> map( Function<? super T,? extends U> mapFn ) {
    return new WithInsertIndex<>( index, mapFn.apply(getData()) );
  }
}