package org.apache.spark.deploy;

import scala.Function1;
import scala.Option;
import scala.Product;
import scala.Product$class;
import scala.Serializable;
import scala.Tuple5;
import scala.collection.Iterator;
import scala.reflect.ScalaSignature;
import scala.runtime.BoxesRunTime;
import scala.runtime.ScalaRunTime$;
import scala.runtime.Statics;

@ScalaSignature(
   bytes = "\u0006\u0001\u0005ud\u0001B\u0001\u0003\t.\u0011ab\u00149uS>t\u0017i]:jO:,'O\u0003\u0002\u0004\t\u00051A-\u001a9m_fT!!\u0002\u0004\u0002\u000bM\u0004\u0018M]6\u000b\u0005\u001dA\u0011AB1qC\u000eDWMC\u0001\n\u0003\ry'oZ\u0002\u0001'\u0011\u0001ABE\u000b\u0011\u00055\u0001R\"\u0001\b\u000b\u0003=\tQa]2bY\u0006L!!\u0005\b\u0003\r\u0005s\u0017PU3g!\ti1#\u0003\u0002\u0015\u001d\t9\u0001K]8ek\u000e$\bCA\u0007\u0017\u0013\t9bB\u0001\u0007TKJL\u0017\r\\5{C\ndW\r\u0003\u0005\u001a\u0001\tU\r\u0011\"\u0001\u001b\u0003\u00151\u0018\r\\;f+\u0005Y\u0002C\u0001\u000f \u001d\tiQ$\u0003\u0002\u001f\u001d\u00051\u0001K]3eK\u001aL!\u0001I\u0011\u0003\rM#(/\u001b8h\u0015\tqb\u0002\u0003\u0005$\u0001\tE\t\u0015!\u0003\u001c\u0003\u00191\u0018\r\\;fA!AQ\u0005\u0001BK\u0002\u0013\u0005a%\u0001\bdYV\u001cH/\u001a:NC:\fw-\u001a:\u0016\u0003\u001d\u0002\"!\u0004\u0015\n\u0005%r!aA%oi\"A1\u0006\u0001B\tB\u0003%q%A\bdYV\u001cH/\u001a:NC:\fw-\u001a:!\u0011!i\u0003A!f\u0001\n\u00031\u0013A\u00033fa2|\u00170T8eK\"Aq\u0006\u0001B\tB\u0003%q%A\u0006eKBdw._'pI\u0016\u0004\u0003\u0002C\u0019\u0001\u0005+\u0007I\u0011\u0001\u000e\u0002\u0011\rdw\n\u001d;j_:D\u0001b\r\u0001\u0003\u0012\u0003\u0006IaG\u0001\nG2|\u0005\u000f^5p]\u0002B\u0001\"\u000e\u0001\u0003\u0016\u0004%\tAG\u0001\bG>tgmS3z\u0011!9\u0004A!E!\u0002\u0013Y\u0012\u0001C2p]\u001a\\U-\u001f\u0011\t\u000be\u0002A\u0011\u0001\u001e\u0002\rqJg.\u001b;?)\u0019YTHP A\u0003B\u0011A\bA\u0007\u0002\u0005!)\u0011\u0004\u000fa\u00017!)Q\u0005\u000fa\u0001O!)Q\u0006\u000fa\u0001O!9\u0011\u0007\u000fI\u0001\u0002\u0004Y\u0002bB\u001b9!\u0003\u0005\ra\u0007\u0005\b\u0007\u0002\t\t\u0011\"\u0001E\u0003\u0011\u0019w\u000e]=\u0015\rm*ei\u0012%J\u0011\u001dI\"\t%AA\u0002mAq!\n\"\u0011\u0002\u0003\u0007q\u0005C\u0004.\u0005B\u0005\t\u0019A\u0014\t\u000fE\u0012\u0005\u0013!a\u00017!9QG\u0011I\u0001\u0002\u0004Y\u0002bB&\u0001#\u0003%\t\u0001T\u0001\u000fG>\u0004\u0018\u0010\n3fM\u0006,H\u000e\u001e\u00132+\u0005i%FA\u000eOW\u0005y\u0005C\u0001)V\u001b\u0005\t&B\u0001*T\u0003%)hn\u00195fG.,GM\u0003\u0002U\u001d\u0005Q\u0011M\u001c8pi\u0006$\u0018n\u001c8\n\u0005Y\u000b&!E;oG\",7m[3e-\u0006\u0014\u0018.\u00198dK\"9\u0001\fAI\u0001\n\u0003I\u0016AD2paf$C-\u001a4bk2$HEM\u000b\u00025*\u0012qE\u0014\u0005\b9\u0002\t\n\u0011\"\u0001Z\u00039\u0019w\u000e]=%I\u00164\u0017-\u001e7uIMBqA\u0018\u0001\u0012\u0002\u0013\u0005A*\u0001\bd_BLH\u0005Z3gCVdG\u000f\n\u001b\t\u000f\u0001\u0004\u0011\u0013!C\u0001\u0019\u0006q1m\u001c9zI\u0011,g-Y;mi\u0012*\u0004b\u00022\u0001\u0003\u0003%\teY\u0001\u000eaJ|G-^2u!J,g-\u001b=\u0016\u0003\u0011\u0004\"!\u001a6\u000e\u0003\u0019T!a\u001a5\u0002\t1\fgn\u001a\u0006\u0002S\u0006!!.\u0019<b\u0013\t\u0001c\rC\u0004m\u0001\u0005\u0005I\u0011\u0001\u0014\u0002\u0019A\u0014x\u000eZ;di\u0006\u0013\u0018\u000e^=\t\u000f9\u0004\u0011\u0011!C\u0001_\u0006q\u0001O]8ek\u000e$X\t\\3nK:$HC\u00019t!\ti\u0011/\u0003\u0002s\u001d\t\u0019\u0011I\\=\t\u000fQl\u0017\u0011!a\u0001O\u0005\u0019\u0001\u0010J\u0019\t\u000fY\u0004\u0011\u0011!C!o\u0006y\u0001O]8ek\u000e$\u0018\n^3sCR|'/F\u0001y!\rIH\u0010]\u0007\u0002u*\u00111PD\u0001\u000bG>dG.Z2uS>t\u0017BA?{\u0005!IE/\u001a:bi>\u0014\b\u0002C@\u0001\u0003\u0003%\t!!\u0001\u0002\u0011\r\fg.R9vC2$B!a\u0001\u0002\nA\u0019Q\"!\u0002\n\u0007\u0005\u001daBA\u0004C_>dW-\u00198\t\u000fQt\u0018\u0011!a\u0001a\"I\u0011Q\u0002\u0001\u0002\u0002\u0013\u0005\u0013qB\u0001\tQ\u0006\u001c\bnQ8eKR\tq\u0005C\u0005\u0002\u0014\u0001\t\t\u0011\"\u0011\u0002\u0016\u0005AAo\\*ue&tw\rF\u0001e\u0011%\tI\u0002AA\u0001\n\u0003\nY\"\u0001\u0004fcV\fGn\u001d\u000b\u0005\u0003\u0007\ti\u0002\u0003\u0005u\u0003/\t\t\u00111\u0001q\u000f%\t\tCAA\u0001\u0012\u0013\t\u0019#\u0001\bPaRLwN\\!tg&<g.\u001a:\u0011\u0007q\n)C\u0002\u0005\u0002\u0005\u0005\u0005\t\u0012BA\u0014'\u0015\t)#!\u000b\u0016!)\tY#!\r\u001cO\u001dZ2dO\u0007\u0003\u0003[Q1!a\f\u000f\u0003\u001d\u0011XO\u001c;j[\u0016LA!a\r\u0002.\t\t\u0012IY:ue\u0006\u001cGOR;oGRLwN\\\u001b\t\u000fe\n)\u0003\"\u0001\u00028Q\u0011\u00111\u0005\u0005\u000b\u0003'\t)#!A\u0005F\u0005U\u0001BCA\u001f\u0003K\t\t\u0011\"!\u0002@\u0005)\u0011\r\u001d9msRY1(!\u0011\u0002D\u0005\u0015\u0013qIA%\u0011\u0019I\u00121\ba\u00017!1Q%a\u000fA\u0002\u001dBa!LA\u001e\u0001\u00049\u0003\u0002C\u0019\u0002<A\u0005\t\u0019A\u000e\t\u0011U\nY\u0004%AA\u0002mA!\"!\u0014\u0002&\u0005\u0005I\u0011QA(\u0003\u001d)h.\u00199qYf$B!!\u0015\u0002^A)Q\"a\u0015\u0002X%\u0019\u0011Q\u000b\b\u0003\r=\u0003H/[8o!!i\u0011\u0011L\u000e(OmY\u0012bAA.\u001d\t1A+\u001e9mKVB\u0011\"a\u0018\u0002L\u0005\u0005\t\u0019A\u001e\u0002\u0007a$\u0003\u0007C\u0005\u0002d\u0005\u0015\u0012\u0013!C\u0001\u0019\u0006y\u0011\r\u001d9ms\u0012\"WMZ1vYR$C\u0007C\u0005\u0002h\u0005\u0015\u0012\u0013!C\u0001\u0019\u0006y\u0011\r\u001d9ms\u0012\"WMZ1vYR$S\u0007C\u0005\u0002l\u0005\u0015\u0012\u0013!C\u0001\u0019\u0006YB\u0005\\3tg&t\u0017\u000e\u001e\u0013he\u0016\fG/\u001a:%I\u00164\u0017-\u001e7uIQB\u0011\"a\u001c\u0002&E\u0005I\u0011\u0001'\u00027\u0011bWm]:j]&$He\u001a:fCR,'\u000f\n3fM\u0006,H\u000e\u001e\u00136\u0011)\t\u0019(!\n\u0002\u0002\u0013%\u0011QO\u0001\fe\u0016\fGMU3t_24X\r\u0006\u0002\u0002xA\u0019Q-!\u001f\n\u0007\u0005mdM\u0001\u0004PE*,7\r\u001e"
)
public class OptionAssigner implements Product, Serializable {
   private final String value;
   private final int clusterManager;
   private final int deployMode;
   private final String clOption;
   private final String confKey;

