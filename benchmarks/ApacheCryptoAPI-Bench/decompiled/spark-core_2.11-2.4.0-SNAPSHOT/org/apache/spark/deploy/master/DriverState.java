package org.apache.spark.deploy.master;

import scala.Enumeration;
import scala.reflect.ScalaSignature;

@ScalaSignature(
   bytes = "\u0006\u0001\u0005;a!\u0001\u0002\t\u0002\u0011a\u0011a\u0003#sSZ,'o\u0015;bi\u0016T!a\u0001\u0003\u0002\r5\f7\u000f^3s\u0015\t)a!\u0001\u0004eKBdw.\u001f\u0006\u0003\u000f!\tQa\u001d9be.T!!\u0003\u0006\u0002\r\u0005\u0004\u0018m\u00195f\u0015\u0005Y\u0011aA8sOB\u0011QBD\u0007\u0002\u0005\u00191qB\u0001E\u0001\tA\u00111\u0002\u0012:jm\u0016\u00148\u000b^1uKN\u0011a\"\u0005\t\u0003%Ui\u0011a\u0005\u0006\u0002)\u0005)1oY1mC&\u0011ac\u0005\u0002\f\u000b:,X.\u001a:bi&|g\u000eC\u0003\u0019\u001d\u0011\u0005!$\u0001\u0004=S:LGOP\u0002\u0001)\u0005aQ\u0001B\b\u000f\u0001q\u0001\"!\b\u0010\u000e\u00039I!aH\u000b\u0003\u000bY\u000bG.^3\t\u000f\u0005r!\u0019!C\u0001E\u0005I1+\u0016\"N\u0013R#V\tR\u000b\u00029!1AE\u0004Q\u0001\nq\t!bU+C\u001b&#F+\u0012#!\u0011\u001d1cB1A\u0005\u0002\t\nqAU+O\u001d&su\t\u0003\u0004)\u001d\u0001\u0006I\u0001H\u0001\t%Vse*\u0013(HA!9!F\u0004b\u0001\n\u0003\u0011\u0013\u0001\u0003$J\u001d&\u001b\u0006*\u0012#\t\r1r\u0001\u0015!\u0003\u001d\u0003%1\u0015JT%T\u0011\u0016#\u0005\u0005C\u0004/\u001d\t\u0007I\u0011\u0001\u0012\u0002\u0017I+E*Q+O\u0007\"Kej\u0012\u0005\u0007a9\u0001\u000b\u0011\u0002\u000f\u0002\u0019I+E*Q+O\u0007\"Kej\u0012\u0011\t\u000fIr!\u0019!C\u0001E\u00059QKT&O\u001f^s\u0005B\u0002\u001b\u000fA\u0003%A$\u0001\u0005V\u001d.suj\u0016(!\u0011\u001d1dB1A\u0005\u0002\t\naaS%M\u0019\u0016#\u0005B\u0002\u001d\u000fA\u0003%A$A\u0004L\u00132cU\t\u0012\u0011\t\u000fir!\u0019!C\u0001E\u00051a)Q%M\u000b\u0012Ca\u0001\u0010\b!\u0002\u0013a\u0012a\u0002$B\u00132+E\t\t\u0005\b}9\u0011\r\u0011\"\u0001#\u0003\u0015)%KU(S\u0011\u0019\u0001e\u0002)A\u00059\u00051QI\u0015*P%\u0002\u0002"
)
public final class DriverState {
   public static Enumeration.Value ERROR() {
      return DriverState$.MODULE$.ERROR();
   }

   public static Enumeration.Value FAILED() {
      return DriverState$.MODULE$.FAILED();
   }

   public static Enumeration.Value KILLED() {
      return DriverState$.MODULE$.KILLED();
   }

   public static Enumeration.Value UNKNOWN() {
      return DriverState$.MODULE$.UNKNOWN();
   }

   public static Enumeration.Value RELAUNCHING() {
      return DriverState$.MODULE$.RELAUNCHING();
   }

   public static Enumeration.Value FINISHED() {
      return DriverState$.MODULE$.FINISHED();
   }

   public static Enumeration.Value RUNNING() {
      return DriverState$.MODULE$.RUNNING();
   }

   public static Enumeration.Value SUBMITTED() {
      return DriverState$.MODULE$.SUBMITTED();
   }

   public static Enumeration.Value withName(String var0) {
      return DriverState$.MODULE$.withName(var0);
   }

   public static Enumeration.Value apply(int var0) {
      return DriverState$.MODULE$.apply(var0);
   }

   public static int maxId() {
      return DriverState$.MODULE$.maxId();
   }

   public static Enumeration.ValueSet values() {
      return DriverState$.MODULE$.values();
   }

   public static String toString() {
      return DriverState$.MODULE$.toString();
   }
}
