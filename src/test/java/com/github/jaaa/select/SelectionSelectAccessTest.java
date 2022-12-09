package com.github.jaaa.select;

import com.github.jaaa.compare.CompareRandomAccessor;
import net.jqwik.api.Group;
import net.jqwik.api.PropertyDefaults;


@Group
public class SelectionSelectAccessTest
{
//  @Group
//  class SelectionSelect
//  {
//    private interface TestTemplate extends SelectAccessorTestTemplate {
//      @Override default <T> SelectAccessor<T> createAccessor( CompareRandomAccessor<T> acc ) {
//        return (arr, from, mid, until) ->
//          new SelectionSelectAccess() {
//            @Override public void   swap( int i, int j ) {        acc.   swap(arr,i, arr,j); }
//            @Override public int compare( int i, int j ) { return acc.compare(arr,i, arr,j); }
//          }.selectionSelect(from,mid,until);
//      }
//      @Override default boolean isStable() { return false; }
//    }
//    @PropertyDefaults(tries =  1000) @Group class Medium implements TestTemplate { @Override public int maxArraySize() {return    10_000;} }
//    @PropertyDefaults(tries = 10000) @Group class Small  implements TestTemplate { @Override public int maxArraySize() {return       100;} }
//  }
  @Group
  class SelectionSelectL
  {
    private interface TestTemplate extends SelectAccessorTestTemplate {
      @Override default <T> SelectAccessor<T> createAccessor( CompareRandomAccessor<T> acc ) {
        return (arr, from, mid, until) ->
          new SelectionSelectAccess() {
            @Override public void   swap( int i, int j ) {        acc.   swap(arr,i, arr,j); }
            @Override public int compare( int i, int j ) { return acc.compare(arr,i, arr,j); }
          }.selectionSelectL(from,mid,until);
      }
      @Override default boolean isStable() { return false; }
      @Override default boolean sortsLHS(int from, int mid, int until) { return true; }
      @Override default boolean sortsRHS(int from, int mid, int until) { return false; }
    }
    @PropertyDefaults(tries =  1000) @Group class Medium implements TestTemplate { @Override public int maxArraySize() {return 1_000;} }
    @PropertyDefaults(tries = 10000) @Group class Small  implements TestTemplate { @Override public int maxArraySize() {return   100;} }
  }
  @Group
  class SelectionSelectR
  {
    private interface TestTemplate extends SelectAccessorTestTemplate {
      @Override default <T> SelectAccessor<T> createAccessor( CompareRandomAccessor<T> acc ) {
        return (arr, from, mid, until) ->
          new SelectionSelectAccess() {
            @Override public void   swap( int i, int j ) {        acc.   swap(arr,i, arr,j); }
            @Override public int compare( int i, int j ) { return acc.compare(arr,i, arr,j); }
          }.selectionSelectR(from,mid,until);
      }
      @Override default boolean isStable() { return false; }
      @Override default boolean sortsLHS(int from, int mid, int until) { return false; }
      @Override default boolean sortsRHS(int from, int mid, int until) { return true; }
    }
    @PropertyDefaults(tries =  1000) @Group class Medium implements TestTemplate { @Override public int maxArraySize() {return 1_000;} }
    @PropertyDefaults(tries = 10000) @Group class Small  implements TestTemplate { @Override public int maxArraySize() {return   100;} }
  }
}