   public static String $lessinit$greater$default$5() {
      return OptionAssigner$.MODULE$.$lessinit$greater$default$5();
   }

   public static String $lessinit$greater$default$4() {
      return OptionAssigner$.MODULE$.$lessinit$greater$default$4();
   }

   public static String apply$default$5() {
      return OptionAssigner$.MODULE$.apply$default$5();
   }

   public static String apply$default$4() {
      return OptionAssigner$.MODULE$.apply$default$4();
   }

   public static Option<Tuple5<String, Object, Object, String, String>> unapply(OptionAssigner var0) {
      return OptionAssigner$.MODULE$.unapply(var0);
   }

   public static OptionAssigner apply(String var0, int var1, int var2, String var3, String var4) {
      return OptionAssigner$.MODULE$.apply(var0, var1, var2, var3, var4);
   }

   public static Function1<Tuple5<String, Object, Object, String, String>, OptionAssigner> tupled() {
      return OptionAssigner$.MODULE$.tupled();
   }

   public static Function1<String, Function1<Object, Function1<Object, Function1<String, Function1<String, OptionAssigner>>>>> curried() {
      return OptionAssigner$.MODULE$.curried();
   }

   public String value() {
      return this.value;
   }

   public int clusterManager() {
      return this.clusterManager;
   }

