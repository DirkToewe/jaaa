package com.github.jaaa.partition;

public class MuRaSaBiPartitionV2Test extends BiPartitionTestTemplate
{
  MuRaSaBiPartitionV2Test() { super(MuRaSaBiPartitionV2.class); }

//  private static int blockSize( int len )
//  {
//    int n = sqrtFloor(4 + len) - 2, // <- find largest n such that: `(4+n)*n = len`
//        m = n+1,  lg2 = m<2 ? 1 : log2Ceil(m),
//        s = n/lg2*lg2;
//    return 2*s + (2+m)*m <= len ? m : n;
//  }
//
//  @Property( tries = Integer.MAX_VALUE )
//  void testBlockSize( @ForAll @IntRange(min=0, max=Integer.MAX_VALUE>>>1) int len )
//  {
//    IntUnaryOperator lenFn = n -> {
//      int lg2 = n<2 ? 1 : log2Ceil(n),
//            s = (n-1)/lg2*lg2;
//      return 2*s + (2+n)*n;
//    };
//
//    int n = blockSize(len);
//
//    assertThat( lenFn.applyAsInt(n  ) ).isLessThanOrEqualTo( len );
//    assertThat( lenFn.applyAsInt(n+1) ).isGreaterThan      ( len );
//  }
}
