package org.apache.meecrowave.cxf;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.json.Json;
import javax.json.JsonStructure;
import javax.servlet.ServletContext;
import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.ext.Provider;
import org.apache.cxf.Bus;
import org.apache.cxf.bus.extension.ExtensionManagerBus;
import org.apache.cxf.common.util.ClassUnwrapper;
import org.apache.cxf.feature.Feature;
import org.apache.cxf.interceptor.Interceptor;
import org.apache.cxf.message.Message;
import org.apache.johnzon.jaxrs.DelegateProvider;
import org.apache.johnzon.jaxrs.JsrMessageBodyReader;
import org.apache.johnzon.jaxrs.JsrMessageBodyWriter;
import org.apache.johnzon.jaxrs.jsonb.jaxrs.JsonbJaxrsProvider;
import org.apache.meecrowave.Meecrowave;
import org.apache.webbeans.config.WebBeansContext;
import org.apache.webbeans.proxy.InterceptorDecoratorProxyFactory;
import org.apache.webbeans.proxy.NormalScopeProxyFactory;

@Named("cxf")
@ApplicationScoped
public class MeecrowaveBus implements Bus {
   private final Bus delegate = new ExtensionManagerBus();
   private NormalScopeProxyFactory normalScopeProxyFactory;
   private InterceptorDecoratorProxyFactory interceptorDecoratorProxyFactory;

   protected MeecrowaveBus() {
   }

   @Inject
   public MeecrowaveBus(ServletContext context) {
      WebBeansContext webBeansContext = WebBeansContext.currentInstance();
      this.normalScopeProxyFactory = webBeansContext.getNormalScopeProxyFactory();
      this.interceptorDecoratorProxyFactory = webBeansContext.getInterceptorDecoratorProxyFactory();
      this.setProperty(ClassUnwrapper.class.getName(), this::getRealClass);
      Meecrowave.Builder builder = (Meecrowave.Builder)Meecrowave.Builder.class.cast(context.getAttribute("meecrowave.configuration"));
      if (builder != null && builder.isJaxrsProviderSetup()) {
         List<Object> providers = (List)Optional.ofNullable(builder.getJaxrsDefaultProviders()).map((s) -> (List)Stream.of(s.split(" *, *")).map(String::trim).filter((p) -> !p.isEmpty()).map((name) -> {
               try {
                  return Thread.currentThread().getContextClassLoader().loadClass(name).newInstance();
               } catch (IllegalAccessException | ClassNotFoundException | InstantiationException var2) {
                  throw new IllegalArgumentException(name + " can't be created");
               }
            }).collect(Collectors.toList())).orElseGet(() -> (List)Stream.of(new ConfiguredJsonbJaxrsProvider(builder.getJsonbEncoding(), builder.isJsonbNulls(), builder.isJsonbIJson(), builder.isJsonbPrettify(), builder.getJsonbBinaryStrategy(), builder.getJsonbNamingStrategy(), builder.getJsonbOrderStrategy()), new ConfiguredJsrProvider(builder.getJsonpBufferStrategy(), builder.getJsonpMaxStringLen(), builder.getJsonpMaxReadBufferLen(), builder.getJsonpMaxWriteBufferLen(), builder.isJsonpSupportsComment(), builder.isJsonpPrettify())).collect(Collectors.toList()));
         if (builder.isJaxrsAutoActivateBeanValidation()) {
            try {
               ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
               contextClassLoader.loadClass("javax.validation.Validation");
               providers.add(contextClassLoader.loadClass("org.apache.cxf.jaxrs.validation.ValidationExceptionMapper").newInstance());
            } catch (NoClassDefFoundError | Exception var6) {
            }
         }

         if (this.getProperty("org.apache.cxf.jaxrs.bus.providers") == null) {
            this.setProperty("skip.default.json.provider.registration", "true");
            this.setProperty("org.apache.cxf.jaxrs.bus.providers", providers);
         }
      }

   }

   protected Class<?> getRealClass(Object o) {
      Class<?> aClass = o.getClass();
      if (!aClass.getName().contains("$$")) {
         return aClass;
      } else {
         Class realClass = aClass.getSuperclass();
         if (realClass == Object.class || realClass.isInterface()) {
            Class<?>[] interfaces = aClass.getInterfaces();
            if (interfaces.length > 0) {
               Class<?> rootInterface = interfaces[0];

               for(Class<?> anInterface : interfaces) {
                  if (rootInterface.isAssignableFrom(anInterface)) {
                     rootInterface = anInterface;
                  }
               }

               return rootInterface;
            }
         }

         return realClass;
      }
   }

