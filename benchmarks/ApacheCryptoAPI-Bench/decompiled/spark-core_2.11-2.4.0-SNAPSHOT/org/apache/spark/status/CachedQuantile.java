package org.apache.spark.status;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.apache.spark.util.kvstore.KVIndex;
import scala.Array$;
import scala.Predef$;
import scala.reflect.ClassTag$;
import scala.reflect.ScalaSignature;
import scala.runtime.BoxesRunTime;

@ScalaSignature(
   bytes = "\u0006\u0001\u0005mg!B\u0001\u0003\u0001\u0011Q!AD\"bG\",G-U;b]RLG.\u001a\u0006\u0003\u0007\u0011\taa\u001d;biV\u001c(BA\u0003\u0007\u0003\u0015\u0019\b/\u0019:l\u0015\t9\u0001\"\u0001\u0004ba\u0006\u001c\u0007.\u001a\u0006\u0002\u0013\u0005\u0019qN]4\u0014\u0005\u0001Y\u0001C\u0001\u0007\u0010\u001b\u0005i!\"\u0001\b\u0002\u000bM\u001c\u0017\r\\1\n\u0005Ai!AB!osJ+g\r\u0003\u0005\u0013\u0001\t\u0015\r\u0011\"\u0001\u0015\u0003\u001d\u0019H/Y4f\u0013\u0012\u001c\u0001!F\u0001\u0016!\taa#\u0003\u0002\u0018\u001b\t\u0019\u0011J\u001c;\t\u0011e\u0001!\u0011!Q\u0001\nU\t\u0001b\u001d;bO\u0016LE\r\t\u0005\t7\u0001\u0011)\u0019!C\u0001)\u0005q1\u000f^1hK\u0006#H/Z7qi&#\u0007\u0002C\u000f\u0001\u0005\u0003\u0005\u000b\u0011B\u000b\u0002\u001fM$\u0018mZ3BiR,W\u000e\u001d;JI\u0002B\u0001b\b\u0001\u0003\u0006\u0004%\t\u0001I\u0001\tcV\fg\u000e^5mKV\t\u0011\u0005\u0005\u0002#K9\u0011AbI\u0005\u0003I5\ta\u0001\u0015:fI\u00164\u0017B\u0001\u0014(\u0005\u0019\u0019FO]5oO*\u0011A%\u0004\u0005\tS\u0001\u0011\t\u0011)A\u0005C\u0005I\u0011/^1oi&dW\r\t\u0005\tW\u0001\u0011)\u0019!C\u0001Y\u0005IA/Y:l\u0007>,h\u000e^\u000b\u0002[A\u0011ABL\u0005\u0003_5\u0011A\u0001T8oO\"A\u0011\u0007\u0001B\u0001B\u0003%Q&\u0001\u0006uCN\\7i\\;oi\u0002B\u0001b\r\u0001\u0003\u0006\u0004%\t\u0001N\u0001\u0018Kb,7-\u001e;pe\u0012+7/\u001a:jC2L'0\u001a+j[\u0016,\u0012!\u000e\t\u0003\u0019YJ!aN\u0007\u0003\r\u0011{WO\u00197f\u0011!I\u0004A!A!\u0002\u0013)\u0014\u0001G3yK\u000e,Ho\u001c:EKN,'/[1mSj,G+[7fA!A1\b\u0001BC\u0002\u0013\u0005A'\u0001\u000efq\u0016\u001cW\u000f^8s\t\u0016\u001cXM]5bY&TXm\u00119v)&lW\r\u0003\u0005>\u0001\t\u0005\t\u0015!\u00036\u0003m)\u00070Z2vi>\u0014H)Z:fe&\fG.\u001b>f\u0007B,H+[7fA!Aq\b\u0001BC\u0002\u0013\u0005A'A\bfq\u0016\u001cW\u000f^8s%VtG+[7f\u0011!\t\u0005A!A!\u0002\u0013)\u0014\u0001E3yK\u000e,Ho\u001c:Sk:$\u0016.\\3!\u0011!\u0019\u0005A!b\u0001\n\u0003!\u0014aD3yK\u000e,Ho\u001c:DaV$\u0016.\\3\t\u0011\u0015\u0003!\u0011!Q\u0001\nU\n\u0001#\u001a=fGV$xN]\"qkRKW.\u001a\u0011\t\u0011\u001d\u0003!Q1A\u0005\u0002Q\n!B]3tk2$8+\u001b>f\u0011!I\u0005A!A!\u0002\u0013)\u0014a\u0003:fgVdGoU5{K\u0002B\u0001b\u0013\u0001\u0003\u0006\u0004%\t\u0001N\u0001\nUZlwi\u0019+j[\u0016D\u0001\"\u0014\u0001\u0003\u0002\u0003\u0006I!N\u0001\u000bUZlwi\u0019+j[\u0016\u0004\u0003\u0002C(\u0001\u0005\u000b\u0007I\u0011\u0001\u001b\u0002/I,7/\u001e7u'\u0016\u0014\u0018.\u00197ju\u0006$\u0018n\u001c8US6,\u0007\u0002C)\u0001\u0005\u0003\u0005\u000b\u0011B\u001b\u00021I,7/\u001e7u'\u0016\u0014\u0018.\u00197ju\u0006$\u0018n\u001c8US6,\u0007\u0005\u0003\u0005T\u0001\t\u0015\r\u0011\"\u00015\u0003E9W\r\u001e;j]\u001e\u0014Vm];miRKW.\u001a\u0005\t+\u0002\u0011\t\u0011)A\u0005k\u0005\u0011r-\u001a;uS:<'+Z:vYR$\u0016.\\3!\u0011!9\u0006A!b\u0001\n\u0003!\u0014AD:dQ\u0016$W\u000f\\3s\t\u0016d\u0017-\u001f\u0005\t3\u0002\u0011\t\u0011)A\u0005k\u0005y1o\u00195fIVdWM\u001d#fY\u0006L\b\u0005\u0003\u0005\\\u0001\t\u0015\r\u0011\"\u00015\u0003M\u0001X-Y6Fq\u0016\u001cW\u000f^5p]6+Wn\u001c:z\u0011!i\u0006A!A!\u0002\u0013)\u0014\u0001\u00069fC.,\u00050Z2vi&|g.T3n_JL\b\u0005\u0003\u0005`\u0001\t\u0015\r\u0011\"\u00015\u0003IiW-\\8ss\nKH/Z:Ta&dG.\u001a3\t\u0011\u0005\u0004!\u0011!Q\u0001\nU\n1#\\3n_JL()\u001f;fgN\u0003\u0018\u000e\u001c7fI\u0002B\u0001b\u0019\u0001\u0003\u0006\u0004%\t\u0001N\u0001\u0011I&\u001c8NQ=uKN\u001c\u0006/\u001b7mK\u0012D\u0001\"\u001a\u0001\u0003\u0002\u0003\u0006I!N\u0001\u0012I&\u001c8NQ=uKN\u001c\u0006/\u001b7mK\u0012\u0004\u0003\u0002C4\u0001\u0005\u000b\u0007I\u0011\u0001\u001b\u0002\u0013\tLH/Z:SK\u0006$\u0007\u0002C5\u0001\u0005\u0003\u0005\u000b\u0011B\u001b\u0002\u0015\tLH/Z:SK\u0006$\u0007\u0005\u0003\u0005l\u0001\t\u0015\r\u0011\"\u00015\u0003-\u0011XmY8sIN\u0014V-\u00193\t\u00115\u0004!\u0011!Q\u0001\nU\nAB]3d_J$7OU3bI\u0002B\u0001b\u001c\u0001\u0003\u0006\u0004%\t\u0001N\u0001\rEf$Xm],sSR$XM\u001c\u0005\tc\u0002\u0011\t\u0011)A\u0005k\u0005i!-\u001f;fg^\u0013\u0018\u000e\u001e;f]\u0002B\u0001b\u001d\u0001\u0003\u0006\u0004%\t\u0001N\u0001\u000fe\u0016\u001cwN\u001d3t/JLG\u000f^3o\u0011!)\bA!A!\u0002\u0013)\u0014a\u0004:fG>\u0014Hm],sSR$XM\u001c\u0011\t\u0011]\u0004!Q1A\u0005\u0002Q\n\u0001c\u001d5vM\u001adWMU3bI\nKH/Z:\t\u0011e\u0004!\u0011!Q\u0001\nU\n\u0011c\u001d5vM\u001adWMU3bI\nKH/Z:!\u0011!Y\bA!b\u0001\n\u0003!\u0014AE:ik\u001a4G.\u001a*fG>\u0014Hm\u001d*fC\u0012D\u0001\" \u0001\u0003\u0002\u0003\u0006I!N\u0001\u0014g\",hM\u001a7f%\u0016\u001cwN\u001d3t%\u0016\fG\r\t\u0005\t\u007f\u0002\u0011)\u0019!C\u0001i\u0005Q2\u000f[;gM2,'+Z7pi\u0016\u0014En\\2lg\u001a+Go\u00195fI\"I\u00111\u0001\u0001\u0003\u0002\u0003\u0006I!N\u0001\u001cg\",hM\u001a7f%\u0016lw\u000e^3CY>\u001c7n\u001d$fi\u000eDW\r\u001a\u0011\t\u0013\u0005\u001d\u0001A!b\u0001\n\u0003!\u0014!G:ik\u001a4G.\u001a'pG\u0006d'\t\\8dWN4U\r^2iK\u0012D\u0011\"a\u0003\u0001\u0005\u0003\u0005\u000b\u0011B\u001b\u00025MDWO\u001a4mK2{7-\u00197CY>\u001c7n\u001d$fi\u000eDW\r\u001a\u0011\t\u0013\u0005=\u0001A!b\u0001\n\u0003!\u0014\u0001F:ik\u001a4G.\u001a$fi\u000eDw+Y5u)&lW\rC\u0005\u0002\u0014\u0001\u0011\t\u0011)A\u0005k\u0005)2\u000f[;gM2,g)\u001a;dQ^\u000b\u0017\u000e\u001e+j[\u0016\u0004\u0003\"CA\f\u0001\t\u0015\r\u0011\"\u00015\u0003Y\u0019\b.\u001e4gY\u0016\u0014V-\\8uK\nKH/Z:SK\u0006$\u0007\"CA\u000e\u0001\t\u0005\t\u0015!\u00036\u0003]\u0019\b.\u001e4gY\u0016\u0014V-\\8uK\nKH/Z:SK\u0006$\u0007\u0005C\u0005\u0002 \u0001\u0011)\u0019!C\u0001i\u0005a2\u000f[;gM2,'+Z7pi\u0016\u0014\u0015\u0010^3t%\u0016\fG\rV8ESN\\\u0007\"CA\u0012\u0001\t\u0005\t\u0015!\u00036\u0003u\u0019\b.\u001e4gY\u0016\u0014V-\\8uK\nKH/Z:SK\u0006$Gk\u001c#jg.\u0004\u0003\"CA\u0014\u0001\t\u0015\r\u0011\"\u00015\u0003e\u0019\b.\u001e4gY\u0016$v\u000e^1m\u00052|7m[:GKR\u001c\u0007.\u001a3\t\u0013\u0005-\u0002A!A!\u0002\u0013)\u0014AG:ik\u001a4G.\u001a+pi\u0006d'\t\\8dWN4U\r^2iK\u0012\u0004\u0003\"CA\u0018\u0001\t\u0015\r\u0011\"\u00015\u0003E\u0019\b.\u001e4gY\u0016<&/\u001b;f\u0005f$Xm\u001d\u0005\n\u0003g\u0001!\u0011!Q\u0001\nU\n!c\u001d5vM\u001adWm\u0016:ji\u0016\u0014\u0015\u0010^3tA!I\u0011q\u0007\u0001\u0003\u0006\u0004%\t\u0001N\u0001\u0014g\",hM\u001a7f/JLG/\u001a*fG>\u0014Hm\u001d\u0005\n\u0003w\u0001!\u0011!Q\u0001\nU\nAc\u001d5vM\u001adWm\u0016:ji\u0016\u0014VmY8sIN\u0004\u0003\"CA \u0001\t\u0015\r\u0011\"\u00015\u0003A\u0019\b.\u001e4gY\u0016<&/\u001b;f)&lW\rC\u0005\u0002D\u0001\u0011\t\u0011)A\u0005k\u0005\t2\u000f[;gM2,wK]5uKRKW.\u001a\u0011\t\u000f\u0005\u001d\u0003\u0001\"\u0001\u0002J\u00051A(\u001b8jiz\"\u0002)a\u0013\u0002P\u0005E\u00131KA+\u0003/\nI&a\u0017\u0002^\u0005}\u0013\u0011MA2\u0003K\n9'!\u001b\u0002l\u00055\u0014qNA9\u0003g\n)(a\u001e\u0002z\u0005m\u0014QPA@\u0003\u0003\u000b\u0019)!\"\u0002\b\u0006%\u00151\u0012\t\u0004\u0003\u001b\u0002Q\"\u0001\u0002\t\rI\t)\u00051\u0001\u0016\u0011\u0019Y\u0012Q\ta\u0001+!1q$!\u0012A\u0002\u0005BaaKA#\u0001\u0004i\u0003BB\u001a\u0002F\u0001\u0007Q\u0007\u0003\u0004<\u0003\u000b\u0002\r!\u000e\u0005\u0007\u007f\u0005\u0015\u0003\u0019A\u001b\t\r\r\u000b)\u00051\u00016\u0011\u00199\u0015Q\ta\u0001k!11*!\u0012A\u0002UBaaTA#\u0001\u0004)\u0004BB*\u0002F\u0001\u0007Q\u0007\u0003\u0004X\u0003\u000b\u0002\r!\u000e\u0005\u00077\u0006\u0015\u0003\u0019A\u001b\t\r}\u000b)\u00051\u00016\u0011\u0019\u0019\u0017Q\ta\u0001k!1q-!\u0012A\u0002UBaa[A#\u0001\u0004)\u0004BB8\u0002F\u0001\u0007Q\u0007\u0003\u0004t\u0003\u000b\u0002\r!\u000e\u0005\u0007o\u0006\u0015\u0003\u0019A\u001b\t\rm\f)\u00051\u00016\u0011\u0019y\u0018Q\ta\u0001k!9\u0011qAA#\u0001\u0004)\u0004bBA\b\u0003\u000b\u0002\r!\u000e\u0005\b\u0003/\t)\u00051\u00016\u0011\u001d\ty\"!\u0012A\u0002UBq!a\n\u0002F\u0001\u0007Q\u0007C\u0004\u00020\u0005\u0015\u0003\u0019A\u001b\t\u000f\u0005]\u0012Q\ta\u0001k!9\u0011qHA#\u0001\u0004)\u0004bBAH\u0001\u0011\u0005\u0011\u0011S\u0001\u0003S\u0012,\"!a%\u0011\u000b1\t)*!'\n\u0007\u0005]UBA\u0003BeJ\f\u0017\u0010E\u0002\r\u00037K1!!(\u000e\u0005\r\te.\u001f\u0015\u0005\u0003\u001b\u000b\t\u000b\u0005\u0003\u0002$\u0006UVBAAS\u0015\u0011\t9+!+\u0002\u0015\u0005tgn\u001c;bi&|gN\u0003\u0003\u0002,\u00065\u0016a\u00026bG.\u001cxN\u001c\u0006\u0005\u0003_\u000b\t,A\u0005gCN$XM\u001d=nY*\u0011\u00111W\u0001\u0004G>l\u0017\u0002BA\\\u0003K\u0013!BS:p]&;gn\u001c:fQ\u0011\ti)a/\u0011\t\u0005u\u0016qY\u0007\u0003\u0003\u007fSA!!1\u0002D\u000691N^:u_J,'bAAc\t\u0005!Q\u000f^5m\u0013\u0011\tI-a0\u0003\u000f-3\u0016J\u001c3fq\"9\u0011Q\u001a\u0001\u0005\u0002\u0005=\u0017!B:uC\u001e,WCAAi!\u0011a\u0011QS\u000b)\t\u0005-\u0017\u0011\u0015\u0015\t\u0003\u0017\fY,a6\u0002Z\u0006)a/\u00197vK\u0006\u0012\u0011Q\u001a"
)
public class CachedQuantile {
   private final int stageId;
   private final int stageAttemptId;
   private final String quantile;
   private final long taskCount;
   private final double executorDeserializeTime;
   private final double executorDeserializeCpuTime;
   private final double executorRunTime;
   private final double executorCpuTime;
   private final double resultSize;
   private final double jvmGcTime;
   private final double resultSerializationTime;
   private final double gettingResultTime;
   private final double schedulerDelay;
   private final double peakExecutionMemory;
   private final double memoryBytesSpilled;
   private final double diskBytesSpilled;
   private final double bytesRead;
   private final double recordsRead;
   private final double bytesWritten;
   private final double recordsWritten;
   private final double shuffleReadBytes;
   private final double shuffleRecordsRead;
   private final double shuffleRemoteBlocksFetched;
   private final double shuffleLocalBlocksFetched;
   private final double shuffleFetchWaitTime;
   private final double shuffleRemoteBytesRead;
   private final double shuffleRemoteBytesReadToDisk;
   private final double shuffleTotalBlocksFetched;
   private final double shuffleWriteBytes;
   private final double shuffleWriteRecords;
   private final double shuffleWriteTime;

