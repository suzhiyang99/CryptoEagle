package org.apache.tika.exception;

public class TikaException extends Exception {
   public TikaException(String msg) {
      super(msg);
   }

   public TikaException(String msg, Throwable cause) {
      super(msg, cause);
   }
}
