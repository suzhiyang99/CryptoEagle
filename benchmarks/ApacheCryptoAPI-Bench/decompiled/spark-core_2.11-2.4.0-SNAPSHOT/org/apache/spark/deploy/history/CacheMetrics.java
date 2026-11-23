package org.apache.spark.deploy.history;

import com.codahale.metrics.Counter;
import com.codahale.metrics.Metric;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Timer;
import org.apache.spark.metrics.source.Source;
import scala.MatchError;
import scala.Predef$;
import scala.Serializable;
import scala.Tuple2;
import scala.collection.Seq;
import scala.collection.Seq$;
import scala.collection.mutable.StringBuilder;
import scala.reflect.ScalaSignature;

@ScalaSignature(
   bytes = "\u0006\u0001\u0005Ea!B\u0001\u0003\u0001\ta!\u0001D\"bG\",W*\u001a;sS\u000e\u001c(BA\u0002\u0005\u0003\u001dA\u0017n\u001d;pefT!!\u0002\u0004\u0002\r\u0011,\u0007\u000f\\8z\u0015\t9\u0001\"A\u0003ta\u0006\u00148N\u0003\u0002\n\u0015\u00051\u0011\r]1dQ\u0016T\u0011aC\u0001\u0004_J<7c\u0001\u0001\u000e'A\u0011a\"E\u0007\u0002\u001f)\t\u0001#A\u0003tG\u0006d\u0017-\u0003\u0002\u0013\u001f\t1\u0011I\\=SK\u001a\u0004\"\u0001F\r\u000e\u0003UQ!AF\f\u0002\rM|WO]2f\u0015\tAb!A\u0004nKR\u0014\u0018nY:\n\u0005i)\"AB*pkJ\u001cW\r\u0003\u0005\u001d\u0001\t\u0005\t\u0015!\u0003\u001f\u0003\u0019\u0001(/\u001a4jq\u000e\u0001\u0001CA\u0010#\u001d\tq\u0001%\u0003\u0002\"\u001f\u00051\u0001K]3eK\u001aL!a\t\u0013\u0003\rM#(/\u001b8h\u0015\t\ts\u0002C\u0003'\u0001\u0011\u0005q%\u0001\u0004=S:LGO\u0010\u000b\u0003Q)\u0002\"!\u000b\u0001\u000e\u0003\tAQ\u0001H\u0013A\u0002yAq\u0001\f\u0001C\u0002\u0013\u0005Q&A\u0006m_>\\W\u000f]\"pk:$X#\u0001\u0018\u0011\u0005=*T\"\u0001\u0019\u000b\u0005a\t$B\u0001\u001a4\u0003!\u0019w\u000eZ1iC2,'\"\u0001\u001b\u0002\u0007\r|W.\u0003\u00027a\t91i\\;oi\u0016\u0014\bB\u0002\u001d\u0001A\u0003%a&\u0001\u0007m_>\\W\u000f]\"pk:$\b\u0005C\u0004;\u0001\t\u0007I\u0011A\u0017\u0002%1|wn[;q\r\u0006LG.\u001e:f\u0007>,h\u000e\u001e\u0005\u0007y\u0001\u0001\u000b\u0011\u0002\u0018\u0002'1|wn[;q\r\u0006LG.\u001e:f\u0007>,h\u000e\u001e\u0011\t\u000fy\u0002!\u0019!C\u0001[\u0005iQM^5di&|gnQ8v]RDa\u0001\u0011\u0001!\u0002\u0013q\u0013AD3wS\u000e$\u0018n\u001c8D_VtG\u000f\t\u0005\b\u0005\u0002\u0011\r\u0011\"\u0001.\u0003%aw.\u00193D_VtG\u000f\u0003\u0004E\u0001\u0001\u0006IAL\u0001\u000bY>\fGmQ8v]R\u0004\u0003b\u0002$\u0001\u0005\u0004%\taR\u0001\nY>\fG\rV5nKJ,\u0012\u0001\u0013\t\u0003_%K!A\u0013\u0019\u0003\u000bQKW.\u001a:\t\r1\u0003\u0001\u0015!\u0003I\u0003)aw.\u00193US6,'\u000f\t\u0005\b\u001d\u0002\u0011\r\u0011\"\u0003P\u0003!\u0019w.\u001e8uKJ\u001cX#\u0001)\u0011\u0007E#f+D\u0001S\u0015\t\u0019v\"\u0001\u0006d_2dWm\u0019;j_:L!!\u0016*\u0003\u0007M+\u0017\u000f\u0005\u0003\u000f/fs\u0013B\u0001-\u0010\u0005\u0019!V\u000f\u001d7feA\u0011!lX\u0007\u00027*\u0011A,X\u0001\u0005Y\u0006twMC\u0001_\u0003\u0011Q\u0017M^1\n\u0005\rZ\u0006BB1\u0001A\u0003%\u0001+A\u0005d_VtG/\u001a:tA!91\r\u0001b\u0001\n\u0013!\u0017AC1mY6+GO]5dgV\tQ\rE\u0002R)\u001a\u0004BAD,ZOJ\u0019\u0001N[7\u0007\t%\u0004\u0001a\u001a\u0002\ryI,g-\u001b8f[\u0016tGO\u0010\t\u0003_-L!\u0001\u001c\u0019\u0003\r5+GO]5d!\tyc.\u0003\u0002pa\tA1i\\;oi&tw\r\u0003\u0004r\u0001\u0001\u0006I!Z\u0001\fC2dW*\u001a;sS\u000e\u001c\b\u0005C\u0004t\u0001\t\u0007I\u0011\t;\u0002\u0015M|WO]2f\u001d\u0006lW-F\u0001Z\u0011\u00191\b\u0001)A\u00053\u0006Y1o\\;sG\u0016t\u0015-\\3!\u0011\u001dA\bA1A\u0005Be\fa\"\\3ue&\u001c'+Z4jgR\u0014\u00180F\u0001{!\ty30\u0003\u0002}a\tqQ*\u001a;sS\u000e\u0014VmZ5tiJL\bB\u0002@\u0001A\u0003%!0A\bnKR\u0014\u0018n\u0019*fO&\u001cHO]=!\u0011\u001d\t\t\u0001\u0001C\u0005\u0003\u0007\tA!\u001b8jiR\u0011\u0011Q\u0001\t\u0004\u001d\u0005\u001d\u0011bAA\u0005\u001f\t!QK\\5u\u0011\u001d\ti\u0001\u0001C!\u0003\u001f\t\u0001\u0002^8TiJLgn\u001a\u000b\u0002=\u0001"
)
public class CacheMetrics implements Source {
   public final String org$apache$spark$deploy$history$CacheMetrics$$prefix;
   private final Counter lookupCount;
   private final Counter lookupFailureCount;
   private final Counter evictionCount;
   private final Counter loadCount;
   private final Timer loadTimer;
   private final Seq<Tuple2<String, Counter>> counters;
   private final Seq<Tuple2<String, Metric>> allMetrics;
   private final String sourceName;
   private final MetricRegistry metricRegistry;

