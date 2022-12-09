package com.github.jaaa.permute;

import com.github.jaaa.WithRange;
import net.jqwik.api.*;

import static com.github.jaaa.permute.Revert.revert;
import static org.assertj.core.api.Assertions.assertThat;

public class RevertTest
{
  private static final int N_TRIES = 100_000;

  @Property( tries = N_TRIES )
  void revertsArraysByte1( @ForAll byte[] seq )
  {
              seq = seq.clone();
    byte[] backup = seq.clone();
    revert(seq);
    for( int i=seq.length; i-- > 0; )
      assertThat( backup[i] ).isEqualTo( seq[seq.length-1-i] );
  }

  @Property( tries = N_TRIES )
  void revertsArraysShort1( @ForAll short[] seq )
  {
               seq = seq.clone();
    short[] backup = seq.clone();
    revert(seq);
    for( int i=seq.length; i-- > 0; )
      assertThat( backup[i] ).isEqualTo( seq[seq.length-1-i] );
  }

  @Property( tries = N_TRIES )
  void revertsArraysInt1( @ForAll int[] seq )
  {
             seq = seq.clone();
    int[] backup = seq.clone();
    revert(seq);
    for( int i=seq.length; i-- > 0; )
      assertThat( backup[i] ).isEqualTo( seq[seq.length-1-i] );
  }

  @Property( tries = N_TRIES )
  void revertsArraysLong1( @ForAll long[] seq )
  {
              seq = seq.clone();
    long[] backup = seq.clone();
    revert(seq);
    for( int i=seq.length; i-- > 0; )
      assertThat( backup[i] ).isEqualTo( seq[seq.length-1-i] );
  }

  @Property( tries = N_TRIES )
  void revertsArraysChar1( @ForAll char[] seq )
  {
              seq = seq.clone();
    char[] backup = seq.clone();
    revert(seq);
    for( int i=seq.length; i-- > 0; )
      assertThat( backup[i] ).isEqualTo( seq[seq.length-1-i] );
  }

  @Property( tries = N_TRIES )
  void revertsArraysFloat1( @ForAll float[] seq )
  {
               seq = seq.clone();
    float[] backup = seq.clone();
    revert(seq);
    for( int i=seq.length; i-- > 0; )
      assertThat( backup[i] ).isEqualTo( seq[seq.length-1-i] );
  }

  @Property( tries = N_TRIES )
  void revertsArraysDouble1( @ForAll double[] seq )
  {
                seq = seq.clone();
    double[] backup = seq.clone();
    revert(seq);
    for( int i=seq.length; i-- > 0; )
      assertThat( backup[i] ).isEqualTo( seq[seq.length-1-i] );
  }

  @Property( tries = N_TRIES )
  void revertsArraysBoolean1( @ForAll boolean[] seq )
  {
           seq = seq.clone();
    var backup = seq.clone();
    revert(seq);
    for( int i=seq.length; i-- > 0; )
      assertThat( backup[i] ).isEqualTo( seq[seq.length-1-i] );
  }

  @Property( tries = N_TRIES )
  void revertsArraysByte2( @ForAll WithRange<byte[]> sample )
  {
    int  until = sample.getUntil(),
          from = sample.getFrom();
    byte[] seq = sample.getData().clone(),
        backup = seq.clone();

    revert(seq, from, until);

    final int len = until-from;

    for( int i=0    ; i < from      ; i++ ) assertThat(seq[     i]).isEqualTo(backup[        i]);
    for( int i=0    ; i < len       ; i++ ) assertThat(seq[from+i]).isEqualTo(backup[until-1-i]);
    for( int i=until; i < seq.length; i++ ) assertThat(seq[     i]).isEqualTo(backup[        i]);
  }

  @Property( tries = N_TRIES )
  void revertsArraysShort2( @ForAll WithRange<short[]> sample )
  {
    int  until = sample.getUntil(),
          from = sample.getFrom();
    short[] seq = sample.getData().clone(),
         backup = seq.clone();

    revert(seq, from, until);

    final int len = until-from;

    for( int i=0    ; i < from      ; i++ ) assertThat(seq[     i]).isEqualTo(backup[        i]);
    for( int i=0    ; i < len       ; i++ ) assertThat(seq[from+i]).isEqualTo(backup[until-1-i]);
    for( int i=until; i < seq.length; i++ ) assertThat(seq[     i]).isEqualTo(backup[        i]);
  }

