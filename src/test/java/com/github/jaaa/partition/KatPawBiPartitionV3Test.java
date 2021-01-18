package com.github.jaaa.partition;

import com.github.jaaa.PredicateSwapAccess;
import com.github.jaaa.Swap;
import net.jqwik.api.Example;
import net.jqwik.api.ForAll;
import net.jqwik.api.Property;
import net.jqwik.api.ShrinkingMode;
import net.jqwik.api.constraints.IntRange;
import net.jqwik.api.constraints.Size;

import java.util.Arrays;

import static java.lang.Math.sqrt;
import static java.util.stream.IntStream.range;
import static org.assertj.core.api.Assertions.assertThat;

public class KatPawBiPartitionV3Test extends BiPartitionTestTemplate
{
  public KatPawBiPartitionV3Test() { super(KatPawBiPartitionV3.class); }

  @Property( tries=1_000_000 )
  void testBlockSize( @ForAll @IntRange(min=0) int len )
  {
    int      B = 16*16,
      blockLen = (int) ( ( sqrt(1d*len*B + 4) - 2 ) / B ),
      blockNum = blockLen + (len - blockLen*blockLen*B - 4*blockLen) / (B*blockLen+4);

    assertThat(blockNum).isGreaterThanOrEqualTo(blockLen);
    assertThat( len -  blockNum   *blockLen*B ).isGreaterThanOrEqualTo(4*blockNum);
    assertThat( len - (blockNum+1)*blockLen*B ).isLessThan( 4*(blockNum+1) );
  }
}
