package org.apache.spark;

import org.apache.spark.annotation.DeveloperApi;
import org.apache.spark.util.collection.ExternalAppendOnlyMap;
import org.apache.spark.util.collection.ExternalAppendOnlyMap$;
import scala.Function1;
import scala.Function2;
import scala.Option;
import scala.Option$;
import scala.Predef$;
import scala.Product;
import scala.Product$class;
import scala.Product2;
import scala.Serializable;
import scala.Tuple2;
import scala.Tuple3;
import scala.collection.Iterator;
import scala.reflect.ScalaSignature;
import scala.runtime.BoxesRunTime;
import scala.runtime.ScalaRunTime$;

@DeveloperApi
@ScalaSignature(
   bytes = "\u0006\u0001\tUc\u0001B\u0001\u0003\u0001&\u0011!\"Q4he\u0016<\u0017\r^8s\u0015\t\u0019A!A\u0003ta\u0006\u00148N\u0003\u0002\u0006\r\u00051\u0011\r]1dQ\u0016T\u0011aB\u0001\u0004_J<7\u0001A\u000b\u0005\u0015\u0005{\u0012f\u0005\u0003\u0001\u0017E!\u0002C\u0001\u0007\u0010\u001b\u0005i!\"\u0001\b\u0002\u000bM\u001c\u0017\r\\1\n\u0005Ai!AB!osJ+g\r\u0005\u0002\r%%\u00111#\u0004\u0002\b!J|G-^2u!\taQ#\u0003\u0002\u0017\u001b\ta1+\u001a:jC2L'0\u00192mK\"A\u0001\u0004\u0001BK\u0002\u0013\u0005\u0011$\u0001\bde\u0016\fG/Z\"p[\nLg.\u001a:\u0016\u0003i\u0001B\u0001D\u000e\u001eQ%\u0011A$\u0004\u0002\n\rVt7\r^5p]F\u0002\"AH\u0010\r\u0001\u0011)\u0001\u0005\u0001b\u0001C\t\ta+\u0005\u0002#KA\u0011AbI\u0005\u0003I5\u0011qAT8uQ&tw\r\u0005\u0002\rM%\u0011q%\u0004\u0002\u0004\u0003:L\bC\u0001\u0010*\t\u0015Q\u0003A1\u0001\"\u0005\u0005\u0019\u0005\u0002\u0003\u0017\u0001\u0005#\u0005\u000b\u0011\u0002\u000e\u0002\u001f\r\u0014X-\u0019;f\u0007>l'-\u001b8fe\u0002B\u0001B\f\u0001\u0003\u0016\u0004%\taL\u0001\u000b[\u0016\u0014x-\u001a,bYV,W#\u0001\u0019\u0011\u000b1\t\u0004&\b\u0015\n\u0005Ij!!\u0003$v]\u000e$\u0018n\u001c83\u0011!!\u0004A!E!\u0002\u0013\u0001\u0014aC7fe\u001e,g+\u00197vK\u0002B\u0001B\u000e\u0001\u0003\u0016\u0004%\taN\u0001\u000f[\u0016\u0014x-Z\"p[\nLg.\u001a:t+\u0005A\u0004#\u0002\u00072Q!B\u0003\u0002\u0003\u001e\u0001\u0005#\u0005\u000b\u0011\u0002\u001d\u0002\u001f5,'oZ3D_6\u0014\u0017N\\3sg\u0002BQ\u0001\u0010\u0001\u0005\u0002u\na\u0001P5oSRtD\u0003\u0002 D\t\u0016\u0003Ra\u0010\u0001A;!j\u0011A\u0001\t\u0003=\u0005#QA\u0011\u0001C\u0002\u0005\u0012\u0011a\u0013\u0005\u00061m\u0002\rA\u0007\u0005\u0006]m\u0002\r\u0001\r\u0005\u0006mm\u0002\r\u0001\u000f\u0005\u0006\u000f\u0002!\t\u0001S\u0001\u0013G>l'-\u001b8f-\u0006dW/Z:Cs.+\u0017\u0010F\u0002J1\u000e\u00042A\u0013*V\u001d\tY\u0005K\u0004\u0002M\u001f6\tQJ\u0003\u0002O\u0011\u00051AH]8pizJ\u0011AD\u0005\u0003#6\tq\u0001]1dW\u0006<W-\u0003\u0002T)\nA\u0011\n^3sCR|'O\u0003\u0002R\u001bA!AB\u0016!)\u0013\t9VB\u0001\u0004UkBdWM\r\u0005\u00063\u001a\u0003\rAW\u0001\u0005SR,'\u000f\r\u0002\\;B\u0019!J\u0015/\u0011\u0005yiF!\u00030Y\u0003\u0003\u0005\tQ!\u0001`\u0005\ryF%M\t\u0003E\u0001\u0004B\u0001D1A;%\u0011!-\u0004\u0002\t!J|G-^2ue!)AM\u0012a\u0001K\u000691m\u001c8uKb$\bCA g\u0013\t9'AA\u0006UCN\\7i\u001c8uKb$\b\"B5\u0001\t\u0003Q\u0017!F2p[\nLg.Z\"p[\nLg.\u001a:t\u0005f\\U-\u001f\u000b\u0004\u0013.\u001c\b\"B-i\u0001\u0004a\u0007GA7p!\rQ%K\u001c\t\u0003==$\u0011\u0002]6\u0002\u0002\u0003\u0005)\u0011A9\u0003\u0007}##'\u0005\u0002#eB!A\"\u0019!)\u0011\u0015!\u0007\u000e1\u0001f\u0011\u0015)\b\u0001\"\u0003w\u00035)\b\u000fZ1uK6+GO]5dgR\u0019qO_>\u0011\u00051A\u0018BA=\u000e\u0005\u0011)f.\u001b;\t\u000b\u0011$\b\u0019A3\t\u000bq$\b\u0019A?\u0002\u00075\f\u0007\u000fM\u0004\u007f\u0003\u001f\t)\"a\u0007\u0011\u0013}\fI!!\u0004\u0002\u0014\u0005eQBAA\u0001\u0015\u0011\t\u0019!!\u0002\u0002\u0015\r|G\u000e\\3di&|gNC\u0002\u0002\b\t\tA!\u001e;jY&!\u00111BA\u0001\u0005U)\u0005\u0010^3s]\u0006d\u0017\t\u001d9f]\u0012|e\u000e\\=NCB\u00042AHA\b\t)\t\tb_A\u0001\u0002\u0003\u0015\t!\t\u0002\u0004?\u0012\u001a\u0004c\u0001\u0010\u0002\u0016\u0011Q\u0011qC>\u0002\u0002\u0003\u0005)\u0011A\u0011\u0003\u0007}#C\u0007E\u0002\u001f\u00037!!\"!\b|\u0003\u0003\u0005\tQ!\u0001\"\u0005\ryF%\u000e\u0005\n\u0003C\u0001\u0011\u0011!C\u0001\u0003G\tAaY8qsVA\u0011QEA\u0016\u0003_\t\u0019\u0004\u0006\u0005\u0002(\u0005U\u0012\u0011HA\u001f!!y\u0004!!\u000b\u0002.\u0005E\u0002c\u0001\u0010\u0002,\u00111!)a\bC\u0002\u0005\u00022AHA\u0018\t\u0019\u0001\u0013q\u0004b\u0001CA\u0019a$a\r\u0005\r)\nyB1\u0001\"\u0011%A\u0012q\u0004I\u0001\u0002\u0004\t9\u0004\u0005\u0004\r7\u00055\u0012\u0011\u0007\u0005\n]\u0005}\u0001\u0013!a\u0001\u0003w\u0001\u0002\u0002D\u0019\u00022\u00055\u0012\u0011\u0007\u0005\nm\u0005}\u0001\u0013!a\u0001\u0003\u007f\u0001\u0002\u0002D\u0019\u00022\u0005E\u0012\u0011\u0007\u0005\n\u0003\u0007\u0002\u0011\u0013!C\u0001\u0003\u000b\nabY8qs\u0012\"WMZ1vYR$\u0013'\u0006\u0005\u0002H\u0005u\u0013qLA1+\t\tIEK\u0002\u001b\u0003\u0017Z#!!\u0014\u0011\t\u0005=\u0013\u0011L\u0007\u0003\u0003#RA!a\u0015\u0002V\u0005IQO\\2iK\u000e\\W\r\u001a\u0006\u0004\u0003/j\u0011AC1o]>$\u0018\r^5p]&!\u00111LA)\u0005E)hn\u00195fG.,GMV1sS\u0006t7-\u001a\u0003\u0007\u0005\u0006\u0005#\u0019A\u0011\u0005\r\u0001\n\tE1\u0001\"\t\u0019Q\u0013\u0011\tb\u0001C!I\u0011Q\r\u0001\u0012\u0002\u0013\u0005\u0011qM\u0001\u000fG>\u0004\u0018\u0010\n3fM\u0006,H\u000e\u001e\u00133+!\tI'!\u001c\u0002p\u0005ETCAA6U\r\u0001\u00141\n\u0003\u0007\u0005\u0006\r$\u0019A\u0011\u0005\r\u0001\n\u0019G1\u0001\"\t\u0019Q\u00131\rb\u0001C!I\u0011Q\u000f\u0001\u0012\u0002\u0013\u0005\u0011qO\u0001\u000fG>\u0004\u0018\u0010\n3fM\u0006,H\u000e\u001e\u00134+!\tI(! \u0002\u0000\u0005\u0005UCAA>U\rA\u00141\n\u0003\u0007\u0005\u0006M$\u0019A\u0011\u0005\r\u0001\n\u0019H1\u0001\"\t\u0019Q\u00131\u000fb\u0001C!I\u0011Q\u0011\u0001\u0002\u0002\u0013\u0005\u0013qQ\u0001\u000eaJ|G-^2u!J,g-\u001b=\u0016\u0005\u0005%\u0005\u0003BAF\u0003+k!!!$\u000b\t\u0005=\u0015\u0011S\u0001\u0005Y\u0006twM\u0003\u0002\u0002\u0014\u0006!!.\u0019<b\u0013\u0011\t9*!$\u0003\rM#(/\u001b8h\u0011%\tY\nAA\u0001\n\u0003\ti*\u0001\u0007qe>$Wo\u0019;Be&$\u00180\u0006\u0002\u0002 B\u0019A\"!)\n\u0007\u0005\rVBA\u0002J]RD\u0011\"a*\u0001\u0003\u0003%\t!!+\u0002\u001dA\u0014x\u000eZ;di\u0016cW-\\3oiR\u0019Q%a+\t\u0015\u00055\u0016QUA\u0001\u0002\u0004\ty*A\u0002yIEB\u0011\"!-\u0001\u0003\u0003%\t%a-\u0002\u001fA\u0014x\u000eZ;di&#XM]1u_J,\"!!.\u0011\u000b\u0005]\u00161X\u0013\u000e\u0005\u0005e&bAA\u0002\u001b%\u00191+!/\t\u0013\u0005}\u0006!!A\u0005\u0002\u0005\u0005\u0017\u0001C2b]\u0016\u000bX/\u00197\u0015\t\u0005\r\u0017\u0011\u001a\t\u0004\u0019\u0005\u0015\u0017bAAd\u001b\t9!i\\8mK\u0006t\u0007\"CAW\u0003{\u000b\t\u00111\u0001&\u0011%\ti\rAA\u0001\n\u0003\ny-\u0001\u0005iCND7i\u001c3f)\t\ty\nC\u0005\u0002T\u0002\t\t\u0011\"\u0011\u0002V\u0006AAo\\*ue&tw\r\u0006\u0002\u0002\n\"I\u0011\u0011\u001c\u0001\u0002\u0002\u0013\u0005\u00131\\\u0001\u0007KF,\u0018\r\\:\u0015\t\u0005\r\u0017Q\u001c\u0005\n\u0003[\u000b9.!AA\u0002\u0015B3\u0001AAq!\u0011\t\u0019/a:\u000e\u0005\u0005\u0015(bAA,\u0005%!\u0011\u0011^As\u00051!UM^3m_B,'/\u00119j\u000f%\tiOAA\u0001\u0012\u0003\ty/\u0001\u0006BO\u001e\u0014XmZ1u_J\u00042aPAy\r!\t!!!A\t\u0002\u0005M8\u0003BAy\u0017QAq\u0001PAy\t\u0003\t9\u0010\u0006\u0002\u0002p\"Q\u00111[Ay\u0003\u0003%)%!6\t\u0015\u0005u\u0018\u0011_A\u0001\n\u0003\u000by0A\u0003baBd\u00170\u0006\u0005\u0003\u0002\t\u001d!1\u0002B\b)!\u0011\u0019A!\u0005\u0003\u0016\te\u0001\u0003C \u0001\u0005\u000b\u0011IA!\u0004\u0011\u0007y\u00119\u0001\u0002\u0004C\u0003w\u0014\r!\t\t\u0004=\t-AA\u0002\u0011\u0002|\n\u0007\u0011\u0005E\u0002\u001f\u0005\u001f!aAKA~\u0005\u0004\t\u0003b\u0002\r\u0002|\u0002\u0007!1\u0003\t\u0007\u0019m\u0011IA!\u0004\t\u000f9\nY\u00101\u0001\u0003\u0018AAA\"\rB\u0007\u0005\u0013\u0011i\u0001C\u00047\u0003w\u0004\rAa\u0007\u0011\u00111\t$Q\u0002B\u0007\u0005\u001bA!Ba\b\u0002r\u0006\u0005I\u0011\u0011B\u0011\u0003\u001d)h.\u00199qYf,\u0002Ba\t\u0003H\tU\"\u0011\b\u000b\u0005\u0005K\u0011y\u0004E\u0003\r\u0005O\u0011Y#C\u0002\u0003*5\u0011aa\u00149uS>t\u0007#\u0003\u0007\u0003.\tE\"1\bB\u001f\u0013\r\u0011y#\u0004\u0002\u0007)V\u0004H.Z\u001a\u0011\r1Y\"1\u0007B\u001c!\rq\"Q\u0007\u0003\u0007A\tu!\u0019A\u0011\u0011\u0007y\u0011I\u0004\u0002\u0004+\u0005;\u0011\r!\t\t\t\u0019E\u00129Da\r\u00038AAA\"\rB\u001c\u0005o\u00119\u0004\u0003\u0006\u0003B\tu\u0011\u0011!a\u0001\u0005\u0007\n1\u0001\u001f\u00131!!y\u0004A!\u0012\u00034\t]\u0002c\u0001\u0010\u0003H\u00111!I!\bC\u0002\u0005B!Ba\u0013\u0002r\u0006\u0005I\u0011\u0002B'\u0003-\u0011X-\u00193SKN|GN^3\u0015\u0005\t=\u0003\u0003BAF\u0005#JAAa\u0015\u0002\u000e\n1qJ\u00196fGR\u0004"
)
public class Aggregator<K, V, C> implements Product, Serializable {
   private final Function1<V, C> createCombiner;
   private final Function2<C, V, C> mergeValue;
   private final Function2<C, C, C> mergeCombiners;

