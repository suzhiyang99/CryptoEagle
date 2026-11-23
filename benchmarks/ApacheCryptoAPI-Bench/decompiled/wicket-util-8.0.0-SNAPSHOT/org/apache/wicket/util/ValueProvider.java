package org.apache.wicket.util;

import java.util.function.Supplier;

public class ValueProvider<T> implements Supplier<T> {
   private final T value;

   public ValueProvider(T value) {
      this.value = value;
   }

   public T get() {
      return this.value;
   }

   public static <T> ValueProvider<T> of(T value) {
      return new ValueProvider<T>(value);
   }
}
