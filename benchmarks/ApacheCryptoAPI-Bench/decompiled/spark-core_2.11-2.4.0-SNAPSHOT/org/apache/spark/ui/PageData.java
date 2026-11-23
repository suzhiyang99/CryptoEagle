package org.apache.spark.ui;

import scala.Option;
import scala.Product;
import scala.Product$class;
import scala.Serializable;
import scala.Tuple2;
import scala.collection.Iterator;
import scala.collection.Seq;
import scala.reflect.ScalaSignature;
import scala.runtime.BoxesRunTime;
import scala.runtime.ScalaRunTime$;
import scala.runtime.Statics;

@ScalaSignature(
   bytes = "\u0006\u0001\u0005=d!B\u0001\u0003\u0001\nQ!\u0001\u0003)bO\u0016$\u0015\r^1\u000b\u0005\r!\u0011AA;j\u0015\t)a!A\u0003ta\u0006\u00148N\u0003\u0002\b\u0011\u00051\u0011\r]1dQ\u0016T\u0011!C\u0001\u0004_J<WCA\u00063'\u0011\u0001ABE\u000b\u0011\u00055\u0001R\"\u0001\b\u000b\u0003=\tQa]2bY\u0006L!!\u0005\b\u0003\r\u0005s\u0017PU3g!\ti1#\u0003\u0002\u0015\u001d\t9\u0001K]8ek\u000e$\bCA\u0007\u0017\u0013\t9bB\u0001\u0007TKJL\u0017\r\\5{C\ndW\r\u0003\u0005\u001a\u0001\tU\r\u0011\"\u0001\u001c\u0003%!x\u000e^1m!\u0006<Wm\u0001\u0001\u0016\u0003q\u0001\"!D\u000f\n\u0005yq!aA%oi\"A\u0001\u0005\u0001B\tB\u0003%A$\u0001\u0006u_R\fG\u000eU1hK\u0002B\u0001B\t\u0001\u0003\u0016\u0004%\taI\u0001\u0005I\u0006$\u0018-F\u0001%!\r)S\u0006\r\b\u0003M-r!a\n\u0016\u000e\u0003!R!!\u000b\u000e\u0002\rq\u0012xn\u001c;?\u0013\u0005y\u0011B\u0001\u0017\u000f\u0003\u001d\u0001\u0018mY6bO\u0016L!AL\u0018\u0003\u0007M+\u0017O\u0003\u0002-\u001dA\u0011\u0011G\r\u0007\u0001\t\u0015\u0019\u0004A1\u00015\u0005\u0005!\u0016CA\u001b9!\tia'\u0003\u00028\u001d\t9aj\u001c;iS:<\u0007CA\u0007:\u0013\tQdBA\u0002B]fD\u0001\u0002\u0010\u0001\u0003\u0012\u0003\u0006I\u0001J\u0001\u0006I\u0006$\u0018\r\t\u0005\u0006}\u0001!\taP\u0001\u0007y%t\u0017\u000e\u001e \u0015\u0007\u0001\u00135\tE\u0002B\u0001Aj\u0011A\u0001\u0005\u00063u\u0002\r\u0001\b\u0005\u0006Eu\u0002\r\u0001\n\u0005\b\u000b\u0002\t\t\u0011\"\u0001G\u0003\u0011\u0019w\u000e]=\u0016\u0005\u001dSEc\u0001%L\u0019B\u0019\u0011\tA%\u0011\u0005ERE!B\u001aE\u0005\u0004!\u0004bB\rE!\u0003\u0005\r\u0001\b\u0005\bE\u0011\u0003\n\u00111\u0001N!\r)S&\u0013\u0005\b\u001f\u0002\t\n\u0011\"\u0001Q\u00039\u0019w\u000e]=%I\u00164\u0017-\u001e7uIE*\"!\u0015/\u0016\u0003IS#\u0001H*,\u0003Q\u0003\"!\u0016.\u000e\u0003YS!a\u0016-\u0002\u0013Ut7\r[3dW\u0016$'BA-\u000f\u0003)\tgN\\8uCRLwN\\\u0005\u00037Z\u0013\u0011#\u001e8dQ\u0016\u001c7.\u001a3WCJL\u0017M\\2f\t\u0015\u0019dJ1\u00015\u0011\u001dq\u0006!%A\u0005\u0002}\u000babY8qs\u0012\"WMZ1vYR$#'\u0006\u0002aEV\t\u0011M\u000b\u0002%'\u0012)1'\u0018b\u0001i!9A\rAA\u0001\n\u0003*\u0017!\u00049s_\u0012,8\r\u001e)sK\u001aL\u00070F\u0001g!\t9G.D\u0001i\u0015\tI'.\u0001\u0003mC:<'\"A6\u0002\t)\fg/Y\u0005\u0003[\"\u0014aa\u0015;sS:<\u0007bB8\u0001\u0003\u0003%\taG\u0001\raJ|G-^2u\u0003JLG/\u001f\u0005\bc\u0002\t\t\u0011\"\u0001s\u00039\u0001(o\u001c3vGR,E.Z7f]R$\"\u0001O:\t\u000fQ\u0004\u0018\u0011!a\u00019\u0005\u0019\u0001\u0010J\u0019\t\u000fY\u0004\u0011\u0011!C!o\u0006y\u0001O]8ek\u000e$\u0018\n^3sCR|'/F\u0001y!\rIH\u0010O\u0007\u0002u*\u00111PD\u0001\u000bG>dG.Z2uS>t\u0017BA?{\u0005!IE/\u001a:bi>\u0014\b\u0002C@\u0001\u0003\u0003%\t!!\u0001\u0002\u0011\r\fg.R9vC2$B!a\u0001\u0002\nA\u0019Q\"!\u0002\n\u0007\u0005\u001daBA\u0004C_>dW-\u00198\t\u000fQt\u0018\u0011!a\u0001q!I\u0011Q\u0002\u0001\u0002\u0002\u0013\u0005\u0013qB\u0001\tQ\u0006\u001c\bnQ8eKR\tA\u0004C\u0005\u0002\u0014\u0001\t\t\u0011\"\u0011\u0002\u0016\u0005AAo\\*ue&tw\rF\u0001g\u0011%\tI\u0002AA\u0001\n\u0003\nY\"\u0001\u0004fcV\fGn\u001d\u000b\u0005\u0003\u0007\ti\u0002\u0003\u0005u\u0003/\t\t\u00111\u00019\u000f)\t\tCAA\u0001\u0012\u0003\u0011\u00111E\u0001\t!\u0006<W\rR1uCB\u0019\u0011)!\n\u0007\u0013\u0005\u0011\u0011\u0011!E\u0001\u0005\u0005\u001d2\u0003BA\u0013\u0019UAqAPA\u0013\t\u0003\tY\u0003\u0006\u0002\u0002$!Q\u00111CA\u0013\u0003\u0003%)%!\u0006\t\u0015\u0005E\u0012QEA\u0001\n\u0003\u000b\u0019$A\u0003baBd\u00170\u0006\u0003\u00026\u0005mBCBA\u001c\u0003{\ty\u0004\u0005\u0003B\u0001\u0005e\u0002cA\u0019\u0002<\u001111'a\fC\u0002QBa!GA\u0018\u0001\u0004a\u0002b\u0002\u0012\u00020\u0001\u0007\u0011\u0011\t\t\u0005K5\nI\u0004\u0003\u0006\u0002F\u0005\u0015\u0012\u0011!CA\u0003\u000f\nq!\u001e8baBd\u00170\u0006\u0003\u0002J\u0005mC\u0003BA&\u0003;\u0002R!DA'\u0003#J1!a\u0014\u000f\u0005\u0019y\u0005\u000f^5p]B1Q\"a\u0015\u001d\u0003/J1!!\u0016\u000f\u0005\u0019!V\u000f\u001d7feA!Q%LA-!\r\t\u00141\f\u0003\u0007g\u0005\r#\u0019\u0001\u001b\t\u0015\u0005}\u00131IA\u0001\u0002\u0004\t\t'A\u0002yIA\u0002B!\u0011\u0001\u0002Z!Q\u0011QMA\u0013\u0003\u0003%I!a\u001a\u0002\u0017I,\u0017\r\u001a*fg>dg/\u001a\u000b\u0003\u0003S\u00022aZA6\u0013\r\ti\u0007\u001b\u0002\u0007\u001f\nTWm\u0019;"
)
public class PageData<T> implements Product, Serializable {
   private final int totalPage;
   private final Seq<T> data;

