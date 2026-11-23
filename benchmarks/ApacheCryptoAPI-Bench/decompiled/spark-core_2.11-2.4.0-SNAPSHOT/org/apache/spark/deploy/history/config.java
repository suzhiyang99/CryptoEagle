package org.apache.spark.deploy.history;

import org.apache.spark.internal.config.ConfigEntry;
import org.apache.spark.internal.config.OptionalConfigEntry;
import scala.reflect.ScalaSignature;

@ScalaSignature(
   bytes = "\u0006\u0001a;a!\u0001\u0002\t\u0002\u0019a\u0011AB2p]\u001aLwM\u0003\u0002\u0004\t\u00059\u0001.[:u_JL(BA\u0003\u0007\u0003\u0019!W\r\u001d7ps*\u0011q\u0001C\u0001\u0006gB\f'o\u001b\u0006\u0003\u0013)\ta!\u00199bG\",'\"A\u0006\u0002\u0007=\u0014x\r\u0005\u0002\u000e\u001d5\t!A\u0002\u0004\u0010\u0005!\u0005a\u0001\u0005\u0002\u0007G>tg-[4\u0014\u00059\t\u0002C\u0001\n\u0016\u001b\u0005\u0019\"\"\u0001\u000b\u0002\u000bM\u001c\u0017\r\\1\n\u0005Y\u0019\"AB!osJ+g\rC\u0003\u0019\u001d\u0011\u0005!$\u0001\u0004=S:LGOP\u0002\u0001)\u0005a\u0001b\u0002\u000f\u000f\u0005\u0004%\t!H\u0001\u0010\t\u00163\u0015)\u0016'U?2{ui\u0018#J%V\ta\u0004\u0005\u0002 I5\t\u0001E\u0003\u0002\"E\u0005!A.\u00198h\u0015\u0005\u0019\u0013\u0001\u00026bm\u0006L!!\n\u0011\u0003\rM#(/\u001b8h\u0011\u00199c\u0002)A\u0005=\u0005\u0001B)\u0012$B+2#v\fT(H?\u0012K%\u000b\t\u0005\bS9\u0011\r\u0011\"\u0001+\u00035)e+\u0012(U?2{ui\u0018#J%V\t1\u0006E\u0002-aIj\u0011!\f\u0006\u0003\u00039R!a\f\u0004\u0002\u0011%tG/\u001a:oC2L!!M\u0017\u0003\u0017\r{gNZ5h\u000b:$(/\u001f\t\u0003gYr!A\u0005\u001b\n\u0005U\u001a\u0012A\u0002)sK\u0012,g-\u0003\u0002&o)\u0011Qg\u0005\u0005\u0007s9\u0001\u000b\u0011B\u0016\u0002\u001d\u00153VI\u0014+`\u0019>;u\fR%SA!91H\u0004b\u0001\n\u0003a\u0014!D'B1~cujR0B\u000f\u0016{6+F\u0001>!\ra\u0003G\u0010\t\u0003%}J!\u0001Q\n\u0003\t1{gn\u001a\u0005\u0007\u0005:\u0001\u000b\u0011B\u001f\u0002\u001d5\u000b\u0005l\u0018'P\u000f~\u000bu)R0TA!9AI\u0004b\u0001\n\u0003)\u0015a\u0004'P\u0007\u0006cul\u0015+P%\u0016{F)\u0013*\u0016\u0003\u0019\u00032\u0001L$3\u0013\tAUFA\nPaRLwN\\1m\u0007>tg-[4F]R\u0014\u0018\u0010\u0003\u0004K\u001d\u0001\u0006IAR\u0001\u0011\u0019>\u001b\u0015\tT0T)>\u0013Vi\u0018#J%\u0002Bq\u0001\u0014\bC\u0002\u0013\u0005A(\u0001\u000bN\u0003b{FjT\"B\u0019~#\u0015jU&`+N\u000bu)\u0012\u0005\u0007\u001d:\u0001\u000b\u0011B\u001f\u0002+5\u000b\u0005l\u0018'P\u0007\u0006cu\fR%T\u0017~+6+Q$FA!9\u0001K\u0004b\u0001\n\u0003\t\u0016A\u0006%J'R{%+W0T\u000bJ3VIU0V\u0013~\u0003vJ\u0015+\u0016\u0003I\u00032\u0001\f\u0019T!\t\u0011B+\u0003\u0002V'\t\u0019\u0011J\u001c;\t\r]s\u0001\u0015!\u0003S\u0003]A\u0015j\u0015+P%f{6+\u0012*W\u000bJ{V+S0Q\u001fJ#\u0006\u0005"
)
public final class config {
   public static ConfigEntry<Object> HISTORY_SERVER_UI_PORT() {
      return config$.MODULE$.HISTORY_SERVER_UI_PORT();
   }

   public static ConfigEntry<Object> MAX_LOCAL_DISK_USAGE() {
      return config$.MODULE$.MAX_LOCAL_DISK_USAGE();
   }

   public static OptionalConfigEntry<String> LOCAL_STORE_DIR() {
      return config$.MODULE$.LOCAL_STORE_DIR();
   }

   public static ConfigEntry<Object> MAX_LOG_AGE_S() {
      return config$.MODULE$.MAX_LOG_AGE_S();
   }

   public static ConfigEntry<String> EVENT_LOG_DIR() {
      return config$.MODULE$.EVENT_LOG_DIR();
   }

   public static String DEFAULT_LOG_DIR() {
      return config$.MODULE$.DEFAULT_LOG_DIR();
   }
}
