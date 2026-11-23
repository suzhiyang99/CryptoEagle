package org.apache.spark.scheduler;

import java.util.Properties;
import org.apache.spark.ShuffleDependency;
import org.apache.spark.util.CallSite;
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
   bytes = "\u0006\u0001\t\u0005b!B\u0001\u0003\u0001\nQ!!E'baN#\u0018mZ3Tk\nl\u0017\u000e\u001e;fI*\u00111\u0001B\u0001\ng\u000eDW\rZ;mKJT!!\u0002\u0004\u0002\u000bM\u0004\u0018M]6\u000b\u0005\u001dA\u0011AB1qC\u000eDWMC\u0001\n\u0003\ry'oZ\n\u0006\u0001-\tR\u0003\u0007\t\u0003\u0019=i\u0011!\u0004\u0006\u0002\u001d\u0005)1oY1mC&\u0011\u0001#\u0004\u0002\u0007\u0003:L(+\u001a4\u0011\u0005I\u0019R\"\u0001\u0002\n\u0005Q\u0011!!\u0005#B\u000fN\u001b\u0007.\u001a3vY\u0016\u0014XI^3oiB\u0011ABF\u0005\u0003/5\u0011q\u0001\u0015:pIV\u001cG\u000f\u0005\u0002\r3%\u0011!$\u0004\u0002\r'\u0016\u0014\u0018.\u00197ju\u0006\u0014G.\u001a\u0005\t9\u0001\u0011)\u001a!C\u0001=\u0005)!n\u001c2JI\u000e\u0001Q#A\u0010\u0011\u00051\u0001\u0013BA\u0011\u000e\u0005\rIe\u000e\u001e\u0005\tG\u0001\u0011\t\u0012)A\u0005?\u00051!n\u001c2JI\u0002B\u0001\"\n\u0001\u0003\u0016\u0004%\tAJ\u0001\u000bI\u0016\u0004XM\u001c3f]\u000eLX#A\u00141\t!rCI\u0012\t\u0006S)b3)R\u0007\u0002\t%\u00111\u0006\u0002\u0002\u0012'\",hM\u001a7f\t\u0016\u0004XM\u001c3f]\u000eL\bCA\u0017/\u0019\u0001!\u0011b\f\u0019\u0002\u0002\u0003\u0005)\u0011\u0001\u001c\u0003\u0007}#C\u0007\u0003\u00052\u0001\tE\t\u0015!\u00033\u0003-!W\r]3oI\u0016t7-\u001f\u00111\tM*d(\u0011\t\u0006S)\"T\b\u0011\t\u0003[U\"\u0011b\f\u0019\u0002\u0002\u0003\u0005)\u0011\u0001\u001c\u0012\u0005]R\u0004C\u0001\u00079\u0013\tITBA\u0004O_RD\u0017N\\4\u0011\u00051Y\u0014B\u0001\u001f\u000e\u0005\r\te.\u001f\t\u0003[y\"\u0011b\u0010\u0019\u0002\u0002\u0003\u0005)\u0011\u0001\u001c\u0003\u0007}#S\u0007\u0005\u0002.\u0003\u0012I!\tMA\u0001\u0002\u0003\u0015\tA\u000e\u0002\u0004?\u00122\u0004CA\u0017E\t%y\u0004'!A\u0001\u0002\u000b\u0005a\u0007\u0005\u0002.\r\u0012I!\tMA\u0001\u0002\u0003\u0015\tA\u000e\u0005\t\u0011\u0002\u0011)\u001a!C\u0001\u0013\u0006A1-\u00197m'&$X-F\u0001K!\tYe*D\u0001M\u0015\tiE!\u0001\u0003vi&d\u0017BA(M\u0005!\u0019\u0015\r\u001c7TSR,\u0007\u0002C)\u0001\u0005#\u0005\u000b\u0011\u0002&\u0002\u0013\r\fG\u000e\\*ji\u0016\u0004\u0003\u0002C*\u0001\u0005+\u0007I\u0011\u0001+\u0002\u00111L7\u000f^3oKJ,\u0012!\u0016\t\u0003%YK!a\u0016\u0002\u0003\u0017){'\rT5ti\u0016tWM\u001d\u0005\t3\u0002\u0011\t\u0012)A\u0005+\u0006IA.[:uK:,'\u000f\t\u0005\t7\u0002\u0011)\u001a!C\u00019\u0006Q\u0001O]8qKJ$\u0018.Z:\u0016\u0003u\u0003\"A\u00182\u000e\u0003}S!!\u00141\u000b\u0003\u0005\fAA[1wC&\u00111m\u0018\u0002\u000b!J|\u0007/\u001a:uS\u0016\u001c\b\u0002C3\u0001\u0005#\u0005\u000b\u0011B/\u0002\u0017A\u0014x\u000e]3si&,7\u000f\t\u0005\u0006O\u0002!\t\u0001[\u0001\u0007y%t\u0017\u000e\u001e \u0015\r%T7\u000e^;w!\t\u0011\u0002\u0001C\u0003\u001dM\u0002\u0007q\u0004C\u0003&M\u0002\u0007A\u000e\r\u0003n_F\u001c\b#B\u0015+]B\u0014\bCA\u0017p\t%y3.!A\u0001\u0002\u000b\u0005a\u0007\u0005\u0002.c\u0012Iqh[A\u0001\u0002\u0003\u0015\tA\u000e\t\u0003[M$\u0011BQ6\u0002\u0002\u0003\u0005)\u0011\u0001\u001c\t\u000b!3\u0007\u0019\u0001&\t\u000bM3\u0007\u0019A+\t\u000fm3\u0007\u0013!a\u0001;\"9\u0001\u0010AA\u0001\n\u0003I\u0018\u0001B2paf$b!\u001b>|yvt\bb\u0002\u000fx!\u0003\u0005\ra\b\u0005\bK]\u0004\n\u00111\u0001m\u0011\u001dAu\u000f%AA\u0002)CqaU<\u0011\u0002\u0003\u0007Q\u000bC\u0004\\oB\u0005\t\u0019A/\t\u0013\u0005\u0005\u0001!%A\u0005\u0002\u0005\r\u0011AD2paf$C-\u001a4bk2$H%M\u000b\u0003\u0003\u000bQ3aHA\u0004W\t\tI\u0001\u0005\u0003\u0002\f\u0005UQBAA\u0007\u0015\u0011\ty!!\u0005\u0002\u0013Ut7\r[3dW\u0016$'bAA\n\u001b\u0005Q\u0011M\u001c8pi\u0006$\u0018n\u001c8\n\t\u0005]\u0011Q\u0002\u0002\u0012k:\u001c\u0007.Z2lK\u00124\u0016M]5b]\u000e,\u0007\"CA\u000e\u0001E\u0005I\u0011AA\u000f\u00039\u0019w\u000e]=%I\u00164\u0017-\u001e7uII*\"!a\b1\u0011\u0005\u0005\u0012QEA\u0015\u0003[\u0001\u0002\"\u000b\u0016\u0002$\u0005\u001d\u00121\u0006\t\u0004[\u0005\u0015BAC\u0018\u0002\u001a\u0005\u0005\t\u0011!B\u0001mA\u0019Q&!\u000b\u0005\u0015}\nI\"!A\u0001\u0002\u000b\u0005a\u0007E\u0002.\u0003[!!BQA\r\u0003\u0003\u0005\tQ!\u00017\u0011%\t\t\u0004AI\u0001\n\u0003\t\u0019$\u0001\bd_BLH\u0005Z3gCVdG\u000fJ\u001a\u0016\u0005\u0005U\"f\u0001&\u0002\b!I\u0011\u0011\b\u0001\u0012\u0002\u0013\u0005\u00111H\u0001\u000fG>\u0004\u0018\u0010\n3fM\u0006,H\u000e\u001e\u00135+\t\tiDK\u0002V\u0003\u000fA\u0011\"!\u0011\u0001#\u0003%\t!a\u0011\u0002\u001d\r|\u0007/\u001f\u0013eK\u001a\fW\u000f\u001c;%kU\u0011\u0011Q\t\u0016\u0004;\u0006\u001d\u0001\"CA%\u0001\u0005\u0005I\u0011IA&\u00035\u0001(o\u001c3vGR\u0004&/\u001a4jqV\u0011\u0011Q\n\t\u0005\u0003\u001f\n)&\u0004\u0002\u0002R)\u0019\u00111\u000b1\u0002\t1\fgnZ\u0005\u0005\u0003/\n\tF\u0001\u0004TiJLgn\u001a\u0005\t\u00037\u0002\u0011\u0011!C\u0001=\u0005a\u0001O]8ek\u000e$\u0018I]5us\"I\u0011q\f\u0001\u0002\u0002\u0013\u0005\u0011\u0011M\u0001\u000faJ|G-^2u\u000b2,W.\u001a8u)\rQ\u00141\r\u0005\n\u0003K\ni&!AA\u0002}\t1\u0001\u001f\u00132\u0011%\tI\u0007AA\u0001\n\u0003\nY'A\bqe>$Wo\u0019;Ji\u0016\u0014\u0018\r^8s+\t\ti\u0007E\u0003\u0002p\u0005U$(\u0004\u0002\u0002r)\u0019\u00111O\u0007\u0002\u0015\r|G\u000e\\3di&|g.\u0003\u0003\u0002x\u0005E$\u0001C%uKJ\fGo\u001c:\t\u0013\u0005m\u0004!!A\u0005\u0002\u0005u\u0014\u0001C2b]\u0016\u000bX/\u00197\u0015\t\u0005}\u0014Q\u0011\t\u0004\u0019\u0005\u0005\u0015bAAB\u001b\t9!i\\8mK\u0006t\u0007\"CA3\u0003s\n\t\u00111\u0001;\u0011%\tI\tAA\u0001\n\u0003\nY)\u0001\u0005iCND7i\u001c3f)\u0005y\u0002\"CAH\u0001\u0005\u0005I\u0011IAI\u0003!!xn\u0015;sS:<GCAA'\u0011%\t)\nAA\u0001\n\u0003\n9*\u0001\u0004fcV\fGn\u001d\u000b\u0005\u0003\u007f\nI\nC\u0005\u0002f\u0005M\u0015\u0011!a\u0001u\u001dQ\u0011Q\u0014\u0002\u0002\u0002#\u0005!!a(\u0002#5\u000b\u0007o\u0015;bO\u0016\u001cVOY7jiR,G\rE\u0002\u0013\u0003C3\u0011\"\u0001\u0002\u0002\u0002#\u0005!!a)\u0014\u000b\u0005\u0005\u0016Q\u0015\r\u0011\u0017\u0005\u001d\u0016QV\u0010\u00022*+V,[\u0007\u0003\u0003SS1!a+\u000e\u0003\u001d\u0011XO\u001c;j[\u0016LA!a,\u0002*\n\t\u0012IY:ue\u0006\u001cGOR;oGRLwN\\\u001b1\u0011\u0005M\u0016qWA^\u0003\u007f\u0003\u0002\"\u000b\u0016\u00026\u0006e\u0016Q\u0018\t\u0004[\u0005]FAC\u0018\u0002\"\u0006\u0005\t\u0011!B\u0001mA\u0019Q&a/\u0005\u0015}\n\t+!A\u0001\u0002\u000b\u0005a\u0007E\u0002.\u0003\u007f#!BQAQ\u0003\u0003\u0005\tQ!\u00017\u0011\u001d9\u0017\u0011\u0015C\u0001\u0003\u0007$\"!a(\t\u0015\u0005=\u0015\u0011UA\u0001\n\u000b\n\t\n\u0003\u0006\u0002J\u0006\u0005\u0016\u0011!CA\u0003\u0017\fQ!\u00199qYf$2\"[Ag\u0003\u001f\f\t/a9\u0002f\"1A$a2A\u0002}Aq!JAd\u0001\u0004\t\t\u000e\r\u0005\u0002T\u0006]\u00171\\Ap!!I#&!6\u0002Z\u0006u\u0007cA\u0017\u0002X\u0012Qq&a4\u0002\u0002\u0003\u0005)\u0011\u0001\u001c\u0011\u00075\nY\u000e\u0002\u0006@\u0003\u001f\f\t\u0011!A\u0003\u0002Y\u00022!LAp\t)\u0011\u0015qZA\u0001\u0002\u0003\u0015\tA\u000e\u0005\u0007\u0011\u0006\u001d\u0007\u0019\u0001&\t\rM\u000b9\r1\u0001V\u0011!Y\u0016q\u0019I\u0001\u0002\u0004i\u0006BCAu\u0003C\u000b\t\u0011\"!\u0002l\u00069QO\\1qa2LH\u0003BAw\u0005\u0013\u0001\u0004\"a<\u0002\u0000\n\r!q\u0001\t\u0006\u0019\u0005E\u0018Q_\u0005\u0004\u0003gl!AB(qi&|g\u000eE\u0005\r\u0003o|\u00121 &V;&\u0019\u0011\u0011`\u0007\u0003\rQ+\b\u000f\\36!!I#&!@\u0003\u0002\t\u0015\u0001cA\u0017\u0002\u0000\u0012Qq&a:\u0002\u0002\u0003\u0005)\u0011\u0001\u001c\u0011\u00075\u0012\u0019\u0001\u0002\u0006@\u0003O\f\t\u0011!A\u0003\u0002Y\u00022!\fB\u0004\t)\u0011\u0015q]A\u0001\u0002\u0003\u0015\tA\u000e\u0005\n\u0005\u0017\t9/!AA\u0002%\f1\u0001\u001f\u00131\u0011)\u0011y!!)\u0012\u0002\u0013\u0005\u00111I\u0001\u0010CB\u0004H.\u001f\u0013eK\u001a\fW\u000f\u001c;%k!Q!1CAQ#\u0003%\t!a\u0011\u00027\u0011bWm]:j]&$He\u001a:fCR,'\u000f\n3fM\u0006,H\u000e\u001e\u00136\u0011)\u00119\"!)\u0002\u0002\u0013%!\u0011D\u0001\fe\u0016\fGMU3t_24X\r\u0006\u0002\u0003\u001cA!\u0011q\nB\u000f\u0013\u0011\u0011y\"!\u0015\u0003\r=\u0013'.Z2u\u0001"
)
public class MapStageSubmitted implements DAGSchedulerEvent, Product, Serializable {
   private final int jobId;
   private final ShuffleDependency<?, ?, ?> dependency;
   private final CallSite callSite;
   private final JobListener listener;
   private final Properties properties;

