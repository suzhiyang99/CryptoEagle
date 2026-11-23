package org.apache.openejb.core.webservices;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.Executor;
import javax.jws.WebService;
import javax.xml.bind.JAXBContext;
import javax.xml.namespace.QName;
import javax.xml.transform.Source;
import javax.xml.ws.BindingProvider;
import javax.xml.ws.Dispatch;
import javax.xml.ws.Endpoint;
import javax.xml.ws.EndpointReference;
import javax.xml.ws.Service;
import javax.xml.ws.WebServiceException;
import javax.xml.ws.WebServiceFeature;
import javax.xml.ws.handler.HandlerResolver;
import javax.xml.ws.soap.SOAPBinding;
import javax.xml.ws.spi.Provider;
import javax.xml.ws.spi.ServiceDelegate;
import javax.xml.ws.wsaddressing.W3CEndpointReference;
import org.apache.openejb.OpenEJBRuntimeException;
import org.apache.openejb.core.ivm.naming.JaxWsServiceReference;
import org.apache.openejb.loader.IO;
import org.apache.openejb.util.JavaSecurityManagers;
import org.apache.openejb.util.LogCategory;
import org.apache.openejb.util.Logger;
import org.w3c.dom.Element;

public class ProviderWrapper extends Provider {
   public static final Logger logger;
   private static final String JAXWSPROVIDER_PROPERTY;
   private static final ThreadLocal<ProviderWrapperData> threadPortRefs;
   private final Provider delegate = findProvider();
   private final List<PortRefData> portRefs;
   private static final Method createW3CEndpointReference;
   private static final Method providerGetPort;
   private static final Method readEndpointReference;
   private static final Method createDispatchReferenceJaxB;
   private static final Method createDispatchReferenceClass;
   private static final Method createDispatchInterface;
   private static final Method createDispatchJaxBContext;
   private static final Method serviceGetPortByEndpointReference;
   private static final Method serviceGetPortByQName;
   private static final Method serviceGetPortByInterface;

   public static void beforeCreate(List<PortRefData> portRefData, JaxWsServiceReference.WebServiceClientCustomizer customizer, Properties properties) {
      String oldProperty = JavaSecurityManagers.getSystemProperty(JAXWSPROVIDER_PROPERTY);
      if (oldProperty != null && !oldProperty.equals(ProviderWrapper.class.getName())) {
         JavaSecurityManagers.setSystemProperty("openejb." + JAXWSPROVIDER_PROPERTY, oldProperty);
         JavaSecurityManagers.setSystemProperty(JAXWSPROVIDER_PROPERTY, ProviderWrapper.class.getName());
      }

      if (oldProperty == null || !oldProperty.equals(ProviderWrapper.class.getName())) {
         JavaSecurityManagers.setSystemProperty(JAXWSPROVIDER_PROPERTY, ProviderWrapper.class.getName());
      }

      ClassLoader oldClassLoader = Thread.currentThread().getContextClassLoader();
      if (oldClassLoader != null) {
         Thread.currentThread().setContextClassLoader(new ProviderClassLoader(oldClassLoader));
      } else {
         Thread.currentThread().setContextClassLoader(new ProviderClassLoader());
      }

      threadPortRefs.set(new ProviderWrapperData(portRefData, oldClassLoader, customizer, properties));
   }

   public static void afterCreate() {
      Thread.currentThread().setContextClassLoader(((ProviderWrapperData)threadPortRefs.get()).callerClassLoader);
      threadPortRefs.set((Object)null);
   }

   public ProviderWrapper() {
      this.portRefs = threadPortRefs.get() == null ? null : ((ProviderWrapperData)threadPortRefs.get()).portRefData;
   }

   public Provider getDelegate() {
      return this.delegate;
   }

   public ServiceDelegate createServiceDelegate(URL wsdlDocumentLocation, QName serviceName, Class serviceClass) {
      ServiceDelegate serviceDelegate = this.delegate.createServiceDelegate(wsdlDocumentLocation, serviceName, serviceClass);
      if (threadPortRefs.get() != null) {
         serviceDelegate = new ServiceDelegateWrapper(serviceDelegate);
      }

      return serviceDelegate;
   }

