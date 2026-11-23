package org.apache.deltaspike.core.impl.util;

import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.Map;
import javax.enterprise.inject.Typed;
import org.apache.deltaspike.core.util.ClassUtils;
import org.apache.deltaspike.core.util.metadata.AnnotationInstanceProvider;

@Typed
public abstract class AnnotationInstanceUtils {
   private AnnotationInstanceUtils() {
   }

   public static Annotation getPriorityAnnotationInstance(int priorityValue) {
      Annotation priorityAnnotationInstance = null;
      Class<? extends Annotation> priorityAnnotationClass = ClassUtils.tryToLoadClassForName("javax.annotation.Priority");
      if (priorityAnnotationClass != null && ClassUtils.tryToLoadClassForName("javax.enterprise.inject.spi.AfterTypeDiscovery") != null) {
         Map<String, Object> defaultValueMap = new HashMap();
         defaultValueMap.put("value", priorityValue);
         priorityAnnotationInstance = AnnotationInstanceProvider.of(priorityAnnotationClass, defaultValueMap);
      }

      return priorityAnnotationInstance;
   }
}
