package org.apache.openejb.config;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import org.apache.openejb.OpenEJBException;
import org.apache.openejb.config.provider.ID;
import org.apache.openejb.config.provider.ProviderManager;
import org.apache.openejb.config.provider.ServiceJarXmlLoader;
import org.apache.openejb.config.sys.ServiceProvider;
import org.apache.openejb.loader.SystemInstance;
import org.apache.openejb.util.LogCategory;
import org.apache.openejb.util.Logger;
import org.apache.openejb.util.Messages;

public class ServiceUtils {
   public static final String ANY = ServiceUtils.class.getName() + "@ANY";
   public static final String NONE = ServiceUtils.class.getName() + "@NONE";
   public static final String DEFAULT_PROVIDER_URL;
   public static Logger logger;

   private static String currentDefaultProviderUrl(String defaultValue) {
      return SystemInstance.get().getProperty("openejb.provider.default", defaultValue);
   }

   public static ProviderManager getManager() {
      ProviderManager manager = (ProviderManager)SystemInstance.get().getComponent(ProviderManager.class);
      if (manager != null) {
         return manager;
      } else {
         SystemInstance.get().setComponent(ProviderManager.class, new ProviderManager(new ServiceJarXmlLoader()));
         return getManager();
      }
   }

   public static boolean hasServiceProvider(String id) {
      try {
         ProviderInfo info = getProviderInfo(id);

         for(ServiceProvider service : getServiceProviders(info.getPackageName())) {
            if (service.getId().equals(id)) {
               return true;
            }
         }
      } catch (IllegalStateException | OpenEJBException var5) {
      }

      return false;
   }

   public static ServiceProvider getServiceProvider(String idString) throws OpenEJBException {
      ID id = ID.parse(idString, currentDefaultProviderUrl(DEFAULT_PROVIDER_URL));
      ServiceProvider provider = getManager().get(id.getNamespace(), id.getName());
      if (provider != null) {
         return provider;
      } else {
         throw new NoSuchProviderException((new Messages("org.apache.openejb.util.resources")).format("conf.4901", id.getName(), id.getNamespace()));
      }
   }

   public static String getServiceProviderId(String type) throws OpenEJBException {
      return getServiceProviderId(type, (Properties)null);
   }

   public static String getServiceProviderId(String type, Properties required) throws OpenEJBException {
      ServiceProvider provider = getServiceProviderByType(type, required);
      return provider != null ? provider.getId() : null;
   }

   public static List<ServiceProvider> getServiceProvidersByServiceType(String type) throws OpenEJBException {
      ArrayList<ServiceProvider> providers = new ArrayList();
      if (type == null) {
         return providers;
      } else {
         for(ServiceProvider service : getServiceProviders(currentDefaultProviderUrl(DEFAULT_PROVIDER_URL))) {
            if (service.getService().equals(type)) {
               providers.add(service);
            }
         }

         return providers;
      }
   }

   public static ServiceProvider getServiceProviderByType(String type, Properties required) throws OpenEJBException {
      if (type == null) {
         return null;
      } else {
         if (required == null) {
            required = new Properties();
         }

         for(ServiceProvider service : getServiceProviders(currentDefaultProviderUrl(DEFAULT_PROVIDER_URL))) {
            if (service.getTypes().contains(type) && implies(required, service.getProperties())) {
               return service;
            }
         }

         return null;
      }
   }

   public static boolean implies(Properties required, Properties available) {
      if (available.containsKey("openejb.connector")) {
         return true;
      } else {
         for(Map.Entry<Object, Object> entry : required.entrySet()) {
            Object value = available.get(entry.getKey());
            Object expected = entry.getValue();
            if (expected.equals(NONE)) {
               if (value != null) {
                  return false;
               }
            } else if (expected.equals(ANY)) {
               if (value == null) {
                  return false;
               }
            } else {
               if (value instanceof String) {
                  value = ((String)value).toLowerCase();
               }

               if (expected instanceof String) {
                  expected = ((String)expected).toLowerCase();
               }

               if (!expected.equals(value)) {
                  return false;
               }
            }
         }

         return true;
      }
   }

   public static ServiceProvider getServiceProviderByType(String providerType, String serviceType) throws OpenEJBException {
      if (serviceType == null) {
         return null;
      } else {
         for(ServiceProvider service : getServiceProvidersByServiceType(providerType)) {
            if (service.getTypes().contains(serviceType)) {
               return service;
            }
         }

         return null;
      }
   }

   public static List<ServiceProvider> getServiceProviders() throws OpenEJBException {
      return getServiceProviders(currentDefaultProviderUrl(DEFAULT_PROVIDER_URL));
   }

   public static List<ServiceProvider> getServiceProviders(String packageName) throws OpenEJBException {
      return getManager().load(packageName);
   }

   public static void registerServiceProvider(String packageName, ServiceProvider provider) {
      getManager().register(packageName, provider);
   }

   private static ProviderInfo getProviderInfo(String id) {
      String providerName = null;
      String serviceName = null;
      if (id.indexOf("#") != -1) {
         providerName = id.substring(0, id.indexOf("#"));
         serviceName = id.substring(id.indexOf("#") + 1);
      } else if (id.indexOf(":") != -1) {
         providerName = id.substring(0, id.indexOf(":"));
         serviceName = id.substring(id.indexOf(":") + 1);
      } else {
         providerName = currentDefaultProviderUrl(DEFAULT_PROVIDER_URL);
         serviceName = id;
      }

      return new ProviderInfo(providerName, serviceName);
   }

   static {
      String defaultValue = "org.apache.openejb";

      try {
         SystemInstance system = SystemInstance.get();
         if (system.getProperty("openejb.embedded") != null) {
            defaultValue = "org.apache.openejb.embedded";
         }
      } catch (Exception var2) {
      }

      DEFAULT_PROVIDER_URL = defaultValue;
      logger = Logger.getInstance(LogCategory.OPENEJB, "org.apache.openejb.util.resources");
   }

   public static class ProviderInfo {
      private final String packageName;
      private final String serviceName;

      public ProviderInfo(String providerName, String serviceName) {
         this.packageName = providerName;
         this.serviceName = serviceName;
      }

      public String getPackageName() {
         return this.packageName;
      }

      public String getServiceName() {
         return this.serviceName;
      }
   }
}
