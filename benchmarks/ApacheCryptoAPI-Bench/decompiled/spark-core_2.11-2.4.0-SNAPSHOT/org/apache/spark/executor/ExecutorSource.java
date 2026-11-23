package org.apache.spark.executor;

import com.codahale.metrics.Counter;
import com.codahale.metrics.Gauge;
import com.codahale.metrics.MetricRegistry;
import java.util.concurrent.ThreadPoolExecutor;
import org.apache.hadoop.fs.FileSystem;
import org.apache.spark.metrics.source.Source;
import scala.Function1;
import scala.Option;
import scala.Predef$;
import scala.Serializable;
import scala.collection.IterableLike;
import scala.collection.JavaConverters$;
import scala.reflect.ScalaSignature;
import scala.runtime.BoxesRunTime;

@ScalaSignature(
   bytes = "\u0006\u0001\u0005Eg!B\u0001\u0003\u0001\u0011Q!AD#yK\u000e,Ho\u001c:T_V\u00148-\u001a\u0006\u0003\u0007\u0011\t\u0001\"\u001a=fGV$xN\u001d\u0006\u0003\u000b\u0019\tQa\u001d9be.T!a\u0002\u0005\u0002\r\u0005\u0004\u0018m\u00195f\u0015\u0005I\u0011aA8sON\u0019\u0001aC\t\u0011\u00051yQ\"A\u0007\u000b\u00039\tQa]2bY\u0006L!\u0001E\u0007\u0003\r\u0005s\u0017PU3g!\t\u0011r#D\u0001\u0014\u0015\t!R#\u0001\u0004t_V\u00148-\u001a\u0006\u0003-\u0011\tq!\\3ue&\u001c7/\u0003\u0002\u0019'\t11k\\;sG\u0016D\u0001B\u0007\u0001\u0003\u0002\u0003\u0006I\u0001H\u0001\u000bi\"\u0014X-\u00193Q_>d7\u0001\u0001\t\u0003;\u0011j\u0011A\b\u0006\u0003?\u0001\n!bY8oGV\u0014(/\u001a8u\u0015\t\t#%\u0001\u0003vi&d'\"A\u0012\u0002\t)\fg/Y\u0005\u0003Ky\u0011!\u0003\u00165sK\u0006$\u0007k\\8m\u000bb,7-\u001e;pe\"Aq\u0005\u0001B\u0001B\u0003%\u0001&\u0001\u0006fq\u0016\u001cW\u000f^8s\u0013\u0012\u0004\"!\u000b\u0017\u000f\u00051Q\u0013BA\u0016\u000e\u0003\u0019\u0001&/\u001a3fM&\u0011QF\f\u0002\u0007'R\u0014\u0018N\\4\u000b\u0005-j\u0001\"\u0002\u0019\u0001\t\u0003\t\u0014A\u0002\u001fj]&$h\bF\u00023iU\u0002\"a\r\u0001\u000e\u0003\tAQAG\u0018A\u0002qAQaJ\u0018A\u0002!BQa\u000e\u0001\u0005\na\n\u0011BZ5mKN#\u0018\r^:\u0015\u0005eB\u0005c\u0001\u0007;y%\u00111(\u0004\u0002\u0007\u001fB$\u0018n\u001c8\u0011\u0005u*eB\u0001 D\u001b\u0005y$B\u0001!B\u0003\t17O\u0003\u0002C\r\u00051\u0001.\u00193p_BL!\u0001R \u0002\u0015\u0019KG.Z*zgR,W.\u0003\u0002G\u000f\nQ1\u000b^1uSN$\u0018nY:\u000b\u0005\u0011{\u0004\"B%7\u0001\u0004A\u0013AB:dQ\u0016lW\rC\u0003L\u0001\u0011%A*\u0001\fsK\u001eL7\u000f^3s\r&dWmU=ti\u0016l7\u000b^1u+\ti%\rF\u0003OW2t7OE\u0002P#^3A\u0001\u0015&\u0001\u001d\naAH]3gS:,W.\u001a8u}A\u0011!+V\u0007\u0002'*\u0011AKI\u0001\u0005Y\u0006tw-\u0003\u0002W'\n1qJ\u00196fGR\u00042\u0001\u00170a\u001b\u0005I&B\u0001\f[\u0015\tYF,\u0001\u0005d_\u0012\f\u0007.\u00197f\u0015\u0005i\u0016aA2p[&\u0011q,\u0017\u0002\u0006\u000f\u0006,x-\u001a\t\u0003C\nd\u0001\u0001B\u0003d\u0015\n\u0007AMA\u0001U#\t)\u0007\u000e\u0005\u0002\rM&\u0011q-\u0004\u0002\b\u001d>$\b.\u001b8h!\ta\u0011.\u0003\u0002k\u001b\t\u0019\u0011I\\=\t\u000b%S\u0005\u0019\u0001\u0015\t\u000b5T\u0005\u0019\u0001\u0015\u0002\t9\fW.\u001a\u0005\u0006_*\u0003\r\u0001]\u0001\u0002MB!A\"\u001d\u001fa\u0013\t\u0011XBA\u0005Gk:\u001cG/[8oc!)AO\u0013a\u0001A\u0006aA-\u001a4bk2$h+\u00197vK\"9a\u000f\u0001b\u0001\n\u0003:\u0018AD7fiJL7MU3hSN$(/_\u000b\u0002qB\u0011\u0001,_\u0005\u0003uf\u0013a\"T3ue&\u001c'+Z4jgR\u0014\u0018\u0010\u0003\u0004}\u0001\u0001\u0006I\u0001_\u0001\u0010[\u0016$(/[2SK\u001eL7\u000f\u001e:zA!9a\u0010\u0001b\u0001\n\u0003z\u0018AC:pkJ\u001cWMT1nKV\u0011\u0011\u0011\u0001\t\u0004%\u0006\r\u0011BA\u0017T\u0011!\t9\u0001\u0001Q\u0001\n\u0005\u0005\u0011aC:pkJ\u001cWMT1nK\u0002B\u0011\"a\u0003\u0001\u0005\u0004%\t!!\u0004\u0002\u001f5+EKU%D?\u000e\u0003Vk\u0018+J\u001b\u0016+\"!a\u0004\u0011\u0007a\u000b\t\"C\u0002\u0002\u0014e\u0013qaQ8v]R,'\u000f\u0003\u0005\u0002\u0018\u0001\u0001\u000b\u0011BA\b\u0003AiU\t\u0016*J\u0007~\u001b\u0005+V0U\u00136+\u0005\u0005C\u0005\u0002\u001c\u0001\u0011\r\u0011\"\u0001\u0002\u000e\u0005yQ*\u0012+S\u0013\u000e{&+\u0016(`)&kU\t\u0003\u0005\u0002 \u0001\u0001\u000b\u0011BA\b\u0003AiU\t\u0016*J\u0007~\u0013VKT0U\u00136+\u0005\u0005C\u0005\u0002$\u0001\u0011\r\u0011\"\u0001\u0002\u000e\u0005\u0011R*\u0012+S\u0013\u000e{&JV'`\u000f\u000e{F+S'F\u0011!\t9\u0003\u0001Q\u0001\n\u0005=\u0011aE'F)JK5i\u0018&W\u001b~;5i\u0018+J\u001b\u0016\u0003\u0003\"CA\u0016\u0001\t\u0007I\u0011AA\u0007\u0003]iU\t\u0016*J\u0007~#UiU#S\u0013\u0006c\u0015JW#`)&kU\t\u0003\u0005\u00020\u0001\u0001\u000b\u0011BA\b\u0003aiU\t\u0016*J\u0007~#UiU#S\u0013\u0006c\u0015JW#`)&kU\t\t\u0005\n\u0003g\u0001!\u0019!C\u0001\u0003\u001b\t1$T#U%&\u001bu\fR#T\u000bJK\u0015\tT%[\u000b~\u001b\u0005+V0U\u00136+\u0005\u0002CA\u001c\u0001\u0001\u0006I!a\u0004\u000295+EKU%D?\u0012+5+\u0012*J\u00032K%,R0D!V{F+S'FA!I\u00111\b\u0001C\u0002\u0013\u0005\u0011QB\u0001\u001d\u001b\u0016#&+S\"`%\u0016\u001bV\u000b\u0014+`'\u0016\u0013\u0016*\u0011'J5\u0016{F+S'F\u0011!\ty\u0004\u0001Q\u0001\n\u0005=\u0011!H'F)JK5i\u0018*F'VcEkX*F%&\u000bE*\u0013.F?RKU*\u0012\u0011\t\u0013\u0005\r\u0003A1A\u0005\u0002\u00055\u0011AH'F)JK5iX*I+\u001a3E*R0G\u000bR\u001b\u0005jX,B\u0013R{F+S'F\u0011!\t9\u0005\u0001Q\u0001\n\u0005=\u0011aH'F)JK5iX*I+\u001a3E*R0G\u000bR\u001b\u0005jX,B\u0013R{F+S'FA!I\u00111\n\u0001C\u0002\u0013\u0005\u0011QB\u0001\u001a\u001b\u0016#&+S\"`'\"+fI\u0012'F?^\u0013\u0016\nV#`)&kU\t\u0003\u0005\u0002P\u0001\u0001\u000b\u0011BA\b\u0003iiU\t\u0016*J\u0007~\u001b\u0006*\u0016$G\u0019\u0016{vKU%U\u000b~#\u0016*T#!\u0011%\t\u0019\u0006\u0001b\u0001\n\u0003\ti!A\u0010N\u000bR\u0013\u0016jQ0T\u0011V3e\tT#`)>#\u0016\tT0C3R+5k\u0018*F\u0003\u0012C\u0001\"a\u0016\u0001A\u0003%\u0011qB\u0001!\u001b\u0016#&+S\"`'\"+fI\u0012'F?R{E+\u0011'`\u0005f#ViU0S\u000b\u0006#\u0005\u0005C\u0005\u0002\\\u0001\u0011\r\u0011\"\u0001\u0002\u000e\u0005\u0001S*\u0012+S\u0013\u000e{6\u000bS+G\r2+uLU#N\u001fR+uLQ-U\u000bN{&+R!E\u0011!\ty\u0006\u0001Q\u0001\n\u0005=\u0011!I'F)JK5iX*I+\u001a3E*R0S\u000b6{E+R0C3R+5k\u0018*F\u0003\u0012\u0003\u0003\"CA2\u0001\t\u0007I\u0011AA\u0007\u0003!jU\t\u0016*J\u0007~\u001b\u0006*\u0016$G\u0019\u0016{&+R'P)\u0016{&)\u0017+F'~\u0013V)\u0011#`)>{F)S*L\u0011!\t9\u0007\u0001Q\u0001\n\u0005=\u0011!K'F)JK5iX*I+\u001a3E*R0S\u000b6{E+R0C3R+5k\u0018*F\u0003\u0012{FkT0E\u0013N[\u0005\u0005C\u0005\u0002l\u0001\u0011\r\u0011\"\u0001\u0002\u000e\u0005yR*\u0012+S\u0013\u000e{6\u000bS+G\r2+u\fT(D\u00032{&)\u0017+F'~\u0013V)\u0011#\t\u0011\u0005=\u0004\u0001)A\u0005\u0003\u001f\t\u0001%T#U%&\u001bul\u0015%V\r\u001acUi\u0018'P\u0007\u0006cuLQ-U\u000bN{&+R!EA!I\u00111\u000f\u0001C\u0002\u0013\u0005\u0011QB\u0001\u001c\u001b\u0016#&+S\"`'\"+fI\u0012'F?J+5i\u0014*E'~\u0013V)\u0011#\t\u0011\u0005]\u0004\u0001)A\u0005\u0003\u001f\tA$T#U%&\u001bul\u0015%V\r\u001acUi\u0018*F\u0007>\u0013FiU0S\u000b\u0006#\u0005\u0005C\u0005\u0002|\u0001\u0011\r\u0011\"\u0001\u0002\u000e\u0005!S*\u0012+S\u0013\u000e{6\u000bS+G\r2+uLU#N\u001fR+uL\u0011'P\u0007.\u001bvLR#U\u0007\"+E\t\u0003\u0005\u0002\u0000\u0001\u0001\u000b\u0011BA\b\u0003\u0015jU\t\u0016*J\u0007~\u001b\u0006*\u0016$G\u0019\u0016{&+R'P)\u0016{&\tT(D\u0017N{f)\u0012+D\u0011\u0016#\u0005\u0005C\u0005\u0002\u0004\u0002\u0011\r\u0011\"\u0001\u0002\u000e\u0005\u0019S*\u0012+S\u0013\u000e{6\u000bS+G\r2+u\fT(D\u00032{&\tT(D\u0017N{f)\u0012+D\u0011\u0016#\u0005\u0002CAD\u0001\u0001\u0006I!a\u0004\u0002I5+EKU%D?NCUK\u0012$M\u000b~cujQ!M?\ncujQ&T?\u001a+Ek\u0011%F\t\u0002B\u0011\"a#\u0001\u0005\u0004%\t!!\u0004\u000295+EKU%D?NCUK\u0012$M\u000b~\u0013\u0015\fV#T?^\u0013\u0016\n\u0016+F\u001d\"A\u0011q\u0012\u0001!\u0002\u0013\ty!A\u000fN\u000bR\u0013\u0016jQ0T\u0011V3e\tT#`\u0005f#ViU0X%&#F+\u0012(!\u0011%\t\u0019\n\u0001b\u0001\n\u0003\ti!\u0001\u0010N\u000bR\u0013\u0016jQ0T\u0011V3e\tT#`%\u0016\u001buJ\u0015#T?^\u0013\u0016\n\u0016+F\u001d\"A\u0011q\u0013\u0001!\u0002\u0013\ty!A\u0010N\u000bR\u0013\u0016jQ0T\u0011V3e\tT#`%\u0016\u001buJ\u0015#T?^\u0013\u0016\n\u0016+F\u001d\u0002B\u0011\"a'\u0001\u0005\u0004%\t!!\u0004\u0002/5+EKU%D?&s\u0005+\u0016+`\u0005f#ViU0S\u000b\u0006#\u0005\u0002CAP\u0001\u0001\u0006I!a\u0004\u000215+EKU%D?&s\u0005+\u0016+`\u0005f#ViU0S\u000b\u0006#\u0005\u0005C\u0005\u0002$\u0002\u0011\r\u0011\"\u0001\u0002\u000e\u0005IR*\u0012+S\u0013\u000e{\u0016J\u0014)V)~\u0013ViQ(S\tN{&+R!E\u0011!\t9\u000b\u0001Q\u0001\n\u0005=\u0011AG'F)JK5iX%O!V#vLU#D\u001fJ#5k\u0018*F\u0003\u0012\u0003\u0003\"CAV\u0001\t\u0007I\u0011AA\u0007\u0003miU\t\u0016*J\u0007~{U\u000b\u0016)V)~\u0013\u0015\fV#T?^\u0013\u0016\n\u0016+F\u001d\"A\u0011q\u0016\u0001!\u0002\u0013\ty!\u0001\u000fN\u000bR\u0013\u0016jQ0P+R\u0003V\u000bV0C3R+5kX,S\u0013R#VI\u0014\u0011\t\u0013\u0005M\u0006A1A\u0005\u0002\u00055\u0011!H'F)JK5iX(V)B+Fk\u0018*F\u0007>\u0013FiU0X%&#F+\u0012(\t\u0011\u0005]\u0006\u0001)A\u0005\u0003\u001f\ta$T#U%&\u001bulT+U!V#vLU#D\u001fJ#5kX,S\u0013R#VI\u0014\u0011\t\u0013\u0005m\u0006A1A\u0005\u0002\u00055\u0011AE'F)JK5i\u0018*F'VcEkX*J5\u0016C\u0001\"a0\u0001A\u0003%\u0011qB\u0001\u0014\u001b\u0016#&+S\"`%\u0016\u001bV\u000b\u0014+`'&SV\t\t\u0005\n\u0003\u0007\u0004!\u0019!C\u0001\u0003\u001b\t\u0011$T#U%&\u001bu\fR%T\u0017~\u0013\u0015\fV#T?N\u0003\u0016\n\u0014'F\t\"A\u0011q\u0019\u0001!\u0002\u0013\ty!\u0001\u000eN\u000bR\u0013\u0016jQ0E\u0013N[uLQ-U\u000bN{6\u000bU%M\u0019\u0016#\u0005\u0005C\u0005\u0002L\u0002\u0011\r\u0011\"\u0001\u0002\u000e\u0005YR*\u0012+S\u0013\u000e{V*R'P%f{&)\u0017+F'~\u001b\u0006+\u0013'M\u000b\u0012C\u0001\"a4\u0001A\u0003%\u0011qB\u0001\u001d\u001b\u0016#&+S\"`\u001b\u0016kuJU-`\u0005f#ViU0T!&cE*\u0012#!\u0001"
)
public class ExecutorSource implements Source {
   public final ThreadPoolExecutor org$apache$spark$executor$ExecutorSource$$threadPool;
   private final MetricRegistry metricRegistry;
   private final String sourceName;
   private final Counter METRIC_CPU_TIME;
   private final Counter METRIC_RUN_TIME;
   private final Counter METRIC_JVM_GC_TIME;
   private final Counter METRIC_DESERIALIZE_TIME;
   private final Counter METRIC_DESERIALIZE_CPU_TIME;
   private final Counter METRIC_RESULT_SERIALIZE_TIME;
   private final Counter METRIC_SHUFFLE_FETCH_WAIT_TIME;
   private final Counter METRIC_SHUFFLE_WRITE_TIME;
   private final Counter METRIC_SHUFFLE_TOTAL_BYTES_READ;
   private final Counter METRIC_SHUFFLE_REMOTE_BYTES_READ;
   private final Counter METRIC_SHUFFLE_REMOTE_BYTES_READ_TO_DISK;
   private final Counter METRIC_SHUFFLE_LOCAL_BYTES_READ;
   private final Counter METRIC_SHUFFLE_RECORDS_READ;
   private final Counter METRIC_SHUFFLE_REMOTE_BLOCKS_FETCHED;
   private final Counter METRIC_SHUFFLE_LOCAL_BLOCKS_FETCHED;
   private final Counter METRIC_SHUFFLE_BYTES_WRITTEN;
   private final Counter METRIC_SHUFFLE_RECORDS_WRITTEN;
   private final Counter METRIC_INPUT_BYTES_READ;
   private final Counter METRIC_INPUT_RECORDS_READ;
   private final Counter METRIC_OUTPUT_BYTES_WRITTEN;
   private final Counter METRIC_OUTPUT_RECORDS_WRITTEN;
   private final Counter METRIC_RESULT_SIZE;
   private final Counter METRIC_DISK_BYTES_SPILLED;
   private final Counter METRIC_MEMORY_BYTES_SPILLED;

