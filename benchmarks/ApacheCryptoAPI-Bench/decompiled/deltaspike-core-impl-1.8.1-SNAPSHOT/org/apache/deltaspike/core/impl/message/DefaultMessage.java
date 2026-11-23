package org.apache.deltaspike.core.impl.message;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import javax.enterprise.inject.Typed;
import org.apache.deltaspike.core.api.message.Message;
import org.apache.deltaspike.core.api.message.MessageContext;
import org.apache.deltaspike.core.api.message.MessageInterpolator;
import org.apache.deltaspike.core.api.message.MessageResolver;

@Typed
public class DefaultMessage implements Message {
   private String messageTemplate;
   private List<Serializable> arguments = new ArrayList();
   private MessageContext messageContext;

   public DefaultMessage(MessageContext messageContext) {
      this.reset();
      this.messageContext = messageContext;
   }

   protected void reset() {
      this.messageTemplate = null;
      this.arguments = new ArrayList();
   }

   public Message argument(Serializable... arguments) {
      if (arguments != null) {
         Collections.addAll(this.arguments, arguments);
      }

      return this;
   }

   public Message template(String messageTemplate) {
      this.messageTemplate = messageTemplate;
      return this;
   }

   public String getTemplate() {
      return this.messageTemplate;
   }

   public Serializable[] getArguments() {
      return (Serializable[])this.arguments.toArray(new Serializable[this.arguments.size()]);
   }

   public String toString() {
      return this.toString((String)null);
   }

   public String toString(String category) {
      String template = this.getTemplate();
      if (template == null) {
         return "";
      } else {
         String ret = template;
         MessageResolver messageResolver = this.messageContext.getMessageResolver();
         if (messageResolver != null) {
            String resolvedTemplate = messageResolver.getMessage(this.messageContext, template, category);
            if (resolvedTemplate == null) {
               resolvedTemplate = this.markAsUnresolved(template);
            }

            ret = resolvedTemplate;
            template = resolvedTemplate;
         }

         MessageInterpolator messageInterpolator = this.messageContext.getMessageInterpolator();
         if (messageInterpolator != null) {
            Locale locale = this.messageContext.getLocale();
            ret = messageInterpolator.interpolate(template, this.getArguments(), locale);
         }

         return ret;
      }
   }

   private String markAsUnresolved(String template) {
      if (this.messageTemplate.startsWith("{") && this.messageTemplate.endsWith("}")) {
         template = this.messageTemplate.substring(1, this.messageTemplate.length() - 1);
      }

      StringBuilder sb = new StringBuilder("???" + template + "???");
      if (this.getArguments() != null && this.getArguments().length > 0) {
         sb.append(" ").append(Arrays.toString(this.getArguments()));
      }

      return sb.toString();
   }

   public String toString(MessageContext messageContext) {
      return this.toString(messageContext, (String)null);
   }

   public String toString(MessageContext messageContext, String category) {
      return messageContext.message().template(this.getTemplate()).argument(this.getArguments()).toString(category);
   }

   public boolean equals(Object o) {
      if (this == o) {
         return true;
      } else if (!(o instanceof Message)) {
         return false;
      } else {
         Message other = (Message)o;
         if (this.getTemplate() == null && other.getTemplate() != null) {
            return false;
         } else if (this.getTemplate() != null && !this.getTemplate().equals(other.getTemplate())) {
            return false;
         } else {
            if (this.arguments != null) {
               if (!Arrays.equals(this.arguments.toArray(), other.getArguments())) {
                  return false;
               }
            } else if (other.getArguments() != null) {
               return false;
            }

            return true;
         }
      }
   }

   public int hashCode() {
      int result = this.getTemplate().hashCode();
      result = 31 * result + (this.arguments != null ? this.arguments.hashCode() : 0);
      return result;
   }

   public Message argumentArray(Serializable[] arguments) {
      return (Message)(arguments != null ? this.argument((Collection)Arrays.asList(arguments)) : this);
   }

   public Message argument(Collection<Serializable> arguments) {
      if (arguments != null) {
         this.arguments.addAll(arguments);
      }

      return this;
   }
}
