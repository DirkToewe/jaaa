package com.github.jaaa.sort;

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
import static java.util.Arrays.*;
import static java.util.Comparator.comparing;
import static java.util.stream.IntStream.range;
import static net.jqwik.api.Arbitraries.integers;
import static org.assertj.core.api.Assertions.assertThat;


@Group
@PropertyDefaults( tries = 100_000 )
public class ExtractSortBufOrdinalAccessTest
{
  private static final int MAX_SIZE = 8192;

  private record ExtractSortBufArgs<T>( T[] arr, int from, int until, int desiredLen, int destination )
  {
    @Override public String toString() {
      return format(
        "{\n  arr: %s,\n  from: %d, until: %d, desiredLen: %d, destination: %d\n}",
        Arrays.toString(arr), from,until, desiredLen, destination
      );
    }
  }

  private static <T> Arbitrary<ExtractSortBufArgs<T>> extractSortBufArgs_withRange(T[] arr ) {
    return integers().between(0,arr.length).tuple3().flatMap( args -> {
      int from = min(args.get1(), args.get2()),
         until = max(args.get1(), args.get2());
      return Arbitraries.integers().between(0, 2+2*(until-from)).flatMap(
        desiredLen  -> Arbitraries.integers().between(0, max(0, arr.length - desiredLen)).map(
        destination -> new ExtractSortBufArgs<>(arr, from, until, desiredLen, destination)
      ));
    });
  }

  @Group class Test_extractSortBuf_ordinal_l_sorted
  {
    @Provide Arbitrary<ExtractSortBufArgs<Byte>> extractSortBufArgs_withRange_small_Byte() {
      return integers().between(1, 16).flatMap(
         len -> integers().between(0, (1<<len-1)-1).flatMap(
           bits -> {
             var arr = new byte[len];
             for( int i=1; i < len; i++ ) {
               arr[i] = arr[i-1];
               arr[i]+= 1 & bits>>>i-1;
             }
             return           integers().between(0, len).flatMap(
               desiredLen  -> integers().between(0, len - desiredLen).map(
               destination -> new ExtractSortBufArgs<>(boxed(arr), 0,len, desiredLen, destination)
             ));
           }
         )
      );
    }

    @Provide Arbitrary<ExtractSortBufArgs<Boolean  >> extractSortBufArgs_withRange_Boolean  (@ForAll @Size(max=MAX_SIZE) Boolean  [] arr ) { return extractSortBufArgs_withRange(arr); }
    @Provide Arbitrary<ExtractSortBufArgs<Byte     >> extractSortBufArgs_withRange_Byte     (@ForAll @Size(max=MAX_SIZE) Byte     [] arr ) { return extractSortBufArgs_withRange(arr); }
    @Provide Arbitrary<ExtractSortBufArgs<Short    >> extractSortBufArgs_withRange_Short    (@ForAll @Size(max=MAX_SIZE) Short    [] arr ) { return extractSortBufArgs_withRange(arr); }
    @Provide Arbitrary<ExtractSortBufArgs<Integer  >> extractSortBufArgs_withRange_Integer  (@ForAll @Size(max=MAX_SIZE) Integer  [] arr ) { return extractSortBufArgs_withRange(arr); }
    @Provide Arbitrary<ExtractSortBufArgs<Long     >> extractSortBufArgs_withRange_Long     (@ForAll @Size(max=MAX_SIZE) Long     [] arr ) { return extractSortBufArgs_withRange(arr); }
    @Provide Arbitrary<ExtractSortBufArgs<Character>> extractSortBufArgs_withRange_Character(@ForAll @Size(max=MAX_SIZE) Character[] arr ) { return extractSortBufArgs_withRange(arr); }
    @Provide Arbitrary<ExtractSortBufArgs<Float    >> extractSortBufArgs_withRange_Float    (@ForAll @Size(max=MAX_SIZE) Float    [] arr ) { return extractSortBufArgs_withRange(arr); }
    @Provide Arbitrary<ExtractSortBufArgs<Double   >> extractSortBufArgs_withRange_Double   (@ForAll @Size(max=MAX_SIZE) Double   [] arr ) { return extractSortBufArgs_withRange(arr); }
    @Provide Arbitrary<ExtractSortBufArgs<String   >> extractSortBufArgs_withRange_String   (@ForAll @Size(max=MAX_SIZE) String   [] arr ) { return extractSortBufArgs_withRange(arr); }

