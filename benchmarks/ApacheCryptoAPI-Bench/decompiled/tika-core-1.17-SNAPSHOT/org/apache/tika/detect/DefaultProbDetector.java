package org.apache.tika.detect;

import java.util.List;
import org.apache.tika.config.ServiceLoader;
import org.apache.tika.mime.MimeTypes;
import org.apache.tika.mime.ProbabilisticMimeDetectionSelector;
import org.apache.tika.utils.ServiceLoaderUtils;

public class DefaultProbDetector extends CompositeDetector {
   private static final long serialVersionUID = -8836240060532323352L;
   private final transient ServiceLoader loader;

   private static List<Detector> getDefaultDetectors(ProbabilisticMimeDetectionSelector sel, ServiceLoader loader) {
      List<Detector> detectors = loader.<Detector>loadStaticServiceProviders(Detector.class);
      ServiceLoaderUtils.sortLoadedClasses(detectors);
      detectors.add(sel);
      return detectors;
   }

   public DefaultProbDetector(ProbabilisticMimeDetectionSelector sel, ServiceLoader loader) {
      super(sel.getMediaTypeRegistry(), getDefaultDetectors(sel, loader));
      this.loader = loader;
   }

   public DefaultProbDetector(ProbabilisticMimeDetectionSelector sel, ClassLoader loader) {
      this(sel, new ServiceLoader(loader));
   }

   public DefaultProbDetector(ClassLoader loader) {
      this(new ProbabilisticMimeDetectionSelector(), loader);
   }

   public DefaultProbDetector(MimeTypes types) {
      this(new ProbabilisticMimeDetectionSelector(types), new ServiceLoader());
   }

   public DefaultProbDetector() {
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
