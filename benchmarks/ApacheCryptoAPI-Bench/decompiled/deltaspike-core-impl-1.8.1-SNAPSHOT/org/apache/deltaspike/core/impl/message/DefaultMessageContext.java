package org.apache.deltaspike.core.impl.message;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Typed;
import javax.inject.Inject;
import org.apache.deltaspike.core.api.message.LocaleResolver;
import org.apache.deltaspike.core.api.message.Message;
import org.apache.deltaspike.core.api.message.MessageContext;
import org.apache.deltaspike.core.api.message.MessageInterpolator;
import org.apache.deltaspike.core.api.message.MessageResolver;

@Dependent
@Typed({MessageContext.class})
class DefaultMessageContext implements MessageContext {
   private static final long serialVersionUID = -110779217295211303L;
   @Inject
   private MessageInterpolator messageInterpolator = null;
   @Inject
   private MessageResolver messageResolver = null;
   @Inject
   private LocaleResolver localeResolver = null;
   private List<String> messageSources = new ArrayList();

   public DefaultMessageContext() {
   }

   public DefaultMessageContext(MessageContext otherMessageContext) {
      this.messageInterpolator(otherMessageContext.getMessageInterpolator());
      this.localeResolver(otherMessageContext.getLocaleResolver());
      this.messageResolver(otherMessageContext.getMessageResolver());
      this.messageSources.addAll(otherMessageContext.getMessageSources());
   }

   public MessageContext clone() {
      return new DefaultMessageContext(this);
   }

   public Message message() {
      return new DefaultMessage(this);
   }

   public MessageContext messageSource(String... messageSource) {
      List<String> newMessageSources = new ArrayList();

      for(String currentMessageSource : messageSource) {
         if (!this.messageSources.contains(currentMessageSource)) {
            newMessageSources.add(currentMessageSource);
         }
      }

      this.messageSources.addAll(0, newMessageSources);
      return this;
   }

   public Locale getLocale() {
      return this.getLocaleResolver() == null ? Locale.getDefault() : this.getLocaleResolver().getLocale();
   }

   public LocaleResolver getLocaleResolver() {
      return this.localeResolver;
   }

   public List<String> getMessageSources() {
      return Collections.unmodifiableList(this.messageSources);
   }

   public MessageContext localeResolver(LocaleResolver localeResolver) {
      this.localeResolver = localeResolver;
      return this;
   }

   public MessageInterpolator getMessageInterpolator() {
      return this.messageInterpolator;
   }

   public MessageContext messageInterpolator(MessageInterpolator messageInterpolator) {
      this.messageInterpolator = messageInterpolator;
      return this;
   }

   public MessageResolver getMessageResolver() {
      return this.messageResolver;
   }

   public MessageContext messageResolver(MessageResolver messageResolver) {
      this.messageResolver = messageResolver;
      return this;
   }
}
