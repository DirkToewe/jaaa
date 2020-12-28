package com.github.jaaa.misc;

import net.jqwik.api.ForAll;
import net.jqwik.api.Property;
import net.jqwik.api.constraints.Size;

import java.util.Random;

import static org.assertj.core.api.Assertions.assertThat;

public class ParallelCopyTest
{
  private static final int NORMAL_TRIES =    100_000,
                           NORMAL_SIZE  =    100_000,
                           LARGE_TRIES  =      1_000,
                           LARGE_SIZE   = 10_000_000;

  @Property( tries = LARGE_TRIES )
  void copiesArraysObjIntegerLarge( @ForAll @Size(min=0, max=LARGE_SIZE) Integer[] srcRef )
  {
    copiesArraysObjInteger(srcRef);
  }

  @Property( tries = NORMAL_TRIES )
  void copiesArraysObjInteger( @ForAll @Size(min=0, max=NORMAL_SIZE) Integer[] srcRef )
  {
    Random rng = new Random();
    Integer[] dstRef = rng.ints().parallel().limit(srcRef.length).boxed().toArray(Integer[]::new),
              dstTest= rng.ints().parallel().limit(srcRef.length).boxed().toArray(Integer[]::new),
              srcTest= srcRef.clone();

          System.arraycopy(srcRef, 0, dstRef, 0,  srcRef.length);
    ParallelCopy.arraycopy(srcTest,0, dstTest,0, srcTest.length);

    assertThat(srcRef).isEqualTo(srcTest);
    assertThat(dstRef).isEqualTo(dstTest);
  }
}
