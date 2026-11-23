package org.apache.spark.status;

import org.apache.spark.util.kvstore.KVIndex;
import scala.collection.immutable.Set;
import scala.reflect.ScalaSignature;

@ScalaSignature(
   bytes = "\u0006\u0001\r3Q!\u0001\u0002\u0001\t)\u0011\u0001\u0002U8pY\u0012\u000bG/\u0019\u0006\u0003\u0007\u0011\taa\u001d;biV\u001c(BA\u0003\u0007\u0003\u0015\u0019\b/\u0019:l\u0015\t9\u0001\"\u0001\u0004ba\u0006\u001c\u0007.\u001a\u0006\u0002\u0013\u0005\u0019qN]4\u0014\u0005\u0001Y\u0001C\u0001\u0007\u0010\u001b\u0005i!\"\u0001\b\u0002\u000bM\u001c\u0017\r\\1\n\u0005Ai!AB!osJ+g\r\u0003\u0005\u0013\u0001\t\u0015\r\u0011\"\u0001\u0015\u0003\u0011q\u0017-\\3\u0004\u0001U\tQ\u0003\u0005\u0002\u001739\u0011AbF\u0005\u000315\ta\u0001\u0015:fI\u00164\u0017B\u0001\u000e\u001c\u0005\u0019\u0019FO]5oO*\u0011\u0001$\u0004\u0015\u0003#uQ#A\b\u0014\u0011\u0005}!S\"\u0001\u0011\u000b\u0005\u0005\u0012\u0013aB6wgR|'/\u001a\u0006\u0003G\u0011\tA!\u001e;jY&\u0011Q\u0005\t\u0002\b\u0017ZKe\u000eZ3yW\u00059\u0003C\u0001\u0015.\u001b\u0005I#B\u0001\u0016,\u0003\u0011iW\r^1\u000b\u00051j\u0011AC1o]>$\u0018\r^5p]&\u0011a&\u000b\u0002\u0007O\u0016$H/\u001a:\t\u0011A\u0002!\u0011!Q\u0001\nU\tQA\\1nK\u0002B\u0001B\r\u0001\u0003\u0006\u0004%\taM\u0001\tgR\fw-Z%egV\tA\u0007E\u0002\u0017k]J!AN\u000e\u0003\u0007M+G\u000f\u0005\u0002\rq%\u0011\u0011(\u0004\u0002\u0004\u0013:$\b\u0002C\u001e\u0001\u0005\u0003\u0005\u000b\u0011\u0002\u001b\u0002\u0013M$\u0018mZ3JIN\u0004\u0003\"B\u001f\u0001\t\u0003q\u0014A\u0002\u001fj]&$h\bF\u0002@\u0003\n\u0003\"\u0001\u0011\u0001\u000e\u0003\tAQA\u0005\u001fA\u0002UAQA\r\u001fA\u0002Q\u0002"
)
public class PoolData {
   private final String name;
   private final Set<Object> stageIds;

   @KVIndex
   public String name() {
      return this.name;
   }

   public Set<Object> stageIds() {
      return this.stageIds;
   }

   public PoolData(String name, Set<Object> stageIds) {
      this.name = name;
      this.stageIds = stageIds;
   }
}
