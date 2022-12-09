package com.github.jaaa.merge;

import com.github.jaaa.permute.Swap;
import net.jqwik.api.*;
import net.jqwik.api.Tuple.Tuple2;
import net.jqwik.api.constraints.Size;

import java.util.Arrays;
import java.util.Comparator;
import java.util.TreeSet;

import static com.github.jaaa.Boxing.boxed;
import static java.lang.Math.max;
import static java.lang.Math.min;
import static java.lang.String.format;
import static java.lang.System.arraycopy;
import static java.util.Arrays.copyOfRange;
import static java.util.Arrays.stream;
import static java.util.Comparator.comparing;
import static java.util.stream.IntStream.range;
import static org.assertj.core.api.Assertions.assertThat;


@Group
@PropertyDefaults( tries = 100_000 )
public class ExtractMergeBufOrdinalAccessTest
{
  private static final int MAX_SIZE = 8192;

  private record ExtractMergeBufArgs<T>( T[] arr, int from, int until, int desiredLen, int destination )
  {
    @Override public String toString() {
      return format(
        "{\n  arr: %s,\n  from: %d, until: %d, desiredLen: %d, destination: %d\n}",
        Arrays.toString(arr), from,until, desiredLen, destination
      );
    }
  }

  private static <T> Arbitrary<ExtractMergeBufArgs<T>> extractMergeBufArgs_withRange( T[] arr ) {
    return Arbitraries.integers().between(0,arr.length).tuple3().flatMap( args -> {
      int   from = min(args.get1(), args.get2()),
           until = max(args.get1(), args.get2());
      return           Arbitraries.integers().between(0, 2+2*(until-from)).flatMap(
        desiredLen  -> Arbitraries.integers().between(0, max(0, arr.length - desiredLen)).map(
        destination -> new ExtractMergeBufArgs<>(arr, from, until, desiredLen, destination)
      ));
    });
  }

  @Group class Test_extractMergeBuf_ordinal_l_min_sorted
  {
    @Provide Arbitrary<ExtractMergeBufArgs<Byte>> extractMergeBufArgs_withRange_small_Byte() {
      return   Arbitraries.integers().between(1, 16).flatMap(
        len -> Arbitraries.integers().between(0, (1<<len-1)-1).flatMap(
          bits -> {
            var arr = new byte[len];
            for( int i=1; i < len; i++ ) {
              arr[i] = arr[i-1];
              arr[i]+= 1 & bits>>>i-1;
            }
            return           Arbitraries.integers().between(0, len).flatMap(
              desiredLen  -> Arbitraries.integers().between(0, len - desiredLen).map(
              destination -> new ExtractMergeBufArgs<>(boxed(arr), 0,len, desiredLen, destination)
            ));
          }
        )
      );
    }

