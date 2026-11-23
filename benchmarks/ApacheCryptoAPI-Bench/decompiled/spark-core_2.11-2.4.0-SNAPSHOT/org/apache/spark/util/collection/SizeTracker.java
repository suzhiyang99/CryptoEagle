package org.apache.spark.util.collection;

import scala.None$;
import scala.Option;
import scala.Product;
import scala.Product$class;
import scala.Serializable;
import scala.Some;
import scala.Tuple2;
import scala.Tuple2$mcJJ$sp;
import scala.collection.Iterator;
import scala.collection.mutable.Queue;
import scala.reflect.ScalaSignature;
import scala.runtime.AbstractFunction2;
import scala.runtime.BoxesRunTime;
import scala.runtime.ScalaRunTime$;
import scala.runtime.Statics;
import scala.runtime.TraitSetter;

@ScalaSignature(
   bytes = "\u0006\u0001\u0005ef\u0001C\u0001\u0003!\u0003\r\tA\u0002\u0007\u0003\u0017MK'0\u001a+sC\u000e\\WM\u001d\u0006\u0003\u0007\u0011\t!bY8mY\u0016\u001cG/[8o\u0015\t)a!\u0001\u0003vi&d'BA\u0004\t\u0003\u0015\u0019\b/\u0019:l\u0015\tI!\"\u0001\u0004ba\u0006\u001c\u0007.\u001a\u0006\u0002\u0017\u0005\u0019qN]4\u0014\u0005\u0001i\u0001C\u0001\b\u0012\u001b\u0005y!\"\u0001\t\u0002\u000bM\u001c\u0017\r\\1\n\u0005Iy!AB!osJ+g\rC\u0003\u0015\u0001\u0011\u0005a#\u0001\u0004%S:LG\u000fJ\u0002\u0001)\u00059\u0002C\u0001\b\u0019\u0013\tIrB\u0001\u0003V]&$\bbB\u000e\u0001\u0005\u0004%I\u0001H\u0001\u0013'\u0006k\u0005\u000bT#`\u000fJ{u\u000b\u0016%`%\u0006#V)F\u0001\u001e!\tqa$\u0003\u0002 \u001f\t1Ai\\;cY\u0016Da!\t\u0001!\u0002\u0013i\u0012aE*B\u001bBcUiX$S\u001f^#\u0006j\u0018*B)\u0016\u0003\u0003bB\u0012\u0001\u0005\u0004%I\u0001J\u0001\bg\u0006l\u0007\u000f\\3t+\u0005)\u0003c\u0001\u0014+Y5\tqE\u0003\u0002)S\u00059Q.\u001e;bE2,'BA\u0002\u0010\u0013\tYsEA\u0003Rk\u0016,X\r\u0005\u0002.o9\u0011afL\u0007\u0002\u0005\u001d)\u0001G\u0001E\u0005c\u0005Y1+\u001b>f)J\f7m[3s!\tq#GB\u0003\u0002\u0005!%1g\u0005\u00023\u001b!)QG\rC\u0001m\u00051A(\u001b8jiz\"\u0012!\r\u0004\u0005qI\u0002\u0015H\u0001\u0004TC6\u0004H.Z\n\u0005o5QT\b\u0005\u0002\u000fw%\u0011Ah\u0004\u0002\b!J|G-^2u!\tqa(\u0003\u0002@\u001f\ta1+\u001a:jC2L'0\u00192mK\"A\u0011i\u000eBK\u0002\u0013\u0005!)\u0001\u0003tSj,W#A\"\u0011\u00059!\u0015BA#\u0010\u0005\u0011auN\\4\t\u0011\u001d;$\u0011#Q\u0001\n\r\u000bQa]5{K\u0002B\u0001\"S\u001c\u0003\u0016\u0004%\tAQ\u0001\u000b]VlW\u000b\u001d3bi\u0016\u001c\b\u0002C&8\u0005#\u0005\u000b\u0011B\"\u0002\u00179,X.\u00169eCR,7\u000f\t\u0005\u0006k]\"\t!\u0014\u000b\u0004\u001dB\u000b\u0006CA(8\u001b\u0005\u0011\u0004\"B!M\u0001\u0004\u0019\u0005\"B%M\u0001\u0004\u0019\u0005bB*8\u0003\u0003%\t\u0001V\u0001\u0005G>\u0004\u0018\u0010F\u0002O+ZCq!\u0011*\u0011\u0002\u0003\u00071\tC\u0004J%B\u0005\t\u0019A\"\t\u000fa;\u0014\u0013!C\u00013\u0006q1m\u001c9zI\u0011,g-Y;mi\u0012\nT#\u0001.+\u0005\r[6&\u0001/\u0011\u0005u\u0013W\"\u00010\u000b\u0005}\u0003\u0017!C;oG\",7m[3e\u0015\t\tw\"\u0001\u0006b]:|G/\u0019;j_:L!a\u00190\u0003#Ut7\r[3dW\u0016$g+\u0019:jC:\u001cW\rC\u0004foE\u0005I\u0011A-\u0002\u001d\r|\u0007/\u001f\u0013eK\u001a\fW\u000f\u001c;%e!9qmNA\u0001\n\u0003B\u0017!\u00049s_\u0012,8\r\u001e)sK\u001aL\u00070F\u0001j!\tQw.D\u0001l\u0015\taW.\u0001\u0003mC:<'\"\u00018\u0002\t)\fg/Y\u0005\u0003a.\u0014aa\u0015;sS:<\u0007b\u0002:8\u0003\u0003%\ta]\u0001\raJ|G-^2u\u0003JLG/_\u000b\u0002iB\u0011a\"^\u0005\u0003m>\u00111!\u00138u\u0011\u001dAx'!A\u0005\u0002e\fa\u0002\u001d:pIV\u001cG/\u00127f[\u0016tG\u000f\u0006\u0002{{B\u0011ab_\u0005\u0003y>\u00111!\u00118z\u0011\u001dqx/!AA\u0002Q\f1\u0001\u001f\u00132\u0011%\t\taNA\u0001\n\u0003\n\u0019!A\bqe>$Wo\u0019;Ji\u0016\u0014\u0018\r^8s+\t\t)\u0001E\u0003\u0002\b\u0005%!0D\u0001*\u0013\r\tY!\u000b\u0002\t\u0013R,'/\u0019;pe\"I\u0011qB\u001c\u0002\u0002\u0013\u0005\u0011\u0011C\u0001\tG\u0006tW)];bYR!\u00111CA\r!\rq\u0011QC\u0005\u0004\u0003/y!a\u0002\"p_2,\u0017M\u001c\u0005\t}\u00065\u0011\u0011!a\u0001u\"I\u0011QD\u001c\u0002\u0002\u0013\u0005\u0013qD\u0001\tQ\u0006\u001c\bnQ8eKR\tA\u000fC\u0005\u0002$]\n\t\u0011\"\u0011\u0002&\u0005AAo\\*ue&tw\rF\u0001j\u0011%\tIcNA\u0001\n\u0003\nY#\u0001\u0004fcV\fGn\u001d\u000b\u0005\u0003'\ti\u0003\u0003\u0005\u007f\u0003O\t\t\u00111\u0001{\u000f%\t\tDMA\u0001\u0012\u0003\t\u0019$\u0001\u0004TC6\u0004H.\u001a\t\u0004\u001f\u0006Ub\u0001\u0003\u001d3\u0003\u0003E\t!a\u000e\u0014\u000b\u0005U\u0012\u0011H\u001f\u0011\u000f\u0005m\u0012\u0011I\"D\u001d6\u0011\u0011Q\b\u0006\u0004\u0003\u007fy\u0011a\u0002:v]RLW.Z\u0005\u0005\u0003\u0007\niDA\tBEN$(/Y2u\rVt7\r^5p]JBq!NA\u001b\t\u0003\t9\u0005\u0006\u0002\u00024!Q\u00111EA\u001b\u0003\u0003%)%!\n\t\u0015\u00055\u0013QGA\u0001\n\u0003\u000by%A\u0003baBd\u0017\u0010F\u0003O\u0003#\n\u0019\u0006\u0003\u0004B\u0003\u0017\u0002\ra\u0011\u0005\u0007\u0013\u0006-\u0003\u0019A\"\t\u0015\u0005]\u0013QGA\u0001\n\u0003\u000bI&A\u0004v]\u0006\u0004\b\u000f\\=\u0015\t\u0005m\u0013q\r\t\u0006\u001d\u0005u\u0013\u0011M\u0005\u0004\u0003?z!AB(qi&|g\u000eE\u0003\u000f\u0003G\u001a5)C\u0002\u0002f=\u0011a\u0001V;qY\u0016\u0014\u0004\"CA5\u0003+\n\t\u00111\u0001O\u0003\rAH\u0005\r\u0005\u000b\u0003[\n)$!A\u0005\n\u0005=\u0014a\u0003:fC\u0012\u0014Vm]8mm\u0016$\"!!\u001d\u0011\u0007)\f\u0019(C\u0002\u0002v-\u0014aa\u00142kK\u000e$\bbBA=\u0001\u0001\u0006I!J\u0001\tg\u0006l\u0007\u000f\\3tA!Q\u0011Q\u0010\u0001A\u0002\u0003\u0007I\u0011\u0002\u000f\u0002\u001d\tLH/Z:QKJ,\u0006\u000fZ1uK\"Y\u0011\u0011\u0011\u0001A\u0002\u0003\u0007I\u0011BAB\u0003I\u0011\u0017\u0010^3t!\u0016\u0014X\u000b\u001d3bi\u0016|F%Z9\u0015\u0007]\t)\t\u0003\u0005\u007f\u0003\u007f\n\t\u00111\u0001\u001e\u0011\u001d\tI\t\u0001Q!\nu\tqBY=uKN\u0004VM]+qI\u0006$X\r\t\u0005\n\u0013\u0002\u0001\r\u00111A\u0005\n\tC1\"a$\u0001\u0001\u0004\u0005\r\u0011\"\u0003\u0002\u0012\u0006qa.^7Va\u0012\fG/Z:`I\u0015\fHcA\f\u0002\u0014\"Aa0!$\u0002\u0002\u0003\u00071\t\u0003\u0004L\u0001\u0001\u0006Ka\u0011\u0005\u000b\u00033\u0003\u0001\u0019!a\u0001\n\u0013\u0011\u0015!\u00048fqR\u001c\u0016-\u001c9mK:+X\u000eC\u0006\u0002\u001e\u0002\u0001\r\u00111A\u0005\n\u0005}\u0015!\u00058fqR\u001c\u0016-\u001c9mK:+Xn\u0018\u0013fcR\u0019q#!)\t\u0011y\fY*!AA\u0002\rCq!!*\u0001A\u0003&1)\u0001\boKb$8+Y7qY\u0016tU/\u001c\u0011\t\r\u0005%\u0006\u0001\"\u0005\u0017\u00031\u0011Xm]3u'\u0006l\u0007\u000f\\3t\u0011\u0019\ti\u000b\u0001C\t-\u0005Y\u0011M\u001a;feV\u0003H-\u0019;f\u0011\u0019\t\t\f\u0001C\u0005-\u0005QA/Y6f'\u0006l\u0007\u000f\\3\t\u000f\u0005U\u0006\u0001\"\u0001\u00028\u0006aQm\u001d;j[\u0006$XmU5{KR\t1\t"
)
public interface SizeTracker {
   void org$apache$spark$util$collection$SizeTracker$_setter_$org$apache$spark$util$collection$SizeTracker$$SAMPLE_GROWTH_RATE_$eq(double var1);

