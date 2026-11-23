package org.apache.openejb.core.stateless;

import java.io.Flushable;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.logging.Level;
import javax.ejb.ConcurrentAccessTimeoutException;
import javax.ejb.EJBContext;
import javax.ejb.SessionBean;
import javax.ejb.SessionContext;
import javax.management.MBeanServer;
import javax.management.ObjectName;
import javax.naming.Context;
import javax.naming.NamingException;
import org.apache.openejb.ApplicationException;
import org.apache.openejb.BeanContext;
import org.apache.openejb.OpenEJBException;
import org.apache.openejb.SystemException;
import org.apache.openejb.cdi.CdiEjbBean;
import org.apache.openejb.core.InstanceContext;
import org.apache.openejb.core.Operation;
import org.apache.openejb.core.ThreadContext;
import org.apache.openejb.core.interceptor.InterceptorData;
import org.apache.openejb.core.interceptor.InterceptorInstance;
import org.apache.openejb.core.interceptor.InterceptorStack;
import org.apache.openejb.core.timer.TimerServiceWrapper;
import org.apache.openejb.loader.Options;
import org.apache.openejb.monitoring.LocalMBeanServer;
import org.apache.openejb.monitoring.ManagedMBean;
import org.apache.openejb.monitoring.ObjectNameBuilder;
import org.apache.openejb.monitoring.StatsInterceptor;
import org.apache.openejb.spi.SecurityService;
import org.apache.openejb.util.DaemonThreadFactory;
import org.apache.openejb.util.Duration;
import org.apache.openejb.util.LogCategory;
import org.apache.openejb.util.Logger;
import org.apache.openejb.util.PassthroughFactory;
import org.apache.openejb.util.Pool;
import org.apache.xbean.recipe.ObjectRecipe;
import org.apache.xbean.recipe.Option;

public class StatelessInstanceManager {
   private static final Logger logger;
   private static final Method removeSessionBeanMethod;
   private final Duration accessTimeout;
   private final Duration closeTimeout;
   private final SecurityService securityService;
   private final Pool.Builder poolBuilder;
   private final ThreadPoolExecutor executor;
   private final ScheduledExecutorService scheduledExecutor;

   public StatelessInstanceManager(SecurityService securityService, Duration accessTimeout, Duration closeTimeout, Pool.Builder poolBuilder, int callbackThreads, ScheduledExecutorService ses) {
      this.securityService = securityService;
      this.accessTimeout = accessTimeout;
      this.closeTimeout = closeTimeout;
      this.poolBuilder = poolBuilder;
      this.scheduledExecutor = ses;
      if (ScheduledThreadPoolExecutor.class.isInstance(ses) && !((ScheduledThreadPoolExecutor)ScheduledThreadPoolExecutor.class.cast(ses)).getRemoveOnCancelPolicy()) {
         ((ScheduledThreadPoolExecutor)ScheduledThreadPoolExecutor.class.cast(ses)).setRemoveOnCancelPolicy(true);
      }

      if (accessTimeout.getUnit() == null) {
         accessTimeout.setUnit(TimeUnit.MILLISECONDS);
      }

      int qsize = callbackThreads > 1 ? callbackThreads - 1 : 1;
      ThreadFactory threadFactory = new DaemonThreadFactory(new Object[]{"StatelessPool.worker."});
      this.executor = new ThreadPoolExecutor(callbackThreads, callbackThreads * 2, 1L, TimeUnit.MINUTES, new LinkedBlockingQueue(qsize), threadFactory);
      this.executor.setRejectedExecutionHandler(new RejectedExecutionHandler() {
         public void rejectedExecution(Runnable r, ThreadPoolExecutor tpe) {
            if (null != r && null != tpe && !tpe.isShutdown() && !tpe.isTerminated() && !tpe.isTerminating()) {
               try {
                  if (!tpe.getQueue().offer(r, 20L, TimeUnit.SECONDS)) {
                     StatelessInstanceManager.logger.warning("Executor failed to run asynchronous process: " + r);
                  }
               } catch (InterruptedException var4) {
               }

            }
         }
      });
   }

