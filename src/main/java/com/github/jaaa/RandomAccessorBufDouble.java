package com.github.jaaa;

import java.nio.DoubleBuffer;

public interface RandomAccessorBufDouble extends RandomAccessor<DoubleBuffer>
{
  @Override default DoubleBuffer malloc( int len ) { return DoubleBuffer.allocate(len); }
  @Override default void swap     ( DoubleBuffer a, int i,  DoubleBuffer b, int j ) { Swap.swap(a,i, b,j); }
  @Override default void copy     ( DoubleBuffer a, int i,  DoubleBuffer b, int j ) { b.put( j, a.get(i) ); }
  @Override default void copyRange( DoubleBuffer a, int i,  DoubleBuffer b, int j, int len ) {
    a = a.duplicate(); a.position(i); a.limit(i+len);
    b = b.duplicate(); b.position(j); b.limit(j+len);
    b.put(a);
  }
}
