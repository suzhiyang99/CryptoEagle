package org.apache.spark.ui;

import org.spark_project.jetty.server.Server;
import org.spark_project.jetty.server.handler.ContextHandler;
import org.spark_project.jetty.server.handler.ContextHandlerCollection;
import org.spark_project.jetty.util.component.LifeCycle;
import org.spark_project.jetty.util.thread.ThreadPool;
import scala.Function1;
import scala.Option;
import scala.Predef$;
import scala.Product;
import scala.Product$class;
import scala.Serializable;
import scala.Tuple4;
import scala.collection.Iterator;
import scala.reflect.ScalaSignature;
import scala.runtime.BoxesRunTime;
import scala.runtime.ScalaRunTime$;
import scala.runtime.Statics;

@ScalaSignature(
   bytes = "\u0006\u0001\u0005\u0005f!B\u0001\u0003\u0001\u0012Q!AC*feZ,'/\u00138g_*\u00111\u0001B\u0001\u0003k&T!!\u0002\u0004\u0002\u000bM\u0004\u0018M]6\u000b\u0005\u001dA\u0011AB1qC\u000eDWMC\u0001\n\u0003\ry'oZ\n\u0005\u0001-\tB\u0003\u0005\u0002\r\u001f5\tQBC\u0001\u000f\u0003\u0015\u00198-\u00197b\u0013\t\u0001RB\u0001\u0004B]f\u0014VM\u001a\t\u0003\u0019II!aE\u0007\u0003\u000fA\u0013x\u000eZ;diB\u0011A\"F\u0005\u0003-5\u0011AbU3sS\u0006d\u0017N_1cY\u0016D\u0001\u0002\u0007\u0001\u0003\u0016\u0004%\tAG\u0001\u0007g\u0016\u0014h/\u001a:\u0004\u0001U\t1\u0004\u0005\u0002\u001dE5\tQD\u0003\u0002\u0019=)\u0011q\u0004I\u0001\u0006U\u0016$H/\u001f\u0006\u0003C!\tq!Z2mSB\u001cX-\u0003\u0002$;\t11+\u001a:wKJD\u0001\"\n\u0001\u0003\u0012\u0003\u0006IaG\u0001\bg\u0016\u0014h/\u001a:!\u0011!9\u0003A!f\u0001\n\u0003A\u0013!\u00032pk:$\u0007k\u001c:u+\u0005I\u0003C\u0001\u0007+\u0013\tYSBA\u0002J]RD\u0001\"\f\u0001\u0003\u0012\u0003\u0006I!K\u0001\u000bE>,h\u000e\u001a)peR\u0004\u0003\u0002C\u0018\u0001\u0005+\u0007I\u0011\u0001\u0019\u0002\u0015M,7-\u001e:f!>\u0014H/F\u00012!\ra!'K\u0005\u0003g5\u0011aa\u00149uS>t\u0007\u0002C\u001b\u0001\u0005#\u0005\u000b\u0011B\u0019\u0002\u0017M,7-\u001e:f!>\u0014H\u000f\t\u0005\to\u0001\u0011)\u0019!C\u0005q\u0005Y!o\\8u\u0011\u0006tG\r\\3s+\u0005I\u0004C\u0001\u001e>\u001b\u0005Y$B\u0001\u001f\u001e\u0003\u001dA\u0017M\u001c3mKJL!AP\u001e\u00031\r{g\u000e^3yi\"\u000bg\u000e\u001a7fe\u000e{G\u000e\\3di&|g\u000e\u0003\u0005A\u0001\tE\t\u0015!\u0003:\u00031\u0011xn\u001c;IC:$G.\u001a:!\u0011\u0015\u0011\u0005\u0001\"\u0001D\u0003\u0019a\u0014N\\5u}Q)AIR$I\u0013B\u0011Q\tA\u0007\u0002\u0005!)\u0001$\u0011a\u00017!)q%\u0011a\u0001S!)q&\u0011a\u0001c!)q'\u0011a\u0001s!)1\n\u0001C\u0001\u0019\u0006Q\u0011\r\u001a3IC:$G.\u001a:\u0015\u00055\u0003\u0006C\u0001\u0007O\u0013\tyUB\u0001\u0003V]&$\b\"\u0002\u001fK\u0001\u0004\t\u0006C\u0001\u001eS\u0013\t\u00196H\u0001\bD_:$X\r\u001f;IC:$G.\u001a:\t\u000bU\u0003A\u0011\u0001,\u0002\u001bI,Wn\u001c<f\u0011\u0006tG\r\\3s)\tiu\u000bC\u0003=)\u0002\u0007\u0011\u000bC\u0003Z\u0001\u0011\u0005!,\u0001\u0003ti>\u0004H#A'\t\u000fq\u0003\u0011\u0011!C\u0001;\u0006!1m\u001c9z)\u0015!el\u00181b\u0011\u001dA2\f%AA\u0002mAqaJ.\u0011\u0002\u0003\u0007\u0011\u0006C\u000407B\u0005\t\u0019A\u0019\t\u000f]Z\u0006\u0013!a\u0001s!91\rAI\u0001\n\u0003!\u0017AD2paf$C-\u001a4bk2$H%M\u000b\u0002K*\u00121DZ\u0016\u0002OB\u0011\u0001.\\\u0007\u0002S*\u0011!n[\u0001\nk:\u001c\u0007.Z2lK\u0012T!\u0001\\\u0007\u0002\u0015\u0005tgn\u001c;bi&|g.\u0003\u0002oS\n\tRO\\2iK\u000e\\W\r\u001a,be&\fgnY3\t\u000fA\u0004\u0011\u0013!C\u0001c\u0006q1m\u001c9zI\u0011,g-Y;mi\u0012\u0012T#\u0001:+\u0005%2\u0007b\u0002;\u0001#\u0003%\t!^\u0001\u000fG>\u0004\u0018\u0010\n3fM\u0006,H\u000e\u001e\u00134+\u00051(FA\u0019g\u0011\u001dA\b!%A\u0005\u0002e\fabY8qs\u0012\"WMZ1vYR$C'F\u0001{U\tId\rC\u0004}\u0001-\u0005I\u0011\u0001\u001d\u0002\u001bI|w\u000e\u001e%b]\u0012dWM\u001d\u00132\u0011\u001dq\b!!A\u0005B}\fQ\u0002\u001d:pIV\u001cG\u000f\u0015:fM&DXCAA\u0001!\u0011\t\u0019!!\u0004\u000e\u0005\u0005\u0015!\u0002BA\u0004\u0003\u0013\tA\u0001\\1oO*\u0011\u00111B\u0001\u0005U\u00064\u0018-\u0003\u0003\u0002\u0010\u0005\u0015!AB*ue&tw\r\u0003\u0005\u0002\u0014\u0001\t\t\u0011\"\u0001)\u00031\u0001(o\u001c3vGR\f%/\u001b;z\u0011%\t9\u0002AA\u0001\n\u0003\tI\"\u0001\bqe>$Wo\u0019;FY\u0016lWM\u001c;\u0015\t\u0005m\u0011\u0011\u0005\t\u0004\u0019\u0005u\u0011bAA\u0010\u001b\t\u0019\u0011I\\=\t\u0013\u0005\r\u0012QCA\u0001\u0002\u0004I\u0013a\u0001=%c!I\u0011q\u0005\u0001\u0002\u0002\u0013\u0005\u0013\u0011F\u0001\u0010aJ|G-^2u\u0013R,'/\u0019;peV\u0011\u00111\u0006\t\u0007\u0003[\t\u0019$a\u0007\u000e\u0005\u0005=\"bAA\u0019\u001b\u0005Q1m\u001c7mK\u000e$\u0018n\u001c8\n\t\u0005U\u0012q\u0006\u0002\t\u0013R,'/\u0019;pe\"I\u0011\u0011\b\u0001\u0002\u0002\u0013\u0005\u00111H\u0001\tG\u0006tW)];bYR!\u0011QHA\"!\ra\u0011qH\u0005\u0004\u0003\u0003j!a\u0002\"p_2,\u0017M\u001c\u0005\u000b\u0003G\t9$!AA\u0002\u0005m\u0001\"CA$\u0001\u0005\u0005I\u0011IA%\u0003!A\u0017m\u001d5D_\u0012,G#A\u0015\t\u0013\u00055\u0003!!A\u0005B\u0005=\u0013\u0001\u0003;p'R\u0014\u0018N\\4\u0015\u0005\u0005\u0005\u0001\"CA*\u0001\u0005\u0005I\u0011IA+\u0003\u0019)\u0017/^1mgR!\u0011QHA,\u0011)\t\u0019#!\u0015\u0002\u0002\u0003\u0007\u00111D\u0004\u000b\u00037\u0012\u0011\u0011!E\u0001\t\u0005u\u0013AC*feZ,'/\u00138g_B\u0019Q)a\u0018\u0007\u0013\u0005\u0011\u0011\u0011!E\u0001\t\u0005\u00054#BA0\u0003G\"\u0002#CA3\u0003WZ\u0012&M\u001dE\u001b\t\t9GC\u0002\u0002j5\tqA];oi&lW-\u0003\u0003\u0002n\u0005\u001d$!E!cgR\u0014\u0018m\u0019;Gk:\u001cG/[8oi!9!)a\u0018\u0005\u0002\u0005EDCAA/\u0011)\ti%a\u0018\u0002\u0002\u0013\u0015\u0013q\n\u0005\u000b\u0003o\ny&!A\u0005\u0002\u0006e\u0014!B1qa2LH#\u0003#\u0002|\u0005u\u0014qPAA\u0011\u0019A\u0012Q\u000fa\u00017!1q%!\u001eA\u0002%BaaLA;\u0001\u0004\t\u0004BB\u001c\u0002v\u0001\u0007\u0011\b\u0003\u0006\u0002\u0006\u0006}\u0013\u0011!CA\u0003\u000f\u000bq!\u001e8baBd\u0017\u0010\u0006\u0003\u0002\n\u0006E\u0005\u0003\u0002\u00073\u0003\u0017\u0003r\u0001DAG7%\n\u0014(C\u0002\u0002\u00106\u0011a\u0001V;qY\u0016$\u0004\"CAJ\u0003\u0007\u000b\t\u00111\u0001E\u0003\rAH\u0005\r\u0005\u000b\u0003/\u000by&!A\u0005\n\u0005e\u0015a\u0003:fC\u0012\u0014Vm]8mm\u0016$\"!a'\u0011\t\u0005\r\u0011QT\u0005\u0005\u0003?\u000b)A\u0001\u0004PE*,7\r\u001e"
)
public class ServerInfo implements Product, Serializable {
   private final Server server;
   private final int boundPort;
   private final Option<Object> securePort;
   private final ContextHandlerCollection org$apache$spark$ui$ServerInfo$$rootHandler;

