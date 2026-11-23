package org.apache.openejb.config;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.ProcessBuilder.Redirect;
import java.lang.reflect.Field;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.openejb.OpenEJBRuntimeException;
import org.apache.openejb.loader.IO;
import org.apache.openejb.loader.Options;
import org.apache.openejb.util.JavaSecurityManagers;
import org.apache.openejb.util.Join;

public class RemoteServer {
   private static final Options options = new Options(JavaSecurityManagers.getSystemProperties());
   public static final String SERVER_DEBUG_PORT = "server.debug.port";
   public static final String SERVER_SHUTDOWN_PORT = "server.shutdown.port";
   public static final String SERVER_SHUTDOWN_HOST = "server.shutdown.host";
   public static final String SERVER_SHUTDOWN_COMMAND = "server.shutdown.command";
   public static final String SOCKET_TIMEOUT = "server.socket.timeout";
   public static final String OPENEJB_SERVER_DEBUG = "openejb.server.debug";
   public static final String START = "start";
   public static final String STOP = "stop";
   private boolean debug;
   private final boolean profile;
   private final boolean tomcat;
   private final String javaOpts;
   private String additionalClasspath;
   private boolean serverHasAlreadyBeenStarted;
   private Properties properties;
   private final AtomicReference<Process> server;
   private final int tries;
   private final boolean verbose;
   private final int portShutdown;
   private final String host;
   private final String command;
   private File home;
   private int portStartup;
   private final int connectTimeout;
   private static final List<Process> kill = new ArrayList();

   public RemoteServer() {
      this(options.get("connect.tries", 60), options.get("verbose", false));
   }

   public RemoteServer(int tries, boolean verbose) {
      this.debug = options.get("openejb.server.debug", false);
      this.profile = options.get("openejb.server.profile", false);
      this.javaOpts = JavaSecurityManagers.getSystemProperty("java.opts");
      this.serverHasAlreadyBeenStarted = true;
      this.server = new AtomicReference();
      this.tries = tries < 1 ? 1 : (tries > 3600 ? 3600 : tries);
      this.verbose = verbose;
      this.home = this.getHome();
      this.tomcat = this.home != null && (new File(new File(this.home, "bin"), "catalina.sh")).exists();
      this.portShutdown = options.get("server.shutdown.port", this.tomcat ? 8005 : 4200);
      this.portStartup = this.portShutdown;
      this.command = options.get("server.shutdown.command", "SHUTDOWN");
      this.host = options.get("server.shutdown.host", "localhost");
      this.connectTimeout = options.get("server.socket.timeout", 1000);
   }

   public void init(Properties props) {
      this.properties = props;
      props.put("java.naming.factory.initial", "org.apache.openejb.client.RemoteInitialContextFactory");
      int port = options.get("ejbd.port", 4201);
      props.put("java.naming.provider.url", options.get("java.naming.provider.url", "127.0.0.1:" + port));
      props.put("java.naming.security.principal", "testuser");
      props.put("java.naming.security.credentials", "testpassword");
   }

   public static void main(String[] args) {
      assert args.length > 0 : "no arguments supplied: valid arguments are 'start' or 'stop'";

      if (args[0].equalsIgnoreCase("start")) {
         RemoteServer remoteServer = new RemoteServer();

         try {
            remoteServer.start();
         } catch (Exception e) {
            remoteServer.destroy();
            throw e;
         }
      } else {
         if (!args[0].equalsIgnoreCase("stop")) {
            throw new OpenEJBRuntimeException("valid arguments are 'start' or 'stop'");
         }

         RemoteServer remoteServer = new RemoteServer();
         remoteServer.serverHasAlreadyBeenStarted = false;

         try {
            remoteServer.forceStop();
         } catch (Exception e) {
            e.printStackTrace(System.err);
         }
      }

   }

   public int getPortStartup() {
      return this.portStartup;
   }

   public void setPortStartup(int portStartup) {
      this.portStartup = portStartup;
   }

   public Properties getProperties() {
      return this.properties;
   }

   public void destroy() {
      try {
         boolean stopSent = this.stop();
         Process p = (Process)this.server.get();
         if (p != null) {
            if (stopSent) {
               this.waitFor(p);
            } else {
               p.destroy();
            }
         }
      } catch (Exception e) {
         Logger.getLogger(RemoteServer.class.getName()).log(Level.WARNING, "Failed to destroy server", e);
      }

   }

