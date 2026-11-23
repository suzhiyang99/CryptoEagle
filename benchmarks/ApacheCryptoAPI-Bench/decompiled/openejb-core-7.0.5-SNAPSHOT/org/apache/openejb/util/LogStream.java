package org.apache.openejb.util;

public interface LogStream {
   boolean isFatalEnabled();

   void fatal(String var1);

   void fatal(String var1, Throwable var2);

   boolean isErrorEnabled();

   void error(String var1);

   void error(String var1, Throwable var2);

   boolean isWarnEnabled();

   void warn(String var1);

   void warn(String var1, Throwable var2);

   boolean isInfoEnabled();

   void info(String var1);

   void info(String var1, Throwable var2);

   boolean isDebugEnabled();

   void debug(String var1);

   void debug(String var1, Throwable var2);
}
