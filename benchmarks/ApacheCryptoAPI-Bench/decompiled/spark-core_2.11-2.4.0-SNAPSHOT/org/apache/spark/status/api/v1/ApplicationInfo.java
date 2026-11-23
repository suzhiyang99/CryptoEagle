package org.apache.spark.status.api.v1;

import scala.Function1;
import scala.Option;
import scala.Product;
import scala.Product$class;
import scala.Serializable;
import scala.Tuple7;
import scala.collection.Iterator;
import scala.collection.Seq;
import scala.reflect.ScalaSignature;
import scala.runtime.BoxesRunTime;
import scala.runtime.ScalaRunTime$;

@ScalaSignature(
   bytes = "\u0006\u0001\u0005\u0005g\u0001B\u0001\u0003\u0001>\u0011q\"\u00119qY&\u001c\u0017\r^5p]&sgm\u001c\u0006\u0003\u0007\u0011\t!A^\u0019\u000b\u0005\u00151\u0011aA1qS*\u0011q\u0001C\u0001\u0007gR\fG/^:\u000b\u0005%Q\u0011!B:qCJ\\'BA\u0006\r\u0003\u0019\t\u0007/Y2iK*\tQ\"A\u0002pe\u001e\u001c\u0001a\u0005\u0003\u0001!YI\u0002CA\t\u0015\u001b\u0005\u0011\"\"A\n\u0002\u000bM\u001c\u0017\r\\1\n\u0005U\u0011\"AB!osJ+g\r\u0005\u0002\u0012/%\u0011\u0001D\u0005\u0002\b!J|G-^2u!\t\t\"$\u0003\u0002\u001c%\ta1+\u001a:jC2L'0\u00192mK\"AQ\u0004\u0001BK\u0002\u0013\u0005a$\u0001\u0002jIV\tq\u0004\u0005\u0002!G9\u0011\u0011#I\u0005\u0003EI\ta\u0001\u0015:fI\u00164\u0017B\u0001\u0013&\u0005\u0019\u0019FO]5oO*\u0011!E\u0005\u0005\tO\u0001\u0011\t\u0012)A\u0005?\u0005\u0019\u0011\u000e\u001a\u0011\t\u0011%\u0002!Q3A\u0005\u0002y\tAA\\1nK\"A1\u0006\u0001B\tB\u0003%q$A\u0003oC6,\u0007\u0005\u0003\u0005.\u0001\tU\r\u0011\"\u0001/\u00031\u0019wN]3t\u000fJ\fg\u000e^3e+\u0005y\u0003cA\t1e%\u0011\u0011G\u0005\u0002\u0007\u001fB$\u0018n\u001c8\u0011\u0005E\u0019\u0014B\u0001\u001b\u0013\u0005\rIe\u000e\u001e\u0005\tm\u0001\u0011\t\u0012)A\u0005_\u0005i1m\u001c:fg\u001e\u0013\u0018M\u001c;fI\u0002B\u0001\u0002\u000f\u0001\u0003\u0016\u0004%\tAL\u0001\t[\u0006D8i\u001c:fg\"A!\b\u0001B\tB\u0003%q&A\u0005nCb\u001cuN]3tA!AA\b\u0001BK\u0002\u0013\u0005a&\u0001\td_J,7\u000fU3s\u000bb,7-\u001e;pe\"Aa\b\u0001B\tB\u0003%q&A\td_J,7\u000fU3s\u000bb,7-\u001e;pe\u0002B\u0001\u0002\u0011\u0001\u0003\u0016\u0004%\tAL\u0001\u0014[\u0016lwN]=QKJ,\u00050Z2vi>\u0014XJ\u0011\u0005\t\u0005\u0002\u0011\t\u0012)A\u0005_\u0005!R.Z7pef\u0004VM]#yK\u000e,Ho\u001c:N\u0005\u0002B\u0001\u0002\u0012\u0001\u0003\u0016\u0004%\t!R\u0001\tCR$X-\u001c9ugV\ta\tE\u0002H\u001fJs!\u0001S'\u000f\u0005%cU\"\u0001&\u000b\u0005-s\u0011A\u0002\u001fs_>$h(C\u0001\u0014\u0013\tq%#A\u0004qC\u000e\\\u0017mZ3\n\u0005A\u000b&aA*fc*\u0011aJ\u0005\t\u0003'Rk\u0011AA\u0005\u0003+\n\u0011a#\u00119qY&\u001c\u0017\r^5p]\u0006#H/Z7qi&sgm\u001c\u0005\t/\u0002\u0011\t\u0012)A\u0005\r\u0006I\u0011\r\u001e;f[B$8\u000f\t\u0005\u00073\u0002!\t\u0001\u0003.\u0002\rqJg.\u001b;?)!YF,\u00180`A\u0006\u0014\u0007CA*\u0001\u0011\u0015i\u0002\f1\u0001 \u0011\u0015I\u0003\f1\u0001 \u0011\u0015i\u0003\f1\u00010\u0011\u0015A\u0004\f1\u00010\u0011\u0015a\u0004\f1\u00010\u0011\u0015\u0001\u0005\f1\u00010\u0011\u0015!\u0005\f1\u0001G\u0011\u001d!\u0007!!A\u0005\u0002\u0015\fAaY8qsRA1LZ4iS*\\G\u000eC\u0004\u001eGB\u0005\t\u0019A\u0010\t\u000f%\u001a\u0007\u0013!a\u0001?!9Qf\u0019I\u0001\u0002\u0004y\u0003b\u0002\u001dd!\u0003\u0005\ra\f\u0005\by\r\u0004\n\u00111\u00010\u0011\u001d\u00015\r%AA\u0002=Bq\u0001R2\u0011\u0002\u0003\u0007a\tC\u0004o\u0001E\u0005I\u0011A8\u0002\u001d\r|\u0007/\u001f\u0013eK\u001a\fW\u000f\u001c;%cU\t\u0001O\u000b\u0002 c.\n!\u000f\u0005\u0002tq6\tAO\u0003\u0002vm\u0006IQO\\2iK\u000e\\W\r\u001a\u0006\u0003oJ\t!\"\u00198o_R\fG/[8o\u0013\tIHOA\tv]\u000eDWmY6fIZ\u000b'/[1oG\u0016Dqa\u001f\u0001\u0012\u0002\u0013\u0005q.\u0001\bd_BLH\u0005Z3gCVdG\u000f\n\u001a\t\u000fu\u0004\u0011\u0013!C\u0001}\u0006q1m\u001c9zI\u0011,g-Y;mi\u0012\u001aT#A@+\u0005=\n\b\u0002CA\u0002\u0001E\u0005I\u0011\u0001@\u0002\u001d\r|\u0007/\u001f\u0013eK\u001a\fW\u000f\u001c;%i!A\u0011q\u0001\u0001\u0012\u0002\u0013\u0005a0\u0001\bd_BLH\u0005Z3gCVdG\u000fJ\u001b\t\u0011\u0005-\u0001!%A\u0005\u0002y\fabY8qs\u0012\"WMZ1vYR$c\u0007C\u0005\u0002\u0010\u0001\t\n\u0011\"\u0001\u0002\u0012\u0005q1m\u001c9zI\u0011,g-Y;mi\u0012:TCAA\nU\t1\u0015\u000fC\u0005\u0002\u0018\u0001\t\t\u0011\"\u0011\u0002\u001a\u0005i\u0001O]8ek\u000e$\bK]3gSb,\"!a\u0007\u0011\t\u0005u\u0011qE\u0007\u0003\u0003?QA!!\t\u0002$\u0005!A.\u00198h\u0015\t\t)#\u0001\u0003kCZ\f\u0017b\u0001\u0013\u0002 !I\u00111\u0006\u0001\u0002\u0002\u0013\u0005\u0011QF\u0001\raJ|G-^2u\u0003JLG/_\u000b\u0002e!I\u0011\u0011\u0007\u0001\u0002\u0002\u0013\u0005\u00111G\u0001\u000faJ|G-^2u\u000b2,W.\u001a8u)\u0011\t)$a\u000f\u0011\u0007E\t9$C\u0002\u0002:I\u00111!\u00118z\u0011%\ti$a\f\u0002\u0002\u0003\u0007!'A\u0002yIEB\u0011\"!\u0011\u0001\u0003\u0003%\t%a\u0011\u0002\u001fA\u0014x\u000eZ;di&#XM]1u_J,\"!!\u0012\u0011\r\u0005\u001d\u0013QJA\u001b\u001b\t\tIEC\u0002\u0002LI\t!bY8mY\u0016\u001cG/[8o\u0013\u0011\ty%!\u0013\u0003\u0011%#XM]1u_JD\u0011\"a\u0015\u0001\u0003\u0003%\t!!\u0016\u0002\u0011\r\fg.R9vC2$B!a\u0016\u0002^A\u0019\u0011#!\u0017\n\u0007\u0005m#CA\u0004C_>dW-\u00198\t\u0015\u0005u\u0012\u0011KA\u0001\u0002\u0004\t)\u0004C\u0005\u0002b\u0001\t\t\u0011\"\u0011\u0002d\u0005A\u0001.Y:i\u0007>$W\rF\u00013\u0011%\t9\u0007AA\u0001\n\u0003\nI'\u0001\u0005u_N#(/\u001b8h)\t\tY\u0002C\u0005\u0002n\u0001\t\t\u0011\"\u0011\u0002p\u00051Q-];bYN$B!a\u0016\u0002r!Q\u0011QHA6\u0003\u0003\u0005\r!!\u000e\b\u0013\u0005U$!!A\t\u0002\u0005]\u0014aD!qa2L7-\u0019;j_:LeNZ8\u0011\u0007M\u000bIH\u0002\u0005\u0002\u0005\u0005\u0005\t\u0012AA>'\u0015\tI(! \u001a!1\ty(!\" ?=zsf\f$\\\u001b\t\t\tIC\u0002\u0002\u0004J\tqA];oi&lW-\u0003\u0003\u0002\b\u0006\u0005%!E!cgR\u0014\u0018m\u0019;Gk:\u001cG/[8oo!9\u0011,!\u001f\u0005\u0002\u0005-ECAA<\u0011)\t9'!\u001f\u0002\u0002\u0013\u0015\u0013\u0011\u000e\u0005\u000b\u0003#\u000bI(!A\u0005\u0002\u0006M\u0015!B1qa2LHcD.\u0002\u0016\u0006]\u0015\u0011TAN\u0003;\u000by*!)\t\ru\ty\t1\u0001 \u0011\u0019I\u0013q\u0012a\u0001?!1Q&a$A\u0002=Ba\u0001OAH\u0001\u0004y\u0003B\u0002\u001f\u0002\u0010\u0002\u0007q\u0006\u0003\u0004A\u0003\u001f\u0003\ra\f\u0005\u0007\t\u0006=\u0005\u0019\u0001$\t\u0015\u0005\u0015\u0016\u0011PA\u0001\n\u0003\u000b9+A\u0004v]\u0006\u0004\b\u000f\\=\u0015\t\u0005%\u0016\u0011\u0017\t\u0005#A\nY\u000b\u0005\u0006\u0012\u0003[{rdL\u00180_\u0019K1!a,\u0013\u0005\u0019!V\u000f\u001d7fo!I\u00111WAR\u0003\u0003\u0005\raW\u0001\u0004q\u0012\u0002\u0004BCA\\\u0003s\n\t\u0011\"\u0003\u0002:\u0006Y!/Z1e%\u0016\u001cx\u000e\u001c<f)\t\tY\f\u0005\u0003\u0002\u001e\u0005u\u0016\u0002BA`\u0003?\u0011aa\u00142kK\u000e$\b"
)
public class ApplicationInfo implements Product, Serializable {
   private final String id;
   private final String name;
   private final Option<Object> coresGranted;
   private final Option<Object> maxCores;
   private final Option<Object> coresPerExecutor;
   private final Option<Object> memoryPerExecutorMB;
   private final Seq<ApplicationAttemptInfo> attempts;

