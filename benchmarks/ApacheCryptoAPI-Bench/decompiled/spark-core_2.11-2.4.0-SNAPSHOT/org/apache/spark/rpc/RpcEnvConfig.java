package org.apache.spark.rpc;

import org.apache.spark.SecurityManager;
import org.apache.spark.SparkConf;
import scala.Function1;
import scala.Option;
import scala.Product;
import scala.Product$class;
import scala.Serializable;
import scala.Tuple8;
import scala.collection.Iterator;
import scala.reflect.ScalaSignature;
import scala.runtime.BoxesRunTime;
import scala.runtime.ScalaRunTime$;
import scala.runtime.Statics;

@ScalaSignature(
   bytes = "\u0006\u0001\u0005\rg!B\u0001\u0003\u0001\u0012Q!\u0001\u0004*qG\u0016sgoQ8oM&<'BA\u0002\u0005\u0003\r\u0011\bo\u0019\u0006\u0003\u000b\u0019\tQa\u001d9be.T!a\u0002\u0005\u0002\r\u0005\u0004\u0018m\u00195f\u0015\u0005I\u0011aA8sON!\u0001aC\t\u0015!\taq\"D\u0001\u000e\u0015\u0005q\u0011!B:dC2\f\u0017B\u0001\t\u000e\u0005\u0019\te.\u001f*fMB\u0011ABE\u0005\u0003'5\u0011q\u0001\u0015:pIV\u001cG\u000f\u0005\u0002\r+%\u0011a#\u0004\u0002\r'\u0016\u0014\u0018.\u00197ju\u0006\u0014G.\u001a\u0005\t1\u0001\u0011)\u001a!C\u00015\u0005!1m\u001c8g\u0007\u0001)\u0012a\u0007\t\u00039ui\u0011\u0001B\u0005\u0003=\u0011\u0011\u0011b\u00159be.\u001cuN\u001c4\t\u0011\u0001\u0002!\u0011#Q\u0001\nm\tQaY8oM\u0002B\u0001B\t\u0001\u0003\u0016\u0004%\taI\u0001\u0005]\u0006lW-F\u0001%!\t)\u0003F\u0004\u0002\rM%\u0011q%D\u0001\u0007!J,G-\u001a4\n\u0005%R#AB*ue&twM\u0003\u0002(\u001b!AA\u0006\u0001B\tB\u0003%A%A\u0003oC6,\u0007\u0005\u0003\u0005/\u0001\tU\r\u0011\"\u0001$\u0003-\u0011\u0017N\u001c3BI\u0012\u0014Xm]:\t\u0011A\u0002!\u0011#Q\u0001\n\u0011\nABY5oI\u0006#GM]3tg\u0002B\u0001B\r\u0001\u0003\u0016\u0004%\taI\u0001\u0011C\u00124XM\u001d;jg\u0016\fE\r\u001a:fgND\u0001\u0002\u000e\u0001\u0003\u0012\u0003\u0006I\u0001J\u0001\u0012C\u00124XM\u001d;jg\u0016\fE\r\u001a:fgN\u0004\u0003\u0002\u0003\u001c\u0001\u0005+\u0007I\u0011A\u001c\u0002\tA|'\u000f^\u000b\u0002qA\u0011A\"O\u0005\u0003u5\u00111!\u00138u\u0011!a\u0004A!E!\u0002\u0013A\u0014!\u00029peR\u0004\u0003\u0002\u0003 \u0001\u0005+\u0007I\u0011A \u0002\u001fM,7-\u001e:jifl\u0015M\\1hKJ,\u0012\u0001\u0011\t\u00039\u0005K!A\u0011\u0003\u0003\u001fM+7-\u001e:jifl\u0015M\\1hKJD\u0001\u0002\u0012\u0001\u0003\u0012\u0003\u0006I\u0001Q\u0001\u0011g\u0016\u001cWO]5us6\u000bg.Y4fe\u0002B\u0001B\u0012\u0001\u0003\u0016\u0004%\taN\u0001\u000f]VlWk]1cY\u0016\u001cuN]3t\u0011!A\u0005A!E!\u0002\u0013A\u0014a\u00048v[V\u001b\u0018M\u00197f\u0007>\u0014Xm\u001d\u0011\t\u0011)\u0003!Q3A\u0005\u0002-\u000b!b\u00197jK:$Xj\u001c3f+\u0005a\u0005C\u0001\u0007N\u0013\tqUBA\u0004C_>dW-\u00198\t\u0011A\u0003!\u0011#Q\u0001\n1\u000b1b\u00197jK:$Xj\u001c3fA!)!\u000b\u0001C\u0001'\u00061A(\u001b8jiz\"\u0012\u0002\u0016,X1fS6\fX/\u0011\u0005U\u0003Q\"\u0001\u0002\t\u000ba\t\u0006\u0019A\u000e\t\u000b\t\n\u0006\u0019\u0001\u0013\t\u000b9\n\u0006\u0019\u0001\u0013\t\u000bI\n\u0006\u0019\u0001\u0013\t\u000bY\n\u0006\u0019\u0001\u001d\t\u000by\n\u0006\u0019\u0001!\t\u000b\u0019\u000b\u0006\u0019\u0001\u001d\t\u000b)\u000b\u0006\u0019\u0001'\t\u000f}\u0003\u0011\u0011!C\u0001A\u0006!1m\u001c9z)%!\u0016MY2eK\u001a<\u0007\u000eC\u0004\u0019=B\u0005\t\u0019A\u000e\t\u000f\tr\u0006\u0013!a\u0001I!9aF\u0018I\u0001\u0002\u0004!\u0003b\u0002\u001a_!\u0003\u0005\r\u0001\n\u0005\bmy\u0003\n\u00111\u00019\u0011\u001dqd\f%AA\u0002\u0001CqA\u00120\u0011\u0002\u0003\u0007\u0001\bC\u0004K=B\u0005\t\u0019\u0001'\t\u000f)\u0004\u0011\u0013!C\u0001W\u0006q1m\u001c9zI\u0011,g-Y;mi\u0012\nT#\u00017+\u0005mi7&\u00018\u0011\u0005=$X\"\u00019\u000b\u0005E\u0014\u0018!C;oG\",7m[3e\u0015\t\u0019X\"\u0001\u0006b]:|G/\u0019;j_:L!!\u001e9\u0003#Ut7\r[3dW\u0016$g+\u0019:jC:\u001cW\rC\u0004x\u0001E\u0005I\u0011\u0001=\u0002\u001d\r|\u0007/\u001f\u0013eK\u001a\fW\u000f\u001c;%eU\t\u0011P\u000b\u0002%[\"91\u0010AI\u0001\n\u0003A\u0018AD2paf$C-\u001a4bk2$He\r\u0005\b{\u0002\t\n\u0011\"\u0001y\u00039\u0019w\u000e]=%I\u00164\u0017-\u001e7uIQB\u0001b \u0001\u0012\u0002\u0013\u0005\u0011\u0011A\u0001\u000fG>\u0004\u0018\u0010\n3fM\u0006,H\u000e\u001e\u00136+\t\t\u0019A\u000b\u00029[\"I\u0011q\u0001\u0001\u0012\u0002\u0013\u0005\u0011\u0011B\u0001\u000fG>\u0004\u0018\u0010\n3fM\u0006,H\u000e\u001e\u00137+\t\tYA\u000b\u0002A[\"I\u0011q\u0002\u0001\u0012\u0002\u0013\u0005\u0011\u0011A\u0001\u000fG>\u0004\u0018\u0010\n3fM\u0006,H\u000e\u001e\u00138\u0011%\t\u0019\u0002AI\u0001\n\u0003\t)\"\u0001\bd_BLH\u0005Z3gCVdG\u000f\n\u001d\u0016\u0005\u0005]!F\u0001'n\u0011%\tY\u0002AA\u0001\n\u0003\ni\"A\u0007qe>$Wo\u0019;Qe\u00164\u0017\u000e_\u000b\u0003\u0003?\u0001B!!\t\u0002,5\u0011\u00111\u0005\u0006\u0005\u0003K\t9#\u0001\u0003mC:<'BAA\u0015\u0003\u0011Q\u0017M^1\n\u0007%\n\u0019\u0003\u0003\u0005\u00020\u0001\t\t\u0011\"\u00018\u00031\u0001(o\u001c3vGR\f%/\u001b;z\u0011%\t\u0019\u0004AA\u0001\n\u0003\t)$\u0001\bqe>$Wo\u0019;FY\u0016lWM\u001c;\u0015\t\u0005]\u0012Q\b\t\u0004\u0019\u0005e\u0012bAA\u001e\u001b\t\u0019\u0011I\\=\t\u0013\u0005}\u0012\u0011GA\u0001\u0002\u0004A\u0014a\u0001=%c!I\u00111\t\u0001\u0002\u0002\u0013\u0005\u0013QI\u0001\u0010aJ|G-^2u\u0013R,'/\u0019;peV\u0011\u0011q\t\t\u0007\u0003\u0013\ny%a\u000e\u000e\u0005\u0005-#bAA'\u001b\u0005Q1m\u001c7mK\u000e$\u0018n\u001c8\n\t\u0005E\u00131\n\u0002\t\u0013R,'/\u0019;pe\"I\u0011Q\u000b\u0001\u0002\u0002\u0013\u0005\u0011qK\u0001\tG\u0006tW)];bYR\u0019A*!\u0017\t\u0015\u0005}\u00121KA\u0001\u0002\u0004\t9\u0004C\u0005\u0002^\u0001\t\t\u0011\"\u0011\u0002`\u0005A\u0001.Y:i\u0007>$W\rF\u00019\u0011%\t\u0019\u0007AA\u0001\n\u0003\n)'\u0001\u0005u_N#(/\u001b8h)\t\ty\u0002C\u0005\u0002j\u0001\t\t\u0011\"\u0011\u0002l\u00051Q-];bYN$2\u0001TA7\u0011)\ty$a\u001a\u0002\u0002\u0003\u0007\u0011qG\u0004\u000b\u0003c\u0012\u0011\u0011!E\u0001\t\u0005M\u0014\u0001\u0004*qG\u0016sgoQ8oM&<\u0007cA+\u0002v\u0019I\u0011AAA\u0001\u0012\u0003!\u0011qO\n\u0006\u0003k\nI\b\u0006\t\u000e\u0003w\n\ti\u0007\u0013%Ia\u0002\u0005\b\u0014+\u000e\u0005\u0005u$bAA@\u001b\u00059!/\u001e8uS6,\u0017\u0002BAB\u0003{\u0012\u0011#\u00112tiJ\f7\r\u001e$v]\u000e$\u0018n\u001c89\u0011\u001d\u0011\u0016Q\u000fC\u0001\u0003\u000f#\"!a\u001d\t\u0015\u0005\r\u0014QOA\u0001\n\u000b\n)\u0007\u0003\u0006\u0002\u000e\u0006U\u0014\u0011!CA\u0003\u001f\u000bQ!\u00199qYf$\u0012\u0003VAI\u0003'\u000b)*a&\u0002\u001a\u0006m\u0015QTAP\u0011\u0019A\u00121\u0012a\u00017!1!%a#A\u0002\u0011BaALAF\u0001\u0004!\u0003B\u0002\u001a\u0002\f\u0002\u0007A\u0005\u0003\u00047\u0003\u0017\u0003\r\u0001\u000f\u0005\u0007}\u0005-\u0005\u0019\u0001!\t\r\u0019\u000bY\t1\u00019\u0011\u0019Q\u00151\u0012a\u0001\u0019\"Q\u00111UA;\u0003\u0003%\t)!*\u0002\u000fUt\u0017\r\u001d9msR!\u0011qUAZ!\u0015a\u0011\u0011VAW\u0013\r\tY+\u0004\u0002\u0007\u001fB$\u0018n\u001c8\u0011\u00171\tyk\u0007\u0013%Ia\u0002\u0005\bT\u0005\u0004\u0003ck!A\u0002+va2,\u0007\bC\u0005\u00026\u0006\u0005\u0016\u0011!a\u0001)\u0006\u0019\u0001\u0010\n\u0019\t\u0015\u0005e\u0016QOA\u0001\n\u0013\tY,A\u0006sK\u0006$'+Z:pYZ,GCAA_!\u0011\t\t#a0\n\t\u0005\u0005\u00171\u0005\u0002\u0007\u001f\nTWm\u0019;"
)
public class RpcEnvConfig implements Product, Serializable {
   private final SparkConf conf;
   private final String name;
   private final String bindAddress;
   private final String advertiseAddress;
   private final int port;
   private final SecurityManager securityManager;
   private final int numUsableCores;
   private final boolean clientMode;

