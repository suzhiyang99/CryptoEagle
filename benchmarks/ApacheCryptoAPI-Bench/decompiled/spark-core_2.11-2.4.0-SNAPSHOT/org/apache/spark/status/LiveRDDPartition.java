package org.apache.spark.status;

import org.apache.spark.status.api.v1.RDDPartitionInfo;
import scala.collection.Seq;
import scala.reflect.ScalaSignature;

@ScalaSignature(
   bytes = "\u0006\u000194A!\u0001\u0002\u0005\u0017\t\u0001B*\u001b<f%\u0012#\u0005+\u0019:uSRLwN\u001c\u0006\u0003\u0007\u0011\taa\u001d;biV\u001c(BA\u0003\u0007\u0003\u0015\u0019\b/\u0019:l\u0015\t9\u0001\"\u0001\u0004ba\u0006\u001c\u0007.\u001a\u0006\u0002\u0013\u0005\u0019qN]4\u0004\u0001M\u0011\u0001\u0001\u0004\t\u0003\u001bAi\u0011A\u0004\u0006\u0002\u001f\u0005)1oY1mC&\u0011\u0011C\u0004\u0002\u0007\u0003:L(+\u001a4\t\u0011M\u0001!Q1A\u0005\u0002Q\t\u0011B\u00197pG.t\u0015-\\3\u0016\u0003U\u0001\"AF\r\u000f\u000559\u0012B\u0001\r\u000f\u0003\u0019\u0001&/\u001a3fM&\u0011!d\u0007\u0002\u0007'R\u0014\u0018N\\4\u000b\u0005aq\u0001\u0002C\u000f\u0001\u0005\u0003\u0005\u000b\u0011B\u000b\u0002\u0015\tdwnY6OC6,\u0007\u0005C\u0003 \u0001\u0011\u0005\u0001%\u0001\u0004=S:LGO\u0010\u000b\u0003C\r\u0002\"A\t\u0001\u000e\u0003\tAQa\u0005\u0010A\u0002UAq!\n\u0001A\u0002\u0013\u0005a%\u0001\u0003qe\u00164X#A\u0011\t\u000f!\u0002\u0001\u0019!C\u0001S\u0005A\u0001O]3w?\u0012*\u0017\u000f\u0006\u0002+[A\u0011QbK\u0005\u0003Y9\u0011A!\u00168ji\"9afJA\u0001\u0002\u0004\t\u0013a\u0001=%c!1\u0001\u0007\u0001Q!\n\u0005\nQ\u0001\u001d:fm\u0002B#a\f\u001a\u0011\u00055\u0019\u0014B\u0001\u001b\u000f\u0005!1x\u000e\\1uS2,\u0007b\u0002\u001c\u0001\u0001\u0004%\tAJ\u0001\u0005]\u0016DH\u000fC\u00049\u0001\u0001\u0007I\u0011A\u001d\u0002\u00119,\u0007\u0010^0%KF$\"A\u000b\u001e\t\u000f9:\u0014\u0011!a\u0001C!1A\b\u0001Q!\n\u0005\nQA\\3yi\u0002B#a\u000f\u001a\t\u000f}\u0002\u0001\u0019!C\u0001\u0001\u0006)a/\u00197vKV\t\u0011\t\u0005\u0002C\u000f6\t1I\u0003\u0002E\u000b\u0006\u0011a/\r\u0006\u0003\r\n\t1!\u00199j\u0013\tA5I\u0001\tS\t\u0012\u0003\u0016M\u001d;ji&|g.\u00138g_\"9!\n\u0001a\u0001\n\u0003Y\u0015!\u0003<bYV,w\fJ3r)\tQC\nC\u0004/\u0013\u0006\u0005\t\u0019A!\t\r9\u0003\u0001\u0015)\u0003B\u0003\u00191\u0018\r\\;fA!)\u0001\u000b\u0001C\u0001#\u0006IQ\r_3dkR|'o]\u000b\u0002%B\u00191kW\u000b\u000f\u0005QKfBA+Y\u001b\u00051&BA,\u000b\u0003\u0019a$o\\8u}%\tq\"\u0003\u0002[\u001d\u00059\u0001/Y2lC\u001e,\u0017B\u0001/^\u0005\r\u0019V-\u001d\u0006\u00035:AQa\u0018\u0001\u0005\u0002\u0001\f!\"\\3n_JLXk]3e+\u0005\t\u0007CA\u0007c\u0013\t\u0019gB\u0001\u0003M_:<\u0007\"B3\u0001\t\u0003\u0001\u0017\u0001\u00033jg.,6/\u001a3\t\u000b\u001d\u0004A\u0011\u00015\u0002\rU\u0004H-\u0019;f)\u0015Q\u0013N\u001b7n\u0011\u0015\u0001f\r1\u0001S\u0011\u0015Yg\r1\u0001\u0016\u00031\u0019Ho\u001c:bO\u0016dUM^3m\u0011\u0015yf\r1\u0001b\u0011\u0015)g\r1\u0001b\u0001"
)
public class LiveRDDPartition {
   private final String blockName;
   private volatile LiveRDDPartition prev;
   private volatile LiveRDDPartition next;
   private RDDPartitionInfo value;

   public String blockName() {
      return this.blockName;
   }

   public LiveRDDPartition prev() {
      return this.prev;
   }

   public void prev_$eq(LiveRDDPartition x$1) {
      this.prev = x$1;
   }

   public LiveRDDPartition next() {
      return this.next;
   }

   public void next_$eq(LiveRDDPartition x$1) {
      this.next = x$1;
   }

   public RDDPartitionInfo value() {
      return this.value;
   }

   public void value_$eq(RDDPartitionInfo x$1) {
      this.value = x$1;
   }

   public Seq<String> executors() {
      return this.value().executors();
   }

   public long memoryUsed() {
      return this.value().memoryUsed();
   }

   public long diskUsed() {
      return this.value().diskUsed();
   }

   public void update(Seq<String> executors, String storageLevel, long memoryUsed, long diskUsed) {
      this.value_$eq(new RDDPartitionInfo(this.blockName(), LiveEntityHelpers$.MODULE$.weakIntern(storageLevel), memoryUsed, diskUsed, executors));
   }

   public LiveRDDPartition(String blockName) {
      this.blockName = blockName;
      this.prev = null;
      this.next = null;
      this.value = null;
   }
}
