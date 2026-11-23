package org.apache.openejb.resource.jdbc.router;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Pattern;
import javax.annotation.PostConstruct;
import javax.naming.NamingException;
import javax.sql.DataSource;
import javax.transaction.Transaction;
import javax.transaction.TransactionManager;
import javax.transaction.TransactionSynchronizationRegistry;
import org.apache.openejb.OpenEJB;
import org.apache.openejb.loader.SystemInstance;
import org.apache.openejb.util.LogCategory;
import org.apache.openejb.util.Logger;

public class FailOverRouter extends AbstractRouter {
   private static final Logger LOGGER;
   public static final String DEFAULT_STRATEGY = "default";
   private ExceptionSelector exceptionSelectorRuntime;
   private ErrorHandler errorHandlerRuntime;
   private Strategy strategyRuntime;
   private DataSource facade;
   private final List<DataSourceHolder> dataSources = new CopyOnWriteArrayList();
   private String delimiter = ",";
   private String strategy = "default";
   private String datasourceNames = "";

   public DataSource getDataSource() {
      return this.facade;
   }

   @PostConstruct
   public void init() {
      this.initDataSources();
      this.initStrategy();
      this.initFacade();
   }

   public void setDatasourceNames(String datasourceNames) {
      this.datasourceNames = datasourceNames;
   }

   public void setDelimiter(String delimiter) {
      this.delimiter = delimiter;
   }

   public void setStrategy(String strategy) {
      this.strategy = strategy;
   }

   public void setStrategyInstance(Strategy strategy) {
      this.strategyRuntime = strategy;
   }

   public void setExceptionSelectorInstance(ExceptionSelector selector) {
      this.exceptionSelectorRuntime = selector;
   }

   public void setExceptionSelector(String selector) {
      try {
         this.exceptionSelectorRuntime = (ExceptionSelector)("mysql".equalsIgnoreCase(selector) ? new MySQLExceptionSelector() : (ExceptionSelector)ExceptionSelector.class.cast(Thread.currentThread().getContextClassLoader().loadClass(selector.trim()).newInstance()));
      } catch (IllegalAccessException | ClassNotFoundException | InstantiationException e) {
         throw new IllegalArgumentException(e);
      }
   }

   public void setErrorHandlerInstance(ErrorHandler errorHandler) {
      this.errorHandlerRuntime = errorHandler;
   }

   public void setErrorHandler(String errorHandler) {
      try {
         this.errorHandlerRuntime = (ErrorHandler)ErrorHandler.class.cast(Thread.currentThread().getContextClassLoader().loadClass(errorHandler.trim()).newInstance());
      } catch (IllegalAccessException | ClassNotFoundException | InstantiationException e) {
         throw new IllegalArgumentException(e);
      }
   }

   private void initStrategy() {
      switch (this.strategy) {
         case "round-robin":
            this.strategyRuntime = new Strategy() {
               private final AtomicInteger idx = new AtomicInteger(0);

               public Collection<DataSourceHolder> prepare(Collection<DataSourceHolder> list) {
                  int step;
                  if (FailOverRouter.this.strategy.contains("%")) {
                     step = Math.max(1, Integer.parseInt(FailOverRouter.this.strategy.substring(FailOverRouter.this.strategy.lastIndexOf("%") + 1)));
                  } else {
                     step = 1;
                  }

                  List<DataSourceHolder> ds = new ArrayList(list);
                  int currentIdx = 0;

                  for(int i = 0; i < step; ++i) {
                     currentIdx = this.idx.incrementAndGet();
                  }

                  Collections.rotate(ds, 1 + currentIdx % ds.size());
                  return ds;
               }

               public void used(DataSourceHolder holder) {
               }
            };
            break;
         case "random":
            this.strategyRuntime = new Strategy() {
               public Collection<DataSourceHolder> prepare(Collection<DataSourceHolder> list) {
                  List<DataSourceHolder> ds = new ArrayList(list);
                  Collections.shuffle(ds);
                  return ds;
               }

               public void used(DataSourceHolder holder) {
               }
            };
            break;
         case "reverse":
            this.strategyRuntime = new Strategy() {
               private final AtomicInteger idx = new AtomicInteger();

               public Collection<DataSourceHolder> prepare(Collection<DataSourceHolder> list) {
                  List<DataSourceHolder> ds = new ArrayList(list);
                  int times = this.idx.incrementAndGet() % ds.size();

                  for(int i = 0; i < times; ++i) {
                     Collections.reverse(ds);
                  }

                  return ds;
               }

               public void used(DataSourceHolder holder) {
               }
            };
            break;
         case "static":
            this.strategyRuntime = new Strategy() {
               public Collection<DataSourceHolder> prepare(Collection<DataSourceHolder> list) {
                  return list;
               }

               public void used(DataSourceHolder holder) {
               }
            };
            break;
         case "default":
         default:
            this.strategyRuntime = new Strategy() {
               public Collection<DataSourceHolder> prepare(Collection<DataSourceHolder> list) {
                  return list;
               }

               public void used(DataSourceHolder holder) {
                  if (FailOverRouter.this.dataSources.get(0) != holder) {
                     synchronized(this) {
                        if (FailOverRouter.this.dataSources.get(0) != holder) {
                           DataSourceHolder old = (DataSourceHolder)FailOverRouter.this.dataSources.set(0, holder);
                           if (old != holder) {
                              FailOverRouter.this.dataSources.set(FailOverRouter.this.dataSources.lastIndexOf(holder), old);
                           }

                        }
                     }
                  }
               }
            };
      }

   }

