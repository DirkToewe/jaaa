package com.github.jaaa;

import net.jqwik.api.Arbitrary;
import net.jqwik.api.providers.ArbitraryProvider;
import net.jqwik.api.providers.TypeUsage;

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Set;

import static java.util.stream.Collectors.toSet;
import static net.jqwik.api.Arbitraries.integers;
import static net.jqwik.api.RandomDistribution.uniform;

public class WithIndexArbitraryProvider implements ArbitraryProvider
{
  @Override public boolean canProvideFor( TypeUsage targetType ) {
    if( ! targetType.isOfType(WithIndex.class) )
      return false;

    TypeUsage content = targetType.getTypeArgument(0);

    return content.isArray()
        || content.isOfType(Collection.class)
        || content.isOfType(WithIndex.class);
  }

  @Override public Set<Arbitrary<?>> provideFor(TypeUsage targetType, ArbitraryProvider.SubtypeProvider subtypeProvider ) {
    TypeUsage contentType = targetType.getTypeArgument(0);

    var samples = subtypeProvider.apply(contentType).stream();

    if( contentType.isArray() )
      return samples.map(
        x -> x.flatMap( arr -> {  int len = Array.getLength(arr);
          return integers().between(0,len-1).withDistribution(uniform()).map( index -> new WithIndex<>(index, arr) );
        })
      ).collect( toSet() );

    if( contentType.isOfType(Collection.class) )
      return samples.map(
        x -> x.flatMap( col -> {  int len = ((Collection<?>) col).size();
          return integers().between(0,len-1).withDistribution(uniform()).map( index -> new WithIndex<>(index, col) );
        })
      ).collect( toSet() );

    if( contentType.isOfType(WithIndex.class) )
      return samples.map(
        x -> x.flatMap( withIdx -> {  int len = ((WithIndex<?>) withIdx).contentLength();
          return integers().between(0,len-1).withDistribution(uniform()).map( index -> new WithIndex<>(index, withIdx) );
        })
      ).collect( toSet() );

    throw new IllegalArgumentException();
  }
}
