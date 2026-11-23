package org.apache.spark.executor;

import org.apache.spark.annotation.DeveloperApi;
import scala.Enumeration;
import scala.reflect.ScalaSignature;

@DeveloperApi
@ScalaSignature(
   bytes = "\u0006\u0001i:Q!\u0001\u0002\t\u0002-\ta\u0002R1uCJ+\u0017\rZ'fi\"|GM\u0003\u0002\u0004\t\u0005AQ\r_3dkR|'O\u0003\u0002\u0006\r\u0005)1\u000f]1sW*\u0011q\u0001C\u0001\u0007CB\f7\r[3\u000b\u0003%\t1a\u001c:h\u0007\u0001\u0001\"\u0001D\u0007\u000e\u0003\t1QA\u0004\u0002\t\u0002=\u0011a\u0002R1uCJ+\u0017\rZ'fi\"|GmE\u0002\u000e!Y\u0001\"!\u0005\u000b\u000e\u0003IQ\u0011aE\u0001\u0006g\u000e\fG.Y\u0005\u0003+I\u00111\"\u00128v[\u0016\u0014\u0018\r^5p]B\u0011\u0011cF\u0005\u00031I\u0011AbU3sS\u0006d\u0017N_1cY\u0016DQAG\u0007\u0005\u0002m\ta\u0001P5oSRtD#A\u0006\u0006\t9i\u0001!\b\t\u0003=}i\u0011!D\u0005\u0003AQ\u0011QAV1mk\u0016DqAI\u0007C\u0002\u0013\u00051%\u0001\u0004NK6|'/_\u000b\u0002;!1Q%\u0004Q\u0001\nu\tq!T3n_JL\b\u0005C\u0004(\u001b\t\u0007I\u0011A\u0012\u0002\t\u0011K7o\u001b\u0005\u0007S5\u0001\u000b\u0011B\u000f\u0002\u000b\u0011K7o\u001b\u0011\t\u000f-j!\u0019!C\u0001G\u00051\u0001*\u00193p_BDa!L\u0007!\u0002\u0013i\u0012a\u0002%bI>|\u0007\u000f\t\u0005\b_5\u0011\r\u0011\"\u0001$\u0003\u001dqU\r^<pe.Da!M\u0007!\u0002\u0013i\u0012\u0001\u0003(fi^|'o\u001b\u0011)\u00055\u0019\u0004C\u0001\u001b8\u001b\u0005)$B\u0001\u001c\u0005\u0003)\tgN\\8uCRLwN\\\u0005\u0003qU\u0012A\u0002R3wK2|\u0007/\u001a:Ba&D#\u0001A\u001a"
)
public final class DataReadMethod {
   public static Enumeration.Value Network() {
      return DataReadMethod$.MODULE$.Network();
   }

   public static Enumeration.Value Hadoop() {
      return DataReadMethod$.MODULE$.Hadoop();
   }

   public static Enumeration.Value Disk() {
      return DataReadMethod$.MODULE$.Disk();
   }

   public static Enumeration.Value Memory() {
      return DataReadMethod$.MODULE$.Memory();
   }

   public static Enumeration.Value withName(String var0) {
      return DataReadMethod$.MODULE$.withName(var0);
   }

   public static Enumeration.Value apply(int var0) {
      return DataReadMethod$.MODULE$.apply(var0);
   }

   public static int maxId() {
      return DataReadMethod$.MODULE$.maxId();
   }

   public static Enumeration.ValueSet values() {
      return DataReadMethod$.MODULE$.values();
   }

   public static String toString() {
      return DataReadMethod$.MODULE$.toString();
   }
}