   void org$apache$spark$util$collection$SizeTracker$_setter_$org$apache$spark$util$collection$SizeTracker$$samples_$eq(Queue var1);

   double org$apache$spark$util$collection$SizeTracker$$SAMPLE_GROWTH_RATE();

   Queue<Sample> org$apache$spark$util$collection$SizeTracker$$samples();

   double org$apache$spark$util$collection$SizeTracker$$bytesPerUpdate();

   @TraitSetter
   void org$apache$spark$util$collection$SizeTracker$$bytesPerUpdate_$eq(double var1);

   long org$apache$spark$util$collection$SizeTracker$$numUpdates();

   @TraitSetter
   void org$apache$spark$util$collection$SizeTracker$$numUpdates_$eq(long var1);

   long org$apache$spark$util$collection$SizeTracker$$nextSampleNum();

   @TraitSetter
   void org$apache$spark$util$collection$SizeTracker$$nextSampleNum_$eq(long var1);

   void resetSamples();

   void afterUpdate();

   long estimateSize();

   public static class Sample implements Product, Serializable {
      private final long size;
      private final long numUpdates;

      public long size() {
         return this.size;
      }

      public long numUpdates() {
         return this.numUpdates;
      }

      public Sample copy(long size, long numUpdates) {
         return new Sample(size, numUpdates);
      }

