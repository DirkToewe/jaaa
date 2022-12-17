package com.github.jaaa;

import java.util.function.Function;

import static java.lang.String.format;


public final class WithIndex<T> implements With<T>
{
  public final int index;
  public final T data;
  public WithIndex( int _index, T _data )
  {
    if( _index < 0                         ) throw new IllegalArgumentException();
    if( _index >= With.contentLength(_data)) throw new IllegalArgumentException();
    With.checkData(_data);
    index = _index;
    data  = _data;
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
