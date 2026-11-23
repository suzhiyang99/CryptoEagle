package org.apache.spark.deploy.history;

import scala.Function1;
import scala.Option;
import scala.Predef$;
import scala.Product;
import scala.Product$class;
import scala.Serializable;
import scala.StringContext;
import scala.Tuple2;
import scala.collection.Iterator;
import scala.collection.mutable.StringBuilder;
import scala.reflect.ScalaSignature;
import scala.runtime.BoxesRunTime;
import scala.runtime.ScalaRunTime$;

@ScalaSignature(
   bytes = "\u0006\u0001\u0005\u0005c!B\u0001\u0003\u0005\na!\u0001C\"bG\",7*Z=\u000b\u0005\r!\u0011a\u00025jgR|'/\u001f\u0006\u0003\u000b\u0019\ta\u0001Z3qY>L(BA\u0004\t\u0003\u0015\u0019\b/\u0019:l\u0015\tI!\"\u0001\u0004ba\u0006\u001c\u0007.\u001a\u0006\u0002\u0017\u0005\u0019qN]4\u0014\t\u0001i1C\u0006\t\u0003\u001dEi\u0011a\u0004\u0006\u0002!\u0005)1oY1mC&\u0011!c\u0004\u0002\u0007\u0003:L(+\u001a4\u0011\u00059!\u0012BA\u000b\u0010\u0005\u001d\u0001&o\u001c3vGR\u0004\"AD\f\n\u0005ay!\u0001D*fe&\fG.\u001b>bE2,\u0007\u0002\u0003\u000e\u0001\u0005+\u0007I\u0011\u0001\u000f\u0002\u000b\u0005\u0004\b/\u00133\u0004\u0001U\tQ\u0004\u0005\u0002\u001fC9\u0011abH\u0005\u0003A=\ta\u0001\u0015:fI\u00164\u0017B\u0001\u0012$\u0005\u0019\u0019FO]5oO*\u0011\u0001e\u0004\u0005\tK\u0001\u0011\t\u0012)A\u0005;\u00051\u0011\r\u001d9JI\u0002B\u0001b\n\u0001\u0003\u0016\u0004%\t\u0001K\u0001\nCR$X-\u001c9u\u0013\u0012,\u0012!\u000b\t\u0004\u001d)j\u0012BA\u0016\u0010\u0005\u0019y\u0005\u000f^5p]\"AQ\u0006\u0001B\tB\u0003%\u0011&\u0001\u0006biR,W\u000e\u001d;JI\u0002BQa\f\u0001\u0005\u0002A\na\u0001P5oSRtDcA\u00194iA\u0011!\u0007A\u0007\u0002\u0005!)!D\fa\u0001;!)qE\fa\u0001S!)a\u0007\u0001C!o\u0005AAo\\*ue&tw\rF\u0001\u001e\u0011\u001dI\u0004!!A\u0005\u0002i\nAaY8qsR\u0019\u0011g\u000f\u001f\t\u000fiA\u0004\u0013!a\u0001;!9q\u0005\u000fI\u0001\u0002\u0004I\u0003b\u0002 \u0001#\u0003%\taP\u0001\u000fG>\u0004\u0018\u0010\n3fM\u0006,H\u000e\u001e\u00132+\u0005\u0001%FA\u000fBW\u0005\u0011\u0005CA\"I\u001b\u0005!%BA#G\u0003%)hn\u00195fG.,GM\u0003\u0002H\u001f\u0005Q\u0011M\u001c8pi\u0006$\u0018n\u001c8\n\u0005%#%!E;oG\",7m[3e-\u0006\u0014\u0018.\u00198dK\"91\nAI\u0001\n\u0003a\u0015AD2paf$C-\u001a4bk2$HEM\u000b\u0002\u001b*\u0012\u0011&\u0011\u0005\b\u001f\u0002\t\t\u0011\"\u0011Q\u00035\u0001(o\u001c3vGR\u0004&/\u001a4jqV\t\u0011\u000b\u0005\u0002S/6\t1K\u0003\u0002U+\u0006!A.\u00198h\u0015\u00051\u0016\u0001\u00026bm\u0006L!AI*\t\u000fe\u0003\u0011\u0011!C\u00015\u0006a\u0001O]8ek\u000e$\u0018I]5usV\t1\f\u0005\u0002\u000f9&\u0011Ql\u0004\u0002\u0004\u0013:$\bbB0\u0001\u0003\u0003%\t\u0001Y\u0001\u000faJ|G-^2u\u000b2,W.\u001a8u)\t\tG\r\u0005\u0002\u000fE&\u00111m\u0004\u0002\u0004\u0003:L\bbB3_\u0003\u0003\u0005\raW\u0001\u0004q\u0012\n\u0004bB4\u0001\u0003\u0003%\t\u0005[\u0001\u0010aJ|G-^2u\u0013R,'/\u0019;peV\t\u0011\u000eE\u0002k[\u0006l\u0011a\u001b\u0006\u0003Y>\t!bY8mY\u0016\u001cG/[8o\u0013\tq7N\u0001\u0005Ji\u0016\u0014\u0018\r^8s\u0011\u001d\u0001\b!!A\u0005\u0002E\f\u0001bY1o\u000bF,\u0018\r\u001c\u000b\u0003eV\u0004\"AD:\n\u0005Q|!a\u0002\"p_2,\u0017M\u001c\u0005\bK>\f\t\u00111\u0001b\u0011\u001d9\b!!A\u0005Ba\f\u0001\u0002[1tQ\u000e{G-\u001a\u000b\u00027\"9!\u0010AA\u0001\n\u0003Z\u0018AB3rk\u0006d7\u000f\u0006\u0002sy\"9Q-_A\u0001\u0002\u0004\tw\u0001\u0003@\u0003\u0003\u0003E\tAA@\u0002\u0011\r\u000b7\r[3LKf\u00042AMA\u0001\r%\t!!!A\t\u0002\t\t\u0019aE\u0003\u0002\u0002\u0005\u0015a\u0003E\u0004\u0002\b\u00055Q$K\u0019\u000e\u0005\u0005%!bAA\u0006\u001f\u00059!/\u001e8uS6,\u0017\u0002BA\b\u0003\u0013\u0011\u0011#\u00112tiJ\f7\r\u001e$v]\u000e$\u0018n\u001c83\u0011\u001dy\u0013\u0011\u0001C\u0001\u0003'!\u0012a \u0005\nm\u0005\u0005\u0011\u0011!C#\u0003/!\u0012!\u0015\u0005\u000b\u00037\t\t!!A\u0005\u0002\u0006u\u0011!B1qa2LH#B\u0019\u0002 \u0005\u0005\u0002B\u0002\u000e\u0002\u001a\u0001\u0007Q\u0004\u0003\u0004(\u00033\u0001\r!\u000b\u0005\u000b\u0003K\t\t!!A\u0005\u0002\u0006\u001d\u0012aB;oCB\u0004H.\u001f\u000b\u0005\u0003S\t\t\u0004\u0005\u0003\u000fU\u0005-\u0002#\u0002\b\u0002.uI\u0013bAA\u0018\u001f\t1A+\u001e9mKJB\u0011\"a\r\u0002$\u0005\u0005\t\u0019A\u0019\u0002\u0007a$\u0003\u0007\u0003\u0006\u00028\u0005\u0005\u0011\u0011!C\u0005\u0003s\t1B]3bIJ+7o\u001c7wKR\u0011\u00111\b\t\u0004%\u0006u\u0012bAA '\n1qJ\u00196fGR\u0004"
)
public final class CacheKey implements Product, Serializable {
   private final String appId;
   private final Option<String> attemptId;

