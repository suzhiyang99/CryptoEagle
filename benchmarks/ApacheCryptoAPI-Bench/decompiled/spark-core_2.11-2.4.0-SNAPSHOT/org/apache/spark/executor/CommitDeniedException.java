package org.apache.spark.executor;

import org.apache.spark.TaskCommitDenied;
import scala.reflect.ScalaSignature;

@ScalaSignature(
   bytes = "\u0006\u0001u2Q!\u0001\u0002\u0001\t)\u0011QcQ8n[&$H)\u001a8jK\u0012,\u0005pY3qi&|gN\u0003\u0002\u0004\t\u0005AQ\r_3dkR|'O\u0003\u0002\u0006\r\u0005)1\u000f]1sW*\u0011q\u0001C\u0001\u0007CB\f7\r[3\u000b\u0003%\t1a\u001c:h'\t\u00011\u0002\u0005\u0002\r/9\u0011Q\u0002\u0006\b\u0003\u001dIi\u0011a\u0004\u0006\u0003!E\ta\u0001\u0010:p_Rt4\u0001A\u0005\u0002'\u0005)1oY1mC&\u0011QCF\u0001\ba\u0006\u001c7.Y4f\u0015\u0005\u0019\u0012B\u0001\r\u001a\u0005%)\u0005pY3qi&|gN\u0003\u0002\u0016-!A1\u0004\u0001B\u0001B\u0003%A$A\u0002ng\u001e\u0004\"!H\u0011\u000f\u0005yyR\"\u0001\f\n\u0005\u00012\u0012A\u0002)sK\u0012,g-\u0003\u0002#G\t11\u000b\u001e:j]\u001eT!\u0001\t\f\t\u0011\u0015\u0002!\u0011!Q\u0001\n\u0019\nQA[8c\u0013\u0012\u0003\"AH\u0014\n\u0005!2\"aA%oi\"A!\u0006\u0001B\u0001B\u0003%a%A\u0004ta2LG/\u0013#\t\u00111\u0002!\u0011!Q\u0001\n\u0019\nQ\"\u0019;uK6\u0004HOT;nE\u0016\u0014\b\"\u0002\u0018\u0001\t\u0003y\u0013A\u0002\u001fj]&$h\bF\u00031eM\"T\u0007\u0005\u00022\u00015\t!\u0001C\u0003\u001c[\u0001\u0007A\u0004C\u0003&[\u0001\u0007a\u0005C\u0003+[\u0001\u0007a\u0005C\u0003-[\u0001\u0007a\u0005C\u00038\u0001\u0011\u0005\u0001(\u0001\ru_R\u000b7o[\"p[6LG\u000fR3oS\u0016$'+Z1t_:,\u0012!\u000f\t\u0003umj\u0011\u0001B\u0005\u0003y\u0011\u0011\u0001\u0003V1tW\u000e{W.\\5u\t\u0016t\u0017.\u001a3"
)
public class CommitDeniedException extends Exception {
   private final int jobID;
   private final int splitID;
   private final int attemptNumber;

   public TaskCommitDenied toTaskCommitDeniedReason() {
      return new TaskCommitDenied(this.jobID, this.splitID, this.attemptNumber);
   }

   public CommitDeniedException(String msg, int jobID, int splitID, int attemptNumber) {
      super(msg);
      this.jobID = jobID;
      this.splitID = splitID;
      this.attemptNumber = attemptNumber;
   }
}