    @Provide Arbitrary<ExtractMergeBufArgs<Boolean  >> extractMergeBufArgs_withRange_Boolean  ( @ForAll @Size(max=MAX_SIZE) Boolean  [] arr ) { return extractMergeBufArgs_withRange(arr); }
    @Provide Arbitrary<ExtractMergeBufArgs<Byte     >> extractMergeBufArgs_withRange_Byte     ( @ForAll @Size(max=MAX_SIZE) Byte     [] arr ) { return extractMergeBufArgs_withRange(arr); }
    @Provide Arbitrary<ExtractMergeBufArgs<Short    >> extractMergeBufArgs_withRange_Short    ( @ForAll @Size(max=MAX_SIZE) Short    [] arr ) { return extractMergeBufArgs_withRange(arr); }
    @Provide Arbitrary<ExtractMergeBufArgs<Integer  >> extractMergeBufArgs_withRange_Integer  ( @ForAll @Size(max=MAX_SIZE) Integer  [] arr ) { return extractMergeBufArgs_withRange(arr); }
    @Provide Arbitrary<ExtractMergeBufArgs<Long     >> extractMergeBufArgs_withRange_Long     ( @ForAll @Size(max=MAX_SIZE) Long     [] arr ) { return extractMergeBufArgs_withRange(arr); }
    @Provide Arbitrary<ExtractMergeBufArgs<Character>> extractMergeBufArgs_withRange_Character( @ForAll @Size(max=MAX_SIZE) Character[] arr ) { return extractMergeBufArgs_withRange(arr); }
    @Provide Arbitrary<ExtractMergeBufArgs<Float    >> extractMergeBufArgs_withRange_Float    ( @ForAll @Size(max=MAX_SIZE) Float    [] arr ) { return extractMergeBufArgs_withRange(arr); }
    @Provide Arbitrary<ExtractMergeBufArgs<Double   >> extractMergeBufArgs_withRange_Double   ( @ForAll @Size(max=MAX_SIZE) Double   [] arr ) { return extractMergeBufArgs_withRange(arr); }
    @Provide Arbitrary<ExtractMergeBufArgs<String   >> extractMergeBufArgs_withRange_String   ( @ForAll @Size(max=MAX_SIZE) String   [] arr ) { return extractMergeBufArgs_withRange(arr); }

    @Property                         void extractsUnstably_withRange_small_Byte( @ForAll("extractMergeBufArgs_withRange_small_Byte") ExtractMergeBufArgs<Byte     > sample, @ForAll Comparator<Byte     > cmp ) { extractsUnstably(sample,cmp); }
    @Property                         void extractsUnstably_withRange_Boolean   ( @ForAll("extractMergeBufArgs_withRange_Boolean"   ) ExtractMergeBufArgs<Boolean  > sample, @ForAll Comparator<Boolean  > cmp ) { extractsUnstably(sample,cmp); }
    @Property                         void extractsUnstably_withRange_Byte      ( @ForAll("extractMergeBufArgs_withRange_Byte"      ) ExtractMergeBufArgs<Byte     > sample, @ForAll Comparator<Byte     > cmp ) { extractsUnstably(sample,cmp); }
    @Property                         void extractsUnstably_withRange_Short     ( @ForAll("extractMergeBufArgs_withRange_Short"     ) ExtractMergeBufArgs<Short    > sample, @ForAll Comparator<Short    > cmp ) { extractsUnstably(sample,cmp); }
    @Property                         void extractsUnstably_withRange_Integer   ( @ForAll("extractMergeBufArgs_withRange_Integer"   ) ExtractMergeBufArgs<Integer  > sample, @ForAll Comparator<Integer  > cmp ) { extractsUnstably(sample,cmp); }
    @Property                         void extractsUnstably_withRange_Long      ( @ForAll("extractMergeBufArgs_withRange_Long"      ) ExtractMergeBufArgs<Long     > sample, @ForAll Comparator<Long     > cmp ) { extractsUnstably(sample,cmp); }
    @Property                         void extractsUnstably_withRange_Character ( @ForAll("extractMergeBufArgs_withRange_Character" ) ExtractMergeBufArgs<Character> sample, @ForAll Comparator<Character> cmp ) { extractsUnstably(sample,cmp); }
    @Property                         void extractsUnstably_withRange_Float     ( @ForAll("extractMergeBufArgs_withRange_Float"     ) ExtractMergeBufArgs<Float    > sample, @ForAll Comparator<Float    > cmp ) { extractsUnstably(sample,cmp); }
    @Property                         void extractsUnstably_withRange_Double    ( @ForAll("extractMergeBufArgs_withRange_Double"    ) ExtractMergeBufArgs<Double   > sample, @ForAll Comparator<Double   > cmp ) { extractsUnstably(sample,cmp); }
//    @Property                         void extractsUnstably_withRange_String    ( @ForAll("extractMergeBufArgs_withRange_String"    ) ExtractMergeBufArgs<String   > sample, @ForAll Comparator<String   > cmp ) { extractsUnstably(sample,cmp); }
    <T extends Comparable<? super T>> void extractsUnstably                     ( ExtractMergeBufArgs<T> args, Comparator<? super T> cmp )
    {
      int from = args.from,
         until = args.until,
        desLen = args.desiredLen,
           dst = args.destination;

      var ref = args.arr.clone();
      Arrays.sort(ref, from,until, cmp);
      var tst = ref.clone();

      class Acc implements ExtractMergeBufOrdinalAccess {
        long nSwaps = 0;
        @Override public void   swap( int i, int j ) { ++nSwaps; Swap.swap(tst,i,j); }
        @Override public int compare( int i, int j ) { return cmp.compare(tst[i], tst[j]); }
      }

      var           acc = new Acc();
      int nUnique = acc.extractMergeBuf_ordinal_l_min_sorted(from,until, desLen, dst);

      assertThat(acc.nSwaps).isLessThanOrEqualTo(
          until-from // <- for "combing" through the range
        + max(until-dst, dst+nUnique-from) // <- for moving buffer to destination
        + nUnique*(nUnique+1L)/2 // <- for collecting buffer elements
      );

      assertThat(nUnique).isBetween(0 < desLen && from < until ? 1 : 0, desLen);

      var buf = copyOfRange(tst, dst,dst+nUnique);

      assertThat(buf).isEqualTo(
        stream(ref, from,until).filter(new TreeSet<>(cmp)::add).limit(desLen).toArray(Object[]::new)
      );

      arraycopy(tst,dst+nUnique, tst,dst,          tst.length-nUnique-dst );
      arraycopy(tst,from,        tst,from+nUnique, tst.length-nUnique-from);

      assertThat( copyOfRange(tst,from+nUnique,until) ).isSortedAccordingTo(cmp);

      TapeMerge.merge(buf,0,nUnique, tst,from+nUnique,until-from-nUnique, tst,from, cmp);
      assertThat(tst).isEqualTo(ref);
    }

