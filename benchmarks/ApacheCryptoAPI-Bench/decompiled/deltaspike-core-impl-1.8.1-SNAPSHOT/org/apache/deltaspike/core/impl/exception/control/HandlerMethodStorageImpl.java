package org.apache.deltaspike.core.impl.exception.control;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.logging.Logger;
import javax.enterprise.inject.Typed;
import javax.enterprise.inject.spi.BeanManager;
import org.apache.deltaspike.core.api.exception.control.HandlerMethod;
import org.apache.deltaspike.core.api.literal.AnyLiteral;
import org.apache.deltaspike.core.util.HierarchyDiscovery;

@Typed
class HandlerMethodStorageImpl implements HandlerMethodStorage {
   private final Map<Type, Collection<HandlerMethod<? extends Throwable>>> allHandlers;
   private Logger log = Logger.getLogger(HandlerMethodStorageImpl.class.toString());

   HandlerMethodStorageImpl(Map<Type, Collection<HandlerMethod<? extends Throwable>>> allHandlers) {
      this.allHandlers = allHandlers;
   }

   public <T extends Throwable> void registerHandlerMethod(HandlerMethod<T> handlerMethod) {
      this.log.fine(String.format("Adding handler %s to known handlers", handlerMethod));
      if (this.allHandlers.containsKey(handlerMethod.getExceptionType())) {
         ((Collection)this.allHandlers.get(handlerMethod.getExceptionType())).add(handlerMethod);
      } else {
         this.allHandlers.put(handlerMethod.getExceptionType(), new HashSet(Collections.singleton(handlerMethod)));
      }

   }

   public Collection<HandlerMethod<? extends Throwable>> getHandlersForException(Type exceptionClass, BeanManager bm, Set<Annotation> handlerQualifiers, boolean isBefore) {
      Collection<HandlerMethod<? extends Throwable>> returningHandlers = new TreeSet(new ExceptionHandlerComparator());
      HierarchyDiscovery h = new HierarchyDiscovery(exceptionClass);

      for(Type hierarchyType : h.getTypeClosure()) {
         if (this.allHandlers.get(hierarchyType) != null) {
            for(HandlerMethod<?> handler : (Collection)this.allHandlers.get(hierarchyType)) {
               if (handler.isBeforeHandler() && isBefore) {
                  if (handler.getQualifiers().contains(new AnyLiteral())) {
                     returningHandlers.add(handler);
                  } else if (!handlerQualifiers.isEmpty() && handlerQualifiers.equals(handler.getQualifiers())) {
                     returningHandlers.add(handler);
                  }
               } else if (!handler.isBeforeHandler() && !isBefore) {
                  if (handler.getQualifiers().contains(new AnyLiteral())) {
                     returningHandlers.add(handler);
                  } else if (!handlerQualifiers.isEmpty() && handlerQualifiers.equals(handler.getQualifiers())) {
                     returningHandlers.add(handler);
                  }
               }
            }
         }
      }

      this.log.fine(String.format("Found handlers %s for exception type %s, qualifiers %s", returningHandlers, exceptionClass, handlerQualifiers));
      return Collections.unmodifiableCollection(returningHandlers);
   }
}
