package org.apache.spark.rpc;

import org.apache.spark.SparkException;
import scala.Predef$;
import scala.StringContext;
import scala.reflect.ScalaSignature;

@ScalaSignature(
   bytes = "\u0006\u0001\t2Q!\u0001\u0002\u0001\u0005)\u0011AD\u00159d\u000b:$\u0007o\\5oi:{GOR8v]\u0012,\u0005pY3qi&|gN\u0003\u0002\u0004\t\u0005\u0019!\u000f]2\u000b\u0005\u00151\u0011!B:qCJ\\'BA\u0004\t\u0003\u0019\t\u0007/Y2iK*\t\u0011\"A\u0002pe\u001e\u001c\"\u0001A\u0006\u0011\u00051iQ\"\u0001\u0003\n\u00059!!AD*qCJ\\W\t_2faRLwN\u001c\u0005\t!\u0001\u0011\t\u0011)A\u0005%\u0005\u0019QO]5\u0004\u0001A\u00111#\u0007\b\u0003)]i\u0011!\u0006\u0006\u0002-\u0005)1oY1mC&\u0011\u0001$F\u0001\u0007!J,G-\u001a4\n\u0005iY\"AB*ue&twM\u0003\u0002\u0019+!)Q\u0004\u0001C\u0001=\u00051A(\u001b8jiz\"\"aH\u0011\u0011\u0005\u0001\u0002Q\"\u0001\u0002\t\u000bAa\u0002\u0019\u0001\n"
)
public class RpcEndpointNotFoundException extends SparkException {
   public RpcEndpointNotFoundException(String uri) {
      super((new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Cannot find endpoint: ", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{uri})));
   }
}
