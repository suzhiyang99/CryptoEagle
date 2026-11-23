package org.apache.spark.ui;

import java.net.URI;
import java.net.URL;
import java.util.EnumSet;
import java.util.concurrent.Executor;
import javax.servlet.DispatcherType;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.spark.SSLOptions;
import org.apache.spark.SecurityManager;
import org.apache.spark.SparkConf;
import org.apache.spark.internal.Logging;
import org.apache.spark.internal.Logging$class;
import org.apache.spark.internal.config.ConfigEntry;
import org.apache.spark.internal.config.package$;
import org.apache.spark.util.Utils$;
import org.json4s.Formats;
import org.json4s.JsonAST;
import org.json4s.jackson.JsonMethods;
import org.json4s.jackson.JsonMethods.;
import org.slf4j.Logger;
import org.spark_project.jetty.client.HttpClient;
import org.spark_project.jetty.client.api.Response;
import org.spark_project.jetty.client.http.HttpClientTransportOverHTTP;
import org.spark_project.jetty.io.ByteBufferPool;
import org.spark_project.jetty.proxy.ProxyServlet;
import org.spark_project.jetty.server.AbstractConnectionFactory;
import org.spark_project.jetty.server.ConnectionFactory;
import org.spark_project.jetty.server.HttpConnectionFactory;
import org.spark_project.jetty.server.Request;
import org.spark_project.jetty.server.Server;
import org.spark_project.jetty.server.ServerConnector;
import org.spark_project.jetty.server.handler.AbstractHandler;
import org.spark_project.jetty.server.handler.ContextHandler;
import org.spark_project.jetty.server.handler.ContextHandlerCollection;
import org.spark_project.jetty.server.handler.ErrorHandler;
import org.spark_project.jetty.server.handler.gzip.GzipHandler;
import org.spark_project.jetty.servlet.DefaultServlet;
import org.spark_project.jetty.servlet.FilterHolder;
import org.spark_project.jetty.servlet.ServletContextHandler;
import org.spark_project.jetty.servlet.ServletHolder;
import org.spark_project.jetty.util.ssl.SslContextFactory;
import org.spark_project.jetty.util.thread.QueuedThreadPool;
import org.spark_project.jetty.util.thread.ScheduledExecutorScheduler;
import scala.Array$;
import scala.Function0;
import scala.Function1;
import scala.MatchError;
import scala.None$;
import scala.Option;
import scala.Option$;
import scala.Predef$;
import scala.Serializable;
import scala.Some;
import scala.StringContext;
import scala.Tuple2;
import scala.collection.Seq;
import scala.collection.Seq$;
import scala.collection.TraversableOnce;
import scala.collection.immutable.Set;
import scala.collection.immutable.StringOps;
import scala.collection.mutable.StringBuilder;
import scala.reflect.ClassTag$;
import scala.runtime.BoxedUnit;
import scala.runtime.BoxesRunTime;
import scala.runtime.IntRef;
import scala.runtime.TraitSetter;
import scala.xml.Node;

public final class JettyUtils$ implements Logging {
   public static final JettyUtils$ MODULE$;
   private final String SPARK_CONNECTOR_NAME;
   private final String REDIRECT_CONNECTOR_NAME;
   private transient Logger org$apache$spark$internal$Logging$$log_;

   static {
      new JettyUtils$();
   }

   public Logger org$apache$spark$internal$Logging$$log_() {
      return this.org$apache$spark$internal$Logging$$log_;
   }

   @TraitSetter
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

   public String SPARK_CONNECTOR_NAME() {
      return this.SPARK_CONNECTOR_NAME;
   }

   public String REDIRECT_CONNECTOR_NAME() {
      return this.REDIRECT_CONNECTOR_NAME;
   }

   public JettyUtils.ServletParams<JsonAST.JValue> jsonResponderToServlet(Function1<HttpServletRequest, JsonAST.JValue> responder) {
      return new JettyUtils.ServletParams<JsonAST.JValue>(responder, "text/json", new Serializable() {
         public static final long serialVersionUID = 0L;

         public final String apply(JsonAST.JValue in) {
            JsonMethods var10000 = .MODULE$;
            Formats x$11 = .MODULE$.render$default$2(in);
            return var10000.pretty(.MODULE$.render(in, x$11));
         }
      });
   }

