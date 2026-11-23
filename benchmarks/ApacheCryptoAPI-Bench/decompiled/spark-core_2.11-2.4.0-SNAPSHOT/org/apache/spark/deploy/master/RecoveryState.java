package org.apache.spark.deploy.master;

import scala.Enumeration;
import scala.reflect.ScalaSignature;

@ScalaSignature(
   bytes = "\u0006\u0001I:a!\u0001\u0002\t\u0002\u0011a\u0011!\u0004*fG>4XM]=Ti\u0006$XM\u0003\u0002\u0004\t\u00051Q.Y:uKJT!!\u0002\u0004\u0002\r\u0011,\u0007\u000f\\8z\u0015\t9\u0001\"A\u0003ta\u0006\u00148N\u0003\u0002\n\u0015\u00051\u0011\r]1dQ\u0016T\u0011aC\u0001\u0004_J<\u0007CA\u0007\u000f\u001b\u0005\u0011aAB\b\u0003\u0011\u0003!\u0001CA\u0007SK\u000e|g/\u001a:z'R\fG/Z\n\u0003\u001dE\u0001\"AE\u000b\u000e\u0003MQ\u0011\u0001F\u0001\u0006g\u000e\fG.Y\u0005\u0003-M\u00111\"\u00128v[\u0016\u0014\u0018\r^5p]\")\u0001D\u0004C\u00015\u00051A(\u001b8jiz\u001a\u0001\u0001F\u0001\r\u000b\u0011ab\u0002A\u000f\u0003\u00175\u000b7\u000f^3s'R\fG/\u001a\t\u0003=}i\u0011AD\u0005\u0003AU\u0011QAV1mk\u0016DqA\t\bC\u0002\u0013\u00051%A\u0004T)\u0006sEIQ-\u0016\u0003uAa!\n\b!\u0002\u0013i\u0012\u0001C*U\u0003:#%)\u0017\u0011\t\u000f\u001dr!\u0019!C\u0001G\u0005)\u0011\tT%W\u000b\"1\u0011F\u0004Q\u0001\nu\ta!\u0011'J-\u0016\u0003\u0003bB\u0016\u000f\u0005\u0004%\taI\u0001\u000b%\u0016\u001buJV#S\u0013:;\u0005BB\u0017\u000fA\u0003%Q$A\u0006S\u000b\u000e{e+\u0012*J\u001d\u001e\u0003\u0003bB\u0018\u000f\u0005\u0004%\taI\u0001\u0014\u0007>k\u0005\u000bT#U\u0013:;uLU#D\u001fZ+%+\u0017\u0005\u0007c9\u0001\u000b\u0011B\u000f\u0002)\r{U\n\u0015'F)&sui\u0018*F\u0007>3VIU-!\u0001"
)
public final class RecoveryState {
   public static Enumeration.Value COMPLETING_RECOVERY() {
      return RecoveryState$.MODULE$.COMPLETING_RECOVERY();
   }

   public static Enumeration.Value RECOVERING() {
      return RecoveryState$.MODULE$.RECOVERING();
   }

   public static Enumeration.Value ALIVE() {
      return RecoveryState$.MODULE$.ALIVE();
   }

   public static Enumeration.Value STANDBY() {
      return RecoveryState$.MODULE$.STANDBY();
   }

   public static Enumeration.Value withName(String var0) {
      return RecoveryState$.MODULE$.withName(var0);
   }

   public static Enumeration.Value apply(int var0) {
      return RecoveryState$.MODULE$.apply(var0);
   }

   public static int maxId() {
      return RecoveryState$.MODULE$.maxId();
   }

   public static Enumeration.ValueSet values() {
      return RecoveryState$.MODULE$.values();
   }

   public static String toString() {
      return RecoveryState$.MODULE$.toString();
   }
}
