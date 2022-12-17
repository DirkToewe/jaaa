package com.github.jaaa;

import com.github.jaaa.util.CombinatoricsTest;
import net.jqwik.api.Example;

import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;


public class ArrayProviderTemplateTest
{
  @Example void test_arraysByte_smallPermutations()
  {
    ArrayProviderTemplate tmpl = () -> { throw new AssertionError(); };
    Map<Integer,List<byte[]>> groups = tmpl.arraysByte_smallPermutations().allValues().get().collect( groupingBy(x -> x.length, toList() ) );
    groups.forEach(CombinatoricsTest::test_permutations);
  }
}