   public static <K, V, C> Option<Tuple3<Function1<V, C>, Function2<C, V, C>, Function2<C, C, C>>> unapply(Aggregator<K, V, C> var0) {
      return Aggregator$.MODULE$.unapply(var0);
   }

   public static <K, V, C> Aggregator<K, V, C> apply(Function1<V, C> var0, Function2<C, V, C> var1, Function2<C, C, C> var2) {
      return Aggregator$.MODULE$.<K, V, C>apply(var0, var1, var2);
   }

   public Function1<V, C> createCombiner() {
      return this.createCombiner;
   }

   public Function2<C, V, C> mergeValue() {
      return this.mergeValue;
   }

   public Function2<C, C, C> mergeCombiners() {
      return this.mergeCombiners;
   }

   public Iterator<Tuple2<K, C>> combineValuesByKey(Iterator<? extends Product2<K, V>> iter, TaskContext context) {
      ExternalAppendOnlyMap combiners = new ExternalAppendOnlyMap(this.createCombiner(), this.mergeValue(), this.mergeCombiners(), ExternalAppendOnlyMap$.MODULE$.$lessinit$greater$default$4(), ExternalAppendOnlyMap$.MODULE$.$lessinit$greater$default$5(), ExternalAppendOnlyMap$.MODULE$.$lessinit$greater$default$6(), ExternalAppendOnlyMap$.MODULE$.$lessinit$greater$default$7());
      combiners.insertAll(iter);
      this.updateMetrics(context, combiners);
      return combiners.iterator();
   }

