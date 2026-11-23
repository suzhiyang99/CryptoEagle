package org.apache.deltaspike.core.impl.util;

import java.lang.annotation.Annotation;
import java.util.Set;
import javax.enterprise.context.spi.Contextual;
import javax.enterprise.inject.Typed;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.spi.PassivationCapable;
import org.apache.deltaspike.core.api.scope.ConversationGroup;
import org.apache.deltaspike.core.api.scope.ConversationSubGroup;
import org.apache.deltaspike.core.impl.scope.conversation.ConversationKey;

@Typed
public abstract class ConversationUtils {
   private ConversationUtils() {
   }

   public static ConversationKey convertToConversationKey(Contextual<?> contextual, BeanManager beanManager) {
      if (!(contextual instanceof Bean)) {
         if (!(contextual instanceof PassivationCapable)) {
            throw new IllegalArgumentException(contextual.getClass().getName() + " is not of type " + Bean.class.getName());
         }

         contextual = beanManager.getPassivationCapableBean(((PassivationCapable)contextual).getId());
      }

      Bean<?> bean = (Bean)contextual;
      ConversationGroup conversationGroupAnnotation = findConversationGroupAnnotation(bean);
      Class<?> conversationGroup;
      if (conversationGroupAnnotation != null) {
         conversationGroup = conversationGroupAnnotation.value();
      } else {
         conversationGroup = bean.getBeanClass();
      }

      Set<Annotation> qualifiers = bean.getQualifiers();
      return new ConversationKey(conversationGroup, (Annotation[])qualifiers.toArray(new Annotation[qualifiers.size()]));
   }

   private static ConversationGroup findConversationGroupAnnotation(Bean<?> bean) {
      for(Annotation qualifier : bean.getQualifiers()) {
         if (ConversationGroup.class.isAssignableFrom(qualifier.annotationType())) {
            return (ConversationGroup)qualifier;
         }
      }

      return null;
   }

   public static Class<?> getDeclaredConversationGroup(Class<?> conversationGroup) {
      ConversationSubGroup conversationSubGroup = (ConversationSubGroup)conversationGroup.getAnnotation(ConversationSubGroup.class);
      if (conversationSubGroup == null) {
         return conversationGroup;
      } else {
         Class<?> result = conversationSubGroup.of();
         if (!ConversationSubGroup.class.equals(result)) {
            return result;
         } else {
            result = conversationGroup.getSuperclass();
            if ((result == null || Object.class.getName().equals(result.getName())) && conversationGroup.getInterfaces().length == 1) {
               return conversationGroup.getInterfaces()[0];
            } else if (result == null) {
               throw new IllegalStateException(conversationGroup.getName() + " hosts an invalid usage of @" + ConversationSubGroup.class.getName());
            } else {
               return result;
            }
         }
      }
   }
}
