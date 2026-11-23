package org.apache.openejb.resource.jdbc.logging;

import java.io.ByteArrayInputStream;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.sql.ParameterMetaData;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.apache.openejb.core.ObjectInputStreamFiltered;
import org.apache.openejb.util.LogCategory;
import org.apache.openejb.util.Logger;

public class LoggingPreparedSqlStatement implements InvocationHandler {
   private static final Logger LOGGER;
   private final PreparedStatement delegate;
   private final String sql;
   private final List<Parameter> parameters = new ArrayList();
   private final String[] packages;
   private int parameterIndex;

   public LoggingPreparedSqlStatement(PreparedStatement result, String query, String[] debugPackages) {
      this.delegate = result;
      this.sql = query;
      this.parameterIndex = 0;
      this.packages = debugPackages;
   }

   public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
      String mtdName = method.getName();
      boolean execute = mtdName.startsWith("execute");
      boolean debug = false;
      TimeWatcherExecutor.TimerWatcherResult result = TimeWatcherExecutor.execute(method, this.delegate, args, execute);
      if (!mtdName.startsWith("set") || args.length < 2 || !args[0].getClass().equals(Integer.TYPE) && !args[0].getClass().equals(Integer.class)) {
         if (execute) {
            String str = this.sql;
            if (str.contains("?")) {
               Collections.sort(this.parameters);
               int lastBatch = 0;

               for(int i = 0; i < this.parameters.size(); ++i) {
                  Parameter param = (Parameter)this.parameters.get(i);
                  if (str.contains("?")) {
                     try {
                        String val;
                        if (ByteArrayInputStream.class.isInstance(param.value)) {
                           ByteArrayInputStream bais = (ByteArrayInputStream)ByteArrayInputStream.class.cast(param.value);

                           try {
                              bais.reset();
                              val = (new ObjectInputStreamFiltered(bais)).readObject().toString();
                           } catch (Exception var15) {
                              val = param.value.toString();
                           }
                        } else {
                           val = param.value.toString();
                        }

                        str = str.replaceFirst("\\?", val);
                     } catch (Exception var16) {
                        if (param.value == null) {
                           str = str.replaceFirst("\\?", "null");
                        } else {
                           str = str.replaceFirst("\\?", param.value.getClass().getName());
                        }
                     }

                     lastBatch = param.batchIndex;
                  } else {
                     if (lastBatch != param.batchIndex) {
                        str = str + ", (";
                        lastBatch = param.batchIndex;
                     }

                     try {
                        str = str + param.value.toString();
                     } catch (Exception var17) {
                        if (param.value == null) {
                           str = str + "null";
                        } else {
                           str = str + param.value.getClass().getName();
                        }
                     }

                     if (i != this.parameters.size() - 1 && ((Parameter)this.parameters.get(i + 1)).batchIndex == lastBatch) {
                        str = str + ",";
                     } else {
                        str = str + ")";
                     }
                  }
               }
            }

            LOGGER.info(result.format(str) + (this.packages != null ? " - stack:" + TimeWatcherExecutor.inlineStack(this.packages) : ""));
         } else if ("clearParameters".equals(mtdName)) {
            this.parameters.clear();
            this.parameterIndex = 0;
         } else if ("addBatch".equals(mtdName)) {
            ++this.parameterIndex;
         }
      } else {
         Parameter param = new Parameter(mtdName.substring(3), this.parameterIndex, (Integer)args[0], args[1]);
         this.parameters.add(param);
      }

      if (result.getThrowable() != null) {
         throw result.getThrowable();
      } else {
         return result.getResult();
      }
   }

   private void logDebug() {
      try {
         LOGGER.info("SQL " + this.sql);

         for(Parameter parameter : this.parameters) {
            this.logParam(parameter);
         }
      } catch (SQLException e) {
         e.printStackTrace();
      }

   }

   private void logParam(Parameter parameter) throws SQLException {
      this.logParam(this.delegate.getParameterMetaData(), parameter);
   }

   private void logParam(ParameterMetaData md, Parameter parameter) throws SQLException {
      int i = parameter.key;
      String format = String.format(" - PARAM  index=%s, type%s, typeName=%s, className=%s, nullable=%s, mode=%s, precision=%s, value=%s", i, md.getParameterType(i), md.getParameterTypeName(i), md.getParameterClassName(i), md.isNullable(i), md.getParameterMode(i), md.getPrecision(i), parameter.value);
      LOGGER.info(format);
   }

   static {
      LOGGER = Logger.getInstance(LogCategory.OPENEJB_SQL, LoggingPreparedSqlStatement.class);
   }

   protected static class Parameter implements Comparable<Parameter> {
      private final String type;
      private final int batchIndex;
      private final int key;
      private final Object value;

      public Parameter(String type, int batchIdx, int key, Object value) {
         this.type = type;
         this.batchIndex = batchIdx;
         this.key = key;
         this.value = value;
      }

      public int compareTo(Parameter o) {
         int comp = this.batchIndex - o.batchIndex;
         return comp == 0 ? this.key - o.key : comp;
      }

      public String toString() {
         return this.value + " (" + this.type + ")";
      }
   }
}
