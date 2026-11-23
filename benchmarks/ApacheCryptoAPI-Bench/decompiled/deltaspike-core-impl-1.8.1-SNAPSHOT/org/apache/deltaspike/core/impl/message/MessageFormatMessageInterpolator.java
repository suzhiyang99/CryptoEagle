package org.apache.deltaspike.core.impl.message;

import java.io.Serializable;
import java.text.MessageFormat;
import java.util.Locale;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Alternative;
import org.apache.deltaspike.core.api.message.MessageInterpolator;

@ApplicationScoped
@Alternative
public class MessageFormatMessageInterpolator implements MessageInterpolator, Serializable {
   private static final long serialVersionUID = -8854087197813424812L;

   public String interpolate(String messageTemplate, Serializable[] arguments, Locale locale) {
      MessageFormat messageFormat = new MessageFormat(messageTemplate, locale);
      return messageFormat.format(arguments);
   }
}
