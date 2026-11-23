package org.spark_project.jetty.util;

import java.util.Objects;
import org.spark_project.jetty.util.log.Log;

public interface Promise<C> {
   void succeeded(C var1);

   void failed(Throwable var1);

   public static class Adapter<C> implements Promise<C> {
      public void succeeded(C result) {
      }

      public void failed(Throwable x) {
         Log.getLogger(this.getClass()).warn(x);
      }
   }

   public static class Wrapper<W> implements Promise<W> {
      private final Promise<W> promise;

      public Wrapper(Promise<W> promise) {
         this.promise = (Promise)Objects.requireNonNull(promise);
      }

      public void succeeded(W result) {
         this.promise.succeeded(result);
      }

      public void failed(Throwable x) {
         this.promise.failed(x);
      }

      public Promise<W> getPromise() {
         return this.promise;
      }

      public Promise<W> unwrap() {
         Promise<W> result;
         for(result = this.promise; result instanceof Wrapper; result = (result).unwrap()) {
         }

         return result;
      }
   }
}
