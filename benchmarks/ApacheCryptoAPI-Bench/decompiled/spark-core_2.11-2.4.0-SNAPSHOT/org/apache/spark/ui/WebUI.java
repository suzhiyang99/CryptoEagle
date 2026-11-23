package org.apache.spark.ui;

import javax.servlet.http.HttpServletRequest;
import org.apache.spark.SSLOptions;
import org.apache.spark.SecurityManager;
import org.apache.spark.SparkConf;
import org.apache.spark.internal.Logging;
import org.apache.spark.internal.Logging$class;
import org.apache.spark.internal.config.package$;
import org.apache.spark.util.Utils$;
import org.json4s.JsonAST;
import org.slf4j.Logger;
import org.spark_project.jetty.servlet.ServletContextHandler;
import scala.Function0;
import scala.None$;
import scala.Option;
import scala.Option$;
import scala.Predef$;
import scala.Serializable;
import scala.Some;
import scala.StringContext;
import scala.collection.Seq;
import scala.collection.immutable.Nil$;
import scala.collection.immutable.StringOps;
import scala.collection.mutable.ArrayBuffer;
import scala.collection.mutable.ArrayBuffer$;
import scala.collection.mutable.HashMap;
import scala.collection.mutable.StringBuilder;
import scala.reflect.ScalaSignature;
import scala.runtime.BoxesRunTime;
import scala.xml.Node;

