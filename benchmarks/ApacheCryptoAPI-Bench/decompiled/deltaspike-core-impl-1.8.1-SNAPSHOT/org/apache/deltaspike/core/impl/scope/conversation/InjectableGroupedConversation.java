package org.apache.deltaspike.core.impl.scope.conversation;

import java.lang.annotation.Annotation;
import java.util.Set;
import javax.enterprise.inject.Typed;
import org.apache.deltaspike.core.api.scope.GroupedConversation;
import org.apache.deltaspike.core.spi.scope.conversation.GroupedConversationManager;

@Typed
class InjectableGroupedConversation implements GroupedConversation {
   private static final long serialVersionUID = -3909049219127821425L;
   private final ConversationKey conversationKey;
   private final GroupedConversationManager conversationManager;

   InjectableGroupedConversation(ConversationKey conversationKey, GroupedConversationManager conversationManager) {
      this.conversationManager = conversationManager;
      this.conversationKey = conversationKey;
   }

   public void close() {
      Set<Annotation> qualifiers = this.conversationKey.getQualifiers();
      this.conversationManager.closeConversation(this.conversationKey.getConversationGroup(), (Annotation[])qualifiers.toArray(new Annotation[qualifiers.size()]));
   }
}
