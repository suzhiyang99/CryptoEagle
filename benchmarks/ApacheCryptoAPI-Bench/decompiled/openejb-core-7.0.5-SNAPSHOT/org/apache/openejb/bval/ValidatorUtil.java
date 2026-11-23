package org.apache.openejb.bval;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.Collection;
import javax.naming.InitialContext;
import javax.naming.NameNotFoundException;
import javax.naming.NamingException;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import org.apache.openejb.AppContext;
import org.apache.openejb.BeanContext;
import org.apache.openejb.core.ThreadContext;
import org.apache.openejb.core.WebContext;
import org.apache.openejb.loader.SystemInstance;
import org.apache.openejb.spi.ContainerSystem;

public final class ValidatorUtil {
   private ValidatorUtil() {
   }

   public static ValidatorFactory validatorFactory() {
      try {
         return lookupFactory();
      } catch (NamingException var1) {
         return tryJndiLaterFactory();
      }
   }

   public static ValidatorFactory lookupFactory() throws NamingException {
      return (ValidatorFactory)(new InitialContext()).lookup("java:comp/ValidatorFactory");
   }

   public static ValidatorFactory tryJndiLaterFactory() {
      return (ValidatorFactory)proxy(ValidatorFactory.class, "java:comp/ValidatorFactory");
   }

   public static Validator validator() {
      try {
         return (Validator)(new InitialContext()).lookup("java:comp/Validator");
      } catch (NamingException var1) {
         return (Validator)proxy(Validator.class, "java:comp/Validator");
      }
   }

   private static <T> T proxy(final Class<T> t, final String jndi) {
      return (T)t.cast(Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(), new Class[]{t}, new InvocationHandler() {
         public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            if (Object.class.equals(method.getDeclaringClass())) {
               return method.invoke(this);
            } else {
               ThreadContext ctx = ThreadContext.getThreadContext();
               if (ctx != null) {
                  return method.invoke(ctx.getBeanContext().getJndiContext().lookup(jndi), args);
               } else {
                  ClassLoader tccl = Thread.currentThread().getContextClassLoader();
                  if (tccl == null) {
                     return null;
                  } else {
                     ContainerSystem containerSystem = (ContainerSystem)SystemInstance.get().getComponent(ContainerSystem.class);
                     Object value = null;

                     for(AppContext appContext : containerSystem.getAppContexts()) {
                        ClassLoader appContextClassLoader = appContext.getClassLoader();
                        if (tccl.equals(appContextClassLoader) || appContextClassLoader.equals(tccl)) {
                           Collection<String> tested = new ArrayList();

                           for(BeanContext bean : appContext.getBeanContexts()) {
                              if (BeanContext.Comp.class.equals(bean.getBeanClass())) {
                                 String uniqueId = bean.getModuleContext().getUniqueId();
                                 if (!tested.contains(uniqueId)) {
                                    tested.add(uniqueId);

                                    try {
                                       value = containerSystem.getJNDIContext().lookup((jndi.endsWith("Factory") ? "openejb/ValidatorFactory/" : "openejb/Validator/") + uniqueId);
                                       break;
                                    } catch (NameNotFoundException var16) {
                                    }
                                 }
                              }
                           }

                           if (ClassLoader.getSystemClassLoader() != appContextClassLoader) {
                              break;
                           }
                        }

                        for(WebContext web : appContext.getWebContexts()) {
                           ClassLoader webClassLoader = web.getClassLoader();
                           if (webClassLoader.equals(tccl) || tccl.equals(webClassLoader)) {
                              value = web.getJndiEnc().lookup(jndi);
                              break;
                           }
                        }

                        if (value != null) {
                           break;
                        }
                     }

                     return value != null ? method.invoke(value, args) : null;
                  }
               }
            }
         }

         public String toString() {
            return "Proxy::" + t.getName();
         }
      }));
   }
}
