package com.github.jaaa.sort;

import com.github.jaaa.*;

import java.util.Comparator;

import static java.util.Comparator.naturalOrder;

public interface SorterInPlace extends Sorter
{
  void sort( int from, int until, CompareSwapAccess acc );
}