   public void start() {
      this.start(Collections.emptyList(), "start", true);
   }

   public void start(List<String> additionalArgs, String cmd, boolean checkPortAvailable) {
      this.cmd(additionalArgs, cmd, checkPortAvailable);
   }

   private void cmd(List<String> additionalArgs, String cmd, boolean checkPortAvailable) {
      boolean ok = true;
      int port = "start".equals(cmd) && this.portStartup > 0 ? this.portStartup : this.portShutdown;
      if (checkPortAvailable) {
         ok = !this.connect(port, 1);
      }

      if (ok) {
         try {
            if (this.verbose) {
               System.out.println("[] " + cmd.toUpperCase(Locale.ENGLISH) + " SERVER");
            }

            File home = this.getHome();
            String javaVersion = JavaSecurityManagers.getSystemProperty("java.version");
            if (this.verbose) {
               System.out.println("OPENEJB_HOME = " + home.getAbsolutePath());
               String systemInfo = "Java " + javaVersion + "; " + JavaSecurityManagers.getSystemProperty("os.name") + "/" + JavaSecurityManagers.getSystemProperty("os.version");
               System.out.println("SYSTEM_INFO  = " + systemInfo);
            }

            this.serverHasAlreadyBeenStarted = false;
            File lib = new File(home, "lib");
            File webapplib = new File(new File(new File(home, "webapps"), "tomee"), "lib");
            File javaagentJar = null;

            try {
               javaagentJar = this.lib("openejb-javaagent", lib, webapplib);
            } catch (IllegalStateException var27) {
            }

            File conf = new File(home, "conf");
            File loggingProperties = new File(conf, "logging.properties");
            boolean isWindows = JavaSecurityManagers.getSystemProperty("os.name", "unknown").toLowerCase(Locale.ENGLISH).startsWith("windows");
            String java;
            if (isWindows && "start".equals(cmd) && options.get("server.windows.fork", false)) {
               java = (new File(JavaSecurityManagers.getSystemProperty("java.home"), "bin/javaw")).getAbsolutePath();
            } else {
               java = (new File(JavaSecurityManagers.getSystemProperty("java.home"), "bin/java")).getAbsolutePath();
            }

            List<String> argsList = new ArrayList(20);
            argsList.add(java);
            argsList.add("-XX:+HeapDumpOnOutOfMemoryError");
            if (this.debug) {
               argsList.add("-Xdebug");
               argsList.add("-Xnoagent");
               argsList.add("-Djava.compiler=NONE");
               argsList.add("-Xrunjdwp:transport=dt_socket,server=y,suspend=y,address=" + options.get("server.debug.port", 5005));
            }

            if (this.profile) {
               String yourkitHome = options.get("yourkit.home", "/Applications/YourKit_Java_Profiler_9.5.6.app/bin/mac/");
               if (!yourkitHome.endsWith("/")) {
                  yourkitHome = yourkitHome + "/";
               }

               String yourkitOpts = options.get("yourkit.opts", "disablestacktelemetry,disableexceptiontelemetry,builtinprobes=none,delay=10000,sessionname=Tomcat");
               argsList.add("-agentpath:" + yourkitHome + "libyjpagent.jnilib=" + yourkitOpts);
            }

            if (this.javaOpts != null) {
               argsList.addAll(parse(this.javaOpts.replace("${openejb.base}", home.getAbsolutePath())));
            }

            Map<String, String> addedArgs = new HashMap();
            if (additionalArgs != null) {
               for(String arg : additionalArgs) {
                  int equal = arg.indexOf(61);
                  if (equal < 0) {
                     addedArgs.put(arg, "null");
                  } else {
                     addedArgs.put(arg.substring(0, equal), arg.substring(equal + 1).replace("${openejb.base}", home.getAbsolutePath()));
                  }

                  argsList.add(arg.replace("${openejb.base}", home.getAbsolutePath()));
               }
            }

            if (!addedArgs.containsKey("-Djava.util.logging.config.file") && loggingProperties.exists()) {
               argsList.add("-Djava.util.logging.config.file=" + loggingProperties.getAbsolutePath());
            }

            if (javaagentJar != null && javaagentJar.exists()) {
               argsList.add("-javaagent:" + javaagentJar.getAbsolutePath());
            }

            String ps = File.pathSeparator;
            if (!this.tomcat) {
               File openejbJar = this.lib("openejb-core", lib, webapplib);
               StringBuilder cp = new StringBuilder(openejbJar.getAbsolutePath());
               if (this.additionalClasspath != null) {
                  cp.append(ps).append(this.additionalClasspath.replace("${openejb.base}", home.getAbsolutePath()));
               }

               argsList.add("-cp");
               argsList.add(cp.toString());
               argsList.add("org.apache.openejb.cli.Bootstrap");
            } else {
               File bin = new File(home, "bin");
               File tlib = new File(home, "lib");
               File bootstrapJar = new File(bin, "bootstrap.jar");
               File juliJar = new File(bin, "tomcat-juli.jar");
               File commonsLoggingJar = new File(bin, "commons-logging-api.jar");
               File endorsed = new File(home, "endorsed");
               File temp = new File(home, "temp");
               if (!addedArgs.containsKey("-Djava.util.logging.manager")) {
                  argsList.add("-Djava.util.logging.manager=org.apache.juli.ClassLoaderLogManager");
               }

               if (!addedArgs.containsKey("-Djava.io.tmpdir")) {
                  argsList.add("-Djava.io.tmpdir=" + temp.getAbsolutePath());
               }

               if ((javaVersion.startsWith("1.7") || javaVersion.startsWith("1.8")) && !addedArgs.containsKey("-Djava.endorsed.dirs") && endorsed.exists()) {
                  argsList.add("-Djava.endorsed.dirs=" + endorsed.getAbsolutePath());
               }

               if (!addedArgs.containsKey("-Dcatalina.base")) {
                  argsList.add("-Dcatalina.base=" + home.getAbsolutePath());
               }

               if (!addedArgs.containsKey("-Dcatalina.home")) {
                  argsList.add("-Dcatalina.home=" + home.getAbsolutePath());
               }

               if (!addedArgs.containsKey("-Dcatalina.ext.dirs")) {
                  argsList.add("-Dcatalina.ext.dirs=" + tlib.getAbsolutePath());
               }

               if (!addedArgs.containsKey("-Dorg.apache.catalina.STRICT_SERVLET_COMPLIANCE")) {
                  argsList.add("-Dorg.apache.catalina.STRICT_SERVLET_COMPLIANCE=true");
               }

               if (!addedArgs.containsKey("-Dorg.apache.tomcat.util.http.ServerCookie.ALLOW_HTTP_SEPARATORS_IN_V0")) {
                  argsList.add("-Dorg.apache.tomcat.util.http.ServerCookie.ALLOW_HTTP_SEPARATORS_IN_V0=true");
               }

               if (addedArgs.isEmpty()) {
                  this.addIfSet(argsList, "javax.net.ssl.keyStore");
                  this.addIfSet(argsList, "javax.net.ssl.keyStorePassword");
                  this.addIfSet(argsList, "javax.net.ssl.trustStore");
                  this.addIfSet(argsList, "java.protocol.handler.pkgs");
               }

               argsList.add("-ea");
               argsList.add("-classpath");
               StringBuilder cp = (new StringBuilder(bootstrapJar.getAbsolutePath())).append(ps).append(juliJar.getAbsolutePath());
               if (commonsLoggingJar.exists()) {
                  cp.append(ps).append(commonsLoggingJar.getAbsolutePath());
               }

               if (this.additionalClasspath != null) {
                  cp.append(ps).append(this.additionalClasspath.replace("${openejb.base}", home.getAbsolutePath()));
               }

               argsList.add(cp.toString());
               argsList.add("org.apache.catalina.startup.Bootstrap");
            }

            if (cmd == null) {
               argsList.add("start");
            } else {
               argsList.add(cmd);
            }

            String[] args = (String[])argsList.toArray(new String[argsList.size()]);
            if (this.verbose) {
               System.out.println(Join.join("\n", (Object[])args));
            }

            ProcessBuilder pb = (new ProcessBuilder(args)).redirectOutput(Redirect.INHERIT).redirectError(Redirect.INHERIT).directory(home.getAbsoluteFile());
            Process p = pb.start();
            if ("start".equals(cmd)) {
               this.server.set(p);
            } else if ("stop".equals(cmd)) {
               this.waitFor(p);
               p = (Process)this.server.get();
               if (p != null) {
                  this.waitFor(p);
               }
            }
         } catch (Exception e) {
            throw (RuntimeException)(new OpenEJBRuntimeException("Cannot start the server.  Exception: " + e.getClass().getName() + ": " + e.getMessage())).initCause(e);
         }

         if (port > 0) {
            if (this.debug) {
               if (!this.connect(port, Integer.MAX_VALUE)) {
                  throw new OpenEJBRuntimeException("Could not connect to server: " + this.host + ":" + port);
               }
            } else if (!this.connect(port, this.tries)) {
               throw new OpenEJBRuntimeException("Could not connect to server: " + this.host + ":" + port);
            }
         }
      } else if (this.verbose) {
         System.out.println("[] FOUND STARTED SERVER");
      }

   }

