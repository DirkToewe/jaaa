package com.github.jaaa.fn;

import java.util.function.Consumer;

import static java.util.Map.Entry;

public interface EntryConsumer<K,V> extends Consumer<Entry<K,V>>
{
// STATIC FIELDS
  static <K,V> EntryConsumer<K,V> of( EntryConsumer<K,V> entryConsumer ) { return entryConsumer; }

// STATIC CONSTRUCTOR

// STATIC METHODS

// FIELDS

// CONSTRUCTORS

// METHODS
  void accept( K key, V value );

  @Override default void accept( Entry<K,V> entry ) {
    accept(
      entry.getKey(),
      entry.getValue()
    );
  }
}
