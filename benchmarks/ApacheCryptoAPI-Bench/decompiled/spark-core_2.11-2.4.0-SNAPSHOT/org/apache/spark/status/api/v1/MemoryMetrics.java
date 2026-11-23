package org.apache.spark.status.api.v1;

import scala.reflect.ScalaSignature;

@ScalaSignature(
   bytes = "\u0006\u0001M2A!\u0001\u0002\u0001\u001f\tiQ*Z7peflU\r\u001e:jGNT!a\u0001\u0003\u0002\u0005Y\f$BA\u0003\u0007\u0003\r\t\u0007/\u001b\u0006\u0003\u000f!\taa\u001d;biV\u001c(BA\u0005\u000b\u0003\u0015\u0019\b/\u0019:l\u0015\tYA\"\u0001\u0004ba\u0006\u001c\u0007.\u001a\u0006\u0002\u001b\u0005\u0019qN]4\u0004\u0001M\u0011\u0001\u0001\u0005\t\u0003#Qi\u0011A\u0005\u0006\u0002'\u0005)1oY1mC&\u0011QC\u0005\u0002\u0007\u0003:L(+\u001a4\t\u0011]\u0001!Q1A\u0005\u0002a\tq#^:fI>s\u0007*Z1q'R|'/Y4f\u001b\u0016lwN]=\u0016\u0003e\u0001\"!\u0005\u000e\n\u0005m\u0011\"\u0001\u0002'p]\u001eD\u0001\"\b\u0001\u0003\u0002\u0003\u0006I!G\u0001\u0019kN,Gm\u00148IK\u0006\u00048\u000b^8sC\u001e,W*Z7pef\u0004\u0003\u0002C\u0010\u0001\u0005\u000b\u0007I\u0011\u0001\r\u00021U\u001cX\rZ(gM\"+\u0017\r]*u_J\fw-Z'f[>\u0014\u0018\u0010\u0003\u0005\"\u0001\t\u0005\t\u0015!\u0003\u001a\u0003e)8/\u001a3PM\u001aDU-\u00199Ti>\u0014\u0018mZ3NK6|'/\u001f\u0011\t\u0011\r\u0002!Q1A\u0005\u0002a\t\u0001\u0004^8uC2|e\u000eS3baN#xN]1hK6+Wn\u001c:z\u0011!)\u0003A!A!\u0002\u0013I\u0012!\u0007;pi\u0006dwJ\u001c%fCB\u001cFo\u001c:bO\u0016lU-\\8ss\u0002B\u0001b\n\u0001\u0003\u0006\u0004%\t\u0001G\u0001\u001ai>$\u0018\r\\(gM\"+\u0017\r]*u_J\fw-Z'f[>\u0014\u0018\u0010\u0003\u0005*\u0001\t\u0005\t\u0015!\u0003\u001a\u0003i!x\u000e^1m\u001f\u001a4\u0007*Z1q'R|'/Y4f\u001b\u0016lwN]=!\u0011\u0019Y\u0003\u0001\"\u0001\tY\u00051A(\u001b8jiz\"R!L\u00181cI\u0002\"A\f\u0001\u000e\u0003\tAQa\u0006\u0016A\u0002eAQa\b\u0016A\u0002eAQa\t\u0016A\u0002eAQa\n\u0016A\u0002e\u0001"
)
public class MemoryMetrics {
   private final long usedOnHeapStorageMemory;
   private final long usedOffHeapStorageMemory;
   private final long totalOnHeapStorageMemory;
   private final long totalOffHeapStorageMemory;

   public long usedOnHeapStorageMemory() {
      return this.usedOnHeapStorageMemory;
   }

   public long usedOffHeapStorageMemory() {
      return this.usedOffHeapStorageMemory;
   }

   public long totalOnHeapStorageMemory() {
      return this.totalOnHeapStorageMemory;
   }

   public long totalOffHeapStorageMemory() {
      return this.totalOffHeapStorageMemory;
   }

   public MemoryMetrics(long usedOnHeapStorageMemory, long usedOffHeapStorageMemory, long totalOnHeapStorageMemory, long totalOffHeapStorageMemory) {
      this.usedOnHeapStorageMemory = usedOnHeapStorageMemory;
      this.usedOffHeapStorageMemory = usedOffHeapStorageMemory;
      this.totalOnHeapStorageMemory = totalOnHeapStorageMemory;
      this.totalOffHeapStorageMemory = totalOffHeapStorageMemory;
   }
}
