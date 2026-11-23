package org.apache.openejb.assembler.classic.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import javax.naming.Context;
import javax.naming.NamingException;
import org.apache.openejb.OpenEJBException;
import org.apache.openejb.OpenEJBRuntimeException;
import org.apache.openejb.assembler.classic.Assembler;
import org.apache.openejb.assembler.classic.OpenEjbConfiguration;
import org.apache.openejb.assembler.classic.ServiceInfo;
import org.apache.openejb.loader.SystemInstance;
import org.apache.openejb.spi.ContainerSystem;
import org.apache.openejb.util.LogCategory;
import org.apache.openejb.util.Logger;
import org.apache.xbean.recipe.ObjectRecipe;
import org.apache.xbean.recipe.Option;
import org.apache.xbean.recipe.UnsetPropertiesRecipe;

public final class ServiceInfos {
   private ServiceInfos() {
   }

   public static Object resolve(Collection<ServiceInfo> services, String id) {
      if (id == null) {
         return null;
      } else {
         try {
            return build(services, find(services, id));
         } catch (OpenEJBException e) {
            throw new OpenEJBRuntimeException(e);
         }
      }
   }

   public static ServiceInfo find(Collection<ServiceInfo> services, String id) {
      for(ServiceInfo s : services) {
         if (id.equals(s.id)) {
            return s;
         }
      }

      return null;
   }

   public static ServiceInfo findByClass(Collection<ServiceInfo> services, String clazz) {
      for(ServiceInfo s : services) {
         if (clazz.equals(s.className)) {
            return s;
         }
      }

      return null;
   }

   public static List<Object> resolve(Collection<ServiceInfo> serviceInfos, String[] ids) {
      return resolve(serviceInfos, ids, (Factory)null);
   }

   public static List<Object> resolve(Collection<ServiceInfo> serviceInfos, String[] ids, Factory factory) {
      if (ids != null && ids.length != 0) {
         List<Object> instances = new ArrayList();

         for(String id : ids) {
            Object instance = resolve(serviceInfos, id);
            if (instance == null) {
               try {
                  Class<?> aClass = Thread.currentThread().getContextClassLoader().loadClass(id);
                  if (factory == null) {
                     instance = aClass.newInstance();
                  } else {
                     instance = factory.newInstance(aClass);
                  }
               } catch (Exception var10) {
               }
            }

            if (instance == null) {
               instance = resolve(((OpenEjbConfiguration)SystemInstance.get().getComponent(OpenEjbConfiguration.class)).facilities.services, (String)id);
            }

            if (instance != null) {
               instances.add(instance);
            }
         }

         return instances;
      } else {
         return null;
      }
   }

   public static Properties serviceProperties(Collection<ServiceInfo> serviceInfos, String id) {
      if (id == null) {
         return null;
      } else {
         ServiceInfo info = find(serviceInfos, id);
         return info == null ? null : info.properties;
      }
   }

   private static Object build(Collection<ServiceInfo> services, ServiceInfo info) throws OpenEJBException {
      if (info == null) {
         return null;
      } else {
         ObjectRecipe serviceRecipe = Assembler.prepareRecipe(info);
         return build(services, info, serviceRecipe);
      }
   }

   public static Object build(Collection<ServiceInfo> services, ServiceInfo info, ObjectRecipe serviceRecipe) {
      if ("org.apache.openejb.config.sys.MapFactory".equals(info.className)) {
         return info.properties;
      } else {
         if (!info.properties.containsKey("properties")) {
            info.properties.put("properties", new UnsetPropertiesRecipe());
         }

         serviceRecipe.allow(Option.FIELD_INJECTION);
         serviceRecipe.allow(Option.PRIVATE_PROPERTIES);
         setProperties(services, info, serviceRecipe);
         Object service = serviceRecipe.create();
         SystemInstance.get().addObserver(service);
         Assembler.logUnusedProperties(serviceRecipe, info);
         return service;
      }
   }

   public static void setProperties(Collection<ServiceInfo> services, ServiceInfo info, ObjectRecipe serviceRecipe) {
      for(Map.Entry<Object, Object> entry : info.properties.entrySet()) {
         String key = entry.getKey().toString();
         Object value = entry.getValue();
         if (value instanceof String) {
            String valueStr = value.toString();
            if (valueStr.startsWith("collection:")) {
               valueStr = valueStr.substring("collection:".length());
               String[] elt = valueStr.split(" *, *");
               List<Object> val = new ArrayList(elt.length);

               for(String e : elt) {
                  if (!e.trim().isEmpty()) {
                     val.add(e.startsWith("@") ? lookup(e) : resolve(services, e.startsWith("$") ? e.substring(1) : e));
                  }
               }

               serviceRecipe.setProperty(key, val);
            } else if (valueStr.startsWith("$")) {
               serviceRecipe.setProperty(key, resolve(services, valueStr.substring(1)));
            } else if (valueStr.startsWith("@")) {
               serviceRecipe.setProperty(key, lookup(value));
            } else {
               serviceRecipe.setProperty(key, value);
            }
         } else {
            serviceRecipe.setProperty(key, entry.getValue());
         }
      }

   }

   private static Object lookup(Object value) {
      String name = String.valueOf(value).substring(1);
      Context jndiContext = ((ContainerSystem)SystemInstance.get().getComponent(ContainerSystem.class)).getJNDIContext();

      Object lookup;
      try {
         lookup = jndiContext.lookup("openejb/Resource/" + name);
      } catch (NamingException var7) {
         try {
            lookup = jndiContext.lookup(name);
         } catch (NamingException var6) {
            Logger.getInstance(LogCategory.OPENEJB, ServiceInfos.class).warning("Value " + name + " starting with @ but doesn't point to an existing resource, using raw value");
            lookup = value;
         }
      }

      return lookup;
   }

   public interface Factory {
      Object newInstance(Class<?> var1) throws Exception;
   }
}
