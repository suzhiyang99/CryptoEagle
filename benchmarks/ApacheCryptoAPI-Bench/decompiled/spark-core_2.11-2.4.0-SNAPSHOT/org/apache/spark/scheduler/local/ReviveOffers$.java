package org.apache.spark.scheduler.local;

import scala.Serializable;
import scala.runtime.AbstractFunction0;

public final class ReviveOffers$ extends AbstractFunction0<ReviveOffers> implements Serializable {
   public static final ReviveOffers$ MODULE$;

   static {
      new ReviveOffers$();
   }

   public final String toString() {
      return "ReviveOffers";
   }

   public ReviveOffers apply() {
      return new ReviveOffers();
   }

   public boolean unapply(ReviveOffers x$0) {
      return x$0 != null;
   }

   private Object readResolve() {
      return MODULE$;
   }

   private ReviveOffers$() {
      MODULE$ = this;
   }
}
