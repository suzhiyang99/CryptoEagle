package org.apache.spark.util;

import scala.Predef$;
import scala.Serializable;
import scala.collection.Seq;
import scala.collection.TraversableOnce;
import scala.collection.immutable.Nil$;
import scala.collection.immutable.StringOps;
import scala.reflect.ScalaSignature;
import scala.runtime.BoxesRunTime;

@ScalaSignature(
   bytes = "\u0006\u0001\u0005\u001dd\u0001B\u0001\u0003\u0001-\u00111b\u0015;bi\u000e{WO\u001c;fe*\u00111\u0001B\u0001\u0005kRLGN\u0003\u0002\u0006\r\u0005)1\u000f]1sW*\u0011q\u0001C\u0001\u0007CB\f7\r[3\u000b\u0003%\t1a\u001c:h\u0007\u0001\u00192\u0001\u0001\u0007\u0013!\ti\u0001#D\u0001\u000f\u0015\u0005y\u0011!B:dC2\f\u0017BA\t\u000f\u0005\u0019\te.\u001f*fMB\u0011QbE\u0005\u0003)9\u0011AbU3sS\u0006d\u0017N_1cY\u0016D\u0001B\u0006\u0001\u0003\u0002\u0003\u0006IaF\u0001\u0007m\u0006dW/Z:\u0011\u0007a\u00013E\u0004\u0002\u001a=9\u0011!$H\u0007\u00027)\u0011ADC\u0001\u0007yI|w\u000e\u001e \n\u0003=I!a\b\b\u0002\u000fA\f7m[1hK&\u0011\u0011E\t\u0002\u0010)J\fg/\u001a:tC\ndWm\u00148dK*\u0011qD\u0004\t\u0003\u001b\u0011J!!\n\b\u0003\r\u0011{WO\u00197f\u0011\u00159\u0003\u0001\"\u0001)\u0003\u0019a\u0014N\\5u}Q\u0011\u0011f\u000b\t\u0003U\u0001i\u0011A\u0001\u0005\u0006-\u0019\u0002\ra\u0006\u0005\b[\u0001\u0001\r\u0011\"\u0003/\u0003\u0005qW#A\u0018\u0011\u00055\u0001\u0014BA\u0019\u000f\u0005\u0011auN\\4\t\u000fM\u0002\u0001\u0019!C\u0005i\u0005)an\u0018\u0013fcR\u0011Q\u0007\u000f\t\u0003\u001bYJ!a\u000e\b\u0003\tUs\u0017\u000e\u001e\u0005\bsI\n\t\u00111\u00010\u0003\rAH%\r\u0005\u0007w\u0001\u0001\u000b\u0015B\u0018\u0002\u00059\u0004\u0003bB\u001f\u0001\u0001\u0004%IAP\u0001\u0003[V,\u0012a\t\u0005\b\u0001\u0002\u0001\r\u0011\"\u0003B\u0003\u0019iWo\u0018\u0013fcR\u0011QG\u0011\u0005\bs}\n\t\u00111\u0001$\u0011\u0019!\u0005\u0001)Q\u0005G\u0005\u0019Q.\u001e\u0011\t\u000f\u0019\u0003\u0001\u0019!C\u0005}\u0005\u0011QN\r\u0005\b\u0011\u0002\u0001\r\u0011\"\u0003J\u0003\u0019i'g\u0018\u0013fcR\u0011QG\u0013\u0005\bs\u001d\u000b\t\u00111\u0001$\u0011\u0019a\u0005\u0001)Q\u0005G\u0005\u0019QN\r\u0011\t\u000f9\u0003\u0001\u0019!C\u0005}\u0005AQ.\u0019=WC2,X\rC\u0004Q\u0001\u0001\u0007I\u0011B)\u0002\u00195\f\u0007PV1mk\u0016|F%Z9\u0015\u0005U\u0012\u0006bB\u001dP\u0003\u0003\u0005\ra\t\u0005\u0007)\u0002\u0001\u000b\u0015B\u0012\u0002\u00135\f\u0007PV1mk\u0016\u0004\u0003b\u0002,\u0001\u0001\u0004%IAP\u0001\t[&tg+\u00197vK\"9\u0001\f\u0001a\u0001\n\u0013I\u0016\u0001D7j]Z\u000bG.^3`I\u0015\fHCA\u001b[\u0011\u001dIt+!AA\u0002\rBa\u0001\u0018\u0001!B\u0013\u0019\u0013!C7j]Z\u000bG.^3!\u0011\u00159\u0003\u0001\"\u0001_)\u0005I\u0003\"\u00021\u0001\t\u0003\t\u0017!B7fe\u001e,GCA\u0015c\u0011\u0015\u0019w\f1\u0001$\u0003\u00151\u0018\r\\;f\u0011\u0015\u0001\u0007\u0001\"\u0001f)\tIc\rC\u0003\u0017I\u0002\u0007q\u0003C\u0003a\u0001\u0011\u0005\u0001\u000e\u0006\u0002*S\")!n\u001aa\u0001S\u0005)q\u000e\u001e5fe\")A\u000e\u0001C\u0001=\u0006!1m\u001c9z\u0011\u0015q\u0007\u0001\"\u0001/\u0003\u0015\u0019w.\u001e8u\u0011\u0015\u0001\b\u0001\"\u0001?\u0003\u0011iW-\u00198\t\u000bI\u0004A\u0011\u0001 \u0002\u0007M,X\u000eC\u0003u\u0001\u0011\u0005a(A\u0002nCbDQA\u001e\u0001\u0005\u0002y\n1!\\5o\u0011\u0015A\b\u0001\"\u0001?\u0003!1\u0018M]5b]\u000e,\u0007\"\u0002>\u0001\t\u0003q\u0014a\u00039paZ\u000b'/[1oG\u0016DC!\u001f?\u0002\u0006A\u0019Q0!\u0001\u000e\u0003yT!a \u0003\u0002\u0015\u0005tgn\u001c;bi&|g.C\u0002\u0002\u0004y\u0014QaU5oG\u0016\f#!a\u0002\u0002\u000bIr\u0013G\f\u0019\t\r\u0005-\u0001\u0001\"\u0001?\u00039\u0019\u0018-\u001c9mKZ\u000b'/[1oG\u0016Da!a\u0004\u0001\t\u0003q\u0014!B:uI\u00164\bBBA\n\u0001\u0011\u0005a(\u0001\u0005q_B\u001cF\u000fZ3wQ\u0015\t\t\u0002`A\u0003\u0011\u0019\tI\u0002\u0001C\u0001}\u0005Y1/Y7qY\u0016\u001cF\u000fZ3w\u0011\u001d\ti\u0002\u0001C!\u0003?\t\u0001\u0002^8TiJLgn\u001a\u000b\u0003\u0003C\u0001B!a\t\u0002*9\u0019Q\"!\n\n\u0007\u0005\u001db\"\u0001\u0004Qe\u0016$WMZ\u0005\u0005\u0003W\tiC\u0001\u0004TiJLgn\u001a\u0006\u0004\u0003OqqaBA\u0019\u0005!\u0005\u00111G\u0001\f'R\fGoQ8v]R,'\u000fE\u0002+\u0003k1a!\u0001\u0002\t\u0002\u0005]2\u0003BA\u001b\u0019IAqaJA\u001b\t\u0003\tY\u0004\u0006\u0002\u00024!A\u0011qHA\u001b\t\u0003\t\t%A\u0003baBd\u0017\u0010F\u0002*\u0003\u0007BaAFA\u001f\u0001\u00049\u0002\u0002CA \u0003k!\t!a\u0012\u0015\u0007%\nI\u0005C\u0004\u0017\u0003\u000b\u0002\r!a\u0013\u0011\t5\tieI\u0005\u0004\u0003\u001fr!A\u0003\u001fsKB,\u0017\r^3e}!Q\u00111KA\u001b\u0003\u0003%I!!\u0016\u0002\u0017I,\u0017\r\u001a*fg>dg/\u001a\u000b\u0003\u0003/\u0002B!!\u0017\u0002d5\u0011\u00111\f\u0006\u0005\u0003;\ny&\u0001\u0003mC:<'BAA1\u0003\u0011Q\u0017M^1\n\t\u0005\u0015\u00141\f\u0002\u0007\u001f\nTWm\u0019;"
)
public class StatCounter implements Serializable {
   private long n;
   private double mu;
   private double m2;
   private double maxValue;
   private double minValue;

