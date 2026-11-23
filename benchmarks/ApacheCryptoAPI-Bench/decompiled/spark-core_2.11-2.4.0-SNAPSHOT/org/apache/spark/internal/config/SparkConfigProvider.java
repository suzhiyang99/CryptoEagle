package org.apache.spark.internal.config;

import java.util.Map;
import org.apache.spark.SparkConf$;
import scala.None$;
import scala.Option;
import scala.Option$;
import scala.Serializable;
import scala.reflect.ScalaSignature;

@ScalaSignature(
   bytes = "\u0006\u0001Y2Q!\u0001\u0002\u0001\r1\u00111c\u00159be.\u001cuN\u001c4jOB\u0013xN^5eKJT!a\u0001\u0003\u0002\r\r|gNZ5h\u0015\t)a!\u0001\u0005j]R,'O\\1m\u0015\t9\u0001\"A\u0003ta\u0006\u00148N\u0003\u0002\n\u0015\u00051\u0011\r]1dQ\u0016T\u0011aC\u0001\u0004_J<7c\u0001\u0001\u000e'A\u0011a\"E\u0007\u0002\u001f)\t\u0001#A\u0003tG\u0006d\u0017-\u0003\u0002\u0013\u001f\t1\u0011I\\=SK\u001a\u0004\"\u0001F\u000b\u000e\u0003\tI!A\u0006\u0002\u0003\u001d\r{gNZ5h!J|g/\u001b3fe\"A\u0001\u0004\u0001B\u0001B\u0003%!$\u0001\u0003d_:47\u0001\u0001\t\u00057\u0001\u0012#%D\u0001\u001d\u0015\tib$\u0001\u0003vi&d'\"A\u0010\u0002\t)\fg/Y\u0005\u0003Cq\u00111!T1q!\t\u0019cE\u0004\u0002\u000fI%\u0011QeD\u0001\u0007!J,G-\u001a4\n\u0005\u001dB#AB*ue&twM\u0003\u0002&\u001f!)!\u0006\u0001C\u0001W\u00051A(\u001b8jiz\"\"\u0001L\u0017\u0011\u0005Q\u0001\u0001\"\u0002\r*\u0001\u0004Q\u0002\"B\u0018\u0001\t\u0003\u0002\u0014aA4fiR\u0011\u0011\u0007\u000e\t\u0004\u001dI\u0012\u0013BA\u001a\u0010\u0005\u0019y\u0005\u000f^5p]\")QG\fa\u0001E\u0005\u00191.Z="
)
public class SparkConfigProvider implements ConfigProvider {
   public final Map<String, String> org$apache$spark$internal$config$SparkConfigProvider$$conf;

   public Option<String> get(String key) {
      return (Option<String>)(key.startsWith("spark.") ? Option$.MODULE$.apply(this.org$apache$spark$internal$config$SparkConfigProvider$$conf.get(key)).orElse(new Serializable(this, key) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final SparkConfigProvider $outer;
         private final String key$1;

         public final Option<String> apply() {
            return SparkConf$.MODULE$.getDeprecatedConfig(this.key$1, this.$outer.org$apache$spark$internal$config$SparkConfigProvider$$conf);
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
               this.key$1 = key$1;
            }
         }
      }) : None$.MODULE$);
   }

   public SparkConfigProvider(Map<String, String> conf) {
      this.org$apache$spark$internal$config$SparkConfigProvider$$conf = conf;
   }
}
