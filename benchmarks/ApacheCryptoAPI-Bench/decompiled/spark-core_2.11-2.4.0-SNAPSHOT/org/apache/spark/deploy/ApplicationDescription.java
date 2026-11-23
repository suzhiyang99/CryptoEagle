package org.apache.spark.deploy;

import java.net.URI;
import scala.Function1;
import scala.Option;
import scala.Product;
import scala.Product$class;
import scala.Serializable;
import scala.Tuple10;
import scala.collection.Iterator;
import scala.collection.mutable.StringBuilder;
import scala.reflect.ScalaSignature;
import scala.runtime.BoxesRunTime;
import scala.runtime.ScalaRunTime$;
import scala.runtime.Statics;

@ScalaSignature(
   bytes = "\u0006\u0001\t\u0005b!B\u0001\u0003\u0001\u0012Q!AF!qa2L7-\u0019;j_:$Um]2sSB$\u0018n\u001c8\u000b\u0005\r!\u0011A\u00023fa2|\u0017P\u0003\u0002\u0006\r\u0005)1\u000f]1sW*\u0011q\u0001C\u0001\u0007CB\f7\r[3\u000b\u0003%\t1a\u001c:h'\u0011\u00011\"\u0005\u000b\u0011\u00051yQ\"A\u0007\u000b\u00039\tQa]2bY\u0006L!\u0001E\u0007\u0003\r\u0005s\u0017PU3g!\ta!#\u0003\u0002\u0014\u001b\t9\u0001K]8ek\u000e$\bC\u0001\u0007\u0016\u0013\t1RB\u0001\u0007TKJL\u0017\r\\5{C\ndW\r\u0003\u0005\u0019\u0001\tU\r\u0011\"\u0001\u001b\u0003\u0011q\u0017-\\3\u0004\u0001U\t1\u0004\u0005\u0002\u001d?9\u0011A\"H\u0005\u0003=5\ta\u0001\u0015:fI\u00164\u0017B\u0001\u0011\"\u0005\u0019\u0019FO]5oO*\u0011a$\u0004\u0005\tG\u0001\u0011\t\u0012)A\u00057\u0005)a.Y7fA!AQ\u0005\u0001BK\u0002\u0013\u0005a%\u0001\u0005nCb\u001cuN]3t+\u00059\u0003c\u0001\u0007)U%\u0011\u0011&\u0004\u0002\u0007\u001fB$\u0018n\u001c8\u0011\u00051Y\u0013B\u0001\u0017\u000e\u0005\rIe\u000e\u001e\u0005\t]\u0001\u0011\t\u0012)A\u0005O\u0005IQ.\u0019=D_J,7\u000f\t\u0005\ta\u0001\u0011)\u001a!C\u0001c\u0005\u0019R.Z7pef\u0004VM]#yK\u000e,Ho\u001c:N\u0005V\t!\u0006\u0003\u00054\u0001\tE\t\u0015!\u0003+\u0003QiW-\\8ssB+'/\u0012=fGV$xN]'CA!AQ\u0007\u0001BK\u0002\u0013\u0005a'A\u0004d_6l\u0017M\u001c3\u0016\u0003]\u0002\"\u0001O\u001d\u000e\u0003\tI!A\u000f\u0002\u0003\u000f\r{W.\\1oI\"AA\b\u0001B\tB\u0003%q'\u0001\u0005d_6l\u0017M\u001c3!\u0011!q\u0004A!f\u0001\n\u0003Q\u0012\u0001C1qaVKWK\u001d7\t\u0011\u0001\u0003!\u0011#Q\u0001\nm\t\u0011\"\u00199q+&,&\u000f\u001c\u0011\t\u0011\t\u0003!Q3A\u0005\u0002\r\u000b1\"\u001a<f]Rdun\u001a#jeV\tA\tE\u0002\rQ\u0015\u0003\"AR&\u000e\u0003\u001dS!\u0001S%\u0002\u00079,GOC\u0001K\u0003\u0011Q\u0017M^1\n\u00051;%aA+S\u0013\"Aa\n\u0001B\tB\u0003%A)\u0001\u0007fm\u0016tG\u000fT8h\t&\u0014\b\u0005\u0003\u0005Q\u0001\tU\r\u0011\"\u0001R\u00035)g/\u001a8u\u0019><7i\u001c3fGV\t!\u000bE\u0002\rQmA\u0001\u0002\u0016\u0001\u0003\u0012\u0003\u0006IAU\u0001\u000fKZ,g\u000e\u001e'pO\u000e{G-Z2!\u0011!1\u0006A!f\u0001\n\u00031\u0013\u0001E2pe\u0016\u001c\b+\u001a:Fq\u0016\u001cW\u000f^8s\u0011!A\u0006A!E!\u0002\u00139\u0013!E2pe\u0016\u001c\b+\u001a:Fq\u0016\u001cW\u000f^8sA!A!\f\u0001BK\u0002\u0013\u0005a%\u0001\u000bj]&$\u0018.\u00197Fq\u0016\u001cW\u000f^8s\u0019&l\u0017\u000e\u001e\u0005\t9\u0002\u0011\t\u0012)A\u0005O\u0005)\u0012N\\5uS\u0006dW\t_3dkR|'\u000fT5nSR\u0004\u0003\u0002\u00030\u0001\u0005+\u0007I\u0011\u0001\u000e\u0002\tU\u001cXM\u001d\u0005\tA\u0002\u0011\t\u0012)A\u00057\u0005)Qo]3sA!)!\r\u0001C\u0001G\u00061A(\u001b8jiz\"2\u0002Z3gO\"L'n\u001b7n]B\u0011\u0001\b\u0001\u0005\u00061\u0005\u0004\ra\u0007\u0005\u0006K\u0005\u0004\ra\n\u0005\u0006a\u0005\u0004\rA\u000b\u0005\u0006k\u0005\u0004\ra\u000e\u0005\u0006}\u0005\u0004\ra\u0007\u0005\b\u0005\u0006\u0004\n\u00111\u0001E\u0011\u001d\u0001\u0016\r%AA\u0002ICqAV1\u0011\u0002\u0003\u0007q\u0005C\u0004[CB\u0005\t\u0019A\u0014\t\u000fy\u000b\u0007\u0013!a\u00017!)\u0001\u000f\u0001C!c\u0006AAo\\*ue&tw\rF\u0001\u001c\u0011\u001d\u0019\b!!A\u0005\u0002Q\fAaY8qsRYA-\u001e<xqfT8\u0010`?\u007f\u0011\u001dA\"\u000f%AA\u0002mAq!\n:\u0011\u0002\u0003\u0007q\u0005C\u00041eB\u0005\t\u0019\u0001\u0016\t\u000fU\u0012\b\u0013!a\u0001o!9aH\u001dI\u0001\u0002\u0004Y\u0002b\u0002\"s!\u0003\u0005\r\u0001\u0012\u0005\b!J\u0004\n\u00111\u0001S\u0011\u001d1&\u000f%AA\u0002\u001dBqA\u0017:\u0011\u0002\u0003\u0007q\u0005C\u0004_eB\u0005\t\u0019A\u000e\t\u0013\u0005\u0005\u0001!%A\u0005\u0002\u0005\r\u0011AD2paf$C-\u001a4bk2$H%M\u000b\u0003\u0003\u000bQ3aGA\u0004W\t\tI\u0001\u0005\u0003\u0002\f\u0005UQBAA\u0007\u0015\u0011\ty!!\u0005\u0002\u0013Ut7\r[3dW\u0016$'bAA\n\u001b\u0005Q\u0011M\u001c8pi\u0006$\u0018n\u001c8\n\t\u0005]\u0011Q\u0002\u0002\u0012k:\u001c\u0007.Z2lK\u00124\u0016M]5b]\u000e,\u0007\"CA\u000e\u0001E\u0005I\u0011AA\u000f\u00039\u0019w\u000e]=%I\u00164\u0017-\u001e7uII*\"!a\b+\u0007\u001d\n9\u0001C\u0005\u0002$\u0001\t\n\u0011\"\u0001\u0002&\u0005q1m\u001c9zI\u0011,g-Y;mi\u0012\u001aTCAA\u0014U\rQ\u0013q\u0001\u0005\n\u0003W\u0001\u0011\u0013!C\u0001\u0003[\tabY8qs\u0012\"WMZ1vYR$C'\u0006\u0002\u00020)\u001aq'a\u0002\t\u0013\u0005M\u0002!%A\u0005\u0002\u0005\r\u0011AD2paf$C-\u001a4bk2$H%\u000e\u0005\n\u0003o\u0001\u0011\u0013!C\u0001\u0003s\tabY8qs\u0012\"WMZ1vYR$c'\u0006\u0002\u0002<)\u001aA)a\u0002\t\u0013\u0005}\u0002!%A\u0005\u0002\u0005\u0005\u0013AD2paf$C-\u001a4bk2$HeN\u000b\u0003\u0003\u0007R3AUA\u0004\u0011%\t9\u0005AI\u0001\n\u0003\ti\"\u0001\bd_BLH\u0005Z3gCVdG\u000f\n\u001d\t\u0013\u0005-\u0003!%A\u0005\u0002\u0005u\u0011AD2paf$C-\u001a4bk2$H%\u000f\u0005\n\u0003\u001f\u0002\u0011\u0013!C\u0001\u0003\u0007\tqbY8qs\u0012\"WMZ1vYR$\u0013\u0007\r\u0005\n\u0003'\u0002\u0011\u0011!C!\u0003+\nQ\u0002\u001d:pIV\u001cG\u000f\u0015:fM&DXCAA,!\u0011\tI&a\u0018\u000e\u0005\u0005m#bAA/\u0013\u0006!A.\u00198h\u0013\r\u0001\u00131\f\u0005\t\u0003G\u0002\u0011\u0011!C\u0001c\u0005a\u0001O]8ek\u000e$\u0018I]5us\"I\u0011q\r\u0001\u0002\u0002\u0013\u0005\u0011\u0011N\u0001\u000faJ|G-^2u\u000b2,W.\u001a8u)\u0011\tY'!\u001d\u0011\u00071\ti'C\u0002\u0002p5\u00111!\u00118z\u0011%\t\u0019(!\u001a\u0002\u0002\u0003\u0007!&A\u0002yIEB\u0011\"a\u001e\u0001\u0003\u0003%\t%!\u001f\u0002\u001fA\u0014x\u000eZ;di&#XM]1u_J,\"!a\u001f\u0011\r\u0005u\u00141QA6\u001b\t\tyHC\u0002\u0002\u00026\t!bY8mY\u0016\u001cG/[8o\u0013\u0011\t))a \u0003\u0011%#XM]1u_JD\u0011\"!#\u0001\u0003\u0003%\t!a#\u0002\u0011\r\fg.R9vC2$B!!$\u0002\u0014B\u0019A\"a$\n\u0007\u0005EUBA\u0004C_>dW-\u00198\t\u0015\u0005M\u0014qQA\u0001\u0002\u0004\tY\u0007C\u0005\u0002\u0018\u0002\t\t\u0011\"\u0011\u0002\u001a\u0006A\u0001.Y:i\u0007>$W\rF\u0001+\u0011%\ti\nAA\u0001\n\u0003\ny*\u0001\u0004fcV\fGn\u001d\u000b\u0005\u0003\u001b\u000b\t\u000b\u0003\u0006\u0002t\u0005m\u0015\u0011!a\u0001\u0003W:!\"!*\u0003\u0003\u0003E\t\u0001BAT\u0003Y\t\u0005\u000f\u001d7jG\u0006$\u0018n\u001c8EKN\u001c'/\u001b9uS>t\u0007c\u0001\u001d\u0002*\u001aI\u0011AAA\u0001\u0012\u0003!\u00111V\n\u0006\u0003S\u000bi\u000b\u0006\t\u0010\u0003_\u000b)lG\u0014+om!%kJ\u0014\u001cI6\u0011\u0011\u0011\u0017\u0006\u0004\u0003gk\u0011a\u0002:v]RLW.Z\u0005\u0005\u0003o\u000b\tL\u0001\nBEN$(/Y2u\rVt7\r^5p]F\u0002\u0004b\u00022\u0002*\u0012\u0005\u00111\u0018\u000b\u0003\u0003OC\u0011\u0002]AU\u0003\u0003%)%a0\u0015\u0005\u0005]\u0003BCAb\u0003S\u000b\t\u0011\"!\u0002F\u0006)\u0011\r\u001d9msR)B-a2\u0002J\u0006-\u0017QZAh\u0003#\f\u0019.!6\u0002X\u0006e\u0007B\u0002\r\u0002B\u0002\u00071\u0004\u0003\u0004&\u0003\u0003\u0004\ra\n\u0005\u0007a\u0005\u0005\u0007\u0019\u0001\u0016\t\rU\n\t\r1\u00018\u0011\u0019q\u0014\u0011\u0019a\u00017!A!)!1\u0011\u0002\u0003\u0007A\t\u0003\u0005Q\u0003\u0003\u0004\n\u00111\u0001S\u0011!1\u0016\u0011\u0019I\u0001\u0002\u00049\u0003\u0002\u0003.\u0002BB\u0005\t\u0019A\u0014\t\u0011y\u000b\t\r%AA\u0002mA!\"!8\u0002*\u0006\u0005I\u0011QAp\u0003\u001d)h.\u00199qYf$B!!9\u0002jB!A\u0002KAr!5a\u0011Q]\u000e(U]ZBIU\u0014(7%\u0019\u0011q]\u0007\u0003\u000fQ+\b\u000f\\32a!I\u00111^An\u0003\u0003\u0005\r\u0001Z\u0001\u0004q\u0012\u0002\u0004BCAx\u0003S\u000b\n\u0011\"\u0001\u0002:\u0005YB\u0005\\3tg&t\u0017\u000e\u001e\u0013he\u0016\fG/\u001a:%I\u00164\u0017-\u001e7uIYB!\"a=\u0002*F\u0005I\u0011AA!\u0003m!C.Z:tS:LG\u000fJ4sK\u0006$XM\u001d\u0013eK\u001a\fW\u000f\u001c;%o!Q\u0011q_AU#\u0003%\t!!\b\u00027\u0011bWm]:j]&$He\u001a:fCR,'\u000f\n3fM\u0006,H\u000e\u001e\u00139\u0011)\tY0!+\u0012\u0002\u0013\u0005\u0011QD\u0001\u001cI1,7o]5oSR$sM]3bi\u0016\u0014H\u0005Z3gCVdG\u000fJ\u001d\t\u0015\u0005}\u0018\u0011VI\u0001\n\u0003\t\u0019!\u0001\u000f%Y\u0016\u001c8/\u001b8ji\u0012:'/Z1uKJ$C-\u001a4bk2$H%\r\u0019\t\u0015\t\r\u0011\u0011VI\u0001\n\u0003\tI$A\bbaBd\u0017\u0010\n3fM\u0006,H\u000e\u001e\u00137\u0011)\u00119!!+\u0012\u0002\u0013\u0005\u0011\u0011I\u0001\u0010CB\u0004H.\u001f\u0013eK\u001a\fW\u000f\u001c;%o!Q!1BAU#\u0003%\t!!\b\u0002\u001f\u0005\u0004\b\u000f\\=%I\u00164\u0017-\u001e7uIaB!Ba\u0004\u0002*F\u0005I\u0011AA\u000f\u0003=\t\u0007\u000f\u001d7zI\u0011,g-Y;mi\u0012J\u0004B\u0003B\n\u0003S\u000b\n\u0011\"\u0001\u0002\u0004\u0005\u0001\u0012\r\u001d9ms\u0012\"WMZ1vYR$\u0013\u0007\r\u0005\u000b\u0005/\tI+!A\u0005\n\te\u0011a\u0003:fC\u0012\u0014Vm]8mm\u0016$\"Aa\u0007\u0011\t\u0005e#QD\u0005\u0005\u0005?\tYF\u0001\u0004PE*,7\r\u001e"
)
public class ApplicationDescription implements Product, Serializable {
   private final String name;
   private final Option<Object> maxCores;
   private final int memoryPerExecutorMB;
   private final Command command;
   private final String appUiUrl;
   private final Option<URI> eventLogDir;
   private final Option<String> eventLogCodec;
   private final Option<Object> coresPerExecutor;
   private final Option<Object> initialExecutorLimit;
   private final String user;