   public Endpoint createEndpoint(String bindingId, Object implementor) {
      return this.delegate.createEndpoint(bindingId, implementor);
   }

   public Endpoint createAndPublishEndpoint(String address, Object implementor) {
      return this.delegate.createAndPublishEndpoint(address, implementor);
   }

   public W3CEndpointReference createW3CEndpointReference(String address, QName serviceName, QName portName, List<Element> metadata, String wsdlDocumentLocation, List<Element> referenceParameters) {
      return (W3CEndpointReference)invoke21Delegate(this.delegate, createW3CEndpointReference, address, serviceName, portName, metadata, wsdlDocumentLocation, referenceParameters);
   }

   public EndpointReference readEndpointReference(Source source) {
      return (EndpointReference)invoke21Delegate(this.delegate, readEndpointReference, source);
   }

   public <T> T getPort(EndpointReference endpointReference, Class<T> serviceEndpointInterface, WebServiceFeature... features) {
      return (T)invoke21Delegate(this.delegate, providerGetPort, endpointReference, serviceEndpointInterface, features);
   }

   private static Provider findProvider() {
      ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
      if (classLoader == null) {
         classLoader = ClassLoader.getSystemClassLoader();
      }

      String providerClass = JavaSecurityManagers.getSystemProperty("openejb." + JAXWSPROVIDER_PROPERTY);
      Provider provider = createProviderInstance(providerClass, classLoader);
      if (provider != null) {
         return provider;
      } else {
         try {
            for(URL url : Collections.list(classLoader.getResources("META-INF/services/" + JAXWSPROVIDER_PROPERTY))) {
               BufferedReader in = null;

               try {
                  in = new BufferedReader(new InputStreamReader(url.openStream()));
                  providerClass = in.readLine();
                  provider = createProviderInstance(providerClass, classLoader);
                  if (provider != null) {
                     Provider var6 = provider;
                     return var6;
                  }
               } catch (Exception var32) {
               } finally {
                  if (in != null) {
                     try {
                        in.close();
                     } catch (IOException var30) {
                     }
                  }

               }
            }
         } catch (Exception var34) {
         }

         String javaHome = JavaSecurityManagers.getSystemProperty("java.home");
         File jaxrpcPropertiesFile = new File(new File(javaHome, "lib"), "jaxrpc.properties");
         if (jaxrpcPropertiesFile.exists()) {
            try {
               Properties properties = IO.readProperties(jaxrpcPropertiesFile);
               providerClass = properties.getProperty(JAXWSPROVIDER_PROPERTY);
               provider = createProviderInstance(providerClass, classLoader);
               if (provider != null) {
                  return provider;
               }
            } catch (Exception var29) {
            }
         }

         providerClass = JavaSecurityManagers.getSystemProperty(JAXWSPROVIDER_PROPERTY);
         provider = createProviderInstance(providerClass, classLoader);
         if (provider != null) {
            return provider;
         } else {
            Provider var45;
            try {
               JavaSecurityManagers.removeSystemProperty(JAXWSPROVIDER_PROPERTY);
               provider = Provider.provider();
               if (provider == null || provider.getClass().getName().equals(ProviderWrapper.class.getName())) {
                  throw new WebServiceException("No " + JAXWSPROVIDER_PROPERTY + " implementation found");
               }

               var45 = provider;
            } finally {
               JavaSecurityManagers.setSystemProperty(JAXWSPROVIDER_PROPERTY, providerClass);
            }

            return var45;
         }
      }
   }

   private static Provider createProviderInstance(String providerClass, ClassLoader classLoader) {
      if (providerClass != null && providerClass.length() > 0 && !providerClass.equals(ProviderWrapper.class.getName())) {
         try {
            Class<? extends Provider> clazz = classLoader.loadClass(providerClass).asSubclass(Provider.class);
            return (Provider)clazz.newInstance();
         } catch (Throwable e) {
            logger.warning("Unable to construct provider implementation " + providerClass, e);
         }
      }

      return null;
   }

