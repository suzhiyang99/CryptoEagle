package org.apache.openejb.util;

import java.util.Collections;
import java.util.List;

public class CollectionsUtil {
   public static <T> List<T> safe(List<T> list) {
      return list == null ? Collections.emptyList() : list;
   }
}
