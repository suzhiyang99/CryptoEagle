package org.apache.openejb.config.rules;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import org.apache.openejb.config.AppModule;
import org.apache.openejb.config.EjbModule;
import org.apache.openejb.jee.EnterpriseBean;
import org.apache.openejb.jee.SessionBean;
import org.apache.openejb.jee.SessionType;
import org.apache.openejb.util.CircularReferencesException;
import org.apache.openejb.util.Join;
import org.apache.openejb.util.LinkResolver;
import org.apache.openejb.util.References;

public class CheckDependsOn extends ValidationBase {
   public void validate(AppModule appModule) {
      this.module = appModule;
      LinkResolver<Bean> app = new LinkResolver<Bean>();

      for(EjbModule ejbModule : appModule.getEjbModules()) {
         Resolver<Bean> resolver = new Resolver<Bean>(app, new LinkResolver());

         for(EnterpriseBean bean : ejbModule.getEjbJar().getEnterpriseBeans()) {
            Bean b = new Bean(bean, ejbModule, ejbModule.getModuleUri(), resolver);
            resolver.module.add((URI)ejbModule.getModuleUri(), bean.getEjbName(), b);
            resolver.app.add((URI)ejbModule.getModuleUri(), bean.getEjbName(), b);
         }
      }

      for(Bean bean : app.values()) {
         EnterpriseBean enterpriseBean = bean.bean;
         if (enterpriseBean instanceof SessionBean) {
            SessionBean sessionBean = (SessionBean)enterpriseBean;
            if (sessionBean.getSessionType() == SessionType.SINGLETON) {
               for(String ejbName : sessionBean.getDependsOn()) {
                  Bean referee = bean.resolveLink(ejbName);
                  if (referee == null) {
                     bean.module.getValidation().fail(enterpriseBean.getEjbName(), "dependsOn.noSuchEjb", ejbName);
                  } else {
                     bean.dependsOn.add(referee);
                  }
               }
            }
         }
      }

      try {
         References.sort(new ArrayList(app.values()), new References.Visitor<Bean>() {
            public String getName(Bean t) {
               return t.getId();
            }

            public Set<String> getReferences(Bean t) {
               LinkedHashSet<String> refs = new LinkedHashSet();

               for(Bean bean : t.dependsOn) {
                  refs.add(bean.getId());
               }

               return refs;
            }
         });
      } catch (CircularReferencesException e) {
         for(List<Bean> circuit : e.getCircuits()) {
            List<String> ejbNames = new ArrayList(circuit.size());

            for(Bean bean : circuit) {
               ejbNames.add(bean.bean.getEjbName());
            }

            this.fail("EAR", "dependsOn.circuit", new Object[]{Join.join(" -> ", (Collection)ejbNames), ejbNames.get(0)});
         }
      }

   }

   public static class Resolver<T> {
      private final LinkResolver<T> module;
      private final LinkResolver<T> app;

      public Resolver(LinkResolver<T> app, LinkResolver<T> module) {
         this.app = app;
         this.module = module;
      }

      public T resolveLink(String link, URI moduleUri) {
         T value = (T)this.module.resolveLink(link, moduleUri);
         return (T)(value != null ? value : this.app.resolveLink(link, moduleUri));
      }
   }

   public static class Bean {
      private final URI moduleUri;
      private final EnterpriseBean bean;
      private final ArrayList<Bean> dependsOn = new ArrayList();
      private final EjbModule module;
      private final Resolver<Bean> resolver;

      public Bean(EnterpriseBean bean, EjbModule module, URI moduleUri, Resolver<Bean> resolver) {
         this.bean = bean;
         this.module = module;
         this.moduleUri = moduleUri;
         this.resolver = resolver;
      }

      public Bean resolveLink(String ejbName) {
         return this.resolver.resolveLink(ejbName, this.moduleUri);
      }

      public String getId() {
         return this.toString();
      }
   }
}
