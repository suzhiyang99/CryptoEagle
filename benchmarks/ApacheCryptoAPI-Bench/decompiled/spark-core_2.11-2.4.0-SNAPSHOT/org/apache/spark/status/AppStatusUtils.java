package org.apache.spark.status;

import org.apache.spark.status.api.v1.TaskData;
import scala.reflect.ScalaSignature;

@ScalaSignature(
   bytes = "\u0006\u0001\r;a!\u0001\u0002\t\u0002\u0011Q\u0011AD!qaN#\u0018\r^;t+RLGn\u001d\u0006\u0003\u0007\u0011\taa\u001d;biV\u001c(BA\u0003\u0007\u0003\u0015\u0019\b/\u0019:l\u0015\t9\u0001\"\u0001\u0004ba\u0006\u001c\u0007.\u001a\u0006\u0002\u0013\u0005\u0019qN]4\u0011\u0005-aQ\"\u0001\u0002\u0007\r5\u0011\u0001\u0012\u0001\u0003\u000f\u00059\t\u0005\u000f]*uCR,8/\u0016;jYN\u001c\"\u0001D\b\u0011\u0005A\u0019R\"A\t\u000b\u0003I\tQa]2bY\u0006L!\u0001F\t\u0003\r\u0005s\u0017PU3g\u0011\u00151B\u0002\"\u0001\u0019\u0003\u0019a\u0014N\\5u}\r\u0001A#\u0001\u0006\t\u000biaA\u0011A\u000e\u0002\u001dM\u001c\u0007.\u001a3vY\u0016\u0014H)\u001a7bsR\u0011Ad\b\t\u0003!uI!AH\t\u0003\t1{gn\u001a\u0005\u0006Ae\u0001\r!I\u0001\u0005i\u0006\u001c8\u000e\u0005\u0002#O5\t1E\u0003\u0002%K\u0005\u0011a/\r\u0006\u0003M\t\t1!\u00199j\u0013\tA3E\u0001\u0005UCN\\G)\u0019;b\u0011\u0015QC\u0002\"\u0001,\u0003E9W\r\u001e;j]\u001e\u0014Vm];miRKW.\u001a\u000b\u000391BQ\u0001I\u0015A\u0002\u0005BQA\u0007\u0007\u0005\u00029\"r\u0001H\u00182gU:\u0014\bC\u00031[\u0001\u0007A$\u0001\u0006mCVt7\r\u001b+j[\u0016DQAM\u0017A\u0002q\t!BZ3uG\"\u001cF/\u0019:u\u0011\u0015!T\u00061\u0001\u001d\u0003!!WO]1uS>t\u0007\"\u0002\u001c.\u0001\u0004a\u0012a\u00043fg\u0016\u0014\u0018.\u00197ju\u0016$\u0016.\\3\t\u000baj\u0003\u0019\u0001\u000f\u0002\u001bM,'/[1mSj,G+[7f\u0011\u0015QT\u00061\u0001\u001d\u0003\u001d\u0011XO\u001c+j[\u0016DQA\u000b\u0007\u0005\u0002q\"B\u0001H\u001f?\u007f!)\u0001g\u000fa\u00019!)!g\u000fa\u00019!)Ag\u000fa\u00019!)!\u0007\u0004C\u0005\u0003R\u0011AD\u0011\u0005\u0006A\u0001\u0003\r!\t"
)
public final class AppStatusUtils {
   public static long gettingResultTime(long var0, long var2, long var4) {
      return AppStatusUtils$.MODULE$.gettingResultTime(var0, var2, var4);
   }

   public static long schedulerDelay(long var0, long var2, long var4, long var6, long var8, long var10) {
      return AppStatusUtils$.MODULE$.schedulerDelay(var0, var2, var4, var6, var8, var10);
   }

   public static long gettingResultTime(TaskData var0) {
      return AppStatusUtils$.MODULE$.gettingResultTime(var0);
   }

   public static long schedulerDelay(TaskData var0) {
      return AppStatusUtils$.MODULE$.schedulerDelay(var0);
   }
}