   public int stageId() {
      return this.stageId;
   }

   public int stageAttemptId() {
      return this.stageAttemptId;
   }

   public String quantile() {
      return this.quantile;
   }

   public long taskCount() {
      return this.taskCount;
   }

   public double executorDeserializeTime() {
      return this.executorDeserializeTime;
   }

   public double executorDeserializeCpuTime() {
      return this.executorDeserializeCpuTime;
   }

   public double executorRunTime() {
      return this.executorRunTime;
   }

   public double executorCpuTime() {
      return this.executorCpuTime;
   }

   public double resultSize() {
      return this.resultSize;
   }

   public double jvmGcTime() {
      return this.jvmGcTime;
   }

   public double resultSerializationTime() {
      return this.resultSerializationTime;
   }

   public double gettingResultTime() {
      return this.gettingResultTime;
   }

   public double schedulerDelay() {
      return this.schedulerDelay;
   }

   public double peakExecutionMemory() {
      return this.peakExecutionMemory;
   }

   public double memoryBytesSpilled() {
      return this.memoryBytesSpilled;
   }

   public double diskBytesSpilled() {
      return this.diskBytesSpilled;
   }

   public double bytesRead() {
      return this.bytesRead;
   }

   public double recordsRead() {
      return this.recordsRead;
   }

