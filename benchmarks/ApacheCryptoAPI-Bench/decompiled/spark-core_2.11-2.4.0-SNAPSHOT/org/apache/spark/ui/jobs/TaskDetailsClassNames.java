package org.apache.spark.ui.jobs;

import scala.reflect.ScalaSignature;

@ScalaSignature(
   bytes = "\u0006\u0001\u0001;a!\u0001\u0002\t\u0002\u0019a\u0011!\u0006+bg.$U\r^1jYN\u001cE.Y:t\u001d\u0006lWm\u001d\u0006\u0003\u0007\u0011\tAA[8cg*\u0011QAB\u0001\u0003k&T!a\u0002\u0005\u0002\u000bM\u0004\u0018M]6\u000b\u0005%Q\u0011AB1qC\u000eDWMC\u0001\f\u0003\ry'o\u001a\t\u0003\u001b9i\u0011A\u0001\u0004\u0007\u001f\tA\tA\u0002\t\u0003+Q\u000b7o\u001b#fi\u0006LGn]\"mCN\u001ch*Y7fgN\u0011a\"\u0005\t\u0003%Ui\u0011a\u0005\u0006\u0002)\u0005)1oY1mC&\u0011ac\u0005\u0002\u0007\u0003:L(+\u001a4\t\u000baqA\u0011\u0001\u000e\u0002\rqJg.\u001b;?\u0007\u0001!\u0012\u0001\u0004\u0005\b99\u0011\r\u0011\"\u0001\u001e\u0003=\u00196\tS#E+2+%k\u0018#F\u0019\u0006KV#\u0001\u0010\u0011\u0005}!S\"\u0001\u0011\u000b\u0005\u0005\u0012\u0013\u0001\u00027b]\u001eT\u0011aI\u0001\u0005U\u00064\u0018-\u0003\u0002&A\t11\u000b\u001e:j]\u001eDaa\n\b!\u0002\u0013q\u0012\u0001E*D\u0011\u0016#U\u000bT#S?\u0012+E*Q-!\u0011\u001dIcB1A\u0005\u0002u\t\u0011\u0004V!T\u0017~#UiU#S\u0013\u0006c\u0015JW!U\u0013>su\fV%N\u000b\"11F\u0004Q\u0001\ny\t!\u0004V!T\u0017~#UiU#S\u0013\u0006c\u0015JW!U\u0013>su\fV%N\u000b\u0002Bq!\f\bC\u0002\u0013\u0005Q$A\rT\u0011V3e\tT#`%\u0016\u000bEi\u0018\"M\u001f\u000e[U\tR0U\u00136+\u0005BB\u0018\u000fA\u0003%a$\u0001\u000eT\u0011V3e\tT#`%\u0016\u000bEi\u0018\"M\u001f\u000e[U\tR0U\u00136+\u0005\u0005C\u00042\u001d\t\u0007I\u0011A\u000f\u00021MCUK\u0012$M\u000b~\u0013V)\u0011#`%\u0016ku\nV#`'&SV\t\u0003\u00044\u001d\u0001\u0006IAH\u0001\u001a'\"+fI\u0012'F?J+\u0015\tR0S\u000b6{E+R0T\u0013j+\u0005\u0005C\u00046\u001d\t\u0007I\u0011A\u000f\u00023I+5+\u0016'U?N+%+S!M\u0013j\u000bE+S(O?RKU*\u0012\u0005\u0007o9\u0001\u000b\u0011\u0002\u0010\u00025I+5+\u0016'U?N+%+S!M\u0013j\u000bE+S(O?RKU*\u0012\u0011\t\u000fer!\u0019!C\u0001;\u0005\u0019r)\u0012+U\u0013:;uLU#T+2#v\fV%N\u000b\"11H\u0004Q\u0001\ny\tAcR#U)&sui\u0018*F'VcEk\u0018+J\u001b\u0016\u0003\u0003bB\u001f\u000f\u0005\u0004%\t!H\u0001\u0016!\u0016\u000b5jX#Y\u000b\u000e+F+S(O?6+Uj\u0014*Z\u0011\u0019yd\u0002)A\u0005=\u00051\u0002+R!L?\u0016CViQ+U\u0013>su,T#N\u001fJK\u0006\u0005"
)
public final class TaskDetailsClassNames {
   public static String PEAK_EXECUTION_MEMORY() {
      return TaskDetailsClassNames$.MODULE$.PEAK_EXECUTION_MEMORY();
   }

   public static String GETTING_RESULT_TIME() {
      return TaskDetailsClassNames$.MODULE$.GETTING_RESULT_TIME();
   }

   public static String RESULT_SERIALIZATION_TIME() {
      return TaskDetailsClassNames$.MODULE$.RESULT_SERIALIZATION_TIME();
   }

   public static String SHUFFLE_READ_REMOTE_SIZE() {
      return TaskDetailsClassNames$.MODULE$.SHUFFLE_READ_REMOTE_SIZE();
   }

   public static String SHUFFLE_READ_BLOCKED_TIME() {
      return TaskDetailsClassNames$.MODULE$.SHUFFLE_READ_BLOCKED_TIME();
   }

   public static String TASK_DESERIALIZATION_TIME() {
      return TaskDetailsClassNames$.MODULE$.TASK_DESERIALIZATION_TIME();
   }

   public static String SCHEDULER_DELAY() {
      return TaskDetailsClassNames$.MODULE$.SCHEDULER_DELAY();
   }
}
