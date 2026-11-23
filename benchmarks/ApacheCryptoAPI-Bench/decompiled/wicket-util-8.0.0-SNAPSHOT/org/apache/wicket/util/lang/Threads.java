package org.apache.wicket.util.lang;

import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.util.HashMap;
import java.util.Map;
import org.apache.wicket.util.string.interpolator.MapVariableInterpolator;
import org.slf4j.Logger;

public class Threads {
   private static final String FORMAT = "\"${name}\"${isDaemon} prio=${priority} tid=${threadIdDec} state=${state} ";

   private Threads() {
   }

   public static void dumpAllThreads(Logger logger) {
      Args.notNull(logger, "logger");
      if (logger.isWarnEnabled()) {
         RuntimeMXBean runtimeMXBean = ManagementFactory.getRuntimeMXBean();
         StringBuilder dump = new StringBuilder();
         dump.append("Full thread dump ").append(runtimeMXBean.getVmName()).append('(').append(runtimeMXBean.getVmVersion()).append(')');
         logger.warn(dump.toString());
         Map<Thread, StackTraceElement[]> allStackTraces = Thread.getAllStackTraces();

         for(Map.Entry<Thread, StackTraceElement[]> entry : allStackTraces.entrySet()) {
            dumpSingleThread(logger, (Thread)entry.getKey(), (StackTraceElement[])entry.getValue());
         }

      }
   }

   public static void dumpSingleThread(Logger logger, Thread thread) {
      Args.notNull(logger, "logger");
      if (logger.isWarnEnabled()) {
         dumpSingleThread(logger, thread, thread.getStackTrace());
      }
   }

   private static void dumpSingleThread(Logger logger, Thread thread, StackTraceElement[] trace) {
      Map<CharSequence, Object> variables = new HashMap();
      variables.put("name", thread.getName());
      variables.put("isDaemon", thread.isDaemon() ? " daemon" : "");
      variables.put("priority", thread.getPriority());
      variables.put("threadIdDec", thread.getId());
      variables.put("state", thread.getState());
      ThreadDump throwable = new ThreadDump();
      throwable.setStackTrace(trace);
      logger.warn(MapVariableInterpolator.interpolate("\"${name}\"${isDaemon} prio=${priority} tid=${threadIdDec} state=${state} ", variables), throwable);
   }

   private static class ThreadDump extends RuntimeException {
      private static final long serialVersionUID = 1L;

      private ThreadDump() {
      }

      public synchronized Throwable fillInStackTrace() {
         return null;
      }
   }
}
