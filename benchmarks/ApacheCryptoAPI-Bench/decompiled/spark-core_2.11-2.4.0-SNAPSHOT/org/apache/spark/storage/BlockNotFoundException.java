package org.apache.spark.storage;

import scala.Predef$;
import scala.StringContext;
import scala.reflect.ScalaSignature;

@ScalaSignature(
   bytes = "\u0006\u0001)2A!\u0001\u0002\u0001\u0017\t1\"\t\\8dW:{GOR8v]\u0012,\u0005pY3qi&|gN\u0003\u0002\u0004\t\u000591\u000f^8sC\u001e,'BA\u0003\u0007\u0003\u0015\u0019\b/\u0019:l\u0015\t9\u0001\"\u0001\u0004ba\u0006\u001c\u0007.\u001a\u0006\u0002\u0013\u0005\u0019qN]4\u0004\u0001M\u0011\u0001\u0001\u0004\t\u0003\u001b]q!A\u0004\u000b\u000f\u0005=\u0011R\"\u0001\t\u000b\u0005EQ\u0011A\u0002\u001fs_>$h(C\u0001\u0014\u0003\u0015\u00198-\u00197b\u0013\t)b#A\u0004qC\u000e\\\u0017mZ3\u000b\u0003MI!\u0001G\r\u0003\u0013\u0015C8-\u001a9uS>t'BA\u000b\u0017\u0011!Y\u0002A!A!\u0002\u0013a\u0012a\u00022m_\u000e\\\u0017\n\u001a\t\u0003;\u0005r!AH\u0010\u000e\u0003YI!\u0001\t\f\u0002\rA\u0013X\rZ3g\u0013\t\u00113E\u0001\u0004TiJLgn\u001a\u0006\u0003AYAQ!\n\u0001\u0005\u0002\u0019\na\u0001P5oSRtDCA\u0014*!\tA\u0003!D\u0001\u0003\u0011\u0015YB\u00051\u0001\u001d\u0001"
)
public class BlockNotFoundException extends Exception {
   public BlockNotFoundException(String blockId) {
      super((new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Block ", " not found"})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{blockId})));
   }
}
