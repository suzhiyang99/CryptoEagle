package org.apache.spark.status.api.v1;

import scala.Option;
import scala.collection.Seq;
import scala.reflect.ScalaSignature;

@ScalaSignature(
   bytes = "\u0006\u0001E4A!\u0001\u0002\u0001\u001f\tq!\u000b\u0012#Ti>\u0014\u0018mZ3J]\u001a|'BA\u0002\u0005\u0003\t1\u0018G\u0003\u0002\u0006\r\u0005\u0019\u0011\r]5\u000b\u0005\u001dA\u0011AB:uCR,8O\u0003\u0002\n\u0015\u0005)1\u000f]1sW*\u00111\u0002D\u0001\u0007CB\f7\r[3\u000b\u00035\t1a\u001c:h\u0007\u0001\u0019\"\u0001\u0001\t\u0011\u0005E!R\"\u0001\n\u000b\u0003M\tQa]2bY\u0006L!!\u0006\n\u0003\r\u0005s\u0017PU3g\u0011!9\u0002A!b\u0001\n\u0003A\u0012AA5e+\u0005I\u0002CA\t\u001b\u0013\tY\"CA\u0002J]RD\u0001\"\b\u0001\u0003\u0002\u0003\u0006I!G\u0001\u0004S\u0012\u0004\u0003\u0002C\u0010\u0001\u0005\u000b\u0007I\u0011\u0001\u0011\u0002\t9\fW.Z\u000b\u0002CA\u0011!%\n\b\u0003#\rJ!\u0001\n\n\u0002\rA\u0013X\rZ3g\u0013\t1sE\u0001\u0004TiJLgn\u001a\u0006\u0003IIA\u0001\"\u000b\u0001\u0003\u0002\u0003\u0006I!I\u0001\u0006]\u0006lW\r\t\u0005\tW\u0001\u0011)\u0019!C\u00011\u0005ia.^7QCJ$\u0018\u000e^5p]ND\u0001\"\f\u0001\u0003\u0002\u0003\u0006I!G\u0001\u000f]Vl\u0007+\u0019:uSRLwN\\:!\u0011!y\u0003A!b\u0001\n\u0003A\u0012a\u00058v[\u000e\u000b7\r[3e!\u0006\u0014H/\u001b;j_:\u001c\b\u0002C\u0019\u0001\u0005\u0003\u0005\u000b\u0011B\r\u0002)9,XnQ1dQ\u0016$\u0007+\u0019:uSRLwN\\:!\u0011!\u0019\u0004A!b\u0001\n\u0003\u0001\u0013\u0001D:u_J\fw-\u001a'fm\u0016d\u0007\u0002C\u001b\u0001\u0005\u0003\u0005\u000b\u0011B\u0011\u0002\u001bM$xN]1hK2+g/\u001a7!\u0011!9\u0004A!b\u0001\n\u0003A\u0014AC7f[>\u0014\u00180V:fIV\t\u0011\b\u0005\u0002\u0012u%\u00111H\u0005\u0002\u0005\u0019>tw\r\u0003\u0005>\u0001\t\u0005\t\u0015!\u0003:\u0003-iW-\\8ssV\u001bX\r\u001a\u0011\t\u0011}\u0002!Q1A\u0005\u0002a\n\u0001\u0002Z5tWV\u001bX\r\u001a\u0005\t\u0003\u0002\u0011\t\u0011)A\u0005s\u0005IA-[:l+N,G\r\t\u0005\t\u0007\u0002\u0011)\u0019!C\u0001\t\u0006\u0001B-\u0019;b\t&\u001cHO]5ckRLwN\\\u000b\u0002\u000bB\u0019\u0011C\u0012%\n\u0005\u001d\u0013\"AB(qi&|g\u000eE\u0002J#Rs!AS(\u000f\u0005-sU\"\u0001'\u000b\u00055s\u0011A\u0002\u001fs_>$h(C\u0001\u0014\u0013\t\u0001&#A\u0004qC\u000e\\\u0017mZ3\n\u0005I\u001b&aA*fc*\u0011\u0001K\u0005\t\u0003+Zk\u0011AA\u0005\u0003/\n\u00111C\u0015#E\t\u0006$\u0018\rR5tiJL'-\u001e;j_:D\u0001\"\u0017\u0001\u0003\u0002\u0003\u0006I!R\u0001\u0012I\u0006$\u0018\rR5tiJL'-\u001e;j_:\u0004\u0003\u0002C.\u0001\u0005\u000b\u0007I\u0011\u0001/\u0002\u0015A\f'\u000f^5uS>t7/F\u0001^!\r\tbI\u0018\t\u0004\u0013F{\u0006CA+a\u0013\t\t'A\u0001\tS\t\u0012\u0003\u0016M\u001d;ji&|g.\u00138g_\"A1\r\u0001B\u0001B\u0003%Q,A\u0006qCJ$\u0018\u000e^5p]N\u0004\u0003BB3\u0001\t\u0003Aa-\u0001\u0004=S:LGO\u0010\u000b\u000bO\"L'n\u001b7n]>\u0004\bCA+\u0001\u0011\u00159B\r1\u0001\u001a\u0011\u0015yB\r1\u0001\"\u0011\u0015YC\r1\u0001\u001a\u0011\u0015yC\r1\u0001\u001a\u0011\u0015\u0019D\r1\u0001\"\u0011\u00159D\r1\u0001:\u0011\u0015yD\r1\u0001:\u0011\u0015\u0019E\r1\u0001F\u0011\u0015YF\r1\u0001^\u0001"
)
public class RDDStorageInfo {
   private final int id;
   private final String name;
   private final int numPartitions;
   private final int numCachedPartitions;
   private final String storageLevel;
   private final long memoryUsed;
   private final long diskUsed;
   private final Option<Seq<RDDDataDistribution>> dataDistribution;
   private final Option<Seq<RDDPartitionInfo>> partitions;

   public int id() {
      return this.id;
   }

   public String name() {
      return this.name;
   }

   public int numPartitions() {
      return this.numPartitions;
   }

   public int numCachedPartitions() {
      return this.numCachedPartitions;
   }

   public String storageLevel() {
      return this.storageLevel;
   }

   public long memoryUsed() {
      return this.memoryUsed;
   }

   public long diskUsed() {
      return this.diskUsed;
   }

   public Option<Seq<RDDDataDistribution>> dataDistribution() {
      return this.dataDistribution;
   }

   public Option<Seq<RDDPartitionInfo>> partitions() {
      return this.partitions;
   }

   public RDDStorageInfo(int id, String name, int numPartitions, int numCachedPartitions, String storageLevel, long memoryUsed, long diskUsed, Option<Seq<RDDDataDistribution>> dataDistribution, Option<Seq<RDDPartitionInfo>> partitions) {
      this.id = id;
      this.name = name;
      this.numPartitions = numPartitions;
      this.numCachedPartitions = numCachedPartitions;
      this.storageLevel = storageLevel;
      this.memoryUsed = memoryUsed;
      this.diskUsed = diskUsed;
      this.dataDistribution = dataDistribution;
      this.partitions = partitions;
   }
}
