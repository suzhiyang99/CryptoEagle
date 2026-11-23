package org.apache.deltaspike.core.impl.scope.viewaccess;

import java.lang.annotation.Annotation;
import java.util.Map;
import javax.enterprise.context.spi.Contextual;
import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.Typed;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.spi.PassivationCapable;
import org.apache.deltaspike.core.api.scope.ViewAccessScoped;
import org.apache.deltaspike.core.impl.scope.window.WindowContextImpl;
import org.apache.deltaspike.core.spi.scope.viewaccess.ViewAccessContextManager;
import org.apache.deltaspike.core.util.context.AbstractContext;
import org.apache.deltaspike.core.util.context.ContextualInstanceInfo;
import org.apache.deltaspike.core.util.context.ContextualStorage;

@Typed
public class ViewAccessContext extends AbstractContext implements ViewAccessContextManager {
   private static final String KEY = "VAS";
   private final BeanManager beanManager;
   private final WindowContextImpl windowContext;
   private ViewAccessBeanHolder viewAccessBeanHolder;
   private ViewAccessBeanAccessHistory viewAccessBeanAccessHistory;
   private ViewAccessViewHistory viewAccessViewHistory;

   public ViewAccessContext(BeanManager beanManager, WindowContextImpl windowContext) {
      super(beanManager);
      this.beanManager = beanManager;
      this.windowContext = windowContext;
   }

   public void init(ViewAccessBeanHolder viewAccessBeanHolder, ViewAccessBeanAccessHistory viewAccessBeanAccessHistory, ViewAccessViewHistory viewAccessViewHistory) {
      this.viewAccessBeanHolder = viewAccessBeanHolder;
      this.viewAccessBeanAccessHistory = viewAccessBeanAccessHistory;
      this.viewAccessViewHistory = viewAccessViewHistory;
   }

   public <T> T get(Contextual<T> bean) {
      Object var2;
      try {
         var2 = super.get(bean);
      } finally {
         if (bean instanceof PassivationCapable) {
            PassivationCapable pc = (PassivationCapable)bean;
            this.viewAccessBeanAccessHistory.getAccessedBeans().add(pc.getId());
         }

      }

      return (T)var2;
   }

   public <T> T get(Contextual<T> bean, CreationalContext<T> creationalContext) {
      Object var3;
      try {
         var3 = super.get(bean, creationalContext);
      } finally {
         if (bean instanceof PassivationCapable) {
            PassivationCapable pc = (PassivationCapable)bean;
            this.viewAccessBeanAccessHistory.getAccessedBeans().add(pc.getId());
         }

      }

      return (T)var3;
   }

   protected ContextualStorage getContextualStorage(Contextual<?> contextual, boolean createIfNotExist) {
      return this.viewAccessBeanHolder.getContextualStorage(this.beanManager, "VAS", createIfNotExist);
   }

   public Class<? extends Annotation> getScope() {
      return ViewAccessScoped.class;
   }

   public boolean isActive() {
      return this.windowContext.isActive();
   }

   public void onProcessingViewFinished(String view) {
      this.close(view, false);
   }

   public void close(String view, boolean force) {
      if (this.windowContext.isActive()) {
         if (force || !view.equals(this.viewAccessViewHistory.getLastView())) {
            this.viewAccessViewHistory.setLastView(view);
            this.destroyExpiredBeans(force);
         }

         this.viewAccessBeanAccessHistory.getAccessedBeans().clear();
      }
   }

   private void destroyExpiredBeans(boolean force) {
      ContextualStorage storage = this.viewAccessBeanHolder.getContextualStorage(this.beanManager, "VAS", false);
      if (storage != null) {
         for(Map.Entry<Object, ContextualInstanceInfo<?>> storageEntry : storage.getStorage().entrySet()) {
            if (force || !this.viewAccessBeanAccessHistory.getAccessedBeans().contains((String)storageEntry.getKey())) {
               Contextual bean = storage.getBean(storageEntry.getKey());
               AbstractContext.destroyBean(bean, (ContextualInstanceInfo)storageEntry.getValue());
               storage.getStorage().remove(storageEntry.getKey());
            }
         }
      }

   }

   public void close() {
      this.close((String)null, true);
   }
}
