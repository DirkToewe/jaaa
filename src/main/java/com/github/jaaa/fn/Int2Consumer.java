package com.github.jaaa.fn;

import static java.util.Objects.requireNonNull;

public interface Int2Consumer
{
  void accept( int x, int y );

  default Int2Consumer andThen(Int2Consumer after ) {
    requireNonNull(after);
    return (x,y) -> { accept(x,y); after.accept(x,y); };
  }
}
