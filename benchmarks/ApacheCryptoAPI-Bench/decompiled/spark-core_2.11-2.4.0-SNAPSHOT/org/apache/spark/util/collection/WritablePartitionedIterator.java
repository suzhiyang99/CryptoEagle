package org.apache.spark.util.collection;

import org.apache.spark.storage.DiskBlockObjectWriter;
import scala.reflect.ScalaSignature;

@ScalaSignature(
   bytes = "\u0006\u000192\u0001\"\u0001\u0002\u0011\u0002G\u0005a\u0001\u0004\u0002\u001c/JLG/\u00192mKB\u000b'\u000f^5uS>tW\rZ%uKJ\fGo\u001c:\u000b\u0005\r!\u0011AC2pY2,7\r^5p]*\u0011QAB\u0001\u0005kRLGN\u0003\u0002\b\u0011\u0005)1\u000f]1sW*\u0011\u0011BC\u0001\u0007CB\f7\r[3\u000b\u0003-\t1a\u001c:h'\t\u0001Q\u0002\u0005\u0002\u000f#5\tqBC\u0001\u0011\u0003\u0015\u00198-\u00197b\u0013\t\u0011rB\u0001\u0004B]f\u0014VM\u001a\u0005\u0006)\u00011\tAF\u0001\noJLG/\u001a(fqR\u001c\u0001\u0001\u0006\u0002\u00185A\u0011a\u0002G\u0005\u00033=\u0011A!\u00168ji\")1d\u0005a\u00019\u00051qO]5uKJ\u0004\"!\b\u0011\u000e\u0003yQ!a\b\u0004\u0002\u000fM$xN]1hK&\u0011\u0011E\b\u0002\u0016\t&\u001c8N\u00117pG.|%M[3di^\u0013\u0018\u000e^3s\u0011\u0015\u0019\u0003A\"\u0001%\u0003\u001dA\u0017m\u001d(fqR$\u0012!\n\t\u0003\u001d\u0019J!aJ\b\u0003\u000f\t{w\u000e\\3b]\")\u0011\u0006\u0001D\u0001U\u0005ia.\u001a=u!\u0006\u0014H/\u001b;j_:$\u0012a\u000b\t\u0003\u001d1J!!L\b\u0003\u0007%sG\u000f"
)
public interface WritablePartitionedIterator {
   void writeNext(DiskBlockObjectWriter var1);

   boolean hasNext();

   int nextPartition();
}