   public static Properties $lessinit$greater$default$5() {
      return MapStageSubmitted$.MODULE$.$lessinit$greater$default$5();
   }

   public static Properties apply$default$5() {
      return MapStageSubmitted$.MODULE$.apply$default$5();
   }

   public static Option<Tuple5<Object, ShuffleDependency<Object, Object, Object>, CallSite, JobListener, Properties>> unapply(MapStageSubmitted var0) {
      return MapStageSubmitted$.MODULE$.unapply(var0);
   }

   public static MapStageSubmitted apply(int var0, ShuffleDependency<?, ?, ?> var1, CallSite var2, JobListener var3, Properties var4) {
      return MapStageSubmitted$.MODULE$.apply(var0, var1, var2, var3, var4);
   }

   public static Function1<Tuple5<Object, ShuffleDependency<?, ?, ?>, CallSite, JobListener, Properties>, MapStageSubmitted> tupled() {
      return MapStageSubmitted$.MODULE$.tupled();
   }

   public static Function1<Object, Function1<ShuffleDependency<?, ?, ?>, Function1<CallSite, Function1<JobListener, Function1<Properties, MapStageSubmitted>>>>> curried() {
      return MapStageSubmitted$.MODULE$.curried();
   }

   public int jobId() {
      return this.jobId;
   }

