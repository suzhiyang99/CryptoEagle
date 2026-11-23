package org.apache.openejb.config;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.ejb.EJBContext;
import javax.ejb.EntityContext;
import javax.ejb.MessageDrivenContext;
import javax.ejb.SessionContext;
import javax.ejb.TimerService;
import javax.enterprise.inject.spi.BeanManager;
import javax.transaction.TransactionManager;
import javax.transaction.TransactionSynchronizationRegistry;
import javax.transaction.UserTransaction;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import javax.xml.ws.WebServiceContext;
import org.apache.openejb.OpenEJBException;
import org.apache.openejb.jee.EjbJar;
import org.apache.openejb.jee.EnterpriseBean;
import org.apache.openejb.jee.JndiConsumer;
import org.apache.openejb.jee.JndiReference;

public class LinkBuiltInTypes implements DynamicDeployer {
   public AppModule deploy(AppModule appModule) throws OpenEJBException {
      for(ClientModule module : appModule.getClientModules()) {
         JndiConsumer consumer = module.getApplicationClient();
         if (consumer != null) {
            this.link(consumer);
         }
      }

      for(WebModule module : appModule.getWebModules()) {
         JndiConsumer consumer = module.getWebApp();
         if (consumer != null) {
            this.link(consumer);
         }
      }

      for(EjbModule module : appModule.getEjbModules()) {
         EjbJar ejbJar = module.getEjbJar();
         if (ejbJar != null) {
            for(EnterpriseBean consumer : ejbJar.getEnterpriseBeans()) {
               this.link(consumer);
            }
         }
      }

      return appModule;
   }

   private void link(JndiConsumer consumer) {
      Map<String, String> links = new HashMap();
      this.add(links, BeanManager.class);
      this.add(links, Validator.class);
      this.add(links, ValidatorFactory.class);
      this.add(links, EJBContext.class, EntityContext.class, SessionContext.class, MessageDrivenContext.class);
      this.add(links, UserTransaction.class);
      this.add(links, TransactionManager.class);
      this.add(links, TransactionSynchronizationRegistry.class);
      this.add(links, TimerService.class);
      this.add(links, WebServiceContext.class);
      List<JndiReference> refs = new ArrayList();
      refs.addAll(consumer.getResourceRef());
      refs.addAll(consumer.getResourceEnvRef());

      for(JndiReference ref : refs) {
         String link = (String)links.get(ref.getType());
         if (link != null) {
            if (ref.getName().equals(link)) {
               ref.setLookupName((String)null);
            } else {
               ref.setLookupName(link);
            }
         }
      }

   }

   private void add(Map<String, String> links, Class<?> type, Class... aliases) {
      links.put(type.getName(), "java:comp/" + type.getSimpleName());

      for(Class clazz : aliases) {
         links.put(clazz.getName(), "java:comp/" + type.getSimpleName());
      }

   }
}
