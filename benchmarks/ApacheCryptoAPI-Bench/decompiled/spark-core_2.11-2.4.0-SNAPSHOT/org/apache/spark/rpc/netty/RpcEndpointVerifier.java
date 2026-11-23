package org.apache.spark.rpc.netty;

import org.apache.spark.rpc.RpcAddress;
import org.apache.spark.rpc.RpcCallContext;
import org.apache.spark.rpc.RpcEndpoint;
import org.apache.spark.rpc.RpcEndpoint$class;
import org.apache.spark.rpc.RpcEndpointRef;
import org.apache.spark.rpc.RpcEnv;
import scala.Function1;
import scala.None$;
import scala.Option;
import scala.PartialFunction;
import scala.Product;
import scala.Product$class;
import scala.Serializable;
import scala.Some;
import scala.collection.Iterator;
import scala.reflect.ScalaSignature;
import scala.runtime.AbstractFunction1;
import scala.runtime.BoxedUnit;
import scala.runtime.BoxesRunTime;
import scala.runtime.ScalaRunTime$;

@ScalaSignature(
   bytes = "\u0006\u0001\u0005\u0015e!B\u0001\u0003\u0001\ta!a\u0005*qG\u0016sG\r]8j]R4VM]5gS\u0016\u0014(BA\u0002\u0005\u0003\u0015qW\r\u001e;z\u0015\t)a!A\u0002sa\u000eT!a\u0002\u0005\u0002\u000bM\u0004\u0018M]6\u000b\u0005%Q\u0011AB1qC\u000eDWMC\u0001\f\u0003\ry'oZ\n\u0004\u00015\u0019\u0002C\u0001\b\u0012\u001b\u0005y!\"\u0001\t\u0002\u000bM\u001c\u0017\r\\1\n\u0005Iy!AB!osJ+g\r\u0005\u0002\u0015+5\tA!\u0003\u0002\u0017\t\tY!\u000b]2F]\u0012\u0004x.\u001b8u\u0011!A\u0002A!b\u0001\n\u0003R\u0012A\u0002:qG\u0016sgo\u0001\u0001\u0016\u0003m\u0001\"\u0001\u0006\u000f\n\u0005u!!A\u0002*qG\u0016sg\u000f\u0003\u0005 \u0001\t\u0005\t\u0015!\u0003\u001c\u0003\u001d\u0011\boY#om\u0002B\u0001\"\t\u0001\u0003\u0002\u0003\u0006IAI\u0001\u000bI&\u001c\b/\u0019;dQ\u0016\u0014\bCA\u0012%\u001b\u0005\u0011\u0011BA\u0013\u0003\u0005)!\u0015n\u001d9bi\u000eDWM\u001d\u0005\u0006O\u0001!\t\u0001K\u0001\u0007y%t\u0017\u000e\u001e \u0015\u0007%R3\u0006\u0005\u0002$\u0001!)\u0001D\na\u00017!)\u0011E\na\u0001E!)Q\u0006\u0001C!]\u0005y!/Z2fSZ,\u0017I\u001c3SKBd\u0017\u0010\u0006\u00020qA!a\u0002\r\u001a6\u0013\t\ttBA\bQCJ$\u0018.\u00197Gk:\u001cG/[8o!\tq1'\u0003\u00025\u001f\t\u0019\u0011I\\=\u0011\u000591\u0014BA\u001c\u0010\u0005\u0011)f.\u001b;\t\u000beb\u0003\u0019\u0001\u001e\u0002\u000f\r|g\u000e^3yiB\u0011AcO\u0005\u0003y\u0011\u0011aB\u00159d\u0007\u0006dGnQ8oi\u0016DHo\u0002\u0004?\u0005!\u0005!aP\u0001\u0014%B\u001cWI\u001c3q_&tGOV3sS\u001aLWM\u001d\t\u0003G\u00013a!\u0001\u0002\t\u0002\t\t5C\u0001!\u000e\u0011\u00159\u0003\t\"\u0001D)\u0005y\u0004bB#A\u0005\u0004%\tAR\u0001\u0005\u001d\u0006kU)F\u0001H!\tAU*D\u0001J\u0015\tQ5*\u0001\u0003mC:<'\"\u0001'\u0002\t)\fg/Y\u0005\u0003\u001d&\u0013aa\u0015;sS:<\u0007B\u0002)AA\u0003%q)A\u0003O\u00036+\u0005E\u0002\u0003S\u0001\u0002\u001b&AD\"iK\u000e\\W\t_5ti\u0016t7-Z\n\u0005#6!v\u000b\u0005\u0002\u000f+&\u0011ak\u0004\u0002\b!J|G-^2u!\tq\u0001,\u0003\u0002Z\u001f\ta1+\u001a:jC2L'0\u00192mK\"A1,\u0015BK\u0002\u0013\u0005A,\u0001\u0003oC6,W#A/\u0011\u0005y\u000bgB\u0001\b`\u0013\t\u0001w\"\u0001\u0004Qe\u0016$WMZ\u0005\u0003\u001d\nT!\u0001Y\b\t\u0011\u0011\f&\u0011#Q\u0001\nu\u000bQA\\1nK\u0002BQaJ)\u0005\u0002\u0019$\"aZ5\u0011\u0005!\fV\"\u0001!\t\u000bm+\u0007\u0019A/\t\u000f-\f\u0016\u0011!C\u0001Y\u0006!1m\u001c9z)\t9W\u000eC\u0004\\UB\u0005\t\u0019A/\t\u000f=\f\u0016\u0013!C\u0001a\u0006q1m\u001c9zI\u0011,g-Y;mi\u0012\nT#A9+\u0005u\u00138&A:\u0011\u0005QLX\"A;\u000b\u0005Y<\u0018!C;oG\",7m[3e\u0015\tAx\"\u0001\u0006b]:|G/\u0019;j_:L!A_;\u0003#Ut7\r[3dW\u0016$g+\u0019:jC:\u001cW\rC\u0004}#\u0006\u0005I\u0011\t$\u0002\u001bA\u0014x\u000eZ;diB\u0013XMZ5y\u0011\u001dq\u0018+!A\u0005\u0002}\fA\u0002\u001d:pIV\u001cG/\u0011:jif,\"!!\u0001\u0011\u00079\t\u0019!C\u0002\u0002\u0006=\u00111!\u00138u\u0011%\tI!UA\u0001\n\u0003\tY!\u0001\bqe>$Wo\u0019;FY\u0016lWM\u001c;\u0015\u0007I\ni\u0001\u0003\u0006\u0002\u0010\u0005\u001d\u0011\u0011!a\u0001\u0003\u0003\t1\u0001\u001f\u00132\u0011%\t\u0019\"UA\u0001\n\u0003\n)\"A\bqe>$Wo\u0019;Ji\u0016\u0014\u0018\r^8s+\t\t9\u0002E\u0003\u0002\u001a\u0005}!'\u0004\u0002\u0002\u001c)\u0019\u0011QD\b\u0002\u0015\r|G\u000e\\3di&|g.\u0003\u0003\u0002\"\u0005m!\u0001C%uKJ\fGo\u001c:\t\u0013\u0005\u0015\u0012+!A\u0005\u0002\u0005\u001d\u0012\u0001C2b]\u0016\u000bX/\u00197\u0015\t\u0005%\u0012q\u0006\t\u0004\u001d\u0005-\u0012bAA\u0017\u001f\t9!i\\8mK\u0006t\u0007\"CA\b\u0003G\t\t\u00111\u00013\u0011%\t\u0019$UA\u0001\n\u0003\n)$\u0001\u0005iCND7i\u001c3f)\t\t\t\u0001C\u0005\u0002:E\u000b\t\u0011\"\u0011\u0002<\u0005AAo\\*ue&tw\rF\u0001H\u0011%\ty$UA\u0001\n\u0003\n\t%\u0001\u0004fcV\fGn\u001d\u000b\u0005\u0003S\t\u0019\u0005C\u0005\u0002\u0010\u0005u\u0012\u0011!a\u0001e\u001dI\u0011q\t!\u0002\u0002#\u0005\u0011\u0011J\u0001\u000f\u0007\",7m[#ySN$XM\\2f!\rA\u00171\n\u0004\t%\u0002\u000b\t\u0011#\u0001\u0002NM)\u00111JA(/B1\u0011\u0011KA,;\u001el!!a\u0015\u000b\u0007\u0005Us\"A\u0004sk:$\u0018.\\3\n\t\u0005e\u00131\u000b\u0002\u0012\u0003\n\u001cHO]1di\u001a+hn\u0019;j_:\f\u0004bB\u0014\u0002L\u0011\u0005\u0011Q\f\u000b\u0003\u0003\u0013B!\"!\u000f\u0002L\u0005\u0005IQIA\u001e\u0011)\t\u0019'a\u0013\u0002\u0002\u0013\u0005\u0015QM\u0001\u0006CB\u0004H.\u001f\u000b\u0004O\u0006\u001d\u0004BB.\u0002b\u0001\u0007Q\f\u0003\u0006\u0002l\u0005-\u0013\u0011!CA\u0003[\nq!\u001e8baBd\u0017\u0010\u0006\u0003\u0002p\u0005U\u0004\u0003\u0002\b\u0002ruK1!a\u001d\u0010\u0005\u0019y\u0005\u000f^5p]\"I\u0011qOA5\u0003\u0003\u0005\raZ\u0001\u0004q\u0012\u0002\u0004BCA>\u0003\u0017\n\t\u0011\"\u0003\u0002~\u0005Y!/Z1e%\u0016\u001cx\u000e\u001c<f)\t\ty\bE\u0002I\u0003\u0003K1!a!J\u0005\u0019y%M[3di\u0002"
)
public class RpcEndpointVerifier implements RpcEndpoint {
   private final RpcEnv rpcEnv;
   public final Dispatcher org$apache$spark$rpc$netty$RpcEndpointVerifier$$dispatcher;

