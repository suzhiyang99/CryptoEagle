package org.apache.spark.scheduler;

import org.apache.spark.annotation.DeveloperApi;
import scala.Option;
import scala.Product;
import scala.Product$class;
import scala.Serializable;
import scala.Tuple7;
import scala.collection.Iterator;
import scala.reflect.ScalaSignature;
import scala.runtime.BoxesRunTime;
import scala.runtime.ScalaRunTime$;
import scala.runtime.Statics;

@DeveloperApi
@ScalaSignature(
   bytes = "\u0006\u0001\u0005Mh\u0001B\u0001\u0003\u0001.\u0011q\"Q2dk6,H.\u00192mK&sgm\u001c\u0006\u0003\u0007\u0011\t\u0011b]2iK\u0012,H.\u001a:\u000b\u0005\u00151\u0011!B:qCJ\\'BA\u0004\t\u0003\u0019\t\u0007/Y2iK*\t\u0011\"A\u0002pe\u001e\u001c\u0001a\u0005\u0003\u0001\u0019I)\u0002CA\u0007\u0011\u001b\u0005q!\"A\b\u0002\u000bM\u001c\u0017\r\\1\n\u0005Eq!AB!osJ+g\r\u0005\u0002\u000e'%\u0011AC\u0004\u0002\b!J|G-^2u!\tia#\u0003\u0002\u0018\u001d\ta1+\u001a:jC2L'0\u00192mK\"A\u0011\u0004\u0001BK\u0002\u0013\u0005!$\u0001\u0002jIV\t1\u0004\u0005\u0002\u000e9%\u0011QD\u0004\u0002\u0005\u0019>tw\r\u0003\u0005 \u0001\tE\t\u0015!\u0003\u001c\u0003\rIG\r\t\u0005\tC\u0001\u0011)\u001a!C\u0001E\u0005!a.Y7f+\u0005\u0019\u0003cA\u0007%M%\u0011QE\u0004\u0002\u0007\u001fB$\u0018n\u001c8\u0011\u0005\u001dRcBA\u0007)\u0013\tIc\"\u0001\u0004Qe\u0016$WMZ\u0005\u0003W1\u0012aa\u0015;sS:<'BA\u0015\u000f\u0011!q\u0003A!E!\u0002\u0013\u0019\u0013!\u00028b[\u0016\u0004\u0003\u0002\u0003\u0019\u0001\u0005+\u0007I\u0011A\u0019\u0002\rU\u0004H-\u0019;f+\u0005\u0011\u0004cA\u0007%gA\u0011Q\u0002N\u0005\u0003k9\u00111!\u00118z\u0011!9\u0004A!E!\u0002\u0013\u0011\u0014aB;qI\u0006$X\r\t\u0005\ts\u0001\u0011)\u001a!C\u0001c\u0005)a/\u00197vK\"A1\b\u0001B\tB\u0003%!'\u0001\u0004wC2,X\r\t\u0005\n{\u0001\u0011)\u0019!C\u0001\ty\n\u0001\"\u001b8uKJt\u0017\r\\\u000b\u0002\u007fA\u0011Q\u0002Q\u0005\u0003\u0003:\u0011qAQ8pY\u0016\fg\u000e\u0003\u0005D\u0001\tE\t\u0015!\u0003@\u0003%Ig\u000e^3s]\u0006d\u0007\u0005C\u0005F\u0001\t\u0015\r\u0011\"\u0001\u0005}\u0005\t2m\\;oi\u001a\u000b\u0017\u000e\\3e-\u0006dW/Z:\t\u0011\u001d\u0003!\u0011#Q\u0001\n}\n!cY8v]R4\u0015-\u001b7fIZ\u000bG.^3tA!I\u0011\n\u0001BC\u0002\u0013\u0005AAI\u0001\t[\u0016$\u0018\rZ1uC\"A1\n\u0001B\tB\u0003%1%A\u0005nKR\fG-\u0019;bA!1Q\n\u0001C\u0001\t9\u000ba\u0001P5oSRtD\u0003C(R%N#VKV,\u0011\u0005A\u0003Q\"\u0001\u0002\t\u000bea\u0005\u0019A\u000e\t\u000b\u0005b\u0005\u0019A\u0012\t\u000bAb\u0005\u0019\u0001\u001a\t\u000beb\u0005\u0019\u0001\u001a\t\u000bub\u0005\u0019A \t\u000b\u0015c\u0005\u0019A \t\u000f%c\u0005\u0013!a\u0001G!9\u0011\fAA\u0001\n\u0003Q\u0016\u0001B2paf$\u0002bT.];z{\u0006-\u0019\u0005\b3a\u0003\n\u00111\u0001\u001c\u0011\u001d\t\u0003\f%AA\u0002\rBq\u0001\r-\u0011\u0002\u0003\u0007!\u0007C\u0004:1B\u0005\t\u0019\u0001\u001a\t\u000fuB\u0006\u0013!a\u0001\u007f!9Q\t\u0017I\u0001\u0002\u0004y\u0004bB%Y!\u0003\u0005\ra\t\u0005\bG\u0002\t\n\u0011\"\u0001e\u00039\u0019w\u000e]=%I\u00164\u0017-\u001e7uIE*\u0012!\u001a\u0016\u00037\u0019\\\u0013a\u001a\t\u0003Q6l\u0011!\u001b\u0006\u0003U.\f\u0011\"\u001e8dQ\u0016\u001c7.\u001a3\u000b\u00051t\u0011AC1o]>$\u0018\r^5p]&\u0011a.\u001b\u0002\u0012k:\u001c\u0007.Z2lK\u00124\u0016M]5b]\u000e,\u0007b\u00029\u0001#\u0003%\t!]\u0001\u000fG>\u0004\u0018\u0010\n3fM\u0006,H\u000e\u001e\u00133+\u0005\u0011(FA\u0012g\u0011\u001d!\b!%A\u0005\u0002U\fabY8qs\u0012\"WMZ1vYR$3'F\u0001wU\t\u0011d\rC\u0004y\u0001E\u0005I\u0011A;\u0002\u001d\r|\u0007/\u001f\u0013eK\u001a\fW\u000f\u001c;%i!9!\u0010AI\u0001\n\u0003Y\u0018AD2paf$C-\u001a4bk2$H%N\u000b\u0002y*\u0012qH\u001a\u0005\b}\u0002\t\n\u0011\"\u0001|\u00039\u0019w\u000e]=%I\u00164\u0017-\u001e7uIYB\u0001\"!\u0001\u0001#\u0003%\t!]\u0001\u000fG>\u0004\u0018\u0010\n3fM\u0006,H\u000e\u001e\u00138\u0011!\t)\u0001AF\u0001\n\u0003q\u0014AC5oi\u0016\u0014h.\u00197%c!A\u0011\u0011\u0002\u0001\f\u0002\u0013\u0005a(A\nd_VtGOR1jY\u0016$g+\u00197vKN$\u0013\u0007\u0003\u0005\u0002\u000e\u0001Y\t\u0011\"\u0001#\u0003)iW\r^1eCR\fG%\r\u0005\n\u0003#\u0001\u0011\u0011!C!\u0003'\tQ\u0002\u001d:pIV\u001cG\u000f\u0015:fM&DXCAA\u000b!\u0011\t9\"!\t\u000e\u0005\u0005e!\u0002BA\u000e\u0003;\tA\u0001\\1oO*\u0011\u0011qD\u0001\u0005U\u00064\u0018-C\u0002,\u00033A\u0011\"!\n\u0001\u0003\u0003%\t!a\n\u0002\u0019A\u0014x\u000eZ;di\u0006\u0013\u0018\u000e^=\u0016\u0005\u0005%\u0002cA\u0007\u0002,%\u0019\u0011Q\u0006\b\u0003\u0007%sG\u000fC\u0005\u00022\u0001\t\t\u0011\"\u0001\u00024\u0005q\u0001O]8ek\u000e$X\t\\3nK:$HcA\u001a\u00026!Q\u0011qGA\u0018\u0003\u0003\u0005\r!!\u000b\u0002\u0007a$\u0013\u0007C\u0005\u0002<\u0001\t\t\u0011\"\u0011\u0002>\u0005y\u0001O]8ek\u000e$\u0018\n^3sCR|'/\u0006\u0002\u0002@A)\u0011\u0011IA$g5\u0011\u00111\t\u0006\u0004\u0003\u000br\u0011AC2pY2,7\r^5p]&!\u0011\u0011JA\"\u0005!IE/\u001a:bi>\u0014\b\"CA'\u0001\u0005\u0005I\u0011AA(\u0003!\u0019\u0017M\\#rk\u0006dGcA \u0002R!I\u0011qGA&\u0003\u0003\u0005\ra\r\u0005\n\u0003+\u0002\u0011\u0011!C!\u0003/\n\u0001\u0002[1tQ\u000e{G-\u001a\u000b\u0003\u0003SA\u0011\"a\u0017\u0001\u0003\u0003%\t%!\u0018\u0002\u0011Q|7\u000b\u001e:j]\u001e$\"!!\u0006\t\u0013\u0005\u0005\u0004!!A\u0005B\u0005\r\u0014AB3rk\u0006d7\u000fF\u0002@\u0003KB\u0011\"a\u000e\u0002`\u0005\u0005\t\u0019A\u001a)\u0007\u0001\tI\u0007\u0005\u0003\u0002l\u0005=TBAA7\u0015\taG!\u0003\u0003\u0002r\u00055$\u0001\u0004#fm\u0016dw\u000e]3s\u0003BLwaBA;\u0005!\u0005\u0011qO\u0001\u0010\u0003\u000e\u001cW/\\;mC\ndW-\u00138g_B\u0019\u0001+!\u001f\u0007\r\u0005\u0011\u0001\u0012AA>'\u0011\tI\bD\u000b\t\u000f5\u000bI\b\"\u0001\u0002\u0000Q\u0011\u0011q\u000f\u0005\t\u0003\u0007\u000bI\b\"\u0001\u0002\u0006\u0006)\u0011\r\u001d9msRYq*a\"\u0002\n\u0006-\u0015QRAH\u0011\u0019I\u0012\u0011\u0011a\u00017!1\u0011%!!A\u0002\u0019Ba\u0001MAA\u0001\u0004\u0019\u0003BB\u001d\u0002\u0002\u0002\u0007a\u0005\u0003\u0004>\u0003\u0003\u0003\ra\u0010\u0015\t\u0003\u0003\u000b\u0019*!'\u0002\u001eB\u0019Q\"!&\n\u0007\u0005]eB\u0001\u0006eKB\u0014XmY1uK\u0012\f#!a'\u0002;\u0011|\u0007E\\8uA\r\u0014X-\u0019;fA\u0005\u001b7-^7vY\u0006\u0014G.Z%oM>\f#!a(\u0002\u000bIr\u0003G\f\u0019\t\u0011\u0005\r\u0015\u0011\u0010C\u0001\u0003G#\u0012bTAS\u0003O\u000bI+a+\t\re\t\t\u000b1\u0001\u001c\u0011\u0019\t\u0013\u0011\u0015a\u0001M!1\u0001'!)A\u0002\rBa!OAQ\u0001\u00041\u0003\u0006CAQ\u0003'\u000bI*!(\t\u0011\u0005\r\u0015\u0011\u0010C\u0001\u0003c#raTAZ\u0003k\u000b9\f\u0003\u0004\u001a\u0003_\u0003\ra\u0007\u0005\u0007C\u0005=\u0006\u0019\u0001\u0014\t\re\ny\u000b1\u0001'Q!\ty+a%\u0002\u001a\u0006u\u0005BCAB\u0003s\n\t\u0011\"!\u0002>Ryq*a0\u0002B\u0006\r\u0017QYAd\u0003\u0013\fY\r\u0003\u0004\u001a\u0003w\u0003\ra\u0007\u0005\u0007C\u0005m\u0006\u0019A\u0012\t\rA\nY\f1\u00013\u0011\u0019I\u00141\u0018a\u0001e!1Q(a/A\u0002}Ba!RA^\u0001\u0004y\u0004\u0002C%\u0002<B\u0005\t\u0019A\u0012\t\u0015\u0005=\u0017\u0011PA\u0001\n\u0003\u000b\t.A\u0004v]\u0006\u0004\b\u000f\\=\u0015\t\u0005M\u00171\u001c\t\u0005\u001b\u0011\n)\u000e\u0005\u0006\u000e\u0003/\\2E\r\u001a@\u007f\rJ1!!7\u000f\u0005\u0019!V\u000f\u001d7fo!I\u0011Q\\Ag\u0003\u0003\u0005\raT\u0001\u0004q\u0012\u0002\u0004\"CAq\u0003s\n\n\u0011\"\u0001r\u0003m!C.Z:tS:LG\u000fJ4sK\u0006$XM\u001d\u0013eK\u001a\fW\u000f\u001c;%o!I\u0011Q]A=#\u0003%\t!]\u0001\u0010CB\u0004H.\u001f\u0013eK\u001a\fW\u000f\u001c;%o!Q\u0011\u0011^A=\u0003\u0003%I!a;\u0002\u0017I,\u0017\r\u001a*fg>dg/\u001a\u000b\u0003\u0003[\u0004B!a\u0006\u0002p&!\u0011\u0011_A\r\u0005\u0019y%M[3di\u0002"
)
public class AccumulableInfo implements Product, Serializable {
   private final long id;
   private final Option<String> name;
   private final Option<Object> update;
   private final Option<Object> value;
   private final boolean internal;
   private final boolean countFailedValues;
   private final Option<String> metadata;

