package com.github.jaaa.util;

import java.io.PrintStream;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.VarHandle;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.DoubleConsumer;
import java.util.function.IntConsumer;
import java.util.function.LongConsumer;
import java.util.stream.DoubleStream;
import java.util.stream.IntStream;
import java.util.stream.LongStream;
import java.util.stream.Stream;

import static java.lang.String.format;
import static java.lang.System.nanoTime;
import static java.util.stream.StreamSupport.intStream;
import static java.util.stream.StreamSupport.longStream;
import static java.util.stream.StreamSupport.doubleStream;
import static java.util.stream.StreamSupport.stream;


public class Progress
{
// STATIC FIELDS
  private static char[]
    BAR_SUBSTEPS_UTF8  = { ' ', '▏', '▎', '▍', '▌', '▋', '▊', '▉', '█' },
    BAR_SUBSTEPS_ASCII = { ' ', '-', '=', '#' },
    BAR_ENDS_UTF8      = { '▕', '▏' },
    BAR_ENDS_ASCII     = { '[', ']' };

  public interface ProgressMonitor { void increment(); }

  private static class ProgressPrinter implements ProgressMonitor
  {
  // STATIC FIELDS
    private static final long REFRESH_DELAY = 20_000_000L;
    private static final VarHandle PROGRESS;

  // STATIC CONSTRUCTOR
    static {
    try {
      PROGRESS = MethodHandles.lookup().findVarHandle(ProgressPrinter.class, "progress", long.class);
    } catch( NoSuchFieldException | IllegalAccessException ex ) {
      throw new ExceptionInInitializerError(ex);
    }
  }
  // STATIC METHODS
  // FIELDS
    private final long pTotal,     tStart = nanoTime();
    private       long progress=-1, tLast  = Long.MIN_VALUE;
    private final boolean usePercent;
    private final PrintStream out;
    private final int barLength;
    private final char[] barSubsteps,
                         barEnds;
    private int pLast = 0,
               clrLen = 0;

  // CONSTRUCTORS
    public ProgressPrinter( long _nTotal, PrintStream _out, boolean _usePercent, int _barLength, char[] _barSubsteps, char[] _barEnds )
    {
      if( null == _out ) throw new NullPointerException();
      if( null != _barSubsteps && _barSubsteps.length < 2 || _barEnds.length != 2 ) throw new IllegalArgumentException();
      pTotal =_nTotal;
      out =_out;
      usePercent  = _usePercent;
      barLength   = _barLength;
      barSubsteps = _barSubsteps;
      barEnds     = _barEnds;
      increment();
    }

  // METHODS
    public void increment()
    {
      long progress;
      do {
        progress = (long) PROGRESS.getVolatile(this);
      } while( ! PROGRESS.weakCompareAndSet(this, progress, ++progress) );

      var refresh = (tLast + REFRESH_DELAY) < nanoTime();
      int n = barSubsteps.length - 1,
          p = 0;
      if( 0 < pTotal ) {
        if( pTotal < progress )
          throw new IllegalStateException();
        p = (int) ( (double) progress*n*barLength / pTotal);
        refresh = refresh || pLast < p || progress == pTotal;
      }

      if( !refresh ) return;

      synchronized(out)
      {
        progress = (long) PROGRESS.getVolatile(this);

        var                                 now = nanoTime();
        refresh = (tLast + REFRESH_DELAY) < now;
        if( 0 < pTotal) {
          if( pTotal < progress )
            throw new IllegalStateException();
          p = (int) ( (double) progress*n*barLength / pTotal);
          refresh = refresh || pLast < p || progress == pTotal;
        }

        if( !refresh ) return;

        tLast = now;
        pLast = p;

        var line = new StringBuilder();
        for( int i=clrLen; i-- > 0; )
          line.append('\b');

        if( pTotal < 0 )
          line.append(progress);
        else
        {
          if( 0 < pTotal )
          {
            int div = p / n,
                mod = p % n;

            // PLOT PROGRESS BAR
            // -----------------
            line.append( barEnds[0] );

            char full = barSubsteps[n];
            for( int i=div; i-- > 0; ) line.append(full);

            if( mod != 0 ) {
              line.append(barSubsteps[mod]);
              div++;
            }

            for( int i=barLength - div; i-- > 0; ) line.append(' ');
            line.append( barEnds[1] );
          }

          // PLOT PROGRESS
          // -------------
          if( usePercent && 0 < pTotal )
            line.append( format("%5.1f%%", 100d*progress/pTotal) );
          else {
            var r = String.valueOf(pTotal);
            var l = String.valueOf(progress);

            for( int i = r.length() - l.length(); i-- > 0; )
              line.append(' ');

            line.append(l);
            line.append('/');
            line.append(r);
          }

          // PLOT ETA
          // --------
          if( 0 < progress )
          {
            long dt = now - tStart;
                 dt *= (double) (pTotal - progress) / progress;

            long sec = 1_000_000_000L,
                   h = dt / (60*60*sec),
                   m = dt % (60*60*sec) / (60*sec),
                   s = dt % (   60*sec) /     sec;

            line.append(" (");
            if( 0 < h ) line.append( format("%d:%02d:%02d", h,m,s) );
            else        line.append( format(     "%d:%02d",   m,s) );
            line.append(")");
          }

          if( pTotal == progress )
            line.append('\n');
        }

        out.print(line);
        clrLen = line.length() - clrLen;
      }
    }
  }