   public static <T> Option<Tuple2<Object, Seq<T>>> unapply(PageData<T> var0) {
      return PageData$.MODULE$.unapply(var0);
   }

   public static <T> PageData<T> apply(int var0, Seq<T> var1) {
      return PageData$.MODULE$.<T>apply(var0, var1);
   }

   public int totalPage() {
      return this.totalPage;
   }

   public Seq<T> data() {
      return this.data;
   }

   public <T> PageData<T> copy(int totalPage, Seq<T> data) {
      return new PageData<T>(totalPage, data);
   }

   public <T> int copy$default$1() {
      return this.totalPage();
   }

   public <T> Seq<T> copy$default$2() {
      return this.data();
   }

   public String productPrefix() {
      return "PageData";
   }

   public int productArity() {
      return 2;
   }

   public Object productElement(int x$1) {
      Object var10000;
      switch (x$1) {
         case 0:
            var10000 = BoxesRunTime.boxToInteger(this.totalPage());
            break;
         case 1:
            var10000 = this.data();
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
      return x$1 instanceof PageData;
   }

   public int hashCode() {
      int var1 = -889275714;
      var1 = Statics.mix(var1, this.totalPage());
      var1 = Statics.mix(var1, Statics.anyHash(this.data()));
      return Statics.finalizeHash(var1, 2);
   }

   public String toString() {
      return ScalaRunTime$.MODULE$._toString(this);
   }

   public boolean equals(Object x$1) {
      boolean var7;
      if (this != x$1) {
         label54: {
            boolean var3;
            if (x$1 instanceof PageData) {
               var3 = true;
            } else {
               var3 = false;
            }

            if (var3) {
               label37: {
                  PageData var4 = (PageData)x$1;
                  if (this.totalPage() == var4.totalPage()) {
                     label35: {
                        Seq var10000 = this.data();
                        Seq var5 = var4.data();
                        if (var10000 == null) {
                           if (var5 != null) {
                              break label35;
                           }
                        } else if (!var10000.equals(var5)) {
                           break label35;
                        }

                        if (var4.canEqual(this)) {
                           var7 = true;
                           break label37;
                        }
                     }
                  }

                  var7 = false;
               }

               if (var7) {
                  break label54;
               }
            }

            var7 = false;
            return var7;
         }
      }

      var7 = true;
      return var7;
   }

   public PageData(int totalPage, Seq<T> data) {
      this.totalPage = totalPage;
      this.data = data;
      Product$class.$init$(this);
   }
}
