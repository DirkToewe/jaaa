package com.github.jaaa.util;

import net.jqwik.api.*;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;
import static java.util.stream.Collectors.toSet;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountedCompleter;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import static java.util.Arrays.stream;

import static org.assertj.core.api.Assertions.assertThat;

public class TypesTest
{
  private static final int N_TRIES = 100_000;

  @Provide
  Arbitrary<Class<?>> handPickedClasses()
  {
    Set<Class<?>> types = Set.of(
      Integer.class,
      String.class,
      ByteArrayOutputStream.class,
      LinkedList.class,
      IntStream.class,
      CountedCompleter.class,
      CompletableFuture.class,
      FileNotFoundException.class
    );

    for(;;)
    {
      Set<Class<?>> moreTypes = types.stream().flatMap(
        clazz -> Stream.<Class<?>>iterate(clazz, cl -> cl != null, Class::getSuperclass)
      ).flatMap(
        clazz -> Stream.of( Stream.of(clazz), stream( clazz.getInterfaces() ), stream( clazz.getClasses() ) ).flatMap(x->x)
      ).collect( toSet() );

      if( moreTypes.equals(types) )
        break;

      types = moreTypes;
    }

    System.out.println( types.size() );

    return Arbitraries.of(types);
  }

  @Property( tries = N_TRIES )
  void revertsArraysByte1(
    @ForAll("handPickedClasses") Class<?> classA,
    @ForAll("handPickedClasses") Class<?> classB
  )
  {
    var superClass = Types.superClass(classA,classB);
    assertThat(superClass).isAssignableFrom(classA);
    assertThat(superClass).isAssignableFrom(classB);
  }
}
