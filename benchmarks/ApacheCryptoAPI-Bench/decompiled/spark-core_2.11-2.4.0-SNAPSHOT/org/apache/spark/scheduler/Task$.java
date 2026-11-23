package org.apache.spark.scheduler;

import java.util.Properties;
import org.apache.spark.SparkEnv$;
import org.apache.spark.executor.TaskMetrics;
import org.apache.spark.executor.TaskMetrics$;
import scala.None$;
import scala.Option;
import scala.Serializable;
import scala.reflect.ClassTag$;

public final class Task$ implements Serializable {
   public static final Task$ MODULE$;

   static {
      new Task$();
   }

   public <T> Properties $lessinit$greater$default$4() {
      return new Properties();
   }

   public <T> byte[] $lessinit$greater$default$5() {
      return SparkEnv$.MODULE$.get().closureSerializer().newInstance().serialize(TaskMetrics$.MODULE$.registered(), ClassTag$.MODULE$.apply(TaskMetrics.class)).array();
   }

   public <T> Option<Object> $lessinit$greater$default$6() {
      return None$.MODULE$;
   }

   public <T> Option<String> $lessinit$greater$default$7() {
      return None$.MODULE$;
   }

   public <T> Option<String> $lessinit$greater$default$8() {
      return None$.MODULE$;
   }

   private Object readResolve() {
      return MODULE$;
   }

   private Task$() {
      MODULE$ = this;
   }
}
