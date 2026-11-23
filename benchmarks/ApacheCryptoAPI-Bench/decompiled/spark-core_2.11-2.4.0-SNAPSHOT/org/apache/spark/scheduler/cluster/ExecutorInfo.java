package org.apache.spark.scheduler.cluster;

import org.apache.spark.annotation.DeveloperApi;
import scala.Predef$;
import scala.Serializable;
import scala.collection.Seq;
import scala.collection.Seq$;
import scala.collection.TraversableOnce;
import scala.collection.immutable.Map;
import scala.reflect.ScalaSignature;
import scala.runtime.BoxesRunTime;

@DeveloperApi
@ScalaSignature(
   bytes = "\u0006\u0001E3A!\u0001\u0002\u0001\u001b\taQ\t_3dkR|'/\u00138g_*\u00111\u0001B\u0001\bG2,8\u000f^3s\u0015\t)a!A\u0005tG\",G-\u001e7fe*\u0011q\u0001C\u0001\u0006gB\f'o\u001b\u0006\u0003\u0013)\ta!\u00199bG\",'\"A\u0006\u0002\u0007=\u0014xm\u0001\u0001\u0014\u0005\u0001q\u0001CA\b\u0013\u001b\u0005\u0001\"\"A\t\u0002\u000bM\u001c\u0017\r\\1\n\u0005M\u0001\"AB!osJ+g\r\u0003\u0005\u0016\u0001\t\u0015\r\u0011\"\u0001\u0017\u00031)\u00070Z2vi>\u0014\bj\\:u+\u00059\u0002C\u0001\r\u001c\u001d\ty\u0011$\u0003\u0002\u001b!\u00051\u0001K]3eK\u001aL!\u0001H\u000f\u0003\rM#(/\u001b8h\u0015\tQ\u0002\u0003\u0003\u0005 \u0001\t\u0005\t\u0015!\u0003\u0018\u00035)\u00070Z2vi>\u0014\bj\\:uA!A\u0011\u0005\u0001BC\u0002\u0013\u0005!%\u0001\u0006u_R\fGnQ8sKN,\u0012a\t\t\u0003\u001f\u0011J!!\n\t\u0003\u0007%sG\u000f\u0003\u0005(\u0001\t\u0005\t\u0015!\u0003$\u0003-!x\u000e^1m\u0007>\u0014Xm\u001d\u0011\t\u0011%\u0002!Q1A\u0005\u0002)\n\u0011\u0002\\8h+JdW*\u00199\u0016\u0003-\u0002B\u0001\u0007\u0017\u0018/%\u0011Q&\b\u0002\u0004\u001b\u0006\u0004\b\u0002C\u0018\u0001\u0005\u0003\u0005\u000b\u0011B\u0016\u0002\u00151|w-\u0016:m\u001b\u0006\u0004\b\u0005C\u00032\u0001\u0011\u0005!'\u0001\u0004=S:LGO\u0010\u000b\u0005gU2t\u0007\u0005\u00025\u00015\t!\u0001C\u0003\u0016a\u0001\u0007q\u0003C\u0003\"a\u0001\u00071\u0005C\u0003*a\u0001\u00071\u0006C\u0003:\u0001\u0011\u0005!(\u0001\u0005dC:,\u0015/^1m)\tYd\b\u0005\u0002\u0010y%\u0011Q\b\u0005\u0002\b\u0005>|G.Z1o\u0011\u0015y\u0004\b1\u0001A\u0003\u0015yG\u000f[3s!\ty\u0011)\u0003\u0002C!\t\u0019\u0011I\\=\t\u000b\u0011\u0003A\u0011I#\u0002\r\u0015\fX/\u00197t)\tYd\tC\u0003@\u0007\u0002\u0007\u0001\tC\u0003I\u0001\u0011\u0005\u0013*\u0001\u0005iCND7i\u001c3f)\u0005\u0019\u0003F\u0001\u0001L!\tau*D\u0001N\u0015\tqe!\u0001\u0006b]:|G/\u0019;j_:L!\u0001U'\u0003\u0019\u0011+g/\u001a7pa\u0016\u0014\u0018\t]5"
)
public class ExecutorInfo {
   private final String executorHost;
   private final int totalCores;
   private final Map<String, String> logUrlMap;

   public String executorHost() {
      return this.executorHost;
   }

   public int totalCores() {
      return this.totalCores;
   }

   public Map<String, String> logUrlMap() {
      return this.logUrlMap;
   }

   public boolean canEqual(Object other) {
      return other instanceof ExecutorInfo;
   }

   public boolean equals(Object other) {
      boolean var4;
      if (other instanceof ExecutorInfo) {
         boolean var8;
         label34: {
            label40: {
               ExecutorInfo var3 = (ExecutorInfo)other;
               if (var3.canEqual(this)) {
                  label39: {
                     String var10000 = this.executorHost();
                     String var5 = var3.executorHost();
                     if (var10000 == null) {
                        if (var5 != null) {
                           break label39;
                        }
                     } else if (!var10000.equals(var5)) {
                        break label39;
                     }

                     if (this.totalCores() == var3.totalCores()) {
                        Map var7 = this.logUrlMap();
                        Map var6 = var3.logUrlMap();
                        if (var7 == null) {
                           if (var6 == null) {
                              break label40;
                           }
                        } else if (var7.equals(var6)) {
                           break label40;
                        }
                     }
                  }
               }

               var8 = false;
               break label34;
            }

            var8 = true;
         }

         var4 = var8;
      } else {
         var4 = false;
      }

      return var4;
   }

   public int hashCode() {
      Seq state = (Seq)Seq$.MODULE$.apply(Predef$.MODULE$.genericWrapArray(new Object[]{this.executorHost(), BoxesRunTime.boxToInteger(this.totalCores()), this.logUrlMap()}));
      return BoxesRunTime.unboxToInt(((TraversableOnce)state.map(new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final int apply(Object x$1) {
            return x$1.hashCode();
         }
      }, Seq$.MODULE$.canBuildFrom())).foldLeft(BoxesRunTime.boxToInteger(0), new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final int apply(int a, int b) {
            return this.apply$mcIII$sp(a, b);
         }

         public int apply$mcIII$sp(int a, int b) {
            return 31 * a + b;
         }
      }));
   }

   public ExecutorInfo(String executorHost, int totalCores, Map<String, String> logUrlMap) {
      this.executorHost = executorHost;
      this.totalCores = totalCores;
      this.logUrlMap = logUrlMap;
   }
}
