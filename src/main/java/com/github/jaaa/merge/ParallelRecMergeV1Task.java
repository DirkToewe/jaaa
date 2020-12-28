package com.github.jaaa.merge;

import java.util.concurrent.CountedCompleter;

import static java.util.Objects.requireNonNull;

public class ParallelRecMergeV1Task<T> extends CountedCompleter<Void>
{
  // STATIC FIELDS
  interface Context<T>
  {
    void      copy( T a, int i, T b, int j );
    int searchGapL( T a, int a0, int aLen, T b, int key );
    int searchGapR( T a, int a0, int aLen, T b, int key );
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
  private final  T  a, b, c;
  private final int a0,b0,c0,
                    a1,b1, granularity;

  // CONSTRUCTORS
  public ParallelRecMergeV1Task(
    int _granularity, CountedCompleter<?> parent,
    T _a, int _a0, int _a1,
    T _b, int _b0, int _b1,
    T _c, int _c0, Context<? super T> _ctx
  )
  {
    super(parent);
         if( 1 > _granularity ) throw new IllegalArgumentException();
    granularity =_granularity;
    ctx = requireNonNull(_ctx);
    a =_a; a0 =_a0; a1 =_a1;
    b =_b; b0 =_b0; b1 =_b1;
    c =_c; c0 =_c0;
  }

  // METHODS
  @Override public void compute()
  {
    final var ctx = this.ctx;
    final T a = this.a,
            b = this.b,
            c = this.c;
    final int a0 = this.a0,
              b0 = this.b0,
              c0 = this.c0,
     granularity = this.granularity;
    int a1 = this.a1,
        b1 = this.b1;

    int aLen,
        bLen,
        cLen;
    for(;;)
    {
      aLen = a1-a0;
      bLen = b1-b0;
      cLen = aLen + bLen;
      if( cLen <= granularity ) break;

      int am,bm,cm;
      if( aLen > bLen )
      {
        am = a0 + (cm = aLen>>>1);
        bm  = ctx.searchGapL(b,b0,b1, a,am);
        cm += c0 + bm-b0;
        ctx.copy(a,am++, c,cm++);
      }
      else
      {
        bm  = b0 + (cm = bLen>>>1);
        am  = ctx.searchGapR(a,a0,a1, b,bm);
        cm += c0 + am-a0;
        ctx.copy(b,bm++, c,cm++);
      }

      addToPendingCount(1);
      new ParallelRecMergeV1Task<>(
        granularity, this,
        a,am,a1,
        b,bm,b1,
        c,cm, ctx
      ).fork();

      a1 = am;
      b1 = bm;
    }

    ctx.mergePart(
      a,a0,aLen,
      b,b0,bLen,
      c,c0,cLen
    );

    tryComplete();
  }
}