   public static Option<String> apply$default$7() {
      return AccumulableInfo$.MODULE$.apply$default$7();
   }

   public static Option<String> $lessinit$greater$default$7() {
      return AccumulableInfo$.MODULE$.$lessinit$greater$default$7();
   }

   public static Option<Tuple7<Object, Option<String>, Option<Object>, Option<Object>, Object, Object, Option<String>>> unapply(AccumulableInfo var0) {
      return AccumulableInfo$.MODULE$.unapply(var0);
   }

   public static AccumulableInfo apply(long var0, Option<String> var2, Option<Object> var3, Option<Object> var4, boolean var5, boolean var6, Option<String> var7) {
      return AccumulableInfo$.MODULE$.apply(var0, var2, var3, var4, var5, var6, var7);
   }

   public static AccumulableInfo apply(long var0, String var2, String var3) {
      return AccumulableInfo$.MODULE$.apply(var0, var2, var3);
   }

   public static AccumulableInfo apply(long var0, String var2, Option<String> var3, String var4) {
      return AccumulableInfo$.MODULE$.apply(var0, var2, var3, var4);
   }

   public static AccumulableInfo apply(long var0, String var2, Option<String> var3, String var4, boolean var5) {
      return AccumulableInfo$.MODULE$.apply(var0, var2, var3, var4, var5);
   }

