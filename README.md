`JAAA` is a collection of advanced array-based algorithms for Java 8+.
Currently, the following main types of algorithms are included in this library:

  * Sorting
  * Merging
  * Searching
  * Selection
  * Heap Construction and Access
  * Partition

Design Goals
------------
The coding style of this library may seem a little off to the seasoned Java programmer.
Some Java veterans may be shocked by the lack of getters, setters, factories and even
dependency injection. Yet it is not all chaos and madness, but also some deliberate
consideration in order to best achieve the following goals:

  * __Performance:__ Implementations shall beat the benchmark (e.g. OpenJDK) wherever possible.
  * __Specialization:__ Performance shall be independent of the element type. One implementation
                        has to work well for `int[]` and `String[]`, no Boxing allowed.
  * __Abstraction:__ The notion of an array has to be as abstract as possible. Algorithms
                     have to work for arrays, buffers, `MemorySegment`, `RandomAccessFile`, ... 
  * __Readability:__ Implementations of individual algorithms are supposed be as close to
                     Pseudocode as Java allows.
  * __Modularity:__ Implementations of advanced algorithms are to be built on top of implementations
                    of simpler algorithms in a way that respects all the other design goals.  
  * __DRY:__ One algorithm implemented in one place only.

In order to achieve these goals, the algorithms are implemented in form of default methods
of interfaces. The interfaces represent the modules which are composed via inheritance.
The array access is achieved via abstract methods which have to be overridden for specific
array types.

Access-Based Algorithms
-----------------------
For in-place algorithms, access-style interfaces are used to access arrays. In-place
sorting algorithm for example, extend the `CompareSwapAccess` which has, as the name
suggests, two abstract methods: `compare(int,int)` and `swap(int,int)` which compare
and swap the given indices of an array. The following example uses the KiwiSort
algorithm to sort an array in-place:

```java
import com.github.jaaa.permute.Swap;
import com.github.jaaa.sort.KiwiSortAccess;
import java.util.Arrays;

public class Example1 {
  public static void main( String... args ) {
    int[] array = { 14, 11, 19, 2, 10, 14, 8, 15, 22, 8, 0, 22, 5, 21, 12, 2 };
    new KiwiSortAccess() {
      @Override public void   swap( int i, int j ) { Swap.swap(array,i,j); }
      @Override public int compare( int i, int j ) { return Integer.compare(array[i], array[j]); }
    }.kiwiSort(0,array.length);
    System.out.println( Arrays.toString(array) );
    // [0, 2, 2, 5, 8, 8, 10, 11, 12, 14, 14, 15, 19, 21, 22, 22]
  }
}
```

Note that this array access is incredibly flexible. Let's say, for example, we want
to sort a collection of 2d points, where the x-coordinates are stored in an array `x`,
and the y-coordinates in a separate array `y`. Here is an example of how to use the
HeapSort algorithm to sort such a collection of points:

```java
import com.github.jaaa.permute.Swap;
import com.github.jaaa.sort.HeapSortAccess;
import java.util.Arrays;

public class Example2 {
  public static void main( String... args ) {
    int[] x = {  0, 12,  8, 14, 19,  6, 3, 21, 18, 16, 22, 14, 7, 8, 8, 20 },
          y = { 13, 17, 12, 23, 13, 10, 1,  8,  3, 23, 18,  3, 8, 8, 5, 21 };
    new HeapSortAccess() {
      @Override public void swap( int i, int j ) {
        Swap.swap(x,i,j);
        Swap.swap(y,i,j);
      }
      @Override public int compare( int i, int j ) {
        int    c  =         Integer.compare(x[i], x[j]);
        return c != 0 ? c : Integer.compare(y[i], y[j]);
      }
    }.heapSort(0,x.length);
    System.out.println( Arrays.toString(x) );
    System.out.println( Arrays.toString(y) );
    // [ 0, 3,  6, 7, 8, 8,  8, 12, 14, 14, 16, 18, 19, 20, 21, 22]
    // [13, 1, 10, 8, 5, 8, 12, 17,  3, 23, 23,  3, 13, 21,  8, 18]
  }
}
```

