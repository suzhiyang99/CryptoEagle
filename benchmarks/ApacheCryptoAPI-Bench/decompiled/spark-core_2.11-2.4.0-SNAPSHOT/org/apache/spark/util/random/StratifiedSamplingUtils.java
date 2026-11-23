package org.apache.spark.util.random;

import org.apache.commons.math3.distribution.PoissonDistribution;
import org.apache.spark.rdd.RDD;
import org.slf4j.Logger;
import scala.Function0;
import scala.Function2;
import scala.Option;
import scala.Serializable;
import scala.Tuple2;
import scala.collection.Iterator;
import scala.collection.Map;
import scala.collection.immutable.Nil$;
import scala.collection.mutable.Map$;
import scala.reflect.ClassTag;
import scala.reflect.ScalaSignature;
import scala.runtime.BoxesRunTime;

@ScalaSignature(
   bytes = "\u0006\u0001\u0005mxAB\u0001\u0003\u0011\u00031A\"A\fTiJ\fG/\u001b4jK\u0012\u001c\u0016-\u001c9mS:<W\u000b^5mg*\u00111\u0001B\u0001\u0007e\u0006tGm\\7\u000b\u0005\u00151\u0011\u0001B;uS2T!a\u0002\u0005\u0002\u000bM\u0004\u0018M]6\u000b\u0005%Q\u0011AB1qC\u000eDWMC\u0001\f\u0003\ry'o\u001a\t\u0003\u001b9i\u0011A\u0001\u0004\u0007\u001f\tA\tA\u0002\t\u0003/M#(/\u0019;jM&,GmU1na2LgnZ+uS2\u001c8c\u0001\b\u0012/A\u0011!#F\u0007\u0002')\tA#A\u0003tG\u0006d\u0017-\u0003\u0002\u0017'\t1\u0011I\\=SK\u001a\u0004\"\u0001G\u000e\u000e\u0003eQ!A\u0007\u0004\u0002\u0011%tG/\u001a:oC2L!\u0001H\r\u0003\u000f1{wmZ5oO\")aD\u0004C\u0001A\u00051A(\u001b8jiz\u001a\u0001\u0001F\u0001\r\u0011\u0015\u0011c\u0002\"\u0001$\u0003Q9W\r^!dG\u0016\u0004H/\u00198dKJ+7/\u001e7ugV\u0019Ae\f$\u0015\r\u0015Z\u0004*T+_!\u001113&\f\u001d\u000e\u0003\u001dR!\u0001K\u0015\u0002\u000f5,H/\u00192mK*\u0011!fE\u0001\u000bG>dG.Z2uS>t\u0017B\u0001\u0017(\u0005\ri\u0015\r\u001d\t\u0003]=b\u0001\u0001B\u00031C\t\u0007\u0011GA\u0001L#\t\u0011T\u0007\u0005\u0002\u0013g%\u0011Ag\u0005\u0002\b\u001d>$\b.\u001b8h!\t\u0011b'\u0003\u00028'\t\u0019\u0011I\\=\u0011\u00055I\u0014B\u0001\u001e\u0003\u0005A\t5mY3qi\u0006t7-\u001a*fgVdG\u000fC\u0003=C\u0001\u0007Q(A\u0002sI\u0012\u00042A\u0010!C\u001b\u0005y$B\u0001\u001f\u0007\u0013\t\tuHA\u0002S\t\u0012\u0003BAE\".\u000b&\u0011Ai\u0005\u0002\u0007)V\u0004H.\u001a\u001a\u0011\u000592E!B$\"\u0005\u0004\t$!\u0001,\t\u000b%\u000b\u0003\u0019\u0001&\u0002\u001f]LG\u000f\u001b*fa2\f7-Z7f]R\u0004\"AE&\n\u00051\u001b\"a\u0002\"p_2,\u0017M\u001c\u0005\u0006\u001d\u0006\u0002\raT\u0001\nMJ\f7\r^5p]N\u0004B\u0001U).%6\t\u0011&\u0003\u0002-SA\u0011!cU\u0005\u0003)N\u0011a\u0001R8vE2,\u0007\"\u0002,\"\u0001\u00049\u0016AB2pk:$8\u000fE\u0002\u00131jK!!W\n\u0003\r=\u0003H/[8o!\u0011\u0001\u0016+L.\u0011\u0005Ia\u0016BA/\u0014\u0005\u0011auN\\4\t\u000b}\u000b\u0003\u0019A.\u0002\tM,W\r\u001a\u0005\u0006C:!\tAY\u0001\tO\u0016$8+Z9PaV\u00191-\u001b7\u0015\r\u0011lg\u000e]A.!\u0015\u0011Rm\u001a6h\u0013\t17CA\u0005Gk:\u001cG/[8oeA!ae\u000b59!\tq\u0013\u000eB\u00031A\n\u0007\u0011\u0007\u0005\u0003\u0013\u0007\"\\\u0007C\u0001\u0018m\t\u00159\u0005M1\u00012\u0011\u0015I\u0005\r1\u0001K\u0011\u0015q\u0005\r1\u0001p!\u0011\u0001\u0016\u000b\u001b*\t\u000bE\u0004\u0007\u0019\u0001:\u0002\u0007Itw\r\u0005\u0002ti6\taB\u0002\u0003v\u001d\u00111(a\u0005*b]\u0012|W\u000eR1uC\u001e+g.\u001a:bi>\u00148C\u0001;\u0012\u0011\u0015qB\u000f\"\u0001y)\u0005\u0011\bb\u0002>u\u0005\u0004%\ta_\u0001\bk:Lgm\u001c:n+\u0005a\bCA\u0007~\u0013\tq(A\u0001\bY\u001fJ\u001b\u0006.\u001b4u%\u0006tGm\\7\t\u000f\u0005\u0005A\u000f)A\u0005y\u0006AQO\\5g_Jl\u0007\u0005C\u0005\u0002\u0006Q\u0014\r\u0011\"\u0001\u0002\b\u0005a\u0001o\\5tg>t7)Y2iKV\u0011\u0011\u0011\u0002\t\u0006M-\u0012\u00161\u0002\t\u0005\u0003\u001b\tY\"\u0004\u0002\u0002\u0010)!\u0011\u0011CA\n\u00031!\u0017n\u001d;sS\n,H/[8o\u0015\u0011\t)\"a\u0006\u0002\u000b5\fG\u000f[\u001a\u000b\u0007\u0005e\u0001\"A\u0004d_6lwN\\:\n\t\u0005u\u0011q\u0002\u0002\u0014!>L7o]8o\t&\u001cHO]5ckRLwN\u001c\u0005\t\u0003C!\b\u0015!\u0003\u0002\n\u0005i\u0001o\\5tg>t7)Y2iK\u0002B\u0011\"!\nu\u0001\u0004%\t!a\n\u0002\u0017A|\u0017n]:p]N+W\rZ\u000b\u00027\"I\u00111\u0006;A\u0002\u0013\u0005\u0011QF\u0001\u0010a>L7o]8o'\u0016,Gm\u0018\u0013fcR!\u0011qFA\u001b!\r\u0011\u0012\u0011G\u0005\u0004\u0003g\u0019\"\u0001B+oSRD\u0011\"a\u000e\u0002*\u0005\u0005\t\u0019A.\u0002\u0007a$\u0013\u0007C\u0004\u0002<Q\u0004\u000b\u0015B.\u0002\u0019A|\u0017n]:p]N+W\r\u001a\u0011\t\u000f\u0005}B\u000f\"\u0001\u0002B\u00051!/Z*fK\u0012$B!a\f\u0002D!1q,!\u0010A\u0002mCq!a\u0012u\t\u0003\tI%A\u0006oKb$\bk\\5tg>tG\u0003BA&\u0003#\u00022AEA'\u0013\r\tye\u0005\u0002\u0004\u0013:$\bbBA*\u0003\u000b\u0002\rAU\u0001\u0005[\u0016\fg\u000eC\u0004\u0002XQ$\t!!\u0017\u0002\u00179,\u0007\u0010^+oS\u001a|'/\u001c\u000b\u0002%\"1a\u000b\u0019a\u0001\u0003;\u0002BA\u0005-\u0002`A!\u0001+\u00155\\\u0011\u001d\t\u0019G\u0004C\u0001\u0003K\n\u0011bZ3u\u0007>l'm\u00149\u0016\t\u0005\u001d\u0014qN\u000b\u0003\u0003S\u0002\u0002BE3\u0002l\u0005-\u00141\u000e\t\u0006M-\ni\u0007\u000f\t\u0004]\u0005=DA\u0002\u0019\u0002b\t\u0007\u0011\u0007C\u0004\u0002t9!\t!!\u001e\u0002+\r|W\u000e];uKRC'/Z:i_2$')_&fsV!\u0011qOA?)\u0019\tI(a \u0002\u0006B)\u0001+UA>%B\u0019a&! \u0005\rA\n\tH1\u00012\u0011!\t\t)!\u001dA\u0002\u0005\r\u0015a\u00034j]\u0006d'+Z:vYR\u0004R\u0001U)\u0002|aBqATA9\u0001\u0004\tI\bC\u0004\u0002\n:!\t!a#\u00029\u001d,GOQ3s]>,H\u000e\\5TC6\u0004H.\u001b8h\rVt7\r^5p]V1\u0011QRAW\u0003c#\"\"a$\u00024\u0006]\u00161XA`!!\u0011R-a\u0013\u0002\u0012\u0006E\u0005CBAJ\u0003G\u000bIK\u0004\u0003\u0002\u0016\u0006}e\u0002BAL\u0003;k!!!'\u000b\u0007\u0005mu$\u0001\u0004=e>|GOP\u0005\u0002)%\u0019\u0011\u0011U\n\u0002\u000fA\f7m[1hK&!\u0011QUAT\u0005!IE/\u001a:bi>\u0014(bAAQ'A1!cQAV\u0003_\u00032ALAW\t\u0019\u0001\u0014q\u0011b\u0001cA\u0019a&!-\u0005\r\u001d\u000b9I1\u00012\u0011\u001da\u0014q\u0011a\u0001\u0003k\u0003BA\u0010!\u0002*\"9a*a\"A\u0002\u0005e\u0006#\u0002)R\u0003W\u0013\u0006bBA_\u0003\u000f\u0003\rAS\u0001\u0006Kb\f7\r\u001e\u0005\u0007?\u0006\u001d\u0005\u0019A.\t\u000f\u0005\rg\u0002\"\u0001\u0002F\u0006Qr-\u001a;Q_&\u001c8o\u001c8TC6\u0004H.\u001b8h\rVt7\r^5p]V1\u0011qYAj\u0003/$\"\"!3\u0002p\u0006M\u0018q_A})\u0019\tY-!7\u0002jBA!#ZA&\u0003\u001b\fi\r\u0005\u0004\u0002\u0014\u0006\r\u0016q\u001a\t\u0007%\r\u000b\t.!6\u0011\u00079\n\u0019\u000e\u0002\u00041\u0003\u0003\u0014\r!\r\t\u0004]\u0005]GAB$\u0002B\n\u0007\u0011\u0007\u0003\u0006\u0002\\\u0006\u0005\u0017\u0011!a\u0002\u0003;\f!\"\u001a<jI\u0016t7-\u001a\u00132!\u0019\ty.!:\u0002R6\u0011\u0011\u0011\u001d\u0006\u0004\u0003G\u001c\u0012a\u0002:fM2,7\r^\u0005\u0005\u0003O\f\tO\u0001\u0005DY\u0006\u001c8\u000fV1h\u0011)\tY/!1\u0002\u0002\u0003\u000f\u0011Q^\u0001\u000bKZLG-\u001a8dK\u0012\u0012\u0004CBAp\u0003K\f)\u000eC\u0004=\u0003\u0003\u0004\r!!=\u0011\ty\u0002\u0015q\u001a\u0005\b\u001d\u0006\u0005\u0007\u0019AA{!\u0015\u0001\u0016+!5S\u0011\u001d\ti,!1A\u0002)CaaXAa\u0001\u0004Y\u0006"
)
public final class StratifiedSamplingUtils {
   public static boolean initializeLogIfNecessary$default$2() {
      return StratifiedSamplingUtils$.MODULE$.initializeLogIfNecessary$default$2();
   }

