package org.apache.spark.deploy;

import scala.Function1;
import scala.Option;
import scala.Predef$;
import scala.Product;
import scala.Product$class;
import scala.Serializable;
import scala.StringContext;
import scala.Tuple5;
import scala.collection.Iterator;
import scala.reflect.ScalaSignature;
import scala.runtime.BoxesRunTime;
import scala.runtime.ScalaRunTime$;
import scala.runtime.Statics;

@ScalaSignature(
   bytes = "\u0006\u0001\u0005\u0005e!B\u0001\u0003\u0001\nQ!!\u0005#sSZ,'\u000fR3tGJL\u0007\u000f^5p]*\u00111\u0001B\u0001\u0007I\u0016\u0004Hn\\=\u000b\u0005\u00151\u0011!B:qCJ\\'BA\u0004\t\u0003\u0019\t\u0007/Y2iK*\t\u0011\"A\u0002pe\u001e\u001cB\u0001A\u0006\u0012)A\u0011AbD\u0007\u0002\u001b)\ta\"A\u0003tG\u0006d\u0017-\u0003\u0002\u0011\u001b\t1\u0011I\\=SK\u001a\u0004\"\u0001\u0004\n\n\u0005Mi!a\u0002)s_\u0012,8\r\u001e\t\u0003\u0019UI!AF\u0007\u0003\u0019M+'/[1mSj\f'\r\\3\t\u0011a\u0001!Q3A\u0005\u0002i\taA[1s+Jd7\u0001A\u000b\u00027A\u0011Ad\b\b\u0003\u0019uI!AH\u0007\u0002\rA\u0013X\rZ3g\u0013\t\u0001\u0013E\u0001\u0004TiJLgn\u001a\u0006\u0003=5A\u0001b\t\u0001\u0003\u0012\u0003\u0006IaG\u0001\bU\u0006\u0014XK\u001d7!\u0011!)\u0003A!f\u0001\n\u00031\u0013aA7f[V\tq\u0005\u0005\u0002\rQ%\u0011\u0011&\u0004\u0002\u0004\u0013:$\b\u0002C\u0016\u0001\u0005#\u0005\u000b\u0011B\u0014\u0002\t5,W\u000e\t\u0005\t[\u0001\u0011)\u001a!C\u0001M\u0005)1m\u001c:fg\"Aq\u0006\u0001B\tB\u0003%q%\u0001\u0004d_J,7\u000f\t\u0005\tc\u0001\u0011)\u001a!C\u0001e\u0005I1/\u001e9feZL7/Z\u000b\u0002gA\u0011A\u0002N\u0005\u0003k5\u0011qAQ8pY\u0016\fg\u000e\u0003\u00058\u0001\tE\t\u0015!\u00034\u0003)\u0019X\u000f]3sm&\u001cX\r\t\u0005\ts\u0001\u0011)\u001a!C\u0001u\u000591m\\7nC:$W#A\u001e\u0011\u0005qjT\"\u0001\u0002\n\u0005y\u0012!aB\"p[6\fg\u000e\u001a\u0005\t\u0001\u0002\u0011\t\u0012)A\u0005w\u0005A1m\\7nC:$\u0007\u0005C\u0003C\u0001\u0011\u00051)\u0001\u0004=S:LGO\u0010\u000b\u0007\t\u00163u\tS%\u0011\u0005q\u0002\u0001\"\u0002\rB\u0001\u0004Y\u0002\"B\u0013B\u0001\u00049\u0003\"B\u0017B\u0001\u00049\u0003\"B\u0019B\u0001\u0004\u0019\u0004\"B\u001dB\u0001\u0004Y\u0004\"B&\u0001\t\u0003b\u0015\u0001\u0003;p'R\u0014\u0018N\\4\u0015\u0003mAqA\u0014\u0001\u0002\u0002\u0013\u0005q*\u0001\u0003d_BLHC\u0002#Q#J\u001bF\u000bC\u0004\u0019\u001bB\u0005\t\u0019A\u000e\t\u000f\u0015j\u0005\u0013!a\u0001O!9Q&\u0014I\u0001\u0002\u00049\u0003bB\u0019N!\u0003\u0005\ra\r\u0005\bs5\u0003\n\u00111\u0001<\u0011\u001d1\u0006!%A\u0005\u0002]\u000babY8qs\u0012\"WMZ1vYR$\u0013'F\u0001YU\tY\u0012lK\u0001[!\tY\u0006-D\u0001]\u0015\tif,A\u0005v]\u000eDWmY6fI*\u0011q,D\u0001\u000bC:tw\u000e^1uS>t\u0017BA1]\u0005E)hn\u00195fG.,GMV1sS\u0006t7-\u001a\u0005\bG\u0002\t\n\u0011\"\u0001e\u00039\u0019w\u000e]=%I\u00164\u0017-\u001e7uII*\u0012!\u001a\u0016\u0003OeCqa\u001a\u0001\u0012\u0002\u0013\u0005A-\u0001\bd_BLH\u0005Z3gCVdG\u000fJ\u001a\t\u000f%\u0004\u0011\u0013!C\u0001U\u0006q1m\u001c9zI\u0011,g-Y;mi\u0012\"T#A6+\u0005MJ\u0006bB7\u0001#\u0003%\tA\\\u0001\u000fG>\u0004\u0018\u0010\n3fM\u0006,H\u000e\u001e\u00136+\u0005y'FA\u001eZ\u0011\u001d\t\b!!A\u0005BI\fQ\u0002\u001d:pIV\u001cG\u000f\u0015:fM&DX#A:\u0011\u0005QLX\"A;\u000b\u0005Y<\u0018\u0001\u00027b]\u001eT\u0011\u0001_\u0001\u0005U\u00064\u0018-\u0003\u0002!k\"91\u0010AA\u0001\n\u00031\u0013\u0001\u00049s_\u0012,8\r^!sSRL\bbB?\u0001\u0003\u0003%\tA`\u0001\u000faJ|G-^2u\u000b2,W.\u001a8u)\ry\u0018Q\u0001\t\u0004\u0019\u0005\u0005\u0011bAA\u0002\u001b\t\u0019\u0011I\\=\t\u0011\u0005\u001dA0!AA\u0002\u001d\n1\u0001\u001f\u00132\u0011%\tY\u0001AA\u0001\n\u0003\ni!A\bqe>$Wo\u0019;Ji\u0016\u0014\u0018\r^8s+\t\ty\u0001E\u0003\u0002\u0012\u0005]q0\u0004\u0002\u0002\u0014)\u0019\u0011QC\u0007\u0002\u0015\r|G\u000e\\3di&|g.\u0003\u0003\u0002\u001a\u0005M!\u0001C%uKJ\fGo\u001c:\t\u0013\u0005u\u0001!!A\u0005\u0002\u0005}\u0011\u0001C2b]\u0016\u000bX/\u00197\u0015\u0007M\n\t\u0003C\u0005\u0002\b\u0005m\u0011\u0011!a\u0001\u007f\"I\u0011Q\u0005\u0001\u0002\u0002\u0013\u0005\u0013qE\u0001\tQ\u0006\u001c\bnQ8eKR\tq\u0005C\u0005\u0002,\u0001\t\t\u0011\"\u0011\u0002.\u00051Q-];bYN$2aMA\u0018\u0011%\t9!!\u000b\u0002\u0002\u0003\u0007qp\u0002\u0006\u00024\t\t\t\u0011#\u0001\u0003\u0003k\t\u0011\u0003\u0012:jm\u0016\u0014H)Z:de&\u0004H/[8o!\ra\u0014q\u0007\u0004\n\u0003\t\t\t\u0011#\u0001\u0003\u0003s\u0019R!a\u000e\u0002<Q\u0001\"\"!\u0010\u0002Dm9seM\u001eE\u001b\t\tyDC\u0002\u0002B5\tqA];oi&lW-\u0003\u0003\u0002F\u0005}\"!E!cgR\u0014\u0018m\u0019;Gk:\u001cG/[8ok!9!)a\u000e\u0005\u0002\u0005%CCAA\u001b\u0011%Y\u0015qGA\u0001\n\u000b\ni\u0005F\u0001t\u0011)\t\t&a\u000e\u0002\u0002\u0013\u0005\u00151K\u0001\u0006CB\u0004H.\u001f\u000b\f\t\u0006U\u0013qKA-\u00037\ni\u0006\u0003\u0004\u0019\u0003\u001f\u0002\ra\u0007\u0005\u0007K\u0005=\u0003\u0019A\u0014\t\r5\ny\u00051\u0001(\u0011\u0019\t\u0014q\na\u0001g!1\u0011(a\u0014A\u0002mB!\"!\u0019\u00028\u0005\u0005I\u0011QA2\u0003\u001d)h.\u00199qYf$B!!\u001a\u0002rA)A\"a\u001a\u0002l%\u0019\u0011\u0011N\u0007\u0003\r=\u0003H/[8o!!a\u0011QN\u000e(OMZ\u0014bAA8\u001b\t1A+\u001e9mKVB\u0011\"a\u001d\u0002`\u0005\u0005\t\u0019\u0001#\u0002\u0007a$\u0003\u0007\u0003\u0006\u0002x\u0005]\u0012\u0011!C\u0005\u0003s\n1B]3bIJ+7o\u001c7wKR\u0011\u00111\u0010\t\u0004i\u0006u\u0014bAA@k\n1qJ\u00196fGR\u0004"
)
public class DriverDescription implements Product, Serializable {
   private final String jarUrl;
   private final int mem;
   private final int cores;
   private final boolean supervise;
   private final Command command;

