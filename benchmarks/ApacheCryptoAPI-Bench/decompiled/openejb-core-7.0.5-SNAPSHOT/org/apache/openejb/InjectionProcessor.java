package org.apache.openejb;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.naming.Context;
import javax.naming.NamingException;
import org.apache.openejb.core.ivm.naming.JndiUrlReference;
import org.apache.openejb.injection.FallbackPropertyInjector;
import org.apache.openejb.loader.SystemInstance;
import org.apache.openejb.spi.ContainerSystem;
import org.apache.openejb.util.LogCategory;
import org.apache.openejb.util.Logger;
import org.apache.openejb.util.PassthroughFactory;
import org.apache.xbean.naming.reference.SimpleReference;
import org.apache.xbean.recipe.ObjectRecipe;
import org.apache.xbean.recipe.Option;

public class InjectionProcessor<T> {
   private static final Logger logger;
   private final Class<? extends T> beanClass;
   private final Collection<Injection> injections;
   private final Map<String, Object> properties;
   private final List<Method> postConstructMethods;
   private final List<Method> preDestroyMethods;
   private final Context context;
   private T instance;
   private T suppliedInstance;
   private final Map<String, Object> bindings;

   public InjectionProcessor(T suppliedInstance, Collection<Injection> injections, Context context) {
      this.properties = new LinkedHashMap();
      this.bindings = new HashMap();
      this.beanClass = null;
      this.suppliedInstance = suppliedInstance;
      this.injections = injections;
      this.context = context;
      this.postConstructMethods = null;
      this.preDestroyMethods = null;
   }

   public InjectionProcessor(Class<? extends T> beanClass, Collection<Injection> injections, Context context) {
      this.properties = new LinkedHashMap();
      this.bindings = new HashMap();
      this.beanClass = beanClass;
      this.injections = injections;
      this.context = context;
      this.postConstructMethods = null;
      this.preDestroyMethods = null;
   }

   public InjectionProcessor(Class<? extends T> beanClass, Collection<Injection> injections, List<Method> postConstructMethods, List<Method> preDestroyMethods, Context context) {
      this.properties = new LinkedHashMap();
      this.bindings = new HashMap();
      this.beanClass = beanClass;
      this.injections = injections;
      this.postConstructMethods = postConstructMethods;
      this.preDestroyMethods = preDestroyMethods;
      this.context = context;
   }

   public InjectionProcessor(Class<? extends T> beanClass, Collection<Injection> injections, List<Method> postConstructMethods, List<Method> preDestroyMethods, Context context, Map<String, Object> bindings) {
      this(beanClass, injections, postConstructMethods, preDestroyMethods, context);
      this.bindings.putAll(bindings);
   }

   public void setProperty(String name, Object value) {
      this.properties.put(name, value);
   }

   public T createInstance() throws OpenEJBException {
      if (this.instance == null) {
         this.construct();
      }

      return this.instance;
   }

   public T getInstance() {
      return this.instance;
   }

   private void construct() throws OpenEJBException {
      if (this.instance != null) {
         throw new IllegalStateException("Instance already constructed");
      } else {
         Class<? extends T> clazz = this.beanClass;
         ObjectRecipe objectRecipe;
         if (this.suppliedInstance != null) {
            clazz = this.suppliedInstance.getClass();
            objectRecipe = PassthroughFactory.recipe(this.suppliedInstance);
         } else {
            objectRecipe = new ObjectRecipe(clazz);
         }

         objectRecipe.allow(Option.FIELD_INJECTION);
         objectRecipe.allow(Option.PRIVATE_PROPERTIES);
         objectRecipe.allow(Option.IGNORE_MISSING_PROPERTIES);
         objectRecipe.allow(Option.NAMED_PARAMETERS);
         this.fillInjectionProperties(objectRecipe);
         this.bindings.clear();

         for(Map.Entry<String, Object> entry : this.properties.entrySet()) {
            objectRecipe.setProperty((String)entry.getKey(), entry.getValue());
         }

         Object object;
         try {
            object = objectRecipe.create(clazz.getClassLoader());
         } catch (Exception e) {
            throw new OpenEJBException("Error while creating bean " + clazz.getName(), e);
         }

         Map unsetProperties = objectRecipe.getUnsetProperties();
         if (unsetProperties.size() > 0) {
            for(Object property : unsetProperties.keySet()) {
               logger.warning("Injection: No such property '" + property + "' in class " + clazz.getName());
            }
         }

         this.instance = (T)clazz.cast(object);
      }
   }