   public JettyUtils.ServletParams<Seq<Node>> htmlResponderToServlet(Function1<HttpServletRequest, Seq<Node>> responder) {
      return new JettyUtils.ServletParams<Seq<Node>>(responder, "text/html", new Serializable() {
         public static final long serialVersionUID = 0L;

         public final String apply(Seq<Node> in) {
            return (new StringBuilder()).append((Object)"<!DOCTYPE html>").append((Object)in.toString()).toString();
         }
      });
   }

   public JettyUtils.ServletParams<String> textResponderToServlet(Function1<HttpServletRequest, String> responder) {
      return new JettyUtils.ServletParams<String>(responder, "text/plain", JettyUtils.ServletParams$.MODULE$.$lessinit$greater$default$3());
   }

   public <T> HttpServlet createServlet(JettyUtils.ServletParams<T> servletParams, SecurityManager securityMgr, SparkConf conf) {
      Option allowFramingFrom = conf.getOption("spark.ui.allowFramingFrom");
      String xFrameOptionsValue = (String)allowFramingFrom.map(new Serializable() {
         public static final long serialVersionUID = 0L;

         public final String apply(String uri) {
            return (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"ALLOW-FROM ", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{uri}));
         }
      }).getOrElse(new Serializable() {
         public static final long serialVersionUID = 0L;

         public final String apply() {
            return "SAMEORIGIN";
         }
      });
      return new HttpServlet(servletParams, securityMgr, conf, xFrameOptionsValue) {
         private final JettyUtils.ServletParams servletParams$1;
         private final SecurityManager securityMgr$1;
         private final SparkConf conf$2;
         private final String xFrameOptionsValue$1;

         public void doGet(HttpServletRequest request, HttpServletResponse response) {
            try {
               if (this.securityMgr$1.checkUIViewPermissions(request.getRemoteUser())) {
                  response.setContentType((new StringOps(Predef$.MODULE$.augmentString("%s;charset=utf-8"))).format(Predef$.MODULE$.genericWrapArray(new Object[]{this.servletParams$1.contentType()})));
                  response.setStatus(200);
                  Object result = this.servletParams$1.responder().apply(request);
                  response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
                  response.setHeader("X-Frame-Options", this.xFrameOptionsValue$1);
                  response.setHeader("X-XSS-Protection", (String)this.conf$2.get(package$.MODULE$.UI_X_XSS_PROTECTION()));
                  if (BoxesRunTime.unboxToBoolean(this.conf$2.get(package$.MODULE$.UI_X_CONTENT_TYPE_OPTIONS()))) {
                     response.setHeader("X-Content-Type-Options", "nosniff");
                  }

                  label25: {
                     String var10000 = request.getScheme();
                     String var6 = "https";
                     if (var10000 == null) {
                        if (var6 != null) {
                           break label25;
                        }
                     } else if (!var10000.equals(var6)) {
                        break label25;
                     }

                     ((Option)this.conf$2.get((ConfigEntry)package$.MODULE$.UI_STRICT_TRANSPORT_SECURITY())).foreach(new Serializable(this, response) {
                        public static final long serialVersionUID = 0L;
                        private final HttpServletResponse response$1;

                        public final void apply(String x$1) {
                           this.response$1.setHeader("Strict-Transport-Security", x$1);
                        }

                        public {
                           this.response$1 = response$1;
                        }
                     });
                  }

                  response.getWriter().print((String)this.servletParams$1.extractFn().apply(result));
               } else {
                  response.setStatus(403);
                  response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
                  response.sendError(403, "User is not authorized to access this page.");
               }
            } catch (IllegalArgumentException var7) {
               response.sendError(400, var7.getMessage());
            } catch (Exception var8) {
               JettyUtils$.MODULE$.logWarning(new Serializable(this, request, var8) {
                  public static final long serialVersionUID = 0L;
                  private final HttpServletRequest request$1;
                  private final Exception e$1;

                  public final String apply() {
                     return (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"GET ", " failed: ", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.request$1.getRequestURI(), this.e$1}));
                  }

                  public {
                     this.request$1 = request$1;
                     this.e$1 = e$1;
                  }
               }, var8);
               throw var8;
            }

         }

         public void doTrace(HttpServletRequest req, HttpServletResponse res) {
            res.sendError(405);
         }

         public {
            this.servletParams$1 = servletParams$1;
            this.securityMgr$1 = securityMgr$1;
            this.conf$2 = conf$2;
            this.xFrameOptionsValue$1 = xFrameOptionsValue$1;
         }
      };
   }

   public <T> ServletContextHandler createServletHandler(String path, JettyUtils.ServletParams<T> servletParams, SecurityManager securityMgr, SparkConf conf, String basePath) {
      return this.createServletHandler(path, this.createServlet(servletParams, securityMgr, conf), basePath);
   }

   public ServletContextHandler createServletHandler(String path, HttpServlet servlet, String basePath) {
      String var10000;
      label26: {
         label25: {
            label24: {
               String var5 = "";
               if (basePath == null) {
                  if (var5 != null) {
                     break label24;
                  }
               } else if (!basePath.equals(var5)) {
                  break label24;
               }

               String var6 = "/";
               if (path == null) {
                  if (var6 == null) {
                     break label25;
                  }
               } else if (path.equals(var6)) {
                  break label25;
               }
            }

            var10000 = (new StringOps(Predef$.MODULE$.augmentString((new StringBuilder()).append((Object)basePath).append((Object)path).toString()))).stripSuffix("/");
            break label26;
         }

         var10000 = path;
      }

      String prefixedPath = var10000;
      ServletContextHandler contextHandler = new ServletContextHandler();
      ServletHolder holder = new ServletHolder(servlet);
      contextHandler.setContextPath(prefixedPath);
      contextHandler.addServlet(holder, "/");
      return contextHandler;
   }

   public <T> String createServletHandler$default$5() {
      return "";
   }

   public ServletContextHandler createRedirectHandler(String srcPath, String destPath, Function1<HttpServletRequest, BoxedUnit> beforeRedirect, String basePath, Set<String> httpMethods) {
      String prefixedDestPath = (new StringBuilder()).append((Object)basePath).append((Object)destPath).toString();
      HttpServlet servlet = new HttpServlet(beforeRedirect, httpMethods, prefixedDestPath) {
         private final Function1 beforeRedirect$1;
         private final Set httpMethods$1;
         private final String prefixedDestPath$1;

         public void doGet(HttpServletRequest request, HttpServletResponse response) {
            if (this.httpMethods$1.contains("GET")) {
               this.doRequest(request, response);
            } else {
               response.sendError(405);
            }

         }

         public void doPost(HttpServletRequest request, HttpServletResponse response) {
            if (this.httpMethods$1.contains("POST")) {
               this.doRequest(request, response);
            } else {
               response.sendError(405);
            }

         }

         private void doRequest(HttpServletRequest request, HttpServletResponse response) {
            this.beforeRedirect$1.apply(request);
            String newUrl = (new URL(new URL(request.getRequestURL().toString()), this.prefixedDestPath$1)).toString();
            response.sendRedirect(newUrl);
         }

         public void doTrace(HttpServletRequest req, HttpServletResponse res) {
            res.sendError(405);
         }

         public {
            this.beforeRedirect$1 = beforeRedirect$1;
            this.httpMethods$1 = httpMethods$1;
            this.prefixedDestPath$1 = prefixedDestPath$1;
         }
      };
      return this.createServletHandler(srcPath, servlet, basePath);
   }

   public Function1<HttpServletRequest, BoxedUnit> createRedirectHandler$default$3() {
      return new Serializable() {
         public static final long serialVersionUID = 0L;

         public final void apply(HttpServletRequest x) {
         }
      };
   }

   public String createRedirectHandler$default$4() {
      return "";
   }

   public Set<String> createRedirectHandler$default$5() {
      return (Set)Predef$.MODULE$.Set().apply(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"GET"})));
   }

   public ServletContextHandler createStaticHandler(String resourceBase, String path) {
      ServletContextHandler contextHandler = new ServletContextHandler();
      contextHandler.setInitParameter("org.spark_project.jetty.servlet.Default.gzip", "false");
      DefaultServlet staticHandler = new DefaultServlet();
      ServletHolder holder = new ServletHolder(staticHandler);
      Option var6 = Option$.MODULE$.apply(Utils$.MODULE$.getSparkClassLoader().getResource(resourceBase));
      if (var6 instanceof Some) {
         Some var7 = (Some)var6;
         URL res = (URL)var7.x();
         holder.setInitParameter("resourceBase", res.toString());
         BoxedUnit var9 = BoxedUnit.UNIT;
         contextHandler.setContextPath(path);
         contextHandler.addServlet(holder, "/");
         return contextHandler;
      } else if (None$.MODULE$.equals(var6)) {
         throw new Exception((new StringBuilder()).append((Object)"Could not find resource path for Web UI: ").append((Object)resourceBase).toString());
      } else {
         throw new MatchError(var6);
      }
   }

   public ServletContextHandler createProxyHandler(Function1<String, Option<String>> idToUiAddress) {
      ProxyServlet servlet = new ProxyServlet(idToUiAddress) {
         private final Function1 idToUiAddress$1;

         public String rewriteTarget(HttpServletRequest request) {
            String path = request.getPathInfo();
            if (path == null) {
               Object var10000 = null;
               return null;
            } else {
               int prefixTrailingSlashIndex = path.indexOf(47, 1);
               String prefix = prefixTrailingSlashIndex == -1 ? path : path.substring(0, prefixTrailingSlashIndex);
               String id = (String)(new StringOps(Predef$.MODULE$.augmentString(prefix))).drop(1);
               return (String)((Option)this.idToUiAddress$1.apply(id)).map(new Serializable(this, request, path, prefix) {
                  public static final long serialVersionUID = 0L;
                  private final HttpServletRequest request$2;
                  private final String path$1;
                  private final String prefix$1;

                  public final URI apply(String x$2) {
                     return JettyUtils$.MODULE$.createProxyURI(this.prefix$1, x$2, this.path$1, this.request$2.getQueryString());
                  }

                  public {
                     this.request$2 = request$2;
                     this.path$1 = path$1;
                     this.prefix$1 = prefix$1;
                  }
               }).filter(new Serializable(this) {
                  public static final long serialVersionUID = 0L;
                  // $FF: synthetic field
                  private final <undefinedtype> $outer;

                  public final boolean apply(URI uri) {
                     return uri != null && this.$outer.validateDestination(uri.getHost(), uri.getPort());
                  }

                  public {
                     if ($outer == null) {
                        throw null;
                     } else {
                        this.$outer = $outer;
                     }
                  }
               }).map(new Serializable(this) {
                  public static final long serialVersionUID = 0L;

                  public final String apply(URI x$3) {
                     return x$3.toString();
                  }
               }).orNull(Predef$.MODULE$.$conforms());
            }
         }

         public HttpClient newHttpClient() {
            int numSelectors = scala.math.package$.MODULE$.max(1, scala.math.package$.MODULE$.min(8, Runtime.getRuntime().availableProcessors() / 2));
            return new HttpClient(new HttpClientTransportOverHTTP(numSelectors), (SslContextFactory)null);
         }

         public String filterServerResponseHeader(HttpServletRequest clientRequest, Response serverResponse, String headerName, String headerValue) {
            if (headerName.equalsIgnoreCase("location")) {
               String newHeader = JettyUtils$.MODULE$.createProxyLocationHeader(headerValue, clientRequest, serverResponse.getRequest().getURI());
               if (newHeader != null) {
                  return newHeader;
               }
            }

            return super.filterServerResponseHeader(clientRequest, serverResponse, headerName, headerValue);
         }

         public {
            this.idToUiAddress$1 = idToUiAddress$1;
         }
      };
      ServletContextHandler contextHandler = new ServletContextHandler();
      ServletHolder holder = new ServletHolder(servlet);
      contextHandler.setContextPath("/proxy");
      contextHandler.addServlet(holder, "/*");
      return contextHandler;
   }

   public void addFilters(Seq<ServletContextHandler> handlers, SparkConf conf) {
      String[] filters = (String[])Predef$.MODULE$.refArrayOps((Object[])(new StringOps(Predef$.MODULE$.augmentString(conf.get("spark.ui.filters", "")))).split(',')).map(new Serializable() {
         public static final long serialVersionUID = 0L;

         public final String apply(String x$4) {
            return x$4.trim();
         }
      }, Array$.MODULE$.canBuildFrom(ClassTag$.MODULE$.apply(String.class)));
      Predef$.MODULE$.refArrayOps((Object[])filters).foreach(new Serializable(handlers, conf) {
         public static final long serialVersionUID = 0L;
         private final Seq handlers$1;
         private final SparkConf conf$1;

         public final void apply(String x0$1) {
            if (x0$1 != null) {
               if (x0$1.isEmpty()) {
                  BoxedUnit var10000 = BoxedUnit.UNIT;
               } else {
                  JettyUtils$.MODULE$.logInfo(new Serializable(this, x0$1) {
                     public static final long serialVersionUID = 0L;
                     private final String x2$1;

                     public final String apply() {
                        return (new StringBuilder()).append((Object)"Adding filter: ").append((Object)this.x2$1).toString();
                     }

                     public {
                        this.x2$1 = x2$1;
                     }
                  });
                  FilterHolder holder = new FilterHolder();
                  holder.setClassName(x0$1);
                  Predef$.MODULE$.refArrayOps(Predef$.MODULE$.refArrayOps((Object[])(new StringOps(Predef$.MODULE$.augmentString(this.conf$1.get((new StringBuilder()).append((Object)"spark.").append((Object)x0$1).append((Object)".params").toString(), "")))).split(',')).map(new Serializable(this) {
                     public static final long serialVersionUID = 0L;

                     public final String apply(String x$5) {
                        return x$5.trim();
                     }
                  }, Array$.MODULE$.canBuildFrom(ClassTag$.MODULE$.apply(String.class)))).toSet().foreach(new Serializable(this, holder) {
                     public static final long serialVersionUID = 0L;
                     private final FilterHolder holder$1;

                     public final void apply(String param) {
                        if (!param.isEmpty()) {
                           String[] parts = param.split("=");
                           if (parts.length == 2) {
                              this.holder$1.setInitParameter(parts[0], parts[1]);
                           }
                        }

                     }

                     public {
                        this.holder$1 = holder$1;
                     }
                  });
                  String prefix = (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"spark.", ".param."})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{x0$1}));
                  Predef$.MODULE$.refArrayOps(Predef$.MODULE$.refArrayOps(this.conf$1.getAll()).filter(new Serializable(this, prefix) {
                     public static final long serialVersionUID = 0L;
                     private final String prefix$2;

                     public final boolean apply(Tuple2<String, String> x0$2) {
                        if (x0$2 == null) {
                           throw new MatchError(x0$2);
                        } else {
                           String k = (String)x0$2._1();
                           boolean var4 = k.length() > this.prefix$2.length() && k.startsWith(this.prefix$2);
                           return var4;
                        }
                     }

                     public {
                        this.prefix$2 = prefix$2;
                     }
                  })).foreach(new Serializable(this, holder, prefix) {
                     public static final long serialVersionUID = 0L;
                     private final FilterHolder holder$1;
                     private final String prefix$2;

                     public final void apply(Tuple2<String, String> x0$3) {
                        if (x0$3 != null) {
                           String k = (String)x0$3._1();
                           String v = (String)x0$3._2();
                           this.holder$1.setInitParameter(k.substring(this.prefix$2.length()), v);
                           BoxedUnit var5 = BoxedUnit.UNIT;
                        } else {
                           throw new MatchError(x0$3);
                        }
                     }

                     public {
                        this.holder$1 = holder$1;
                        this.prefix$2 = prefix$2;
                     }
                  });
                  EnumSet enumDispatcher = EnumSet.of(DispatcherType.ASYNC, DispatcherType.ERROR, DispatcherType.FORWARD, DispatcherType.INCLUDE, DispatcherType.REQUEST);
                  this.handlers$1.foreach(new Serializable(this, holder, enumDispatcher) {
                     public static final long serialVersionUID = 0L;
                     private final FilterHolder holder$1;
                     private final EnumSet enumDispatcher$1;

                     public final void apply(ServletContextHandler x0$4) {
                        x0$4.addFilter(this.holder$1, "/*", this.enumDispatcher$1);
                        BoxedUnit var3 = BoxedUnit.UNIT;
                     }

                     public {
                        this.holder$1 = holder$1;
                        this.enumDispatcher$1 = enumDispatcher$1;
                     }
                  });
                  BoxedUnit var8 = BoxedUnit.UNIT;
               }

            } else {
               throw new MatchError(x0$1);
            }
         }

         public {
            this.handlers$1 = handlers$1;
            this.conf$1 = conf$1;
         }
      });
   }

   public ServerInfo startJettyServer(String hostName, int port, SSLOptions sslOptions, Seq<ServletContextHandler> handlers, SparkConf conf, String serverName) {
      this.addFilters(handlers, conf);
      QueuedThreadPool pool = new QueuedThreadPool();
      if ((new StringOps(Predef$.MODULE$.augmentString(serverName))).nonEmpty()) {
         pool.setName(serverName);
      }

      pool.setDaemon(true);
      Server server = new Server(pool);
      ErrorHandler errorHandler = new ErrorHandler();
      errorHandler.setShowStacks(true);
      errorHandler.setServer(server);
      server.addBean(errorHandler);
      ContextHandlerCollection collection = new ContextHandlerCollection();
      server.setHandler(collection);
      ScheduledExecutorScheduler serverExecutor = new ScheduledExecutorScheduler((new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"", "-JettyScheduler"})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{serverName})), true);

      try {
         server.start();
         IntRef minThreads = IntRef.create(1);
         Option securePort = sslOptions.createJettySslContextFactory().map(new Serializable(hostName, port, sslOptions, conf, serverName, server, serverExecutor, minThreads) {
            public static final long serialVersionUID = 0L;
            private final String hostName$1;
            public final int port$1;
            private final SSLOptions sslOptions$1;
            private final SparkConf conf$3;
            private final String serverName$1;
            private final Server server$1;
            private final ScheduledExecutorScheduler serverExecutor$1;
            private final IntRef minThreads$1;

            public final int apply(SslContextFactory factory) {
               int securePort = BoxesRunTime.unboxToInt(this.sslOptions$1.port().getOrElse(new Serializable(this) {
                  public static final long serialVersionUID = 0L;
                  // $FF: synthetic field
                  private final <undefinedtype> $outer;

                  public final int apply() {
                     return this.apply$mcI$sp();
                  }

                  public int apply$mcI$sp() {
                     return this.$outer.port$1 > 0 ? Utils$.MODULE$.userPort(this.$outer.port$1, 400) : 0;
                  }

                  public {
                     if ($outer == null) {
                        throw null;
                     } else {
                        this.$outer = $outer;
                     }
                  }
               }));
               String secureServerName = (new StringOps(Predef$.MODULE$.augmentString(this.serverName$1))).nonEmpty() ? (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"", " (HTTPS)"})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.serverName$1})) : this.serverName$1;
               ConnectionFactory[] connectionFactories = AbstractConnectionFactory.getFactories(factory, new HttpConnectionFactory());
               Tuple2 var6 = Utils$.MODULE$.startServiceOnPort(securePort, new Serializable(this, connectionFactories) {
                  public static final long serialVersionUID = 0L;
                  // $FF: synthetic field
                  private final <undefinedtype> $outer;
                  private final ConnectionFactory[] connectionFactories$1;

                  public final Tuple2<ServerConnector, Object> apply(int currentPort) {
                     return this.$outer.org$apache$spark$ui$JettyUtils$$anonfun$$sslConnect$1(currentPort, this.connectionFactories$1);
                  }

                  public {
                     if ($outer == null) {
                        throw null;
                     } else {
                        this.$outer = $outer;
                        this.connectionFactories$1 = connectionFactories$1;
                     }
                  }
               }, this.conf$3, secureServerName);
               if (var6 != null) {
                  ServerConnector connector = (ServerConnector)var6._1();
                  int boundPort = var6._2$mcI$sp();
                  Tuple2 var9 = new Tuple2(connector, BoxesRunTime.boxToInteger(boundPort));
                  ServerConnector connector = (ServerConnector)var9._1();
                  int boundPort = var9._2$mcI$sp();
                  connector.setName(JettyUtils$.MODULE$.SPARK_CONNECTOR_NAME());
                  this.server$1.addConnector(connector);
                  return boundPort;
               } else {
                  throw new MatchError(var6);
               }
            }

            public final Tuple2 org$apache$spark$ui$JettyUtils$$anonfun$$sslConnect$1(int currentPort, ConnectionFactory[] connectionFactories$1) {
               return JettyUtils$.MODULE$.org$apache$spark$ui$JettyUtils$$newConnector$1(connectionFactories$1, currentPort, this.hostName$1, this.server$1, this.serverExecutor$1, this.minThreads$1);
            }

            public {
               this.hostName$1 = hostName$1;
               this.port$1 = port$1;
               this.sslOptions$1 = sslOptions$1;
               this.conf$3 = conf$3;
               this.serverName$1 = serverName$1;
               this.server$1 = server$1;
               this.serverExecutor$1 = serverExecutor$1;
               this.minThreads$1 = minThreads$1;
            }
         });
         Tuple2 var16 = Utils$.MODULE$.startServiceOnPort(port, new Serializable(hostName, server, serverExecutor, minThreads) {
            public static final long serialVersionUID = 0L;
            private final String hostName$1;
            private final Server server$1;
            private final ScheduledExecutorScheduler serverExecutor$1;
            private final IntRef minThreads$1;

            public final Tuple2<ServerConnector, Object> apply(int currentPort) {
               return JettyUtils$.MODULE$.org$apache$spark$ui$JettyUtils$$httpConnect$1(currentPort, this.hostName$1, this.server$1, this.serverExecutor$1, this.minThreads$1);
            }

            public {
               this.hostName$1 = hostName$1;
               this.server$1 = server$1;
               this.serverExecutor$1 = serverExecutor$1;
               this.minThreads$1 = minThreads$1;
            }
         }, conf, serverName);
         if (var16 != null) {
            ServerConnector httpConnector = (ServerConnector)var16._1();
            int httpPort = var16._2$mcI$sp();
            Tuple2 var19 = new Tuple2(httpConnector, BoxesRunTime.boxToInteger(httpPort));
            ServerConnector httpConnector = (ServerConnector)var19._1();
            int httpPort = var19._2$mcI$sp();
            if (securePort instanceof Some) {
               Some var23 = (Some)securePort;
               int p = BoxesRunTime.unboxToInt(var23.x());
               httpConnector.setName(this.REDIRECT_CONNECTOR_NAME());
               ContextHandler redirector = this.createRedirectHttpsHandler(p, "https");
               collection.addHandler(redirector);
               redirector.start();
               BoxedUnit var25 = BoxedUnit.UNIT;
            } else {
               if (!None$.MODULE$.equals(securePort)) {
                  throw new MatchError(securePort);
               }

               httpConnector.setName(this.SPARK_CONNECTOR_NAME());
               BoxedUnit var28 = BoxedUnit.UNIT;
            }

            server.addConnector(httpConnector);
            handlers.foreach(new Serializable(collection) {
               public static final long serialVersionUID = 0L;
               private final ContextHandlerCollection collection$1;

               public final void apply(ServletContextHandler h) {
                  h.setVirtualHosts(JettyUtils$.MODULE$.toVirtualHosts(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{JettyUtils$.MODULE$.SPARK_CONNECTOR_NAME()}))));
                  GzipHandler gzipHandler = new GzipHandler();
                  gzipHandler.setHandler(h);
                  this.collection$1.addHandler(gzipHandler);
                  gzipHandler.start();
               }

               public {
                  this.collection$1 = collection$1;
               }
            });
            pool.setMaxThreads(scala.math.package$.MODULE$.max(pool.getMaxThreads(), minThreads.elem));
            return new ServerInfo(server, httpPort, securePort, collection);
         } else {
            throw new MatchError(var16);
         }
      } catch (Exception var27) {
         server.stop();
         if (serverExecutor.isStarted()) {
            serverExecutor.stop();
         }

         if (pool.isStarted()) {
            pool.stop();
         }

         throw var27;
      }
   }

   public String startJettyServer$default$6() {
      return "";
   }

   private ContextHandler createRedirectHttpsHandler(int securePort, String scheme) {
      ContextHandler redirectHandler = new ContextHandler();
      redirectHandler.setContextPath("/");
      redirectHandler.setVirtualHosts(this.toVirtualHosts(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{this.REDIRECT_CONNECTOR_NAME()}))));
      redirectHandler.setHandler(new AbstractHandler(securePort, scheme) {
         private final int securePort$1;
         private final String scheme$1;

         public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response) {
            if (!baseRequest.isSecure()) {
               String httpsURI = JettyUtils$.MODULE$.org$apache$spark$ui$JettyUtils$$createRedirectURI(this.scheme$1, baseRequest.getServerName(), this.securePort$1, baseRequest.getRequestURI(), baseRequest.getQueryString());
               response.setContentLength(0);
               response.sendRedirect(response.encodeRedirectURL(httpsURI));
               baseRequest.setHandled(true);
            }
         }

         public {
            this.securePort$1 = securePort$1;
            this.scheme$1 = scheme$1;
         }
      });
      return redirectHandler;
   }

   public URI createProxyURI(String prefix, String target, String path, String query) {
      if (!path.startsWith(prefix)) {
         Object var9 = null;
         return null;
      } else {
         StringBuilder uri = new StringBuilder(target);
         String rest = path.substring(prefix.length());
         if (rest.isEmpty()) {
            BoxedUnit var10000 = BoxedUnit.UNIT;
         } else {
            if (!rest.startsWith("/") && !uri.endsWith(Predef$.MODULE$.wrapString("/"))) {
               uri.append("/");
            } else {
               BoxedUnit var8 = BoxedUnit.UNIT;
            }

            uri.append(rest);
         }

         URI rewrittenURI = URI.create(uri.toString());
         return query == null ? rewrittenURI.normalize() : (new URI(rewrittenURI.getScheme(), rewrittenURI.getAuthority(), rewrittenURI.getPath(), query, rewrittenURI.getFragment())).normalize();
      }
   }

   public String createProxyLocationHeader(String headerValue, HttpServletRequest clientRequest, URI targetUri) {
      String toReplace = (new StringBuilder()).append((Object)targetUri.getScheme()).append((Object)"://").append((Object)targetUri.getAuthority()).toString();
      String var10000;
      if (headerValue.startsWith(toReplace)) {
         String id = (String)(new StringOps(Predef$.MODULE$.augmentString(clientRequest.getPathInfo().substring("/proxy/".length())))).takeWhile(new Serializable() {
            public static final long serialVersionUID = 0L;

            public final boolean apply(char x$8) {
               return x$8 != '/';
            }
         });
         String headerPath = headerValue.substring(toReplace.length());
         var10000 = (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"", "://", "/proxy/", "", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{clientRequest.getScheme(), clientRequest.getHeader("host"), id, headerPath}));
      } else {
         var10000 = null;
      }

      return var10000;
   }

   public String org$apache$spark$ui$JettyUtils$$createRedirectURI(String scheme, String server, int port, String path, String query) {
      String redirectServer = server.contains(":") && !server.startsWith("[") ? (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"[", "]"})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{server})) : server;
      String authority = (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"", ":", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{redirectServer, BoxesRunTime.boxToInteger(port)}));
      return (new URI(scheme, authority, path, query, (String)null)).toString();
   }

   public String[] toVirtualHosts(Seq<String> connectors) {
      return (String[])((TraversableOnce)connectors.map(new Serializable() {
         public static final long serialVersionUID = 0L;

         public final String apply(String x$9) {
            return (new StringBuilder()).append((Object)"@").append((Object)x$9).toString();
         }
      }, Seq$.MODULE$.canBuildFrom())).toArray(ClassTag$.MODULE$.apply(String.class));
   }

   public final Tuple2 org$apache$spark$ui$JettyUtils$$newConnector$1(ConnectionFactory[] connectionFactories, int port, String hostName$1, Server server$1, ScheduledExecutorScheduler serverExecutor$1, IntRef minThreads$1) {
      ServerConnector connector = new ServerConnector(server$1, (Executor)null, serverExecutor$1, (ByteBufferPool)null, -1, -1, connectionFactories);
      connector.setPort(port);
      connector.start();
      connector.setAcceptQueueSize(scala.math.package$.MODULE$.min(connector.getAcceptors(), 8));
      connector.setHost(hostName$1);
      minThreads$1.elem += connector.getAcceptors() * 2;
      return new Tuple2(connector, BoxesRunTime.boxToInteger(connector.getLocalPort()));
   }

   public final Tuple2 org$apache$spark$ui$JettyUtils$$httpConnect$1(int currentPort, String hostName$1, Server server$1, ScheduledExecutorScheduler serverExecutor$1, IntRef minThreads$1) {
      return this.org$apache$spark$ui$JettyUtils$$newConnector$1((ConnectionFactory[])(new ConnectionFactory[]{new HttpConnectionFactory()}), currentPort, hostName$1, server$1, serverExecutor$1, minThreads$1);
   }

   private JettyUtils$() {
      MODULE$ = this;
      Logging$class.$init$(this);
      this.SPARK_CONNECTOR_NAME = "Spark";
      this.REDIRECT_CONNECTOR_NAME = "HttpsRedirect";
   }
}
