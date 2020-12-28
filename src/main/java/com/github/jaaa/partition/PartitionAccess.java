package com.github.jaaa.partition;

import com.github.jaaa.SwapAccess;

public interface PartitionAccess extends SwapAccess
{
  public int  key( int i );
  public int nKeys();
}