Accessor-Based Algorithms
-------------------------
For algorithms which require temporary arrays as working memory, the access
pattern is insufficient. In these cases, accessor-style interfaces are used.
In case of sorting "arrays" of type `T`, the `RandomAccessor<T>` interface
is used which has the following abstract methods:

  * __compare(T a, int i, T b, int j):__ Compares `a[i]` to `b[j]`.
  * __copy(T a, int i, T b, int j):__ Copies the value of `a[i]` to `b[j]`.
  * __swap(T a, int i, T b, int j):__ Swaps the entries `a[i]` and `b[j]`.
  * __malloc( int len ):__ Allocates and returns a new array of type `T`.

A single sort accessor can be used to sort more than one array. The following
example shows how the TimSort algorithm can be used to sort two arrays of type
`String[]`. Note that the TimSort implementation allows for an initial working
array to be passed. If no such array is provided, `null,0,0` can be passed instead:

```java
import com.github.jaaa.permute.Swap;
import com.github.jaaa.sort.TimSortAccessor;
import java.util.Arrays;

public class Example3 {
  public static void main( String... args ) {
    TimSortAccessor<String[]> acc = new TimSortAccessor<String[]>() {
      @Override public String[] malloc( int len ) { return new String[len]; }
      @Override public void   swap( String[] a, int i, String[] b, int j ) { Swap.swap(a,i, b,j); }
      @Override public void   copy( String[] a, int i, String[] b, int j ) { b[j] = a[i]; }
      @Override public int compare( String[] a, int i, String[] b, int j ) { return a[i].compareTo(b[j]); }
    };
    String[] array1 = { "C", "P", "X", "N", "V", "X", "B", "M", "P", "E", "R", "E", "H", "J", "X", "K" },
             array2 = { "Q", "H", "B", "C", "V", "C", "B", "G", "Q" };
    acc.timSort(array1, 0, array1.length, /*init work mem*/null,0,0);
    acc.timSort(array2, 0, array2.length, /*init work mem*/null,0,0);
    System.out.println( Arrays.toString(array1) );
    System.out.println( Arrays.toString(array2) );
    // [B, C, E, E, H, J, K, M, N, P, P, R, V, X, X, X]
    // [B, B, C, C, G, H, Q, Q, V]
  }
}
```

Sorting Algorithms
------------------
As the examples above demonstrate `JAAA` offers a variety of in-place and
out-of-place sorting algorithms:

| Algorithm                 | Comparisons | Swaps/Copies | In-Place | Stable | Adaptive | Comment                |
|---------------------------|-------------|--------------|----------|--------|----------|------------------------|
| __HeapSort__              | O(n*log(n)) | O(n*log(n))  | Yes      | No     | No       |                        |
| __InsertionAdaptiveSort__ | O(n*log(n)) | O(n²)        | Yes      | Yes    | Yes      | Great for small arrays |
| __KiwiSort__              | O(n*log(n)) | O(n*log(n))  | Yes      | Yes    | Yes      | Enhanced WikiSort      |
| __QuickSort__             | O(n*log(n)) | O(n*log(n))  | Yes      | No     | No       |                        |
| __MergeSort__             | O(n*log(n)) | O(n*log(n))  | No       | Yes    | No       |                        |
| __ParallelSkipMergeSort__ | O(n*log(n)) | O(n*log(n))  | No       | Yes    | No       |                        |
| __ParallelZenMergeSort__  | O(n*log(n)) | O(n*log(n))  | No       | Yes    | Yes      |                        |
| __TimSort__               | O(n*log(n)) | O(n*log(n))  | No       | Yes    | No       | De-facto standard      |

Merge Algorithms
----------------
Merging algorithms take two sorted sequences of elements and merges them into a
single sorted sequence. The most commonly known merging algorithm is the TapeMerge
algorithm that is used in most MergeSort implementations. `TapeMerge` is excellent
for pairs of random sequences of roughly equal length. `TapeMerge` does however
lack the ability to adapt to the structure or the length difference of sequence
pairs. In these case, other algorithms may perform significantly better. The
following stable in-place algorithms are available:

| Algorithm          | Comparisons | Swaps | Adaptive |
|--------------------|-------------|-------|----------|
| __ExpMerge__       | O(n)        | O(n²) | Yes      |
| __HwangLingMerge__ | O(n)        | O(n²) | No       |
| __KiwiMerge__      | O(n)        | O(n)  | Yes      |
| __TapeMerge__      | O(n)        | O(n²) | No       |
| __TimMerge__       | O(n)        | O(n²) | Yes      |