   public static String apply$default$10() {
      return ApplicationDescription$.MODULE$.apply$default$10();
   }

   public static Option<Object> apply$default$9() {
      return ApplicationDescription$.MODULE$.apply$default$9();
   }

   public static Option<Object> apply$default$8() {
      return ApplicationDescription$.MODULE$.apply$default$8();
   }

   public static Option<String> apply$default$7() {
      return ApplicationDescription$.MODULE$.apply$default$7();
   }

   public static Option<URI> apply$default$6() {
      return ApplicationDescription$.MODULE$.apply$default$6();
   }

   public static String $lessinit$greater$default$10() {
      return ApplicationDescription$.MODULE$.$lessinit$greater$default$10();
   }

   public static Option<Object> $lessinit$greater$default$9() {
      return ApplicationDescription$.MODULE$.$lessinit$greater$default$9();
   }

   public static Option<Object> $lessinit$greater$default$8() {
      return ApplicationDescription$.MODULE$.$lessinit$greater$default$8();
   }

   public static Option<String> $lessinit$greater$default$7() {
      return ApplicationDescription$.MODULE$.$lessinit$greater$default$7();
   }

   public static Option<URI> $lessinit$greater$default$6() {
      return ApplicationDescription$.MODULE$.$lessinit$greater$default$6();
   }