   public void postConstruct() throws OpenEJBException {
      if (this.instance == null) {
         throw new IllegalStateException("Instance has not been constructed");
      } else if (this.postConstructMethods != null) {
         for(Method postConstruct : this.postConstructMethods) {
            try {
               postConstruct.invoke(this.instance);
            } catch (Exception var4) {
               Exception e = var4;
               if (var4 instanceof InvocationTargetException && var4.getCause() instanceof Exception) {
                  e = (Exception)var4.getCause();
               }

               throw new OpenEJBException("Error while calling post construct method", e);
            }
         }

      }
   }

   public void preDestroy() {
      if (this.instance != null) {
         if (this.preDestroyMethods != null) {
            for(Method preDestroy : this.preDestroyMethods) {
               try {
                  preDestroy.invoke(this.instance);
               } catch (Exception var4) {
                  Exception e = var4;
                  if (var4 instanceof InvocationTargetException && var4.getCause() instanceof Exception) {
                     e = (Exception)var4.getCause();
                  }

                  logger.error("Error while calling pre destroy method", (Throwable)e);
               }
            }

         }
      }
   }

   private void fillInjectionProperties(ObjectRecipe objectRecipe) {
      if (this.injections != null) {
         boolean usePrefix = true;

         try {
            if (this.beanClass != null) {
               this.beanClass.getConstructor();
            }
         } catch (NoSuchMethodException var12) {
            usePrefix = false;
         }

         Class clazz = this.beanClass;
         if (this.suppliedInstance != null) {
            clazz = this.suppliedInstance.getClass();
         }

         if (this.context != null) {
            for(Injection injection : this.injections) {
               if (injection.getTarget() != null && injection.getTarget().isAssignableFrom(clazz)) {
                  String jndiName = injection.getJndiName();

                  Object value;
                  try {
                     value = this.context.lookup(jndiName);
                  } catch (NamingException var14) {
                     value = this.bindings.get(jndiName);
                     if (value instanceof SimpleReference) {
                        try {
                           value = ((SimpleReference)value).getContent();
                        } catch (NamingException var13) {
                           if (value instanceof JndiUrlReference) {
                              try {
                                 value = ((ContainerSystem)SystemInstance.get().getComponent(ContainerSystem.class)).getJNDIContext().lookup(((JndiUrlReference)value).getJndiName());
                              } catch (NamingException var11) {
                                 value = null;
                              }
                           }
                        }
                     }
                  }

                  if (value == null) {
                     FallbackPropertyInjector fallback = (FallbackPropertyInjector)SystemInstance.get().getComponent(FallbackPropertyInjector.class);
                     if (fallback != null) {
                        value = fallback.getValue(injection);
                     }
                  }

                  if (value != null) {
                     String prefix;
                     if (usePrefix) {
                        prefix = injection.getTarget().getName() + "/";
                     } else {
                        prefix = "";
                     }

                     objectRecipe.setProperty(prefix + injection.getName(), value);
                  } else {
                     logger.warning("Injection data not found in JNDI context: jndiName='" + injection.getJndiName() + "', target=" + injection.getTarget().getName() + "/" + injection.getName());
                  }
               }
            }
         }

      }
   }

   public static Context unwrap(Context context) {
      return context;
   }

   static {
      logger = Logger.getInstance(LogCategory.OPENEJB, InjectionProcessor.class);
   }
}
