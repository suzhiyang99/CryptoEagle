package org.apache.spark.deploy;

import scala.Enumeration;
import scala.Predef$;
import scala.Serializable;
import scala.collection.immutable.StringOps;
import scala.reflect.ScalaSignature;
import scala.runtime.BoxesRunTime;

@ScalaSignature(
   bytes = "\u0006\u0001\u00193Q!\u0001\u0002\u0001\u0005)\u00111#\u0012=fGV$xN\u001d#fg\u000e\u0014\u0018\u000e\u001d;j_:T!a\u0001\u0003\u0002\r\u0011,\u0007\u000f\\8z\u0015\t)a!A\u0003ta\u0006\u00148N\u0003\u0002\b\u0011\u00051\u0011\r]1dQ\u0016T\u0011!C\u0001\u0004_J<7c\u0001\u0001\f#A\u0011AbD\u0007\u0002\u001b)\ta\"A\u0003tG\u0006d\u0017-\u0003\u0002\u0011\u001b\t1\u0011I\\=SK\u001a\u0004\"\u0001\u0004\n\n\u0005Mi!\u0001D*fe&\fG.\u001b>bE2,\u0007\u0002C\u000b\u0001\u0005\u000b\u0007I\u0011A\f\u0002\u000b\u0005\u0004\b/\u00133\u0004\u0001U\t\u0001\u0004\u0005\u0002\u001a99\u0011ABG\u0005\u000375\ta\u0001\u0015:fI\u00164\u0017BA\u000f\u001f\u0005\u0019\u0019FO]5oO*\u00111$\u0004\u0005\tA\u0001\u0011\t\u0011)A\u00051\u00051\u0011\r\u001d9JI\u0002B\u0001B\t\u0001\u0003\u0006\u0004%\taI\u0001\u0007Kb,7-\u00133\u0016\u0003\u0011\u0002\"\u0001D\u0013\n\u0005\u0019j!aA%oi\"A\u0001\u0006\u0001B\u0001B\u0003%A%A\u0004fq\u0016\u001c\u0017\n\u001a\u0011\t\u0011)\u0002!Q1A\u0005\u0002\r\nQaY8sKND\u0001\u0002\f\u0001\u0003\u0002\u0003\u0006I\u0001J\u0001\u0007G>\u0014Xm\u001d\u0011\t\u00119\u0002!Q1A\u0005\u0002=\nQa\u001d;bi\u0016,\u0012\u0001\r\t\u0003cUr!AM\u001a\u000e\u0003\tI!\u0001\u000e\u0002\u0002\u001b\u0015CXmY;u_J\u001cF/\u0019;f\u0013\t1tGA\u0003WC2,X-\u0003\u00029\u001b\tYQI\\;nKJ\fG/[8o\u0011!Q\u0004A!A!\u0002\u0013\u0001\u0014AB:uCR,\u0007\u0005C\u0003=\u0001\u0011\u0005Q(\u0001\u0004=S:LGO\u0010\u000b\u0006}}\u0002\u0015I\u0011\t\u0003e\u0001AQ!F\u001eA\u0002aAQAI\u001eA\u0002\u0011BQAK\u001eA\u0002\u0011BQAL\u001eA\u0002ABQ\u0001\u0012\u0001\u0005B\u0015\u000b\u0001\u0002^8TiJLgn\u001a\u000b\u00021\u0001"
)
public class ExecutorDescription implements Serializable {
   private final String appId;
   private final int execId;
   private final int cores;
   private final Enumeration.Value state;

   public String appId() {
      return this.appId;
   }

   public int execId() {
      return this.execId;
   }

   public int cores() {
      return this.cores;
   }

   public Enumeration.Value state() {
      return this.state;
   }

   public String toString() {
      return (new StringOps(Predef$.MODULE$.augmentString("ExecutorState(appId=%s, execId=%d, cores=%d, state=%s)"))).format(Predef$.MODULE$.genericWrapArray(new Object[]{this.appId(), BoxesRunTime.boxToInteger(this.execId()), BoxesRunTime.boxToInteger(this.cores()), this.state()}));
   }

   public ExecutorDescription(String appId, int execId, int cores, Enumeration.Value state) {
      this.appId = appId;
      this.execId = execId;
      this.cores = cores;
      this.state = state;
   }
}