  private static class ProgressSpliterator<T> implements Spliterator<T>
  {
  // FIELDS
    private final ProgressMonitor progressMonitor;
    private final Spliterator<T> spliterator;

  // CONSTRUCTORS
    public ProgressSpliterator( ProgressMonitor _progressMonitor, Spliterator<T> _spliterator ) {
      if( null == _progressMonitor || null == _spliterator )
        throw new NullPointerException();
      progressMonitor = _progressMonitor;
      spliterator     = _spliterator;
    }

  // METHODS
    @Override public boolean tryAdvance( Consumer<? super T> action ) {
      var result = spliterator.tryAdvance(action);
      if( result )
        progressMonitor.increment();
      return result;
    }

    @Override public void forEachRemaining( Consumer<? super T> action ) {
      spliterator.forEachRemaining( x -> {
        action.accept(x);
        progressMonitor.increment();
      });
    }

    @Override public Spliterator<T> trySplit() {
      var left = spliterator.trySplit();
      if( left != null )
          left = new ProgressSpliterator<>(progressMonitor, left);
      return left;
    }

    @Override public long estimateSize() {
      return spliterator. estimateSize();
    }

    @Override public long getExactSizeIfKnown() {
      return spliterator. getExactSizeIfKnown();
    }

    @Override public int characteristics() {
      return spliterator.characteristics();
    }

    @Override public boolean hasCharacteristics( int characteristics ) {
      return spliterator.    hasCharacteristics(characteristics);
    }

    @Override public Comparator<? super T> getComparator() {
      return spliterator.getComparator();
    }
  }

  private static class ProgressSpliteratorInt implements Spliterator.OfInt
  {
  // FIELDS
    private final ProgressMonitor progressMonitor;
    private final Spliterator.OfInt spliterator;

  // CONSTRUCTORS
    public ProgressSpliteratorInt( ProgressMonitor _progressMonitor, Spliterator.OfInt _spliterator ) {
      if( null == _progressMonitor || null == _spliterator )
        throw new NullPointerException();
      progressMonitor = _progressMonitor;
      spliterator     = _spliterator;
    }

  // METHODS
    @Override public boolean tryAdvance( IntConsumer action ) {
      var result = spliterator.tryAdvance(action);
      if( result )
        progressMonitor.increment();
      return result;
    }

    @Override
    public void forEachRemaining( IntConsumer action ) {
      spliterator.forEachRemaining( (int x) -> {
        action.accept(x);
        progressMonitor.increment();
      });
    }

    @Override public Spliterator.OfInt trySplit() {
      var left = spliterator.trySplit();
      if( left != null )
          left = new ProgressSpliteratorInt(progressMonitor, left);
      return left;
    }

    @Override public long estimateSize() {
      return spliterator. estimateSize();
    }

    @Override public long getExactSizeIfKnown() {
      return spliterator. getExactSizeIfKnown();
    }

    @Override public int characteristics() {
      return spliterator.characteristics();
    }

    @Override public boolean hasCharacteristics( int characteristics ) {
      return spliterator.    hasCharacteristics(characteristics);
    }

    @Override public Comparator<? super Integer> getComparator() {
      return spliterator.getComparator();
    }
  }

  private static class ProgressSpliteratorLong implements Spliterator.OfLong
  {
  // FIELDS
    private final ProgressMonitor progressMonitor;
    private final Spliterator.OfLong spliterator;

