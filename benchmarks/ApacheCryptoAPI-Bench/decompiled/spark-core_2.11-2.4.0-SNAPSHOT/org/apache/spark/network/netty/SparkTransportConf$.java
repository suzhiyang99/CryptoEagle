package org.apache.spark.network.netty;

import java.util.Map;
import org.apache.spark.SparkConf;
import org.apache.spark.network.util.ConfigProvider;
import org.apache.spark.network.util.TransportConf;
import scala.Predef$;
import scala.StringContext;
import scala.collection.JavaConverters$;
import scala.math.package$;
import scala.runtime.BoxesRunTime;

public final class SparkTransportConf$ {
   public static final SparkTransportConf$ MODULE$;
   private final int MAX_DEFAULT_NETTY_THREADS;

   static {
      new SparkTransportConf$();
   }

   private int MAX_DEFAULT_NETTY_THREADS() {
      return this.MAX_DEFAULT_NETTY_THREADS;
   }

   public TransportConf fromSparkConf(SparkConf _conf, String module, int numUsableCores) {
      SparkConf conf = _conf.clone();
      int numThreads = this.defaultNumThreads(numUsableCores);
      conf.setIfMissing((new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"spark.", ".io.serverThreads"})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{module})), BoxesRunTime.boxToInteger(numThreads).toString());
      conf.setIfMissing((new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"spark.", ".io.clientThreads"})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{module})), BoxesRunTime.boxToInteger(numThreads).toString());
      return new TransportConf(module, new ConfigProvider(conf) {
         private final SparkConf conf$1;

         public String get(String name) {
            return this.conf$1.get(name);
         }

         public String get(String name, String defaultValue) {
            return this.conf$1.get(name, defaultValue);
         }

         public Iterable<Map.Entry<String, String>> getAll() {
            return ((Map)JavaConverters$.MODULE$.mapAsJavaMapConverter(Predef$.MODULE$.refArrayOps(this.conf$1.getAll()).toMap(Predef$.MODULE$.$conforms())).asJava()).entrySet();
         }

         public {
            this.conf$1 = conf$1;
         }
      });
   }

   public int fromSparkConf$default$3() {
      return 0;
   }

   private int defaultNumThreads(int numUsableCores) {
      int availableCores = numUsableCores > 0 ? numUsableCores : Runtime.getRuntime().availableProcessors();
      return package$.MODULE$.min(availableCores, this.MAX_DEFAULT_NETTY_THREADS());
   }

   private SparkTransportConf$() {
      MODULE$ = this;
      this.MAX_DEFAULT_NETTY_THREADS = 8;
   }
}
