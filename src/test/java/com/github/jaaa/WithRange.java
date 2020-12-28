package com.github.jaaa;

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
}
