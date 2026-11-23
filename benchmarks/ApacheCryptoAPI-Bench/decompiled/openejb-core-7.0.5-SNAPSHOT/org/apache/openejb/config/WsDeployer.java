package org.apache.openejb.config;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import javax.jws.HandlerChain;
import javax.wsdl.Definition;
import javax.wsdl.Port;
import javax.wsdl.extensions.http.HTTPAddress;
import javax.wsdl.extensions.soap.SOAPAddress;
import javax.wsdl.factory.WSDLFactory;
import javax.wsdl.xml.WSDLReader;
import javax.xml.namespace.QName;
import javax.xml.ws.soap.MTOM;
import org.apache.openejb.OpenEJBException;
import org.apache.openejb.core.webservices.JaxWsUtils;
import org.apache.openejb.core.webservices.WsdlResolver;
import org.apache.openejb.jee.EnterpriseBean;
import org.apache.openejb.jee.HandlerChains;
import org.apache.openejb.jee.JndiConsumer;
import org.apache.openejb.jee.PortComponent;
import org.apache.openejb.jee.ServiceImplBean;
import org.apache.openejb.jee.ServiceRef;
import org.apache.openejb.jee.Servlet;
import org.apache.openejb.jee.ServletMapping;
import org.apache.openejb.jee.SessionBean;
import org.apache.openejb.jee.SessionType;
import org.apache.openejb.jee.WebApp;
import org.apache.openejb.jee.WebserviceDescription;
import org.apache.openejb.jee.Webservices;
import org.apache.openejb.jee.oejb3.EjbDeployment;
import org.apache.openejb.loader.IO;
import org.apache.openejb.util.LogCategory;
import org.apache.openejb.util.Logger;
import org.apache.openejb.util.URLs;
import org.xml.sax.InputSource;

public class WsDeployer implements DynamicDeployer {
   public static final Logger logger;

   public AppModule deploy(AppModule appModule) throws OpenEJBException {
      for(EjbModule ejbModule : appModule.getEjbModules()) {
         this.processPorts(ejbModule);
      }

      for(WebModule webModule : appModule.getWebModules()) {
         this.processPorts(webModule);
      }

      for(EjbModule ejbModule : appModule.getEjbModules()) {
         for(EnterpriseBean enterpriseBean : ejbModule.getEjbJar().getEnterpriseBeans()) {
            this.resolveServiceRefs(ejbModule, enterpriseBean);
         }
      }

      for(WebModule webModule : appModule.getWebModules()) {
         this.resolveServiceRefs(webModule, webModule.getWebApp());
      }

      for(ClientModule clientModule : appModule.getClientModules()) {
         this.resolveServiceRefs(clientModule, clientModule.getApplicationClient());
      }

      return appModule;
   }

   private void resolveServiceRefs(DeploymentModule module, JndiConsumer jndiConsumer) {
      for(ServiceRef serviceRef : jndiConsumer.getServiceRef()) {
         if (serviceRef.getServiceQname() == null && serviceRef.getWsdlFile() != null) {
            try {
               String wsdlFile = serviceRef.getWsdlFile();
               Definition definition = this.getWsdl(module, wsdlFile);
               Set serviceQNames = definition.getServices().keySet();
               if (serviceQNames.size() == 1) {
                  QName serviceQName = (QName)serviceQNames.iterator().next();
                  serviceRef.setServiceQname(serviceQName);
               } else if (serviceQNames.isEmpty()) {
                  logger.error("The service-ref " + serviceRef.getName() + " must define service-qname because the wsdl-file " + serviceRef.getWsdlFile() + " does not constain any service definitions ");
               } else {
                  logger.error("The service-ref " + serviceRef.getName() + " must define service-qname because the wsdl-file " + serviceRef.getWsdlFile() + " constain more then one service definitions " + serviceQNames);
               }
            } catch (Exception var9) {
               logger.error("Unable to read wsdl file " + serviceRef.getWsdlFile());
            }
         }
      }

   }

