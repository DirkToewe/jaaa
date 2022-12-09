package com.github.jaaa.partition;

import com.github.jaaa.permute.SwapAccess;

public interface PartitionAccess extends SwapAccess
{
  int  key( int i );
  int nKeys();
}
