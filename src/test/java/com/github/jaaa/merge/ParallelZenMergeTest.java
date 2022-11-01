package com.github.jaaa.merge;

import com.github.jaaa.CompareRandomAccessor;
import com.github.jaaa.Swap;
import net.jqwik.api.Example;
import net.jqwik.api.Group;
import net.jqwik.api.PropertyDefaults;

import static com.github.jaaa.merge.ParallelZenMerge.PARALLEL_ZEN_MERGER;


@Group
public class ParallelZenMergeTest
{
  private final Merger merger = new StaticMethodsMerger(ParallelZenMerge.class) {
    @Override public boolean isStable() { return PARALLEL_ZEN_MERGER.isStable(); }
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
    @Override public Merger merger() { return PARALLEL_ZEN_MERGER; }
  }
  @Group class MergerTestMedium implements MergerTestTemplate {
    @Override public int maxArraySize() { return 10_000; }
    @Override public Merger merger() { return PARALLEL_ZEN_MERGER; }
  }
  @PropertyDefaults( tries = 1_000 )
  @Group class MergerTestLarge implements MergerTestTemplate {
    @Override public int maxArraySize() { return 1_000_000; }
    @Override public Merger merger() { return PARALLEL_ZEN_MERGER; }
  }
}