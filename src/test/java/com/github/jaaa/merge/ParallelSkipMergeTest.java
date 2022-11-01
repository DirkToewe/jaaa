package com.github.jaaa.merge;

import net.jqwik.api.Example;
import net.jqwik.api.Group;
import net.jqwik.api.PropertyDefaults;

import static com.github.jaaa.merge.ParallelSkipMerge.PARALLEL_SKIP_MERGER;


@Group
public class ParallelSkipMergeTest
{
  private final Merger merger = new StaticMethodsMerger(ParallelSkipMerge.class) {
    @Override public boolean isStable() { return PARALLEL_SKIP_MERGER.isStable(); }
  };

  @Group class MergeTestSmall implements MergerTestTemplate {
    @Override public int maxArraySize() { return 100; }
    @Override public Merger merger() { return merger; }
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
    @Override public Merger merger() { return PARALLEL_SKIP_MERGER; }
  }
  @Group class MergerTestMedium implements MergerTestTemplate {
    @Override public int maxArraySize() { return 10_000; }
    @Override public Merger merger() { return PARALLEL_SKIP_MERGER; }
  }
  @PropertyDefaults( tries = 1_000 )
  @Group class MergerTestLarge implements MergerTestTemplate {
    @Override public int maxArraySize() { return 1_000_000; }
    @Override public Merger merger() { return PARALLEL_SKIP_MERGER; }
  }
}
