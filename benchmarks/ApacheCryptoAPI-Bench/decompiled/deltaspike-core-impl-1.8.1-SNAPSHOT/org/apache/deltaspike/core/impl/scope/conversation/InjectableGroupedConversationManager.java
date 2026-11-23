package org.apache.deltaspike.core.impl.scope.conversation;

import java.lang.annotation.Annotation;
import java.util.Set;
import javax.enterprise.inject.Typed;
import org.apache.deltaspike.core.api.provider.BeanProvider;
import org.apache.deltaspike.core.impl.scope.DeltaSpikeContextExtension;
import org.apache.deltaspike.core.spi.scope.conversation.GroupedConversationManager;
import org.apache.deltaspike.core.util.context.ContextualStorage;

@Typed
class InjectableGroupedConversationManager implements GroupedConversationManager {
   private transient volatile GroupedConversationManager conversationManager;

   public InjectableGroupedConversationManager(GroupedConversationManager conversationManager) {
      this.conversationManager = conversationManager;
   }

   private GroupedConversationManager getConversationManager() {
      if (this.conversationManager == null) {
         this.conversationManager = ((DeltaSpikeContextExtension)BeanProvider.getContextualReference(DeltaSpikeContextExtension.class, new Annotation[0])).getConversationContext();
      }

      return this.conversationManager;
   }

   public ContextualStorage closeConversation(Class<?> conversationGroup, Annotation... qualifiers) {
      return this.getConversationManager().closeConversation(conversationGroup, qualifiers);
   }

   public Set<ContextualStorage> closeConversationGroup(Class<?> conversationGroup) {
      return this.getConversationManager().closeConversationGroup(conversationGroup);
   }

   public void closeConversations() {
      this.getConversationManager().closeConversations();
   }
}
