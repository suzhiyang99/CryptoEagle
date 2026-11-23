package org.apache.spark;

import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import scala.reflect.ScalaSignature;

@ScalaSignature(
   bytes = "\u0006\u0001E2A!\u0001\u0002\u0005\u0013\tA2\t\\3b]V\u0004H+Y:l/\u0016\f7NU3gKJ,gnY3\u000b\u0005\r!\u0011!B:qCJ\\'BA\u0003\u0007\u0003\u0019\t\u0007/Y2iK*\tq!A\u0002pe\u001e\u001c\u0001a\u0005\u0002\u0001\u0015A\u00191B\u0005\u000b\u000e\u00031Q!!\u0004\b\u0002\u0007I,gM\u0003\u0002\u0010!\u0005!A.\u00198h\u0015\u0005\t\u0012\u0001\u00026bm\u0006L!a\u0005\u0007\u0003\u001b]+\u0017m\u001b*fM\u0016\u0014XM\\2f!\t)\u0002$D\u0001\u0017\u0015\u00059\u0012!B:dC2\f\u0017BA\r\u0017\u0005\u0019\te.\u001f*fM\"A1\u0004\u0001BC\u0002\u0013\u0005A$\u0001\u0003uCN\\W#A\u000f\u0011\u0005yyR\"\u0001\u0002\n\u0005\u0001\u0012!aC\"mK\u0006tW\u000f\u001d+bg.D\u0001B\t\u0001\u0003\u0002\u0003\u0006I!H\u0001\u0006i\u0006\u001c8\u000e\t\u0005\tI\u0001\u0011\t\u0011)A\u0005)\u0005A!/\u001a4fe\u0016tG\u000f\u0003\u0005'\u0001\t\u0005\t\u0015!\u0003(\u00039\u0011XMZ3sK:\u001cW-U;fk\u0016\u00042a\u0003\u0015\u0015\u0013\tICB\u0001\bSK\u001a,'/\u001a8dKF+X-^3\t\u000b-\u0002A\u0011\u0001\u0017\u0002\rqJg.\u001b;?)\u0011icf\f\u0019\u0011\u0005y\u0001\u0001\"B\u000e+\u0001\u0004i\u0002\"\u0002\u0013+\u0001\u0004!\u0002\"\u0002\u0014+\u0001\u00049\u0003"
)
public class CleanupTaskWeakReference extends WeakReference<Object> {
   private final CleanupTask task;

   public CleanupTask task() {
      return this.task;
   }

   public CleanupTaskWeakReference(CleanupTask task, Object referent, ReferenceQueue<Object> referenceQueue) {
      super(referent, referenceQueue);
      this.task = task;
   }
}