   public double bytesWritten() {
      return this.bytesWritten;
   }

   public double recordsWritten() {
      return this.recordsWritten;
   }

   public double shuffleReadBytes() {
      return this.shuffleReadBytes;
   }

   public double shuffleRecordsRead() {
      return this.shuffleRecordsRead;
   }

   public double shuffleRemoteBlocksFetched() {
      return this.shuffleRemoteBlocksFetched;
   }

   public double shuffleLocalBlocksFetched() {
      return this.shuffleLocalBlocksFetched;
   }

   public double shuffleFetchWaitTime() {
      return this.shuffleFetchWaitTime;
   }

   public double shuffleRemoteBytesRead() {
      return this.shuffleRemoteBytesRead;
   }

   public double shuffleRemoteBytesReadToDisk() {
      return this.shuffleRemoteBytesReadToDisk;
   }

   public double shuffleTotalBlocksFetched() {
      return this.shuffleTotalBlocksFetched;
   }

   public double shuffleWriteBytes() {
      return this.shuffleWriteBytes;
   }

   public double shuffleWriteRecords() {
      return this.shuffleWriteRecords;
   }

   public double shuffleWriteTime() {
      return this.shuffleWriteTime;
   }

   @KVIndex
   @JsonIgnore
   public Object[] id() {
      return Array$.MODULE$.apply(Predef$.MODULE$.genericWrapArray(new Object[]{BoxesRunTime.boxToInteger(this.stageId()), BoxesRunTime.boxToInteger(this.stageAttemptId()), this.quantile()}), ClassTag$.MODULE$.Any());
   }

