package org.apache.spark.util.random;

import java.util.Random;
import scala.Predef$;
import scala.Serializable;
import scala.StringContext;
import scala.reflect.ScalaSignature;
import scala.runtime.BoxesRunTime;

@ScalaSignature(
   bytes = "\u0006\u0001-4Q!\u0001\u0002\u0001\r1\u00111bR1q'\u0006l\u0007\u000f\\5oO*\u00111\u0001B\u0001\u0007e\u0006tGm\\7\u000b\u0005\u00151\u0011\u0001B;uS2T!a\u0002\u0005\u0002\u000bM\u0004\u0018M]6\u000b\u0005%Q\u0011AB1qC\u000eDWMC\u0001\f\u0003\ry'oZ\n\u0004\u00015\u0019\u0002C\u0001\b\u0012\u001b\u0005y!\"\u0001\t\u0002\u000bM\u001c\u0017\r\\1\n\u0005Iy!AB!osJ+g\r\u0005\u0002\u000f)%\u0011Qc\u0004\u0002\r'\u0016\u0014\u0018.\u00197ju\u0006\u0014G.\u001a\u0005\t/\u0001\u0011\t\u0011)A\u00053\u0005\tam\u0001\u0001\u0011\u00059Q\u0012BA\u000e\u0010\u0005\u0019!u.\u001e2mK\"AQ\u0004\u0001B\u0001B\u0003%a$A\u0002s]\u001e\u0004\"aH\u0012\u000e\u0003\u0001R!!B\u0011\u000b\u0003\t\nAA[1wC&\u0011A\u0005\t\u0002\u0007%\u0006tGm\\7\t\u0011\u0019\u0002!\u0011!Q\u0001\ne\tq!\u001a9tS2|g\u000eC\u0003)\u0001\u0011\u0005\u0011&\u0001\u0004=S:LGO\u0010\u000b\u0005U1jc\u0006\u0005\u0002,\u00015\t!\u0001C\u0003\u0018O\u0001\u0007\u0011\u0004C\u0004\u001eOA\u0005\t\u0019\u0001\u0010\t\u000f\u0019:\u0003\u0013!a\u00013!9\u0001\u0007\u0001b\u0001\n\u0013\t\u0014a\u00017ocV\t\u0011\u0004\u0003\u00044\u0001\u0001\u0006I!G\u0001\u0005Y:\f\b\u0005C\u00036\u0001\u0011\u0005a'\u0001\u0004tC6\u0004H.\u001a\u000b\u0002oA\u0011a\u0002O\u0005\u0003s=\u00111!\u00138u\u0011\u001dY\u0004\u00011A\u0005\nq\n\u0001cY8v]R4uN\u001d#s_B\u0004\u0018N\\4\u0016\u0003]BqA\u0010\u0001A\u0002\u0013%q(\u0001\u000bd_VtGOR8s\tJ|\u0007\u000f]5oO~#S-\u001d\u000b\u0003\u0001\u000e\u0003\"AD!\n\u0005\t{!\u0001B+oSRDq\u0001R\u001f\u0002\u0002\u0003\u0007q'A\u0002yIEBaA\u0012\u0001!B\u00139\u0014!E2pk:$hi\u001c:Ee>\u0004\b/\u001b8hA!)\u0001\n\u0001C\u0005\u0013\u00069\u0011\r\u001a<b]\u000e,G#\u0001!\b\u0011-\u0013\u0011\u0011!E\u0001\r1\u000b1bR1q'\u0006l\u0007\u000f\\5oOB\u00111&\u0014\u0004\t\u0003\t\t\t\u0011#\u0001\u0007\u001dN\u0019Q*D\n\t\u000b!jE\u0011\u0001)\u0015\u00031CqAU'\u0012\u0002\u0013\u00051+A\u000e%Y\u0016\u001c8/\u001b8ji\u0012:'/Z1uKJ$C-\u001a4bk2$HEM\u000b\u0002)*\u0012a$V\u0016\u0002-B\u0011q\u000bX\u0007\u00021*\u0011\u0011LW\u0001\nk:\u001c\u0007.Z2lK\u0012T!aW\b\u0002\u0015\u0005tgn\u001c;bi&|g.\u0003\u0002^1\n\tRO\\2iK\u000e\\W\r\u001a,be&\fgnY3\t\u000f}k\u0015\u0013!C\u0001A\u0006YB\u0005\\3tg&t\u0017\u000e\u001e\u0013he\u0016\fG/\u001a:%I\u00164\u0017-\u001e7uIM*\u0012!\u0019\u0016\u00033UCqaY'\u0002\u0002\u0013%A-A\u0006sK\u0006$'+Z:pYZ,G#A3\u0011\u0005\u0019LW\"A4\u000b\u0005!\f\u0013\u0001\u00027b]\u001eL!A[4\u0003\r=\u0013'.Z2u\u0001"
)
public class GapSampling implements Serializable {
   public final double org$apache$spark$util$random$GapSampling$$f;
   private final Random rng;
   public final double org$apache$spark$util$random$GapSampling$$epsilon;
   private final double lnq;
   private int countForDropping;

   public static double $lessinit$greater$default$3() {
      return GapSampling$.MODULE$.$lessinit$greater$default$3();
   }

   public static Random $lessinit$greater$default$2() {
      return GapSampling$.MODULE$.$lessinit$greater$default$2();
   }

   private double lnq() {
      return this.lnq;
   }

   public int sample() {
      byte var10000;
      if (this.countForDropping() > 0) {
         this.countForDropping_$eq(this.countForDropping() - 1);
         var10000 = 0;
      } else {
         this.advance();
         var10000 = 1;
      }

      return var10000;
   }

   private int countForDropping() {
      return this.countForDropping;
   }

   private void countForDropping_$eq(int x$1) {
      this.countForDropping = x$1;
   }

   private void advance() {
      double u = scala.math.package$.MODULE$.max(this.rng.nextDouble(), this.org$apache$spark$util$random$GapSampling$$epsilon);
      this.countForDropping_$eq((int)(scala.math.package$.MODULE$.log(u) / this.lnq()));
   }

   public GapSampling(double f, Random rng, double epsilon) {
      this.org$apache$spark$util$random$GapSampling$$f = f;
      this.rng = rng;
      this.org$apache$spark$util$random$GapSampling$$epsilon = epsilon;
      Predef$.MODULE$.require(f > (double)0.0F && f < (double)1.0F, new Serializable(this) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final GapSampling $outer;

         public final String apply() {
            return (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Sampling fraction (", ") must reside on open interval (0, 1)"})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{BoxesRunTime.boxToDouble(this.$outer.org$apache$spark$util$random$GapSampling$$f)}));
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
            }
         }
      });
      Predef$.MODULE$.require(epsilon > (double)0.0F, new Serializable(this) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final GapSampling $outer;

         public final String apply() {
            return (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"epsilon (", ") must be > 0"})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{BoxesRunTime.boxToDouble(this.$outer.org$apache$spark$util$random$GapSampling$$epsilon)}));
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
            }
         }
      });
      this.lnq = scala.math.package$.MODULE$.log1p(-f);
      this.countForDropping = 0;
      this.advance();
   }
}
