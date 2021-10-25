package com.github.jaaa.merge;

import com.github.jaaa.Swap;
import net.jqwik.api.ForAll;
import net.jqwik.api.Property;
import net.jqwik.api.constraints.IntRange;

import java.util.HashSet;
import java.util.List;
import java.util.SplittableRandom;

import static com.github.jaaa.misc.Boxing.boxed;
import static com.github.jaaa.misc.Revert.revert;
import static org.assertj.core.api.Assertions.assertThat;
import static com.github.jaaa.misc.Concat.concat;


public class MergeInputsOfLengthTest
{
  @Property void produces_unique_samples( @ForAll @IntRange(min=0, max=14) int len )
  {
    var lst = new MergeInputsOfLength(len);
    var set = new HashSet<>(lst);
    assertThat( lst.size() ).isEqualTo( set.size() );
  }

  @Property void produces_unique_merges( @ForAll @IntRange(min=0, max=14) int len )
  {
    var lst = new MergeInputsOfLength(len);

    assertThat(lst).map( sample -> {
      var arr = sample.array();
      int mid = sample.mid(),
         from = sample.from(),
        until = sample.until();

      for( int i=mid; i < until; i++ )
        arr[i] ^= -1;

      new TapeMergeAccess() {
        @Override public void   swap( int i, int j ) { Swap.swap(arr, i,j); }
        @Override public int compare( int i, int j ) {
          var x = arr[i]; if( x < 0 ) x ^= -1;
          var y = arr[j]; if( y < 0 ) y ^= -1;
          return Byte.compare(x,y);
        }
      }.tapeMerge(from,mid,until);

      return List.of( boxed(arr) );
    }).doesNotHaveDuplicates();
  }

  @Property void matches_recursive_generation( @ForAll @IntRange(min=1, max=14) int len  )
  {
    var set = new HashSet<>( new MergeInputsOfLength(len) );

    new Object()
    {
      private final int[] sample = new int[len];
      private void gen( boolean wasB, int l, int r )
      {
        if( l >r ) throw new IllegalStateException();
        if( l==r ) {
          var result = new byte[len];
          for( int i=len; i-- > 0; )
            result[i] = (byte) sample[i];
          revert(result, l,len);
          assertThat( new MergeInput<>(result, 0,l,len) ).satisfies( x -> assertThat( set.remove(x) ).isTrue() );
          return;
        }
        int val = sample[ wasB ? r : l-1 ];
        if( ! wasB ) {
          sample[l]   = val;   gen(false, l+1, r);
        } sample[l]   = val+1; gen(false, l+1, r);
          sample[r-1] = val;   gen(true,  l,   r-1);
          sample[r-1] = val+1; gen(true,  l,   r-1);
      }

      {
        int l,r;
        l=0; r=len; sample[l++] = 0; gen(false, l,r);
        l=0; r=len; sample[--r] = 0; gen(true,  l,r);
      }
    };

    assertThat(set).isEmpty();
  }

  @Property void contains_random_examples( @ForAll @IntRange(min=0, max=14) int len )
  {
    var rand = new SplittableRandom();
    var rng = new RandomMergeInputGenerator(rand);
    var set = new HashSet<>( new MergeInputsOfLength(len) );

    for( int run=0; run++ < 1024; )
    {
      int mid = rand.nextInt(len+1);
      var arr = new byte[len];
      ;{
        var ab = rng.next(mid, len - mid);
        var a  = ab.get1();
        var  b = ab.get2();
        for( int i=0;   i < mid; i++ ) arr[i] = (byte) a[i];
        for( int i=mid; i < len; i++ ) arr[i] = (byte) b[i-mid];
      }
      var sample = new MergeInput<>(arr, 0,mid,len);

      assertThat(sample).isIn(set);
    }
  }
}
