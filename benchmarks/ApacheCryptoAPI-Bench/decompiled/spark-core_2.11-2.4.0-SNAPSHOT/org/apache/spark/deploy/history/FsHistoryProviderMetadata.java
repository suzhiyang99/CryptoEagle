package org.apache.spark.deploy.history;

import scala.Function1;
import scala.Option;
import scala.Product;
import scala.Product$class;
import scala.Serializable;
import scala.Tuple3;
import scala.collection.Iterator;
import scala.reflect.ScalaSignature;
import scala.runtime.BoxesRunTime;
import scala.runtime.ScalaRunTime$;
import scala.runtime.Statics;

@ScalaSignature(
   bytes = "\u0006\u0001\u0005Uc!B\u0001\u0003\u0001\na!!\u0007$t\u0011&\u001cHo\u001c:z!J|g/\u001b3fe6+G/\u00193bi\u0006T!a\u0001\u0003\u0002\u000f!L7\u000f^8ss*\u0011QAB\u0001\u0007I\u0016\u0004Hn\\=\u000b\u0005\u001dA\u0011!B:qCJ\\'BA\u0005\u000b\u0003\u0019\t\u0007/Y2iK*\t1\"A\u0002pe\u001e\u001cB\u0001A\u0007\u0014-A\u0011a\"E\u0007\u0002\u001f)\t\u0001#A\u0003tG\u0006d\u0017-\u0003\u0002\u0013\u001f\t1\u0011I\\=SK\u001a\u0004\"A\u0004\u000b\n\u0005Uy!a\u0002)s_\u0012,8\r\u001e\t\u0003\u001d]I!\u0001G\b\u0003\u0019M+'/[1mSj\f'\r\\3\t\u0011i\u0001!Q3A\u0005\u0002q\tqA^3sg&|gn\u0001\u0001\u0016\u0003u\u0001\"A\u0004\u0010\n\u0005}y!\u0001\u0002'p]\u001eD\u0001\"\t\u0001\u0003\u0012\u0003\u0006I!H\u0001\tm\u0016\u00148/[8oA!A1\u0005\u0001BK\u0002\u0013\u0005A$A\u0005vSZ+'o]5p]\"AQ\u0005\u0001B\tB\u0003%Q$\u0001\u0006vSZ+'o]5p]\u0002B\u0001b\n\u0001\u0003\u0016\u0004%\t\u0001K\u0001\u0007Y><G)\u001b:\u0016\u0003%\u0002\"AK\u0017\u000f\u00059Y\u0013B\u0001\u0017\u0010\u0003\u0019\u0001&/\u001a3fM&\u0011af\f\u0002\u0007'R\u0014\u0018N\\4\u000b\u00051z\u0001\u0002C\u0019\u0001\u0005#\u0005\u000b\u0011B\u0015\u0002\u000f1|w\rR5sA!)1\u0007\u0001C\u0001i\u00051A(\u001b8jiz\"B!N\u001c9sA\u0011a\u0007A\u0007\u0002\u0005!)!D\ra\u0001;!)1E\ra\u0001;!)qE\ra\u0001S!91\bAA\u0001\n\u0003a\u0014\u0001B2paf$B!N\u001f?\u007f!9!D\u000fI\u0001\u0002\u0004i\u0002bB\u0012;!\u0003\u0005\r!\b\u0005\bOi\u0002\n\u00111\u0001*\u0011\u001d\t\u0005!%A\u0005\u0002\t\u000babY8qs\u0012\"WMZ1vYR$\u0013'F\u0001DU\tiBiK\u0001F!\t15*D\u0001H\u0015\tA\u0015*A\u0005v]\u000eDWmY6fI*\u0011!jD\u0001\u000bC:tw\u000e^1uS>t\u0017B\u0001'H\u0005E)hn\u00195fG.,GMV1sS\u0006t7-\u001a\u0005\b\u001d\u0002\t\n\u0011\"\u0001C\u00039\u0019w\u000e]=%I\u00164\u0017-\u001e7uIIBq\u0001\u0015\u0001\u0012\u0002\u0013\u0005\u0011+\u0001\bd_BLH\u0005Z3gCVdG\u000fJ\u001a\u0016\u0003IS#!\u000b#\t\u000fQ\u0003\u0011\u0011!C!+\u0006i\u0001O]8ek\u000e$\bK]3gSb,\u0012A\u0016\t\u0003/rk\u0011\u0001\u0017\u0006\u00033j\u000bA\u0001\\1oO*\t1,\u0001\u0003kCZ\f\u0017B\u0001\u0018Y\u0011\u001dq\u0006!!A\u0005\u0002}\u000bA\u0002\u001d:pIV\u001cG/\u0011:jif,\u0012\u0001\u0019\t\u0003\u001d\u0005L!AY\b\u0003\u0007%sG\u000fC\u0004e\u0001\u0005\u0005I\u0011A3\u0002\u001dA\u0014x\u000eZ;di\u0016cW-\\3oiR\u0011a-\u001b\t\u0003\u001d\u001dL!\u0001[\b\u0003\u0007\u0005s\u0017\u0010C\u0004kG\u0006\u0005\t\u0019\u00011\u0002\u0007a$\u0013\u0007C\u0004m\u0001\u0005\u0005I\u0011I7\u0002\u001fA\u0014x\u000eZ;di&#XM]1u_J,\u0012A\u001c\t\u0004_J4W\"\u00019\u000b\u0005E|\u0011AC2pY2,7\r^5p]&\u00111\u000f\u001d\u0002\t\u0013R,'/\u0019;pe\"9Q\u000fAA\u0001\n\u00031\u0018\u0001C2b]\u0016\u000bX/\u00197\u0015\u0005]T\bC\u0001\by\u0013\tIxBA\u0004C_>dW-\u00198\t\u000f)$\u0018\u0011!a\u0001M\"9A\u0010AA\u0001\n\u0003j\u0018\u0001\u00035bg\"\u001cu\u000eZ3\u0015\u0003\u0001D\u0001b \u0001\u0002\u0002\u0013\u0005\u0013\u0011A\u0001\ti>\u001cFO]5oOR\ta\u000bC\u0005\u0002\u0006\u0001\t\t\u0011\"\u0011\u0002\b\u00051Q-];bYN$2a^A\u0005\u0011!Q\u00171AA\u0001\u0002\u00041wACA\u0007\u0005\u0005\u0005\t\u0012\u0001\u0002\u0002\u0010\u0005Ibi\u001d%jgR|'/\u001f)s_ZLG-\u001a:NKR\fG-\u0019;b!\r1\u0014\u0011\u0003\u0004\n\u0003\t\t\t\u0011#\u0001\u0003\u0003'\u0019R!!\u0005\u0002\u0016Y\u0001\u0002\"a\u0006\u0002\u001eui\u0012&N\u0007\u0003\u00033Q1!a\u0007\u0010\u0003\u001d\u0011XO\u001c;j[\u0016LA!a\b\u0002\u001a\t\t\u0012IY:ue\u0006\u001cGOR;oGRLwN\\\u001a\t\u000fM\n\t\u0002\"\u0001\u0002$Q\u0011\u0011q\u0002\u0005\n\u007f\u0006E\u0011\u0011!C#\u0003\u0003A!\"!\u000b\u0002\u0012\u0005\u0005I\u0011QA\u0016\u0003\u0015\t\u0007\u000f\u001d7z)\u001d)\u0014QFA\u0018\u0003cAaAGA\u0014\u0001\u0004i\u0002BB\u0012\u0002(\u0001\u0007Q\u0004\u0003\u0004(\u0003O\u0001\r!\u000b\u0005\u000b\u0003k\t\t\"!A\u0005\u0002\u0006]\u0012aB;oCB\u0004H.\u001f\u000b\u0005\u0003s\t)\u0005E\u0003\u000f\u0003w\ty$C\u0002\u0002>=\u0011aa\u00149uS>t\u0007C\u0002\b\u0002Bui\u0012&C\u0002\u0002D=\u0011a\u0001V;qY\u0016\u001c\u0004\"CA$\u0003g\t\t\u00111\u00016\u0003\rAH\u0005\r\u0005\u000b\u0003\u0017\n\t\"!A\u0005\n\u00055\u0013a\u0003:fC\u0012\u0014Vm]8mm\u0016$\"!a\u0014\u0011\u0007]\u000b\t&C\u0002\u0002Ta\u0013aa\u00142kK\u000e$\b"
)
public class FsHistoryProviderMetadata implements Product, Serializable {
   private final long version;
   private final long uiVersion;
   private final String logDir;

