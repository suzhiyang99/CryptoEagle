package org.apache.spark.storage;

import org.apache.spark.annotation.DeveloperApi;
import scala.collection.Seq;
import scala.collection.immutable.List;
import scala.collection.mutable.HashSet;
import scala.reflect.ScalaSignature;

@DeveloperApi
@ScalaSignature(
   bytes = "\u0006\u0001\u001d3q!\u0001\u0002\u0011\u0002G\u00051B\u0001\fCY>\u001c7NU3qY&\u001c\u0017\r^5p]B{G.[2z\u0015\t\u0019A!A\u0004ti>\u0014\u0018mZ3\u000b\u0005\u00151\u0011!B:qCJ\\'BA\u0004\t\u0003\u0019\t\u0007/Y2iK*\t\u0011\"A\u0002pe\u001e\u001c\u0001a\u0005\u0002\u0001\u0019A\u0011Q\u0002E\u0007\u0002\u001d)\tq\"A\u0003tG\u0006d\u0017-\u0003\u0002\u0012\u001d\t1\u0011I\\=SK\u001aDQa\u0005\u0001\u0007\u0002Q\t!\u0002\u001d:j_JLG/\u001b>f)\u0019)Re\n\u00177wA\u0019aCH\u0011\u000f\u0005]abB\u0001\r\u001c\u001b\u0005I\"B\u0001\u000e\u000b\u0003\u0019a$o\\8u}%\tq\"\u0003\u0002\u001e\u001d\u00059\u0001/Y2lC\u001e,\u0017BA\u0010!\u0005\u0011a\u0015n\u001d;\u000b\u0005uq\u0001C\u0001\u0012$\u001b\u0005\u0011\u0011B\u0001\u0013\u0003\u00059\u0011En\\2l\u001b\u0006t\u0017mZ3s\u0013\u0012DQA\n\nA\u0002\u0005\naB\u00197pG.l\u0015M\\1hKJLE\rC\u0003)%\u0001\u0007\u0011&A\u0003qK\u0016\u00148\u000fE\u0002\u0017U\u0005J!a\u000b\u0011\u0003\u0007M+\u0017\u000fC\u0003.%\u0001\u0007a&A\tqK\u0016\u00148OU3qY&\u001c\u0017\r^3e)>\u00042a\f\u001b\"\u001b\u0005\u0001$BA\u00193\u0003\u001diW\u000f^1cY\u0016T!a\r\b\u0002\u0015\r|G\u000e\\3di&|g.\u0003\u00026a\t9\u0001*Y:i'\u0016$\b\"B\u001c\u0013\u0001\u0004A\u0014a\u00022m_\u000e\\\u0017\n\u001a\t\u0003EeJ!A\u000f\u0002\u0003\u000f\tcwnY6JI\")AH\u0005a\u0001{\u0005Ya.^7SKBd\u0017nY1t!\tia(\u0003\u0002@\u001d\t\u0019\u0011J\u001c;)\u0005\u0001\t\u0005C\u0001\"F\u001b\u0005\u0019%B\u0001#\u0005\u0003)\tgN\\8uCRLwN\\\u0005\u0003\r\u000e\u0013A\u0002R3wK2|\u0007/\u001a:Ba&\u0004"
)
public interface BlockReplicationPolicy {
   List<BlockManagerId> prioritize(BlockManagerId var1, Seq<BlockManagerId> var2, HashSet<BlockManagerId> var3, BlockId var4, int var5);
}
