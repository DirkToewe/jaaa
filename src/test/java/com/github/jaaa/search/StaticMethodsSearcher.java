package com.github.jaaa.search;

import com.github.jaaa.search.Searcher;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.util.Comparator;
import java.util.function.IntUnaryOperator;

import static java.lang.invoke.MethodType.methodType;

/** A {@link Searcher} which relays all sort calls to the static search methods of a class.
 *  Used to test static search methods the same way as any other {@link Searcher}.
 */
public class StaticMethodsSearcher implements Searcher
{
  private static final class SearchMethodArray<A,E,C>
  {
    private final MethodHandle
      methodAIIC,
      methodAII,
      methodAC,
      methodA;

    public SearchMethodArray( Class<?> searchClass, String methodName, Class<A> arrClass, Class<C> cmpClass )
    {
      Class<?> cmpArrClass = arrClass == Object[].class ? Comparable[].class : arrClass;

      MethodHandles.Lookup lookup = MethodHandles.publicLookup();

      try {
        methodA    = lookup.findStatic( searchClass, methodName, methodType(int.class, cmpArrClass,                       cmpArrClass.getComponentType()) );
        methodAII  = lookup.findStatic( searchClass, methodName, methodType(int.class, cmpArrClass, int.class, int.class, cmpArrClass.getComponentType()) );
        methodAC   = lookup.findStatic( searchClass, methodName, methodType(int.class,    arrClass,                          arrClass.getComponentType(), cmpClass) );
        methodAIIC = lookup.findStatic( searchClass, methodName, methodType(int.class,    arrClass, int.class, int.class,    arrClass.getComponentType(), cmpClass) );
      }
      catch( NoSuchMethodException | IllegalAccessException err ) {
        throw new Error(err);
      }
    }

    public int call( A arr, E key ) {
      try {
        return (int) methodA.invoke(arr,key);
      }
      catch( RuntimeException|Error ex ) { throw ex; }
      catch( Throwable t ) { throw new Error(t); }
    }

    public int call( A arr, int from, int until , E key) {
      try {
        return (int) methodAII.invoke(arr, from,until, key);
      }
      catch( RuntimeException|Error ex ) { throw ex; }
      catch( Throwable t ) { throw new Error(t); }
    }

    public int call( A arr, E key, C cmp ) {
      try {
        return (int) methodAC.invoke(arr,key, cmp);
      }
      catch( RuntimeException|Error ex ) { throw ex; }
      catch( Throwable t ) { throw new Error(t); }
    }

    public int call( A arr, int from, int until, E key, C cmp ) {
      try {
        return (int) methodAIIC.invoke(arr, from,until, key, cmp);
      }
      catch( RuntimeException|Error ex ) { throw ex; }
      catch( Throwable t ) { throw new Error(t); }
    }
  }
  private static final class SearchMethod
  {
    private final MethodHandle methodCompass;
    public final SearchMethodArray<Object[], Object, Comparator> arrayObject;

    public SearchMethod( Class<?> searchClass, String methodName )
    {
      arrayObject = new SearchMethodArray<>(searchClass, methodName, Object[].class, Comparator.class);

      MethodHandles.Lookup lookup = MethodHandles.publicLookup();

      try {
        methodCompass = lookup.findStatic( searchClass, methodName, methodType(int.class, int.class, int.class, IntUnaryOperator.class) );
      }
      catch( NoSuchMethodException | IllegalAccessException err ) {
        throw new Error(err);
      }
    }

    public int call( int from, int until, IntUnaryOperator compass ) {
      try {
        return (int) methodCompass.invoke(from,until, compass);
      }
      catch( RuntimeException|Error ex ) { throw ex; }
      catch( Throwable t ) { throw new Error(t); }
    }
  }

  private final SearchMethod
    search , searchGap,
    searchR, searchGapR,
    searchL, searchGapL;

  public StaticMethodsSearcher(Class<?> sortClass )
  {
    search  = new SearchMethod(sortClass, "search" );
    searchR = new SearchMethod(sortClass, "searchR");
    searchL = new SearchMethod(sortClass, "searchL");
    searchGap  = new SearchMethod(sortClass, "searchGap" );
    searchGapR = new SearchMethod(sortClass, "searchGapR");
    searchGapL = new SearchMethod(sortClass, "searchGapL");
  }


  @Override public int search ( int from, int until, IntUnaryOperator compass ) { return search .call(from,until, compass); }
  @Override public int searchR( int from, int until, IntUnaryOperator compass ) { return searchR.call(from,until, compass); }
  @Override public int searchL( int from, int until, IntUnaryOperator compass ) { return searchL.call(from,until, compass); }


