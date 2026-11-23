package org.apache.deltaspike.core.impl.scope;

import java.lang.annotation.Annotation;
import javax.enterprise.event.Observes;
import javax.enterprise.inject.spi.AfterBeanDiscovery;
import javax.enterprise.inject.spi.AfterDeploymentValidation;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.spi.BeforeBeanDiscovery;
import javax.enterprise.inject.spi.Extension;
import org.apache.deltaspike.core.api.provider.BeanProvider;
import org.apache.deltaspike.core.impl.scope.conversation.ConversationBeanHolder;
import org.apache.deltaspike.core.impl.scope.conversation.GroupedConversationContext;
import org.apache.deltaspike.core.impl.scope.viewaccess.ViewAccessBeanAccessHistory;
import org.apache.deltaspike.core.impl.scope.viewaccess.ViewAccessBeanHolder;
import org.apache.deltaspike.core.impl.scope.viewaccess.ViewAccessContext;
import org.apache.deltaspike.core.impl.scope.viewaccess.ViewAccessViewHistory;
import org.apache.deltaspike.core.impl.scope.window.WindowBeanHolder;
import org.apache.deltaspike.core.impl.scope.window.WindowContextImpl;
import org.apache.deltaspike.core.impl.scope.window.WindowIdHolder;
import org.apache.deltaspike.core.spi.activation.Deactivatable;
import org.apache.deltaspike.core.util.ClassDeactivationUtils;

public class DeltaSpikeContextExtension implements Extension, Deactivatable {
   private WindowContextImpl windowContext;
   private GroupedConversationContext conversationContext;
   private ViewAccessContext viewAccessScopedContext;
   private Boolean isActivated = true;

   protected void init(@Observes BeforeBeanDiscovery beforeBeanDiscovery) {
      this.isActivated = ClassDeactivationUtils.isActivated(this.getClass());
   }

   public void registerDeltaSpikeContexts(@Observes AfterBeanDiscovery afterBeanDiscovery, BeanManager beanManager) {
      if (this.isActivated) {
         this.windowContext = new WindowContextImpl(beanManager);
         this.conversationContext = new GroupedConversationContext(beanManager, this.windowContext);
         this.viewAccessScopedContext = new ViewAccessContext(beanManager, this.windowContext);
         afterBeanDiscovery.addContext(this.windowContext);
         afterBeanDiscovery.addContext(this.conversationContext);
         afterBeanDiscovery.addContext(this.viewAccessScopedContext);
      }
   }

   public void initializeDeltaSpikeContexts(@Observes AfterDeploymentValidation adv, BeanManager beanManager) {
      if (this.isActivated) {
         WindowBeanHolder windowBeanHolder = (WindowBeanHolder)BeanProvider.getContextualReference(beanManager, WindowBeanHolder.class, false, new Annotation[0]);
         WindowIdHolder windowIdHolder = (WindowIdHolder)BeanProvider.getContextualReference(beanManager, WindowIdHolder.class, false, new Annotation[0]);
         this.windowContext.init(windowBeanHolder, windowIdHolder);
         ConversationBeanHolder conversationBeanHolder = (ConversationBeanHolder)BeanProvider.getContextualReference(beanManager, ConversationBeanHolder.class, false, new Annotation[0]);
         this.conversationContext.init(conversationBeanHolder);
         ViewAccessBeanHolder viewAccessBeanHolder = (ViewAccessBeanHolder)BeanProvider.getContextualReference(beanManager, ViewAccessBeanHolder.class, false, new Annotation[0]);
         ViewAccessBeanAccessHistory viewAccessBeanAccessHistory = (ViewAccessBeanAccessHistory)BeanProvider.getContextualReference(beanManager, ViewAccessBeanAccessHistory.class, false, new Annotation[0]);
         ViewAccessViewHistory viewAccessViewHistory = (ViewAccessViewHistory)BeanProvider.getContextualReference(beanManager, ViewAccessViewHistory.class, false, new Annotation[0]);
         this.viewAccessScopedContext.init(viewAccessBeanHolder, viewAccessBeanAccessHistory, viewAccessViewHistory);
      }
   }

   public WindowContextImpl getWindowContext() {
      return this.windowContext;
   }

   public GroupedConversationContext getConversationContext() {
      return this.conversationContext;
   }

   public ViewAccessContext getViewAccessScopedContext() {
      return this.viewAccessScopedContext;
   }
}
