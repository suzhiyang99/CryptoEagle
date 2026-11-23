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
   bytes = "\u0006\u0001\u00055f!B\u0001\u0003\u0001\na!a\u0002'pO&sgm\u001c\u0006\u0003\u0007\u0011\tq\u0001[5ti>\u0014\u0018P\u0003\u0002\u0006\r\u00051A-\u001a9m_fT!a\u0002\u0005\u0002\u000bM\u0004\u0018M]6\u000b\u0005%Q\u0011AB1qC\u000eDWMC\u0001\f\u0003\ry'oZ\n\u0005\u00015\u0019b\u0003\u0005\u0002\u000f#5\tqBC\u0001\u0011\u0003\u0015\u00198-\u00197b\u0013\t\u0011rB\u0001\u0004B]f\u0014VM\u001a\t\u0003\u001dQI!!F\b\u0003\u000fA\u0013x\u000eZ;diB\u0011abF\u0005\u00031=\u0011AbU3sS\u0006d\u0017N_1cY\u0016D\u0001B\u0007\u0001\u0003\u0016\u0004%\t\u0001H\u0001\bY><\u0007+\u0019;i\u0007\u0001)\u0012!\b\t\u0003=\u0005r!AD\u0010\n\u0005\u0001z\u0011A\u0002)sK\u0012,g-\u0003\u0002#G\t11\u000b\u001e:j]\u001eT!\u0001I\b)\u0005e)#F\u0001\u0014/!\t9C&D\u0001)\u0015\tI#&A\u0004lmN$xN]3\u000b\u0005-2\u0011\u0001B;uS2L!!\f\u0015\u0003\u000f-3\u0016J\u001c3fq.\nq\u0006\u0005\u00021k5\t\u0011G\u0003\u00023g\u0005!Q.\u001a;b\u0015\t!t\"\u0001\u0006b]:|G/\u0019;j_:L!AN\u0019\u0003\r\u001d,G\u000f^3s\u0011!A\u0004A!E!\u0002\u0013i\u0012\u0001\u00037pOB\u000bG\u000f\u001b\u0011\t\u0011i\u0002!Q3A\u0005\u0002m\nQ\u0002\\1tiB\u0013xnY3tg\u0016$W#\u0001\u001f\u0011\u00059i\u0014B\u0001 \u0010\u0005\u0011auN\\4)\te*\u0003)Q\u0001\u0006m\u0006dW/Z\u0011\u0002u!A1\t\u0001B\tB\u0003%A(\u0001\bmCN$\bK]8dKN\u001cX\r\u001a\u0011\t\u0011\u0015\u0003!Q3A\u0005\u0002\u0019\u000bQ!\u00199q\u0013\u0012,\u0012a\u0012\t\u0004\u001d!k\u0012BA%\u0010\u0005\u0019y\u0005\u000f^5p]\"A1\n\u0001B\tB\u0003%q)\u0001\u0004baBLE\r\t\u0005\t\u001b\u0002\u0011)\u001a!C\u0001\r\u0006I\u0011\r\u001e;f[B$\u0018\n\u001a\u0005\t\u001f\u0002\u0011\t\u0012)A\u0005\u000f\u0006Q\u0011\r\u001e;f[B$\u0018\n\u001a\u0011\t\u0011E\u0003!Q3A\u0005\u0002m\n\u0001BZ5mKNK'0\u001a\u0005\t'\u0002\u0011\t\u0012)A\u0005y\u0005Ia-\u001b7f'&TX\r\t\u0005\u0006+\u0002!\tAV\u0001\u0007y%t\u0017\u000e\u001e \u0015\r]K&l\u0017/^!\tA\u0006!D\u0001\u0003\u0011\u0015QB\u000b1\u0001\u001e\u0011\u0015QD\u000b1\u0001=\u0011\u0015)E\u000b1\u0001H\u0011\u0015iE\u000b1\u0001H\u0011\u0015\tF\u000b1\u0001=\u0011\u001dy\u0006!!A\u0005\u0002\u0001\fAaY8qsR1q+\u00192dI\u0016DqA\u00070\u0011\u0002\u0003\u0007Q\u0004C\u0004;=B\u0005\t\u0019\u0001\u001f\t\u000f\u0015s\u0006\u0013!a\u0001\u000f\"9QJ\u0018I\u0001\u0002\u00049\u0005bB)_!\u0003\u0005\r\u0001\u0010\u0005\bO\u0002\t\n\u0011\"\u0001i\u00039\u0019w\u000e]=%I\u00164\u0017-\u001e7uIE*\u0012!\u001b\u0016\u0003;)\\\u0013a\u001b\t\u0003Y>l\u0011!\u001c\u0006\u0003]N\n\u0011\"\u001e8dQ\u0016\u001c7.\u001a3\n\u0005Al'!E;oG\",7m[3e-\u0006\u0014\u0018.\u00198dK\"9!\u000fAI\u0001\n\u0003\u0019\u0018AD2paf$C-\u001a4bk2$HEM\u000b\u0002i*\u0012AH\u001b\u0005\bm\u0002\t\n\u0011\"\u0001x\u00039\u0019w\u000e]=%I\u00164\u0017-\u001e7uIM*\u0012\u0001\u001f\u0016\u0003\u000f*DqA\u001f\u0001\u0012\u0002\u0013\u0005q/\u0001\bd_BLH\u0005Z3gCVdG\u000f\n\u001b\t\u000fq\u0004\u0011\u0013!C\u0001g\u0006q1m\u001c9zI\u0011,g-Y;mi\u0012*\u0004b\u0002@\u0001\u0003\u0003%\te`\u0001\u000eaJ|G-^2u!J,g-\u001b=\u0016\u0005\u0005\u0005\u0001\u0003BA\u0002\u0003\u001bi!!!\u0002\u000b\t\u0005\u001d\u0011\u0011B\u0001\u0005Y\u0006twM\u0003\u0002\u0002\f\u0005!!.\u0019<b\u0013\r\u0011\u0013Q\u0001\u0005\n\u0003#\u0001\u0011\u0011!C\u0001\u0003'\tA\u0002\u001d:pIV\u001cG/\u0011:jif,\"!!\u0006\u0011\u00079\t9\"C\u0002\u0002\u001a=\u00111!\u00138u\u0011%\ti\u0002AA\u0001\n\u0003\ty\"\u0001\bqe>$Wo\u0019;FY\u0016lWM\u001c;\u0015\t\u0005\u0005\u0012q\u0005\t\u0004\u001d\u0005\r\u0012bAA\u0013\u001f\t\u0019\u0011I\\=\t\u0015\u0005%\u00121DA\u0001\u0002\u0004\t)\"A\u0002yIEB\u0011\"!\f\u0001\u0003\u0003%\t%a\f\u0002\u001fA\u0014x\u000eZ;di&#XM]1u_J,\"!!\r\u0011\r\u0005M\u0012\u0011HA\u0011\u001b\t\t)DC\u0002\u00028=\t!bY8mY\u0016\u001cG/[8o\u0013\u0011\tY$!\u000e\u0003\u0011%#XM]1u_JD\u0011\"a\u0010\u0001\u0003\u0003%\t!!\u0011\u0002\u0011\r\fg.R9vC2$B!a\u0011\u0002JA\u0019a\"!\u0012\n\u0007\u0005\u001dsBA\u0004C_>dW-\u00198\t\u0015\u0005%\u0012QHA\u0001\u0002\u0004\t\t\u0003C\u0005\u0002N\u0001\t\t\u0011\"\u0011\u0002P\u0005A\u0001.Y:i\u0007>$W\r\u0006\u0002\u0002\u0016!I\u00111\u000b\u0001\u0002\u0002\u0013\u0005\u0013QK\u0001\ti>\u001cFO]5oOR\u0011\u0011\u0011\u0001\u0005\n\u00033\u0002\u0011\u0011!C!\u00037\na!Z9vC2\u001cH\u0003BA\"\u0003;B!\"!\u000b\u0002X\u0005\u0005\t\u0019AA\u0011\u000f)\t\tGAA\u0001\u0012\u0003\u0011\u00111M\u0001\b\u0019><\u0017J\u001c4p!\rA\u0016Q\r\u0004\n\u0003\t\t\t\u0011#\u0001\u0003\u0003O\u001aR!!\u001a\u0002jY\u0001\"\"a\u001b\u0002ruati\u0012\u001fX\u001b\t\tiGC\u0002\u0002p=\tqA];oi&lW-\u0003\u0003\u0002t\u00055$!E!cgR\u0014\u0018m\u0019;Gk:\u001cG/[8ok!9Q+!\u001a\u0005\u0002\u0005]DCAA2\u0011)\t\u0019&!\u001a\u0002\u0002\u0013\u0015\u0013Q\u000b\u0005\u000b\u0003{\n)'!A\u0005\u0002\u0006}\u0014!B1qa2LHcC,\u0002\u0002\u0006\u0015\u0015\u0011RAF\u0003\u001bCaAGA>\u0001\u0004i\u0002fAAAK!1!(a\u001fA\u0002qBS!!\"&\u0001\u0006Ca!RA>\u0001\u00049\u0005BB'\u0002|\u0001\u0007q\t\u0003\u0004R\u0003w\u0002\r\u0001\u0010\u0005\u000b\u0003#\u000b)'!A\u0005\u0002\u0006M\u0015aB;oCB\u0004H.\u001f\u000b\u0005\u0003+\u000bi\n\u0005\u0003\u000f\u0011\u0006]\u0005\u0003\u0003\b\u0002\u001avati\u0012\u001f\n\u0007\u0005muB\u0001\u0004UkBdW-\u000e\u0005\n\u0003?\u000by)!AA\u0002]\u000b1\u0001\u001f\u00131\u0011)\t\u0019+!\u001a\u0002\u0002\u0013%\u0011QU\u0001\fe\u0016\fGMU3t_24X\r\u0006\u0002\u0002(B!\u00111AAU\u0013\u0011\tY+!\u0002\u0003\r=\u0013'.Z2u\u0001"
)
public class LogInfo implements Product, Serializable {
   private final String logPath;
   private final long lastProcessed;
   private final Option<String> appId;
   private final Option<String> attemptId;
   private final long fileSize;

