package org.apache.spark.api.python;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import org.apache.spark.SparkEnv$;
import org.apache.spark.SparkException;
import org.apache.spark.internal.Logging;
import org.apache.spark.internal.Logging$class;
import org.apache.spark.util.RedirectThread;
import org.apache.spark.util.RedirectThread$;
import org.apache.spark.util.Utils$;
import org.slf4j.Logger;
import scala.Array$;
import scala.Function0;
import scala.Option;
import scala.Option$;
import scala.Predef$;
import scala.Serializable;
import scala.StringContext;
import scala.collection.JavaConverters$;
import scala.collection.immutable.Map;
import scala.collection.immutable.StringOps;
import scala.collection.mutable.Queue;
import scala.collection.mutable.StringBuilder;
import scala.collection.mutable.WeakHashMap;
import scala.reflect.ClassTag$;
import scala.reflect.ScalaSignature;
import scala.runtime.BoxedUnit;
import scala.runtime.BoxesRunTime;
import scala.sys.package$;

@ScalaSignature(
   bytes = "\u0006\u0001\u0005}f!B\u0001\u0003\u0001\u0019a!a\u0005)zi\"|gnV8sW\u0016\u0014h)Y2u_JL(BA\u0002\u0005\u0003\u0019\u0001\u0018\u0010\u001e5p]*\u0011QAB\u0001\u0004CBL'BA\u0004\t\u0003\u0015\u0019\b/\u0019:l\u0015\tI!\"\u0001\u0004ba\u0006\u001c\u0007.\u001a\u0006\u0002\u0017\u0005\u0019qN]4\u0014\u0007\u0001i1\u0003\u0005\u0002\u000f#5\tqBC\u0001\u0011\u0003\u0015\u00198-\u00197b\u0013\t\u0011rB\u0001\u0004B]f\u0014VM\u001a\t\u0003)]i\u0011!\u0006\u0006\u0003-\u0019\t\u0001\"\u001b8uKJt\u0017\r\\\u0005\u00031U\u0011q\u0001T8hO&tw\r\u0003\u0005\u001b\u0001\t\u0005\t\u0015!\u0003\u001d\u0003)\u0001\u0018\u0010\u001e5p]\u0016CXmY\u0002\u0001!\ti\u0002E\u0004\u0002\u000f=%\u0011qdD\u0001\u0007!J,G-\u001a4\n\u0005\u0005\u0012#AB*ue&twM\u0003\u0002 \u001f!AA\u0005\u0001B\u0001B\u0003%Q%A\u0004f]Z4\u0016M]:\u0011\tu1C\u0004H\u0005\u0003O\t\u00121!T1q\u0011\u0015I\u0003\u0001\"\u0001+\u0003\u0019a\u0014N\\5u}Q\u00191&\f\u0018\u0011\u00051\u0002Q\"\u0001\u0002\t\u000biA\u0003\u0019\u0001\u000f\t\u000b\u0011B\u0003\u0019A\u0013\t\u000fA\u0002!\u0019!C\u0001c\u0005IQo]3EC\u0016lwN\\\u000b\u0002eA\u0011abM\u0005\u0003i=\u0011qAQ8pY\u0016\fg\u000e\u0003\u00047\u0001\u0001\u0006IAM\u0001\u000bkN,G)Y3n_:\u0004\u0003b\u0002\u001d\u0001\u0005\u0004%\t!O\u0001\rI\u0006,Wn\u001c8N_\u0012,H.Z\u000b\u00029!11\b\u0001Q\u0001\nq\tQ\u0002Z1f[>tWj\u001c3vY\u0016\u0004\u0003bB\u001f\u0001\u0005\u0004%\t!O\u0001\ro>\u00148.\u001a:N_\u0012,H.\u001a\u0005\u0007\u007f\u0001\u0001\u000b\u0011\u0002\u000f\u0002\u001b]|'o[3s\u001b>$W\u000f\\3!\u0011\u001d\t\u0005\u00011A\u0005\u0002\t\u000ba\u0001Z1f[>tW#A\"\u0011\u0005\u0011KU\"A#\u000b\u0005\u0019;\u0015\u0001\u00027b]\u001eT\u0011\u0001S\u0001\u0005U\u00064\u0018-\u0003\u0002K\u000b\n9\u0001K]8dKN\u001c\bb\u0002'\u0001\u0001\u0004%\t!T\u0001\u000bI\u0006,Wn\u001c8`I\u0015\fHC\u0001(R!\tqq*\u0003\u0002Q\u001f\t!QK\\5u\u0011\u001d\u00116*!AA\u0002\r\u000b1\u0001\u001f\u00132\u0011\u0019!\u0006\u0001)Q\u0005\u0007\u00069A-Y3n_:\u0004\u0003b\u0002,\u0001\u0005\u0004%\taV\u0001\u000bI\u0006,Wn\u001c8I_N$X#\u0001-\u0011\u0005ecV\"\u0001.\u000b\u0005m;\u0015a\u00018fi&\u0011QL\u0017\u0002\f\u0013:,G/\u00113ee\u0016\u001c8\u000f\u0003\u0004`\u0001\u0001\u0006I\u0001W\u0001\fI\u0006,Wn\u001c8I_N$\b\u0005C\u0004b\u0001\u0001\u0007I\u0011\u00012\u0002\u0015\u0011\fW-\\8o!>\u0014H/F\u0001d!\tqA-\u0003\u0002f\u001f\t\u0019\u0011J\u001c;\t\u000f\u001d\u0004\u0001\u0019!C\u0001Q\u0006qA-Y3n_:\u0004vN\u001d;`I\u0015\fHC\u0001(j\u0011\u001d\u0011f-!AA\u0002\rDaa\u001b\u0001!B\u0013\u0019\u0017a\u00033bK6|g\u000eU8si\u0002Bq!\u001c\u0001C\u0002\u0013\u0005a.A\u0007eC\u0016lwN\\,pe.,'o]\u000b\u0002_B!\u0001/^<d\u001b\u0005\t(B\u0001:t\u0003\u001diW\u000f^1cY\u0016T!\u0001^\b\u0002\u0015\r|G\u000e\\3di&|g.\u0003\u0002wc\nYq+Z1l\u0011\u0006\u001c\b.T1q!\tI\u00060\u0003\u0002z5\n11k\\2lKRDaa\u001f\u0001!\u0002\u0013y\u0017A\u00043bK6|gnV8sW\u0016\u00148\u000f\t\u0005\b{\u0002\u0011\r\u0011\"\u0001\u007f\u0003-IG\r\\3X_J\\WM]:\u0016\u0003}\u0004B\u0001]A\u0001o&\u0019\u00111A9\u0003\u000bE+X-^3\t\u000f\u0005\u001d\u0001\u0001)A\u0005\u007f\u0006a\u0011\u000e\u001a7f/>\u00148.\u001a:tA!I\u00111\u0002\u0001A\u0002\u0013\u0005\u0011QB\u0001\rY\u0006\u001cH/Q2uSZLG/_\u000b\u0003\u0003\u001f\u00012ADA\t\u0013\r\t\u0019b\u0004\u0002\u0005\u0019>tw\rC\u0005\u0002\u0018\u0001\u0001\r\u0011\"\u0001\u0002\u001a\u0005\u0001B.Y:u\u0003\u000e$\u0018N^5us~#S-\u001d\u000b\u0004\u001d\u0006m\u0001\"\u0003*\u0002\u0016\u0005\u0005\t\u0019AA\b\u0011!\ty\u0002\u0001Q!\n\u0005=\u0011!\u00047bgR\f5\r^5wSRL\b\u0005C\u0005\u0002$\u0001\u0001\r\u0011\"\u0001\u0002&\u0005i1/[7qY\u0016<vN]6feN,\"!a\n\u0011\tA,xo\u0011\u0005\n\u0003W\u0001\u0001\u0019!C\u0001\u0003[\t\u0011c]5na2,wk\u001c:lKJ\u001cx\fJ3r)\rq\u0015q\u0006\u0005\n%\u0006%\u0012\u0011!a\u0001\u0003OA\u0001\"a\r\u0001A\u0003&\u0011qE\u0001\u000fg&l\u0007\u000f\\3X_J\\WM]:!\u0011!\t9\u0004\u0001b\u0001\n\u0003I\u0014A\u00039zi\"|g\u000eU1uQ\"9\u00111\b\u0001!\u0002\u0013a\u0012a\u00039zi\"|g\u000eU1uQ\u0002Bq!a\u0010\u0001\t\u0003\t\t%\u0001\u0004de\u0016\fG/\u001a\u000b\u0002o\"9\u0011Q\t\u0001\u0005\n\u0005\u0005\u0013aE2sK\u0006$X\r\u00165s_V<\u0007\u000eR1f[>t\u0007bBA%\u0001\u0011%\u0011\u0011I\u0001\u0013GJ,\u0017\r^3TS6\u0004H.Z,pe.,'\u000fC\u0004\u0002N\u0001!I!a\u0014\u0002\u0017M$\u0018M\u001d;EC\u0016lwN\u001c\u000b\u0002\u001d\"9\u00111\u000b\u0001\u0005\n\u0005U\u0013a\u0006:fI&\u0014Xm\u0019;TiJ,\u0017-\\:U_N#H-\u001a:s)\u0015q\u0015qKA4\u0011!\tI&!\u0015A\u0002\u0005m\u0013AB:uI>,H\u000f\u0005\u0003\u0002^\u0005\rTBAA0\u0015\r\t\tgR\u0001\u0003S>LA!!\u001a\u0002`\tY\u0011J\u001c9viN#(/Z1n\u0011!\tI'!\u0015A\u0002\u0005m\u0013AB:uI\u0016\u0014(O\u0002\u0004\u0002n\u0001!\u0011q\u000e\u0002\u000e\u001b>t\u0017\u000e^8s)\"\u0014X-\u00193\u0014\t\u0005-\u0014\u0011\u000f\t\u0004\t\u0006M\u0014bAA;\u000b\n1A\u000b\u001b:fC\u0012Dq!KA6\t\u0003\tI\b\u0006\u0002\u0002|A!\u0011QPA6\u001b\u0005\u0001\u0001\u0002CAA\u0003W\"\t%a\u0014\u0002\u0007I,h\u000eC\u0004\u0002\u0006\u0002!I!a\u0014\u0002%\rdW-\u00198va&#G.Z,pe.,'o\u001d\u0005\b\u0003\u0013\u0003A\u0011BA(\u0003)\u0019Ho\u001c9EC\u0016lwN\u001c\u0005\b\u0003\u001b\u0003A\u0011AA(\u0003\u0011\u0019Ho\u001c9\t\u000f\u0005E\u0005\u0001\"\u0001\u0002\u0014\u0006Q1\u000f^8q/>\u00148.\u001a:\u0015\u00079\u000b)\nC\u0004\u0002\u0018\u0006=\u0005\u0019A<\u0002\r]|'o[3s\u0011\u001d\tY\n\u0001C\u0001\u0003;\u000bQB]3mK\u0006\u001cXmV8sW\u0016\u0014Hc\u0001(\u0002 \"9\u0011qSAM\u0001\u00049xaBAR\u0005!%\u0011QU\u0001\u0014!f$\bn\u001c8X_J\\WM\u001d$bGR|'/\u001f\t\u0004Y\u0005\u001dfAB\u0001\u0003\u0011\u0013\tIkE\u0002\u0002(6Aq!KAT\t\u0003\ti\u000b\u0006\u0002\u0002&\"I\u0011\u0011WAT\u0005\u0004%\tAY\u0001\u0018!J{5)R*T?^\u000b\u0015\nV0U\u00136+u*\u0016+`\u001bNC\u0001\"!.\u0002(\u0002\u0006IaY\u0001\u0019!J{5)R*T?^\u000b\u0015\nV0U\u00136+u*\u0016+`\u001bN\u0003\u0003\"CA]\u0003O\u0013\r\u0011\"\u0001c\u0003YIE\tT#`/>\u00136*\u0012*`)&kUiT+U?6\u001b\u0006\u0002CA_\u0003O\u0003\u000b\u0011B2\u0002/%#E*R0X\u001fJ[UIU0U\u00136+u*\u0016+`\u001bN\u0003\u0003"
)
public class PythonWorkerFactory implements Logging {
   public final String org$apache$spark$api$python$PythonWorkerFactory$$pythonExec;
   private final Map<String, String> envVars;
   private final boolean useDaemon;
   private final String daemonModule;
   private final String workerModule;
   private Process daemon;
   private final InetAddress daemonHost;
   private int daemonPort;
   private final WeakHashMap<Socket, Object> daemonWorkers;
   private final Queue<Socket> idleWorkers;
   private long lastActivity;
   private WeakHashMap<Socket, Process> simpleWorkers;
   private final String pythonPath;
   private transient Logger org$apache$spark$internal$Logging$$log_;

