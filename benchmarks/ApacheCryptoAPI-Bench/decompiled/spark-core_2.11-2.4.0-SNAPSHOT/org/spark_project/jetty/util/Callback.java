package org.spark_project.jetty.util;

public interface Callback {
   Callback NOOP = new Callback() {
   };

   default void succeeded() {
   }

   default void failed(Throwable x) {
   }

   default boolean isNonBlocking() {
      return false;
   }

   public interface NonBlocking extends Callback {
      default boolean isNonBlocking() {
         return true;
      }
   }

   public static class Nested implements Callback {
      private final Callback callback;

      public Nested(Callback callback) {
         this.callback = callback;
      }

      public Nested(Nested nested) {
         this.callback = nested.callback;
      }

      public void succeeded() {
         this.callback.succeeded();
      }

      public void failed(Throwable x) {
         this.callback.failed(x);
      }

      public boolean isNonBlocking() {
         return this.callback.isNonBlocking();
      }
   }

   /** @deprecated */
   @Deprecated
   public static class Adapter implements Callback {
   }
}
