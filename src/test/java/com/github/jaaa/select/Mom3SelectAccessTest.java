package com.github.jaaa.select;

import com.github.jaaa.compare.CompareRandomAccessor;
import com.github.jaaa.permute.Swap;
import net.jqwik.api.Example;
import net.jqwik.api.Group;
import net.jqwik.api.PropertyDefaults;

import java.util.Arrays;

import static com.github.jaaa.util.Combinatorics.permutations;
import static org.assertj.core.api.Assertions.assertThat;


@Group
public class Mom3SelectAccessTest
{
  private void momSelect_select3_test_impl( int a, int b, int c ) {
    permutations(3).forEach( ref -> {
      var tst = ref.clone();
      new Mom3SelectAccess() {
        @Override public void   swap( int i, int j ) { Swap.swap(tst,i,j); }
        @Override public int compare( int i, int j ) { return Integer.compare(tst[i], tst[j]); }
      }.mom3Select_median3(a,b,c);
      Arrays.sort(ref);
      assertThat(tst[a]).isEqualTo(ref[0]);
      assertThat(tst[b]).isEqualTo(ref[1]);
      assertThat(tst[c]).isEqualTo(ref[2]);
    });
  }
  @Example public void momSelect_select3_test() {
    momSelect_select3_test_impl(0,1,2);
    momSelect_select3_test_impl(0,2,1);
    momSelect_select3_test_impl(1,0,2);
    momSelect_select3_test_impl(1,2,0);
    momSelect_select3_test_impl(2,0,1);
    momSelect_select3_test_impl(2,1,0);
  }

  private interface TestTemplate extends SelectAccessorTestTemplate {
    @Override default <T> SelectAccessor<T> createAccessor( CompareRandomAccessor<T> acc ) {
      return (arr, from, mid, until) ->
        new Mom3SelectAccess() {
          @Override public void   swap( int i, int j ) {        acc.   swap(arr,i, arr,j); }
          @Override public int compare( int i, int j ) { return acc.compare(arr,i, arr,j); }
        }.mom3Select(from,mid,until);
    }
    @Override default boolean isStable() { return false; }
  }
  @PropertyDefaults(tries =   100) @Group class Large  implements TestTemplate { @Override public int maxArraySize() {return 1_000_000;} }
  @PropertyDefaults(tries =  1000) @Group class Medium implements TestTemplate { @Override public int maxArraySize() {return    10_000;} }
  @PropertyDefaults(tries = 10000) @Group class Small  implements TestTemplate { @Override public int maxArraySize() {return       100;} }
}