@ScalaSignature(
   bytes = "\u0006\u0001\u0005}gAB\u0001\u0003\u0003\u0003!!BA\u0003XK\n,\u0016J\u0003\u0002\u0004\t\u0005\u0011Q/\u001b\u0006\u0003\u000b\u0019\tQa\u001d9be.T!a\u0002\u0005\u0002\r\u0005\u0004\u0018m\u00195f\u0015\u0005I\u0011aA8sON\u0019\u0001aC\t\u0011\u00051yQ\"A\u0007\u000b\u00039\tQa]2bY\u0006L!\u0001E\u0007\u0003\r\u0005s\u0017PU3g!\t\u0011R#D\u0001\u0014\u0015\t!B!\u0001\u0005j]R,'O\\1m\u0013\t12CA\u0004M_\u001e<\u0017N\\4\t\u0011a\u0001!Q1A\u0005\u0002i\tqb]3dkJLG/_'b]\u0006<WM]\u0002\u0001+\u0005Y\u0002C\u0001\u000f\u001e\u001b\u0005!\u0011B\u0001\u0010\u0005\u0005=\u0019VmY;sSRLX*\u00198bO\u0016\u0014\b\u0002\u0003\u0011\u0001\u0005\u0003\u0005\u000b\u0011B\u000e\u0002!M,7-\u001e:jifl\u0015M\\1hKJ\u0004\u0003\u0002\u0003\u0012\u0001\u0005\u000b\u0007I\u0011A\u0012\u0002\u0015M\u001cHn\u00149uS>t7/F\u0001%!\taR%\u0003\u0002'\t\tQ1k\u0015'PaRLwN\\:\t\u0011!\u0002!\u0011!Q\u0001\n\u0011\n1b]:m\u001fB$\u0018n\u001c8tA!A!\u0006\u0001B\u0001B\u0003%1&\u0001\u0003q_J$\bC\u0001\u0007-\u0013\tiSBA\u0002J]RD\u0001b\f\u0001\u0003\u0002\u0003\u0006I\u0001M\u0001\u0005G>tg\r\u0005\u0002\u001dc%\u0011!\u0007\u0002\u0002\n'B\f'o[\"p]\u001aD\u0001\u0002\u000e\u0001\u0003\u0002\u0003\u0006I!N\u0001\tE\u0006\u001cX\rU1uQB\u0011a'\u000f\b\u0003\u0019]J!\u0001O\u0007\u0002\rA\u0013X\rZ3g\u0013\tQ4H\u0001\u0004TiJLgn\u001a\u0006\u0003q5A\u0001\"\u0010\u0001\u0003\u0002\u0003\u0006I!N\u0001\u0005]\u0006lW\rC\u0003@\u0001\u0011\u0005\u0001)\u0001\u0004=S:LGO\u0010\u000b\b\u0003\u000e#UIR$I!\t\u0011\u0005!D\u0001\u0003\u0011\u0015Ab\b1\u0001\u001c\u0011\u0015\u0011c\b1\u0001%\u0011\u0015Qc\b1\u0001,\u0011\u0015yc\b1\u00011\u0011\u001d!d\b%AA\u0002UBq!\u0010 \u0011\u0002\u0003\u0007Q\u0007C\u0004K\u0001\t\u0007I\u0011C&\u0002\tQ\f'm]\u000b\u0002\u0019B\u0019QJ\u0015+\u000e\u00039S!a\u0014)\u0002\u000f5,H/\u00192mK*\u0011\u0011+D\u0001\u000bG>dG.Z2uS>t\u0017BA*O\u0005-\t%O]1z\u0005V4g-\u001a:\u0011\u0005\t+\u0016B\u0001,\u0003\u0005!9VMY+J)\u0006\u0014\u0007B\u0002-\u0001A\u0003%A*A\u0003uC\n\u001c\b\u0005C\u0004[\u0001\t\u0007I\u0011C.\u0002\u0011!\fg\u000e\u001a7feN,\u0012\u0001\u0018\t\u0004\u001bJk\u0006C\u00010f\u001b\u0005y&B\u00011b\u0003\u001d\u0019XM\u001d<mKRT!AY2\u0002\u000b),G\u000f^=\u000b\u0005\u0011D\u0011aB3dY&\u00048/Z\u0005\u0003M~\u0013QcU3sm2,GoQ8oi\u0016DH\u000fS1oI2,'\u000f\u0003\u0004i\u0001\u0001\u0006I\u0001X\u0001\nQ\u0006tG\r\\3sg\u0002BqA\u001b\u0001C\u0002\u0013E1.\u0001\bqC\u001e,Gk\u001c%b]\u0012dWM]:\u0016\u00031\u0004B!T7p9&\u0011aN\u0014\u0002\b\u0011\u0006\u001c\b.T1q!\t\u0011\u0005/\u0003\u0002r\u0005\tIq+\u001a2V\u0013B\u000bw-\u001a\u0005\u0007g\u0002\u0001\u000b\u0011\u00027\u0002\u001fA\fw-\u001a+p\u0011\u0006tG\r\\3sg\u0002Bq!\u001e\u0001A\u0002\u0013Ea/\u0001\u0006tKJ4XM]%oM>,\u0012a\u001e\t\u0004\u0019aT\u0018BA=\u000e\u0005\u0019y\u0005\u000f^5p]B\u0011!i_\u0005\u0003y\n\u0011!bU3sm\u0016\u0014\u0018J\u001c4p\u0011\u001dq\b\u00011A\u0005\u0012}\fab]3sm\u0016\u0014\u0018J\u001c4p?\u0012*\u0017\u000f\u0006\u0003\u0002\u0002\u0005\u001d\u0001c\u0001\u0007\u0002\u0004%\u0019\u0011QA\u0007\u0003\tUs\u0017\u000e\u001e\u0005\t\u0003\u0013i\u0018\u0011!a\u0001o\u0006\u0019\u0001\u0010J\u0019\t\u000f\u00055\u0001\u0001)Q\u0005o\u0006Y1/\u001a:wKJLeNZ8!\u0011%\t\t\u0002\u0001b\u0001\n#\t\u0019\"\u0001\bqk\nd\u0017n\u0019%pgRt\u0015-\\3\u0016\u0003UBq!a\u0006\u0001A\u0003%Q'A\bqk\nd\u0017n\u0019%pgRt\u0015-\\3!\u0011%\tY\u0002\u0001b\u0001\n\u0013\t\u0019\"A\u0005dY\u0006\u001c8OT1nK\"9\u0011q\u0004\u0001!\u0002\u0013)\u0014AC2mCN\u001ch*Y7fA!9\u00111\u0005\u0001\u0005\u0002\u0005M\u0011aC4fi\n\u000b7/\u001a)bi\"Dq!a\n\u0001\t\u0003\tI#A\u0004hKR$\u0016MY:\u0016\u0005\u0005-\u0002#BA\u0017\u0003{!f\u0002BA\u0018\u0003sqA!!\r\u000285\u0011\u00111\u0007\u0006\u0004\u0003kI\u0012A\u0002\u001fs_>$h(C\u0001\u000f\u0013\r\tY$D\u0001\ba\u0006\u001c7.Y4f\u0013\u0011\ty$!\u0011\u0003\u0007M+\u0017OC\u0002\u0002<5Aq!!\u0012\u0001\t\u0003\t9%A\u0006hKRD\u0015M\u001c3mKJ\u001cXCAA%!\u0015\ti#!\u0010^\u0011\u0019\ti\u0005\u0001C\u00015\u0005\u0011r-\u001a;TK\u000e,(/\u001b;z\u001b\u0006t\u0017mZ3s\u0011\u001d\t\t\u0006\u0001C\u0001\u0003'\n\u0011\"\u0019;uC\u000eDG+\u00192\u0015\t\u0005\u0005\u0011Q\u000b\u0005\b\u0003/\ny\u00051\u0001U\u0003\r!\u0018M\u0019\u0005\b\u00037\u0002A\u0011AA/\u0003%!W\r^1dQR\u000b'\r\u0006\u0003\u0002\u0002\u0005}\u0003bBA,\u00033\u0002\r\u0001\u0016\u0005\b\u0003G\u0002A\u0011AA3\u0003)!W\r^1dQB\u000bw-\u001a\u000b\u0005\u0003\u0003\t9\u0007C\u0004\u0002j\u0005\u0005\u0004\u0019A8\u0002\tA\fw-\u001a\u0005\b\u0003[\u0002A\u0011AA8\u0003)\tG\u000f^1dQB\u000bw-\u001a\u000b\u0005\u0003\u0003\t\t\bC\u0004\u0002j\u0005-\u0004\u0019A8\t\u000f\u0005U\u0004\u0001\"\u0001\u0002x\u0005i\u0011\r\u001e;bG\"D\u0015M\u001c3mKJ$B!!\u0001\u0002z!9\u00111PA:\u0001\u0004i\u0016a\u00025b]\u0012dWM\u001d\u0005\b\u0003\u007f\u0002A\u0011AAA\u00035!W\r^1dQ\"\u000bg\u000e\u001a7feR!\u0011\u0011AAB\u0011\u001d\tY(! A\u0002uCq!a\"\u0001\t\u0003\tI)\u0001\tbI\u0012\u001cF/\u0019;jG\"\u000bg\u000e\u001a7feR1\u0011\u0011AAF\u0003\u001fCq!!$\u0002\u0006\u0002\u0007Q'\u0001\u0007sKN|WO]2f\u0005\u0006\u001cX\rC\u0004\u0002\u0012\u0006\u0015\u0005\u0019A\u001b\u0002\tA\fG\u000f\u001b\u0005\b\u0003+\u0003A\u0011AAL\u0003M\u0011X-\\8wKN#\u0018\r^5d\u0011\u0006tG\r\\3s)\u0011\t\t!!'\t\u000f\u0005E\u00151\u0013a\u0001k!9\u0011Q\u0014\u0001\u0007\u0002\u0005}\u0015AC5oSRL\u0017\r\\5{KR\u0011\u0011\u0011\u0001\u0005\b\u0003G\u0003A\u0011AAP\u0003\u0011\u0011\u0017N\u001c3\t\u000f\u0005\u001d\u0006\u0001\"\u0001\u0002\u0014\u00051q/\u001a2Ve2Dq!a+\u0001\t\u0003\ti+A\u0005c_VtG\rU8siV\t1\u0006C\u0004\u00022\u0002!\t!a(\u0002\tM$x\u000e]\u0004\u000b\u0003k\u0013\u0011\u0011!E\u0001\t\u0005]\u0016!B,fEVK\u0005c\u0001\"\u0002:\u001aI\u0011AAA\u0001\u0012\u0003!\u00111X\n\u0004\u0003s[\u0001bB \u0002:\u0012\u0005\u0011q\u0018\u000b\u0003\u0003oC!\"a1\u0002:F\u0005I\u0011AAc\u0003m!C.Z:tS:LG\u000fJ4sK\u0006$XM\u001d\u0013eK\u001a\fW\u000f\u001c;%kU\u0011\u0011q\u0019\u0016\u0004k\u0005%7FAAf!\u0011\ti-a6\u000e\u0005\u0005='\u0002BAi\u0003'\f\u0011\"\u001e8dQ\u0016\u001c7.\u001a3\u000b\u0007\u0005UW\"\u0001\u0006b]:|G/\u0019;j_:LA!!7\u0002P\n\tRO\\2iK\u000e\\W\r\u001a,be&\fgnY3\t\u0015\u0005u\u0017\u0011XI\u0001\n\u0003\t)-A\u000e%Y\u0016\u001c8/\u001b8ji\u0012:'/Z1uKJ$C-\u001a4bk2$HE\u000e"
)
public abstract class WebUI implements Logging {
   private final SecurityManager securityManager;
   private final SSLOptions sslOptions;
   private final int port;
   public final SparkConf org$apache$spark$ui$WebUI$$conf;
   private final String basePath;
   private final String name;
   private final ArrayBuffer<WebUITab> tabs;
   private final ArrayBuffer<ServletContextHandler> handlers;
   private final HashMap<WebUIPage, ArrayBuffer<ServletContextHandler>> pageToHandlers;
   private Option<ServerInfo> serverInfo;
   private final String publicHostName;
   private final String org$apache$spark$ui$WebUI$$className;
   private transient Logger org$apache$spark$internal$Logging$$log_;

