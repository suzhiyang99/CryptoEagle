package org.apache.spark.executor;

import java.net.URL;
import java.util.Properties;
import org.apache.spark.util.SparkUncaughtExceptionHandler;
import org.apache.spark.util.SparkUncaughtExceptionHandler$;
import scala.collection.Seq;
import scala.collection.immutable.Nil$;

public final class Executor$ {
   public static final Executor$ MODULE$;
   private final ThreadLocal<Properties> taskDeserializationProps;

   static {
      new Executor$();
   }

   public ThreadLocal<Properties> taskDeserializationProps() {
      return this.taskDeserializationProps;
   }

   public Seq<URL> $lessinit$greater$default$4() {
      return Nil$.MODULE$;
   }

   public boolean $lessinit$greater$default$5() {
      return false;
   }

   public Thread.UncaughtExceptionHandler $lessinit$greater$default$6() {
      return new SparkUncaughtExceptionHandler(SparkUncaughtExceptionHandler$.MODULE$.$lessinit$greater$default$1());
   }

   private Executor$() {
      MODULE$ = this;
      this.taskDeserializationProps = new ThreadLocal();
   }
}
