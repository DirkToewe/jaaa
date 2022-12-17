package com.github.jaaa.copy;

import java.util.concurrent.CountedCompleter;

import static java.util.Objects.requireNonNull;


public class ParallelCopyTask<T> extends CountedCompleter<T>
{
  private final int a0,b0,height,length;
  private final  T  a, b;
  private final CopyAccessor<? super T> acc;

  public ParallelCopyTask( int _height, CountedCompleter<?> parent, T _a, int _a0, T _b, int _b0, int _length, CopyAccessor<? super T> _acc )
  {
    super(parent);
    if( _height < 0 || _length < 0 )
      throw new IllegalArgumentException();
    acc = requireNonNull(_acc);
    a =_a; a0 =_a0; height = _height;
    b =_b; b0 =_b0; length = _length;
  }

  @Override public void compute()
  {
    int height = this.height,
        length = this.length;
    CopyAccessor<? super T> acc = this.acc;
    int a0 = this.a0, b0 = this.b0;
    T   a  = this.a,  b  = this.b;
    setPendingCount(height);
    while( 0 < height ) {                     int len = length>>>1;
      new ParallelCopyTask<>(--height, this, a,a0+len, b,b0+len, length-len, acc).fork();
      length = len;
    }
    acc.copyRange(a,a0, b,b0, length);
    tryComplete();
  }
}
