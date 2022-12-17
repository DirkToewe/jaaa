package com.github.jaaa.merge.datagen;

import com.github.jaaa.merge.TapeMergeAccess;
import com.github.jaaa.permute.Swap;
import net.jqwik.api.ForAll;
import net.jqwik.api.Property;
import net.jqwik.api.Tuple.Tuple2;
import net.jqwik.api.constraints.IntRange;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.SplittableRandom;

import static com.github.jaaa.Boxing.boxed;
import static com.github.jaaa.permute.Revert.revert;
import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;


public class MergeInputsOfLengthTest
{
  @Property void produces_unique_samples( @ForAll @IntRange(min=0, max=14) int len )
  {
    MergeInputsOfLength lst = new MergeInputsOfLength(len);
    HashSet<MergeInput<byte[]>> set = new HashSet<>(lst);
    assertThat( lst.size() ).isEqualTo( set.size() );
  }

  @Property void produces_unique_merges( @ForAll @IntRange(min=0, max=14) int len )
  {
    MergeInputsOfLength lst = new MergeInputsOfLength(len);

    assertThat(lst).map( sample -> {
      byte[] arr = sample.array();
      int mid = sample.mid,
         from = sample.from,
        until = sample.until;

      for( int i=mid; i < until; i++ )
        arr[i] ^= -1;

      new TapeMergeAccess() {
        @Override public void   swap( int i, int j ) { Swap.swap(arr, i,j); }
        @Override public int compare( int i, int j ) {
          byte x = arr[i]; if( x < 0 ) x ^= -1;
          byte y = arr[j]; if( y < 0 ) y ^= -1;
          return Byte.compare(x,y);
        }
      }.tapeMerge(from,mid,until);

      return asList( boxed(arr) );
    }).doesNotHaveDuplicates();
  }

  @Property void matches_recursive_generation( @ForAll @IntRange(min=1, max=14) int len  )
  {
    HashSet<MergeInput<byte[]>> set = new HashSet<>( new MergeInputsOfLength(len) );

    new Object()
    {
      private final int[] sample = new int[len];
      private void gen( boolean wasB, int l, int r )
      {
        if( l >r ) throw new IllegalStateException();
        if( l==r ) {
          byte[] result = new byte[len];
          for( int i=len; i-- > 0; )
            result[i] = (byte) sample[i];
          revert(result, l,len);
          assertThat( MergeInput.of(result, 0,l,len) ).satisfies( x -> assertThat( set.remove(x) ).isTrue() );
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
    SplittableRandom rand = new SplittableRandom();
    RandomMergeInputGenerator rng = new RandomMergeInputGenerator(rand);
    HashSet<MergeInput<byte[]>> set = new HashSet<>( new MergeInputsOfLength(len) );

    for( int run=0; run++ < 1024; )
    {
      int mid = rand.nextInt(len+1);
      byte[] arr = new byte[len];
      ;{
        Tuple2<int[],int[]> ab = rng.next(mid, len - mid);
        int[] a = ab.get1(),
              b = ab.get2();
        for( int i=0;   i < mid; i++ ) arr[i] = (byte) a[i];
        for( int i=mid; i < len; i++ ) arr[i] = (byte) b[i-mid];
      }
      MergeInput<byte[]> sample = MergeInput.of(arr, 0,mid,len);

      assertThat(sample).isIn(set);
    }
  }

  @Property void equals_itself( @ForAll @IntRange(min=0, max=14) int len )
  {
    List<MergeInput<byte[]>> tst = new MergeInputsOfLength(len);
    assertThat(tst).isEqualTo(tst);
  }

  @Property void equals_own(
    @ForAll @IntRange(min=0, max=14) int lenA,
    @ForAll @IntRange(min=0, max=14) int lenB
  ) {
    List<MergeInput<byte[]>>
      tst = new MergeInputsOfLength(lenA),
      ref = new MergeInputsOfLength(lenB);
    if( lenA == lenB )
      assertThat(tst).isEqualTo(ref);
    else
      assertThat(tst).isNotEqualTo(ref);
  }

  @Property void equals_listCopy( @ForAll @IntRange(min=0, max=14) int len )
  {
    List<MergeInput<byte[]>>
      tst = new MergeInputsOfLength(len),
      ref = new ArrayList<>(tst);
    assertThat(tst).isEqualTo(ref);
    assertThat(ref).isEqualTo(tst);
  }

  @Property void hashCode_isConsistent(
    @ForAll @IntRange(min=0, max=14) int lenA,
    @ForAll @IntRange(min=0, max=14) int lenB
  ) {
    List<MergeInput<byte[]>>
      tst = new MergeInputsOfLength(lenA),
      ref = new MergeInputsOfLength(lenB);
    if( lenA == lenB )
      assertThat( tst.hashCode() ).isEqualTo( ref.hashCode() );
    else
      assertThat( tst.hashCode() ).isNotEqualTo( ref.hashCode() );
  }

  @Property void hashCode_isCorrect( @ForAll @IntRange(min=0, max=14) int len )
  {
    List<MergeInput<byte[]>>
      tst = new MergeInputsOfLength(len),
      ref = new ArrayList<>(tst);
    assertThat( tst.hashCode() ).isEqualTo( ref.hashCode() );
    assertThat( ref.hashCode() ).isEqualTo( tst.hashCode() );
  }
}
