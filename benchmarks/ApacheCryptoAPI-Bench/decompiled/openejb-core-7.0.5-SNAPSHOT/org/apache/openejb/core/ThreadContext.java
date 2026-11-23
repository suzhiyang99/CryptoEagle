package org.apache.openejb.core;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicBoolean;
import org.apache.openejb.BeanContext;
import org.apache.openejb.core.transaction.TransactionPolicy;
import org.apache.openejb.util.LogCategory;
import org.apache.openejb.util.Logger;

public class ThreadContext {
   private static final Logger log;
   private static final ThreadLocal<ThreadContext> threadStorage;
   private static final List<ThreadContextListener> listeners;
   private static final ThreadLocal<AtomicBoolean> asynchronousCancelled;
   private final BeanContext beanContext;
   private final Object primaryKey;
   private final Map<Class, Object> data;
   private ClassLoader oldClassLoader;
   private Operation currentOperation;
   private Class invokedInterface;
   private TransactionPolicy transactionPolicy;
   private boolean discardInstance;

   public static ThreadContext getThreadContext() {
      return (ThreadContext)threadStorage.get();
   }

   public static ThreadContext enter(ThreadContext newContext) {
      if (newContext == null) {
         throw new NullPointerException("newContext is null");
      } else {
         Thread thread = Thread.currentThread();
         newContext.oldClassLoader = thread.getContextClassLoader();
         thread.setContextClassLoader(newContext.beanContext.getClassLoader());
         ThreadContext oldContext = (ThreadContext)threadStorage.get();
         threadStorage.set(newContext);

         for(ThreadContextListener listener : listeners) {
            try {
               listener.contextEntered(oldContext, newContext);
            } catch (Throwable e) {
               log.warning("ThreadContextListener threw an exception", e);
            }
         }

         return oldContext;
      }
   }

   public static void exit(ThreadContext oldContext) {
      ThreadContext exitingContext = (ThreadContext)threadStorage.get();
      if (exitingContext == null) {
         throw new IllegalStateException("No existing context");
      } else {
         Thread.currentThread().setContextClassLoader(exitingContext.oldClassLoader);
         exitingContext.oldClassLoader = null;
         threadStorage.set(oldContext);

         for(ThreadContextListener listener : listeners) {
            try {
               listener.contextExited(exitingContext, oldContext);
            } catch (Throwable e) {
               log.debug("ThreadContextListener threw an exception", e);
            }
         }

      }
   }

   public static void initAsynchronousCancelled(AtomicBoolean initializeValue) {
      asynchronousCancelled.set(initializeValue);
   }

   public static boolean isAsynchronousCancelled() {
      return ((AtomicBoolean)asynchronousCancelled.get()).get();
   }

   public static void removeAsynchronousCancelled() {
      asynchronousCancelled.remove();
   }

   public static void addThreadContextListener(ThreadContextListener listener) {
      listeners.add(listener);
   }

   public static void removeThreadContextListener(ThreadContextListener listener) {
      listeners.remove(listener);
   }

   public ThreadContext(BeanContext beanContext, Object primaryKey) {
      this(beanContext, primaryKey, (Operation)null);
   }

   public ThreadContext(BeanContext beanContext, Object primaryKey, Operation operation) {
      this.data = Collections.synchronizedMap(new HashMap());
      if (beanContext == null) {
         throw new NullPointerException("deploymentInfo is null");
      } else {
         this.beanContext = beanContext;
         this.primaryKey = primaryKey;
         this.currentOperation = operation;
      }
   }

   public ThreadContext(ThreadContext that) {
      this.data = Collections.synchronizedMap(new HashMap());
      this.beanContext = that.beanContext;
      this.primaryKey = that.primaryKey;
      this.data.putAll(that.data);
      this.oldClassLoader = that.oldClassLoader;
   }

   public BeanContext getBeanContext() {
      return this.beanContext;
   }

   public Object getPrimaryKey() {
      return this.primaryKey;
   }

   public Operation getCurrentOperation() {
      return this.currentOperation;
   }

   public void setCurrentOperation(Operation operation) {
      this.currentOperation = operation;
   }

   public Class getInvokedInterface() {
      return this.invokedInterface;
   }

   public void setInvokedInterface(Class invokedInterface) {
      this.invokedInterface = invokedInterface;
   }

   public TransactionPolicy getTransactionPolicy() {
      return this.transactionPolicy;
   }

   public void setTransactionPolicy(TransactionPolicy transactionPolicy) {
      this.transactionPolicy = transactionPolicy;
   }

   public BaseContext.State[] getCurrentAllowedStates() {
      return null;
   }

   public BaseContext.State[] setCurrentAllowedStates(BaseContext.State[] newAllowedStates) {
      return null;
   }

   public <T> T get(Class<T> type) {
      return (T)this.data.get(type);
   }

   public <T> T set(Class<T> type, T value) {
      return (T)this.data.put(type, value);
   }

   public <T> T remove(Class<T> type) {
      return (T)this.data.remove(type);
   }

   public boolean isDiscardInstance() {
      return this.discardInstance;
   }

   public void setDiscardInstance(boolean discardInstance) {
      this.discardInstance = discardInstance;
   }

   public String toString() {
      return "ThreadContext{beanContext=" + this.beanContext.getId() + ", primaryKey=" + this.primaryKey + ", data=" + this.data.size() + ", oldClassLoader=" + this.oldClassLoader + ", currentOperation=" + this.currentOperation + ", invokedInterface=" + this.invokedInterface + ", transactionPolicy=" + this.transactionPolicy + ", discardInstance=" + this.discardInstance + '}';
   }

   static {
      log = Logger.getInstance(LogCategory.OPENEJB, "org.apache.openejb.util.resources");
      threadStorage = new ThreadLocal();
      listeners = new CopyOnWriteArrayList();
      asynchronousCancelled = new ThreadLocal();
   }
}
