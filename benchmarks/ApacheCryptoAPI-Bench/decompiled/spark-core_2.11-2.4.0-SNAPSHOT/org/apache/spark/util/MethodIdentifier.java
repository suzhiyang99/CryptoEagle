package org.apache.spark.util;

import scala.Option;
import scala.Product;
import scala.Product$class;
import scala.Serializable;
import scala.Tuple3;
import scala.collection.Iterator;
import scala.reflect.ScalaSignature;
import scala.runtime.BoxesRunTime;
import scala.runtime.ScalaRunTime$;

@ScalaSignature(
   bytes = "\u0006\u0001\u0005\u0005e\u0001B\u0001\u0003\t.\u0011\u0001#T3uQ>$\u0017\nZ3oi&4\u0017.\u001a:\u000b\u0005\r!\u0011\u0001B;uS2T!!\u0002\u0004\u0002\u000bM\u0004\u0018M]6\u000b\u0005\u001dA\u0011AB1qC\u000eDWMC\u0001\n\u0003\ry'oZ\u0002\u0001+\taQe\u0005\u0003\u0001\u001bM1\u0002C\u0001\b\u0012\u001b\u0005y!\"\u0001\t\u0002\u000bM\u001c\u0017\r\\1\n\u0005Iy!AB!osJ+g\r\u0005\u0002\u000f)%\u0011Qc\u0004\u0002\b!J|G-^2u!\tqq#\u0003\u0002\u0019\u001f\ta1+\u001a:jC2L'0\u00192mK\"A!\u0004\u0001BK\u0002\u0013\u00051$A\u0002dYN,\u0012\u0001\b\t\u0004;\u0001\u001acB\u0001\b\u001f\u0013\tyr\"\u0001\u0004Qe\u0016$WMZ\u0005\u0003C\t\u0012Qa\u00117bgNT!aH\b\u0011\u0005\u0011*C\u0002\u0001\u0003\u0006M\u0001\u0011\ra\n\u0002\u0002)F\u0011\u0001f\u000b\t\u0003\u001d%J!AK\b\u0003\u000f9{G\u000f[5oOB\u0011a\u0002L\u0005\u0003[=\u00111!\u00118z\u0011!y\u0003A!E!\u0002\u0013a\u0012\u0001B2mg\u0002B\u0001\"\r\u0001\u0003\u0016\u0004%\tAM\u0001\u0005]\u0006lW-F\u00014!\tiB'\u0003\u00026E\t11\u000b\u001e:j]\u001eD\u0001b\u000e\u0001\u0003\u0012\u0003\u0006IaM\u0001\u0006]\u0006lW\r\t\u0005\ts\u0001\u0011)\u001a!C\u0001e\u0005!A-Z:d\u0011!Y\u0004A!E!\u0002\u0013\u0019\u0014!\u00023fg\u000e\u0004\u0003\"B\u001f\u0001\t\u0003q\u0014A\u0002\u001fj]&$h\b\u0006\u0003@\u0003\n\u001b\u0005c\u0001!\u0001G5\t!\u0001C\u0003\u001by\u0001\u0007A\u0004C\u00032y\u0001\u00071\u0007C\u0003:y\u0001\u00071\u0007C\u0004F\u0001\u0005\u0005I\u0011\u0001$\u0002\t\r|\u0007/_\u000b\u0003\u000f*#B\u0001S&N\u001dB\u0019\u0001\tA%\u0011\u0005\u0011RE!\u0002\u0014E\u0005\u00049\u0003b\u0002\u000eE!\u0003\u0005\r\u0001\u0014\t\u0004;\u0001J\u0005bB\u0019E!\u0003\u0005\ra\r\u0005\bs\u0011\u0003\n\u00111\u00014\u0011\u001d\u0001\u0006!%A\u0005\u0002E\u000babY8qs\u0012\"WMZ1vYR$\u0013'\u0006\u0002S;V\t1K\u000b\u0002\u001d).\nQ\u000b\u0005\u0002W76\tqK\u0003\u0002Y3\u0006IQO\\2iK\u000e\\W\r\u001a\u0006\u00035>\t!\"\u00198o_R\fG/[8o\u0013\tavKA\tv]\u000eDWmY6fIZ\u000b'/[1oG\u0016$QAJ(C\u0002\u001dBqa\u0018\u0001\u0012\u0002\u0013\u0005\u0001-\u0001\bd_BLH\u0005Z3gCVdG\u000f\n\u001a\u0016\u0005\u0005\u001cW#\u00012+\u0005M\"F!\u0002\u0014_\u0005\u00049\u0003bB3\u0001#\u0003%\tAZ\u0001\u000fG>\u0004\u0018\u0010\n3fM\u0006,H\u000e\u001e\u00134+\t\tw\rB\u0003'I\n\u0007q\u0005C\u0004j\u0001\u0005\u0005I\u0011\t6\u0002\u001bA\u0014x\u000eZ;diB\u0013XMZ5y+\u0005Y\u0007C\u00017r\u001b\u0005i'B\u00018p\u0003\u0011a\u0017M\\4\u000b\u0003A\fAA[1wC&\u0011Q'\u001c\u0005\bg\u0002\t\t\u0011\"\u0001u\u00031\u0001(o\u001c3vGR\f%/\u001b;z+\u0005)\bC\u0001\bw\u0013\t9xBA\u0002J]RDq!\u001f\u0001\u0002\u0002\u0013\u0005!0\u0001\bqe>$Wo\u0019;FY\u0016lWM\u001c;\u0015\u0005-Z\bb\u0002?y\u0003\u0003\u0005\r!^\u0001\u0004q\u0012\n\u0004b\u0002@\u0001\u0003\u0003%\te`\u0001\u0010aJ|G-^2u\u0013R,'/\u0019;peV\u0011\u0011\u0011\u0001\t\u0006\u0003\u0007\tIaK\u0007\u0003\u0003\u000bQ1!a\u0002\u0010\u0003)\u0019w\u000e\u001c7fGRLwN\\\u0005\u0005\u0003\u0017\t)A\u0001\u0005Ji\u0016\u0014\u0018\r^8s\u0011%\ty\u0001AA\u0001\n\u0003\t\t\"\u0001\u0005dC:,\u0015/^1m)\u0011\t\u0019\"!\u0007\u0011\u00079\t)\"C\u0002\u0002\u0018=\u0011qAQ8pY\u0016\fg\u000e\u0003\u0005}\u0003\u001b\t\t\u00111\u0001,\u0011%\ti\u0002AA\u0001\n\u0003\ny\"\u0001\u0005iCND7i\u001c3f)\u0005)\b\"CA\u0012\u0001\u0005\u0005I\u0011IA\u0013\u0003!!xn\u0015;sS:<G#A6\t\u0013\u0005%\u0002!!A\u0005B\u0005-\u0012AB3rk\u0006d7\u000f\u0006\u0003\u0002\u0014\u00055\u0002\u0002\u0003?\u0002(\u0005\u0005\t\u0019A\u0016\b\u0013\u0005E\"!!A\t\n\u0005M\u0012\u0001E'fi\"|G-\u00133f]RLg-[3s!\r\u0001\u0015Q\u0007\u0004\t\u0003\t\t\t\u0011#\u0003\u00028M!\u0011QG\u0007\u0017\u0011\u001di\u0014Q\u0007C\u0001\u0003w!\"!a\r\t\u0015\u0005\r\u0012QGA\u0001\n\u000b\n)\u0003\u0003\u0006\u0002B\u0005U\u0012\u0011!CA\u0003\u0007\nQ!\u00199qYf,B!!\u0012\u0002LQA\u0011qIA'\u0003#\n\u0019\u0006\u0005\u0003A\u0001\u0005%\u0003c\u0001\u0013\u0002L\u00111a%a\u0010C\u0002\u001dBqAGA \u0001\u0004\ty\u0005\u0005\u0003\u001eA\u0005%\u0003BB\u0019\u0002@\u0001\u00071\u0007\u0003\u0004:\u0003\u007f\u0001\ra\r\u0005\u000b\u0003/\n)$!A\u0005\u0002\u0006e\u0013aB;oCB\u0004H._\u000b\u0005\u00037\ni\u0007\u0006\u0003\u0002^\u0005=\u0004#\u0002\b\u0002`\u0005\r\u0014bAA1\u001f\t1q\n\u001d;j_:\u0004rADA3\u0003S\u001a4'C\u0002\u0002h=\u0011a\u0001V;qY\u0016\u001c\u0004\u0003B\u000f!\u0003W\u00022\u0001JA7\t\u00191\u0013Q\u000bb\u0001O!Q\u0011\u0011OA+\u0003\u0003\u0005\r!a\u001d\u0002\u0007a$\u0003\u0007\u0005\u0003A\u0001\u0005-\u0004BCA<\u0003k\t\t\u0011\"\u0003\u0002z\u0005Y!/Z1e%\u0016\u001cx\u000e\u001c<f)\t\tY\bE\u0002m\u0003{J1!a n\u0005\u0019y%M[3di\u0002"
)
public class MethodIdentifier<T> implements Product, Serializable {
   private final Class<T> cls;
   private final String name;
   private final String desc;