      public long copy$default$1() {
         return this.size();
      }

      public long copy$default$2() {
         return this.numUpdates();
      }

      public String productPrefix() {
         return "Sample";
      }

      public int productArity() {
         return 2;
      }

      public Object productElement(int x$1) {
         Long var10000;
         switch (x$1) {
            case 0:
               var10000 = BoxesRunTime.boxToLong(this.size());
               break;
            case 1:
               var10000 = BoxesRunTime.boxToLong(this.numUpdates());
               break;
            default:
               throw new IndexOutOfBoundsException(BoxesRunTime.boxToInteger(x$1).toString());
         }

         return var10000;
      }

      public Iterator<Object> productIterator() {
         return ScalaRunTime$.MODULE$.<Object>typedProductIterator(this);
      }

      public boolean canEqual(Object x$1) {
         return x$1 instanceof Sample;
      }

      public int hashCode() {
         int var1 = -889275714;
         var1 = Statics.mix(var1, Statics.longHash(this.size()));
         var1 = Statics.mix(var1, Statics.longHash(this.numUpdates()));
         return Statics.finalizeHash(var1, 2);
      }

      public String toString() {
         return ScalaRunTime$.MODULE$._toString(this);
      }

      public boolean equals(Object x$1) {
         boolean var10000;
         if (this != x$1) {
            label50: {
               boolean var3;
               if (x$1 instanceof Sample) {
                  var3 = true;
               } else {
                  var3 = false;
               }

               if (var3) {
                  Sample var4 = (Sample)x$1;
                  if (this.size() == var4.size() && this.numUpdates() == var4.numUpdates() && var4.canEqual(this)) {
                     break label50;
                  }
               }

               var10000 = false;
               return var10000;
            }
         }

         var10000 = true;
         return var10000;
      }

      public Sample(long size, long numUpdates) {
         this.size = size;
         this.numUpdates = numUpdates;
         Product$class.$init$(this);
      }
   }

   public static class Sample$ extends AbstractFunction2<Object, Object, Sample> implements Serializable {
      public static final Sample$ MODULE$;

      static {
         new Sample$();
      }

      public final String toString() {
         return "Sample";
      }

      public Sample apply(long size, long numUpdates) {
         return new Sample(size, numUpdates);
      }

      public Option<Tuple2<Object, Object>> unapply(Sample x$0) {
         return (Option<Tuple2<Object, Object>>)(x$0 == null ? None$.MODULE$ : new Some(new Tuple2$mcJJ$sp(x$0.size(), x$0.numUpdates())));
      }

      private Object readResolve() {
         return MODULE$;
      }

      public Sample$() {
         MODULE$ = this;
      }
   }
}
