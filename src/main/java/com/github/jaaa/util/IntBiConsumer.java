package com.github.jaaa.util;

import static java.util.Objects.requireNonNull;

public interface IntBiConsumer
{
  void accept( int x, int y );

  default IntBiConsumer andThen( IntBiConsumer after ) {
    requireNonNull(after);
    return (x,y) -> { accept(x,y); after.accept(x,y); };
  }
}