   public static int IDLE_WORKER_TIMEOUT_MS() {
      return PythonWorkerFactory$.MODULE$.IDLE_WORKER_TIMEOUT_MS();
   }

   public static int PROCESS_WAIT_TIMEOUT_MS() {
      return PythonWorkerFactory$.MODULE$.PROCESS_WAIT_TIMEOUT_MS();
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

   public boolean useDaemon() {
      return this.useDaemon;
   }

   public String daemonModule() {
      return this.daemonModule;
   }

   public String workerModule() {
      return this.workerModule;
   }

   public Process daemon() {
      return this.daemon;
   }

   public void daemon_$eq(Process x$1) {
      this.daemon = x$1;
   }

   public InetAddress daemonHost() {
      return this.daemonHost;
   }

   public int daemonPort() {
      return this.daemonPort;
   }

   public void daemonPort_$eq(int x$1) {
      this.daemonPort = x$1;
   }

   public WeakHashMap<Socket, Object> daemonWorkers() {
      return this.daemonWorkers;
   }

   public Queue<Socket> idleWorkers() {
      return this.idleWorkers;
   }

   public long lastActivity() {
      return this.lastActivity;
   }

   public void lastActivity_$eq(long x$1) {
      this.lastActivity = x$1;
   }

   public WeakHashMap<Socket, Process> simpleWorkers() {
      return this.simpleWorkers;
   }

   public void simpleWorkers_$eq(WeakHashMap<Socket, Process> x$1) {
      this.simpleWorkers = x$1;
   }

   public String pythonPath() {
      return this.pythonPath;
   }

   public Socket create() {
      Socket var10000;
      if (this.useDaemon()) {
         synchronized(this) {
            if (this.idleWorkers().size() > 0) {
               return (Socket)this.idleWorkers().dequeue();
            }

            BoxedUnit var3 = BoxedUnit.UNIT;
         }

         var10000 = this.createThroughDaemon();
      } else {
         var10000 = this.createSimpleWorker();
      }

      return var10000;
   }

   private Socket createThroughDaemon() {
      Socket var2;
      synchronized(this) {
         this.startDaemon();
         var2 = this.liftedTree1$1();
      }

      return var2;
   }

   private Socket createSimpleWorker() {
      ServerSocket serverSocket = null;

      ServerSocket var10000;
      try {
         serverSocket = new ServerSocket(0, 1, InetAddress.getByAddress((byte[])Array$.MODULE$.apply(Predef$.MODULE$.wrapByteArray(new byte[]{127, 0, 0, 1}), ClassTag$.MODULE$.Byte())));
         ProcessBuilder pb = new ProcessBuilder(Arrays.asList((Object[])(new String[]{this.org$apache$spark$api$python$PythonWorkerFactory$$pythonExec, "-m", this.workerModule()})));
         java.util.Map workerEnv = pb.environment();
         workerEnv.putAll((java.util.Map)JavaConverters$.MODULE$.mapAsJavaMapConverter(this.envVars).asJava());
         workerEnv.put("PYTHONPATH", this.pythonPath());
         workerEnv.put("PYTHONUNBUFFERED", "YES");
         Process worker = pb.start();
         this.redirectStreamsToStderr(worker.getInputStream(), worker.getErrorStream());
         OutputStreamWriter out = new OutputStreamWriter(worker.getOutputStream(), StandardCharsets.UTF_8);
         out.write((new StringBuilder()).append(serverSocket.getLocalPort()).append((Object)"\n").toString());
         out.flush();
         serverSocket.setSoTimeout(10000);

         try {
            Socket socket = serverSocket.accept();
            this.simpleWorkers().put(socket, worker);
         } catch (Exception var11) {
            throw new SparkException("Python worker did not connect back in time", var11);
         }
      } finally {
         var10000 = serverSocket;
         if (serverSocket != null) {
            var10000 = serverSocket;
            serverSocket.close();
         }

      }

      return var10000;
   }

   private synchronized void startDaemon() {
      if (this.daemon() == null) {
         try {
            ProcessBuilder pb = new ProcessBuilder(Arrays.asList((Object[])(new String[]{this.org$apache$spark$api$python$PythonWorkerFactory$$pythonExec, "-m", this.daemonModule()})));
            java.util.Map workerEnv = pb.environment();
            workerEnv.putAll((java.util.Map)JavaConverters$.MODULE$.mapAsJavaMapConverter(this.envVars).asJava());
            workerEnv.put("PYTHONPATH", this.pythonPath());
            workerEnv.put("PYTHONUNBUFFERED", "YES");
            this.daemon_$eq(pb.start());
            DataInputStream in = new DataInputStream(this.daemon().getInputStream());
            this.daemonPort_$eq(in.readInt());
            this.redirectStreamsToStderr(in, this.daemon().getErrorStream());
         } catch (Exception var10) {
            String stderr = (String)Option$.MODULE$.apply(this.daemon()).flatMap(new Serializable(this) {
               public static final long serialVersionUID = 0L;

               public final Option<String> apply(Process d) {
                  return Utils$.MODULE$.getStderr(d, (long)PythonWorkerFactory$.MODULE$.PROCESS_WAIT_TIMEOUT_MS());
               }
            }).getOrElse(new Serializable(this) {
               public static final long serialVersionUID = 0L;

               public final String apply() {
                  return "";
               }
            });
            this.stopDaemon();
            String var3 = "";
            if (stderr == null) {
               if (var3 == null) {
                  throw var10;
               }
            } else if (stderr.equals(var3)) {
               throw var10;
            }

            String formattedStderr = stderr.replace("\n", "\n  ");
            String errorMessage = (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"\n              |Error from python worker:\n              |  ", "\n              |PYTHONPATH was:\n              |  ", "\n              |", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{formattedStderr, this.pythonPath(), var10}));
            SparkException wrappedException = new SparkException((new StringOps(Predef$.MODULE$.augmentString(errorMessage))).stripMargin());
            wrappedException.setStackTrace(var10.getStackTrace());
            throw wrappedException;
         }
      }
   }

