package com.github.jaaa.select;

import com.github.jaaa.CompareRandomAccessor;
import com.github.jaaa.Swap;
import net.jqwik.api.Example;
import net.jqwik.api.Group;
import net.jqwik.api.PropertyDefaults;

import java.util.Arrays;

import static com.github.jaaa.util.Combinatorics.permutations;
import static org.assertj.core.api.Assertions.assertThat;


public class Mom5SelectV2AccessTest
{
  private void momSelectV2_select5_test( int a, int c, int d ) {
    permutations(5).forEach( ref -> {
      var tst = ref.clone();
      new Mom5SelectV2Access() {
        @Override public void   swap( int i, int j ) { Swap.swap(tst,i,j); }
        @Override public int compare( int i, int j ) { return Integer.compare(tst[i], tst[j]); }
      }.mom5SelectV2_median5(a,c,d);
      Arrays.sort(tst,a,a+2);
      Arrays.sort(tst,d,d+2);
      Arrays.sort(ref);
      assertThat(tst[a  ]).isEqualTo(ref[0]);
      assertThat(tst[a+1]).isEqualTo(ref[1]);
      assertThat(tst[c  ]).isEqualTo(ref[2]);
      assertThat(tst[d  ]).isEqualTo(ref[3]);
      assertThat(tst[d+1]).isEqualTo(ref[4]);
    });
  }
  @Example public void momSelectV2_select5_test1() { momSelectV2_select5_test(0,2,3); }
  @Example public void momSelectV2_select5_test2() { momSelectV2_select5_test(3,2,0); }
  @Example public void momSelectV2_select5_test3() { momSelectV2_select5_test(1,0,3); }
  @Example public void momSelectV2_select5_test4() { momSelectV2_select5_test(3,0,1); }
  @Example public void momSelectV2_select5_test5() { momSelectV2_select5_test(0,4,2); }
  @Example public void momSelectV2_select5_test6() { momSelectV2_select5_test(2,4,0); }

  private interface TestTemplate extends SelectAccessorTestTemplate {
    @Override default <T> SelectAccessor<T> createAccessor( CompareRandomAccessor<T> acc ) {
      return (arr, from, mid, until) ->
        new Mom5SelectV2Access() {
          @Override public void   swap( int i, int j ) {        acc.   swap(arr,i, arr,j); }
          @Override public int compare( int i, int j ) { return acc.compare(arr,i, arr,j); }
        }.mom5SelectV2(from,mid,until);
    }
    @Override default boolean isStable() { return false; }
  }
  @PropertyDefaults(tries =   100) @Group class Large  implements TestTemplate { @Override public int maxArraySize() {return 1_000_000;} }
  @PropertyDefaults(tries =  1000) @Group class Medium implements TestTemplate { @Override public int maxArraySize() {return    10_000;} }
  @PropertyDefaults(tries = 10000) @Group class Small  implements TestTemplate { @Override public int maxArraySize() {return       100;} }
}
