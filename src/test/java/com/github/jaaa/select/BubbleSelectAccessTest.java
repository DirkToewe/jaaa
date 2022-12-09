package com.github.jaaa.select;

import com.github.jaaa.compare.CompareRandomAccessor;
import net.jqwik.api.Group;
import net.jqwik.api.PropertyDefaults;

public class BubbleSelectAccessTest
{
  @Group
  public class BubbleSelect {
    private abstract class TestTemplate implements SelectAccessorTestTemplate {
      @Override public boolean isStable() { return false; }
      @Override public boolean sortsLHS(int from, int mid, int until) { return mid-from <= until-mid-2; }
      @Override public boolean sortsRHS(int from, int mid, int until) { return mid-from >  until-mid-2; }
      @Override public <T> SelectAccessor<T> createAccessor( CompareRandomAccessor<T> acc ) {
        return (arr, from, mid, until) ->
          new BubbleSelectAccess() {
            @Override public void   swap( int i, int j ) {        acc.   swap(arr,i, arr,j); }
            @Override public int compare( int i, int j ) { return acc.compare(arr,i, arr,j); }
          }.bubbleSelect(from,mid,until);
      }
    }
    @PropertyDefaults(tries =  1000) @Group class Medium extends TestTemplate { @Override public int maxArraySize() {return 1_000;} }
    @PropertyDefaults(tries = 10000) @Group class Small  extends TestTemplate { @Override public int maxArraySize() {return   100;} }
  }
  @Group
  public class BubbleSelectL {
    private abstract class TestTemplate implements SelectAccessorTestTemplate {
      @Override public boolean isStable() { return false; }
      @Override public boolean sortsLHS(int from, int mid, int until) { return true; }
      @Override public boolean sortsRHS(int from, int mid, int until) { return false; }
      @Override public <T> SelectAccessor<T> createAccessor( CompareRandomAccessor<T> acc ) {
        return (arr, from, mid, until) ->
          new BubbleSelectAccess() {
            @Override public void   swap( int i, int j ) {        acc.   swap(arr,i, arr,j); }
            @Override public int compare( int i, int j ) { return acc.compare(arr,i, arr,j); }
          }.bubbleSelectL(from,mid,until);
      }
    }
    @PropertyDefaults(tries =  1000) @Group class Medium extends TestTemplate { @Override public int maxArraySize() {return 1_000;} }
    @PropertyDefaults(tries = 10000) @Group class Small  extends TestTemplate { @Override public int maxArraySize() {return   100;} }
  }
  @Group
  public class BubbleSelectR {
    private abstract class TestTemplate implements SelectAccessorTestTemplate {
      @Override public boolean isStable() { return false; }
      @Override public boolean sortsLHS(int from, int mid, int until) { return false; }
      @Override public boolean sortsRHS(int from, int mid, int until) { return true; }
      @Override public <T> SelectAccessor<T> createAccessor( CompareRandomAccessor<T> acc ) {
        return (arr, from, mid, until) ->
          new BubbleSelectAccess() {
            @Override public void   swap( int i, int j ) {        acc.   swap(arr,i, arr,j); }
            @Override public int compare( int i, int j ) { return acc.compare(arr,i, arr,j); }
          }.bubbleSelectR(from,mid,until);
      }
    }
    @PropertyDefaults(tries =  1000) @Group class Medium extends TestTemplate { @Override public int maxArraySize() {return 1_000;} }
    @PropertyDefaults(tries = 10000) @Group class Small  extends TestTemplate { @Override public int maxArraySize() {return   100;} }
  }
}
