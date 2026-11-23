package org.apache.spark.util.random;

import java.util.Random;
import scala.Predef$;
import scala.Serializable;
import scala.StringContext;
import scala.reflect.ScalaSignature;
import scala.runtime.BoxesRunTime;

@ScalaSignature(
   bytes = "\u0006\u0001I4Q!\u0001\u0002\u0001\r1\u0011acR1q'\u0006l\u0007\u000f\\5oOJ+\u0007\u000f\\1dK6,g\u000e\u001e\u0006\u0003\u0007\u0011\taA]1oI>l'BA\u0003\u0007\u0003\u0011)H/\u001b7\u000b\u0005\u001dA\u0011!B:qCJ\\'BA\u0005\u000b\u0003\u0019\t\u0007/Y2iK*\t1\"A\u0002pe\u001e\u001c2\u0001A\u0007\u0014!\tq\u0011#D\u0001\u0010\u0015\u0005\u0001\u0012!B:dC2\f\u0017B\u0001\n\u0010\u0005\u0019\te.\u001f*fMB\u0011a\u0002F\u0005\u0003+=\u0011AbU3sS\u0006d\u0017N_1cY\u0016D\u0001b\u0006\u0001\u0003\u0006\u0004%\t!G\u0001\u0002M\u000e\u0001Q#\u0001\u000e\u0011\u00059Y\u0012B\u0001\u000f\u0010\u0005\u0019!u.\u001e2mK\"Aa\u0004\u0001B\u0001B\u0003%!$\u0001\u0002gA!A\u0001\u0005\u0001BC\u0002\u0013\u0005\u0011%A\u0002s]\u001e,\u0012A\t\t\u0003G\u001dj\u0011\u0001\n\u0006\u0003\u000b\u0015R\u0011AJ\u0001\u0005U\u00064\u0018-\u0003\u0002)I\t1!+\u00198e_6D\u0001B\u000b\u0001\u0003\u0002\u0003\u0006IAI\u0001\u0005e:<\u0007\u0005\u0003\u0005-\u0001\t\u0005\t\u0015!\u0003\u001b\u0003\u001d)\u0007o]5m_:DQA\f\u0001\u0005\u0002=\na\u0001P5oSRtD\u0003\u0002\u00193gQ\u0002\"!\r\u0001\u000e\u0003\tAQaF\u0017A\u0002iAq\u0001I\u0017\u0011\u0002\u0003\u0007!\u0005C\u0004-[A\u0005\t\u0019\u0001\u000e\t\u000fY\u0002!\u0019!C\t3\u0005\t\u0011\u000f\u0003\u00049\u0001\u0001\u0006IAG\u0001\u0003c\u0002BQA\u000f\u0001\u0005\u0012m\n!\u0002]8jgN|gnR#2+\u0005a\u0004C\u0001\b>\u0013\tqtBA\u0002J]RDq\u0001\u0011\u0001A\u0002\u0013%1(\u0001\td_VtGOR8s\tJ|\u0007\u000f]5oO\"9!\t\u0001a\u0001\n\u0013\u0019\u0015\u0001F2pk:$hi\u001c:Ee>\u0004\b/\u001b8h?\u0012*\u0017\u000f\u0006\u0002E\u000fB\u0011a\"R\u0005\u0003\r>\u0011A!\u00168ji\"9\u0001*QA\u0001\u0002\u0004a\u0014a\u0001=%c!1!\n\u0001Q!\nq\n\u0011cY8v]R4uN\u001d#s_B\u0004\u0018N\\4!\u0011\u0015a\u0005\u0001\"\u0001N\u0003\u0019\u0019\u0018-\u001c9mKR\tA\bC\u0003P\u0001\u0011%\u0001+A\u0004bIZ\fgnY3\u0015\u0003\u0011;\u0001B\u0015\u0002\u0002\u0002#\u0005aaU\u0001\u0017\u000f\u0006\u00048+Y7qY&twMU3qY\u0006\u001cW-\\3oiB\u0011\u0011\u0007\u0016\u0004\t\u0003\t\t\t\u0011#\u0001\u0007+N\u0019A+D\n\t\u000b9\"F\u0011A,\u0015\u0003MCq!\u0017+\u0012\u0002\u0013\u0005!,A\u000e%Y\u0016\u001c8/\u001b8ji\u0012:'/Z1uKJ$C-\u001a4bk2$HEM\u000b\u00027*\u0012!\u0005X\u0016\u0002;B\u0011alY\u0007\u0002?*\u0011\u0001-Y\u0001\nk:\u001c\u0007.Z2lK\u0012T!AY\b\u0002\u0015\u0005tgn\u001c;bi&|g.\u0003\u0002e?\n\tRO\\2iK\u000e\\W\r\u001a,be&\fgnY3\t\u000f\u0019$\u0016\u0013!C\u0001O\u0006YB\u0005\\3tg&t\u0017\u000e\u001e\u0013he\u0016\fG/\u001a:%I\u00164\u0017-\u001e7uIM*\u0012\u0001\u001b\u0016\u00035qCqA\u001b+\u0002\u0002\u0013%1.A\u0006sK\u0006$'+Z:pYZ,G#\u00017\u0011\u00055\u0004X\"\u00018\u000b\u0005=,\u0013\u0001\u00027b]\u001eL!!\u001d8\u0003\r=\u0013'.Z2u\u0001"
)
public class GapSamplingReplacement implements Serializable {
   private final double f;
   private final Random rng;
   public final double org$apache$spark$util$random$GapSamplingReplacement$$epsilon;
   private final double q;
   private int countForDropping;

