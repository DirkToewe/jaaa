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

public class WithRangeArbitraryProvider implements ArbitraryProvider
{
  @Override public boolean canProvideFor( TypeUsage targetType ) {
    if( ! targetType.isOfType(WithRange.class) )
      return false;

    TypeUsage content = targetType.getTypeArgument(0);

    return content.isArray()
        || content.isOfType(Collection.class)
        || With.class.isAssignableFrom( content.getRawType() );
  }

  @Override public Set<Arbitrary<?>> provideFor( TypeUsage targetType, SubtypeProvider subtypeProvider ) {
    TypeUsage contentType = targetType.getTypeArgument(0);

    var samples = subtypeProvider.apply(contentType).stream();

    if( contentType.isArray() )
      return samples.map(
        x -> x.flatMap( arr -> {  int len = Array.getLength(arr);
          return integers().between(   0,len).withDistribution(uniform()).flatMap(  from ->
                 integers().between(from,len).withDistribution(uniform()).    map( until -> new WithRange<>(from,until, arr) ));
        })
      ).collect( toSet() );

    if( contentType.isOfType(Collection.class) )
      return samples.map(
        x -> x.flatMap( col -> {  int len = ((Collection<?>) col).size();
          return integers().between(   0,len).withDistribution(uniform()).flatMap(  from ->
                 integers().between(from,len).withDistribution(uniform()).    map( until -> new WithRange<>(from,until, col) ));
        })
      ).collect( toSet() );

    if( With.class.isAssignableFrom( contentType.getRawType() ) )
      return samples.map(
        x -> x.flatMap( wt -> {  int len = ((With<?>) wt).contentLength();
          return integers().between(   0,len).withDistribution(uniform()).flatMap(  from ->
                 integers().between(from,len).withDistribution(uniform()).    map( until -> new WithRange<>(from,until, wt) ));
        })
      ).collect( toSet() );

    throw new IllegalArgumentException();
  }
}
