package org.apache.spark.api.r;

import scala.None$;
import scala.Option;
import scala.Serializable;
import scala.Some;
import scala.runtime.AbstractFunction1;

public final class JVMObjectId$ extends AbstractFunction1<String, JVMObjectId> implements Serializable {
   public static final JVMObjectId$ MODULE$;

   static {
      new JVMObjectId$();
   }

   public final String toString() {
      return "JVMObjectId";
   }

   public JVMObjectId apply(String id) {
      return new JVMObjectId(id);
   }

   public Option<String> unapply(JVMObjectId x$0) {
      return (Option<String>)(x$0 == null ? None$.MODULE$ : new Some(x$0.id()));
   }

   private Object readResolve() {
      return MODULE$;
   }

   private JVMObjectId$() {
      MODULE$ = this;
   }
}
