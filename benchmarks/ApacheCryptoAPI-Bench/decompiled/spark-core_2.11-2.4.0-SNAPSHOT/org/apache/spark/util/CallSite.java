package org.apache.spark.util;

import scala.Option;
import scala.Product;
import scala.Product$class;
import scala.Serializable;
import scala.Tuple2;
import scala.collection.Iterator;
import scala.reflect.ScalaSignature;
import scala.runtime.BoxesRunTime;
import scala.runtime.ScalaRunTime$;

@ScalaSignature(
   bytes = "\u0006\u0001\u0005}b!B\u0001\u0003\u0001\u0012Q!\u0001C\"bY2\u001c\u0016\u000e^3\u000b\u0005\r!\u0011\u0001B;uS2T!!\u0002\u0004\u0002\u000bM\u0004\u0018M]6\u000b\u0005\u001dA\u0011AB1qC\u000eDWMC\u0001\n\u0003\ry'oZ\n\u0005\u0001-\tB\u0003\u0005\u0002\r\u001f5\tQBC\u0001\u000f\u0003\u0015\u00198-\u00197b\u0013\t\u0001RB\u0001\u0004B]f\u0014VM\u001a\t\u0003\u0019II!aE\u0007\u0003\u000fA\u0013x\u000eZ;diB\u0011A\"F\u0005\u0003-5\u0011AbU3sS\u0006d\u0017N_1cY\u0016D\u0001\u0002\u0007\u0001\u0003\u0016\u0004%\tAG\u0001\ng\"|'\u000f\u001e$pe6\u001c\u0001!F\u0001\u001c!\tarD\u0004\u0002\r;%\u0011a$D\u0001\u0007!J,G-\u001a4\n\u0005\u0001\n#AB*ue&twM\u0003\u0002\u001f\u001b!A1\u0005\u0001B\tB\u0003%1$\u0001\u0006tQ>\u0014HOR8s[\u0002B\u0001\"\n\u0001\u0003\u0016\u0004%\tAG\u0001\tY>twMR8s[\"Aq\u0005\u0001B\tB\u0003%1$A\u0005m_:<gi\u001c:nA!)\u0011\u0006\u0001C\u0001U\u00051A(\u001b8jiz\"2aK\u0017/!\ta\u0003!D\u0001\u0003\u0011\u0015A\u0002\u00061\u0001\u001c\u0011\u0015)\u0003\u00061\u0001\u001c\u0011\u001d\u0001\u0004!!A\u0005\u0002E\nAaY8qsR\u00191FM\u001a\t\u000fay\u0003\u0013!a\u00017!9Qe\fI\u0001\u0002\u0004Y\u0002bB\u001b\u0001#\u0003%\tAN\u0001\u000fG>\u0004\u0018\u0010\n3fM\u0006,H\u000e\u001e\u00132+\u00059$FA\u000e9W\u0005I\u0004C\u0001\u001e@\u001b\u0005Y$B\u0001\u001f>\u0003%)hn\u00195fG.,GM\u0003\u0002?\u001b\u0005Q\u0011M\u001c8pi\u0006$\u0018n\u001c8\n\u0005\u0001[$!E;oG\",7m[3e-\u0006\u0014\u0018.\u00198dK\"9!\tAI\u0001\n\u00031\u0014AD2paf$C-\u001a4bk2$HE\r\u0005\b\t\u0002\t\t\u0011\"\u0011F\u00035\u0001(o\u001c3vGR\u0004&/\u001a4jqV\ta\t\u0005\u0002H\u00196\t\u0001J\u0003\u0002J\u0015\u0006!A.\u00198h\u0015\u0005Y\u0015\u0001\u00026bm\u0006L!\u0001\t%\t\u000f9\u0003\u0011\u0011!C\u0001\u001f\u0006a\u0001O]8ek\u000e$\u0018I]5usV\t\u0001\u000b\u0005\u0002\r#&\u0011!+\u0004\u0002\u0004\u0013:$\bb\u0002+\u0001\u0003\u0003%\t!V\u0001\u000faJ|G-^2u\u000b2,W.\u001a8u)\t1\u0016\f\u0005\u0002\r/&\u0011\u0001,\u0004\u0002\u0004\u0003:L\bb\u0002.T\u0003\u0003\u0005\r\u0001U\u0001\u0004q\u0012\n\u0004b\u0002/\u0001\u0003\u0003%\t%X\u0001\u0010aJ|G-^2u\u0013R,'/\u0019;peV\ta\fE\u0002`EZk\u0011\u0001\u0019\u0006\u0003C6\t!bY8mY\u0016\u001cG/[8o\u0013\t\u0019\u0007M\u0001\u0005Ji\u0016\u0014\u0018\r^8s\u0011\u001d)\u0007!!A\u0005\u0002\u0019\f\u0001bY1o\u000bF,\u0018\r\u001c\u000b\u0003O*\u0004\"\u0001\u00045\n\u0005%l!a\u0002\"p_2,\u0017M\u001c\u0005\b5\u0012\f\t\u00111\u0001W\u0011\u001da\u0007!!A\u0005B5\f\u0001\u0002[1tQ\u000e{G-\u001a\u000b\u0002!\"9q\u000eAA\u0001\n\u0003\u0002\u0018\u0001\u0003;p'R\u0014\u0018N\\4\u0015\u0003\u0019CqA\u001d\u0001\u0002\u0002\u0013\u00053/\u0001\u0004fcV\fGn\u001d\u000b\u0003ORDqAW9\u0002\u0002\u0003\u0007ak\u0002\u0004w\u0005!\u0005Aa^\u0001\t\u0007\u0006dGnU5uKB\u0011A\u0006\u001f\u0004\u0007\u0003\tA\t\u0001B=\u0014\u0007a\\A\u0003C\u0003*q\u0012\u00051\u0010F\u0001x\u0011\u001di\bP1A\u0005\u0002\u0015\u000b!b\u0015%P%R{fi\u0014*N\u0011\u0019y\b\u0010)A\u0005\r\u0006Y1\u000bS(S)~3uJU'!\u0011!\t\u0019\u0001\u001fb\u0001\n\u0003)\u0015!\u0003'P\u001d\u001e{fi\u0014*N\u0011\u001d\t9\u0001\u001fQ\u0001\n\u0019\u000b!\u0002T(O\u000f~3uJU'!\u0011%\tY\u0001\u001fb\u0001\n\u0003\ti!A\u0003f[B$\u00180F\u0001,\u0011\u001d\t\t\u0002\u001fQ\u0001\n-\na!Z7qif\u0004\u0003\"CA\u000bq\u0006\u0005I\u0011QA\f\u0003\u0015\t\u0007\u000f\u001d7z)\u0015Y\u0013\u0011DA\u000e\u0011\u0019A\u00121\u0003a\u00017!1Q%a\u0005A\u0002mA\u0011\"a\by\u0003\u0003%\t)!\t\u0002\u000fUt\u0017\r\u001d9msR!\u00111EA\u0018!\u0015a\u0011QEA\u0015\u0013\r\t9#\u0004\u0002\u0007\u001fB$\u0018n\u001c8\u0011\u000b1\tYcG\u000e\n\u0007\u00055RB\u0001\u0004UkBdWM\r\u0005\n\u0003c\ti\"!AA\u0002-\n1\u0001\u001f\u00131\u0011%\t)\u0004_A\u0001\n\u0013\t9$A\u0006sK\u0006$'+Z:pYZ,GCAA\u001d!\r9\u00151H\u0005\u0004\u0003{A%AB(cU\u0016\u001cG\u000f"
)
public class CallSite implements Product, Serializable {
   private final String shortForm;
   private final String longForm;

