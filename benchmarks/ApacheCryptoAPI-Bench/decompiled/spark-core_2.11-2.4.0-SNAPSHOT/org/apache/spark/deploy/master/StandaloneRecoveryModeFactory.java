package org.apache.spark.deploy.master;

import org.apache.spark.SparkConf;
import org.apache.spark.annotation.DeveloperApi;
import org.apache.spark.serializer.Serializer;
import scala.reflect.ScalaSignature;

@DeveloperApi
@ScalaSignature(
   bytes = "\u0006\u0001}2Q!\u0001\u0002\u0002\u00025\u0011Qd\u0015;b]\u0012\fGn\u001c8f%\u0016\u001cwN^3ss6{G-\u001a$bGR|'/\u001f\u0006\u0003\u0007\u0011\ta!\\1ti\u0016\u0014(BA\u0003\u0007\u0003\u0019!W\r\u001d7ps*\u0011q\u0001C\u0001\u0006gB\f'o\u001b\u0006\u0003\u0013)\ta!\u00199bG\",'\"A\u0006\u0002\u0007=\u0014xm\u0001\u0001\u0014\u0005\u0001q\u0001CA\b\u0013\u001b\u0005\u0001\"\"A\t\u0002\u000bM\u001c\u0017\r\\1\n\u0005M\u0001\"AB!osJ+g\r\u0003\u0005\u0016\u0001\t\u0005\t\u0015!\u0003\u0017\u0003\u0011\u0019wN\u001c4\u0011\u0005]AR\"\u0001\u0004\n\u0005e1!!C*qCJ\\7i\u001c8g\u0011!Y\u0002A!A!\u0002\u0013a\u0012AC:fe&\fG.\u001b>feB\u0011QdH\u0007\u0002=)\u00111DB\u0005\u0003Ay\u0011!bU3sS\u0006d\u0017N_3s\u0011\u0015\u0011\u0003\u0001\"\u0001$\u0003\u0019a\u0014N\\5u}Q\u0019AEJ\u0014\u0011\u0005\u0015\u0002Q\"\u0001\u0002\t\u000bU\t\u0003\u0019\u0001\f\t\u000bm\t\u0003\u0019\u0001\u000f\t\u000b%\u0002a\u0011\u0001\u0016\u0002/\r\u0014X-\u0019;f!\u0016\u00148/[:uK:\u001cW-\u00128hS:,G#A\u0016\u0011\u0005\u0015b\u0013BA\u0017\u0003\u0005E\u0001VM]:jgR,gnY3F]\u001eLg.\u001a\u0005\u0006_\u00011\t\u0001M\u0001\u001aGJ,\u0017\r^3MK\u0006$WM]#mK\u000e$\u0018n\u001c8BO\u0016tG\u000f\u0006\u00022iA\u0011QEM\u0005\u0003g\t\u00111\u0003T3bI\u0016\u0014X\t\\3di&|g.Q4f]RDQa\u0001\u0018A\u0002U\u0002\"!\n\u001c\n\u0005]\u0012!a\u0004'fC\u0012,'/\u00127fGR\f'\r\\3)\u0005\u0001I\u0004C\u0001\u001e>\u001b\u0005Y$B\u0001\u001f\u0007\u0003)\tgN\\8uCRLwN\\\u0005\u0003}m\u0012A\u0002R3wK2|\u0007/\u001a:Ba&\u0004"
)
public abstract class StandaloneRecoveryModeFactory {
   public abstract PersistenceEngine createPersistenceEngine();

   public abstract LeaderElectionAgent createLeaderElectionAgent(LeaderElectable var1);

   public StandaloneRecoveryModeFactory(SparkConf conf, Serializer serializer) {
   }
}
