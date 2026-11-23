package org.apache.spark.util.random;

import java.util.Random;
import org.apache.spark.annotation.DeveloperApi;
import scala.Predef$;
import scala.Serializable;
import scala.StringContext;
import scala.collection.Iterator;
import scala.reflect.ScalaSignature;
import scala.runtime.BoxesRunTime;

@DeveloperApi
@ScalaSignature(
   bytes = "\u0006\u0001\u0005\ra\u0001B\u0001\u0003\u00015\u0011ACQ3s]>,H\u000e\\5DK2d7+Y7qY\u0016\u0014(BA\u0002\u0005\u0003\u0019\u0011\u0018M\u001c3p[*\u0011QAB\u0001\u0005kRLGN\u0003\u0002\b\u0011\u0005)1\u000f]1sW*\u0011\u0011BC\u0001\u0007CB\f7\r[3\u000b\u0003-\t1a\u001c:h\u0007\u0001)\"AD\u000e\u0014\u0007\u0001yQ\u0003\u0005\u0002\u0011'5\t\u0011CC\u0001\u0013\u0003\u0015\u00198-\u00197b\u0013\t!\u0012C\u0001\u0004B]f\u0014VM\u001a\t\u0005-]I\u0012$D\u0001\u0003\u0013\tA\"AA\u0007SC:$w.\\*b[BdWM\u001d\t\u00035ma\u0001\u0001B\u0003\u001d\u0001\t\u0007QDA\u0001U#\tq\u0012\u0005\u0005\u0002\u0011?%\u0011\u0001%\u0005\u0002\b\u001d>$\b.\u001b8h!\t\u0001\"%\u0003\u0002$#\t\u0019\u0011I\\=\t\u0011\u0015\u0002!\u0011!Q\u0001\n\u0019\n!\u0001\u001c2\u0011\u0005A9\u0013B\u0001\u0015\u0012\u0005\u0019!u.\u001e2mK\"A!\u0006\u0001B\u0001B\u0003%a%\u0001\u0002vE\"AA\u0006\u0001B\u0001B\u0003%Q&\u0001\u0006d_6\u0004H.Z7f]R\u0004\"\u0001\u0005\u0018\n\u0005=\n\"a\u0002\"p_2,\u0017M\u001c\u0005\u0006c\u0001!\tAM\u0001\u0007y%t\u0017\u000e\u001e \u0015\tM\"TG\u000e\t\u0004-\u0001I\u0002\"B\u00131\u0001\u00041\u0003\"\u0002\u00161\u0001\u00041\u0003b\u0002\u00171!\u0003\u0005\r!\f\u0005\bq\u0001\u0011\r\u0011\"\u0003:\u0003\r\u0011hnZ\u000b\u0002uA\u00111hP\u0007\u0002y)\u0011Q!\u0010\u0006\u0002}\u0005!!.\u0019<b\u0013\t\u0001EH\u0001\u0004SC:$w.\u001c\u0005\u0007\u0005\u0002\u0001\u000b\u0011\u0002\u001e\u0002\tItw\r\t\u0005\u0006\t\u0002!\t%R\u0001\bg\u0016$8+Z3e)\t1\u0015\n\u0005\u0002\u0011\u000f&\u0011\u0001*\u0005\u0002\u0005+:LG\u000fC\u0003K\u0007\u0002\u00071*\u0001\u0003tK\u0016$\u0007C\u0001\tM\u0013\ti\u0015C\u0001\u0003M_:<\u0007\"B(\u0001\t\u0003\u0002\u0016AB:b[BdW\rF\u0001R!\t\u0001\"+\u0003\u0002T#\t\u0019\u0011J\u001c;\t\u000bU\u0003A\u0011\u0001,\u0002\u001f\rdwN\\3D_6\u0004H.Z7f]R$\u0012a\r\u0005\u00061\u0002!\tEV\u0001\u0006G2|g.\u001a\u0015\u0003\u0001i\u0003\"a\u00170\u000e\u0003qS!!\u0018\u0004\u0002\u0015\u0005tgn\u001c;bi&|g.\u0003\u0002`9\naA)\u001a<fY>\u0004XM]!qS\u001e9\u0011MAA\u0001\u0012\u0003\u0011\u0017\u0001\u0006\"fe:|W\u000f\u001c7j\u0007\u0016dGnU1na2,'\u000f\u0005\u0002\u0017G\u001a9\u0011AAA\u0001\u0012\u0003!7cA2\u0010KB\u0011\u0001CZ\u0005\u0003OF\u0011AbU3sS\u0006d\u0017N_1cY\u0016DQ!M2\u0005\u0002%$\u0012A\u0019\u0005\bW\u000e\f\n\u0011\"\u0001m\u0003m!C.Z:tS:LG\u000fJ4sK\u0006$XM\u001d\u0013eK\u001a\fW\u000f\u001c;%gU\u0011Qn^\u000b\u0002]*\u0012Qf\\\u0016\u0002aB\u0011\u0011/^\u0007\u0002e*\u00111\u000f^\u0001\nk:\u001c\u0007.Z2lK\u0012T!!X\t\n\u0005Y\u0014(!E;oG\",7m[3e-\u0006\u0014\u0018.\u00198dK\u0012)AD\u001bb\u0001;!9\u0011pYA\u0001\n\u0013Q\u0018a\u0003:fC\u0012\u0014Vm]8mm\u0016$\u0012a\u001f\t\u0003y~l\u0011! \u0006\u0003}v\nA\u0001\\1oO&\u0019\u0011\u0011A?\u0003\r=\u0013'.Z2u\u0001"
)
public class BernoulliCellSampler<T> implements RandomSampler<T, T> {
   public final double org$apache$spark$util$random$BernoulliCellSampler$$lb;
   public final double org$apache$spark$util$random$BernoulliCellSampler$$ub;
   private final boolean complement;
   private final Random rng;

