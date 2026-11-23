package org.apache.deltaspike.core.impl.scope.conversation;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.spi.InjectionPoint;
import javax.inject.Inject;
import org.apache.deltaspike.core.api.scope.GroupedConversation;
import org.apache.deltaspike.core.impl.scope.DeltaSpikeContextExtension;
import org.apache.deltaspike.core.impl.util.ConversationUtils;
import org.apache.deltaspike.core.spi.scope.conversation.GroupedConversationManager;

@ApplicationScoped
public class GroupedConversationArtifactProducer {
   @Inject
   private DeltaSpikeContextExtension deltaSpikeContextExtension;

   @Produces
   @Dependent
   public GroupedConversationManager getGroupedConversationManager() {
      return new InjectableGroupedConversationManager(this.deltaSpikeContextExtension.getConversationContext());
   }

   @Produces
   @Dependent
   public GroupedConversation getGroupedConversation(InjectionPoint injectionPoint, BeanManager beanManager) {
      ConversationKey conversationKey = ConversationUtils.convertToConversationKey(injectionPoint.getBean(), beanManager);
      return new InjectableGroupedConversation(conversationKey, this.getGroupedConversationManager());
   }
}