  // CONSTRUCTORS
    public ProgressSpliteratorLong( ProgressMonitor _progressMonitor, Spliterator.OfLong _spliterator ) {
      if( null == _progressMonitor || null == _spliterator )
        throw new NullPointerException();
      progressMonitor = _progressMonitor;
      spliterator     = _spliterator;
    }

  // METHODS
    @Override public boolean tryAdvance( LongConsumer action ) {
      var result = spliterator.tryAdvance(action);
      if( result )
        progressMonitor.increment();
      return result;
    }

    @Override public void forEachRemaining( LongConsumer action ) {
      spliterator.forEachRemaining( (long x) -> {
        action.accept(x);
        progressMonitor.increment();
      });
    }

    @Override public Spliterator.OfLong trySplit() {
      var left = spliterator.trySplit();
      if( left != null )
          left = new ProgressSpliteratorLong(progressMonitor, left);
      return left;
    }

    @Override public long estimateSize() {
      return spliterator. estimateSize();
    }

    @Override public long getExactSizeIfKnown() {
      return spliterator. getExactSizeIfKnown();
    }

    @Override public int characteristics() {
      return spliterator.characteristics();
    }

    @Override public boolean hasCharacteristics( int characteristics ) {
      return spliterator.    hasCharacteristics(characteristics);
    }

    @Override public Comparator<? super Long> getComparator() {
      return spliterator.getComparator();
    }
  }

  private static class ProgressSpliteratorDouble implements Spliterator.OfDouble
  {
  // FIELDS
    private final ProgressMonitor progressMonitor;
    private final Spliterator.OfDouble spliterator;

  // CONSTRUCTORS
    public ProgressSpliteratorDouble( ProgressMonitor _progressMonitor, Spliterator.OfDouble _spliterator ) {
      if( null == _progressMonitor || null == _spliterator )
        throw new NullPointerException();
      progressMonitor = _progressMonitor;
      spliterator     = _spliterator;
    }

  // METHODS
    @Override public boolean tryAdvance( DoubleConsumer action ) {
      var result = spliterator.tryAdvance(action);
      if( result )
        progressMonitor.increment();
      return result;
    }

    @Override public void forEachRemaining( DoubleConsumer action ) {
      spliterator.forEachRemaining( (double x) -> {
        action.accept(x);
        progressMonitor.increment();
      });
    }

    @Override public Spliterator.OfDouble trySplit() {
      var left = spliterator.trySplit();
      if( left != null )
          left = new ProgressSpliteratorDouble(progressMonitor, left);
      return left;
    }

    @Override public long estimateSize() {
      return spliterator. estimateSize();
    }

    @Override public long getExactSizeIfKnown() {
      return spliterator. getExactSizeIfKnown();
    }

    @Override public int characteristics() {
      return spliterator.characteristics();
    }

    @Override public boolean hasCharacteristics( int characteristics ) {
      return spliterator.    hasCharacteristics(characteristics);
    }

    @Override public Comparator<? super Double> getComparator() {
      return spliterator.getComparator();
    }
  }

  private static class ProgressIterator<T> implements Iterator<T>
  {
  // FIELDS
    protected ProgressMonitor progressMonitor;
    protected final Iterator<T> iterator;

  // CONSTRUCTORS
    public ProgressIterator( ProgressMonitor _progressMonitor, Iterator<T> _iterator )
    {
      if( null == _progressMonitor || null == _iterator )
        throw new NullPointerException();
      progressMonitor = _progressMonitor;
      iterator        = _iterator;
    }

    @Override public boolean hasNext() { return iterator.hasNext(); }

    @Override public T next() {
      var result = iterator.next();
      progressMonitor.increment();
      return result;
    }

    @Override public void remove() { iterator.remove(); }

    @Override public void forEachRemaining( Consumer<? super T> action ) {
      iterator.forEachRemaining( x -> {
        action.accept(x);
        progressMonitor.increment();
      });
    }
  }

  private static class ProgressIterable<T> implements Iterable<T>
  {
  // FIELDS
    private ProgressMonitor progressMonitor;
    private Iterable<T> iterable;

  // CONSTRUCTORS
    public ProgressIterable( ProgressMonitor _progressMonitor, Iterable<T> _iterable )
    {
      if( null == _progressMonitor || null == _iterable )
        throw new NullPointerException();
      progressMonitor = _progressMonitor;
      iterable        = _iterable;
    }

