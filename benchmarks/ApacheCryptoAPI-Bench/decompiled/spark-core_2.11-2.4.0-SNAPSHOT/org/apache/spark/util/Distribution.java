package org.apache.spark.util;

import java.io.PrintStream;
import java.util.Arrays;
import scala.Option;
import scala.Predef$;
import scala.Serializable;
import scala.collection.Traversable;
import scala.collection.immutable.IndexedSeq;
import scala.collection.immutable.IndexedSeq$;
import scala.collection.mutable.StringBuilder;
import scala.reflect.ClassTag$;
import scala.reflect.ScalaSignature;

@ScalaSignature(
   bytes = "\u0006\u0001\u0005]b!B\u0001\u0003\u0001\u0011Q!\u0001\u0004#jgR\u0014\u0018NY;uS>t'BA\u0002\u0005\u0003\u0011)H/\u001b7\u000b\u0005\u00151\u0011!B:qCJ\\'BA\u0004\t\u0003\u0019\t\u0007/Y2iK*\t\u0011\"A\u0002pe\u001e\u001c\"\u0001A\u0006\u0011\u00051yQ\"A\u0007\u000b\u00039\tQa]2bY\u0006L!\u0001E\u0007\u0003\r\u0005s\u0017PU3g\u0011!\u0011\u0002A!b\u0001\n\u0003!\u0012\u0001\u00023bi\u0006\u001c\u0001!F\u0001\u0016!\raa\u0003G\u0005\u0003/5\u0011Q!\u0011:sCf\u0004\"\u0001D\r\n\u0005ii!A\u0002#pk\ndW\r\u0003\u0005\u001d\u0001\t\u0005\t\u0015!\u0003\u0016\u0003\u0015!\u0017\r^1!\u0011!q\u0002A!b\u0001\n\u0003y\u0012\u0001C:uCJ$\u0018\n\u001a=\u0016\u0003\u0001\u0002\"\u0001D\u0011\n\u0005\tj!aA%oi\"AA\u0005\u0001B\u0001B\u0003%\u0001%A\u0005ti\u0006\u0014H/\u00133yA!Aa\u0005\u0001BC\u0002\u0013\u0005q$\u0001\u0004f]\u0012LE\r\u001f\u0005\tQ\u0001\u0011\t\u0011)A\u0005A\u00059QM\u001c3JIb\u0004\u0003\"\u0002\u0016\u0001\t\u0003Y\u0013A\u0002\u001fj]&$h\b\u0006\u0003-]=\u0002\u0004CA\u0017\u0001\u001b\u0005\u0011\u0001\"\u0002\n*\u0001\u0004)\u0002\"\u0002\u0010*\u0001\u0004\u0001\u0003\"\u0002\u0014*\u0001\u0004\u0001\u0003\"\u0002\u0016\u0001\t\u0003\u0011DC\u0001\u00174\u0011\u0015\u0011\u0012\u00071\u00015!\r)T\b\u0007\b\u0003mmr!a\u000e\u001e\u000e\u0003aR!!O\n\u0002\rq\u0012xn\u001c;?\u0013\u0005q\u0011B\u0001\u001f\u000e\u0003\u001d\u0001\u0018mY6bO\u0016L!AP \u0003\u0017Q\u0013\u0018M^3sg\u0006\u0014G.\u001a\u0006\u0003y5Aq!\u0011\u0001C\u0002\u0013\u0005q$\u0001\u0004mK:<G\u000f\u001b\u0005\u0007\u0007\u0002\u0001\u000b\u0011\u0002\u0011\u0002\u000f1,gn\u001a;iA!9Q\t\u0001b\u0001\n\u0003!\u0012\u0001\u00063fM\u0006,H\u000e\u001e)s_\n\f'-\u001b7ji&,7\u000f\u0003\u0004H\u0001\u0001\u0006I!F\u0001\u0016I\u00164\u0017-\u001e7u!J|'-\u00192jY&$\u0018.Z:!\u0011\u0015I\u0005\u0001\"\u0001K\u000319W\r^)vC:$\u0018\u000e\\3t)\tY5\u000bE\u0002M#bi\u0011!\u0014\u0006\u0003\u001d>\u000b\u0011\"[7nkR\f'\r\\3\u000b\u0005Ak\u0011AC2pY2,7\r^5p]&\u0011!+\u0014\u0002\u000b\u0013:$W\r_3e'\u0016\f\bb\u0002+I!\u0003\u0005\r\u0001N\u0001\u000eaJ|'-\u00192jY&$\u0018.Z:\t\u000bY\u0003A\u0011B,\u0002\u0019\rdwn]3ti&sG-\u001a=\u0015\u0005\u0001B\u0006\"B-V\u0001\u0004A\u0012!\u00019\t\u000bm\u0003A\u0011\u0001/\u0002\u001bMDwn^)vC:$\u0018\u000e\\3t)\ti\u0006\r\u0005\u0002\r=&\u0011q,\u0004\u0002\u0005+:LG\u000fC\u0004b5B\u0005\t\u0019\u00012\u0002\u0007=,H\u000f\u0005\u0002dQ6\tAM\u0003\u0002fM\u0006\u0011\u0011n\u001c\u0006\u0002O\u0006!!.\u0019<b\u0013\tIGMA\u0006Qe&tGo\u0015;sK\u0006l\u0007\"B6\u0001\t\u0003a\u0017aC:uCR\u001cu.\u001e8uKJ,\u0012!\u001c\t\u0003[9L!a\u001c\u0002\u0003\u0017M#\u0018\r^\"pk:$XM\u001d\u0005\u0006c\u0002!\tA]\u0001\bgVlW.\u0019:z)\ti6\u000fC\u0004baB\u0005\t\u0019\u00012\t\u000fU\u0004\u0011\u0013!C\u0001m\u00061r-\u001a;Rk\u0006tG/\u001b7fg\u0012\"WMZ1vYR$\u0013'F\u0001xU\t!\u0004pK\u0001z!\tQx0D\u0001|\u0015\taX0A\u0005v]\u000eDWmY6fI*\u0011a0D\u0001\u000bC:tw\u000e^1uS>t\u0017bAA\u0001w\n\tRO\\2iK\u000e\\W\r\u001a,be&\fgnY3\t\u0013\u0005\u0015\u0001!%A\u0005\u0002\u0005\u001d\u0011aF:i_^\fV/\u00198uS2,7\u000f\n3fM\u0006,H\u000e\u001e\u00132+\t\tIA\u000b\u0002cq\"I\u0011Q\u0002\u0001\u0012\u0002\u0013\u0005\u0011qA\u0001\u0012gVlW.\u0019:zI\u0011,g-Y;mi\u0012\nt\u0001CA\t\u0005!\u0005A!a\u0005\u0002\u0019\u0011K7\u000f\u001e:jEV$\u0018n\u001c8\u0011\u00075\n)BB\u0004\u0002\u0005!\u0005A!a\u0006\u0014\u0007\u0005U1\u0002C\u0004+\u0003+!\t!a\u0007\u0015\u0005\u0005M\u0001\u0002CA\u0010\u0003+!\t!!\t\u0002\u000b\u0005\u0004\b\u000f\\=\u0015\t\u0005\r\u0012\u0011\u0006\t\u0005\u0019\u0005\u0015B&C\u0002\u0002(5\u0011aa\u00149uS>t\u0007B\u0002\n\u0002\u001e\u0001\u0007A\u0007C\u0004\\\u0003+!\t!!\f\u0015\u000bu\u000by#!\r\t\u0011\u0005\fY\u0003%AA\u0002\tDq!a\r\u0002,\u0001\u0007A'A\u0005rk\u0006tG/\u001b7fg\"Q\u0011QAA\u000b#\u0003%\t!a\u0002"
)
public class Distribution {
   private final double[] data;
   private final int startIdx;
   private final int endIdx;
   private final int length;
   private final double[] defaultProbabilities;

