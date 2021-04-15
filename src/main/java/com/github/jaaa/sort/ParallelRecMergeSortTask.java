package com.github.jaaa.sort;

import java.util.concurrent.CountedCompleter;

import static java.util.Objects.requireNonNull;

public class ParallelRecMergeSortTask<T> extends CountedCompleter<Void>
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
    public final void compute() {}
    public final void onCompletion(CountedCompleter<?> t) { target.compute(); }
  }

// STATIC CONSTRUCTOR

// STATIC METHODS

// FIELDS
  private final int src0,
       height, len, dst0;
  private final T src, dst;
  ParallelRecMergeSort.Accessor<? super T> acc;

// CONSTRUCTORS
  public ParallelRecMergeSortTask(
    int _height, CountedCompleter<?> completer,
    T _src, int _src0,
    T _dst, int _dst0, int _len,
    ParallelRecMergeSort.Accessor<? super T> _acc
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
          lenR = len - lenL;
      parentTask = acc.newMergeTask(
        height, parentTask,
        src,src0,     lenL,
            src0+lenL,lenR,
        dst,dst0
      );
      parentTask = new Relay(parentTask);

      --height;

      new ParallelRecMergeSortTask<T>(
        height, parentTask,
        src, src0+lenL,
        dst, dst0+lenL, lenR, acc
      ).fork();

      len = lenL;
    }

    acc.sort(
      src,src0,src0+len,
      dst,dst0,dst0+len
    );
    parentTask.tryComplete();
  }
}
