package org.apache.openejb;

public class OpenEJBRuntimeException extends RuntimeException {
   public OpenEJBRuntimeException(String str) {
      super(str);
   }

   public OpenEJBRuntimeException(String str, Throwable e) {
      super(str, e);
   }

   public OpenEJBRuntimeException(Exception e) {
      super(e);
   }
}
