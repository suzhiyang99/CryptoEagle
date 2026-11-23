package org.apache.spark.status.api.v1;

import scala.collection.IndexedSeq;
import scala.reflect.ScalaSignature;

@ScalaSignature(
   bytes = "\u0006\u0001M3A!\u0001\u0002\u0001\u001f\tq2\u000b[;gM2,'+Z1e\u001b\u0016$(/[2ESN$(/\u001b2vi&|gn\u001d\u0006\u0003\u0007\u0011\t!A^\u0019\u000b\u0005\u00151\u0011aA1qS*\u0011q\u0001C\u0001\u0007gR\fG/^:\u000b\u0005%Q\u0011!B:qCJ\\'BA\u0006\r\u0003\u0019\t\u0007/Y2iK*\tQ\"A\u0002pe\u001e\u001c\u0001a\u0005\u0002\u0001!A\u0011\u0011\u0003F\u0007\u0002%)\t1#A\u0003tG\u0006d\u0017-\u0003\u0002\u0016%\t1\u0011I\\=SK\u001aD\u0001b\u0006\u0001\u0003\u0006\u0004%\t\u0001G\u0001\ne\u0016\fGMQ=uKN,\u0012!\u0007\t\u00045\t*cBA\u000e!\u001d\tar$D\u0001\u001e\u0015\tqb\"\u0001\u0004=e>|GOP\u0005\u0002'%\u0011\u0011EE\u0001\ba\u0006\u001c7.Y4f\u0013\t\u0019CE\u0001\u0006J]\u0012,\u00070\u001a3TKFT!!\t\n\u0011\u0005E1\u0013BA\u0014\u0013\u0005\u0019!u.\u001e2mK\"A\u0011\u0006\u0001B\u0001B\u0003%\u0011$\u0001\u0006sK\u0006$')\u001f;fg\u0002B\u0001b\u000b\u0001\u0003\u0006\u0004%\t\u0001G\u0001\fe\u0016\fGMU3d_J$7\u000f\u0003\u0005.\u0001\t\u0005\t\u0015!\u0003\u001a\u00031\u0011X-\u00193SK\u000e|'\u000fZ:!\u0011!y\u0003A!b\u0001\n\u0003A\u0012a\u0005:f[>$XM\u00117pG.\u001ch)\u001a;dQ\u0016$\u0007\u0002C\u0019\u0001\u0005\u0003\u0005\u000b\u0011B\r\u0002)I,Wn\u001c;f\u00052|7m[:GKR\u001c\u0007.\u001a3!\u0011!\u0019\u0004A!b\u0001\n\u0003A\u0012A\u00057pG\u0006d'\t\\8dWN4U\r^2iK\u0012D\u0001\"\u000e\u0001\u0003\u0002\u0003\u0006I!G\u0001\u0014Y>\u001c\u0017\r\u001c\"m_\u000e\\7OR3uG\",G\r\t\u0005\to\u0001\u0011)\u0019!C\u00011\u0005ia-\u001a;dQ^\u000b\u0017\u000e\u001e+j[\u0016D\u0001\"\u000f\u0001\u0003\u0002\u0003\u0006I!G\u0001\u000fM\u0016$8\r[,bSR$\u0016.\\3!\u0011!Y\u0004A!b\u0001\n\u0003A\u0012a\u0004:f[>$XMQ=uKN\u0014V-\u00193\t\u0011u\u0002!\u0011!Q\u0001\ne\t\u0001C]3n_R,')\u001f;fgJ+\u0017\r\u001a\u0011\t\u0011}\u0002!Q1A\u0005\u0002a\tQC]3n_R,')\u001f;fgJ+\u0017\r\u001a+p\t&\u001c8\u000e\u0003\u0005B\u0001\t\u0005\t\u0015!\u0003\u001a\u0003Y\u0011X-\\8uK\nKH/Z:SK\u0006$Gk\u001c#jg.\u0004\u0003\u0002C\"\u0001\u0005\u000b\u0007I\u0011\u0001\r\u0002%Q|G/\u00197CY>\u001c7n\u001d$fi\u000eDW\r\u001a\u0005\t\u000b\u0002\u0011\t\u0011)A\u00053\u0005\u0019Bo\u001c;bY\ncwnY6t\r\u0016$8\r[3eA!1q\t\u0001C\u0001\u0011!\u000ba\u0001P5oSRtD#C%L\u00196su\nU)S!\tQ\u0005!D\u0001\u0003\u0011\u00159b\t1\u0001\u001a\u0011\u0015Yc\t1\u0001\u001a\u0011\u0015yc\t1\u0001\u001a\u0011\u0015\u0019d\t1\u0001\u001a\u0011\u00159d\t1\u0001\u001a\u0011\u0015Yd\t1\u0001\u001a\u0011\u0015yd\t1\u0001\u001a\u0011\u0015\u0019e\t1\u0001\u001a\u0001"
)
public class ShuffleReadMetricDistributions {
   private final IndexedSeq<Object> readBytes;
   private final IndexedSeq<Object> readRecords;
   private final IndexedSeq<Object> remoteBlocksFetched;
   private final IndexedSeq<Object> localBlocksFetched;
   private final IndexedSeq<Object> fetchWaitTime;
   private final IndexedSeq<Object> remoteBytesRead;
   private final IndexedSeq<Object> remoteBytesReadToDisk;
   private final IndexedSeq<Object> totalBlocksFetched;

   public IndexedSeq<Object> readBytes() {
      return this.readBytes;
   }

   public IndexedSeq<Object> readRecords() {
      return this.readRecords;
   }

   public IndexedSeq<Object> remoteBlocksFetched() {
      return this.remoteBlocksFetched;
   }

   public IndexedSeq<Object> localBlocksFetched() {
      return this.localBlocksFetched;
   }

   public IndexedSeq<Object> fetchWaitTime() {
      return this.fetchWaitTime;
   }

   public IndexedSeq<Object> remoteBytesRead() {
      return this.remoteBytesRead;
   }

   public IndexedSeq<Object> remoteBytesReadToDisk() {
      return this.remoteBytesReadToDisk;
   }

   public IndexedSeq<Object> totalBlocksFetched() {
      return this.totalBlocksFetched;
   }

   public ShuffleReadMetricDistributions(IndexedSeq<Object> readBytes, IndexedSeq<Object> readRecords, IndexedSeq<Object> remoteBlocksFetched, IndexedSeq<Object> localBlocksFetched, IndexedSeq<Object> fetchWaitTime, IndexedSeq<Object> remoteBytesRead, IndexedSeq<Object> remoteBytesReadToDisk, IndexedSeq<Object> totalBlocksFetched) {
      this.readBytes = readBytes;
      this.readRecords = readRecords;
      this.remoteBlocksFetched = remoteBlocksFetched;
      this.localBlocksFetched = localBlocksFetched;
      this.fetchWaitTime = fetchWaitTime;
      this.remoteBytesRead = remoteBytesRead;
      this.remoteBytesReadToDisk = remoteBytesReadToDisk;
      this.totalBlocksFetched = totalBlocksFetched;
   }
}
