package com.github.jaaa.merge.datagen;

import net.jqwik.api.ForAll;
import net.jqwik.api.Property;
import net.jqwik.api.constraints.IntRange;

import java.util.List;

import static java.util.stream.IntStream.rangeClosed;
import static org.assertj.core.api.Assertions.assertThat;


public class MergeInputsUpToLengthTest
{
  @Property void concatenates_MergeInputsOfLength( @ForAll @IntRange(min=0, max=14) int len )
  {
    var lst = new MergeInputsUpToLength(len);
    var ref = rangeClosed(0,len).mapToObj(MergeInputsOfLength::new).flatMap(List::stream).toList();
    assertThat(lst).isEqualTo(ref);
  }
}
