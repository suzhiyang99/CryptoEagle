package org.apache.tika.language.translate;

import java.io.IOException;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import org.apache.tika.config.ServiceLoader;
import org.apache.tika.exception.TikaException;

public class DefaultTranslator implements Translator {
   private final transient ServiceLoader loader;

   public DefaultTranslator(ServiceLoader loader) {
      this.loader = loader;
   }

   public DefaultTranslator() {
      this(new ServiceLoader());
   }

   private static List<Translator> getDefaultTranslators(ServiceLoader loader) {
      List<Translator> translators = loader.<Translator>loadStaticServiceProviders(Translator.class);
      Collections.sort(translators, new Comparator<Translator>() {
         public int compare(Translator t1, Translator t2) {
            String n1 = t1.getClass().getName();
            String n2 = t2.getClass().getName();
            boolean tika1 = n1.startsWith("org.apache.tika.");
            boolean tika2 = n2.startsWith("org.apache.tika.");
            if (tika1 == tika2) {
               return n1.compareTo(n2);
            } else {
               return tika1 ? -1 : 1;
            }
         }
      });
      return translators;
   }

   private static Translator getFirstAvailable(ServiceLoader loader) {
      for(Translator t : getDefaultTranslators(loader)) {
         if (t.isAvailable()) {
            return t;
         }
      }

      return null;
   }

   public String translate(String text, String sourceLanguage, String targetLanguage) throws TikaException, IOException {
      Translator t = getFirstAvailable(this.loader);
      if (t != null) {
         return t.translate(text, sourceLanguage, targetLanguage);
      } else {
         throw new TikaException("No translators currently available");
      }
   }

   public String translate(String text, String targetLanguage) throws TikaException, IOException {
      Translator t = getFirstAvailable(this.loader);
      if (t != null) {
         return t.translate(text, targetLanguage);
      } else {
         throw new TikaException("No translators currently available");
      }
   }

   public List<Translator> getTranslators() {
      return getDefaultTranslators(this.loader);
   }

   public Translator getTranslator() {
      return getFirstAvailable(this.loader);
   }

   public boolean isAvailable() {
      return getFirstAvailable(this.loader) != null;
   }
}
