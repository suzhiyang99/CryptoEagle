package org.apache.spark.deploy;

import java.net.URI;
import java.net.URISyntaxException;
import org.apache.spark.util.Utils$;

public final class ClientArguments$ {
   public static final ClientArguments$ MODULE$;
   private final int DEFAULT_CORES;
   private final int DEFAULT_MEMORY;
   private final boolean DEFAULT_SUPERVISE;

   static {
      new ClientArguments$();
   }

   public int DEFAULT_CORES() {
      return this.DEFAULT_CORES;
   }

   public int DEFAULT_MEMORY() {
      return this.DEFAULT_MEMORY;
   }

   public boolean DEFAULT_SUPERVISE() {
      return this.DEFAULT_SUPERVISE;
   }

   public boolean isValidJarUrl(String s) {
      boolean var10000;
      try {
         URI uri = new URI(s);
         var10000 = uri.getScheme() != null && uri.getPath() != null && uri.getPath().endsWith(".jar");
      } catch (URISyntaxException var3) {
         var10000 = false;
      }

      return var10000;
   }

   private ClientArguments$() {
      MODULE$ = this;
      this.DEFAULT_CORES = 1;
      this.DEFAULT_MEMORY = Utils$.MODULE$.DEFAULT_DRIVER_MEM_MB();
      this.DEFAULT_SUPERVISE = false;
   }
}
