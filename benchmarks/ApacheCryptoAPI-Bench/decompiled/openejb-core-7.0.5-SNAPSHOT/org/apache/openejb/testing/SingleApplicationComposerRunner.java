package org.apache.openejb.testing;

import java.lang.reflect.Field;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import javax.enterprise.context.spi.CreationalContext;
import org.apache.openejb.core.ThreadContext;
import org.apache.openejb.loader.JarLocation;
import org.apache.openejb.util.JavaSecurityManagers;
import org.apache.webbeans.config.WebBeansContext;
import org.apache.webbeans.inject.OWBInjector;
import org.apache.xbean.finder.AnnotationFinder;
import org.apache.xbean.finder.archive.FileArchive;
import org.junit.rules.MethodRule;
import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runner.notification.RunListener;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.InitializationError;
import org.junit.runners.model.Statement;

public class SingleApplicationComposerRunner extends BlockJUnit4ClassRunner {
   private static volatile boolean started = false;
   private static final AtomicReference<Object> APP = new AtomicReference();
   private static final AtomicReference<Thread> HOOK = new AtomicReference();

   public static void setApp(Object o) {
      APP.set(o);
   }

   public static void close() {
      Thread hook = (Thread)HOOK.get();
      if (hook != null) {
         hook.run();
         Runtime.getRuntime().removeShutdownHook(hook);
         HOOK.compareAndSet(hook, (Object)null);
         APP.set((Object)null);
      }

   }

   public SingleApplicationComposerRunner(Class<?> klass) throws InitializationError {
      super(klass);
   }

   protected List<MethodRule> rules(Object test) {
      List<MethodRule> rules = super.rules(test);
      rules.add(new MethodRule() {
         public Statement apply(final Statement base, FrameworkMethod method, final Object target) {
            return new Statement() {
               public void evaluate() throws Throwable {
                  SingleApplicationComposerRunner.start(SingleApplicationComposerRunner.this.getTestClass().getJavaClass());
                  SingleApplicationComposerRunner.composerInject(target);
                  base.evaluate();
               }
            };
         }
      });
      return rules;
   }

   private static void start(Class<?> marker) throws Exception {
      if (APP.get() == null) {
         String typeStr = JavaSecurityManagers.getSystemProperty("tomee.application-composer.application");
         Class<?> type;
         if (typeStr != null) {
            try {
               type = Thread.currentThread().getContextClassLoader().loadClass(typeStr);
            } catch (ClassNotFoundException e) {
               throw new IllegalArgumentException(e);
            }
         } else {
            if (marker == null) {
               throw new IllegalArgumentException("set tomee.application-composer.application system property or add a marker to the rule or runner");
            }

            Iterator<Class<?>> descriptors = (new AnnotationFinder(new FileArchive(Thread.currentThread().getContextClassLoader(), JarLocation.jarLocation(marker)), false)).findAnnotatedClasses(Application.class).iterator();
            if (!descriptors.hasNext()) {
               throw new IllegalArgumentException("No descriptor class using @Application");
            }

            type = (Class)descriptors.next();
            if (descriptors.hasNext()) {
               throw new IllegalArgumentException("Ambiguous @Application: " + type + ", " + descriptors.next());
            }
         }

         try {
            APP.compareAndSet((Object)null, type.newInstance());
         } catch (IllegalAccessException | InstantiationException e) {
            throw new IllegalStateException(e);
         }
      }

      if (!started) {
         Object app = APP.get();
         ApplicationComposers composers = new ApplicationComposers(app.getClass(), new Object[0]) {
            public void deployApp(Object inputTestInstance) throws Exception {
               super.deployApp(inputTestInstance);
               if (!SingleApplicationComposerRunner.started) {
                  final ThreadContext previous = ThreadContext.getThreadContext();
                  Thread hook = new Thread() {
                     public void run() {
                        try {
                           ((ApplicationComposers)<VAR_NAMELESS_ENCLOSURE>).after();
                        } catch (Exception e) {
                           ThreadContext.exit(previous);
                           throw new IllegalStateException(e);
                        }
                     }
                  };
                  SingleApplicationComposerRunner.HOOK.set(hook);
                  Runtime.getRuntime().addShutdownHook(hook);
                  SingleApplicationComposerRunner.started = true;
               }

            }
         };
         composers.before(app);
         composers.handleLifecycle(app.getClass(), app);
      }

   }

   private static void composerInject(Object target) throws IllegalAccessException {
      WebBeansContext wbc = null;

      try {
         wbc = WebBeansContext.currentInstance();
      } catch (IllegalStateException var13) {
      }

      if (wbc != null) {
         OWBInjector.inject(wbc.getBeanManagerImpl(), target, (CreationalContext)null);
      }

      Object app = APP.get();
      Class<?> aClass = target.getClass();

      for(Field f : aClass.getDeclaredFields()) {
         if (f.isAnnotationPresent(RandomPort.class)) {
            for(Field field : app.getClass().getDeclaredFields()) {
               if (field.getType() == f.getType()) {
                  if (!field.isAccessible()) {
                     field.setAccessible(true);
                  }

                  if (!f.isAccessible()) {
                     f.setAccessible(true);
                  }

                  Object value = field.get(app);
                  f.set(target, value);
                  break;
               }
            }
         } else if (f.isAnnotationPresent(Application.class)) {
            if (!f.isAccessible()) {
               f.setAccessible(true);
            }

            f.set(target, app);
         }
      }

      Class<?> superclass = aClass.getSuperclass();
      if (superclass != Object.class) {
         composerInject(superclass);
      }

   }

   public static class Rule implements TestRule {
      private final Object test;

      public Rule(Object test) {
         this.test = test;
      }

      public Statement apply(final Statement base, Description description) {
         return new Statement() {
            public void evaluate() throws Throwable {
               SingleApplicationComposerRunner.start(Rule.this.test.getClass());
               SingleApplicationComposerRunner.composerInject(Rule.this.test);
               base.evaluate();
            }
         };
      }
   }

   public static class Start extends RunListener {
      public void testStarted(Description description) throws Exception {
         SingleApplicationComposerRunner.start((Class)null);
      }
   }
}
