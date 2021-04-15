package com.github.jaaa.merge;

import net.jqwik.api.Example;
import net.jqwik.api.Group;
import net.jqwik.api.PropertyDefaults;

import static com.github.jaaa.merge.ParallelRebelMerge.PARALLEL_REBEL_MERGER;


@Group
public class ParallelRebelMergeTest
{
  private final Merger merger = new StaticMethodsMerger(ParallelRebelMerge.class) {
    @Override public boolean isStable() { return PARALLEL_REBEL_MERGER.isStable(); }
  };

  @Group class MergeTestSmall implements MergerTestTemplate {
    @Override public int maxArraySize() { return 100; }
    @Override public Merger merger() { return merger; }

    @Example
    void test() {
      int[] a = {},
            b = {0},
            c = {0};
      merger().merge(a,0,0, b,0,1, c,0);
    }
  }
  @Group class MergeTestMedium implements MergerTestTemplate {
    @Override public int maxArraySize() { return 10_000; }
    @Override public Merger merger() { return merger; }
  }
  @PropertyDefaults( tries = 1_000 )
  @Group class MergeTestLarge implements MergerTestTemplate {
    @Override public int maxArraySize() { return 1_000_000; }
    @Override public Merger merger() { return merger; }
  }

  @Group class MergerTestSmall implements MergerTestTemplate {
    @Override public int maxArraySize() { return 100; }
    @Override public Merger merger() { return PARALLEL_REBEL_MERGER; }
  }
  @Group class MergerTestMedium implements MergerTestTemplate {
    @Override public int maxArraySize() { return 10_000; }
    @Override public Merger merger() { return PARALLEL_REBEL_MERGER; }
  }
  @PropertyDefaults( tries = 1_000 )
  @Group class MergerTestLarge implements MergerTestTemplate {
    @Override public int maxArraySize() { return 1_000_000; }
    @Override public Merger merger() { return PARALLEL_REBEL_MERGER; }
  }
}