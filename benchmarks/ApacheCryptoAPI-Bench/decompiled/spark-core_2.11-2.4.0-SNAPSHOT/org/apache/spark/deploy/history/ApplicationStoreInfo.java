package org.apache.spark.deploy.history;

import org.apache.spark.util.kvstore.KVIndex;
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
   bytes = "\u0006\u0001\u00055f\u0001B\u0001\u0003\t6\u0011A#\u00119qY&\u001c\u0017\r^5p]N#xN]3J]\u001a|'BA\u0002\u0005\u0003\u001dA\u0017n\u001d;pefT!!\u0002\u0004\u0002\r\u0011,\u0007\u000f\\8z\u0015\t9\u0001\"A\u0003ta\u0006\u00148N\u0003\u0002\n\u0015\u00051\u0011\r]1dQ\u0016T\u0011aC\u0001\u0004_J<7\u0001A\n\u0005\u00019!r\u0003\u0005\u0002\u0010%5\t\u0001CC\u0001\u0012\u0003\u0015\u00198-\u00197b\u0013\t\u0019\u0002C\u0001\u0004B]f\u0014VM\u001a\t\u0003\u001fUI!A\u0006\t\u0003\u000fA\u0013x\u000eZ;diB\u0011q\u0002G\u0005\u00033A\u0011AbU3sS\u0006d\u0017N_1cY\u0016D\u0001b\u0007\u0001\u0003\u0016\u0004%\t\u0001H\u0001\u0005a\u0006$\b.F\u0001\u001e!\tq\u0012E\u0004\u0002\u0010?%\u0011\u0001\u0005E\u0001\u0007!J,G-\u001a4\n\u0005\t\u001a#AB*ue&twM\u0003\u0002!!!\u0012!$\n\u0016\u0003M9\u0002\"a\n\u0017\u000e\u0003!R!!\u000b\u0016\u0002\u000f-48\u000f^8sK*\u00111FB\u0001\u0005kRLG.\u0003\u0002.Q\t91JV%oI\u0016D8&A\u0018\u0011\u0005A*T\"A\u0019\u000b\u0005I\u001a\u0014\u0001B7fi\u0006T!\u0001\u000e\t\u0002\u0015\u0005tgn\u001c;bi&|g.\u0003\u00027c\t1q-\u001a;uKJD\u0001\u0002\u000f\u0001\u0003\u0012\u0003\u0006I!H\u0001\u0006a\u0006$\b\u000e\t\u0005\tu\u0001\u0011)\u001a!C\u0001w\u0005QA.Y:u\u0003\u000e\u001cWm]:\u0016\u0003q\u0002\"aD\u001f\n\u0005y\u0002\"\u0001\u0002'p]\u001eDC!O\u0013A\u0003\u0006)a/\u00197vK\u0006\n!\b\u0003\u0005D\u0001\tE\t\u0015!\u0003=\u0003-a\u0017m\u001d;BG\u000e,7o\u001d\u0011\t\u0011\u0015\u0003!Q3A\u0005\u0002q\tQ!\u00199q\u0013\u0012D\u0001b\u0012\u0001\u0003\u0012\u0003\u0006I!H\u0001\u0007CB\u0004\u0018\n\u001a\u0011\t\u0011%\u0003!Q3A\u0005\u0002)\u000b\u0011\"\u0019;uK6\u0004H/\u00133\u0016\u0003-\u00032a\u0004'\u001e\u0013\ti\u0005C\u0001\u0004PaRLwN\u001c\u0005\t\u001f\u0002\u0011\t\u0012)A\u0005\u0017\u0006Q\u0011\r\u001e;f[B$\u0018\n\u001a\u0011\t\u0011E\u0003!Q3A\u0005\u0002m\nAa]5{K\"A1\u000b\u0001B\tB\u0003%A(A\u0003tSj,\u0007\u0005C\u0003V\u0001\u0011\u0005a+\u0001\u0004=S:LGO\u0010\u000b\u0007/fS6\fX/\u0011\u0005a\u0003Q\"\u0001\u0002\t\u000bm!\u0006\u0019A\u000f\t\u000bi\"\u0006\u0019\u0001\u001f\t\u000b\u0015#\u0006\u0019A\u000f\t\u000b%#\u0006\u0019A&\t\u000bE#\u0006\u0019\u0001\u001f\t\u000f}\u0003\u0011\u0011!C\u0001A\u0006!1m\u001c9z)\u00199\u0016MY2eK\"91D\u0018I\u0001\u0002\u0004i\u0002b\u0002\u001e_!\u0003\u0005\r\u0001\u0010\u0005\b\u000bz\u0003\n\u00111\u0001\u001e\u0011\u001dIe\f%AA\u0002-Cq!\u00150\u0011\u0002\u0003\u0007A\bC\u0004h\u0001E\u0005I\u0011\u00015\u0002\u001d\r|\u0007/\u001f\u0013eK\u001a\fW\u000f\u001c;%cU\t\u0011N\u000b\u0002\u001eU.\n1\u000e\u0005\u0002m_6\tQN\u0003\u0002og\u0005IQO\\2iK\u000e\\W\rZ\u0005\u0003a6\u0014\u0011#\u001e8dQ\u0016\u001c7.\u001a3WCJL\u0017M\\2f\u0011\u001d\u0011\b!%A\u0005\u0002M\fabY8qs\u0012\"WMZ1vYR$#'F\u0001uU\ta$\u000eC\u0004w\u0001E\u0005I\u0011\u00015\u0002\u001d\r|\u0007/\u001f\u0013eK\u001a\fW\u000f\u001c;%g!9\u0001\u0010AI\u0001\n\u0003I\u0018AD2paf$C-\u001a4bk2$H\u0005N\u000b\u0002u*\u00121J\u001b\u0005\by\u0002\t\n\u0011\"\u0001t\u00039\u0019w\u000e]=%I\u00164\u0017-\u001e7uIUBqA \u0001\u0002\u0002\u0013\u0005s0A\u0007qe>$Wo\u0019;Qe\u00164\u0017\u000e_\u000b\u0003\u0003\u0003\u0001B!a\u0001\u0002\u000e5\u0011\u0011Q\u0001\u0006\u0005\u0003\u000f\tI!\u0001\u0003mC:<'BAA\u0006\u0003\u0011Q\u0017M^1\n\u0007\t\n)\u0001C\u0005\u0002\u0012\u0001\t\t\u0011\"\u0001\u0002\u0014\u0005a\u0001O]8ek\u000e$\u0018I]5usV\u0011\u0011Q\u0003\t\u0004\u001f\u0005]\u0011bAA\r!\t\u0019\u0011J\u001c;\t\u0013\u0005u\u0001!!A\u0005\u0002\u0005}\u0011A\u00049s_\u0012,8\r^#mK6,g\u000e\u001e\u000b\u0005\u0003C\t9\u0003E\u0002\u0010\u0003GI1!!\n\u0011\u0005\r\te.\u001f\u0005\u000b\u0003S\tY\"!AA\u0002\u0005U\u0011a\u0001=%c!I\u0011Q\u0006\u0001\u0002\u0002\u0013\u0005\u0013qF\u0001\u0010aJ|G-^2u\u0013R,'/\u0019;peV\u0011\u0011\u0011\u0007\t\u0007\u0003g\tI$!\t\u000e\u0005\u0005U\"bAA\u001c!\u0005Q1m\u001c7mK\u000e$\u0018n\u001c8\n\t\u0005m\u0012Q\u0007\u0002\t\u0013R,'/\u0019;pe\"I\u0011q\b\u0001\u0002\u0002\u0013\u0005\u0011\u0011I\u0001\tG\u0006tW)];bYR!\u00111IA%!\ry\u0011QI\u0005\u0004\u0003\u000f\u0002\"a\u0002\"p_2,\u0017M\u001c\u0005\u000b\u0003S\ti$!AA\u0002\u0005\u0005\u0002\"CA'\u0001\u0005\u0005I\u0011IA(\u0003!A\u0017m\u001d5D_\u0012,GCAA\u000b\u0011%\t\u0019\u0006AA\u0001\n\u0003\n)&\u0001\u0005u_N#(/\u001b8h)\t\t\t\u0001C\u0005\u0002Z\u0001\t\t\u0011\"\u0011\u0002\\\u00051Q-];bYN$B!a\u0011\u0002^!Q\u0011\u0011FA,\u0003\u0003\u0005\r!!\t\b\u0013\u0005\u0005$!!A\t\n\u0005\r\u0014\u0001F!qa2L7-\u0019;j_:\u001cFo\u001c:f\u0013:4w\u000eE\u0002Y\u0003K2\u0001\"\u0001\u0002\u0002\u0002#%\u0011qM\n\u0006\u0003K\nIg\u0006\t\u000b\u0003W\n\t(\b\u001f\u001e\u0017r:VBAA7\u0015\r\ty\u0007E\u0001\beVtG/[7f\u0013\u0011\t\u0019(!\u001c\u0003#\u0005\u00137\u000f\u001e:bGR4UO\\2uS>tW\u0007C\u0004V\u0003K\"\t!a\u001e\u0015\u0005\u0005\r\u0004BCA*\u0003K\n\t\u0011\"\u0012\u0002V!Q\u0011QPA3\u0003\u0003%\t)a \u0002\u000b\u0005\u0004\b\u000f\\=\u0015\u0017]\u000b\t)!\"\u0002\n\u0006-\u0015Q\u0012\u0005\u00077\u0005m\u0004\u0019A\u000f)\u0007\u0005\u0005U\u0005\u0003\u0004;\u0003w\u0002\r\u0001\u0010\u0015\u0006\u0003\u000b+\u0003)\u0011\u0005\u0007\u000b\u0006m\u0004\u0019A\u000f\t\r%\u000bY\b1\u0001L\u0011\u0019\t\u00161\u0010a\u0001y!Q\u0011\u0011SA3\u0003\u0003%\t)a%\u0002\u000fUt\u0017\r\u001d9msR!\u0011QSAO!\u0011yA*a&\u0011\u0011=\tI*\b\u001f\u001e\u0017rJ1!a'\u0011\u0005\u0019!V\u000f\u001d7fk!I\u0011qTAH\u0003\u0003\u0005\raV\u0001\u0004q\u0012\u0002\u0004BCAR\u0003K\n\t\u0011\"\u0003\u0002&\u0006Y!/Z1e%\u0016\u001cx\u000e\u001c<f)\t\t9\u000b\u0005\u0003\u0002\u0004\u0005%\u0016\u0002BAV\u0003\u000b\u0011aa\u00142kK\u000e$\b"
)
public class ApplicationStoreInfo implements Product, Serializable {
   private final String path;
   private final long lastAccess;
   private final String appId;
   private final Option<String> attemptId;
   private final long size;

