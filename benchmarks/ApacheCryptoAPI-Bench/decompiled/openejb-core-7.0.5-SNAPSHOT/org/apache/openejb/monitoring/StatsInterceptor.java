package org.apache.openejb.monitoring;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.AfterBegin;
import javax.ejb.AfterCompletion;
import javax.ejb.BeforeCompletion;
import javax.ejb.PostActivate;
import javax.ejb.PrePassivate;
import javax.interceptor.AroundInvoke;
import javax.interceptor.AroundTimeout;
import javax.interceptor.InvocationContext;
import org.apache.openejb.api.Monitor;
import org.apache.openejb.core.interceptor.InterceptorData;
import org.apache.openejb.loader.SystemInstance;
import org.apache.openejb.math.stat.descriptive.SynchronizedDescriptiveStatistics;
import org.apache.openejb.util.LogCategory;
import org.apache.openejb.util.Logger;
import org.apache.xbean.finder.ClassFinder;

public class StatsInterceptor {
   private static final String DISABLE_STAT_INTERCEPTOR_PROPERTY = "openejb.stats.interceptor.disable";
   public static final InterceptorData metadata;
   private final Map<Method, Stats> map = new ConcurrentHashMap();
   private final AtomicLong invocations = new AtomicLong();
   private final AtomicLong invocationTime = new AtomicLong();
   private final Monitor monitor;
   private final boolean enabled;

   public StatsInterceptor(Class<?> componentClass) {
      this.monitor = (Monitor)componentClass.getAnnotation(Monitor.class);
      ClassFinder finder = new ClassFinder(new Class[]{componentClass});

      for(Method method : finder.findAnnotatedMethods(Monitor.class)) {
         this.map.put(method, new Stats(method, this.monitor));
      }

      this.enabled = this.monitor != null || this.map.size() > 0;
   }

   public boolean isMonitoringEnabled() {
      return this.enabled;
   }

   @Managed
   public long getInvocationCount() {
      return this.invocations.get();
   }

   @Managed
   public long getInvocationTime() {
      return this.invocationTime.get();
   }

   @Managed
   public long getMonitoredMethods() {
      return (long)this.map.size();
   }

   @ManagedCollection(
      type = Stats.class,
      key = "method"
   )
   public Collection<Stats> stats() {
      return this.map.values();
   }

   @AroundInvoke
   public Object invoke(InvocationContext invocationContext) throws Exception {
      return this.record(invocationContext, (Method)null);
   }

   public Method PostConstruct() throws NoSuchMethodException {
      return this.getClass().getMethod("PostConstruct");
   }

   @PostConstruct
   public void PostConstruct(InvocationContext invocationContext) throws Exception {
      long start = System.nanoTime();
      this.record(invocationContext, this.PostConstruct());
      long end = System.nanoTime();
      Logger.getInstance(LogCategory.MONITORING, "org.apache.openejb.monitoring").debug("instance.created", invocationContext.getTarget().getClass().getName(), end - start);
   }

   public Method PreDestroy() throws NoSuchMethodException {
      return this.getClass().getMethod("PreDestroy");
   }

   @PreDestroy
   public void PreDestroy(InvocationContext invocationContext) throws Exception {
      long start = System.nanoTime();
      this.record(invocationContext, this.PreDestroy());
      long end = System.nanoTime();
      Logger.getInstance(LogCategory.MONITORING, "org.apache.openejb.monitoring").debug("instance.discarded", invocationContext.getTarget().getClass().getName(), end - start);
   }

   public Method PostActivate() throws NoSuchMethodException {
      return this.getClass().getMethod("PostActivate");
   }

   @PostActivate
   public void PostActivate(InvocationContext invocationContext) throws Exception {
      this.record(invocationContext, this.PostActivate());
   }

   public Method PrePassivate() throws NoSuchMethodException {
      return this.getClass().getMethod("PrePassivate");
   }

   @PrePassivate
   public void PrePassivate(InvocationContext invocationContext) throws Exception {
      this.record(invocationContext, this.PrePassivate());
   }

   public Method AroundTimeout() throws NoSuchMethodException {
      return this.getClass().getMethod("AroundTimeout");
   }

   @AroundTimeout
   public void AroundTimeout(InvocationContext invocationContext) throws Exception {
      this.record(invocationContext, this.AroundTimeout());
   }

   public Method AfterBegin() throws NoSuchMethodException {
      return this.getClass().getMethod("AfterBegin");
   }

   @AfterBegin
   public void AfterBegin(InvocationContext invocationContext) throws Exception {
      this.record(invocationContext, this.AfterBegin());
   }

   public Method BeforeCompletion() throws NoSuchMethodException {
      return this.getClass().getMethod("BeforeCompletion");
   }

   @BeforeCompletion
   public void BeforeCompletion(InvocationContext invocationContext) throws Exception {
      this.record(invocationContext, this.BeforeCompletion());
   }

