package org.apache.openejb.util;

import javax.naming.Context;
import javax.naming.NamingException;

public final class Contexts {
   private Contexts() {
   }

   public static void unbindLeaf(Context context, String path) {
      if (path != null) {
         String[] parts = path.split("/");
         String name = parts[parts.length - 1];
         if (parts.length > 1 && parts[0].startsWith("java:")) {
            parts[0] = parts[0].substring("java:".length());
         }

         Context lastContext = context;

         for(int i = 0; i < parts.length - 1; ++i) {
            try {
               lastContext = (Context)lastContext.lookup(parts[i]);
            } catch (NamingException var8) {
               return;
            }
         }

         try {
            lastContext.unbind(name);
         } catch (NamingException var7) {
         }

      }
   }

   public static Context createSubcontexts(Context context, String key) {
      String[] parts = key.split("/");
      int i = 0;
      Context lastContext = context;

      for(String part : parts) {
         ++i;
         if (i == parts.length) {
            return lastContext;
         }

         try {
            lastContext = lastContext.createSubcontext(part);
         } catch (NamingException var12) {
            try {
               lastContext = (Context)lastContext.lookup(part);
            } catch (NamingException var11) {
               return lastContext;
            }
         }
      }

      return lastContext;
   }
}
