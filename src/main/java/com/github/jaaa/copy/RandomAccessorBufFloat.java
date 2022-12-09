package com.github.jaaa.copy;

import com.github.jaaa.permute.Swap;

import java.nio.FloatBuffer;

public interface RandomAccessorBufFloat extends RandomAccessor<FloatBuffer>
{
  @Override default FloatBuffer malloc( int len ) { return FloatBuffer.allocate(len); }
  @Override default void swap     ( FloatBuffer a, int i,  FloatBuffer b, int j ) { Swap.swap(a,i, b,j); }
  @Override default void copy     ( FloatBuffer a, int i,  FloatBuffer b, int j ) { b.put( j, a.get(i) ); }
  @Override default void copyRange( FloatBuffer a, int i,  FloatBuffer b, int j, int len ) {
    a = a.duplicate(); a.position(i); a.limit(i+len);
    b = b.duplicate(); b.position(j); b.limit(j+len);
    b.put(a);
  }
}