   private void redirectStreamsToStderr(InputStream stdout, InputStream stderr) {
      try {
         (new RedirectThread(stdout, System.err, (new StringBuilder()).append((Object)"stdout reader for ").append((Object)this.org$apache$spark$api$python$PythonWorkerFactory$$pythonExec).toString(), RedirectThread$.MODULE$.$lessinit$greater$default$4())).start();
         (new RedirectThread(stderr, System.err, (new StringBuilder()).append((Object)"stderr reader for ").append((Object)this.org$apache$spark$api$python$PythonWorkerFactory$$pythonExec).toString(), RedirectThread$.MODULE$.$lessinit$greater$default$4())).start();
      } catch (Exception var4) {
         this.logError(new Serializable(this) {
            public static final long serialVersionUID = 0L;

            public final String apply() {
               return "Exception in redirecting streams";
            }
         }, var4);
      }

   }

   public void org$apache$spark$api$python$PythonWorkerFactory$$cleanupIdleWorkers() {
      while(this.idleWorkers().nonEmpty()) {
         Socket worker = (Socket)this.idleWorkers().dequeue();

         try {
            worker.close();
         } catch (Exception var3) {
            this.logWarning(new Serializable(this) {
               public static final long serialVersionUID = 0L;

               public final String apply() {
                  return "Failed to close worker socket";
               }
            }, var3);
         }
      }

   }

