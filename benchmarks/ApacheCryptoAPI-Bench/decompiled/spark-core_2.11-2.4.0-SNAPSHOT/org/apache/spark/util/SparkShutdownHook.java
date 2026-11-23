package org.apache.spark.util;

import scala.Function0;
import scala.reflect.ScalaSignature;
import scala.runtime.BoxedUnit;

@ScalaSignature(
   bytes = "\u0006\u0001e2A!\u0001\u0002\u0005\u0017\t\t2\u000b]1sWNCW\u000f\u001e3po:Dun\\6\u000b\u0005\r!\u0011\u0001B;uS2T!!\u0002\u0004\u0002\u000bM\u0004\u0018M]6\u000b\u0005\u001dA\u0011AB1qC\u000eDWMC\u0001\n\u0003\ry'oZ\u0002\u0001'\r\u0001AB\u0005\t\u0003\u001bAi\u0011A\u0004\u0006\u0002\u001f\u0005)1oY1mC&\u0011\u0011C\u0004\u0002\u0007\u0003:L(+\u001a4\u0011\u0007MA\"$D\u0001\u0015\u0015\t)b#\u0001\u0003mC:<'\"A\f\u0002\t)\fg/Y\u0005\u00033Q\u0011!bQ8na\u0006\u0014\u0018M\u00197f!\tY\u0002!D\u0001\u0003\u0011!i\u0002A!b\u0001\n\u0013q\u0012\u0001\u00039sS>\u0014\u0018\u000e^=\u0016\u0003}\u0001\"!\u0004\u0011\n\u0005\u0005r!aA%oi\"A1\u0005\u0001B\u0001B\u0003%q$A\u0005qe&|'/\u001b;zA!AQ\u0005\u0001B\u0001B\u0003%a%\u0001\u0003i_>\\\u0007cA\u0007(S%\u0011\u0001F\u0004\u0002\n\rVt7\r^5p]B\u0002\"!\u0004\u0016\n\u0005-r!\u0001B+oSRDQ!\f\u0001\u0005\u00029\na\u0001P5oSRtDc\u0001\u000e0a!)Q\u0004\fa\u0001?!)Q\u0005\fa\u0001M!)!\u0007\u0001C!g\u0005I1m\\7qCJ,Gk\u001c\u000b\u0003?QBQ!N\u0019A\u0002i\tQa\u001c;iKJDQa\u000e\u0001\u0005\u0002a\n1A];o)\u0005I\u0003"
)
public class SparkShutdownHook implements Comparable<SparkShutdownHook> {
   private final int priority;
   private final Function0<BoxedUnit> hook;

   private int priority() {
      return this.priority;
   }

   public int compareTo(SparkShutdownHook other) {
      return other.priority() - this.priority();
   }

   public void run() {
      this.hook.apply$mcV$sp();
   }

   public SparkShutdownHook(int priority, Function0<BoxedUnit> hook) {
      this.priority = priority;
      this.hook = hook;
   }
}
