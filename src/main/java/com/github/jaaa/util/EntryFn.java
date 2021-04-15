package com.github.jaaa.util;

import static java.util.Map.Entry;
import java.util.function.Function;

public interface EntryFn<K,V,R> extends Function<Entry<K,V>,R>
{
// STATIC FIELDS
  static <K,V,R> EntryFn<K,V,R> of( EntryFn<K,V,R> entryFn ) { return entryFn; }

// STATIC CONSTRUCTORS

// STATIC METHODS

// FIELDS

// CONSTRUCTORS

// METHODS
  R apply( K key, V value );

  @Override default R apply( Entry<K,V> entry ) {
    return apply(
      entry.getKey(),
      entry.getValue()
    );
  }
}
