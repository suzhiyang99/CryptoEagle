package org.spark_project.jetty.plus.webapp;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.naming.Binding;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.Name;
import javax.naming.NameNotFoundException;
import javax.naming.NameParser;
import javax.naming.NamingException;
import org.spark_project.jetty.jndi.ContextFactory;
import org.spark_project.jetty.jndi.NamingContext;
import org.spark_project.jetty.jndi.NamingUtil;
import org.spark_project.jetty.jndi.local.localContextRoot;
import org.spark_project.jetty.plus.jndi.EnvEntry;
import org.spark_project.jetty.plus.jndi.NamingEntry;
import org.spark_project.jetty.plus.jndi.NamingEntryUtil;
import org.spark_project.jetty.util.log.Log;
import org.spark_project.jetty.util.log.Logger;
import org.spark_project.jetty.util.resource.Resource;
import org.spark_project.jetty.webapp.AbstractConfiguration;
import org.spark_project.jetty.webapp.WebAppContext;
import org.spark_project.jetty.xml.XmlConfiguration;

public class EnvConfiguration extends AbstractConfiguration {
   private static final Logger LOG = Log.getLogger(EnvConfiguration.class);
   private static final String JETTY_ENV_BINDINGS = "org.spark_project.jetty.jndi.EnvConfiguration";
   private URL jettyEnvXmlUrl;

   public void setJettyEnvXml(URL url) {
      this.jettyEnvXmlUrl = url;
   }

   public void preConfigure(WebAppContext context) throws Exception {
      this.createEnvContext(context);
   }

   public void configure(WebAppContext context) throws Exception {
      if (LOG.isDebugEnabled()) {
         LOG.debug("Created java:comp/env for webapp " + context.getContextPath());
      }

      if (this.jettyEnvXmlUrl == null) {
         Resource web_inf = context.getWebInf();
         if (web_inf != null && web_inf.isDirectory()) {
            Resource jettyEnv = web_inf.addPath("jetty-env.xml");
            if (jettyEnv.exists()) {
               this.jettyEnvXmlUrl = jettyEnv.getURL();
            }
         }
      }

      if (this.jettyEnvXmlUrl != null) {
         synchronized(localContextRoot.getRoot()) {
            final List<Bound> bindings = new ArrayList();
            NamingContext.Listener listener = new NamingContext.Listener() {
               public void unbind(NamingContext ctx, Binding binding) {
               }

               public Binding bind(NamingContext ctx, Binding binding) {
                  bindings.add(new Bound(ctx, binding.getName()));
                  return binding;
               }
            };

            try {
               localContextRoot.getRoot().addListener(listener);
               XmlConfiguration configuration = new XmlConfiguration(this.jettyEnvXmlUrl);
               configuration.configure(context);
            } finally {
               localContextRoot.getRoot().removeListener(listener);
               context.setAttribute("org.spark_project.jetty.jndi.EnvConfiguration", bindings);
            }
         }
      }

      this.bindEnvEntries(context);
   }

   public void deconfigure(WebAppContext context) throws Exception {
      ClassLoader oldLoader = Thread.currentThread().getContextClassLoader();
      Thread.currentThread().setContextClassLoader(context.getClassLoader());
      ContextFactory.associateClassLoader(context.getClassLoader());

      try {
         Context ic = new InitialContext();
         Context compCtx = (Context)ic.lookup("java:comp");
         compCtx.destroySubcontext("env");
         List<Bound> bindings = (List)context.getAttribute("org.spark_project.jetty.jndi.EnvConfiguration");
         context.setAttribute("org.spark_project.jetty.jndi.EnvConfiguration", (Object)null);
         if (bindings != null) {
            Collections.reverse(bindings);

            for(Bound b : bindings) {
               b._context.destroySubcontext(b._name);
            }
         }
      } catch (NameNotFoundException e) {
         LOG.warn(e);
      } finally {
         ContextFactory.disassociateClassLoader();
         Thread.currentThread().setContextClassLoader(oldLoader);
      }

   }

   public void destroy(WebAppContext context) throws Exception {
      try {
         NamingContext scopeContext = (NamingContext)NamingEntryUtil.getContextForScope(context);
         scopeContext.getParent().destroySubcontext(scopeContext.getName());
      } catch (NameNotFoundException e) {
         LOG.ignore(e);
         LOG.debug("No jndi entries scoped to webapp {}", context);
      } catch (NamingException e) {
         LOG.debug("Error unbinding jndi entries scoped to webapp " + context, (Throwable)e);
      }

   }

   public void bindEnvEntries(WebAppContext context) throws NamingException {
      LOG.debug("Binding env entries from the jvm scope");
      InitialContext ic = new InitialContext();
      Context envCtx = (Context)ic.lookup("java:comp/env");
      Object scope = null;

      for(EnvEntry ee : NamingEntryUtil.lookupNamingEntries(scope, EnvEntry.class)) {
         ee.bindToENC(ee.getJndiName());
         Name namingEntryName = NamingEntryUtil.makeNamingEntryName((NameParser)null, (NamingEntry)ee);
         NamingUtil.bind(envCtx, namingEntryName.toString(), ee);
      }

      LOG.debug("Binding env entries from the server scope");
      Object var9 = context.getServer();

      for(EnvEntry ee : NamingEntryUtil.lookupNamingEntries(var9, EnvEntry.class)) {
         ee.bindToENC(ee.getJndiName());
         Name namingEntryName = NamingEntryUtil.makeNamingEntryName((NameParser)null, (NamingEntry)ee);
         NamingUtil.bind(envCtx, namingEntryName.toString(), ee);
      }

      LOG.debug("Binding env entries from the context scope");

      for(EnvEntry ee : NamingEntryUtil.lookupNamingEntries(context, EnvEntry.class)) {
         ee.bindToENC(ee.getJndiName());
         Name namingEntryName = NamingEntryUtil.makeNamingEntryName((NameParser)null, (NamingEntry)ee);
         NamingUtil.bind(envCtx, namingEntryName.toString(), ee);
      }

   }

   protected void createEnvContext(WebAppContext wac) throws NamingException {
      ClassLoader old_loader = Thread.currentThread().getContextClassLoader();
      Thread.currentThread().setContextClassLoader(wac.getClassLoader());
      ContextFactory.associateClassLoader(wac.getClassLoader());

      try {
         Context context = new InitialContext();
         Context compCtx = (Context)context.lookup("java:comp");
         compCtx.createSubcontext("env");
      } finally {
         ContextFactory.disassociateClassLoader();
         Thread.currentThread().setContextClassLoader(old_loader);
      }

   }

   private static class Bound {
      final NamingContext _context;
      final String _name;

      Bound(NamingContext context, String name) {
         this._context = context;
         this._name = name;
      }
   }
}
