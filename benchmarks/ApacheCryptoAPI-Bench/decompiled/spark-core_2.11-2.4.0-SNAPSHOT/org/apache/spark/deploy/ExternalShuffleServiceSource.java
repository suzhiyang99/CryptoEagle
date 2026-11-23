package org.apache.spark.deploy;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.MetricSet;
import javax.annotation.concurrent.ThreadSafe;
import org.apache.spark.metrics.source.Source;
import scala.reflect.ScalaSignature;

@ScalaSignature(
   bytes = "\u0006\u0001A3A!\u0001\u0002\u0005\u0017\taR\t\u001f;fe:\fGn\u00155vM\u001adWmU3sm&\u001cWmU8ve\u000e,'BA\u0002\u0005\u0003\u0019!W\r\u001d7ps*\u0011QAB\u0001\u0006gB\f'o\u001b\u0006\u0003\u000f!\ta!\u00199bG\",'\"A\u0005\u0002\u0007=\u0014xm\u0001\u0001\u0014\u0007\u0001a!\u0003\u0005\u0002\u000e!5\taBC\u0001\u0010\u0003\u0015\u00198-\u00197b\u0013\t\tbB\u0001\u0004B]f\u0014VM\u001a\t\u0003'ai\u0011\u0001\u0006\u0006\u0003+Y\taa]8ve\u000e,'BA\f\u0005\u0003\u001diW\r\u001e:jGNL!!\u0007\u000b\u0003\rM{WO]2f\u0011\u0015Y\u0002\u0001\"\u0001\u001d\u0003\u0019a\u0014N\\5u}Q\tQ\u0004\u0005\u0002\u001f\u00015\t!\u0001C\u0004!\u0001\t\u0007I\u0011I\u0011\u0002\u001d5,GO]5d%\u0016<\u0017n\u001d;ssV\t!\u0005\u0005\u0002$S5\tAE\u0003\u0002\u0018K)\u0011aeJ\u0001\tG>$\u0017\r[1mK*\t\u0001&A\u0002d_6L!A\u000b\u0013\u0003\u001d5+GO]5d%\u0016<\u0017n\u001d;ss\"1A\u0006\u0001Q\u0001\n\t\nq\"\\3ue&\u001c'+Z4jgR\u0014\u0018\u0010\t\u0005\b]\u0001\u0011\r\u0011\"\u00110\u0003)\u0019x.\u001e:dK:\u000bW.Z\u000b\u0002aA\u0011\u0011GN\u0007\u0002e)\u00111\u0007N\u0001\u0005Y\u0006twMC\u00016\u0003\u0011Q\u0017M^1\n\u0005]\u0012$AB*ue&tw\r\u0003\u0004:\u0001\u0001\u0006I\u0001M\u0001\fg>,(oY3OC6,\u0007\u0005C\u0003<\u0001\u0011\u0005A(A\tsK\u001eL7\u000f^3s\u001b\u0016$(/[2TKR$\"!\u0010!\u0011\u00055q\u0014BA \u000f\u0005\u0011)f.\u001b;\t\u000b\u0005S\u0004\u0019\u0001\"\u0002\u00135,GO]5d'\u0016$\bCA\u0012D\u0013\t!EEA\u0005NKR\u0014\u0018nY*fi\"\u0012\u0001A\u0012\t\u0003\u000f:k\u0011\u0001\u0013\u0006\u0003\u0013*\u000b!bY8oGV\u0014(/\u001a8u\u0015\tYE*\u0001\u0006b]:|G/\u0019;j_:T\u0011!T\u0001\u0006U\u00064\u0018\r_\u0005\u0003\u001f\"\u0013!\u0002\u00165sK\u0006$7+\u00194f\u0001"
)
@ThreadSafe
public class ExternalShuffleServiceSource implements Source {
   private final MetricRegistry metricRegistry = new MetricRegistry();
   private final String sourceName = "shuffleService";

   public MetricRegistry metricRegistry() {
      return this.metricRegistry;
   }

   public String sourceName() {
      return this.sourceName;
   }

   public void registerMetricSet(MetricSet metricSet) {
      this.metricRegistry().registerAll(metricSet);
   }
}
