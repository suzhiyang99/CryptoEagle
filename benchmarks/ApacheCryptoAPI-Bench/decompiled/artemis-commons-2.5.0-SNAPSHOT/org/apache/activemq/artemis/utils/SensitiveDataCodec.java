package org.apache.activemq.artemis.utils;

import java.util.Map;

public interface SensitiveDataCodec<T> {
   T decode(Object var1) throws Exception;

   T encode(Object var1) throws Exception;

   void init(Map<String, String> var1) throws Exception;
}