   private void waitFor(final Process p) {
      final CountDownLatch latch = new CountDownLatch(1);
      Thread t = new Thread(new Runnable() {
         public void run() {
            try {
               p.waitFor();
               synchronized(RemoteServer.kill) {
                  RemoteServer.kill.remove(p);
               }
            } catch (InterruptedException var8) {
               Thread.interrupted();
            } finally {
               latch.countDown();
            }

         }
      }, "process-waitFor");
      t.start();

      try {
         if (!latch.await((long)Integer.getInteger("openejb.server.waitFor.seconds", 10), TimeUnit.SECONDS)) {
            killOnExit(p);
            throw new RuntimeException("Timeout waiting for process");
         }
      } catch (InterruptedException var5) {
         Thread.interrupted();
         killOnExit(p);
      }

   }

   public void kill3UNIX() {
      if (!JavaSecurityManagers.getSystemProperty("os.name", "unknown").toLowerCase(Locale.ENGLISH).startsWith("windows")) {
         try {
            Field f = ((Process)this.server.get()).getClass().getDeclaredField("pid");
            f.setAccessible(true);
            int pid = (Integer)f.get(this.server.get());
            (new ProcessBuilder(new String[]{"kill", "-3", Integer.toString(pid)})).redirectOutput(Redirect.INHERIT).redirectError(Redirect.INHERIT).start();
         } catch (Exception e1) {
            e1.printStackTrace();
         }

      }
   }