   public Iterator<Tuple2<K, C>> combineCombinersByKey(Iterator<? extends Product2<K, C>> iter, TaskContext context) {
      ExternalAppendOnlyMap combiners = new ExternalAppendOnlyMap(new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final C apply(C x) {
            return (C)Predef$.MODULE$.identity(x);
         }
      }, this.mergeCombiners(), this.mergeCombiners(), ExternalAppendOnlyMap$.MODULE$.$lessinit$greater$default$4(), ExternalAppendOnlyMap$.MODULE$.$lessinit$greater$default$5(), ExternalAppendOnlyMap$.MODULE$.$lessinit$greater$default$6(), ExternalAppendOnlyMap$.MODULE$.$lessinit$greater$default$7());
      combiners.insertAll(iter);
      this.updateMetrics(context, combiners);
      return combiners.iterator();
   }

   private void updateMetrics(TaskContext context, ExternalAppendOnlyMap<?, ?, ?> map) {
      Option$.MODULE$.apply(context).foreach(new Serializable(this, map) {
         public static final long serialVersionUID = 0L;
         private final ExternalAppendOnlyMap map$1;

         public final void apply(TaskContext c) {
            c.taskMetrics().incMemoryBytesSpilled(this.map$1.memoryBytesSpilled());
            c.taskMetrics().incDiskBytesSpilled(this.map$1.diskBytesSpilled());
            c.taskMetrics().incPeakExecutionMemory(this.map$1.peakMemoryUsedBytes());
         }

         public {
            this.map$1 = map$1;
         }
      });
   }

   public <K, V, C> Aggregator<K, V, C> copy(Function1<V, C> createCombiner, Function2<C, V, C> mergeValue, Function2<C, C, C> mergeCombiners) {
      return new Aggregator<K, V, C>(createCombiner, mergeValue, mergeCombiners);
   }

   public <K, V, C> Function1<V, C> copy$default$1() {
      return this.createCombiner();
   }

   public <K, V, C> Function2<C, V, C> copy$default$2() {
      return this.mergeValue();
   }

   public <K, V, C> Function2<C, C, C> copy$default$3() {
      return this.mergeCombiners();
   }

   public String productPrefix() {
      return "Aggregator";
   }

   public int productArity() {
      return 3;
   }

   public Object productElement(int x$1) {
      Object var10000;
      switch (x$1) {
         case 0:
            var10000 = this.createCombiner();
            break;
         case 1:
            var10000 = this.mergeValue();
            break;
         case 2:
            var10000 = this.mergeCombiners();
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
      return x$1 instanceof Aggregator;
   }

   public int hashCode() {
      return ScalaRunTime$.MODULE$._hashCode(this);
   }

   public String toString() {
      return ScalaRunTime$.MODULE$._toString(this);
   }

   public boolean equals(Object x$1) {
      boolean var11;
      if (this != x$1) {
         label71: {
            boolean var3;
            if (x$1 instanceof Aggregator) {
               var3 = true;
            } else {
               var3 = false;
            }

            if (var3) {
               label53: {
                  label62: {
                     Aggregator var4 = (Aggregator)x$1;
                     Function1 var10000 = this.createCombiner();
                     Function1 var5 = var4.createCombiner();
                     if (var10000 == null) {
                        if (var5 != null) {
                           break label62;
                        }
                     } else if (!var10000.equals(var5)) {
                        break label62;
                     }

                     Function2 var8 = this.mergeValue();
                     Function2 var6 = var4.mergeValue();
                     if (var8 == null) {
                        if (var6 != null) {
                           break label62;
                        }
                     } else if (!var8.equals(var6)) {
                        break label62;
                     }

                     var8 = this.mergeCombiners();
                     Function2 var7 = var4.mergeCombiners();
                     if (var8 == null) {
                        if (var7 != null) {
                           break label62;
                        }
                     } else if (!var8.equals(var7)) {
                        break label62;
                     }

                     if (var4.canEqual(this)) {
                        var11 = true;
                        break label53;
                     }
                  }

                  var11 = false;
               }

               if (var11) {
                  break label71;
               }
            }

            var11 = false;
            return var11;
         }
      }

      var11 = true;
      return var11;
   }

   public Aggregator(Function1<V, C> createCombiner, Function2<C, V, C> mergeValue, Function2<C, C, C> mergeCombiners) {
      this.createCombiner = createCombiner;
      this.mergeValue = mergeValue;
      this.mergeCombiners = mergeCombiners;
      Product$class.$init$(this);
   }
}
