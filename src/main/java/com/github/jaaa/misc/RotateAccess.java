package com.github.jaaa.misc;

public interface RotateAccess extends RevertAccess
{
//  default void rotate( int from, int until, int rot )
//  {
//    if( from <  0     ) throw new IllegalArgumentException();
//    if( from >  until ) throw new IllegalArgumentException();
//    if( from == until ) return;
//
//    // Rolling Algorithm
//    // -----------------
//    // Array rotation can also be interpreted as swapping two
//    // consecutive blocks of possibly different sizes. The idea
//    // of this algorithm is to roll the smaller of the two
//    // blocks over the larger block. After rolling the smaller
//    // of the two blocks is now rotated and has to be rotated
//    // back, which means we now have a smaller rotation problem
//    // to solve. Since we always roll the smaller of the two
//    // blocks, the rotation problem is cut in at least half
//    // each iteration.
//    for(;;){ int len = until - from;
//          rot %= len;
//      if( rot == 0 ) return;
//          rot += len & -(rot>>>31); // <- handles negative rot
//      if( rot <= (len>>>1) )
//      {
//        for(;;) {
//          int i = --until - rot;
//          swap(i,until);
//          if( i == from ) break;
//        }
//        rot = -len;
//      }
//      else
//      {
//        rot = len-rot;
//        for(;;) {
//          int i = from + rot;
//          if( i >= until ) break;
//          swap(from++,i);
//        }
//        rot = len;
//      }
//    }
//  }



//  default void rotate( int from, int until, int rot )
//  {
//    if( from < 0    ) throw new IllegalArgumentException();
//    if( from > until) throw new IllegalArgumentException();
//
//    int len = until - from;
//    if( len <= 1 ) return;
//        rot %= len;
//    if( rot == 0 ) return;
//        rot += len & -(rot>>>31);
//
//    // Juggling Algorithm
//    // ------------------
//                         until = from + IMath.gcd(rot,len);
//    for( int i=from; i < until; i++ )
//      for( int j=i;; ) { // start swap cycle
//        int k = j-rot;
//            k += len & -(k-from>>>31);
//        if( k == i ) break;
//        swap(j,j=k);
//      }
//  }



//  default void rotate( int from, int until, int rot )
//  {
//    if( from <  0     ) throw new IllegalArgumentException();
//    if( from >  until ) throw new IllegalArgumentException();
//
//    int    len = until - from;
//    if( 0==len ) return;
//    rot %= len;
//    rot += len & -(rot>>>31); // <- handles negative rot
//
//    while( rot != 0 ){
//      rot = len-rot;
//      for(;;) {
//        int i = from + rot;
//        if( i >= until ) break;
//        swap(from++,i);
//      }
//      rot  = len;
//      rot %= len = until - from;
//    }
//  }



//  default void rotate( int from, int until, int rot )
//  {
//    if( from <  0     ) throw new IllegalArgumentException();
//    if( from >  until ) throw new IllegalArgumentException();
//    if( from == until ) return;
//
//    for(;;){ int len = until - from;
//          rot %= len;
//      if( rot == 0 ) return;
//          rot += len & -(rot>>>31); // <- handles negative rot
//      for(;;) {
//        int i = --until - rot;
//        swap(i,until);
//        if( i == from ) break;
//      }
//      rot = -len;
//    }
//  }



//  default void rotate( int from, int until, int rot )
//  {
//    if( from <  0     ) throw new IllegalArgumentException();
//    if( from >  until ) throw new IllegalArgumentException();
//
//    int    len = until - from;
//    if( 0==len ) return;
//    rot %= len;
//    rot += len & -(rot>>>31); // <- handles negative rot
//
//    while( rot != 0 ){
//      rot = len-rot;
//      for(;;) {
//        int i = from + rot;
//        if( i >= until ) break;
//        swap(from++,i);
//      }
//      rot  = len;
//      rot %= len = until - from;
//    }
//  }



  default void rotate( int from, int until, int rot )
  {
    if( from < 0 || from > until ) throw new IllegalArgumentException();

    int len = until - from;
    if( len <= 1 || 0 == (rot %= len) ) return;
        rot += len & -(rot>>>31);

    revert(from,         until);
    revert(from,from+rot      );
    revert(     from+rot,until);
  }
}
