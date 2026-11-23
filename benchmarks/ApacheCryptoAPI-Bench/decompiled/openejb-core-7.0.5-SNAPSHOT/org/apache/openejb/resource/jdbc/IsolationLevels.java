package org.apache.openejb.resource.jdbc;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import org.apache.openejb.util.Join;

public class IsolationLevels {
   private static final Map<String, Integer> isolation = new HashMap();

   public static int getIsolationLevel(String s) {
      if (!isolation.containsKey(s)) {
         throw new IllegalArgumentException("No such transaction isolation level '" + s + "'.  Possible values are " + Join.join(", ", (Collection)isolation.keySet()));
      } else {
         int level = (Integer)isolation.get(s);
         return level;
      }
   }

   static {
      isolation.put("NONE", 0);
      isolation.put("READ_COMMITTED", 2);
      isolation.put("READ_UNCOMMITTED", 1);
      isolation.put("REPEATABLE_READ", 4);
      isolation.put("SERIALIZABLE", 8);
   }
}