   public static Option<Tuple3<Object, Object, String>> unapply(FsHistoryProviderMetadata var0) {
      return FsHistoryProviderMetadata$.MODULE$.unapply(var0);
   }

   public static FsHistoryProviderMetadata apply(long var0, long var2, String var4) {
      return FsHistoryProviderMetadata$.MODULE$.apply(var0, var2, var4);
   }

   public static Function1<Tuple3<Object, Object, String>, FsHistoryProviderMetadata> tupled() {
      return FsHistoryProviderMetadata$.MODULE$.tupled();
   }

   public static Function1<Object, Function1<Object, Function1<String, FsHistoryProviderMetadata>>> curried() {
      return FsHistoryProviderMetadata$.MODULE$.curried();
   }

   public long version() {
      return this.version;
   }

   public long uiVersion() {
      return this.uiVersion;
   }

   public String logDir() {
      return this.logDir;
   }

   public FsHistoryProviderMetadata copy(long version, long uiVersion, String logDir) {
      return new FsHistoryProviderMetadata(version, uiVersion, logDir);
   }

   public long copy$default$1() {
      return this.version();
   }

   public long copy$default$2() {
      return this.uiVersion();
   }

   public String copy$default$3() {
      return this.logDir();
   }

   public String productPrefix() {
      return "FsHistoryProviderMetadata";
   }