   public static Option<Tuple5<String, Object, Option<String>, Option<String>, Object>> unapply(LogInfo var0) {
      return LogInfo$.MODULE$.unapply(var0);
   }

   public static LogInfo apply(@KVIndex String var0, @KVIndex("lastProcessed") long var1, Option<String> var3, Option<String> var4, long var5) {
      return LogInfo$.MODULE$.apply(var0, var1, var3, var4, var5);
   }

   public static Function1<Tuple5<String, Object, Option<String>, Option<String>, Object>, LogInfo> tupled() {
      return LogInfo$.MODULE$.tupled();
   }

   public static Function1<String, Function1<Object, Function1<Option<String>, Function1<Option<String>, Function1<Object, LogInfo>>>>> curried() {
      return LogInfo$.MODULE$.curried();
   }

   @KVIndex
   public String logPath() {
      return this.logPath;
   }

   @KVIndex("lastProcessed")
   public long lastProcessed() {
      return this.lastProcessed;
   }

   public Option<String> appId() {
      return this.appId;
   }

   public Option<String> attemptId() {
      return this.attemptId;
   }

   public long fileSize() {
      return this.fileSize;
   }

   public LogInfo copy(String logPath, long lastProcessed, Option<String> appId, Option<String> attemptId, long fileSize) {
      return new LogInfo(logPath, lastProcessed, appId, attemptId, fileSize);
   }

