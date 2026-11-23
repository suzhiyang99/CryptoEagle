package org.apache.manifoldcf.core.interfaces;

public class CacheKeyFactory {
   public static final String _rcsid = "@(#)$Id: CacheKeyFactory.java 988245 2010-08-23 18:39:35Z kwright $";
   private static final String DATABASE = "DB-";
   private static final String DASH = "-";
   private static final String TABLE = "TBL-";

   protected CacheKeyFactory() {
   }

   public static String makeDatabaseKey(String keyName, String databaseName) {
      String rval;
      if (keyName == null) {
         rval = "DB-" + databaseName;
      } else {
         rval = "DB-" + databaseName + "-" + keyName;
      }

      return rval;
   }

   public static String makeTableKey(String keyName, String tableName, String databaseName) {
      String rval;
      if (keyName == null) {
         rval = "TBL-" + tableName;
      } else {
         rval = "TBL-" + tableName + "-" + keyName;
      }

      return makeDatabaseKey(rval, databaseName);
   }

   public static String makeDatabaseKey(String databaseName) {
      return makeDatabaseKey("GENERAL", databaseName);
   }
}
