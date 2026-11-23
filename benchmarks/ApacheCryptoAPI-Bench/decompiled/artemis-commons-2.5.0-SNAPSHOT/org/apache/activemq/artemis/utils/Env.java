package org.apache.activemq.artemis.utils;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import sun.misc.Unsafe;

public final class Env {
   private static final int OS_PAGE_SIZE;
   private static boolean testEnv;

   private Env() {
   }

   public static int osPageSize() {
      return OS_PAGE_SIZE;
   }

   public static boolean isTestEnv() {
      return testEnv;
   }

   public static void setTestEnv(boolean testEnv) {
      Env.testEnv = testEnv;
   }

   static {
      int osPageSize = 4096;

      Unsafe instance;
      try {
         Field field = Unsafe.class.getDeclaredField("theUnsafe");
         field.setAccessible(true);
         instance = (Unsafe)field.get((Object)null);
      } catch (Throwable var5) {
         try {
            Constructor<Unsafe> c = Unsafe.class.getDeclaredConstructor();
            c.setAccessible(true);
            instance = (Unsafe)c.newInstance();
         } catch (Throwable var4) {
            instance = null;
         }
      }

      if (instance != null) {
         osPageSize = instance.pageSize();
      }

      OS_PAGE_SIZE = osPageSize;
      testEnv = false;
   }
}
