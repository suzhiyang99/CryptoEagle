package org.apache.spark.deploy;

import scala.Function1;
import scala.Option;
import scala.Product;
import scala.Product$class;
import scala.Serializable;
import scala.Tuple6;
import scala.collection.Iterator;
import scala.collection.Map;
import scala.collection.Seq;
import scala.reflect.ScalaSignature;
import scala.runtime.BoxesRunTime;
import scala.runtime.ScalaRunTime$;

@ScalaSignature(
   bytes = "\u0006\u0001\u0005\u0015f!B\u0001\u0003\u0001\u0012Q!aB\"p[6\fg\u000e\u001a\u0006\u0003\u0007\u0011\ta\u0001Z3qY>L(BA\u0003\u0007\u0003\u0015\u0019\b/\u0019:l\u0015\t9\u0001\"\u0001\u0004ba\u0006\u001c\u0007.\u001a\u0006\u0002\u0013\u0005\u0019qN]4\u0014\t\u0001Y\u0011\u0003\u0006\t\u0003\u0019=i\u0011!\u0004\u0006\u0002\u001d\u0005)1oY1mC&\u0011\u0001#\u0004\u0002\u0007\u0003:L(+\u001a4\u0011\u00051\u0011\u0012BA\n\u000e\u0005\u001d\u0001&o\u001c3vGR\u0004\"\u0001D\u000b\n\u0005Yi!\u0001D*fe&\fG.\u001b>bE2,\u0007\u0002\u0003\r\u0001\u0005+\u0007I\u0011\u0001\u000e\u0002\u00135\f\u0017N\\\"mCN\u001c8\u0001A\u000b\u00027A\u0011Ad\b\b\u0003\u0019uI!AH\u0007\u0002\rA\u0013X\rZ3g\u0013\t\u0001\u0013E\u0001\u0004TiJLgn\u001a\u0006\u0003=5A\u0001b\t\u0001\u0003\u0012\u0003\u0006IaG\u0001\u000b[\u0006Lgn\u00117bgN\u0004\u0003\u0002C\u0013\u0001\u0005+\u0007I\u0011\u0001\u0014\u0002\u0013\u0005\u0014x-^7f]R\u001cX#A\u0014\u0011\u0007!\u00024D\u0004\u0002*]9\u0011!&L\u0007\u0002W)\u0011A&G\u0001\u0007yI|w\u000e\u001e \n\u00039I!aL\u0007\u0002\u000fA\f7m[1hK&\u0011\u0011G\r\u0002\u0004'\u0016\f(BA\u0018\u000e\u0011!!\u0004A!E!\u0002\u00139\u0013AC1sOVlWM\u001c;tA!Aa\u0007\u0001BK\u0002\u0013\u0005q'A\u0006f]ZL'o\u001c8nK:$X#\u0001\u001d\u0011\teb4dG\u0007\u0002u)\u00111(D\u0001\u000bG>dG.Z2uS>t\u0017BA\u001f;\u0005\ri\u0015\r\u001d\u0005\t\u007f\u0001\u0011\t\u0012)A\u0005q\u0005aQM\u001c<je>tW.\u001a8uA!A\u0011\t\u0001BK\u0002\u0013\u0005a%\u0001\tdY\u0006\u001c8\u000fU1uQ\u0016sGO]5fg\"A1\t\u0001B\tB\u0003%q%A\tdY\u0006\u001c8\u000fU1uQ\u0016sGO]5fg\u0002B\u0001\"\u0012\u0001\u0003\u0016\u0004%\tAJ\u0001\u0013Y&\u0014'/\u0019:z!\u0006$\b.\u00128ue&,7\u000f\u0003\u0005H\u0001\tE\t\u0015!\u0003(\u0003Ma\u0017N\u0019:bef\u0004\u0016\r\u001e5F]R\u0014\u0018.Z:!\u0011!I\u0005A!f\u0001\n\u00031\u0013\u0001\u00036bm\u0006|\u0005\u000f^:\t\u0011-\u0003!\u0011#Q\u0001\n\u001d\n\u0011B[1wC>\u0003Ho\u001d\u0011\t\u000b5\u0003A\u0011\u0001(\u0002\rqJg.\u001b;?)\u001dy\u0015KU*U+Z\u0003\"\u0001\u0015\u0001\u000e\u0003\tAQ\u0001\u0007'A\u0002mAQ!\n'A\u0002\u001dBQA\u000e'A\u0002aBQ!\u0011'A\u0002\u001dBQ!\u0012'A\u0002\u001dBQ!\u0013'A\u0002\u001dBq\u0001\u0017\u0001\u0002\u0002\u0013\u0005\u0011,\u0001\u0003d_BLHcB([7rkfl\u0018\u0005\b1]\u0003\n\u00111\u0001\u001c\u0011\u001d)s\u000b%AA\u0002\u001dBqAN,\u0011\u0002\u0003\u0007\u0001\bC\u0004B/B\u0005\t\u0019A\u0014\t\u000f\u0015;\u0006\u0013!a\u0001O!9\u0011j\u0016I\u0001\u0002\u00049\u0003bB1\u0001#\u0003%\tAY\u0001\u000fG>\u0004\u0018\u0010\n3fM\u0006,H\u000e\u001e\u00132+\u0005\u0019'FA\u000eeW\u0005)\u0007C\u00014l\u001b\u00059'B\u00015j\u0003%)hn\u00195fG.,GM\u0003\u0002k\u001b\u0005Q\u0011M\u001c8pi\u0006$\u0018n\u001c8\n\u00051<'!E;oG\",7m[3e-\u0006\u0014\u0018.\u00198dK\"9a\u000eAI\u0001\n\u0003y\u0017AD2paf$C-\u001a4bk2$HEM\u000b\u0002a*\u0012q\u0005\u001a\u0005\be\u0002\t\n\u0011\"\u0001t\u00039\u0019w\u000e]=%I\u00164\u0017-\u001e7uIM*\u0012\u0001\u001e\u0016\u0003q\u0011DqA\u001e\u0001\u0012\u0002\u0013\u0005q.\u0001\bd_BLH\u0005Z3gCVdG\u000f\n\u001b\t\u000fa\u0004\u0011\u0013!C\u0001_\u0006q1m\u001c9zI\u0011,g-Y;mi\u0012*\u0004b\u0002>\u0001#\u0003%\ta\\\u0001\u000fG>\u0004\u0018\u0010\n3fM\u0006,H\u000e\u001e\u00137\u0011\u001da\b!!A\u0005Bu\fQ\u0002\u001d:pIV\u001cG\u000f\u0015:fM&DX#\u0001@\u0011\u0007}\fI!\u0004\u0002\u0002\u0002)!\u00111AA\u0003\u0003\u0011a\u0017M\\4\u000b\u0005\u0005\u001d\u0011\u0001\u00026bm\u0006L1\u0001IA\u0001\u0011%\ti\u0001AA\u0001\n\u0003\ty!\u0001\u0007qe>$Wo\u0019;Be&$\u00180\u0006\u0002\u0002\u0012A\u0019A\"a\u0005\n\u0007\u0005UQBA\u0002J]RD\u0011\"!\u0007\u0001\u0003\u0003%\t!a\u0007\u0002\u001dA\u0014x\u000eZ;di\u0016cW-\\3oiR!\u0011QDA\u0012!\ra\u0011qD\u0005\u0004\u0003Ci!aA!os\"Q\u0011QEA\f\u0003\u0003\u0005\r!!\u0005\u0002\u0007a$\u0013\u0007C\u0005\u0002*\u0001\t\t\u0011\"\u0011\u0002,\u0005y\u0001O]8ek\u000e$\u0018\n^3sCR|'/\u0006\u0002\u0002.A)\u0011(a\f\u0002\u001e%\u0019\u0011\u0011\u0007\u001e\u0003\u0011%#XM]1u_JD\u0011\"!\u000e\u0001\u0003\u0003%\t!a\u000e\u0002\u0011\r\fg.R9vC2$B!!\u000f\u0002@A\u0019A\"a\u000f\n\u0007\u0005uRBA\u0004C_>dW-\u00198\t\u0015\u0005\u0015\u00121GA\u0001\u0002\u0004\ti\u0002C\u0005\u0002D\u0001\t\t\u0011\"\u0011\u0002F\u0005A\u0001.Y:i\u0007>$W\r\u0006\u0002\u0002\u0012!I\u0011\u0011\n\u0001\u0002\u0002\u0013\u0005\u00131J\u0001\ti>\u001cFO]5oOR\ta\u0010C\u0005\u0002P\u0001\t\t\u0011\"\u0011\u0002R\u00051Q-];bYN$B!!\u000f\u0002T!Q\u0011QEA'\u0003\u0003\u0005\r!!\b\b\u0015\u0005]#!!A\t\u0002\u0011\tI&A\u0004D_6l\u0017M\u001c3\u0011\u0007A\u000bYFB\u0005\u0002\u0005\u0005\u0005\t\u0012\u0001\u0003\u0002^M)\u00111LA0)AY\u0011\u0011MA47\u001dBteJ\u0014P\u001b\t\t\u0019GC\u0002\u0002f5\tqA];oi&lW-\u0003\u0003\u0002j\u0005\r$!E!cgR\u0014\u0018m\u0019;Gk:\u001cG/[8om!9Q*a\u0017\u0005\u0002\u00055DCAA-\u0011)\tI%a\u0017\u0002\u0002\u0013\u0015\u00131\n\u0005\u000b\u0003g\nY&!A\u0005\u0002\u0006U\u0014!B1qa2LH#D(\u0002x\u0005e\u00141PA?\u0003\u007f\n\t\t\u0003\u0004\u0019\u0003c\u0002\ra\u0007\u0005\u0007K\u0005E\u0004\u0019A\u0014\t\rY\n\t\b1\u00019\u0011\u0019\t\u0015\u0011\u000fa\u0001O!1Q)!\u001dA\u0002\u001dBa!SA9\u0001\u00049\u0003BCAC\u00037\n\t\u0011\"!\u0002\b\u00069QO\\1qa2LH\u0003BAE\u0003+\u0003R\u0001DAF\u0003\u001fK1!!$\u000e\u0005\u0019y\u0005\u000f^5p]BIA\"!%\u001cOa:seJ\u0005\u0004\u0003'k!A\u0002+va2,g\u0007C\u0005\u0002\u0018\u0006\r\u0015\u0011!a\u0001\u001f\u0006\u0019\u0001\u0010\n\u0019\t\u0015\u0005m\u00151LA\u0001\n\u0013\ti*A\u0006sK\u0006$'+Z:pYZ,GCAAP!\ry\u0018\u0011U\u0005\u0005\u0003G\u000b\tA\u0001\u0004PE*,7\r\u001e"
)
public class Command implements Product, Serializable {
   private final String mainClass;
   private final Seq<String> arguments;
   private final Map<String, String> environment;
   private final Seq<String> classPathEntries;
   private final Seq<String> libraryPathEntries;
   private final Seq<String> javaOpts;

