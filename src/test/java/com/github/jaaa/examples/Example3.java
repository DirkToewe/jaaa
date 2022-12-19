package com.github.jaaa.examples;

import com.github.jaaa.permute.Swap;
import com.github.jaaa.sort.TimSortAccessor;
import java.util.Arrays;

public class Example3 {
  public static void main( String... args ) {
    TimSortAccessor<String[]> acc = new TimSortAccessor<String[]>() {
      @Override public String[] malloc( int len ) { return new String[len]; }
      @Override public void   swap( String[] a, int i, String[] b, int j ) { Swap.swap(a,i, b,j); }
      @Override public void   copy( String[] a, int i, String[] b, int j ) { b[j] = a[i]; }
      @Override public int compare( String[] a, int i, String[] b, int j ) { return a[i].compareTo(b[j]); }
    };
    String[] array1 = { "C", "P", "X", "N", "V", "X", "B", "M", "P", "E", "R", "E", "H", "J", "X", "K" },
             array2 = { "Q", "H", "B", "C", "V", "C", "B", "G", "Q" };
    acc.timSort(array1, 0, array1.length, /*init work mem*/null,0,0);
    acc.timSort(array2, 0, array2.length, /*init work mem*/null,0,0);
    System.out.println( Arrays.toString(array1) );
    System.out.println( Arrays.toString(array2) );
    // [B, C, E, E, H, J, K, M, N, P, P, R, V, X, X, X]
    // [B, B, C, C, G, H, Q, Q, V]
  }
}
