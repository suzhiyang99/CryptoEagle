package org.apache.spark.shuffle.sort;

import org.apache.spark.ShuffleDependency;
import org.apache.spark.shuffle.BaseShuffleHandle;
import scala.reflect.ScalaSignature;

@ScalaSignature(
   bytes = "\u0006\u0001y2Q!\u0001\u0002\u0001\r1\u0011ADQ=qCN\u001cX*\u001a:hKN{'\u000f^*ik\u001a4G.\u001a%b]\u0012dWM\u0003\u0002\u0004\t\u0005!1o\u001c:u\u0015\t)a!A\u0004tQV4g\r\\3\u000b\u0005\u001dA\u0011!B:qCJ\\'BA\u0005\u000b\u0003\u0019\t\u0007/Y2iK*\t1\"A\u0002pe\u001e,2!\u0004\u000b#'\t\u0001a\u0002E\u0003\u0010!I\t\u0013%D\u0001\u0005\u0013\t\tBAA\tCCN,7\u000b[;gM2,\u0007*\u00198eY\u0016\u0004\"a\u0005\u000b\r\u0001\u0011)Q\u0003\u0001b\u0001/\t\t1j\u0001\u0001\u0012\u0005aq\u0002CA\r\u001d\u001b\u0005Q\"\"A\u000e\u0002\u000bM\u001c\u0017\r\\1\n\u0005uQ\"a\u0002(pi\"Lgn\u001a\t\u00033}I!\u0001\t\u000e\u0003\u0007\u0005s\u0017\u0010\u0005\u0002\u0014E\u0011)1\u0005\u0001b\u0001/\t\ta\u000bC\u0005&\u0001\t\u0005\t\u0015!\u0003'S\u0005I1\u000f[;gM2,\u0017\n\u001a\t\u00033\u001dJ!\u0001\u000b\u000e\u0003\u0007%sG/\u0003\u0002&U%\u00111\u0006\u0002\u0002\u000e'\",hM\u001a7f\u0011\u0006tG\r\\3\t\u00135\u0002!\u0011!Q\u0001\n\u0019r\u0013a\u00028v[6\u000b\u0007o]\u0005\u0003[AA\u0011\u0002\r\u0001\u0003\u0002\u0003\u0006I!M\u001b\u0002\u0015\u0011,\u0007/\u001a8eK:\u001c\u0017\u0010E\u00033gI\t\u0013%D\u0001\u0007\u0013\t!dAA\tTQV4g\r\\3EKB,g\u000eZ3oGfL!\u0001\r\t\t\u000b]\u0002A\u0011\u0001\u001d\u0002\rqJg.\u001b;?)\u0011I4\bP\u001f\u0011\ti\u0002!#I\u0007\u0002\u0005!)QE\u000ea\u0001M!)QF\u000ea\u0001M!)\u0001G\u000ea\u0001c\u0001"
)
public class BypassMergeSortShuffleHandle<K, V> extends BaseShuffleHandle<K, V, V> {
   public BypassMergeSortShuffleHandle(int shuffleId, int numMaps, ShuffleDependency<K, V, V> dependency) {
      super(shuffleId, numMaps, dependency);
   }
}
