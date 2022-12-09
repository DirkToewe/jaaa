package com.github.jaaa.copy;

import com.github.jaaa.permute.Swap;

import java.nio.CharBuffer;

public interface RandomAccessorBufChar extends RandomAccessor<CharBuffer>
{
  @Override default CharBuffer malloc( int len ) { return CharBuffer.allocate(len); }
  @Override default void swap     ( CharBuffer a, int i,  CharBuffer b, int j ) { Swap.swap(a,i, b,j); }
  @Override default void copy     ( CharBuffer a, int i,  CharBuffer b, int j ) { b.put( j, a.get(i) ); }
  @Override default void copyRange( CharBuffer a, int i,  CharBuffer b, int j, int len ) {
    a = a.duplicate(); a.position(i); a.limit(i+len);
    b = b.duplicate(); b.position(j); b.limit(j+len);
    b.put(a);
  }
}
