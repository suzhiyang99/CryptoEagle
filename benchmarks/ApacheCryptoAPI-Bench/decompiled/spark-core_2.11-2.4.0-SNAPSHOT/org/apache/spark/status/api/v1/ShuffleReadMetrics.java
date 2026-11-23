package org.apache.spark.status.api.v1;

import scala.reflect.ScalaSignature;

@ScalaSignature(
   bytes = "\u0006\u0001\t3A!\u0001\u0002\u0001\u001f\t\u00112\u000b[;gM2,'+Z1e\u001b\u0016$(/[2t\u0015\t\u0019A!\u0001\u0002wc)\u0011QAB\u0001\u0004CBL'BA\u0004\t\u0003\u0019\u0019H/\u0019;vg*\u0011\u0011BC\u0001\u0006gB\f'o\u001b\u0006\u0003\u00171\ta!\u00199bG\",'\"A\u0007\u0002\u0007=\u0014xm\u0001\u0001\u0014\u0005\u0001\u0001\u0002CA\t\u0015\u001b\u0005\u0011\"\"A\n\u0002\u000bM\u001c\u0017\r\\1\n\u0005U\u0011\"AB!osJ+g\r\u0003\u0005\u0018\u0001\t\u0015\r\u0011\"\u0001\u0019\u0003M\u0011X-\\8uK\ncwnY6t\r\u0016$8\r[3e+\u0005I\u0002CA\t\u001b\u0013\tY\"C\u0001\u0003M_:<\u0007\u0002C\u000f\u0001\u0005\u0003\u0005\u000b\u0011B\r\u0002)I,Wn\u001c;f\u00052|7m[:GKR\u001c\u0007.\u001a3!\u0011!y\u0002A!b\u0001\n\u0003A\u0012A\u00057pG\u0006d'\t\\8dWN4U\r^2iK\u0012D\u0001\"\t\u0001\u0003\u0002\u0003\u0006I!G\u0001\u0014Y>\u001c\u0017\r\u001c\"m_\u000e\\7OR3uG\",G\r\t\u0005\tG\u0001\u0011)\u0019!C\u00011\u0005ia-\u001a;dQ^\u000b\u0017\u000e\u001e+j[\u0016D\u0001\"\n\u0001\u0003\u0002\u0003\u0006I!G\u0001\u000fM\u0016$8\r[,bSR$\u0016.\\3!\u0011!9\u0003A!b\u0001\n\u0003A\u0012a\u0004:f[>$XMQ=uKN\u0014V-\u00193\t\u0011%\u0002!\u0011!Q\u0001\ne\t\u0001C]3n_R,')\u001f;fgJ+\u0017\r\u001a\u0011\t\u0011-\u0002!Q1A\u0005\u0002a\tQC]3n_R,')\u001f;fgJ+\u0017\r\u001a+p\t&\u001c8\u000e\u0003\u0005.\u0001\t\u0005\t\u0015!\u0003\u001a\u0003Y\u0011X-\\8uK\nKH/Z:SK\u0006$Gk\u001c#jg.\u0004\u0003\u0002C\u0018\u0001\u0005\u000b\u0007I\u0011\u0001\r\u0002\u001d1|7-\u00197CsR,7OU3bI\"A\u0011\u0007\u0001B\u0001B\u0003%\u0011$A\bm_\u000e\fGNQ=uKN\u0014V-\u00193!\u0011!\u0019\u0004A!b\u0001\n\u0003A\u0012a\u0003:fG>\u0014Hm\u001d*fC\u0012D\u0001\"\u000e\u0001\u0003\u0002\u0003\u0006I!G\u0001\re\u0016\u001cwN\u001d3t%\u0016\fG\r\t\u0005\u0007o\u0001!\t\u0001\u0003\u001d\u0002\rqJg.\u001b;?)!I4\bP\u001f?\u007f\u0001\u000b\u0005C\u0001\u001e\u0001\u001b\u0005\u0011\u0001\"B\f7\u0001\u0004I\u0002\"B\u00107\u0001\u0004I\u0002\"B\u00127\u0001\u0004I\u0002\"B\u00147\u0001\u0004I\u0002\"B\u00167\u0001\u0004I\u0002\"B\u00187\u0001\u0004I\u0002\"B\u001a7\u0001\u0004I\u0002"
)
public class ShuffleReadMetrics {
   private final long remoteBlocksFetched;
   private final long localBlocksFetched;
   private final long fetchWaitTime;
   private final long remoteBytesRead;
   private final long remoteBytesReadToDisk;
   private final long localBytesRead;
   private final long recordsRead;

   public long remoteBlocksFetched() {
      return this.remoteBlocksFetched;
   }

   public long localBlocksFetched() {
      return this.localBlocksFetched;
   }

   public long fetchWaitTime() {
      return this.fetchWaitTime;
   }

   public long remoteBytesRead() {
      return this.remoteBytesRead;
   }

   public long remoteBytesReadToDisk() {
      return this.remoteBytesReadToDisk;
   }

   public long localBytesRead() {
      return this.localBytesRead;
   }

   public long recordsRead() {
      return this.recordsRead;
   }

   public ShuffleReadMetrics(long remoteBlocksFetched, long localBlocksFetched, long fetchWaitTime, long remoteBytesRead, long remoteBytesReadToDisk, long localBytesRead, long recordsRead) {
      this.remoteBlocksFetched = remoteBlocksFetched;
      this.localBlocksFetched = localBlocksFetched;
      this.fetchWaitTime = fetchWaitTime;
      this.remoteBytesRead = remoteBytesRead;
      this.remoteBytesReadToDisk = remoteBytesReadToDisk;
      this.localBytesRead = localBytesRead;
      this.recordsRead = recordsRead;
   }
}
