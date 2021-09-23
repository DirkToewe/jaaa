package com.github.jaaa.util;

public class Hashing
{
  // https://stackoverflow.com/questions/664014/what-integer-hash-function-are-good-that-accepts-an-integer-hash-key
  public static int hash( int x )
  {
    x = (x>>>16 ^ x) * 0x45d9f3b;
    x = (x>>>16 ^ x) * 0x45d9f3b;
    x =  x>>>16 ^ x;
    return x;
  }

  public static int unhash( int x )
  {
    x = (x>>>16 ^ x) * 0x119de1f3;
    x = (x>>>16 ^ x) * 0x119de1f3;
    x =  x>>>16 ^ x;
    return x;
  }
}
