package org.apache.tika.language.translate;

public class EmptyTranslator implements Translator {
   public String translate(String text, String sourceLanguage, String targetLanguage) {
      return null;
   }

   public String translate(String text, String targetLanguage) {
      return null;
   }

   public boolean isAvailable() {
      return true;
   }
}
