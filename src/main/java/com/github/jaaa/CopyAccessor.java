package com.github.jaaa;

public interface CopyAccessor<T>
{
          void copy     ( T a, int i, T b, int j );
  default void copyRange( T a, int i, T b, int j, int len ) {
    if( len < 0 )
      throw new IllegalArgumentException();
    if( a == b ) {
      if( i == j ) return;
      if( i <  j ) {
        while( 0 < len-- ) copy(a,i+len, b,j+len);
        return;
      }
    }
    for( int k=0; k < len; k++) copy(a,i+k, b,j+k);
  }
}