   private static Object invoke21Delegate(Object delegate, Method method, Object... args) {
      if (method == null) {
         throw new UnsupportedOperationException("JaxWS 2.1 APIs are not supported");
      } else {
         try {
            return method.invoke(delegate, args);
         } catch (IllegalAccessException e) {
            throw new WebServiceException(e);
         } catch (InvocationTargetException e) {
            if (e.getCause() != null) {
               throw new WebServiceException(e.getCause());
            } else {
               throw new WebServiceException(e);
            }
         }
      }
   }

   static {
      logger = Logger.getInstance(LogCategory.OPENEJB_WS, ProviderWrapper.class);
      JAXWSPROVIDER_PROPERTY = Provider.class.getName();
      threadPortRefs = new ThreadLocal();
      Method method = null;

      try {
         method = Provider.class.getMethod("createW3CEndpointReference", String.class, QName.class, QName.class, List.class, String.class, List.class);
      } catch (NoSuchMethodException var11) {
      }

      createW3CEndpointReference = method;
      method = null;

      try {
         method = Provider.class.getMethod("getPort", EndpointReference.class, Class.class, WebServiceFeature[].class);
      } catch (NoSuchMethodException var10) {
      }

      providerGetPort = method;
      method = null;

      try {
         method = Provider.class.getMethod("readEndpointReference", Source.class);
      } catch (NoSuchMethodException var9) {
      }

      readEndpointReference = method;
      method = null;

      try {
         method = ServiceDelegate.class.getMethod("createDispatch", EndpointReference.class, JAXBContext.class, Service.Mode.class, WebServiceFeature[].class);
      } catch (NoSuchMethodException var8) {
      }

      createDispatchReferenceJaxB = method;
      method = null;

      try {
         method = ServiceDelegate.class.getMethod("createDispatch", EndpointReference.class, Class.class, Service.Mode.class, WebServiceFeature[].class);
      } catch (NoSuchMethodException var7) {
      }

      createDispatchReferenceClass = method;
      method = null;

      try {
         method = ServiceDelegate.class.getMethod("createDispatch", QName.class, JAXBContext.class, Service.Mode.class, WebServiceFeature[].class);
      } catch (NoSuchMethodException var6) {
      }

      createDispatchJaxBContext = method;
      method = null;

      try {
         method = ServiceDelegate.class.getMethod("createDispatch", QName.class, Class.class, Service.Mode.class, WebServiceFeature[].class);
      } catch (NoSuchMethodException var5) {
      }

      createDispatchInterface = method;
      method = null;

      try {
         method = ServiceDelegate.class.getMethod("getPort", EndpointReference.class, Class.class, WebServiceFeature[].class);
      } catch (NoSuchMethodException var4) {
      }

      serviceGetPortByEndpointReference = method;
      method = null;

      try {
         method = ServiceDelegate.class.getMethod("getPort", QName.class, Class.class, WebServiceFeature[].class);
      } catch (NoSuchMethodException var3) {
      }

      serviceGetPortByQName = method;
      method = null;

      try {
         method = ServiceDelegate.class.getMethod("getPort", Class.class, WebServiceFeature[].class);
      } catch (NoSuchMethodException var2) {
      }

      serviceGetPortByInterface = method;
   }

   private static class ProviderWrapperData {
      private final List<PortRefData> portRefData;
      private final ClassLoader callerClassLoader;
      private final JaxWsServiceReference.WebServiceClientCustomizer customizer;
      private final Properties properties;

      public ProviderWrapperData(List<PortRefData> portRefData, ClassLoader callerClassLoader, JaxWsServiceReference.WebServiceClientCustomizer customizer, Properties properties) {
         this.portRefData = portRefData;
         this.callerClassLoader = callerClassLoader;
         this.customizer = customizer;
         this.properties = properties;
      }
   }

   private class ServiceDelegateWrapper extends ServiceDelegate {
      private final ServiceDelegate serviceDelegate;
      private final JaxWsServiceReference.WebServiceClientCustomizer customizer;
      private final Properties configuration;

      public ServiceDelegateWrapper(ServiceDelegate serviceDelegate) {
         this.serviceDelegate = serviceDelegate;
         ProviderWrapperData providerWrapperData = (ProviderWrapperData)ProviderWrapper.threadPortRefs.get();
         if (providerWrapperData != null) {
            this.customizer = providerWrapperData.customizer;
            this.configuration = providerWrapperData.properties;
         } else {
            this.customizer = null;
            this.configuration = null;
         }

      }

