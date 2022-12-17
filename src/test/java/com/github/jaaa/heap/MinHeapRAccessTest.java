package com.github.jaaa.heap;

import com.github.jaaa.*;
import com.github.jaaa.compare.CompareRandomAccessor;
import com.github.jaaa.Boxing;
import com.github.jaaa.permute.Swap;
import com.github.jaaa.sort.SortAccessorTestTemplate;
import net.jqwik.api.ForAll;
import net.jqwik.api.Group;
import net.jqwik.api.Property;
import net.jqwik.api.PropertyDefaults;
import net.jqwik.api.constraints.IntRange;

import java.util.function.IntConsumer;

import static com.github.jaaa.util.IMath.log2Ceil;
import static org.assertj.core.api.Assertions.assertThat;


@Group
public class MinHeapRAccessTest
{
  abstract static class HeapSortImpl
  {
    abstract void sortImpl( MinHeapRAccess acc, int from, int until );
    private abstract class TestTemplate implements SortAccessorTestTemplate {
      @Override public <T> SortAccessor<T> createAccessor( CompareRandomAccessor<T> acc ) {
        return (arr, from, until) -> {
          MinHeapRAccess acs = new MinHeapRAccess(){
            @Override public int compare( int i, int j ) { return acc.compare(arr,i, arr,j); }
            @Override public void   swap( int i, int j ) {        acc.   swap(arr,i, arr,j); }
          };
          sortImpl(acs, from, until);
        };
      }
      @Override public boolean isStable() { return false; }
    }
    @PropertyDefaults( tries = 100 )
    @Group class Large  extends TestTemplate { @Override public int maxArraySize() {return 1_000_000;} }
    @Group class Medium extends TestTemplate { @Override public int maxArraySize() {return    10_000;} }
    @Group class Small  extends TestTemplate { @Override public int maxArraySize() {return       100;} }
  }
  @Group class HeapSortImpl1 extends HeapSortImpl {
    @Override void sortImpl(MinHeapRAccess acc, int from, int until) {
      acc.minHeapR_build(from,until);
      for( int i=from; i < until-1; ) {
        acc.swap(i++, until-1);
        acc.minHeapR_siftDown(i, until, until-1);
      }
    }
  }
  @Group class HeapSortImpl2 extends HeapSortImpl {
    @Override void sortImpl(MinHeapRAccess acc, int from, int until) {
      acc.minHeapR_buildFast(from,until);
      for( int i=from; i < until-1; ) {
        acc.swap(i++, until-1);
        acc.minHeapR_siftDown(i, until, until-1);
      }
    }
  }
  @Group class HeapSortImpl3 extends HeapSortImpl {
    @Override void sortImpl(MinHeapRAccess acc, int from, int until) {
      acc.minHeapR_build(from,until);
      for( int i=from; i < until-1; ) {
        acc.swap(i++, until-1);
        acc.minHeapR_siftDownFast(i, until, until-1);
      }
    }
  }
  @Group class HeapSortImpl4 extends HeapSortImpl {
    @Override void sortImpl(MinHeapRAccess acc, int from, int until) {
      acc.minHeapR_buildFast(from,until);
      for( int i=from; i < until-1; ) {
        acc.swap(i++, until-1);
        acc.minHeapR_siftDownFast(i, until, until-1);
      }
    }
  }

  static class CountingAcc<T extends Comparable<? super T>> implements MinHeapRAccess {
    private final T[] arr;
    private final int from, until;
    CountingAcc( T[] _arr, int _from, int _until ) {
      assertThat(_from).isLessThanOrEqualTo(_until);
      arr   = _arr;
      from  = _from;
      until = _until;
    }
    public long nComps = 0;
    @Override public void   swap( int i, int j ) {
      assertThat(i).isGreaterThanOrEqualTo(from); assertThat(i).isLessThan(until);
      assertThat(j).isGreaterThanOrEqualTo(from); assertThat(j).isLessThan(until);
      Swap.swap(arr,i,j);
    }
    @Override public int compare( int i, int j ) {
      assertThat(i).isGreaterThanOrEqualTo(from); assertThat(i).isLessThan(until);
      assertThat(j).isGreaterThanOrEqualTo(from); assertThat(j).isLessThan(until);
      ++nComps;
      return arr[i].compareTo(arr[j]);
    }
  }

  @PropertyDefaults( tries = 100_000 )
  abstract static class BuildHeapImpl implements ArrayProviderTemplate
  {
    @Override public int maxArraySize() { return 1_000; }

    abstract void buildImpl( MinHeapRAccess acc, int from, int until );
    abstract long maxComps( int n );

