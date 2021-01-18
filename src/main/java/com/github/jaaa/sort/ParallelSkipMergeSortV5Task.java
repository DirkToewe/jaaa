package com.github.jaaa.sort;

import java.util.concurrent.CountedCompleter;

import static java.util.Objects.requireNonNull;

public class ParallelSkipMergeSortV5Task<T> extends CountedCompleter<Void>
{
// STATIC FIELDS
  private static final class SubTask<T> extends CountedCompleter<Void>
  {
    private long stack = 0;
    private final int          dstPos, dstLen;
    private int    src0, dst0, srcPos, srcLen, height;
    private Object src,  dst;
    private ParallelSkipMergeSortV5.Accessor<? super T> ctx;

    public SubTask(
      CountedCompleter<?> parent, int _height,
      ParallelSkipMergeSortV5.Accessor<? super T> _ctx,
      T _src, int _src0,
      T _dst, int _dst0, long _stack, int pos, int len
    )
    {
      super(parent);
      assert 0 <= _height;
      assert 0 < len;

      ctx = _ctx;

      src =_src; src0 =_src0;
      dst =_dst; dst0 =_dst0;
      srcPos = dstPos = pos;
      srcLen = dstLen = len;
      height =_height;
      stack = _stack;
    }

    @Override
    public void compute()
    {
      var ctx = this.ctx;
      if( 0 == height )
      {
        T   src  = (T) this.src,
            dst  = (T) this.dst;
        int src0 =     this.src0,
            dst0 =     this.dst0;

        if( srcLen == dstLen ) {
          assert srcPos == dstPos;
          ctx.sort(
            src,src0+srcPos,
            dst,dst0+srcPos,
                     srcLen
          );
        }
        else
        {
          int nSkip = dstPos - srcPos,
               lenL = srcLen >>> 1,  l0 = src0 + srcPos,
               lenR = srcLen - lenL, r0 = l0 + lenL;

          int l = ctx.mergeOffset(src,l0,lenL, r0,lenR, nSkip),
              r = nSkip - l;

          ctx.mergePart(
            src, l0+l, lenL-l,
                 r0+r, lenR-r,
            dst, dst0+dstPos, dstLen
          );

          // swap buffers to change merge direction next time
          this.src = dst; this.src0 = dst0;
          this.dst = src; this.dst0 = src0;
        }

        int oddBit = (int) (stack & 1); stack >>>= 1; // <- bit set if parent merge range is odd
        int rhsBit = (int) (stack & 1); stack >>>= 1; // <- bit set if current merge range is a right-hand side child
        // updated src range to next merge
        srcLen <<= 1;
        srcLen += oddBit * (1 - 2*rhsBit);
        srcPos -= rhsBit * (srcLen >>> 1);
      }
      else
      {
        int pend = 2;
        if( 0 < height ) {
          T src = (T) this.src,
            dst = (T) this.dst;

          assert srcLen == dstLen;
          assert srcPos == dstPos;

          long stack = dstLen&1L | this.stack<<2;
          int    len = dstLen >>> 1;

          this.src = new SubTask<T>(this, height-1, ctx, src,src0, dst,dst0, stack,    dstPos,            len);
          this.dst = new SubTask<T>(this, height-1, ctx, src,src0, dst,dst0, stack|2L, dstPos+len, dstLen-len);
          // first time around, we need child nodes to finish twice, once for sorting and once for merging
          if( 1 < height ) pend = 4;
        }
        setPendingCount(pend);
        ( (SubTask<?>) dst ).fork();
        ( (SubTask<?>) src ).compute();
      }

      tryComplete();
    }

    @Override
    public void onCompletion( CountedCompleter<?> caller ) {
      if( height >  0 ) {
        height = -1;
        compute(); // <- trigger merge on this tree level TODO: fork here maybe to prevent stack overflow?
      }
    }
  }

// STATIC CONSTRUCTOR

// STATIC METHODS

// FIELDS
  private final T src, dst;
  private final int src0,
       height, len, dst0;
  private final ParallelSkipMergeSortV5.Accessor<? super T> ctx;

  // CONSTRUCTORS
  public ParallelSkipMergeSortV5Task(
    int _height, CountedCompleter<?> completer,
    T _src, int _src0,
    T _dst, int _dst0, int _len,
    ParallelSkipMergeSortV5.Accessor<? super T> _ctx
  )
  {
    if( _height%2 != 0 ) throw new IllegalArgumentException("Task tree height must be multiple of 2.");
    if( _height <  2 ) throw new IllegalArgumentException("Task tree height cannot (yet) be less than 2.");
    if( _height > 32 ) throw new IllegalArgumentException("Task tree height must not be greater than 32.");
    if( _len < 0 ) throw new IllegalArgumentException();
    assert _src0 >= 0;
    assert _dst0 >= 0;
    src =_src; src0 =_src0; height =_height;
    dst =_dst; dst0 =_dst0;    len =_len;
    ctx = requireNonNull(_ctx);
  }

// METHODS
  @Override public void compute() {
    setPendingCount(1);
    new SubTask<>(this, height, ctx, src,src0, dst,dst0, 0L, 0,len).compute();
  }
}
