package com.github.jaaa;

import com.github.jaaa.util.CombinatoricsTest;
import net.jqwik.api.Example;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toUnmodifiableList;


public class ArrayProviderTemplateTest
{
  @Example void test_arraysByte_smallPermutations()
  {
    ArrayProviderTemplate tmpl = () -> { throw new AssertionError(); };
    var groups = tmpl.arraysByte_smallPermutations().allValues().get().collect( groupingBy( x -> x.length, toUnmodifiableList() ) );
    groups.forEach(CombinatoricsTest::test_permutations);
  }
}
