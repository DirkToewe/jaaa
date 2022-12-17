package com.github.jaaa.merge.datagen;

import net.jqwik.api.ForAll;
import net.jqwik.api.Property;
import net.jqwik.api.constraints.IntRange;

import java.util.ArrayList;
import java.util.List;

import static java.util.stream.IntStream.rangeClosed;
import static org.assertj.core.api.Assertions.assertThat;
import static java.util.stream.Collectors.toList;


public class MergeInputsUpToLengthTest
{
  @Property void concatenates_MergeInputsOfLength( @ForAll @IntRange(min=0, max=14) int len )
  {
    MergeInputsUpToLength    lst = new MergeInputsUpToLength(len);
    List<MergeInput<byte[]>> ref = rangeClosed(0,len).mapToObj(MergeInputsOfLength::new).flatMap(List::stream).collect(toList());
    assertThat(lst).isEqualTo(ref);
  }

  @Property void equals_itself( @ForAll @IntRange(min=0, max=14) int len )
  {
    List<MergeInput<byte[]>> tst = new MergeInputsUpToLength(len);
    assertThat(tst).isEqualTo(tst);
  }

  @Property void equals_own(
    @ForAll @IntRange(min=0, max=14) int lenA,
    @ForAll @IntRange(min=0, max=14) int lenB
  ) {
    List<MergeInput<byte[]>>
      tst = new MergeInputsUpToLength(lenA),
      ref = new MergeInputsUpToLength(lenB);
    if( lenA == lenB )
      assertThat(tst).isEqualTo(ref);
    else
      assertThat(tst).isNotEqualTo(ref);
  }

  @Property void equals_listCopy( @ForAll @IntRange(min=0, max=14) int len )
  {
    List<MergeInput<byte[]>>
      tst = new MergeInputsUpToLength(len),
      ref = new ArrayList<>(tst);
    assertThat(tst).isEqualTo(ref);
    assertThat(ref).isEqualTo(tst);
  }

  @Property void hashCode_isConsistent(
          @ForAll @IntRange(min=0, max=14) int lenA,
          @ForAll @IntRange(min=0, max=14) int lenB
  ) {
    List<MergeInput<byte[]>>
      tst = new MergeInputsUpToLength(lenA),
      ref = new MergeInputsUpToLength(lenB);
    if( lenA == lenB )
      assertThat( tst.hashCode() ).isEqualTo( ref.hashCode() );
    else
      assertThat( tst.hashCode() ).isNotEqualTo( ref.hashCode() );
  }

  @Property void hashCode_isCorrect( @ForAll @IntRange(min=0, max=14) int len )
  {
    List<MergeInput<byte[]>>
      tst = new MergeInputsUpToLength(len),
      ref = new ArrayList<>(tst);
    assertThat( tst.hashCode() ).isEqualTo( ref.hashCode() );
    assertThat( ref.hashCode() ).isEqualTo( tst.hashCode() );
  }
}