   public static <T> Option<Tuple3<Class<T>, String, String>> unapply(MethodIdentifier<T> var0) {
      return MethodIdentifier$.MODULE$.unapply(var0);
   }

   public static <T> MethodIdentifier<T> apply(Class<T> var0, String var1, String var2) {
      return MethodIdentifier$.MODULE$.<T>apply(var0, var1, var2);
   }

   public Class<T> cls() {
      return this.cls;
   }

   public String name() {
      return this.name;
   }

   public String desc() {
      return this.desc;
   }

   public <T> MethodIdentifier<T> copy(Class<T> cls, String name, String desc) {
      return new MethodIdentifier<T>(cls, name, desc);
   }

   public <T> Class<T> copy$default$1() {
      return this.cls();
   }

   public <T> String copy$default$2() {
      return this.name();
   }

   public <T> String copy$default$3() {
      return this.desc();
   }

   public String productPrefix() {
      return "MethodIdentifier";
   }

   public int productArity() {
      return 3;
   }

   public Object productElement(int x$1) {
      Object var10000;
      switch (x$1) {
         case 0:
            var10000 = this.cls();
            break;
         case 1:
            var10000 = this.name();
            break;
         case 2:
            var10000 = this.desc();
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
      return x$1 instanceof MethodIdentifier;
   }

   public int hashCode() {
      return ScalaRunTime$.MODULE$._hashCode(this);
   }

   public String toString() {
      return ScalaRunTime$.MODULE$._toString(this);
   }

   public boolean equals(Object x$1) {
      boolean var11;
      if (this != x$1) {
         label71: {
            boolean var3;
            if (x$1 instanceof MethodIdentifier) {
               var3 = true;
            } else {
               var3 = false;
            }

            if (var3) {
               label53: {
                  label62: {
                     MethodIdentifier var4 = (MethodIdentifier)x$1;
                     Class var10000 = this.cls();
                     Class var5 = var4.cls();
                     if (var10000 == null) {
                        if (var5 != null) {
                           break label62;
                        }
                     } else if (!var10000.equals(var5)) {
                        break label62;
                     }

                     String var8 = this.name();
                     String var6 = var4.name();
                     if (var8 == null) {
                        if (var6 != null) {
                           break label62;
                        }
                     } else if (!var8.equals(var6)) {
                        break label62;
                     }

                     var8 = this.desc();
                     String var7 = var4.desc();
                     if (var8 == null) {
                        if (var7 != null) {
                           break label62;
                        }
                     } else if (!var8.equals(var7)) {
                        break label62;
                     }

                     if (var4.canEqual(this)) {
                        var11 = true;
                        break label53;
                     }
                  }

                  var11 = false;
               }

               if (var11) {
                  break label71;
               }
            }

            var11 = false;
            return var11;
         }
      }

      var11 = true;
      return var11;
   }

   public MethodIdentifier(Class<T> cls, String name, String desc) {
      this.cls = cls;
      this.name = name;
      this.desc = desc;
      Product$class.$init$(this);
   }
}
