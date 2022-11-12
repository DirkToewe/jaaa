package com.github.jaaa.select;

import com.github.jaaa.CompareRandomAccessor;
import net.jqwik.api.Group;
import net.jqwik.api.PropertyDefaults;


@Group
public class HeapSelectFastAccessTest
{
  @Group
  class HeapSelectFastL
  {
    private interface TestTemplate extends SelectAccessorTestTemplate {
      @Override default <T> SelectAccessor<T> createAccessor( CompareRandomAccessor<T> acc ) {
        return (arr, from, mid, until) ->
          new HeapSelectFastAccess() {
            @Override public void   swap( int i, int j ) {        acc.   swap(arr,i, arr,j); }
            @Override public int compare( int i, int j ) { return acc.compare(arr,i, arr,j); }
          }.heapSelectFastL(from,mid,until);
      }
      @Override default boolean isStable() { return false; }
    }
    @PropertyDefaults(tries =   100) @Group class Large  implements TestTemplate { @Override public int maxArraySize() {return 1_000_000;} }
    @PropertyDefaults(tries =  1000) @Group class Medium implements TestTemplate { @Override public int maxArraySize() {return    10_000;} }
    @PropertyDefaults(tries = 10000) @Group class Small  implements TestTemplate { @Override public int maxArraySize() {return       100;} }
  }
  @Group
  class HeapSelectFastR
  {
    interface TestTemplate extends SelectAccessorTestTemplate {
      @Override default <T> SelectAccessor<T> createAccessor( CompareRandomAccessor<T> acc ) {
        return (arr, from, mid, until) ->
          new HeapSelectFastAccess() {
            @Override public void   swap( int i, int j ) {        acc.   swap(arr,i, arr,j); }
            @Override public int compare( int i, int j ) { return acc.compare(arr,i, arr,j); }
          }.heapSelectFastR(from,mid,until);
      }
      @Override default boolean isStable() { return false; }
    }
    @PropertyDefaults(tries =   100) @Group class Large  implements TestTemplate { @Override public int maxArraySize() {return 1_000_000;} }
    @PropertyDefaults(tries =  1000) @Group class Medium implements TestTemplate { @Override public int maxArraySize() {return    10_000;} }
    @PropertyDefaults(tries = 10000) @Group class Small  implements TestTemplate { @Override public int maxArraySize() {return       100;} }
  }
  @Group
  class HeapSelectFastV1
  {
    interface TestTemplate extends SelectAccessorTestTemplate {
      @Override default <T> SelectAccessor<T> createAccessor( CompareRandomAccessor<T> acc ) {
        return (arr, from, mid, until) ->
          new HeapSelectFastAccess() {
            @Override public void   swap( int i, int j ) {        acc.   swap(arr,i, arr,j); }
            @Override public int compare( int i, int j ) { return acc.compare(arr,i, arr,j); }
          }.heapSelectFastV1(from,mid,until);
      }
      @Override default boolean isStable() { return false; }
    }
    @PropertyDefaults(tries =   100) @Group class Large  implements TestTemplate { @Override public int maxArraySize() {return 1_000_000;} }
    @PropertyDefaults(tries =  1000) @Group class Medium implements TestTemplate { @Override public int maxArraySize() {return    10_000;} }
    @PropertyDefaults(tries = 10000) @Group class Small  implements TestTemplate { @Override public int maxArraySize() {return       100;} }
  }
  @Group
  class HeapSelectFastV2
  {
    interface TestTemplate extends SelectAccessorTestTemplate {
      @Override default <T> SelectAccessor<T> createAccessor( CompareRandomAccessor<T> acc ) {
        return (arr, from, mid, until) ->
          new HeapSelectFastAccess() {
            @Override public void   swap( int i, int j ) {        acc.   swap(arr,i, arr,j); }
            @Override public int compare( int i, int j ) { return acc.compare(arr,i, arr,j); }
          }.heapSelectFastV2(from,mid,until);
      }
      @Override default boolean isStable() { return false; }
    }
    @PropertyDefaults(tries =   100) @Group class Large  implements TestTemplate { @Override public int maxArraySize() {return 1_000_000;} }
    @PropertyDefaults(tries =  1000) @Group class Medium implements TestTemplate { @Override public int maxArraySize() {return    10_000;} }
    @PropertyDefaults(tries = 10000) @Group class Small  implements TestTemplate { @Override public int maxArraySize() {return       100;} }
  }
}