   public static Option<Tuple10<String, Option<Object>, Object, Command, String, Option<URI>, Option<String>, Option<Object>, Option<Object>, String>> unapply(ApplicationDescription var0) {
      return ApplicationDescription$.MODULE$.unapply(var0);
   }

   public static ApplicationDescription apply(String var0, Option<Object> var1, int var2, Command var3, String var4, Option<URI> var5, Option<String> var6, Option<Object> var7, Option<Object> var8, String var9) {
      return ApplicationDescription$.MODULE$.apply(var0, var1, var2, var3, var4, var5, var6, var7, var8, var9);
   }

   public static Function1<Tuple10<String, Option<Object>, Object, Command, String, Option<URI>, Option<String>, Option<Object>, Option<Object>, String>, ApplicationDescription> tupled() {
      return ApplicationDescription$.MODULE$.tupled();
   }

   public static Function1<String, Function1<Option<Object>, Function1<Object, Function1<Command, Function1<String, Function1<Option<URI>, Function1<Option<String>, Function1<Option<Object>, Function1<Option<Object>, Function1<String, ApplicationDescription>>>>>>>>>> curried() {
      return ApplicationDescription$.MODULE$.curried();
   }

   public String name() {
      return this.name;
   }

   public Option<Object> maxCores() {
      return this.maxCores;
   }