   public static Option<Tuple6<String, Seq<String>, Map<String, String>, Seq<String>, Seq<String>, Seq<String>>> unapply(Command var0) {
      return Command$.MODULE$.unapply(var0);
   }

   public static Command apply(String var0, Seq<String> var1, Map<String, String> var2, Seq<String> var3, Seq<String> var4, Seq<String> var5) {
      return Command$.MODULE$.apply(var0, var1, var2, var3, var4, var5);
   }

   public static Function1<Tuple6<String, Seq<String>, Map<String, String>, Seq<String>, Seq<String>, Seq<String>>, Command> tupled() {
      return Command$.MODULE$.tupled();
   }

   public static Function1<String, Function1<Seq<String>, Function1<Map<String, String>, Function1<Seq<String>, Function1<Seq<String>, Function1<Seq<String>, Command>>>>>> curried() {
      return Command$.MODULE$.curried();
   }

   public String mainClass() {
      return this.mainClass;
   }

   public Seq<String> arguments() {
      return this.arguments;
   }

   public Map<String, String> environment() {
      return this.environment;
   }

   public Seq<String> classPathEntries() {
      return this.classPathEntries;
   }

   public Seq<String> libraryPathEntries() {
      return this.libraryPathEntries;
   }

   public Seq<String> javaOpts() {
      return this.javaOpts;
   }

