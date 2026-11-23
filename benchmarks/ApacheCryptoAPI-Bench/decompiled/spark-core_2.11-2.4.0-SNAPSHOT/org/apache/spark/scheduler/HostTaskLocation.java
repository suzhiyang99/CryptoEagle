package org.apache.spark.scheduler;

import scala.Function1;
import scala.Option;
import scala.Product;
import scala.Product$class;
import scala.Serializable;
import scala.collection.Iterator;
import scala.reflect.ScalaSignature;
import scala.runtime.BoxesRunTime;
import scala.runtime.ScalaRunTime$;

@ScalaSignature(
   bytes = "\u0006\u0001\u0005\rb!B\u0001\u0003\u0001\u0012Q!\u0001\u0005%pgR$\u0016m]6M_\u000e\fG/[8o\u0015\t\u0019A!A\u0005tG\",G-\u001e7fe*\u0011QAB\u0001\u0006gB\f'o\u001b\u0006\u0003\u000f!\ta!\u00199bG\",'\"A\u0005\u0002\u0007=\u0014xmE\u0003\u0001\u0017E)\u0002\u0004\u0005\u0002\r\u001f5\tQBC\u0001\u000f\u0003\u0015\u00198-\u00197b\u0013\t\u0001RB\u0001\u0004B]f\u0014VM\u001a\t\u0003%Mi\u0011AA\u0005\u0003)\t\u0011A\u0002V1tW2{7-\u0019;j_:\u0004\"\u0001\u0004\f\n\u0005]i!a\u0002)s_\u0012,8\r\u001e\t\u0003\u0019eI!AG\u0007\u0003\u0019M+'/[1mSj\f'\r\\3\t\u0011q\u0001!Q3A\u0005By\tA\u0001[8ti\u000e\u0001Q#A\u0010\u0011\u0005\u0001\u001acB\u0001\u0007\"\u0013\t\u0011S\"\u0001\u0004Qe\u0016$WMZ\u0005\u0003I\u0015\u0012aa\u0015;sS:<'B\u0001\u0012\u000e\u0011!9\u0003A!E!\u0002\u0013y\u0012!\u00025pgR\u0004\u0003\"B\u0015\u0001\t\u0003Q\u0013A\u0002\u001fj]&$h\b\u0006\u0002,YA\u0011!\u0003\u0001\u0005\u00069!\u0002\ra\b\u0005\u0006]\u0001!\teL\u0001\ti>\u001cFO]5oOR\tq\u0004C\u00042\u0001\u0005\u0005I\u0011\u0001\u001a\u0002\t\r|\u0007/\u001f\u000b\u0003WMBq\u0001\b\u0019\u0011\u0002\u0003\u0007q\u0004C\u00046\u0001E\u0005I\u0011\u0001\u001c\u0002\u001d\r|\u0007/\u001f\u0013eK\u001a\fW\u000f\u001c;%cU\tqG\u000b\u0002 q-\n\u0011\b\u0005\u0002;\u007f5\t1H\u0003\u0002={\u0005IQO\\2iK\u000e\\W\r\u001a\u0006\u0003}5\t!\"\u00198o_R\fG/[8o\u0013\t\u00015HA\tv]\u000eDWmY6fIZ\u000b'/[1oG\u0016DqA\u0011\u0001\u0002\u0002\u0013\u00053)A\u0007qe>$Wo\u0019;Qe\u00164\u0017\u000e_\u000b\u0002\tB\u0011QIS\u0007\u0002\r*\u0011q\tS\u0001\u0005Y\u0006twMC\u0001J\u0003\u0011Q\u0017M^1\n\u0005\u00112\u0005b\u0002'\u0001\u0003\u0003%\t!T\u0001\raJ|G-^2u\u0003JLG/_\u000b\u0002\u001dB\u0011AbT\u0005\u0003!6\u00111!\u00138u\u0011\u001d\u0011\u0006!!A\u0005\u0002M\u000ba\u0002\u001d:pIV\u001cG/\u00127f[\u0016tG\u000f\u0006\u0002U/B\u0011A\"V\u0005\u0003-6\u00111!\u00118z\u0011\u001dA\u0016+!AA\u00029\u000b1\u0001\u001f\u00132\u0011\u001dQ\u0006!!A\u0005Bm\u000bq\u0002\u001d:pIV\u001cG/\u0013;fe\u0006$xN]\u000b\u00029B\u0019Q\f\u0019+\u000e\u0003yS!aX\u0007\u0002\u0015\r|G\u000e\\3di&|g.\u0003\u0002b=\nA\u0011\n^3sCR|'\u000fC\u0004d\u0001\u0005\u0005I\u0011\u00013\u0002\u0011\r\fg.R9vC2$\"!\u001a5\u0011\u000511\u0017BA4\u000e\u0005\u001d\u0011un\u001c7fC:Dq\u0001\u00172\u0002\u0002\u0003\u0007A\u000bC\u0004k\u0001\u0005\u0005I\u0011I6\u0002\u0011!\f7\u000f[\"pI\u0016$\u0012A\u0014\u0005\b[\u0002\t\t\u0011\"\u0011o\u0003\u0019)\u0017/^1mgR\u0011Qm\u001c\u0005\b12\f\t\u00111\u0001U\u000f!\t(!!A\t\u0002\u0011\u0011\u0018\u0001\u0005%pgR$\u0016m]6M_\u000e\fG/[8o!\t\u00112O\u0002\u0005\u0002\u0005\u0005\u0005\t\u0012\u0001\u0003u'\r\u0019X\u000f\u0007\t\u0005mf|2&D\u0001x\u0015\tAX\"A\u0004sk:$\u0018.\\3\n\u0005i<(!E!cgR\u0014\u0018m\u0019;Gk:\u001cG/[8oc!)\u0011f\u001dC\u0001yR\t!\u000fC\u0004/g\u0006\u0005IQ\t@\u0015\u0003\u0011C\u0011\"!\u0001t\u0003\u0003%\t)a\u0001\u0002\u000b\u0005\u0004\b\u000f\\=\u0015\u0007-\n)\u0001C\u0003\u001d\u007f\u0002\u0007q\u0004C\u0005\u0002\nM\f\t\u0011\"!\u0002\f\u00059QO\\1qa2LH\u0003BA\u0007\u0003'\u0001B\u0001DA\b?%\u0019\u0011\u0011C\u0007\u0003\r=\u0003H/[8o\u0011%\t)\"a\u0002\u0002\u0002\u0003\u00071&A\u0002yIAB\u0011\"!\u0007t\u0003\u0003%I!a\u0007\u0002\u0017I,\u0017\r\u001a*fg>dg/\u001a\u000b\u0003\u0003;\u00012!RA\u0010\u0013\r\t\tC\u0012\u0002\u0007\u001f\nTWm\u0019;"
)
public class HostTaskLocation implements TaskLocation, Product, Serializable {
   private final String host;