   private File lib(String name, File... dirs) {
      for(File dir : dirs) {
         File[] files = dir.listFiles();
         if (files != null) {
            for(File file : files) {
               if (file.isFile() && file.getName().endsWith(".jar") && file.getName().startsWith(name)) {
                  return file;
               }
            }
         }
      }

      if (this.debug) {
         for(File dir : dirs) {
            dumpLibs(dir);
         }
      }

      throw new IllegalStateException("Cannot find the " + name + " jar");
   }

   private static void dumpLibs(File dir) {
      if (!dir.exists()) {
         System.out.println("lib dir doesn't exist");
      } else {
         File[] files = dir.listFiles();
         if (files != null) {
            for(File lib : files) {
               System.out.println(lib.getAbsolutePath());
            }
         }

      }
   }

   public Process getServer() {
      return (Process)this.server.get();
   }

   private void addIfSet(List<String> argsList, String key) {
      String systemProperty = JavaSecurityManagers.getSystemProperty(key);
      if (systemProperty != null) {
         argsList.add("-D" + key + "=" + systemProperty);
      }

   }

   private File getHome() {
      if (this.home != null) {
         return this.home;
      } else {
         String openejbHome = JavaSecurityManagers.getSystemProperty("openejb.home");
         if (openejbHome != null) {
            this.home = new File(openejbHome);
         }

         return this.home;
      }
   }

   public boolean stop() {
      if (this.sendShutdown(5)) {
         return true;
      } else {
         if (this.verbose) {
            this.notSent();
         }

         return false;
      }
   }

   private void notSent() {
      System.out.println("Failed to send the shutdown notification - TomEE is likely shut down already");
   }

   public void forceStop() throws Exception {
      if (this.sendShutdown(5)) {
         this.waitForServerShutdown();
      } else if (this.verbose) {
         this.notSent();
      }

   }

   private void waitForServerShutdown() throws Exception {
      if (this.verbose) {
         System.out.print("Waiting for TomEE shutdown.");
      }

      boolean b = this.disconnect(this.portShutdown, this.tries);
      if (this.verbose) {
         System.out.println();
      }

      if (!b) {
         System.out.println("SEVERE: Failed to shutdown TomEE running on port " + this.portStartup + " using shutdown port: " + this.portShutdown);
      }

   }

