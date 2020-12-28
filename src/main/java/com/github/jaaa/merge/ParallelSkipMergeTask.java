package com.github.jaaa.merge;

import java.util.concurrent.CountedCompleter;

import static java.util.Objects.requireNonNull;

public class ParallelSkipMergeTask<T> extends CountedCompleter<Void>
{
// STATIC FIELDS
  interface Context<T>
  {
    int mergeOffset(
      T a, int a0, int aLen,
      T b, int b0, int bLen, int nSkip
    );
    void mergePart(
      T a, int a0, int aLen,
      T b, int b0, int bLen,
      T c, int c0, int cLen
    );
  }

// STATIC CONSTRUCTOR

// STATIC METHODS

// FIELDS
  private final Context<? super T> ctx;
  private final T   a,   b,   c;
  private final int a0,  b0,  c0,
                    aLen,bLen, height, nSkip, nTake;

// CONSTRUCTORS
  public ParallelSkipMergeTask(
    int _height, CountedCompleter<?> parent,
    T _a, int _a0, int _aLen,
    T _b, int _b0, int _bLen,
    T _c, int _c0, int _nSkip, int _nTake, Context<? super T> _ctx
  )
  {
    super(parent);
    if( 0 > _a0    ) throw new IllegalArgumentException();
    if( 0 > _b0    ) throw new IllegalArgumentException();
    if( 0 > _c0    ) throw new IllegalArgumentException();
    if( 0 > _aLen  ) throw new IllegalArgumentException();
    if( 0 > _bLen  ) throw new IllegalArgumentException();
    if( 0 > _nSkip ) throw new IllegalArgumentException();
    if( 0 > _nTake ) throw new IllegalArgumentException();
    if( 0 > _height) throw new IllegalArgumentException();
    height =_height;
    ctx = requireNonNull(_ctx);
    a =_a; a0 =_a0; aLen =_aLen;
    b =_b; b0 =_b0; bLen =_bLen;
    c =_c; c0 =_c0;
    nTake =_nTake;
    nSkip =_nSkip;
  }

// METHODS
  @Override public void compute()
  {
    final var ctx = this.ctx;
    final T a = this.a,
            b = this.b,
            c = this.c;
    final int a0 = this.a0,  aLen = this.aLen,
              b0 = this.b0,  bLen = this.bLen,
              c0 = this.c0, nSkip = this.nSkip;
    int height=this.height, nTake = this.nTake;

    setPendingCount(height);

    while( 0 < height )
    {
      int n = nTake>>>1;
      new ParallelSkipMergeTask<>(
        --height, this,
        a,a0,aLen,
        b,b0,bLen,
        c,c0,
        nSkip+n,
        nTake-n, ctx
      ).fork();
      nTake = n;
    }

    int l = ctx.mergeOffset(a,a0,aLen, b,b0,bLen, nSkip),
        r = nSkip-l;

    ctx.mergePart(
      a, a0+l, aLen-l,
      b, b0+r, bLen-r,
      c, c0+nSkip, nTake
    );

    tryComplete();
  }
}
