package com.github.jaaa;

import net.jqwik.api.Arbitraries;
import net.jqwik.api.Arbitrary;
import net.jqwik.api.providers.ArbitraryProvider;
import net.jqwik.api.providers.TypeUsage;

import java.lang.reflect.Array;
import java.util.*;

import static java.util.stream.Collectors.toSet;
import static net.jqwik.api.Arbitraries.integers;
import static net.jqwik.api.RandomDistribution.uniform;

public class ComparatorArbitraryProvider implements ArbitraryProvider
{
  @Override public boolean canProvideFor( TypeUsage targetType )
  {
    return targetType.isOfType(Comparator.class);
  }

  @Override public Set<Arbitrary<?>> provideFor( TypeUsage targetType, ArbitraryProvider.SubtypeProvider subtypeProvider )
  {
    TypeUsage contentType = targetType.getTypeArgument(0);

    var byHashAscending = new Comparator<>() {
      @Override public int compare( Object x, Object y ) {
        return Integer.compare(
          Objects.hashCode(x),
          Objects.hashCode(y)
        );
      }
      @Override public String toString() { return "Comparator.comparing(Objects::hashCode)"; }
    };

    var byHashDescending = new Comparator<>() {
      @Override public int compare( Object x, Object y ) {
        return Integer.compare(
          Objects.hashCode(y),
          Objects.hashCode(x)
        );
      }
      @Override public String toString() { return "Comparator.comparing(Objects::hashCode).reversed()"; }
    };

    if( Comparable.class.isAssignableFrom( contentType.getRawType() ) )
    {
      @SuppressWarnings("rawtypes")
      var natOrdAscending = new Comparator<Comparable>() {
        @SuppressWarnings("unchecked")
        @Override public int compare( Comparable x, Comparable y ) { return x.compareTo(y); }
        @Override public String toString() { return "Comparator.naturalOrder()"; }
      };

      @SuppressWarnings("rawtypes")
      var natOrdDescending = new Comparator<Comparable>() {
        @SuppressWarnings("unchecked")
        @Override public int compare( Comparable x, Comparable y ) { return y.compareTo(x); }
        @Override public String toString() { return "Comparator.naturalOrder().reversed()"; }
      };

      return Set.of( Arbitraries.of(
        byHashAscending,
        byHashDescending,
        natOrdAscending,
        natOrdDescending
      ));
    }

    return Set.of( Arbitraries.of(
      byHashAscending,
      byHashDescending
    ));
  }
}