   public static String NAME() {
      return RpcEndpointVerifier$.MODULE$.NAME();
   }

   public final RpcEndpointRef self() {
      return RpcEndpoint$class.self(this);
   }

   public PartialFunction<Object, BoxedUnit> receive() {
      return RpcEndpoint$class.receive(this);
   }

   public void onError(Throwable cause) {
      RpcEndpoint$class.onError(this, cause);
   }

   public void onConnected(RpcAddress remoteAddress) {
      RpcEndpoint$class.onConnected(this, remoteAddress);
   }

   public void onDisconnected(RpcAddress remoteAddress) {
      RpcEndpoint$class.onDisconnected(this, remoteAddress);
   }

   public void onNetworkError(Throwable cause, RpcAddress remoteAddress) {
      RpcEndpoint$class.onNetworkError(this, cause, remoteAddress);
   }

   public void onStart() {
      RpcEndpoint$class.onStart(this);
   }

   public void onStop() {
      RpcEndpoint$class.onStop(this);
   }

   public final void stop() {
      RpcEndpoint$class.stop(this);
   }

   public RpcEnv rpcEnv() {
      return this.rpcEnv;
   }

   public PartialFunction<Object, BoxedUnit> receiveAndReply(RpcCallContext context) {
      return new Serializable(this, context) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final RpcEndpointVerifier $outer;
         private final RpcCallContext context$1;

         public final <A1, B1> B1 applyOrElse(A1 x1, Function1<A1, B1> var2) {
            Object var6;
            if (x1 instanceof CheckExistence) {
               CheckExistence var4 = (CheckExistence)x1;
               String name = var4.name();
               this.context$1.reply(BoxesRunTime.boxToBoolean(this.$outer.org$apache$spark$rpc$netty$RpcEndpointVerifier$$dispatcher.verify(name)));
               var6 = BoxedUnit.UNIT;
            } else {
               var6 = var2.apply(x1);
            }

            return (B1)var6;
         }

         public final boolean isDefinedAt(Object x1) {
            boolean var3;
            if (x1 instanceof CheckExistence) {
               var3 = true;
            } else {
               var3 = false;
            }

            return var3;
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
               this.context$1 = context$1;
            }
         }
      };
   }

   public RpcEndpointVerifier(RpcEnv rpcEnv, Dispatcher dispatcher) {
      this.rpcEnv = rpcEnv;
      this.org$apache$spark$rpc$netty$RpcEndpointVerifier$$dispatcher = dispatcher;
      RpcEndpoint$class.$init$(this);
   }

   public static class CheckExistence implements Product, Serializable {
      private final String name;

      public String name() {
         return this.name;
      }

      public CheckExistence copy(String name) {
         return new CheckExistence(name);
      }

      public String copy$default$1() {
         return this.name();
      }

      public String productPrefix() {
         return "CheckExistence";
      }

      public int productArity() {
         return 1;
      }

      public Object productElement(int x$1) {
         switch (x$1) {
            case 0:
               return this.name();
            default:
               throw new IndexOutOfBoundsException(BoxesRunTime.boxToInteger(x$1).toString());
         }
      }

      public Iterator<Object> productIterator() {
         return ScalaRunTime$.MODULE$.<Object>typedProductIterator(this);
      }

      public boolean canEqual(Object x$1) {
         return x$1 instanceof CheckExistence;
      }

      public int hashCode() {
         return ScalaRunTime$.MODULE$._hashCode(this);
      }

      public String toString() {
         return ScalaRunTime$.MODULE$._toString(this);
      }

      public boolean equals(Object x$1) {
         boolean var7;
         if (this != x$1) {
            label52: {
               boolean var3;
               if (x$1 instanceof CheckExistence) {
                  var3 = true;
               } else {
                  var3 = false;
               }

               if (var3) {
                  label35: {
                     label34: {
                        CheckExistence var4 = (CheckExistence)x$1;
                        String var10000 = this.name();
                        String var5 = var4.name();
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

      public CheckExistence(String name) {
         this.name = name;
         Product$class.$init$(this);
      }
   }

   public static class CheckExistence$ extends AbstractFunction1<String, CheckExistence> implements Serializable {
      public static final CheckExistence$ MODULE$;

      static {
         new CheckExistence$();
      }

      public final String toString() {
         return "CheckExistence";
      }

      public CheckExistence apply(String name) {
         return new CheckExistence(name);
      }

      public Option<String> unapply(CheckExistence x$0) {
         return (Option<String>)(x$0 == null ? None$.MODULE$ : new Some(x$0.name()));
      }

      private Object readResolve() {
         return MODULE$;
      }

      public CheckExistence$() {
         MODULE$ = this;
      }
   }
}
