package com.github.jaaa.sort;

import com.github.jaaa.CompareRandomAccessor;
import com.github.jaaa.merge.TimMergeAccessor;
import com.github.jaaa.misc.RevertAccessor;
import com.github.jaaa.search.ExpL2RSearch;

import java.util.Spliterators.AbstractIntSpliterator;
import java.util.function.IntConsumer;
import java.util.function.IntUnaryOperator;

import static java.lang.Math.*;
import static java.util.Spliterator.*;
import static java.util.stream.StreamSupport.intStream;


public interface TimSortAccessor<T> extends CompareRandomAccessor<T>,
                                                 TimMergeAccessor<T>,
                                                   RevertAccessor<T>
{
  int MIN_RUN_LEN = 16;

  IntUnaryOperator STACK_LEN_FN = new IntUnaryOperator()
  {
    private final int[] arrayLenghts = intStream(
      new AbstractIntSpliterator(Long.MAX_VALUE, DISTINCT|IMMUTABLE|NONNULL|ORDERED)
      {
        private int sum = 0;
        private long  a = 0,
                      b = MIN_RUN_LEN;
        @Override public boolean tryAdvance( IntConsumer action ) {
          if( sum >= Integer.MAX_VALUE ) return false;
          action.accept( sum = (int) min(Integer.MAX_VALUE, addExact(sum,a)) );
          b = addExact(1+a, a=b);
          return true;
        }
      },
      /*parallel=*/false
    ).toArray();
    @Override public int applyAsInt( int len ) {
      return ExpL2RSearch.searchGap(arrayLenghts, len) + 2;
    }
  };

  private static int minRunLen( int n )
  {
    assert 1 == Integer.bitCount(MIN_RUN_LEN);
    return TimSort.optimalRunLength(MIN_RUN_LEN,n);
  }

  default int timSort_prepareNextRun( T arr, int from, int until, int minRunLen )
  {
    // TODO: improve adaptive in-place sorting of the runs
    int start = from+1;
    if( start >= until ) return start;

    boolean ascending = compare(arr,start++, arr,from) >= 0;

    while( start < until && compare(arr,start, arr,start-1) >= 0 == ascending )
         ++start;

    if( ! ascending )
      revert(arr, from,start);

                  until = min(from+minRunLen, until);
    for(; start < until; start++)
    {
      int lo = from,
          hi = start;
      while( lo < hi ) {       int mid = lo+hi >>> 1;
        if( compare(arr,start, arr,mid) < 0 )
                              hi = mid;
        else                  lo = mid+1;
      }

      for( int i=start; i > lo; )
        swap(arr,i, arr,--i);
    }
    return start;
  }

  default void timSort( T arr, int arrFrom, int arrUntil, T _buf, int _bufFrom, int _bufUntil )
  {
    if( arrFrom < 0         ) throw new IllegalArgumentException();
    if( arrFrom > arrUntil  ) throw new IllegalArgumentException();
    if(_bufFrom < 0         ) throw new IllegalArgumentException();
    if(_bufFrom > _bufUntil ) throw new IllegalArgumentException();

    int length = arrUntil - arrFrom;
    if( length <   2 ) return;
    if( length <= MIN_RUN_LEN*2 ) {
      timSort_prepareNextRun(arr,arrFrom,arrUntil,MIN_RUN_LEN*2);
      return;
    }

    new Object()
    {
    // STATIC FIELDS
      private static final int INIT_BUF_LEN = 256;

    // FIELDS
      private  T  buf    = _buf;
      private int buf0   = _bufFrom,
                  bufLen = _bufUntil - _bufFrom,
                stackTop = 0,
               minGallop = MIN_GALLOP;
      private final int minRunLen = minRunLen(length);
      private final int[] stack = new int[ STACK_LEN_FN.applyAsInt(length) ];

    // CONSTRUCTOR
      {
        ensureCapacity(1); // <- required by selectionSort

        stack[0] = arrFrom;
        for( int i=arrFrom; i < arrUntil; ) {
          stack[++stackTop] = i = timSort_prepareNextRun(arr, i,arrUntil, minRunLen);
          mergeCollapse();
        }

        mergeRest();
      }

    // METHODS
      private void ensureCapacity( int capacity )
      {
        int half = length>>>1;
//        assert half>=capacity;
        if( bufLen < capacity ) {
            bufLen = 1<<31; capacity = max(capacity, INIT_BUF_LEN-1);
          // find next power of two larger larger than capacity
          if( bufLen>>>16 > capacity ) bufLen >>>= 16;
          if( bufLen>>> 8 > capacity ) bufLen >>>=  8;
          if( bufLen>>> 4 > capacity ) bufLen >>>=  4;
          if( bufLen>>> 2 > capacity ) bufLen >>>=  2;
          if( bufLen>>> 1 > capacity ) bufLen >>>=  1;
          if( bufLen > half || bufLen < 0 )
              bufLen = half;
          buf  = malloc(bufLen);
          buf0 = 0;
        }
      }

      private void merge( int from, int mid, int until )
      {
//        assert 0 <= from;
//        assert      from < mid;
//        assert             mid < until;

        from  = expL2RSearchGapR(arr,from,mid,       arr,mid  ); if( from == mid ) return; // <- trivially mergeable case
        until = expR2LSearchGapL(arr,   1+mid,until, arr,mid-1); // 1+mid since trivially mergeable case is caught above
//        from  = gallopL2RSearchGapR(arr,from,mid,       arr,mid  ); if( from == mid ) return; // <- trivially mergeable case
//        until = gallopR2LSearchGapL(arr,   1+mid,until, arr,mid-1); // 1+mid since trivially mergeable case is caught above

        int aLen =       mid-from,
            bLen = until-mid;
        if( aLen <= bLen ) {
          ensureCapacity(aLen);
          copyRange(arr,from, buf,buf0, aLen);
          copy(arr,mid, arr,from);
          minGallop = _timMergeL2R(
            minGallop, buf,  buf0, --aLen,
                       arr, mid+1, --bLen,
                       arr,from+1
          );
          copy(buf,buf0+aLen, arr,until-1);
        }
        else {
          ensureCapacity(bLen);
          copyRange(arr,mid, buf,buf0, bLen);
          copy(arr,mid-1, arr,until-1);
          minGallop = _timMergeR2L(
            minGallop, arr,from,  --aLen,
                       buf,buf0+1,--bLen,
                       arr,from+1
          );
          copy(buf,buf0, arr,from);
        }
      }

      private int runLen( int stackPos ) {
        return stack[stackPos] - stack[stackPos-1];
      }

//      private boolean checkStack() {
//        for( int i=stackTop-2; 0 < i; i-- ) assert runLen(i) > runLen(i+1) + runLen(i+2);
//        for( int i=stackTop-1; 0 < i; i-- ) assert runLen(i) > runLen(i+1);
//        return true;
//      }

      private void mergeCollapse()
      {
        loop: for(; stackTop > 1; stackTop--) {
          int pos = stackTop;
          block: {          int l2 = runLen(pos-0),
                                l1 = runLen(pos-1);
            if( 2 < pos ) { int l0 = runLen(pos-2);
              if(                     l0 <= l1+l2 ||
                3 < pos && runLen(pos-3) <= l1+l0 )
              {
                if( l0 < l2 ) --pos;
                break block;
              }
            }
            if( l1 > l2 ) break loop;
          }
          merge(
            stack[pos-2],
            stack[pos-1],
            stack[pos-0]
          );stack[pos-1] = stack[pos  ];
            stack[pos  ] = stack[pos+1]; // <- never out of bounds (stack is 1 entry larger)
        }

//        assert rangeClosed(2,stackTop).allMatch( i -> runLen(i-1) >               runLen(i) );
//        assert rangeClosed(3,stackTop).allMatch( i -> runLen(i-2) > runLen(i-1) + runLen(i) );
      }

      private void mergeRest()
      {
        for( ;  1 < stackTop; stackTop--) {
          int pos = stackTop;
          if( pos > 2 && runLen(pos-2) < runLen(pos-0) )
            --pos;
          merge(
            stack[pos-2],
            stack[pos-1],
            stack[pos-0]
          );stack[pos-1] = stack[pos  ];
            stack[pos  ] = stack[pos+1]; // <- never out of bounds (stack is 1 entry larger)
        }
      }
    };
  }
}
