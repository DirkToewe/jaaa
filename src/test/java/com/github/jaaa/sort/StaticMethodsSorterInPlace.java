package com.github.jaaa.sort;

import com.github.jaaa.*;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;

import static java.lang.invoke.MethodType.methodType;

/** A {@link SorterInPlace} which relays all sort calls to the static sort methods of a class.
 *  Used to test static sort methods the same way as any other {@link SorterInPlace}.
 */
public abstract class StaticMethodsSorterInPlace extends StaticMethodsSorter implements SorterInPlace
{
// STATIC FIELDS

// STATIC CONSTRUCTOR

// STATIC METHODS

// FIELDS
  private final MethodHandle access;

// CONSTRUCTORS
  public StaticMethodsSorterInPlace(Class<?> sortClass )
  {
    super(sortClass);
    MethodHandles.Lookup lookup = MethodHandles.publicLookup();
    try {
      access = lookup.findStatic( sortClass, "sort", methodType(void.class, int.class, int.class, CompareSwapAccess.class) );
    }
    catch( NoSuchMethodException | IllegalAccessException err ) {
      throw new Error(err);
    }
  }

// METHODS
  @Override public void sort( int from, int until, CompareSwapAccess acc )
  {
    try {
      access.invoke(from,until,acc);
    }
    catch( RuntimeException|Error ex ) { throw ex; }
    catch( Throwable t ) { throw new Error(t); }
  }
}
