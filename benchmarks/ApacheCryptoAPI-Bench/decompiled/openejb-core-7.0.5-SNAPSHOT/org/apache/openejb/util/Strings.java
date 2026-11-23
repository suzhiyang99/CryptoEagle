package org.apache.openejb.util;

public class Strings {
   public static String lc(String string) {
      return lowercase(string);
   }

   public static String lowercase(String string) {
      if (string == null) {
         return null;
      } else {
         StringBuilder sb = new StringBuilder(string);

         for(int i = 0; i < sb.length(); ++i) {
            sb.setCharAt(i, Character.toLowerCase(sb.charAt(i)));
         }

         return sb.toString();
      }
   }

   public static String uc(String string) {
      return uppercase(string);
   }

   public static String uppercase(String string) {
      if (string == null) {
         return null;
      } else {
         StringBuilder sb = new StringBuilder(string);

         for(int i = 0; i < sb.length(); ++i) {
            sb.setCharAt(i, Character.toUpperCase(sb.charAt(i)));
         }

         return sb.toString();
      }
   }

   public static String ucfirst(String string) {
      if (string == null) {
         return null;
      } else {
         StringBuilder sb = new StringBuilder(string);
         if (sb.length() > 0) {
            sb.setCharAt(0, Character.toUpperCase(sb.charAt(0)));
         }

         return sb.toString();
      }
   }

   public static String lcfirst(String string) {
      if (string == null) {
         return null;
      } else {
         StringBuilder sb = new StringBuilder(string);
         if (sb.length() > 0) {
            sb.setCharAt(0, Character.toLowerCase(sb.charAt(0)));
         }

         return sb.toString();
      }
   }

   public static String camelCase(String string) {
      return camelCase(string, "-");
   }

   public static String camelCase(String string, String delimiter) {
      StringBuilder sb = new StringBuilder();
      String[] strings = string.split(delimiter);

      for(String s : strings) {
         int l = sb.length();
         sb.append(s);
         sb.setCharAt(l, Character.toUpperCase(sb.charAt(l)));
      }

      return sb.toString();
   }

   public static boolean checkNullBlankString(String stringToCheck) {
      return stringToCheck == null || "".equals(stringToCheck.trim());
   }

   public static String replaceNullOrBlankStringWithNonBreakingSpace(String stringToCheckForNull) {
      return stringToCheckForNull != null && !stringToCheckForNull.equals("") ? stringToCheckForNull : "&nbsp;";
   }

   public static String lastPart(String className, char sep) {
      int idx = className.lastIndexOf(sep);
      return idx >= 0 ? className.substring(idx + 1, className.length()) : className;
   }
}
