package org.apache.spark.scheduler;

import org.apache.spark.executor.TaskMetrics;
import scala.None$;
import scala.Option;
import scala.Serializable;
import scala.Some;
import scala.Tuple3;
import scala.runtime.BoxesRunTime;

public final class RuntimePercentage$ implements Serializable {
   public static final RuntimePercentage$ MODULE$;

   static {
      new RuntimePercentage$();
   }

   public RuntimePercentage apply(long totalTime, TaskMetrics metrics) {
      double denom = (double)totalTime;
      Some fetchTime = new Some(BoxesRunTime.boxToLong(metrics.shuffleReadMetrics().fetchWaitTime()));
      Option fetch = fetchTime.map(new Serializable(denom) {
         public static final long serialVersionUID = 0L;
         private final double denom$1;

         public final double apply(long x$13) {
            return this.apply$mcDJ$sp(x$13);
         }

         public double apply$mcDJ$sp(long x$13) {
            return (double)x$13 / this.denom$1;
         }

         public {
            this.denom$1 = denom$1;
         }
      });
      double exec = (double)(metrics.executorRunTime() - BoxesRunTime.unboxToLong(fetchTime.getOrElse(new Serializable() {
         public static final long serialVersionUID = 0L;

         public final long apply() {
            return this.apply$mcJ$sp();
         }

         public long apply$mcJ$sp() {
            return 0L;
         }
      }))) / denom;
      double other = (double)1.0F - (exec + BoxesRunTime.unboxToDouble(fetch.getOrElse(new Serializable() {
         public static final long serialVersionUID = 0L;

         public final double apply() {
            return this.apply$mcD$sp();
         }

         public double apply$mcD$sp() {
            return (double)0.0F;
         }
      })));
      return new RuntimePercentage(exec, fetch, other);
   }

   public RuntimePercentage apply(double executorPct, Option<Object> fetchPct, double other) {
      return new RuntimePercentage(executorPct, fetchPct, other);
   }

   public Option<Tuple3<Object, Option<Object>, Object>> unapply(RuntimePercentage x$0) {
      return (Option<Tuple3<Object, Option<Object>, Object>>)(x$0 == null ? None$.MODULE$ : new Some(new Tuple3(BoxesRunTime.boxToDouble(x$0.executorPct()), x$0.fetchPct(), BoxesRunTime.boxToDouble(x$0.other()))));
   }

   private Object readResolve() {
      return MODULE$;
   }

   private RuntimePercentage$() {
      MODULE$ = this;
   }
}
