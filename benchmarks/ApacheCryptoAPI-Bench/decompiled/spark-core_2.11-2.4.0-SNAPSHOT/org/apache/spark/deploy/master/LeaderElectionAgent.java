package org.apache.spark.deploy.master;

import org.apache.spark.annotation.DeveloperApi;
import scala.reflect.ScalaSignature;

@DeveloperApi
@ScalaSignature(
   bytes = "\u0006\u0001)2q!\u0001\u0002\u0011\u0002\u0007\u0005QBA\nMK\u0006$WM]#mK\u000e$\u0018n\u001c8BO\u0016tGO\u0003\u0002\u0004\t\u00051Q.Y:uKJT!!\u0002\u0004\u0002\r\u0011,\u0007\u000f\\8z\u0015\t9\u0001\"A\u0003ta\u0006\u00148N\u0003\u0002\n\u0015\u00051\u0011\r]1dQ\u0016T\u0011aC\u0001\u0004_J<7\u0001A\n\u0003\u00019\u0001\"a\u0004\n\u000e\u0003AQ\u0011!E\u0001\u0006g\u000e\fG.Y\u0005\u0003'A\u0011a!\u00118z%\u00164\u0007\"B\u000b\u0001\t\u00031\u0012A\u0002\u0013j]&$H\u0005F\u0001\u0018!\ty\u0001$\u0003\u0002\u001a!\t!QK\\5u\u0011\u001dY\u0002A1A\u0007\u0002q\ta\"\\1ti\u0016\u0014\u0018J\\:uC:\u001cW-F\u0001\u001e!\tqr$D\u0001\u0003\u0013\t\u0001#AA\bMK\u0006$WM]#mK\u000e$\u0018M\u00197f\u0011\u0015\u0011\u0003\u0001\"\u0001\u0017\u0003\u0011\u0019Ho\u001c9)\u0005\u0001!\u0003CA\u0013)\u001b\u00051#BA\u0014\u0007\u0003)\tgN\\8uCRLwN\\\u0005\u0003S\u0019\u0012A\u0002R3wK2|\u0007/\u001a:Ba&\u0004"
)
public interface LeaderElectionAgent {
   LeaderElectable masterInstance();

   void stop();
}
