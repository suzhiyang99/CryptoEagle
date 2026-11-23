package org.apache.openejb.util;

import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.script.Bindings;
import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineFactory;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import javax.script.SimpleScriptContext;
import org.apache.openejb.AppContext;
import org.apache.openejb.OpenEJBRuntimeException;
import org.apache.openejb.loader.SystemInstance;
import org.apache.openejb.spi.ContainerSystem;

public class OpenEJBScripter {
   private static final Map<String, ScriptEngineFactory> ENGINE_FACTORIES = new ConcurrentHashMap();
   private static final ThreadLocal<Map<String, ScriptEngine>> ENGINES = new ThreadLocal<Map<String, ScriptEngine>>() {
      protected Map<String, ScriptEngine> initialValue() {
         return new HashMap();
      }
   };

   public static Set<String> getSupportedLanguages() {
      return ENGINE_FACTORIES.keySet();
   }

   public Object evaluate(String language, String script) throws ScriptException {
      return this.evaluate(language, script, (ScriptContext)null);
   }

   public Object evaluate(String language, String script, ScriptContext context) throws ScriptException {
      if (!ENGINE_FACTORIES.containsKey(language)) {
         throw new IllegalArgumentException("can't find factory for language " + language + ". You probably need to add the jar to openejb libs.");
      } else {
         ScriptContext executionContext = context;
         if (context == null) {
            executionContext = new SimpleScriptContext();
         }

         bindLocal(executionContext);
         ScriptEngine engine = engine(language);
         return engine.eval(script, executionContext);
      }
   }

   private static ScriptEngine engine(String language) {
      ScriptEngine engine = (ScriptEngine)((Map)ENGINES.get()).get(language);
      if (engine == null) {
         ScriptEngineFactory factory = (ScriptEngineFactory)ENGINE_FACTORIES.get(language);
         engine = factory.getScriptEngine();
         ((Map)ENGINES.get()).put(language, engine);
      }

      return engine;
   }

   public static void clearEngines() {
      ((Map)ENGINES.get()).clear();
   }

   private static void bindLocal(ScriptContext context) {
      Bindings bindings = context.getBindings(100);
      bindings.put("bm", new BeanManagerHelper());
   }

   static {
      ScriptEngineManager mgr = new ScriptEngineManager();

      for(ScriptEngineFactory factory : mgr.getEngineFactories()) {
         if (factory.getParameter("THREADING") != null) {
            for(String ext : factory.getExtensions()) {
               ENGINE_FACTORIES.put(ext, factory);
            }
         }
      }

   }

   public static class BeanManagerHelper {
      public Object beanFromClass(String appName, String classname) {
         AppContext appContext = this.appContext(appName);
         BeanManager bm = appContext.getBeanManager();

         Class<?> clazz;
         try {
            clazz = appContext.getClassLoader().loadClass(classname);
         } catch (ClassNotFoundException e) {
            throw new OpenEJBRuntimeException(e);
         }

         Set<Bean<?>> beans = bm.getBeans(clazz, new Annotation[0]);
         return this.instance(bm, beans, clazz);
      }

      public Object beanFromName(String appName, String name) {
         BeanManager bm = this.beanManager(appName);
         Set<Bean<?>> beans = bm.getBeans(name);
         return this.instance(bm, beans, Object.class);
      }

      private <T> T instance(BeanManager bm, Set<Bean<?>> beans, Class<T> clazz) {
         Bean<?> bean = bm.resolve(beans);
         return (T)bm.getReference(bean, clazz, bm.createCreationalContext(bean));
      }

      private BeanManager beanManager(String appName) {
         return this.appContext(appName).getBeanManager();
      }

      private AppContext appContext(String appName) {
         ContainerSystem cs = (ContainerSystem)SystemInstance.get().getComponent(ContainerSystem.class);
         AppContext appContext = cs.getAppContext(appName);
         if (appContext == null) {
            throw new OpenEJBRuntimeException("can't find application " + appName);
         } else {
            return appContext;
         }
      }
   }
}