   public static Option<Tuple5<String, Object, String, Option<String>, Object>> unapply(ApplicationStoreInfo var0) {
      return ApplicationStoreInfo$.MODULE$.unapply(var0);
   }

   public static ApplicationStoreInfo apply(@KVIndex String var0, @KVIndex("lastAccess") long var1, String var3, Option<String> var4, long var5) {
      return ApplicationStoreInfo$.MODULE$.apply(var0, var1, var3, var4, var5);
   }

   public static Function1<Tuple5<String, Object, String, Option<String>, Object>, ApplicationStoreInfo> tupled() {
      return ApplicationStoreInfo$.MODULE$.tupled();
   }

   public static Function1<String, Function1<Object, Function1<String, Function1<Option<String>, Function1<Object, ApplicationStoreInfo>>>>> curried() {
      return ApplicationStoreInfo$.MODULE$.curried();
   }

   @KVIndex
   public String path() {
      return this.path;
   }

   @KVIndex("lastAccess")
   public long lastAccess() {
      return this.lastAccess;
   }

   public String appId() {
      return this.appId;
   }

   public Option<String> attemptId() {
      return this.attemptId;
   }

   public long size() {
      return this.size;
   }

   public ApplicationStoreInfo copy(String path, long lastAccess, String appId, Option<String> attemptId, long size) {
      return new ApplicationStoreInfo(path, lastAccess, appId, attemptId, size);
   }

