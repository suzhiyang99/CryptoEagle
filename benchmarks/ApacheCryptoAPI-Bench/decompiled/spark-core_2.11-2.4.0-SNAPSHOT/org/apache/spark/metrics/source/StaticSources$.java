package org.apache.spark.metrics.source;

import scala.Predef$;
import scala.collection.Seq;
import scala.collection.Seq$;

public final class StaticSources$ {
   public static final StaticSources$ MODULE$;
   private final Seq<Source> allSources;

   static {
      new StaticSources$();
   }

   public Seq<Source> allSources() {
      return this.allSources;
   }

   private StaticSources$() {
      MODULE$ = this;
      this.allSources = (Seq)Seq$.MODULE$.apply(Predef$.MODULE$.wrapRefArray(new Source[]{CodegenMetrics$.MODULE$, HiveCatalogMetrics$.MODULE$}));
   }
}
