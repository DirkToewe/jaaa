package com.github.jaaa.select;

import static com.github.jaaa.util.FMath.log2;
import static java.lang.Math.log;
import static java.lang.Math.round;


public final class HeapSelect
{
  static long performance_worstCase( int heapSize, int restSize ) {
    if( restSize  < 0 || heapSize < 1 ) throw new IllegalArgumentException();
    if( restSize == 0 ) return heapSize;
    if( heapSize == 1 ) return restSize;
    double m = heapSize,
           n = restSize,
        logM = log2(m+1),
        cost = 0;
    // building the heap (see https://stackoverflow.com/questions/69620948/build-heap-in-on-max-number-of-comparisons)
    cost += 2 * (m - logM);
    // enqueueing every element from other side into heap
    cost += (2*logM - 1) * n;
    return round(cost);
  }

  static long performance_average( int heapSize, int restSize ) {
    if( restSize  < 0 || heapSize < 1 ) throw new IllegalArgumentException();
    if( restSize == 0 ) return heapSize;
    if( heapSize == 1 ) return restSize;
    double m = heapSize,
           n = restSize,
        logM = log2(m+1),
        cost = 0;
    // Heap Construction, See: https://stackoverflow.com/questions/69620948/build-heap-in-on-max-number-of-comparisons)
    cost += 1.88 * (m - logM);
    // Comparing top of heap to every element on other side
    cost += n;
    // Enqueueing rest elements into heap. In the random
    // case this is somewhat tricky to estimate.
    //
    // For simplicity, let us assume that - at all times -
    // each level of the heap is strictly less than or equal
    // to the level above it.
    //
    // Let's look the element at index i > m.
    //
    // If element i belongs to the smallest m elements
    // encountered so far, it has to be enqueued into
    // the heap, requiring at least 2 comparisons for
    // a single sift-down step.
    //
    // If element i belongs to the smallest m-1 elements,
    // another sift-down step is required, i.e. at least
    // 4 comparison.
    //
    // If element i belongs to the smallest m-3 elements,
    // at least 6 comparisons are required.
    //
    // If element i belongs to the smallest m-7 elements,
    // at least 8 comparisons are required.
    //
    // If element i belongs to the smallest m-15 elements,
    // at least 10 comparisons are required.
    //
    // If element i belongs to the smallest m+1-2^k elements,
    // at least 2*k comparisons are required.
    //
    // For randomly shuffled input data, the probability that
    // element i belongs to the smallest m-2^k elements is (m-2^k) / (i+1).
    //
    // For enqueueing element i, the expected number of
    // sift-down steps e[i] should therefore be:
    //
    // e[i] = sum[k=0...log2(m+1)]( m+1 - 2^k ) / (i+1) ~= (log2(m+1)*(m+1) - m) / (i+1)
    //
    // The total number of sift-down steps should be:
    //
    // E = sum[i=m...m+n] e[i] ~= ( log(m+1)*m - m ) * (1/(m+1) + 1/(m+2) + ... + 1/(m+n))
    //                         ~= ( log(m+1)*m - m ) (H[m+n] - H[m])
    //
    // Where H[i] is the i-th harmonic number, see: https://en.wikipedia.org/wiki/Harmonic_number
    // With the approximation H[i] ~= log(i), we can
    // approximate the number of comparisons to:
    cost += log((m+n)/m) * (logM*(m+1) - m) * 2;
    return round(cost);
  }

  private HeapSelect() {
    throw new UnsupportedOperationException("Cannot instantiate static class.");
  }
}