   private void processPorts(WebModule webModule) throws OpenEJBException {
      Webservices webservices = webModule.getWebservices();
      Map<String, PortComponent> portMap = new TreeMap();
      if (webservices != null) {
         for(WebserviceDescription webserviceDescription : webservices.getWebserviceDescription()) {
            for(PortComponent portComponent : webserviceDescription.getPortComponent()) {
               ServiceImplBean serviceImplBean = portComponent.getServiceImplBean();
               if (serviceImplBean != null && serviceImplBean.getServletLink() != null) {
                  portMap.put(serviceImplBean.getServletLink(), portComponent);
               }
            }
         }
      }

      WebApp webApp = webModule.getWebApp();
      Map<String, ServletMapping> servletMappings = new TreeMap();

      for(ServletMapping servletMapping : webApp.getServletMapping()) {
         servletMappings.put(servletMapping.getServletName(), servletMapping);
      }

      for(Servlet servlet : webApp.getServlet()) {
         String className = servlet.getServletClass();
         if (className != null) {
            try {
               Class<?> clazz = webModule.getClassLoader().loadClass(className);
               if (JaxWsUtils.isWebService(clazz)) {
                  ServletMapping servletMapping = (ServletMapping)servletMappings.get(servlet.getServletName());
                  String serviceName = JaxWsUtils.getServiceName(clazz);
                  if (servletMapping == null) {
                     servletMapping = new ServletMapping();
                     servletMapping.setServletName(servlet.getServletName());
                     String location = "/" + serviceName;
                     servletMapping.getUrlPattern().add(location);
                     webApp.getServletMapping().add(servletMapping);
                  }

                  if (webservices == null) {
                     webservices = new Webservices();
                     webModule.setWebservices(webservices);
                  }

                  WebserviceDescription webserviceDescription = (WebserviceDescription)webservices.getWebserviceDescriptionMap().get(serviceName);
                  if (webserviceDescription == null) {
                     webserviceDescription = new WebserviceDescription();
                     webserviceDescription.setWebserviceDescriptionName(serviceName);
                     webservices.getWebserviceDescription().add(webserviceDescription);
                  }

                  PortComponent portComponent = (PortComponent)portMap.get(servlet.getServletName());
                  if (portComponent == null) {
                     portComponent = new PortComponent();
                     portComponent.setPortComponentName(clazz.getSimpleName());
                     ServiceImplBean serviceImplBean = new ServiceImplBean();
                     serviceImplBean.setServletLink(servlet.getServletName());
                     portComponent.setServiceImplBean(serviceImplBean);
                     webserviceDescription.getPortComponent().add(portComponent);
                  }

                  if (portComponent.getId() == null) {
                     portComponent.setId(webModule.getHost() + "." + webModule.getModuleId() + "." + servlet.getServletName());
                  }

                  if (webserviceDescription.getId() == null) {
                     webserviceDescription.setId(webModule.getHost() + "." + webModule.getModuleId() + "." + servlet.getServletName());
                  }

                  if (portComponent.getServiceEndpointInterface() == null) {
                     portComponent.setServiceEndpointInterface(JaxWsUtils.getServiceInterface(clazz));
                  }

                  if (portComponent.getWsdlPort() == null) {
                     portComponent.setWsdlPort(JaxWsUtils.getPortQName(clazz));
                  }

                  if (webserviceDescription.getWsdlFile() == null) {
                     webserviceDescription.setWsdlFile(JaxWsUtils.getServiceWsdlLocation(clazz, webModule.getClassLoader()));
                  }

                  if (portComponent.getWsdlService() == null) {
                     Definition definition = this.getWsdl(webModule, webserviceDescription.getWsdlFile());
                     if (definition != null && definition.getServices().size() == 1) {
                        QName serviceQName = (QName)definition.getServices().keySet().iterator().next();
                        portComponent.setWsdlService(serviceQName);
                     } else {
                        portComponent.setWsdlService(JaxWsUtils.getServiceQName(clazz));
                     }
                  }

                  if (portComponent.getProtocolBinding() == null) {
                     portComponent.setProtocolBinding(JaxWsUtils.getBindingUriFromAnn(clazz));
                  }

                  this.configMtomAnnotation(clazz, portComponent);
                  if ("http://www.w3.org/2003/05/soap/bindings/HTTP/?mtom=true".equals(portComponent.getProtocolBinding()) || "http://schemas.xmlsoap.org/wsdl/soap/http?mtom=true".equals(portComponent.getProtocolBinding())) {
                     portComponent.setEnableMtom(true);
                  }

                  if (portComponent.getHandlerChains() == null) {
                     HandlerChains handlerChains = getHandlerChains(clazz, portComponent.getServiceEndpointInterface(), webModule.getClassLoader());
                     portComponent.setHandlerChains(handlerChains);
                  }
               }
            } catch (Exception e) {
               throw new OpenEJBException("Unable to load servlet class: " + className, e);
            }
         }
      }

   }

