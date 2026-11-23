package org.apache.openejb.core.webservices;

import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;
import javax.jws.WebService;
import javax.xml.namespace.QName;
import javax.xml.ws.BindingType;
import javax.xml.ws.WebServiceClient;
import javax.xml.ws.WebServiceProvider;
import org.apache.openejb.OpenEJBRuntimeException;

public final class JaxWsUtils {
   private static final Map<String, String> BINDING_MAP = new HashMap();

   private JaxWsUtils() {
   }

   public static QName getPortType(Class<?> seiClass) {
      WebService webService = (WebService)seiClass.getAnnotation(WebService.class);
      if (webService == null) {
         return null;
      } else {
         String localName = webService.name();
         if (localName == null || localName.length() == 0) {
            localName = seiClass.getSimpleName();
         }

         String namespace = webService.targetNamespace();
         return new QName(getNamespace(seiClass, namespace), localName);
      }
   }

   public static String getBindingURI(String token) {
      if (token != null) {
         if (token.startsWith("##")) {
            String uri = (String)BINDING_MAP.get(token);
            if (uri == null) {
               throw new IllegalArgumentException("Unsupported binding token: " + token);
            } else {
               return uri;
            }
         } else {
            return token;
         }
      } else {
         return (String)BINDING_MAP.get("##SOAP11_HTTP");
      }
   }

   public static boolean isWebService(Class clazz) {
      return (clazz.isAnnotationPresent(WebService.class) || clazz.isAnnotationPresent(WebServiceProvider.class)) && isProperWebService(clazz);
   }

   private static boolean isProperWebService(Class clazz) {
      int modifiers = clazz.getModifiers();
      return Modifier.isPublic(modifiers) && !Modifier.isFinal(modifiers) && !Modifier.isAbstract(modifiers);
   }

   public static String getServiceName(Class clazz) {
      return getServiceQName(clazz).getLocalPart();
   }

   private static String getServiceName(Class clazz, String name) {
      return name != null && name.trim().length() != 0 ? name.trim() : clazz.getSimpleName() + "Service";
   }

   private static String getPortName(Class clazz, String name, String portName) {
      if (portName != null && portName.trim().length() != 0) {
         return portName.trim();
      } else {
         return name != null && name.trim().length() != 0 ? name + "Port" : clazz.getSimpleName() + "Port";
      }
   }

   private static String getNamespace(Class clazz, String namespace) {
      if (namespace != null && namespace.trim().length() != 0) {
         return namespace.trim();
      } else {
         Package pkg = clazz.getPackage();
         return pkg == null ? null : getNamespace(pkg.getName());
      }
   }

   private static String getNamespace(String packageName) {
      if (packageName != null && packageName.length() != 0) {
         StringTokenizer tokenizer = new StringTokenizer(packageName, ".");
         String[] tokens;
         if (tokenizer.countTokens() == 0) {
            tokens = new String[0];
         } else {
            tokens = new String[tokenizer.countTokens()];

            for(int i = tokenizer.countTokens() - 1; i >= 0; --i) {
               tokens[i] = tokenizer.nextToken();
            }
         }

         StringBuilder namespace = new StringBuilder("http://");
         String dot = "";

         for(int i = 0; i < tokens.length; ++i) {
            if (i == 1) {
               dot = ".";
            }

            namespace.append(dot).append(tokens[i]);
         }

         namespace.append('/');
         return namespace.toString();
      } else {
         return null;
      }
   }

   private static QName getServiceQName(Class clazz, String namespace, String name) {
      return new QName(getNamespace(clazz, namespace), getServiceName(clazz, name));
   }

   public static QName getServiceQName(Class<?> clazz) {
      WebService webService = (WebService)clazz.getAnnotation(WebService.class);
      if (webService != null) {
         return getServiceQName(clazz, webService.targetNamespace(), webService.serviceName());
      } else {
         WebServiceProvider webServiceProvider = (WebServiceProvider)clazz.getAnnotation(WebServiceProvider.class);
         if (webServiceProvider != null) {
            return getServiceQName(clazz, webServiceProvider.targetNamespace(), webServiceProvider.serviceName());
         } else {
            WebServiceClient webServiceClient = (WebServiceClient)clazz.getAnnotation(WebServiceClient.class);
            if (webServiceClient != null) {
               return getServiceQName(clazz, webServiceClient.targetNamespace(), webServiceClient.name());
            } else {
               throw new IllegalArgumentException("The " + clazz.getName() + " is not annotated");
            }
         }
      }
   }

   private static QName getPortQName(Class<?> clazz, String namespace, String name, String portName) {
      return new QName(getNamespace(clazz, namespace), getPortName(clazz, name, portName));
   }

   public static QName getPortQName(Class<?> clazz) {
      WebService webService = (WebService)clazz.getAnnotation(WebService.class);
      if (webService != null) {
         return getPortQName(clazz, webService.targetNamespace(), webService.name(), webService.portName());
      } else {
         WebServiceProvider webServiceProvider = (WebServiceProvider)clazz.getAnnotation(WebServiceProvider.class);
         if (webServiceProvider != null) {
            return getPortQName(clazz, webServiceProvider.targetNamespace(), (String)null, webServiceProvider.portName());
         } else {
            throw new IllegalArgumentException("The " + clazz.getName() + " is not annotated");
         }
      }
   }

