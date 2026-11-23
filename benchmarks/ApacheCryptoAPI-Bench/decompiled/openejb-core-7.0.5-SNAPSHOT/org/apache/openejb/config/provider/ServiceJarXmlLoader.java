package org.apache.openejb.config.provider;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.apache.openejb.OpenEJBException;
import org.apache.openejb.config.sys.JaxbOpenejb;
import org.apache.openejb.config.sys.ServiceProvider;
import org.apache.openejb.config.sys.ServicesJar;

public class ServiceJarXmlLoader implements ProviderLoader {
   private final Map<ID, ServiceProvider> loaded = new LinkedHashMap();
   private final List<String> namespaces = new LinkedList();

   public ServiceProvider load(ID id) {
      id.validate();
      ServiceProvider provider = (ServiceProvider)this.loaded.remove(id);
      if (provider != null) {
         return provider;
      } else {
         String namespace = id.getNamespace();
         if (this.namespaces.contains(namespace)) {
            return null;
         } else {
            this.namespaces.add(namespace);
            this.parse(namespace);
            return this.load(id);
         }
      }
   }

   private void parse(String namespace) {
      try {
         ServicesJar servicesJar = JaxbOpenejb.readServicesJar(namespace);

         for(ServiceProvider provider : servicesJar.getServiceProvider()) {
            ID found = new ID(namespace, provider.getId());
            this.loaded.put(found, provider);
         }

      } catch (OpenEJBException e) {
         throw new IllegalStateException(e);
      }
   }

   public List<ServiceProvider> load(String namespace) {
      if (!this.namespaces.contains(namespace)) {
         this.namespaces.add(namespace);
         this.parse(namespace);
      }

      List<ServiceProvider> list = new ArrayList();

      for(Map.Entry<ID, ServiceProvider> entry : this.loaded.entrySet()) {
         if (((ID)entry.getKey()).getNamespace().equals(namespace)) {
            list.add(entry.getValue());
         }
      }

      return list;
   }
}
