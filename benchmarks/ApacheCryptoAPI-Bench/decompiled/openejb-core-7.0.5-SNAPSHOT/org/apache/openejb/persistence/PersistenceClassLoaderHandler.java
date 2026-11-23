package org.apache.openejb.persistence;

import java.lang.instrument.ClassFileTransformer;

public interface PersistenceClassLoaderHandler {
   void addTransformer(String var1, ClassLoader var2, ClassFileTransformer var3);

   void destroy(String var1);

   ClassLoader getNewTempClassLoader(ClassLoader var1);
}
