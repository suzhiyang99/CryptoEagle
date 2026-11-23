package org.apache.spark.api.r;

import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;
import org.apache.spark.SparkConf;
import org.apache.spark.SparkEnv$;
import org.apache.spark.SparkFiles$;
import org.apache.spark.util.Utils$;
import scala.collection.Seq;
import scala.collection.mutable.StringBuilder;
import scala.runtime.BoxesRunTime;

public final class RRunner$ {
   public static final RRunner$ MODULE$;
   private BufferedStreamThread errThread;
   private DataOutputStream daemonChannel;

   static {
      new RRunner$();
   }

   private BufferedStreamThread startStdoutThread(Process proc) {
      int BUFFER_SIZE = 100;
      BufferedStreamThread thread = new BufferedStreamThread(proc.getInputStream(), "stdout reader for R", BUFFER_SIZE);
      thread.setDaemon(true);
      thread.start();
      return thread;
   }

   private BufferedStreamThread createRProcess(int port, String script) {
      SparkConf sparkConf = SparkEnv$.MODULE$.get().conf();
      String rCommand = sparkConf.get("spark.sparkr.r.command", "Rscript");
      rCommand = sparkConf.get("spark.r.command", rCommand);
      int rConnectionTimeout = sparkConf.getInt("spark.r.backendConnectionTimeout", SparkRDefaults$.MODULE$.DEFAULT_CONNECTION_TIMEOUT());
      String rOptions = "--vanilla";
      Seq rLibDir = RUtils$.MODULE$.sparkRPackagePath(false);
      String rExecScript = (new StringBuilder()).append((Object)((String)rLibDir.apply(0))).append((Object)"/SparkR/worker/").append((Object)script).toString();
      ProcessBuilder pb = new ProcessBuilder(Arrays.asList((Object[])(new String[]{rCommand, rOptions, rExecScript})));
      pb.environment().put("R_TESTS", "");
      pb.environment().put("SPARKR_RLIBDIR", rLibDir.mkString(","));
      pb.environment().put("SPARKR_WORKER_PORT", BoxesRunTime.boxToInteger(port).toString());
      pb.environment().put("SPARKR_BACKEND_CONNECTION_TIMEOUT", BoxesRunTime.boxToInteger(rConnectionTimeout).toString());
      pb.environment().put("SPARKR_SPARKFILES_ROOT_DIR", SparkFiles$.MODULE$.getRootDirectory());
      pb.environment().put("SPARKR_IS_RUNNING_ON_WORKER", "TRUE");
      pb.redirectErrorStream(true);
      Process proc = pb.start();
      BufferedStreamThread errThread = this.startStdoutThread(proc);
      return errThread;
   }

   public BufferedStreamThread createRWorker(int port) {
      boolean useDaemon = SparkEnv$.MODULE$.get().conf().getBoolean("spark.sparkr.use.daemon", true);
      BufferedStreamThread var10000;
      if (!Utils$.MODULE$.isWindows() && useDaemon) {
         BufferedStreamThread var4;
         synchronized(this) {
            if (this.daemonChannel == null) {
               ServerSocket serverSocket = new ServerSocket(0, 1, InetAddress.getByName("localhost"));
               int daemonPort = serverSocket.getLocalPort();
               this.errThread = this.createRProcess(daemonPort, "daemon.R");
               serverSocket.setSoTimeout(10000);
               Socket sock = serverSocket.accept();
               this.daemonChannel = new DataOutputStream(new BufferedOutputStream(sock.getOutputStream()));
               serverSocket.close();
            }

            this.liftedTree1$1(port);
            var4 = this.errThread;
         }

         var10000 = var4;
      } else {
         var10000 = this.createRProcess(port, "worker.R");
      }

      return var10000;
   }

   public <U> int $lessinit$greater$default$6() {
      return -1;
   }

   public <U> boolean $lessinit$greater$default$7() {
      return false;
   }

   public <U> String[] $lessinit$greater$default$8() {
      return null;
   }

   public <U> int $lessinit$greater$default$9() {
      return RRunnerModes$.MODULE$.RDD();
   }

   private final void liftedTree1$1(int port$1) {
      try {
         this.daemonChannel.writeInt(port$1);
         this.daemonChannel.flush();
      } catch (IOException var3) {
         this.daemonChannel.close();
         this.daemonChannel = null;
         this.errThread = null;
         throw var3;
      }
   }

   private RRunner$() {
      MODULE$ = this;
   }
}
