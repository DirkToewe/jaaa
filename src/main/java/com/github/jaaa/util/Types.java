package com.github.jaaa.util;

import static java.util.Objects.requireNonNull;


public class Types
{
// STATIC FIELDS

// STATIC CONSTRUCTOR

// STATIC METHODS
  public static <T> Class<? super T> superClass( Class<? extends T> classA, Class<? extends T> classB )
  {
    for( Class<?> cl = classA; null != cl; cl = cl.getSuperclass() )
      if( cl.isAssignableFrom(classB) )
        return (Class<? super T>) cl;
    return Object.class;
  }

// FIELDS

// CONSTRUCTORS
  private Types() { throw new UnsupportedOperationException("Static class cannot be instantiated."); }

// METHODS
}
