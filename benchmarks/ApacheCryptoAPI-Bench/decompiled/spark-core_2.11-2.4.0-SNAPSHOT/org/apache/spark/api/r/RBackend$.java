package org.apache.spark.api.r;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import org.apache.spark.SparkConf;
import org.apache.spark.internal.Logging;
import org.apache.spark.internal.Logging$class;
import org.slf4j.Logger;
import scala.Function0;
import scala.Serializable;
import scala.collection.mutable.StringBuilder;

public final class RBackend$ implements Logging {
   public static final RBackend$ MODULE$;
   private transient Logger org$apache$spark$internal$Logging$$log_;

   static {
      new RBackend$();
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

   public void main(String[] args) {
      if (args.length < 1) {
         System.err.println("Usage: RBackend <tempFilePath>");
         System.exit(-1);
      }

      RBackend sparkRBackend = new RBackend();

      try {
         int boundPort = sparkRBackend.init();
         ServerSocket serverSocket = new ServerSocket(0, 1, InetAddress.getByName("localhost"));
         int listenPort = serverSocket.getLocalPort();
         SparkConf conf = new SparkConf();
         int backendConnectionTimeout = conf.getInt("spark.r.backendConnectionTimeout", SparkRDefaults$.MODULE$.DEFAULT_CONNECTION_TIMEOUT());
         String path = args[0];
         File f = new File((new StringBuilder()).append((Object)path).append((Object)".tmp").toString());
         DataOutputStream dos = new DataOutputStream(new FileOutputStream(f));
         dos.writeInt(boundPort);
         dos.writeInt(listenPort);
         SerDe$.MODULE$.writeString(dos, (String)RUtils$.MODULE$.rPackages().getOrElse(new Serializable() {
            public static final long serialVersionUID = 0L;

            public final String apply() {
               return "";
            }
         }));
         dos.writeInt(backendConnectionTimeout);
         dos.close();
         f.renameTo(new File(path));
         (new Thread(sparkRBackend, serverSocket) {
            private final RBackend sparkRBackend$1;
            private final ServerSocket serverSocket$1;

            public void run() {
               byte[] buf = new byte[1024];
               this.serverSocket$1.setSoTimeout(10000);

               try {
                  Socket inSocket = this.serverSocket$1.accept();
                  this.serverSocket$1.close();
                  inSocket.getInputStream().read(buf);
               } finally {
                  this.sparkRBackend$1.close();
                  System.exit(0);
               }

            }

            public {
               this.sparkRBackend$1 = sparkRBackend$1;
               this.serverSocket$1 = serverSocket$1;
               this.setDaemon(true);
            }
         }).start();
         sparkRBackend.run();
      } catch (IOException var12) {
         this.logError(new Serializable() {
            public static final long serialVersionUID = 0L;

            public final String apply() {
               return "Server shutting down: failed with exception ";
            }
         }, var12);
         sparkRBackend.close();
         System.exit(1);
      }

      System.exit(0);
   }

   private RBackend$() {
      MODULE$ = this;
      Logging$class.$init$(this);
      this.initializeLogIfNecessary(true);
   }
}
