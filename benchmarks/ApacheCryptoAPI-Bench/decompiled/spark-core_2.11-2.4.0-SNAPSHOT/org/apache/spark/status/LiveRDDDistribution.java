package org.apache.spark.status;

import org.apache.spark.status.api.v1.RDDDataDistribution;
import scala.None$;
import scala.Option;
import scala.Some;
import scala.reflect.ScalaSignature;
import scala.runtime.BoxesRunTime;

@ScalaSignature(
   bytes = "\u0006\u0001\u00154A!\u0001\u0002\u0005\u0017\t\u0019B*\u001b<f%\u0012#E)[:ue&\u0014W\u000f^5p]*\u00111\u0001B\u0001\u0007gR\fG/^:\u000b\u0005\u00151\u0011!B:qCJ\\'BA\u0004\t\u0003\u0019\t\u0007/Y2iK*\t\u0011\"A\u0002pe\u001e\u001c\u0001a\u0005\u0002\u0001\u0019A\u0011Q\u0002E\u0007\u0002\u001d)\tq\"A\u0003tG\u0006d\u0017-\u0003\u0002\u0012\u001d\t1\u0011I\\=SK\u001aD\u0001b\u0005\u0001\u0003\u0002\u0003\u0006I\u0001F\u0001\u0005Kb,7\r\u0005\u0002\u0016-5\t!!\u0003\u0002\u0018\u0005\taA*\u001b<f\u000bb,7-\u001e;pe\")\u0011\u0004\u0001C\u00015\u00051A(\u001b8jiz\"\"a\u0007\u000f\u0011\u0005U\u0001\u0001\"B\n\u0019\u0001\u0004!\u0002b\u0002\u0010\u0001\u0005\u0004%\taH\u0001\u000bKb,7-\u001e;pe&#W#\u0001\u0011\u0011\u0005\u0005\"cBA\u0007#\u0013\t\u0019c\"\u0001\u0004Qe\u0016$WMZ\u0005\u0003K\u0019\u0012aa\u0015;sS:<'BA\u0012\u000f\u0011\u0019A\u0003\u0001)A\u0005A\u0005YQ\r_3dkR|'/\u00133!\u0011\u001dQ\u0003\u00011A\u0005\u0002-\n!\"\\3n_JLXk]3e+\u0005a\u0003CA\u0007.\u0013\tqcB\u0001\u0003M_:<\u0007b\u0002\u0019\u0001\u0001\u0004%\t!M\u0001\u000f[\u0016lwN]=Vg\u0016$w\fJ3r)\t\u0011T\u0007\u0005\u0002\u000eg%\u0011AG\u0004\u0002\u0005+:LG\u000fC\u00047_\u0005\u0005\t\u0019\u0001\u0017\u0002\u0007a$\u0013\u0007\u0003\u00049\u0001\u0001\u0006K\u0001L\u0001\f[\u0016lwN]=Vg\u0016$\u0007\u0005C\u0004;\u0001\u0001\u0007I\u0011A\u0016\u0002\u0011\u0011L7o[+tK\u0012Dq\u0001\u0010\u0001A\u0002\u0013\u0005Q(\u0001\u0007eSN\\Wk]3e?\u0012*\u0017\u000f\u0006\u00023}!9agOA\u0001\u0002\u0004a\u0003B\u0002!\u0001A\u0003&A&A\u0005eSN\\Wk]3eA!9!\t\u0001a\u0001\n\u0003Y\u0013AC8o\u0011\u0016\f\u0007/V:fI\"9A\t\u0001a\u0001\n\u0003)\u0015AD8o\u0011\u0016\f\u0007/V:fI~#S-\u001d\u000b\u0003e\u0019CqAN\"\u0002\u0002\u0003\u0007A\u0006\u0003\u0004I\u0001\u0001\u0006K\u0001L\u0001\f_:DU-\u00199Vg\u0016$\u0007\u0005C\u0004K\u0001\u0001\u0007I\u0011A\u0016\u0002\u0017=4g\rS3baV\u001bX\r\u001a\u0005\b\u0019\u0002\u0001\r\u0011\"\u0001N\u0003=ygM\u001a%fCB,6/\u001a3`I\u0015\fHC\u0001\u001aO\u0011\u001d14*!AA\u00021Ba\u0001\u0015\u0001!B\u0013a\u0013\u0001D8gM\"+\u0017\r]+tK\u0012\u0004\u0003b\u0002*\u0001\u0001\u0004%\taU\u0001\u000bY\u0006\u001cH/\u00169eCR,W#\u0001+\u0011\u0005USV\"\u0001,\u000b\u0005]C\u0016A\u0001<2\u0015\tI&!A\u0002ba&L!a\u0017,\u0003'I#E\tR1uC\u0012K7\u000f\u001e:jEV$\u0018n\u001c8\t\u000fu\u0003\u0001\u0019!C\u0001=\u0006qA.Y:u+B$\u0017\r^3`I\u0015\fHC\u0001\u001a`\u0011\u001d1D,!AA\u0002QCa!\u0019\u0001!B\u0013!\u0016a\u00037bgR,\u0006\u000fZ1uK\u0002BQa\u0019\u0001\u0005\u0002\u0011\fQ\u0001^8Ba&$\u0012\u0001\u0016"
)
public class LiveRDDDistribution {
   private final LiveExecutor exec;
   private final String executorId;
   private long memoryUsed;
   private long diskUsed;
   private long onHeapUsed;
   private long offHeapUsed;
   private RDDDataDistribution lastUpdate;

