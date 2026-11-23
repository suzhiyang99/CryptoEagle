package org.apache.openejb.util;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Properties;
import org.apache.openejb.loader.IO;
import org.apache.openejb.loader.SystemInstance;
import org.apache.openejb.observer.Observes;
import org.apache.openejb.observer.event.ObserverAdded;

public class UpdateChecker {
   private static final Logger LOGGER;
   private static final String TOMEE_ARTIFACT = "apache-tomee";
   private String repoUrl = SystemInstance.get().getOptions().get("openejb.version.check.repo.url", "http://repo1.maven.org/maven2/");
   private String groupId = "org/apache/openejb/";
   private String metadata = "/maven-metadata.xml";
   private String checkerProxy = SystemInstance.get().getOptions().get("openejb.version.check.proxy", (String)null);
   private String auto = "auto";
   private String url;
   private String tag;
   private String undefined;
   private String latest;
   private String current;

   public UpdateChecker() {
      this.url = SystemInstance.get().getOptions().get("openejb.version.check.url", this.auto);
      this.tag = "release";
      this.undefined = "undefined";
      this.latest = "undefined";
   }

   public void check(@Observes ObserverAdded event) {
      if (event.getObserver() == this) {
         String originalProxyHost = null;
         String originalProxyPort = null;
         String originalProxyUser = null;
         String originalProxyPwd = null;
         String proxyProtocol = null;
         if (this.checkerProxy != null) {
            try {
               URL proxyUrl = new URL(this.checkerProxy);
               proxyProtocol = proxyUrl.getProtocol();
               originalProxyHost = JavaSecurityManagers.getSystemProperty(proxyProtocol + ".proxyHost");
               originalProxyPort = JavaSecurityManagers.getSystemProperty(proxyProtocol + ".proxyPort");
               originalProxyUser = JavaSecurityManagers.getSystemProperty(proxyProtocol + ".proxyUser");
               originalProxyPwd = JavaSecurityManagers.getSystemProperty(proxyProtocol + ".proxyPassword");
               JavaSecurityManagers.setSystemProperty(proxyProtocol + ".proxyHost", proxyUrl.getHost());
               JavaSecurityManagers.setSystemProperty(proxyProtocol + ".proxyPort", Integer.toString(proxyUrl.getPort()));
               String userInfo = proxyUrl.getUserInfo();
               if (userInfo != null) {
                  int sep = userInfo.indexOf(":");
                  if (sep >= 0) {
                     JavaSecurityManagers.setSystemProperty(proxyProtocol + ".proxyUser", userInfo.substring(0, sep));
                     JavaSecurityManagers.setSystemProperty(proxyProtocol + ".proxyPassword", userInfo.substring(sep + 1));
                  } else {
                     JavaSecurityManagers.setSystemProperty(proxyProtocol + ".proxyUser", userInfo);
                  }
               }
            } catch (MalformedURLException var15) {
               this.checkerProxy = null;
            }
         }

         String realUrl = this.url;
         if ("auto".equals(realUrl)) {
            realUrl = this.repoUrl + this.groupId + artifact() + this.metadata;
         }

         try {
            URL url = new URL(realUrl);
            String metaData = IO.slurp(url);
            this.latest = this.extractLatest(metaData);
            if (!this.usesLatest()) {
               LOGGER.warning(this.message());
            }
         } catch (Exception e) {
            LOGGER.warning("can't check the version: " + e.getMessage());
         } finally {
            if (proxyProtocol != null) {
               resetSystemProp(proxyProtocol + ".proxyHost", originalProxyHost);
               resetSystemProp(proxyProtocol + ".proxyPort", originalProxyPort);
               resetSystemProp(proxyProtocol + ".proxyUser", originalProxyUser);
               resetSystemProp(proxyProtocol + ".proxyPassword", originalProxyPwd);
            }

         }

      }
   }

   private static String artifact() {
      try {
         UpdateChecker.class.getClassLoader().loadClass("org.apache.tomee.catalina.TomcatWebAppBuilder");
         return "apache-tomee";
      } catch (ClassNotFoundException var1) {
         return "openejb";
      }
   }

   private static void resetSystemProp(String key, String value) {
      if (value == null) {
         System.clearProperty(key);
      } else {
         JavaSecurityManagers.setSystemProperty(key, value);
      }

   }

   private String extractLatest(String metaData) {
      if (metaData != null) {
         boolean found = false;

         for(String s : metaData.replace(">", ">\n").split("\n")) {
            if (found) {
               return this.trim(s).replace("</" + this.tag + ">", "");
            }

            if (!s.isEmpty() && this.trim(s).endsWith("<" + this.tag + ">")) {
               found = true;
            }
         }
      }

      return this.undefined;
   }

   private String trim(String s) {
      return s.replace("\t", "").replace(" ", "");
   }

   public boolean usesLatest() {
      if (artifact().contains("apache-tomee")) {
         try {
            InputStream is = this.getClass().getClassLoader().getResourceAsStream("META-INF/maven/org.apache.openejb/tomee-catalina/pom.properties");
            Throwable var2 = null;

            try {
               if (is != null) {
                  Properties prop = new Properties();
                  prop.load(is);
                  this.current = prop.getProperty("version");
               }
            } catch (Throwable var12) {
               var2 = var12;
               throw var12;
            } finally {
               if (is != null) {
                  if (var2 != null) {
                     try {
                        is.close();
                     } catch (Throwable var11) {
                        var2.addSuppressed(var11);
                     }
                  } else {
                     is.close();
                  }
               }

            }
         } catch (IOException var14) {
            LOGGER.error("can't get tomee version, will use openejb one");
         }
      }

      if (this.current == null) {
         this.current = OpenEjbVersion.get().getVersion();
      }

      return this.current.equals(this.latest);
   }

   public String message() {
      if (this.undefined.equals(this.latest)) {
         return "can't determine the latest version";
      } else {
         return this.current.equals(this.latest) ? "running on the latest version" : "you are using the version " + this.current + ", our latest stable version " + this.latest + " is available on " + this.repoUrl;
      }
   }

   public void setRepoUrl(String repoUrl) {
      this.repoUrl = repoUrl;
   }

   public void setGroupId(String groupId) {
      this.groupId = groupId;
   }

   public void setMetadata(String metadata) {
      this.metadata = metadata;
   }

   public void setCheckerProxy(String checkerProxy) {
      this.checkerProxy = checkerProxy;
   }

   public void setAuto(String auto) {
      this.auto = auto;
   }

   public void setUrl(String url) {
      this.url = url;
   }

   public void setTag(String tag) {
      this.tag = tag;
   }

   public void setUndefined(String undefined) {
      this.undefined = undefined;
   }

   public void setLatest(String latest) {
      this.latest = latest;
   }

   static {
      LOGGER = Logger.getInstance(LogCategory.OPENEJB_STARTUP, UpdateChecker.class);
   }
}
