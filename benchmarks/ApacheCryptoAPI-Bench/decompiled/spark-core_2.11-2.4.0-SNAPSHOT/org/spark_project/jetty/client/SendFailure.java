package org.spark_project.jetty.client;

public class SendFailure {
   public final Throwable failure;
   public final boolean retry;

   public SendFailure(Throwable failure, boolean retry) {
      this.failure = failure;
      this.retry = retry;
   }

   public String toString() {
      return String.format("%s[failure=%s,retry=%b]", super.toString(), this.failure, this.retry);
   }
}
