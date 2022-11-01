package com.github.jaaa.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.Random;
import java.util.function.IntSupplier;
import java.util.function.LongSupplier;

import static java.lang.Runtime.getRuntime;
import static java.lang.System.*;


public class RNGTestDieHarder
{
  private static void pump( InputStream in, PrintStream out, byte[] buf ) throws IOException
  {
    while( in.available() > 0 ) {
      int             nRead = in.read(buf);
      out.write(buf,0,nRead);
    }
    out.flush();
  }

  public static void testRNG( IntSupplier randInt ) throws IOException
  {
    Process             proc = getRuntime().exec("dieharder -k 2 -a -g 200");
    OutputStream pin  = proc.getOutputStream();
    InputStream  pOut = proc.getInputStream(),
                 pErr = proc.getErrorStream();

    byte[] buf = new byte[3*1024];
    if( buf.length%8 != 0 )
      throw new IllegalArgumentException();

    for(;;)
    {
      pump(pErr,err, buf);
      pump(pOut,out, buf);

      for( int i=0; i < buf.length; )
      {
        int nxt = randInt.getAsInt();
        for( int j=0; j < 3; j++ )
          buf[i++] = (byte) (0xFF & nxt >>> 8*j);
      }

      if( ! proc.isAlive() )
        break;

      try {
        pin.write(buf, 0, buf.length);
      }
      catch( IOException ioe ) {
        if( proc.isAlive() )
          throw ioe;
      }
    }

    if( proc.exitValue() != 0 )
      throw new AssertionError();
  }

  public static void testRNG( LongSupplier randLong ) throws IOException
  {
    Process             proc = getRuntime().exec("dieharder -k 2 -a -g 200");
    OutputStream pin  = proc.getOutputStream();
     InputStream pout = proc.getInputStream(),
                 perr = proc.getErrorStream();

    byte[] buf = new byte[1024];
    if( buf.length%8 != 0 )
      throw new IllegalArgumentException();

    for(;;)
    {
      Thread.yield();
      pump(perr,err, buf);
      pump(pout,out, buf);

      for( int i=0; i < buf.length; )
      {
        long nxt = randLong.getAsLong();
        for( int j=0; j < 8; j++ )
          buf[i++] = (byte) (0xFFL & nxt >>> 8*j);
      }

      if( ! proc.isAlive() )
        break;

      try {
        pin.write(buf, 0, buf.length);
      }
      catch( IOException ioe ) {
        if( proc.isAlive() )
          throw ioe;
      }
    }

    if( proc.exitValue() != 0 )
      throw new AssertionError();
  }

  public static void main( String... args ) throws IOException
  {
    out.println("\n  //=====//");
    out.println(  " // RNG //");
    out.println(  "//=====//\n");
    var rng = new RNG( nanoTime() );
    testRNG( () -> rng.nextInt(0, 1<<24) );

    out.println("\n  //=====//");
    out.println(  " // RNG //");
    out.println(  "//=====//\n");
    testRNG( new RNG( nanoTime() )::nextLong );

    out.println("\n  //==================//");
    out.println(  " // java.util.Random //");
    out.println(  "//==================//\n");
    testRNG( (LongSupplier) new Random()::nextLong );
  }
}
