package org.apache.spark.deploy;

import org.apache.spark.deploy.master.ApplicationInfo;
import org.apache.spark.deploy.master.DriverInfo;
import org.apache.spark.deploy.master.WorkerInfo;
import org.apache.spark.deploy.worker.ExecutorRunner;
import org.json4s.JsonAST;
import scala.reflect.ScalaSignature;

@ScalaSignature(
   bytes = "\u0006\u0001-<a!\u0001\u0002\t\u0002\tQ\u0011\u0001\u0004&t_:\u0004&o\u001c;pG>d'BA\u0002\u0005\u0003\u0019!W\r\u001d7ps*\u0011QAB\u0001\u0006gB\f'o\u001b\u0006\u0003\u000f!\ta!\u00199bG\",'\"A\u0005\u0002\u0007=\u0014x\r\u0005\u0002\f\u00195\t!A\u0002\u0004\u000e\u0005!\u0005!A\u0004\u0002\r\u0015N|g\u000e\u0015:pi>\u001cw\u000e\\\n\u0003\u0019=\u0001\"\u0001E\n\u000e\u0003EQ\u0011AE\u0001\u0006g\u000e\fG.Y\u0005\u0003)E\u0011a!\u00118z%\u00164\u0007\"\u0002\f\r\t\u0003A\u0012A\u0002\u001fj]&$hh\u0001\u0001\u0015\u0003)AQA\u0007\u0007\u0005\u0002m\tqb\u001e:ji\u0016<vN]6fe&sgm\u001c\u000b\u000391\u0002\"!H\u0015\u000f\u0005y1cBA\u0010%\u001d\t\u00013%D\u0001\"\u0015\t\u0011s#\u0001\u0004=e>|GOP\u0005\u0002\u0013%\u0011Q\u0005C\u0001\u0007UN|g\u000eN:\n\u0005\u001dB\u0013a\u0002&t_:\f5\u000b\u0016\u0006\u0003K!I!AK\u0016\u0003\u000f){%M[3di*\u0011q\u0005\u000b\u0005\u0006[e\u0001\rAL\u0001\u0004_\nT\u0007CA\u00183\u001b\u0005\u0001$BA\u0019\u0003\u0003\u0019i\u0017m\u001d;fe&\u00111\u0007\r\u0002\u000b/>\u00148.\u001a:J]\u001a|\u0007\"B\u001b\r\t\u00031\u0014\u0001F<sSR,\u0017\t\u001d9mS\u000e\fG/[8o\u0013:4w\u000e\u0006\u0002\u001do!)Q\u0006\u000ea\u0001qA\u0011q&O\u0005\u0003uA\u0012q\"\u00119qY&\u001c\u0017\r^5p]&sgm\u001c\u0005\u0006y1!\t!P\u0001\u001coJLG/Z!qa2L7-\u0019;j_:$Um]2sSB$\u0018n\u001c8\u0015\u0005qq\u0004\"B\u0017<\u0001\u0004y\u0004CA\u0006A\u0013\t\t%A\u0001\fBaBd\u0017nY1uS>tG)Z:de&\u0004H/[8o\u0011\u0015\u0019E\u0002\"\u0001E\u0003M9(/\u001b;f\u000bb,7-\u001e;peJ+hN\\3s)\taR\tC\u0003.\u0005\u0002\u0007a\t\u0005\u0002H\u00156\t\u0001J\u0003\u0002J\u0005\u00051qo\u001c:lKJL!a\u0013%\u0003\u001d\u0015CXmY;u_J\u0014VO\u001c8fe\")Q\n\u0004C\u0001\u001d\u0006yqO]5uK\u0012\u0013\u0018N^3s\u0013:4w\u000e\u0006\u0002\u001d\u001f\")Q\u0006\u0014a\u0001!B\u0011q&U\u0005\u0003%B\u0012!\u0002\u0012:jm\u0016\u0014\u0018J\u001c4p\u0011\u0015!F\u0002\"\u0001V\u0003A9(/\u001b;f\u001b\u0006\u001cH/\u001a:Ti\u0006$X\r\u0006\u0002\u001d-\")Qf\u0015a\u0001/B\u0011\u0001,\u0019\b\u00033~s!A\u00170\u000f\u0005mkfBA\u0010]\u0013\t9\u0001\"\u0003\u0002\u0006\r%\u00111\u0001B\u0005\u0003A\n\ta\u0002R3qY>LX*Z:tC\u001e,7/\u0003\u0002cG\n\u0019R*Y:uKJ\u001cF/\u0019;f%\u0016\u001c\bo\u001c8tK*\u0011\u0001M\u0001\u0005\u0006K2!\tAZ\u0001\u0011oJLG/Z,pe.,'o\u0015;bi\u0016$\"\u0001H4\t\u000b5\"\u0007\u0019\u00015\u0011\u0005aK\u0017B\u00016d\u0005M9vN]6feN#\u0018\r^3SKN\u0004xN\\:f\u0001"
)
public final class JsonProtocol {
   public static JsonAST.JObject writeWorkerState(DeployMessages.WorkerStateResponse var0) {
      return JsonProtocol$.MODULE$.writeWorkerState(var0);
   }

   public static JsonAST.JObject writeMasterState(DeployMessages.MasterStateResponse var0) {
      return JsonProtocol$.MODULE$.writeMasterState(var0);
   }

   public static JsonAST.JObject writeDriverInfo(DriverInfo var0) {
      return JsonProtocol$.MODULE$.writeDriverInfo(var0);
   }

   public static JsonAST.JObject writeExecutorRunner(ExecutorRunner var0) {
      return JsonProtocol$.MODULE$.writeExecutorRunner(var0);
   }

   public static JsonAST.JObject writeApplicationDescription(ApplicationDescription var0) {
      return JsonProtocol$.MODULE$.writeApplicationDescription(var0);
   }

   public static JsonAST.JObject writeApplicationInfo(ApplicationInfo var0) {
      return JsonProtocol$.MODULE$.writeApplicationInfo(var0);
   }

   public static JsonAST.JObject writeWorkerInfo(WorkerInfo var0) {
      return JsonProtocol$.MODULE$.writeWorkerInfo(var0);
   }
}