    @Property                         void extractsStably_withRange_small_Byte( @ForAll("extractMergeBufArgs_withRange_small_Byte") ExtractMergeBufArgs<Byte     > sample, @ForAll Comparator<Byte     > cmp ) { extractsStably(sample,cmp); }
    @Property                         void extractsStably_withRange_Boolean   ( @ForAll("extractMergeBufArgs_withRange_Boolean"   ) ExtractMergeBufArgs<Boolean  > sample, @ForAll Comparator<Boolean  > cmp ) { extractsStably(sample,cmp); }
    @Property                         void extractsStably_withRange_Byte      ( @ForAll("extractMergeBufArgs_withRange_Byte"      ) ExtractMergeBufArgs<Byte     > sample, @ForAll Comparator<Byte     > cmp ) { extractsStably(sample,cmp); }
    @Property                         void extractsStably_withRange_Short     ( @ForAll("extractMergeBufArgs_withRange_Short"     ) ExtractMergeBufArgs<Short    > sample, @ForAll Comparator<Short    > cmp ) { extractsStably(sample,cmp); }
    @Property                         void extractsStably_withRange_Integer   ( @ForAll("extractMergeBufArgs_withRange_Integer"   ) ExtractMergeBufArgs<Integer  > sample, @ForAll Comparator<Integer  > cmp ) { extractsStably(sample,cmp); }
    @Property                         void extractsStably_withRange_Long      ( @ForAll("extractMergeBufArgs_withRange_Long"      ) ExtractMergeBufArgs<Long     > sample, @ForAll Comparator<Long     > cmp ) { extractsStably(sample,cmp); }
    @Property                         void extractsStably_withRange_Character ( @ForAll("extractMergeBufArgs_withRange_Character" ) ExtractMergeBufArgs<Character> sample, @ForAll Comparator<Character> cmp ) { extractsStably(sample,cmp); }
    @Property                         void extractsStably_withRange_Float     ( @ForAll("extractMergeBufArgs_withRange_Float"     ) ExtractMergeBufArgs<Float    > sample, @ForAll Comparator<Float    > cmp ) { extractsStably(sample,cmp); }
    @Property                         void extractsStably_withRange_Double    ( @ForAll("extractMergeBufArgs_withRange_Double"    ) ExtractMergeBufArgs<Double   > sample, @ForAll Comparator<Double   > cmp ) { extractsStably(sample,cmp); }
//    @Property                         void extractsStably_withRange_String    ( @ForAll("extractMergeBufArgs_withRange_String"    ) ExtractMergeBufArgs<String   > sample, @ForAll Comparator<String   > cmp ) { extractsStably(sample,cmp); }
    <T extends Comparable<? super T>> void extractsStably                     ( ExtractMergeBufArgs<T> args, Comparator<? super T> _cmp )
    {
      var raw = args.arr;
      int from = args.from,
         until = args.until,
        desLen = args.desiredLen,
           dst = args.destination;
      Comparator<Tuple2<T,Integer>> cmp = comparing(Tuple2::get1, _cmp);

      @SuppressWarnings("unchecked")
      Tuple2<T,Integer>[] ref = range(0,raw.length).mapToObj( i -> Tuple.of(raw[i],i) ).toArray(Tuple2[]::new);
      Arrays.sort(ref, from,until, cmp);
      var tst = ref.clone();

      class Acc implements ExtractMergeBufOrdinalAccess {
        long nSwaps = 0;
        @Override public void   swap( int i, int j ) { ++nSwaps; Swap.swap(tst,i,j); }
        @Override public int compare( int i, int j ) { return cmp.compare(tst[i], tst[j]); }
      }

      var           acc = new Acc();
      int nUnique = acc.extractMergeBuf_ordinal_l_min_sorted(from,until, desLen, dst);

      assertThat(acc.nSwaps).isLessThanOrEqualTo(
          until-from // <- for "combing" through the range
        + max(until-dst, dst+nUnique-from) // <- for moving buffer to destination
        + nUnique*(nUnique+1L)/2 // <- for collecting buffer elements
      );

      assertThat(nUnique).isBetween(0 < desLen && from < until ? 1 : 0, desLen);

      var buf = copyOfRange(tst, dst,dst+nUnique);

      assertThat(buf).isEqualTo(
        stream(ref, from,until).filter(new TreeSet<>(cmp)::add).limit(desLen).toArray(Tuple2[]::new)
      );

      arraycopy(tst,dst+nUnique, tst,dst,          tst.length-nUnique-dst );
      arraycopy(tst,from,        tst,from+nUnique, tst.length-nUnique-from);

      assertThat( copyOfRange(tst,from+nUnique,until) ).isSortedAccordingTo(cmp);

      TapeMerge.merge(buf,0,nUnique, tst,from+nUnique,until-from-nUnique, tst,from, cmp);
      assertThat(tst).isEqualTo(ref);
    }
  }

