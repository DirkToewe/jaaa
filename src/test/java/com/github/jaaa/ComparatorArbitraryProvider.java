package com.github.jaaa;

import net.jqwik.api.Arbitraries;
import net.jqwik.api.Arbitrary;
import net.jqwik.api.providers.ArbitraryProvider;
import net.jqwik.api.providers.TypeUsage;

import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;


public class ComparatorArbitraryProvider implements ArbitraryProvider
{
  @Override public boolean canProvideFor( TypeUsage targetType )
  {
    return targetType.isOfType(Comparator.class);
  }

  @Override public Set<Arbitrary<?>> provideFor( TypeUsage targetType, ArbitraryProvider.SubtypeProvider subtypeProvider )
  {
    TypeUsage contentType = targetType.getTypeArgument(0);

    Comparator<?> byHashAscending = new Comparator<Object>() {
      @Override public int compare( Object x, Object y ) {
        return Integer.compare(
          Objects.hashCode(x),
          Objects.hashCode(y)
        );
      }
      @Override public String toString() { return "Comparator.comparing(Objects::hashCode)"; }
    };

    Comparator<?> byHashDescending = new Comparator<Object>() {
      @Override public int compare( Object x, Object y ) {
        return Integer.compare(
          Objects.hashCode(y),
          Objects.hashCode(x)
        );
      }
      @Override public String toString() { return "Comparator.comparing(Objects::hashCode).reversed()"; }
    };

    Set<Arbitrary<?>> result = new LinkedHashSet<>();

    if( Comparable.class.isAssignableFrom( contentType.getRawType() ) )
    {
      @SuppressWarnings("rawtypes")
      Comparator<Comparable> natOrdAscending = new Comparator<Comparable>() {
        @SuppressWarnings("unchecked")
        @Override public int compare( Comparable x, Comparable y ) { return x.compareTo(y); }
        @Override public String toString() { return "Comparator.naturalOrder()"; }
      };

      @SuppressWarnings("rawtypes")
      Comparator<Comparable> natOrdDescending = new Comparator<Comparable>() {
        @SuppressWarnings("unchecked")
        @Override public int compare( Comparable x, Comparable y ) { return y.compareTo(x); }
        @Override public String toString() { return "Comparator.naturalOrder().reversed()"; }
      };

      result.add( Arbitraries.of(
        byHashAscending,
        byHashDescending,
        natOrdAscending,
        natOrdDescending
      ));
      return result;
    }

    result.add( Arbitraries.of(
      byHashAscending,
      byHashDescending
    ));
    return result;
  }
}
