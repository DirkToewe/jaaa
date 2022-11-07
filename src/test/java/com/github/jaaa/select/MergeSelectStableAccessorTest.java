package com.github.jaaa.select;

import com.github.jaaa.CompareRandomAccessor;
import net.jqwik.api.Group;
import net.jqwik.api.PropertyDefaults;


@Group
public class MergeSelectStableAccessorTest
{
  @Group
  public class MergeSelectStable {
    private abstract class TestTemplate implements SelectAccessorTestTemplate {
      @Override public boolean isStable() { return true; }
      @Override public boolean sortsLHS(int from, int mid, int until) { return mid-from <= until-mid; }
      @Override public boolean sortsRHS(int from, int mid, int until) { return mid-from >  until-mid; }
      @Override public <T> SelectAccessor<T> createAccessor( CompareRandomAccessor<T> acc ) {
        return (arr, from, mid, until) ->
          new MergeSelectStableAccessor<T>() {
            @Override public T malloc( int len ) { return acc.malloc(len); }
            @Override public void   swap( T a, int i, T b, int j ) { acc.swap(a,i, b,j); }
            @Override public void   copy( T a, int i, T b, int j ) { acc.copy(a,i, b,j); }
            @Override public int compare( T a, int i, T b, int j ) { return acc.compare(a,i, b,j); }
          }.mergeSelectStable(arr,from,mid,until, null,0,0);
      }
    }
    @PropertyDefaults(tries =   100) @Group class Large  extends TestTemplate { @Override public int maxArraySize() {return 1_000_000;} }
    @PropertyDefaults(tries =  1000) @Group class Medium extends TestTemplate { @Override public int maxArraySize() {return    10_000;} }
    @PropertyDefaults(tries = 10000) @Group class Small  extends TestTemplate { @Override public int maxArraySize() {return       100;} }
  }
  @Group
  public class MergeSelectStableL {
    private abstract class TestTemplate implements SelectAccessorTestTemplate {
      @Override public boolean isStable() { return true; }
      @Override public boolean sortsLHS(int from, int mid, int until) { return true; }
      @Override public boolean sortsRHS(int from, int mid, int until) { return false; }
      @Override public <T> SelectAccessor<T> createAccessor( CompareRandomAccessor<T> acc ) {
        return (arr, from, mid, until) ->
          new MergeSelectStableAccessor<T>() {
            @Override public T malloc( int len ) { return acc.malloc(len); }
            @Override public void   swap( T a, int i, T b, int j ) { acc.swap(a,i, b,j); }
            @Override public void   copy( T a, int i, T b, int j ) { acc.copy(a,i, b,j); }
            @Override public int compare( T a, int i, T b, int j ) { return acc.compare(a,i, b,j); }
          }.mergeSelectStableL(arr,from,mid,until, null,0,0);
      }
    }
    @PropertyDefaults(tries =   100) @Group class Large  extends TestTemplate { @Override public int maxArraySize() {return 1_000_000;} }
    @PropertyDefaults(tries =  1000) @Group class Medium extends TestTemplate { @Override public int maxArraySize() {return    10_000;} }
    @PropertyDefaults(tries = 10000) @Group class Small  extends TestTemplate { @Override public int maxArraySize() {return       100;} }
  }
  @Group
  public class MergeSelectStableR {
    private abstract class TestTemplate implements SelectAccessorTestTemplate {
      @Override public boolean isStable() { return true; }
      @Override public boolean sortsLHS(int from, int mid, int until) { return false; }
      @Override public boolean sortsRHS(int from, int mid, int until) { return true; }
      @Override public <T> SelectAccessor<T> createAccessor( CompareRandomAccessor<T> acc ) {
        return (arr, from, mid, until) ->
          new MergeSelectStableAccessor<T>() {
            @Override public T malloc( int len ) { return acc.malloc(len); }
            @Override public void   swap( T a, int i, T b, int j ) { acc.swap(a,i, b,j); }
            @Override public void   copy( T a, int i, T b, int j ) { acc.copy(a,i, b,j); }
            @Override public int compare( T a, int i, T b, int j ) { return acc.compare(a,i, b,j); }
          }.mergeSelectStableR(arr,from,mid,until, null,0,0);
      }
    }
    @PropertyDefaults(tries =   100) @Group class Large  extends TestTemplate { @Override public int maxArraySize() {return 1_000_000;} }
    @PropertyDefaults(tries =  1000) @Group class Medium extends TestTemplate { @Override public int maxArraySize() {return    10_000;} }
    @PropertyDefaults(tries = 10000) @Group class Small  extends TestTemplate { @Override public int maxArraySize() {return       100;} }
  }
}