    @Property public void compare_to_ref_RangeBoolean( @ForAll("arraysWithRangeBoolean") WithRange<boolean[]> sample ) { compare_to_ref( sample.map(Boxing::boxed) ); }
    @Property public void compare_to_ref_RangeByte   ( @ForAll("arraysWithRangeByte"   ) WithRange<   byte[]> sample ) { compare_to_ref( sample.map(Boxing::boxed) ); }
    @Property public void compare_to_ref_RangeShort  ( @ForAll("arraysWithRangeShort"  ) WithRange<  short[]> sample ) { compare_to_ref( sample.map(Boxing::boxed) ); }
    @Property public void compare_to_ref_RangeInt    ( @ForAll("arraysWithRangeInt"    ) WithRange<    int[]> sample ) { compare_to_ref( sample.map(Boxing::boxed) ); }
    @Property public void compare_to_ref_RangeLong   ( @ForAll("arraysWithRangeLong"   ) WithRange<   long[]> sample ) { compare_to_ref( sample.map(Boxing::boxed) ); }
    @Property public void compare_to_ref_RangeChar   ( @ForAll("arraysWithRangeChar"   ) WithRange<   char[]> sample ) { compare_to_ref( sample.map(Boxing::boxed) ); }
    @Property public void compare_to_ref_RangeFloat  ( @ForAll("arraysWithRangeFloat"  ) WithRange<  float[]> sample ) { compare_to_ref( sample.map(Boxing::boxed) ); }
    @Property public void compare_to_ref_RangeDouble ( @ForAll("arraysWithRangeDouble" ) WithRange< double[]> sample ) { compare_to_ref( sample.map(Boxing::boxed) ); }

    private <T extends Comparable<? super T>> void compare_to_ref( WithRange<T[]> sample )
    {
      int from = sample.getFrom(),
         until = sample.getUntil();

      T[] tst = sample.getData().clone();
      CountingAcc<T> acc = new CountingAcc<>(tst, from, until);
      buildImpl(acc, from, until);
      assertThat(acc.nComps).isLessThanOrEqualTo( maxComps(until-from) );

      if( from < until )
      {
        boolean[]   visited = new boolean[until-from];
        IntConsumer visitor = new IntConsumer(){
          @Override public void accept( int parent )
          {
            // make sure every entry is visited only once
            assertThat(visited[parent-from]).isFalse();
            visited[parent-from] = true;
            // check right child
            int child = acc.minHeapR_child(from,until, parent);
            if( child >= from ) {
              assertThat( acc.compare(child,parent) ).isGreaterThanOrEqualTo(0);
              accept(child);
            }
            // check left child
            if( child-- > from ) {
              assertThat( acc.compare(child,parent) ).isGreaterThanOrEqualTo(0);
              accept(child);
            }
          }
        };
        visitor.accept(until-1);
        assertThat(visited).containsOnly(true);
      }
    }
  }
  @Group class BuildHeap extends BuildHeapImpl {
    @Override void buildImpl( MinHeapRAccess acc, int from, int until ) { acc.minHeapR_build(from,until); }
    @Override long maxComps( int n ) { return 2L*n; }
  }
  @Group class BuildHeapFast extends BuildHeapImpl {
    @Override void buildImpl( MinHeapRAccess acc, int from, int until ) { acc.minHeapR_buildFast(from,until); }
    @Override long maxComps( int n ) { return 2L*n; }
  }
  @Group class BuildHeapSiftDown extends BuildHeapImpl {
    @Override void buildImpl( MinHeapRAccess acc, int from, int until ) {
      for( int i=from; i < until; i++ )
        acc.minHeapR_siftDown(from,until, i);
    }
    @Override long maxComps( int n ) { return 2L*n; }
  }
  @Group class BuildHeapSiftDownFast extends BuildHeapImpl {
    @Override void buildImpl( MinHeapRAccess acc, int from, int until ) {
      for( int i=from; i < until; i++ )
        acc.minHeapR_siftDownFast(from,until, i);
    }
    @Override long maxComps( int n ) { return 2L*n; }
  }
  @Group class BuildHeapBubbleUp extends BuildHeapImpl {
    @Override void buildImpl( MinHeapRAccess acc, final int from, final int until ) {
      for( int i=until; i-- > from; )
        acc.minHeapR_bubbleUp(from,until, i);
    }
    @Override long maxComps( int n ) { return (long) log2Ceil(n+1)*n; }
  }

  @Property public void child_parent_test( @ForAll @IntRange(min=0, max=1_000_000) int from, @ForAll @IntRange(min=0, max=1_000_000) int until ) {
    if( from > until ) {
      int t = from;
      from = until;
      until = t;
    }

    CountingAcc<?> acc = new CountingAcc<>(null, from, until);

    for( int p=from; p < until; p++ ) {
      int c = acc.minHeapR_child(from,until, p);
      if( c  >= from ) { int q = acc.minHeapR_parent(from,until, c); assertThat(q).isEqualTo(p); }
      if( c-- > from ) { int q = acc.minHeapR_parent(from,until, c); assertThat(q).isEqualTo(p); }
    }
  }
}