   public static boolean initializeLogIfNecessary(boolean var0, boolean var1) {
      return StratifiedSamplingUtils$.MODULE$.initializeLogIfNecessary(var0, var1);
   }

   public static void initializeLogIfNecessary(boolean var0) {
      StratifiedSamplingUtils$.MODULE$.initializeLogIfNecessary(var0);
   }

   public static boolean isTraceEnabled() {
      return StratifiedSamplingUtils$.MODULE$.isTraceEnabled();
   }

   public static void logError(Function0<String> var0, Throwable var1) {
      StratifiedSamplingUtils$.MODULE$.logError(var0, var1);
   }

   public static void logWarning(Function0<String> var0, Throwable var1) {
      StratifiedSamplingUtils$.MODULE$.logWarning(var0, var1);
   }

   public static void logTrace(Function0<String> var0, Throwable var1) {
      StratifiedSamplingUtils$.MODULE$.logTrace(var0, var1);
   }

   public static void logDebug(Function0<String> var0, Throwable var1) {
      StratifiedSamplingUtils$.MODULE$.logDebug(var0, var1);
   }

   public static void logInfo(Function0<String> var0, Throwable var1) {
      StratifiedSamplingUtils$.MODULE$.logInfo(var0, var1);
   }

   public static void logError(Function0<String> var0) {
      StratifiedSamplingUtils$.MODULE$.logError(var0);
   }

