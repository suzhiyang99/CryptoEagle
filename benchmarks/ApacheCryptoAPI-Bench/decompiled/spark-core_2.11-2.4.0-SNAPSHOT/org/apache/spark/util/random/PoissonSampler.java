package org.apache.spark.util.random;

import java.util.Random;
import org.apache.commons.math3.distribution.PoissonDistribution;
import org.apache.spark.annotation.DeveloperApi;
import scala.Predef$;
import scala.Serializable;
import scala.StringContext;
import scala.collection.GenTraversableOnce;
import scala.collection.Iterator;
import scala.reflect.ScalaSignature;
import scala.runtime.BoxedUnit;
import scala.runtime.BoxesRunTime;

@DeveloperApi
@ScalaSignature(
   bytes = "\u0006\u0001\u0005-a\u0001B\u0001\u0003\u00015\u0011a\u0002U8jgN|gnU1na2,'O\u0003\u0002\u0004\t\u00051!/\u00198e_6T!!\u0002\u0004\u0002\tU$\u0018\u000e\u001c\u0006\u0003\u000f!\tQa\u001d9be.T!!\u0003\u0006\u0002\r\u0005\u0004\u0018m\u00195f\u0015\u0005Y\u0011aA8sO\u000e\u0001QC\u0001\b\u001c'\r\u0001q\"\u0006\t\u0003!Mi\u0011!\u0005\u0006\u0002%\u0005)1oY1mC&\u0011A#\u0005\u0002\u0007\u0003:L(+\u001a4\u0011\tY9\u0012$G\u0007\u0002\u0005%\u0011\u0001D\u0001\u0002\u000e%\u0006tGm\\7TC6\u0004H.\u001a:\u0011\u0005iYB\u0002\u0001\u0003\u00069\u0001\u0011\r!\b\u0002\u0002)F\u0011a$\t\t\u0003!}I!\u0001I\t\u0003\u000f9{G\u000f[5oOB\u0011\u0001CI\u0005\u0003GE\u00111!\u00118z\u0011!)\u0003A!A!\u0002\u00131\u0013\u0001\u00034sC\u000e$\u0018n\u001c8\u0011\u0005A9\u0013B\u0001\u0015\u0012\u0005\u0019!u.\u001e2mK\"A!\u0006\u0001B\u0001B\u0003%1&\u0001\rvg\u0016<\u0015\r]*b[Bd\u0017N\\4JMB{7o]5cY\u0016\u0004\"\u0001\u0005\u0017\n\u00055\n\"a\u0002\"p_2,\u0017M\u001c\u0005\u0006_\u0001!\t\u0001M\u0001\u0007y%t\u0017\u000e\u001e \u0015\u0007E\u00124\u0007E\u0002\u0017\u0001eAQ!\n\u0018A\u0002\u0019BQA\u000b\u0018A\u0002-BQa\f\u0001\u0005\u0002U\"\"!\r\u001c\t\u000b\u0015\"\u0004\u0019\u0001\u0014\t\u000fa\u0002!\u0019!C\u0005s\u0005\u0019!O\\4\u0016\u0003i\u0002\"a\u000f\"\u000e\u0003qR!!\u0010 \u0002\u0019\u0011L7\u000f\u001e:jEV$\u0018n\u001c8\u000b\u0005}\u0002\u0015!B7bi\"\u001c$BA!\t\u0003\u001d\u0019w.\\7p]NL!a\u0011\u001f\u0003'A{\u0017n]:p]\u0012K7\u000f\u001e:jEV$\u0018n\u001c8\t\r\u0015\u0003\u0001\u0015!\u0003;\u0003\u0011\u0011hn\u001a\u0011\t\u000f\u001d\u0003!\u0019!C\u0005\u0011\u00061!O\\4HCB,\u0012!\u0013\t\u0003\u0015:k\u0011a\u0013\u0006\u0003\u000b1S\u0011!T\u0001\u0005U\u00064\u0018-\u0003\u0002P\u0017\n1!+\u00198e_6Da!\u0015\u0001!\u0002\u0013I\u0015a\u0002:oO\u001e\u000b\u0007\u000f\t\u0005\u0006'\u0002!\t\u0005V\u0001\bg\u0016$8+Z3e)\t)\u0006\f\u0005\u0002\u0011-&\u0011q+\u0005\u0002\u0005+:LG\u000fC\u0003Z%\u0002\u0007!,\u0001\u0003tK\u0016$\u0007C\u0001\t\\\u0013\ta\u0016C\u0001\u0003M_:<\u0007\u0002\u00030\u0001\u0011\u000b\u0007I\u0011B0\u0002-\u001d\f\u0007oU1na2Lgn\u001a*fa2\f7-Z7f]R,\u0012\u0001\u0019\t\u0003-\u0005L!A\u0019\u0002\u0003-\u001d\u000b\u0007oU1na2Lgn\u001a*fa2\f7-Z7f]RD\u0001\u0002\u001a\u0001\t\u0002\u0003\u0006K\u0001Y\u0001\u0018O\u0006\u00048+Y7qY&twMU3qY\u0006\u001cW-\\3oi\u0002BQA\u001a\u0001\u0005B\u001d\faa]1na2,G#\u00015\u0011\u0005AI\u0017B\u00016\u0012\u0005\rIe\u000e\u001e\u0005\u0006M\u0002!\t\u0005\u001c\u000b\u0003[f\u00042A\u001c<\u001a\u001d\tyGO\u0004\u0002qg6\t\u0011O\u0003\u0002s\u0019\u00051AH]8pizJ\u0011AE\u0005\u0003kF\tq\u0001]1dW\u0006<W-\u0003\u0002xq\nA\u0011\n^3sCR|'O\u0003\u0002v#!)!p\u001ba\u0001[\u0006)\u0011\u000e^3ng\")A\u0010\u0001C!{\u0006)1\r\\8oKR\t\u0011\u0007\u000b\u0002\u0001\u007fB!\u0011\u0011AA\u0004\u001b\t\t\u0019AC\u0002\u0002\u0006\u0019\t!\"\u00198o_R\fG/[8o\u0013\u0011\tI!a\u0001\u0003\u0019\u0011+g/\u001a7pa\u0016\u0014\u0018\t]5"
)
public class PoissonSampler<T> implements RandomSampler<T, T> {
   public final double org$apache$spark$util$random$PoissonSampler$$fraction;
   private final boolean useGapSamplingIfPossible;
   private final PoissonDistribution org$apache$spark$util$random$PoissonSampler$$rng;
   private final Random rngGap;
   private GapSamplingReplacement org$apache$spark$util$random$PoissonSampler$$gapSamplingReplacement;
   private volatile boolean bitmap$0;