   public void destroy() {
      if (this.executor != null) {
         this.executor.shutdown();

         try {
            if (!this.executor.awaitTermination(10000L, TimeUnit.MILLISECONDS)) {
               java.util.logging.Logger.getLogger(this.getClass().getName()).log(Level.WARNING, this.getClass().getSimpleName() + " pool  timeout expired");
            }
         } catch (InterruptedException var3) {
            Thread.interrupted();
         }
      }

      if (this.scheduledExecutor != null) {
         this.scheduledExecutor.shutdown();

         try {
            if (!this.scheduledExecutor.awaitTermination(10000L, TimeUnit.MILLISECONDS)) {
               java.util.logging.Logger.getLogger(this.getClass().getName()).log(Level.WARNING, this.getClass().getSimpleName() + " pool  timeout expired");
            }
         } catch (InterruptedException var2) {
            Thread.interrupted();
         }
      }

   }

   public Instance getInstance(ThreadContext callContext) throws OpenEJBException {
      BeanContext beanContext = callContext.getBeanContext();
      Data data = (Data)beanContext.getContainerData();
      Instance instance = null;

      try {
         Pool<Instance>.Entry entry = data.poolPop();
         if (entry != null) {
            instance = (Instance)entry.get();
            instance.setPoolEntry(entry);
         }
      } catch (TimeoutException var8) {
         String msg = "No instances available in Stateless Session Bean pool.  Waited " + data.accessTimeout.toString();
         ConcurrentAccessTimeoutException timeoutException = new ConcurrentAccessTimeoutException(msg);
         timeoutException.fillInStackTrace();
         throw new ApplicationException(timeoutException);
      } catch (InterruptedException e) {
         Thread.interrupted();
         throw new OpenEJBException("Unexpected Interruption of current thread: ", e);
      }

      if (null == instance) {
         instance = this.createInstance(callContext, beanContext);
      }

      return instance;
   }

   private Instance createInstance(ThreadContext callContext, BeanContext beanContext) throws ApplicationException {
      try {
         InstanceContext context = beanContext.newInstance();
         return new Instance(context.getBean(), context.getInterceptors(), context.getCreationalContext());
      } catch (Throwable var5) {
         Throwable e = var5;
         if (var5 instanceof InvocationTargetException) {
            e = ((InvocationTargetException)var5).getTargetException();
         }

         String t = "The bean instance " + beanContext.getDeploymentID() + " threw a system exception:" + e;
         logger.error(t, e);
         throw new ApplicationException(new RemoteException("Cannot obtain a free instance.", e));
      }
   }

   public void poolInstance(ThreadContext callContext, Object bean) throws OpenEJBException {
      if (bean == null) {
         throw new SystemException("Invalid arguments");
      } else {
         Instance instance = (Instance)Instance.class.cast(bean);
         BeanContext beanContext = callContext.getBeanContext();
         Data data = (Data)beanContext.getContainerData();
         Pool<Instance> pool = data.getPool();
         if (instance.getPoolEntry() != null) {
            pool.push(instance.getPoolEntry());
         } else {
            pool.push(instance);
         }

      }
   }

   public void discardInstance(ThreadContext callContext, Object bean) throws SystemException {
      if (bean == null) {
         throw new SystemException("Invalid arguments");
      } else {
         Instance instance = (Instance)Instance.class.cast(bean);
         BeanContext beanContext = callContext.getBeanContext();
         Data data = (Data)beanContext.getContainerData();
         if (null != data) {
            Pool<Instance> pool = data.getPool();
            pool.discard(instance.getPoolEntry());
         }

      }
   }

   private void freeInstance(ThreadContext callContext, Instance instance) {
      try {
         callContext.setCurrentOperation(Operation.PRE_DESTROY);
         BeanContext beanContext = callContext.getBeanContext();
         Method remove = instance.bean instanceof SessionBean ? removeSessionBeanMethod : null;
         List<InterceptorData> callbackInterceptors = beanContext.getCallbackInterceptors();
         InterceptorStack interceptorStack = new InterceptorStack(instance.bean, remove, Operation.PRE_DESTROY, callbackInterceptors, instance.interceptors);
         CdiEjbBean<Object> bean = (CdiEjbBean)beanContext.get(CdiEjbBean.class);
         if (bean != null) {
            bean.getInjectionTarget().preDestroy(instance.bean);
         }

         interceptorStack.invoke();
         if (instance.creationalContext != null) {
            instance.creationalContext.release();
         }
      } catch (Throwable re) {
         logger.error("The bean instance " + instance + " threw a system exception:" + re, re);
      }

   }