  @Override public int searchGap ( int from, int until, IntUnaryOperator compass ) { return searchGap .call(from,until, compass); }
  @Override public int searchGapR( int from, int until, IntUnaryOperator compass ) { return searchGapR.call(from,until, compass); }
  @Override public int searchGapL( int from, int until, IntUnaryOperator compass ) { return searchGapL.call(from,until, compass); }


  @Override public <T extends Comparable<? super T>> int search ( T[] seq,                      T key                            ) { return search .arrayObject.call(seq,             key); }
  @Override public <T extends Comparable<? super T>> int search ( T[] seq, int from, int until, T key                            ) { return search .arrayObject.call(seq, from,until, key); }
  @Override public <T>                               int search ( T[] seq,                      T key, Comparator<? super T> cmp ) { return search .arrayObject.call(seq,             key, cmp); }
  @Override public <T>                               int search ( T[] seq, int from, int until, T key, Comparator<? super T> cmp ) { return search .arrayObject.call(seq, from,until, key, cmp); }

  @Override public <T extends Comparable<? super T>> int searchR( T[] seq,                      T key                            ) { return searchR.arrayObject.call(seq,             key); }
  @Override public <T extends Comparable<? super T>> int searchR( T[] seq, int from, int until, T key                            ) { return searchR.arrayObject.call(seq, from,until, key); }
  @Override public <T>                               int searchR( T[] seq,                      T key, Comparator<? super T> cmp ) { return searchR.arrayObject.call(seq,             key, cmp); }
  @Override public <T>                               int searchR( T[] seq, int from, int until, T key, Comparator<? super T> cmp ) { return searchR.arrayObject.call(seq, from,until, key, cmp); }

  @Override public <T extends Comparable<? super T>> int searchL( T[] seq,                      T key                            ) { return searchL.arrayObject.call(seq,             key); }
  @Override public <T extends Comparable<? super T>> int searchL( T[] seq, int from, int until, T key                            ) { return searchL.arrayObject.call(seq, from,until, key); }
  @Override public <T>                               int searchL( T[] seq,                      T key, Comparator<? super T> cmp ) { return searchL.arrayObject.call(seq,             key, cmp); }
  @Override public <T>                               int searchL( T[] seq, int from, int until, T key, Comparator<? super T> cmp ) { return searchL.arrayObject.call(seq, from,until, key, cmp); }


  @Override public <T extends Comparable<? super T>> int searchGap ( T[] seq,                      T key                            ) { return searchGap .arrayObject.call(seq,             key); }
  @Override public <T extends Comparable<? super T>> int searchGap ( T[] seq, int from, int until, T key                            ) { return searchGap .arrayObject.call(seq, from,until, key); }
  @Override public <T>                               int searchGap ( T[] seq,                      T key, Comparator<? super T> cmp ) { return searchGap .arrayObject.call(seq,             key, cmp); }
  @Override public <T>                               int searchGap ( T[] seq, int from, int until, T key, Comparator<? super T> cmp ) { return searchGap .arrayObject.call(seq, from,until, key, cmp); }

  @Override public <T extends Comparable<? super T>> int searchGapR( T[] seq,                      T key                            ) { return searchGapR.arrayObject.call(seq,             key); }
  @Override public <T extends Comparable<? super T>> int searchGapR( T[] seq, int from, int until, T key                            ) { return searchGapR.arrayObject.call(seq, from,until, key); }
  @Override public <T>                               int searchGapR( T[] seq,                      T key, Comparator<? super T> cmp ) { return searchGapR.arrayObject.call(seq,             key, cmp); }
  @Override public <T>                               int searchGapR( T[] seq, int from, int until, T key, Comparator<? super T> cmp ) { return searchGapR.arrayObject.call(seq, from,until, key, cmp); }

  @Override public <T extends Comparable<? super T>> int searchGapL( T[] seq,                      T key                            ) { return searchGapL.arrayObject.call(seq,             key); }
  @Override public <T extends Comparable<? super T>> int searchGapL( T[] seq, int from, int until, T key                            ) { return searchGapL.arrayObject.call(seq, from,until, key); }
  @Override public <T>                               int searchGapL( T[] seq,                      T key, Comparator<? super T> cmp ) { return searchGapL.arrayObject.call(seq,             key, cmp); }
  @Override public <T>                               int searchGapL( T[] seq, int from, int until, T key, Comparator<? super T> cmp ) { return searchGapL.arrayObject.call(seq, from,until, key, cmp); }
}
