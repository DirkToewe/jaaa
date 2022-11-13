package com.github.jaaa.select;

import com.github.jaaa.CompareRandomAccessor;
import net.jqwik.api.Group;
import net.jqwik.api.PropertyDefaults;


@Group
public class HeapSelectRandomAccessTest
{
  @Group
  class HeapSelectRandom
  {
    private interface TestTemplate extends SelectAccessorTestTemplate {
      @Override default <T> SelectAccessor<T> createAccessor( CompareRandomAccessor<T> acc ) {
        return (arr, from, mid, until) ->
          new HeapSelectRandomAccess() {
            @Override public void   swap( int i, int j ) {        acc.   swap(arr,i, arr,j); }
            @Override public int compare( int i, int j ) { return acc.compare(arr,i, arr,j); }
          }.heapSelectRandom(from,mid,until);
      }
      @Override default boolean isStable() { return false; }
    }
    @PropertyDefaults(tries =   100) @Group class Large  implements TestTemplate { @Override public int maxArraySize() {return 1_000_000;} }
    @PropertyDefaults(tries =  1000) @Group class Medium implements TestTemplate { @Override public int maxArraySize() {return    10_000;} }
    @PropertyDefaults(tries = 10000) @Group class Small  implements TestTemplate { @Override public int maxArraySize() {return       100;} }
  }
  @Group
  class HeapSelectRandomL
  {
    private interface TestTemplate extends SelectAccessorTestTemplate {
      @Override default <T> SelectAccessor<T> createAccessor( CompareRandomAccessor<T> acc ) {
        return (arr, from, mid, until) ->
          new HeapSelectRandomAccess() {
            @Override public void   swap( int i, int j ) {        acc.   swap(arr,i, arr,j); }
            @Override public int compare( int i, int j ) { return acc.compare(arr,i, arr,j); }
          }.heapSelectRandomL(from,mid,until);
      }
      @Override default boolean isStable() { return false; }
    }
    @PropertyDefaults(tries =   100) @Group class Large  implements TestTemplate { @Override public int maxArraySize() {return 1_000_000;} }
    @PropertyDefaults(tries =  1000) @Group class Medium implements TestTemplate { @Override public int maxArraySize() {return    10_000;} }
    @PropertyDefaults(tries = 10000) @Group class Small  implements TestTemplate { @Override public int maxArraySize() {return       100;} }
  }
  @Group
  class HeapSelectRandomR
  {
    private interface TestTemplate extends SelectAccessorTestTemplate {
      @Override default <T> SelectAccessor<T> createAccessor( CompareRandomAccessor<T> acc ) {
        return (arr, from, mid, until) ->
          new HeapSelectRandomAccess() {
            @Override public void   swap( int i, int j ) {        acc.   swap(arr,i, arr,j); }
            @Override public int compare( int i, int j ) { return acc.compare(arr,i, arr,j); }
          }.heapSelectRandomR(from,mid,until);
      }
      @Override default boolean isStable() { return false; }
    }
    @PropertyDefaults(tries =   100) @Group class Large  implements TestTemplate { @Override public int maxArraySize() {return 1_000_000;} }
    @PropertyDefaults(tries =  1000) @Group class Medium implements TestTemplate { @Override public int maxArraySize() {return    10_000;} }
    @PropertyDefaults(tries = 10000) @Group class Small  implements TestTemplate { @Override public int maxArraySize() {return       100;} }
  }
}
