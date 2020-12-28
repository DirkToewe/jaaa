package com.github.jaaa;


/** Instances of this class allow access to array-like objects of generic type
 *
 *  @param <T>
 */
public interface RandomAccessor<T>
{
  public int len( T buf ); // <- TODO: this should be removed, perhaps moved to MallocAccessor
  public void copy( T a, int i, T b, int j );
  public void swap( T a, int i, T b, int j );

  public default void copyRange( T a, int i, T b, int j, int len ) {
    if( len < 0 )
      throw new IllegalArgumentException();
    if( i < j )    while( len-- > 0 ) copy(a,i+len, b,j+len);
    else for( int k=0; k < len; k++ ) copy(a,i+k,   b,j+k  );
  }
}
