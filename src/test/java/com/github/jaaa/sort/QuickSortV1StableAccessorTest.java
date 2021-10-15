package com.github.jaaa.sort;

import net.jqwik.api.Group;


@Group
public class QuickSortV1StableAccessorTest
{
//  private static class Acc<T> implements SortAccessorTestTemplate.SortAccessor<T>, QuickSortAccessor<T>
//  {
//    private final CompareRandomAccessor<T> acc;
//    public Acc(   CompareRandomAccessor<T> _acc ) { acc =_acc; }
//    @Override public T malloc( int len ) { return acc.malloc(len); }
//    @Override public int    compare( T a, int i, T b, int j ) { return acc.compare(a,i, b,j); }
//    @Override public void      swap( T a, int i, T b, int j ) { acc.swap(a,i, b,j); }
//    @Override public void copy     ( T a, int i, T b, int j ) { acc.copy(a,i, b,j); }
//    @Override public void copyRange( T a, int i, T b, int j, int len ) { acc.copyRange(a,i, b,j, len); }
//    @Override public void sort( T arr, int from, int until ) { timSort(arr,from,until, null,0,0); }
//  }
//  private interface TestTemplate extends SortAccessorTestTemplate {
//    @Override default <T> SortAccessor<T> createAccessor( CompareRandomAccessor<T> acc ) { return new Acc(acc); }
//    @Override default boolean isStable() { return true; }
//  }
//
//  @PropertyDefaults( tries = 1_000 )
//  @Group class SorterTestLarge       implements TestTemplate { @Override public int maxArraySize() {return 1_000_000;} }
//  @Group class SorterTestMedium      implements TestTemplate { @Override public int maxArraySize() {return    10_000;} }
//  @Group class SortAccessorTestSmall implements TestTemplate { @Override public int maxArraySize() {return       100;} }
}