   private void initDataSources() {
      this.dataSources.clear();

      for(String ds : this.datasourceNames.split(Pattern.quote(this.delimiter))) {
         try {
            String name = ds.trim();
            Object o = this.getOpenEJBResource(name);
            if (!DataSource.class.isInstance(o)) {
               throw new IllegalArgumentException(name + " (" + o + ") is not a datasource");
            }

            LOGGER.debug("Found datasource '" + ds + "'");
            this.dataSources.add(new DataSourceHolder((DataSource)DataSource.class.cast(o), name));
         } catch (NamingException error) {
            throw new IllegalStateException(error);
         }
      }

      this.initFacade();
   }

   private void initFacade() {
      this.facade = (DataSource)DataSource.class.cast(Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(), new Class[]{DataSource.class}, new FacadeHandler(this.dataSources, this.strategyRuntime, this.errorHandlerRuntime, this.exceptionSelectorRuntime)));
   }

   public Collection<DataSourceHolder> getDataSources() {
      return this.dataSources;
   }

   public void updateDataSources(Collection<DataSourceHolder> ds) {
      this.dataSources.clear();
      this.dataSources.addAll(ds);
      this.initFacade();
   }

   static {
      LOGGER = Logger.getInstance(LogCategory.OPENEJB_SERVER, FailOverRouter.class);
   }

   private static class FacadeHandler implements InvocationHandler {
      private static final TransactionSynchronizationRegistry SYNCHRONIZATION_REGISTRY = (TransactionSynchronizationRegistry)SystemInstance.get().getComponent(TransactionSynchronizationRegistry.class);
      private final TransactionManager transactionManager;
      private final Collection<DataSourceHolder> delegates;
      private final Strategy strategy;
      private final ErrorHandler handler;
      private final ExceptionSelector selector;

      public FacadeHandler(Collection<DataSourceHolder> dataSources, Strategy strategy, ErrorHandler handler, ExceptionSelector selector) {
         this.delegates = dataSources;
         this.strategy = strategy;
         this.handler = handler;
         this.selector = selector;
         this.transactionManager = OpenEJB.getTransactionManager();
      }

      public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
         if (Object.class.equals(method.getDeclaringClass())) {
            if ("toString".equals(method.getName())) {
               return "DataSourceFacade" + this.delegates;
            }

            if ("hashCode".equals(method.getName())) {
               return this.delegates.hashCode();
            }

            if ("equals".equals(method.getName())) {
               return method.invoke(this, args);
            }
         }

         Transaction transaction = this.transactionManager.getTransaction();
         if (transaction != null) {
            DataSource currentDs = (DataSource)DataSource.class.cast(SYNCHRONIZATION_REGISTRY.getResource(FacadeHandler.class.getName()));
            if (currentDs != null) {
               return method.invoke(currentDs, args);
            }
         }

         int ex = 0;
         Collection<DataSourceHolder> sources = this.strategy.prepare(this.delegates);
         int size = sources.size();
         Object out = null;
         Map<String, Throwable> failed = null;
         DataSourceHolder used = null;

         for(DataSourceHolder ds : sources) {
            used = ds;

            try {
               boolean set = method.getName().startsWith("set");
               if (set) {
                  method.invoke(ds.dataSource, args);
               } else {
                  out = method.invoke(ds.dataSource, args);
               }

               if (transaction != null) {
                  SYNCHRONIZATION_REGISTRY.putResource(FacadeHandler.class.getName(), ds.dataSource);
                  break;
               }

               if (!set) {
                  break;
               }
            } catch (InvocationTargetException var15) {
               Throwable cause = var15.getTargetException();
               if (this.selector != null && !this.selector.shouldFailover(cause)) {
                  if (failed != null) {
                     this.handler.onError(failed, ds);
                  }

                  throw cause;
               }

               if (this.handler != null) {
                  if (failed == null) {
                     failed = new HashMap();
                  }

                  failed.put(ds.name, var15.getCause());
               }

               ++ex;
               if (ex == size) {
                  if (failed != null) {
                     this.handler.onError(failed, (DataSourceHolder)null);
                  }

                  throw var15.getCause();
               }
            }
         }

         if (failed != null) {
            this.handler.onError(failed, used);
         }

         this.strategy.used(used);
         return out;
      }
   }

   public abstract class SQLExceptionSelector implements ExceptionSelector {
      public boolean shouldFailover(Throwable sqle) {
         return SQLException.class.isInstance(sqle) && this.shouldFailover((SQLException)SQLException.class.cast(sqle));
      }

      abstract boolean shouldFailover(SQLException var1);
   }

   public class MySQLExceptionSelector extends SQLExceptionSelector {
      private final Class<?> communicationException;

      public MySQLExceptionSelector() {
         try {
            this.communicationException = Thread.currentThread().getContextClassLoader().loadClass("com.mysql.jdbc.CommunicationsException");
         } catch (ClassNotFoundException var3) {
            throw new IllegalArgumentException("com.mysql.jdbc.CommunicationsException not available, please use another ExceptionSelector");
         }
      }

      public boolean shouldFailover(SQLException ex) {
         String sqlState = ex.getSQLState();
         return sqlState != null && sqlState.startsWith("08") || this.communicationException.isInstance(ex);
      }
   }

   public static final class DataSourceHolder {
      private final DataSource dataSource;
      private final String name;

      public DataSourceHolder(DataSource dataSource, String name) {
         this.dataSource = dataSource;
         this.name = name;
      }
   }

   public interface ErrorHandler {
      void onError(Map<String, Throwable> var1, DataSourceHolder var2);
   }

   public interface ExceptionSelector {
      boolean shouldFailover(Throwable var1);
   }

   public interface Strategy {
      Collection<DataSourceHolder> prepare(Collection<DataSourceHolder> var1);

      void used(DataSourceHolder var1);
   }
}