The following stable out-of-place algorithms are available:

| Algorithm                 | Comparisons/Copies | Adaptive |
|---------------------------|--------------------|----------|
| __ExpMerge__              | O(n)               | Yes      |
| __HwangLingMerge__        | O(n)               | No       |
| __ParallelSkipMergeSort__ | O(n)               | No       |
| __ParallelZenMergeSort__  | O(n)               | Yes      |
| __TapeMerge__             | O(n)               | No       |
| __TimMerge__              | O(n)               | Yes      |

The following example demonstrates, how the KiwiMerge algorithm can be used to
merge two arrays:

```java
import com.github.jaaa.merge.KiwiMergeAccess;
import com.github.jaaa.permute.Swap;
import java.util.Arrays;

public class Example4 {
  public static void main( String... args ) {
    int[] a = { 0, 2, 12, 15, 17, 18, 19, 19, 22 },
          b = { 0, 2, 12, 14, 16, 17, 22 };
    int[] merged = new int[a.length + b.length];
    System.arraycopy(a,0, merged,0,        a.length);
    System.arraycopy(b,0, merged,a.length, b.length);
    new KiwiMergeAccess() {
      @Override public void swap( int i, int j ) { Swap.swap(merged,i,j); }
      @Override public int compare( int i, int j ) { return Integer.compare(merged[i], merged[j]); }
    }.kiwiMerge(0, a.length, merged.length);
    System.out.println( Arrays.toString(merged) );
    // [0, 0, 2, 2, 12, 12, 14, 15, 16, 17, 17, 18, 19, 19, 22, 22]
  }
}
```

