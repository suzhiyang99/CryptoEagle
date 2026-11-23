package org.apache.deltaspike.core.impl.exception.control;

import javax.enterprise.inject.Typed;
import org.apache.deltaspike.core.api.exception.control.ExceptionHandlingFlow;
import org.apache.deltaspike.core.api.exception.control.event.ExceptionStackEvent;
import org.apache.deltaspike.core.spi.exception.control.event.IntrospectiveExceptionEvent;

@Typed
public class DefaultExceptionEvent<T extends Throwable> implements IntrospectiveExceptionEvent<T> {
   private final T exception;
   private boolean unmute;
   private ExceptionHandlingFlow flow;
   private Throwable throwNewException;
   private final boolean beforeTraversal;
   private final boolean markedHandled;

   public DefaultExceptionEvent(ExceptionStackEvent stackEvent, boolean beforeTraversal, boolean handled) {
      if (stackEvent == null) {
         throw new IllegalArgumentException("null is not valid for stackEvent");
      } else {
         this.exception = stackEvent.getCurrent();
         this.beforeTraversal = beforeTraversal;
         this.markedHandled = handled;
         this.flow = ExceptionHandlingFlow.HANDLED_AND_CONTINUE;
      }
   }

   public T getException() {
      return this.exception;
   }

   public void abort() {
      this.flow = ExceptionHandlingFlow.ABORT;
   }

   public void throwOriginal() {
      this.flow = ExceptionHandlingFlow.THROW_ORIGINAL;
   }

   public void handled() {
      this.flow = ExceptionHandlingFlow.HANDLED;
   }

   public void handledAndContinue() {
      this.flow = ExceptionHandlingFlow.HANDLED_AND_CONTINUE;
   }

   public void skipCause() {
      this.flow = ExceptionHandlingFlow.SKIP_CAUSE;
   }

   public void unmute() {
      this.unmute = true;
   }

   public boolean isUnmute() {
      return this.unmute;
   }

   public ExceptionHandlingFlow getCurrentExceptionHandlingFlow() {
      return this.flow;
   }

   public boolean isMarkedHandled() {
      return this.markedHandled;
   }

   public boolean isBeforeTraversal() {
      return this.beforeTraversal;
   }

   public void rethrow(Throwable t) {
      this.throwNewException = t;
      this.flow = ExceptionHandlingFlow.THROW;
   }

   public Throwable getThrowNewException() {
      return this.throwNewException;
   }
}
