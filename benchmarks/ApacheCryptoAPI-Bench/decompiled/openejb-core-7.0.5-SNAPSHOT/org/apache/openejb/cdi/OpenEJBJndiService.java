package org.apache.openejb.cdi;

import org.apache.webbeans.spi.JNDIService;

public class OpenEJBJndiService implements JNDIService {
   private Object bm;

   public void bind(String name, Object object) {
      this.bm = object;
   }

   public void unbind(String name) {
   }

   public <T> T lookup(String name, Class<? extends T> expectedClass) {
      return (T)expectedClass.cast(this.bm);
   }
}
