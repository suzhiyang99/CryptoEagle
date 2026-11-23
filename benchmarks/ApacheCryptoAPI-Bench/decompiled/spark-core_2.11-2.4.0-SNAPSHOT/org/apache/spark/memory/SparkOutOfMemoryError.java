package org.apache.spark.memory;

import org.apache.spark.annotation.Private;

@Private
public final class SparkOutOfMemoryError extends OutOfMemoryError {
   public SparkOutOfMemoryError(String s) {
      super(s);
   }

   public SparkOutOfMemoryError(OutOfMemoryError e) {
      super(e.getMessage());
   }
}
