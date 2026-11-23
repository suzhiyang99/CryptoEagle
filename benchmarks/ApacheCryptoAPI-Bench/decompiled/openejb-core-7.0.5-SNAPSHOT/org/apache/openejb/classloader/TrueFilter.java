package org.apache.openejb.classloader;

import org.apache.xbean.finder.filter.Filter;

public class TrueFilter implements Filter {
   public static final TrueFilter INSTANCE = new TrueFilter();

   public boolean accept(String name) {
      return true;
   }
}
