package org.apache.meecrowave.tomcat;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import javax.servlet.ServletContext;
import org.apache.tomcat.Jar;
import org.apache.tomcat.JarScanFilter;
import org.apache.tomcat.JarScanType;
import org.apache.tomcat.JarScanner;
import org.apache.tomcat.JarScannerCallback;
import org.apache.tomcat.util.buf.UriUtil;
import org.apache.tomcat.util.scan.JarFactory;
import org.apache.webbeans.config.WebBeansContext;
import org.apache.webbeans.corespi.scanner.xbean.CdiArchive;
import org.apache.webbeans.web.scanner.WebScannerService;

public class OWBJarScanner implements JarScanner {
   private JarScanFilter filter;

   public void scan(JarScanType jarScanType, ServletContext servletContext, JarScannerCallback callback) {
      switch (jarScanType) {
         case PLUGGABILITY:
            ((CdiArchive)CdiArchive.class.cast(((WebScannerService)WebScannerService.class.cast(WebBeansContext.getInstance().getScannerService())).getFinder().getArchive())).classesByUrl().keySet().stream().filter((u) -> !"jar:file://!/".equals(u)).forEach((u) -> {
               try {
                  URL url = new URL(u);
                  if (!"jar".equals(url.getProtocol()) && !url.getPath().endsWith(".jar")) {
                     if ("file".equals(url.getProtocol())) {
                        File f = new File(url.toURI());
                        if (f.isFile()) {
                           Jar jar = JarFactory.newInstance(UriUtil.buildJarUrl(f));
                           Throwable var5 = null;

                           try {
                              callback.scan(jar, f.getAbsolutePath(), true);
                           } catch (Throwable var31) {
                              var5 = var31;
                              throw var31;
                           } finally {
                              if (jar != null) {
                                 if (var5 != null) {
                                    try {
                                       jar.close();
                                    } catch (Throwable var29) {
                                       var5.addSuppressed(var29);
                                    }
                                 } else {
                                    jar.close();
                                 }
                              }

                           }
                        } else if (f.isDirectory()) {
                           callback.scan(f, f.getAbsolutePath(), true);
                        }
                     }
                  } else {
                     Jar jar = JarFactory.newInstance(url);
                     Throwable var4 = null;

                     try {
                        callback.scan(jar, u, true);
                     } catch (Throwable var32) {
                        var4 = var32;
                        throw var32;
                     } finally {
                        if (jar != null) {
                           if (var4 != null) {
                              try {
                                 jar.close();
                              } catch (Throwable var30) {
                                 var4.addSuppressed(var30);
                              }
                           } else {
                              jar.close();
                           }
                        }

                     }
                  }
               } catch (MalformedURLException var35) {
               } catch (URISyntaxException | IOException ioe) {
                  throw new IllegalArgumentException(ioe);
               }

            });
            return;
         case TLD:
         default:
      }
   }

   public JarScanFilter getJarScanFilter() {
      return this.filter;
   }

   public void setJarScanFilter(JarScanFilter jarScanFilter) {
      this.filter = jarScanFilter;
   }
}
