package org.apache.spark.storage;

import scala.MatchError;
import scala.Predef$;
import scala.Serializable;
import scala.Tuple2;
import scala.collection.Seq;
import scala.collection.Seq$;
import scala.collection.TraversableOnce;
import scala.collection.immutable.List;
import scala.collection.immutable.List$;
import scala.collection.mutable.LinkedHashSet;
import scala.collection.mutable.LinkedHashSet$;
import scala.runtime.BoxesRunTime;
import scala.runtime.RichInt$;
import scala.util.Random;

public final class BlockReplicationUtils$ {
   public static final BlockReplicationUtils$ MODULE$;

   static {
      new BlockReplicationUtils$();
   }

   private List<Object> getSampleIds(int n, int m, Random r) {
      LinkedHashSet indices = (LinkedHashSet)RichInt$.MODULE$.to$extension0(Predef$.MODULE$.intWrapper(n - m + 1), n).foldLeft(LinkedHashSet$.MODULE$.empty(), new Serializable(r) {
         public static final long serialVersionUID = 0L;
         private final Random r$1;

         public final LinkedHashSet<Object> apply(LinkedHashSet<Object> x0$1, int x1$1) {
            Tuple2 var3 = new Tuple2(x0$1, BoxesRunTime.boxToInteger(x1$1));
            if (var3 != null) {
               LinkedHashSet set = (LinkedHashSet)var3._1();
               int i = var3._2$mcI$sp();
               int t = this.r$1.nextInt(i) + 1;
               LinkedHashSet var6 = set.contains(BoxesRunTime.boxToInteger(t)) ? (LinkedHashSet)set.$plus(BoxesRunTime.boxToInteger(i)) : (LinkedHashSet)set.$plus(BoxesRunTime.boxToInteger(t));
               return var6;
            } else {
               throw new MatchError(var3);
            }
         }

         public {
            this.r$1 = r$1;
         }
      });
      return ((TraversableOnce)indices.map(new Serializable() {
         public static final long serialVersionUID = 0L;

         public final int apply(int x$1) {
            return this.apply$mcII$sp(x$1);
         }

         public int apply$mcII$sp(int x$1) {
            return x$1 - 1;
         }
      }, LinkedHashSet$.MODULE$.canBuildFrom())).toList();
   }

   public <T> List<T> getRandomSample(Seq<T> elems, int m, Random r) {
      return elems.size() > m ? (List)this.getSampleIds(elems.size(), m, r).map(new Serializable(elems) {
         public static final long serialVersionUID = 0L;
         private final Seq elems$1;

         public final T apply(int x$2) {
            return (T)this.elems$1.apply(x$2);
         }

         public {
            this.elems$1 = elems$1;
         }
      }, List$.MODULE$.canBuildFrom()) : r.shuffle(elems, Seq$.MODULE$.canBuildFrom()).toList();
   }

   private BlockReplicationUtils$() {
      MODULE$ = this;
   }
}
