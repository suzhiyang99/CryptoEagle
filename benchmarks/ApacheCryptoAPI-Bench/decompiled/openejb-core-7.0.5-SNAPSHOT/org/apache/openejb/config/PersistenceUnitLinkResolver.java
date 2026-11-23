package org.apache.openejb.config;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import org.apache.openejb.jee.jpa.unit.Persistence;
import org.apache.openejb.jee.jpa.unit.PersistenceUnit;
import org.apache.openejb.util.LogCategory;
import org.apache.openejb.util.Logger;
import org.apache.openejb.util.URLs;
import org.apache.openejb.util.UniqueDefaultLinkResolver;

public class PersistenceUnitLinkResolver extends UniqueDefaultLinkResolver<PersistenceUnit> {
   private static final Logger LOGGER;
   private final AppModule module;

   public PersistenceUnitLinkResolver(AppModule appModule) {
      this.module = appModule;
   }

   protected Collection<PersistenceUnit> tryToResolveForEar(Collection<PersistenceUnit> values, URI moduleUri, String link) {
      if (this.module != null && !this.module.isStandaloneModule()) {
         WebModule war = this.extractWebApp(moduleUri);
         if (war != null) {
            Iterator<PersistenceUnit> it = values.iterator();

            while(it.hasNext()) {
               if (!this.isIn((PersistenceUnit)it.next(), war)) {
                  it.remove();
               }
            }

            return values;
         } else {
            Iterator<PersistenceUnit> it = values.iterator();

            while(it.hasNext()) {
               PersistenceUnit next = (PersistenceUnit)it.next();

               for(WebModule webModule : this.module.getWebModules()) {
                  if (this.isIn(next, webModule)) {
                     it.remove();
                  }
               }
            }

            return values;
         }
      } else {
         return values;
      }
   }

   private boolean isIn(PersistenceUnit value, WebModule war) {
      Collection<URL> urls = (Collection)war.getAltDDs().get("ear-webapp-persistence-xml-jars");
      if (urls != null && !urls.isEmpty()) {
         Collection<String> strUrls = new ArrayList();

         for(URL url : urls) {
            strUrls.add(URLs.toFilePath(url));
         }

         for(PersistenceModule persistenceModule : this.module.getPersistenceModules()) {
            Persistence persistence = persistenceModule.getPersistence();

            String rootUrl;
            try {
               rootUrl = URLs.toFilePath(new URL(persistenceModule.getRootUrl()));
            } catch (MalformedURLException var11) {
               continue;
            }

            for(PersistenceUnit unit : persistence.getPersistenceUnit()) {
               if (unit == value && strUrls.contains(rootUrl)) {
                  return true;
               }
            }
         }

         return false;
      } else {
         return false;
      }
   }

   private WebModule extractWebApp(URI moduleUri) {
      if (this.module != null && this.module.getJarLocation() != null) {
         File appModuleFile = new File(this.module.getJarLocation());

         File moduleFile;
         try {
            moduleFile = URLs.toFile(moduleUri.toURL());
         } catch (MalformedURLException var6) {
            return null;
         } catch (IllegalArgumentException iae) {
            File f = new File(appModuleFile, moduleUri.getPath());
            if (!f.exists()) {
               LOGGER.warning("Illegal uri: " + moduleUri, (Throwable)iae);
               return null;
            }

            moduleFile = f;
         }

         for(WebModule webModule : this.module.getWebModules()) {
            if (webModule.getJarLocation() != null && isParent(new File(webModule.getJarLocation()), moduleFile, appModuleFile)) {
               return webModule;
            }
         }

         return null;
      } else {
         return null;
      }
   }

   private static boolean isParent(File file, File moduleFile, File appModuleFile) {
      for(File current = file; current != null; current = current.getParentFile()) {
         if (current.equals(moduleFile)) {
            return true;
         }

         if (current.equals(appModuleFile)) {
            return false;
         }
      }

      return false;
   }

   static {
      LOGGER = Logger.getInstance(LogCategory.OPENEJB, PersistenceUnitLinkResolver.class);
   }
}
