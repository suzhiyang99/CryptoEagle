package org.apache.tika.language.detect;

import java.io.IOException;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.tika.config.ServiceLoader;

public abstract class LanguageDetector {
   private static final ServiceLoader DEFAULT_SERVICE_LOADER = new ServiceLoader();
   protected boolean mixedLanguages = false;
   protected boolean shortText = false;

   public static LanguageDetector getDefaultLanguageDetector() {
      List<LanguageDetector> detectors = getLanguageDetectors();
      if (detectors.isEmpty()) {
         throw new IllegalStateException("No language detectors available");
      } else {
         return (LanguageDetector)detectors.get(0);
      }
   }

   public static List<LanguageDetector> getLanguageDetectors() {
      return getLanguageDetectors(DEFAULT_SERVICE_LOADER);
   }

   public static List<LanguageDetector> getLanguageDetectors(ServiceLoader loader) {
      List<LanguageDetector> detectors = loader.<LanguageDetector>loadStaticServiceProviders(LanguageDetector.class);
      Collections.sort(detectors, new Comparator<LanguageDetector>() {
         public int compare(LanguageDetector d1, LanguageDetector d2) {
            String n1 = d1.getClass().getName();
            String n2 = d2.getClass().getName();
            boolean tika1 = n1.startsWith("org.apache.tika.");
            boolean tika2 = n2.startsWith("org.apache.tika.");
            if (tika1 == tika2) {
               return n1.compareTo(n2);
            } else {
               return tika1 ? -1 : 1;
            }
         }
      });
      return detectors;
   }

   public boolean isMixedLanguages() {
      return this.mixedLanguages;
   }

   public LanguageDetector setMixedLanguages(boolean mixedLanguages) {
      this.mixedLanguages = mixedLanguages;
      return this;
   }

   public boolean isShortText() {
      return this.shortText;
   }

   public LanguageDetector setShortText(boolean shortText) {
      this.shortText = shortText;
      return this;
   }

   public abstract LanguageDetector loadModels() throws IOException;

   public abstract LanguageDetector loadModels(Set<String> var1) throws IOException;

   public abstract boolean hasModel(String var1);

   public abstract LanguageDetector setPriors(Map<String, Float> var1) throws IOException;

   public abstract void reset();

   public abstract void addText(char[] var1, int var2, int var3);

   public void addText(CharSequence text) {
      char[] chars = text.toString().toCharArray();
      this.addText(chars, 0, chars.length);
   }

   public boolean hasEnoughText() {
      return false;
   }

   public abstract List<LanguageResult> detectAll();

   public LanguageResult detect() {
      List<LanguageResult> results = this.detectAll();
      return (LanguageResult)results.get(0);
   }

   public List<LanguageResult> detectAll(String text) {
      this.reset();
      this.addText(text);
      return this.detectAll();
   }

   public LanguageResult detect(CharSequence text) {
      this.reset();
      this.addText(text);
      return this.detect();
   }
}
