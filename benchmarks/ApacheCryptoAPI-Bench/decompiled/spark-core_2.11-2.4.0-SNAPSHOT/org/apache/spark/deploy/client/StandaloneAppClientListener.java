package org.apache.spark.deploy.client;

import scala.Option;
import scala.reflect.ScalaSignature;

@ScalaSignature(
   bytes = "\u0006\u0001I3\u0001\"\u0001\u0002\u0011\u0002G\u0005a\u0001\u0004\u0002\u001c'R\fg\u000eZ1m_:,\u0017\t\u001d9DY&,g\u000e\u001e'jgR,g.\u001a:\u000b\u0005\r!\u0011AB2mS\u0016tGO\u0003\u0002\u0006\r\u00051A-\u001a9m_fT!a\u0002\u0005\u0002\u000bM\u0004\u0018M]6\u000b\u0005%Q\u0011AB1qC\u000eDWMC\u0001\f\u0003\ry'oZ\n\u0003\u00015\u0001\"AD\t\u000e\u0003=Q\u0011\u0001E\u0001\u0006g\u000e\fG.Y\u0005\u0003%=\u0011a!\u00118z%\u00164\u0007\"\u0002\u000b\u0001\r\u00031\u0012!C2p]:,7\r^3e\u0007\u0001!\"a\u0006\u000e\u0011\u00059A\u0012BA\r\u0010\u0005\u0011)f.\u001b;\t\u000bm\u0019\u0002\u0019\u0001\u000f\u0002\u000b\u0005\u0004\b/\u00133\u0011\u0005u\u0001cB\u0001\b\u001f\u0013\tyr\"\u0001\u0004Qe\u0016$WMZ\u0005\u0003C\t\u0012aa\u0015;sS:<'BA\u0010\u0010\u0011\u0015!\u0003A\"\u0001&\u00031!\u0017n]2p]:,7\r^3e)\u00059\u0002\"B\u0014\u0001\r\u0003A\u0013\u0001\u00023fC\u0012$\"aF\u0015\t\u000b)2\u0003\u0019\u0001\u000f\u0002\rI,\u0017m]8o\u0011\u0015a\u0003A\"\u0001.\u00035)\u00070Z2vi>\u0014\u0018\t\u001a3fIR1qC\f\u00193ieBQaL\u0016A\u0002q\taAZ;mY&#\u0007\"B\u0019,\u0001\u0004a\u0012\u0001C<pe.,'/\u00133\t\u000bMZ\u0003\u0019\u0001\u000f\u0002\u0011!|7\u000f\u001e)peRDQ!N\u0016A\u0002Y\nQaY8sKN\u0004\"AD\u001c\n\u0005az!aA%oi\")!h\u000ba\u0001m\u00051Q.Z7pefDQ\u0001\u0010\u0001\u0007\u0002u\nq\"\u001a=fGV$xN\u001d*f[>4X\r\u001a\u000b\u0006/yz\u0014I\u0012\u0005\u0006_m\u0002\r\u0001\b\u0005\u0006\u0001n\u0002\r\u0001H\u0001\b[\u0016\u001c8/Y4f\u0011\u0015\u00115\b1\u0001D\u0003))\u00070\u001b;Ti\u0006$Xo\u001d\t\u0004\u001d\u00113\u0014BA#\u0010\u0005\u0019y\u0005\u000f^5p]\")qi\u000fa\u0001\u0011\u0006Qqo\u001c:lKJdun\u001d;\u0011\u00059I\u0015B\u0001&\u0010\u0005\u001d\u0011un\u001c7fC:DQ\u0001\u0014\u0001\u0007\u00025\u000bQb^8sW\u0016\u0014(+Z7pm\u0016$G\u0003B\fO\u001fFCQ!M&A\u0002qAQ\u0001U&A\u0002q\tA\u0001[8ti\")\u0001i\u0013a\u00019\u0001"
)
public interface StandaloneAppClientListener {
   void connected(String var1);

   void disconnected();

   void dead(String var1);

   void executorAdded(String var1, String var2, String var3, int var4, int var5);

   void executorRemoved(String var1, String var2, Option<Object> var3, boolean var4);

   void workerRemoved(String var1, String var2, String var3);
}
