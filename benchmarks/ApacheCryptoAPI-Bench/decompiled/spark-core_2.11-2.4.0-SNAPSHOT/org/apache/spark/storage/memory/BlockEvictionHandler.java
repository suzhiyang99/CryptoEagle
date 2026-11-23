package org.apache.spark.storage.memory;

import org.apache.spark.storage.BlockId;
import org.apache.spark.storage.StorageLevel;
import org.apache.spark.util.io.ChunkedByteBuffer;
import scala.Function0;
import scala.reflect.ClassTag;
import scala.reflect.ScalaSignature;
import scala.util.Either;

@ScalaSignature(
   bytes = "\u0006\u0001E3\u0001\"\u0001\u0002\u0011\u0002G\u0005A\u0001\u0004\u0002\u0015\u00052|7m[#wS\u000e$\u0018n\u001c8IC:$G.\u001a:\u000b\u0005\r!\u0011AB7f[>\u0014\u0018P\u0003\u0002\u0006\r\u000591\u000f^8sC\u001e,'BA\u0004\t\u0003\u0015\u0019\b/\u0019:l\u0015\tI!\"\u0001\u0004ba\u0006\u001c\u0007.\u001a\u0006\u0002\u0017\u0005\u0019qN]4\u0014\u0005\u0001i\u0001C\u0001\b\u0012\u001b\u0005y!\"\u0001\t\u0002\u000bM\u001c\u0017\r\\1\n\u0005Iy!AB!osJ+g\r\u0003\u0004\u0015\u0001\u0019\u0005A!F\u0001\u000fIJ|\u0007O\u0012:p[6+Wn\u001c:z+\t1r\u0005F\u0002\u0018aU\"\"\u0001\u0007\u000f\u0011\u0005eQR\"\u0001\u0003\n\u0005m!!\u0001D*u_J\fw-\u001a'fm\u0016d\u0007bB\u000f\u0014\u0003\u0003\u0005\u001daH\u0001\u000bKZLG-\u001a8dK\u0012\n4\u0001\u0001\t\u0004A\r*S\"A\u0011\u000b\u0005\tz\u0011a\u0002:fM2,7\r^\u0005\u0003I\u0005\u0012\u0001b\u00117bgN$\u0016m\u001a\t\u0003M\u001db\u0001\u0001B\u0003)'\t\u0007\u0011FA\u0001U#\tQS\u0006\u0005\u0002\u000fW%\u0011Af\u0004\u0002\b\u001d>$\b.\u001b8h!\tqa&\u0003\u00020\u001f\t\u0019\u0011I\\=\t\u000bE\u001a\u0002\u0019\u0001\u001a\u0002\u000f\tdwnY6JIB\u0011\u0011dM\u0005\u0003i\u0011\u0011qA\u00117pG.LE\rC\u00037'\u0001\u0007q'\u0001\u0003eCR\f\u0007c\u0001\b9u%\u0011\u0011h\u0004\u0002\n\rVt7\r^5p]B\u0002BaO\"G\u0013:\u0011A(\u0011\b\u0003{\u0001k\u0011A\u0010\u0006\u0003\u007fy\ta\u0001\u0010:p_Rt\u0014\"\u0001\t\n\u0005\t{\u0011a\u00029bG.\fw-Z\u0005\u0003\t\u0016\u0013a!R5uQ\u0016\u0014(B\u0001\"\u0010!\rqq)J\u0005\u0003\u0011>\u0011Q!\u0011:sCf\u0004\"AS(\u000e\u0003-S!\u0001T'\u0002\u0005%|'B\u0001(\u0007\u0003\u0011)H/\u001b7\n\u0005A[%!E\"ik:\\W\r\u001a\"zi\u0016\u0014UO\u001a4fe\u0002"
)
public interface BlockEvictionHandler {
   <T> StorageLevel dropFromMemory(BlockId var1, Function0<Either<Object, ChunkedByteBuffer>> var2, ClassTag<T> var3);
}