  @Group class Test_extractMergeBuf_ordinal_l_min_unsorted
  {
    @Provide Arbitrary<ExtractMergeBufArgs<Byte>> extractMergeBufArgs_withRange_small_Byte() {
      return   Arbitraries.integers().between(1, 16).flatMap(
        len -> Arbitraries.integers().between(0, (1<<len-1)-1).flatMap(
          bits -> {
            var arr = new byte[len];
            for( int i=1; i < len; i++ ) {
              arr[i] = arr[i-1];
              arr[i]+= 1 & bits>>>i-1;
            }
            return           Arbitraries.integers().between(0, len).flatMap(
              desiredLen  -> Arbitraries.integers().between(0, len - desiredLen).map(
              destination -> new ExtractMergeBufArgs<>(boxed(arr), 0,len, desiredLen, destination)
            ));
          }
        )
      );
    }

    @Provide Arbitrary<ExtractMergeBufArgs<Boolean  >> extractMergeBufArgs_withRange_Boolean  ( @ForAll @Size(max=MAX_SIZE) Boolean  [] arr ) { return extractMergeBufArgs_withRange(arr); }
    @Provide Arbitrary<ExtractMergeBufArgs<Byte     >> extractMergeBufArgs_withRange_Byte     ( @ForAll @Size(max=MAX_SIZE) Byte     [] arr ) { return extractMergeBufArgs_withRange(arr); }
    @Provide Arbitrary<ExtractMergeBufArgs<Short    >> extractMergeBufArgs_withRange_Short    ( @ForAll @Size(max=MAX_SIZE) Short    [] arr ) { return extractMergeBufArgs_withRange(arr); }
    @Provide Arbitrary<ExtractMergeBufArgs<Integer  >> extractMergeBufArgs_withRange_Integer  ( @ForAll @Size(max=MAX_SIZE) Integer  [] arr ) { return extractMergeBufArgs_withRange(arr); }
    @Provide Arbitrary<ExtractMergeBufArgs<Long     >> extractMergeBufArgs_withRange_Long     ( @ForAll @Size(max=MAX_SIZE) Long     [] arr ) { return extractMergeBufArgs_withRange(arr); }
    @Provide Arbitrary<ExtractMergeBufArgs<Character>> extractMergeBufArgs_withRange_Character( @ForAll @Size(max=MAX_SIZE) Character[] arr ) { return extractMergeBufArgs_withRange(arr); }
    @Provide Arbitrary<ExtractMergeBufArgs<Float    >> extractMergeBufArgs_withRange_Float    ( @ForAll @Size(max=MAX_SIZE) Float    [] arr ) { return extractMergeBufArgs_withRange(arr); }
    @Provide Arbitrary<ExtractMergeBufArgs<Double   >> extractMergeBufArgs_withRange_Double   ( @ForAll @Size(max=MAX_SIZE) Double   [] arr ) { return extractMergeBufArgs_withRange(arr); }
    @Provide Arbitrary<ExtractMergeBufArgs<String   >> extractMergeBufArgs_withRange_String   ( @ForAll @Size(max=MAX_SIZE) String   [] arr ) { return extractMergeBufArgs_withRange(arr); }