   private boolean sendShutdown(int attempts) {
      Socket socket = null;
      OutputStream stream = null;

      try {
         socket = new Socket(this.host, this.portShutdown);
         stream = socket.getOutputStream();
         String shutdown = this.command + Character.toString('\u0000');

         for(int i = 0; i < shutdown.length(); ++i) {
            stream.write(shutdown.charAt(i));
         }

         stream.flush();
         return true;
      } catch (Exception var19) {
         if (attempts <= 0) {
            boolean ie = false;
            return ie;
         } else {
            try {
               Thread.sleep(1000L);
            } catch (InterruptedException var20) {
               boolean var6 = false;
               return var6;
            }

            --attempts;
            boolean i = this.sendShutdown(attempts);
            return i;
         }
      } finally {
         IO.close(stream);
         if (socket != null) {
            try {
               socket.close();
            } catch (Exception var18) {
            }
         }

      }
   }

   private boolean connect(int port, int tries) {
      if (this.verbose) {
         System.out.println("[] CONNECT ATTEMPT " + (this.tries - tries) + " on port: " + port);
      }

      Socket s = null;

      boolean e2;
      try {
         try {
            s = new Socket();
            s.connect(new InetSocketAddress(this.host, port), this.connectTimeout);
            s.getOutputStream().close();
            if (this.verbose) {
               System.out.println("[] CONNECTED IN " + (this.tries - tries));
            }

            return true;
         } catch (Exception var18) {
            if (tries >= 2) {
               try {
                  Thread.sleep(1000L);
               } catch (InterruptedException var17) {
                  Thread.interrupted();
               }

               --tries;
               e2 = this.connect(port, tries);
               return e2;
            }
         }

         if (this.verbose) {
            System.out.println("[] CONNECT ATTEMPTS FAILED ( " + (this.tries - tries) + " ATTEMPTS)");
         }

         e2 = false;
      } finally {
         if (s != null) {
            try {
               s.close();
            } catch (Exception var16) {
            }
         }

      }

      return e2;
   }

   private boolean disconnect(int port, int tries) {
      if (this.verbose) {
         System.out.println("[] DISCONNECT ATTEMPT " + (this.tries - tries) + " on port: " + port);
      }

      Socket s = null;

      boolean e2;
      try {
         s = new Socket();
         s.connect(new InetSocketAddress(this.host, port), this.connectTimeout);
         s.getOutputStream().close();
         if (this.verbose) {
            System.out.println("[] NOT DISCONNECTED AFTER ( " + (this.tries - tries) + " ATTEMPTS)");
         }

         if (tries >= 2) {
            try {
               Thread.sleep(1000L);
            } catch (InterruptedException var16) {
               Thread.interrupted();
            }

            --tries;
            e2 = this.disconnect(port, tries);
            return e2;
         }

         e2 = false;
      } catch (IOException var17) {
         return true;
      } finally {
         if (s != null) {
            try {
               s.close();
            } catch (Exception var15) {
            }
         }

      }

      return e2;
   }

   public void setAdditionalClasspath(String additionalClasspath) {
      this.additionalClasspath = additionalClasspath;
   }

   public void killOnExit() {
      Process p = (Process)this.server.get();
      if (this.serverHasAlreadyBeenStarted || !kill.contains(p)) {
         killOnExit(p);
      }
   }

   private static void killOnExit(Process p) {
      synchronized(kill) {
         kill.add(p);
      }
   }

   public void setDebug(boolean debug) {
      this.debug = debug;
   }

   private static Collection<String> parse(String raw) {
      Collection<String> result = new LinkedList();
      Character end = null;
      boolean escaped = false;
      StringBuilder current = new StringBuilder();

      for(int i = 0; i < raw.length(); ++i) {
         char c = raw.charAt(i);
         if (escaped) {
            escaped = false;
            current.append(c);
         } else if ((end == null || end != c) && (c != ' ' || end != null)) {
            if (c == '\\') {
               escaped = true;
            } else if (c != '"' && c != '\'') {
               current.append(c);
            } else {
               end = c;
            }
         } else {
            if (current.length() > 0) {
               result.add(current.toString());
               current.setLength(0);
            }

            end = null;
         }
      }

      if (current.length() > 0) {
         result.add(current.toString());
      }

      return result;
   }

   static {
      Runtime.getRuntime().addShutdownHook(new CleanUpThread());
   }

   public static class CleanUpThread extends Thread {
      public void run() {
         for(Process server : RemoteServer.kill) {
            try {
               if (server != null) {
                  server.destroy();
                  server.waitFor();
               }
            } catch (Throwable var4) {
            }
         }

      }
   }
}