   @KVIndex("stage")
   @JsonIgnore
   public int[] stage() {
      return new int[]{this.stageId(), this.stageAttemptId()};
   }

   public CachedQuantile(int stageId, int stageAttemptId, String quantile, long taskCount, double executorDeserializeTime, double executorDeserializeCpuTime, double executorRunTime, double executorCpuTime, double resultSize, double jvmGcTime, double resultSerializationTime, double gettingResultTime, double schedulerDelay, double peakExecutionMemory, double memoryBytesSpilled, double diskBytesSpilled, double bytesRead, double recordsRead, double bytesWritten, double recordsWritten, double shuffleReadBytes, double shuffleRecordsRead, double shuffleRemoteBlocksFetched, double shuffleLocalBlocksFetched, double shuffleFetchWaitTime, double shuffleRemoteBytesRead, double shuffleRemoteBytesReadToDisk, double shuffleTotalBlocksFetched, double shuffleWriteBytes, double shuffleWriteRecords, double shuffleWriteTime) {
      this.stageId = stageId;
      this.stageAttemptId = stageAttemptId;
      this.quantile = quantile;
      this.taskCount = taskCount;
      this.executorDeserializeTime = executorDeserializeTime;
      this.executorDeserializeCpuTime = executorDeserializeCpuTime;
      this.executorRunTime = executorRunTime;
      this.executorCpuTime = executorCpuTime;
      this.resultSize = resultSize;
      this.jvmGcTime = jvmGcTime;
      this.resultSerializationTime = resultSerializationTime;
      this.gettingResultTime = gettingResultTime;
      this.schedulerDelay = schedulerDelay;
      this.peakExecutionMemory = peakExecutionMemory;
      this.memoryBytesSpilled = memoryBytesSpilled;
      this.diskBytesSpilled = diskBytesSpilled;
      this.bytesRead = bytesRead;
      this.recordsRead = recordsRead;
      this.bytesWritten = bytesWritten;
      this.recordsWritten = recordsWritten;
      this.shuffleReadBytes = shuffleReadBytes;
      this.shuffleRecordsRead = shuffleRecordsRead;
      this.shuffleRemoteBlocksFetched = shuffleRemoteBlocksFetched;
      this.shuffleLocalBlocksFetched = shuffleLocalBlocksFetched;
      this.shuffleFetchWaitTime = shuffleFetchWaitTime;
      this.shuffleRemoteBytesRead = shuffleRemoteBytesRead;
      this.shuffleRemoteBytesReadToDisk = shuffleRemoteBytesReadToDisk;
      this.shuffleTotalBlocksFetched = shuffleTotalBlocksFetched;
      this.shuffleWriteBytes = shuffleWriteBytes;
      this.shuffleWriteRecords = shuffleWriteRecords;
      this.shuffleWriteTime = shuffleWriteTime;
   }
}
