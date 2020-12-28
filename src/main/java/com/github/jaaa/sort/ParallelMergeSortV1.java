package com.github.jaaa.sort;

import java.lang.reflect.Array;
import java.util.Comparator;

import static com.github.jaaa.util.IMath.log2Ceil;
import static java.lang.Math.max;
import static java.lang.Math.min;
import static java.lang.Math.subtractExact;
import static java.util.Comparator.naturalOrder;
import static java.util.stream.IntStream.range;
import static java.lang.Runtime.getRuntime;
import static java.lang.System.arraycopy;

public class ParallelMergeSortV1
{
  public static <T extends Comparable<? super T>> void sort( T[] seq                                                 ) { sort( seq, 0,seq.length, naturalOrder()); }
  public static <T extends Comparable<? super T>> void sort( T[] seq, int from, int until                            ) { sort( seq,  from, until, naturalOrder()); }
  public static <T>                               void sort( T[] seq,                      Comparator<? super T> cmp ) { sort( seq, 0,seq.length, cmp ); }
  public static <T>                               void sort( T[] seq, int from, int until, Comparator<? super T> cmp ) {
    int len = subtractExact(until,from);
    if( len <= 1 ) return;

    int H = log2Ceil(len);

    T[] buf = (T[]) Array.newInstance(seq.getClass().getComponentType(), len); // <- FIXME

    for( int h=0; h++ < H; ) // <- TODO: below a certain threshold, sorting networks might be better
    {
      final int L = 1<<h;
                   T[] A,     B; int a,      b;
      if( 1 == h%2 ) { A=seq; B=buf; a=from; b=0; }
      else           { B=seq; A=buf; b=from; a=0; }

      range(0,len).forEach( k -> {
        final int                       skip = k%L,
                           off = a + (k-skip),
          lenL = min(a+len-off, L/2),
                           mid = lenL + off,
          lenR = min(a+len-mid, L/2);

        int lo = max(skip-lenR, 0),
            hi = min(skip,lenL);
        while( lo < hi ) {
          int l = lo+hi >>> 1,
              r = skip - l;
               if( 0 < r && l < lenL && cmp.compare(A[off+l  ], A[mid+r-1]) <= 0 ) lo = l+1;
          else if( 0 < l && r < lenR && cmp.compare(A[off+l-1], A[mid+r  ]) >  0 ) hi = l-1;
          else lo=hi=l;
        }
        int l = lo,
            r = skip-l;

        B[b+k] = r==lenR || l < lenL && cmp.compare(A[off+l],
                                                    A[mid+r]) <= 0
          ? A[off+l]
          : A[mid+r];
      });
    }

    if( 1 == H%2 )
      arraycopy(buf,0, seq,from, len);
  }
}
