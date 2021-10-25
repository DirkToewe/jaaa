package com.github.jaaa.merge;

import com.github.jaaa.misc.RotateAccess;
import com.github.jaaa.search.ExpL2RSearchAccess;
import com.github.jaaa.search.ExpR2LSearchAccess;

import static java.lang.Math.abs;


public interface TimMergeAccess extends RotateAccess,
                                  ExpL2RSearchAccess,
                                  ExpR2LSearchAccess
{
  int MIN_GALLOP = 7;

  default void timMerge( int from, int mid, int until )
  {
    _timMerge(MIN_GALLOP, from,mid,until);
  }

  default int _timMerge( int minGallop, int from, int mid, int until )
  {
    if( from < 0 || from > mid || mid > until )
      throw new IllegalArgumentException();

    merge: for( int s,delta;; )
    {
      int count1;

      // TAPE MERGE
      // ----------
      tape_merge: for( int l,r;; rotate(l,r,s) )
      {
        s = mid;
        if( (delta = (mid<<1) - until - from) <= 0 )
        { // FORWARD MERGE
          for( count1=0;;) if( from == mid ) break merge;
             else if( compare(from,mid) > 0 ) break;
             else {
               from++;
               if( ++count1 >= minGallop )
                 break tape_merge;
             }
          for( count1=0; ++mid < until && compare(from,mid) > 0; )
            if( ++count1 >= minGallop )
              break tape_merge;
          r = mid;
          s = mid - s;
          l = from;
          from += s+1;
        }
        else
        { // BACKWARD MERGE
          for( count1=0;;) if( mid == until ) break merge;
             else if( compare(mid-1,--until) > 0 ) break;
             else if( ++count1 >= minGallop )
               break tape_merge;
          for( count1=0; from < --mid && compare(mid-1,until) > 0; )
            if( ++count1 >= minGallop ) {
              ++until;
              break tape_merge;
            }
          l = mid;
          s = mid - s;
          r = until+1;
          until += s;
        }
      }

      // GALLOPING MERGE
      // ---------------
      gallop_merge: for( int l,r, count2;; )
      {
        count1 = mid;
        if( delta <= 0 ) // <- if( (mid-from) <= (until-mid) )
        { // MERGE FORWARD
          count2 = from;
          from = expL2RSearchGap(from,s,     mid++, /*rightBias=*/true ); if( s == from ) break merge;
          mid  = expL2RSearchGap(mid,until, from++, /*rightBias=*/false);
          count2 = from - count2;
          l = from-1;
          r = mid;
          from += s = mid-s;
        }
        else
        { // MERGE BACKWARD
          count2 = until;
          until = expR2LSearchGap(s,until,  --mid,   /*rightBias=*/false); if( s == until ) break merge;
          mid   = expR2LSearchGap(from,mid, --until, /*rightBias=*/true );
          count2 -= until;
          l = mid;
          r = until+1;
          until += s = mid-s;
        }
        count1 -= mid; int abs = abs(count1);{
          int sgn = count1>>31; count1 = (count1^sgn) - sgn;
        }
        assert count1 == abs;

        rotate(l,r,s);
        --minGallop;

        if( count1 < MIN_GALLOP && count2 < MIN_GALLOP )
          break gallop_merge;

        s=mid;
        delta = (mid<<1) - until - from;
      }

      if(minGallop < 0)
         minGallop = 0;
         minGallop+= 2;  // Penalize for leaving gallop mode
    }

    if(1 > minGallop)
           minGallop = 1;
    return minGallop;
  }
}
