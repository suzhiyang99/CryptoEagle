package org.spark_project.jetty.util;

public abstract class IteratingNestedCallback extends IteratingCallback {
   final Callback _callback;

   public IteratingNestedCallback(Callback callback) {
      this._callback = callback;
   }

   public boolean isNonBlocking() {
      return this._callback.isNonBlocking();
   }

   protected void onCompleteSuccess() {
      this._callback.succeeded();
   }

   protected void onCompleteFailure(Throwable x) {
      this._callback.failed(x);
   }

   public String toString() {
      return String.format("%s@%x", this.getClass().getSimpleName(), this.hashCode());
   }
}
