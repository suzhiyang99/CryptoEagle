package org.apache.deltaspike.core.impl.message;

import java.io.Serializable;
import java.util.Locale;
import javax.enterprise.context.ApplicationScoped;
import org.apache.deltaspike.core.api.message.MessageInterpolator;

@ApplicationScoped
public class DefaultMessageInterpolator implements MessageInterpolator, Serializable {
   private static final long serialVersionUID = -8854087197813424812L;

   public String interpolate(String messageTemplate, Serializable[] arguments, Locale locale) {
      return arguments != null && arguments.length != 0 ? String.format(locale, messageTemplate, arguments) : messageTemplate;
   }
}
