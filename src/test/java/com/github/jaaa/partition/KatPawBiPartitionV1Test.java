package com.github.jaaa.partition;

import net.jqwik.api.ForAll;
import net.jqwik.api.Property;
import net.jqwik.api.constraints.IntRange;

import static java.lang.Math.sqrt;
import static org.assertj.core.api.Assertions.assertThat;

public class KatPawBiPartitionV1Test extends BiPartitionTestTemplate
{
  public KatPawBiPartitionV1Test() { super(KatPawBiPartitionV1.class); }
}
