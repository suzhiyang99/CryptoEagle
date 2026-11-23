package org.apache.spark.storage;

import org.apache.spark.Dependency;
import org.apache.spark.rdd.RDD;
import org.apache.spark.rdd.RDDOperationScope;
import org.apache.spark.util.Utils$;
import scala.None$;
import scala.Option;
import scala.Option$;
import scala.Serializable;
import scala.collection.Seq;
import scala.collection.Seq$;

public final class RDDInfo$ {
   public static final RDDInfo$ MODULE$;

   static {
      new RDDInfo$();
   }

   public RDDInfo fromRdd(RDD<?> rdd) {
      String rddName = (String)Option$.MODULE$.apply(rdd.name()).getOrElse(new Serializable(rdd) {
         public static final long serialVersionUID = 0L;
         private final RDD rdd$1;

         public final String apply() {
            return Utils$.MODULE$.getFormattedClassName(this.rdd$1);
         }

         public {
            this.rdd$1 = rdd$1;
         }
      });
      Seq parentIds = (Seq)rdd.dependencies().map(new Serializable() {
         public static final long serialVersionUID = 0L;

         public final int apply(Dependency<?> x$1) {
            return x$1.rdd().id();
         }
      }, Seq$.MODULE$.canBuildFrom());
      return new RDDInfo(rdd.id(), rddName, rdd.partitions().length, rdd.getStorageLevel(), parentIds, rdd.creationSite().shortForm(), rdd.scope());
   }

   public String $lessinit$greater$default$6() {
      return "";
   }

   public Option<RDDOperationScope> $lessinit$greater$default$7() {
      return None$.MODULE$;
   }

   private RDDInfo$() {
      MODULE$ = this;
   }
}
