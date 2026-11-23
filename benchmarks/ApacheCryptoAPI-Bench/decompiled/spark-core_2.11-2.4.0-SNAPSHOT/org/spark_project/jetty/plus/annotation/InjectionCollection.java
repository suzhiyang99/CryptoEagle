package org.spark_project.jetty.plus.annotation;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import org.spark_project.jetty.util.log.Log;
import org.spark_project.jetty.util.log.Logger;

public class InjectionCollection {
   private static final Logger LOG = Log.getLogger(InjectionCollection.class);
   public static final String INJECTION_COLLECTION = "org.spark_project.jetty.injectionCollection";
   private HashMap<String, List<Injection>> _injectionMap = new HashMap();

   public void add(Injection injection) {
      if (injection != null && injection.getTargetClass() != null) {
         if (LOG.isDebugEnabled()) {
            LOG.debug("Adding injection for class=" + injection.getTargetClass() + " on a " + injection.getTarget().getName());
         }

         List<Injection> injections = (List)this._injectionMap.get(injection.getTargetClass().getCanonicalName());
         if (injections == null) {
            injections = new ArrayList();
            this._injectionMap.put(injection.getTargetClass().getCanonicalName(), injections);
         }

         injections.add(injection);
      }
   }

   public List<Injection> getInjections(String className) {
      return className == null ? null : (List)this._injectionMap.get(className);
   }

   public Injection getInjection(String jndiName, Class<?> clazz, Field field) {
      if (field != null && clazz != null) {
         List<Injection> injections = this.getInjections(clazz.getCanonicalName());
         if (injections == null) {
            return null;
         } else {
            Iterator<Injection> itor = injections.iterator();
            Injection injection = null;

            while(itor.hasNext() && injection == null) {
               Injection i = (Injection)itor.next();
               if (i.isField() && field.getName().equals(i.getTarget().getName())) {
                  injection = i;
               }
            }

            return injection;
         }
      } else {
         return null;
      }
   }

   public Injection getInjection(String jndiName, Class<?> clazz, Method method, Class<?> paramClass) {
      if (clazz != null && method != null && paramClass != null) {
         List<Injection> injections = this.getInjections(clazz.getCanonicalName());
         if (injections == null) {
            return null;
         } else {
            Iterator<Injection> itor = injections.iterator();
            Injection injection = null;

            while(itor.hasNext() && injection == null) {
               Injection i = (Injection)itor.next();
               if (i.isMethod() && i.getTarget().getName().equals(method.getName()) && paramClass.equals(i.getParamClass())) {
                  injection = i;
               }
            }

            return injection;
         }
      } else {
         return null;
      }
   }

   public void inject(Object injectable) {
      if (injectable != null) {
         for(Class<?> clazz = injectable.getClass(); clazz != null; clazz = clazz.getSuperclass()) {
            List<Injection> injections = (List)this._injectionMap.get(clazz.getCanonicalName());
            if (injections != null) {
               for(Injection i : injections) {
                  i.inject(injectable);
               }
            }
         }

      }
   }
}