   public static void logWarning(Function0<String> var0) {
      StratifiedSamplingUtils$.MODULE$.logWarning(var0);
   }

   public static void logTrace(Function0<String> var0) {
      StratifiedSamplingUtils$.MODULE$.logTrace(var0);
   }

   public static void logDebug(Function0<String> var0) {
      StratifiedSamplingUtils$.MODULE$.logDebug(var0);
   }

   public static void logInfo(Function0<String> var0) {
      StratifiedSamplingUtils$.MODULE$.logInfo(var0);
   }

   public static Logger log() {
      return StratifiedSamplingUtils$.MODULE$.log();
   }

   public static String logName() {
      return StratifiedSamplingUtils$.MODULE$.logName();
   }

   public static <K, V> Function2<Object, Iterator<Tuple2<K, V>>, Iterator<Tuple2<K, V>>> getPoissonSamplingFunction(RDD<Tuple2<K, V>> var0, Map<K, Object> var1, boolean var2, long var3, ClassTag<K> var5, ClassTag<V> var6) {
      return StratifiedSamplingUtils$.MODULE$.getPoissonSamplingFunction(var0, var1, var2, var3, var5, var6);
   }

   public static <K, V> Function2<Object, Iterator<Tuple2<K, V>>, Iterator<Tuple2<K, V>>> getBernoulliSamplingFunction(RDD<Tuple2<K, V>> var0, Map<K, Object> var1, boolean var2, long var3) {
      return StratifiedSamplingUtils$.MODULE$.getBernoulliSamplingFunction(var0, var1, var2, var3);
   }

