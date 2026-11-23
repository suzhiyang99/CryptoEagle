package org.apache.spark.internal;

import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.apache.spark.util.Utils$;
import org.slf4j.impl.StaticLoggerBinder;
import scala.runtime.BoxedUnit;
import scala.runtime.BoxesRunTime;

public final class Logging$ {
   public static final Logging$ MODULE$;
   private volatile boolean org$apache$spark$internal$Logging$$initialized;
   private volatile Level org$apache$spark$internal$Logging$$defaultRootLevel;
   private volatile boolean org$apache$spark$internal$Logging$$defaultSparkLog4jConfig;
   private final Object initLock;

   static {
      new Logging$();
   }

   public boolean org$apache$spark$internal$Logging$$initialized() {
      return this.org$apache$spark$internal$Logging$$initialized;
   }

   public void org$apache$spark$internal$Logging$$initialized_$eq(boolean x$1) {
      this.org$apache$spark$internal$Logging$$initialized = x$1;
   }

   public Level org$apache$spark$internal$Logging$$defaultRootLevel() {
      return this.org$apache$spark$internal$Logging$$defaultRootLevel;
   }

   public void org$apache$spark$internal$Logging$$defaultRootLevel_$eq(Level x$1) {
      this.org$apache$spark$internal$Logging$$defaultRootLevel = x$1;
   }

   private boolean org$apache$spark$internal$Logging$$defaultSparkLog4jConfig() {
      return this.org$apache$spark$internal$Logging$$defaultSparkLog4jConfig;
   }

   public void org$apache$spark$internal$Logging$$defaultSparkLog4jConfig_$eq(boolean x$1) {
      this.org$apache$spark$internal$Logging$$defaultSparkLog4jConfig = x$1;
   }

   public Object initLock() {
      return this.initLock;
   }

   public void uninitialize() {
      synchronized(this.initLock()) {
         if (this.org$apache$spark$internal$Logging$$isLog4j12()) {
            if (this.org$apache$spark$internal$Logging$$defaultSparkLog4jConfig()) {
               this.org$apache$spark$internal$Logging$$defaultSparkLog4jConfig_$eq(false);
               LogManager.resetConfiguration();
            } else {
               LogManager.getRootLogger().setLevel(this.org$apache$spark$internal$Logging$$defaultRootLevel());
            }
         }

         this.org$apache$spark$internal$Logging$$initialized_$eq(false);
         BoxedUnit var10000 = BoxedUnit.UNIT;
      }
   }

   public boolean org$apache$spark$internal$Logging$$isLog4j12() {
      String binderClass = StaticLoggerBinder.getSingleton().getLoggerFactoryClassStr();
      return "org.slf4j.impl.Log4jLoggerFactory".equals(binderClass);
   }

   private Logging$() {
      MODULE$ = this;
      this.org$apache$spark$internal$Logging$$initialized = false;
      this.org$apache$spark$internal$Logging$$defaultRootLevel = null;
      this.org$apache$spark$internal$Logging$$defaultSparkLog4jConfig = false;
      this.initLock = new Object();

      try {
         Class bridgeClass = Utils$.MODULE$.classForName("org.slf4j.bridge.SLF4JBridgeHandler");
         bridgeClass.getMethod("removeHandlersForRootLogger").invoke((Object)null);
         boolean installed = BoxesRunTime.unboxToBoolean(bridgeClass.getMethod("isInstalled").invoke((Object)null));
         if (installed) {
            BoxedUnit var5 = BoxedUnit.UNIT;
         } else {
            bridgeClass.getMethod("install").invoke((Object)null);
         }
      } catch (ClassNotFoundException var4) {
         BoxedUnit var10000 = BoxedUnit.UNIT;
      }

   }
}
