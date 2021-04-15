package com.github.jaaa;

import java.nio.LongBuffer;

public interface RandomAccessorBufLong extends RandomAccessor<LongBuffer>
{
  @Override default LongBuffer malloc( int len ) { return LongBuffer.allocate(len); }
  @Override default void swap     ( LongBuffer a, int i,  LongBuffer b, int j ) { Swap.swap(a,i, b,j); }
  @Override default void copy     ( LongBuffer a, int i,  LongBuffer b, int j ) { b.put( j, a.get(i) ); }
  @Override default void copyRange( LongBuffer a, int i,  LongBuffer b, int j, int len ) {
    a = a.duplicate(); a.position(i); a.limit(i+len);
    b = b.duplicate(); b.position(j); b.limit(j+len);
    b.put(a);
  }
}
