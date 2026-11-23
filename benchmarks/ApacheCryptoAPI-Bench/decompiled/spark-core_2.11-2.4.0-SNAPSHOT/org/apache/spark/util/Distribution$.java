package org.apache.spark.util;

import java.io.PrintStream;
import scala.None$;
import scala.Option;
import scala.Serializable;
import scala.Some;
import scala.collection.Traversable;
import scala.collection.mutable.StringBuilder;

public final class Distribution$ {
   public static final Distribution$ MODULE$;

   static {
      new Distribution$();
   }

   public Option<Distribution> apply(Traversable<Object> data) {
      return (Option<Distribution>)(data.size() > 0 ? new Some(new Distribution(data)) : None$.MODULE$);
   }

   public void showQuantiles(PrintStream out, Traversable<Object> quantiles) {
      out.println("min\t25%\t50%\t75%\tmax");
      quantiles.foreach(new Serializable(out) {
         public static final long serialVersionUID = 0L;
         private final PrintStream out$2;

         public final void apply(double q) {
            this.apply$mcVD$sp(q);
         }

         public void apply$mcVD$sp(double q) {
            this.out$2.print((new StringBuilder()).append(q).append((Object)"\t").toString());
         }

         public {
            this.out$2 = out$2;
         }
      });
      out.println();
   }

   public PrintStream showQuantiles$default$1() {
      return System.out;
   }

   private Distribution$() {
      MODULE$ = this;
   }
}