Search Algorithms
-----------------
Search algorithms find the index of an element in a sorted array.
The most commonly known algorithm is [binary search](https://en.wikipedia.org/wiki/Binary_search_algorithm),
also known as bisection. Given a sorted array of `n` elements,
binary search requires `O(log(n))` operations to find an element.

The JDK comes with a binary search implementation in form of `Arrays.binarySearch`.
The implementation however has some limitations. Let's say for example
we want to look to the entry `7` in the array `{1,2,3,5,7,7,7,7,7,8,9}`.
Which index will `Arrays.binarySearch` return? Turns out it returns
the index of the first element it finds. This is not always desirable.
For stable insertion it may be preferable if the left-most or right-most
index is returned. This can be achieved by using a custom comparator but
this ist cumbersome and error-prone. `JAAA` therefore offers `searchL`
and `searchR` variants of every search algorithm to expressly look for
the left-most or right-most element. If an element is not found by
a search, the bit complement of the insertion index (i.e. a negative
integer) is returned. See the [Arrays.binarySearch](https://docs.oracle.com/en/java/javase/19/docs/api/java.base/java/util/Arrays.html#binarySearch(T%5B%5D,int,int,T,java.util.Comparator))
documentation for more information. If a search method is used for
insertion only, it may be more convenient, if search methods always
returned a positive integer index. `JAAA` offers the `searchGap`
(`searchGapL`, `searchGapR`) methods to achieve that.

A lesser known specialized variant of binary search is
[exponential search](https://en.wikipedia.org/wiki/Exponential_search).
Its perhaps most predominant use is as part of the
[TimSort](https://en.wikipedia.org/wiki/Tim_sort) algorithm where
it is referred to as "galloping search". Given a start index `i` for
the search, exponential search finds the correct index `j` of an
element in `O(log|i-j|)` operations. That means if a good start
index is chosen, exponential search will significantly outperform
binary search. `JAAA` offers different variants of exponential
search which choose different start indices for the search:

  * __ExpSearch:__ Accepts an explicit start index as argument.
  * __ExpL2RSearch:__ Always starts search at the left end.
  * __ExpR2LSearch:__ Always starts search at the right end.
  * __AkimboSearch:__ If the searched entry is in the left half,
                      it starts search at the left end, otherwise
                      it starts search at the right end.

The following example shows how an `ExpL2RSearchAccessor` can be
used to  find an element from one array in another array:

```java
import com.github.jaaa.search.ExpL2RSearchAccessor;

public class Example5 {
  public static void main( String... args ) {
    int[] array = { 0, 1, 1, 3, 5, 5, 6, 7, 7, 7, 8, 11, 12, 13, 14, 21 },
            key = { 7 };
    ExpL2RSearchAccessor<int[]> acc = (a,i, b,j) -> Integer.compare(a[i], b[j]);
    int index = acc.expL2RSearchL(array,0,array.length, key,0);
    System.out.println(index); // 7
  }
}
```

Search algorithms can be used without access-/accessor-style interfaces.
In those cases a "compass" function is used. For an argument `i` the
compass has to return `-1` if the searched element is to the left, `0`
if a searched element is found or `+1` if the search element is to the
right. The following example demonstrates how a compass can be used to
find the integer floor of a square root:

```java
import com.github.jaaa.search.ExpL2RSearch;
import java.util.stream.IntStream;
import static java.lang.Math.*;

public class Example6 {
  private static int sqrtFloor( int x ) {
    if( x < 0 ) throw new IllegalArgumentException();
    return ExpL2RSearch.searchGapL(0, Integer.MAX_VALUE, y -> Long.signum((long)y*y - x) );
  }
  public static void main( String... args ) {
    IntStream.rangeClosed(0, Integer.MAX_VALUE).parallel().forEach( x -> {
      assert sqrtFloor(x) == floor( sqrt(x) );
    });
  }
}
```

Selection Algorithms
--------------------
The selection problem is perhaps one of the most misunderstood and least
known algorithmic array problems. Given an (unsorted) array `a` and three
indices `from <= mid <= until`, a selection algorithm rearranges the 
elements in the range `[from,until)` of `a` such that the smallest `mid-from`
elements are moved to  the left and the `until-mid` larges elements are
moved to the right with `a[mid]` being the in-between element. In other words:
`a[from <= i <= mid] <= a[mid] <= a[mid <= j < until]`. The selection
problem naturally occurs as part of evolutionary algorithms. Computing the
median or the percentile of a data series is a selection problem as well.

Every sorting algorithm is a selection algorithm. A common misconception
among programmers is that sorting (which requires `O(log(n))` operations)
is the best possible solution. There are however `O(n)` selection algorithms
which have been known for decades.  Another misconception is that these
`O(n)` algorithms are slower than sorting in practice. In reality, most
selection algorithms easily beat TimSort for `until-from > 10_000`, especially
if `mid` is close to `from` or `until`. It can also be shown that sorting
algorithms like HeapSort, QuickSort or MergeSort can be turned into
a selection resulting in significant overhead reductions.

`JAAA` offers a wide variety of selection algorithms. All implementations
are unstable and in-place. Most in-place algorithms could easily be turned
into a stable out-of-place variants. The following algorithms are available:

| Algorithm       | Comparisons/Copies        | Adaptive | Comment                      |
|-----------------|---------------------------|----------|------------------------------|
| __QuickSelect__ | O(n)                      | No       |                              |
| __HeapSelect__  | O(max(l,r)*log(min(l,r))) | Yes      | `l=mid-from+1` `r=until-mid` |
| __Mom3Select__  | O(n)                      | No       | Enhanced Median-ofMedians    |
| __Mom5Select__  | O(n)                      | No       | Classic Median-of-Medians    |

The following example shows how to apply Mom3Select:

```java
import com.github.jaaa.permute.Swap;
import com.github.jaaa.select.Mom3SelectAccess;
import java.util.Arrays;

public class Example7 {
  public static void main( String... args ) {
    int[] array = { 14, 14, 0, 6, 13, 23, 9, 4, 22, 23, 5, 1, 7, 12, 7, 11 };
    new Mom3SelectAccess() {
      @Override public void   swap( int i, int j ) { Swap.swap(array,i,j); }
      @Override public int compare( int i, int j ) { return Integer.compare(array[i], array[j]); }
    }.heapSelect(/*from=*/0, /*mid=*/array.length/2, /*until=*/array.length);
    System.out.println( array[array.length/2] ); // 11
    System.out.println( Arrays.toString(array) );
    // [1, 4, 0, 6, 7, 5, 9, 7, 11, 23, 23, 22, 14, 14, 13, 12]
  }
}
```
