package org.apache.spark.status;

import scala.reflect.ScalaSignature;

@ScalaSignature(
   bytes = "\u0006\u0001%3a!\u0001\u0002\u0002\u0002\u0011Q!A\u0003'jm\u0016,e\u000e^5us*\u00111\u0001B\u0001\u0007gR\fG/^:\u000b\u0005\u00151\u0011!B:qCJ\\'BA\u0004\t\u0003\u0019\t\u0007/Y2iK*\t\u0011\"A\u0002pe\u001e\u001c\"\u0001A\u0006\u0011\u00051yQ\"A\u0007\u000b\u00039\tQa]2bY\u0006L!\u0001E\u0007\u0003\r\u0005s\u0017PU3g\u0011\u0015\u0011\u0002\u0001\"\u0001\u0015\u0003\u0019a\u0014N\\5u}\r\u0001A#A\u000b\u0011\u0005Y\u0001Q\"\u0001\u0002\t\u000fa\u0001\u0001\u0019!C\u00013\u0005iA.Y:u/JLG/\u001a+j[\u0016,\u0012A\u0007\t\u0003\u0019mI!\u0001H\u0007\u0003\t1{gn\u001a\u0005\b=\u0001\u0001\r\u0011\"\u0001 \u0003Ea\u0017m\u001d;Xe&$X\rV5nK~#S-\u001d\u000b\u0003A\r\u0002\"\u0001D\u0011\n\u0005\tj!\u0001B+oSRDq\u0001J\u000f\u0002\u0002\u0003\u0007!$A\u0002yIEBaA\n\u0001!B\u0013Q\u0012A\u00047bgR<&/\u001b;f)&lW\r\t\u0005\u0006Q\u0001!\t!K\u0001\u0006oJLG/\u001a\u000b\u0005A)z\u0013\u0007C\u0003,O\u0001\u0007A&A\u0003ti>\u0014X\r\u0005\u0002\u0017[%\u0011aF\u0001\u0002\u0015\u000b2,W.\u001a8u)J\f7m[5oON#xN]3\t\u000bA:\u0003\u0019\u0001\u000e\u0002\u00079|w\u000fC\u00043OA\u0005\t\u0019A\u001a\u0002\u001b\rDWmY6Ue&<w-\u001a:t!\taA'\u0003\u00026\u001b\t9!i\\8mK\u0006t\u0007\"B\u001c\u0001\r#A\u0014\u0001\u00033p+B$\u0017\r^3\u0015\u0003e\u0002\"\u0001\u0004\u001e\n\u0005mj!aA!os\"9Q\bAI\u0001\n\u0003q\u0014aD<sSR,G\u0005Z3gCVdG\u000fJ\u001a\u0016\u0003}R#a\r!,\u0003\u0005\u0003\"AQ$\u000e\u0003\rS!\u0001R#\u0002\u0013Ut7\r[3dW\u0016$'B\u0001$\u000e\u0003)\tgN\\8uCRLwN\\\u0005\u0003\u0011\u000e\u0013\u0011#\u001e8dQ\u0016\u001c7.\u001a3WCJL\u0017M\\2f\u0001"
)
public abstract class LiveEntity {
   private long lastWriteTime = -1L;

   public long lastWriteTime() {
      return this.lastWriteTime;
   }

   public void lastWriteTime_$eq(long x$1) {
      this.lastWriteTime = x$1;
   }

   public void write(ElementTrackingStore store, long now, boolean checkTriggers) {
      store.write(this.doUpdate(), checkTriggers || this.lastWriteTime() == -1L);
      this.lastWriteTime_$eq(now);
   }

   public boolean write$default$3() {
      return false;
   }

   public abstract Object doUpdate();
}
