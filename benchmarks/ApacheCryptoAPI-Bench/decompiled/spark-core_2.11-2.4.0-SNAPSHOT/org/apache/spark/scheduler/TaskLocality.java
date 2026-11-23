package org.apache.spark.scheduler;

import org.apache.spark.annotation.DeveloperApi;
import scala.Enumeration;
import scala.reflect.ScalaSignature;

@DeveloperApi
@ScalaSignature(
   bytes = "\u0006\u0001\u0019;Q!\u0001\u0002\t\u0002-\tA\u0002V1tW2{7-\u00197jifT!a\u0001\u0003\u0002\u0013M\u001c\u0007.\u001a3vY\u0016\u0014(BA\u0003\u0007\u0003\u0015\u0019\b/\u0019:l\u0015\t9\u0001\"\u0001\u0004ba\u0006\u001c\u0007.\u001a\u0006\u0002\u0013\u0005\u0019qN]4\u0004\u0001A\u0011A\"D\u0007\u0002\u0005\u0019)aB\u0001E\u0001\u001f\taA+Y:l\u0019>\u001c\u0017\r\\5usN\u0011Q\u0002\u0005\t\u0003#Qi\u0011A\u0005\u0006\u0002'\u0005)1oY1mC&\u0011QC\u0005\u0002\f\u000b:,X.\u001a:bi&|g\u000eC\u0003\u0018\u001b\u0011\u0005\u0001$\u0001\u0004=S:LGO\u0010\u000b\u0002\u0017!9!$\u0004b\u0001\n\u0003Y\u0012!\u0004)S\u001f\u000e+5kU0M\u001f\u000e\u000bE*F\u0001\u001d!\tib$D\u0001\u000e\u0013\tyBCA\u0003WC2,X\r\u0003\u0004\"\u001b\u0001\u0006I\u0001H\u0001\u000f!J{5)R*T?2{5)\u0011'!\u0011\u001d\u0019SB1A\u0005\u0002m\t!BT(E\u000b~cujQ!M\u0011\u0019)S\u0002)A\u00059\u0005Yaj\u0014#F?2{5)\u0011'!\u0011\u001d9SB1A\u0005\u0002m\tqAT(`!J+e\t\u0003\u0004*\u001b\u0001\u0006I\u0001H\u0001\t\u001d>{\u0006KU#GA!91&\u0004b\u0001\n\u0003Y\u0012A\u0003*B\u0007.{FjT\"B\u0019\"1Q&\u0004Q\u0001\nq\t1BU!D\u0017~cujQ!MA!9q&\u0004b\u0001\n\u0003Y\u0012aA!O3\"1\u0011'\u0004Q\u0001\nq\tA!\u0011(ZA\u0015!a\"\u0004\u0001\u001d\u0011\u0015!T\u0002\"\u00016\u0003%I7/\u00117m_^,G\rF\u00027sq\u0002\"!E\u001c\n\u0005a\u0012\"a\u0002\"p_2,\u0017M\u001c\u0005\u0006uM\u0002\raO\u0001\u000bG>t7\u000f\u001e:bS:$\bCA\u000f3\u0011\u0015i4\u00071\u0001<\u0003%\u0019wN\u001c3ji&|g\u000e\u000b\u0002\u000e\u007fA\u0011\u0001iQ\u0007\u0002\u0003*\u0011!\tB\u0001\u000bC:tw\u000e^1uS>t\u0017B\u0001#B\u00051!UM^3m_B,'/\u00119jQ\t\u0001q\b"
)
public final class TaskLocality {
   public static boolean isAllowed(Enumeration.Value var0, Enumeration.Value var1) {
      return TaskLocality$.MODULE$.isAllowed(var0, var1);
   }

   public static Enumeration.Value ANY() {
      return TaskLocality$.MODULE$.ANY();
   }

   public static Enumeration.Value RACK_LOCAL() {
      return TaskLocality$.MODULE$.RACK_LOCAL();
   }

   public static Enumeration.Value NO_PREF() {
      return TaskLocality$.MODULE$.NO_PREF();
   }

   public static Enumeration.Value NODE_LOCAL() {
      return TaskLocality$.MODULE$.NODE_LOCAL();
   }

   public static Enumeration.Value PROCESS_LOCAL() {
      return TaskLocality$.MODULE$.PROCESS_LOCAL();
   }

   public static Enumeration.Value withName(String var0) {
      return TaskLocality$.MODULE$.withName(var0);
   }

   public static Enumeration.Value apply(int var0) {
      return TaskLocality$.MODULE$.apply(var0);
   }

   public static int maxId() {
      return TaskLocality$.MODULE$.maxId();
   }

   public static Enumeration.ValueSet values() {
      return TaskLocality$.MODULE$.values();
   }

   public static String toString() {
      return TaskLocality$.MODULE$.toString();
   }
}
