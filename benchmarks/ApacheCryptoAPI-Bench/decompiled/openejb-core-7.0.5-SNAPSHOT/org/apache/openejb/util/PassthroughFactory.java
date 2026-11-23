package org.apache.openejb.util;

import org.apache.xbean.recipe.ObjectRecipe;

public class PassthroughFactory {
   public static ObjectRecipe recipe(Object instance) {
      ObjectRecipe recipe = new ObjectRecipe(Create.class);
      recipe.setFactoryMethod("create");
      String param = "instance" + recipe.hashCode();
      recipe.setConstructorArgNames(new String[]{param});
      recipe.setProperty(param, instance);
      return recipe;
   }

   public static class Create {
      public static Object create(Object instance) {
         return instance;
      }
   }
}