   private void stopDaemon() {
      synchronized(this) {
         if (this.useDaemon()) {
            this.org$apache$spark$api$python$PythonWorkerFactory$$cleanupIdleWorkers();
            if (this.daemon() != null) {
               this.daemon().destroy();
            }

            this.daemon_$eq((Process)null);
            this.daemonPort_$eq(0);
            BoxedUnit var10000 = BoxedUnit.UNIT;
         } else {
            this.simpleWorkers().mapValues(new Serializable(this) {
               public static final long serialVersionUID = 0L;

               public final void apply(Process x$1) {
                  x$1.destroy();
               }
            });
         }

      }
   }

   public void stop() {
      this.stopDaemon();
   }

   public void stopWorker(Socket worker) {
      synchronized(this) {
         if (this.useDaemon()) {
            if (this.daemon() == null) {
               BoxedUnit var10000 = BoxedUnit.UNIT;
            } else {
               this.daemonWorkers().get(worker).foreach(new Serializable(this) {
                  public static final long serialVersionUID = 0L;
                  // $FF: synthetic field
                  private final PythonWorkerFactory $outer;

                  public final void apply(int pid) {
                     this.apply$mcVI$sp(pid);
                  }

                  public void apply$mcVI$sp(int pid) {
                     DataOutputStream output = new DataOutputStream(this.$outer.daemon().getOutputStream());
                     output.writeInt(pid);
                     output.flush();
                     this.$outer.daemon().getOutputStream().flush();
                  }

                  public {
                     if ($outer == null) {
                        throw null;
                     } else {
                        this.$outer = $outer;
                     }
                  }
               });
               BoxedUnit var4 = BoxedUnit.UNIT;
            }
         } else {
            this.simpleWorkers().get(worker).foreach(new Serializable(this) {
               public static final long serialVersionUID = 0L;

               public final void apply(Process x$2) {
                  x$2.destroy();
               }
            });
            BoxedUnit var5 = BoxedUnit.UNIT;
         }
      }

      worker.close();
   }