   public static Option<Tuple7<String, String, Option<Object>, Option<Object>, Option<Object>, Option<Object>, Seq<ApplicationAttemptInfo>>> unapply(ApplicationInfo var0) {
      return ApplicationInfo$.MODULE$.unapply(var0);
   }

   public static ApplicationInfo apply(String var0, String var1, Option<Object> var2, Option<Object> var3, Option<Object> var4, Option<Object> var5, Seq<ApplicationAttemptInfo> var6) {
      return ApplicationInfo$.MODULE$.apply(var0, var1, var2, var3, var4, var5, var6);
   }

   public static Function1<Tuple7<String, String, Option<Object>, Option<Object>, Option<Object>, Option<Object>, Seq<ApplicationAttemptInfo>>, ApplicationInfo> tupled() {
      return ApplicationInfo$.MODULE$.tupled();
   }

   public static Function1<String, Function1<String, Function1<Option<Object>, Function1<Option<Object>, Function1<Option<Object>, Function1<Option<Object>, Function1<Seq<ApplicationAttemptInfo>, ApplicationInfo>>>>>>> curried() {
      return ApplicationInfo$.MODULE$.curried();
   }

   public String id() {
      return this.id;
   }

   public String name() {
      return this.name;
   }

   public Option<Object> coresGranted() {
      return this.coresGranted;
   }

