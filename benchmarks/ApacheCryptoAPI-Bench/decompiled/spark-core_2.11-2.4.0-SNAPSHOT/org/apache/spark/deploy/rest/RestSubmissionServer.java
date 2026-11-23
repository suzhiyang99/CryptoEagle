package org.apache.spark.deploy.rest;

import java.util.concurrent.Executor;
import org.apache.spark.SparkConf;
import org.apache.spark.internal.Logging;
import org.apache.spark.internal.Logging$class;
import org.apache.spark.util.Utils$;
import org.slf4j.Logger;
import org.spark_project.jetty.io.ByteBufferPool;
import org.spark_project.jetty.server.ConnectionFactory;
import org.spark_project.jetty.server.HttpConnectionFactory;
import org.spark_project.jetty.server.Server;
import org.spark_project.jetty.server.ServerConnector;
import org.spark_project.jetty.servlet.ServletContextHandler;
import org.spark_project.jetty.servlet.ServletHolder;
import org.spark_project.jetty.util.thread.QueuedThreadPool;
import org.spark_project.jetty.util.thread.ScheduledExecutorScheduler;
import scala.Function0;
import scala.MatchError;
import scala.None$;
import scala.Option;
import scala.Predef;
import scala.Predef$;
import scala.Serializable;
import scala.Some;
import scala.StringContext;
import scala.Tuple2;
import scala.collection.immutable.Map;
import scala.reflect.ScalaSignature;
import scala.runtime.BoxedUnit;
import scala.runtime.BoxesRunTime;

