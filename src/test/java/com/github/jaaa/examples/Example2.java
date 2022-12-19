package com.github.jaaa.examples;

import com.github.jaaa.permute.Swap;
import com.github.jaaa.sort.HeapSortAccess;
import java.util.Arrays;

public class Example2
{
  public static void main( String... args ) {
    int[] x = {  0, 12,  8, 14, 19,  6, 3, 21, 18, 16, 22, 14, 7, 8, 8, 20 },
          y = { 13, 17, 12, 23, 13, 10, 1,  8,  3, 23, 18,  3, 8, 8, 5, 21 };
    new HeapSortAccess() {
      @Override public void swap( int i, int j ) {
        Swap.swap(x,i,j);
        Swap.swap(y,i,j);
      }
      @Override public int compare( int i, int j ) {
        int    c  =         Integer.compare(x[i], x[j]);
        return c != 0 ? c : Integer.compare(y[i], y[j]);
      }
    }.heapSort(0,x.length);
    System.out.println( Arrays.toString(x) );
    System.out.println( Arrays.toString(y) );
    // [ 0, 3,  6, 7, 8, 8,  8, 12, 14, 14, 16, 18, 19, 20, 21, 22]
    // [13, 1, 10, 8, 5, 8, 12, 17,  3, 23, 23,  3, 13, 21,  8, 18]
  }
}
