package org.apache.openejb.threads.task;

import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.Callable;
import javax.security.auth.login.LoginException;
import org.apache.openejb.OpenEJBRuntimeException;
import org.apache.openejb.core.ThreadContext;
import org.apache.openejb.core.ivm.ClientSecurity;
import org.apache.openejb.core.security.AbstractSecurityService;
import org.apache.openejb.loader.SystemInstance;
import org.apache.openejb.spi.SecurityService;
import org.apache.openejb.util.Join;
import org.apache.openejb.util.LogCategory;
import org.apache.openejb.util.Logger;

public abstract class CUTask<T> extends ManagedTaskListenerTask implements Comparable<Object> {
   private static final SecurityService SECURITY_SERVICE = (SecurityService)SystemInstance.get().getComponent(SecurityService.class);
   private static volatile ContainerListener[] CONTAINER_LISTENERS = new ContainerListener[0];
   private final Context initialContext;
   private final Object[] containerListenerStates;

   public static void addContainerListener(ContainerListener cl) {
      ContainerListener[] array = new ContainerListener[CONTAINER_LISTENERS.length + 1];
      if (CONTAINER_LISTENERS.length > 0) {
         System.arraycopy(CONTAINER_LISTENERS, 0, array, 0, CONTAINER_LISTENERS.length);
      }

      array[CONTAINER_LISTENERS.length] = cl;
      CONTAINER_LISTENERS = array;
   }

   public CUTask(Object task) {
      super(task);
      Object stateTmp = SECURITY_SERVICE.currentState();
      boolean associate;
      if (stateTmp == null) {
         stateTmp = ClientSecurity.getIdentity();
         associate = stateTmp != null;
      } else {
         associate = false;
      }

      ThreadContext threadContext = ThreadContext.getThreadContext();
      AbstractSecurityService.SecurityContext sc = threadContext == null ? null : (AbstractSecurityService.SecurityContext)threadContext.get(AbstractSecurityService.SecurityContext.class);
      if (threadContext == null || threadContext.getBeanContext() == null || threadContext.getBeanContext().getRunAs() == null && threadContext.getBeanContext().getRunAsUser() == null) {
         this.initialContext = new Context(associate, stateTmp, sc, threadContext, Thread.currentThread().getContextClassLoader(), (Context)null);
      } else {
         this.initialContext = new Context(associate, stateTmp, new AbstractSecurityService.SecurityContext(((AbstractSecurityService)AbstractSecurityService.class.cast(SECURITY_SERVICE)).getRunAsSubject(threadContext.getBeanContext())), threadContext, Thread.currentThread().getContextClassLoader(), (Context)null);
      }

      if (CONTAINER_LISTENERS.length > 0) {
         this.containerListenerStates = new Object[CONTAINER_LISTENERS.length];

         for(int i = 0; i < CONTAINER_LISTENERS.length; ++i) {
            this.containerListenerStates[i] = CONTAINER_LISTENERS[i].onCreation();
         }
      } else {
         this.containerListenerStates = null;
      }

   }

   protected T invoke(Callable<T> call) throws Exception {
      this.initialContext.enter();
      Object[] oldStates;
      if (CONTAINER_LISTENERS.length > 0) {
         oldStates = new Object[CONTAINER_LISTENERS.length];

         for(int i = 0; i < CONTAINER_LISTENERS.length; ++i) {
            oldStates[i] = CONTAINER_LISTENERS[i].onStart(this.containerListenerStates[i]);
         }
      } else {
         oldStates = null;
      }

      Throwable throwable = null;
      boolean var37 = false;

      Object var4;
      label624: {
         Object i;
         try {
            var37 = true;
            this.taskStarting(this.future, this.executor, this.delegate);
            var4 = call.call();
            var37 = false;
            break label624;
         } catch (Throwable t) {
            throwable = t;
            this.taskAborted(t);
            i = this.rethrow(t);
            var37 = false;
         } finally {
            if (var37) {
               boolean var31 = false;

               try {
                  var31 = true;
                  this.taskDone(this.future, this.executor, this.delegate, throwable);
                  var31 = false;
               } finally {
                  if (var31) {
                     if (CONTAINER_LISTENERS.length > 0) {
                        for(int i = 0; i < CONTAINER_LISTENERS.length; ++i) {
                           CONTAINER_LISTENERS[i].onEnd(oldStates[i]);
                        }
                     }

                     this.initialContext.exit();
                  }
               }

               if (CONTAINER_LISTENERS.length > 0) {
                  for(int i = 0; i < CONTAINER_LISTENERS.length; ++i) {
                     CONTAINER_LISTENERS[i].onEnd(oldStates[i]);
                  }
               }

               this.initialContext.exit();
            }
         }

         boolean var25 = false;

         try {
            var25 = true;
            this.taskDone(this.future, this.executor, this.delegate, throwable);
            var25 = false;
         } finally {
            if (var25) {
               if (CONTAINER_LISTENERS.length > 0) {
                  for(int i = 0; i < CONTAINER_LISTENERS.length; ++i) {
                     CONTAINER_LISTENERS[i].onEnd(oldStates[i]);
                  }
               }

               this.initialContext.exit();
            }
         }

         if (CONTAINER_LISTENERS.length > 0) {
            for(int i = 0; i < CONTAINER_LISTENERS.length; ++i) {
               CONTAINER_LISTENERS[i].onEnd(oldStates[i]);
            }
         }

         this.initialContext.exit();
         return (T)i;
      }

      boolean var19 = false;

      try {
         var19 = true;
         this.taskDone(this.future, this.executor, this.delegate, throwable);
         var19 = false;
      } finally {
         if (var19) {
            if (CONTAINER_LISTENERS.length > 0) {
               for(int i = 0; i < CONTAINER_LISTENERS.length; ++i) {
                  CONTAINER_LISTENERS[i].onEnd(oldStates[i]);
               }
            }

            this.initialContext.exit();
         }
      }

      if (CONTAINER_LISTENERS.length > 0) {
         for(int i = 0; i < CONTAINER_LISTENERS.length; ++i) {
            CONTAINER_LISTENERS[i].onEnd(oldStates[i]);
         }
      }

      this.initialContext.exit();
      return (T)var4;
   }

