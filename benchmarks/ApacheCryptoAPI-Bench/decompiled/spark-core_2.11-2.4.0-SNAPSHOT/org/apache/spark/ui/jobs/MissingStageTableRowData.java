package org.apache.spark.ui.jobs;

import java.util.Date;
import org.apache.spark.status.api.v1.StageData;
import scala.None$;
import scala.reflect.ScalaSignature;

@ScalaSignature(
   bytes = "\u0006\u0001M2Q!\u0001\u0002\u0001\t1\u0011\u0001$T5tg&twm\u0015;bO\u0016$\u0016M\u00197f%><H)\u0019;b\u0015\t\u0019A!\u0001\u0003k_\n\u001c(BA\u0003\u0007\u0003\t)\u0018N\u0003\u0002\b\u0011\u0005)1\u000f]1sW*\u0011\u0011BC\u0001\u0007CB\f7\r[3\u000b\u0003-\t1a\u001c:h'\t\u0001Q\u0002\u0005\u0002\u000f\u001f5\t!!\u0003\u0002\u0011\u0005\t\t2\u000b^1hKR\u000b'\r\\3S_^$\u0015\r^1\t\u0013I\u0001!\u0011!Q\u0001\nQq\u0012!C:uC\u001e,\u0017J\u001c4p\u0007\u0001\u0001\"!\u0006\u000f\u000e\u0003YQ!a\u0006\r\u0002\u0005Y\f$BA\r\u001b\u0003\r\t\u0007/\u001b\u0006\u00037\u0019\taa\u001d;biV\u001c\u0018BA\u000f\u0017\u0005%\u0019F/Y4f\t\u0006$\u0018-\u0003\u0002 \u001f\u0005)1\u000f^1hK\"I\u0011\u0005\u0001B\u0001B\u0003%!\u0005K\u0001\bgR\fw-Z%e!\t\u0019c%D\u0001%\u0015\u0005)\u0013!B:dC2\f\u0017BA\u0014%\u0005\rIe\u000e^\u0005\u0003C=A\u0011B\u000b\u0001\u0003\u0002\u0003\u0006IAI\u0016\u0002\u0013\u0005$H/Z7qi&#\u0017B\u0001\u0016\u0010\u0011\u0015i\u0003\u0001\"\u0001/\u0003\u0019a\u0014N\\5u}Q!q\u0006M\u00193!\tq\u0001\u0001C\u0003\u0013Y\u0001\u0007A\u0003C\u0003\"Y\u0001\u0007!\u0005C\u0003+Y\u0001\u0007!\u0005"
)
public class MissingStageTableRowData extends StageTableRowData {
   public MissingStageTableRowData(StageData stageInfo, int stageId, int attemptId) {
      super(stageInfo, None$.MODULE$, stageId, attemptId, "", None$.MODULE$, new Date(0L), "", -1L, "", 0L, "", 0L, "", 0L, "", 0L, "");
   }
}