   public static Option<Tuple4<Server, Object, Option<Object>, ContextHandlerCollection>> unapply(ServerInfo var0) {
      return ServerInfo$.MODULE$.unapply(var0);
   }

   public static ServerInfo apply(Server var0, int var1, Option<Object> var2, ContextHandlerCollection var3) {
      return ServerInfo$.MODULE$.apply(var0, var1, var2, var3);
   }

   public static Function1<Tuple4<Server, Object, Option<Object>, ContextHandlerCollection>, ServerInfo> tupled() {
      return ServerInfo$.MODULE$.tupled();
   }

   public static Function1<Server, Function1<Object, Function1<Option<Object>, Function1<ContextHandlerCollection, ServerInfo>>>> curried() {
      return ServerInfo$.MODULE$.curried();
   }

   public ContextHandlerCollection rootHandler$1() {
      return this.org$apache$spark$ui$ServerInfo$$rootHandler;
   }

   public Server server() {
      return this.server;
   }

   public int boundPort() {
      return this.boundPort;
   }

   public Option<Object> securePort() {
      return this.securePort;
   }

   public ContextHandlerCollection org$apache$spark$ui$ServerInfo$$rootHandler() {
      return this.org$apache$spark$ui$ServerInfo$$rootHandler;
   }

   public void addHandler(ContextHandler handler) {
      handler.setVirtualHosts(JettyUtils$.MODULE$.toVirtualHosts(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{JettyUtils$.MODULE$.SPARK_CONNECTOR_NAME()}))));
      this.org$apache$spark$ui$ServerInfo$$rootHandler().addHandler(handler);
      if (!handler.isStarted()) {
         handler.start();
      }

   }

   public void removeHandler(ContextHandler handler) {
      this.org$apache$spark$ui$ServerInfo$$rootHandler().removeHandler(handler);
      if (handler.isStarted()) {
         handler.stop();
      }

   }

   public void stop() {
      this.server().stop();
      ThreadPool threadPool = this.server().getThreadPool();
      if (threadPool != null && threadPool instanceof LifeCycle) {
         ((LifeCycle)threadPool).stop();
      }

   }

   public ServerInfo copy(Server server, int boundPort, Option<Object> securePort, ContextHandlerCollection rootHandler) {
      return new ServerInfo(server, boundPort, securePort, rootHandler);
   }

   public Server copy$default$1() {
      return this.server();
   }

   public int copy$default$2() {
      return this.boundPort();
   }

   public Option<Object> copy$default$3() {
      return this.securePort();
   }

   public ContextHandlerCollection copy$default$4() {
      return this.org$apache$spark$ui$ServerInfo$$rootHandler();
   }

   public String productPrefix() {
      return "ServerInfo";
   }

   public int productArity() {
      return 4;
   }

   public Object productElement(int x$1) {
      Object var10000;
      switch (x$1) {
         case 0:
            var10000 = this.server();
            break;
         case 1:
            var10000 = BoxesRunTime.boxToInteger(this.boundPort());
            break;
         case 2:
            var10000 = this.securePort();
            break;
         case 3:
            var10000 = this.rootHandler$1();
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
      return x$1 instanceof ServerInfo;
   }

   public int hashCode() {
      int var1 = -889275714;
      var1 = Statics.mix(var1, Statics.anyHash(this.server()));
      var1 = Statics.mix(var1, this.boundPort());
      var1 = Statics.mix(var1, Statics.anyHash(this.securePort()));
      var1 = Statics.mix(var1, Statics.anyHash(this.rootHandler$1()));
      return Statics.finalizeHash(var1, 4);
   }

   public String toString() {
      return ScalaRunTime$.MODULE$._toString(this);
   }

   public boolean equals(Object x$1) {
      boolean var11;
      if (this != x$1) {
         label73: {
            boolean var3;
            if (x$1 instanceof ServerInfo) {
               var3 = true;
            } else {
               var3 = false;
            }

            if (var3) {
               label55: {
                  label54: {
                     ServerInfo var4 = (ServerInfo)x$1;
                     Server var10000 = this.server();
                     Server var5 = var4.server();
                     if (var10000 == null) {
                        if (var5 != null) {
                           break label54;
                        }
                     } else if (!var10000.equals(var5)) {
                        break label54;
                     }

                     if (this.boundPort() == var4.boundPort()) {
                        label64: {
                           Option var8 = this.securePort();
                           Option var6 = var4.securePort();
                           if (var8 == null) {
                              if (var6 != null) {
                                 break label64;
                              }
                           } else if (!var8.equals(var6)) {
                              break label64;
                           }

                           ContextHandlerCollection var9 = this.rootHandler$1();
                           ContextHandlerCollection var7 = var4.rootHandler$1();
                           if (var9 == null) {
                              if (var7 != null) {
                                 break label64;
                              }
                           } else if (!var9.equals(var7)) {
                              break label64;
                           }

                           if (var4.canEqual(this)) {
                              var11 = true;
                              break label55;
                           }
                        }
                     }
                  }

                  var11 = false;
               }

               if (var11) {
                  break label73;
               }
            }

            var11 = false;
            return var11;
         }
      }

      var11 = true;
      return var11;
   }

   public ServerInfo(Server server, int boundPort, Option<Object> securePort, ContextHandlerCollection rootHandler) {
      this.server = server;
      this.boundPort = boundPort;
      this.securePort = securePort;
      this.org$apache$spark$ui$ServerInfo$$rootHandler = rootHandler;
      Product$class.$init$(this);
   }
}
