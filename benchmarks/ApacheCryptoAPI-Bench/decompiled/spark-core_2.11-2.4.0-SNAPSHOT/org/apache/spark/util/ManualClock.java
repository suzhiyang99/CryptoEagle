package org.apache.spark.util;

import scala.reflect.ScalaSignature;

@ScalaSignature(
   bytes = "\u0006\u0001}2Q!\u0001\u0002\u0001\t)\u00111\"T1ok\u0006d7\t\\8dW*\u00111\u0001B\u0001\u0005kRLGN\u0003\u0002\u0006\r\u0005)1\u000f]1sW*\u0011q\u0001C\u0001\u0007CB\f7\r[3\u000b\u0003%\t1a\u001c:h'\r\u00011\"\u0005\t\u0003\u0019=i\u0011!\u0004\u0006\u0002\u001d\u0005)1oY1mC&\u0011\u0001#\u0004\u0002\u0007\u0003:L(+\u001a4\u0011\u0005I\u0019R\"\u0001\u0002\n\u0005Q\u0011!!B\"m_\u000e\\\u0007\u0002\u0003\f\u0001\u0005\u0003\u0007I\u0011\u0002\r\u0002\tQLW.Z\u0002\u0001+\u0005I\u0002C\u0001\u0007\u001b\u0013\tYRB\u0001\u0003M_:<\u0007\u0002C\u000f\u0001\u0005\u0003\u0007I\u0011\u0002\u0010\u0002\u0011QLW.Z0%KF$\"a\b\u0012\u0011\u00051\u0001\u0013BA\u0011\u000e\u0005\u0011)f.\u001b;\t\u000f\rb\u0012\u0011!a\u00013\u0005\u0019\u0001\u0010J\u0019\t\u0011\u0015\u0002!\u0011!Q!\ne\tQ\u0001^5nK\u0002BQa\n\u0001\u0005\u0002!\na\u0001P5oSRtDCA\u0015+!\t\u0011\u0002\u0001C\u0003\u0017M\u0001\u0007\u0011\u0004C\u0003(\u0001\u0011\u0005A\u0006F\u0001*\u0011\u0015q\u0003\u0001\"\u00010\u000359W\r\u001e+j[\u0016l\u0015\u000e\u001c7jgR\t\u0011\u0004C\u00032\u0001\u0011\u0005!'A\u0004tKR$\u0016.\\3\u0015\u0005}\u0019\u0004\"\u0002\u001b1\u0001\u0004I\u0012!\u0003;j[\u0016$vnU3u\u0011\u00151\u0004\u0001\"\u00018\u0003\u001d\tGM^1oG\u0016$\"a\b\u001d\t\u000be*\u0004\u0019A\r\u0002\u0013QLW.\u001a+p\u0003\u0012$\u0007\"B\u001e\u0001\t\u0003a\u0014\u0001D<bSR$\u0016\u000e\u001c7US6,GCA\r>\u0011\u0015q$\b1\u0001\u001a\u0003)!\u0018M]4fiRKW.\u001a"
)
public class ManualClock implements Clock {
   private long time;

   private long time() {
      return this.time;
   }

   private void time_$eq(long x$1) {
      this.time = x$1;
   }

   public synchronized long getTimeMillis() {
      return this.time();
   }

   public synchronized void setTime(long timeToSet) {
      this.time_$eq(timeToSet);
      this.notifyAll();
   }

   public synchronized void advance(long timeToAdd) {
      this.time_$eq(this.time() + timeToAdd);
      this.notifyAll();
   }

   public synchronized long waitTillTime(long targetTime) {
      while(this.time() < targetTime) {
         this.wait(10L);
      }

      return this.getTimeMillis();
   }

   public ManualClock(long time) {
      this.time = time;
      super();
   }

   public ManualClock() {
      this(0L);
   }
}
