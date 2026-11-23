package org.apache.spark.status.api.v1;

import scala.collection.IndexedSeq;
import scala.reflect.ScalaSignature;

@ScalaSignature(
   bytes = "\u0006\u0001\u0005\u0005b\u0001B\u0001\u0003\u0001=\u0011q\u0003V1tW6+GO]5d\t&\u001cHO]5ckRLwN\\:\u000b\u0005\r!\u0011A\u0001<2\u0015\t)a!A\u0002ba&T!a\u0002\u0005\u0002\rM$\u0018\r^;t\u0015\tI!\"A\u0003ta\u0006\u00148N\u0003\u0002\f\u0019\u00051\u0011\r]1dQ\u0016T\u0011!D\u0001\u0004_J<7\u0001A\n\u0003\u0001A\u0001\"!\u0005\u000b\u000e\u0003IQ\u0011aE\u0001\u0006g\u000e\fG.Y\u0005\u0003+I\u0011a!\u00118z%\u00164\u0007\u0002C\f\u0001\u0005\u000b\u0007I\u0011\u0001\r\u0002\u0013E,\u0018M\u001c;jY\u0016\u001cX#A\r\u0011\u0007i\u0011SE\u0004\u0002\u001cA9\u0011AdH\u0007\u0002;)\u0011aDD\u0001\u0007yI|w\u000e\u001e \n\u0003MI!!\t\n\u0002\u000fA\f7m[1hK&\u00111\u0005\n\u0002\u000b\u0013:$W\r_3e'\u0016\f(BA\u0011\u0013!\t\tb%\u0003\u0002(%\t1Ai\\;cY\u0016D\u0001\"\u000b\u0001\u0003\u0002\u0003\u0006I!G\u0001\u000bcV\fg\u000e^5mKN\u0004\u0003\u0002C\u0016\u0001\u0005\u000b\u0007I\u0011\u0001\r\u0002/\u0015DXmY;u_J$Um]3sS\u0006d\u0017N_3US6,\u0007\u0002C\u0017\u0001\u0005\u0003\u0005\u000b\u0011B\r\u00021\u0015DXmY;u_J$Um]3sS\u0006d\u0017N_3US6,\u0007\u0005\u0003\u00050\u0001\t\u0015\r\u0011\"\u0001\u0019\u0003i)\u00070Z2vi>\u0014H)Z:fe&\fG.\u001b>f\u0007B,H+[7f\u0011!\t\u0004A!A!\u0002\u0013I\u0012aG3yK\u000e,Ho\u001c:EKN,'/[1mSj,7\t];US6,\u0007\u0005\u0003\u00054\u0001\t\u0015\r\u0011\"\u0001\u0019\u0003=)\u00070Z2vi>\u0014(+\u001e8US6,\u0007\u0002C\u001b\u0001\u0005\u0003\u0005\u000b\u0011B\r\u0002!\u0015DXmY;u_J\u0014VO\u001c+j[\u0016\u0004\u0003\u0002C\u001c\u0001\u0005\u000b\u0007I\u0011\u0001\r\u0002\u001f\u0015DXmY;u_J\u001c\u0005/\u001e+j[\u0016D\u0001\"\u000f\u0001\u0003\u0002\u0003\u0006I!G\u0001\u0011Kb,7-\u001e;pe\u000e\u0003X\u000fV5nK\u0002B\u0001b\u000f\u0001\u0003\u0006\u0004%\t\u0001G\u0001\u000be\u0016\u001cX\u000f\u001c;TSj,\u0007\u0002C\u001f\u0001\u0005\u0003\u0005\u000b\u0011B\r\u0002\u0017I,7/\u001e7u'&TX\r\t\u0005\t\u007f\u0001\u0011)\u0019!C\u00011\u0005I!N^7HGRKW.\u001a\u0005\t\u0003\u0002\u0011\t\u0011)A\u00053\u0005Q!N^7HGRKW.\u001a\u0011\t\u0011\r\u0003!Q1A\u0005\u0002a\tqC]3tk2$8+\u001a:jC2L'0\u0019;j_:$\u0016.\\3\t\u0011\u0015\u0003!\u0011!Q\u0001\ne\t\u0001D]3tk2$8+\u001a:jC2L'0\u0019;j_:$\u0016.\\3!\u0011!9\u0005A!b\u0001\n\u0003A\u0012!E4fiRLgn\u001a*fgVdG\u000fV5nK\"A\u0011\n\u0001B\u0001B\u0003%\u0011$\u0001\nhKR$\u0018N\\4SKN,H\u000e\u001e+j[\u0016\u0004\u0003\u0002C&\u0001\u0005\u000b\u0007I\u0011\u0001\r\u0002\u001dM\u001c\u0007.\u001a3vY\u0016\u0014H)\u001a7bs\"AQ\n\u0001B\u0001B\u0003%\u0011$A\btG\",G-\u001e7fe\u0012+G.Y=!\u0011!y\u0005A!b\u0001\n\u0003A\u0012a\u00059fC.,\u00050Z2vi&|g.T3n_JL\b\u0002C)\u0001\u0005\u0003\u0005\u000b\u0011B\r\u0002)A,\u0017m[#yK\u000e,H/[8o\u001b\u0016lwN]=!\u0011!\u0019\u0006A!b\u0001\n\u0003A\u0012AE7f[>\u0014\u0018PQ=uKN\u001c\u0006/\u001b7mK\u0012D\u0001\"\u0016\u0001\u0003\u0002\u0003\u0006I!G\u0001\u0014[\u0016lwN]=CsR,7o\u00159jY2,G\r\t\u0005\t/\u0002\u0011)\u0019!C\u00011\u0005\u0001B-[:l\u0005f$Xm]*qS2dW\r\u001a\u0005\t3\u0002\u0011\t\u0011)A\u00053\u0005\tB-[:l\u0005f$Xm]*qS2dW\r\u001a\u0011\t\u0011m\u0003!Q1A\u0005\u0002q\u000bA\"\u001b8qkRlU\r\u001e:jGN,\u0012!\u0018\t\u0003=~k\u0011AA\u0005\u0003A\n\u0011\u0001$\u00138qkRlU\r\u001e:jG\u0012K7\u000f\u001e:jEV$\u0018n\u001c8t\u0011!\u0011\u0007A!A!\u0002\u0013i\u0016!D5oaV$X*\u001a;sS\u000e\u001c\b\u0005\u0003\u0005e\u0001\t\u0015\r\u0011\"\u0001f\u00035yW\u000f\u001e9vi6+GO]5dgV\ta\r\u0005\u0002_O&\u0011\u0001N\u0001\u0002\u001a\u001fV$\b/\u001e;NKR\u0014\u0018n\u0019#jgR\u0014\u0018NY;uS>t7\u000f\u0003\u0005k\u0001\t\u0005\t\u0015!\u0003g\u00039yW\u000f\u001e9vi6+GO]5dg\u0002B\u0001\u0002\u001c\u0001\u0003\u0006\u0004%\t!\\\u0001\u0013g\",hM\u001a7f%\u0016\fG-T3ue&\u001c7/F\u0001o!\tqv.\u0003\u0002q\u0005\tq2\u000b[;gM2,'+Z1e\u001b\u0016$(/[2ESN$(/\u001b2vi&|gn\u001d\u0005\te\u0002\u0011\t\u0011)A\u0005]\u0006\u00192\u000f[;gM2,'+Z1e\u001b\u0016$(/[2tA!AA\u000f\u0001BC\u0002\u0013\u0005Q/A\ntQV4g\r\\3Xe&$X-T3ue&\u001c7/F\u0001w!\tqv/\u0003\u0002y\u0005\ty2\u000b[;gM2,wK]5uK6+GO]5d\t&\u001cHO]5ckRLwN\\:\t\u0011i\u0004!\u0011!Q\u0001\nY\fAc\u001d5vM\u001adWm\u0016:ji\u0016lU\r\u001e:jGN\u0004\u0003B\u0002?\u0001\t\u0003AQ0\u0001\u0004=S:LGO\u0010\u000b#}~\f\t!a\u0001\u0002\u0006\u0005\u001d\u0011\u0011BA\u0006\u0003\u001b\ty!!\u0005\u0002\u0014\u0005U\u0011qCA\r\u00037\ti\"a\b\u0011\u0005y\u0003\u0001\"B\f|\u0001\u0004I\u0002\"B\u0016|\u0001\u0004I\u0002\"B\u0018|\u0001\u0004I\u0002\"B\u001a|\u0001\u0004I\u0002\"B\u001c|\u0001\u0004I\u0002\"B\u001e|\u0001\u0004I\u0002\"B |\u0001\u0004I\u0002\"B\"|\u0001\u0004I\u0002\"B$|\u0001\u0004I\u0002\"B&|\u0001\u0004I\u0002\"B(|\u0001\u0004I\u0002\"B*|\u0001\u0004I\u0002\"B,|\u0001\u0004I\u0002\"B.|\u0001\u0004i\u0006\"\u00023|\u0001\u00041\u0007\"\u00027|\u0001\u0004q\u0007\"\u0002;|\u0001\u00041\b"
)
public class TaskMetricDistributions {
   private final IndexedSeq<Object> quantiles;
   private final IndexedSeq<Object> executorDeserializeTime;
   private final IndexedSeq<Object> executorDeserializeCpuTime;
   private final IndexedSeq<Object> executorRunTime;
   private final IndexedSeq<Object> executorCpuTime;
   private final IndexedSeq<Object> resultSize;
   private final IndexedSeq<Object> jvmGcTime;
   private final IndexedSeq<Object> resultSerializationTime;
   private final IndexedSeq<Object> gettingResultTime;
   private final IndexedSeq<Object> schedulerDelay;
   private final IndexedSeq<Object> peakExecutionMemory;
   private final IndexedSeq<Object> memoryBytesSpilled;
   private final IndexedSeq<Object> diskBytesSpilled;
   private final InputMetricDistributions inputMetrics;
   private final OutputMetricDistributions outputMetrics;
   private final ShuffleReadMetricDistributions shuffleReadMetrics;
   private final ShuffleWriteMetricDistributions shuffleWriteMetrics;

