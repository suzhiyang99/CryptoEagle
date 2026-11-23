package org.apache.spark.scheduler;

import org.apache.spark.annotation.DeveloperApi;
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

@DeveloperApi
@ScalaSignature(
   bytes = "\u0006\u0001\u0005\rd\u0001B\u0001\u0003\u0001.\u0011Ad\u00159be.d\u0015n\u001d;f]\u0016\u0014X\t_3dkR|'OU3n_Z,GM\u0003\u0002\u0004\t\u0005I1o\u00195fIVdWM\u001d\u0006\u0003\u000b\u0019\tQa\u001d9be.T!a\u0002\u0005\u0002\r\u0005\u0004\u0018m\u00195f\u0015\u0005I\u0011aA8sO\u000e\u00011#\u0002\u0001\r%YI\u0002CA\u0007\u0011\u001b\u0005q!\"A\b\u0002\u000bM\u001c\u0017\r\\1\n\u0005Eq!AB!osJ+g\r\u0005\u0002\u0014)5\t!!\u0003\u0002\u0016\u0005\t\u00112\u000b]1sW2K7\u000f^3oKJ,e/\u001a8u!\tiq#\u0003\u0002\u0019\u001d\t9\u0001K]8ek\u000e$\bCA\u0007\u001b\u0013\tYbB\u0001\u0007TKJL\u0017\r\\5{C\ndW\r\u0003\u0005\u001e\u0001\tU\r\u0011\"\u0001\u001f\u0003\u0011!\u0018.\\3\u0016\u0003}\u0001\"!\u0004\u0011\n\u0005\u0005r!\u0001\u0002'p]\u001eD\u0001b\t\u0001\u0003\u0012\u0003\u0006IaH\u0001\u0006i&lW\r\t\u0005\tK\u0001\u0011)\u001a!C\u0001M\u0005QQ\r_3dkR|'/\u00133\u0016\u0003\u001d\u0002\"\u0001K\u0016\u000f\u00055I\u0013B\u0001\u0016\u000f\u0003\u0019\u0001&/\u001a3fM&\u0011A&\f\u0002\u0007'R\u0014\u0018N\\4\u000b\u0005)r\u0001\u0002C\u0018\u0001\u0005#\u0005\u000b\u0011B\u0014\u0002\u0017\u0015DXmY;u_JLE\r\t\u0005\tc\u0001\u0011)\u001a!C\u0001M\u00051!/Z1t_:D\u0001b\r\u0001\u0003\u0012\u0003\u0006IaJ\u0001\be\u0016\f7o\u001c8!\u0011\u0015)\u0004\u0001\"\u00017\u0003\u0019a\u0014N\\5u}Q!q\u0007O\u001d;!\t\u0019\u0002\u0001C\u0003\u001ei\u0001\u0007q\u0004C\u0003&i\u0001\u0007q\u0005C\u00032i\u0001\u0007q\u0005C\u0004=\u0001\u0005\u0005I\u0011A\u001f\u0002\t\r|\u0007/\u001f\u000b\u0005oyz\u0004\tC\u0004\u001ewA\u0005\t\u0019A\u0010\t\u000f\u0015Z\u0004\u0013!a\u0001O!9\u0011g\u000fI\u0001\u0002\u00049\u0003b\u0002\"\u0001#\u0003%\taQ\u0001\u000fG>\u0004\u0018\u0010\n3fM\u0006,H\u000e\u001e\u00132+\u0005!%FA\u0010FW\u00051\u0005CA$M\u001b\u0005A%BA%K\u0003%)hn\u00195fG.,GM\u0003\u0002L\u001d\u0005Q\u0011M\u001c8pi\u0006$\u0018n\u001c8\n\u00055C%!E;oG\",7m[3e-\u0006\u0014\u0018.\u00198dK\"9q\nAI\u0001\n\u0003\u0001\u0016AD2paf$C-\u001a4bk2$HEM\u000b\u0002#*\u0012q%\u0012\u0005\b'\u0002\t\n\u0011\"\u0001Q\u00039\u0019w\u000e]=%I\u00164\u0017-\u001e7uIMBq!\u0016\u0001\u0002\u0002\u0013\u0005c+A\u0007qe>$Wo\u0019;Qe\u00164\u0017\u000e_\u000b\u0002/B\u0011\u0001,X\u0007\u00023*\u0011!lW\u0001\u0005Y\u0006twMC\u0001]\u0003\u0011Q\u0017M^1\n\u00051J\u0006bB0\u0001\u0003\u0003%\t\u0001Y\u0001\raJ|G-^2u\u0003JLG/_\u000b\u0002CB\u0011QBY\u0005\u0003G:\u00111!\u00138u\u0011\u001d)\u0007!!A\u0005\u0002\u0019\fa\u0002\u001d:pIV\u001cG/\u00127f[\u0016tG\u000f\u0006\u0002hUB\u0011Q\u0002[\u0005\u0003S:\u00111!\u00118z\u0011\u001dYG-!AA\u0002\u0005\f1\u0001\u001f\u00132\u0011\u001di\u0007!!A\u0005B9\fq\u0002\u001d:pIV\u001cG/\u0013;fe\u0006$xN]\u000b\u0002_B\u0019\u0001o]4\u000e\u0003ET!A\u001d\b\u0002\u0015\r|G\u000e\\3di&|g.\u0003\u0002uc\nA\u0011\n^3sCR|'\u000fC\u0004w\u0001\u0005\u0005I\u0011A<\u0002\u0011\r\fg.R9vC2$\"\u0001_>\u0011\u00055I\u0018B\u0001>\u000f\u0005\u001d\u0011un\u001c7fC:Dqa[;\u0002\u0002\u0003\u0007q\rC\u0004~\u0001\u0005\u0005I\u0011\t@\u0002\u0011!\f7\u000f[\"pI\u0016$\u0012!\u0019\u0005\n\u0003\u0003\u0001\u0011\u0011!C!\u0003\u0007\t\u0001\u0002^8TiJLgn\u001a\u000b\u0002/\"I\u0011q\u0001\u0001\u0002\u0002\u0013\u0005\u0013\u0011B\u0001\u0007KF,\u0018\r\\:\u0015\u0007a\fY\u0001\u0003\u0005l\u0003\u000b\t\t\u00111\u0001hQ\r\u0001\u0011q\u0002\t\u0005\u0003#\t)\"\u0004\u0002\u0002\u0014)\u00111\nB\u0005\u0005\u0003/\t\u0019B\u0001\u0007EKZ,Gn\u001c9fe\u0006\u0003\u0018nB\u0005\u0002\u001c\t\t\t\u0011#\u0001\u0002\u001e\u0005a2\u000b]1sW2K7\u000f^3oKJ,\u00050Z2vi>\u0014(+Z7pm\u0016$\u0007cA\n\u0002 \u0019A\u0011AAA\u0001\u0012\u0003\t\tcE\u0003\u0002 \u0005\r\u0012\u0004\u0005\u0005\u0002&\u0005-rdJ\u00148\u001b\t\t9CC\u0002\u0002*9\tqA];oi&lW-\u0003\u0003\u0002.\u0005\u001d\"!E!cgR\u0014\u0018m\u0019;Gk:\u001cG/[8og!9Q'a\b\u0005\u0002\u0005EBCAA\u000f\u0011)\t\t!a\b\u0002\u0002\u0013\u0015\u00131\u0001\u0005\u000b\u0003o\ty\"!A\u0005\u0002\u0006e\u0012!B1qa2LHcB\u001c\u0002<\u0005u\u0012q\b\u0005\u0007;\u0005U\u0002\u0019A\u0010\t\r\u0015\n)\u00041\u0001(\u0011\u0019\t\u0014Q\u0007a\u0001O!Q\u00111IA\u0010\u0003\u0003%\t)!\u0012\u0002\u000fUt\u0017\r\u001d9msR!\u0011qIA*!\u0015i\u0011\u0011JA'\u0013\r\tYE\u0004\u0002\u0007\u001fB$\u0018n\u001c8\u0011\r5\tyeH\u0014(\u0013\r\t\tF\u0004\u0002\u0007)V\u0004H.Z\u001a\t\u0013\u0005U\u0013\u0011IA\u0001\u0002\u00049\u0014a\u0001=%a!Q\u0011\u0011LA\u0010\u0003\u0003%I!a\u0017\u0002\u0017I,\u0017\r\u001a*fg>dg/\u001a\u000b\u0003\u0003;\u00022\u0001WA0\u0013\r\t\t'\u0017\u0002\u0007\u001f\nTWm\u0019;"
)
public class SparkListenerExecutorRemoved implements SparkListenerEvent, Product, Serializable {
   private final long time;
   private final String executorId;
   private final String reason;