   private void configMtomAnnotation(Class<?> clazz, PortComponent portComponent) {
      MTOM mtom = (MTOM)clazz.getAnnotation(MTOM.class);
      if (mtom != null) {
         if (portComponent.getEnableMtom() == null) {
            portComponent.setEnableMtom(mtom.enabled());
         }

         if (portComponent.getMtomThreshold() == null) {
            portComponent.setMtomThreshold(mtom.threshold());
         }
      }

   }

   private void processPorts(EjbModule ejbModule) throws OpenEJBException {
      Webservices webservices = ejbModule.getWebservices();
      Map<String, PortComponent> portMap = new TreeMap();
      if (webservices != null) {
         for(WebserviceDescription webserviceDescription : webservices.getWebserviceDescription()) {
            for(PortComponent portComponent : webserviceDescription.getPortComponent()) {
               ServiceImplBean serviceImplBean = portComponent.getServiceImplBean();
               if (serviceImplBean != null && serviceImplBean.getEjbLink() != null) {
                  portMap.put(serviceImplBean.getEjbLink(), portComponent);
               }
            }
         }
      }

      Map<String, EjbDeployment> deploymentsByEjbName = ejbModule.getOpenejbJar().getDeploymentsByEjbName();

      for(EnterpriseBean enterpriseBean : ejbModule.getEjbJar().getEnterpriseBeans()) {
         if (enterpriseBean instanceof SessionBean) {
            SessionBean sessionBean = (SessionBean)enterpriseBean;
            if (sessionBean.getSessionType() != SessionType.STATEFUL && sessionBean.getSessionType() != SessionType.MANAGED && sessionBean.getServiceEndpoint() != null) {
               EjbDeployment deployment = (EjbDeployment)deploymentsByEjbName.get(sessionBean.getEjbName());
               if (deployment != null) {
                  Class<?> ejbClass;
                  try {
                     ejbClass = ejbModule.getClassLoader().loadClass(sessionBean.getEjbClass());
                  } catch (ClassNotFoundException e) {
                     throw new OpenEJBException("Unable to load ejb class: " + sessionBean.getEjbClass(), e);
                  }

                  if (JaxWsUtils.isWebService(ejbClass)) {
                     if (webservices == null) {
                        webservices = new Webservices();
                        ejbModule.setWebservices(webservices);
                     }

                     WebserviceDescription webserviceDescription = (WebserviceDescription)webservices.getWebserviceDescriptionMap().get(JaxWsUtils.getServiceName(ejbClass));
                     if (webserviceDescription == null) {
                        webserviceDescription = new WebserviceDescription();
                        if (JaxWsUtils.isWebService(ejbClass)) {
                           webserviceDescription.setWebserviceDescriptionName(JaxWsUtils.getServiceName(ejbClass));
                        }

                        webservices.getWebserviceDescription().add(webserviceDescription);
                     }

                     PortComponent portComponent = (PortComponent)portMap.get(sessionBean.getEjbName());
                     if (portComponent == null) {
                        portComponent = new PortComponent();
                        if (webserviceDescription.getPortComponentMap().containsKey(JaxWsUtils.getPortQName(ejbClass).getLocalPart())) {
                           portComponent.setPortComponentName(sessionBean.getEjbName());
                        } else {
                           portComponent.setPortComponentName(JaxWsUtils.getPortQName(ejbClass).getLocalPart());
                        }

                        webserviceDescription.getPortComponent().add(portComponent);
                        ServiceImplBean serviceImplBean = new ServiceImplBean();
                        serviceImplBean.setEjbLink(sessionBean.getEjbName());
                        portComponent.setServiceImplBean(serviceImplBean);
                        if (portComponent.getProtocolBinding() == null) {
                           portComponent.setProtocolBinding(JaxWsUtils.getBindingUriFromAnn(ejbClass));
                        }

                        this.configMtomAnnotation(ejbClass, portComponent);
                        if ("http://www.w3.org/2003/05/soap/bindings/HTTP/?mtom=true".equals(portComponent.getProtocolBinding()) || "http://schemas.xmlsoap.org/wsdl/soap/http?mtom=true".equals(portComponent.getProtocolBinding())) {
                           portComponent.setEnableMtom(true);
                        }
                     }

                     if (portComponent.getId() == null) {
                        portComponent.setId(deployment.getDeploymentId());
                     }

                     if (webserviceDescription.getId() == null) {
                        webserviceDescription.setId(deployment.getDeploymentId());
                     }

                     if (portComponent.getServiceEndpointInterface() == null) {
                        portComponent.setServiceEndpointInterface(sessionBean.getServiceEndpoint());
                     }

                     if (JaxWsUtils.isWebService(ejbClass)) {
                        if (portComponent.getWsdlPort() == null) {
                           portComponent.setWsdlPort(JaxWsUtils.getPortQName(ejbClass));
                        }

                        if (webserviceDescription.getWsdlFile() == null) {
                           webserviceDescription.setWsdlFile(JaxWsUtils.getServiceWsdlLocation(ejbClass, ejbModule.getClassLoader()));
                        }

                        if (portComponent.getWsdlService() == null) {
                           Definition definition = this.getWsdl(ejbModule, webserviceDescription.getWsdlFile());
                           if (definition != null && definition.getServices().size() == 1) {
                              QName serviceQName = (QName)definition.getServices().keySet().iterator().next();
                              portComponent.setWsdlService(serviceQName);
                           } else {
                              portComponent.setWsdlService(JaxWsUtils.getServiceQName(ejbClass));
                           }
                        }

                        if (portComponent.getLocation() == null && webserviceDescription.getWsdlFile() != null) {
                           Definition definition = this.getWsdl(ejbModule, webserviceDescription.getWsdlFile());
                           String locationURI = this.getLocationFromWsdl(definition, portComponent);
                           portComponent.setLocation(locationURI);
                        }

                        if (portComponent.getProtocolBinding() == null) {
                           portComponent.setProtocolBinding(JaxWsUtils.getBindingUriFromAnn(ejbClass));
                        }

                        if (portComponent.getHandlerChains() == null) {
                           HandlerChains handlerChains = getHandlerChains(ejbClass, sessionBean.getServiceEndpoint(), ejbModule.getClassLoader());
                           portComponent.setHandlerChains(handlerChains);
                        }
                     }
                  }
               }
            }
         }
      }

   }

