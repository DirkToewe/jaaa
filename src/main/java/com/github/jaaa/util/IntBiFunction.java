package com.github.jaaa.util;

import static java.util.Objects.requireNonNull;
import java.util.function.Function;

public interface IntBiFunction<R>
{
  public R apply( int x, int y );

  default <V> IntBiFunction<V> andThen( Function<? super R, ? extends V> after ) {
    requireNonNull(after);
    return (x,y) -> after.apply( apply(x,y) );
  }
}