    @Property                         void extractsUnstably_withRange_small_Byte( @ForAll("extractMergeBufArgs_withRange_small_Byte") ExtractMergeBufArgs<Byte     > sample, @ForAll Comparator<Byte     > cmp ) { extractsUnstably(sample,cmp); }
    @Property                         void extractsUnstably_withRange_Boolean   ( @ForAll("extractMergeBufArgs_withRange_Boolean"   ) ExtractMergeBufArgs<Boolean  > sample, @ForAll Comparator<Boolean  > cmp ) { extractsUnstably(sample,cmp); }
    @Property                         void extractsUnstably_withRange_Byte      ( @ForAll("extractMergeBufArgs_withRange_Byte"      ) ExtractMergeBufArgs<Byte     > sample, @ForAll Comparator<Byte     > cmp ) { extractsUnstably(sample,cmp); }
    @Property                         void extractsUnstably_withRange_Short     ( @ForAll("extractMergeBufArgs_withRange_Short"     ) ExtractMergeBufArgs<Short    > sample, @ForAll Comparator<Short    > cmp ) { extractsUnstably(sample,cmp); }
    @Property                         void extractsUnstably_withRange_Integer   ( @ForAll("extractMergeBufArgs_withRange_Integer"   ) ExtractMergeBufArgs<Integer  > sample, @ForAll Comparator<Integer  > cmp ) { extractsUnstably(sample,cmp); }
    @Property                         void extractsUnstably_withRange_Long      ( @ForAll("extractMergeBufArgs_withRange_Long"      ) ExtractMergeBufArgs<Long     > sample, @ForAll Comparator<Long     > cmp ) { extractsUnstably(sample,cmp); }
    @Property                         void extractsUnstably_withRange_Character ( @ForAll("extractMergeBufArgs_withRange_Character" ) ExtractMergeBufArgs<Character> sample, @ForAll Comparator<Character> cmp ) { extractsUnstably(sample,cmp); }
    @Property                         void extractsUnstably_withRange_Float     ( @ForAll("extractMergeBufArgs_withRange_Float"     ) ExtractMergeBufArgs<Float    > sample, @ForAll Comparator<Float    > cmp ) { extractsUnstably(sample,cmp); }
    @Property                         void extractsUnstably_withRange_Double    ( @ForAll("extractMergeBufArgs_withRange_Double"    ) ExtractMergeBufArgs<Double   > sample, @ForAll Comparator<Double   > cmp ) { extractsUnstably(sample,cmp); }
    //    @Property                         void extractsUnstably_withRange_String    ( @ForAll("extractMergeBufArgs_withRange_String"    ) ExtractMergeBufArgs<String   > sample, @ForAll Comparator<String   > cmp ) { extractsUnstably(sample,cmp); }
    <T extends Comparable<? super T>> void extractsUnstably                     ( ExtractMergeBufArgs<T> args, Comparator<? super T> cmp )
    {
      int from = args.from,
         until = args.until,
        desLen = args.desiredLen,
           dst = args.destination;

      var ref = args.arr.clone();
      Arrays.sort(ref, from,until, cmp);
      var tst = ref.clone();

      class Acc implements ExtractMergeBufOrdinalAccess {
        long nSwaps = 0;
        @Override public void   swap( int i, int j ) { ++nSwaps; Swap.swap(tst,i,j); }
        @Override public int compare( int i, int j ) { return cmp.compare(tst[i], tst[j]); }
      }

      var           acc = new Acc();
      int nUnique = acc.extractMergeBuf_ordinal_l_min_unsorted(from,until, desLen, dst);

      assertThat(acc.nSwaps).isLessThanOrEqualTo(
          until-from // <- for "combing" through the range
        + max(until-dst, dst+nUnique-from) // <- for moving buffer to destination
      );

      assertThat(nUnique).isBetween(0 < desLen && from < until ? 1 : 0, desLen);

      var buf = copyOfRange(tst, dst,dst+nUnique);
      Arrays.sort(buf, cmp);

      assertThat(buf).isEqualTo(
        stream(ref, from,until).filter(new TreeSet<>(cmp)::add).limit(desLen).toArray(Object[]::new)
      );

      System.arraycopy(tst,dst+nUnique, tst,dst,          tst.length-nUnique-dst );
      System.arraycopy(tst,from,        tst,from+nUnique, tst.length-nUnique-from);

      assertThat( copyOfRange(tst,from+nUnique,until) ).isSortedAccordingTo(cmp);

      TapeMerge.merge(buf,0,nUnique, tst,from+nUnique,until-from-nUnique, tst,from, cmp);
      assertThat(tst).isEqualTo(ref);
    }

