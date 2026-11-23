package org.apache.tika.detect;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.metadata.TikaCoreProperties;
import org.apache.tika.mime.MediaType;
import org.apache.tika.mime.MediaTypeRegistry;

public class CompositeDetector implements Detector {
   private static final long serialVersionUID = 5980683158436430252L;
   private final MediaTypeRegistry registry;
   private final List<Detector> detectors;

   public CompositeDetector(MediaTypeRegistry registry, List<Detector> detectors, Collection<Class<? extends Detector>> excludeDetectors) {
      if (excludeDetectors != null && !excludeDetectors.isEmpty()) {
         this.detectors = new ArrayList();

         for(Detector d : detectors) {
            if (!this.isExcluded(excludeDetectors, d.getClass())) {
               this.detectors.add(d);
            }
         }
      } else {
         this.detectors = detectors;
      }

      this.registry = registry;
   }

   public CompositeDetector(MediaTypeRegistry registry, List<Detector> detectors) {
      this(registry, detectors, (Collection)null);
   }

   public CompositeDetector(List<Detector> detectors) {
      this(new MediaTypeRegistry(), detectors);
   }

   public CompositeDetector(Detector... detectors) {
      this(Arrays.asList(detectors));
   }

   public MediaType detect(InputStream input, Metadata metadata) throws IOException {
      MediaType type = MediaType.OCTET_STREAM;

      for(Detector detector : this.getDetectors()) {
         if (detector instanceof OverrideDetector && metadata.get(TikaCoreProperties.CONTENT_TYPE_OVERRIDE) != null) {
            return detector.detect(input, metadata);
         }

         MediaType detected = detector.detect(input, metadata);
         if (this.registry.isSpecializationOf(detected, type)) {
            type = detected;
         }
      }

      return type;
   }

   public List<Detector> getDetectors() {
      return Collections.unmodifiableList(this.detectors);
   }

   private boolean isExcluded(Collection<Class<? extends Detector>> excludeDetectors, Class<? extends Detector> d) {
      return excludeDetectors.contains(d) || this.assignableFrom(excludeDetectors, d);
   }

   private boolean assignableFrom(Collection<Class<? extends Detector>> excludeDetectors, Class<? extends Detector> d) {
      for(Class<? extends Detector> e : excludeDetectors) {
         if (e.isAssignableFrom(d)) {
            return true;
         }
      }

      return false;
   }
}