   public String copy$default$1() {
      return this.logPath();
   }

   public long copy$default$2() {
      return this.lastProcessed();
   }

   public Option<String> copy$default$3() {
      return this.appId();
   }

   public Option<String> copy$default$4() {
      return this.attemptId();
   }

   public long copy$default$5() {
      return this.fileSize();
   }

   public String productPrefix() {
      return "LogInfo";
   }

   public int productArity() {
      return 5;
   }

   public Object productElement(int x$1) {
      Object var10000;
      switch (x$1) {
         case 0:
            var10000 = this.logPath();
            break;
         case 1:
            var10000 = BoxesRunTime.boxToLong(this.lastProcessed());
            break;
         case 2:
            var10000 = this.appId();
            break;
         case 3:
            var10000 = this.attemptId();
            break;
         case 4:
            var10000 = BoxesRunTime.boxToLong(this.fileSize());
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
      return x$1 instanceof LogInfo;
   }

   public int hashCode() {
      int var1 = -889275714;
      var1 = Statics.mix(var1, Statics.anyHash(this.logPath()));
      var1 = Statics.mix(var1, Statics.longHash(this.lastProcessed()));
      var1 = Statics.mix(var1, Statics.anyHash(this.appId()));
      var1 = Statics.mix(var1, Statics.anyHash(this.attemptId()));
      var1 = Statics.mix(var1, Statics.longHash(this.fileSize()));
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
            if (x$1 instanceof LogInfo) {
               var3 = true;
            } else {
               var3 = false;
            }

            if (var3) {
               label57: {
                  label56: {
                     LogInfo var4 = (LogInfo)x$1;
                     String var10000 = this.logPath();
                     String var5 = var4.logPath();
                     if (var10000 == null) {
                        if (var5 != null) {
                           break label56;
                        }
                     } else if (!var10000.equals(var5)) {
                        break label56;
                     }

                     if (this.lastProcessed() == var4.lastProcessed()) {
                        label66: {
                           Option var8 = this.appId();
                           Option var6 = var4.appId();
                           if (var8 == null) {
                              if (var6 != null) {
                                 break label66;
                              }
                           } else if (!var8.equals(var6)) {
                              break label66;
                           }

                           var8 = this.attemptId();
                           Option var7 = var4.attemptId();
                           if (var8 == null) {
                              if (var7 != null) {
                                 break label66;
                              }
                           } else if (!var8.equals(var7)) {
                              break label66;
                           }

                           if (this.fileSize() == var4.fileSize() && var4.canEqual(this)) {
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

   public LogInfo(String logPath, long lastProcessed, Option<String> appId, Option<String> attemptId, long fileSize) {
      this.logPath = logPath;
      this.lastProcessed = lastProcessed;
      this.appId = appId;
      this.attemptId = attemptId;
      this.fileSize = fileSize;
      Product$class.$init$(this);
   }
}