   public Option<Object> maxCores() {
      return this.maxCores;
   }

   public Option<Object> coresPerExecutor() {
      return this.coresPerExecutor;
   }

   public Option<Object> memoryPerExecutorMB() {
      return this.memoryPerExecutorMB;
   }

   public Seq<ApplicationAttemptInfo> attempts() {
      return this.attempts;
   }

   public ApplicationInfo copy(String id, String name, Option<Object> coresGranted, Option<Object> maxCores, Option<Object> coresPerExecutor, Option<Object> memoryPerExecutorMB, Seq<ApplicationAttemptInfo> attempts) {
      return new ApplicationInfo(id, name, coresGranted, maxCores, coresPerExecutor, memoryPerExecutorMB, attempts);
   }

   public String copy$default$1() {
      return this.id();
   }

   public String copy$default$2() {
      return this.name();
   }

   public Option<Object> copy$default$3() {
      return this.coresGranted();
   }

   public Option<Object> copy$default$4() {
      return this.maxCores();
   }

   public Option<Object> copy$default$5() {
      return this.coresPerExecutor();
   }

   public Option<Object> copy$default$6() {
      return this.memoryPerExecutorMB();
   }

   public Seq<ApplicationAttemptInfo> copy$default$7() {
      return this.attempts();
   }