  @Property( tries = N_TRIES )
  void revertsArraysInt2( @ForAll WithRange<int[]> sample )
  {
    int until = sample.getUntil(),
         from = sample.getFrom();
    int[] seq = sample.getData().clone(),
       backup = seq.clone();

    revert(seq, from, until);

    final int len = until-from;

    for( int i=0    ; i < from      ; i++ ) assertThat(seq[     i]).isEqualTo(backup[        i]);
    for( int i=0    ; i < len       ; i++ ) assertThat(seq[from+i]).isEqualTo(backup[until-1-i]);
    for( int i=until; i < seq.length; i++ ) assertThat(seq[     i]).isEqualTo(backup[        i]);
  }

  @Property( tries = N_TRIES )
  void revertsArraysLong2( @ForAll WithRange<long[]> sample )
  {
    int  until = sample.getUntil(),
          from = sample.getFrom();
    long[] seq = sample.getData().clone(),
        backup = seq.clone();

    revert(seq, from, until);

    final int len = until-from;

    for( int i=0    ; i < from      ; i++ ) assertThat(seq[     i]).isEqualTo(backup[        i]);
    for( int i=0    ; i < len       ; i++ ) assertThat(seq[from+i]).isEqualTo(backup[until-1-i]);
    for( int i=until; i < seq.length; i++ ) assertThat(seq[     i]).isEqualTo(backup[        i]);
  }

  @Property( tries = N_TRIES )
  void revertsArraysChar2( @ForAll WithRange<char[]> sample )
  {
    int  until = sample.getUntil(),
          from = sample.getFrom();
    char[] seq = sample.getData().clone(),
        backup = seq.clone();

    revert(seq, from, until);

    final int len = until-from;

    for( int i=0    ; i < from      ; i++ ) assertThat(seq[     i]).isEqualTo(backup[        i]);
    for( int i=0    ; i < len       ; i++ ) assertThat(seq[from+i]).isEqualTo(backup[until-1-i]);
    for( int i=until; i < seq.length; i++ ) assertThat(seq[     i]).isEqualTo(backup[        i]);
  }

  @Property( tries = N_TRIES )
  void revertsArraysFloat2( @ForAll WithRange<float[]> sample )
  {
    int   until = sample.getUntil(),
           from = sample.getFrom();
    float[] seq = sample.getData().clone(),
         backup = seq.clone();

    revert(seq, from, until);

    final int len = until-from;

    for( int i=0    ; i < from      ; i++ ) assertThat(seq[     i]).isEqualTo(backup[        i]);
    for( int i=0    ; i < len       ; i++ ) assertThat(seq[from+i]).isEqualTo(backup[until-1-i]);
    for( int i=until; i < seq.length; i++ ) assertThat(seq[     i]).isEqualTo(backup[        i]);
  }

  @Property( tries = N_TRIES )
  void revertsArraysDouble2( @ForAll WithRange<double[]> sample )
  {
    int    until = sample.getUntil(),
            from = sample.getFrom();
    double[] seq = sample.getData().clone(),
          backup = seq.clone();

    revert(seq, from, until);

    final int len = until-from;

    for( int i=0    ; i < from      ; i++ ) assertThat(seq[     i]).isEqualTo(backup[        i]);
    for( int i=0    ; i < len       ; i++ ) assertThat(seq[from+i]).isEqualTo(backup[until-1-i]);
    for( int i=until; i < seq.length; i++ ) assertThat(seq[     i]).isEqualTo(backup[        i]);
  }

  @Property( tries = N_TRIES )
  void revertsArraysBoolean2( @ForAll WithRange<boolean[]> sample )
  {
    int  until = sample.getUntil(),
          from = sample.getFrom();
    var    seq = sample.getData().clone();
    var backup = seq.clone();

    revert(seq, from, until);

    final int len = until-from;

    for( int i=0    ; i < from      ; i++ ) assertThat(seq[     i]).isEqualTo(backup[        i]);
    for( int i=0    ; i < len       ; i++ ) assertThat(seq[from+i]).isEqualTo(backup[until-1-i]);
    for( int i=until; i < seq.length; i++ ) assertThat(seq[     i]).isEqualTo(backup[        i]);
  }
}
