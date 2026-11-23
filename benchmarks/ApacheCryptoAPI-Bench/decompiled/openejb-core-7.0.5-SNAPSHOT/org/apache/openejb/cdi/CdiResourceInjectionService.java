package org.apache.openejb.cdi;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.enterprise.inject.spi.Bean;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import org.apache.openejb.AppContext;
import org.apache.openejb.BeanContext;
import org.apache.openejb.Injection;
import org.apache.openejb.InjectionProcessor;
import org.apache.openejb.core.WebContext;
import org.apache.openejb.core.ivm.naming.Reference;
import org.apache.openejb.util.LogCategory;
import org.apache.openejb.util.Logger;
import org.apache.openejb.util.PassthroughFactory;
import org.apache.webbeans.component.ResourceBean;
import org.apache.webbeans.config.WebBeansContext;
import org.apache.webbeans.spi.ResourceInjectionService;
import org.apache.webbeans.spi.api.ResourceReference;
import org.apache.xbean.recipe.ObjectRecipe;
import org.apache.xbean.recipe.Option;

public class CdiResourceInjectionService implements ResourceInjectionService {
   private final Logger logger;
   private final CdiPlugin ejbPlugin;
   private final List<BeanContext> compContexts;
   private volatile AppContext appCtx;
   private volatile boolean ear;

   public CdiResourceInjectionService(WebBeansContext context) {
      this.logger = Logger.getInstance(LogCategory.OPENEJB.createChild("cdi"), CdiResourceInjectionService.class);
      this.compContexts = new ArrayList();
      this.ejbPlugin = (CdiPlugin)CdiPlugin.class.cast(context.getPluginLoader().getEjbPlugin());
   }

   public void setAppContext(AppContext appModule, Collection<BeanContext> ejbs) {
      this.compContexts.clear();
      this.ear = false;

      for(BeanContext beanContext : appModule.getBeanContexts()) {
         if (!this.ear) {
            this.ear = beanContext.getEjbName().contains("ear-scoped-cdi-beans_");
         }

         if (beanContext.getBeanClass().equals(BeanContext.Comp.class) && (ejbs == null || this.ear || ejbs.contains(beanContext))) {
            this.compContexts.add(beanContext);
         }
      }

      this.appCtx = appModule;
   }

   public <X, T extends Annotation> X getResourceReference(ResourceReference<X, T> resourceReference) {
      Class<X> type = resourceReference.getResourceType();
      String name = resourceReference.getJndiName();

      try {
         return (X)type.cast((new InitialContext()).lookup(name));
      } catch (NamingException var11) {
         String noJavaPrefix = name.replace("java:", "");

         for(BeanContext beanContext : this.compContexts) {
            try {
               return (X)type.cast(beanContext.getJndiContext().lookup(noJavaPrefix));
            } catch (NamingException var10) {
            }
         }

         if (this.appCtx != null) {
            for(WebContext w : this.appCtx.getWebContexts()) {
               Object instance = w.getBindings().get(noJavaPrefix);
               if (instance != null) {
                  if (Reference.class.isInstance(instance)) {
                     try {
                        return (X)type.cast(((Reference)Reference.class.cast(instance)).getObject());
                     } catch (NamingException var9) {
                     }
                  }

                  if (type.isInstance(instance)) {
                     return (X)type.cast(instance);
                  }
               }
            }
         }

         return null;
      }
   }

   public void injectJavaEEResources(Object managedBeanInstance) {
      if (managedBeanInstance != null) {
         Class<?> managedBeanInstanceClass = managedBeanInstance.getClass();
         if (!this.ejbPlugin.isSessionBean(managedBeanInstanceClass)) {
            ObjectRecipe receipe = PassthroughFactory.recipe(managedBeanInstance);
            receipe.allow(Option.FIELD_INJECTION);
            receipe.allow(Option.PRIVATE_PROPERTIES);
            receipe.allow(Option.IGNORE_MISSING_PROPERTIES);
            receipe.allow(Option.NAMED_PARAMETERS);
            this.fillInjectionProperties(receipe, managedBeanInstance);
            receipe.create();
         }
      }
   }

   private void fillInjectionProperties(ObjectRecipe objectRecipe, Object managedBeanInstance) {
      boolean usePrefix = true;
      Class<?> clazz = managedBeanInstance.getClass();
      WebBeansContext webBeansContext = null;
      Collection<BeanContext> comps;
      if (this.ear) {
         try {
            webBeansContext = WebBeansContext.currentInstance();
            comps = ((CdiResourceInjectionService)CdiResourceInjectionService.class.cast(webBeansContext.getService(ResourceInjectionService.class))).compContexts;
         } catch (Exception var17) {
            comps = this.compContexts;
         }
      } else {
         comps = this.compContexts;
      }

      for(BeanContext beanContext : comps) {
         for(Injection injection : beanContext.getInjections()) {
            if (injection.getTarget() != null && injection.getTarget().isAssignableFrom(clazz)) {
               String prefix = injection.getTarget().getName() + "/";

               try {
                  Object value = this.lookup(beanContext, injection);
                  objectRecipe.setProperty(prefix + injection.getName(), value);
               } catch (NamingException var18) {
                  boolean found = false;
                  if (webBeansContext != null) {
                     for(WebContext w : this.appCtx.getWebContexts()) {
                        if (w.getWebBeansContext() == webBeansContext) {
                           Object value = w.getBindings().get(injection.getJndiName());
                           if (value != null) {
                              objectRecipe.setProperty(prefix + injection.getName(), value);
                              found = true;
                           }
                           break;
                        }
                     }
                  }

                  if (!found) {
                     this.logger.warning("Injection data not found in JNDI context: jndiName='" + injection.getJndiName() + "', target=" + injection.getTarget().getName() + "/" + injection.getName());
                  }
               }
            }
         }
      }

   }

   private Object lookup(BeanContext beanContext, Injection injection) throws NamingException {
      String jndiName = injection.getJndiName();

      try {
         return beanContext.getJndiContext().lookup(jndiName);
      } catch (NamingException e) {
         if (!jndiName.startsWith("java:")) {
            jndiName = "java:" + jndiName;
         }

         Object value;
         try {
            value = InjectionProcessor.unwrap(beanContext.getJndiContext()).lookup(jndiName);
         } catch (NamingException var9) {
            try {
               value = (new InitialContext()).lookup(jndiName);
            } catch (NamingException var8) {
               throw e;
            }
         }

         return value;
      }
   }

   public void clear() {
      this.compContexts.clear();
   }

   public <T> void writeExternal(Bean<T> bean, T actualResource, ObjectOutput out) throws IOException {
   }

   public <T> T readExternal(Bean<T> bean, ObjectInput out) throws IOException, ClassNotFoundException {
      return (T)((ResourceBean)bean).getActualInstance();
   }
}
