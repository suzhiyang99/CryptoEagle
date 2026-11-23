package org.apache.spark.util.random;

import scala.NotImplementedError;
import scala.Serializable;
import scala.collection.Iterator;

public abstract class RandomSampler$class {
   public static Iterator sample(RandomSampler $this, Iterator items) {
      return items.filter(new Serializable($this) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final RandomSampler $outer;

         public final boolean apply(T x$1) {
            return this.$outer.sample() > 0;
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
            }
         }
      });
   }

   public static RandomSampler clone(RandomSampler $this) {
      throw new NotImplementedError("clone() is not implemented.");
   }

   public static void $init$(RandomSampler $this) {
   }
}