    @Property                         void extractsStably_withRange_small_Byte( @ForAll("extractMergeBufArgs_withRange_small_Byte") ExtractMergeBufArgs<Byte     > sample, @ForAll Comparator<Byte     > cmp ) { extractsStably(sample,cmp); }
    @Property                         void extractsStably_withRange_Boolean   ( @ForAll("extractMergeBufArgs_withRange_Boolean"   ) ExtractMergeBufArgs<Boolean  > sample, @ForAll Comparator<Boolean  > cmp ) { extractsStably(sample,cmp); }
    @Property                         void extractsStably_withRange_Byte      ( @ForAll("extractMergeBufArgs_withRange_Byte"      ) ExtractMergeBufArgs<Byte     > sample, @ForAll Comparator<Byte     > cmp ) { extractsStably(sample,cmp); }
    @Property                         void extractsStably_withRange_Short     ( @ForAll("extractMergeBufArgs_withRange_Short"     ) ExtractMergeBufArgs<Short    > sample, @ForAll Comparator<Short    > cmp ) { extractsStably(sample,cmp); }
    @Property                         void extractsStably_withRange_Integer   ( @ForAll("extractMergeBufArgs_withRange_Integer"   ) ExtractMergeBufArgs<Integer  > sample, @ForAll Comparator<Integer  > cmp ) { extractsStably(sample,cmp); }
    @Property                         void extractsStably_withRange_Long      ( @ForAll("extractMergeBufArgs_withRange_Long"      ) ExtractMergeBufArgs<Long     > sample, @ForAll Comparator<Long     > cmp ) { extractsStably(sample,cmp); }
    @Property                         void extractsStably_withRange_Character ( @ForAll("extractMergeBufArgs_withRange_Character" ) ExtractMergeBufArgs<Character> sample, @ForAll Comparator<Character> cmp ) { extractsStably(sample,cmp); }
    @Property                         void extractsStably_withRange_Float     ( @ForAll("extractMergeBufArgs_withRange_Float"     ) ExtractMergeBufArgs<Float    > sample, @ForAll Comparator<Float    > cmp ) { extractsStably(sample,cmp); }
    @Property                         void extractsStably_withRange_Double    ( @ForAll("extractMergeBufArgs_withRange_Double"    ) ExtractMergeBufArgs<Double   > sample, @ForAll Comparator<Double   > cmp ) { extractsStably(sample,cmp); }
    //    @Property                         void extractsStably_withRange_String    ( @ForAll("extractMergeBufArgs_withRange_String"    ) ExtractMergeBufArgs<String   > sample, @ForAll Comparator<String   > cmp ) { extractsStably(sample,cmp); }
    <T extends Comparable<? super T>> void extractsStably                     ( ExtractMergeBufArgs<T> args, Comparator<? super T> _cmp )
    {
      var raw = args.arr;
      int from = args.from,
              until = args.until,
              desLen = args.desiredLen,
              dst = args.destination;
      Comparator<Tuple2<T,Integer>> cmp = comparing(Tuple2::get1, _cmp);

      @SuppressWarnings("unchecked")
      Tuple2<T,Integer>[] ref = range(0,raw.length).mapToObj( i -> Tuple.of(raw[i],i) ).toArray(Tuple2[]::new);
      Arrays.sort(ref, from,until, cmp);
      var tst = ref.clone();

      class Acc implements ExtractMergeBufOrdinalAccess {
        long nSwaps = 0;
        @Override public void   swap( int i, int j ) { ++nSwaps; Swap.swap(tst,i,j); }
        @Override public int compare( int i, int j ) { return cmp.compare(tst[i], tst[j]); }
      }

      var           acc = new Acc();
      int nUnique = acc.extractMergeBuf_ordinal_l_min_unsorted(from,until, desLen, dst);

      assertThat(acc.nSwaps).isLessThanOrEqualTo(
          until-from // <- for "combing" through the range
        + max(until-dst, dst+nUnique-from) // <- for moving buffer to destination
      );

      assertThat(nUnique).isBetween(0 < desLen && from < until ? 1 : 0, desLen);

      var buf = copyOfRange(tst, dst,dst+nUnique);
      Arrays.sort(buf, cmp);

      assertThat(buf).isEqualTo(
        stream(ref, from,until).filter(new TreeSet<>(cmp)::add).limit(desLen).toArray(Tuple2[]::new)
      );

      System.arraycopy(tst,dst+nUnique, tst,dst,          tst.length-nUnique-dst );
      System.arraycopy(tst,from,        tst,from+nUnique, tst.length-nUnique-from);

      assertThat( copyOfRange(tst,from+nUnique,until) ).isSortedAccordingTo(cmp);

      TapeMerge.merge(buf,0,nUnique, tst,from+nUnique,until-from-nUnique, tst,from, cmp);
      assertThat(tst).isEqualTo(ref);
    }
  }
}