   private T rethrow(Throwable t) throws Exception {
      if (Exception.class.isInstance(t)) {
         throw (Exception)Exception.class.cast(t);
      } else if (Error.class.isInstance(t)) {
         throw (Error)Error.class.cast(t);
      } else {
         throw new OpenEJBRuntimeException(t.getMessage(), t);
      }
   }

   public int compareTo(Object o) {
      return Comparable.class.isInstance(this.delegate) ? ((Comparable)Comparable.class.cast(this.delegate)).compareTo(o) : -1;
   }

   public static final class Context {
      public static final ThreadLocal<Context> CURRENT = new ThreadLocal();
      private final Object securityServiceState;
      private final ThreadContext threadContext;
      private final ClassLoader loader;
      private final boolean associate;
      private final AbstractSecurityService.SecurityContext securityContext;
      private final Context stack;
      private Context currentContext;
      private Collection<Runnable> exitTasks;

      private Context(boolean associate, Object initialSecurityServiceState, AbstractSecurityService.SecurityContext securityContext, ThreadContext initialThreadContext, ClassLoader initialLoader, Context stack) {
         this.associate = associate;
         this.securityServiceState = initialSecurityServiceState;
         this.securityContext = securityContext;
         this.loader = initialLoader;
         this.stack = stack;
         this.threadContext = initialThreadContext == null ? null : new ThreadContext(initialThreadContext);
      }

      public void enter() {
         Thread thread = Thread.currentThread();
         ClassLoader oldCl = thread.getContextClassLoader();
         thread.setContextClassLoader(this.loader);
         Object threadState;
         if (this.associate) {
            try {
               CUTask.SECURITY_SERVICE.associate(this.securityServiceState);
            } catch (LoginException e) {
               throw new IllegalStateException(e);
            }

            threadState = null;
         } else {
            threadState = CUTask.SECURITY_SERVICE.currentState();
            CUTask.SECURITY_SERVICE.setState(this.securityServiceState);
         }

         ThreadContext oldCtx;
         if (this.threadContext != null) {
            ThreadContext newContext = new ThreadContext(this.threadContext);
            newContext.set(Context.class, this);
            if (this.securityContext != null) {
               newContext.set(AbstractSecurityService.ProvidedSecurityContext.class, new AbstractSecurityService.ProvidedSecurityContext(this.securityContext));
            }

            oldCtx = ThreadContext.enter(newContext);
         } else {
            oldCtx = null;
         }

         this.currentContext = new Context(this.associate, threadState, this.securityContext, oldCtx, oldCl, this);
         CURRENT.set(this);
      }

      public void exit() {
         Collection<RuntimeException> errors = null;
         if (this.exitTasks != null) {
            for(Runnable r : this.exitTasks) {
               try {
                  r.run();
               } catch (RuntimeException re) {
                  if (errors == null) {
                     errors = new ArrayList();
                  }

                  errors.add(re);
                  Logger.getInstance(LogCategory.OPENEJB, CUTask.class).warning(re.getMessage(), (Throwable)re);
               }
            }
         }

         if (this.threadContext != null) {
            try {
               ThreadContext.exit(this.currentContext.threadContext);
            } catch (RuntimeException re) {
               if (errors == null) {
                  errors = new ArrayList();
               }

               errors.add(re);
               Logger.getInstance(LogCategory.OPENEJB, CUTask.class).warning(re.getMessage(), (Throwable)re);
            }
         }

         try {
            if (!this.associate) {
               CUTask.SECURITY_SERVICE.setState(this.currentContext.securityServiceState);
            } else {
               CUTask.SECURITY_SERVICE.disassociate();
            }
         } catch (RuntimeException re) {
            if (errors == null) {
               errors = new ArrayList();
            }

            errors.add(re);
            Logger.getInstance(LogCategory.OPENEJB, CUTask.class).warning(re.getMessage(), (Throwable)re);
         }

         Thread.currentThread().setContextClassLoader(this.currentContext.loader);
         if (this.currentContext.stack == null) {
            CURRENT.remove();
         } else {
            CURRENT.set(this.currentContext.stack);
         }

         this.currentContext = null;
         if (errors != null) {
            if (errors.size() == 1) {
               throw (RuntimeException)errors.iterator().next();
            } else {
               throw new OpenEJBRuntimeException(Join.join("\n", new Join.NameCallback<RuntimeException>() {
                  public String getName(RuntimeException object) {
                     return object.getMessage();
                  }
               }, errors));
            }
         }
      }

      public void pushExitTask(Runnable runnable) {
         if (this.exitTasks == null) {
            this.exitTasks = new ArrayList(2);
         }

         this.exitTasks.add(runnable);
      }
   }

   public interface ContainerListener<T> {
      T onCreation();

      T onStart(T var1);

      void onEnd(T var1);
   }
}
