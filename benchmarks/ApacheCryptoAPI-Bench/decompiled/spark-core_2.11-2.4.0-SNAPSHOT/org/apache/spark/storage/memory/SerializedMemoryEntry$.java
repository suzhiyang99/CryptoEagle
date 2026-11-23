package org.apache.spark.storage.memory;

import org.apache.spark.memory.MemoryMode;
import org.apache.spark.util.io.ChunkedByteBuffer;
import scala.None$;
import scala.Option;
import scala.Serializable;
import scala.Some;
import scala.Tuple3;
import scala.reflect.ClassTag;

public final class SerializedMemoryEntry$ implements Serializable {
   public static final SerializedMemoryEntry$ MODULE$;

   static {
      new SerializedMemoryEntry$();
   }

   public final String toString() {
      return "SerializedMemoryEntry";
   }

   public <T> SerializedMemoryEntry<T> apply(ChunkedByteBuffer buffer, MemoryMode memoryMode, ClassTag<T> classTag) {
      return new SerializedMemoryEntry<T>(buffer, memoryMode, classTag);
   }

   public <T> Option<Tuple3<ChunkedByteBuffer, MemoryMode, ClassTag<T>>> unapply(SerializedMemoryEntry<T> x$0) {
      return (Option<Tuple3<ChunkedByteBuffer, MemoryMode, ClassTag<T>>>)(x$0 == null ? None$.MODULE$ : new Some(new Tuple3(x$0.buffer(), x$0.memoryMode(), x$0.classTag())));
   }

   private Object readResolve() {
      return MODULE$;
   }

   private SerializedMemoryEntry$() {
      MODULE$ = this;
   }
}
