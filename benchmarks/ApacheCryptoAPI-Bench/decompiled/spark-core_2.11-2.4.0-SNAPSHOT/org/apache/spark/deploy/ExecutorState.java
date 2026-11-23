package org.apache.spark.deploy;

import scala.Enumeration;
import scala.reflect.ScalaSignature;

@ScalaSignature(
   bytes = "\u0006\u0001\u0001;a!\u0001\u0002\t\u0002\tQ\u0011!D#yK\u000e,Ho\u001c:Ti\u0006$XM\u0003\u0002\u0004\t\u00051A-\u001a9m_fT!!\u0002\u0004\u0002\u000bM\u0004\u0018M]6\u000b\u0005\u001dA\u0011AB1qC\u000eDWMC\u0001\n\u0003\ry'o\u001a\t\u0003\u00171i\u0011A\u0001\u0004\u0007\u001b\tA\tA\u0001\b\u0003\u001b\u0015CXmY;u_J\u001cF/\u0019;f'\taq\u0002\u0005\u0002\u0011'5\t\u0011CC\u0001\u0013\u0003\u0015\u00198-\u00197b\u0013\t!\u0012CA\u0006F]VlWM]1uS>t\u0007\"\u0002\f\r\t\u0003A\u0012A\u0002\u001fj]&$hh\u0001\u0001\u0015\u0003)AqA\u0007\u0007C\u0002\u0013\u00051$A\u0005M\u0003Vs5\tS%O\u000fV\tA\u0004\u0005\u0002\u001e=5\tA\"\u0003\u0002 '\t)a+\u00197vK\"1\u0011\u0005\u0004Q\u0001\nq\t!\u0002T!V\u001d\u000eC\u0015JT$!\u0011\u001d\u0019CB1A\u0005\u0002m\tqAU+O\u001d&su\t\u0003\u0004&\u0019\u0001\u0006I\u0001H\u0001\t%Vse*\u0013(HA!9q\u0005\u0004b\u0001\n\u0003Y\u0012AB&J\u00192+E\t\u0003\u0004*\u0019\u0001\u0006I\u0001H\u0001\b\u0017&cE*\u0012#!\u0011\u001dYCB1A\u0005\u0002m\taAR!J\u0019\u0016#\u0005BB\u0017\rA\u0003%A$A\u0004G\u0003&cU\t\u0012\u0011\t\u000f=b!\u0019!C\u00017\u0005!AjT*U\u0011\u0019\tD\u0002)A\u00059\u0005)AjT*UA!91\u0007\u0004b\u0001\n\u0003Y\u0012AB#Y\u0013R+E\t\u0003\u00046\u0019\u0001\u0006I\u0001H\u0001\b\u000bbKE+\u0012#!\u000b\u0011iA\u0002\u0001\u000f\t\u000babA\u0011A\u001d\u0002\u0015%\u001ch)\u001b8jg\",G\r\u0006\u0002;{A\u0011\u0001cO\u0005\u0003yE\u0011qAQ8pY\u0016\fg\u000eC\u0003?o\u0001\u0007q(A\u0003ti\u0006$X\r\u0005\u0002\u001em\u0001"
)
public final class ExecutorState {
   public static boolean isFinished(Enumeration.Value var0) {
      return ExecutorState$.MODULE$.isFinished(var0);
   }

   public static Enumeration.Value EXITED() {
      return ExecutorState$.MODULE$.EXITED();
   }

   public static Enumeration.Value LOST() {
      return ExecutorState$.MODULE$.LOST();
   }

   public static Enumeration.Value FAILED() {
      return ExecutorState$.MODULE$.FAILED();
   }

   public static Enumeration.Value KILLED() {
      return ExecutorState$.MODULE$.KILLED();
   }

   public static Enumeration.Value RUNNING() {
      return ExecutorState$.MODULE$.RUNNING();
   }

   public static Enumeration.Value LAUNCHING() {
      return ExecutorState$.MODULE$.LAUNCHING();
   }

   public static Enumeration.Value withName(String var0) {
      return ExecutorState$.MODULE$.withName(var0);
   }

   public static Enumeration.Value apply(int var0) {
      return ExecutorState$.MODULE$.apply(var0);
   }

   public static int maxId() {
      return ExecutorState$.MODULE$.maxId();
   }

   public static Enumeration.ValueSet values() {
      return ExecutorState$.MODULE$.values();
   }

   public static String toString() {
      return ExecutorState$.MODULE$.toString();
   }
}