   private GapSamplingReplacement org$apache$spark$util$random$PoissonSampler$$gapSamplingReplacement$lzycompute() {
      synchronized(this) {
         if (!this.bitmap$0) {
            this.org$apache$spark$util$random$PoissonSampler$$gapSamplingReplacement = new GapSamplingReplacement(this.org$apache$spark$util$random$PoissonSampler$$fraction, this.rngGap(), RandomSampler$.MODULE$.rngEpsilon());
            this.bitmap$0 = true;
         }

         BoxedUnit var10000 = BoxedUnit.UNIT;
      }

      return this.org$apache$spark$util$random$PoissonSampler$$gapSamplingReplacement;
   }

   public PoissonDistribution org$apache$spark$util$random$PoissonSampler$$rng() {
      return this.org$apache$spark$util$random$PoissonSampler$$rng;
   }

   private Random rngGap() {
      return this.rngGap;
   }

   public void setSeed(long seed) {
      this.org$apache$spark$util$random$PoissonSampler$$rng().reseedRandomGenerator(seed);
      this.rngGap().setSeed(seed);
   }

   public GapSamplingReplacement org$apache$spark$util$random$PoissonSampler$$gapSamplingReplacement() {
      return this.bitmap$0 ? this.org$apache$spark$util$random$PoissonSampler$$gapSamplingReplacement : this.org$apache$spark$util$random$PoissonSampler$$gapSamplingReplacement$lzycompute();
   }

   public int sample() {
      return this.org$apache$spark$util$random$PoissonSampler$$fraction <= (double)0.0F ? 0 : (this.useGapSamplingIfPossible && this.org$apache$spark$util$random$PoissonSampler$$fraction <= RandomSampler$.MODULE$.defaultMaxGapSamplingFraction() ? this.org$apache$spark$util$random$PoissonSampler$$gapSamplingReplacement().sample() : this.org$apache$spark$util$random$PoissonSampler$$rng().sample());
   }

   public Iterator<T> sample(Iterator<T> items) {
      Iterator var10000;
      if (this.org$apache$spark$util$random$PoissonSampler$$fraction <= (double)0.0F) {
         var10000 = scala.package$.MODULE$.Iterator().empty();
      } else {
         boolean useGapSampling = this.useGapSamplingIfPossible && this.org$apache$spark$util$random$PoissonSampler$$fraction <= RandomSampler$.MODULE$.defaultMaxGapSamplingFraction();
         var10000 = items.flatMap(new Serializable(this, useGapSampling) {
            public static final long serialVersionUID = 0L;
            // $FF: synthetic field
            private final PoissonSampler $outer;
            private final boolean useGapSampling$1;

            public final GenTraversableOnce<T> apply(T item) {
               int count = this.useGapSampling$1 ? this.$outer.org$apache$spark$util$random$PoissonSampler$$gapSamplingReplacement().sample() : this.$outer.org$apache$spark$util$random$PoissonSampler$$rng().sample();
               return count == 0 ? scala.package$.MODULE$.Iterator().empty() : scala.package$.MODULE$.Iterator().fill(count, new Serializable(this, item) {
                  public static final long serialVersionUID = 0L;
                  private final Object item$1;

                  public final T apply() {
                     return (T)this.item$1;
                  }

                  public {
                     this.item$1 = item$1;
                  }
               });
            }

            public {
               if ($outer == null) {
                  throw null;
               } else {
                  this.$outer = $outer;
                  this.useGapSampling$1 = useGapSampling$1;
               }
            }
         });
      }

      return var10000;
   }

   public PoissonSampler<T> clone() {
      return new PoissonSampler<T>(this.org$apache$spark$util$random$PoissonSampler$$fraction, this.useGapSamplingIfPossible);
   }

   public PoissonSampler(double fraction, boolean useGapSamplingIfPossible) {
      this.org$apache$spark$util$random$PoissonSampler$$fraction = fraction;
      this.useGapSamplingIfPossible = useGapSamplingIfPossible;
      RandomSampler$class.$init$(this);
      Predef$.MODULE$.require(fraction >= (double)0.0F - RandomSampler$.MODULE$.roundingEpsilon(), new Serializable(this) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final PoissonSampler $outer;

         public final String apply() {
            return (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Sampling fraction (", ") must be >= 0"})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{BoxesRunTime.boxToDouble(this.$outer.org$apache$spark$util$random$PoissonSampler$$fraction)}));
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
            }
         }
      });
      this.org$apache$spark$util$random$PoissonSampler$$rng = new PoissonDistribution(fraction > (double)0.0F ? fraction : (double)1.0F);
      this.rngGap = RandomSampler$.MODULE$.newDefaultRNG();
   }

   public PoissonSampler(double fraction) {
      this(fraction, true);
   }
}
