package com.github.jaaa.examples;

import com.github.jaaa.merge.KiwiMergeAccess;
import com.github.jaaa.permute.Swap;
import java.util.Arrays;

public class Example4 {
  public static void main( String... args ) {
    int[] a = { 0, 2, 12, 15, 17, 18, 19, 19, 22 },
          b = { 0, 2, 12, 14, 16, 17, 22 };
    int[] merged = new int[a.length + b.length];
    System.arraycopy(a,0, merged,0,        a.length);
    System.arraycopy(b,0, merged,a.length, b.length);
    new KiwiMergeAccess() {
      @Override public void swap( int i, int j ) { Swap.swap(merged,i,j); }
      @Override public int compare( int i, int j ) { return Integer.compare(merged[i], merged[j]); }
    }.kiwiMerge(0, a.length, merged.length);
    System.out.println( Arrays.toString(merged) );
    // [0, 0, 2, 2, 12, 12, 14, 15, 16, 17, 17, 18, 19, 19, 22, 22]
  }
}
