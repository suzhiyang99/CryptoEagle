package org.apache.wicket.util.resource;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.wicket.util.string.Strings;

public class ResourceUtils {
   public static final String MIN_POSTFIX_DEFAULT = "min";
   public static final String MIN_POSTFIX_DEFAULT_AS_EXTENSION = ".min.";
   private static final Pattern LOCALE_MIN_PATTERN = Pattern.compile("_([a-z]{2})(_([A-Z]{2})(_([^_\\.]+))?)?(\\.min)?$");
   private static final Set<String> isoCountries = new HashSet(Arrays.asList(Locale.getISOCountries()));
   private static final Set<String> isoLanguages = new HashSet(Arrays.asList(Locale.getISOLanguages()));

   public static String getMinifiedName(String name, String minPostfix) {
      int idxOfExtension = name.lastIndexOf(46);
      String dottedPostfix = "." + minPostfix;
      String minifiedName;
      if (idxOfExtension > -1) {
         String extension = name.substring(idxOfExtension);
         String baseName = name.substring(0, name.length() - extension.length() + 1);
         if (!dottedPostfix.equals(extension) && !baseName.endsWith(dottedPostfix + ".")) {
            minifiedName = baseName + minPostfix + extension;
         } else {
            minifiedName = name;
         }
      } else {
         minifiedName = name + dottedPostfix;
      }

      return minifiedName;
   }

   public static PathLocale getLocaleFromFilename(String path) {
      String extension = "";
      int pos = path.lastIndexOf(46);
      if (pos != -1) {
         extension = path.substring(pos);
         path = path.substring(0, pos);
      }

      String filename = Strings.lastPathComponent(path, '/');
      Matcher matcher = LOCALE_MIN_PATTERN.matcher(filename);
      if (matcher.find()) {
         String language = matcher.group(1);
         String country = matcher.group(3);
         String variant = matcher.group(5);
         String min = matcher.group(6);
         if (language != null && !isoLanguages.contains(language)) {
            language = null;
            country = null;
            variant = null;
         }

         if (language != null && country != null && !isoCountries.contains(country)) {
            country = null;
            variant = null;
         }

         if (language != null) {
            int languagePos = path.length() - filename.length() + matcher.start();
            String basePath = path.substring(0, languagePos) + (min == null ? "" : min) + extension;
            Locale locale = new Locale(language, country != null ? country : "", variant != null ? variant : "");
            return new PathLocale(basePath, locale);
         }
      }

      return new PathLocale(path + extension, (Locale)null);
   }

   public static class PathLocale {
      public final String path;
      public final Locale locale;

      public PathLocale(String path, Locale locale) {
         this.path = path;
         this.locale = locale;
      }
   }
}
