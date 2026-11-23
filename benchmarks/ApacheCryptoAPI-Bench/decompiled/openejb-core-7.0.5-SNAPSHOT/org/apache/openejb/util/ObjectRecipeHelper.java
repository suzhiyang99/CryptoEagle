package org.apache.openejb.util;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import org.apache.openejb.loader.SystemInstance;
import org.apache.xbean.recipe.ObjectRecipe;
import org.apache.xbean.recipe.Option;

public final class ObjectRecipeHelper {
   private ObjectRecipeHelper() {
   }

   public static Object createMeFromSystemProps(String prefix, String suffix, Class<?> clazz) {
      Properties props = SystemInstance.get().getProperties();
      Map<String, Object> usedOnes = new HashMap();

      for(Map.Entry<Object, Object> entry : props.entrySet()) {
         String key = entry.getKey().toString();
         if ((prefix == null || key.startsWith(prefix)) && (suffix == null || key.endsWith(suffix))) {
            String newKey = key;
            if (prefix != null) {
               newKey = key.substring(prefix.length());
            }

            if (suffix != null) {
               newKey = newKey.substring(0, newKey.length() - suffix.length());
            }

            usedOnes.put(newKey, entry.getValue());
         }
      }

      ObjectRecipe recipe = new ObjectRecipe(clazz);
      recipe.allow(Option.CASE_INSENSITIVE_PROPERTIES);
      recipe.allow(Option.IGNORE_MISSING_PROPERTIES);
      recipe.allow(Option.PRIVATE_PROPERTIES);
      recipe.allow(Option.FIELD_INJECTION);
      recipe.allow(Option.NAMED_PARAMETERS);
      recipe.setAllProperties(usedOnes);
      return recipe.create();
   }
}