   public static double $lessinit$greater$default$3() {
      return GapSamplingReplacement$.MODULE$.$lessinit$greater$default$3();
   }

   public static Random $lessinit$greater$default$2() {
      return GapSamplingReplacement$.MODULE$.$lessinit$greater$default$2();
   }

   public double f() {
      return this.f;
   }

   public Random rng() {
      return this.rng;
   }

   public double q() {
      return this.q;
   }

   public int poissonGE1() {
      double pp = this.q() + ((double)1.0F - this.q()) * this.rng().nextDouble();
      int r = 1;

      for(double var4 = pp * this.rng().nextDouble(); var4 > this.q(); var4 *= this.rng().nextDouble()) {
         ++r;
      }

      return r;
   }

   private int countForDropping() {
      return this.countForDropping;
   }

   private void countForDropping_$eq(int x$1) {
      this.countForDropping = x$1;
   }

   public int sample() {
      int var10000;
      if (this.countForDropping() > 0) {
         this.countForDropping_$eq(this.countForDropping() - 1);
         var10000 = 0;
      } else {
         int r = this.poissonGE1();
         this.advance();
         var10000 = r;
      }

      return var10000;
   }

   private void advance() {
      double u = scala.math.package$.MODULE$.max(this.rng().nextDouble(), this.org$apache$spark$util$random$GapSamplingReplacement$$epsilon);
      this.countForDropping_$eq((int)(scala.math.package$.MODULE$.log(u) / -this.f()));
   }

   public GapSamplingReplacement(double f, Random rng, double epsilon) {
      this.f = f;
      this.rng = rng;
      this.org$apache$spark$util$random$GapSamplingReplacement$$epsilon = epsilon;
      Predef$.MODULE$.require(f > (double)0.0F, new Serializable(this) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final GapSamplingReplacement $outer;

         public final String apply() {
            return (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Sampling fraction (", ") must be > 0"})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{BoxesRunTime.boxToDouble(this.$outer.f())}));
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
         private final GapSamplingReplacement $outer;

         public final String apply() {
            return (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"epsilon (", ") must be > 0"})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{BoxesRunTime.boxToDouble(this.$outer.org$apache$spark$util$random$GapSamplingReplacement$$epsilon)}));
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
            }
         }
      });
      this.q = scala.math.package$.MODULE$.exp(-f);
      this.countForDropping = 0;
      this.advance();
   }
}
