package org.apache.spark.util.random;

import java.util.Random;
import org.apache.spark.annotation.DeveloperApi;
import scala.Predef$;
import scala.Serializable;
import scala.StringContext;
import scala.collection.Iterator;
import scala.reflect.ClassTag;
import scala.reflect.ScalaSignature;
import scala.runtime.BoxedUnit;
import scala.runtime.BoxesRunTime;

@DeveloperApi
@ScalaSignature(
   bytes = "\u0006\u0001\u001d4A!\u0001\u0002\u0001\u001b\t\u0001\")\u001a:o_VdG.[*b[BdWM\u001d\u0006\u0003\u0007\u0011\taA]1oI>l'BA\u0003\u0007\u0003\u0011)H/\u001b7\u000b\u0005\u001dA\u0011!B:qCJ\\'BA\u0005\u000b\u0003\u0019\t\u0007/Y2iK*\t1\"A\u0002pe\u001e\u001c\u0001!\u0006\u0002\u000f7M\u0019\u0001aD\u000b\u0011\u0005A\u0019R\"A\t\u000b\u0003I\tQa]2bY\u0006L!\u0001F\t\u0003\r\u0005s\u0017PU3g!\u00111r#G\r\u000e\u0003\tI!\u0001\u0007\u0002\u0003\u001bI\u000bg\u000eZ8n'\u0006l\u0007\u000f\\3s!\tQ2\u0004\u0004\u0001\u0005\u000bq\u0001!\u0019A\u000f\u0003\u0003Q\u000b\"AH\u0011\u0011\u0005Ay\u0012B\u0001\u0011\u0012\u0005\u001dqu\u000e\u001e5j]\u001e\u0004\"\u0001\u0005\u0012\n\u0005\r\n\"aA!os\"AQ\u0005\u0001B\u0001B\u0003%a%\u0001\u0005ge\u0006\u001cG/[8o!\t\u0001r%\u0003\u0002)#\t1Ai\\;cY\u0016D\u0001B\u000b\u0001\u0003\u0004\u0003\u0006YaK\u0001\u000bKZLG-\u001a8dK\u0012\n\u0004c\u0001\u0017035\tQF\u0003\u0002/#\u00059!/\u001a4mK\u000e$\u0018B\u0001\u0019.\u0005!\u0019E.Y:t)\u0006<\u0007\"\u0002\u001a\u0001\t\u0003\u0019\u0014A\u0002\u001fj]&$h\b\u0006\u00025oQ\u0011QG\u000e\t\u0004-\u0001I\u0002\"\u0002\u00162\u0001\bY\u0003\"B\u00132\u0001\u00041\u0003bB\u001d\u0001\u0005\u0004%IAO\u0001\u0004e:<W#A\u001e\u0011\u0005q\u0002U\"A\u001f\u000b\u0005\u0015q$\"A \u0002\t)\fg/Y\u0005\u0003\u0003v\u0012aAU1oI>l\u0007BB\"\u0001A\u0003%1(\u0001\u0003s]\u001e\u0004\u0003\"B#\u0001\t\u00032\u0015aB:fiN+W\r\u001a\u000b\u0003\u000f*\u0003\"\u0001\u0005%\n\u0005%\u000b\"\u0001B+oSRDQa\u0013#A\u00021\u000bAa]3fIB\u0011\u0001#T\u0005\u0003\u001dF\u0011A\u0001T8oO\"A\u0001\u000b\u0001EC\u0002\u0013%\u0011+A\u0006hCB\u001c\u0016-\u001c9mS:<W#\u0001*\u0011\u0005Y\u0019\u0016B\u0001+\u0003\u0005-9\u0015\r]*b[Bd\u0017N\\4\t\u0011Y\u0003\u0001\u0012!Q!\nI\u000bAbZ1q'\u0006l\u0007\u000f\\5oO\u0002BQ\u0001\u0017\u0001\u0005Be\u000baa]1na2,G#\u0001.\u0011\u0005AY\u0016B\u0001/\u0012\u0005\rIe\u000e\u001e\u0005\u0006=\u0002!\teX\u0001\u0006G2|g.\u001a\u000b\u0002k!\u0012\u0001!\u0019\t\u0003E\u0016l\u0011a\u0019\u0006\u0003I\u001a\t!\"\u00198o_R\fG/[8o\u0013\t17M\u0001\u0007EKZ,Gn\u001c9fe\u0006\u0003\u0018\u000e"
)
public class BernoulliSampler<T> implements RandomSampler<T, T> {
   public final double org$apache$spark$util$random$BernoulliSampler$$fraction;
   private final ClassTag<T> evidence$1;
   private final Random rng;
   private GapSampling gapSampling;
   private volatile boolean bitmap$0;

   private GapSampling gapSampling$lzycompute() {
      synchronized(this) {
         if (!this.bitmap$0) {
            this.gapSampling = new GapSampling(this.org$apache$spark$util$random$BernoulliSampler$$fraction, this.rng(), RandomSampler$.MODULE$.rngEpsilon());
            this.bitmap$0 = true;
         }

         BoxedUnit var10000 = BoxedUnit.UNIT;
      }

      return this.gapSampling;
   }

   public Iterator<T> sample(Iterator<T> items) {
      return RandomSampler$class.sample(this, items);
   }

   private Random rng() {
      return this.rng;
   }

   public void setSeed(long seed) {
      this.rng().setSeed(seed);
   }

   private GapSampling gapSampling() {
      return this.bitmap$0 ? this.gapSampling : this.gapSampling$lzycompute();
   }

   public int sample() {
      return this.org$apache$spark$util$random$BernoulliSampler$$fraction <= (double)0.0F ? 0 : (this.org$apache$spark$util$random$BernoulliSampler$$fraction >= (double)1.0F ? 1 : (this.org$apache$spark$util$random$BernoulliSampler$$fraction <= RandomSampler$.MODULE$.defaultMaxGapSamplingFraction() ? this.gapSampling().sample() : (this.rng().nextDouble() <= this.org$apache$spark$util$random$BernoulliSampler$$fraction ? 1 : 0)));
   }

   public BernoulliSampler<T> clone() {
      return new BernoulliSampler<T>(this.org$apache$spark$util$random$BernoulliSampler$$fraction, this.evidence$1);
   }

   public BernoulliSampler(double fraction, ClassTag<T> evidence$1) {
      this.org$apache$spark$util$random$BernoulliSampler$$fraction = fraction;
      this.evidence$1 = evidence$1;
      RandomSampler$class.$init$(this);
      Predef$.MODULE$.require(fraction >= (double)0.0F - RandomSampler$.MODULE$.roundingEpsilon() && fraction <= (double)1.0F + RandomSampler$.MODULE$.roundingEpsilon(), new Serializable(this) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final BernoulliSampler $outer;

         public final String apply() {
            return (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Sampling fraction (", ") must be on interval [0, 1]"})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{BoxesRunTime.boxToDouble(this.$outer.org$apache$spark$util$random$BernoulliSampler$$fraction)}));
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
            }
         }
      });
      this.rng = RandomSampler$.MODULE$.newDefaultRNG();
   }
}