   public Command copy(String mainClass, Seq<String> arguments, Map<String, String> environment, Seq<String> classPathEntries, Seq<String> libraryPathEntries, Seq<String> javaOpts) {
      return new Command(mainClass, arguments, environment, classPathEntries, libraryPathEntries, javaOpts);
   }

   public String copy$default$1() {
      return this.mainClass();
   }

   public Seq<String> copy$default$2() {
      return this.arguments();
   }

   public Map<String, String> copy$default$3() {
      return this.environment();
   }

   public Seq<String> copy$default$4() {
      return this.classPathEntries();
   }

   public Seq<String> copy$default$5() {
      return this.libraryPathEntries();
   }

   public Seq<String> copy$default$6() {
      return this.javaOpts();
   }

   public String productPrefix() {
      return "Command";
   }

   public int productArity() {
      return 6;
   }

   public Object productElement(int x$1) {
      Object var10000;
      switch (x$1) {
         case 0:
            var10000 = this.mainClass();
            break;
         case 1:
            var10000 = this.arguments();
            break;
         case 2:
            var10000 = this.environment();
            break;
         case 3:
            var10000 = this.classPathEntries();
            break;
         case 4:
            var10000 = this.libraryPathEntries();
            break;
         case 5:
            var10000 = this.javaOpts();
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
      return x$1 instanceof Command;
   }

   public int hashCode() {
      return ScalaRunTime$.MODULE$._hashCode(this);
   }

   public String toString() {
      return ScalaRunTime$.MODULE$._toString(this);
   }

   public boolean equals(Object x$1) {
      boolean var17;
      if (this != x$1) {
         label98: {
            boolean var3;
            if (x$1 instanceof Command) {
               var3 = true;
            } else {
               var3 = false;
            }

            if (var3) {
               label80: {
                  label89: {
                     Command var4 = (Command)x$1;
                     String var10000 = this.mainClass();
                     String var5 = var4.mainClass();
                     if (var10000 == null) {
                        if (var5 != null) {
                           break label89;
                        }
                     } else if (!var10000.equals(var5)) {
                        break label89;
                     }

                     Seq var11 = this.arguments();
                     Seq var6 = var4.arguments();
                     if (var11 == null) {
                        if (var6 != null) {
                           break label89;
                        }
                     } else if (!var11.equals(var6)) {
                        break label89;
                     }

                     Map var12 = this.environment();
                     Map var7 = var4.environment();
                     if (var12 == null) {
                        if (var7 != null) {
                           break label89;
                        }
                     } else if (!var12.equals(var7)) {
                        break label89;
                     }

                     Seq var13 = this.classPathEntries();
                     Seq var8 = var4.classPathEntries();
                     if (var13 == null) {
                        if (var8 != null) {
                           break label89;
                        }
                     } else if (!var13.equals(var8)) {
                        break label89;
                     }

                     var13 = this.libraryPathEntries();
                     Seq var9 = var4.libraryPathEntries();
                     if (var13 == null) {
                        if (var9 != null) {
                           break label89;
                        }
                     } else if (!var13.equals(var9)) {
                        break label89;
                     }

                     var13 = this.javaOpts();
                     Seq var10 = var4.javaOpts();
                     if (var13 == null) {
                        if (var10 != null) {
                           break label89;
                        }
                     } else if (!var13.equals(var10)) {
                        break label89;
                     }

                     if (var4.canEqual(this)) {
                        var17 = true;
                        break label80;
                     }
                  }

                  var17 = false;
               }

               if (var17) {
                  break label98;
               }
            }

            var17 = false;
            return var17;
         }
      }

      var17 = true;
      return var17;
   }

   public Command(String mainClass, Seq<String> arguments, Map<String, String> environment, Seq<String> classPathEntries, Seq<String> libraryPathEntries, Seq<String> javaOpts) {
      this.mainClass = mainClass;
      this.arguments = arguments;
      this.environment = environment;
      this.classPathEntries = classPathEntries;
      this.libraryPathEntries = libraryPathEntries;
      this.javaOpts = javaOpts;
      Product$class.$init$(this);
   }
}
