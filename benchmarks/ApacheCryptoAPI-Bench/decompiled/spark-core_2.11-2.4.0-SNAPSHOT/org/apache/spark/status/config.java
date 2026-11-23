package org.apache.spark.status;

import org.apache.spark.internal.config.ConfigEntry;
import scala.reflect.ScalaSignature;

@ScalaSignature(
   bytes = "\u0006\u0001);a!\u0001\u0002\t\u0002\u0011Q\u0011AB2p]\u001aLwM\u0003\u0002\u0004\t\u000511\u000f^1ukNT!!\u0002\u0004\u0002\u000bM\u0004\u0018M]6\u000b\u0005\u001dA\u0011AB1qC\u000eDWMC\u0001\n\u0003\ry'o\u001a\t\u0003\u00171i\u0011A\u0001\u0004\u0007\u001b\tA\t\u0001\u0002\b\u0003\r\r|gNZ5h'\taq\u0002\u0005\u0002\u0011'5\t\u0011CC\u0001\u0013\u0003\u0015\u00198-\u00197b\u0013\t!\u0012C\u0001\u0004B]f\u0014VM\u001a\u0005\u0006-1!\t\u0001G\u0001\u0007y%t\u0017\u000e\u001e \u0004\u0001Q\t!\u0002C\u0004\u001b\u0019\t\u0007I\u0011A\u000e\u0002-\u0005\u001b\u0016LT\"`)J\u000b5iS%O\u000f~+e*\u0011\"M\u000b\u0012+\u0012\u0001\b\t\u0004;\u0005\u001aS\"\u0001\u0010\u000b\u0005\u0005y\"B\u0001\u0011\u0005\u0003!Ig\u000e^3s]\u0006d\u0017B\u0001\u0012\u001f\u0005-\u0019uN\u001c4jO\u0016sGO]=\u0011\u0005A!\u0013BA\u0013\u0012\u0005\u001d\u0011un\u001c7fC:Daa\n\u0007!\u0002\u0013a\u0012aF!T3:\u001bu\f\u0016*B\u0007.KejR0F\u001d\u0006\u0013E*\u0012#!\u0011\u001dICB1A\u0005\u0002)\n\u0011\u0004T%W\u000b~+e\nV%U3~+\u0006\u000bR!U\u000b~\u0003VIU%P\tV\t1\u0006E\u0002\u001eC1\u0002\"\u0001E\u0017\n\u00059\n\"\u0001\u0002'p]\u001eDa\u0001\r\u0007!\u0002\u0013Y\u0013A\u0007'J-\u0016{VI\u0014+J)f{V\u000b\u0015#B)\u0016{\u0006+\u0012*J\u001f\u0012\u0003\u0003b\u0002\u001a\r\u0005\u0004%\taM\u0001\u0012\u001b\u0006CvLU#U\u0003&sU\tR0K\u001f\n\u001bV#\u0001\u001b\u0011\u0007u\tS\u0007\u0005\u0002\u0011m%\u0011q'\u0005\u0002\u0004\u0013:$\bBB\u001d\rA\u0003%A'\u0001\nN\u0003b{&+\u0012+B\u0013:+Ei\u0018&P\u0005N\u0003\u0003bB\u001e\r\u0005\u0004%\taM\u0001\u0014\u001b\u0006CvLU#U\u0003&sU\tR0T)\u0006;Ui\u0015\u0005\u0007{1\u0001\u000b\u0011\u0002\u001b\u0002)5\u000b\u0005l\u0018*F)\u0006Ke*\u0012#`'R\u000bu)R*!\u0011\u001dyDB1A\u0005\u0002M\nA$T!Y?J+E+Q%O\u000b\u0012{F+Q*L'~\u0003VIU0T)\u0006;U\t\u0003\u0004B\u0019\u0001\u0006I\u0001N\u0001\u001e\u001b\u0006CvLU#U\u0003&sU\tR0U\u0003N[5k\u0018)F%~\u001bF+Q$FA!91\t\u0004b\u0001\n\u0003\u0019\u0014aG'B1~\u0013V\tV!J\u001d\u0016#u\fR#B\t~+\u0005,R\"V)>\u00136\u000b\u0003\u0004F\u0019\u0001\u0006I\u0001N\u0001\u001d\u001b\u0006CvLU#U\u0003&sU\tR0E\u000b\u0006#u,\u0012-F\u0007V#vJU*!\u0011\u001d9EB1A\u0005\u0002M\nq#T!Y?J+E+Q%O\u000b\u0012{&kT(U?:{E)R*\t\r%c\u0001\u0015!\u00035\u0003ai\u0015\tW0S\u000bR\u000b\u0015JT#E?J{u\nV0O\u001f\u0012+5\u000b\t"
)
public final class config {
   public static ConfigEntry<Object> MAX_RETAINED_ROOT_NODES() {
      return config$.MODULE$.MAX_RETAINED_ROOT_NODES();
   }

   public static ConfigEntry<Object> MAX_RETAINED_DEAD_EXECUTORS() {
      return config$.MODULE$.MAX_RETAINED_DEAD_EXECUTORS();
   }

   public static ConfigEntry<Object> MAX_RETAINED_TASKS_PER_STAGE() {
      return config$.MODULE$.MAX_RETAINED_TASKS_PER_STAGE();
   }

   public static ConfigEntry<Object> MAX_RETAINED_STAGES() {
      return config$.MODULE$.MAX_RETAINED_STAGES();
   }

   public static ConfigEntry<Object> MAX_RETAINED_JOBS() {
      return config$.MODULE$.MAX_RETAINED_JOBS();
   }

   public static ConfigEntry<Object> LIVE_ENTITY_UPDATE_PERIOD() {
      return config$.MODULE$.LIVE_ENTITY_UPDATE_PERIOD();
   }

   public static ConfigEntry<Object> ASYNC_TRACKING_ENABLED() {
      return config$.MODULE$.ASYNC_TRACKING_ENABLED();
   }
}