   public String productPrefix() {
      return "ApplicationInfo";
   }

   public int productArity() {
      return 7;
   }

   public Object productElement(int x$1) {
      Object var10000;
      switch (x$1) {
         case 0:
            var10000 = this.id();
            break;
         case 1:
            var10000 = this.name();
            break;
         case 2:
            var10000 = this.coresGranted();
            break;
         case 3:
            var10000 = this.maxCores();
            break;
         case 4:
            var10000 = this.coresPerExecutor();
            break;
         case 5:
            var10000 = this.memoryPerExecutorMB();
            break;
         case 6:
            var10000 = this.attempts();
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
      return x$1 instanceof ApplicationInfo;
   }

   public int hashCode() {
      return ScalaRunTime$.MODULE$._hashCode(this);
   }

   public String toString() {
      return ScalaRunTime$.MODULE$._toString(this);
   }

   public boolean equals(Object x$1) {
      boolean var19;
      if (this != x$1) {
         label107: {
            boolean var3;
            if (x$1 instanceof ApplicationInfo) {
               var3 = true;
            } else {
               var3 = false;
            }

            if (var3) {
               label89: {
                  label98: {
                     ApplicationInfo var4 = (ApplicationInfo)x$1;
                     String var10000 = this.id();
                     String var5 = var4.id();
                     if (var10000 == null) {
                        if (var5 != null) {
                           break label98;
                        }
                     } else if (!var10000.equals(var5)) {
                        break label98;
                     }

                     var10000 = this.name();
                     String var6 = var4.name();
                     if (var10000 == null) {
                        if (var6 != null) {
                           break label98;
                        }
                     } else if (!var10000.equals(var6)) {
                        break label98;
                     }

                     Option var13 = this.coresGranted();
                     Option var7 = var4.coresGranted();
                     if (var13 == null) {
                        if (var7 != null) {
                           break label98;
                        }
                     } else if (!var13.equals(var7)) {
                        break label98;
                     }

                     var13 = this.maxCores();
                     Option var8 = var4.maxCores();
                     if (var13 == null) {
                        if (var8 != null) {
                           break label98;
                        }
                     } else if (!var13.equals(var8)) {
                        break label98;
                     }

                     var13 = this.coresPerExecutor();
                     Option var9 = var4.coresPerExecutor();
                     if (var13 == null) {
                        if (var9 != null) {
                           break label98;
                        }
                     } else if (!var13.equals(var9)) {
                        break label98;
                     }

                     var13 = this.memoryPerExecutorMB();
                     Option var10 = var4.memoryPerExecutorMB();
                     if (var13 == null) {
                        if (var10 != null) {
                           break label98;
                        }
                     } else if (!var13.equals(var10)) {
                        break label98;
                     }

                     Seq var17 = this.attempts();
                     Seq var11 = var4.attempts();
                     if (var17 == null) {
                        if (var11 != null) {
                           break label98;
                        }
                     } else if (!var17.equals(var11)) {
                        break label98;
                     }

                     if (var4.canEqual(this)) {
                        var19 = true;
                        break label89;
                     }
                  }

                  var19 = false;
               }

               if (var19) {
                  break label107;
               }
            }

            var19 = false;
            return var19;
         }
      }

      var19 = true;
      return var19;
   }

   public ApplicationInfo(String id, String name, Option<Object> coresGranted, Option<Object> maxCores, Option<Object> coresPerExecutor, Option<Object> memoryPerExecutorMB, Seq<ApplicationAttemptInfo> attempts) {
      this.id = id;
      this.name = name;
      this.coresGranted = coresGranted;
      this.maxCores = maxCores;
      this.coresPerExecutor = coresPerExecutor;
      this.memoryPerExecutorMB = memoryPerExecutorMB;
      this.attempts = attempts;
      Product$class.$init$(this);
   }
}
