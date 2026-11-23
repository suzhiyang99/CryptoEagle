package org.apache.spark.api.java;

import java.io.Serializable;
import java.util.Objects;
import org.spark_project.guava.base.Preconditions;

public final class Optional<T> implements Serializable {
   private static final Optional<?> EMPTY = new Optional();
   private final T value;

   private Optional() {
      this.value = null;
   }

   private Optional(T value) {
      Preconditions.checkNotNull(value);
      this.value = value;
   }

   public static <T> Optional<T> empty() {
      Optional<T> t = EMPTY;
      return t;
   }

   public static <T> Optional<T> of(T value) {
      return new Optional<T>(value);
   }

   public static <T> Optional<T> ofNullable(T value) {
      return value == null ? empty() : of(value);
   }

   public T get() {
      Preconditions.checkNotNull(this.value);
      return this.value;
   }

   public T orElse(T other) {
      return (T)(this.value != null ? this.value : other);
   }

   public boolean isPresent() {
      return this.value != null;
   }

   public static <T> Optional<T> absent() {
      return empty();
   }

   public static <T> Optional<T> fromNullable(T value) {
      return ofNullable(value);
   }

   public T or(T other) {
      return (T)(this.value != null ? this.value : other);
   }

   public T orNull() {
      return this.value;
   }

   public boolean equals(Object obj) {
      if (!(obj instanceof Optional)) {
         return false;
      } else {
         Optional<?> other = (Optional)obj;
         return Objects.equals(this.value, other.value);
      }
   }

   public int hashCode() {
      return this.value == null ? 0 : this.value.hashCode();
   }

   public String toString() {
      return this.value == null ? "Optional.empty" : String.format("Optional[%s]", this.value);
   }
}