   public static StatCounter apply(Seq<Object> var0) {
      return StatCounter$.MODULE$.apply(var0);
   }

   public static StatCounter apply(TraversableOnce<Object> var0) {
      return StatCounter$.MODULE$.apply(var0);
   }

   private long n() {
      return this.n;
   }

   private void n_$eq(long x$1) {
      this.n = x$1;
   }

   private double mu() {
      return this.mu;
   }

   private void mu_$eq(double x$1) {
      this.mu = x$1;
   }

   private double m2() {
      return this.m2;
   }

   private void m2_$eq(double x$1) {
      this.m2 = x$1;
   }

   private double maxValue() {
      return this.maxValue;
   }

   private void maxValue_$eq(double x$1) {
      this.maxValue = x$1;
   }

   private double minValue() {
      return this.minValue;
   }

   private void minValue_$eq(double x$1) {
      this.minValue = x$1;
   }

   public StatCounter merge(double value) {
      double delta = value - this.mu();
      this.n_$eq(this.n() + 1L);
      this.mu_$eq(this.mu() + delta / (double)this.n());
      this.m2_$eq(this.m2() + delta * (value - this.mu()));
      this.maxValue_$eq(scala.math.package$.MODULE$.max(this.maxValue(), value));
      this.minValue_$eq(scala.math.package$.MODULE$.min(this.minValue(), value));
      return this;
   }

