package org.apache.deltaspike.core.impl.exception.control;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.spi.Contextual;
import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.event.Observes;
import javax.enterprise.inject.Any;
import javax.enterprise.inject.spi.BeanManager;
import org.apache.deltaspike.core.api.exception.control.HandlerMethod;
import org.apache.deltaspike.core.api.exception.control.event.ExceptionStackEvent;
import org.apache.deltaspike.core.api.exception.control.event.ExceptionToCatchEvent;
import org.apache.deltaspike.core.api.provider.BeanProvider;

@ApplicationScoped
public class ExceptionHandlerBroadcaster {
   private static final Logger LOG = Logger.getLogger(ExceptionHandlerBroadcaster.class.getName());

   public void executeHandlers(@Observes @Any ExceptionToCatchEvent exceptionEventEvent, BeanManager beanManager) throws Throwable {
      LOG.entering(ExceptionHandlerBroadcaster.class.getName(), "executeHandlers", exceptionEventEvent.getException());
      CreationalContext<Object> creationalContext = null;
      Throwable throwException = null;
      HandlerMethodStorage handlerMethodStorage = (HandlerMethodStorage)BeanProvider.getContextualReference(HandlerMethodStorage.class, new Annotation[0]);

      try {
         creationalContext = beanManager.createCreationalContext((Contextual)null);
         Set<HandlerMethod<?>> processedHandlers = new HashSet();
         ExceptionStackEvent stack = new ExceptionStackEvent(exceptionEventEvent.getException());
         beanManager.fireEvent(stack, new Annotation[0]);

         label258:
         while(stack.getCurrent() != null) {
            for(HandlerMethod<?> handler : new ArrayList(handlerMethodStorage.getHandlersForException(stack.getCurrent().getClass(), beanManager, exceptionEventEvent.getQualifiers(), true))) {
               if (!processedHandlers.contains(handler)) {
                  LOG.fine(String.format("Notifying handler %s", handler));
                  DefaultExceptionEvent callbackEvent = new DefaultExceptionEvent(stack, true, exceptionEventEvent.isHandled());
                  handler.notify(callbackEvent, beanManager);
                  LOG.fine(String.format("Handler %s returned status %s", handler, callbackEvent.getCurrentExceptionHandlingFlow().name()));
                  if (!callbackEvent.isUnmute()) {
                     processedHandlers.add(handler);
                  }

                  switch (callbackEvent.getCurrentExceptionHandlingFlow()) {
                     case HANDLED:
                        exceptionEventEvent.setHandled(true);
                        return;
                     case HANDLED_AND_CONTINUE:
                        exceptionEventEvent.setHandled(true);
                        break;
                     case ABORT:
                        return;
                     case SKIP_CAUSE:
                        exceptionEventEvent.setHandled(true);
                        stack.skipCause();
                        continue label258;
                     case THROW_ORIGINAL:
                        throw exceptionEventEvent.getException();
                     case THROW:
                        throw callbackEvent.getThrowNewException();
                     default:
                        throw new IllegalStateException("Unexpected enum type " + callbackEvent.getCurrentExceptionHandlingFlow());
                  }
               }
            }

            Collection<HandlerMethod<? extends Throwable>> handlersForException = handlerMethodStorage.getHandlersForException(stack.getCurrent().getClass(), beanManager, exceptionEventEvent.getQualifiers(), false);
            List<HandlerMethod<? extends Throwable>> handlerMethods = new ArrayList(handlersForException);
            Collections.reverse(handlerMethods);

            for(HandlerMethod<?> handler : handlerMethods) {
               if (!processedHandlers.contains(handler)) {
                  LOG.fine(String.format("Notifying handler %s", handler));
                  DefaultExceptionEvent depthFirstEvent = new DefaultExceptionEvent(stack, false, exceptionEventEvent.isHandled());
                  handler.notify(depthFirstEvent, beanManager);
                  LOG.fine(String.format("Handler %s returned status %s", handler, depthFirstEvent.getCurrentExceptionHandlingFlow().name()));
                  if (!depthFirstEvent.isUnmute()) {
                     processedHandlers.add(handler);
                  }

                  switch (depthFirstEvent.getCurrentExceptionHandlingFlow()) {
                     case HANDLED:
                        exceptionEventEvent.setHandled(true);
                        return;
                     case HANDLED_AND_CONTINUE:
                        exceptionEventEvent.setHandled(true);
                        break;
                     case ABORT:
                        return;
                     case SKIP_CAUSE:
                        exceptionEventEvent.setHandled(true);
                        stack.skipCause();
                        continue label258;
                     case THROW_ORIGINAL:
                        throwException = exceptionEventEvent.getException();
                        break;
                     case THROW:
                        throwException = depthFirstEvent.getThrowNewException();
                        break;
                     default:
                        throw new IllegalStateException("Unexpected enum type " + depthFirstEvent.getCurrentExceptionHandlingFlow());
                  }
               }
            }

            stack.skipCause();
         }

         if (!exceptionEventEvent.isHandled() && throwException == null && !exceptionEventEvent.isOptional()) {
            LOG.warning(String.format("No handlers found for exception %s", exceptionEventEvent.getException()));
            throw exceptionEventEvent.getException();
         } else if (throwException != null) {
            throw throwException;
         }
      } finally {
         if (creationalContext != null) {
            creationalContext.release();
         }

         LOG.exiting(ExceptionHandlerBroadcaster.class.getName(), "executeHandlers", exceptionEventEvent.getException());
      }
   }
}
