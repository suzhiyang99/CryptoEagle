package org.apache.spark.status;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.apache.spark.util.kvstore.KVIndex;
import scala.reflect.ScalaSignature;

@ScalaSignature(
   bytes = "\u0006\u0001A4Q!\u0001\u0002\u0001\t)\u0011qb\u0015;sK\u0006l'\t\\8dW\u0012\u000bG/\u0019\u0006\u0003\u0007\u0011\taa\u001d;biV\u001c(BA\u0003\u0007\u0003\u0015\u0019\b/\u0019:l\u0015\t9\u0001\"\u0001\u0004ba\u0006\u001c\u0007.\u001a\u0006\u0002\u0013\u0005\u0019qN]4\u0014\u0005\u0001Y\u0001C\u0001\u0007\u0010\u001b\u0005i!\"\u0001\b\u0002\u000bM\u001c\u0017\r\\1\n\u0005Ai!AB!osJ+g\r\u0003\u0005\u0013\u0001\t\u0015\r\u0011\"\u0001\u0015\u0003\u0011q\u0017-\\3\u0004\u0001U\tQ\u0003\u0005\u0002\u001739\u0011AbF\u0005\u000315\ta\u0001\u0015:fI\u00164\u0017B\u0001\u000e\u001c\u0005\u0019\u0019FO]5oO*\u0011\u0001$\u0004\u0005\t;\u0001\u0011\t\u0011)A\u0005+\u0005)a.Y7fA!Aq\u0004\u0001BC\u0002\u0013\u0005A#\u0001\u0006fq\u0016\u001cW\u000f^8s\u0013\u0012D\u0001\"\t\u0001\u0003\u0002\u0003\u0006I!F\u0001\fKb,7-\u001e;pe&#\u0007\u0005\u0003\u0005$\u0001\t\u0015\r\u0011\"\u0001\u0015\u0003!Awn\u001d;Q_J$\b\u0002C\u0013\u0001\u0005\u0003\u0005\u000b\u0011B\u000b\u0002\u0013!|7\u000f\u001e)peR\u0004\u0003\u0002C\u0014\u0001\u0005\u000b\u0007I\u0011\u0001\u000b\u0002\u0019M$xN]1hK2+g/\u001a7\t\u0011%\u0002!\u0011!Q\u0001\nU\tQb\u001d;pe\u0006<W\rT3wK2\u0004\u0003\u0002C\u0016\u0001\u0005\u000b\u0007I\u0011\u0001\u0017\u0002\u0013U\u001cX-T3n_JLX#A\u0017\u0011\u00051q\u0013BA\u0018\u000e\u0005\u001d\u0011un\u001c7fC:D\u0001\"\r\u0001\u0003\u0002\u0003\u0006I!L\u0001\u000bkN,W*Z7pef\u0004\u0003\u0002C\u001a\u0001\u0005\u000b\u0007I\u0011\u0001\u0017\u0002\u000fU\u001cX\rR5tW\"AQ\u0007\u0001B\u0001B\u0003%Q&\u0001\u0005vg\u0016$\u0015n]6!\u0011!9\u0004A!b\u0001\n\u0003a\u0013\u0001\u00043fg\u0016\u0014\u0018.\u00197ju\u0016$\u0007\u0002C\u001d\u0001\u0005\u0003\u0005\u000b\u0011B\u0017\u0002\u001b\u0011,7/\u001a:jC2L'0\u001a3!\u0011!Y\u0004A!b\u0001\n\u0003a\u0014aB7f[NK'0Z\u000b\u0002{A\u0011ABP\u0005\u0003\u007f5\u0011A\u0001T8oO\"A\u0011\t\u0001B\u0001B\u0003%Q(\u0001\u0005nK6\u001c\u0016N_3!\u0011!\u0019\u0005A!b\u0001\n\u0003a\u0014\u0001\u00033jg.\u001c\u0016N_3\t\u0011\u0015\u0003!\u0011!Q\u0001\nu\n\u0011\u0002Z5tWNK'0\u001a\u0011\t\u000b\u001d\u0003A\u0011\u0001%\u0002\rqJg.\u001b;?))I5\nT'O\u001fB\u000b&k\u0015\t\u0003\u0015\u0002i\u0011A\u0001\u0005\u0006%\u0019\u0003\r!\u0006\u0005\u0006?\u0019\u0003\r!\u0006\u0005\u0006G\u0019\u0003\r!\u0006\u0005\u0006O\u0019\u0003\r!\u0006\u0005\u0006W\u0019\u0003\r!\f\u0005\u0006g\u0019\u0003\r!\f\u0005\u0006o\u0019\u0003\r!\f\u0005\u0006w\u0019\u0003\r!\u0010\u0005\u0006\u0007\u001a\u0003\r!\u0010\u0005\u0006+\u0002!\tAV\u0001\u0004W\u0016LX#A,\u0011\u00071AV#\u0003\u0002Z\u001b\t)\u0011I\u001d:bs\"\u0012Ak\u0017\t\u00039\u0006l\u0011!\u0018\u0006\u0003=~\u000bqa\u001b<ti>\u0014XM\u0003\u0002a\t\u0005!Q\u000f^5m\u0013\t\u0011WLA\u0004L-&sG-\u001a=)\u0005Q#\u0007CA3o\u001b\u00051'BA4i\u0003)\tgN\\8uCRLwN\u001c\u0006\u0003S*\fqA[1dWN|gN\u0003\u0002lY\u0006Ia-Y:uKJDX\u000e\u001c\u0006\u0002[\u0006\u00191m\\7\n\u0005=4'A\u0003&t_:LuM\\8sK\u0002"
)
public class StreamBlockData {
   private final String name;
   private final String executorId;
   private final String hostPort;
   private final String storageLevel;
   private final boolean useMemory;
   private final boolean useDisk;
   private final boolean deserialized;
   private final long memSize;
   private final long diskSize;

   public String name() {
      return this.name;
   }

   public String executorId() {
      return this.executorId;
   }

   public String hostPort() {
      return this.hostPort;
   }

   public String storageLevel() {
      return this.storageLevel;
   }

   public boolean useMemory() {
      return this.useMemory;
   }

   public boolean useDisk() {
      return this.useDisk;
   }

   public boolean deserialized() {
      return this.deserialized;
   }

   public long memSize() {
      return this.memSize;
   }

   public long diskSize() {
      return this.diskSize;
   }

   @JsonIgnore
   @KVIndex
   public String[] key() {
      return (String[])((Object[])(new String[]{this.name(), this.executorId()}));
   }

   public StreamBlockData(String name, String executorId, String hostPort, String storageLevel, boolean useMemory, boolean useDisk, boolean deserialized, long memSize, long diskSize) {
      this.name = name;
      this.executorId = executorId;
      this.hostPort = hostPort;
      this.storageLevel = storageLevel;
      this.useMemory = useMemory;
      this.useDisk = useDisk;
      this.deserialized = deserialized;
      this.memSize = memSize;
      this.diskSize = diskSize;
   }
}
