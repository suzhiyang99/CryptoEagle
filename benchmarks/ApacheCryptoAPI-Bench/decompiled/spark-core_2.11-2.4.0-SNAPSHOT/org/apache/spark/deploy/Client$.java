package org.apache.spark.deploy;

import org.apache.spark.SparkConf;
import scala.Predef$;
import scala.sys.package$;

public final class Client$ {
   public static final Client$ MODULE$;

   static {
      new Client$();
   }

   public void main(String[] args) {
      if (!package$.MODULE$.props().contains("SPARK_SUBMIT")) {
         Predef$.MODULE$.println("WARNING: This client is deprecated and will be removed in a future version of Spark");
         Predef$.MODULE$.println("Use ./bin/spark-submit with \"--master spark://host:port\"");
      }

      (new ClientApp()).start(args, new SparkConf());
   }

   private Client$() {
      MODULE$ = this;
   }
}
