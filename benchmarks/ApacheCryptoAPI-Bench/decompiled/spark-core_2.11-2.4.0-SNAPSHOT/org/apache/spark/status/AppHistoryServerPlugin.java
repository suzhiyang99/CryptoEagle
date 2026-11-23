package org.apache.spark.status;

import org.apache.spark.SparkConf;
import org.apache.spark.scheduler.SparkListener;
import org.apache.spark.ui.SparkUI;
import scala.collection.Seq;
import scala.reflect.ScalaSignature;

@ScalaSignature(
   bytes = "\u0006\u0001\u00013\u0001\"\u0001\u0002\u0011\u0002G\u0005AA\u0003\u0002\u0017\u0003B\u0004\b*[:u_JL8+\u001a:wKJ\u0004F.^4j]*\u00111\u0001B\u0001\u0007gR\fG/^:\u000b\u0005\u00151\u0011!B:qCJ\\'BA\u0004\t\u0003\u0019\t\u0007/Y2iK*\t\u0011\"A\u0002pe\u001e\u001c\"\u0001A\u0006\u0011\u00051yQ\"A\u0007\u000b\u00039\tQa]2bY\u0006L!\u0001E\u0007\u0003\r\u0005s\u0017PU3g\u0011\u0015\u0011\u0002A\"\u0001\u0015\u0003=\u0019'/Z1uK2K7\u000f^3oKJ\u001c8\u0001\u0001\u000b\u0004+\u001dj\u0003c\u0001\f\u001fC9\u0011q\u0003\b\b\u00031mi\u0011!\u0007\u0006\u00035M\ta\u0001\u0010:p_Rt\u0014\"\u0001\b\n\u0005ui\u0011a\u00029bG.\fw-Z\u0005\u0003?\u0001\u00121aU3r\u0015\tiR\u0002\u0005\u0002#K5\t1E\u0003\u0002%\t\u0005I1o\u00195fIVdWM]\u0005\u0003M\r\u0012Qb\u00159be.d\u0015n\u001d;f]\u0016\u0014\b\"\u0002\u0015\u0012\u0001\u0004I\u0013\u0001B2p]\u001a\u0004\"AK\u0016\u000e\u0003\u0011I!\u0001\f\u0003\u0003\u0013M\u0003\u0018M]6D_:4\u0007\"\u0002\u0018\u0012\u0001\u0004y\u0013!B:u_J,\u0007C\u0001\u00192\u001b\u0005\u0011\u0011B\u0001\u001a\u0003\u0005Q)E.Z7f]R$&/Y2lS:<7\u000b^8sK\")A\u0007\u0001D\u0001k\u000591/\u001a;vaVKEC\u0001\u001c:!\taq'\u0003\u00029\u001b\t!QK\\5u\u0011\u0015Q4\u00071\u0001<\u0003\t)\u0018\u000e\u0005\u0002=}5\tQH\u0003\u0002;\t%\u0011q(\u0010\u0002\b'B\f'o[+J\u0001"
)
public interface AppHistoryServerPlugin {
   Seq<SparkListener> createListeners(SparkConf var1, ElementTrackingStore var2);

   void setupUI(SparkUI var1);
}
