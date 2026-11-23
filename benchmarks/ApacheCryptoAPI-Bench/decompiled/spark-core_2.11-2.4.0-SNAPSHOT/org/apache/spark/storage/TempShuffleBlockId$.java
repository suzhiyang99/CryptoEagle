package org.apache.spark.storage;

import java.util.UUID;
import scala.None$;
import scala.Option;
import scala.Serializable;
import scala.Some;
import scala.runtime.AbstractFunction1;

public final class TempShuffleBlockId$ extends AbstractFunction1<UUID, TempShuffleBlockId> implements Serializable {
   public static final TempShuffleBlockId$ MODULE$;

   static {
      new TempShuffleBlockId$();
   }

   public final String toString() {
      return "TempShuffleBlockId";
   }

   public TempShuffleBlockId apply(UUID id) {
      return new TempShuffleBlockId(id);
   }

   public Option<UUID> unapply(TempShuffleBlockId x$0) {
      return (Option<UUID>)(x$0 == null ? None$.MODULE$ : new Some(x$0.id()));
   }

   private Object readResolve() {
      return MODULE$;
   }

   private TempShuffleBlockId$() {
      MODULE$ = this;
   }
}
