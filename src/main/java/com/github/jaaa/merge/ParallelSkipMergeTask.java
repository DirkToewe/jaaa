package com.github.jaaa.merge;

import java.util.concurrent.CountedCompleter;

import static java.util.Objects.requireNonNull;


// OVERVIEW
// --------
// The ParallelSkipMerge algorithm splits the computation of the
// merged sequence int roughly equally sized, parallel computation
// tasks. Each task uses `mergeOffset` to skip the required number
// of elements from the merged sequences. Starting at the skipped
// heads, `mergePart` is then used to compute a chunk of the merged
// output. Each of the tasks can be computed completely independently
// which makes this algorithm well-suited for high parallelism, even
// on GPUs.
//
// The name SkipMerge is derived from the fact that each parallel
// merge task effectively skips a certain number of elements of
// the merged sequence.
//
// [1] gives another perspective on this algorithm which describes
// the merging process as as the traversal of a 2D grid.
//
// PERFORMANCE
// -----------
// Requires O(log(m+n)) cycles, given O((m+n)/log(m+n)) threads.
//   m: Length of the left sequence to be merged.
//   n: Length of the right sequence to be merged.
//
// REFERENCES
// ----------
// .. [1] "GPU Merge Path - A GPU Merging Algorithm"
//         Oded Green, Robert McColl & David A. Bader
//

public class ParallelSkipMergeTask<T> extends CountedCompleter<Void>
{
// STATIC FIELDS

// STATIC CONSTRUCTOR

// STATIC METHODS

// FIELDS
  private final ParallelSkipMerge.Accessor<? super T> acc;
  private final T   a,   b,   c;
  private final int a0,  b0,  c0,
                    aLen,bLen, height, nSkip, nTake;

// CONSTRUCTORS
  public ParallelSkipMergeTask(
    int _height, CountedCompleter<?> parent,
    T _a, int _a0, int _aLen,
    T _b, int _b0, int _bLen,
    T _c, int _c0, int _nSkip, int _nTake,
    ParallelSkipMerge.Accessor<? super T> _acc
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
    acc = requireNonNull(_acc);
    a =_a; a0 =_a0; aLen =_aLen;
    b =_b; b0 =_b0; bLen =_bLen;
    c =_c; c0 =_c0;
    nTake =_nTake;
    nSkip =_nSkip;
  }

// METHODS
  @Override public void compute()
  {
    final var ctx = this.acc;
    final T a = this.a,
            b = this.b,
            c = this.c;
    final int a0 = this.a0,  aLen = this.aLen,
              b0 = this.b0,  bLen = this.bLen,
              c0 = this.c0, nSkip = this.nSkip;
    int height=this.height, nTake = this.nTake;

    setPendingCount(height);

    // BRANCH OUT
    // ----------
    while( 0 < height )
    {
      // split off right half into a new task
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

    // COMPUTE LEAF
    // ------------

    // skip over desired number of elements of the merged sequences
    int l = ctx.skipMerge_mergeOffset(a,a0,aLen, b,b0,bLen, nSkip),
        r = nSkip-l;

    // merge part of the skipped, merged sequences
    ctx.skipMerge_mergePart(
      a, a0+l, aLen-l,
      b, b0+r, bLen-r,
      c, c0+nSkip, nTake
    );

    tryComplete();
  }
}
