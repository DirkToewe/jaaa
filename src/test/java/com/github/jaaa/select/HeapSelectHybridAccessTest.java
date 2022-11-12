package com.github.jaaa.select;

import com.github.jaaa.CompareRandomAccessor;
import net.jqwik.api.Group;
import net.jqwik.api.PropertyDefaults;


@Group
public class HeapSelectHybridAccessTest
{
  @Group
  class HeapSelectHybridL
  {
    private interface TestTemplate extends SelectAccessorTestTemplate {
      @Override default <T> SelectAccessor<T> createAccessor( CompareRandomAccessor<T> acc ) {
        return (arr, from, mid, until) ->
          new HeapSelectHybridAccess() {
            @Override public void   swap( int i, int j ) {        acc.   swap(arr,i, arr,j); }
            @Override public int compare( int i, int j ) { return acc.compare(arr,i, arr,j); }
          }.heapSelectHybridL(from,mid,until);
      }
      @Override default boolean isStable() { return false; }
    }
    @PropertyDefaults(tries =   100) @Group class Large  implements TestTemplate { @Override public int maxArraySize() {return 1_000_000;} }
    @PropertyDefaults(tries =  1000) @Group class Medium implements TestTemplate { @Override public int maxArraySize() {return    10_000;} }
    @PropertyDefaults(tries = 10000) @Group class Small  implements TestTemplate { @Override public int maxArraySize() {return       100;} }
  }
  @Group
  class HeapSelectHybridR
  {
    private interface TestTemplate extends SelectAccessorTestTemplate {
      @Override default <T> SelectAccessor<T> createAccessor( CompareRandomAccessor<T> acc ) {
        return (arr, from, mid, until) ->
          new HeapSelectHybridAccess() {
            @Override public void   swap( int i, int j ) {        acc.   swap(arr,i, arr,j); }
            @Override public int compare( int i, int j ) { return acc.compare(arr,i, arr,j); }
          }.heapSelectHybridR(from,mid,until);
      }
      @Override default boolean isStable() { return false; }
    }
    @PropertyDefaults(tries =   100) @Group class Large  implements TestTemplate { @Override public int maxArraySize() {return 1_000_000;} }
    @PropertyDefaults(tries =  1000) @Group class Medium implements TestTemplate { @Override public int maxArraySize() {return    10_000;} }
    @PropertyDefaults(tries = 10000) @Group class Small  implements TestTemplate { @Override public int maxArraySize() {return       100;} }
  }
  @Group
  class HeapSelectHybridV1
  {
    private interface TestTemplate extends SelectAccessorTestTemplate {
      @Override default <T> SelectAccessor<T> createAccessor( CompareRandomAccessor<T> acc ) {
        return (arr, from, mid, until) ->
          new HeapSelectHybridAccess() {
            @Override public void   swap( int i, int j ) {        acc.   swap(arr,i, arr,j); }
            @Override public int compare( int i, int j ) { return acc.compare(arr,i, arr,j); }
          }.heapSelectHybridV1(from,mid,until);
      }
      @Override default boolean isStable() { return false; }
    }
    @PropertyDefaults(tries =   100) @Group class Large  implements TestTemplate { @Override public int maxArraySize() {return 1_000_000;} }
    @PropertyDefaults(tries =  1000) @Group class Medium implements TestTemplate { @Override public int maxArraySize() {return    10_000;} }
    @PropertyDefaults(tries = 10000) @Group class Small  implements TestTemplate { @Override public int maxArraySize() {return       100;} }
  }
  @Group
  class HeapSelectHybridV2
  {
    private interface TestTemplate extends SelectAccessorTestTemplate {
      @Override default <T> SelectAccessor<T> createAccessor( CompareRandomAccessor<T> acc ) {
        return (arr, from, mid, until) ->
          new HeapSelectHybridAccess() {
            @Override public void   swap( int i, int j ) {        acc.   swap(arr,i, arr,j); }
            @Override public int compare( int i, int j ) { return acc.compare(arr,i, arr,j); }
          }.heapSelectHybridV2(from,mid,until);
      }
      @Override default boolean isStable() { return false; }
    }
    @PropertyDefaults(tries =   100) @Group class Large  implements TestTemplate { @Override public int maxArraySize() {return 1_000_000;} }
    @PropertyDefaults(tries =  1000) @Group class Medium implements TestTemplate { @Override public int maxArraySize() {return    10_000;} }
    @PropertyDefaults(tries = 10000) @Group class Small  implements TestTemplate { @Override public int maxArraySize() {return       100;} }
  }
}
