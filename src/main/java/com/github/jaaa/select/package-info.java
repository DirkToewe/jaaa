/**
 * <p>
 * A package containing a wide variety of selection algorithm,
 * most of them operating in-place.
 * </p><p>
 * A selection algorithm, also known as median selection algorithm,
 * finds the (i+1)-th smallest element of a sequence of element and
 * swaps it to index `i`. Every element less than element `i` is
 * moved to the left of `i`, and every element greater to the right,
 * albeit in no particular order (in many cases not even stable).
 * </p><p>
 * Every sorting algorithm is also a selection algorithm, but not
 * the other way around. While sorting requires at least O(n*log(n))
 * comparisons, good selection algorithms only require O(n) comparisons
 * (and swaps).
 * </p>
 * @see <a href="https://en.wikipedia.org/wiki/Selection_algorithm">Selection algorithm - Wikipedia</a>
 */
package com.github.jaaa.select;