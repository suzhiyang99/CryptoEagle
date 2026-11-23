package org.apache.spark.status;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.apache.spark.status.api.v1.ExecutorStageSummary;
import org.apache.spark.util.kvstore.KVIndex;
import scala.Array$;
import scala.Predef$;
import scala.reflect.ClassTag$;
import scala.reflect.ScalaSignature;
import scala.runtime.BoxesRunTime;

@ScalaSignature(
   bytes = "\u0006\u000114Q!\u0001\u0002\u0001\t)\u00111$\u0012=fGV$xN]*uC\u001e,7+^7nCJLxK]1qa\u0016\u0014(BA\u0002\u0005\u0003\u0019\u0019H/\u0019;vg*\u0011QAB\u0001\u0006gB\f'o\u001b\u0006\u0003\u000f!\ta!\u00199bG\",'\"A\u0005\u0002\u0007=\u0014xm\u0005\u0002\u0001\u0017A\u0011AbD\u0007\u0002\u001b)\ta\"A\u0003tG\u0006d\u0017-\u0003\u0002\u0011\u001b\t1\u0011I\\=SK\u001aD\u0001B\u0005\u0001\u0003\u0006\u0004%\t\u0001F\u0001\bgR\fw-Z%e\u0007\u0001)\u0012!\u0006\t\u0003\u0019YI!aF\u0007\u0003\u0007%sG\u000f\u0003\u0005\u001a\u0001\t\u0005\t\u0015!\u0003\u0016\u0003!\u0019H/Y4f\u0013\u0012\u0004\u0003\u0002C\u000e\u0001\u0005\u000b\u0007I\u0011\u0001\u000b\u0002\u001dM$\u0018mZ3BiR,W\u000e\u001d;JI\"AQ\u0004\u0001B\u0001B\u0003%Q#A\bti\u0006<W-\u0011;uK6\u0004H/\u00133!\u0011!y\u0002A!b\u0001\n\u0003\u0001\u0013AC3yK\u000e,Ho\u001c:JIV\t\u0011\u0005\u0005\u0002#K9\u0011AbI\u0005\u0003I5\ta\u0001\u0015:fI\u00164\u0017B\u0001\u0014(\u0005\u0019\u0019FO]5oO*\u0011A%\u0004\u0005\tS\u0001\u0011\t\u0011)A\u0005C\u0005YQ\r_3dkR|'/\u00133!\u0011!Y\u0003A!b\u0001\n\u0003a\u0013\u0001B5oM>,\u0012!\f\t\u0003]Mj\u0011a\f\u0006\u0003aE\n!A^\u0019\u000b\u0005I\u0012\u0011aA1qS&\u0011Ag\f\u0002\u0015\u000bb,7-\u001e;peN#\u0018mZ3Tk6l\u0017M]=\t\u0011Y\u0002!\u0011!Q\u0001\n5\nQ!\u001b8g_\u0002BQ\u0001\u000f\u0001\u0005\u0002e\na\u0001P5oSRtD#\u0002\u001e={yz\u0004CA\u001e\u0001\u001b\u0005\u0011\u0001\"\u0002\n8\u0001\u0004)\u0002\"B\u000e8\u0001\u0004)\u0002\"B\u00108\u0001\u0004\t\u0003\"B\u00168\u0001\u0004i\u0003bB!\u0001\u0005\u0004%IAQ\u0001\u0004?&$W#A\"\u0011\u00071!e)\u0003\u0002F\u001b\t)\u0011I\u001d:bsB\u0011AbR\u0005\u0003\u00116\u00111!\u00118z\u0011\u0019Q\u0005\u0001)A\u0005\u0007\u0006!q,\u001b3!Q\tIE\n\u0005\u0002N%6\taJ\u0003\u0002P!\u000691N^:u_J,'BA)\u0005\u0003\u0011)H/\u001b7\n\u0005Ms%aB&W\u0013:$W\r\u001f\u0015\u0003\u0013V\u0003\"AV0\u000e\u0003]S!\u0001W-\u0002\u0015\u0005tgn\u001c;bi&|gN\u0003\u0002[7\u00069!.Y2lg>t'B\u0001/^\u0003%1\u0017m\u001d;feblGNC\u0001_\u0003\r\u0019w.\\\u0005\u0003A^\u0013!BS:p]&;gn\u001c:f\u0011\u0015\u0011\u0007\u0001\"\u0003d\u0003\u0015\u0019H/Y4f+\u0005!\u0007c\u0001\u0007E+!\"\u0011\r\u00144h\u0003\u00151\u0018\r\\;fC\u0005\u0011\u0007FA1V\u0011\u0015Q\u0007\u0001\"\u0001C\u0003\tIG\r\u000b\u0002j+\u0002"
)
public class ExecutorStageSummaryWrapper {
   private final int stageId;
   private final int stageAttemptId;
   private final String executorId;
   private final ExecutorStageSummary info;
   @JsonIgnore
   @KVIndex
   private final Object[] _id;

   public int stageId() {
      return this.stageId;
   }

   public int stageAttemptId() {
      return this.stageAttemptId;
   }

   public String executorId() {
      return this.executorId;
   }

   public ExecutorStageSummary info() {
      return this.info;
   }

   private Object[] _id() {
      return this._id;
   }

   @JsonIgnore
   @KVIndex("stage")
   private int[] stage() {
      return new int[]{this.stageId(), this.stageAttemptId()};
   }

   @JsonIgnore
   public Object[] id() {
      return this._id();
   }

   public ExecutorStageSummaryWrapper(int stageId, int stageAttemptId, String executorId, ExecutorStageSummary info) {
      this.stageId = stageId;
      this.stageAttemptId = stageAttemptId;
      this.executorId = executorId;
      this.info = info;
      this._id = Array$.MODULE$.apply(Predef$.MODULE$.genericWrapArray(new Object[]{BoxesRunTime.boxToInteger(stageId), BoxesRunTime.boxToInteger(stageAttemptId), executorId}), ClassTag$.MODULE$.Any());
   }
}
