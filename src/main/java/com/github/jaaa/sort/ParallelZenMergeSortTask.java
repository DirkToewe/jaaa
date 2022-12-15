package com.github.jaaa.sort;

import com.github.jaaa.merge.ParallelZenMergeTask;

import java.util.concurrent.CountedCompleter;

import static java.util.Objects.requireNonNull;


public class ParallelZenMergeSortTask<T> extends CountedCompleter<Void>
{
// STATIC FIELDS
  private static final class Relay extends CountedCompleter<Void>
  {
    private final CountedCompleter<?> target;
    Relay( CountedCompleter<?> _target ) {
      super( null, 1 );
      assert null != _target;
      target = _target;
    }
    public void compute() {}
    public void onCompletion( CountedCompleter<?> t) { target.compute(); }
  }

// STATIC CONSTRUCTOR

// STATIC METHODS

// FIELDS
  private final int src0,
       height, len, dst0;
  private final T src, dst;
  ParallelZenMergeSort.Accessor<? super T> acc;

// CONSTRUCTORS
  public ParallelZenMergeSortTask(
    int _height, CountedCompleter<?> completer,
    T _src, int _src0,
    T _dst, int _dst0, int _len,
    ParallelZenMergeSort.Accessor<? super T> _acc
  ) {
    super(completer);
    src =_src; src0 =_src0; height =_height;
    dst =_dst; dst0 =_dst0;    len =_len;
    acc = requireNonNull(_acc);
  }

// METHODS
  @Override
  public void compute()
  {
    var acc = this.acc;
    T src = this.src,
      dst = this.dst;
    int len = this.len,
       src0 = this.src0,
       dst0 = this.dst0,
     height = this.height;

    CountedCompleter<?> parentTask = this;

    setPendingCount(0);

    while( height > 0 ) {
      swap: {
        // swap source and destination
        T   tmp = src;  src = dst;  dst = tmp;
        int idx = src0; src0= dst0; dst0= idx;
      }

      int lenL = len >>> 1,
          lenR = len - lenL,
          src1 = src0 + lenL,
          src2 = src1 + lenR;
      parentTask = new ParallelZenMergeTask<>(
        height, parentTask,
        src,src0,src1,
        src,src1,src2,
        dst,dst0, acc
      );
      parentTask = new Relay(parentTask);

      --height;

      int dst1 = dst0 + lenL;
      new ParallelZenMergeSortTask<T>(
        height, parentTask,
        src, src1,
        dst, dst1, lenR, acc
      ).fork();

      len = lenL;
    }

    acc.parallelZenMergeSort_sort(
      src,src0,src0+len,
      dst,dst0,dst0+len
    );
    parentTask.tryComplete();
  }
}
