package com.github.jaaa.partition;

import com.github.jaaa.fn.PredicateSwapAccess;
import com.github.jaaa.permute.Swap;
import net.jqwik.api.*;
import net.jqwik.api.constraints.IntRange;
import net.jqwik.api.constraints.Size;
import net.jqwik.api.stateful.Action;
import net.jqwik.api.stateful.ActionSequence;

import java.util.Arrays;

import static java.lang.String.format;
import static java.util.stream.IntStream.range;
import static net.jqwik.api.RandomDistribution.uniform;
import static org.assertj.core.api.Assertions.assertThat;


public class InPlacePartitionBufferIntTest
{
  final class TestState
  {
  // FIELDS
    public final int nBits, nInts;
    public final InPlacePartitionBufferInt tst;
    public final int[] ref;

  // CONSTRUCTORS
    public TestState( int _nBits, int _nInts ) {
      var acc = new PredicateSwapAccess() {
        private final boolean[] bits = new boolean[2*_nBits*_nInts]; {
          Arrays.fill(bits, bits.length/2, bits.length, true);
        }
        @Override public void swap( int i, int j ) { Swap.swap(bits, i,j); }
        @Override public boolean predicate( int i ) { return bits[i]; }
      };
      nBits =_nBits;
      nInts =_nInts;
      tst = new InPlacePartitionBufferInt(acc, 0, _nBits*_nInts, _nBits,_nInts);
      ref  = new int[_nInts];
    }

  // METHODS
  }

  @Provide
  Arbitrary<ActionSequence<TestState>> actions()
  {
    var set = Arbitraries.integers().greaterOrEqual(0).withDistribution( uniform() ).flatMap( posRaw ->
              Arbitraries.integers().greaterOrEqual(0).withDistribution( uniform() ).    map( valRaw ->
      new Action<TestState>() {
        private int pos = posRaw,
                    val = valRaw;
        @Override public boolean precondition( TestState state ) {
          if( state.nInts <= 0 ) return false;
          pos =         posRaw %        state.nInts;
          val = (int) ( valRaw % (1L << state.nBits) );
          return true;
        }
        @Override public TestState run( TestState state ) {
          assert state.nInts > 0;
          pos =         posRaw %        state.nInts;
          val = (int) ( valRaw % (1L << state.nBits) );
          state.tst.set(pos, val);
          state.ref[pos] = val;
          return state;
        }
        @Override public String toString() { return format("set(%d,%d)", pos, val); }
      }
    ));

    var reset = Arbitraries.of(
      new Action<TestState>() {
        @Override public TestState run( TestState state ) {
          state.tst.reset();
          Arrays.fill(state.ref, 0);
          return state;
        }
        @Override public String toString() { return "reset()"; }
      }
    );

    return Arbitraries.sequences(
      Arbitraries.oneOf(set, reset)
    );
  }

  @Property( tries = 8192 )
  void testInplaceBufferIntTest(
    @ForAll @IntRange(min=0, max= 31) int nBits,
    @ForAll @IntRange(min=0, max=512) int nInts,
    @ForAll("actions") @Size(min=1, max=1024) ActionSequence<TestState> actions
  ) {
    actions.withInvariant( state -> {

      assertThat( state.nBits ).isBetween(0,31);
      assertThat( state.nInts ).isGreaterThanOrEqualTo(0);
      assertThat( range(0,nInts).map(state.tst::get).toArray() ).isEqualTo( state.ref );

    }).run( new TestState(nBits,nInts) );
  }
}
