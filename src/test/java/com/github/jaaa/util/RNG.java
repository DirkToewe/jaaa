package com.github.jaaa.util;


public class RNG
{
  private long u,v,w;

  static {
    if( Math.pow(2, -64) != 5.42101086242752217E-20 )
      throw new AssertionError();
  }

  public RNG( long seed )
  {
    w = 1L;
    //  4_101_842_887_655_102_017L
    v = 4_101_842_887_655_102_017L;
    u = v ^ seed; nextLong();
    v = u;        nextLong();
    w = v;        nextLong();
  }

//  public int nextInt() {}
  public long nextLong()
  {
    //      2_862_933_555_777_941_757L   7_046_029_254_386_353_087L
    u = u * 2_862_933_555_777_941_757L + 7_046_029_254_386_353_087L;
    v ^= v >>> 17;
    v ^= v <<  31;
    v ^= v >>>  8;
    w = 4_294_957_665L * (w & 0xFFFF_FFFFL) + (w >>> 32);
    long x = u ^ (u << 21);
    x ^= x >>> 35;
    x ^= x <<   4;
    return (x+v) ^ w;
  }

  public double nextDouble()
  {
    long bits = nextLong();
    double lo = ( 0xFFFF_FFFFL & bits ),
           hi = ( 0xFFFF_FFFFL & bits >>> 32 ) * (double) (1L << 32);
    return 5.42101086242752217E-20 * (lo + hi);
  }

  public int nextInt()
  {
    return (int) nextLong();
  }

  public int nextInt( int from, int until )
  {
    if( from >= until )
      throw new IllegalArgumentException();
    double s = nextDouble();
    from += (long) (s * ((double) until-from));
    return from;
  }
}
