package org.apache.tika.language.detect;

import java.util.Locale;

public class LanguageResult {
   public static final LanguageResult NULL;
   private String language;
   private LanguageConfidence confidence;
   private float rawScore;

   public LanguageResult(String language, LanguageConfidence confidence, float rawScore) {
      this.language = language;
      this.confidence = confidence;
      this.rawScore = rawScore;
   }

   public String getLanguage() {
      return this.language;
   }

   public float getRawScore() {
      return this.rawScore;
   }

   public LanguageConfidence getConfidence() {
      return this.confidence;
   }

   public boolean isReasonablyCertain() {
      return this.confidence == LanguageConfidence.HIGH;
   }

   public boolean isUnknown() {
      return this.confidence == LanguageConfidence.NONE;
   }

   public boolean isLanguage(String language) {
      String[] targetLanguage = language.split("\\-");
      String[] resultLanguage = this.language.split("\\-");
      int minLength = Math.min(targetLanguage.length, resultLanguage.length);

      for(int i = 0; i < minLength; ++i) {
         if (!targetLanguage[i].equalsIgnoreCase(resultLanguage[i])) {
            return false;
         }
      }

      return true;
   }

   public String toString() {
      return String.format(Locale.US, "%s: %s (%f)", this.language, this.confidence, this.rawScore);
   }

   static {
      NULL = new LanguageResult("", LanguageConfidence.NONE, 0.0F);
   }
}