   public <T> T getExtension(Class<T> extensionType) {
      return (T)this.delegate.getExtension(extensionType);
   }

   public <T> void setExtension(T extension, Class<T> extensionType) {
      this.delegate.setExtension(extension, extensionType);
   }

   public boolean hasExtensionByName(String name) {
      return this.delegate.hasExtensionByName(name);
   }

   public String getId() {
      return this.delegate.getId();
   }

   public void setId(String i) {
      this.delegate.setId(i);
   }

   public void shutdown(boolean wait) {
      this.delegate.shutdown(wait);
   }

   public void setProperty(String s, Object o) {
      this.delegate.setProperty(s, o);
   }

   public Object getProperty(String s) {
      return this.delegate.getProperty(s);
   }

   public void setProperties(Map<String, Object> properties) {
      this.delegate.setProperties(properties);
   }

   public Map<String, Object> getProperties() {
      return this.delegate.getProperties();
   }

   public Collection<Feature> getFeatures() {
      return this.delegate.getFeatures();
   }

   public void setFeatures(Collection<? extends Feature> features) {
      this.delegate.setFeatures(features);
   }

   public Bus.BusState getState() {
      return this.delegate.getState();
   }

   public List<Interceptor<? extends Message>> getInInterceptors() {
      return this.delegate.getInInterceptors();
   }

   public List<Interceptor<? extends Message>> getOutInterceptors() {
      return this.delegate.getOutInterceptors();
   }

   public List<Interceptor<? extends Message>> getInFaultInterceptors() {
      return this.delegate.getInFaultInterceptors();
   }

   public List<Interceptor<? extends Message>> getOutFaultInterceptors() {
      return this.delegate.getOutFaultInterceptors();
   }

   @Provider
   @Produces({"application/json", "*/*+json"})
   @Consumes({"application/json", "*/*+json"})
   public static class ConfiguredJsonbJaxrsProvider<T> extends JsonbJaxrsProvider<T> {
      private ConfiguredJsonbJaxrsProvider(String encoding, boolean nulls, boolean iJson, boolean pretty, String binaryStrategy, String namingStrategy, String orderStrategy) {
         super(Arrays.asList("[B"));
         Optional.ofNullable(encoding).ifPresent(this::setEncoding);
         Optional.ofNullable(namingStrategy).ifPresent(this::setPropertyNamingStrategy);
         Optional.ofNullable(orderStrategy).ifPresent(this::setPropertyOrderStrategy);
         Optional.ofNullable(binaryStrategy).ifPresent(this::setBinaryDataStrategy);
         this.setNullValues(nulls);
         this.setIJson(iJson);
         this.setPretty(pretty);
      }
   }

   @Provider
   @Produces({"application/json", "application/*+json"})
   @Consumes({"application/json", "application/*+json"})
   public static class ConfiguredJsrProvider extends DelegateProvider<JsonStructure> {
      private ConfiguredJsrProvider(final String bufferStrategy, final int maxStringLen, final int maxReadBufferLen, final int maxWriteBufferLen, final boolean supportsComment, final boolean pretty) {
         super(new JsrMessageBodyReader(Json.createReaderFactory(new HashMap<String, Object>() {
            {
               this.put("org.apache.johnzon.supports-comments", supportsComment);
               Optional.of(maxStringLen).filter((v) -> v > 0).ifPresent((s) -> this.put("org.apache.johnzon.max-string-length", s));
               Optional.of(maxReadBufferLen).filter((v) -> v > 0).ifPresent((s) -> this.put("org.apache.johnzon.default-char-buffer", s));
               Optional.ofNullable(bufferStrategy).ifPresent((s) -> this.put("org.apache.johnzon.buffer-strategy", s));
            }
         }), false), new JsrMessageBodyWriter(Json.createWriterFactory(new HashMap<String, Object>() {
            {
               this.put("javax.json.stream.JsonGenerator.prettyPrinting", pretty);
               Optional.of(maxWriteBufferLen).filter((v) -> v > 0).ifPresent((v) -> this.put("org.apache.johnzon.default-char-buffer-generator", v));
               Optional.ofNullable(bufferStrategy).ifPresent((s) -> this.put("org.apache.johnzon.buffer-strategy", s));
            }
         }), false));
      }
   }
}
