package org.apache.openejb.util;

import java.io.Closeable;
import java.io.IOException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

public class LogStreamAsync implements LogStream, Closeable {
   private static final LinkedBlockingQueue<Message> log = new LinkedBlockingQueue();
   private static final Thread t;
   private static final AtomicBoolean started;
   private final LogStream ls;

   public LogStreamAsync(LogStream ls) {
      this.ls = ls;
      if (!started.getAndSet(true)) {
         t.setDaemon(true);
         t.start();
      }

   }

   public void close() throws IOException {
      log.clear();

      try {
         log.put(new Message(this.ls, LogStreamAsync.level.quit, ""));
      } catch (InterruptedException var2) {
      }

   }

   public boolean isFatalEnabled() {
      return this.ls.isFatalEnabled();
   }

   public void fatal(String message) {
      this.log(LogStreamAsync.level.fatal, message);
   }

   public void fatal(String message, Throwable t) {
      this.log(LogStreamAsync.level.fatal, message, t);
   }

   public boolean isErrorEnabled() {
      return this.ls.isErrorEnabled();
   }

   public void error(String message) {
      this.log(LogStreamAsync.level.error, message);
   }

   public void error(String message, Throwable t) {
      this.log(LogStreamAsync.level.error, message, t);
   }

   public boolean isWarnEnabled() {
      return this.ls.isWarnEnabled();
   }

   public void warn(String message) {
      this.log(LogStreamAsync.level.warn, message);
   }

   public void warn(String message, Throwable t) {
      this.log(LogStreamAsync.level.warn, message, t);
   }

   public boolean isInfoEnabled() {
      return this.ls.isInfoEnabled();
   }

   public void info(String message) {
      this.log(LogStreamAsync.level.info, message, (Throwable)null);
   }

   public void info(String message, Throwable t) {
      this.log(LogStreamAsync.level.info, message, (Throwable)null);
   }

   public boolean isDebugEnabled() {
      return this.ls.isDebugEnabled();
   }

   public void debug(String message) {
      this.log(LogStreamAsync.level.debug, message);
   }

   public void debug(String message, Throwable t) {
      this.log(LogStreamAsync.level.debug, message, t);
   }

   public void log(level l, String s) {
      this.log(l, s, (Throwable)null);
   }

   public void log(level l, String s, Throwable t) {
      try {
         log.put(new Message(this.ls, l, s, t));
      } catch (InterruptedException var5) {
      }

   }

   static {
      t = new Thread(new Consumer(log), "LogStreamAsync.Thread");
      started = new AtomicBoolean(false);
   }

   private static enum level {
      fatal,
      error,
      warn,
      info,
      debug,
      quit;
   }

   private static final class Message {
      private final LogStream ls;
      private final level l;
      private final String s;
      private final Throwable t;

      private Message(LogStream ls, level l, String s) {
         this(ls, l, s, (Throwable)null);
      }

      private Message(LogStream ls, level l, String s, Throwable t) {
         this.ls = ls;
         this.l = l;
         this.s = s;
         this.t = t;
      }
   }

   private static final class Consumer implements Runnable {
      private final BlockingQueue<Message> queue;

      private Consumer(BlockingQueue<Message> queue) {
         this.queue = queue;
      }

      public void run() {
         Message msg;
         try {
            while(!LogStreamAsync.level.quit.equals((msg = (Message)this.queue.take()).l)) {
               Throwable t = msg.t;
               if (null != t) {
                  switch (msg.l) {
                     case fatal:
                        msg.ls.fatal(msg.s, t);
                        break;
                     case error:
                        msg.ls.error(msg.s, t);
                        break;
                     case warn:
                        msg.ls.warn(msg.s, t);
                        break;
                     case info:
                        msg.ls.info(msg.s, t);
                        break;
                     case debug:
                        msg.ls.debug(msg.s, t);
                  }
               } else {
                  switch (msg.l) {
                     case fatal:
                        msg.ls.fatal(msg.s);
                        break;
                     case error:
                        msg.ls.error(msg.s);
                        break;
                     case warn:
                        msg.ls.warn(msg.s);
                        break;
                     case info:
                        msg.ls.info(msg.s);
                        break;
                     case debug:
                        msg.ls.debug(msg.s);
                  }
               }
            }
         } catch (InterruptedException var3) {
         }

      }
   }
}