@ScalaSignature(
   bytes = "\u0006\u0001\u0005%cAB\u0001\u0003\u0003\u00031AB\u0001\u000bSKN$8+\u001e2nSN\u001c\u0018n\u001c8TKJ4XM\u001d\u0006\u0003\u0007\u0011\tAA]3ti*\u0011QAB\u0001\u0007I\u0016\u0004Hn\\=\u000b\u0005\u001dA\u0011!B:qCJ\\'BA\u0005\u000b\u0003\u0019\t\u0007/Y2iK*\t1\"A\u0002pe\u001e\u001c2\u0001A\u0007\u0014!\tq\u0011#D\u0001\u0010\u0015\u0005\u0001\u0012!B:dC2\f\u0017B\u0001\n\u0010\u0005\u0019\te.\u001f*fMB\u0011AcF\u0007\u0002+)\u0011aCB\u0001\tS:$XM\u001d8bY&\u0011\u0001$\u0006\u0002\b\u0019><w-\u001b8h\u0011!Q\u0002A!b\u0001\n\u0003a\u0012\u0001\u00025pgR\u001c\u0001!F\u0001\u001e!\tq\u0012E\u0004\u0002\u000f?%\u0011\u0001eD\u0001\u0007!J,G-\u001a4\n\u0005\t\u001a#AB*ue&twM\u0003\u0002!\u001f!AQ\u0005\u0001B\u0001B\u0003%Q$A\u0003i_N$\b\u0005\u0003\u0005(\u0001\t\u0015\r\u0011\"\u0001)\u00035\u0011X-];fgR,G\rU8siV\t\u0011\u0006\u0005\u0002\u000fU%\u00111f\u0004\u0002\u0004\u0013:$\b\u0002C\u0017\u0001\u0005\u0003\u0005\u000b\u0011B\u0015\u0002\u001dI,\u0017/^3ti\u0016$\u0007k\u001c:uA!Aq\u0006\u0001BC\u0002\u0013\u0005\u0001'\u0001\u0006nCN$XM]\"p]\u001a,\u0012!\r\t\u0003eMj\u0011AB\u0005\u0003i\u0019\u0011\u0011b\u00159be.\u001cuN\u001c4\t\u0011Y\u0002!\u0011!Q\u0001\nE\n1\"\\1ti\u0016\u00148i\u001c8gA!)\u0001\b\u0001C\u0001s\u00051A(\u001b8jiz\"BA\u000f\u001f>}A\u00111\bA\u0007\u0002\u0005!)!d\u000ea\u0001;!)qe\u000ea\u0001S!)qf\u000ea\u0001c!9\u0001\t\u0001b\u0001\u000e#\t\u0015\u0001F:vE6LGOU3rk\u0016\u001cHoU3sm2,G/F\u0001C!\tY4)\u0003\u0002E\u0005\t!2+\u001e2nSR\u0014V-];fgR\u001cVM\u001d<mKRDqA\u0012\u0001C\u0002\u001bEq)\u0001\nlS2d'+Z9vKN$8+\u001a:wY\u0016$X#\u0001%\u0011\u0005mJ\u0015B\u0001&\u0003\u0005IY\u0015\u000e\u001c7SKF,Xm\u001d;TKJ4H.\u001a;\t\u000f1\u0003!\u0019!D\t\u001b\u0006!2\u000f^1ukN\u0014V-];fgR\u001cVM\u001d<mKR,\u0012A\u0014\t\u0003w=K!\u0001\u0015\u0002\u0003)M#\u0018\r^;t%\u0016\fX/Z:u'\u0016\u0014h\u000f\\3u\u0011\u001d\u0011\u0006\u00011A\u0005\nM\u000bqaX:feZ,'/F\u0001U!\rqQkV\u0005\u0003->\u0011aa\u00149uS>t\u0007C\u0001-`\u001b\u0005I&B\u0001.\\\u0003\u0019\u0019XM\u001d<fe*\u0011A,X\u0001\u0006U\u0016$H/\u001f\u0006\u0003=*\tq!Z2mSB\u001cX-\u0003\u0002a3\n11+\u001a:wKJDqA\u0019\u0001A\u0002\u0013%1-A\u0006`g\u0016\u0014h/\u001a:`I\u0015\fHC\u00013h!\tqQ-\u0003\u0002g\u001f\t!QK\\5u\u0011\u001dA\u0017-!AA\u0002Q\u000b1\u0001\u001f\u00132\u0011\u0019Q\u0007\u0001)Q\u0005)\u0006Aql]3sm\u0016\u0014\b\u0005C\u0004m\u0001\t\u0007I\u0011\u0003\u000f\u0002\u0017\t\f7/Z\"p]R,\u0007\u0010\u001e\u0005\u0007]\u0002\u0001\u000b\u0011B\u000f\u0002\u0019\t\f7/Z\"p]R,\u0007\u0010\u001e\u0011\t\u0011A\u0004\u0001R1A\u0005\u0012E\f\u0001cY8oi\u0016DH\u000fV8TKJ4H.\u001a;\u0016\u0003I\u0004Ba\u001d=\u001eu6\tAO\u0003\u0002vm\u0006I\u0011.\\7vi\u0006\u0014G.\u001a\u0006\u0003o>\t!bY8mY\u0016\u001cG/[8o\u0013\tIHOA\u0002NCB\u0004\"aO>\n\u0005q\u0014!a\u0003*fgR\u001cVM\u001d<mKRD\u0001B \u0001\t\u0002\u0003\u0006KA]\u0001\u0012G>tG/\u001a=u)>\u001cVM\u001d<mKR\u0004\u0003bBA\u0001\u0001\u0011\u0005\u00111A\u0001\u0006gR\f'\u000f\u001e\u000b\u0002S!9\u0011q\u0001\u0001\u0005\n\u0005%\u0011a\u00023p'R\f'\u000f\u001e\u000b\u0005\u0003\u0017\t\t\u0002E\u0003\u000f\u0003\u001b9\u0016&C\u0002\u0002\u0010=\u0011a\u0001V;qY\u0016\u0014\u0004bBA\n\u0003\u000b\u0001\r!K\u0001\ngR\f'\u000f\u001e)peRDq!a\u0006\u0001\t\u0003\tI\"\u0001\u0003ti>\u0004H#\u00013\b\u0011\u0005u!\u0001#\u0001\u0003\u0003?\tACU3tiN+(-\\5tg&|gnU3sm\u0016\u0014\bcA\u001e\u0002\"\u00199\u0011A\u0001E\u0001\u0005\u0005\r2cAA\u0011\u001b!9\u0001(!\t\u0005\u0002\u0005\u001dBCAA\u0010\u0011)\tY#!\tC\u0002\u0013\u0005\u0011QF\u0001\u0011!J{EkT\"P\u0019~3VIU*J\u001f:+\"!a\f\u0011\t\u0005E\u00121H\u0007\u0003\u0003gQA!!\u000e\u00028\u0005!A.\u00198h\u0015\t\tI$\u0001\u0003kCZ\f\u0017b\u0001\u0012\u00024!I\u0011qHA\u0011A\u0003%\u0011qF\u0001\u0012!J{EkT\"P\u0019~3VIU*J\u001f:\u0003\u0003\"CA\"\u0003C\u0011\r\u0011\"\u0001)\u0003m\u00196iX+O\u0017:{uKT0Q%>#vjQ(M?Z+%kU%P\u001d\"A\u0011qIA\u0011A\u0003%\u0011&\u0001\u000fT\u0007~+fj\u0013(P/:{\u0006KU(U\u001f\u000e{Ej\u0018,F%NKuJ\u0014\u0011"
)
public abstract class RestSubmissionServer implements Logging {
   private final String host;
   private final int requestedPort;
   private final SparkConf masterConf;
   private Option<Server> _server;
   private final String baseContext;
   private Map<String, RestServlet> contextToServlet;
   private transient Logger org$apache$spark$internal$Logging$$log_;
   private volatile boolean bitmap$0;

   public static int SC_UNKNOWN_PROTOCOL_VERSION() {
      return RestSubmissionServer$.MODULE$.SC_UNKNOWN_PROTOCOL_VERSION();
   }

   public static String PROTOCOL_VERSION() {
      return RestSubmissionServer$.MODULE$.PROTOCOL_VERSION();
   }

