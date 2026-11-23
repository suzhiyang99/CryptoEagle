package org.apache.spark.scheduler;

import scala.Predef$;
import scala.Serializable;
import scala.collection.Seq;
import scala.collection.immutable.Map;
import scala.collection.immutable.Set;
import scala.collection.mutable.HashMap;
import scala.collection.mutable.HashSet;

public final class InputFormatInfo$ {
   public static final InputFormatInfo$ MODULE$;

   static {
      new InputFormatInfo$();
   }

   public Map<String, Set<SplitInfo>> computePreferredLocations(Seq<InputFormatInfo> formats) {
      HashMap nodeToSplit = new HashMap();
      formats.foreach(new Serializable(nodeToSplit) {
         public static final long serialVersionUID = 0L;
         public final HashMap nodeToSplit$1;

         public final void apply(InputFormatInfo inputSplit) {
            Set splits = inputSplit.org$apache$spark$scheduler$InputFormatInfo$$findPreferredLocations();
            splits.foreach(new Serializable(this) {
               public static final long serialVersionUID = 0L;
               // $FF: synthetic field
               private final <undefinedtype> $outer;

               public final HashSet<SplitInfo> apply(SplitInfo split) {
                  String location = split.hostLocation();
                  HashSet set = (HashSet)this.$outer.nodeToSplit$1.getOrElseUpdate(location, new Serializable(this) {
                     public static final long serialVersionUID = 0L;

                     public final HashSet<SplitInfo> apply() {
                        return new HashSet<SplitInfo>();
                     }
                  });
                  return set.$plus$eq(split);
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

         public {
            this.nodeToSplit$1 = nodeToSplit$1;
         }
      });
      return nodeToSplit.mapValues(new Serializable() {
         public static final long serialVersionUID = 0L;

         public final Set<SplitInfo> apply(HashSet<SplitInfo> x$1) {
            return x$1.toSet();
         }
      }).toMap(Predef$.MODULE$.$conforms());
   }

   private InputFormatInfo$() {
      MODULE$ = this;
   }
}
