package com.github.jaaa.util;

import java.util.AbstractMap.SimpleEntry;
import java.util.Map.Entry;


@SuppressWarnings({"unchecked", "rawtypes"})
public class ZipWithIndex
{
  public static <T> Entry<        T,Integer>[] zipWithIndex(       T[] array ) { int i=array.length; Entry[] zip=new Entry[i]; while(i-- > 0) zip[i] = new SimpleEntry<>(array[i],i); return zip; }
  public static     Entry<  Boolean,Integer>[] zipWithIndex( boolean[] array ) { int i=array.length; Entry[] zip=new Entry[i]; while(i-- > 0) zip[i] = new SimpleEntry<>(array[i],i); return zip; }
  public static     Entry<     Byte,Integer>[] zipWithIndex(    byte[] array ) { int i=array.length; Entry[] zip=new Entry[i]; while(i-- > 0) zip[i] = new SimpleEntry<>(array[i],i); return zip; }
  public static     Entry<    Short,Integer>[] zipWithIndex(   short[] array ) { int i=array.length; Entry[] zip=new Entry[i]; while(i-- > 0) zip[i] = new SimpleEntry<>(array[i],i); return zip; }
  public static     Entry<  Integer,Integer>[] zipWithIndex(     int[] array ) { int i=array.length; Entry[] zip=new Entry[i]; while(i-- > 0) zip[i] = new SimpleEntry<>(array[i],i); return zip; }
  public static     Entry<     Long,Integer>[] zipWithIndex(    long[] array ) { int i=array.length; Entry[] zip=new Entry[i]; while(i-- > 0) zip[i] = new SimpleEntry<>(array[i],i); return zip; }
  public static     Entry<Character,Integer>[] zipWithIndex(    char[] array ) { int i=array.length; Entry[] zip=new Entry[i]; while(i-- > 0) zip[i] = new SimpleEntry<>(array[i],i); return zip; }
  public static     Entry<    Float,Integer>[] zipWithIndex(   float[] array ) { int i=array.length; Entry[] zip=new Entry[i]; while(i-- > 0) zip[i] = new SimpleEntry<>(array[i],i); return zip; }
  public static     Entry<   Double,Integer>[] zipWithIndex(  double[] array ) { int i=array.length; Entry[] zip=new Entry[i]; while(i-- > 0) zip[i] = new SimpleEntry<>(array[i],i); return zip; }
}
