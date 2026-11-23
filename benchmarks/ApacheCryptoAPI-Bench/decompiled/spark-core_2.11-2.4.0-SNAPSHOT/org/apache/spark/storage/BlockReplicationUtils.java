package org.apache.spark.storage;

import scala.collection.Seq;
import scala.collection.immutable.List;
import scala.reflect.ScalaSignature;
import scala.util.Random;

@ScalaSignature(
   bytes = "\u0006\u00019;Q!\u0001\u0002\t\u0002-\tQC\u00117pG.\u0014V\r\u001d7jG\u0006$\u0018n\u001c8Vi&d7O\u0003\u0002\u0004\t\u000591\u000f^8sC\u001e,'BA\u0003\u0007\u0003\u0015\u0019\b/\u0019:l\u0015\t9\u0001\"\u0001\u0004ba\u0006\u001c\u0007.\u001a\u0006\u0002\u0013\u0005\u0019qN]4\u0004\u0001A\u0011A\"D\u0007\u0002\u0005\u0019)aB\u0001E\u0001\u001f\t)\"\t\\8dWJ+\u0007\u000f\\5dCRLwN\\+uS2\u001c8CA\u0007\u0011!\t\tB#D\u0001\u0013\u0015\u0005\u0019\u0012!B:dC2\f\u0017BA\u000b\u0013\u0005\u0019\te.\u001f*fM\")q#\u0004C\u00011\u00051A(\u001b8jiz\"\u0012a\u0003\u0005\u000655!IaG\u0001\rO\u0016$8+Y7qY\u0016LEm\u001d\u000b\u00059-js\u0006E\u0002\u001eK!r!AH\u0012\u000f\u0005}\u0011S\"\u0001\u0011\u000b\u0005\u0005R\u0011A\u0002\u001fs_>$h(C\u0001\u0014\u0013\t!##A\u0004qC\u000e\\\u0017mZ3\n\u0005\u0019:#\u0001\u0002'jgRT!\u0001\n\n\u0011\u0005EI\u0013B\u0001\u0016\u0013\u0005\rIe\u000e\u001e\u0005\u0006Ye\u0001\r\u0001K\u0001\u0002]\")a&\u0007a\u0001Q\u0005\tQ\u000eC\u000313\u0001\u0007\u0011'A\u0001s!\t\u0011T'D\u00014\u0015\t!$#\u0001\u0003vi&d\u0017B\u0001\u001c4\u0005\u0019\u0011\u0016M\u001c3p[\")\u0001(\u0004C\u0001s\u0005yq-\u001a;SC:$w.\\*b[BdW-\u0006\u0002;}Q!1h\u0012'N!\riR\u0005\u0010\t\u0003{yb\u0001\u0001B\u0003@o\t\u0007\u0001IA\u0001U#\t\tE\t\u0005\u0002\u0012\u0005&\u00111I\u0005\u0002\b\u001d>$\b.\u001b8h!\t\tR)\u0003\u0002G%\t\u0019\u0011I\\=\t\u000b!;\u0004\u0019A%\u0002\u000b\u0015dW-\\:\u0011\u0007uQE(\u0003\u0002LO\t\u00191+Z9\t\u000b9:\u0004\u0019\u0001\u0015\t\u000bA:\u0004\u0019A\u0019"
)
public final class BlockReplicationUtils {
   public static <T> List<T> getRandomSample(Seq<T> var0, int var1, Random var2) {
      return BlockReplicationUtils$.MODULE$.<T>getRandomSample(var0, var1, var2);
   }
}
