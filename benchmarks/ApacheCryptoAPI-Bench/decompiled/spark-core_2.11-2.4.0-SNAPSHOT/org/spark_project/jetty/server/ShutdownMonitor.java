package org.spark_project.jetty.server;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import org.spark_project.jetty.util.IO;
import org.spark_project.jetty.util.component.Destroyable;
import org.spark_project.jetty.util.component.LifeCycle;
import org.spark_project.jetty.util.thread.ShutdownThread;

public class ShutdownMonitor {
   private final Set<LifeCycle> _lifeCycles;
   private boolean debug;
   private final String host;
   private int port;
   private String key;
   private boolean exitVm;
   private boolean alive;

   public static ShutdownMonitor getInstance() {
      return ShutdownMonitor.Holder.instance;
   }

   protected static void reset() {
      ShutdownMonitor.Holder.instance = new ShutdownMonitor();
   }

   public static void register(LifeCycle... lifeCycles) {
      getInstance().addLifeCycles(lifeCycles);
   }

   public static void deregister(LifeCycle lifeCycle) {
      getInstance().removeLifeCycle(lifeCycle);
   }

   public static boolean isRegistered(LifeCycle lifeCycle) {
      return getInstance().containsLifeCycle(lifeCycle);
   }

   private ShutdownMonitor() {
      this._lifeCycles = new LinkedHashSet();
      this.debug = System.getProperty("DEBUG") != null;
      this.host = System.getProperty("STOP.HOST", "127.0.0.1");
      this.port = Integer.parseInt(System.getProperty("STOP.PORT", "-1"));
      this.key = System.getProperty("STOP.KEY", (String)null);
      this.exitVm = true;
   }

   private void addLifeCycles(LifeCycle... lifeCycles) {
      synchronized(this) {
         this._lifeCycles.addAll(Arrays.asList(lifeCycles));
      }
   }

   private void removeLifeCycle(LifeCycle lifeCycle) {
      synchronized(this) {
         this._lifeCycles.remove(lifeCycle);
      }
   }

   private boolean containsLifeCycle(LifeCycle lifeCycle) {
      synchronized(this) {
         return this._lifeCycles.contains(lifeCycle);
      }
   }

   private void debug(String format, Object... args) {
      if (this.debug) {
         System.err.printf("[ShutdownMonitor] " + format + "%n", args);
      }

   }

   private void debug(Throwable t) {
      if (this.debug) {
         t.printStackTrace(System.err);
      }

   }

   public String getKey() {
      synchronized(this) {
         return this.key;
      }
   }

   public int getPort() {
      synchronized(this) {
         return this.port;
      }
   }

   public boolean isExitVm() {
      synchronized(this) {
         return this.exitVm;
      }
   }

   public void setDebug(boolean flag) {
      this.debug = flag;
   }

   public void setExitVm(boolean exitVm) {
      synchronized(this) {
         if (this.alive) {
            throw new IllegalStateException("ShutdownMonitor already started");
         } else {
            this.exitVm = exitVm;
         }
      }
   }

   public void setKey(String key) {
      synchronized(this) {
         if (this.alive) {
            throw new IllegalStateException("ShutdownMonitor already started");
         } else {
            this.key = key;
         }
      }
   }

   public void setPort(int port) {
      synchronized(this) {
         if (this.alive) {
            throw new IllegalStateException("ShutdownMonitor already started");
         } else {
            this.port = port;
         }
      }
   }

   protected void start() throws Exception {
      synchronized(this) {
         if (this.alive) {
            this.debug("Already started");
         } else {
            ServerSocket serverSocket = this.listen();
            if (serverSocket != null) {
               this.alive = true;
               Thread thread = new Thread(new ShutdownMonitorRunnable(serverSocket));
               thread.setDaemon(true);
               thread.setName("ShutdownMonitor");
               thread.start();
            }

         }
      }
   }

   private void stop() {
      synchronized(this) {
         this.alive = false;
         this.notifyAll();
      }
   }

   void await() throws InterruptedException {
      synchronized(this) {
         while(this.alive) {
            this.wait();
         }

      }
   }

   protected boolean isAlive() {
      synchronized(this) {
         return this.alive;
      }
   }

   private ServerSocket listen() {
      int port = this.getPort();
      if (port < 0) {
         this.debug("Not enabled (port < 0): %d", port);
         return null;
      } else {
         String key = this.getKey();

         ServerSocket var4;
         try {
            ServerSocket serverSocket = new ServerSocket();
            serverSocket.setReuseAddress(true);
            serverSocket.bind(new InetSocketAddress(InetAddress.getByName(this.host), port));
            if (port == 0) {
               port = serverSocket.getLocalPort();
               System.out.printf("STOP.PORT=%d%n", port);
               this.setPort(port);
            }

            if (key == null) {
               key = Long.toString((long)((double)Long.MAX_VALUE * Math.random() + (double)this.hashCode() + (double)System.currentTimeMillis()), 36);
               System.out.printf("STOP.KEY=%s%n", key);
               this.setKey(key);
            }

            var4 = serverSocket;
            return var4;
         } catch (Throwable x) {
            this.debug(x);
            System.err.println("Error binding ShutdownMonitor to port " + port + ": " + x.toString());
            var4 = null;
         } finally {
            this.debug("STOP.PORT=%d", port);
            this.debug("STOP.KEY=%s", key);
         }

         return var4;
      }
   }

