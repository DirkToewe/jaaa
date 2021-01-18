package com.github.jaaa.util;

import net.jqwik.api.*;
import net.jqwik.api.constraints.Size;
import net.jqwik.api.stateful.Action;
import net.jqwik.api.stateful.ActionSequence;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static java.lang.String.format;
import static java.util.stream.IntStream.range;
import static org.assertj.core.api.Assertions.assertThat;
import static net.jqwik.api.RandomDistribution.uniform;

public class Byte256Test
{
  final class TestState {
    public final Byte256    byte256 = new Byte256();
    public final List<Integer> list = new ArrayList<>(256);
  }

  @Provide
  Arbitrary<ActionSequence<TestState>> actions()
  {
    var append = Arbitraries.integers().between(0,255).withDistribution( uniform() ).map( i -> new Action<TestState>() {
      @Override public boolean precondition( TestState state ) { return state.byte256.size() < 256; }
      @Override public TestState run( TestState state ) {
        state.byte256.append(i);
        state.list.add(i);
        return state;
      }
      @Override public String toString() { return format("append(%d)",i); }
    });

    var rotate = Arbitraries.integers().withDistribution( uniform() ).map( rot -> new Action<TestState>() {
      @Override public TestState run( TestState state ) {
        state.byte256.rotate(rot);

        // TODO: use Collections.rotate instead once it works for Integer.MIN_VALUE
        int len = state.list.size();
        if( len > 0 ) Collections.rotate(state.list, rot % len);

        return state;
      }
      @Override public String toString() { return format("rotate(%d)",rot); }
    });

    return Arbitraries.sequences(
      Arbitraries.oneOf(append, rotate)
    );
  }

  @Property( tries = 16*1024 )
  void testByte256( @ForAll("actions") @Size(min=1, max=1024) ActionSequence<TestState> actions ) {
    actions
      .withInvariant( state -> {
        var byte256 = state.byte256;
        var    list = state.list;

        assertThat( byte256.size    () ).isEqualTo( list.size    () );
        assertThat( byte256.toString() ).isEqualTo( list.toString() );

        assertThat(
          range(0,byte256.size()).map(byte256::get).toArray()
        ).isEqualTo(
          list.stream().mapToInt(x->x).toArray()
        );

        assertThat( byte256.isFull() ).isEqualTo( byte256.size() == 256 );
      })
      .run( new TestState() );
  }
}
