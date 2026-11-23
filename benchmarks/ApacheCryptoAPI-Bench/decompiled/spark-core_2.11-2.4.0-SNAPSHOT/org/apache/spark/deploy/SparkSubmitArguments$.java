package org.apache.spark.deploy;

import scala.collection.immutable.Map;
import scala.sys.package$;

public final class SparkSubmitArguments$ {
   public static final SparkSubmitArguments$ MODULE$;

   static {
      new SparkSubmitArguments$();
   }

   public Map<String, String> $lessinit$greater$default$2() {
      return package$.MODULE$.env();
   }

   private SparkSubmitArguments$() {
      MODULE$ = this;
   }
}
