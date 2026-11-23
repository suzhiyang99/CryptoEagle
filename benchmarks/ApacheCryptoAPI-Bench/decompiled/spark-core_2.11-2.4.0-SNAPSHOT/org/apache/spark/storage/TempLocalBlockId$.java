package org.apache.spark.storage;

import java.util.UUID;
import scala.None$;
import scala.Option;
import scala.Serializable;
import scala.Some;
import scala.runtime.AbstractFunction1;

public final class TempLocalBlockId$ extends AbstractFunction1<UUID, TempLocalBlockId> implements Serializable {
   public static final TempLocalBlockId$ MODULE$;

   static {
      new TempLocalBlockId$();
   }

   public final String toString() {
      return "TempLocalBlockId";
   }

   public TempLocalBlockId apply(UUID id) {
      return new TempLocalBlockId(id);
   }

   public Option<UUID> unapply(TempLocalBlockId x$0) {
      return (Option<UUID>)(x$0 == null ? None$.MODULE$ : new Some(x$0.id()));
   }

   private Object readResolve() {
      return MODULE$;
   }

   private TempLocalBlockId$() {
      MODULE$ = this;
   }
}
