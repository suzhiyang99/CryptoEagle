package org.apache.deltaspike.core.impl.scope;

import java.io.Serializable;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import javax.annotation.PreDestroy;
import javax.enterprise.inject.spi.BeanManager;
import org.apache.deltaspike.core.util.context.AbstractContext;
import org.apache.deltaspike.core.util.context.ContextualStorage;

public abstract class AbstractBeanHolder<K> implements Serializable {
   private Map<K, ContextualStorage> storageMap;
   private final boolean useConcurrentStorage;
   private final boolean usePassivationCapableStorage;

   protected AbstractBeanHolder() {
      this(true, true);
   }

   protected AbstractBeanHolder(boolean useConcurrentStorage, boolean usePassivationCapableStorage) {
      this.storageMap = new ConcurrentHashMap();
      this.useConcurrentStorage = useConcurrentStorage;
      this.usePassivationCapableStorage = usePassivationCapableStorage;
   }

   public ContextualStorage getContextualStorage(BeanManager beanManager, K key, boolean createIfNotExist) {
      ContextualStorage contextualStorage = (ContextualStorage)this.storageMap.get(key);
      if (contextualStorage == null && createIfNotExist) {
         contextualStorage = this.createContextualStorage(beanManager, key);
      }

      return contextualStorage;
   }

   protected synchronized ContextualStorage createContextualStorage(BeanManager beanManager, K key) {
      ContextualStorage contextualStorage = (ContextualStorage)this.storageMap.get(key);
      if (contextualStorage == null) {
         contextualStorage = new ContextualStorage(beanManager, this.useConcurrentStorage, this.usePassivationCapableStorage);
         this.storageMap.put(key, contextualStorage);
      }

      return contextualStorage;
   }

   public Map<K, ContextualStorage> getStorageMap() {
      return this.storageMap;
   }

   public Map<K, ContextualStorage> forceNewStorage() {
      Map<K, ContextualStorage> oldStorageMap = this.storageMap;
      this.storageMap = new ConcurrentHashMap();
      return oldStorageMap;
   }

   @PreDestroy
   public void destroyBeans() {
      Map<K, ContextualStorage> oldWindowContextStorages = this.forceNewStorage();

      for(ContextualStorage contextualStorage : oldWindowContextStorages.values()) {
         AbstractContext.destroyAllActive(contextualStorage);
      }

   }
}
