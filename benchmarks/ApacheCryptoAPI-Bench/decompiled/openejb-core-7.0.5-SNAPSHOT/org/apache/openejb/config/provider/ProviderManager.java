package org.apache.openejb.config.provider;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.openejb.config.sys.ServiceProvider;
import org.apache.openejb.util.SuperProperties;

public class ProviderManager {
   private final List<String> namespaces = new LinkedList();
   private final Map<ID, ServiceProvider> providers = new LinkedHashMap();
   private final ProviderLoader loader;

   public ProviderManager(ProviderLoader loader) {
      this.loader = loader;
   }

   public ServiceProvider get(String namespace, String name) {
      ID id = new ID(namespace, name);
      return this.getProvider(id, new LinkedHashSet());
   }

   public List<ServiceProvider> getAll() {
      return new ArrayList(this.providers.values());
   }

   public void register(String namespace, ServiceProvider provider) {
      if (provider == null) {
         throw new IllegalArgumentException("provider cannot be null");
      } else {
         ID id = new ID(namespace, provider.getId());
         this.register(id, provider, new LinkedHashSet());
      }
   }

   public List<ServiceProvider> load(String namespace) {
      if (namespace == null) {
         throw new IllegalArgumentException("namespace cannot be null");
      } else {
         namespace = namespace.toLowerCase();
         if (!this.namespaces.contains(namespace)) {
            this.namespaces.add(namespace);

            for(ServiceProvider provider : new ArrayList(this.loader.load(namespace))) {
               this.register(namespace, provider);
            }
         }

         List<ServiceProvider> providers = new ArrayList();

         for(Map.Entry<ID, ServiceProvider> entry : this.providers.entrySet()) {
            if (((ID)entry.getKey()).getNamespace().equals(namespace)) {
               providers.add(entry.getValue());
            }
         }

         return providers;
      }
   }

   private void register(ID id, ServiceProvider provider, Set<ID> seen) {
      if (!this.providers.containsKey(id)) {
         if (provider.getParent() != null) {
            ID parentId = ID.parse(provider.getParent(), id);
            ServiceProvider parent = this.getProvider(parentId, seen);
            if (parent == null) {
               throw new NoSuchParentProviderException(provider, parentId);
            }

            this.inherit(provider, parent);
         }

         this.validate(id, provider);
         this.providers.put(id, provider);
      }
   }

   private void inherit(ServiceProvider child, ServiceProvider parent) {
      if (this.n(child.getClassName())) {
         child.setClassName(parent.getClassName());
      }

      if (this.n(child.getConstructor())) {
         child.setConstructor(parent.getConstructor());
      }

      if (this.n(child.getFactoryName())) {
         child.setFactoryName(parent.getFactoryName());
      }

      if (this.n(child.getDescription())) {
         child.setDescription(parent.getDescription());
      }

      if (this.n(child.getDisplayName())) {
         child.setDisplayName(parent.getDisplayName());
      }

      if (this.n(child.getService())) {
         child.setService(parent.getService());
      }

      Set<String> types = new HashSet();
      types.addAll(parent.getTypes());
      types.addAll(child.getTypes());
      child.getTypes().clear();
      child.getTypes().addAll(types);
      SuperProperties properties = new SuperProperties();
      properties.putAll(parent.getProperties());
      properties.putAll(child.getProperties());
      child.getProperties().clear();
      child.getProperties().putAll(properties);
   }

   private boolean n(String s) {
      return s == null || s.length() == 0;
   }

   private ServiceProvider getProvider(ID id, Set<ID> seen) {
      if (seen.contains(id)) {
         throw new ProviderCircularReferenceException(seen);
      } else {
         seen.add(id);
         ServiceProvider provider = (ServiceProvider)this.providers.get(id);
         if (provider != null) {
            return provider;
         } else {
            provider = this.loader.load(id);
            if (provider != null) {
               this.register(id, provider, seen);
               return provider;
            } else {
               return null;
            }
         }
      }
   }

   private void validate(ID id, ServiceProvider provider) {
      id.validate();
      if (provider.getService() == null) {
         throw new InvalidProviderDeclarationException("'service' attribute cannot be null", id, provider);
      }
   }

   public ProviderLoader getLoader() {
      return this.loader;
   }
}
