package org.apache.spark.rdd.util;

import org.apache.spark.SparkContext;
import org.apache.spark.rdd.RDD;
import org.apache.spark.storage.StorageLevel;
import org.apache.spark.storage.StorageLevel$;
import org.apache.spark.util.PeriodicCheckpointer;
import scala.Option$;
import scala.Serializable;
import scala.collection.Iterable;
import scala.reflect.ScalaSignature;

@ScalaSignature(
   bytes = "\u0006\u0001!4Q!\u0001\u0002\u0001\r1\u0011q\u0003U3sS>$\u0017n\u0019*E\t\u000eCWmY6q_&tG/\u001a:\u000b\u0005\r!\u0011\u0001B;uS2T!!\u0002\u0004\u0002\u0007I$GM\u0003\u0002\b\u0011\u0005)1\u000f]1sW*\u0011\u0011BC\u0001\u0007CB\f7\r[3\u000b\u0003-\t1a\u001c:h+\ti\u0011d\u0005\u0002\u0001\u001dA\u0019q\"E\n\u000e\u0003AQ!a\u0001\u0004\n\u0005I\u0001\"\u0001\u0006)fe&|G-[2DQ\u0016\u001c7\u000e]8j]R,'\u000fE\u0002\u0015+]i\u0011\u0001B\u0005\u0003-\u0011\u00111A\u0015#E!\tA\u0012\u0004\u0004\u0001\u0005\u000bi\u0001!\u0019\u0001\u000f\u0003\u0003Q\u001b\u0001!\u0005\u0002\u001eGA\u0011a$I\u0007\u0002?)\t\u0001%A\u0003tG\u0006d\u0017-\u0003\u0002#?\t9aj\u001c;iS:<\u0007C\u0001\u0010%\u0013\t)sDA\u0002B]fD\u0011b\n\u0001\u0003\u0002\u0003\u0006I\u0001K\u0016\u0002%\rDWmY6q_&tG/\u00138uKJ4\u0018\r\u001c\t\u0003=%J!AK\u0010\u0003\u0007%sG/\u0003\u0002(#!IQ\u0006\u0001B\u0001B\u0003%aFM\u0001\u0003g\u000e\u0004\"a\f\u0019\u000e\u0003\u0019I!!\r\u0004\u0003\u0019M\u0003\u0018M]6D_:$X\r\u001f;\n\u00055\n\u0002\"\u0002\u001b\u0001\t\u0003)\u0014A\u0002\u001fj]&$h\bF\u00027qe\u00022a\u000e\u0001\u0018\u001b\u0005\u0011\u0001\"B\u00144\u0001\u0004A\u0003\"B\u00174\u0001\u0004q\u0003\"B\u001e\u0001\t#b\u0014AC2iK\u000e\\\u0007o\\5oiR\u0011Q\b\u0011\t\u0003=yJ!aP\u0010\u0003\tUs\u0017\u000e\u001e\u0005\u0006\u0003j\u0002\raE\u0001\u0005I\u0006$\u0018\rC\u0003D\u0001\u0011EC)\u0001\bjg\u000eCWmY6q_&tG/\u001a3\u0015\u0005\u0015C\u0005C\u0001\u0010G\u0013\t9uDA\u0004C_>dW-\u00198\t\u000b\u0005\u0013\u0005\u0019A\n\t\u000b)\u0003A\u0011K&\u0002\u000fA,'o]5tiR\u0011Q\b\u0014\u0005\u0006\u0003&\u0003\ra\u0005\u0005\u0006\u001d\u0002!\tfT\u0001\nk:\u0004XM]:jgR$\"!\u0010)\t\u000b\u0005k\u0005\u0019A\n\t\u000bI\u0003A\u0011K*\u0002%\u001d,Go\u00115fG.\u0004x.\u001b8u\r&dWm\u001d\u000b\u0003)\u001e\u00042!V/a\u001d\t16L\u0004\u0002X56\t\u0001L\u0003\u0002Z7\u00051AH]8pizJ\u0011\u0001I\u0005\u00039~\tq\u0001]1dW\u0006<W-\u0003\u0002_?\nA\u0011\n^3sC\ndWM\u0003\u0002]?A\u0011\u0011\r\u001a\b\u0003=\tL!aY\u0010\u0002\rA\u0013X\rZ3g\u0013\t)gM\u0001\u0004TiJLgn\u001a\u0006\u0003G~AQ!Q)A\u0002M\u0001"
)
public class PeriodicRDDCheckpointer<T> extends PeriodicCheckpointer<RDD<T>> {
   public void checkpoint(RDD<T> data) {
      data.checkpoint();
   }

   public boolean isCheckpointed(RDD<T> data) {
      return data.isCheckpointed();
   }

   public void persist(RDD<T> data) {
      StorageLevel var10000 = data.getStorageLevel();
      StorageLevel var2 = StorageLevel$.MODULE$.NONE();
      if (var10000 == null) {
         if (var2 != null) {
            return;
         }
      } else if (!var10000.equals(var2)) {
         return;
      }

      data.persist();
   }

   public void unpersist(RDD<T> data) {
      data.unpersist(false);
   }

   public Iterable<String> getCheckpointFiles(RDD<T> data) {
      return Option$.MODULE$.<String>option2Iterable(data.getCheckpointFile().map(new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final String apply(String x) {
            return x;
         }
      }));
   }

   public PeriodicRDDCheckpointer(int checkpointInterval, SparkContext sc) {
      super(checkpointInterval, sc);
   }
}
