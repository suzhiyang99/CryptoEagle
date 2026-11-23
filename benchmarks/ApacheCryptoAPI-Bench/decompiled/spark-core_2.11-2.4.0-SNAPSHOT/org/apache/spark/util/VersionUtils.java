package org.apache.spark.util;

import scala.Tuple2;
import scala.reflect.ScalaSignature;

@ScalaSignature(
   bytes = "\u0006\u0001}:a!\u0001\u0002\t\u0002\u0011Q\u0011\u0001\u0004,feNLwN\\+uS2\u001c(BA\u0002\u0005\u0003\u0011)H/\u001b7\u000b\u0005\u00151\u0011!B:qCJ\\'BA\u0004\t\u0003\u0019\t\u0007/Y2iK*\t\u0011\"A\u0002pe\u001e\u0004\"a\u0003\u0007\u000e\u0003\t1a!\u0004\u0002\t\u0002\u0011q!\u0001\u0004,feNLwN\\+uS2\u001c8C\u0001\u0007\u0010!\t\u00012#D\u0001\u0012\u0015\u0005\u0011\u0012!B:dC2\f\u0017B\u0001\u000b\u0012\u0005\u0019\te.\u001f*fM\")a\u0003\u0004C\u00011\u00051A(\u001b8jiz\u001a\u0001\u0001F\u0001\u000b\u0011\u001dQBB1A\u0005\nm\tq\"\\1k_Jl\u0015N\\8s%\u0016<W\r_\u000b\u00029A\u0011Q$I\u0007\u0002=)\u0011q\u0004I\u0001\t[\u0006$8\r[5oO*\u00111!E\u0005\u0003Ey\u0011QAU3hKbDa\u0001\n\u0007!\u0002\u0013a\u0012\u0001E7bU>\u0014X*\u001b8peJ+w-\u001a=!\u0011\u00151C\u0002\"\u0001(\u00031i\u0017M[8s-\u0016\u00148/[8o)\tA3\u0006\u0005\u0002\u0011S%\u0011!&\u0005\u0002\u0004\u0013:$\b\"\u0002\u0017&\u0001\u0004i\u0013\u0001D:qCJ\\g+\u001a:tS>t\u0007C\u0001\u00182\u001d\t\u0001r&\u0003\u00021#\u00051\u0001K]3eK\u001aL!AM\u001a\u0003\rM#(/\u001b8h\u0015\t\u0001\u0014\u0003C\u00036\u0019\u0011\u0005a'\u0001\u0007nS:|'OV3sg&|g\u000e\u0006\u0002)o!)A\u0006\u000ea\u0001[!)\u0011\b\u0004C\u0001u\u0005\tR.\u00196pe6Kgn\u001c:WKJ\u001c\u0018n\u001c8\u0015\u0005mr\u0004\u0003\u0002\t=Q!J!!P\t\u0003\rQ+\b\u000f\\33\u0011\u0015a\u0003\b1\u0001.\u0001"
)
public final class VersionUtils {
   public static Tuple2<Object, Object> majorMinorVersion(String var0) {
      return VersionUtils$.MODULE$.majorMinorVersion(var0);
   }

   public static int minorVersion(String var0) {
      return VersionUtils$.MODULE$.minorVersion(var0);
   }

   public static int majorVersion(String var0) {
      return VersionUtils$.MODULE$.majorVersion(var0);
   }
}