   public void releaseWorker(Socket worker) {
      if (this.useDaemon()) {
         synchronized(this) {
            this.lastActivity_$eq(System.currentTimeMillis());
            this.idleWorkers().enqueue(Predef$.MODULE$.wrapRefArray((Object[])(new Socket[]{worker})));
            BoxedUnit var10000 = BoxedUnit.UNIT;
         }
      } else {
         try {
            worker.close();
         } catch (Exception var4) {
            this.logWarning(new Serializable(this) {
               public static final long serialVersionUID = 0L;

               public final String apply() {
                  return "Failed to close worker socket";
               }
            }, var4);
         }
      }

   }

   private final Socket createSocket$1() {
      Socket socket = new Socket(this.daemonHost(), this.daemonPort());
      int pid = (new DataInputStream(socket.getInputStream())).readInt();
      if (pid < 0) {
         throw new IllegalStateException((new StringBuilder()).append((Object)"Python daemon failed to launch worker with code ").append((Object)BoxesRunTime.boxToInteger(pid)).toString());
      } else {
         this.daemonWorkers().put(socket, BoxesRunTime.boxToInteger(pid));
         return socket;
      }
   }

   private final Socket liftedTree1$1() {
      Socket var10000;
      try {
         var10000 = this.createSocket$1();
      } catch (SocketException var2) {
         this.logWarning(new Serializable(this) {
            public static final long serialVersionUID = 0L;

            public final String apply() {
               return "Failed to open socket to Python daemon:";
            }
         }, var2);
         this.logWarning(new Serializable(this) {
            public static final long serialVersionUID = 0L;

            public final String apply() {
               return "Assuming that daemon unexpectedly quit, attempting to restart";
            }
         });
         this.stopDaemon();
         this.startDaemon();
         var10000 = this.createSocket$1();
      }

      return var10000;
   }

