package org.apache.spark.shuffle;

import org.apache.spark.ShuffleDependency;
import scala.reflect.ScalaSignature;

@ScalaSignature(
   bytes = "\u0006\u0001\u00013Q!\u0001\u0002\u0001\t)\u0011\u0011CQ1tKNCWO\u001a4mK\"\u000bg\u000e\u001a7f\u0015\t\u0019A!A\u0004tQV4g\r\\3\u000b\u0005\u00151\u0011!B:qCJ\\'BA\u0004\t\u0003\u0019\t\u0007/Y2iK*\t\u0011\"A\u0002pe\u001e,Ba\u0003\u00153kM\u0011\u0001\u0001\u0004\t\u0003\u001b9i\u0011AA\u0005\u0003\u001f\t\u0011Qb\u00155vM\u001adW\rS1oI2,\u0007\"C\t\u0001\u0005\u0003\u0005\u000b\u0011B\n\u001a\u0003%\u0019\b.\u001e4gY\u0016LEm\u0001\u0001\u0011\u0005Q9R\"A\u000b\u000b\u0003Y\tQa]2bY\u0006L!\u0001G\u000b\u0003\u0007%sG/\u0003\u0002\u0012\u001d!A1\u0004\u0001BC\u0002\u0013\u0005A$A\u0004ok6l\u0015\r]:\u0016\u0003MA\u0001B\b\u0001\u0003\u0002\u0003\u0006IaE\u0001\t]VlW*\u00199tA!A\u0001\u0005\u0001BC\u0002\u0013\u0005\u0011%\u0001\u0006eKB,g\u000eZ3oGf,\u0012A\t\t\u0006G\u00112\u0013\u0007N\u0007\u0002\t%\u0011Q\u0005\u0002\u0002\u0012'\",hM\u001a7f\t\u0016\u0004XM\u001c3f]\u000eL\bCA\u0014)\u0019\u0001!Q!\u000b\u0001C\u0002)\u0012\u0011aS\t\u0003W9\u0002\"\u0001\u0006\u0017\n\u00055*\"a\u0002(pi\"Lgn\u001a\t\u0003)=J!\u0001M\u000b\u0003\u0007\u0005s\u0017\u0010\u0005\u0002(e\u0011)1\u0007\u0001b\u0001U\t\ta\u000b\u0005\u0002(k\u0011)a\u0007\u0001b\u0001U\t\t1\t\u0003\u00059\u0001\t\u0005\t\u0015!\u0003#\u0003-!W\r]3oI\u0016t7-\u001f\u0011\t\u000bi\u0002A\u0011A\u001e\u0002\rqJg.\u001b;?)\u0011aTHP \u0011\u000b5\u0001a%\r\u001b\t\u000bEI\u0004\u0019A\n\t\u000bmI\u0004\u0019A\n\t\u000b\u0001J\u0004\u0019\u0001\u0012"
)
public class BaseShuffleHandle<K, V, C> extends ShuffleHandle {
   private final int numMaps;
   private final ShuffleDependency<K, V, C> dependency;

   public int numMaps() {
      return this.numMaps;
   }

   public ShuffleDependency<K, V, C> dependency() {
      return this.dependency;
   }

   public BaseShuffleHandle(int shuffleId, int numMaps, ShuffleDependency<K, V, C> dependency) {
      super(shuffleId);
      this.numMaps = numMaps;
      this.dependency = dependency;
   }
}