   public boolean internal$1() {
      return this.internal;
   }

   public boolean countFailedValues$1() {
      return this.countFailedValues;
   }

   public Option<String> metadata$1() {
      return this.metadata;
   }

   public long id() {
      return this.id;
   }

   public Option<String> name() {
      return this.name;
   }

   public Option<Object> update() {
      return this.update;
   }

   public Option<Object> value() {
      return this.value;
   }

   public boolean internal() {
      return this.internal;
   }

   public boolean countFailedValues() {
      return this.countFailedValues;
   }

   public Option<String> metadata() {
      return this.metadata;
   }

   public AccumulableInfo copy(long id, Option<String> name, Option<Object> update, Option<Object> value, boolean internal, boolean countFailedValues, Option<String> metadata) {
      return new AccumulableInfo(id, name, update, value, internal, countFailedValues, metadata);
   }

   public long copy$default$1() {
      return this.id();
   }

   public Option<String> copy$default$2() {
      return this.name();
   }

   public Option<Object> copy$default$3() {
      return this.update();
   }

   public Option<Object> copy$default$4() {
      return this.value();
   }

   public boolean copy$default$5() {
      return this.internal();
   }

   public boolean copy$default$6() {
      return this.countFailedValues();
   }

   public Option<String> copy$default$7() {
      return this.metadata();
   }

