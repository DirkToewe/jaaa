package com.github.jaaa.sort;

import com.github.jaaa.misc.RotateAccess;
import com.github.jaaa.search.BinarySearchAccess;
import static java.lang.Math.min;


public interface ExtractSortBufUniqueAccess extends BinarySearchAccess, RotateAccess
{
  default int extractSortBufUniqueMinL( int from, int until, int nUnique )
  {
    // FIXME: takes O( nUnique * n ) swaps, buts should at most take O( n*log(n) ), where n=until-from
    if(   from < 0         ) throw new IllegalArgumentException();
    if(   from > until     ) throw new IllegalArgumentException();
    if(nUnique > until-from) throw new IllegalArgumentException();

    int p=until, // <- current pos. of collected elements
        n=0;     // <- current num. of collected elements
    for( int i=until; i-- > from; )
    {
      int j = binarySearch(p,p+n, i);
      if( j >= 0 )
        swap(i,j);
      else {
        j = ~j - p;
        if( j < nUnique ) {
          rotate(i+1,p+n, n);
          rotate(i,1+i+j,-1);
          p = i;
          n = min(n+1, nUnique);
        }
      }
    }
    rotate(from,p+n, n);
    return n;
  }

  default int extractSortBufUniqueMaxR( int from, int until, int nUnique )
  {
    // FIXME: takes O( nUnique * n ) swaps, buts should at most take O( n*log(n) ), where n=until-from
    if(   from < 0         ) throw new IllegalArgumentException();
    if(   from > until     ) throw new IllegalArgumentException();
    if(nUnique > until-from) throw new IllegalArgumentException();

    int p=from,
        n=0;
    for( int i=from; i < until; i++ )
    {
      int j = binarySearch(p,p+n, i);
      if( j >= 0 )
        swap(i,j);
      else {
        j = p+n - ~j;
        if( j < nUnique ) {
          rotate(p,  i,  -n);
          rotate(i-j,i+1, 1);
          n = min(n+1, nUnique);
          p = i+1-n;
        }
      }
    }
    rotate(p,until, -n);
    return n;
  }
}
