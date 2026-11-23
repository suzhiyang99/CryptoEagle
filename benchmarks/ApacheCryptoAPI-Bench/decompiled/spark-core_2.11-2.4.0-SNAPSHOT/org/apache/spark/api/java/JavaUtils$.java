package org.apache.spark.api.java;

import scala.Option;
import scala.collection.Map;

public final class JavaUtils$ {
   public static final JavaUtils$ MODULE$;

   static {
      new JavaUtils$();
   }

   public <T> Optional<T> optionToOptional(Option<T> option) {
      return option.isDefined() ? Optional.of(option.get()) : Optional.empty();
   }

   public <A, B> JavaUtils.SerializableMapWrapper<A, B> mapAsSerializableJavaMap(Map<A, B> underlying) {
      return new JavaUtils.SerializableMapWrapper<A, B>(underlying);
   }

   private JavaUtils$() {
      MODULE$ = this;
   }
}
