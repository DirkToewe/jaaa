package com.github.jaaa.merge;

import java.util.concurrent.CountedCompleter;

import static java.lang.Math.multiplyExact;


import static java.util.Objects.requireNonNull;

//  Implementation Details
//  ----------------------
//  ParallelRecMerge is a family of parallel merge algorithms based on
//  the RecMerge family of algorithms. All algorithms use a fork-join
//  parallelism. During each fork, the merging problem is split into
//  two, more or less, equally large sub-problems.
//
//  ParallelRecMergeV1, specifically, is a parallel version of RecMergeV4.
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

public class ParallelRecMergeTask<T> extends CountedCompleter<Void>
{
// STATIC FIELDS

// STATIC CONSTRUCTOR

// STATIC METHODS

// FIELDS
  private final ParallelRecMerge.Accessor<? super T> acc;
  private final  T  a, b, c;
  private final int a0,b0,c0,
          a1,b1, granularity;

// CONSTRUCTORS
  public ParallelRecMergeTask(
    int _granularity, CountedCompleter<?> completer,
    T _a, int _a0, int _a1,
    T _b, int _b0, int _b1,
    T _c, int _c0, ParallelRecMerge.Accessor<? super T> _acc
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
    final int granMerge = this.granularity;
          int granCopy  = ctx.parallelRecMerge_mergeOverhead(),
      b0 = this.b0,
      a0 = this.a0,
      c0 = this.c0,
      a1 = this.a1,
      b1 = this.b1;

    assert 0 < granMerge;
    assert 0 < granCopy;
    try {
      granCopy = multiplyExact(granMerge,granCopy);
    }
    catch( ArithmeticException ae ) {
      ae.printStackTrace();
      granCopy = Integer.MAX_VALUE;
    }

    int aLen,
        bLen,
        cLen;
    for(;;)
    {
      aLen = a1-a0;
      bLen = b1-b0;
      cLen = aLen + bLen;
      if( cLen <= (0 < aLen && 0 < bLen ? granMerge : granCopy) ) break;

      int am,bm,cm, a2,b2;
      if( 0==bLen || 0 < aLen && aLen <= bLen ) // <- always split the smaller side, unless the smaller side is empty
      {
        am  = a0 + (cm = aLen>>>1);
        bm  = ctx.parallelRecMerge_searchGap(b,b0,b1, a,am, false);
        cm += c0 + bm-b0;
        ctx.copy(a,am, c,cm);
        a2 = am++;
        b2 = bm;
      }
      else
      {
        bm  = b0 + (cm = bLen>>>1);
        am  = ctx.parallelRecMerge_searchGap(a,a0,a1, b,bm, true);
        cm += c0 + am-a0;
        ctx.copy(b,bm, c,cm);
        a2 = am;
        b2 = bm++;
      }

      addToPendingCount(1);
      new ParallelRecMergeTask<>(
        granularity, this,
        a,am,a1,
        b,bm,b1,
        c,cm+1, ctx
      ).fork();

      a1 = a2;
      b1 = b2;
    }

         if( 0 == aLen ) ctx.copyRange(b,b0, c,c0, bLen);
    else if( 0 == bLen ) ctx.copyRange(a,a0, c,c0, aLen);
    else
      ctx.parallelRecMerge_mergePart(
        a,a0,aLen,
        b,b0,bLen,
        c,c0,cLen
      );

    tryComplete();
  }
}
