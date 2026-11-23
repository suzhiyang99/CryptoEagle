package org.apache.wicket.util.lang;

import org.apache.wicket.util.string.Strings;

public class Checks {
   public static void notNull(Object argument, String message, Object... params) {
      if (argument == null) {
         throw new IllegalStateException(Args.format(message, params));
      }
   }

   public static void notEmpty(String argument, String message, Object... params) {
      if (Strings.isEmpty(argument)) {
         throw new IllegalStateException(Args.format(message, params));
      }
   }

   public static <T extends Comparable<? super T>> void withinRange(T min, T max, T value, String message) {
      notNull(min, message);
      notNull(max, message);
      if (value.compareTo(min) < 0 || value.compareTo(max) > 0) {
         throw new IllegalStateException(message);
      }
   }

   public static void notNullShort(Object argument, String name) {
      notNull(argument, name + " may not be null.");
   }

   public static void notEmptyShort(String argument, String name) {
      notEmpty(argument, name + " may not be null or empty string.");
   }

   public static <T extends Comparable<? super T>> void withinRangeShort(T min, T max, T value, String name) {
      withinRange(min, max, value, String.format("%s must have a value within [%s,%s], but was %s", name, min, max, value));
   }
}
