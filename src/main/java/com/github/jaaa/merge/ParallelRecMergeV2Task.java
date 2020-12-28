package com.github.jaaa.merge;

import java.util.concurrent.CountedCompleter;

import static java.util.Objects.requireNonNull;

public class ParallelRecMergeV2Task<T> extends CountedCompleter<Void>
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
  private final Context<? super T> acc;
  private final  T  a, b, c;
  private final int a0,b0,c0,
                    a1,b1, height;

  // CONSTRUCTORS
  public ParallelRecMergeV2Task(
    int _height, CountedCompleter<?> parent,
    T _a, int _a0, int _a1,
    T _b, int _b0, int _b1,
    T _c, int _c0, Context<? super T> _acc
  )
  {
    super(parent);
    if( 0 > _height) throw new IllegalArgumentException();
    height =_height;
    acc = requireNonNull(_acc);
    a =_a; a0 =_a0; a1 =_a1;
    b =_b; b0 =_b0; b1 =_b1;
    c =_c; c0 =_c0;
  }

  // METHODS
  @Override public void compute()
  {
    final var acc = this.acc;
    final T a = this.a,
            b = this.b,
            c = this.c;
    final int a0 = this.a0,
              b0 = this.b0,
              c0 = this.c0;
    int a1 = this.a1,
        b1 = this.b1;
    int height=this.height;
    setPendingCount(height);

    while( 0 < height )
    {
      int aLen = a1-a0,
          bLen = b1-b0,
            cm = aLen+bLen >>> 1,
            am = acc.mergeOffset(a,a0,aLen, b,b0,bLen, cm),
            bm = cm - am;
      am += a0;
      bm += b0;
      cm += c0;
      new ParallelRecMergeV2Task<>(
        --height, this,
        a,am,a1,
        b,bm,b1,
        c,cm,acc
      ).fork();
      a1 = am;
      b1 = bm;
    }

    int aLen = a1 - a0,
        bLen = b1 - b0,
        cLen = aLen + bLen;
    acc.mergePart(
      a,a0,aLen,
      b,b0,bLen,
      c,c0,cLen
    );

    tryComplete();
  }
}