   public void deploy(BeanContext beanContext) throws OpenEJBException {
      Options options = new Options(beanContext.getProperties());
      Duration accessTimeout = this.getDuration(options, "AccessTimeout", this.getDuration(options, "Timeout", this.accessTimeout, TimeUnit.MILLISECONDS), TimeUnit.MILLISECONDS);
      Duration closeTimeout = this.getDuration(options, "CloseTimeout", this.closeTimeout, TimeUnit.MINUTES);
      ObjectRecipe recipe = PassthroughFactory.recipe(new Pool.Builder(this.poolBuilder));
      recipe.allow(Option.CASE_INSENSITIVE_FACTORY);
      recipe.allow(Option.CASE_INSENSITIVE_PROPERTIES);
      recipe.allow(Option.IGNORE_MISSING_PROPERTIES);
      recipe.setAllProperties(beanContext.getProperties());
      Pool.Builder builder = (Pool.Builder)recipe.create();
      this.setDefault(builder.getMaxAge(), TimeUnit.HOURS);
      this.setDefault(builder.getIdleTimeout(), TimeUnit.MINUTES);
      this.setDefault(builder.getInterval(), TimeUnit.MINUTES);
      StatelessSupplier supplier = new StatelessSupplier(beanContext);
      builder.setSupplier(supplier);
      builder.setExecutor(this.executor);
      builder.setScheduledExecutor(this.scheduledExecutor);
      Data data = new Data(builder.build(), accessTimeout, closeTimeout);
      beanContext.setContainerData(data);
      beanContext.set(EJBContext.class, data.sessionContext);

      try {
         Context context = beanContext.getJndiEnc();
         context.bind("comp/EJBContext", data.sessionContext);
         context.bind("comp/WebServiceContext", new EjbWsContext(data.sessionContext));
         context.bind("comp/TimerService", new TimerServiceWrapper());
      } catch (NamingException e) {
         throw new OpenEJBException("Failed to bind EJBContext/WebServiceContext/TimerService", e);
      }

      int min = builder.getMin();
      long maxAge = builder.getMaxAge().getTime(TimeUnit.MILLISECONDS);
      double maxAgeOffset = builder.getMaxAgeOffset();
      ObjectNameBuilder jmxName = new ObjectNameBuilder("openejb.management");
      jmxName.set("J2EEServer", "openejb");
      jmxName.set("J2EEApplication", (String)null);
      jmxName.set("EJBModule", beanContext.getModuleID());
      jmxName.set("StatelessSessionBean", beanContext.getEjbName());
      jmxName.set("name", beanContext.getEjbName());
      MBeanServer server = LocalMBeanServer.get();
      if (StatsInterceptor.isStatsActivated()) {
         StatsInterceptor stats = null;

         for(InterceptorInstance interceptor : beanContext.getUserAndSystemInterceptors()) {
            if (interceptor.getInterceptor() instanceof StatsInterceptor) {
               stats = (StatsInterceptor)interceptor.getInterceptor();
            }
         }

         if (stats == null) {
            stats = new StatsInterceptor(beanContext.getBeanClass());
            beanContext.addFirstSystemInterceptor(stats);
         }

         try {
            ObjectName objectName = jmxName.set("j2eeType", "Invocations").build();
            if (server.isRegistered(objectName)) {
               server.unregisterMBean(objectName);
            }

            server.registerMBean(new ManagedMBean(stats), objectName);
            data.add(objectName);
         } catch (Exception e) {
            logger.error("Unable to register MBean ", (Throwable)e);
         }
      }

      try {
         ObjectName objectName = jmxName.set("j2eeType", "Pool").build();
         if (server.isRegistered(objectName)) {
            server.unregisterMBean(objectName);
         }

         server.registerMBean(new ManagedMBean(data.pool), objectName);
         data.add(objectName);
      } catch (Exception e) {
         logger.error("Unable to register MBean ", (Throwable)e);
      }

      if (!options.get("BackgroundStartup", false) && min > 0) {
         ExecutorService es = Executors.newFixedThreadPool(min);

         for(int i = 0; i < min; ++i) {
            es.submit(new InstanceCreatorRunnable(maxAge, (long)i, (long)min, maxAgeOffset, data, supplier));
         }

         es.shutdown();

         try {
            es.awaitTermination(5L, TimeUnit.MINUTES);
         } catch (InterruptedException e) {
            logger.error("can't fill the stateless pool", (Throwable)e);
         }
      }

      data.getPool().start();
   }

   private void setDefault(Duration duration, TimeUnit unit) {
      if (duration.getUnit() == null) {
         duration.setUnit(unit);
      }

   }

   private Duration getDuration(Options options, String property, Duration defaultValue, TimeUnit defaultUnit) {
      String s = options.get(property, defaultValue.toString());
      Duration duration = new Duration(s);
      if (duration.getUnit() == null) {
         duration.setUnit(defaultUnit);
      }

      return duration;
   }

