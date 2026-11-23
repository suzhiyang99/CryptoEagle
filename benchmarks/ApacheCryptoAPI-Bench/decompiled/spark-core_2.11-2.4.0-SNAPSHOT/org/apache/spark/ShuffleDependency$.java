package org.apache.spark;

import org.apache.spark.serializer.Serializer;
import scala.None$;
import scala.Serializable;

public final class ShuffleDependency$ implements Serializable {
   public static final ShuffleDependency$ MODULE$;

   static {
      new ShuffleDependency$();
   }

   public <K, V, C> Serializer $lessinit$greater$default$3() {
      return SparkEnv$.MODULE$.get().serializer();
   }

   public <K, V, C> None$ $lessinit$greater$default$4() {
      return None$.MODULE$;
   }

   public <K, V, C> None$ $lessinit$greater$default$5() {
      return None$.MODULE$;
   }

   public <K, V, C> boolean $lessinit$greater$default$6() {
      return false;
   }

   private Object readResolve() {
      return MODULE$;
   }

   private ShuffleDependency$() {
      MODULE$ = this;
   }
}
