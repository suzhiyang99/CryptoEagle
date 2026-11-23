package org.apache.spark.util;

import scala.None$;
import scala.Option;
import scala.Serializable;
import scala.Some;
import scala.Tuple3;

public final class MethodIdentifier$ implements Serializable {
   public static final MethodIdentifier$ MODULE$;

   static {
      new MethodIdentifier$();
   }

   public final String toString() {
      return "MethodIdentifier";
   }

   public <T> MethodIdentifier<T> apply(Class<T> cls, String name, String desc) {
      return new MethodIdentifier<T>(cls, name, desc);
   }

   public <T> Option<Tuple3<Class<T>, String, String>> unapply(MethodIdentifier<T> x$0) {
      return (Option<Tuple3<Class<T>, String, String>>)(x$0 == null ? None$.MODULE$ : new Some(new Tuple3(x$0.cls(), x$0.name(), x$0.desc())));
   }

   private Object readResolve() {
      return MODULE$;
   }

   private MethodIdentifier$() {
      MODULE$ = this;
   }
}