   public static Option<Tuple5<String, Object, Object, Object, Command>> unapply(DriverDescription var0) {
      return DriverDescription$.MODULE$.unapply(var0);
   }

   public static DriverDescription apply(String var0, int var1, int var2, boolean var3, Command var4) {
      return DriverDescription$.MODULE$.apply(var0, var1, var2, var3, var4);
   }

   public static Function1<Tuple5<String, Object, Object, Object, Command>, DriverDescription> tupled() {
      return DriverDescription$.MODULE$.tupled();
   }

   public static Function1<String, Function1<Object, Function1<Object, Function1<Object, Function1<Command, DriverDescription>>>>> curried() {
      return DriverDescription$.MODULE$.curried();
   }

   public String jarUrl() {
      return this.jarUrl;
   }

   public int mem() {
      return this.mem;
   }

   public int cores() {
      return this.cores;
   }

   public boolean supervise() {
      return this.supervise;
   }

   public Command command() {
      return this.command;
   }

   public String toString() {
      return (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"DriverDescription (", ")"})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.command().mainClass()}));
   }

   public DriverDescription copy(String jarUrl, int mem, int cores, boolean supervise, Command command) {
      return new DriverDescription(jarUrl, mem, cores, supervise, command);
   }

   public String copy$default$1() {
      return this.jarUrl();
   }

   public int copy$default$2() {
      return this.mem();
   }

   public int copy$default$3() {
      return this.cores();
   }

   public boolean copy$default$4() {
      return this.supervise();
   }

   public Command copy$default$5() {
      return this.command();
   }

   public String productPrefix() {
      return "DriverDescription";
   }

   public int productArity() {
      return 5;
   }

   public Object productElement(int x$1) {
      Object var10000;
      switch (x$1) {
         case 0:
            var10000 = this.jarUrl();
            break;
         case 1:
            var10000 = BoxesRunTime.boxToInteger(this.mem());
            break;
         case 2:
            var10000 = BoxesRunTime.boxToInteger(this.cores());
            break;
         case 3:
            var10000 = BoxesRunTime.boxToBoolean(this.supervise());
            break;
         case 4:
            var10000 = this.command();
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
      return x$1 instanceof DriverDescription;
   }

   public int hashCode() {
      int var1 = -889275714;
      var1 = Statics.mix(var1, Statics.anyHash(this.jarUrl()));
      var1 = Statics.mix(var1, this.mem());
      var1 = Statics.mix(var1, this.cores());
      var1 = Statics.mix(var1, this.supervise() ? 1231 : 1237);
      var1 = Statics.mix(var1, Statics.anyHash(this.command()));
      return Statics.finalizeHash(var1, 5);
   }

   public boolean equals(Object x$1) {
      boolean var9;
      if (this != x$1) {
         label67: {
            boolean var3;
            if (x$1 instanceof DriverDescription) {
               var3 = true;
            } else {
               var3 = false;
            }

            if (var3) {
               label50: {
                  label49: {
                     DriverDescription var4 = (DriverDescription)x$1;
                     String var10000 = this.jarUrl();
                     String var5 = var4.jarUrl();
                     if (var10000 == null) {
                        if (var5 != null) {
                           break label49;
                        }
                     } else if (!var10000.equals(var5)) {
                        break label49;
                     }

                     if (this.mem() == var4.mem() && this.cores() == var4.cores() && this.supervise() == var4.supervise()) {
                        label41: {
                           Command var7 = this.command();
                           Command var6 = var4.command();
                           if (var7 == null) {
                              if (var6 != null) {
                                 break label41;
                              }
                           } else if (!var7.equals(var6)) {
                              break label41;
                           }

                           if (var4.canEqual(this)) {
                              var9 = true;
                              break label50;
                           }
                        }
                     }
                  }

                  var9 = false;
               }

               if (var9) {
                  break label67;
               }
            }

            var9 = false;
            return var9;
         }
      }

      var9 = true;
      return var9;
   }

   public DriverDescription(String jarUrl, int mem, int cores, boolean supervise, Command command) {
      this.jarUrl = jarUrl;
      this.mem = mem;
      this.cores = cores;
      this.supervise = supervise;
      this.command = command;
      Product$class.$init$(this);
   }
}
