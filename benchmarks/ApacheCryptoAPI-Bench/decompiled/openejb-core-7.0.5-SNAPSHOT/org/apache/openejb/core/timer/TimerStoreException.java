package org.apache.openejb.core.timer;

import org.apache.openejb.OpenEJBException;

public class TimerStoreException extends OpenEJBException {
   public TimerStoreException() {
   }

   public TimerStoreException(String message) {
      super(message);
   }

   public TimerStoreException(String message, Throwable cause) {
      super(message, cause);
   }

   public TimerStoreException(Throwable cause) {
      super(cause);
   }
}