   public int memoryPerExecutorMB() {
      return this.memoryPerExecutorMB;
   }

   public Command command() {
      return this.command;
   }

   public String appUiUrl() {
      return this.appUiUrl;
   }

   public Option<URI> eventLogDir() {
      return this.eventLogDir;
   }

   public Option<String> eventLogCodec() {
      return this.eventLogCodec;
   }

   public Option<Object> coresPerExecutor() {
      return this.coresPerExecutor;
   }

   public Option<Object> initialExecutorLimit() {
      return this.initialExecutorLimit;
   }

   public String user() {
      return this.user;
   }

   public String toString() {
      return (new StringBuilder()).append((Object)"ApplicationDescription(").append((Object)this.name()).append((Object)")").toString();
   }

   public ApplicationDescription copy(String name, Option<Object> maxCores, int memoryPerExecutorMB, Command command, String appUiUrl, Option<URI> eventLogDir, Option<String> eventLogCodec, Option<Object> coresPerExecutor, Option<Object> initialExecutorLimit, String user) {
      return new ApplicationDescription(name, maxCores, memoryPerExecutorMB, command, appUiUrl, eventLogDir, eventLogCodec, coresPerExecutor, initialExecutorLimit, user);
   }

   public String copy$default$1() {
      return this.name();
   }

