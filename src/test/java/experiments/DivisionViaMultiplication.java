package experiments;

import java.util.function.IntUnaryOperator;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static java.lang.Integer.numberOfLeadingZeros;
import static java.lang.Integer.toUnsignedLong;
import static java.lang.Integer.divideUnsigned;
import static java.lang.Math.*;
import static java.lang.String.format;
import static java.util.stream.IntStream.rangeClosed;


public class DivisionViaMultiplication
{
  public record GCDX( long gcd, long bezoutL, long bezoutR ) {}

  public static GCDX gcdx( long a, long b )
  {
    if( a == 0 && b == 0 ) return new GCDX(0,0,0);
    if( a <  0 || b <  0 ) throw new IllegalArgumentException();
    // https://en.wikipedia.org/wiki/Extended_Euclidean_algorithm
    long r = a,  rNew = b,
         s = 1L, sNew = 0L,
         t = 0L, tNew = 1L;
    while( rNew != 0 ) {
      var             q = r / rNew;
      var rNext = r - q*rNew; r = rNew; rNew = rNext;
      var sNext = s - q*sNew; s = sNew; sNew = sNext;
      var tNext = t - q*tNew; t = tNew; tNew = tNext;
    }
    return new GCDX(r,s,t);
  }

  public static int modInv(int x ) {
    if( x%2 == 0 )
      throw new ArithmeticException();
    var gcdx = gcdx(x, 1L<<32);
    assert gcdx.gcd == 1;
    return (int) gcdx.bezoutL;
  }

  public static IntUnaryOperator u32_divBy( int d ) {
    // References
    // ----------
    //   ..[1] "Division by Invariant Integers using Multiplication"
    //          Torbjörn Granlund & Peter L. Montgomery
    //          https://gmplib.org/~tege/divcnst-pldi94.pdf
    final int m, sh1, sh2; {
      int l = 32 - numberOfLeadingZeros(d-1);
      sh1 = min(l,1);
      sh2 = max(l-1, 0);
      long c = toUnsignedLong(d);
      m = (int) Long.divideUnsigned((1L<<l) - c << 32, c) + 1;
    }
    return n -> {
      int    t1 = (int) (toUnsignedLong(n) * toUnsignedLong(m) >>> 32);
      return t1 + (n-t1 >>> sh1) >>> sh2;
    };
  }

  public static IntUnaryOperator i32_divBy( int d ) {
    final int dSign, m, sh1, sh2; {
      dSign = d >> 31; // <- dSign = d<0 ? -1 : 0
      d = (d^dSign) - dSign; // <- d = abs(d)
      int l = 32 - numberOfLeadingZeros(d-1);
      sh1 = min(l,1);
      sh2 = max(l-1, 0);
      long c = toUnsignedLong(d);
      m = (int) Long.divideUnsigned((1L<<l) - c << 32, c) + 1;
    }
    return n -> {
      // determine result sign
      int s = n >> 31; // <- s = n<0 ? -1 : 0;
      n = (n^s) - s; // <- n = abs(n);
      s ^= dSign;
      // perform unsigned division
      int t1 = (int) (toUnsignedLong(n) * toUnsignedLong(m) >>> 32);
      t1 += n-t1 >>> sh1;
      t1 >>>= sh2;
      // apply sign to result
      return (t1^s) - s;
    };
  }

  public static void main( String... args ) {
    Stream.<Runnable>of(
      DivisionViaMultiplication::testDivU32,
      DivisionViaMultiplication::testDivI32,
      DivisionViaMultiplication::testModInv
    ).parallel().forEach(Runnable::run);
  }

  private static void testDivI32() {
    IntStream.rangeClosed(1, Integer.MAX_VALUE).flatMap( d -> IntStream.of(-d,+d) ).forEach( d -> {
      var divByD = i32_divBy(d);

      rangeClosed(Integer.MIN_VALUE, Integer.MAX_VALUE).parallel().forEach( n -> {
        int tst = divByD.applyAsInt(n),
            ref = n / d;
        if( tst != ref )
          throw new AssertionError( tst + "!=" + ref + format("{ n: %d, d: %d }",n,d) );
      });

      System.out.printf("tested i32 {d: %+d}...\n", d);
    });
  }

  private static void testDivU32() {
    IntStream.rangeClosed(1, Integer.MAX_VALUE).flatMap( d -> IntStream.of(-d,+d) ).forEach( d -> {
      var divByD = u32_divBy(d);

      rangeClosed(Integer.MIN_VALUE, Integer.MAX_VALUE).parallel().forEach( n -> {
        int tst = divByD.applyAsInt(n),
            ref = divideUnsigned(n,d);
        if( tst != ref )
          throw new AssertionError( tst + "!=" + ref + format("{ n: %d, d: %d }",n,d) );
      });

      System.out.printf("tested u32 {d: %+d}...\n", d);
    });
  }

  private static void testModInv() {
    IntStream.range(0, Integer.MAX_VALUE >>> 1).map(x -> x*2 + 1).forEach( x -> {
      int inv_x = modInv(x);

      rangeClosed(Integer.MIN_VALUE, Integer.MAX_VALUE).parallel().forEach( y -> {
        if( (y*x) * inv_x != y )
          throw new AssertionError();
      });

      System.out.printf("tested mod {d: %+d}...\n", x);
    });
  }
}
