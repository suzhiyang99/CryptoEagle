package org.apache.spark.executor;

import org.apache.spark.annotation.DeveloperApi;
import scala.Enumeration;

@DeveloperApi
public final class DataWriteMethod$ extends Enumeration {
   public static final DataWriteMethod$ MODULE$;
   private final Enumeration.Value Hadoop;

   static {
      new DataWriteMethod$();
   }

   public Enumeration.Value Hadoop() {
      return this.Hadoop;
   }

   private DataWriteMethod$() {
      MODULE$ = this;
      this.Hadoop = this.Value();
   }
}
