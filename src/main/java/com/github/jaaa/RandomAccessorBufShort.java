package com.github.jaaa;

import java.nio.ShortBuffer;

public interface RandomAccessorBufShort extends RandomAccessor<ShortBuffer>
{
  @Override default ShortBuffer malloc( int len ) { return ShortBuffer.allocate(len); }
  @Override default void      swap( ShortBuffer a, int i,  ShortBuffer b, int j ) { Swap.swap(a,i, b,j); }
  @Override default void copy     ( ShortBuffer a, int i,  ShortBuffer b, int j ) { b.put( j, a.get(i) ); }
  @Override default void copyRange( ShortBuffer a, int i,  ShortBuffer b, int j, int len ) {
    a = a.duplicate(); a.position(i); a.limit(i+len);
    b = b.duplicate(); b.position(j); b.limit(j+len);
    b.put(a);
  }
}