   public StatCounter merge(TraversableOnce<Object> values) {
      values.foreach(new Serializable(this) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final StatCounter $outer;

         public final StatCounter apply(double v) {
            return this.$outer.merge(v);
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
            }
         }
      });
      return this;
   }

   public StatCounter merge(StatCounter other) {
      StatCounter var10000;
      label39: {
         if (other == null) {
            if (this == null) {
               break label39;
            }
         } else if (other.equals(this)) {
            break label39;
         }

         if (this.n() == 0L) {
            this.mu_$eq(other.mu());
            this.m2_$eq(other.m2());
            this.n_$eq(other.n());
            this.maxValue_$eq(other.maxValue());
            this.minValue_$eq(other.minValue());
         } else if (other.n() != 0L) {
            double delta = other.mu() - this.mu();
            if (other.n() * 10L < this.n()) {
               this.mu_$eq(this.mu() + delta * (double)other.n() / (double)(this.n() + other.n()));
            } else if (this.n() * 10L < other.n()) {
               this.mu_$eq(other.mu() - delta * (double)this.n() / (double)(this.n() + other.n()));
            } else {
               this.mu_$eq((this.mu() * (double)this.n() + other.mu() * (double)other.n()) / (double)(this.n() + other.n()));
            }

            this.m2_$eq(this.m2() + other.m2() + delta * delta * (double)this.n() * (double)other.n() / (double)(this.n() + other.n()));
            this.n_$eq(this.n() + other.n());
            this.maxValue_$eq(scala.math.package$.MODULE$.max(this.maxValue(), other.maxValue()));
            this.minValue_$eq(scala.math.package$.MODULE$.min(this.minValue(), other.minValue()));
         }

         var10000 = this;
         return var10000;
      }

      var10000 = this.merge(other.copy());
      return var10000;
   }

   public StatCounter copy() {
      StatCounter other = new StatCounter();
      other.n_$eq(this.n());
      other.mu_$eq(this.mu());
      other.m2_$eq(this.m2());
      other.maxValue_$eq(this.maxValue());
      other.minValue_$eq(this.minValue());
      return other;
   }

   public long count() {
      return this.n();
   }

   public double mean() {
      return this.mu();
   }

   public double sum() {
      return (double)this.n() * this.mu();
   }

   public double max() {
      return this.maxValue();
   }

   public double min() {
      return this.minValue();
   }

   public double variance() {
      return this.popVariance();
   }

   public double popVariance() {
      return this.n() == 0L ? Double.NaN : this.m2() / (double)this.n();
   }

   public double sampleVariance() {
      return this.n() <= 1L ? Double.NaN : this.m2() / (double)(this.n() - 1L);
   }

   public double stdev() {
      return this.popStdev();
   }

   public double popStdev() {
      return scala.math.package$.MODULE$.sqrt(this.popVariance());
   }

   public double sampleStdev() {
      return scala.math.package$.MODULE$.sqrt(this.sampleVariance());
   }

   public String toString() {
      return (new StringOps(Predef$.MODULE$.augmentString("(count: %d, mean: %f, stdev: %f, max: %f, min: %f)"))).format(Predef$.MODULE$.genericWrapArray(new Object[]{BoxesRunTime.boxToLong(this.count()), BoxesRunTime.boxToDouble(this.mean()), BoxesRunTime.boxToDouble(this.stdev()), BoxesRunTime.boxToDouble(this.max()), BoxesRunTime.boxToDouble(this.min())}));
   }

   public StatCounter(TraversableOnce<Object> values) {
      this.n = 0L;
      this.mu = (double)0.0F;
      this.m2 = (double)0.0F;
      this.maxValue = Double.NEGATIVE_INFINITY;
      this.minValue = Double.POSITIVE_INFINITY;
      this.merge(values);
   }

   public StatCounter() {
      this(Nil$.MODULE$);
   }
}