   public int deployMode() {
      return this.deployMode;
   }

   public String clOption() {
      return this.clOption;
   }

   public String confKey() {
      return this.confKey;
   }

   public OptionAssigner copy(String value, int clusterManager, int deployMode, String clOption, String confKey) {
      return new OptionAssigner(value, clusterManager, deployMode, clOption, confKey);
   }

   public String copy$default$1() {
      return this.value();
   }

   public int copy$default$2() {
      return this.clusterManager();
   }

   public int copy$default$3() {
      return this.deployMode();
   }

   public String copy$default$4() {
      return this.clOption();
   }

   public String copy$default$5() {
      return this.confKey();
   }

   public String productPrefix() {
      return "OptionAssigner";
   }

   public int productArity() {
      return 5;
   }

   public Object productElement(int x$1) {
      Object var10000;
      switch (x$1) {
         case 0:
            var10000 = this.value();
            break;
         case 1:
            var10000 = BoxesRunTime.boxToInteger(this.clusterManager());
            break;
         case 2:
            var10000 = BoxesRunTime.boxToInteger(this.deployMode());
            break;
         case 3:
            var10000 = this.clOption();
            break;
         case 4:
            var10000 = this.confKey();
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
      return x$1 instanceof OptionAssigner;
   }

   public int hashCode() {
      int var1 = -889275714;
      var1 = Statics.mix(var1, Statics.anyHash(this.value()));
      var1 = Statics.mix(var1, this.clusterManager());
      var1 = Statics.mix(var1, this.deployMode());
      var1 = Statics.mix(var1, Statics.anyHash(this.clOption()));
      var1 = Statics.mix(var1, Statics.anyHash(this.confKey()));
      return Statics.finalizeHash(var1, 5);
   }

   public String toString() {
      return ScalaRunTime$.MODULE$._toString(this);
   }

   public boolean equals(Object x$1) {
      boolean var11;
      if (this != x$1) {
         label75: {
            boolean var3;
            if (x$1 instanceof OptionAssigner) {
               var3 = true;
            } else {
               var3 = false;
            }

            if (var3) {
               label57: {
                  label56: {
                     OptionAssigner var4 = (OptionAssigner)x$1;
                     String var10000 = this.value();
                     String var5 = var4.value();
                     if (var10000 == null) {
                        if (var5 != null) {
                           break label56;
                        }
                     } else if (!var10000.equals(var5)) {
                        break label56;
                     }

                     if (this.clusterManager() == var4.clusterManager() && this.deployMode() == var4.deployMode()) {
                        label66: {
                           var10000 = this.clOption();
                           String var6 = var4.clOption();
                           if (var10000 == null) {
                              if (var6 != null) {
                                 break label66;
                              }
                           } else if (!var10000.equals(var6)) {
                              break label66;
                           }

                           var10000 = this.confKey();
                           String var7 = var4.confKey();
                           if (var10000 == null) {
                              if (var7 != null) {
                                 break label66;
                              }
                           } else if (!var10000.equals(var7)) {
                              break label66;
                           }

                           if (var4.canEqual(this)) {
                              var11 = true;
                              break label57;
                           }
                        }
                     }
                  }

                  var11 = false;
               }

               if (var11) {
                  break label75;
               }
            }

            var11 = false;
            return var11;
         }
      }

      var11 = true;
      return var11;
   }

   public OptionAssigner(String value, int clusterManager, int deployMode, String clOption, String confKey) {
      this.value = value;
      this.clusterManager = clusterManager;
      this.deployMode = deployMode;
      this.clOption = clOption;
      this.confKey = confKey;
      Product$class.$init$(this);
   }
}
