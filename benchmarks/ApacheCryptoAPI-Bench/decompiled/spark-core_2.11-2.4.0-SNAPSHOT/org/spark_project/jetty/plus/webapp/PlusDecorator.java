package org.spark_project.jetty.plus.webapp;

import org.spark_project.jetty.plus.annotation.InjectionCollection;
import org.spark_project.jetty.plus.annotation.LifeCycleCallbackCollection;
import org.spark_project.jetty.plus.annotation.RunAsCollection;
import org.spark_project.jetty.util.Decorator;
import org.spark_project.jetty.util.log.Log;
import org.spark_project.jetty.util.log.Logger;
import org.spark_project.jetty.webapp.WebAppContext;

public class PlusDecorator implements Decorator {
   private static final Logger LOG = Log.getLogger(PlusDecorator.class);
   protected WebAppContext _context;

   public PlusDecorator(WebAppContext context) {
      this._context = context;
   }

   public Object decorate(Object o) {
      RunAsCollection runAses = (RunAsCollection)this._context.getAttribute("org.spark_project.jetty.runAsCollection");
      if (runAses != null) {
         runAses.setRunAs(o);
      }

      InjectionCollection injections = (InjectionCollection)this._context.getAttribute("org.spark_project.jetty.injectionCollection");
      if (injections != null) {
         injections.inject(o);
      }

      LifeCycleCallbackCollection callbacks = (LifeCycleCallbackCollection)this._context.getAttribute("org.spark_project.jetty.lifecyleCallbackCollection");
      if (callbacks != null) {
         try {
            callbacks.callPostConstructCallback(o);
         } catch (Exception e) {
            throw new RuntimeException(e);
         }
      }

      return o;
   }

   public void destroy(Object o) {
      LifeCycleCallbackCollection callbacks = (LifeCycleCallbackCollection)this._context.getAttribute("org.spark_project.jetty.lifecyleCallbackCollection");
      if (callbacks != null) {
         try {
            callbacks.callPreDestroyCallback(o);
         } catch (Exception e) {
            LOG.warn("Destroying instance of " + o.getClass(), (Throwable)e);
         }
      }

   }
}
