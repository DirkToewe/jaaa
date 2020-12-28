package com.github.jaaa.misc;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.Random;

import static com.github.jaaa.misc.Shuffle.shuffle;
import static java.lang.Runtime.getRuntime;
import static java.lang.System.err;
import static java.lang.System.out;

public class ShuffleTestDieHarder
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
    Process            proc = getRuntime().exec("dieharder -k 2 -a -g 200");
    OutputStream pin = proc.getOutputStream();
    InputStream pout = proc.getInputStream(),
                perr = proc.getErrorStream();

    var rng = new Random();

    byte[] buf = new byte[1024*1024];
    if( buf.length%8 != 0 )
      throw new IllegalArgumentException();

    for(;;)
    {
      pump(perr,err, buf);
      pump(pout,out, buf);

      int off = 0;
      for( int i=0; i < buf.length; i++ )
        buf[i] = (byte) ( (off+i) % buf.length );
      shuffle(buf, rng::nextInt);

      if( ! proc.isAlive() )
        break;

      try {
        pin.write(buf, 0, buf.length);
      }
      catch( IOException ioe ) {}
    }

    if( proc.exitValue() != 0 )
      throw new AssertionError();
  }
}
