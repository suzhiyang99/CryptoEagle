package org.apache.spark.rpc;

import scala.reflect.ScalaSignature;

@ScalaSignature(
   bytes = "\u0006\u0001e1Q!\u0001\u0002\u0001\u0005)\u0011aC\u00159d\u000b:48\u000b^8qa\u0016$W\t_2faRLwN\u001c\u0006\u0003\u0007\u0011\t1A\u001d9d\u0015\t)a!A\u0003ta\u0006\u00148N\u0003\u0002\b\u0011\u00051\u0011\r]1dQ\u0016T\u0011!C\u0001\u0004_J<7C\u0001\u0001\f!\ta\u0011#D\u0001\u000e\u0015\tqq\"\u0001\u0003mC:<'\"\u0001\t\u0002\t)\fg/Y\u0005\u0003%5\u0011Q#\u00137mK\u001e\fGn\u0015;bi\u0016,\u0005pY3qi&|g\u000eC\u0003\u0015\u0001\u0011\u0005a#\u0001\u0004=S:LGOP\u0002\u0001)\u00059\u0002C\u0001\r\u0001\u001b\u0005\u0011\u0001"
)
public class RpcEnvStoppedException extends IllegalStateException {
   public RpcEnvStoppedException() {
      super("RpcEnv already stopped.");
   }
}
