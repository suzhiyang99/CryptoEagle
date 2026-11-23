package org.apache.openejb.bval;

import javax.enterprise.inject.spi.AnnotatedType;
import org.apache.bval.cdi.BValExtension;
import org.apache.openejb.util.ContainerClassesFilter;

public class BValCdiFilter implements BValExtension.AnnotatedTypeFilter {
   private final ContainerClassesFilter delegate = new ContainerClassesFilter();

   public boolean accept(AnnotatedType<?> annotatedType) {
      String name = annotatedType.getJavaClass().getName();
      if (!name.startsWith("org.apache.openejb.")) {
         return this.delegate.accept(name);
      } else {
         String sub = name.substring("org.apache.openejb.".length());
         return !sub.startsWith("cdi.transactional") && !sub.startsWith("resource.activemq.jms2");
      }
   }
}
