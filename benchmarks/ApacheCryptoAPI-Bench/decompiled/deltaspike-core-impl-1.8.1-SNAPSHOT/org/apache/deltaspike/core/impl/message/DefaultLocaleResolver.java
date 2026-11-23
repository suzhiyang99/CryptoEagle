package org.apache.deltaspike.core.impl.message;

import java.io.Serializable;
import java.util.Locale;
import javax.enterprise.context.Dependent;
import org.apache.deltaspike.core.api.message.LocaleResolver;

@Dependent
public class DefaultLocaleResolver implements LocaleResolver, Serializable {
   private static final long serialVersionUID = 2075618472090834156L;

   public Locale getLocale() {
      return Locale.getDefault();
   }
}
