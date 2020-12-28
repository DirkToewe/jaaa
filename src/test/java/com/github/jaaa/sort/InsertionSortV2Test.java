package com.github.jaaa.sort;

import com.github.jaaa.CompareSwapAccess;
import com.github.jaaa.WithRange;
import com.github.jaaa.Swap;
import net.jqwik.api.ForAll;
import net.jqwik.api.Group;
import net.jqwik.api.Property;
import net.jqwik.api.Tuple;
import net.jqwik.api.constraints.Size;

import java.util.Arrays;
import java.util.Comparator;

import static com.github.jaaa.sort.InsertionSortV2.INSERTION_V2_SORTER;
import static java.lang.Math.max;
import static java.util.Comparator.comparing;
import static java.util.stream.IntStream.range;
import static net.jqwik.api.Tuple.Tuple2;
import static org.assertj.core.api.Assertions.assertThat;

@Group
public class InsertionSortV2Test
{
  @Group
  class SortTest extends TestTemplate
  {
    SortTest() {
      super(new StaticMethodsSorter(InsertionSortV2.class) {
        @Override public boolean isStable    () { return INSERTION_V2_SORTER.isStable    (); }
        @Override public boolean isInplace   () { return INSERTION_V2_SORTER.isInplace   (); }
        @Override public boolean isThreadSafe() { return INSERTION_V2_SORTER.isThreadSafe(); }
      });
    }
  }

  @Group
  class SorterInplaceTest extends TestTemplate
  {
    SorterInplaceTest() { super(INSERTION_V2_SORTER); }
  }

  private static abstract class TestTemplate extends SorterInplaceTestTemplate
  {
    private static final int N_TRIES = 1_000_000,
                            MAX_SIZE =    10_000;

    private static abstract class CountingSortAccess implements CompareSwapAccess
    {
      public long nSwaps=0,
                  nComps=0;
    }

    TestTemplate( SorterInplace sorter ) { super(sorter); }

    @Property( tries = N_TRIES )
    void sortsQuicklyOrderedAccessWithRangeByte( @ForAll WithRange<@Size(min=0, max=MAX_SIZE) byte[]> sample )
    {
      int     from = sample.getFrom(),
             until = sample.getUntil();
      byte[] array = sample.getData();

      Comparator<Tuple2<Byte,Integer>> cmp = comparing(Tuple2::get1);

      Tuple2<Byte,Integer>[] reference = range(0,array.length).mapToObj(i -> Tuple.of(array[i],i) ).toArray(Tuple2[]::new);
      Arrays.sort(reference, from,until, cmp); Tuple2<Byte,Integer>[]     input = reference.clone();

      var acc = new CountingSortAccess() {
        @Override public int compare( int i, int j ) { nComps++; return cmp.compare(input[i], input[j]); }
        @Override public void   swap( int i, int j ) { nSwaps++; Swap.swap(input,i,j); }
      };

      InsertionSortV2.sort(from,until, acc);

      assertThat(input).isEqualTo(reference);
      assertThat(acc.nComps).isEqualTo( max(0, until-from-1) );
      assertThat(acc.nSwaps).isEqualTo(0);
    }
  }
}
