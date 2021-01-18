package com.github.jaaa.sort;

import com.github.jaaa.*;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.util.Comparator;

import static java.lang.invoke.MethodType.methodType;

/** A {@link SorterInplace} which relays all sort calls to the static sort methods of a class.
 *  Used to test static sort methods the same way as any other {@link SorterInplace}.
 */
public abstract class StaticMethodsSorterInplace extends StaticMethodsSorter implements SorterInplace
{
// STATIC FIELDS

// STATIC CONSTRUCTOR

// STATIC METHODS

// FIELDS
  private final MethodHandle access;

// CONSTRUCTORS
  public StaticMethodsSorterInplace( Class<?> sortClass )
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