   public String productPrefix() {
      return "AccumulableInfo";
   }

   public int productArity() {
      return 7;
   }

   public Object productElement(int x$1) {
      Object var10000;
      switch (x$1) {
         case 0:
            var10000 = BoxesRunTime.boxToLong(this.id());
            break;
         case 1:
            var10000 = this.name();
            break;
         case 2:
            var10000 = this.update();
            break;
         case 3:
            var10000 = this.value();
            break;
         case 4:
            var10000 = BoxesRunTime.boxToBoolean(this.internal$1());
            break;
         case 5:
            var10000 = BoxesRunTime.boxToBoolean(this.countFailedValues$1());
            break;
         case 6:
            var10000 = this.metadata$1();
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
      return x$1 instanceof AccumulableInfo;
   }

   public int hashCode() {
      int var1 = -889275714;
      var1 = Statics.mix(var1, Statics.longHash(this.id()));
      var1 = Statics.mix(var1, Statics.anyHash(this.name()));
      var1 = Statics.mix(var1, Statics.anyHash(this.update()));
      var1 = Statics.mix(var1, Statics.anyHash(this.value()));
      var1 = Statics.mix(var1, this.internal$1() ? 1231 : 1237);
      var1 = Statics.mix(var1, this.countFailedValues$1() ? 1231 : 1237);
      var1 = Statics.mix(var1, Statics.anyHash(this.metadata$1()));
      return Statics.finalizeHash(var1, 7);
   }

   public String toString() {
      return ScalaRunTime$.MODULE$._toString(this);
   }

   public boolean equals(Object x$1) {
      boolean var13;
      if (this != x$1) {
         label86: {
            boolean var3;
            if (x$1 instanceof AccumulableInfo) {
               var3 = true;
            } else {
               var3 = false;
            }

            if (var3) {
               label68: {
                  AccumulableInfo var4 = (AccumulableInfo)x$1;
                  if (this.id() == var4.id()) {
                     label77: {
                        Option var10000 = this.name();
                        Option var5 = var4.name();
                        if (var10000 == null) {
                           if (var5 != null) {
                              break label77;
                           }
                        } else if (!var10000.equals(var5)) {
                           break label77;
                        }

                        var10000 = this.update();
                        Option var6 = var4.update();
                        if (var10000 == null) {
                           if (var6 != null) {
                              break label77;
                           }
                        } else if (!var10000.equals(var6)) {
                           break label77;
                        }

                        var10000 = this.value();
                        Option var7 = var4.value();
                        if (var10000 == null) {
                           if (var7 != null) {
                              break label77;
                           }
                        } else if (!var10000.equals(var7)) {
                           break label77;
                        }

                        if (this.internal$1() == var4.internal$1() && this.countFailedValues$1() == var4.countFailedValues$1()) {
                           label49: {
                              var10000 = this.metadata$1();
                              Option var8 = var4.metadata$1();
                              if (var10000 == null) {
                                 if (var8 != null) {
                                    break label49;
                                 }
                              } else if (!var10000.equals(var8)) {
                                 break label49;
                              }

                              if (var4.canEqual(this)) {
                                 var13 = true;
                                 break label68;
                              }
                           }
                        }
                     }
                  }

                  var13 = false;
               }

               if (var13) {
                  break label86;
               }
            }

            var13 = false;
            return var13;
         }
      }

      var13 = true;
      return var13;
   }

   public AccumulableInfo(long id, Option<String> name, Option<Object> update, Option<Object> value, boolean internal, boolean countFailedValues, Option<String> metadata) {
      this.id = id;
      this.name = name;
      this.update = update;
      this.value = value;
      this.internal = internal;
      this.countFailedValues = countFailedValues;
      this.metadata = metadata;
      Product$class.$init$(this);
   }
}