   public String executorId() {
      return this.executorId;
   }

   public long memoryUsed() {
      return this.memoryUsed;
   }

   public void memoryUsed_$eq(long x$1) {
      this.memoryUsed = x$1;
   }

   public long diskUsed() {
      return this.diskUsed;
   }

   public void diskUsed_$eq(long x$1) {
      this.diskUsed = x$1;
   }

   public long onHeapUsed() {
      return this.onHeapUsed;
   }

   public void onHeapUsed_$eq(long x$1) {
      this.onHeapUsed = x$1;
   }

   public long offHeapUsed() {
      return this.offHeapUsed;
   }

   public void offHeapUsed_$eq(long x$1) {
      this.offHeapUsed = x$1;
   }

   public RDDDataDistribution lastUpdate() {
      return this.lastUpdate;
   }

   public void lastUpdate_$eq(RDDDataDistribution x$1) {
      this.lastUpdate = x$1;
   }

   public RDDDataDistribution toApi() {
      if (this.lastUpdate() == null) {
         this.lastUpdate_$eq(new RDDDataDistribution(LiveEntityHelpers$.MODULE$.weakIntern(this.exec.hostPort()), this.memoryUsed(), this.exec.maxMemory() - this.exec.memoryUsed(), this.diskUsed(), (Option)(this.exec.hasMemoryInfo() ? new Some(BoxesRunTime.boxToLong(this.onHeapUsed())) : None$.MODULE$), (Option)(this.exec.hasMemoryInfo() ? new Some(BoxesRunTime.boxToLong(this.offHeapUsed())) : None$.MODULE$), (Option)(this.exec.hasMemoryInfo() ? new Some(BoxesRunTime.boxToLong(this.exec.totalOnHeap() - this.exec.usedOnHeap())) : None$.MODULE$), (Option)(this.exec.hasMemoryInfo() ? new Some(BoxesRunTime.boxToLong(this.exec.totalOffHeap() - this.exec.usedOffHeap())) : None$.MODULE$)));
      }

      return this.lastUpdate();
   }

   public LiveRDDDistribution(LiveExecutor exec) {
      this.exec = exec;
      this.executorId = exec.executorId();
      this.memoryUsed = 0L;
      this.diskUsed = 0L;
      this.onHeapUsed = 0L;
      this.offHeapUsed = 0L;
      this.lastUpdate = null;
   }
}
