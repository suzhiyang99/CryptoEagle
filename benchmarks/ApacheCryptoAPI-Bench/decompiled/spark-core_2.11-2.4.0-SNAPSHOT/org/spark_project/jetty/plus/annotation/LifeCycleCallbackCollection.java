package org.spark_project.jetty.plus.annotation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.spark_project.jetty.util.log.Log;
import org.spark_project.jetty.util.log.Logger;

public class LifeCycleCallbackCollection {
   private static final Logger LOG = Log.getLogger(LifeCycleCallbackCollection.class);
   public static final String LIFECYCLE_CALLBACK_COLLECTION = "org.spark_project.jetty.lifecyleCallbackCollection";
   private HashMap<String, List<LifeCycleCallback>> postConstructCallbacksMap = new HashMap();
   private HashMap<String, List<LifeCycleCallback>> preDestroyCallbacksMap = new HashMap();

   public void add(LifeCycleCallback callback) {
      if (callback != null && callback.getTargetClassName() != null) {
         if (LOG.isDebugEnabled()) {
            LOG.debug("Adding callback for class=" + callback.getTargetClass() + " on " + callback.getTarget());
         }

         Map<String, List<LifeCycleCallback>> map = null;
         if (callback instanceof PreDestroyCallback) {
            map = this.preDestroyCallbacksMap;
         }

         if (callback instanceof PostConstructCallback) {
            map = this.postConstructCallbacksMap;
         }

         if (map == null) {
            throw new IllegalArgumentException("Unsupported lifecycle callback type: " + callback);
         } else {
            List<LifeCycleCallback> callbacks = (List)map.get(callback.getTargetClassName());
            if (callbacks == null) {
               callbacks = new ArrayList();
               map.put(callback.getTargetClassName(), callbacks);
            }

            if (!callbacks.contains(callback)) {
               callbacks.add(callback);
            }

         }
      }
   }

   public List<LifeCycleCallback> getPreDestroyCallbacks(Object o) {
      if (o == null) {
         return null;
      } else {
         Class<? extends Object> clazz = o.getClass();
         return (List)this.preDestroyCallbacksMap.get(clazz.getName());
      }
   }

   public List<LifeCycleCallback> getPostConstructCallbacks(Object o) {
      if (o == null) {
         return null;
      } else {
         Class<? extends Object> clazz = o.getClass();
         return (List)this.postConstructCallbacksMap.get(clazz.getName());
      }
   }

   public void callPostConstructCallback(Object o) throws Exception {
      if (o != null) {
         Class<? extends Object> clazz = o.getClass();
         List<LifeCycleCallback> callbacks = (List)this.postConstructCallbacksMap.get(clazz.getName());
         if (callbacks != null) {
            for(int i = 0; i < callbacks.size(); ++i) {
               ((LifeCycleCallback)callbacks.get(i)).callback(o);
            }

         }
      }
   }

   public void callPreDestroyCallback(Object o) throws Exception {
      if (o != null) {
         Class<? extends Object> clazz = o.getClass();
         List<LifeCycleCallback> callbacks = (List)this.preDestroyCallbacksMap.get(clazz.getName());
         if (callbacks != null) {
            for(int i = 0; i < callbacks.size(); ++i) {
               ((LifeCycleCallback)callbacks.get(i)).callback(o);
            }

         }
      }
   }

   public Map<String, List<LifeCycleCallback>> getPostConstructCallbackMap() {
      return Collections.unmodifiableMap(this.postConstructCallbacksMap);
   }

   public Map<String, List<LifeCycleCallback>> getPreDestroyCallbackMap() {
      return Collections.unmodifiableMap(this.preDestroyCallbacksMap);
   }

   public Collection<LifeCycleCallback> getPostConstructCallbacks() {
      List<LifeCycleCallback> list = new ArrayList();

      for(String s : this.postConstructCallbacksMap.keySet()) {
         list.addAll((Collection)this.postConstructCallbacksMap.get(s));
      }

      return Collections.unmodifiableCollection(list);
   }

   public Collection<LifeCycleCallback> getPreDestroyCallbacks() {
      List<LifeCycleCallback> list = new ArrayList();

      for(String s : this.preDestroyCallbacksMap.keySet()) {
         list.addAll((Collection)this.preDestroyCallbacksMap.get(s));
      }

      return Collections.unmodifiableCollection(list);
   }
}
