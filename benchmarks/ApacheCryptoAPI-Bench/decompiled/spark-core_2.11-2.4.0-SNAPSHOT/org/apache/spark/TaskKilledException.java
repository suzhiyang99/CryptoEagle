package org.apache.spark;

import org.apache.spark.annotation.DeveloperApi;
import scala.reflect.ScalaSignature;

@DeveloperApi
@ScalaSignature(
   bytes = "\u0006\u0001Q2A!\u0001\u0002\u0001\u0013\t\u0019B+Y:l\u0017&dG.\u001a3Fq\u000e,\u0007\u000f^5p]*\u00111\u0001B\u0001\u0006gB\f'o\u001b\u0006\u0003\u000b\u0019\ta!\u00199bG\",'\"A\u0004\u0002\u0007=\u0014xm\u0001\u0001\u0014\u0005\u0001Q\u0001CA\u0006\u0016\u001d\ta!C\u0004\u0002\u000e!5\taB\u0003\u0002\u0010\u0011\u00051AH]8pizJ\u0011!E\u0001\u0006g\u000e\fG.Y\u0005\u0003'Q\tq\u0001]1dW\u0006<WMC\u0001\u0012\u0013\t1rC\u0001\tSk:$\u0018.\\3Fq\u000e,\u0007\u000f^5p]*\u00111\u0003\u0006\u0005\t3\u0001\u0011)\u0019!C\u00015\u00051!/Z1t_:,\u0012a\u0007\t\u00039\u0001r!!\b\u0010\u000e\u0003QI!a\b\u000b\u0002\rA\u0013X\rZ3g\u0013\t\t#E\u0001\u0004TiJLgn\u001a\u0006\u0003?QA\u0001\u0002\n\u0001\u0003\u0002\u0003\u0006IaG\u0001\be\u0016\f7o\u001c8!\u0011\u00151\u0003\u0001\"\u0001(\u0003\u0019a\u0014N\\5u}Q\u0011\u0001F\u000b\t\u0003S\u0001i\u0011A\u0001\u0005\u00063\u0015\u0002\ra\u0007\u0005\u0006M\u0001!\t\u0001\f\u000b\u0002Q!\u0012\u0001A\f\t\u0003_Ij\u0011\u0001\r\u0006\u0003c\t\t!\"\u00198o_R\fG/[8o\u0013\t\u0019\u0004G\u0001\u0007EKZ,Gn\u001c9fe\u0006\u0003\u0018\u000e"
)
public class TaskKilledException extends RuntimeException {
   private final String reason;

   public String reason() {
      return this.reason;
   }

   public TaskKilledException(String reason) {
      this.reason = reason;
   }

   public TaskKilledException() {
      this("unknown reason");
   }
}
