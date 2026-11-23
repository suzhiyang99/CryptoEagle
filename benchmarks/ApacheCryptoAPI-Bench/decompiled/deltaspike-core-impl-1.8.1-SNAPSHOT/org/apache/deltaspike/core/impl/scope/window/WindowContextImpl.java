package org.apache.deltaspike.core.impl.scope.window;

import java.lang.annotation.Annotation;
import javax.enterprise.context.ContextNotActiveException;
import javax.enterprise.context.spi.Contextual;
import javax.enterprise.inject.Typed;
import javax.enterprise.inject.spi.BeanManager;
import org.apache.deltaspike.core.api.scope.WindowScoped;
import org.apache.deltaspike.core.spi.scope.window.WindowContext;
import org.apache.deltaspike.core.util.context.AbstractContext;
import org.apache.deltaspike.core.util.context.ContextualStorage;

@Typed
public class WindowContextImpl extends AbstractContext implements WindowContext {
   private WindowIdHolder windowIdHolder;
   private WindowBeanHolder windowBeanHolder;
   private BeanManager beanManager;

   public WindowContextImpl(BeanManager beanManager) {
      super(beanManager);
      this.beanManager = beanManager;
   }

   public void init(WindowBeanHolder windowBeanHolder, WindowIdHolder windowIdHolder) {
      this.windowBeanHolder = windowBeanHolder;
      this.windowIdHolder = windowIdHolder;
   }

   public void activateWindow(String windowId) {
      this.windowIdHolder.setWindowId(windowId);
   }

   public String getCurrentWindowId() {
      return this.windowIdHolder.getWindowId();
   }

   public boolean closeWindow(String windowId) {
      if (windowId == null) {
         return false;
      } else {
         ContextualStorage windowStorage = (ContextualStorage)this.windowBeanHolder.getStorageMap().remove(windowId);
         if (windowStorage != null) {
            if (windowId.equals(this.windowIdHolder.getWindowId())) {
               this.windowIdHolder.setWindowId((String)null);
            }

            AbstractContext.destroyAllActive(windowStorage);
         }

         return windowStorage != null;
      }
   }

   protected ContextualStorage getContextualStorage(Contextual<?> contextual, boolean createIfNotExist) {
      String windowId = this.getCurrentWindowId();
      if (windowId == null) {
         throw new ContextNotActiveException("WindowContext: no windowId set for the current Thread yet!");
      } else {
         return this.windowBeanHolder.getContextualStorage(this.beanManager, windowId, createIfNotExist);
      }
   }

   public Class<? extends Annotation> getScope() {
      return WindowScoped.class;
   }

   public boolean isActive() {
      String windowId = this.getCurrentWindowId();
      return windowId != null;
   }
}
