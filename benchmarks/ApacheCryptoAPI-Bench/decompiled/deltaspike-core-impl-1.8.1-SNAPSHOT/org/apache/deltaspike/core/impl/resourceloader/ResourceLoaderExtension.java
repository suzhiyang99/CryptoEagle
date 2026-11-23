package org.apache.deltaspike.core.impl.resourceloader;

import javax.enterprise.inject.spi.AnnotatedType;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.spi.BeforeBeanDiscovery;
import javax.enterprise.inject.spi.Extension;
import org.apache.deltaspike.core.api.resourceloader.ClasspathResourceProvider;
import org.apache.deltaspike.core.api.resourceloader.FileResourceProvider;

public class ResourceLoaderExtension implements Extension {
   public void addResourceLoaders(BeforeBeanDiscovery beforeBeanDiscovery, BeanManager beanManager) {
      beforeBeanDiscovery.addAnnotatedType(this.createAnnotatedType(ClasspathResourceProvider.class, beanManager));
      beforeBeanDiscovery.addAnnotatedType(this.createAnnotatedType(InjectableResourceProducer.class, beanManager));
      beforeBeanDiscovery.addAnnotatedType(this.createAnnotatedType(FileResourceProvider.class, beanManager));
   }

   private AnnotatedType<?> createAnnotatedType(Class<?> clazz, BeanManager beanManager) {
      return beanManager.createAnnotatedType(clazz);
   }
}
