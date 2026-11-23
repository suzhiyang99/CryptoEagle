package org.apache.spark.util;

import org.apache.spark.annotation.Private;
import org.spark_project.guava.base.Joiner;

@Private
public class EnumUtil {
   public static <E extends Enum<E>> E parseIgnoreCase(Class<E> clz, String str) {
      E[] constants = (E[])(clz.getEnumConstants());
      if (str == null) {
         return null;
      } else {
         for(E e : constants) {
            if (e.name().equalsIgnoreCase(str)) {
               return e;
            }
         }

         throw new IllegalArgumentException(String.format("Illegal type='%s'. Supported type values: %s", str, Joiner.on(", ").join(constants)));
      }
   }
}