   public static Option<Distribution> apply(Traversable<Object> var0) {
      return Distribution$.MODULE$.apply(var0);
   }

   public double[] data() {
      return this.data;
   }

   public int startIdx() {
      return this.startIdx;
   }

   public int endIdx() {
      return this.endIdx;
   }

   public int length() {
      return this.length;
   }

   public double[] defaultProbabilities() {
      return this.defaultProbabilities;
   }

   public IndexedSeq<Object> getQuantiles(Traversable<Object> probabilities) {
      return (IndexedSeq)probabilities.toIndexedSeq().map(new Serializable(this) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final Distribution $outer;

         public final double apply(double p) {
            return this.apply$mcDD$sp(p);
         }

         public double apply$mcDD$sp(double p) {
            return this.$outer.data()[this.$outer.org$apache$spark$util$Distribution$$closestIndex(p)];
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
            }
         }
      }, IndexedSeq$.MODULE$.canBuildFrom());
   }

   public Traversable<Object> getQuantiles$default$1() {
      return Predef$.MODULE$.wrapDoubleArray(this.defaultProbabilities());
   }

   public int org$apache$spark$util$Distribution$$closestIndex(double p) {
      return scala.math.package$.MODULE$.min((int)(p * (double)this.length()) + this.startIdx(), this.endIdx() - 1);
   }

   public void showQuantiles(PrintStream out) {
      out.println("min\t25%\t50%\t75%\tmax");
      this.getQuantiles(Predef$.MODULE$.wrapDoubleArray(this.defaultProbabilities())).foreach(new Serializable(this, out) {
         public static final long serialVersionUID = 0L;
         private final PrintStream out$1;

         public final void apply(double q) {
            this.apply$mcVD$sp(q);
         }

         public void apply$mcVD$sp(double q) {
            this.out$1.print((new StringBuilder()).append(q).append((Object)"\t").toString());
         }

         public {
            this.out$1 = out$1;
         }
      });
      out.println();
   }

   public PrintStream showQuantiles$default$1() {
      return System.out;
   }

   public StatCounter statCounter() {
      return StatCounter$.MODULE$.apply(Predef$.MODULE$.doubleArrayOps((double[])Predef$.MODULE$.doubleArrayOps(this.data()).slice(this.startIdx(), this.endIdx())));
   }

   public void summary(PrintStream out) {
      out.println(this.statCounter());
      this.showQuantiles(out);
   }

   public PrintStream summary$default$1() {
      return System.out;
   }

   public Distribution(double[] data, int startIdx, int endIdx) {
      this.data = data;
      this.startIdx = startIdx;
      this.endIdx = endIdx;
      Predef$.MODULE$.require(startIdx < endIdx);
      Arrays.sort(data, startIdx, endIdx);
      this.length = endIdx - startIdx;
      this.defaultProbabilities = new double[]{(double)0.0F, (double)0.25F, (double)0.5F, (double)0.75F, (double)1.0F};
   }

   public Distribution(Traversable<Object> data) {
      this((double[])data.toArray(ClassTag$.MODULE$.Double()), 0, data.size());
   }
}
