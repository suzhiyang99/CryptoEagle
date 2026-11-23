package org.apache.spark.internal.config;

import scala.Option;
import scala.reflect.ScalaSignature;

@ScalaSignature(
   bytes = "\u0006\u0001-2Q!\u0001\u0002\u0001\r1\u00111\"\u00128w!J|g/\u001b3fe*\u00111\u0001B\u0001\u0007G>tg-[4\u000b\u0005\u00151\u0011\u0001C5oi\u0016\u0014h.\u00197\u000b\u0005\u001dA\u0011!B:qCJ\\'BA\u0005\u000b\u0003\u0019\t\u0007/Y2iK*\t1\"A\u0002pe\u001e\u001c2\u0001A\u0007\u0014!\tq\u0011#D\u0001\u0010\u0015\u0005\u0001\u0012!B:dC2\f\u0017B\u0001\n\u0010\u0005\u0019\te.\u001f*fMB\u0011A#F\u0007\u0002\u0005%\u0011aC\u0001\u0002\u000f\u0007>tg-[4Qe>4\u0018\u000eZ3s\u0011\u0015A\u0002\u0001\"\u0001\u001b\u0003\u0019a\u0014N\\5u}\r\u0001A#A\u000e\u0011\u0005Q\u0001\u0001\"B\u000f\u0001\t\u0003r\u0012aA4fiR\u0011q$\u000b\t\u0004\u001d\u0001\u0012\u0013BA\u0011\u0010\u0005\u0019y\u0005\u000f^5p]B\u00111E\n\b\u0003\u001d\u0011J!!J\b\u0002\rA\u0013X\rZ3g\u0013\t9\u0003F\u0001\u0004TiJLgn\u001a\u0006\u0003K=AQA\u000b\u000fA\u0002\t\n1a[3z\u0001"
)
public class EnvProvider implements ConfigProvider {
   public Option<String> get(String key) {
      return scala.sys.package$.MODULE$.env().get(key);
   }
}
