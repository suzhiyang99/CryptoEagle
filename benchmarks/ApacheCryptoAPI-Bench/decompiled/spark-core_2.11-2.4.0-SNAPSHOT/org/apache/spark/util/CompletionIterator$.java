package org.apache.spark.util;

import scala.Function0;
import scala.collection.Iterator;
import scala.runtime.BoxedUnit;

public final class CompletionIterator$ {
   public static final CompletionIterator$ MODULE$;

   static {
      new CompletionIterator$();
   }

   public <A, I extends Iterator<A>> CompletionIterator<A, I> apply(I sub, Function0<BoxedUnit> completionFunction) {
      return new CompletionIterator<A, I>(sub, completionFunction) {
         private final Function0 completionFunction$1;

         public void completion() {
            this.completionFunction$1.apply$mcV$sp();
         }

         public {
            this.completionFunction$1 = completionFunction$1;
         }
      };
   }

   private CompletionIterator$() {
      MODULE$ = this;
   }
}