   public int productArity() {
      return 3;
   }

   public Object productElement(int x$1) {
      Object var10000;
      switch (x$1) {
         case 0:
            var10000 = BoxesRunTime.boxToLong(this.version());
            break;
         case 1:
            var10000 = BoxesRunTime.boxToLong(this.uiVersion());
            break;
         case 2:
            var10000 = this.logDir();
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
      return x$1 instanceof FsHistoryProviderMetadata;
   }

   public int hashCode() {
      int var1 = -889275714;
      var1 = Statics.mix(var1, Statics.longHash(this.version()));
      var1 = Statics.mix(var1, Statics.longHash(this.uiVersion()));
      var1 = Statics.mix(var1, Statics.anyHash(this.logDir()));
      return Statics.finalizeHash(var1, 3);
   }

   public String toString() {
      return ScalaRunTime$.MODULE$._toString(this);
   }

   public boolean equals(Object x$1) {
      boolean var7;
      if (this != x$1) {
         label56: {
            boolean var3;
            if (x$1 instanceof FsHistoryProviderMetadata) {
               var3 = true;
            } else {
               var3 = false;
            }

            if (var3) {
               label39: {
                  FsHistoryProviderMetadata var4 = (FsHistoryProviderMetadata)x$1;
                  if (this.version() == var4.version() && this.uiVersion() == var4.uiVersion()) {
                     label36: {
                        String var10000 = this.logDir();
                        String var5 = var4.logDir();
                        if (var10000 == null) {
                           if (var5 != null) {
                              break label36;
                           }
                        } else if (!var10000.equals(var5)) {
                           break label36;
                        }

                        if (var4.canEqual(this)) {
                           var7 = true;
                           break label39;
                        }
                     }
                  }

                  var7 = false;
               }

               if (var7) {
                  break label56;
               }
            }

            var7 = false;
            return var7;
         }
      }

      var7 = true;
      return var7;
   }

   public FsHistoryProviderMetadata(long version, long uiVersion, String logDir) {
      this.version = version;
      this.uiVersion = uiVersion;
      this.logDir = logDir;
      Product$class.$init$(this);
   }
}
