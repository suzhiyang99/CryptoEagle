package org.apache.spark;

import scala.collection.Seq;
import scala.collection.immutable.Map;
import scala.reflect.ScalaSignature;

@ScalaSignature(
   bytes = "\u0006\u0001\u00154\u0001\"\u0001\u0002\u0011\u0002\u0007\u0005!\u0001\u0003\u0002\u0019\u000bb,7-\u001e;pe\u0006cGn\\2bi&|gn\u00117jK:$(BA\u0002\u0005\u0003\u0015\u0019\b/\u0019:l\u0015\t)a!\u0001\u0004ba\u0006\u001c\u0007.\u001a\u0006\u0002\u000f\u0005\u0019qN]4\u0014\u0005\u0001I\u0001C\u0001\u0006\u000e\u001b\u0005Y!\"\u0001\u0007\u0002\u000bM\u001c\u0017\r\\1\n\u00059Y!AB!osJ+g\rC\u0003\u0011\u0001\u0011\u0005!#\u0001\u0004%S:LG\u000fJ\u0002\u0001)\u0005\u0019\u0002C\u0001\u0006\u0015\u0013\t)2B\u0001\u0003V]&$\bBB\f\u0001\r\u0003\u0011\u0001$\u0001\bhKR,\u00050Z2vi>\u0014\u0018\nZ:\u0015\u0003e\u00012A\u0007\u0012&\u001d\tY\u0002E\u0004\u0002\u001d?5\tQD\u0003\u0002\u001f#\u00051AH]8pizJ\u0011\u0001D\u0005\u0003C-\tq\u0001]1dW\u0006<W-\u0003\u0002$I\t\u00191+Z9\u000b\u0005\u0005Z\u0001C\u0001\u0014*\u001d\tQq%\u0003\u0002)\u0017\u00051\u0001K]3eK\u001aL!AK\u0016\u0003\rM#(/\u001b8h\u0015\tA3\u0002\u0003\u0004.\u0001\u0019\u0005!AL\u0001\u0016e\u0016\fX/Z:u)>$\u0018\r\\#yK\u000e,Ho\u001c:t)\u0011y#gN\u001d\u0011\u0005)\u0001\u0014BA\u0019\f\u0005\u001d\u0011un\u001c7fC:DQa\r\u0017A\u0002Q\nAB\\;n\u000bb,7-\u001e;peN\u0004\"AC\u001b\n\u0005YZ!aA%oi\")\u0001\b\fa\u0001i\u0005\u0011Bn\\2bY&$\u00180Q<be\u0016$\u0016m]6t\u0011\u0015QD\u00061\u0001<\u0003QAwn\u001d;U_2{7-\u00197UCN\\7i\\;oiB!a\u0005P\u00135\u0013\ti4FA\u0002NCBDQa\u0010\u0001\u0007\u0002\u0001\u000b\u0001C]3rk\u0016\u001cH/\u0012=fGV$xN]:\u0015\u0005=\n\u0005\"\u0002\"?\u0001\u0004!\u0014A\u00068v[\u0006#G-\u001b;j_:\fG.\u0012=fGV$xN]:\t\u000b\u0011\u0003a\u0011A#\u0002\u001b-LG\u000e\\#yK\u000e,Ho\u001c:t)\u0011Ib\t\u0013&\t\u000b\u001d\u001b\u0005\u0019A\r\u0002\u0017\u0015DXmY;u_JLEm\u001d\u0005\b\u0013\u000e\u0003\n\u00111\u00010\u0003\u001d\u0011X\r\u001d7bG\u0016DqaS\"\u0011\u0002\u0003\u0007q&A\u0003g_J\u001cW\rC\u0003N\u0001\u0019\u0005a*A\nlS2dW\t_3dkR|'o](o\u0011>\u001cH\u000f\u0006\u00020\u001f\")\u0001\u000b\u0014a\u0001K\u0005!\u0001n\\:u\u0011\u0015\u0011\u0006\u0001\"\u0001T\u00031Y\u0017\u000e\u001c7Fq\u0016\u001cW\u000f^8s)\tyC\u000bC\u0003V#\u0002\u0007Q%\u0001\u0006fq\u0016\u001cW\u000f^8s\u0013\u0012Dqa\u0016\u0001\u0012\u0002\u0013\u0005\u0001,A\flS2dW\t_3dkR|'o\u001d\u0013eK\u001a\fW\u000f\u001c;%eU\t\u0011L\u000b\u000205.\n1\f\u0005\u0002]C6\tQL\u0003\u0002_?\u0006IQO\\2iK\u000e\\W\r\u001a\u0006\u0003A.\t!\"\u00198o_R\fG/[8o\u0013\t\u0011WLA\tv]\u000eDWmY6fIZ\u000b'/[1oG\u0016Dq\u0001\u001a\u0001\u0012\u0002\u0013\u0005\u0001,A\flS2dW\t_3dkR|'o\u001d\u0013eK\u001a\fW\u000f\u001c;%g\u0001"
)
public interface ExecutorAllocationClient {
   Seq<String> getExecutorIds();

   boolean requestTotalExecutors(int var1, int var2, Map<String, Object> var3);

   boolean requestExecutors(int var1);

   Seq<String> killExecutors(Seq<String> var1, boolean var2, boolean var3);

   boolean killExecutorsOnHost(String var1);

   boolean killExecutor(String var1);

   boolean killExecutors$default$2();

   boolean killExecutors$default$3();
}
