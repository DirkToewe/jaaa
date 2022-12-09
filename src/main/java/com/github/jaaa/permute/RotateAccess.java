package com.github.jaaa.permute;

public interface RotateAccess extends RevertAccess
{
  default void rotate( int from, int until, int rot )
  {
    if( from < 0 || from > until )
      throw new IllegalArgumentException();

    int len = until - from;
    if( len <= 1 || 0 == (rot %= len) ) return;
        rot += len & -(rot>>>31);

    revert(from,         until);
    revert(from,from+rot      );
    revert(     from+rot,until);
  }
}
