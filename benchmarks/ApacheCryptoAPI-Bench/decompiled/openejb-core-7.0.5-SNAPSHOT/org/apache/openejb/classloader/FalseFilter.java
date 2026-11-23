package org.apache.openejb.classloader;

import org.apache.xbean.finder.filter.Filter;

public class FalseFilter implements Filter {
   public static final FalseFilter INSTANCE = new FalseFilter();

   public boolean accept(String name) {
      return false;
   }
}