   public static String $lessinit$greater$default$6() {
      return WebUI$.MODULE$.$lessinit$greater$default$6();
   }

   public static String $lessinit$greater$default$5() {
      return WebUI$.MODULE$.$lessinit$greater$default$5();
   }

   public Logger org$apache$spark$internal$Logging$$log_() {
      return this.org$apache$spark$internal$Logging$$log_;
   }

   public void org$apache$spark$internal$Logging$$log__$eq(Logger x$1) {
      this.org$apache$spark$internal$Logging$$log_ = x$1;
   }

   public String logName() {
      return Logging$class.logName(this);
   }

   public Logger log() {
      return Logging$class.log(this);
   }

   public void logInfo(Function0<String> msg) {
      Logging$class.logInfo(this, msg);
   }

   public void logDebug(Function0<String> msg) {
      Logging$class.logDebug(this, msg);
   }

   public void logTrace(Function0<String> msg) {
      Logging$class.logTrace(this, msg);
   }

   public void logWarning(Function0<String> msg) {
      Logging$class.logWarning(this, msg);
   }

   public void logError(Function0<String> msg) {
      Logging$class.logError(this, msg);
   }

   public void logInfo(Function0<String> msg, Throwable throwable) {
      Logging$class.logInfo(this, msg, throwable);
   }