   public IndexedSeq<Object> quantiles() {
      return this.quantiles;
   }

   public IndexedSeq<Object> executorDeserializeTime() {
      return this.executorDeserializeTime;
   }

   public IndexedSeq<Object> executorDeserializeCpuTime() {
      return this.executorDeserializeCpuTime;
   }

   public IndexedSeq<Object> executorRunTime() {
      return this.executorRunTime;
   }

   public IndexedSeq<Object> executorCpuTime() {
      return this.executorCpuTime;
   }

   public IndexedSeq<Object> resultSize() {
      return this.resultSize;
   }

   public IndexedSeq<Object> jvmGcTime() {
      return this.jvmGcTime;
   }

   public IndexedSeq<Object> resultSerializationTime() {
      return this.resultSerializationTime;
   }

   public IndexedSeq<Object> gettingResultTime() {
      return this.gettingResultTime;
   }

   public IndexedSeq<Object> schedulerDelay() {
      return this.schedulerDelay;
   }

   public IndexedSeq<Object> peakExecutionMemory() {
      return this.peakExecutionMemory;
   }

   public IndexedSeq<Object> memoryBytesSpilled() {
      return this.memoryBytesSpilled;
   }

   public IndexedSeq<Object> diskBytesSpilled() {
      return this.diskBytesSpilled;
   }

