package com.github.jaaa.select;

import com.github.jaaa.compare.CompareRandomAccessor;
import net.jqwik.api.Group;
import net.jqwik.api.PropertyDefaults;


@Group
public class InsertionSelectAccessTest
{
  @Group
  public class InsertionSelect {
    private abstract class TestTemplate implements SelectAccessorTestTemplate {
      @Override public boolean isStable() { return false; }
      @Override public boolean sortsLHS(int from, int mid, int until) { return mid-from <= until-mid-2; }
      @Override public boolean sortsRHS(int from, int mid, int until) { return mid-from >  until-mid-2; }
      @Override public <T> SelectAccessor<T> createAccessor( CompareRandomAccessor<T> acc ) {
        return (arr, from, mid, until) ->
          new InsertionSelectAccess() {
            @Override public void   swap( int i, int j ) {        acc.   swap(arr,i, arr,j); }
            @Override public int compare( int i, int j ) { return acc.compare(arr,i, arr,j); }
          }.insertionSelect(from,mid,until);
      }
    }
    @PropertyDefaults(tries =  1000) @Group class Medium extends TestTemplate { @Override public int maxArraySize() {return    10_000;} }
    @PropertyDefaults(tries = 10000) @Group class Small  extends TestTemplate { @Override public int maxArraySize() {return       100;} }
  }
  @Group
  public class InsertionSelectL {
    private abstract class TestTemplate implements SelectAccessorTestTemplate {
      @Override public boolean isStable() { return false; }
      @Override public boolean sortsLHS(int from, int mid, int until) { return true; }
      @Override public boolean sortsRHS(int from, int mid, int until) { return false; }
      @Override public <T> SelectAccessor<T> createAccessor( CompareRandomAccessor<T> acc ) {
        return (arr, from, mid, until) ->
          new InsertionSelectAccess() {
            @Override public void   swap( int i, int j ) {        acc.   swap(arr,i, arr,j); }
            @Override public int compare( int i, int j ) { return acc.compare(arr,i, arr,j); }
          }.insertionSelectL(from,mid,until);
      }
    }
    @PropertyDefaults(tries =  1000) @Group class Medium extends TestTemplate { @Override public int maxArraySize() {return    10_000;} }
    @PropertyDefaults(tries = 10000) @Group class Small  extends TestTemplate { @Override public int maxArraySize() {return       100;} }
  }
  @Group
  public class InsertionSelectR {
    private abstract class TestTemplate implements SelectAccessorTestTemplate {
      @Override public boolean isStable() { return false; }
      @Override public boolean sortsLHS(int from, int mid, int until) { return false; }
      @Override public boolean sortsRHS(int from, int mid, int until) { return true; }
      @Override public <T> SelectAccessor<T> createAccessor( CompareRandomAccessor<T> acc ) {
        return (arr, from, mid, until) ->
          new InsertionSelectAccess() {
            @Override public void   swap( int i, int j ) {        acc.   swap(arr,i, arr,j); }
            @Override public int compare( int i, int j ) { return acc.compare(arr,i, arr,j); }
          }.insertionSelectR(from,mid,until);
      }
    }
    @PropertyDefaults(tries =  1000) @Group class Medium extends TestTemplate { @Override public int maxArraySize() {return    10_000;} }
    @PropertyDefaults(tries = 10000) @Group class Small  extends TestTemplate { @Override public int maxArraySize() {return       100;} }
  }
}
