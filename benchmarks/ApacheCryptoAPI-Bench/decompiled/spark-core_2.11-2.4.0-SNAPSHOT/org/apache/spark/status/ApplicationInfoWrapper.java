package org.apache.spark.status;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.apache.spark.status.api.v1.ApplicationInfo;
import org.apache.spark.util.kvstore.KVIndex;
import scala.reflect.ScalaSignature;

@ScalaSignature(
   bytes = "\u0006\u0001\u00153Q!\u0001\u0002\u0001\t)\u0011a#\u00119qY&\u001c\u0017\r^5p]&sgm\\,sCB\u0004XM\u001d\u0006\u0003\u0007\u0011\taa\u001d;biV\u001c(BA\u0003\u0007\u0003\u0015\u0019\b/\u0019:l\u0015\t9\u0001\"\u0001\u0004ba\u0006\u001c\u0007.\u001a\u0006\u0002\u0013\u0005\u0019qN]4\u0014\u0005\u0001Y\u0001C\u0001\u0007\u0010\u001b\u0005i!\"\u0001\b\u0002\u000bM\u001c\u0017\r\\1\n\u0005Ai!AB!osJ+g\r\u0003\u0005\u0013\u0001\t\u0015\r\u0011\"\u0001\u0015\u0003\u0011IgNZ8\u0004\u0001U\tQ\u0003\u0005\u0002\u001775\tqC\u0003\u0002\u00193\u0005\u0011a/\r\u0006\u00035\t\t1!\u00199j\u0013\tarCA\bBaBd\u0017nY1uS>t\u0017J\u001c4p\u0011!q\u0002A!A!\u0002\u0013)\u0012!B5oM>\u0004\u0003\"\u0002\u0011\u0001\t\u0003\t\u0013A\u0002\u001fj]&$h\b\u0006\u0002#IA\u00111\u0005A\u0007\u0002\u0005!)!c\ba\u0001+!)a\u0005\u0001C\u0001O\u0005\u0011\u0011\u000eZ\u000b\u0002QA\u0011\u0011\u0006\f\b\u0003\u0019)J!aK\u0007\u0002\rA\u0013X\rZ3g\u0013\ticF\u0001\u0004TiJLgn\u001a\u0006\u0003W5A#!\n\u0019\u0011\u0005E2T\"\u0001\u001a\u000b\u0005M\"\u0014aB6wgR|'/\u001a\u0006\u0003k\u0011\tA!\u001e;jY&\u0011qG\r\u0002\b\u0017ZKe\u000eZ3yQ\t)\u0013\b\u0005\u0002;\u00076\t1H\u0003\u0002={\u0005Q\u0011M\u001c8pi\u0006$\u0018n\u001c8\u000b\u0005yz\u0014a\u00026bG.\u001cxN\u001c\u0006\u0003\u0001\u0006\u000b\u0011BZ1ti\u0016\u0014\b0\u001c7\u000b\u0003\t\u000b1aY8n\u0013\t!5H\u0001\u0006Kg>t\u0017j\u001a8pe\u0016\u0004"
)
public class ApplicationInfoWrapper {
   private final ApplicationInfo info;

   public ApplicationInfo info() {
      return this.info;
   }

   @JsonIgnore
   @KVIndex
   public String id() {
      return this.info().id();
   }

   public ApplicationInfoWrapper(ApplicationInfo info) {
      this.info = info;
   }
}