   public static Option<Tuple3<Object, String, String>> unapply(SparkListenerExecutorRemoved var0) {
      return SparkListenerExecutorRemoved$.MODULE$.unapply(var0);
   }

   public static SparkListenerExecutorRemoved apply(long var0, String var2, String var3) {
      return SparkListenerExecutorRemoved$.MODULE$.apply(var0, var2, var3);
   }

   public static Function1<Tuple3<Object, String, String>, SparkListenerExecutorRemoved> tupled() {
      return SparkListenerExecutorRemoved$.MODULE$.tupled();
   }

   public static Function1<Object, Function1<String, Function1<String, SparkListenerExecutorRemoved>>> curried() {
      return SparkListenerExecutorRemoved$.MODULE$.curried();
   }

   public boolean logEvent() {
      return SparkListenerEvent$class.logEvent(this);
   }

   public long time() {
      return this.time;
   }

   public String executorId() {
      return this.executorId;
   }

   public String reason() {
      return this.reason;
   }

   public SparkListenerExecutorRemoved copy(long time, String executorId, String reason) {
      return new SparkListenerExecutorRemoved(time, executorId, reason);
   }

   public long copy$default$1() {
      return this.time();
   }

   public String copy$default$2() {
      return this.executorId();
   }

   public String copy$default$3() {
      return this.reason();
   }