   public void undeploy(BeanContext beanContext) {
      Data data = (Data)beanContext.getContainerData();
      if (data != null) {
         MBeanServer server = LocalMBeanServer.get();

         for(ObjectName objectName : data.jmxNames) {
            try {
               server.unregisterMBean(objectName);
            } catch (Exception var8) {
               logger.error("Unable to unregister MBean " + objectName);
            }
         }

         try {
            if (!data.closePool()) {
               logger.error("Timed-out waiting for stateless pool to close: for deployment '" + beanContext.getDeploymentID() + "'");
            }
         } catch (InterruptedException var7) {
            Thread.interrupted();
         }

         beanContext.setContainerData((Object)null);
      }
   }

   static {
      logger = Logger.getInstance(LogCategory.OPENEJB, "org.apache.openejb.util.resources");

      Method foundRemoveMethod;
      try {
         foundRemoveMethod = SessionBean.class.getDeclaredMethod("ejbRemove");
      } catch (NoSuchMethodException var2) {
         foundRemoveMethod = null;
      }

      removeSessionBeanMethod = foundRemoveMethod;
   }

   private final class StatelessSupplier implements Pool.Supplier<Instance> {
      private final BeanContext beanContext;

      private StatelessSupplier(BeanContext beanContext) {
         this.beanContext = beanContext;
      }

      public void discard(Instance instance, Pool.Event reason) {
         ThreadContext ctx = new ThreadContext(this.beanContext, (Object)null);
         ThreadContext oldCallContext = ThreadContext.enter(ctx);

         try {
            StatelessInstanceManager.this.freeInstance(ctx, instance);
         } finally {
            ThreadContext.exit(oldCallContext);
         }

      }

      public Instance create() {
         ThreadContext ctx = new ThreadContext(this.beanContext, (Object)null);
         ThreadContext oldCallContext = ThreadContext.enter(ctx);

         try {
            Instance var3 = StatelessInstanceManager.this.createInstance(ctx, ctx.getBeanContext());
            return var3;
         } catch (OpenEJBException e) {
            StatelessInstanceManager.logger.error("Unable to fill pool: for deployment '" + this.beanContext.getDeploymentID() + "'", (Throwable)e);
         } finally {
            ThreadContext.exit(oldCallContext);
         }

         return null;
      }
   }

   private final class Data {
      private final Pool<Instance> pool;
      private final Duration accessTimeout;
      private final Duration closeTimeout;
      private final List<ObjectName> jmxNames;
      private final SessionContext sessionContext;

      private Data(Pool<Instance> pool, Duration accessTimeout, Duration closeTimeout) {
         this.jmxNames = new ArrayList();
         this.pool = pool;
         this.accessTimeout = accessTimeout;
         this.closeTimeout = closeTimeout;
         this.sessionContext = new StatelessContext(StatelessInstanceManager.this.securityService, new Flushable() {
            public void flush() throws IOException {
               Data.this.getPool().flush();
            }
         });
      }

      public Duration getAccessTimeout() {
         return this.accessTimeout;
      }

      public Pool<Instance>.Entry poolPop() throws InterruptedException, TimeoutException {
         return this.pool.pop(this.accessTimeout.getTime(), this.accessTimeout.getUnit());
      }

      public Pool<Instance> getPool() {
         return this.pool;
      }

      public boolean closePool() throws InterruptedException {
         return this.pool.close(this.closeTimeout.getTime(), this.closeTimeout.getUnit());
      }

      public ObjectName add(ObjectName name) {
         this.jmxNames.add(name);
         return name;
      }
   }

   private final class InstanceCreatorRunnable implements Runnable {
      private final long maxAge;
      private final long iteration;
      private final double maxAgeOffset;
      private final long min;
      private final Data data;
      private final StatelessSupplier supplier;

      private InstanceCreatorRunnable(long maxAge, long iteration, long min, double maxAgeOffset, Data data, StatelessSupplier supplier) {
         this.maxAge = maxAge;
         this.iteration = iteration;
         this.min = min;
         this.maxAgeOffset = maxAgeOffset;
         this.data = data;
         this.supplier = supplier;
      }

      public void run() {
         Instance obj = this.supplier.create();
         if (obj != null) {
            long offset = this.maxAge > 0L ? (long)((double)this.maxAge / this.maxAgeOffset * (double)this.min * (double)this.iteration) % this.maxAge : 0L;
            this.data.getPool().add(obj, offset);
         }

      }
   }
}