      private <T> T customizePort(T port) {
         if (this.customizer != null && this.configuration != null) {
            this.customizer.customize(port, this.configuration);
         }

         return port;
      }

      public <T> T getPort(QName portName, Class<T> serviceEndpointInterface) {
         T t = (T)this.serviceDelegate.getPort(portName, serviceEndpointInterface);
         this.setProperties((BindingProvider)t, portName);
         return (T)this.customizePort(t);
      }

      public <T> T getPort(Class<T> serviceEndpointInterface) {
         T t = (T)this.serviceDelegate.getPort(serviceEndpointInterface);
         QName qname = null;
         if (serviceEndpointInterface.isAnnotationPresent(WebService.class)) {
            WebService webService = (WebService)serviceEndpointInterface.getAnnotation(WebService.class);
            String targetNamespace = webService.targetNamespace();
            String name = webService.name();
            if (targetNamespace != null && targetNamespace.length() > 0 && name != null && name.length() > 0) {
               qname = new QName(targetNamespace, name);
            }
         }

         this.setProperties((BindingProvider)t, qname);
         return (T)this.customizePort(t);
      }

      public void addPort(QName portName, String bindingId, String endpointAddress) {
         this.serviceDelegate.addPort(portName, bindingId, endpointAddress);
      }

      public <T> Dispatch<T> createDispatch(QName portName, Class<T> type, Service.Mode mode) {
         Dispatch<T> dispatch = this.serviceDelegate.createDispatch(portName, type, mode);
         this.setProperties(dispatch, portName);
         return dispatch;
      }

      public Dispatch<Object> createDispatch(QName portName, JAXBContext context, Service.Mode mode) {
         Dispatch<Object> dispatch = this.serviceDelegate.createDispatch(portName, context, mode);
         this.setProperties(dispatch, portName);
         return dispatch;
      }

      public <T> Dispatch<T> createDispatch(QName portName, Class<T> type, Service.Mode mode, WebServiceFeature... features) {
         return (Dispatch)ProviderWrapper.invoke21Delegate(this.serviceDelegate, ProviderWrapper.createDispatchInterface, portName, type, mode, features);
      }

      public Dispatch<Object> createDispatch(QName portName, JAXBContext context, Service.Mode mode, WebServiceFeature... features) {
         return (Dispatch)ProviderWrapper.invoke21Delegate(this.serviceDelegate, ProviderWrapper.createDispatchJaxBContext, portName, context, mode, features);
      }

      public Dispatch<Object> createDispatch(EndpointReference endpointReference, JAXBContext context, Service.Mode mode, WebServiceFeature... features) {
         return (Dispatch)ProviderWrapper.invoke21Delegate(this.serviceDelegate, ProviderWrapper.createDispatchReferenceJaxB, endpointReference, context, mode, features);
      }

      public <T> Dispatch<T> createDispatch(EndpointReference endpointReference, Class<T> type, Service.Mode mode, WebServiceFeature... features) {
         return (Dispatch)ProviderWrapper.invoke21Delegate(this.serviceDelegate, ProviderWrapper.createDispatchReferenceClass, endpointReference, type, mode, features);
      }

      public <T> T getPort(QName portName, Class<T> serviceEndpointInterface, WebServiceFeature... features) {
         return (T)this.customizePort(ProviderWrapper.invoke21Delegate(this.serviceDelegate, ProviderWrapper.serviceGetPortByQName, portName, serviceEndpointInterface, features));
      }

      public <T> T getPort(EndpointReference endpointReference, Class<T> serviceEndpointInterface, WebServiceFeature... features) {
         return (T)this.customizePort(ProviderWrapper.invoke21Delegate(this.serviceDelegate, ProviderWrapper.serviceGetPortByEndpointReference, endpointReference, serviceEndpointInterface, features));
      }

      public <T> T getPort(Class<T> serviceEndpointInterface, WebServiceFeature... features) {
         return (T)this.customizePort(ProviderWrapper.invoke21Delegate(this.serviceDelegate, ProviderWrapper.serviceGetPortByInterface, serviceEndpointInterface, features));
      }