   public String productPrefix() {
      return "SparkListenerExecutorRemoved";
   }

   public int productArity() {
      return 3;
   }

   public Object productElement(int x$1) {
      Object var10000;
      switch (x$1) {
         case 0:
            var10000 = BoxesRunTime.boxToLong(this.time());
            break;
         case 1:
            var10000 = this.executorId();
            break;
         case 2:
            var10000 = this.reason();
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
      return x$1 instanceof SparkListenerExecutorRemoved;
   }

   public int hashCode() {
      int var1 = -889275714;
      var1 = Statics.mix(var1, Statics.longHash(this.time()));
      var1 = Statics.mix(var1, Statics.anyHash(this.executorId()));
      var1 = Statics.mix(var1, Statics.anyHash(this.reason()));
      return Statics.finalizeHash(var1, 3);
   }

   public String toString() {
      return ScalaRunTime$.MODULE$._toString(this);
   }

   public boolean equals(Object x$1) {
      boolean var9;
      if (this != x$1) {
         label64: {
            boolean var3;
            if (x$1 instanceof SparkListenerExecutorRemoved) {
               var3 = true;
            } else {
               var3 = false;
            }

            if (var3) {
               label46: {
                  SparkListenerExecutorRemoved var4 = (SparkListenerExecutorRemoved)x$1;
                  if (this.time() == var4.time()) {
                     label55: {
                        String var10000 = this.executorId();
                        String var5 = var4.executorId();
                        if (var10000 == null) {
                           if (var5 != null) {
                              break label55;
                           }
                        } else if (!var10000.equals(var5)) {
                           break label55;
                        }

                        var10000 = this.reason();
                        String var6 = var4.reason();
                        if (var10000 == null) {
                           if (var6 != null) {
                              break label55;
                           }
                        } else if (!var10000.equals(var6)) {
                           break label55;
                        }

                        if (var4.canEqual(this)) {
                           var9 = true;
                           break label46;
                        }
                     }
                  }

                  var9 = false;
               }

               if (var9) {
                  break label64;
               }
            }

            var9 = false;
            return var9;
         }
      }

      var9 = true;
      return var9;
   }

   public SparkListenerExecutorRemoved(long time, String executorId, String reason) {
      this.time = time;
      this.executorId = executorId;
      this.reason = reason;
      SparkListenerEvent$class.$init$(this);
      Product$class.$init$(this);
   }
}
