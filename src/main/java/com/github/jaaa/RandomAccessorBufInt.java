package com.github.jaaa;

import java.nio.IntBuffer;

public interface RandomAccessorBufInt extends RandomAccessor<IntBuffer>
{
  @Override default IntBuffer malloc( int len ) { return IntBuffer.allocate(len); }
  @Override default void swap     ( IntBuffer a, int i,  IntBuffer b, int j ) { Swap.swap(a,i, b,j); }
  @Override default void copy     ( IntBuffer a, int i,  IntBuffer b, int j ) { b.put( j, a.get(i) ); }
  @Override default void copyRange( IntBuffer a, int i,  IntBuffer b, int j, int len ) {
    a = a.duplicate(); a.position(i); a.limit(i+len);
    b = b.duplicate(); b.position(j); b.limit(j+len);
    b.put(a);
  }
}
