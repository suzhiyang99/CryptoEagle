package org.apache.spark.deploy.master;

import scala.Enumeration;
import scala.reflect.ScalaSignature;

@ScalaSignature(
   bytes = "\u0006\u0001E:a!\u0001\u0002\t\u0002\ta\u0011aC,pe.,'o\u0015;bi\u0016T!a\u0001\u0003\u0002\r5\f7\u000f^3s\u0015\t)a!\u0001\u0004eKBdw.\u001f\u0006\u0003\u000f!\tQa\u001d9be.T!!\u0003\u0006\u0002\r\u0005\u0004\u0018m\u00195f\u0015\u0005Y\u0011aA8sOB\u0011QBD\u0007\u0002\u0005\u00191qB\u0001E\u0001\u0005A\u00111bV8sW\u0016\u00148\u000b^1uKN\u0011a\"\u0005\t\u0003%Ui\u0011a\u0005\u0006\u0002)\u0005)1oY1mC&\u0011ac\u0005\u0002\f\u000b:,X.\u001a:bi&|g\u000eC\u0003\u0019\u001d\u0011\u0005!$\u0001\u0004=S:LGOP\u0002\u0001)\u0005aQ\u0001B\b\u000f\u0001q\u0001\"!\b\u0010\u000e\u00039I!aH\u000b\u0003\u000bY\u000bG.^3\t\u000f\u0005r!\u0019!C\u0001E\u0005)\u0011\tT%W\u000bV\tA\u0004\u0003\u0004%\u001d\u0001\u0006I\u0001H\u0001\u0007\u00032Ke+\u0012\u0011\t\u000f\u0019r!\u0019!C\u0001E\u0005!A)R!E\u0011\u0019Ac\u0002)A\u00059\u0005)A)R!EA!9!F\u0004b\u0001\n\u0003\u0011\u0013A\u0004#F\u0007>kU*S*T\u0013>sU\t\u0012\u0005\u0007Y9\u0001\u000b\u0011\u0002\u000f\u0002\u001f\u0011+5iT'N\u0013N\u001b\u0016j\u0014(F\t\u0002BqA\f\bC\u0002\u0013\u0005!%A\u0004V\u001d.suj\u0016(\t\rAr\u0001\u0015!\u0003\u001d\u0003!)fj\u0013(P/:\u0003\u0003"
)
public final class WorkerState {
   public static Enumeration.Value UNKNOWN() {
      return WorkerState$.MODULE$.UNKNOWN();
   }

   public static Enumeration.Value DECOMMISSIONED() {
      return WorkerState$.MODULE$.DECOMMISSIONED();
   }

   public static Enumeration.Value DEAD() {
      return WorkerState$.MODULE$.DEAD();
   }

   public static Enumeration.Value ALIVE() {
      return WorkerState$.MODULE$.ALIVE();
   }

   public static Enumeration.Value withName(String var0) {
      return WorkerState$.MODULE$.withName(var0);
   }

   public static Enumeration.Value apply(int var0) {
      return WorkerState$.MODULE$.apply(var0);
   }

   public static int maxId() {
      return WorkerState$.MODULE$.maxId();
   }

   public static Enumeration.ValueSet values() {
      return WorkerState$.MODULE$.values();
   }

   public static String toString() {
      return WorkerState$.MODULE$.toString();
   }
}
