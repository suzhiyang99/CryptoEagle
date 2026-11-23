package org.apache.openejb.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import org.apache.openejb.OpenEJBRuntimeException;
import org.apache.xbean.recipe.ObjectRecipe;

public final class ListConfigurator {
   private ListConfigurator() {
   }

   public static <T> List<T> getList(Properties properties, String key, ClassLoader classloader, Class<T> filter) {
      if (properties == null) {
         return null;
      } else {
         String features = properties.getProperty(key);
         if (features == null) {
            return null;
         } else {
            List<T> list = new ArrayList();
            String[] split = features.trim().split(",");

            for(String feature : split) {
               if (feature != null && !feature.trim().isEmpty()) {
                  String prefix = key + "." + feature + ".";
                  ObjectRecipe recipe = new ObjectRecipe(feature);

                  for(Map.Entry<Object, Object> entry : properties.entrySet()) {
                     String current = entry.getKey().toString();
                     if (current.startsWith(prefix)) {
                        String property = current.substring(prefix.length());
                        recipe.setProperty(property, entry.getValue());
                     }
                  }

                  Object instance = recipe.create(classloader);
                  if (!filter.isInstance(instance)) {
                     throw new OpenEJBRuntimeException(feature + " is not an abstract feature");
                  }

                  list.add(filter.cast(instance));
               }
            }

            if (list.isEmpty()) {
               return null;
            } else {
               return list;
            }
         }
      }
   }
}