   public static Option<Tuple2<String, Option<String>>> unapply(CacheKey var0) {
      return CacheKey$.MODULE$.unapply(var0);
   }

   public static CacheKey apply(String var0, Option<String> var1) {
      return CacheKey$.MODULE$.apply(var0, var1);
   }

   public static Function1<Tuple2<String, Option<String>>, CacheKey> tupled() {
      return CacheKey$.MODULE$.tupled();
   }

   public static Function1<String, Function1<Option<String>, CacheKey>> curried() {
      return CacheKey$.MODULE$.curried();
   }

   public String appId() {
      return this.appId;
   }

   public Option<String> attemptId() {
      return this.attemptId;
   }

   public String toString() {
      return (new StringBuilder()).append((Object)this.appId()).append(this.attemptId().map(new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final String apply(String id) {
            return (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"/", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{id}));
         }
      }).getOrElse(new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final String apply() {
            return "";
         }
      })).toString();
   }

   public CacheKey copy(String appId, Option<String> attemptId) {
      return new CacheKey(appId, attemptId);
   }

   public String copy$default$1() {
      return this.appId();
   }

   public Option<String> copy$default$2() {
      return this.attemptId();
   }

   public String productPrefix() {
      return "CacheKey";
   }

   public int productArity() {
      return 2;
   }

   public Object productElement(int x$1) {
      Object var10000;
      switch (x$1) {
         case 0:
            var10000 = this.appId();
            break;
         case 1:
            var10000 = this.attemptId();
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
      return x$1 instanceof CacheKey;
   }

   public int hashCode() {
      return ScalaRunTime$.MODULE$._hashCode(this);
   }

   public boolean equals(Object x$1) {
      boolean var9;
      if (this != x$1) {
         label59: {
            boolean var3;
            if (x$1 instanceof CacheKey) {
               var3 = true;
            } else {
               var3 = false;
            }

            if (var3) {
               label42: {
                  label41: {
                     label40: {
                        CacheKey var4 = (CacheKey)x$1;
                        String var10000 = this.appId();
                        String var5 = var4.appId();
                        if (var10000 == null) {
                           if (var5 != null) {
                              break label40;
                           }
                        } else if (!var10000.equals(var5)) {
                           break label40;
                        }

                        Option var7 = this.attemptId();
                        Option var6 = var4.attemptId();
                        if (var7 == null) {
                           if (var6 == null) {
                              break label41;
                           }
                        } else if (var7.equals(var6)) {
                           break label41;
                        }
                     }

                     var9 = false;
                     break label42;
                  }

                  var9 = true;
               }

               if (var9) {
                  break label59;
               }
            }

            var9 = false;
            return var9;
         }
      }

      var9 = true;
      return var9;
   }

   public CacheKey(String appId, Option<String> attemptId) {
      this.appId = appId;
      this.attemptId = attemptId;
      Product$class.$init$(this);
   }
}