   public static Option<Tuple2<String, String>> unapply(CallSite var0) {
      return CallSite$.MODULE$.unapply(var0);
   }

   public static CallSite apply(String var0, String var1) {
      return CallSite$.MODULE$.apply(var0, var1);
   }

   public static CallSite empty() {
      return CallSite$.MODULE$.empty();
   }

   public static String LONG_FORM() {
      return CallSite$.MODULE$.LONG_FORM();
   }

   public static String SHORT_FORM() {
      return CallSite$.MODULE$.SHORT_FORM();
   }

   public String shortForm() {
      return this.shortForm;
   }

   public String longForm() {
      return this.longForm;
   }

   public CallSite copy(String shortForm, String longForm) {
      return new CallSite(shortForm, longForm);
   }

   public String copy$default$1() {
      return this.shortForm();
   }

   public String copy$default$2() {
      return this.longForm();
   }

   public String productPrefix() {
      return "CallSite";
   }

   public int productArity() {
      return 2;
   }

   public Object productElement(int x$1) {
      String var10000;
      switch (x$1) {
         case 0:
            var10000 = this.shortForm();
            break;
         case 1:
            var10000 = this.longForm();
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
      return x$1 instanceof CallSite;
   }

   public int hashCode() {
      return ScalaRunTime$.MODULE$._hashCode(this);
   }

   public String toString() {
      return ScalaRunTime$.MODULE$._toString(this);
   }

   public boolean equals(Object x$1) {
      boolean var9;
      if (this != x$1) {
         label62: {
            boolean var3;
            if (x$1 instanceof CallSite) {
               var3 = true;
            } else {
               var3 = false;
            }

            if (var3) {
               label44: {
                  label53: {
                     CallSite var4 = (CallSite)x$1;
                     String var10000 = this.shortForm();
                     String var5 = var4.shortForm();
                     if (var10000 == null) {
                        if (var5 != null) {
                           break label53;
                        }
                     } else if (!var10000.equals(var5)) {
                        break label53;
                     }

                     var10000 = this.longForm();
                     String var6 = var4.longForm();
                     if (var10000 == null) {
                        if (var6 != null) {
                           break label53;
                        }
                     } else if (!var10000.equals(var6)) {
                        break label53;
                     }

                     if (var4.canEqual(this)) {
                        var9 = true;
                        break label44;
                     }
                  }

                  var9 = false;
               }

               if (var9) {
                  break label62;
               }
            }

            var9 = false;
            return var9;
         }
      }

      var9 = true;
      return var9;
   }

   public CallSite(String shortForm, String longForm) {
      this.shortForm = shortForm;
      this.longForm = longForm;
      Product$class.$init$(this);
   }
}
