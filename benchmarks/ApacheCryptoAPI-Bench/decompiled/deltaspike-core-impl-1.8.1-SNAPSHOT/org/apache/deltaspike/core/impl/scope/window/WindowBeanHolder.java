package org.apache.deltaspike.core.impl.scope.window;

import javax.annotation.PostConstruct;
import javax.enterprise.context.SessionScoped;
import javax.enterprise.inject.spi.BeanManager;
import javax.inject.Inject;
import org.apache.deltaspike.core.impl.scope.AbstractBeanHolder;
import org.apache.deltaspike.core.spi.activation.Deactivatable;
import org.apache.deltaspike.core.spi.scope.window.WindowContextQuotaHandler;
import org.apache.deltaspike.core.util.ClassDeactivationUtils;
import org.apache.deltaspike.core.util.ProxyUtils;
import org.apache.deltaspike.core.util.context.ContextualStorage;

@SessionScoped
public class WindowBeanHolder extends AbstractBeanHolder<String> {
   private static final long serialVersionUID = 6313493410718133308L;
   @Inject
   private WindowContextQuotaHandler windowContextQuotaHandler;
   private boolean windowContextQuotaHandlerEnabled;

   @PostConstruct
   protected void init() {
      Class<? extends Deactivatable> windowContextQuotaHandlerClass = ProxyUtils.getUnproxiedClass(this.windowContextQuotaHandler.getClass());
      this.windowContextQuotaHandlerEnabled = ClassDeactivationUtils.isActivated(windowContextQuotaHandlerClass);
   }

   public ContextualStorage getContextualStorage(BeanManager beanManager, String key, boolean createIfNotExist) {
      ContextualStorage result = super.getContextualStorage(beanManager, key, createIfNotExist);
      if (this.windowContextQuotaHandlerEnabled) {
         this.windowContextQuotaHandler.checkWindowContextQuota(key);
      }

      return result;
   }
}