   public Method AfterCompletion() throws NoSuchMethodException {
      return this.getClass().getMethod("AfterCompletion");
   }

   @AfterCompletion
   public void AfterCompletion(InvocationContext invocationContext) throws Exception {
      this.record(invocationContext, this.AfterCompletion());
   }

   private Object record(InvocationContext invocationContext, Method callback) throws Exception {
      this.invocations.incrementAndGet();
      Stats stats = this.enabled ? this.stats(invocationContext, callback) : null;
      long start = System.nanoTime();
      boolean var13 = false;

      Object var6;
      try {
         var13 = true;
         var6 = invocationContext.proceed();
         var13 = false;
      } finally {
         if (var13) {
            long time = System.nanoTime() - start;
            time = this.millis(time);
            if (stats != null) {
               stats.record(time);
            }

            this.invocationTime.addAndGet(time);
         }
      }

      long time = System.nanoTime() - start;
      time = this.millis(time);
      if (stats != null) {
         stats.record(time);
      }

      this.invocationTime.addAndGet(time);
      return var6;
   }

   private long millis(long nanos) {
      return TimeUnit.MILLISECONDS.convert(nanos, TimeUnit.NANOSECONDS);
   }

   private Stats stats(InvocationContext invocationContext, Method callback) {
      Method method = callback == null ? invocationContext.getMethod() : callback;
      Stats stats = (Stats)this.map.get(method);
      if (stats == null) {
         synchronized(this.map) {
            stats = (Stats)this.map.get(method);
            if (stats == null) {
               stats = new Stats(method, this.monitor);
               this.map.put(method, stats);
            }
         }
      }

      return stats;
   }

   public static boolean isStatsActivated() {
      return SystemInstance.get().getOptions().get("openejb.stats.interceptor.disable", true);
   }

   static {
      InterceptorData.cacheScan(StatsInterceptor.class);
      metadata = InterceptorData.scan(StatsInterceptor.class);
   }

   public class Stats {
      private final AtomicLong count = new AtomicLong();
      private final SynchronizedDescriptiveStatistics samples;
      private final String method;

      public Stats(Method method, Monitor classAnnotation) {
         Monitor methodAnnotation = (Monitor)method.getAnnotation(Monitor.class);
         int window = methodAnnotation != null ? methodAnnotation.sample() : (classAnnotation != null ? classAnnotation.sample() : 2000);
         this.samples = new SynchronizedDescriptiveStatistics(window);
         String s = ",";
         StringBuilder sb = new StringBuilder(method.getName());
         sb.append("(");
         Class<?>[] params = method.getParameterTypes();

         for(Class<?> clazz : params) {
            sb.append(clazz.getSimpleName());
            sb.append(",");
         }

         if (params.length > 0) {
            sb.delete(sb.length() - ",".length(), sb.length());
         }

         sb.append(")");
         this.method = sb.toString();
      }

      @Managed
      public void setSampleSize(int i) {
         this.samples.setWindowSize(i);
      }

      @Managed
      public int getSampleSize() {
         return this.samples.getWindowSize();
      }

      @Managed
      public long getCount() {
         return this.count.get();
      }

      @Managed
      public double getPercentile99() {
         return this.samples.getPercentile((double)99.0F);
      }

      @Managed
      public double getPercentile90() {
         return this.samples.getPercentile((double)90.0F);
      }

      @Managed
      public double getPercentile75() {
         return this.samples.getPercentile((double)75.0F);
      }

      @Managed
      public double getPercentile50() {
         return this.samples.getPercentile((double)50.0F);
      }

      @Managed
      public double getPercentile25() {
         return this.samples.getPercentile((double)25.0F);
      }

      @Managed
      public double getPercentile10() {
         return this.samples.getPercentile((double)10.0F);
      }

      @Managed
      public double getPercentile01() {
         return this.samples.getPercentile((double)1.0F);
      }

      @Managed
      public double getStandardDeviation() {
         return this.samples.getStandardDeviation();
      }

      @Managed
      public double getMean() {
         return this.samples.getMean();
      }

      @Managed
      public double getVariance() {
         return this.samples.getVariance();
      }

      @Managed
      public double getGeometricMean() {
         return this.samples.getGeometricMean();
      }

      @Managed
      public double getSkewness() {
         return this.samples.getSkewness();
      }

      @Managed
      public double getKurtosis() {
         return this.samples.getKurtosis();
      }

      @Managed
      public double getMax() {
         return this.samples.getMax();
      }

      @Managed
      public double getMin() {
         return this.samples.getMin();
      }

      @Managed
      public double getSum() {
         return this.samples.getSum();
      }

      @Managed
      public double getSumsq() {
         return this.samples.getSumsq();
      }

      @Managed
      public double[] sortedValues() {
         return this.samples.getSortedValues();
      }

      @Managed
      public double[] values() {
         return this.samples.getValues();
      }

      public void record(long time) {
         this.count.incrementAndGet();
         this.samples.addValue((double)time);
      }
   }
}
