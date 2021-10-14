package com.github.jaaa;

import java.util.function.Function;

import static java.lang.String.format;

public record WithRange<T>( int from, int until, T data ) implements With<T>
{
  public WithRange {
    if( from < 0                       ) throw new IllegalArgumentException();
    if( from > until                   ) throw new IllegalArgumentException();
    if(until > With.contentLength(data)) throw new IllegalArgumentException();
    With.checkData(data);
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

  @Override
  public <U> WithRange<U> map( Function<? super T,? extends U> mapFn ) {
    return new WithRange<>( from, until, mapFn.apply(getData()) );
  }
}