   public static String getName(Class<?> clazz) {
      WebService webService = (WebService)clazz.getAnnotation(WebService.class);
      if (webService != null) {
         String sei = webService.endpointInterface();
         if (sei != null && sei.trim().length() != 0) {
            try {
               Class seiClass = clazz.getClassLoader().loadClass(sei.trim());
               return getNameFromInterface(seiClass);
            } catch (ClassNotFoundException e) {
               throw new OpenEJBRuntimeException("Unable to load SEI class: " + sei, e);
            }
         } else {
            return getName(clazz, webService.name());
         }
      } else {
         WebServiceProvider webServiceProvider = (WebServiceProvider)clazz.getAnnotation(WebServiceProvider.class);
         if (webServiceProvider != null) {
            return clazz.getName();
         } else {
            throw new IllegalArgumentException("The " + clazz.getName() + " is not annotated");
         }
      }
   }

   private static String getNameFromInterface(Class<?> intf) {
      WebService webService = (WebService)intf.getAnnotation(WebService.class);
      if (webService != null) {
         return getName(intf, webService.name());
      } else {
         throw new IllegalArgumentException("The " + intf.getName() + " is not annotated");
      }
   }

   private static String getName(Class clazz, String name) {
      if (name != null) {
         name = name.trim();
         if (name.length() > 0) {
            return name;
         }
      }

      return clazz.getSimpleName();
   }

   private static String getWsdlLocation(Class<?> clazz) {
      WebService webService = (WebService)clazz.getAnnotation(WebService.class);
      if (webService != null) {
         String wsdlLocation = webService.wsdlLocation().trim();
         if (wsdlLocation.length() == 0) {
            wsdlLocation = null;
         }

         return wsdlLocation;
      } else {
         WebServiceClient webServiceClient = (WebServiceClient)clazz.getAnnotation(WebServiceClient.class);
         if (webServiceClient != null) {
            String wsdlLocation = webServiceClient.wsdlLocation().trim();
            if (wsdlLocation.length() == 0) {
               wsdlLocation = null;
            }

            return wsdlLocation;
         } else {
            WebServiceProvider webServiceProvider = (WebServiceProvider)clazz.getAnnotation(WebServiceProvider.class);
            if (webServiceProvider != null) {
               String wsdlLocation = webServiceProvider.wsdlLocation().trim();
               if (wsdlLocation.length() == 0) {
                  wsdlLocation = null;
               }

               return wsdlLocation;
            } else {
               return null;
            }
         }
      }
   }

   public static String getServiceInterface(Class<?> clazz) {
      WebService webService = (WebService)clazz.getAnnotation(WebService.class);
      String endpointInterface = null;
      if (webService != null && webService.endpointInterface() != null) {
         endpointInterface = webService.endpointInterface().trim();
         if (endpointInterface.length() != 0) {
            return endpointInterface;
         }

         endpointInterface = null;
      }

      for(Class<?> intf : clazz.getInterfaces()) {
         webService = (WebService)intf.getAnnotation(WebService.class);
         if (webService != null) {
            if (endpointInterface != null) {
               endpointInterface = null;
               break;
            }

            endpointInterface = intf.getName();
         }
      }

      return endpointInterface;
   }

   public static String getServiceWsdlLocation(Class<?> clazz, ClassLoader loader) {
      String wsdlLocation = getWsdlLocation(clazz);
      if (wsdlLocation != null && !wsdlLocation.equals("")) {
         return wsdlLocation;
      } else {
         String serviceInterfaceClassName = getServiceInterface(clazz);
         if (serviceInterfaceClassName != null && !serviceInterfaceClassName.equals("")) {
            try {
               Class serviceInterfaceClass = loader.loadClass(serviceInterfaceClassName);
               return getWsdlLocation(serviceInterfaceClass);
            } catch (Exception var5) {
            }
         }

         return null;
      }
   }

   public static boolean containsWsdlLocation(Class<?> clazz, ClassLoader loader) {
      String wsdlLocSEIFromAnnotation = getServiceWsdlLocation(clazz, loader);
      return wsdlLocSEIFromAnnotation != null && !wsdlLocSEIFromAnnotation.equals("");
   }

   public static String getBindingUriFromAnn(Class<?> clazz) {
      BindingType bindingType = (BindingType)clazz.getAnnotation(BindingType.class);
      return bindingType != null ? bindingType.value() : null;
   }

   static {
      BINDING_MAP.put("##SOAP11_HTTP", "http://schemas.xmlsoap.org/wsdl/soap/http");
      BINDING_MAP.put("##SOAP12_HTTP", "http://www.w3.org/2003/05/soap/bindings/HTTP/");
      BINDING_MAP.put("##SOAP11_HTTP_MTOM", "http://schemas.xmlsoap.org/wsdl/soap/http?mtom=true");
      BINDING_MAP.put("##SOAP12_HTTP_MTOM", "http://www.w3.org/2003/05/soap/bindings/HTTP/?mtom=true");
      BINDING_MAP.put("##XML_HTTP", "http://www.w3.org/2004/08/wsdl/http");
   }
}