   public void logDebug(Function0<String> msg, Throwable throwable) {
      Logging$class.logDebug(this, msg, throwable);
   }

   public void logTrace(Function0<String> msg, Throwable throwable) {
      Logging$class.logTrace(this, msg, throwable);
   }

   public void logWarning(Function0<String> msg, Throwable throwable) {
      Logging$class.logWarning(this, msg, throwable);
   }

   public void logError(Function0<String> msg, Throwable throwable) {
      Logging$class.logError(this, msg, throwable);
   }

   public boolean isTraceEnabled() {
      return Logging$class.isTraceEnabled(this);
   }

   public void initializeLogIfNecessary(boolean isInterpreter) {
      Logging$class.initializeLogIfNecessary(this, isInterpreter);
   }

   public boolean initializeLogIfNecessary(boolean isInterpreter, boolean silent) {
      return Logging$class.initializeLogIfNecessary(this, isInterpreter, silent);
   }

   public boolean initializeLogIfNecessary$default$2() {
      return Logging$class.initializeLogIfNecessary$default$2(this);
   }

   public SecurityManager securityManager() {
      return this.securityManager;
   }

   public SSLOptions sslOptions() {
      return this.sslOptions;
   }

   public ArrayBuffer<WebUITab> tabs() {
      return this.tabs;
   }

   public ArrayBuffer<ServletContextHandler> handlers() {
      return this.handlers;
   }

   public HashMap<WebUIPage, ArrayBuffer<ServletContextHandler>> pageToHandlers() {
      return this.pageToHandlers;
   }

   public Option<ServerInfo> serverInfo() {
      return this.serverInfo;
   }

   public void serverInfo_$eq(Option<ServerInfo> x$1) {
      this.serverInfo = x$1;
   }

   public String publicHostName() {
      return this.publicHostName;
   }

   public String org$apache$spark$ui$WebUI$$className() {
      return this.org$apache$spark$ui$WebUI$$className;
   }

   public String getBasePath() {
      return this.basePath;
   }

   public Seq<WebUITab> getTabs() {
      return this.tabs();
   }

   public Seq<ServletContextHandler> getHandlers() {
      return this.handlers();
   }

   public SecurityManager getSecurityManager() {
      return this.securityManager();
   }

