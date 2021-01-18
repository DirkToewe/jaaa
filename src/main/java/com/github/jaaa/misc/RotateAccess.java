package com.github.jaaa.misc;

import static java.lang.Math.subtractExact;

public interface RotateAccess extends RevertAccess
{
  public default void rotate( int from, int until, int off )
  {
    if( from >  until )
      throw new IllegalArgumentException();
    if( from == until ) return;
    int        len = subtractExact(until,from);
        off %= len;
    if( off == 0 ) return;
    if( off <  0 )
        off += len;
    revert(from,           until);
    revert(from, from+off       );
    revert(      from+off, until);
  }
}
