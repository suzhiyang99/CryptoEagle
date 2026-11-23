package org.apache.openejb.util;

import java.io.IOException;
import java.io.PrintStream;
import java.net.URL;
import java.util.Properties;
import org.apache.xbean.finder.ResourceFinder;

public final class OpenEjbVersion {
   private final String copyright;
   private final String url;
   private final String version;
   private final String date;
   private final String time;
   private static OpenEjbVersion openEjbVersion;

   private OpenEjbVersion() {
      Properties info = new Properties();

      try {
         ResourceFinder finder = new ResourceFinder(new URL[0]);
         info = finder.findProperties("openejb-version.properties");
      } catch (IOException e) {
         e.printStackTrace();
      }

      this.copyright = info.getProperty("copyright");
      this.url = info.getProperty("url");
      this.version = info.getProperty("version");
      this.date = info.getProperty("date");
      this.time = info.getProperty("time");
      JavaSecurityManagers.setSystemProperty("openejb.version", this.version);
      JavaSecurityManagers.setSystemProperty("tomee.version", this.version);
   }

   public static OpenEjbVersion get() {
      if (openEjbVersion == null) {
         openEjbVersion = new OpenEjbVersion();
      }

      return openEjbVersion;
   }

   public String getCopyright() {
      return this.copyright;
   }

   public String getDate() {
      return this.date;
   }

   public String getTime() {
      return this.time;
   }

   public String getUrl() {
      return this.url;
   }

   public String getVersion() {
      return this.version;
   }

   public void print(PrintStream out) {
      out.println("Apache OpenEJB " + this.getVersion() + "    build: " + this.getDate() + "-" + this.getTime());
      out.println(this.getUrl());
   }
}
