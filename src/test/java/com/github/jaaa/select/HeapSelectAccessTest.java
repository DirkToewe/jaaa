package com.github.jaaa.select;

import com.github.jaaa.compare.CompareRandomAccessor;
import net.jqwik.api.Group;
import net.jqwik.api.PropertyDefaults;


@Group
public class HeapSelectAccessTest
{
  @Group
  class HeapSelect
  {
    private abstract class TestTemplate implements SelectAccessorTestTemplate {
      @Override public <T> SelectAccessor<T> createAccessor( CompareRandomAccessor<T> acc ) {
        return (arr, from, mid, until) ->
          new HeapSelectAccess() {
            @Override public void   swap( int i, int j ) {        acc.   swap(arr,i, arr,j); }
            @Override public int compare( int i, int j ) { return acc.compare(arr,i, arr,j); }
          }.heapSelect(from,mid,until);
      }
      @Override public boolean isStable() { return false; }
    }
    @PropertyDefaults(tries =   100) @Group class Large  extends TestTemplate { @Override public int maxArraySize() {return 100_000;} }
    @PropertyDefaults(tries =  1000) @Group class Medium extends TestTemplate { @Override public int maxArraySize() {return  10_000;} }
    @PropertyDefaults(tries = 10000) @Group class Small  extends TestTemplate { @Override public int maxArraySize() {return     100;} }
  }
  @Group
  class HeapSelectL
  {
    private abstract class TestTemplate implements SelectAccessorTestTemplate {
      @Override public <T> SelectAccessor<T> createAccessor( CompareRandomAccessor<T> acc ) {
        return (arr, from, mid, until) ->
          new HeapSelectAccess() {
            @Override public void   swap( int i, int j ) {        acc.   swap(arr,i, arr,j); }
            @Override public int compare( int i, int j ) { return acc.compare(arr,i, arr,j); }
          }.heapSelectL(from,mid,until);
      }
      @Override public boolean isStable() { return false; }
    }
    @PropertyDefaults(tries =   100) @Group class Large  extends TestTemplate { @Override public int maxArraySize() {return 100_000;} }
    @PropertyDefaults(tries =  1000) @Group class Medium extends TestTemplate { @Override public int maxArraySize() {return  10_000;} }
    @PropertyDefaults(tries = 10000) @Group class Small  extends TestTemplate { @Override public int maxArraySize() {return     100;} }
  }
  @Group
  class HeapSelectR
  {
    private abstract class TestTemplate implements SelectAccessorTestTemplate {
      @Override public <T> SelectAccessor<T> createAccessor( CompareRandomAccessor<T> acc ) {
        return (arr, from, mid, until) ->
          new HeapSelectAccess() {
            @Override public void   swap( int i, int j ) {        acc.   swap(arr,i, arr,j); }
            @Override public int compare( int i, int j ) { return acc.compare(arr,i, arr,j); }
          }.heapSelectR(from,mid,until);
      }
      @Override public boolean isStable() { return false; }
    }
    @PropertyDefaults(tries =   100) @Group class Large  extends TestTemplate { @Override public int maxArraySize() {return 100_000;} }
    @PropertyDefaults(tries =  1000) @Group class Medium extends TestTemplate { @Override public int maxArraySize() {return  10_000;} }
    @PropertyDefaults(tries = 10000) @Group class Small  extends TestTemplate { @Override public int maxArraySize() {return     100;} }
  }
}