   public PythonWorkerFactory(String pythonExec, Map<String, String> envVars) {
      this.org$apache$spark$api$python$PythonWorkerFactory$$pythonExec = pythonExec;
      this.envVars = envVars;
      Logging$class.$init$(this);
      boolean useDaemonEnabled = SparkEnv$.MODULE$.get().conf().getBoolean("spark.python.use.daemon", true);
      this.useDaemon = !System.getProperty("os.name").startsWith("Windows") && useDaemonEnabled;
      this.daemonModule = (String)SparkEnv$.MODULE$.get().conf().getOption("spark.python.daemon.module").map(new Serializable(this) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final PythonWorkerFactory $outer;

         public final String apply(String value) {
            this.$outer.logInfo(new Serializable(this, value) {
               public static final long serialVersionUID = 0L;
               private final String value$1;

               public final String apply() {
                  return (new StringBuilder()).append((Object)(new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Python daemon module in PySpark is set to [", "] in 'spark.python.daemon.module', "})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.value$1}))).append((Object)"using this to start the daemon up. Note that this configuration only has an effect when ").append((Object)"'spark.python.use.daemon' is enabled and the platform is not Windows.").toString();
               }

               public {
                  this.value$1 = value$1;
               }
            });
            return value;
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
            }
         }
      }).getOrElse(new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final String apply() {
            return "pyspark.daemon";
         }
      });
      this.workerModule = (String)SparkEnv$.MODULE$.get().conf().getOption("spark.python.worker.module").map(new Serializable(this) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final PythonWorkerFactory $outer;

         public final String apply(String value) {
            this.$outer.logInfo(new Serializable(this, value) {
               public static final long serialVersionUID = 0L;
               private final String value$2;

               public final String apply() {
                  return (new StringBuilder()).append((Object)(new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Python worker module in PySpark is set to [", "] in 'spark.python.worker.module', "})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.value$2}))).append((Object)"using this to start the worker up. Note that this configuration only has an effect when ").append((Object)"'spark.python.use.daemon' is disabled or the platform is Windows.").toString();
               }

               public {
                  this.value$2 = value$2;
               }
            });
            return value;
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
            }
         }
      }).getOrElse(new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final String apply() {
            return "pyspark.worker";
         }
      });
      this.daemon = null;
      this.daemonHost = InetAddress.getByAddress((byte[])Array$.MODULE$.apply(Predef$.MODULE$.wrapByteArray(new byte[]{127, 0, 0, 1}), ClassTag$.MODULE$.Byte()));
      this.daemonPort = 0;
      this.daemonWorkers = new WeakHashMap<Socket, Object>();
      this.idleWorkers = new Queue<Socket>();
      this.lastActivity = 0L;
      (new MonitorThread()).start();
      this.simpleWorkers = new WeakHashMap<Socket, Process>();
      this.pythonPath = PythonUtils$.MODULE$.mergePythonPaths(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{PythonUtils$.MODULE$.sparkPythonPath(), (String)envVars.getOrElse("PYTHONPATH", new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final String apply() {
            return "";
         }
      }), (String)package$.MODULE$.env().getOrElse("PYTHONPATH", new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final String apply() {
            return "";
         }
      })})));
   }

   public class MonitorThread extends Thread {
      // $FF: synthetic field
      public final PythonWorkerFactory $outer;

      public void run() {
         while(true) {
            synchronized(this) {
               if (this.org$apache$spark$api$python$PythonWorkerFactory$MonitorThread$$$outer().lastActivity() + (long)PythonWorkerFactory$.MODULE$.IDLE_WORKER_TIMEOUT_MS() < System.currentTimeMillis()) {
                  this.org$apache$spark$api$python$PythonWorkerFactory$MonitorThread$$$outer().org$apache$spark$api$python$PythonWorkerFactory$$cleanupIdleWorkers();
                  this.org$apache$spark$api$python$PythonWorkerFactory$MonitorThread$$$outer().lastActivity_$eq(System.currentTimeMillis());
                  BoxedUnit var10000 = BoxedUnit.UNIT;
               } else {
                  BoxedUnit var3 = BoxedUnit.UNIT;
               }
            }

            Thread.sleep(10000L);
         }
      }

      // $FF: synthetic method
      public PythonWorkerFactory org$apache$spark$api$python$PythonWorkerFactory$MonitorThread$$$outer() {
         return this.$outer;
      }

      public MonitorThread() {
         if (PythonWorkerFactory.this == null) {
            throw null;
         } else {
            this.$outer = PythonWorkerFactory.this;
            super((new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Idle Worker Monitor for ", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{PythonWorkerFactory.this.org$apache$spark$api$python$PythonWorkerFactory$$pythonExec})));
            this.setDaemon(true);
         }
      }
   }
}