   public static <T> boolean $lessinit$greater$default$3() {
      return BernoulliCellSampler$.MODULE$.$lessinit$greater$default$3();
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

   public int sample() {
      int var10000;
      if (this.org$apache$spark$util$random$BernoulliCellSampler$$ub - this.org$apache$spark$util$random$BernoulliCellSampler$$lb <= (double)0.0F) {
         var10000 = this.complement ? 1 : 0;
      } else {
         double x = this.rng().nextDouble();
         int n = x >= this.org$apache$spark$util$random$BernoulliCellSampler$$lb && x < this.org$apache$spark$util$random$BernoulliCellSampler$$ub ? 1 : 0;
         var10000 = this.complement ? 1 - n : n;
      }

      return var10000;
   }

   public BernoulliCellSampler<T> cloneComplement() {
      return new BernoulliCellSampler<T>(this.org$apache$spark$util$random$BernoulliCellSampler$$lb, this.org$apache$spark$util$random$BernoulliCellSampler$$ub, !this.complement);
   }

   public BernoulliCellSampler<T> clone() {
      return new BernoulliCellSampler<T>(this.org$apache$spark$util$random$BernoulliCellSampler$$lb, this.org$apache$spark$util$random$BernoulliCellSampler$$ub, this.complement);
   }

   public BernoulliCellSampler(double lb, double ub, boolean complement) {
      this.org$apache$spark$util$random$BernoulliCellSampler$$lb = lb;
      this.org$apache$spark$util$random$BernoulliCellSampler$$ub = ub;
      this.complement = complement;
      RandomSampler$class.$init$(this);
      Predef$.MODULE$.require(lb <= ub + RandomSampler$.MODULE$.roundingEpsilon(), new Serializable(this) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final BernoulliCellSampler $outer;

         public final String apply() {
            return (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Lower bound (", ") must be <= upper bound (", ")"})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{BoxesRunTime.boxToDouble(this.$outer.org$apache$spark$util$random$BernoulliCellSampler$$lb), BoxesRunTime.boxToDouble(this.$outer.org$apache$spark$util$random$BernoulliCellSampler$$ub)}));
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
            }
         }
      });
      Predef$.MODULE$.require(lb >= (double)0.0F - RandomSampler$.MODULE$.roundingEpsilon(), new Serializable(this) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final BernoulliCellSampler $outer;

         public final String apply() {
            return (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Lower bound (", ") must be >= 0.0"})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{BoxesRunTime.boxToDouble(this.$outer.org$apache$spark$util$random$BernoulliCellSampler$$lb)}));
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
            }
         }
      });
      Predef$.MODULE$.require(ub <= (double)1.0F + RandomSampler$.MODULE$.roundingEpsilon(), new Serializable(this) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final BernoulliCellSampler $outer;

         public final String apply() {
            return (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Upper bound (", ") must be <= 1.0"})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{BoxesRunTime.boxToDouble(this.$outer.org$apache$spark$util$random$BernoulliCellSampler$$ub)}));
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
            }
         }
      });
      this.rng = new XORShiftRandom();
   }
}
