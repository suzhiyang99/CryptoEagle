package org.apache.deltaspike.core.impl.exception.control;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import org.apache.deltaspike.core.impl.exception.control.extension.ExceptionControlExtension;

@ApplicationScoped
public class HandlerMethodStorageProducer {
   @Inject
   private ExceptionControlExtension exceptionControlExtension;

   @Produces
   @ApplicationScoped
   protected HandlerMethodStorage createHandlerMethodStorage() {
      return new HandlerMethodStorageImpl(this.exceptionControlExtension.getAllExceptionHandlers());
   }
}
