package com.github.jaaa.copy;

import com.github.jaaa.permute.Swap;

import java.nio.ByteBuffer;

public interface RandomAccessorBufByte extends RandomAccessor<ByteBuffer>
{
  @Override default ByteBuffer malloc( int len ) { return ByteBuffer.allocate(len); }
  @Override default void swap     ( ByteBuffer a, int i,  ByteBuffer b, int j ) { Swap.swap(a,i, b,j); }
  @Override default void copy     ( ByteBuffer a, int i,  ByteBuffer b, int j ) { b.put( j, a.get(i) ); }
  @Override default void copyRange( ByteBuffer a, int i,  ByteBuffer b, int j, int len ) {
    a = a.duplicate(); a.position(i); a.limit(i+len);
    b = b.duplicate(); b.position(j); b.limit(j+len);
    b.put(a);
  }
}