   public String toString() {
      return String.format("%s[port=%d,alive=%b]", this.getClass().getName(), this.getPort(), this.isAlive());
   }

   private static class Holder {
      static ShutdownMonitor instance = new ShutdownMonitor();
   }

   private class ShutdownMonitorRunnable implements Runnable {
      private final ServerSocket serverSocket;

      private ShutdownMonitorRunnable(ServerSocket serverSocket) {
         this.serverSocket = serverSocket;
      }

      public void run() {
         ShutdownMonitor.this.debug("Started");

         try {
            String key = ShutdownMonitor.this.getKey();

            while(true) {
               try {
                  Socket socket = this.serverSocket.accept();
                  Throwable var3 = null;

                  try {
                     LineNumberReader reader = new LineNumberReader(new InputStreamReader(socket.getInputStream()));
                     String receivedKey = reader.readLine();
                     if (!key.equals(receivedKey)) {
                        ShutdownMonitor.this.debug("Ignoring command with incorrect key: %s", receivedKey);
                     } else {
                        String cmd = reader.readLine();
                        ShutdownMonitor.this.debug("command=%s", cmd);
                        OutputStream out = socket.getOutputStream();
                        boolean exitVm = ShutdownMonitor.this.isExitVm();
                        if ("stop".equalsIgnoreCase(cmd)) {
                           ShutdownMonitor.this.debug("Performing stop command");
                           this.stopLifeCycles(ShutdownThread::isRegistered, exitVm);
                           ShutdownMonitor.this.debug("Informing client that we are stopped");
                           this.informClient(out, "Stopped\r\n");
                           if (!exitVm) {
                              break;
                           }

                           ShutdownMonitor.this.debug("Killing JVM");
                           System.exit(0);
                        } else if ("forcestop".equalsIgnoreCase(cmd)) {
                           ShutdownMonitor.this.debug("Performing forced stop command");
                           this.stopLifeCycles((l) -> true, exitVm);
                           ShutdownMonitor.this.debug("Informing client that we are stopped");
                           this.informClient(out, "Stopped\r\n");
                           if (!exitVm) {
                              break;
                           }

                           ShutdownMonitor.this.debug("Killing JVM");
                           System.exit(0);
                        } else if ("stopexit".equalsIgnoreCase(cmd)) {
                           ShutdownMonitor.this.debug("Performing stop and exit commands");
                           this.stopLifeCycles(ShutdownThread::isRegistered, true);
                           ShutdownMonitor.this.debug("Informing client that we are stopped");
                           this.informClient(out, "Stopped\r\n");
                           ShutdownMonitor.this.debug("Killing JVM");
                           System.exit(0);
                        } else if ("exit".equalsIgnoreCase(cmd)) {
                           ShutdownMonitor.this.debug("Killing JVM");
                           System.exit(0);
                        } else if ("status".equalsIgnoreCase(cmd)) {
                           this.informClient(out, "OK\r\n");
                        }
                     }
                  } catch (Throwable var34) {
                     var3 = var34;
                     throw var34;
                  } finally {
                     if (socket != null) {
                        if (var3 != null) {
                           try {
                              socket.close();
                           } catch (Throwable var33) {
                              var3.addSuppressed(var33);
                           }
                        } else {
                           socket.close();
                        }
                     }

                  }
               } catch (Throwable x) {
                  ShutdownMonitor.this.debug(x);
               }
            }
         } catch (Throwable x) {
            ShutdownMonitor.this.debug(x);
         } finally {
            IO.close((Closeable)this.serverSocket);
            ShutdownMonitor.this.stop();
            ShutdownMonitor.this.debug("Stopped");
         }

      }

      private void informClient(OutputStream out, String message) throws IOException {
         out.write(message.getBytes(StandardCharsets.UTF_8));
         out.flush();
      }

      private void stopLifeCycles(Predicate<LifeCycle> predicate, boolean destroy) {
         List<LifeCycle> lifeCycles = new ArrayList();
         synchronized(this) {
            lifeCycles.addAll(ShutdownMonitor.this._lifeCycles);
         }

         for(LifeCycle l : lifeCycles) {
            try {
               if (l.isStarted() && predicate.test(l)) {
                  l.stop();
               }

               if (l instanceof Destroyable && destroy) {
                  ((Destroyable)l).destroy();
               }
            } catch (Throwable x) {
               ShutdownMonitor.this.debug(x);
            }
         }

      }
   }
}
