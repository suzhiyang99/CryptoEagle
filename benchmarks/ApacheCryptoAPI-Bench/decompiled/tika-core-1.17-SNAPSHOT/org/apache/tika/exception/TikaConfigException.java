package org.apache.tika.exception;

public class TikaConfigException extends TikaException {
   public TikaConfigException(String msg) {
      super(msg);
   }

   public TikaConfigException(String msg, Throwable cause) {
      super(msg, cause);
   }
}
