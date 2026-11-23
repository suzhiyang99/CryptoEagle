package org.apache.activemq.artemis.utils;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.management.LockInfo;
import java.lang.management.ManagementFactory;
import java.lang.management.MonitorInfo;
import java.lang.management.ThreadInfo;
import java.lang.management.ThreadMXBean;
import org.apache.activemq.artemis.logs.ActiveMQUtilLogger;

public final class ThreadDumpUtil {
   public static String threadDump(String msg) {
      try {
         StringWriter str = new StringWriter();
         Throwable var2 = null;

         String var41;
         try {
            PrintWriter out = new PrintWriter(str);
            Throwable var4 = null;

            try {
               ThreadMXBean threadMXBean = ManagementFactory.getThreadMXBean();
               out.println("*******************************************************************************");
               out.println("Complete Thread dump " + msg);

               for(ThreadInfo threadInfo : threadMXBean.dumpAllThreads(true, true)) {
                  out.println(threadInfoToString(threadInfo));
               }

               long[] deadlockedThreads = threadMXBean.findDeadlockedThreads();
               if (deadlockedThreads != null && deadlockedThreads.length > 0) {
                  out.println("Deadlock detected!");
                  out.println();

                  for(ThreadInfo threadInfo : threadMXBean.getThreadInfo(deadlockedThreads, true, true)) {
                     out.println(threadInfoToString(threadInfo));
                  }
               }

               out.println("===============================================================================");
               out.println("End Thread dump " + msg);
               out.println("*******************************************************************************");
               var41 = str.toString();
            } catch (Throwable var34) {
               var4 = var34;
               throw var34;
            } finally {
               if (out != null) {
                  if (var4 != null) {
                     try {
                        out.close();
                     } catch (Throwable var33) {
                        var4.addSuppressed(var33);
                     }
                  } else {
                     out.close();
                  }
               }

            }
         } catch (Throwable var36) {
            var2 = var36;
            throw var36;
         } finally {
            if (str != null) {
               if (var2 != null) {
                  try {
                     str.close();
                  } catch (Throwable var32) {
                     var2.addSuppressed(var32);
                  }
               } else {
                  str.close();
               }
            }

         }

         return var41;
      } catch (IOException e) {
         ActiveMQUtilLogger.LOGGER.error("Exception thrown during generating of thread dump.", e);
         return "Generating of thread dump failed " + msg;
      }
   }

   private static String threadInfoToString(ThreadInfo threadInfo) {
      StringBuilder sb = new StringBuilder("\"" + threadInfo.getThreadName() + "\"" + " Id=" + threadInfo.getThreadId() + " " + threadInfo.getThreadState());
      if (threadInfo.getLockName() != null) {
         sb.append(" on " + threadInfo.getLockName());
      }

      if (threadInfo.getLockOwnerName() != null) {
         sb.append(" owned by \"" + threadInfo.getLockOwnerName() + "\" Id=" + threadInfo.getLockOwnerId());
      }

      if (threadInfo.isSuspended()) {
         sb.append(" (suspended)");
      }

      if (threadInfo.isInNative()) {
         sb.append(" (in native)");
      }

      sb.append('\n');

      for(int i = 0; i < threadInfo.getStackTrace().length; ++i) {
         StackTraceElement ste = threadInfo.getStackTrace()[i];
         sb.append("\tat " + ste.toString());
         sb.append('\n');
         if (i == 0 && threadInfo.getLockInfo() != null) {
            Thread.State ts = threadInfo.getThreadState();
            switch (ts) {
               case BLOCKED:
                  sb.append("\t-  blocked on " + threadInfo.getLockInfo());
                  sb.append('\n');
                  break;
               case WAITING:
                  sb.append("\t-  waiting on " + threadInfo.getLockInfo());
                  sb.append('\n');
                  break;
               case TIMED_WAITING:
                  sb.append("\t-  waiting on " + threadInfo.getLockInfo());
                  sb.append('\n');
            }
         }

         for(MonitorInfo mi : threadInfo.getLockedMonitors()) {
            if (mi.getLockedStackDepth() == i) {
               sb.append("\t-  locked " + mi);
               sb.append('\n');
            }
         }
      }

      LockInfo[] locks = threadInfo.getLockedSynchronizers();
      if (locks.length > 0) {
         sb.append("\n\tNumber of locked synchronizers = " + locks.length);
         sb.append('\n');

         for(LockInfo li : locks) {
            sb.append("\t- " + li);
            sb.append('\n');
         }
      }

      sb.append('\n');
      return sb.toString();
   }
}
