package com.github.jaaa;

import java.util.Objects;
import java.util.function.Function;

import static java.lang.String.format;

public final class WithInsertIndex<T> implements With<T>
{
  public final int index;
  public final T data;
  public WithInsertIndex( int _index, T _data ) {
    if( _index < 0                       ) throw new IllegalArgumentException();
    if( _index > With.contentLength(_data)) throw new IllegalArgumentException();
    With.checkData(_data);
    index = _index;
    data  = _data;
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

  @Override public <U> WithInsertIndex<U> map( Function<? super T,? extends U> mapFn ) {
    return new WithInsertIndex<>( index, mapFn.apply(getData()) );
  }

  @Override public boolean equals( Object obj ) {
    if(this == obj) return true;
    if( !(obj instanceof WithInsertIndex) ) return false;
    WithInsertIndex<?> that = (WithInsertIndex<?>) obj;
    return index == that.index && Objects.equals(data, that.data);
  }

  @Override public int hashCode() {
    return Objects.hash(index, data);
  }
}