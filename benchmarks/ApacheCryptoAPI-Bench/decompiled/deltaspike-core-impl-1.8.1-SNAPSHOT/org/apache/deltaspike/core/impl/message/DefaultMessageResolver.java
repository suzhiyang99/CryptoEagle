package org.apache.deltaspike.core.impl.message;

import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import javax.enterprise.context.Dependent;
import org.apache.deltaspike.core.api.message.MessageContext;
import org.apache.deltaspike.core.api.message.MessageResolver;
import org.apache.deltaspike.core.util.PropertyFileUtils;

@Dependent
public class DefaultMessageResolver implements MessageResolver {
   private static final long serialVersionUID = 5834411208472341006L;

   public String getMessage(MessageContext messageContext, String messageTemplate, String category) {
      if (messageTemplate.startsWith("{{")) {
         return messageTemplate.substring(1);
      } else {
         if (messageTemplate.startsWith("{") && messageTemplate.endsWith("}")) {
            String resourceKey = messageTemplate.substring(1, messageTemplate.length() - 1);
            List<String> messageSources = this.getMessageSources(messageContext);
            if (messageSources == null || messageSources.isEmpty()) {
               return null;
            }

            Iterator<String> messageSourceIterator = messageSources.iterator();
            Locale locale = messageContext.getLocale();

            while(messageSourceIterator.hasNext()) {
               String currentMessageSource = (String)messageSourceIterator.next();

               try {
                  ResourceBundle messageBundle = PropertyFileUtils.getResourceBundle(currentMessageSource, locale);
                  if (category != null && category.length() > 0) {
                     try {
                        return messageBundle.getString(resourceKey + "_" + category);
                     } catch (MissingResourceException var11) {
                        return messageBundle.getString(resourceKey);
                     }
                  }

                  return messageBundle.getString(resourceKey);
               } catch (MissingResourceException var12) {
                  if (!messageSourceIterator.hasNext()) {
                     return null;
                  }
               }
            }
         }

         return messageTemplate;
      }
   }

   protected List<String> getMessageSources(MessageContext messageContext) {
      return messageContext.getMessageSources();
   }
}
