package org.apache.deltaspike.core.impl.resourceloader;

import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Any;
import javax.enterprise.inject.Disposes;
import javax.enterprise.inject.Instance;
import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.InjectionPoint;
import javax.inject.Inject;
import org.apache.deltaspike.core.api.provider.BeanProvider;
import org.apache.deltaspike.core.api.resourceloader.InjectableResource;
import org.apache.deltaspike.core.api.resourceloader.InjectableResourceProvider;

@ApplicationScoped
public class InjectableResourceProducer {
   private static final Logger logger = Logger.getLogger(InjectableResourceProducer.class.getName());
   @Inject
   @Any
   private Instance<InjectableResourceProvider> resourceProviders;

   @Produces
   @InjectableResource(
      resourceProvider = InjectableResourceProvider.class,
      location = ""
   )
   public InputStream getInputStream(InjectionPoint injectionPoint) {
      InjectableResource injectableResource = this.getAnnotation(injectionPoint);
      InjectableResourceProvider provider = (InjectableResourceProvider)BeanProvider.getContextualReference(injectableResource.resourceProvider(), new Annotation[0]);
      InputStream is = provider.readStream(injectableResource);
      return is;
   }

   @Produces
   @InjectableResource(
      resourceProvider = InjectableResourceProvider.class,
      location = ""
   )
   public List<InputStream> getInputStreams(InjectionPoint injectionPoint) {
      InjectableResource injectableResource = this.getAnnotation(injectionPoint);
      InjectableResourceProvider provider = (InjectableResourceProvider)BeanProvider.getContextualReference(injectableResource.resourceProvider(), new Annotation[0]);
      return provider.readStreams(injectableResource);
   }

   @Produces
   @InjectableResource(
      resourceProvider = InjectableResourceProvider.class,
      location = ""
   )
   public Properties getProperties(InjectionPoint injectionPoint) throws IOException {
      InjectableResource injectableResource = this.getAnnotation(injectionPoint);
      InjectableResourceProvider provider = (InjectableResourceProvider)BeanProvider.getContextualReference(injectableResource.resourceProvider(), new Annotation[0]);
      Properties properties = provider.readProperties(injectableResource);
      return properties;
   }

   public void closeInputStream(@Disposes @InjectableResource(resourceProvider = InjectableResourceProvider.class,location = "") InputStream inputStream) {
      if (inputStream != null) {
         try {
            inputStream.close();
         } catch (IOException e) {
            if (logger.isLoggable(Level.FINE)) {
               logger.log(Level.FINE, "Unable to close input stream ", e);
            }
         }
      }

   }

   private InjectableResource getAnnotation(InjectionPoint injectionPoint) {
      for(Annotation annotation : injectionPoint.getQualifiers()) {
         if (annotation instanceof InjectableResource) {
            return (InjectableResource)annotation;
         }
      }

      return null;
   }
}
