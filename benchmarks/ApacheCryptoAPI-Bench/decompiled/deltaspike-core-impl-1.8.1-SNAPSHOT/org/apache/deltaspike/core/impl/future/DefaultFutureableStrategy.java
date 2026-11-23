package org.apache.deltaspike.core.impl.future;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import javax.enterprise.context.Dependent;
import javax.enterprise.inject.spi.AnnotatedMethod;
import javax.enterprise.inject.spi.AnnotatedType;
import javax.enterprise.inject.spi.BeanManager;
import javax.inject.Inject;
import javax.interceptor.InvocationContext;
import org.apache.deltaspike.core.api.future.Futureable;
import org.apache.deltaspike.core.impl.util.AnnotatedMethods;
import org.apache.deltaspike.core.spi.future.FutureableStrategy;
import org.apache.deltaspike.core.util.ExceptionUtils;

@Dependent
public class DefaultFutureableStrategy implements FutureableStrategy {
   private static final Class<?> COMPLETION_STAGE;
   private static final Class<?> COMPLETABLE_FUTURE;
   private static final Method COMPLETABLE_STAGE_TO_FUTURE;
   private static final boolean IS_WELD1;
   private static final ThreadLocal<LinkedList<CallKey>> STACK = new ThreadLocal<LinkedList<CallKey>>() {
      protected LinkedList<CallKey> initialValue() {
         return new LinkedList();
      }
   };
   @Inject
   private ThreadPoolManager manager;
   @Inject
   private BeanManager beanManager;
   private transient ConcurrentMap<Method, ExecutorService> configByMethod = new ConcurrentHashMap();

   public Object execute(final InvocationContext ic) throws Exception {
      final CallKey invocationKey;
      if (IS_WELD1) {
         invocationKey = new CallKey(ic);
         LinkedList<CallKey> stack = (LinkedList)STACK.get();
         if (!stack.isEmpty() && ((CallKey)stack.getLast()).equals(invocationKey)) {
            Object var4;
            try {
               var4 = ic.proceed();
            } finally {
               if (stack.isEmpty()) {
                  STACK.remove();
               }

            }

            return var4;
         }
      } else {
         invocationKey = null;
      }

      Class<?> returnType = ic.getMethod().getReturnType();
      if (Future.class.isAssignableFrom(returnType) || Void.TYPE.isAssignableFrom(returnType) || COMPLETION_STAGE != null && COMPLETION_STAGE.isAssignableFrom(returnType)) {
         if (this.configByMethod == null) {
            synchronized(this) {
               if (this.configByMethod == null) {
                  this.configByMethod = new ConcurrentHashMap();
               }
            }
         }

         Callable<Object> invocation = new Callable<Object>() {
            public Object call() throws Exception {
               LinkedList<CallKey> callStack;
               if (DefaultFutureableStrategy.IS_WELD1) {
                  callStack = (LinkedList)DefaultFutureableStrategy.STACK.get();
                  callStack.add(invocationKey);
               } else {
                  callStack = null;
               }

               Object var4;
               try {
                  Object proceed = ic.proceed();
                  Future<?> future = DefaultFutureableStrategy.COMPLETION_STAGE != null && DefaultFutureableStrategy.COMPLETION_STAGE.isInstance(proceed) ? (Future)Future.class.cast(DefaultFutureableStrategy.COMPLETABLE_STAGE_TO_FUTURE.invoke(proceed)) : (Future)Future.class.cast(proceed);
                  var4 = future.get();
               } catch (InvocationTargetException e) {
                  throw ExceptionUtils.throwAsRuntimeException(e.getCause());
               } catch (Exception e) {
                  throw ExceptionUtils.throwAsRuntimeException(e);
               } finally {
                  if (DefaultFutureableStrategy.IS_WELD1) {
                     callStack.removeLast();
                     if (callStack.isEmpty()) {
                        DefaultFutureableStrategy.STACK.remove();
                     }
                  }

               }

               return var4;
            }
         };
         ExecutorService pool = this.getOrCreatePool(ic);
         if (Void.TYPE.isAssignableFrom(returnType)) {
            pool.submit(invocation);
            return null;
         } else if (COMPLETABLE_FUTURE == null) {
            return pool.submit(invocation);
         } else {
            Object completableFuture = COMPLETABLE_FUTURE.newInstance();
            pool.submit(new J8PromiseCompanionTask(completableFuture, invocation));
            return completableFuture;
         }
      } else {
         throw new IllegalArgumentException("Return type should be a CompletableStage, Future or Void");
      }
   }

   protected ExecutorService getOrCreatePool(InvocationContext ic) {
      Method method = ic.getMethod();
      ExecutorService executorService = (ExecutorService)this.configByMethod.get(method);
      if (executorService == null) {
         AnnotatedType<?> annotatedType = this.beanManager.createAnnotatedType(method.getDeclaringClass());
         AnnotatedMethod<?> annotatedMethod = AnnotatedMethods.findMethod(annotatedType, method);
         Futureable methodConfig = (Futureable)annotatedMethod.getAnnotation(Futureable.class);
         ExecutorService instance = this.manager.find((methodConfig == null ? (Futureable)annotatedType.getAnnotation(Futureable.class) : methodConfig).value());
         this.configByMethod.putIfAbsent(method, instance);
         executorService = instance;
      }

      return executorService;
   }

   static {
      Class<?> completionStageClass = null;
      Class<?> completableFutureClass = null;
      Method completionStageClassToCompletableFuture = null;

      try {
         ClassLoader classLoader = ClassLoader.getSystemClassLoader();
         completionStageClass = classLoader.loadClass("java.util.concurrent.CompletionStage");
         completionStageClassToCompletableFuture = completionStageClass.getMethod("toCompletableFuture");
         completableFutureClass = classLoader.loadClass("java.util.concurrent.CompletableFuture");
      } catch (Exception var6) {
      }

      COMPLETION_STAGE = completionStageClass;
      COMPLETABLE_FUTURE = completableFutureClass;
      COMPLETABLE_STAGE_TO_FUTURE = completionStageClassToCompletableFuture;
      boolean weld1 = false;

      try {
         Class<?> impl = Thread.currentThread().getContextClassLoader().loadClass("org.jboss.weld.manager.BeanManagerImpl");
         Package pck = impl.getPackage();
         weld1 = "Weld Implementation".equals(pck.getImplementationTitle()) && pck.getSpecificationVersion() != null && pck.getSpecificationVersion().startsWith("1.1.");
      } catch (Throwable var7) {
      }

      IS_WELD1 = weld1;
   }

   private static final class CallKey {
      private final InvocationContext ic;
      private final int hash;

      private CallKey(InvocationContext ic) {
         this.ic = ic;
         Object[] parameters = ic.getParameters();
         this.hash = ic.getMethod().hashCode() + (parameters == null ? 0 : Arrays.hashCode(parameters));
      }

      public boolean equals(Object o) {
         return this == o || o != null && this.getClass() == o.getClass() && this.equals(this.ic, ((CallKey)CallKey.class.cast(o)).ic);
      }

      public int hashCode() {
         return this.hash;
      }

      private boolean equals(InvocationContext ic1, InvocationContext ic2) {
         Object[] parameters1 = ic1.getParameters();
         Object[] parameters2 = ic2.getParameters();
         return ic2.getMethod().equals(ic1.getMethod()) && (parameters1 == parameters2 || parameters1 != null && parameters2 != null && Arrays.equals(parameters1, ic2.getParameters()));
      }
   }
}
