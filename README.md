Introduction
------------
`JAAA` is a collection of advanced array-based algorithms for Java.

Coding Style
------------
The coding style of this library may seem a little off to the seasoned Java programmer.
Some Java veterans may be shocked by the lack of getters, setters, factories and even
dependency injection. Yet it is not all chaos and madness, but also some deliberate
consideration in order to best achieve the following goals:

  * __Performance:__ Algorithmic implementations shall beat the benchmark (e.g. OpenJDK) wherever possible.
  * __Specialization:__ Performance should be independent of the element type. One implementation has to work as well
                        for `int[]` and `String[]`, no Boxing allowed.
  * __Generalization:__ The notion of an array should be abstract. Algorithms should be applicable to `byte`,
                        `ArrayList<Byte>` and `ByteBuffer` with next to no overhead or code duplication. 
  * __Readability:__ Implementations of the individual algorithms are supposed be as close to Pseudocode as Java allows.
  * __Composability:__ Implementations of advanced algorithms should be built on top of implementations of simpler
                       algorithms in a way that respects all the other design goals.  
  * __DRY:__ One algorithm implemented in one place only.

In order to achieve these design principles, two array access abstractions are use in the `JAAA` library:
Accessor and Access.

Access-Based Algorithms
-----------------------

Accessor-Based Algorithms
-------------------------

timestamp = 2021-02-09T20:59:56.414578827, MergeTestLarge:mergeArrayWithRangeInt =
java.lang.ArrayIndexOutOfBoundsException:
arraycopy: last source index 1 out of bounds for int[0]

                              |-------------------jqwik-------------------
tries = 1                     | # of calls to property
checks = 1                    | # of not rejected calls
generation = RANDOMIZED       | parameters are randomly generated
after-failure = PREVIOUS_SEED | use the previous seed
edge-cases#mode = MIXIN       | edge cases are mixed in
edge-cases#total = 361        | # of all combined edge cases
edge-cases#tried = 0          | # of edge cases tried in current run
seed = -5195463326180319347   | random seed to reproduce generated values

Shrunk Sample (3130 steps)
--------------------------
ref:
(
WithRange{ from: 0, until: 0, data: [] },
WithRange{ from: 0, until: 1, data: [0] },
WithIndex{ index: 0, data: [0] }
)

After Execution
---------------
    ref:
      (
        WithRange{ from: 0, until: 0, data: [] }, 
        WithRange{ from: 0, until: 1, data: [0] }, 
        WithIndex{ index: 0, data: [0] }
      )

Original Sample
---------------
ref:
(
WithRange{ from: 2, until: 3, data: [299, -36129, -1931856046, -5, -5246204] },
WithRange{ from: 2, until: 19, data: [446952, -47, -3886, -226, 231, 669, -2, 51695, 5, 1155, -991075, 178080, 596618, 219, -58522, -15212, -13297, -1098410171, -16, -24475, 2147483646, 17509, -3291485, 1225424, 12, -35066, 61] },
WithIndex{ index: 14, data: [-128, -1885, 15517, 57, 13, -4765158, -71, -166809, 146252, 0, -297929, -3857676, 0, -7599, 53, 1794524, -158199, -5, -1, 4077664, 35, -6, 35, -69650, 2727311, -1821, 0, 1213, 653767220, -7621, -24040, -42, 504308195, -4048934, 5, 0, -69325, -60, 457, -2, 136179, 29440, 40568, -3, 1530603, 121, -6789] }
)

After Execution
---------------
    ref:
      (
        WithRange{ from: 2, until: 3, data: [299, -36129, -1931856046, -5, -5246204] }, 
        WithRange{ from: 2, until: 19, data: [446952, -47, -1098410171, -991075, -58522, -15212, -13297, -3886, -226, -16, -2, 5, 219, 231, 669, 1155, 51695, 178080, 596618, -24475, 2147483646, 17509, -3291485, 1225424, 12, -35066, 61] }, 
        WithIndex{ index: 14, data: [-128, -1885, 15517, 57, 13, -4765158, -71, -166809, 146252, 0, -297929, -3857676, 0, -7599, -1931856046, -1098410171, -991075, -58522, -15212, -13297, -3886, -226, -16, -2, 5, 219, 231, 669, 1155, 51695, 178080, 596618, 504308195, -4048934, 5, 0, -69325, -60, 457, -2, 136179, 29440, 40568, -3, 1530603, 121, -6789] }
      )

Original Error
--------------
java.lang.ArrayIndexOutOfBoundsException:
arraycopy: last source index 19 out of bounds for int[5]
