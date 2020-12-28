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
