package org.apache.activemq.artemis.utils.critical;

import org.apache.activemq.artemis.utils.uri.BeanSupport;
import org.apache.commons.beanutils.Converter;

public enum CriticalAnalyzerPolicy {
   HALT,
   SHUTDOWN,
   LOG;

   static {
      BeanSupport.registerConverter(new CriticalAnalyzerPolicyConverter(), CriticalAnalyzerPolicy.class);
   }

   static class CriticalAnalyzerPolicyConverter implements Converter {
      public <T> T convert(Class<T> type, Object value) {
         return (T)type.cast(CriticalAnalyzerPolicy.valueOf(value.toString()));
      }
   }
}
