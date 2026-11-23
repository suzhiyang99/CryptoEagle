package org.spark_project.jetty.plus.webapp;

import java.util.Random;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NameNotFoundException;
import org.spark_project.jetty.plus.jndi.Transaction;
import org.spark_project.jetty.util.log.Log;
import org.spark_project.jetty.util.log.Logger;
import org.spark_project.jetty.webapp.AbstractConfiguration;
import org.spark_project.jetty.webapp.WebAppContext;

public class PlusConfiguration extends AbstractConfiguration {
   private static final Logger LOG = Log.getLogger(PlusConfiguration.class);
   private Integer _key;

   public void preConfigure(WebAppContext context) throws Exception {
      context.getObjectFactory().addDecorator(new PlusDecorator(context));
   }

   public void cloneConfigure(WebAppContext template, WebAppContext context) throws Exception {
      context.getObjectFactory().addDecorator(new PlusDecorator(context));
   }

   public void configure(WebAppContext context) throws Exception {
      this.bindUserTransaction(context);
      context.getMetaData().addDescriptorProcessor(new PlusDescriptorProcessor());
   }

   public void postConfigure(WebAppContext context) throws Exception {
      this.lockCompEnv(context);
   }

   public void deconfigure(WebAppContext context) throws Exception {
      this.unlockCompEnv(context);
      this._key = null;
      context.setAttribute("org.spark_project.jetty.injectionCollection", (Object)null);
      context.setAttribute("org.spark_project.jetty.lifecyleCallbackCollection", (Object)null);
   }

   public void bindUserTransaction(WebAppContext context) throws Exception {
      try {
         Transaction.bindToENC();
      } catch (NameNotFoundException var3) {
         LOG.debug("No Transaction manager found - if your webapp requires one, please configure one.");
      }

   }

   protected void lockCompEnv(WebAppContext wac) throws Exception {
      ClassLoader old_loader = Thread.currentThread().getContextClassLoader();
      Thread.currentThread().setContextClassLoader(wac.getClassLoader());

      try {
         Random random = new Random();
         this._key = new Integer(random.nextInt());
         Context context = new InitialContext();
         Context compCtx = (Context)context.lookup("java:comp");
         compCtx.addToEnvironment("org.spark_project.jetty.jndi.lock", this._key);
      } finally {
         Thread.currentThread().setContextClassLoader(old_loader);
      }

   }

   protected void unlockCompEnv(WebAppContext wac) throws Exception {
      if (this._key != null) {
         ClassLoader old_loader = Thread.currentThread().getContextClassLoader();
         Thread.currentThread().setContextClassLoader(wac.getClassLoader());

         try {
            Context context = new InitialContext();
            Context compCtx = (Context)context.lookup("java:comp");
            compCtx.addToEnvironment("org.spark_project.jetty.jndi.unlock", this._key);
         } finally {
            Thread.currentThread().setContextClassLoader(old_loader);
         }
      }

   }
}
