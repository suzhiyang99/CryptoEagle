package org.apache.spark.util.collection;

import scala.runtime.BoxesRunTime;

public class OpenHashSet$Hasher$mcJ$sp extends OpenHashSet.Hasher<Object> {
   public int hash(long o) {
      return this.hash$mcJ$sp(o);
   }

   public int hash$mcJ$sp(long o) {
      return BoxesRunTime.boxToLong(o).hashCode();
   }
}
