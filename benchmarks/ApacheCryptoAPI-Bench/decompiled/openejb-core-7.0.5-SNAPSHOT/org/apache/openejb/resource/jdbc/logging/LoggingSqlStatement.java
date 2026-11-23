package org.apache.openejb.resource.jdbc.logging;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.sql.Statement;
import org.apache.openejb.util.LogCategory;
import org.apache.openejb.util.Logger;

public class LoggingSqlStatement implements InvocationHandler {
   private static final Logger LOGGER;
   private final Statement delegate;
   private final String[] packages;

   public LoggingSqlStatement(Statement result, String[] debugPackages) {
      this.delegate = result;
      this.packages = debugPackages;
   }

   public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
      String mtdName = method.getName();
      boolean execute = mtdName.startsWith("execute") && args != null && args.length > 0;
      TimeWatcherExecutor.TimerWatcherResult result = TimeWatcherExecutor.execute(method, this.delegate, args, execute);
      if (execute) {
         LOGGER.info(result.format((String)args[0]) + (this.packages != null ? " - stack:" + TimeWatcherExecutor.inlineStack(this.packages) : ""));
      }

      if (result.getThrowable() != null) {
         throw result.getThrowable();
      } else {
         return result.getResult();
      }
   }

   static {
      LOGGER = Logger.getInstance(LogCategory.OPENEJB_SQL, LoggingSqlStatement.class);
   }
}
