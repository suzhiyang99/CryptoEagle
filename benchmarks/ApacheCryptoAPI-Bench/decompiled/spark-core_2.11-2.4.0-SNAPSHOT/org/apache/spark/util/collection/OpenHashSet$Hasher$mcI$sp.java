package org.apache.spark.util.collection;

import scala.runtime.BoxesRunTime;

public class OpenHashSet$Hasher$mcI$sp extends OpenHashSet.Hasher<Object> {
   public int hash(int o) {
      return this.hash$mcI$sp(o);
   }

   public int hash$mcI$sp(int o) {
      return BoxesRunTime.boxToInteger(o).hashCode();
   }
}
