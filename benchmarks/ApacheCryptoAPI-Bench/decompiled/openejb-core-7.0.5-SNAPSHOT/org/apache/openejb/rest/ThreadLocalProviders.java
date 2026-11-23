package org.apache.openejb.rest;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.ext.ContextResolver;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.MessageBodyReader;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Providers;

public class ThreadLocalProviders extends AbstractRestThreadLocalProxy<Providers> implements Providers {
   protected ThreadLocalProviders() {
      super(Providers.class);
   }

   public <T> ContextResolver<T> getContextResolver(Class<T> rawType, MediaType mediaType) {
      return ((Providers)this.get()).getContextResolver(rawType, mediaType);
   }

   public <T extends Throwable> ExceptionMapper<T> getExceptionMapper(Class<T> rawType) {
      return ((Providers)this.get()).getExceptionMapper(rawType);
   }

   public <T> MessageBodyReader<T> getMessageBodyReader(Class<T> rawType, Type genericType, Annotation[] annotations, MediaType mediaType) {
      return ((Providers)this.get()).getMessageBodyReader(rawType, genericType, annotations, mediaType);
   }

   public <T> MessageBodyWriter<T> getMessageBodyWriter(Class<T> rawType, Type genericType, Annotation[] annotations, MediaType mediaType) {
      return ((Providers)this.get()).getMessageBodyWriter(rawType, genericType, annotations, mediaType);
   }
}