   public static <K> Map<K, Object> computeThresholdByKey(Map<K, AcceptanceResult> var0, Map<K, Object> var1) {
      return StratifiedSamplingUtils$.MODULE$.computeThresholdByKey(var0, var1);
   }

   public static <K> Function2<scala.collection.mutable.Map<K, AcceptanceResult>, scala.collection.mutable.Map<K, AcceptanceResult>, scala.collection.mutable.Map<K, AcceptanceResult>> getCombOp() {
      return StratifiedSamplingUtils$.MODULE$.getCombOp();
   }

   public static <K, V> Function2<scala.collection.mutable.Map<K, AcceptanceResult>, Tuple2<K, V>, scala.collection.mutable.Map<K, AcceptanceResult>> getSeqOp(boolean var0, Map<K, Object> var1, RandomDataGenerator var2, Option<Map<K, Object>> var3) {
      return StratifiedSamplingUtils$.MODULE$.getSeqOp(var0, var1, var2, var3);
   }

   public static <K, V> scala.collection.mutable.Map<K, AcceptanceResult> getAcceptanceResults(RDD<Tuple2<K, V>> var0, boolean var1, Map<K, Object> var2, Option<Map<K, Object>> var3, long var4) {
      return StratifiedSamplingUtils$.MODULE$.getAcceptanceResults(var0, var1, var2, var3, var4);
   }

   public static class RandomDataGenerator {
      private final XORShiftRandom uniform = new XORShiftRandom();
      private final scala.collection.mutable.Map<Object, PoissonDistribution> poissonCache;
      private long poissonSeed;

      public XORShiftRandom uniform() {
         return this.uniform;
      }

      public scala.collection.mutable.Map<Object, PoissonDistribution> poissonCache() {
         return this.poissonCache;
      }

      public long poissonSeed() {
         return this.poissonSeed;
      }

      public void poissonSeed_$eq(long x$1) {
         this.poissonSeed = x$1;
      }

      public void reSeed(long seed) {
         this.uniform().setSeed(seed);
         this.poissonSeed_$eq(seed);
         this.poissonCache().clear();
      }

      public int nextPoisson(double mean) {
         PoissonDistribution poisson = (PoissonDistribution)this.poissonCache().getOrElseUpdate(BoxesRunTime.boxToDouble(mean), new Serializable(this, mean) {
            public static final long serialVersionUID = 0L;
            // $FF: synthetic field
            private final RandomDataGenerator $outer;
            private final double mean$1;

            public final PoissonDistribution apply() {
               PoissonDistribution newPoisson = new PoissonDistribution(this.mean$1);
               newPoisson.reseedRandomGenerator(this.$outer.poissonSeed());
               return newPoisson;
            }

            public {
               if ($outer == null) {
                  throw null;
               } else {
                  this.$outer = $outer;
                  this.mean$1 = mean$1;
               }
            }
         });
         return poisson.sample();
      }

      public double nextUniform() {
         return this.uniform().nextDouble();
      }

      public RandomDataGenerator() {
         this.poissonCache = (scala.collection.mutable.Map)Map$.MODULE$.apply(Nil$.MODULE$);
         this.poissonSeed = 0L;
      }
   }
}