   public String copy$default$1() {
      return this.path();
   }

   public long copy$default$2() {
      return this.lastAccess();
   }

   public String copy$default$3() {
      return this.appId();
   }

   public Option<String> copy$default$4() {
      return this.attemptId();
   }

   public long copy$default$5() {
      return this.size();
   }

   public String productPrefix() {
      return "ApplicationStoreInfo";
   }

   public int productArity() {
      return 5;
   }

   public Object productElement(int x$1) {
      Object var10000;
      switch (x$1) {
         case 0:
            var10000 = this.path();
            break;
         case 1:
            var10000 = BoxesRunTime.boxToLong(this.lastAccess());
            break;
         case 2:
            var10000 = this.appId();
            break;
         case 3:
            var10000 = this.attemptId();
            break;
         case 4:
            var10000 = BoxesRunTime.boxToLong(this.size());
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
      return x$1 instanceof ApplicationStoreInfo;
   }

   public int hashCode() {
      int var1 = -889275714;
      var1 = Statics.mix(var1, Statics.anyHash(this.path()));
      var1 = Statics.mix(var1, Statics.longHash(this.lastAccess()));
      var1 = Statics.mix(var1, Statics.anyHash(this.appId()));
      var1 = Statics.mix(var1, Statics.anyHash(this.attemptId()));
      var1 = Statics.mix(var1, Statics.longHash(this.size()));
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
            if (x$1 instanceof ApplicationStoreInfo) {
               var3 = true;
            } else {
               var3 = false;
            }

            if (var3) {
               label57: {
                  label56: {
                     ApplicationStoreInfo var4 = (ApplicationStoreInfo)x$1;
                     String var10000 = this.path();
                     String var5 = var4.path();
                     if (var10000 == null) {
                        if (var5 != null) {
                           break label56;
                        }
                     } else if (!var10000.equals(var5)) {
                        break label56;
                     }

                     if (this.lastAccess() == var4.lastAccess()) {
                        label66: {
                           var10000 = this.appId();
                           String var6 = var4.appId();
                           if (var10000 == null) {
                              if (var6 != null) {
                                 break label66;
                              }
                           } else if (!var10000.equals(var6)) {
                              break label66;
                           }

                           Option var9 = this.attemptId();
                           Option var7 = var4.attemptId();
                           if (var9 == null) {
                              if (var7 != null) {
                                 break label66;
                              }
                           } else if (!var9.equals(var7)) {
                              break label66;
                           }

                           if (this.size() == var4.size() && var4.canEqual(this)) {
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

   public ApplicationStoreInfo(String path, long lastAccess, String appId, Option<String> attemptId, long size) {
      this.path = path;
      this.lastAccess = lastAccess;
      this.appId = appId;
      this.attemptId = attemptId;
      this.size = size;
      Product$class.$init$(this);
   }
}
