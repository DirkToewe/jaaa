package com.github.jaaa;

import java.util.function.Function;

import static java.lang.String.format;

public class WithRange<T> extends With<T>
{
  private final int from, until;

  public WithRange( int _from, int _until, T _data )
  {
    super(_data);
    from  =_from;
    until =_until;
    if( from < 0              ) throw new IllegalArgumentException();
    if( from > until          ) throw new IllegalArgumentException();
    if(until > contentLength()) throw new IllegalArgumentException();
  }

  public int getFrom () { return from; }
  public int getUntil() { return until; }

  public int rangeLength() { return until-from; }

  @Override public String toString() {
    return format( "WithRange{ from: %d, until: %d, data: %s }", from, until, dataString() );
  }

  @Override public WithRange<T> clone() {
    return new WithRange<>(from,until, getData());
  }

  public <U> WithRange<U> map( Function<? super T,? extends U> mapFn ) {
    return new WithRange<>( from, until, mapFn.apply(getData()) );
  }
}
