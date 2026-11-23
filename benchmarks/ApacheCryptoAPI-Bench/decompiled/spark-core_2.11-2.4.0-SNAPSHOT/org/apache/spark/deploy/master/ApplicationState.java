package org.apache.spark.deploy.master;

import scala.Enumeration;
import scala.reflect.ScalaSignature;

@ScalaSignature(
   bytes = "\u0006\u0001e:a!\u0001\u0002\t\u0002\ta\u0011\u0001E!qa2L7-\u0019;j_:\u001cF/\u0019;f\u0015\t\u0019A!\u0001\u0004nCN$XM\u001d\u0006\u0003\u000b\u0019\ta\u0001Z3qY>L(BA\u0004\t\u0003\u0015\u0019\b/\u0019:l\u0015\tI!\"\u0001\u0004ba\u0006\u001c\u0007.\u001a\u0006\u0002\u0017\u0005\u0019qN]4\u0011\u00055qQ\"\u0001\u0002\u0007\r=\u0011\u0001\u0012\u0001\u0002\u0011\u0005A\t\u0005\u000f\u001d7jG\u0006$\u0018n\u001c8Ti\u0006$Xm\u0005\u0002\u000f#A\u0011!#F\u0007\u0002')\tA#A\u0003tG\u0006d\u0017-\u0003\u0002\u0017'\tYQI\\;nKJ\fG/[8o\u0011\u0015Ab\u0002\"\u0001\u001b\u0003\u0019a\u0014N\\5u}\r\u0001A#\u0001\u0007\u0006\t=q\u0001\u0001\b\t\u0003;yi\u0011AD\u0005\u0003?U\u0011QAV1mk\u0016Dq!\t\bC\u0002\u0013\u0005!%A\u0004X\u0003&#\u0016JT$\u0016\u0003qAa\u0001\n\b!\u0002\u0013a\u0012\u0001C,B\u0013RKej\u0012\u0011\t\u000f\u0019r!\u0019!C\u0001E\u00059!+\u0016(O\u0013:;\u0005B\u0002\u0015\u000fA\u0003%A$\u0001\u0005S+:s\u0015JT$!\u0011\u001dQcB1A\u0005\u0002\t\n\u0001BR%O\u0013NCU\t\u0012\u0005\u0007Y9\u0001\u000b\u0011\u0002\u000f\u0002\u0013\u0019Ke*S*I\u000b\u0012\u0003\u0003b\u0002\u0018\u000f\u0005\u0004%\tAI\u0001\u0007\r\u0006KE*\u0012#\t\rAr\u0001\u0015!\u0003\u001d\u0003\u001d1\u0015)\u0013'F\t\u0002BqA\r\bC\u0002\u0013\u0005!%\u0001\u0004L\u00132cU\t\u0012\u0005\u0007i9\u0001\u000b\u0011\u0002\u000f\u0002\u000f-KE\nT#EA!9aG\u0004b\u0001\n\u0003\u0011\u0013aB+O\u0017:{uK\u0014\u0005\u0007q9\u0001\u000b\u0011\u0002\u000f\u0002\u0011Us5JT(X\u001d\u0002\u0002"
)
public final class ApplicationState {
   public static Enumeration.Value UNKNOWN() {
      return ApplicationState$.MODULE$.UNKNOWN();
   }

   public static Enumeration.Value KILLED() {
      return ApplicationState$.MODULE$.KILLED();
   }

   public static Enumeration.Value FAILED() {
      return ApplicationState$.MODULE$.FAILED();
   }

   public static Enumeration.Value FINISHED() {
      return ApplicationState$.MODULE$.FINISHED();
   }

   public static Enumeration.Value RUNNING() {
      return ApplicationState$.MODULE$.RUNNING();
   }

   public static Enumeration.Value WAITING() {
      return ApplicationState$.MODULE$.WAITING();
   }

   public static Enumeration.Value withName(String var0) {
      return ApplicationState$.MODULE$.withName(var0);
   }

   public static Enumeration.Value apply(int var0) {
      return ApplicationState$.MODULE$.apply(var0);
   }

   public static int maxId() {
      return ApplicationState$.MODULE$.maxId();
   }

   public static Enumeration.ValueSet values() {
      return ApplicationState$.MODULE$.values();
   }

   public static String toString() {
      return ApplicationState$.MODULE$.toString();
   }
}
