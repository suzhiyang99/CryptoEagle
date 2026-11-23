package org.apache.tika.detect;

import java.util.Collection;
import java.util.List;
import org.apache.tika.config.ServiceLoader;
import org.apache.tika.mime.MimeTypes;
import org.apache.tika.utils.ServiceLoaderUtils;

public class DefaultDetector extends CompositeDetector {
   private static final long serialVersionUID = -8170114575326908027L;
   private final transient ServiceLoader loader;

   private static List<Detector> getDefaultDetectors(MimeTypes types, ServiceLoader loader) {
      List<Detector> detectors = loader.<Detector>loadStaticServiceProviders(Detector.class);
      ServiceLoaderUtils.sortLoadedClasses(detectors);
      detectors.add(types);
      return detectors;
   }

   public DefaultDetector(MimeTypes types, ServiceLoader loader, Collection<Class<? extends Detector>> excludeDetectors) {
      super(types.getMediaTypeRegistry(), getDefaultDetectors(types, loader), excludeDetectors);
      this.loader = loader;
   }

   public DefaultDetector(MimeTypes types, ServiceLoader loader) {
      this(types, loader, (Collection)null);
   }

   public DefaultDetector(MimeTypes types, ClassLoader loader) {
      this(types, new ServiceLoader(loader));
   }

   public DefaultDetector(ClassLoader loader) {
      this(MimeTypes.getDefaultMimeTypes(), loader);
   }

   public DefaultDetector(MimeTypes types) {
      this(types, new ServiceLoader());
   }

   public DefaultDetector() {
      this(MimeTypes.getDefaultMimeTypes());
   }

   public List<Detector> getDetectors() {
      if (this.loader != null) {
         List<Detector> detectors = this.loader.<Detector>loadDynamicServiceProviders(Detector.class);
         detectors.addAll(super.getDetectors());
         return detectors;
      } else {
         return super.getDetectors();
      }
   }
}