   public Counter lookupCount() {
      return this.lookupCount;
   }

   public Counter lookupFailureCount() {
      return this.lookupFailureCount;
   }

   public Counter evictionCount() {
      return this.evictionCount;
   }

   public Counter loadCount() {
      return this.loadCount;
   }

   public Timer loadTimer() {
      return this.loadTimer;
   }

   private Seq<Tuple2<String, Counter>> counters() {
      return this.counters;
   }

   private Seq<Tuple2<String, Metric>> allMetrics() {
      return this.allMetrics;
   }

   public String sourceName() {
      return this.sourceName;
   }

   public MetricRegistry metricRegistry() {
      return this.metricRegistry;
   }

   private void init() {
      this.allMetrics().foreach(new Serializable(this) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final CacheMetrics $outer;

         public final Metric apply(Tuple2<String, Metric> x0$2) {
            if (x0$2 != null) {
               String name = (String)x0$2._1();
               Metric metric = (Metric)x0$2._2();
               Metric var5 = this.$outer.metricRegistry().register(MetricRegistry.name(this.$outer.org$apache$spark$deploy$history$CacheMetrics$$prefix, new String[]{name}), metric);
               return var5;
            } else {
               throw new MatchError(x0$2);
            }
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

   public String toString() {
      StringBuilder sb = new StringBuilder();
      this.counters().foreach(new Serializable(this, sb) {
         public static final long serialVersionUID = 0L;
         private final StringBuilder sb$2;

         public final StringBuilder apply(Tuple2<String, Counter> x0$3) {
            if (x0$3 != null) {
               String name = (String)x0$3._1();
               Counter counter = (Counter)x0$3._2();
               StringBuilder var5 = this.sb$2.append(name).append(" = ").append(counter.getCount()).append('\n');
               return var5;
            } else {
               throw new MatchError(x0$3);
            }
         }

         public {
            this.sb$2 = sb$2;
         }
      });
      return sb.toString();
   }

   public CacheMetrics(String prefix) {
      this.org$apache$spark$deploy$history$CacheMetrics$$prefix = prefix;
      this.lookupCount = new Counter();
      this.lookupFailureCount = new Counter();
      this.evictionCount = new Counter();
      this.loadCount = new Counter();
      this.loadTimer = new Timer();
      this.counters = (Seq)Seq$.MODULE$.apply(Predef$.MODULE$.wrapRefArray(new Tuple2[]{new Tuple2("lookup.count", this.lookupCount()), new Tuple2("lookup.failure.count", this.lookupFailureCount()), new Tuple2("eviction.count", this.evictionCount()), new Tuple2("load.count", this.loadCount())}));
      this.allMetrics = (Seq)this.counters().$plus$plus(Seq$.MODULE$.apply(Predef$.MODULE$.wrapRefArray(new Tuple2[]{new Tuple2("load.timer", this.loadTimer())})), Seq$.MODULE$.canBuildFrom());
      this.sourceName = "ApplicationCache";
      this.metricRegistry = new MetricRegistry();
   }
}
