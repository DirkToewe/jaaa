package com.github.jaaa.merge;

import java.util.concurrent.CountedCompleter;

import static java.util.Objects.requireNonNull;

//  Implementation Details
//  ----------------------
//  ParallelRecMerge is a family of parallel merge algorithms based on
//  the RecMerge family of algorithms. All algorithms use a fork-join
//  parallelism. During each fork, the merging problem is split into
//  two, more or less, equally large sub-problems.
//
//  ParallelRecMergeV1, specifically, is a parallel version of RecMergeV3.
//  On each fork, the merge problem is cut in half, using `mergeOffset`.
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

public class ParallelZenMergeTask<T> extends CountedCompleter<Void>
{
// STATIC FIELDS

// STATIC CONSTRUCTOR

// STATIC METHODS

  // FIELDS
  private final ParallelZenMerge.Accessor<? super T> acc;
  private final  T  a, b, c;
  private final int a0,b0,c0,
                    a1,b1, height;

  // CONSTRUCTORS
  public ParallelZenMergeTask(
    int _height, CountedCompleter<?> completer,
    T _a, int _a0, int _a1,
    T _b, int _b0, int _b1,
    T _c, int _c0, ParallelZenMerge.Accessor<? super T> _acc
  )
  {
    super(completer);
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
            am = acc.parallelZenMerge_mergeOffset(a,a0,aLen, b,b0,bLen, cm),
            bm = cm - am;
      am += a0;
      bm += b0;
      cm += c0;
      new ParallelZenMergeTask<>(
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
    acc.parallelZenMerge_mergePart(
      a,a0,aLen,
      b,b0,bLen,
      c,c0,cLen
    );

    tryComplete();
  }
}