   public Option<FileSystem.Statistics> org$apache$spark$executor$ExecutorSource$$fileStats(String scheme) {
      return ((IterableLike)JavaConverters$.MODULE$.asScalaBufferConverter(FileSystem.getAllStatistics()).asScala()).find(new Serializable(this, scheme) {
         public static final long serialVersionUID = 0L;
         private final String scheme$1;

         public final boolean apply(FileSystem.Statistics s) {
            return s.getScheme().equals(this.scheme$1);
         }

         public {
            this.scheme$1 = scheme$1;
         }
      });
   }

   public <T> Object org$apache$spark$executor$ExecutorSource$$registerFileSystemStat(String scheme, String name, Function1<FileSystem.Statistics, T> f, T defaultValue) {
      return (Gauge)this.metricRegistry().register(MetricRegistry.name("filesystem", new String[]{scheme, name}), new Gauge<T>(this, scheme, f, defaultValue) {
         // $FF: synthetic field
         private final ExecutorSource $outer;
         private final String scheme$2;
         private final Function1 f$1;
         public final Object defaultValue$1;

         public T getValue() {
            return (T)this.$outer.org$apache$spark$executor$ExecutorSource$$fileStats(this.scheme$2).map(this.f$1).getOrElse(new Serializable(this) {
               public static final long serialVersionUID = 0L;
               // $FF: synthetic field
               private final <undefinedtype> $outer;

               public final T apply() {
                  return (T)this.$outer.defaultValue$1;
               }

               public {
                  if ($outer == null) {
                     throw null;
                  } else {
                     this.$outer = $outer;
                  }
               }
            });
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
               this.scheme$2 = scheme$2;
               this.f$1 = f$1;
               this.defaultValue$1 = defaultValue$1;
            }
         }
      });
   }

   public MetricRegistry metricRegistry() {
      return this.metricRegistry;
   }

   public String sourceName() {
      return this.sourceName;
   }

   public Counter METRIC_CPU_TIME() {
      return this.METRIC_CPU_TIME;
   }

   public Counter METRIC_RUN_TIME() {
      return this.METRIC_RUN_TIME;
   }

   public Counter METRIC_JVM_GC_TIME() {
      return this.METRIC_JVM_GC_TIME;
   }

   public Counter METRIC_DESERIALIZE_TIME() {
      return this.METRIC_DESERIALIZE_TIME;
   }

   public Counter METRIC_DESERIALIZE_CPU_TIME() {
      return this.METRIC_DESERIALIZE_CPU_TIME;
   }

   public Counter METRIC_RESULT_SERIALIZE_TIME() {
      return this.METRIC_RESULT_SERIALIZE_TIME;
   }

   public Counter METRIC_SHUFFLE_FETCH_WAIT_TIME() {
      return this.METRIC_SHUFFLE_FETCH_WAIT_TIME;
   }

   public Counter METRIC_SHUFFLE_WRITE_TIME() {
      return this.METRIC_SHUFFLE_WRITE_TIME;
   }

   public Counter METRIC_SHUFFLE_TOTAL_BYTES_READ() {
      return this.METRIC_SHUFFLE_TOTAL_BYTES_READ;
   }

   public Counter METRIC_SHUFFLE_REMOTE_BYTES_READ() {
      return this.METRIC_SHUFFLE_REMOTE_BYTES_READ;
   }

   public Counter METRIC_SHUFFLE_REMOTE_BYTES_READ_TO_DISK() {
      return this.METRIC_SHUFFLE_REMOTE_BYTES_READ_TO_DISK;
   }

   public Counter METRIC_SHUFFLE_LOCAL_BYTES_READ() {
      return this.METRIC_SHUFFLE_LOCAL_BYTES_READ;
   }

   public Counter METRIC_SHUFFLE_RECORDS_READ() {
      return this.METRIC_SHUFFLE_RECORDS_READ;
   }

   public Counter METRIC_SHUFFLE_REMOTE_BLOCKS_FETCHED() {
      return this.METRIC_SHUFFLE_REMOTE_BLOCKS_FETCHED;
   }

   public Counter METRIC_SHUFFLE_LOCAL_BLOCKS_FETCHED() {
      return this.METRIC_SHUFFLE_LOCAL_BLOCKS_FETCHED;
   }

   public Counter METRIC_SHUFFLE_BYTES_WRITTEN() {
      return this.METRIC_SHUFFLE_BYTES_WRITTEN;
   }

   public Counter METRIC_SHUFFLE_RECORDS_WRITTEN() {
      return this.METRIC_SHUFFLE_RECORDS_WRITTEN;
   }

   public Counter METRIC_INPUT_BYTES_READ() {
      return this.METRIC_INPUT_BYTES_READ;
   }

   public Counter METRIC_INPUT_RECORDS_READ() {
      return this.METRIC_INPUT_RECORDS_READ;
   }

   public Counter METRIC_OUTPUT_BYTES_WRITTEN() {
      return this.METRIC_OUTPUT_BYTES_WRITTEN;
   }

   public Counter METRIC_OUTPUT_RECORDS_WRITTEN() {
      return this.METRIC_OUTPUT_RECORDS_WRITTEN;
   }

   public Counter METRIC_RESULT_SIZE() {
      return this.METRIC_RESULT_SIZE;
   }

   public Counter METRIC_DISK_BYTES_SPILLED() {
      return this.METRIC_DISK_BYTES_SPILLED;
   }

   public Counter METRIC_MEMORY_BYTES_SPILLED() {
      return this.METRIC_MEMORY_BYTES_SPILLED;
   }

   public ExecutorSource(ThreadPoolExecutor threadPool, String executorId) {
      this.org$apache$spark$executor$ExecutorSource$$threadPool = threadPool;
      this.metricRegistry = new MetricRegistry();
      this.sourceName = "executor";
      this.metricRegistry().register(MetricRegistry.name("threadpool", new String[]{"activeTasks"}), new Gauge<Object>(this) {
         // $FF: synthetic field
         private final ExecutorSource $outer;

         public int getValue() {
            return this.$outer.org$apache$spark$executor$ExecutorSource$$threadPool.getActiveCount();
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
            }
         }
      });
      this.metricRegistry().register(MetricRegistry.name("threadpool", new String[]{"completeTasks"}), new Gauge<Object>(this) {
         // $FF: synthetic field
         private final ExecutorSource $outer;

         public long getValue() {
            return this.$outer.org$apache$spark$executor$ExecutorSource$$threadPool.getCompletedTaskCount();
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
            }
         }
      });
      this.metricRegistry().register(MetricRegistry.name("threadpool", new String[]{"currentPool_size"}), new Gauge<Object>(this) {
         // $FF: synthetic field
         private final ExecutorSource $outer;

         public int getValue() {
            return this.$outer.org$apache$spark$executor$ExecutorSource$$threadPool.getPoolSize();
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
            }
         }
      });
      this.metricRegistry().register(MetricRegistry.name("threadpool", new String[]{"maxPool_size"}), new Gauge<Object>(this) {
         // $FF: synthetic field
         private final ExecutorSource $outer;

         public int getValue() {
            return this.$outer.org$apache$spark$executor$ExecutorSource$$threadPool.getMaximumPoolSize();
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
            }
         }
      });
      Predef$.MODULE$.refArrayOps((Object[])(new String[]{"hdfs", "file"})).foreach(new Serializable(this) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final ExecutorSource $outer;

         public final Object apply(String scheme) {
            this.$outer.org$apache$spark$executor$ExecutorSource$$registerFileSystemStat(scheme, "read_bytes", new Serializable(this) {
               public static final long serialVersionUID = 0L;

               public final long apply(FileSystem.Statistics x$1) {
                  return x$1.getBytesRead();
               }
            }, BoxesRunTime.boxToLong(0L));
            this.$outer.org$apache$spark$executor$ExecutorSource$$registerFileSystemStat(scheme, "write_bytes", new Serializable(this) {
               public static final long serialVersionUID = 0L;

               public final long apply(FileSystem.Statistics x$2) {
                  return x$2.getBytesWritten();
               }
            }, BoxesRunTime.boxToLong(0L));
            this.$outer.org$apache$spark$executor$ExecutorSource$$registerFileSystemStat(scheme, "read_ops", new Serializable(this) {
               public static final long serialVersionUID = 0L;

               public final int apply(FileSystem.Statistics x$3) {
                  return x$3.getReadOps();
               }
            }, BoxesRunTime.boxToInteger(0));
            this.$outer.org$apache$spark$executor$ExecutorSource$$registerFileSystemStat(scheme, "largeRead_ops", new Serializable(this) {
               public static final long serialVersionUID = 0L;

               public final int apply(FileSystem.Statistics x$4) {
                  return x$4.getLargeReadOps();
               }
            }, BoxesRunTime.boxToInteger(0));
            return this.$outer.org$apache$spark$executor$ExecutorSource$$registerFileSystemStat(scheme, "write_ops", new Serializable(this) {
               public static final long serialVersionUID = 0L;

               public final int apply(FileSystem.Statistics x$5) {
                  return x$5.getWriteOps();
               }
            }, BoxesRunTime.boxToInteger(0));
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
            }
         }
      });
      this.METRIC_CPU_TIME = this.metricRegistry().counter(MetricRegistry.name("cpuTime", new String[0]));
      this.METRIC_RUN_TIME = this.metricRegistry().counter(MetricRegistry.name("runTime", new String[0]));
      this.METRIC_JVM_GC_TIME = this.metricRegistry().counter(MetricRegistry.name("jvmGCTime", new String[0]));
      this.METRIC_DESERIALIZE_TIME = this.metricRegistry().counter(MetricRegistry.name("deserializeTime", new String[0]));
      this.METRIC_DESERIALIZE_CPU_TIME = this.metricRegistry().counter(MetricRegistry.name("deserializeCpuTime", new String[0]));
      this.METRIC_RESULT_SERIALIZE_TIME = this.metricRegistry().counter(MetricRegistry.name("resultSerializationTime", new String[0]));
      this.METRIC_SHUFFLE_FETCH_WAIT_TIME = this.metricRegistry().counter(MetricRegistry.name("shuffleFetchWaitTime", new String[0]));
      this.METRIC_SHUFFLE_WRITE_TIME = this.metricRegistry().counter(MetricRegistry.name("shuffleWriteTime", new String[0]));
      this.METRIC_SHUFFLE_TOTAL_BYTES_READ = this.metricRegistry().counter(MetricRegistry.name("shuffleTotalBytesRead", new String[0]));
      this.METRIC_SHUFFLE_REMOTE_BYTES_READ = this.metricRegistry().counter(MetricRegistry.name("shuffleRemoteBytesRead", new String[0]));
      this.METRIC_SHUFFLE_REMOTE_BYTES_READ_TO_DISK = this.metricRegistry().counter(MetricRegistry.name("shuffleRemoteBytesReadToDisk", new String[0]));
      this.METRIC_SHUFFLE_LOCAL_BYTES_READ = this.metricRegistry().counter(MetricRegistry.name("shuffleLocalBytesRead", new String[0]));
      this.METRIC_SHUFFLE_RECORDS_READ = this.metricRegistry().counter(MetricRegistry.name("shuffleRecordsRead", new String[0]));
      this.METRIC_SHUFFLE_REMOTE_BLOCKS_FETCHED = this.metricRegistry().counter(MetricRegistry.name("shuffleRemoteBlocksFetched", new String[0]));
      this.METRIC_SHUFFLE_LOCAL_BLOCKS_FETCHED = this.metricRegistry().counter(MetricRegistry.name("shuffleLocalBlocksFetched", new String[0]));
      this.METRIC_SHUFFLE_BYTES_WRITTEN = this.metricRegistry().counter(MetricRegistry.name("shuffleBytesWritten", new String[0]));
      this.METRIC_SHUFFLE_RECORDS_WRITTEN = this.metricRegistry().counter(MetricRegistry.name("shuffleRecordsWritten", new String[0]));
      this.METRIC_INPUT_BYTES_READ = this.metricRegistry().counter(MetricRegistry.name("bytesRead", new String[0]));
      this.METRIC_INPUT_RECORDS_READ = this.metricRegistry().counter(MetricRegistry.name("recordsRead", new String[0]));
      this.METRIC_OUTPUT_BYTES_WRITTEN = this.metricRegistry().counter(MetricRegistry.name("bytesWritten", new String[0]));
      this.METRIC_OUTPUT_RECORDS_WRITTEN = this.metricRegistry().counter(MetricRegistry.name("recordsWritten", new String[0]));
      this.METRIC_RESULT_SIZE = this.metricRegistry().counter(MetricRegistry.name("resultSize", new String[0]));
      this.METRIC_DISK_BYTES_SPILLED = this.metricRegistry().counter(MetricRegistry.name("diskBytesSpilled", new String[0]));
      this.METRIC_MEMORY_BYTES_SPILLED = this.metricRegistry().counter(MetricRegistry.name("memoryBytesSpilled", new String[0]));
   }
}
