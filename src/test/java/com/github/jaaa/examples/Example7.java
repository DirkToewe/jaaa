package com.github.jaaa.examples;

import com.github.jaaa.permute.Swap;
import com.github.jaaa.select.Mom3SelectAccess;
import java.util.Arrays;

public class Example7 {
  public static void main( String... args ) {
    int[] array = { 14, 14, 0, 6, 13, 23, 9, 4, 22, 23, 5, 1, 7, 12, 7, 11 };
    new Mom3SelectAccess() {
      @Override public void   swap( int i, int j ) { Swap.swap(array,i,j); }
      @Override public int compare( int i, int j ) { return Integer.compare(array[i], array[j]); }
    }.heapSelect(/*from=*/0, /*mid=*/array.length/2, /*until=*/array.length);
    System.out.println( array[array.length/2] ); // 11
    System.out.println( Arrays.toString(array) );
    // [1, 4, 0, 6, 7, 5, 9, 7, 11, 23, 23, 22, 14, 14, 13, 12]
  }
}
