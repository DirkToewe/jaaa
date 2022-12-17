package com.github.jaaa;

import java.util.Objects;
import java.util.function.Function;

import static java.lang.String.format;

public final class WithRange<T> implements With<T>
{
  public final int from, until;
  public final T data;
  public WithRange( int _from, int _until, T _data ) {
    if( _from < 0                        ) throw new IllegalArgumentException();
    if( _from > _until                   ) throw new IllegalArgumentException();
    if(_until > With.contentLength(_data)) throw new IllegalArgumentException();
    With.checkData(_data);
    from  = _from;
    until = _until;
    data  = _data;
  }

  @Override
  public  T  getData () { return data; }
  public int getFrom () { return from; }
  public int getUntil() { return until; }

  public int rangeLength() { return until-from; }

  @Override public String toString() {
    return format( "WithRange{ from: %d, until: %d, data: %s }", from, until, With.toString(data) );
  }

  @Override public WithRange<T> clone() {
    return new WithRange<>(from,until, With.clone(data));
  }

  @Override public <U> WithRange<U> map( Function<? super T,? extends U> mapFn ) {
    return new WithRange<>( from, until, mapFn.apply(getData()) );
  }

  @Override public boolean equals( Object obj ) {
    if( this == obj ) return true;
    if( !(obj instanceof WithRange) ) return false;
    WithRange<?> withRange = (WithRange<?>) obj;
    return  from == withRange.from
        && until == withRange.until
        && Objects.equals(data, withRange.data);
  }

  @Override public int hashCode() {
    return Objects.hash(from, until, data);
  }
}
