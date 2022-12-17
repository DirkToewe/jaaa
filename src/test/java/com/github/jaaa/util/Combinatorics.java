package com.github.jaaa.util;

import java.util.Spliterator;
import java.util.function.Consumer;
import java.util.stream.Stream;

import static com.github.jaaa.permute.Swap.swap;
import static java.lang.Math.multiplyExact;
import static java.util.stream.StreamSupport.stream;


public class Combinatorics
{
// STATIC FIELDS
  private static final long[] factorials = new long[21];
// STATIC CONSTRUCTOR
  static {
    factorials[0] = 1;
    for( int i=0; ++i < factorials.length; )
        factorials[i] = multiplyExact(i, factorials[i-1]);
  }
// STATIC METHODS
  public static long factorial( int n ) {
    if( n < 0 ) throw new IllegalArgumentException();
    if( n >= factorials.length ) throw new ArithmeticException();
    return   factorials[n];
  }

//  private static class PermutationSpliterator implements Spliterator<int[]>
//  {
//  // STATIC FIELDS
//    private static final int CHARACTERISTICS = DISTINCT | IMMUTABLE | NONNULL | SIZED | SUBSIZED;
//  // STATIC CONSTRUCTOR
//  // STATIC METHODS
//  // FIELDS
//    private long size;
//    protected final int[] perm;
//
//  // CONSTRUCTORS
//    public  PermutationSpliterator( int n ) { this( factorial(n), new int[n] ); }
//    private PermutationSpliterator( long _size, int[] _perm ) {
//      size =_size;
//      perm =_perm;
//    }
//  // METHODS
//    @Override public boolean tryAdvance( Consumer<? super int[]> action ) {
//      assert size >= 0;
//      if( size <= 0 ) return false;
//
//      var next = new int[perm.length];
//      for( int i=0; i < perm.length; i++ ) {
//        next[i] = i;
//        swap(next, i,perm[i]);
//      }
//
//      if( --size > 0 )
//        for( int i=0; ++perm[i] > i; )
//          perm[i++] = 0;
//
//      action.accept(next);
//      return true;
//    }
//
//    @Override public Spliterator<int[]> trySplit() {
//      if( size <= 1 )
//        return null;
//      long half = size >>> 1;
//      var result = new PermutationSpliterator(half, perm.clone());
//
//      size -= half;
//      for( int i=0; 0 < half; ) {
//        int j = i+1;
//        perm[i] += half %  j;
//                   half /= j;
//        if(perm[i] >= j) {
//           perm[i] -= j;
//           half++;
//        }
//        i = j;
//      }
//
//      return result;
//    }
//
//    @Override public long        estimateSize() { return size; }
//    @Override public long getExactSizeIfKnown() { return size; }
//    @Override public int characteristics() { return CHARACTERISTICS; }
//  }

  private static class PermutationSpliterator implements Spliterator<byte[]>
  {
  // STATIC FIELDS
    private static final int CHARACTERISTICS = DISTINCT | IMMUTABLE | NONNULL | SIZED | SUBSIZED;
  // STATIC CONSTRUCTOR
  // STATIC METHODS
  // FIELDS
    private final int n;
    private long off, size;

  // CONSTRUCTORS
    public  PermutationSpliterator( int _n ) { this( _n, 0, factorial(_n) ); }
    private PermutationSpliterator( int _n, long _off, long _size ) {
      n =_n;
      off  =_off;
      size =_size;
    }
  // METHODS
    @Override public boolean tryAdvance( Consumer<? super byte[]> action ) {
      assert size >= 0;
      if( size <= 0 ) return false;
      --size;

      byte[] next = new byte[n];
      long rem = off++;
      for( byte i=0; i < next.length; ) {
             next[i] = i;
        swap(next,i++, (int) (rem % i));
                              rem /=i;
      }

      action.accept(next);
      return true;
    }

    @Override public void forEachRemaining( Consumer<? super byte[]> action )
    {
      assert size >= 0;
      if( size <= 0 ) return;

      byte[] perm = new byte[n];
      long rem = off++;
      for( byte i=0; rem > 0; ) {
        byte j = i++;
        perm[j] += rem %  i;
                   rem /= i;
        if(perm[j] >= i) {
           perm[j] -= i;
           rem++;
        }
      }

      for(;;)
      {
        byte[] next = new byte[n];
        for( byte i=0; i < perm.length; i++ ) {
          next[i] = i;
          swap(next, i,perm[i]);
        }
        action.accept(next);

        if( --size <= 0 )
          break;

        for( byte i=0; ++perm[i] > i; )
          perm[i++] = 0;
      }
    }

    @Override public Spliterator<byte[]> trySplit() {
      if( size <= 1 )
        return null;

      long half = size>>>1;
      PermutationSpliterator result = new PermutationSpliterator(n,off,half);
      off  += half;
      size -= half;
      return result;
    }

    @Override public long        estimateSize() { return size; }
    @Override public long getExactSizeIfKnown() { return size; }
    @Override public int characteristics() { return CHARACTERISTICS; }
  }

  public static Stream<byte[]> permutations(int n ) {
    return stream(new PermutationSpliterator(n), false);
  }
}
