package org.apache.spark.shuffle;

import org.apache.spark.FetchFailed;
import org.apache.spark.TaskContext;
import org.apache.spark.TaskContext$;
import org.apache.spark.TaskFailedReason;
import org.apache.spark.storage.BlockManagerId;
import org.apache.spark.util.Utils$;
import scala.Option$;
import scala.Serializable;
import scala.reflect.ScalaSignature;

@ScalaSignature(
   bytes = "\u0006\u0001a4Q!\u0001\u0002\u0001\t)\u0011ACR3uG\"4\u0015-\u001b7fI\u0016C8-\u001a9uS>t'BA\u0002\u0005\u0003\u001d\u0019\b.\u001e4gY\u0016T!!\u0002\u0004\u0002\u000bM\u0004\u0018M]6\u000b\u0005\u001dA\u0011AB1qC\u000eDWMC\u0001\n\u0003\ry'oZ\n\u0003\u0001-\u0001\"\u0001D\f\u000f\u00055!bB\u0001\b\u0013\u001b\u0005y!B\u0001\t\u0012\u0003\u0019a$o\\8u}\r\u0001\u0011\"A\n\u0002\u000bM\u001c\u0017\r\\1\n\u0005U1\u0012a\u00029bG.\fw-\u001a\u0006\u0002'%\u0011\u0001$\u0007\u0002\n\u000bb\u001cW\r\u001d;j_:T!!\u0006\f\t\u0011m\u0001!\u0011!Q\u0001\nq\t\u0011BY7BI\u0012\u0014Xm]:\u0011\u0005u\u0001S\"\u0001\u0010\u000b\u0005}!\u0011aB:u_J\fw-Z\u0005\u0003Cy\u0011aB\u00117pG.l\u0015M\\1hKJLE\r\u0003\u0005$\u0001\t\u0005\t\u0015!\u0003%\u0003%\u0019\b.\u001e4gY\u0016LE\r\u0005\u0002&M5\ta#\u0003\u0002(-\t\u0019\u0011J\u001c;\t\u0011%\u0002!\u0011!Q\u0001\n\u0011\nQ!\\1q\u0013\u0012D\u0001b\u000b\u0001\u0003\u0002\u0003\u0006I\u0001J\u0001\te\u0016$WoY3JI\"AQ\u0006\u0001B\u0001B\u0003%a&A\u0004nKN\u001c\u0018mZ3\u0011\u0005=\u0012dBA\u00131\u0013\t\td#\u0001\u0004Qe\u0016$WMZ\u0005\u0003gQ\u0012aa\u0015;sS:<'BA\u0019\u0017\u0011!1\u0004A!A!\u0002\u00139\u0014!B2bkN,\u0007C\u0001\u00079\u0013\tI\u0014DA\u0005UQJ|w/\u00192mK\")1\b\u0001C\u0001y\u00051A(\u001b8jiz\"r!P A\u0003\n\u001bE\t\u0005\u0002?\u00015\t!\u0001C\u0003\u001cu\u0001\u0007A\u0004C\u0003$u\u0001\u0007A\u0005C\u0003*u\u0001\u0007A\u0005C\u0003,u\u0001\u0007A\u0005C\u0003.u\u0001\u0007a\u0006C\u00047uA\u0005\t\u0019A\u001c\t\u000bm\u0002A\u0011\u0001$\u0015\ru:\u0005*\u0013&L\u0011\u0015YR\t1\u0001\u001d\u0011\u0015\u0019S\t1\u0001%\u0011\u0015IS\t1\u0001%\u0011\u0015YS\t1\u0001%\u0011\u00151T\t1\u00018\u0011\u0015i\u0005\u0001\"\u0001O\u0003I!x\u000eV1tW\u001a\u000b\u0017\u000e\\3e%\u0016\f7o\u001c8\u0016\u0003=\u0003\"\u0001U)\u000e\u0003\u0011I!A\u0015\u0003\u0003!Q\u000b7o\u001b$bS2,GMU3bg>tw\u0001\u0003+\u0003\u0003\u0003E\t\u0001B+\u0002)\u0019+Go\u00195GC&dW\rZ#yG\u0016\u0004H/[8o!\tqdK\u0002\u0005\u0002\u0005\u0005\u0005\t\u0012\u0001\u0003X'\r1\u0006l\u0017\t\u0003KeK!A\u0017\f\u0003\r\u0005s\u0017PU3g!\t)C,\u0003\u0002^-\ta1+\u001a:jC2L'0\u00192mK\")1H\u0016C\u0001?R\tQ\u000bC\u0004b-F\u0005I\u0011\u00012\u00027\u0011bWm]:j]&$He\u001a:fCR,'\u000f\n3fM\u0006,H\u000e\u001e\u00137+\u0005\u0019'FA\u001ceW\u0005)\u0007C\u00014l\u001b\u00059'B\u00015j\u0003%)hn\u00195fG.,GM\u0003\u0002k-\u0005Q\u0011M\u001c8pi\u0006$\u0018n\u001c8\n\u00051<'!E;oG\",7m[3e-\u0006\u0014\u0018.\u00198dK\"9aNVA\u0001\n\u0013y\u0017a\u0003:fC\u0012\u0014Vm]8mm\u0016$\u0012\u0001\u001d\t\u0003cZl\u0011A\u001d\u0006\u0003gR\fA\u0001\\1oO*\tQ/\u0001\u0003kCZ\f\u0017BA<s\u0005\u0019y%M[3di\u0002"
)
public class FetchFailedException extends Exception {
   private final BlockManagerId bmAddress;
   private final int shuffleId;
   private final int mapId;
   private final int reduceId;

   public static Throwable $lessinit$greater$default$6() {
      return FetchFailedException$.MODULE$.$lessinit$greater$default$6();
   }

   public TaskFailedReason toTaskFailedReason() {
      return new FetchFailed(this.bmAddress, this.shuffleId, this.mapId, this.reduceId, Utils$.MODULE$.exceptionString(this));
   }

   public FetchFailedException(BlockManagerId bmAddress, int shuffleId, int mapId, int reduceId, String message, Throwable cause) {
      super(message, cause);
      this.bmAddress = bmAddress;
      this.shuffleId = shuffleId;
      this.mapId = mapId;
      this.reduceId = reduceId;
      Option$.MODULE$.apply(TaskContext$.MODULE$.get()).map(new Serializable(this) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final FetchFailedException $outer;

         public final void apply(TaskContext x$1) {
            x$1.setFetchFailed(this.$outer);
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
            }
         }
      });
   }

   public FetchFailedException(BlockManagerId bmAddress, int shuffleId, int mapId, int reduceId, Throwable cause) {
      this(bmAddress, shuffleId, mapId, reduceId, cause.getMessage(), cause);
   }
}
