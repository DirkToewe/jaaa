package com.github.jaaa.merge;


public class MergeDataProviderTemplateExperiments
{
  static {
    var    ea = false;
    assert ea = true;
      if( !ea ) throw new AssertionError();
  }

  public static void main( String... args )
  {
    new MergeInputsOfLength(3).forEach(System.out::println);
  }
}