   public static Option<Tuple8<SparkConf, String, String, String, Object, SecurityManager, Object, Object>> unapply(RpcEnvConfig var0) {
      return RpcEnvConfig$.MODULE$.unapply(var0);
   }

   public static RpcEnvConfig apply(SparkConf var0, String var1, String var2, String var3, int var4, SecurityManager var5, int var6, boolean var7) {
      return RpcEnvConfig$.MODULE$.apply(var0, var1, var2, var3, var4, var5, var6, var7);
   }

   public static Function1<Tuple8<SparkConf, String, String, String, Object, SecurityManager, Object, Object>, RpcEnvConfig> tupled() {
      return RpcEnvConfig$.MODULE$.tupled();
   }

   public static Function1<SparkConf, Function1<String, Function1<String, Function1<String, Function1<Object, Function1<SecurityManager, Function1<Object, Function1<Object, RpcEnvConfig>>>>>>>> curried() {
      return RpcEnvConfig$.MODULE$.curried();
   }

   public SparkConf conf() {
      return this.conf;
   }

   public String name() {
      return this.name;
   }

   public String bindAddress() {
      return this.bindAddress;
   }

   public String advertiseAddress() {
      return this.advertiseAddress;
   }

   public int port() {
      return this.port;
   }

   public SecurityManager securityManager() {
      return this.securityManager;
   }

