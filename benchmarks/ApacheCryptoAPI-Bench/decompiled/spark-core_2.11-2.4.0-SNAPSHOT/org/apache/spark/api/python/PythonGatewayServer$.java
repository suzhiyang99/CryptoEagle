package org.apache.spark.api.python;

import java.io.DataOutputStream;
import java.net.Socket;
import org.apache.spark.internal.Logging;
import org.apache.spark.internal.Logging$class;
import org.apache.spark.util.Utils$;
import org.slf4j.Logger;
import py4j.GatewayServer;
import scala.Function0;
import scala.Predef$;
import scala.Serializable;
import scala.StringContext;
import scala.collection.immutable.StringOps;
import scala.runtime.BoxesRunTime;
import scala.sys.package$;

public final class PythonGatewayServer$ implements Logging {
   public static final PythonGatewayServer$ MODULE$;
   private transient Logger org$apache$spark$internal$Logging$$log_;

   static {
      new PythonGatewayServer$();
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
      Utils$.MODULE$.tryOrExit(new Serializable() {
         public static final long serialVersionUID = 0L;

         public final void apply() {
            this.apply$mcV$sp();
         }

         public void apply$mcV$sp() {
            GatewayServer gatewayServer = new GatewayServer((Object)null, 0);
            gatewayServer.start();
            int boundPort = gatewayServer.getListeningPort();
            if (boundPort == -1) {
               PythonGatewayServer$.MODULE$.logError(new Serializable(this) {
                  public static final long serialVersionUID = 0L;

                  public final String apply() {
                     return "GatewayServer failed to bind; exiting";
                  }
               });
               System.exit(1);
            } else {
               PythonGatewayServer$.MODULE$.logDebug(new Serializable(this, boundPort) {
                  public static final long serialVersionUID = 0L;
                  private final int boundPort$1;

                  public final String apply() {
                     return (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Started PythonGatewayServer on port ", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{BoxesRunTime.boxToInteger(this.boundPort$1)}));
                  }

                  public {
                     this.boundPort$1 = boundPort$1;
                  }
               });
            }

            String callbackHost = (String)package$.MODULE$.env().apply("_PYSPARK_DRIVER_CALLBACK_HOST");
            int callbackPort = (new StringOps(Predef$.MODULE$.augmentString((String)package$.MODULE$.env().apply("_PYSPARK_DRIVER_CALLBACK_PORT")))).toInt();
            PythonGatewayServer$.MODULE$.logDebug(new Serializable(this, callbackHost, callbackPort) {
               public static final long serialVersionUID = 0L;
               private final String callbackHost$1;
               private final int callbackPort$1;

               public final String apply() {
                  return (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Communicating GatewayServer port to Python driver at ", ":", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.callbackHost$1, BoxesRunTime.boxToInteger(this.callbackPort$1)}));
               }

               public {
                  this.callbackHost$1 = callbackHost$1;
                  this.callbackPort$1 = callbackPort$1;
               }
            });
            Socket callbackSocket = new Socket(callbackHost, callbackPort);
            DataOutputStream dos = new DataOutputStream(callbackSocket.getOutputStream());
            dos.writeInt(boundPort);
            dos.close();
            callbackSocket.close();

            while(System.in.read() != -1) {
            }

            PythonGatewayServer$.MODULE$.logDebug(new Serializable(this) {
               public static final long serialVersionUID = 0L;

               public final String apply() {
                  return "Exiting due to broken pipe from Python driver";
               }
            });
            System.exit(0);
         }
      });
   }

   private PythonGatewayServer$() {
      MODULE$ = this;
      Logging$class.$init$(this);
      this.initializeLogIfNecessary(true);
   }
}
