package com.github.jaaa;

import java.util.function.Function;

import static java.lang.String.format;


public record WithIndex<T>( int index, T data ) implements With<T>
{
  public WithIndex
  {
    if( index < 0                        ) throw new IllegalArgumentException();
    if( index >= With.contentLength(data)) throw new IllegalArgumentException();
    With.checkData(data);
  }

  @Override
  public  T  getData () { return data; }
  public int getIndex() { return index; }

  @Override public String toString() {
    return format( "WithIndex{ index: %d, data: %s }", index, With.toString(data) );
  }

  @Override public WithIndex<T> clone() {
    return new WithIndex<>( index, With.clone(data) );
  }

  @SuppressWarnings({"rawtypes", "unchecked"})
  @Override
  public <R> WithIndex<R> map(Function<? super T, ? extends R> mapper) {
    return new WithIndex(index, mapper.apply(data));
  }
}
