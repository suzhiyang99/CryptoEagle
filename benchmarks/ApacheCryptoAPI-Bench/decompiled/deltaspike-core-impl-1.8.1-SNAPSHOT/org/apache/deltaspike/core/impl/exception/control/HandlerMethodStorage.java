package org.apache.deltaspike.core.impl.exception.control;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Set;
import javax.enterprise.inject.spi.BeanManager;
import org.apache.deltaspike.core.api.exception.control.HandlerMethod;

public interface HandlerMethodStorage {
   <T extends Throwable> void registerHandlerMethod(HandlerMethod<T> var1);

   Collection<HandlerMethod<? extends Throwable>> getHandlersForException(Type var1, BeanManager var2, Set<Annotation> var3, boolean var4);
}