  // METHODS
    @Override public void forEach( Consumer<? super T> action )
    {
      if( null == iterable )
        throw new IllegalStateException("Progress.print(iterator) can only be traversed once.");
      var progressMonitor = this.progressMonitor; this.progressMonitor = null;
      var iterable        = this.iterable;        this.iterable        = null;
      iterable.forEach( x -> {
        action.accept(x);
        progressMonitor.increment();
      });
    }

    @Override public Iterator<T> iterator()
    {
      if( null == iterable )
        throw new IllegalStateException("Progress.print(iterator) can only be traversed once.");
      var result = new ProgressIterator<>(progressMonitor, iterable.iterator());
      progressMonitor = null;
      iterable        = null;
      return result;
    }

    @Override public Spliterator<T> spliterator()
    {
      if( null == iterable )
        throw new IllegalStateException("Progress.print(iterator) can only be traversed once.");
      var result = new ProgressSpliterator<>(progressMonitor, iterable.spliterator());
      progressMonitor = null;
      iterable        = null;
      return result;
    }
  }

// STATIC CONSTRUCTOR

// STATIC METHODS
  public static IntStream print( IntStream stream )
  {
    var parallel = stream.isParallel();
    var spliterator = print(stream.spliterator());
    return intStream(spliterator, parallel);
  }

  public static LongStream print( LongStream stream )
  {
    var parallel = stream.isParallel();
    var spliterator = print(stream.spliterator());
    return longStream(spliterator, parallel);
  }

  public static DoubleStream print( DoubleStream stream )
  {
    var parallel = stream.isParallel();
    var spliterator = print(stream.spliterator());
    return doubleStream(spliterator, parallel);
  }

  public static <T> Stream<T> print( Stream<T> stream )
  {
    var parallel = stream.isParallel();
    var spliterator = print(stream.spliterator());
    return stream(spliterator, parallel);
  }



  public static Spliterator.OfInt print( Spliterator.OfInt spliterator )
  {
    long nTotal = spliterator.getExactSizeIfKnown();
    var printer = new ProgressPrinter(nTotal, System.out, 10000 <= nTotal, 32, BAR_SUBSTEPS_UTF8, BAR_ENDS_UTF8);
    spliterator = new ProgressSpliteratorInt(printer, spliterator);
    return spliterator;
  }

  public static Spliterator.OfLong print( Spliterator.OfLong spliterator )
  {
    long nTotal = spliterator.getExactSizeIfKnown();
    var printer = new ProgressPrinter(nTotal, System.out, 10000 <= nTotal, 32, BAR_SUBSTEPS_UTF8, BAR_ENDS_UTF8);
    spliterator = new ProgressSpliteratorLong(printer, spliterator);
    return spliterator;
  }

  public static Spliterator.OfDouble print( Spliterator.OfDouble spliterator )
  {
    long nTotal = spliterator.getExactSizeIfKnown();
    var printer = new ProgressPrinter(nTotal, System.out, 10000 <= nTotal, 32, BAR_SUBSTEPS_UTF8, BAR_ENDS_UTF8);
    spliterator = new ProgressSpliteratorDouble(printer, spliterator);
    return spliterator;
  }

  @SuppressWarnings("unchecked")
  public static <T> Spliterator<T> print( Spliterator<T> spliterator )
  {
    if( spliterator instanceof Spliterator.OfInt    spliter ) return (Spliterator<T>) print(spliter);
    if( spliterator instanceof Spliterator.OfLong   spliter ) return (Spliterator<T>) print(spliter);
    if( spliterator instanceof Spliterator.OfDouble spliter ) return (Spliterator<T>) print(spliter);

    long nTotal = spliterator.getExactSizeIfKnown();
    var printer = new ProgressPrinter(nTotal, System.out, 10000 <= nTotal, 32, BAR_SUBSTEPS_UTF8, BAR_ENDS_UTF8);
    spliterator = new ProgressSpliterator<>(printer, spliterator);
    return spliterator;
  }



  public static <T> Iterable<T> print( Iterable<T> iterable )
  {
    long nTotal = -1;
    if( iterable instanceof Collection<T> coll ) {
      int size = coll.size();
      if( size < Integer.MAX_VALUE )
        nTotal = size;
    }
    var printer = new ProgressPrinter(nTotal, System.out, 10000 <= nTotal, 32, BAR_SUBSTEPS_UTF8, BAR_ENDS_UTF8);
    return new ProgressIterable<>(printer, iterable);
  }
}
