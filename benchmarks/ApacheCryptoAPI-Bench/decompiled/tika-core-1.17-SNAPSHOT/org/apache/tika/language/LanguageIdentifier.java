package org.apache.tika.language;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

/** @deprecated */
@Deprecated
public class LanguageIdentifier {
   private static final Map<String, LanguageProfile> PROFILES = new HashMap();
   private static final String PROFILE_SUFFIX = ".ngp";
   private static Properties props = new Properties();
   private static String errors = "";
   private static final String PROPERTIES_OVERRIDE_FILE = "tika.language.override.properties";
   private static final String PROPERTIES_FILE = "tika.language.properties";
   private static final String LANGUAGES_KEY = "languages";
   private static final double CERTAINTY_LIMIT = 0.022;
   private final String language;
   private final double distance;

   private static void addProfile(String language) throws Exception {
      try {
         LanguageProfile profile = new LanguageProfile();
         InputStream stream = LanguageIdentifier.class.getResourceAsStream(language + ".ngp");
         Throwable var3 = null;

         try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8));

            for(String line = reader.readLine(); line != null; line = reader.readLine()) {
               if (line.length() > 0 && !line.startsWith("#")) {
                  int space = line.indexOf(32);
                  profile.add(line.substring(0, space), Long.parseLong(line.substring(space + 1)));
               }
            }
         } catch (Throwable var15) {
            var3 = var15;
            throw var15;
         } finally {
            if (stream != null) {
               if (var3 != null) {
                  try {
                     stream.close();
                  } catch (Throwable var14) {
                     var3.addSuppressed(var14);
                  }
               } else {
                  stream.close();
               }
            }

         }

         addProfile(language, profile);
      } catch (Throwable t) {
         throw new Exception("Failed trying to load language profile for language \"" + language + "\". Error: " + t.getMessage());
      }
   }

   public static void addProfile(String language, LanguageProfile profile) {
      PROFILES.put(language, profile);
   }

   public LanguageIdentifier(LanguageProfile profile) {
      String minLanguage = "unknown";
      double minDistance = (double)1.0F;

      for(Map.Entry<String, LanguageProfile> entry : PROFILES.entrySet()) {
         double distance = profile.distance((LanguageProfile)entry.getValue());
         if (distance < minDistance) {
            minDistance = distance;
            minLanguage = (String)entry.getKey();
         }
      }

      this.language = minLanguage;
      this.distance = minDistance;
   }

   public LanguageIdentifier(String content) {
      this(new LanguageProfile(content));
   }

   public String getLanguage() {
      return this.language;
   }

   public boolean isReasonablyCertain() {
      return this.distance < 0.022;
   }

   public static void initProfiles() {
      clearProfiles();
      errors = "";
      InputStream stream = LanguageIdentifier.class.getResourceAsStream("tika.language.override.properties");
      if (stream == null) {
         stream = LanguageIdentifier.class.getResourceAsStream("tika.language.properties");
      }

      if (stream != null) {
         try {
            props = new Properties();
            props.load(stream);
         } catch (IOException e) {
            errors = errors + "IOException while trying to load property file. Message: " + e.getMessage() + "\n";
         }
      }

      String[] languages = props.getProperty("languages").split(",");

      for(String language : languages) {
         language = language.trim();
         String name = props.getProperty("name." + language, "Unknown");

         try {
            addProfile(language);
         } catch (Exception e) {
            errors = errors + "Language " + language + " (" + name + ") not initialized. Message: " + e.getMessage() + "\n";
         }
      }

   }

   public static void initProfiles(Map<String, LanguageProfile> profilesMap) {
      clearProfiles();

      for(Map.Entry<String, LanguageProfile> entry : profilesMap.entrySet()) {
         addProfile((String)entry.getKey(), (LanguageProfile)entry.getValue());
      }

   }

   public static void clearProfiles() {
      PROFILES.clear();
   }

   public static boolean hasErrors() {
      return errors != "";
   }

   public static String getErrors() {
      return errors;
   }

   public static Set<String> getSupportedLanguages() {
      return PROFILES.keySet();
   }

   public String toString() {
      return this.language + " (" + this.distance + ")";
   }

   static {
      initProfiles();
   }
}
