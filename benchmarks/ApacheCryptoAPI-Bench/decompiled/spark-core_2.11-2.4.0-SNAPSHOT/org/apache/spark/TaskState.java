package org.apache.spark;

import scala.Enumeration;
import scala.reflect.ScalaSignature;

@ScalaSignature(
   bytes = "\u0006\u0001=;a!\u0001\u0002\t\u0002\tA\u0011!\u0003+bg.\u001cF/\u0019;f\u0015\t\u0019A!A\u0003ta\u0006\u00148N\u0003\u0002\u0006\r\u00051\u0011\r]1dQ\u0016T\u0011aB\u0001\u0004_J<\u0007CA\u0005\u000b\u001b\u0005\u0011aAB\u0006\u0003\u0011\u0003\u0011ABA\u0005UCN\\7\u000b^1uKN\u0011!\"\u0004\t\u0003\u001dEi\u0011a\u0004\u0006\u0002!\u0005)1oY1mC&\u0011!c\u0004\u0002\f\u000b:,X.\u001a:bi&|g\u000eC\u0003\u0015\u0015\u0011\u0005a#\u0001\u0004=S:LGOP\u0002\u0001)\u0005A\u0001b\u0002\r\u000b\u0005\u0004%\t!G\u0001\n\u0019\u0006+fj\u0011%J\u001d\u001e+\u0012A\u0007\t\u00037qi\u0011AC\u0005\u0003;E\u0011QAV1mk\u0016Daa\b\u0006!\u0002\u0013Q\u0012A\u0003'B+:\u001b\u0005*\u0013(HA!9\u0011E\u0003b\u0001\n\u0003I\u0012a\u0002*V\u001d:Kej\u0012\u0005\u0007G)\u0001\u000b\u0011\u0002\u000e\u0002\u0011I+fJT%O\u000f\u0002Bq!\n\u0006C\u0002\u0013\u0005\u0011$\u0001\u0005G\u0013:K5\u000bS#E\u0011\u00199#\u0002)A\u00055\u0005Ia)\u0013(J'\"+E\t\t\u0005\bS)\u0011\r\u0011\"\u0001\u001a\u0003\u00191\u0015)\u0013'F\t\"11F\u0003Q\u0001\ni\tqAR!J\u0019\u0016#\u0005\u0005C\u0004.\u0015\t\u0007I\u0011A\r\u0002\r-KE\nT#E\u0011\u0019y#\u0002)A\u00055\u000591*\u0013'M\u000b\u0012\u0003\u0003bB\u0019\u000b\u0005\u0004%\t!G\u0001\u0005\u0019>\u001bF\u000b\u0003\u00044\u0015\u0001\u0006IAG\u0001\u0006\u0019>\u001bF\u000b\t\u0005\bk)\u0011\r\u0011\"\u00037\u0003=1\u0015JT%T\u0011\u0016#ul\u0015+B)\u0016\u001bV#A\u001c\u0011\u0007aj$$D\u0001:\u0015\tQ4(A\u0005j[6,H/\u00192mK*\u0011AhD\u0001\u000bG>dG.Z2uS>t\u0017B\u0001 :\u0005\r\u0019V\r\u001e\u0005\u0007\u0001*\u0001\u000b\u0011B\u001c\u0002!\u0019Ke*S*I\u000b\u0012{6\u000bV!U\u000bN\u0003S\u0001B\u0006\u000b\u0001iAQa\u0011\u0006\u0005\u0002\u0011\u000b\u0001\"[:GC&dW\r\u001a\u000b\u0003\u000b\"\u0003\"A\u0004$\n\u0005\u001d{!a\u0002\"p_2,\u0017M\u001c\u0005\u0006\u0013\n\u0003\rAS\u0001\u0006gR\fG/\u001a\t\u00037\u0005CQ\u0001\u0014\u0006\u0005\u00025\u000b!\"[:GS:L7\u000f[3e)\t)e\nC\u0003J\u0017\u0002\u0007!\n"
)
public final class TaskState {
   public static boolean isFinished(Enumeration.Value var0) {
      return TaskState$.MODULE$.isFinished(var0);
   }

   public static boolean isFailed(Enumeration.Value var0) {
      return TaskState$.MODULE$.isFailed(var0);
   }

   public static Enumeration.Value LOST() {
      return TaskState$.MODULE$.LOST();
   }

   public static Enumeration.Value KILLED() {
      return TaskState$.MODULE$.KILLED();
   }

   public static Enumeration.Value FAILED() {
      return TaskState$.MODULE$.FAILED();
   }

   public static Enumeration.Value FINISHED() {
      return TaskState$.MODULE$.FINISHED();
   }

   public static Enumeration.Value RUNNING() {
      return TaskState$.MODULE$.RUNNING();
   }

   public static Enumeration.Value LAUNCHING() {
      return TaskState$.MODULE$.LAUNCHING();
   }

   public static Enumeration.Value withName(String var0) {
      return TaskState$.MODULE$.withName(var0);
   }

   public static Enumeration.Value apply(int var0) {
      return TaskState$.MODULE$.apply(var0);
   }

   public static int maxId() {
      return TaskState$.MODULE$.maxId();
   }

   public static Enumeration.ValueSet values() {
      return TaskState$.MODULE$.values();
   }

   public static String toString() {
      return TaskState$.MODULE$.toString();
   }
}