    @Property                         void extractsUnstably_withRange_small_Byte(@ForAll("extractSortBufArgs_withRange_small_Byte") ExtractSortBufArgs<Byte     > sample, @ForAll Comparator<Byte     > cmp ) { extractsUnstably(sample,cmp); }
    @Property                         void extractsUnstably_withRange_Boolean   (@ForAll("extractSortBufArgs_withRange_Boolean"   ) ExtractSortBufArgs<Boolean  > sample, @ForAll Comparator<Boolean  > cmp ) { extractsUnstably(sample,cmp); }
    @Property                         void extractsUnstably_withRange_Byte      (@ForAll("extractSortBufArgs_withRange_Byte"      ) ExtractSortBufArgs<Byte     > sample, @ForAll Comparator<Byte     > cmp ) { extractsUnstably(sample,cmp); }
    @Property                         void extractsUnstably_withRange_Short     (@ForAll("extractSortBufArgs_withRange_Short"     ) ExtractSortBufArgs<Short    > sample, @ForAll Comparator<Short    > cmp ) { extractsUnstably(sample,cmp); }
    @Property                         void extractsUnstably_withRange_Integer   (@ForAll("extractSortBufArgs_withRange_Integer"   ) ExtractSortBufArgs<Integer  > sample, @ForAll Comparator<Integer  > cmp ) { extractsUnstably(sample,cmp); }
    @Property                         void extractsUnstably_withRange_Long      (@ForAll("extractSortBufArgs_withRange_Long"      ) ExtractSortBufArgs<Long     > sample, @ForAll Comparator<Long     > cmp ) { extractsUnstably(sample,cmp); }
    @Property                         void extractsUnstably_withRange_Character (@ForAll("extractSortBufArgs_withRange_Character" ) ExtractSortBufArgs<Character> sample, @ForAll Comparator<Character> cmp ) { extractsUnstably(sample,cmp); }
    @Property                         void extractsUnstably_withRange_Float     (@ForAll("extractSortBufArgs_withRange_Float"     ) ExtractSortBufArgs<Float    > sample, @ForAll Comparator<Float    > cmp ) { extractsUnstably(sample,cmp); }
    @Property                         void extractsUnstably_withRange_Double    (@ForAll("extractSortBufArgs_withRange_Double"    ) ExtractSortBufArgs<Double   > sample, @ForAll Comparator<Double   > cmp ) { extractsUnstably(sample,cmp); }
    <T extends Comparable<? super T>> void extractsUnstably                     ( ExtractSortBufArgs<T> args, Comparator<? super T> cmp )
    {
      int from = args.from,
         until = args.until,
        desLen = args.desiredLen,
           dst = args.destination;

      var ref = args.arr.clone();
      var tst = ref.clone();

      class Acc implements ExtractSortBufOrdinalAccess {
        long nSwaps = 0,
             nComps = 0;
        @Override public void   swap( int i, int j ) { ++nSwaps; Swap.swap(tst,i,j); }
        @Override public int compare( int i, int j ) { ++nComps; return cmp.compare(tst[i], tst[j]); }
      }

      var set = new TreeSet<>(cmp);
      stream(ref, from,until).forEach(set::add);

      var           acc = new Acc();
      int nUnique = acc.extractSortBuf_ordinal_l_sorted(from,until, desLen, dst);

      assertThat(nUnique).isEqualTo( min(desLen, set.size()) );

      var buf = copyOfRange(tst, dst,dst+nUnique);

      assertThat(buf).isSortedAccordingTo(cmp);
      assertThat(set).containsAll( asList(buf) );

      assertThat(acc.nSwaps).isLessThanOrEqualTo(
          until-from // <- for "combing" through the range
        + max(until-dst, dst+nUnique-from) // <- for moving buffer to destination
        + nUnique*(nUnique+1L)/2 // <- for collecting buffer elements
      );

      arraycopy(tst,dst+nUnique, tst,dst,          tst.length-nUnique-dst );
      arraycopy(tst,from,        tst,from+nUnique, tst.length-nUnique-from);
      arraycopy(buf,0,           tst,from,                    nUnique     );

      Arrays.sort(tst, from,until, cmp);
      Arrays.sort(ref, from,until, cmp);

      assertThat(tst).isEqualTo(ref);
    }

