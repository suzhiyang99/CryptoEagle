package org.apache.spark.storage.memory;

import scala.None$;
import scala.Option;
import scala.Serializable;
import scala.Some;
import scala.Tuple3;
import scala.reflect.ClassTag;
import scala.runtime.BoxesRunTime;

public final class DeserializedMemoryEntry$ implements Serializable {
   public static final DeserializedMemoryEntry$ MODULE$;

   static {
      new DeserializedMemoryEntry$();
   }

   public final String toString() {
      return "DeserializedMemoryEntry";
   }

   public <T> DeserializedMemoryEntry<T> apply(Object value, long size, ClassTag<T> classTag) {
      return new DeserializedMemoryEntry<T>(value, size, classTag);
   }

   public <T> Option<Tuple3<Object, Object, ClassTag<T>>> unapply(DeserializedMemoryEntry<T> x$0) {
      return (Option<Tuple3<Object, Object, ClassTag<T>>>)(x$0 == null ? None$.MODULE$ : new Some(new Tuple3(x$0.value(), BoxesRunTime.boxToLong(x$0.size()), x$0.classTag())));
   }

   private Object readResolve() {
      return MODULE$;
   }

   private DeserializedMemoryEntry$() {
      MODULE$ = this;
   }
}
