package org.apache.spark.internal.config;

import java.util.Map;
import scala.Option;
import scala.Option$;
import scala.reflect.ScalaSignature;

@ScalaSignature(
   bytes = "\u0006\u0001Y2Q!\u0001\u0002\u0001\r1\u00111\"T1q!J|g/\u001b3fe*\u00111\u0001B\u0001\u0007G>tg-[4\u000b\u0005\u00151\u0011\u0001C5oi\u0016\u0014h.\u00197\u000b\u0005\u001dA\u0011!B:qCJ\\'BA\u0005\u000b\u0003\u0019\t\u0007/Y2iK*\t1\"A\u0002pe\u001e\u001c2\u0001A\u0007\u0014!\tq\u0011#D\u0001\u0010\u0015\u0005\u0001\u0012!B:dC2\f\u0017B\u0001\n\u0010\u0005\u0019\te.\u001f*fMB\u0011A#F\u0007\u0002\u0005%\u0011aC\u0001\u0002\u000f\u0007>tg-[4Qe>4\u0018\u000eZ3s\u0011!A\u0002A!A!\u0002\u0013Q\u0012\u0001B2p]\u001a\u001c\u0001\u0001\u0005\u0003\u001cA\t\u0012S\"\u0001\u000f\u000b\u0005uq\u0012\u0001B;uS2T\u0011aH\u0001\u0005U\u00064\u0018-\u0003\u0002\"9\t\u0019Q*\u00199\u0011\u0005\r2cB\u0001\b%\u0013\t)s\"\u0001\u0004Qe\u0016$WMZ\u0005\u0003O!\u0012aa\u0015;sS:<'BA\u0013\u0010\u0011\u0015Q\u0003\u0001\"\u0001,\u0003\u0019a\u0014N\\5u}Q\u0011A&\f\t\u0003)\u0001AQ\u0001G\u0015A\u0002iAQa\f\u0001\u0005BA\n1aZ3u)\t\tD\u0007E\u0002\u000fe\tJ!aM\b\u0003\r=\u0003H/[8o\u0011\u0015)d\u00061\u0001#\u0003\rYW-\u001f"
)
public class MapProvider implements ConfigProvider {
   private final Map<String, String> conf;

   public Option<String> get(String key) {
      return Option$.MODULE$.<String>apply(this.conf.get(key));
   }

   public MapProvider(Map<String, String> conf) {
      this.conf = conf;
   }
}
