package com.github.jaaa.merge;

import com.github.jaaa.compare.CompareAccess;
import com.github.jaaa.permute.RotateAccess;


public interface TapeMergeAccess extends CompareAccess, RotateAccess
{
  default void tapeMerge( int from, int mid, int until )
  {
    if( until >= mid && mid >= from && from >= 0 )
      for( int l,r,s;; rotate(l,r,s) )
      {
        s = mid;
//        int delta = (mid<<1) - until - from; // = (mid-from) - (until-mid)
//        if( delta <= 0 )
        if( (mid<<1) - until <= from )
        { // FORWARD MERGE
          for(;;) if( from == mid ) return;
             else if( compare(from,mid) <= 0 ) from++;
             else break;
          do {} while( ++mid < until && compare(from,mid) > 0 );
          r = mid;
          s = mid - s;
          l = from;
          from += s+1;
        }
        else
        { // BACKWARD MERGE
          for(;;) if( mid == until ) return;
             else if( compare(mid-1,--until) > 0 ) break;
          do {} while( from < --mid && compare(mid-1,until) > 0 );
          l = mid;
          s = mid - s;
          r = until+1;
          until += s;
        }
      }

    throw new IllegalArgumentException();
  }
}