   public static Option<String> unapply(HostTaskLocation var0) {
      return HostTaskLocation$.MODULE$.unapply(var0);
   }

   public static HostTaskLocation apply(String var0) {
      return HostTaskLocation$.MODULE$.apply(var0);
   }

   public static <A> Function1<String, A> andThen(Function1<HostTaskLocation, A> var0) {
      return HostTaskLocation$.MODULE$.andThen(var0);
   }

   public static <A> Function1<A, HostTaskLocation> compose(Function1<A, String> var0) {
      return HostTaskLocation$.MODULE$.compose(var0);
   }

   public String host() {
      return this.host;
   }

   public String toString() {
      return this.host();
   }

   public HostTaskLocation copy(String host) {
      return new HostTaskLocation(host);
   }

   public String copy$default$1() {
      return this.host();
   }

   public String productPrefix() {
      return "HostTaskLocation";
   }

   public int productArity() {
      return 1;
   }

   public Object productElement(int x$1) {
      switch (x$1) {
         case 0:
            return this.host();
         default:
            throw new IndexOutOfBoundsException(BoxesRunTime.boxToInteger(x$1).toString());
      }
   }

   public Iterator<Object> productIterator() {
      return ScalaRunTime$.MODULE$.<Object>typedProductIterator(this);
   }

   public boolean canEqual(Object x$1) {
      return x$1 instanceof HostTaskLocation;
   }

   public int hashCode() {
      return ScalaRunTime$.MODULE$._hashCode(this);
   }

   public boolean equals(Object x$1) {
      boolean var7;
      if (this != x$1) {
         label52: {
            boolean var3;
            if (x$1 instanceof HostTaskLocation) {
               var3 = true;
            } else {
               var3 = false;
            }

            if (var3) {
               label35: {
                  label34: {
                     HostTaskLocation var4 = (HostTaskLocation)x$1;
                     String var10000 = this.host();
                     String var5 = var4.host();
                     if (var10000 == null) {
                        if (var5 != null) {
                           break label34;
                        }
                     } else if (!var10000.equals(var5)) {
                        break label34;
                     }

                     if (var4.canEqual(this)) {
                        var7 = true;
                        break label35;
                     }
                  }

                  var7 = false;
               }

               if (var7) {
                  break label52;
               }
            }

            var7 = false;
            return var7;
         }
      }

      var7 = true;
      return var7;
   }

   public HostTaskLocation(String host) {
      this.host = host;
      Product$class.$init$(this);
   }
}
