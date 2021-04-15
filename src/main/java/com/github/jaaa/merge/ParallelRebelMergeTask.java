package com.github.jaaa.merge;

import java.util.concurrent.CountedCompleter;

import static java.util.Objects.requireNonNull;

//  Implementation Details
//  ----------------------
//  Parallel(Rec|Rebel|Zen)Merge is a family of parallel merge algorithms
//  based on the RecMerge family of algorithms. All algorithms use a
//  fork-join parallelism. During each fork, the merging problem is
//  split into two, more or less, equally large sub-problems.
//
//  ParallelRebelMerge, specifically, is a parallel version of RebelMerge.
//  On each fork, the longer of the subsequences is split in half. The mid
//  value of the split sequence is then searched in the shorter sequence,
//  using either `searchGapL` or `searchGapR`. Thus the fina position of
//  the mid value is now known and the shorter sequence is split more or
//  less in half as well. With both merged sequences split, the two forked
//  subtask can continue with a smaller merging sub-problem.
//
//  PERFORMANCE
//  -----------
//       COPIES: O( log²(m+n) ) per thread, given O((m+n)/log²(m+n)) threads
//  COMPARISONS: O( log²(m+n) ) per thread, given O((m+n)/log²(m+n)) threads
//
//  The merging algorithm is adaptive, i.e. the number of comparisons decreases
//  if the two merged sequences are less interleaving.
//
//    m: Length of the left sequence to be merged.
//    n: Length of the right sequence to be merged.
//
//  REFERENCES
//  ----------
//  .. [1] OpenJDK 15, java.util.ArraysParallelSortHelpers

public class ParallelRebelMergeTask<T> extends CountedCompleter<Void>
{
// STATIC FIELDS

// STATIC CONSTRUCTOR

// STATIC METHODS

  // FIELDS
  private final ParallelRebelMerge.Accessor<? super T> acc;
  private final  T  a, b, c;
  private final int a0,b0,c0,
                    a1,b1, granularity;

  // CONSTRUCTORS
  public ParallelRebelMergeTask(
    int _granularity, CountedCompleter<?> completer,
    T _a, int _a0, int _a1,
    T _b, int _b0, int _b1,
    T _c, int _c0, ParallelRebelMerge.Accessor<? super T> _acc
  )
  {
    super(completer);
         if( 1 > _granularity ) throw new IllegalArgumentException();
    granularity =_granularity;
    acc = requireNonNull(_acc);
    a =_a; a0 =_a0; a1 =_a1;
    b =_b; b0 =_b0; b1 =_b1;
    c =_c; c0 =_c0;
  }

  // METHODS
  @Override public void compute()
  {
    final var ctx = this.acc;
    final T a = this.a,
            b = this.b,
            c = this.c;
    final int c0 = this.c0, granularity = this.granularity,
              a0 = this.a0,
              b0 = this.b0;
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

      int am,bm,cm, a2,b2;
      if( aLen > bLen )
      {
        am = a0 + (cm = aLen>>>1);
        bm  = ctx.rebelMerge_searchGap(b,b0,b1, a,am, false);
        cm += c0 + bm-b0;
        ctx.copy(a,am, c,cm);
        a2 = am++;
        b2 = bm;
      }
      else
      {
        bm  = b0 + (cm = bLen>>>1);
        am  = ctx.rebelMerge_searchGap(a,a0,a1, b,bm, true);
        cm += c0 + am-a0;
        ctx.copy(b,bm, c,cm);
        a2 = am;
        b2 = bm++;
      }

      addToPendingCount(1);
      new ParallelRebelMergeTask<>(
        granularity, this,
        a,am,a1,
        b,bm,b1,
        c,cm+1, ctx
      ).fork();

      a1 = a2;
      b1 = b2;
    }

    ctx.rebelMerge_mergePart(
      a,a0,aLen,
      b,b0,bLen,
      c,c0,cLen
    );

    tryComplete();
  }
}