   public int numUsableCores() {
      return this.numUsableCores;
   }

   public boolean clientMode() {
      return this.clientMode;
   }

   public RpcEnvConfig copy(SparkConf conf, String name, String bindAddress, String advertiseAddress, int port, SecurityManager securityManager, int numUsableCores, boolean clientMode) {
      return new RpcEnvConfig(conf, name, bindAddress, advertiseAddress, port, securityManager, numUsableCores, clientMode);
   }

   public SparkConf copy$default$1() {
      return this.conf();
   }

   public String copy$default$2() {
      return this.name();
   }

   public String copy$default$3() {
      return this.bindAddress();
   }

   public String copy$default$4() {
      return this.advertiseAddress();
   }

   public int copy$default$5() {
      return this.port();
   }

   public SecurityManager copy$default$6() {
      return this.securityManager();
   }

   public int copy$default$7() {
      return this.numUsableCores();
   }

   public boolean copy$default$8() {
      return this.clientMode();
   }

   public String productPrefix() {
      return "RpcEnvConfig";
   }

   public int productArity() {
      return 8;
   }

   public Object productElement(int x$1) {
      Object var10000;
      switch (x$1) {
         case 0:
            var10000 = this.conf();
            break;
         case 1:
            var10000 = this.name();
            break;
         case 2:
            var10000 = this.bindAddress();
            break;
         case 3:
            var10000 = this.advertiseAddress();
            break;
         case 4:
            var10000 = BoxesRunTime.boxToInteger(this.port());
            break;
         case 5:
            var10000 = this.securityManager();
            break;
         case 6:
            var10000 = BoxesRunTime.boxToInteger(this.numUsableCores());
            break;
         case 7:
            var10000 = BoxesRunTime.boxToBoolean(this.clientMode());
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
      return x$1 instanceof RpcEnvConfig;
   }

   public int hashCode() {
      int var1 = -889275714;
      var1 = Statics.mix(var1, Statics.anyHash(this.conf()));
      var1 = Statics.mix(var1, Statics.anyHash(this.name()));
      var1 = Statics.mix(var1, Statics.anyHash(this.bindAddress()));
      var1 = Statics.mix(var1, Statics.anyHash(this.advertiseAddress()));
      var1 = Statics.mix(var1, this.port());
      var1 = Statics.mix(var1, Statics.anyHash(this.securityManager()));
      var1 = Statics.mix(var1, this.numUsableCores());
      var1 = Statics.mix(var1, this.clientMode() ? 1231 : 1237);
      return Statics.finalizeHash(var1, 8);
   }

   public String toString() {
      return ScalaRunTime$.MODULE$._toString(this);
   }

   public boolean equals(Object x$1) {
      boolean var15;
      if (this != x$1) {
         label95: {
            boolean var3;
            if (x$1 instanceof RpcEnvConfig) {
               var3 = true;
            } else {
               var3 = false;
            }

            if (var3) {
               label77: {
                  label86: {
                     RpcEnvConfig var4 = (RpcEnvConfig)x$1;
                     SparkConf var10000 = this.conf();
                     SparkConf var5 = var4.conf();
                     if (var10000 == null) {
                        if (var5 != null) {
                           break label86;
                        }
                     } else if (!var10000.equals(var5)) {
                        break label86;
                     }

                     String var10 = this.name();
                     String var6 = var4.name();
                     if (var10 == null) {
                        if (var6 != null) {
                           break label86;
                        }
                     } else if (!var10.equals(var6)) {
                        break label86;
                     }

                     var10 = this.bindAddress();
                     String var7 = var4.bindAddress();
                     if (var10 == null) {
                        if (var7 != null) {
                           break label86;
                        }
                     } else if (!var10.equals(var7)) {
                        break label86;
                     }

                     var10 = this.advertiseAddress();
                     String var8 = var4.advertiseAddress();
                     if (var10 == null) {
                        if (var8 != null) {
                           break label86;
                        }
                     } else if (!var10.equals(var8)) {
                        break label86;
                     }

                     if (this.port() == var4.port()) {
                        label55: {
                           SecurityManager var13 = this.securityManager();
                           SecurityManager var9 = var4.securityManager();
                           if (var13 == null) {
                              if (var9 != null) {
                                 break label55;
                              }
                           } else if (!var13.equals(var9)) {
                              break label55;
                           }

                           if (this.numUsableCores() == var4.numUsableCores() && this.clientMode() == var4.clientMode() && var4.canEqual(this)) {
                              var15 = true;
                              break label77;
                           }
                        }
                     }
                  }

                  var15 = false;
               }

               if (var15) {
                  break label95;
               }
            }

            var15 = false;
            return var15;
         }
      }

      var15 = true;
      return var15;
   }

   public RpcEnvConfig(SparkConf conf, String name, String bindAddress, String advertiseAddress, int port, SecurityManager securityManager, int numUsableCores, boolean clientMode) {
      this.conf = conf;
      this.name = name;
      this.bindAddress = bindAddress;
      this.advertiseAddress = advertiseAddress;
      this.port = port;
      this.securityManager = securityManager;
      this.numUsableCores = numUsableCores;
      this.clientMode = clientMode;
      Product$class.$init$(this);
   }
}
