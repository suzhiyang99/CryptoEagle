package org.apache.spark.util.random;

import org.apache.spark.annotation.DeveloperApi;
import scala.reflect.ScalaSignature;

@DeveloperApi
@ScalaSignature(
   bytes = "\u0006\u0001\u00192q!\u0001\u0002\u0011\u0002G\u0005QB\u0001\u0007Qg\u0016,Hm\u001c:b]\u0012|WN\u0003\u0002\u0004\t\u00051!/\u00198e_6T!!\u0002\u0004\u0002\tU$\u0018\u000e\u001c\u0006\u0003\u000f!\tQa\u001d9be.T!!\u0003\u0006\u0002\r\u0005\u0004\u0018m\u00195f\u0015\u0005Y\u0011aA8sO\u000e\u00011C\u0001\u0001\u000f!\ty!#D\u0001\u0011\u0015\u0005\t\u0012!B:dC2\f\u0017BA\n\u0011\u0005\u0019\te.\u001f*fM\")Q\u0003\u0001D\u0001-\u000591/\u001a;TK\u0016$GCA\f\u001b!\ty\u0001$\u0003\u0002\u001a!\t!QK\\5u\u0011\u0015YB\u00031\u0001\u001d\u0003\u0011\u0019X-\u001a3\u0011\u0005=i\u0012B\u0001\u0010\u0011\u0005\u0011auN\\4)\u0005\u0001\u0001\u0003CA\u0011%\u001b\u0005\u0011#BA\u0012\u0007\u0003)\tgN\\8uCRLwN\\\u0005\u0003K\t\u0012A\u0002R3wK2|\u0007/\u001a:Ba&\u0004"
)
public interface Pseudorandom {
   void setSeed(long var1);
}
