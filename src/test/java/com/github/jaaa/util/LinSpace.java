package com.github.jaaa.util;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.DoubleConsumer;
import java.util.stream.DoubleStream;
import java.util.stream.StreamSupport;


public class LinSpace extends AbstractList<Double> implements RandomAccess
{
// STATIC FIELDS
  private static final class LinSpaceSpliterator implements Spliterator.OfDouble
  {
  // STATIC FIELDS
  // STATIC CONSTRUCTOR
  // STATIC METHODS
    private static final int CHARACTERISTICS = IMMUTABLE | NONNULL | SIZED | SUBSIZED;

  // FIELDS
    private final double start, end;
    private final int size;
    private int from,until;

  // CONSTRUCTORS
    public  LinSpaceSpliterator( double  start, double  end, int  size ) { this(start,end,size, 0,size); }
    private LinSpaceSpliterator( double _start, double _end, int _size, int _from, int _until )
    {
      if( _size < 2 || _from < 0 || _from > _until || _until > _size )
        throw new IllegalArgumentException();
      start = _start;
      end   = _end;
      size  = _size;
      from  = _from;
      until = _until;
    }

  // METHODS
    @Override public Spliterator.OfDouble trySplit() {
      if( from >= until-1 )
        return null;
      return new LinSpaceSpliterator(start,end,size, from, from = from+until >>> 1);
    }

    @Override public long estimateSize       () { return until-from; }
    @Override public long getExactSizeIfKnown() { return until-from; }
    @Override public int characteristics() { return CHARACTERISTICS; }

    @Override public boolean tryAdvance( DoubleConsumer action ) {
      if( from >=  until )
        return false;
      action.accept( linSpace_get(start,end, size, from++) );
      return true;
    }

    @Override public void forEachRemaining( DoubleConsumer action ) {
      while( from < until )
        action.accept( linSpace_get(start,end, size, from++) );
    }
  }

// STATIC CONSTRUCTOR
// STATIC METHODS
  private static double linSpace_get( double start, double end, int size, int index )
  {
    if( index < 0 || index >= size )
      throw new IndexOutOfBoundsException();
    if( start == end )
      return start;
    double s = index / (size-1d);
    return start*(1-s) + s*end;
  }

// FIELDS
  private final double start,end;
  private final int size;

// CONSTRUCTORS
  public LinSpace( double _start, double _end, int _size )
  {
    if( _size < 2 )
      throw new IllegalArgumentException();
    start = _start;
    end   = _end;
    size  = _size;
  }

// METHODS
  @Override public int size() { return size; }
  @Override public Double get        ( int index ) { return linSpace_get(start,end, size, index); }
  public           double getAsDouble( int index ) { return linSpace_get(start,end, size, index); }

  @Override public void forEach( Consumer<? super Double> action ) {
    if( action instanceof DoubleConsumer c )
      forEach(c);
    else
      forEach( (DoubleConsumer) action::accept );
  }

  public void forEach( DoubleConsumer action ) {
    for( int i=0; i < size; i++ )
      action.accept( linSpace_get(start,end, size, i) );
  }

  @Override public       Spliterator.OfDouble spliterator() { return new LinSpaceSpliterator(start,end, size); }
  @Override public PrimitiveIterator.OfDouble    iterator()
  {
    return new PrimitiveIterator.OfDouble() {
      private int i=0;
      @Override public boolean hasNext() { return i < size; }
      @Override public double nextDouble() {
        if( i >= size ) throw new NoSuchElementException();
        return linSpace_get(start,end, size, i++);
      }
    };
  }

  public DoubleStream doubleStream() { return StreamSupport.doubleStream(spliterator(), false); }
}
