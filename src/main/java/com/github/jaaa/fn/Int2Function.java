package com.github.jaaa.fn;

import static java.util.Objects.requireNonNull;
import java.util.function.Function;

public interface Int2Function<R>
{
  R apply( int x, int y );

  default <V> Int2Function<V> andThen(Function<? super R, ? extends V> after ) {
    requireNonNull(after);
    return (x,y) -> after.apply( apply(x,y) );
  }
}