   private Definition getWsdl(DeploymentModule module, String wsdlFile) {
      if (wsdlFile == null) {
         return null;
      } else {
         Object object = module.getAltDDs().get(wsdlFile);
         if (object instanceof Definition) {
            return (Definition)object;
         } else {
            try {
               URL wsdlUrl;
               if (object instanceof URL) {
                  wsdlUrl = (URL)object;
               } else {
                  URL baseUrl = this.getBaseUrl(module);
                  wsdlUrl = new URL(baseUrl, wsdlFile);
               }

               Definition definition = readWsdl(wsdlUrl);
               module.getAltDDs().put(wsdlFile, definition);
               return definition;
            } catch (Exception var8) {
               if (module.getClassLoader() != null) {
                  URL wsdlUrl = module.getClassLoader().getResource(wsdlFile.startsWith("classpath:") ? wsdlFile.substring("classpath:".length()) : wsdlFile);

                  try {
                     Definition definition = readWsdl(wsdlUrl);
                     module.getAltDDs().put(wsdlFile, definition);
                     return definition;
                  } catch (OpenEJBException var7) {
                  }
               }

               logger.error("Unable to read wsdl file " + wsdlFile);
               return null;
            }
         }
      }
   }

   public static Definition readWsdl(URL url) throws OpenEJBException {
      try {
         WSDLFactory factory = WSDLFactory.newInstance();
         WSDLReader reader = factory.newWSDLReader();
         reader.setFeature("javax.wsdl.verbose", true);
         reader.setFeature("javax.wsdl.importDocuments", true);
         WsdlResolver wsdlResolver = new WsdlResolver((new URL(url, ".")).toExternalForm(), new InputSource(IO.read(url)));
         Definition definition = reader.readWSDL(wsdlResolver);
         return definition;
      } catch (IOException e) {
         throw new OpenEJBException("Cannot read the wsdl file: " + url.toExternalForm(), e);
      } catch (Exception e) {
         throw new OpenEJBException("Encountered unknown error parsing the wsdl file: " + url.toExternalForm(), e);
      }
   }

