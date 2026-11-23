package org.apache.deltaspike.core.impl.scope.conversation;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.enterprise.context.spi.Contextual;
import javax.enterprise.inject.Typed;
import javax.enterprise.inject.spi.BeanManager;
import org.apache.deltaspike.core.api.scope.ConversationSubGroup;
import org.apache.deltaspike.core.api.scope.GroupedConversationScoped;
import org.apache.deltaspike.core.impl.scope.window.WindowContextImpl;
import org.apache.deltaspike.core.impl.util.ConversationUtils;
import org.apache.deltaspike.core.spi.scope.conversation.GroupedConversationManager;
import org.apache.deltaspike.core.util.context.AbstractContext;
import org.apache.deltaspike.core.util.context.ContextualInstanceInfo;
import org.apache.deltaspike.core.util.context.ContextualStorage;

@Typed
public class GroupedConversationContext extends AbstractContext implements GroupedConversationManager {
   private static final long serialVersionUID = -5463564406828391468L;
   private final BeanManager beanManager;
   private final WindowContextImpl windowContext;
   private ConversationBeanHolder conversationBeanHolder;

   public GroupedConversationContext(BeanManager beanManager, WindowContextImpl windowContext) {
      super(beanManager);
      this.beanManager = beanManager;
      this.windowContext = windowContext;
   }

   public void init(ConversationBeanHolder conversationBeanHolder) {
      this.conversationBeanHolder = conversationBeanHolder;
   }

   protected ContextualStorage getContextualStorage(Contextual<?> contextual, boolean createIfNotExist) {
      ConversationKey conversationKey = ConversationUtils.convertToConversationKey(contextual, this.beanManager);
      return this.conversationBeanHolder.getContextualStorage(this.beanManager, conversationKey, createIfNotExist);
   }

   protected List<ContextualStorage> getActiveContextualStorages() {
      List<ContextualStorage> result = new ArrayList();
      result.addAll(this.conversationBeanHolder.getStorageMap().values());
      return result;
   }

   public Class<? extends Annotation> getScope() {
      return GroupedConversationScoped.class;
   }

   public boolean isActive() {
      return this.windowContext.isActive();
   }

   public ContextualStorage closeConversation(Class<?> conversationGroup, Annotation... qualifiers) {
      ConversationKey conversationKey = new ConversationKey(conversationGroup, qualifiers);
      ContextualStorage contextualStorage = (ContextualStorage)this.conversationBeanHolder.getStorageMap().remove(conversationKey);
      if (contextualStorage != null) {
         AbstractContext.destroyAllActive(contextualStorage);
      }

      return contextualStorage;
   }

   public Set<ContextualStorage> closeConversationGroup(Class<?> conversationGroup) {
      Set<ContextualStorage> result = new HashSet();
      ConversationSubGroup conversationSubGroup = (ConversationSubGroup)conversationGroup.getAnnotation(ConversationSubGroup.class);
      Set<Class<?>> subGroups = null;
      if (conversationSubGroup != null) {
         conversationGroup = ConversationUtils.getDeclaredConversationGroup(conversationGroup);
         subGroups = new HashSet(conversationSubGroup.subGroup().length);
         Collections.addAll(subGroups, conversationSubGroup.subGroup());
      }

      Map<ConversationKey, ContextualStorage> storageMap = this.conversationBeanHolder.getStorageMap();

      for(Map.Entry<ConversationKey, ContextualStorage> entry : storageMap.entrySet()) {
         if (((ConversationKey)entry.getKey()).getConversationGroup().equals(conversationGroup)) {
            if (subGroups == null) {
               AbstractContext.destroyAllActive((ContextualStorage)entry.getValue());
               result.add(entry.getValue());
               storageMap.remove(entry.getKey());
            } else {
               this.tryToDestroySubGroup(subGroups, entry);
               if (((ContextualStorage)entry.getValue()).getStorage().isEmpty()) {
                  storageMap.remove(entry.getKey());
               }
            }
         }
      }

      return result;
   }

   private void tryToDestroySubGroup(Set<Class<?>> subGroups, Map.Entry<ConversationKey, ContextualStorage> entry) {
      ContextualStorage storage = (ContextualStorage)entry.getValue();

      for(Map.Entry<Object, ContextualInstanceInfo<?>> storageEntry : storage.getStorage().entrySet()) {
         for(Class<?> subGroup : subGroups) {
            Class classOfEntry = ((ContextualInstanceInfo)storageEntry.getValue()).getContextualInstance().getClass();
            if (subGroup.equals(classOfEntry) || subGroup.isInterface() && subGroup.isAssignableFrom(classOfEntry)) {
               Contextual bean = storage.getBean(storageEntry.getKey());
               AbstractContext.destroyBean(bean, (ContextualInstanceInfo)storageEntry.getValue());
               storage.getStorage().remove(storageEntry.getKey());
               break;
            }
         }
      }

   }

   public void closeConversations() {
      this.conversationBeanHolder.destroyBeans();
   }
}