    @Property                         void extractsStably_withRange_small_Byte(@ForAll("extractSortBufArgs_withRange_small_Byte") ExtractSortBufArgs<Byte     > sample, @ForAll Comparator<Byte     > cmp ) { extractsStably(sample,cmp); }
    @Property                         void extractsStably_withRange_Boolean   (@ForAll("extractSortBufArgs_withRange_Boolean"   ) ExtractSortBufArgs<Boolean  > sample, @ForAll Comparator<Boolean  > cmp ) { extractsStably(sample,cmp); }
    @Property                         void extractsStably_withRange_Byte      (@ForAll("extractSortBufArgs_withRange_Byte"      ) ExtractSortBufArgs<Byte     > sample, @ForAll Comparator<Byte     > cmp ) { extractsStably(sample,cmp); }
    @Property                         void extractsStably_withRange_Short     (@ForAll("extractSortBufArgs_withRange_Short"     ) ExtractSortBufArgs<Short    > sample, @ForAll Comparator<Short    > cmp ) { extractsStably(sample,cmp); }
    @Property                         void extractsStably_withRange_Integer   (@ForAll("extractSortBufArgs_withRange_Integer"   ) ExtractSortBufArgs<Integer  > sample, @ForAll Comparator<Integer  > cmp ) { extractsStably(sample,cmp); }
    @Property                         void extractsStably_withRange_Long      (@ForAll("extractSortBufArgs_withRange_Long"      ) ExtractSortBufArgs<Long     > sample, @ForAll Comparator<Long     > cmp ) { extractsStably(sample,cmp); }
    @Property                         void extractsStably_withRange_Character (@ForAll("extractSortBufArgs_withRange_Character" ) ExtractSortBufArgs<Character> sample, @ForAll Comparator<Character> cmp ) { extractsStably(sample,cmp); }
    @Property                         void extractsStably_withRange_Float     (@ForAll("extractSortBufArgs_withRange_Float"     ) ExtractSortBufArgs<Float    > sample, @ForAll Comparator<Float    > cmp ) { extractsStably(sample,cmp); }
    @Property                         void extractsStably_withRange_Double    (@ForAll("extractSortBufArgs_withRange_Double"    ) ExtractSortBufArgs<Double   > sample, @ForAll Comparator<Double   > cmp ) { extractsStably(sample,cmp); }
    //    @Property                         void extractsStably_withRange_String    ( @ForAll("extractMergeBufArgs_withRange_String"    ) ExtractMergeBufArgs<String   > sample, @ForAll Comparator<String   > cmp ) { extractsStably(sample,cmp); }
    <T extends Comparable<? super T>> void extractsStably                     (ExtractSortBufArgs<T> args, Comparator<? super T> _cmp )
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

      class Acc implements ExtractSortBufOrdinalAccess {
        long nSwaps = 0,
             nComps = 0;
        @Override public void   swap( int i, int j ) { ++nSwaps; Swap.swap(tst,i,j); }
        @Override public int compare( int i, int j ) { ++nComps; return cmp.compare(tst[i], tst[j]); }
      }

      var set = new TreeSet<>(cmp);
      stream(ref, from,until).forEach(set::add);

      var           acc = new Acc();
      int nUnique = acc.extractSortBuf_ordinal_l_sorted(from,until, desLen, dst);

      assertThat(nUnique).isEqualTo( min(desLen, set.size()) );

      var buf = copyOfRange(tst, dst,dst+nUnique);

      assertThat(buf).isSortedAccordingTo(cmp);
      assertThat(set).containsAll( asList(buf) );

      assertThat(acc.nSwaps).isLessThanOrEqualTo(
          until-from // <- for "combing" through the range
        + max(until-dst, dst+nUnique-from) // <- for moving buffer to destination
        + nUnique*(nUnique+1L)/2 // <- for collecting buffer elements
      );

      arraycopy(tst,dst+nUnique, tst,dst,          tst.length-nUnique-dst );
      arraycopy(tst,from,        tst,from+nUnique, tst.length-nUnique-from);
      arraycopy(buf,0,           tst,from,                    nUnique     );

      Arrays.sort(tst, from,until, cmp);
      Arrays.sort(ref, from,until, cmp);

      assertThat(tst).isEqualTo(ref);
    }
  }
}
