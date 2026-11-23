package org.apache.spark.status;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.apache.spark.status.api.v1.JobData;
import org.apache.spark.util.kvstore.KVIndex;
import scala.collection.immutable.Set;
import scala.reflect.ScalaSignature;

@ScalaSignature(
   bytes = "\u0006\u000193Q!\u0001\u0002\u0001\t)\u0011aBS8c\t\u0006$\u0018m\u0016:baB,'O\u0003\u0002\u0004\t\u000511\u000f^1ukNT!!\u0002\u0004\u0002\u000bM\u0004\u0018M]6\u000b\u0005\u001dA\u0011AB1qC\u000eDWMC\u0001\n\u0003\ry'oZ\n\u0003\u0001-\u0001\"\u0001D\b\u000e\u00035Q\u0011AD\u0001\u0006g\u000e\fG.Y\u0005\u0003!5\u0011a!\u00118z%\u00164\u0007\u0002\u0003\n\u0001\u0005\u000b\u0007I\u0011\u0001\u000b\u0002\t%tgm\\\u0002\u0001+\u0005)\u0002C\u0001\f\u001c\u001b\u00059\"B\u0001\r\u001a\u0003\t1\u0018G\u0003\u0002\u001b\u0005\u0005\u0019\u0011\r]5\n\u0005q9\"a\u0002&pE\u0012\u000bG/\u0019\u0005\t=\u0001\u0011\t\u0011)A\u0005+\u0005)\u0011N\u001c4pA!A\u0001\u0005\u0001BC\u0002\u0013\u0005\u0011%A\u0007tW&\u0004\b/\u001a3Ti\u0006<Wm]\u000b\u0002EA\u00191EJ\u0015\u000f\u00051!\u0013BA\u0013\u000e\u0003\u0019\u0001&/\u001a3fM&\u0011q\u0005\u000b\u0002\u0004'\u0016$(BA\u0013\u000e!\ta!&\u0003\u0002,\u001b\t\u0019\u0011J\u001c;\t\u00115\u0002!\u0011!Q\u0001\n\t\nab]6jaB,Gm\u0015;bO\u0016\u001c\b\u0005C\u00030\u0001\u0011\u0005\u0001'\u0001\u0004=S:LGO\u0010\u000b\u0004cM\"\u0004C\u0001\u001a\u0001\u001b\u0005\u0011\u0001\"\u0002\n/\u0001\u0004)\u0002\"\u0002\u0011/\u0001\u0004\u0011\u0003\"\u0002\u001c\u0001\t\u00139\u0014AA5e+\u0005I\u0003FA\u001b:!\tQt(D\u0001<\u0015\taT(A\u0004lmN$xN]3\u000b\u0005y\"\u0011\u0001B;uS2L!\u0001Q\u001e\u0003\u000f-3\u0016J\u001c3fq\"\u0012QG\u0011\t\u0003\u00072k\u0011\u0001\u0012\u0006\u0003\u000b\u001a\u000b!\"\u00198o_R\fG/[8o\u0015\t9\u0005*A\u0004kC\u000e\\7o\u001c8\u000b\u0005%S\u0015!\u00034bgR,'\u000f_7m\u0015\u0005Y\u0015aA2p[&\u0011Q\n\u0012\u0002\u000b\u0015N|g.S4o_J,\u0007"
)
public class JobDataWrapper {
   private final JobData info;
   private final Set<Object> skippedStages;

   public JobData info() {
      return this.info;
   }

   public Set<Object> skippedStages() {
      return this.skippedStages;
   }

   @JsonIgnore
   @KVIndex
   private int id() {
      return this.info().jobId();
   }

   public JobDataWrapper(JobData info, Set<Object> skippedStages) {
      this.info = info;
      this.skippedStages = skippedStages;
   }
}
