package com.github.jaaa.merge;

import com.github.jaaa.permute.BlockSwapAccess;
import com.github.jaaa.sort.InsertionSortAccess;

import static com.github.jaaa.util.IMath.sqrtFloor;
import static java.lang.Math.min;


// REFERENCES
// ----------
// .. [1] "Practical In-Place Merging"
//         BING-CHAO HUANG and MICHAEL A. LANGSTON


public interface PracticalInplaceMergeAccess extends BlockSwapAccess,
                                             BinaryMergeOffsetAccess,
                                                 InsertionSortAccess,
                                                   BinaryMergeAccess
{
  default void practicalInplaceMerge( int from, int mid, int until )
  {
    assert from <= mid;
    assert         mid <= until;

    if( mid==until) return; from  = binarySearchGapR(from,mid,       mid  );
    if( mid==from ) return; until = binarySearchGapL(     mid,until, mid-1);

    int    s = sqrtFloor(until-from);
    assert s > 0;

    if( min(mid-from,until-mid) <= s ) {
      binaryMerge(from, mid, until);
      return;
    }

    int len  = until  -  from,
        len1 =       mid-from,
        len2 = until-mid;

    // find ends of both sub-lists that contain the s largest elements
    int s1 = len1 - binaryMergeOffset(from,len1, mid,len2, len-s),
        s2 = s - s1;

    // move the s largest elements together as buffer
    int                 buf = mid-s;
    blockSwap(until-s2, buf, s2);

    // sizes of the odd-ends that don't fit into blocks of size s
    int t1 = len1     % s,
        t2 =(len2-s2) % s + s2;

    // HANDLE LEFT ODD-END
    // merge the odd-end of the left sublist and the first block of the right sublist using the buffer
    practicalInplaceMerge_bufferedMerge(mid-t1, from,from+t1, mid,mid+s);
    blockSwap(from,mid-t1, t1);

    // move buffer to front
    blockSwap(buf, buf=from+t1, s);

    // order the block for interleave buffered merge (this step is necessary because otherwise the buffer would not be big enough in almost every case)
    blockSort(from+t1+s,until-t2, s);

    // BUFFERED MERGE SORTED BLOCKS (lastly including the right odd end)
    outer_loop: for( int end = buf + 2*s;; )
    {
      int m=end;
      // skip over blocks that are already in order
      inner_loop: for(;;) {
        if(    m >= until-s2   ) break outer_loop;
        if( compare(m,m-1) < 0 ) break inner_loop;
        m += s;
      }
      end = min(m+s,until-s2);

      // tape merge
      buf = practicalInplaceMerge_bufferedMerge(buf,buf+s,m, m,end);
    }

    if( s2 > 0 && compare(until-s2-1,until-s2) > 0 ) {
      int m = until-s2;
      buf = practicalInplaceMerge_bufferedMerge(buf,buf+s, m,m, until);
    }

    // move/roll buffer back and sort it
    for( ; buf+s < until; buf++ )
      swap(buf+s,buf);
    insertionSort(until-s, until);
  }

  default int practicalInplaceMerge_bufferedMerge( int buf, int l, int L, int r, int R )
  {
    // tape merge
    while( l < L )
      swap(buf++, r >= R || compare(l,r) <= 0 ? l++ : r++);
    return buf;
  }

  /** Sorts blocks with their last element each as key.
   *  Uses selection sort which only requires O(n) swaps.
   *
   *  @param from  First index of the first block.
   *  @param until The end of the last block.
   *  @param s     Block size.
   */
  private void blockSort( int from, int until, int s )
  {
    assert from <= until;
    assert (until-from) % s == 0;

    for( int i=from; i < until; i+=s )
    {
      // find min. block
      int j=i+s-1;
      for( int k=j; (k+=s) < until; )
      {
        int c = compare(k,j);
        if( c < 0 || c==0 && compare(k-s+1, j-s+1) < 0 )
          j=k;
      }
      j -= s-1;
      // move min. block to front
      blockSwap(i,j,s);
    }
  }
}
