package org.apache.spark.util;

import scala.reflect.ScalaSignature;

@ScalaSignature(
   bytes = "\u0006\u0001)2Q!\u0001\u0002\u0001\t)\u00111bU=ti\u0016l7\t\\8dW*\u00111\u0001B\u0001\u0005kRLGN\u0003\u0002\u0006\r\u0005)1\u000f]1sW*\u0011q\u0001C\u0001\u0007CB\f7\r[3\u000b\u0003%\t1a\u001c:h'\r\u00011\"\u0005\t\u0003\u0019=i\u0011!\u0004\u0006\u0002\u001d\u0005)1oY1mC&\u0011\u0001#\u0004\u0002\u0007\u0003:L(+\u001a4\u0011\u0005I\u0019R\"\u0001\u0002\n\u0005Q\u0011!!B\"m_\u000e\\\u0007\"\u0002\f\u0001\t\u0003A\u0012A\u0002\u001fj]&$hh\u0001\u0001\u0015\u0003e\u0001\"A\u0005\u0001\t\u000fm\u0001!\u0019!C\u00019\u0005YQ.\u001b8Q_2dG+[7f+\u0005i\u0002C\u0001\u0007\u001f\u0013\tyRB\u0001\u0003M_:<\u0007BB\u0011\u0001A\u0003%Q$\u0001\u0007nS:\u0004v\u000e\u001c7US6,\u0007\u0005C\u0003$\u0001\u0011\u0005A%A\u0007hKR$\u0016.\\3NS2d\u0017n\u001d\u000b\u0002;!)a\u0005\u0001C\u0001O\u0005aq/Y5u)&dG\u000eV5nKR\u0011Q\u0004\u000b\u0005\u0006S\u0015\u0002\r!H\u0001\u000bi\u0006\u0014x-\u001a;US6,\u0007"
)
public class SystemClock implements Clock {
   private final long minPollTime = 25L;

   public long minPollTime() {
      return this.minPollTime;
   }

   public long getTimeMillis() {
      return System.currentTimeMillis();
   }

   public long waitTillTime(long targetTime) {
      long currentTime = 0L;
      currentTime = System.currentTimeMillis();
      long waitTime = targetTime - currentTime;
      if (waitTime <= 0L) {
         return currentTime;
      } else {
         long pollTime = (long)scala.math.package$.MODULE$.max((double)waitTime / (double)10.0F, (double)this.minPollTime());

         while(true) {
            currentTime = System.currentTimeMillis();
            waitTime = targetTime - currentTime;
            if (waitTime <= 0L) {
               return currentTime;
            }

            long sleepTime = scala.math.package$.MODULE$.min(waitTime, pollTime);
            Thread.sleep(sleepTime);
         }
      }
   }
}
