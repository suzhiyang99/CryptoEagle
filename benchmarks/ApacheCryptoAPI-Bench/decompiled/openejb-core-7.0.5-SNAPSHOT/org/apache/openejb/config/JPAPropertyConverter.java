package org.apache.openejb.config;

import java.util.Properties;
import org.apache.openejb.loader.SystemInstance;

public final class JPAPropertyConverter {
   private JPAPropertyConverter() {
   }

   public static Pair toOpenJPAValue(String key, String value, Properties properties) {
      if (!SystemInstance.get().getOptions().get("openejb.convert-jpa-properties", false)) {
         return null;
      } else {
         if (key.startsWith("eclipselink.ddl-generation") && !properties.containsKey("openjpa.jdbc.SchemaFactory")) {
            if ("create-tables".equals(value)) {
               return new Pair("openjpa.jdbc.SynchronizeMappings", "buildSchema(ForeignKeys=true)");
            }

            if ("drop-and-create-tables".equals("value")) {
               return new Pair("openjpa.jdbc.SynchronizeMappings", "buildSchema(SchemaAction='add,deleteTableContents')");
            }
         }

         return null;
      }
   }

   public static class Pair {
      private final String key;
      private final String value;

      public Pair(String key, String value) {
         this.key = key;
         this.value = value;
      }

      public String getKey() {
         return this.key;
      }

      public String getValue() {
         return this.value;
      }

      public String toString() {
         return this.key + '=' + this.value;
      }
   }
}
