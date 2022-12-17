package experiments;

import net.jqwik.api.*;

import java.util.*;

import static java.util.stream.Collectors.toList;
import static java.util.stream.IntStream.range;

@PropertyDefaults( tries = 10_000 )
public class JQwikHashTest
{
  private static final int N_MAX = 100_000_000;
  @Provide Arbitrary<Integer> uncached() {
    return Arbitraries.of( range(1,N_MAX).boxed().collect(toList()) );
  }
  @Provide Arbitrary<Integer> cached() {
    List<Integer> cached = new AbstractList<Integer>() {
      private final List<Integer>  uncached = range(1,N_MAX).boxed().collect(toList());
      private final int hashCode = uncached.hashCode();
      @Override public Integer get( int index ) { return uncached.get(index); }
      @Override public int size() { return uncached.size(); }
      @Override public int hashCode() { return hashCode; }
    };
    return Arbitraries.of(cached);
  }
  @Property void test1_cached  ( @ForAll(  "cached") Integer i ) { assert i > 0; }
  @Property void test1_uncached( @ForAll("uncached") Integer i ) { assert i > 0; }
  @Property void test2_cached  ( @ForAll(  "cached") Integer i ) { assert i > 0; }
  @Property void test2_uncached( @ForAll("uncached") Integer i ) { assert i > 0; }
  @Property void test3_cached  ( @ForAll(  "cached") Integer i ) { assert i > 0; }
  @Property void test3_uncached( @ForAll("uncached") Integer i ) { assert i > 0; }
}
