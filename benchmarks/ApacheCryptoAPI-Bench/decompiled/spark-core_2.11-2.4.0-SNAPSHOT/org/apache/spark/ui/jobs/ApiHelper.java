package org.apache.spark.ui.jobs;

import org.apache.spark.status.AppStatusStore;
import org.apache.spark.status.api.v1.JobData;
import org.apache.spark.status.api.v1.ShuffleReadMetrics;
import org.apache.spark.status.api.v1.StageData;
import scala.Option;
import scala.Tuple2;
import scala.reflect.ScalaSignature;

@ScalaSignature(
   bytes = "\u0006\u0001\u0005\u0005q!B\u0001\u0003\u0011\u0013i\u0011!C!qS\"+G\u000e]3s\u0015\t\u0019A!\u0001\u0003k_\n\u001c(BA\u0003\u0007\u0003\t)\u0018N\u0003\u0002\b\u0011\u0005)1\u000f]1sW*\u0011\u0011BC\u0001\u0007CB\f7\r[3\u000b\u0003-\t1a\u001c:h\u0007\u0001\u0001\"AD\b\u000e\u0003\t1Q\u0001\u0005\u0002\t\nE\u0011\u0011\"\u00119j\u0011\u0016d\u0007/\u001a:\u0014\u0005=\u0011\u0002CA\n\u0017\u001b\u0005!\"\"A\u000b\u0002\u000bM\u001c\u0017\r\\1\n\u0005]!\"AB!osJ+g\rC\u0003\u001a\u001f\u0011\u0005!$\u0001\u0004=S:LGO\u0010\u000b\u0002\u001b!9Ad\u0004b\u0001\n\u0013i\u0012aD\"P\u0019Vkej\u0018+P?&sE)\u0012-\u0016\u0003y\u0001Ba\b\u0013'M5\t\u0001E\u0003\u0002\"E\u0005I\u0011.\\7vi\u0006\u0014G.\u001a\u0006\u0003GQ\t!bY8mY\u0016\u001cG/[8o\u0013\t)\u0003EA\u0002NCB\u0004\"a\n\u0017\u000e\u0003!R!!\u000b\u0016\u0002\t1\fgn\u001a\u0006\u0002W\u0005!!.\u0019<b\u0013\ti\u0003F\u0001\u0004TiJLgn\u001a\u0005\u0007_=\u0001\u000b\u0011\u0002\u0010\u0002!\r{E*V'O?R{u,\u0013(E\u000bb\u0003\u0003\"B\u0019\u0010\t\u0003\u0011\u0014a\u00045bg\u0006\u001b7-^7vY\u0006$xN]:\u0015\u0005M2\u0004CA\n5\u0013\t)DCA\u0004C_>dW-\u00198\t\u000b]\u0002\u0004\u0019\u0001\u001d\u0002\u0013M$\u0018mZ3ECR\f\u0007CA\u001dA\u001b\u0005Q$BA\u001e=\u0003\t1\u0018G\u0003\u0002>}\u0005\u0019\u0011\r]5\u000b\u0005}2\u0011AB:uCR,8/\u0003\u0002Bu\tI1\u000b^1hK\u0012\u000bG/\u0019\u0005\u0006\u0007>!\t\u0001R\u0001\tQ\u0006\u001c\u0018J\u001c9viR\u00111'\u0012\u0005\u0006o\t\u0003\r\u0001\u000f\u0005\u0006\u000f>!\t\u0001S\u0001\nQ\u0006\u001cx*\u001e;qkR$\"aM%\t\u000b]2\u0005\u0019\u0001\u001d\t\u000b-{A\u0011\u0001'\u0002\u001d!\f7o\u00155vM\u001adWMU3bIR\u00111'\u0014\u0005\u0006o)\u0003\r\u0001\u000f\u0005\u0006\u001f>!\t\u0001U\u0001\u0010Q\u0006\u001c8\u000b[;gM2,wK]5uKR\u00111'\u0015\u0005\u0006o9\u0003\r\u0001\u000f\u0005\u0006'>!\t\u0001V\u0001\u0010Q\u0006\u001c()\u001f;fgN\u0003\u0018\u000e\u001c7fIR\u00111'\u0016\u0005\u0006oI\u0003\r\u0001\u000f\u0005\u0006/>!\t\u0001W\u0001\u000fi>$\u0018\r\u001c\"zi\u0016\u001c(+Z1e)\tIF\f\u0005\u0002\u00145&\u00111\f\u0006\u0002\u0005\u0019>tw\rC\u0003^-\u0002\u0007a,A\u0004nKR\u0014\u0018nY:\u0011\u0005ez\u0016B\u00011;\u0005I\u0019\u0006.\u001e4gY\u0016\u0014V-\u00193NKR\u0014\u0018nY:\t\u000b\t|A\u0011A2\u0002\u0013%tG-\u001a=OC6,GC\u00013n!\r\u0019RmZ\u0005\u0003MR\u0011aa\u00149uS>t\u0007C\u00015l\u001d\t\u0019\u0012.\u0003\u0002k)\u00051\u0001K]3eK\u001aL!!\f7\u000b\u0005)$\u0002\"\u00028b\u0001\u00049\u0017AC:peR\u001cu\u000e\\;n]\")\u0001o\u0004C\u0001c\u0006YB.Y:u'R\fw-\u001a(b[\u0016\fe\u000e\u001a#fg\u000e\u0014\u0018\u000e\u001d;j_:$2A];|!\u0011\u00192oZ4\n\u0005Q$\"A\u0002+va2,'\u0007C\u0003w_\u0002\u0007q/A\u0003ti>\u0014X\r\u0005\u0002ys6\ta(\u0003\u0002{}\tq\u0011\t\u001d9Ti\u0006$Xo]*u_J,\u0007\"\u0002?p\u0001\u0004i\u0018a\u00016pEB\u0011\u0011H`\u0005\u0003\u007fj\u0012qAS8c\t\u0006$\u0018\r"
)
public final class ApiHelper {
   public static Tuple2<String, String> lastStageNameAndDescription(AppStatusStore var0, JobData var1) {
      return ApiHelper$.MODULE$.lastStageNameAndDescription(var0, var1);
   }

   public static Option<String> indexName(String var0) {
      return ApiHelper$.MODULE$.indexName(var0);
   }

   public static long totalBytesRead(ShuffleReadMetrics var0) {
      return ApiHelper$.MODULE$.totalBytesRead(var0);
   }

   public static boolean hasBytesSpilled(StageData var0) {
      return ApiHelper$.MODULE$.hasBytesSpilled(var0);
   }

   public static boolean hasShuffleWrite(StageData var0) {
      return ApiHelper$.MODULE$.hasShuffleWrite(var0);
   }

   public static boolean hasShuffleRead(StageData var0) {
      return ApiHelper$.MODULE$.hasShuffleRead(var0);
   }

   public static boolean hasOutput(StageData var0) {
      return ApiHelper$.MODULE$.hasOutput(var0);
   }

   public static boolean hasInput(StageData var0) {
      return ApiHelper$.MODULE$.hasInput(var0);
   }

   public static boolean hasAccumulators(StageData var0) {
      return ApiHelper$.MODULE$.hasAccumulators(var0);
   }
}