   public void attachTab(WebUITab tab) {
      tab.pages().foreach(new Serializable(this) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final WebUI $outer;

         public final void apply(WebUIPage page) {
            this.$outer.attachPage(page);
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
            }
         }
      });
      this.tabs().$plus$eq(tab);
   }

   public void detachTab(WebUITab tab) {
      tab.pages().foreach(new Serializable(this) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final WebUI $outer;

         public final void apply(WebUIPage page) {
            this.$outer.detachPage(page);
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
            }
         }
      });
      this.tabs().$minus$eq(tab);
   }

   public void detachPage(WebUIPage page) {
      this.pageToHandlers().remove(page).foreach(new Serializable(this) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final WebUI $outer;

         public final void apply(ArrayBuffer<ServletContextHandler> x$1) {
            x$1.foreach(new Serializable(this) {
               public static final long serialVersionUID = 0L;
               // $FF: synthetic field
               private final <undefinedtype> $outer;

               public final void apply(ServletContextHandler handler) {
                  this.$outer.org$apache$spark$ui$WebUI$$anonfun$$$outer().detachHandler(handler);
               }

               public {
                  if ($outer == null) {
                     throw null;
                  } else {
                     this.$outer = $outer;
                  }
               }
            });
         }

         // $FF: synthetic method
         public WebUI org$apache$spark$ui$WebUI$$anonfun$$$outer() {
            return this.$outer;
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
            }
         }
      });
   }

   public void attachPage(WebUIPage page) {
      String pagePath = (new StringBuilder()).append((Object)"/").append((Object)page.prefix()).toString();
      ServletContextHandler renderHandler = JettyUtils$.MODULE$.createServletHandler(pagePath, JettyUtils$.MODULE$.htmlResponderToServlet(new Serializable(this, page) {
         public static final long serialVersionUID = 0L;
         private final WebUIPage page$1;

         public final Seq<Node> apply(HttpServletRequest request) {
            return this.page$1.render(request);
         }

         public {
            this.page$1 = page$1;
         }
      }), this.securityManager(), this.org$apache$spark$ui$WebUI$$conf, this.basePath);
      ServletContextHandler renderJsonHandler = JettyUtils$.MODULE$.createServletHandler((new StringBuilder()).append((Object)(new StringOps(Predef$.MODULE$.augmentString(pagePath))).stripSuffix("/")).append((Object)"/json").toString(), JettyUtils$.MODULE$.jsonResponderToServlet(new Serializable(this, page) {
         public static final long serialVersionUID = 0L;
         private final WebUIPage page$1;

         public final JsonAST.JValue apply(HttpServletRequest request) {
            return this.page$1.renderJson(request);
         }

         public {
            this.page$1 = page$1;
         }
      }), this.securityManager(), this.org$apache$spark$ui$WebUI$$conf, this.basePath);
      this.attachHandler(renderHandler);
      this.attachHandler(renderJsonHandler);
      ArrayBuffer handlers = (ArrayBuffer)this.pageToHandlers().getOrElseUpdate(page, new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final ArrayBuffer<ServletContextHandler> apply() {
            return (ArrayBuffer)ArrayBuffer$.MODULE$.apply(Nil$.MODULE$);
         }
      });
      handlers.$plus$eq(renderHandler);
   }

   public void attachHandler(ServletContextHandler handler) {
      this.handlers().$plus$eq(handler);
      this.serverInfo().foreach(new Serializable(this, handler) {
         public static final long serialVersionUID = 0L;
         private final ServletContextHandler handler$1;

         public final void apply(ServerInfo x$2) {
            x$2.addHandler(this.handler$1);
         }

         public {
            this.handler$1 = handler$1;
         }
      });
   }

   public void detachHandler(ServletContextHandler handler) {
      this.handlers().$minus$eq(handler);
      this.serverInfo().foreach(new Serializable(this, handler) {
         public static final long serialVersionUID = 0L;
         private final ServletContextHandler handler$2;

         public final void apply(ServerInfo x$3) {
            x$3.removeHandler(this.handler$2);
         }

         public {
            this.handler$2 = handler$2;
         }
      });
   }

   public void addStaticHandler(String resourceBase, String path) {
      this.attachHandler(JettyUtils$.MODULE$.createStaticHandler(resourceBase, path));
   }

   public void removeStaticHandler(String path) {
      this.handlers().find(new Serializable(this, path) {
         public static final long serialVersionUID = 0L;
         private final String path$1;

         public final boolean apply(ServletContextHandler x$4) {
            boolean var3;
            label23: {
               String var10000 = x$4.getContextPath();
               String var2 = this.path$1;
               if (var10000 == null) {
                  if (var2 == null) {
                     break label23;
                  }
               } else if (var10000.equals(var2)) {
                  break label23;
               }

               var3 = false;
               return var3;
            }

            var3 = true;
            return var3;
         }

         public {
            this.path$1 = path$1;
         }
      }).foreach(new Serializable(this) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final WebUI $outer;

         public final void apply(ServletContextHandler handler) {
            this.$outer.detachHandler(handler);
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
            }
         }
      });
   }

   public abstract void initialize();

   public void bind() {
      Predef$.MODULE$.assert(this.serverInfo().isEmpty(), new Serializable(this) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final WebUI $outer;

         public final String apply() {
            return (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Attempted to bind ", " more than once!"})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.$outer.org$apache$spark$ui$WebUI$$className()}));
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
            }
         }
      });

      try {
         String host = (String)Option$.MODULE$.apply(this.org$apache$spark$ui$WebUI$$conf.getenv("SPARK_LOCAL_IP")).getOrElse(new Serializable(this) {
            public static final long serialVersionUID = 0L;

            public final String apply() {
               return "0.0.0.0";
            }
         });
         this.serverInfo_$eq(new Some(JettyUtils$.MODULE$.startJettyServer(host, this.port, this.sslOptions(), this.handlers(), this.org$apache$spark$ui$WebUI$$conf, this.name)));
         this.logInfo(new Serializable(this, host) {
            public static final long serialVersionUID = 0L;
            // $FF: synthetic field
            private final WebUI $outer;
            private final String host$1;

            public final String apply() {
               return (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Bound ", " to ", ", and started at ", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.$outer.org$apache$spark$ui$WebUI$$className(), this.host$1, this.$outer.webUrl()}));
            }

            public {
               if ($outer == null) {
                  throw null;
               } else {
                  this.$outer = $outer;
                  this.host$1 = host$1;
               }
            }
         });
      } catch (Exception var3) {
         this.logError(new Serializable(this) {
            public static final long serialVersionUID = 0L;
            // $FF: synthetic field
            private final WebUI $outer;

            public final String apply() {
               return (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Failed to bind ", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.$outer.org$apache$spark$ui$WebUI$$className()}));
            }

            public {
               if ($outer == null) {
                  throw null;
               } else {
                  this.$outer = $outer;
               }
            }
         }, var3);
         System.exit(1);
      }

   }

   public String webUrl() {
      return (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"http://", ":", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.publicHostName(), BoxesRunTime.boxToInteger(this.boundPort())}));
   }

   public int boundPort() {
      return BoxesRunTime.unboxToInt(this.serverInfo().map(new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final int apply(ServerInfo x$5) {
            return x$5.boundPort();
         }
      }).getOrElse(new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final int apply() {
            return this.apply$mcI$sp();
         }

         public int apply$mcI$sp() {
            return -1;
         }
      }));
   }

   public void stop() {
      Predef$.MODULE$.assert(this.serverInfo().isDefined(), new Serializable(this) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final WebUI $outer;

         public final String apply() {
            return (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Attempted to stop ", " before binding to a server!"})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.$outer.org$apache$spark$ui$WebUI$$className()}));
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
            }
         }
      });
      ((ServerInfo)this.serverInfo().get()).stop();
   }

   public WebUI(SecurityManager securityManager, SSLOptions sslOptions, int port, SparkConf conf, String basePath, String name) {
      this.securityManager = securityManager;
      this.sslOptions = sslOptions;
      this.port = port;
      this.org$apache$spark$ui$WebUI$$conf = conf;
      this.basePath = basePath;
      this.name = name;
      Logging$class.$init$(this);
      this.tabs = (ArrayBuffer)ArrayBuffer$.MODULE$.apply(Nil$.MODULE$);
      this.handlers = (ArrayBuffer)ArrayBuffer$.MODULE$.apply(Nil$.MODULE$);
      this.pageToHandlers = new HashMap<WebUIPage, ArrayBuffer<ServletContextHandler>>();
      this.serverInfo = None$.MODULE$;
      this.publicHostName = (String)Option$.MODULE$.apply(conf.getenv("SPARK_PUBLIC_DNS")).getOrElse(new Serializable(this) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final WebUI $outer;

         public final String apply() {
            return (String)this.$outer.org$apache$spark$ui$WebUI$$conf.get(package$.MODULE$.DRIVER_HOST_ADDRESS());
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
            }
         }
      });
      this.org$apache$spark$ui$WebUI$$className = Utils$.MODULE$.getFormattedClassName(this);
   }
}