   private Map contextToServlet$lzycompute() {
      synchronized(this) {
         if (!this.bitmap$0) {
            this.contextToServlet = (Map)Predef$.MODULE$.Map().apply(Predef$.MODULE$.wrapRefArray(new Tuple2[]{Predef.ArrowAssoc$.MODULE$.$minus$greater$extension(Predef$.MODULE$.ArrowAssoc((new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"", "/create/*"})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.baseContext()}))), this.submitRequestServlet()), Predef.ArrowAssoc$.MODULE$.$minus$greater$extension(Predef$.MODULE$.ArrowAssoc((new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"", "/kill/*"})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.baseContext()}))), this.killRequestServlet()), Predef.ArrowAssoc$.MODULE$.$minus$greater$extension(Predef$.MODULE$.ArrowAssoc((new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"", "/status/*"})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.baseContext()}))), this.statusRequestServlet()), Predef.ArrowAssoc$.MODULE$.$minus$greater$extension(Predef$.MODULE$.ArrowAssoc("/*"), new ErrorServlet())}));
            this.bitmap$0 = true;
         }

         BoxedUnit var10000 = BoxedUnit.UNIT;
      }

      return this.contextToServlet;
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

   public String host() {
      return this.host;
   }

   public int requestedPort() {
      return this.requestedPort;
   }

   public SparkConf masterConf() {
      return this.masterConf;
   }

   public abstract SubmitRequestServlet submitRequestServlet();

   public abstract KillRequestServlet killRequestServlet();

   public abstract StatusRequestServlet statusRequestServlet();

   private Option<Server> _server() {
      return this._server;
   }

   private void _server_$eq(Option<Server> x$1) {
      this._server = x$1;
   }

   public String baseContext() {
      return this.baseContext;
   }

   public Map<String, RestServlet> contextToServlet() {
      return this.bitmap$0 ? this.contextToServlet : this.contextToServlet$lzycompute();
   }

   public int start() {
      Tuple2 var2 = Utils$.MODULE$.startServiceOnPort(this.requestedPort(), new Serializable(this) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final RestSubmissionServer $outer;

         public final Tuple2<Server, Object> apply(int startPort) {
            return this.$outer.org$apache$spark$deploy$rest$RestSubmissionServer$$doStart(startPort);
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
            }
         }
      }, this.masterConf(), Utils$.MODULE$.startServiceOnPort$default$4());
      if (var2 != null) {
         Server server = (Server)var2._1();
         int boundPort = var2._2$mcI$sp();
         Tuple2 var5 = new Tuple2(server, BoxesRunTime.boxToInteger(boundPort));
         Server server = (Server)var5._1();
         int boundPort = var5._2$mcI$sp();
         this._server_$eq(new Some(server));
         this.logInfo(new Serializable(this, boundPort) {
            public static final long serialVersionUID = 0L;
            private final int boundPort$1;

            public final String apply() {
               return (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Started REST server for submitting applications on port ", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{BoxesRunTime.boxToInteger(this.boundPort$1)}));
            }

            public {
               this.boundPort$1 = boundPort$1;
            }
         });
         return boundPort;
      } else {
         throw new MatchError(var2);
      }
   }

   public Tuple2<Server, Object> org$apache$spark$deploy$rest$RestSubmissionServer$$doStart(int startPort) {
      QueuedThreadPool threadPool = new QueuedThreadPool();
      threadPool.setDaemon(true);
      Server server = new Server(threadPool);
      ServerConnector connector = new ServerConnector(server, (Executor)null, new ScheduledExecutorScheduler("RestSubmissionServer-JettyScheduler", true), (ByteBufferPool)null, -1, -1, new ConnectionFactory[]{new HttpConnectionFactory()});
      connector.setHost(this.host());
      connector.setPort(startPort);
      server.addConnector(connector);
      ServletContextHandler mainHandler = new ServletContextHandler();
      mainHandler.setServer(server);
      mainHandler.setContextPath("/");
      this.contextToServlet().foreach(new Serializable(this, mainHandler) {
         public static final long serialVersionUID = 0L;
         private final ServletContextHandler mainHandler$1;

         public final void apply(Tuple2<String, RestServlet> x0$1) {
            if (x0$1 != null) {
               String prefix = (String)x0$1._1();
               RestServlet servlet = (RestServlet)x0$1._2();
               this.mainHandler$1.addServlet(new ServletHolder(servlet), prefix);
               BoxedUnit var5 = BoxedUnit.UNIT;
            } else {
               throw new MatchError(x0$1);
            }
         }

         public {
            this.mainHandler$1 = mainHandler$1;
         }
      });
      server.setHandler(mainHandler);
      server.start();
      int boundPort = connector.getLocalPort();
      return new Tuple2<Server, Object>(server, BoxesRunTime.boxToInteger(boundPort));
   }

   public void stop() {
      this._server().foreach(new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final void apply(Server x$2) {
            x$2.stop();
         }
      });
   }

   public RestSubmissionServer(String host, int requestedPort, SparkConf masterConf) {
      this.host = host;
      this.requestedPort = requestedPort;
      this.masterConf = masterConf;
      Logging$class.$init$(this);
      this._server = None$.MODULE$;
      this.baseContext = (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"/", "/submissions"})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{RestSubmissionServer$.MODULE$.PROTOCOL_VERSION()}));
   }
}