   public InputMetricDistributions inputMetrics() {
      return this.inputMetrics;
   }

   public OutputMetricDistributions outputMetrics() {
      return this.outputMetrics;
   }

   public ShuffleReadMetricDistributions shuffleReadMetrics() {
      return this.shuffleReadMetrics;
   }

   public ShuffleWriteMetricDistributions shuffleWriteMetrics() {
      return this.shuffleWriteMetrics;
   }

   public TaskMetricDistributions(IndexedSeq<Object> quantiles, IndexedSeq<Object> executorDeserializeTime, IndexedSeq<Object> executorDeserializeCpuTime, IndexedSeq<Object> executorRunTime, IndexedSeq<Object> executorCpuTime, IndexedSeq<Object> resultSize, IndexedSeq<Object> jvmGcTime, IndexedSeq<Object> resultSerializationTime, IndexedSeq<Object> gettingResultTime, IndexedSeq<Object> schedulerDelay, IndexedSeq<Object> peakExecutionMemory, IndexedSeq<Object> memoryBytesSpilled, IndexedSeq<Object> diskBytesSpilled, InputMetricDistributions inputMetrics, OutputMetricDistributions outputMetrics, ShuffleReadMetricDistributions shuffleReadMetrics, ShuffleWriteMetricDistributions shuffleWriteMetrics) {
      this.quantiles = quantiles;
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
      this.inputMetrics = inputMetrics;
      this.outputMetrics = outputMetrics;
      this.shuffleReadMetrics = shuffleReadMetrics;
      this.shuffleWriteMetrics = shuffleWriteMetrics;
   }
}
