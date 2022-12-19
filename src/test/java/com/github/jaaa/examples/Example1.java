package com.github.jaaa.examples;

import com.github.jaaa.permute.Swap;
import com.github.jaaa.sort.KiwiSortAccess;
import java.util.Arrays;

public class Example1
{
  public static void main( String... args ) {
    int[] array = { 14, 11, 19, 2, 10, 14, 8, 15, 22, 8, 0, 22, 5, 21, 12, 2 };
    new KiwiSortAccess() {
      @Override public void   swap( int i, int j ) { Swap.swap(array,i,j); }
      @Override public int compare( int i, int j ) { return Integer.compare(array[i], array[j]); }
    }.kiwiSort(0,array.length);
    System.out.println( Arrays.toString(array) );
    // [0, 2, 2, 5, 8, 8, 10, 11, 12, 14, 14, 15, 19, 21, 22, 22]
  }
}
