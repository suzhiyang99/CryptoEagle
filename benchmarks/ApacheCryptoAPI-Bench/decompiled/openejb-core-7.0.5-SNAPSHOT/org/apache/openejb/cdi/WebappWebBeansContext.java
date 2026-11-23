package org.apache.openejb.cdi;

import java.util.Map;
import java.util.Properties;
import javax.enterprise.inject.spi.Bean;
import org.apache.webbeans.config.WebBeansContext;
import org.apache.webbeans.container.BeanManagerImpl;

public class WebappWebBeansContext extends WebBeansContext {
   private final WebBeansContext parent;
   private BeanManagerImpl bm;

   public WebappWebBeansContext(Map<Class<?>, Object> services, Properties properties, WebBeansContext webBeansContext) {
      super(services, properties);
      this.parent = webBeansContext;
   }

   public BeanManagerImpl getBeanManagerImpl() {
      if (this.bm == null) {
         synchronized(this) {
            if (this.bm == null) {
               this.bm = new WebappBeanManager(this);
            }
         }
      }

      return this.bm;
   }

   public WebBeansContext getParent() {
      return this.parent;
   }

   public boolean findMissingAnnotatedType(Class<?> missing) {
      for(Bean<?> b : this.getParent().getBeanManagerImpl().getBeans()) {
         if (b.getBeanClass() == missing) {
            return true;
         }
      }

      return false;
   }
}
