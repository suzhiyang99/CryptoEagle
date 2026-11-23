package org.apache.meecrowave.openwebbeans;

import java.lang.annotation.Annotation;
import java.security.Principal;
import javax.enterprise.context.spi.Contextual;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.spi.CDI;
import javax.servlet.http.HttpServletRequest;
import org.apache.webbeans.corespi.security.SimpleSecurityService;

public class MeecrowaveSecurityService extends SimpleSecurityService {
   public Principal getCurrentPrincipal() {
      return new MeecrowavePrincipal();
   }

   private static class MeecrowavePrincipal implements Principal {
      private MeecrowavePrincipal() {
      }

      public String getName() {
         return this.unwrap().getName();
      }

      private Principal unwrap() {
         BeanManager beanManager = CDI.current().getBeanManager();
         return ((HttpServletRequest)HttpServletRequest.class.cast(beanManager.getReference(beanManager.resolve(beanManager.getBeans(HttpServletRequest.class, new Annotation[0])), HttpServletRequest.class, beanManager.createCreationalContext((Contextual)null)))).getUserPrincipal();
      }
   }
}
