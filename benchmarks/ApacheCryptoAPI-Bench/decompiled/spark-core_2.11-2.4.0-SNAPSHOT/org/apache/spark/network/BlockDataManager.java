package org.apache.spark.network;

import org.apache.spark.network.buffer.ManagedBuffer;
import org.apache.spark.storage.BlockId;
import org.apache.spark.storage.StorageLevel;
import scala.Option;
import scala.reflect.ClassTag;
import scala.reflect.ScalaSignature;

@ScalaSignature(
   bytes = "\u0006\u0001Q3\u0001\"\u0001\u0002\u0011\u0002G\u0005AA\u0003\u0002\u0011\u00052|7m\u001b#bi\u0006l\u0015M\\1hKJT!a\u0001\u0003\u0002\u000f9,Go^8sW*\u0011QAB\u0001\u0006gB\f'o\u001b\u0006\u0003\u000f!\ta!\u00199bG\",'\"A\u0005\u0002\u0007=\u0014xm\u0005\u0002\u0001\u0017A\u0011AbD\u0007\u0002\u001b)\ta\"A\u0003tG\u0006d\u0017-\u0003\u0002\u0011\u001b\t1\u0011I\\=SK\u001aDQA\u0005\u0001\u0007\u0002Q\tAbZ3u\u00052|7m\u001b#bi\u0006\u001c\u0001\u0001\u0006\u0002\u00167A\u0011a#G\u0007\u0002/)\u0011\u0001DA\u0001\u0007EV4g-\u001a:\n\u0005i9\"!D'b]\u0006<W\r\u001a\"vM\u001a,'\u000fC\u0003\u001d#\u0001\u0007Q$A\u0004cY>\u001c7.\u00133\u0011\u0005y\tS\"A\u0010\u000b\u0005\u0001\"\u0011aB:u_J\fw-Z\u0005\u0003E}\u0011qA\u00117pG.LE\rC\u0003%\u0001\u0019\u0005Q%\u0001\u0007qkR\u0014En\\2l\t\u0006$\u0018\rF\u0003'S)b\u0013\u0007\u0005\u0002\rO%\u0011\u0001&\u0004\u0002\b\u0005>|G.Z1o\u0011\u0015a2\u00051\u0001\u001e\u0011\u0015Y3\u00051\u0001\u0016\u0003\u0011!\u0017\r^1\t\u000b5\u001a\u0003\u0019\u0001\u0018\u0002\u000b1,g/\u001a7\u0011\u0005yy\u0013B\u0001\u0019 \u00051\u0019Fo\u001c:bO\u0016dUM^3m\u0011\u0015\u00114\u00051\u00014\u0003!\u0019G.Y:t)\u0006<\u0007G\u0001\u001b=!\r)\u0004HO\u0007\u0002m)\u0011q'D\u0001\be\u00164G.Z2u\u0013\tIdG\u0001\u0005DY\u0006\u001c8\u000fV1h!\tYD\b\u0004\u0001\u0005\u0013u\n\u0014\u0011!A\u0001\u0006\u0003q$aA0%cE\u0011qH\u0011\t\u0003\u0019\u0001K!!Q\u0007\u0003\u000f9{G\u000f[5oOB\u0011AbQ\u0005\u0003\t6\u00111!\u00118z\u0011\u00151\u0005A\"\u0001H\u0003-\u0011X\r\\3bg\u0016dunY6\u0015\u0007![E\n\u0005\u0002\r\u0013&\u0011!*\u0004\u0002\u0005+:LG\u000fC\u0003\u001d\u000b\u0002\u0007Q\u0004C\u0003N\u000b\u0002\u0007a*A\u0007uCN\\\u0017\t\u001e;f[B$\u0018\n\u001a\t\u0004\u0019=\u000b\u0016B\u0001)\u000e\u0005\u0019y\u0005\u000f^5p]B\u0011ABU\u0005\u0003'6\u0011A\u0001T8oO\u0002"
)
public interface BlockDataManager {
   ManagedBuffer getBlockData(BlockId var1);

   boolean putBlockData(BlockId var1, ManagedBuffer var2, StorageLevel var3, ClassTag<?> var4);

   void releaseLock(BlockId var1, Option<Object> var2);
}
