package com.github.jaaa.partition;

import net.jqwik.api.ForAll;
import net.jqwik.api.Property;
import net.jqwik.api.constraints.IntRange;

import static java.lang.Math.sqrt;
import static org.assertj.core.api.Assertions.assertThat;

public class KatPawBiPartitionV2Test extends BiPartitionTestTemplate
{
  public KatPawBiPartitionV2Test() { super(KatPawBiPartitionV2.class); }

  @Property( tries=100_000 )
  void testBlockSize( @ForAll @IntRange(min=0) int len )
  {
    int      B = 16*16,
            blockLen = (int) ( ( sqrt(1d*len*B + 4) - 2 ) / B ),
            blockNum = blockLen + (len - blockLen*blockLen*B - 4*blockLen) / (B*blockLen+4);

    assert blockNum >= blockLen;
    assertThat( len -  blockNum   *blockLen*B ).isGreaterThanOrEqualTo(4*blockNum);
    assertThat( len - (blockNum+1)*blockLen*B ).isLessThan( 4*(blockNum+1) );
  }
}
