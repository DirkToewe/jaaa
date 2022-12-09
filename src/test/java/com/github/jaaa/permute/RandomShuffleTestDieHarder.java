package com.github.jaaa.permute;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.SplittableRandom;
import java.util.function.Consumer;

import static com.github.jaaa.permute.RandomShuffle.randomShuffle;
import static java.lang.Runtime.getRuntime;
import static java.lang.System.err;
import static java.lang.System.out;


public class RandomShuffleTestDieHarder
{
  private static void pump( InputStream in, PrintStream out, byte[] buf ) throws IOException
  {
    while( in.available() > 0 ) {
      int             nRead = in.read(buf);
      out.write(buf,0,nRead);
    }
    out.flush();
  }

  public static void main( String... args ) throws IOException
  {
    System.out.println("\no---------------------o\n| shuffle(boolean[]) |\no---------------------o\n");
    testWith( new Consumer<>() {
      final SplittableRandom rng = new SplittableRandom();
      @Override public void accept( byte[] buf ) {
        var bits = new boolean[buf.length*8];
        Arrays.fill(bits,0,buf.length*4, true);
        randomShuffle(bits, rng::nextInt);

        for( int i=0; i < buf.length; i++ ) {
          buf[i] = 0;
          for( int j=0; j < 8; j++ )
            if( bits[8*i+j] )
              buf[i] |= 1<<j;
        }
      }
    });

    System.out.println("\no----------------o\n| shuffle(byte[]) |\no----------------o\n");
    testWith( new Consumer<>() {
      final SplittableRandom rng = new SplittableRandom();
      @Override public void accept( byte[] buf ) {
        for( int i=0; i < buf.length; i++ )
          buf[i] = (byte) i;
        randomShuffle(buf, rng::nextInt);
      }
    });
  }

  private static void testWith( Consumer<byte[]> genBytes ) throws IOException
  {
    Process            proc = getRuntime().exec("dieharder -k 2 -a -g 200");
    OutputStream pin = proc.getOutputStream();
    InputStream pOut = proc.getInputStream(),
                pErr = proc.getErrorStream();

    byte[] buf = new byte[2048];
    if( buf.length%8 != 0 )
      throw new IllegalArgumentException();

    for(;;)
    {
      pump(pErr,err, buf);
      pump(pOut,out, buf);

      genBytes.accept(buf);

      if( ! proc.isAlive() )
        break;

      try {
        pin.write(buf);
      }
      catch ( IOException ioe ) {
        if( proc.isAlive() )
          throw ioe;
      }
    }

    if( proc.exitValue() != 0 )
      throw new AssertionError();
  }
}
