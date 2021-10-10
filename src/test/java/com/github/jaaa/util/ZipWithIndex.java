package com.github.jaaa.util;

import java.util.Map.Entry;

import static java.util.Map.entry;
import static java.util.stream.IntStream.range;


@SuppressWarnings("unchecked")
public class ZipWithIndex
{
  public static <T> Entry<        T,Integer>[] zipWithIndex(       T[] array ) { return range(0,array.length).mapToObj( i -> entry(array[i],i) ).toArray(Entry[]::new); }
  public static <T> Entry<  Boolean,Integer>[] zipWithIndex( boolean[] array ) { return range(0,array.length).mapToObj( i -> entry(array[i],i) ).toArray(Entry[]::new); }
  public static <T> Entry<     Byte,Integer>[] zipWithIndex(    byte[] array ) { return range(0,array.length).mapToObj( i -> entry(array[i],i) ).toArray(Entry[]::new); }
  public static <T> Entry<    Short,Integer>[] zipWithIndex(   short[] array ) { return range(0,array.length).mapToObj( i -> entry(array[i],i) ).toArray(Entry[]::new); }
  public static <T> Entry<  Integer,Integer>[] zipWithIndex(     int[] array ) { return range(0,array.length).mapToObj( i -> entry(array[i],i) ).toArray(Entry[]::new); }
  public static <T> Entry<     Long,Integer>[] zipWithIndex(    long[] array ) { return range(0,array.length).mapToObj( i -> entry(array[i],i) ).toArray(Entry[]::new); }
  public static <T> Entry<Character,Integer>[] zipWithIndex(    char[] array ) { return range(0,array.length).mapToObj( i -> entry(array[i],i) ).toArray(Entry[]::new); }
  public static <T> Entry<    Float,Integer>[] zipWithIndex(   float[] array ) { return range(0,array.length).mapToObj( i -> entry(array[i],i) ).toArray(Entry[]::new); }
  public static <T> Entry<   Double,Integer>[] zipWithIndex(  double[] array ) { return range(0,array.length).mapToObj( i -> entry(array[i],i) ).toArray(Entry[]::new); }
}