   public Option<Object> copy$default$2() {
      return this.maxCores();
   }

   public int copy$default$3() {
      return this.memoryPerExecutorMB();
   }

   public Command copy$default$4() {
      return this.command();
   }

   public String copy$default$5() {
      return this.appUiUrl();
   }

   public Option<URI> copy$default$6() {
      return this.eventLogDir();
   }

   public Option<String> copy$default$7() {
      return this.eventLogCodec();
   }

   public Option<Object> copy$default$8() {
      return this.coresPerExecutor();
   }

   public Option<Object> copy$default$9() {
      return this.initialExecutorLimit();
   }

   public String copy$default$10() {
      return this.user();
   }

   public String productPrefix() {
      return "ApplicationDescription";
   }

   public int productArity() {
      return 10;
   }

   public Object productElement(int x$1) {
      Object var10000;
      switch (x$1) {
         case 0:
            var10000 = this.name();
            break;
         case 1:
            var10000 = this.maxCores();
            break;
         case 2:
            var10000 = BoxesRunTime.boxToInteger(this.memoryPerExecutorMB());
            break;
         case 3:
            var10000 = this.command();
            break;
         case 4:
            var10000 = this.appUiUrl();
            break;
         case 5:
            var10000 = this.eventLogDir();
            break;
         case 6:
            var10000 = this.eventLogCodec();
            break;
         case 7:
            var10000 = this.coresPerExecutor();
            break;
         case 8:
            var10000 = this.initialExecutorLimit();
            break;
         case 9:
            var10000 = this.user();
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
      return x$1 instanceof ApplicationDescription;
   }

   public int hashCode() {
      int var1 = -889275714;
      var1 = Statics.mix(var1, Statics.anyHash(this.name()));
      var1 = Statics.mix(var1, Statics.anyHash(this.maxCores()));
      var1 = Statics.mix(var1, this.memoryPerExecutorMB());
      var1 = Statics.mix(var1, Statics.anyHash(this.command()));
      var1 = Statics.mix(var1, Statics.anyHash(this.appUiUrl()));
      var1 = Statics.mix(var1, Statics.anyHash(this.eventLogDir()));
      var1 = Statics.mix(var1, Statics.anyHash(this.eventLogCodec()));
      var1 = Statics.mix(var1, Statics.anyHash(this.coresPerExecutor()));
      var1 = Statics.mix(var1, Statics.anyHash(this.initialExecutorLimit()));
      var1 = Statics.mix(var1, Statics.anyHash(this.user()));
      return Statics.finalizeHash(var1, 10);
   }

   public boolean equals(Object x$1) {
      boolean var23;
      if (this != x$1) {
         label128: {
            boolean var3;
            if (x$1 instanceof ApplicationDescription) {
               var3 = true;
            } else {
               var3 = false;
            }

            if (var3) {
               label109: {
                  label118: {
                     ApplicationDescription var4 = (ApplicationDescription)x$1;
                     String var10000 = this.name();
                     String var5 = var4.name();
                     if (var10000 == null) {
                        if (var5 != null) {
                           break label118;
                        }
                     } else if (!var10000.equals(var5)) {
                        break label118;
                     }

                     Option var14 = this.maxCores();
                     Option var6 = var4.maxCores();
                     if (var14 == null) {
                        if (var6 != null) {
                           break label118;
                        }
                     } else if (!var14.equals(var6)) {
                        break label118;
                     }

                     if (this.memoryPerExecutorMB() == var4.memoryPerExecutorMB()) {
                        label119: {
                           Command var15 = this.command();
                           Command var7 = var4.command();
                           if (var15 == null) {
                              if (var7 != null) {
                                 break label119;
                              }
                           } else if (!var15.equals(var7)) {
                              break label119;
                           }

                           String var16 = this.appUiUrl();
                           String var8 = var4.appUiUrl();
                           if (var16 == null) {
                              if (var8 != null) {
                                 break label119;
                              }
                           } else if (!var16.equals(var8)) {
                              break label119;
                           }

                           Option var17 = this.eventLogDir();
                           Option var9 = var4.eventLogDir();
                           if (var17 == null) {
                              if (var9 != null) {
                                 break label119;
                              }
                           } else if (!var17.equals(var9)) {
                              break label119;
                           }

                           var17 = this.eventLogCodec();
                           Option var10 = var4.eventLogCodec();
                           if (var17 == null) {
                              if (var10 != null) {
                                 break label119;
                              }
                           } else if (!var17.equals(var10)) {
                              break label119;
                           }

                           var17 = this.coresPerExecutor();
                           Option var11 = var4.coresPerExecutor();
                           if (var17 == null) {
                              if (var11 != null) {
                                 break label119;
                              }
                           } else if (!var17.equals(var11)) {
                              break label119;
                           }

                           var17 = this.initialExecutorLimit();
                           Option var12 = var4.initialExecutorLimit();
                           if (var17 == null) {
                              if (var12 != null) {
                                 break label119;
                              }
                           } else if (!var17.equals(var12)) {
                              break label119;
                           }

                           String var21 = this.user();
                           String var13 = var4.user();
                           if (var21 == null) {
                              if (var13 != null) {
                                 break label119;
                              }
                           } else if (!var21.equals(var13)) {
                              break label119;
                           }

                           if (var4.canEqual(this)) {
                              var23 = true;
                              break label109;
                           }
                        }
                     }
                  }

                  var23 = false;
               }

               if (var23) {
                  break label128;
               }
            }

            var23 = false;
            return var23;
         }
      }

      var23 = true;
      return var23;
   }

   public ApplicationDescription(String name, Option<Object> maxCores, int memoryPerExecutorMB, Command command, String appUiUrl, Option<URI> eventLogDir, Option<String> eventLogCodec, Option<Object> coresPerExecutor, Option<Object> initialExecutorLimit, String user) {
      this.name = name;
      this.maxCores = maxCores;
      this.memoryPerExecutorMB = memoryPerExecutorMB;
      this.command = command;
      this.appUiUrl = appUiUrl;
      this.eventLogDir = eventLogDir;
      this.eventLogCodec = eventLogCodec;
      this.coresPerExecutor = coresPerExecutor;
      this.initialExecutorLimit = initialExecutorLimit;
      this.user = user;
      Product$class.$init$(this);
   }
}
