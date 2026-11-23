package org.apache.tika.exception;

public class AccessPermissionException extends TikaException {
   public AccessPermissionException() {
      super("Unable to process: content extraction is not allowed");
   }

   public AccessPermissionException(Throwable th) {
      super("Unable to process: content extraction is not allowed", th);
   }

   public AccessPermissionException(String info) {
      super(info);
   }

   public AccessPermissionException(String info, Throwable th) {
      super(info, th);
   }
}
