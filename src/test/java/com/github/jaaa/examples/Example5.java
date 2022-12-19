package com.github.jaaa.examples;

import com.github.jaaa.search.ExpL2RSearchAccessor;

public class Example5 {
  public static void main( String... args ) {
    int[] array = { 0, 1, 1, 3, 5, 5, 6, 7, 7, 7, 8, 11, 12, 13, 14, 21 },
            key = { 7 };
    ExpL2RSearchAccessor<int[]> acc = (a,i, b,j) -> Integer.compare(a[i], b[j]);
    int index = acc.expL2RSearchL(array,0,array.length, key,0);
    System.out.println(index); // 7
  }
}
