package com.github.jaaa.util;

import net.jqwik.api.*;
import net.jqwik.api.constraints.Size;
import net.jqwik.api.stateful.Action;
import net.jqwik.api.stateful.ActionSequence;

import java.util.ArrayList;

import static java.lang.String.format;
import static org.assertj.core.api.Assertions.assertThat;
import static net.jqwik.api.RandomDistribution.uniform;

public class Hex16Test
{
  final class TestState {
    public final Hex16             hex16 = new Hex16();
    public final ArrayList<Integer> list = new ArrayList<>(16);
  }

  @Provide
  Arbitrary<ActionSequence<TestState>> actions()
  {
    var append = Arbitraries.integers().between(0,15).withDistribution( uniform() ).map( i -> new Action<TestState>() {
      @Override public boolean precondition( TestState state ) { return state.hex16.size() < 16; }
      @Override public TestState run( TestState state ) {
        state.hex16.append(i);
        state.list.add(i);
        return state;
      }
      @Override public String toString() { return format("append(%d)",i); }
    });

    var insert = Arbitraries.integers().between(0,15).withDistribution( uniform() ).flatMap( pos ->
                 Arbitraries.integers().between(0,15).withDistribution( uniform() ).    map( hex -> new Action<TestState>()
    {
      @Override public boolean precondition( TestState state ) { return pos <= state.hex16.size() && state.hex16.size() < 16; }
      @Override public TestState run( TestState state ) {
        state.hex16.insert(pos, hex);
        state.list.add(pos, hex);
        return state;
      }
      @Override public String toString() { return format("insert(%d,%d)", pos, hex); }
    }));

    var clear = Arbitraries.of( new Action<TestState>() {
      @Override public TestState run( TestState state ) {
        state.hex16.clear();
        state.list.clear();
        return state;
      }
      @Override public String toString() { return "clear"; }
    });

    var rotate = Arbitraries.integers().withDistribution( uniform() ).map( rot -> new Action<TestState>() {
      @Override public TestState run( TestState state ) {
        state.hex16.rotate(rot);

        // TODO: use Collections.rotate instead once it works for Integer.MIN_VALUE
        var       list = state.list;
        int len = list.size();
        if( len > 0 ) {
          int   r = rot % len;
          for(; r > 0; r--) list.add(len-1, list.remove(0));
          for(; r < 0; r++) list.add(0,     list.remove(len-1));
        }

        return state;
      }
      @Override public String toString() { return format("rotate(%d)",rot); }
    });

    var swap = Arbitraries.integers().between(0,15).withDistribution( uniform() ).flatMap( i ->
               Arbitraries.integers().between(0,15).withDistribution( uniform() ).    map( j -> new Action<TestState>()
    {
      @Override public boolean precondition( TestState state ) {
        int        len = state.hex16.size();
        return i < len
            && j < len;
      }
      @Override public TestState run(TestState state ) {
        state.hex16.swap(i,j);

        var list = state.list;
            list.set(i,
            list.set(j,
            list.get(i)));

        return state;
      }
      @Override public String toString() { return format("swap(%d,%d)",i,j); }
    }));

    return Arbitraries.sequences(
      Arbitraries.oneOf(append, insert, clear, rotate, swap)
    );
  }

  @Property( tries = 10_000 )
  void testHex16( @ForAll("actions") @Size(min=1, max=8192) ActionSequence<TestState> actions ) {
    actions
      .withInvariant( state -> {
        var hex16 = state.hex16;
        var  list = state.list;

        assertThat( hex16.size() ).isEqualTo( list.size() );
        assertThat( hex16.toString() ).isEqualTo( list.toString() );

        for( int i=hex16.size(); i-- > 0; )
          assertThat( hex16.get(i) ).isEqualTo( list.get(i) );

        assertThat( hex16.isFull() ).isEqualTo( hex16.size() == 16 );
      })
      .run( new TestState() );
  }
}