      public QName getServiceName() {
         return this.serviceDelegate.getServiceName();
      }

      public Iterator<QName> getPorts() {
         return this.serviceDelegate.getPorts();
      }

      public URL getWSDLDocumentLocation() {
         return this.serviceDelegate.getWSDLDocumentLocation();
      }

      public HandlerResolver getHandlerResolver() {
         return this.serviceDelegate.getHandlerResolver();
      }

      public void setHandlerResolver(HandlerResolver handlerResolver) {
         this.serviceDelegate.setHandlerResolver(handlerResolver);
      }

      public Executor getExecutor() {
         return this.serviceDelegate.getExecutor();
      }

      public void setExecutor(Executor executor) {
         this.serviceDelegate.setExecutor(executor);
      }

      private void setProperties(BindingProvider proxy, QName qname) {
         for(PortRefData portRef : ProviderWrapper.this.portRefs) {
            Class intf = null;
            if (portRef.getServiceEndpointInterface() != null) {
               try {
                  intf = proxy.getClass().getClassLoader().loadClass(portRef.getServiceEndpointInterface());
               } catch (Exception var11) {
               }
            }

            if (qname != null && qname.equals(portRef.getQName()) || intf != null && intf.isInstance(proxy)) {
               if (!portRef.getAddresses().isEmpty()) {
                  proxy.getRequestContext().put("javax.xml.ws.service.endpoint.address", portRef.getAddresses().get(0));
               }

               boolean enableMTOM = portRef.isEnableMtom();
               if (enableMTOM && proxy.getBinding() instanceof SOAPBinding) {
                  ((SOAPBinding)proxy.getBinding()).setMTOMEnabled(enableMTOM);
               }

               for(Map.Entry<Object, Object> entry : portRef.getProperties().entrySet()) {
                  String name = (String)entry.getKey();
                  String value = (String)entry.getValue();
                  proxy.getRequestContext().put(name, value);
               }

               return;
            }
         }

      }
   }

   private static class ProviderClassLoader extends ClassLoader {
      private static final String PROVIDER_RESOURCE;
      private static final URL PROVIDER_URL;

      public ProviderClassLoader() {
      }

      public ProviderClassLoader(ClassLoader parent) {
         super(parent);
      }

      public Enumeration<URL> getResources(String name) throws IOException {
         Enumeration<URL> resources = super.getResources(name);
         if (PROVIDER_RESOURCE.equals(name)) {
            ArrayList<URL> list = new ArrayList();
            list.add(PROVIDER_URL);
            list.addAll(Collections.list(resources));
            resources = Collections.enumeration(list);
         }

         return resources;
      }

      public URL getResource(String name) {
         return PROVIDER_RESOURCE.equals(name) ? PROVIDER_URL : super.getResource(name);
      }

      static {
         PROVIDER_RESOURCE = "META-INF/services/" + ProviderWrapper.JAXWSPROVIDER_PROPERTY;

         try {
            File tempFile = null;

            try {
               tempFile = File.createTempFile("openejb-jaxws-provider", "tmp");
            } catch (Throwable var15) {
               File tmp = new File("tmp");
               if (!tmp.exists() && !tmp.mkdirs()) {
                  throw new IOException("Failed to create local tmp directory: " + tmp.getAbsolutePath());
               }

               tempFile = File.createTempFile("openejb-jaxws-provider", "tmp", tmp);
            }

            tempFile.deleteOnExit();
            OutputStream out = IO.write(tempFile);
            Throwable var18 = null;

            try {
               out.write(ProviderWrapper.class.getName().getBytes());
            } catch (Throwable var13) {
               var18 = var13;
               throw var13;
            } finally {
               if (out != null) {
                  if (var18 != null) {
                     try {
                        out.close();
                     } catch (Throwable var12) {
                        var18.addSuppressed(var12);
                     }
                  } else {
                     out.close();
                  }
               }

            }

            PROVIDER_URL = tempFile.toURI().toURL();
         } catch (IOException var16) {
            throw new OpenEJBRuntimeException("Cound not create openejb-jaxws-provider file");
         }
      }
   }
}
