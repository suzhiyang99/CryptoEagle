package org.apache.spark.util;

import scala.Option;
import scala.Option$;
import scala.Serializable;
import scala.Some;

public final class CausedBy$ {
   public static final CausedBy$ MODULE$;

   static {
      new CausedBy$();
   }

   public Option<Throwable> unapply(Throwable e) {
      return Option$.MODULE$.<Throwable>apply(e.getCause()).<Throwable>flatMap(new Serializable() {
         public static final long serialVersionUID = 0L;

         public final Option<Throwable> apply(Throwable cause) {
            return CausedBy$.MODULE$.unapply(cause);
         }
      }).<Throwable>orElse(new Serializable(e) {
         public static final long serialVersionUID = 0L;
         private final Throwable e$1;

         public final Some<Throwable> apply() {
            return new Some<Throwable>(this.e$1);
         }

         public {
            this.e$1 = e$1;
         }
      });
   }

   private CausedBy$() {
      MODULE$ = this;
   }
}
