package org.apache.openejb.config;

import org.apache.openejb.OpenEJBException;

public class NoSuchProviderException extends OpenEJBException {
   public NoSuchProviderException(String message) {
      super(message);
   }
}