   public ShuffleDependency<?, ?, ?> dependency() {
      return this.dependency;
   }

   public CallSite callSite() {
      return this.callSite;
   }

   public JobListener listener() {
      return this.listener;
   }

   public Properties properties() {
      return this.properties;
   }

   public MapStageSubmitted copy(int jobId, ShuffleDependency<?, ?, ?> dependency, CallSite callSite, JobListener listener, Properties properties) {
      return new MapStageSubmitted(jobId, dependency, callSite, listener, properties);
   }

   public int copy$default$1() {
      return this.jobId();
   }

   public ShuffleDependency<?, ?, ?> copy$default$2() {
      return this.dependency();
   }

   public CallSite copy$default$3() {
      return this.callSite();
   }

   public JobListener copy$default$4() {
      return this.listener();
   }

   public Properties copy$default$5() {
      return this.properties();
   }

   public String productPrefix() {
      return "MapStageSubmitted";
   }

   public int productArity() {
      return 5;
   }

   public Object productElement(int x$1) {
      Object var10000;
      switch (x$1) {
         case 0:
            var10000 = BoxesRunTime.boxToInteger(this.jobId());
            break;
         case 1:
            var10000 = this.dependency();
            break;
         case 2:
            var10000 = this.callSite();
            break;
         case 3:
            var10000 = this.listener();
            break;
         case 4:
            var10000 = this.properties();
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
      return x$1 instanceof MapStageSubmitted;
   }

   public int hashCode() {
      int var1 = -889275714;
      var1 = Statics.mix(var1, this.jobId());
      var1 = Statics.mix(var1, Statics.anyHash(this.dependency()));
      var1 = Statics.mix(var1, Statics.anyHash(this.callSite()));
      var1 = Statics.mix(var1, Statics.anyHash(this.listener()));
      var1 = Statics.mix(var1, Statics.anyHash(this.properties()));
      return Statics.finalizeHash(var1, 5);
   }

   public String toString() {
      return ScalaRunTime$.MODULE$._toString(this);
   }

   public boolean equals(Object x$1) {
      boolean var13;
      if (this != x$1) {
         label82: {
            boolean var3;
            if (x$1 instanceof MapStageSubmitted) {
               var3 = true;
            } else {
               var3 = false;
            }

            if (var3) {
               label64: {
                  MapStageSubmitted var4 = (MapStageSubmitted)x$1;
                  if (this.jobId() == var4.jobId()) {
                     label73: {
                        ShuffleDependency var10000 = this.dependency();
                        ShuffleDependency var5 = var4.dependency();
                        if (var10000 == null) {
                           if (var5 != null) {
                              break label73;
                           }
                        } else if (!var10000.equals(var5)) {
                           break label73;
                        }

                        CallSite var9 = this.callSite();
                        CallSite var6 = var4.callSite();
                        if (var9 == null) {
                           if (var6 != null) {
                              break label73;
                           }
                        } else if (!var9.equals(var6)) {
                           break label73;
                        }

                        JobListener var10 = this.listener();
                        JobListener var7 = var4.listener();
                        if (var10 == null) {
                           if (var7 != null) {
                              break label73;
                           }
                        } else if (!var10.equals(var7)) {
                           break label73;
                        }

                        Properties var11 = this.properties();
                        Properties var8 = var4.properties();
                        if (var11 == null) {
                           if (var8 != null) {
                              break label73;
                           }
                        } else if (!var11.equals(var8)) {
                           break label73;
                        }

                        if (var4.canEqual(this)) {
                           var13 = true;
                           break label64;
                        }
                     }
                  }

                  var13 = false;
               }

               if (var13) {
                  break label82;
               }
            }

            var13 = false;
            return var13;
         }
      }

      var13 = true;
      return var13;
   }

   public MapStageSubmitted(int jobId, ShuffleDependency<?, ?, ?> dependency, CallSite callSite, JobListener listener, Properties properties) {
      this.jobId = jobId;
      this.dependency = dependency;
      this.callSite = callSite;
      this.listener = listener;
      this.properties = properties;
      Product$class.$init$(this);
   }
}
