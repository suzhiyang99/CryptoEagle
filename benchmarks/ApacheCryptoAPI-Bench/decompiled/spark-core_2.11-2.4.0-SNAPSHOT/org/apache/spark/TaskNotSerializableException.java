package org.apache.spark;

import scala.reflect.ScalaSignature;

@ScalaSignature(
   bytes = "\u0006\u0001\r2Q!\u0001\u0002\u0001\u0005!\u0011A\u0004V1tW:{GoU3sS\u0006d\u0017N_1cY\u0016,\u0005pY3qi&|gN\u0003\u0002\u0004\t\u0005)1\u000f]1sW*\u0011QAB\u0001\u0007CB\f7\r[3\u000b\u0003\u001d\t1a\u001c:h'\t\u0001\u0011\u0002\u0005\u0002\u000b+9\u00111B\u0005\b\u0003\u0019Ai\u0011!\u0004\u0006\u0003\u001d=\ta\u0001\u0010:p_Rt4\u0001A\u0005\u0002#\u0005)1oY1mC&\u00111\u0003F\u0001\ba\u0006\u001c7.Y4f\u0015\u0005\t\u0012B\u0001\f\u0018\u0005%)\u0005pY3qi&|gN\u0003\u0002\u0014)!A\u0011\u0004\u0001B\u0001B\u0003%!$A\u0003feJ|'\u000f\u0005\u0002\u000b7%\u0011Ad\u0006\u0002\n)\"\u0014xn^1cY\u0016DQA\b\u0001\u0005\u0002}\ta\u0001P5oSRtDC\u0001\u0011#!\t\t\u0003!D\u0001\u0003\u0011\u0015IR\u00041\u0001\u001b\u0001"
)
public class TaskNotSerializableException extends Exception {
   public TaskNotSerializableException(Throwable error) {
      super(error);
   }
}
