package org.apache.spark.status.api.v1;

import scala.collection.Seq;
import scala.reflect.ScalaSignature;

@ScalaSignature(
   bytes = "\u0006\u000153A!\u0001\u0002\u0001\u001f\t\u0001\"\u000b\u0012#QCJ$\u0018\u000e^5p]&sgm\u001c\u0006\u0003\u0007\u0011\t!A^\u0019\u000b\u0005\u00151\u0011aA1qS*\u0011q\u0001C\u0001\u0007gR\fG/^:\u000b\u0005%Q\u0011!B:qCJ\\'BA\u0006\r\u0003\u0019\t\u0007/Y2iK*\tQ\"A\u0002pe\u001e\u001c\u0001a\u0005\u0002\u0001!A\u0011\u0011\u0003F\u0007\u0002%)\t1#A\u0003tG\u0006d\u0017-\u0003\u0002\u0016%\t1\u0011I\\=SK\u001aD\u0001b\u0006\u0001\u0003\u0006\u0004%\t\u0001G\u0001\nE2|7m\u001b(b[\u0016,\u0012!\u0007\t\u00035uq!!E\u000e\n\u0005q\u0011\u0012A\u0002)sK\u0012,g-\u0003\u0002\u001f?\t11\u000b\u001e:j]\u001eT!\u0001\b\n\t\u0011\u0005\u0002!\u0011!Q\u0001\ne\t!B\u00197pG.t\u0015-\\3!\u0011!\u0019\u0003A!b\u0001\n\u0003A\u0012\u0001D:u_J\fw-\u001a'fm\u0016d\u0007\u0002C\u0013\u0001\u0005\u0003\u0005\u000b\u0011B\r\u0002\u001bM$xN]1hK2+g/\u001a7!\u0011!9\u0003A!b\u0001\n\u0003A\u0013AC7f[>\u0014\u00180V:fIV\t\u0011\u0006\u0005\u0002\u0012U%\u00111F\u0005\u0002\u0005\u0019>tw\r\u0003\u0005.\u0001\t\u0005\t\u0015!\u0003*\u0003-iW-\\8ssV\u001bX\r\u001a\u0011\t\u0011=\u0002!Q1A\u0005\u0002!\n\u0001\u0002Z5tWV\u001bX\r\u001a\u0005\tc\u0001\u0011\t\u0011)A\u0005S\u0005IA-[:l+N,G\r\t\u0005\tg\u0001\u0011)\u0019!C\u0001i\u0005IQ\r_3dkR|'o]\u000b\u0002kA\u0019aGP\r\u000f\u0005]bdB\u0001\u001d<\u001b\u0005I$B\u0001\u001e\u000f\u0003\u0019a$o\\8u}%\t1#\u0003\u0002>%\u00059\u0001/Y2lC\u001e,\u0017BA A\u0005\r\u0019V-\u001d\u0006\u0003{IA\u0001B\u0011\u0001\u0003\u0002\u0003\u0006I!N\u0001\u000bKb,7-\u001e;peN\u0004\u0003B\u0002#\u0001\t\u0003AQ)\u0001\u0004=S:LGO\u0010\u000b\u0007\r\"K%j\u0013'\u0011\u0005\u001d\u0003Q\"\u0001\u0002\t\u000b]\u0019\u0005\u0019A\r\t\u000b\r\u001a\u0005\u0019A\r\t\u000b\u001d\u001a\u0005\u0019A\u0015\t\u000b=\u001a\u0005\u0019A\u0015\t\u000bM\u001a\u0005\u0019A\u001b"
)
public class RDDPartitionInfo {
   private final String blockName;
   private final String storageLevel;
   private final long memoryUsed;
   private final long diskUsed;
   private final Seq<String> executors;

   public String blockName() {
      return this.blockName;
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

   public Seq<String> executors() {
      return this.executors;
   }

   public RDDPartitionInfo(String blockName, String storageLevel, long memoryUsed, long diskUsed, Seq<String> executors) {
      this.blockName = blockName;
      this.storageLevel = storageLevel;
      this.memoryUsed = memoryUsed;
      this.diskUsed = diskUsed;
      this.executors = executors;
   }
}