   private URL getBaseUrl(DeploymentModule module) throws MalformedURLException {
      File file = new File(module.getJarLocation());
      if (!file.exists()) {
         return new URL(module.getJarLocation());
      } else {
         URL baseUrl = file.toURI().toURL();
         if (file.isFile()) {
            baseUrl = new URL("jar", (String)null, baseUrl.toExternalForm() + "!/");
         }

         return baseUrl;
      }
   }

   private String getLocationFromWsdl(Definition definition, PortComponent portComponent) {
      if (definition == null) {
         return null;
      } else {
         try {
            javax.wsdl.Service service = definition.getService(portComponent.getWsdlService());
            if (service == null) {
               return null;
            }

            Port port = service.getPort(portComponent.getWsdlPort().getLocalPart());
            if (port == null) {
               return null;
            }

            for(Object element : port.getExtensibilityElements()) {
               if (element instanceof SOAPAddress) {
                  SOAPAddress soapAddress = (SOAPAddress)element;
                  URI uri = URLs.uri(soapAddress.getLocationURI());
                  return uri.getPath();
               }

               if (element instanceof HTTPAddress) {
                  HTTPAddress httpAddress = (HTTPAddress)element;
                  URI uri = URLs.uri(httpAddress.getLocationURI());
                  return uri.getPath();
               }
            }
         } catch (Exception var9) {
         }

         return null;
      }
   }

   public static HandlerChains getHandlerChains(Class<?> declaringClass, String serviceEndpoint, ClassLoader classLoader) throws OpenEJBException {
      HandlerChain handlerChain = (HandlerChain)declaringClass.getAnnotation(HandlerChain.class);
      if (handlerChain == null && serviceEndpoint != null) {
         try {
            declaringClass = classLoader.loadClass(serviceEndpoint);
            handlerChain = (HandlerChain)declaringClass.getAnnotation(HandlerChain.class);
         } catch (ClassNotFoundException var7) {
         }
      }

      HandlerChains handlerChains = null;
      if (handlerChain != null) {
         try {
            URL handlerFileURL = declaringClass.getResource(handlerChain.file());
            handlerChains = ReadDescriptors.readHandlerChains(handlerFileURL);
         } catch (Throwable e) {
            throw new OpenEJBException("Unable to load handler chain file: " + handlerChain.file(), e);
         }
      }

      return handlerChains;
   }

   static {
      logger = Logger.getInstance(LogCategory.OPENEJB_STARTUP, WsDeployer.class.getPackage().getName());
   }
}
