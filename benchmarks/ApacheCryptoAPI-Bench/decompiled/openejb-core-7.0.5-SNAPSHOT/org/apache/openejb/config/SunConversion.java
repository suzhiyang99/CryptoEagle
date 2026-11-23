package org.apache.openejb.config;

import java.io.ByteArrayInputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.TreeMap;
import javax.xml.namespace.QName;
import org.apache.openejb.jee.ApplicationClient;
import org.apache.openejb.jee.CmpField;
import org.apache.openejb.jee.EjbJar;
import org.apache.openejb.jee.EjbRef;
import org.apache.openejb.jee.EnterpriseBean;
import org.apache.openejb.jee.EntityBean;
import org.apache.openejb.jee.JndiReference;
import org.apache.openejb.jee.PersistenceType;
import org.apache.openejb.jee.PortComponent;
import org.apache.openejb.jee.PortComponentRef;
import org.apache.openejb.jee.ServiceImplBean;
import org.apache.openejb.jee.ServiceRef;
import org.apache.openejb.jee.WebApp;
import org.apache.openejb.jee.WebserviceDescription;
import org.apache.openejb.jee.jpa.AttributeOverride;
import org.apache.openejb.jee.jpa.Attributes;
import org.apache.openejb.jee.jpa.Basic;
import org.apache.openejb.jee.jpa.Column;
import org.apache.openejb.jee.jpa.Entity;
import org.apache.openejb.jee.jpa.EntityMappings;
import org.apache.openejb.jee.jpa.Field;
import org.apache.openejb.jee.jpa.Id;
import org.apache.openejb.jee.jpa.JoinColumn;
import org.apache.openejb.jee.jpa.JoinTable;
import org.apache.openejb.jee.jpa.ManyToOne;
import org.apache.openejb.jee.jpa.NamedQuery;
import org.apache.openejb.jee.jpa.OneToMany;
import org.apache.openejb.jee.jpa.OneToOne;
import org.apache.openejb.jee.jpa.PrimaryKeyJoinColumn;
import org.apache.openejb.jee.jpa.RelationField;
import org.apache.openejb.jee.jpa.Table;
import org.apache.openejb.jee.oejb3.EjbDeployment;
import org.apache.openejb.jee.oejb3.EjbLink;
import org.apache.openejb.jee.oejb3.OpenejbJar;
import org.apache.openejb.jee.oejb3.ResourceLink;
import org.apache.openejb.jee.sun.Cmp;
import org.apache.openejb.jee.sun.CmpFieldMapping;
import org.apache.openejb.jee.sun.CmrFieldMapping;
import org.apache.openejb.jee.sun.ColumnName;
import org.apache.openejb.jee.sun.ColumnPair;
import org.apache.openejb.jee.sun.Ejb;
import org.apache.openejb.jee.sun.EntityMapping;
import org.apache.openejb.jee.sun.Finder;
import org.apache.openejb.jee.sun.JaxbSun;
import org.apache.openejb.jee.sun.MessageDestination;
import org.apache.openejb.jee.sun.MessageDestinationRef;
import org.apache.openejb.jee.sun.OneOneFinders;
import org.apache.openejb.jee.sun.PortInfo;
import org.apache.openejb.jee.sun.ResourceEnvRef;
import org.apache.openejb.jee.sun.ResourceRef;
import org.apache.openejb.jee.sun.SecondaryTable;
import org.apache.openejb.jee.sun.StubProperty;
import org.apache.openejb.jee.sun.SunApplication;
import org.apache.openejb.jee.sun.SunApplicationClient;
import org.apache.openejb.jee.sun.SunCmpMapping;
import org.apache.openejb.jee.sun.SunCmpMappings;
import org.apache.openejb.jee.sun.SunEjbJar;
import org.apache.openejb.jee.sun.SunWebApp;
import org.apache.openejb.jee.sun.Web;
import org.apache.openejb.jee.sun.WebserviceEndpoint;
import org.apache.openejb.jee.sun.WsdlPort;
import org.apache.openejb.loader.IO;

public class SunConversion implements DynamicDeployer {
   public AppModule deploy(AppModule appModule) {
      SunApplication sunApplication = this.getSunApplication(appModule);
      if (sunApplication != null) {
         for(Web web : sunApplication.getWeb()) {
            String webUri = web.getWebUri();

            for(WebModule webModule : appModule.getWebModules()) {
               if (webUri.equals(webModule.getModuleId())) {
                  webModule.setContextRoot(web.getContextRoot());
                  break;
               }
            }
         }

         for(ClientModule clientModule : appModule.getClientModules()) {
            ApplicationClient applicationClient = clientModule.getApplicationClient();
            if (applicationClient != null) {
               Map<String, EjbRef> refMap = applicationClient.getEjbRefMap();

               for(org.apache.openejb.jee.sun.EjbRef ref : sunApplication.getEjbRef()) {
                  if (ref.getJndiName() != null) {
                     String refName = ref.getEjbRefName();
                     refName = this.normalize(refName);
                     EjbRef ejbRef = (EjbRef)refMap.get(refName);

                     for(Map.Entry<String, EjbRef> aRef : refMap.entrySet()) {
                        if (refName.equals(((EjbRef)aRef.getValue()).getLookupName())) {
                           ejbRef = (EjbRef)aRef.getValue();
                           break;
                        }
                     }

                     if (ejbRef == null) {
                        ejbRef = new EjbRef();
                        ejbRef.setEjbRefName(refName);
                        refMap.put(refName, ejbRef);
                        applicationClient.getEjbRef().add(ejbRef);
                     }

                     ejbRef.setMappedName(ref.getJndiName());
                  }
               }

               Map<String, JndiReference> resEnvMap = new TreeMap();
               resEnvMap.putAll(applicationClient.getResourceEnvRefMap());
               resEnvMap.putAll(applicationClient.getMessageDestinationRefMap());

               for(ResourceRef ref : sunApplication.getResourceRef()) {
                  if (ref.getJndiName() != null) {
                     String refName = ref.getResRefName();
                     refName = this.normalize(refName);
                     JndiReference resEnvRef = (JndiReference)resEnvMap.get(refName);
                     if (resEnvRef != null) {
                        resEnvRef.setMappedName(ref.getJndiName());
                     }
                  }
               }

               for(ResourceEnvRef ref : sunApplication.getResourceEnvRef()) {
                  if (ref.getJndiName() != null) {
                     String refName = ref.getResourceEnvRefName();
                     refName = this.normalize(refName);
                     JndiReference resEnvRef = (JndiReference)resEnvMap.get(refName);
                     if (resEnvRef != null) {
                        resEnvRef.setMappedName(ref.getJndiName());
                     }
                  }
               }

               for(MessageDestinationRef ref : sunApplication.getMessageDestinationRef()) {
                  if (ref.getJndiName() != null) {
                     String refName = ref.getMessageDestinationRefName();
                     refName = this.normalize(refName);
                     JndiReference resEnvRef = (JndiReference)resEnvMap.get(refName);
                     if (resEnvRef != null) {
                        resEnvRef.setMappedName(ref.getJndiName());
                     }
                  }
               }

               for(MessageDestination destination : sunApplication.getMessageDestination()) {
                  if (destination.getJndiName() != null) {
                     String name = destination.getMessageDestinationName();
                     name = this.normalize(name);
                     JndiReference ref = (JndiReference)resEnvMap.get(name);
                     if (ref != null) {
                        ref.setMappedName(destination.getJndiName());
                     }
                  }
               }

               Map<String, ServiceRef> serviceRefMap = applicationClient.getServiceRefMap();

               for(org.apache.openejb.jee.sun.ServiceRef ref : sunApplication.getServiceRef()) {
                  String refName = ref.getServiceRefName();
                  refName = this.normalize(refName);
                  ServiceRef serviceRef = (ServiceRef)serviceRefMap.get(refName);
                  if (serviceRef != null) {
                     Map<String, PortComponentRef> ports = new TreeMap();

                     for(PortComponentRef portComponentRef : serviceRef.getPortComponentRef()) {
                        ports.put(portComponentRef.getServiceEndpointInterface(), portComponentRef);
                     }

                     for(PortInfo portInfo : ref.getPortInfo()) {
                        PortComponentRef portComponentRef = (PortComponentRef)ports.get(portInfo.getServiceEndpointInterface());
                        if (portComponentRef != null) {
                           WsdlPort wsdlPort = portInfo.getWsdlPort();
                           if (wsdlPort != null) {
                              QName qname = new QName(wsdlPort.getNamespaceURI(), wsdlPort.getLocalpart());
                              portComponentRef.setQName(qname);
                           }

                           for(StubProperty stubProperty : portInfo.getStubProperty()) {
                              String name = stubProperty.getName();
                              String value = stubProperty.getValue();
                              portComponentRef.getProperties().setProperty(name, value);
                           }
                        }
                     }

                     String wsdlOverride = ref.getWsdlOverride();
                     if (wsdlOverride != null && wsdlOverride.length() > 0) {
                        String serviceId = extractServiceId(wsdlOverride);
                        serviceRef.setMappedName(serviceId);
                     }
                  }
               }
            }
         }
      }

      for(EjbModule ejbModule : appModule.getEjbModules()) {
         this.convertModule(ejbModule, appModule.getCmpMappings());
      }

      for(ClientModule clientModule : appModule.getClientModules()) {
         this.convertModule(clientModule);
      }

      for(WebModule webModule : appModule.getWebModules()) {
         this.convertModule(webModule);
      }

      return appModule;
   }

   private SunApplication getSunApplication(AppModule appModule) {
      Object altDD = appModule.getAltDDs().get("sun-application.xml");
      if (altDD instanceof String) {
         try {
            altDD = JaxbSun.unmarshal(SunApplication.class, new ByteArrayInputStream(((String)altDD).getBytes()));
         } catch (Exception var5) {
         }
      }

      if (altDD instanceof URL) {
         try {
            altDD = JaxbSun.unmarshal(SunApplication.class, IO.read((URL)altDD));
         } catch (Exception var4) {
         }
      }

      return altDD instanceof SunApplication ? (SunApplication)altDD : null;
   }

   private SunApplicationClient getSunApplicationClient(ClientModule clientModule) {
      Object altDD = clientModule.getAltDDs().get("sun-application-client.xml");
      if (altDD instanceof String) {
         try {
            altDD = JaxbSun.unmarshal(SunApplicationClient.class, new ByteArrayInputStream(((String)altDD).getBytes()));
         } catch (Exception var5) {
         }
      }

      if (altDD instanceof URL) {
         try {
            altDD = JaxbSun.unmarshal(SunApplicationClient.class, IO.read((URL)altDD));
         } catch (Exception var4) {
         }
      }

      return altDD instanceof SunApplicationClient ? (SunApplicationClient)altDD : null;
   }

   private SunWebApp getSunWebApp(WebModule webModule) {
      Object altDD = webModule.getAltDDs().get("sun-web.xml");
      if (altDD instanceof String) {
         try {
            altDD = JaxbSun.unmarshal(SunWebApp.class, new ByteArrayInputStream(((String)altDD).getBytes()));
         } catch (Exception var5) {
         }
      }

      if (altDD instanceof URL) {
         try {
            altDD = JaxbSun.unmarshal(SunWebApp.class, IO.read((URL)altDD));
         } catch (Exception e) {
            e.printStackTrace();
         }
      }

      return altDD instanceof SunWebApp ? (SunWebApp)altDD : null;
   }

   private SunEjbJar getSunEjbJar(EjbModule ejbModule) {
      Object altDD = ejbModule.getAltDDs().get("sun-ejb-jar.xml");
      if (altDD instanceof String) {
         try {
            altDD = JaxbSun.unmarshal(SunCmpMappings.class, new ByteArrayInputStream(((String)altDD).getBytes()));
         } catch (Exception var5) {
         }
      }

      if (altDD instanceof URL) {
         try {
            altDD = JaxbSun.unmarshal(SunEjbJar.class, IO.read((URL)altDD));
         } catch (Exception e) {
            e.printStackTrace();
         }
      }

      return altDD instanceof SunEjbJar ? (SunEjbJar)altDD : null;
   }

   private SunCmpMappings getSunCmpMappings(EjbModule ejbModule) {
      Object altDD = ejbModule.getAltDDs().get("sun-cmp-mappings.xml");
      if (altDD instanceof String) {
         try {
            altDD = JaxbSun.unmarshal(SunCmpMappings.class, new ByteArrayInputStream(((String)altDD).getBytes()));
         } catch (Exception var5) {
         }
      }

      if (altDD instanceof URL) {
         try {
            altDD = JaxbSun.unmarshal(SunCmpMappings.class, IO.read((URL)altDD));
         } catch (Exception e) {
            e.printStackTrace();
         }
      }

      return altDD instanceof SunCmpMappings ? (SunCmpMappings)altDD : null;
   }

   public void convertModule(ClientModule clientModule) {
      if (clientModule != null) {
         ApplicationClient applicationClient = clientModule.getApplicationClient();
         if (applicationClient != null) {
            SunApplicationClient sunApplicationClient = this.getSunApplicationClient(clientModule);
            if (sunApplicationClient != null) {
               Map<String, EjbRef> refMap = applicationClient.getEjbRefMap();

               for(org.apache.openejb.jee.sun.EjbRef ref : sunApplicationClient.getEjbRef()) {
                  if (ref.getJndiName() != null) {
                     String refName = ref.getEjbRefName();
                     refName = this.normalize(refName);
                     EjbRef ejbRef = (EjbRef)refMap.get(refName);
                     if (ejbRef == null) {
                        ejbRef = new EjbRef();
                        ejbRef.setEjbRefName(refName);
                        refMap.put(refName, ejbRef);
                        applicationClient.getEjbRef().add(ejbRef);
                     }

                     ejbRef.setMappedName(ref.getJndiName());
                  }
               }

               Map<String, JndiReference> resEnvMap = new TreeMap();
               resEnvMap.putAll(applicationClient.getResourceEnvRefMap());
               resEnvMap.putAll(applicationClient.getMessageDestinationRefMap());

               for(ResourceRef ref : sunApplicationClient.getResourceRef()) {
                  if (ref.getJndiName() != null) {
                     String refName = ref.getResRefName();
                     refName = this.normalize(refName);
                     JndiReference resEnvRef = (JndiReference)resEnvMap.get(refName);
                     if (resEnvRef != null) {
                        resEnvRef.setMappedName(ref.getJndiName());
                     }
                  }
               }

               for(ResourceEnvRef ref : sunApplicationClient.getResourceEnvRef()) {
                  if (ref.getJndiName() != null) {
                     String refName = ref.getResourceEnvRefName();
                     refName = this.normalize(refName);
                     JndiReference resEnvRef = (JndiReference)resEnvMap.get(refName);
                     if (resEnvRef != null) {
                        resEnvRef.setMappedName(ref.getJndiName());
                     }
                  }
               }

               for(MessageDestinationRef ref : sunApplicationClient.getMessageDestinationRef()) {
                  if (ref.getJndiName() != null) {
                     String refName = ref.getMessageDestinationRefName();
                     refName = this.normalize(refName);
                     JndiReference resEnvRef = (JndiReference)resEnvMap.get(refName);
                     if (resEnvRef != null) {
                        resEnvRef.setMappedName(ref.getJndiName());
                     }
                  }
               }

               Map<String, ServiceRef> serviceRefMap = applicationClient.getServiceRefMap();

               for(org.apache.openejb.jee.sun.ServiceRef ref : sunApplicationClient.getServiceRef()) {
                  String refName = ref.getServiceRefName();
                  refName = this.normalize(refName);
                  ServiceRef serviceRef = (ServiceRef)serviceRefMap.get(refName);
                  if (serviceRef != null) {
                     Map<String, PortComponentRef> ports = new TreeMap();

                     for(PortComponentRef portComponentRef : serviceRef.getPortComponentRef()) {
                        ports.put(portComponentRef.getServiceEndpointInterface(), portComponentRef);
                     }

                     for(PortInfo portInfo : ref.getPortInfo()) {
                        PortComponentRef portComponentRef = (PortComponentRef)ports.get(portInfo.getServiceEndpointInterface());
                        if (portComponentRef != null) {
                           WsdlPort wsdlPort = portInfo.getWsdlPort();
                           if (wsdlPort != null) {
                              QName qname = new QName(wsdlPort.getNamespaceURI(), wsdlPort.getLocalpart());
                              portComponentRef.setQName(qname);
                           }

                           for(StubProperty stubProperty : portInfo.getStubProperty()) {
                              String name = stubProperty.getName();
                              String value = stubProperty.getValue();
                              portComponentRef.getProperties().setProperty(name, value);
                           }
                        }
                     }

                     String wsdlOverride = ref.getWsdlOverride();
                     if (wsdlOverride != null && wsdlOverride.length() > 0) {
                        String serviceId = extractServiceId(wsdlOverride);
                        serviceRef.setMappedName(serviceId);
                     }
                  }
               }

            }
         }
      }
   }

   private String normalize(String refName) {
      if (!refName.startsWith("java:")) {
         refName = "java:comp/env/" + refName;
      }

      return refName.startsWith("java:/") ? "java:" + refName.substring("java:/".length()) : refName;
   }

   public void convertModule(WebModule webModule) {
      if (webModule != null) {
         WebApp webApp = webModule.getWebApp();
         if (webApp != null) {
            SunWebApp sunWebApp = this.getSunWebApp(webModule);
            if (sunWebApp != null) {
               if (sunWebApp.getContextRoot() != null) {
                  webModule.setContextRoot(sunWebApp.getContextRoot());
               }

               Map<String, JndiReference> refMap = new TreeMap();
               refMap.putAll(webApp.getEjbRefMap());
               refMap.putAll(webApp.getEjbLocalRefMap());

               for(org.apache.openejb.jee.sun.EjbRef ref : sunWebApp.getEjbRef()) {
                  if (ref.getJndiName() != null) {
                     String refName = ref.getEjbRefName();
                     JndiReference ejbRef = (JndiReference)refMap.get(refName);
                     if (ejbRef == null) {
                        ejbRef = new EjbRef();
                        ejbRef.setName(refName);
                        refMap.put(refName, ejbRef);
                        webApp.getEjbRef().add((EjbRef)ejbRef);
                     }

                     ejbRef.setMappedName(ref.getJndiName());
                  }
               }

               Map<String, JndiReference> resEnvMap = new TreeMap();
               resEnvMap.putAll(webApp.getResourceRefMap());
               resEnvMap.putAll(webApp.getResourceEnvRefMap());
               resEnvMap.putAll(webApp.getMessageDestinationRefMap());

               for(ResourceRef ref : sunWebApp.getResourceRef()) {
                  if (ref.getJndiName() != null) {
                     String refName = ref.getResRefName();
                     refName = this.normalize(refName);
                     JndiReference resEnvRef = (JndiReference)resEnvMap.get(refName);
                     if (resEnvRef != null) {
                        resEnvRef.setMappedName(ref.getJndiName());
                     }
                  }
               }

               for(ResourceEnvRef ref : sunWebApp.getResourceEnvRef()) {
                  if (ref.getJndiName() != null) {
                     String refName = ref.getResourceEnvRefName();
                     refName = this.normalize(refName);
                     JndiReference resEnvRef = (JndiReference)resEnvMap.get(refName);
                     if (resEnvRef != null) {
                        resEnvRef.setMappedName(ref.getJndiName());
                     }
                  }
               }

               for(MessageDestinationRef ref : sunWebApp.getMessageDestinationRef()) {
                  if (ref.getJndiName() != null) {
                     String refName = ref.getMessageDestinationRefName();
                     refName = this.normalize(refName);
                     JndiReference resEnvRef = (JndiReference)resEnvMap.get(refName);
                     if (resEnvRef != null) {
                        resEnvRef.setMappedName(ref.getJndiName());
                     }
                  }
               }

               Map<String, ServiceRef> serviceRefMap = webApp.getServiceRefMap();

               for(org.apache.openejb.jee.sun.ServiceRef ref : sunWebApp.getServiceRef()) {
                  String refName = ref.getServiceRefName();
                  refName = this.normalize(refName);
                  ServiceRef serviceRef = (ServiceRef)serviceRefMap.get(refName);
                  if (serviceRef != null) {
                     Map<String, PortComponentRef> ports = new TreeMap();

                     for(PortComponentRef portComponentRef : serviceRef.getPortComponentRef()) {
                        ports.put(portComponentRef.getServiceEndpointInterface(), portComponentRef);
                     }

                     for(PortInfo portInfo : ref.getPortInfo()) {
                        PortComponentRef portComponentRef = (PortComponentRef)ports.get(portInfo.getServiceEndpointInterface());
                        if (portComponentRef != null) {
                           WsdlPort wsdlPort = portInfo.getWsdlPort();
                           if (wsdlPort != null) {
                              QName qname = new QName(wsdlPort.getNamespaceURI(), wsdlPort.getLocalpart());
                              portComponentRef.setQName(qname);
                           }

                           for(StubProperty stubProperty : portInfo.getStubProperty()) {
                              String name = stubProperty.getName();
                              String value = stubProperty.getValue();
                              portComponentRef.getProperties().setProperty(name, value);
                           }
                        }
                     }

                     String wsdlOverride = ref.getWsdlOverride();
                     if (wsdlOverride != null && wsdlOverride.length() > 0) {
                        String serviceId = extractServiceId(wsdlOverride);
                        serviceRef.setMappedName(serviceId);
                     }
                  }
               }

               if (webModule.getWebservices() != null) {
                  Map<String, WebserviceDescription> descriptions = webModule.getWebservices().getWebserviceDescriptionMap();

                  for(org.apache.openejb.jee.sun.WebserviceDescription sunDescription : sunWebApp.getWebserviceDescription()) {
                     WebserviceDescription description = (WebserviceDescription)descriptions.get(sunDescription.getWebserviceDescriptionName());
                     if (description != null) {
                        String serviceId = extractSerivceId(sunDescription.getWsdlPublishLocation(), description.getWsdlFile());
                        if (serviceId != null) {
                           description.setId(serviceId);
                        }
                     }
                  }
               }

            }
         }
      }
   }

   public static String extractServiceId(String location) {
      return extractSerivceId(location, (String)null);
   }

   public static String extractSerivceId(String location, String wsdlFile) {
      if (location == null) {
         return null;
      } else {
         if (location.startsWith("file:")) {
            location = location.replaceFirst("file:[^/]*/", "");
            if (wsdlFile != null) {
               wsdlFile = wsdlFile.replaceFirst("META-INF/wsdl/", "");
               wsdlFile = wsdlFile.replaceFirst("WEB-INF/wsdl/", "");
               location = location + "/" + wsdlFile;
            }

            location = location.replaceFirst("\\.wsdl$", "");
         } else if (location.startsWith("http:") || location.startsWith("https:")) {
            location = location.replaceFirst("http[s]?://[^/]*/", "");
            location = location.replaceFirst("\\?.*$", "");
         }

         if (location.length() == 0) {
            location = null;
         }

         return location;
      }
   }

   public void convertModule(EjbModule ejbModule, EntityMappings entityMappings) {
      Map<String, EntityData> entities = new TreeMap();
      if (entityMappings != null) {
         for(Entity entity : entityMappings.getEntity()) {
            entities.put(entity.getDescription(), new EntityData(entity));
         }
      }

      SunEjbJar sunEjbJar = this.getSunEjbJar(ejbModule);
      this.mergeEjbConfig(ejbModule, sunEjbJar);
      this.mergeEntityMappings(entities, ejbModule.getModuleId(), ejbModule.getEjbJar(), ejbModule.getOpenejbJar(), sunEjbJar);
      SunCmpMappings sunCmpMappings = this.getSunCmpMappings(ejbModule);
      if (sunCmpMappings != null) {
         for(SunCmpMapping sunCmpMapping : sunCmpMappings.getSunCmpMapping()) {
            this.mergeEntityMappings(entities, ejbModule.getModuleId(), ejbModule, entityMappings, sunCmpMapping);
         }
      }

   }

   private void mergeEjbConfig(EjbModule ejbModule, SunEjbJar sunEjbJar) {
      EjbJar ejbJar = ejbModule.getEjbJar();
      OpenejbJar openejbJar = ejbModule.getOpenejbJar();
      if (openejbJar != null) {
         if (sunEjbJar != null) {
            if (sunEjbJar.getEnterpriseBeans() != null) {
               Map<String, Map<String, WebserviceEndpoint>> endpointMap = new HashMap();

               for(Ejb ejb : sunEjbJar.getEnterpriseBeans().getEjb()) {
                  EjbDeployment deployment = (EjbDeployment)openejbJar.getDeploymentsByEjbName().get(ejb.getEjbName());
                  if (deployment != null) {
                     if (ejb.getJndiName() != null) {
                        deployment.setDeploymentId(ejb.getJndiName());
                     }

                     Map<String, EjbLink> linksMap = deployment.getEjbLinksMap();

                     for(org.apache.openejb.jee.sun.EjbRef ref : ejb.getEjbRef()) {
                        if (ref.getJndiName() != null) {
                           String refName = ref.getEjbRefName();
                           refName = this.normalize(refName);
                           EjbLink link = (EjbLink)linksMap.get(refName);
                           if (link == null) {
                              link = new EjbLink();
                              link.setEjbRefName(refName);
                              linksMap.put(refName, link);
                              deployment.getEjbLink().add(link);
                           }

                           link.setDeployentId(ref.getJndiName());
                        }
                     }

                     Map<String, ResourceLink> resourceLinksMap = deployment.getResourceLinksMap();

                     for(ResourceRef ref : ejb.getResourceRef()) {
                        if (ref.getJndiName() != null) {
                           String refName = ref.getResRefName();
                           refName = this.normalize(refName);
                           ResourceLink link = (ResourceLink)resourceLinksMap.get(refName);
                           if (link == null) {
                              link = new ResourceLink();
                              link.setResRefName(refName);
                              resourceLinksMap.put(refName, link);
                              deployment.getResourceLink().add(link);
                           }

                           link.setResId(ref.getJndiName());
                        }
                     }

                     for(ResourceEnvRef ref : ejb.getResourceEnvRef()) {
                        if (ref.getJndiName() != null) {
                           String refName = ref.getResourceEnvRefName();
                           refName = this.normalize(refName);
                           ResourceLink link = (ResourceLink)resourceLinksMap.get(refName);
                           if (link == null) {
                              link = new ResourceLink();
                              link.setResRefName(refName);
                              resourceLinksMap.put(refName, link);
                              deployment.getResourceLink().add(link);
                           }

                           link.setResId(ref.getJndiName());
                        }
                     }

                     for(MessageDestinationRef ref : ejb.getMessageDestinationRef()) {
                        if (ref.getJndiName() != null) {
                           String refName = ref.getMessageDestinationRefName();
                           refName = this.normalize(refName);
                           ResourceLink link = (ResourceLink)resourceLinksMap.get(refName);
                           if (link == null) {
                              link = new ResourceLink();
                              link.setResRefName(refName);
                              resourceLinksMap.put(refName, link);
                              deployment.getResourceLink().add(link);
                           }

                           link.setResId(ref.getJndiName());
                        }
                     }

                     EnterpriseBean bean = (EnterpriseBean)ejbJar.getEnterpriseBeansByEjbName().get(ejb.getEjbName());
                     if (bean != null) {
                        Map<String, ServiceRef> serviceRefMap = bean.getServiceRefMap();

                        for(org.apache.openejb.jee.sun.ServiceRef ref : ejb.getServiceRef()) {
                           String refName = ref.getServiceRefName();
                           refName = this.normalize(refName);
                           ServiceRef serviceRef = (ServiceRef)serviceRefMap.get(refName);
                           if (serviceRef != null) {
                              Map<String, PortComponentRef> ports = new TreeMap();

                              for(PortComponentRef portComponentRef : serviceRef.getPortComponentRef()) {
                                 ports.put(portComponentRef.getServiceEndpointInterface(), portComponentRef);
                              }

                              for(PortInfo portInfo : ref.getPortInfo()) {
                                 PortComponentRef portComponentRef = (PortComponentRef)ports.get(portInfo.getServiceEndpointInterface());
                                 if (portComponentRef != null) {
                                    WsdlPort wsdlPort = portInfo.getWsdlPort();
                                    if (wsdlPort != null) {
                                       QName qname = new QName(wsdlPort.getNamespaceURI(), wsdlPort.getLocalpart());
                                       portComponentRef.setQName(qname);
                                    }

                                    for(StubProperty stubProperty : portInfo.getStubProperty()) {
                                       String name = stubProperty.getName();
                                       String value = stubProperty.getValue();
                                       portComponentRef.getProperties().setProperty(name, value);
                                    }
                                 }
                              }

                              String wsdlOverride = ref.getWsdlOverride();
                              if (wsdlOverride != null && wsdlOverride.length() > 0) {
                                 String serviceId = extractServiceId(wsdlOverride);
                                 serviceRef.setMappedName(serviceId);
                              }
                           }
                        }
                     }

                     if (ejb.getMdbResourceAdapter() != null) {
                        String resourceAdapterId = ejb.getMdbResourceAdapter().getResourceAdapterMid();
                        deployment.setContainerId(resourceAdapterId);
                     }

                     endpointMap.put(ejb.getEjbName(), ejb.getWebserviceEndpointMap());
                  }
               }

               if (ejbModule.getWebservices() != null) {
                  Map<String, org.apache.openejb.jee.sun.WebserviceDescription> sunDescriptions = sunEjbJar.getEnterpriseBeans().getWebserviceDescriptionMap();

                  for(WebserviceDescription description : ejbModule.getWebservices().getWebserviceDescription()) {
                     org.apache.openejb.jee.sun.WebserviceDescription sunDescription = (org.apache.openejb.jee.sun.WebserviceDescription)sunDescriptions.get(description.getWebserviceDescriptionName());
                     String serviceId = null;
                     if (sunDescription != null) {
                        serviceId = extractSerivceId(sunDescription.getWsdlPublishLocation(), description.getWsdlFile());
                     }

                     if (serviceId != null) {
                        description.setId(serviceId);
                     }

                     for(PortComponent port : description.getPortComponent()) {
                        ServiceImplBean bean = port.getServiceImplBean();
                        if (bean != null && bean.getEjbLink() != null) {
                           Map<String, WebserviceEndpoint> endpoints = (Map)endpointMap.get(bean.getEjbLink());
                           if (endpoints != null) {
                              WebserviceEndpoint endpoint = (WebserviceEndpoint)endpoints.get(port.getPortComponentName());
                              if (endpoint != null && endpoint.getEndpointAddressUri() != null) {
                                 port.setLocation(endpoint.getEndpointAddressUri());
                              }
                           }
                        }
                     }
                  }
               }

            }
         }
      }
   }

   private void mergeEntityMappings(Map<String, EntityData> entities, String moduleId, EjbJar ejbJar, OpenejbJar openejbJar, SunEjbJar sunEjbJar) {
      if (openejbJar != null) {
         if (sunEjbJar != null) {
            if (sunEjbJar.getEnterpriseBeans() != null) {
               for(Ejb ejb : sunEjbJar.getEnterpriseBeans().getEjb()) {
                  Cmp cmp = ejb.getCmp();
                  if (cmp != null) {
                     EnterpriseBean enterpriseBean = ejbJar.getEnterpriseBean(ejb.getEjbName());
                     if (enterpriseBean instanceof EntityBean && ((EntityBean)enterpriseBean).getPersistenceType() == PersistenceType.CONTAINER) {
                        EntityBean bean = (EntityBean)enterpriseBean;
                        EntityData entityData = (EntityData)entities.get(moduleId + "#" + ejb.getEjbName());
                        if (entityData != null) {
                           Collection<String> cmpFields = new ArrayList(bean.getCmpField().size());

                           for(CmpField cmpField : bean.getCmpField()) {
                              cmpFields.add(cmpField.getFieldName());
                           }

                           OneOneFinders oneOneFinders = cmp.getOneOneFinders();
                           if (oneOneFinders != null) {
                              for(Finder finder : oneOneFinders.getFinder()) {
                                 List<List<String>> params = this.parseQueryParamters(finder.getQueryParams());
                                 String queryFilter = finder.getQueryFilter();
                                 String ejbQl = this.convertToEjbQl(entityData.entity.getName(), cmpFields, finder.getQueryParams(), queryFilter);
                                 NamedQuery namedQuery = new NamedQuery();
                                 StringBuilder name = new StringBuilder();
                                 name.append(entityData.entity.getName()).append(".").append(finder.getMethodName());
                                 if (!params.isEmpty()) {
                                    name.append('(');
                                    boolean first = true;

                                    for(List<String> methodParam : params) {
                                       if (!first) {
                                          name.append(",");
                                       }

                                       name.append((String)methodParam.get(0));
                                       first = false;
                                    }

                                    name.append(')');
                                 }

                                 namedQuery.setName(name.toString());
                                 namedQuery.setQuery(ejbQl);
                                 entityData.entity.getNamedQuery().add(namedQuery);
                              }
                           }
                        }
                     }
                  }
               }

            }
         }
      }
   }

   public void mergeEntityMappings(Map<String, EntityData> entities, String moduleId, EjbModule ejbModule, EntityMappings entityMappings, SunCmpMapping sunCmpMapping) {
      for(EntityMapping bean : sunCmpMapping.getEntityMapping()) {
         EntityData entityData = (EntityData)entities.get(moduleId + "#" + bean.getEjbName());
         if (entityData != null) {
            Table table = new Table();
            table.setName(bean.getTableName());
            entityData.entity.setTable(table);

            for(SecondaryTable sunSecondaryTable : bean.getSecondaryTable()) {
               org.apache.openejb.jee.jpa.SecondaryTable secondaryTable = new org.apache.openejb.jee.jpa.SecondaryTable();
               secondaryTable.setName(sunSecondaryTable.getTableName());

               for(ColumnPair columnPair : sunSecondaryTable.getColumnPair()) {
                  SunColumnName localColumnName = new SunColumnName((ColumnName)columnPair.getColumnName().get(0), table.getName());
                  SunColumnName referencedColumnName = new SunColumnName((ColumnName)columnPair.getColumnName().get(1), table.getName());
                  if (localColumnName.table != null) {
                     SunColumnName temp = localColumnName;
                     localColumnName = referencedColumnName;
                     referencedColumnName = temp;
                  }

                  PrimaryKeyJoinColumn primaryKeyJoinColumn = new PrimaryKeyJoinColumn();
                  primaryKeyJoinColumn.setName(localColumnName.column);
                  primaryKeyJoinColumn.setReferencedColumnName(referencedColumnName.column);
                  secondaryTable.getPrimaryKeyJoinColumn().add(primaryKeyJoinColumn);
               }
            }

            for(CmpFieldMapping cmpFieldMapping : bean.getCmpFieldMapping()) {
               String fieldName = cmpFieldMapping.getFieldName();
               Field field = (Field)entityData.fields.get(fieldName);
               if (field != null) {
                  boolean readOnly = cmpFieldMapping.getReadOnly() != null;

                  for(ColumnName columnName : cmpFieldMapping.getColumnName()) {
                     SunColumnName sunColumnName = new SunColumnName(columnName, table.getName());
                     Column column = new Column();
                     column.setTable(sunColumnName.table);
                     column.setName(sunColumnName.column);
                     if (readOnly) {
                        column.setInsertable(false);
                        column.setUpdatable(false);
                     }

                     field.setColumn(column);
                  }
               }
            }

            for(CmrFieldMapping cmrFieldMapping : bean.getCmrFieldMapping()) {
               String fieldName = cmrFieldMapping.getCmrFieldName();
               cmrFieldMapping.getColumnPair();
               RelationField field = (RelationField)entityData.relations.get(fieldName);
               if (field != null) {
                  if (field instanceof OneToOne) {
                     for(ColumnPair columnPair : cmrFieldMapping.getColumnPair()) {
                        SunColumnName localColumnName = new SunColumnName((ColumnName)columnPair.getColumnName().get(0), table.getName());
                        SunColumnName referencedColumnName = new SunColumnName((ColumnName)columnPair.getColumnName().get(1), table.getName());
                        if (localColumnName.table != null) {
                           SunColumnName temp = localColumnName;
                           localColumnName = referencedColumnName;
                           referencedColumnName = temp;
                        }

                        boolean isFk = !entityData.hasPkColumnMapping(localColumnName.column);
                        if (isFk) {
                           field.setMappedBy((String)null);
                           field.getRelatedField().setMappedBy(field.getName());
                           JoinColumn joinColumn = new JoinColumn();
                           joinColumn.setName(localColumnName.column);
                           joinColumn.setReferencedColumnName(referencedColumnName.column);
                           field.getJoinColumn().add(joinColumn);
                        }
                     }
                  } else if (field instanceof OneToMany) {
                     if (field.getRelatedField().isSyntheticField()) {
                        for(ColumnPair columnPair : cmrFieldMapping.getColumnPair()) {
                           SunColumnName localColumnName = new SunColumnName((ColumnName)columnPair.getColumnName().get(0), table.getName());
                           SunColumnName otherColumnName = new SunColumnName((ColumnName)columnPair.getColumnName().get(1), table.getName());
                           if (localColumnName.table != null) {
                              SunColumnName temp = localColumnName;
                              localColumnName = otherColumnName;
                              otherColumnName = temp;
                           }

                           JoinColumn joinColumn = new JoinColumn();
                           joinColumn.setName(otherColumnName.column);
                           joinColumn.setReferencedColumnName(localColumnName.column);
                           field.getRelatedField().getJoinColumn().add(joinColumn);
                        }
                     }
                  } else if (field instanceof ManyToOne) {
                     for(ColumnPair columnPair : cmrFieldMapping.getColumnPair()) {
                        SunColumnName localColumnName = new SunColumnName((ColumnName)columnPair.getColumnName().get(0), table.getName());
                        SunColumnName referencedColumnName = new SunColumnName((ColumnName)columnPair.getColumnName().get(1), table.getName());
                        if (localColumnName.table != null) {
                           SunColumnName temp = localColumnName;
                           localColumnName = referencedColumnName;
                           referencedColumnName = temp;
                        }

                        JoinColumn joinColumn = new JoinColumn();
                        joinColumn.setName(localColumnName.column);
                        joinColumn.setReferencedColumnName(referencedColumnName.column);
                        field.getJoinColumn().add(joinColumn);
                     }
                  } else if (field.getMappedBy() == null) {
                     JoinTable joinTable = new JoinTable();
                     field.setJoinTable(joinTable);

                     for(ColumnPair columnPair : cmrFieldMapping.getColumnPair()) {
                        SunColumnName localColumnName = new SunColumnName((ColumnName)columnPair.getColumnName().get(0), table.getName());
                        SunColumnName joinTableColumnName = new SunColumnName((ColumnName)columnPair.getColumnName().get(1), table.getName());
                        if (localColumnName.table != null && joinTableColumnName.table != null) {
                           if (localColumnName.table.equals(joinTable.getName())) {
                              SunColumnName temp = localColumnName;
                              localColumnName = joinTableColumnName;
                              joinTableColumnName = temp;
                           }

                           JoinColumn joinColumn = new JoinColumn();
                           joinColumn.setName(joinTableColumnName.column);
                           joinColumn.setReferencedColumnName(localColumnName.column);
                           joinTable.getInverseJoinColumn().add(joinColumn);
                        } else {
                           if (localColumnName.table != null) {
                              SunColumnName temp = localColumnName;
                              localColumnName = joinTableColumnName;
                              joinTableColumnName = temp;
                           }

                           joinTable.setName(joinTableColumnName.table);
                           JoinColumn joinColumn = new JoinColumn();
                           joinColumn.setName(joinTableColumnName.column);
                           joinColumn.setReferencedColumnName(localColumnName.column);
                           joinTable.getJoinColumn().add(joinColumn);
                        }
                     }
                  }
               }
            }
         }
      }

   }

   public String convertToEjbQl(String abstractSchemaName, String queryParams, String queryFilter) {
      return this.convertToEjbQl(abstractSchemaName, Collections.emptyList(), queryParams, queryFilter);
   }

   public String convertToEjbQl(String abstractSchemaName, Collection<String> cmpFields, String queryParams, String queryFilter) {
      List<List<String>> variableNames = this.parseQueryParamters(queryParams);
      StringBuilder ejbQl = new StringBuilder();
      ejbQl.append("SELECT OBJECT(o) FROM ").append(abstractSchemaName).append(" AS o");
      String filter = this.convertToEjbQlFilter(cmpFields, variableNames, queryFilter);
      if (filter != null) {
         ejbQl.append(" WHERE ").append(filter);
      }

      return ejbQl.toString();
   }

   private List<List<String>> parseQueryParamters(String queryParams) {
      if (queryParams == null) {
         return Collections.emptyList();
      } else {
         List bits = Collections.list(new StringTokenizer(queryParams, " \t\n\r\f,", false));
         List<List<String>> params = new ArrayList(bits.size() / 2);

         for(int i = 0; i < bits.size(); ++i) {
            String type = this.resolveType((String)bits.get(i));
            ++i;
            String param = (String)bits.get(i);
            params.add(Arrays.asList(type, param));
         }

         return params;
      }
   }

   private String resolveType(String type) {
      try {
         ClassLoader.getSystemClassLoader().loadClass(type);
         return type;
      } catch (ClassNotFoundException var4) {
         try {
            String javaLangType = "java.lang" + type;
            ClassLoader.getSystemClassLoader().loadClass(javaLangType);
            return javaLangType;
         } catch (ClassNotFoundException var3) {
            return type;
         }
      }
   }

   private String convertToEjbQlFilter(Collection<String> cmpFields, List<List<String>> queryParams, String queryFilter) {
      if (queryFilter == null) {
         return null;
      } else {
         Map<String, String> variableMap = new TreeMap();

         for(String cmpField : cmpFields) {
            variableMap.put(cmpField, "o." + cmpField);
         }

         for(int i = 0; i < queryParams.size(); ++i) {
            List<String> param = (List)queryParams.get(i);
            variableMap.put(param.get(1), "?" + (i + 1));
         }

         Map<String, String> symbolMap = new TreeMap();
         symbolMap.put("&&", "and");
         symbolMap.put("||", "or");
         symbolMap.put("!", "not");
         symbolMap.put("==", "=");
         symbolMap.put("!=", "<>");
         StringBuilder ejbQlFilter = new StringBuilder(queryFilter.length() * 2);

         for(String token : this.tokenize(queryFilter)) {
            String mappedToken = (String)symbolMap.get(token);
            if (mappedToken == null) {
               mappedToken = (String)variableMap.get(token);
            }

            if (mappedToken != null) {
               ejbQlFilter.append(mappedToken);
            } else {
               ejbQlFilter.append(token);
            }

            ejbQlFilter.append(" ");
         }

         String filter = ejbQlFilter.toString().trim();
         if (filter.equalsIgnoreCase("true")) {
            return null;
         } else {
            return filter;
         }
      }
   }

   private List<String> tokenize(String queryFilter) {
      LinkedList<String> tokens = new LinkedList();
      List bits = Collections.list(new StringTokenizer(queryFilter, " \t\n\r\f()&|<>=!~+-/*", true));
      boolean inWitespace = false;
      String currentSymbol = "";

      for(int i = 0; i < bits.size(); ++i) {
         String bit = (String)bits.get(i);
         TokenType tokenType;
         switch (bit.charAt(0)) {
            case '\t':
            case '\n':
            case '\f':
            case '\r':
            case ' ':
               inWitespace = true;
               tokenType = SunConversion.TokenType.WHITESPACE;
               break;
            case '!':
            case '&':
            case '<':
            case '=':
            case '>':
            case '|':
               currentSymbol = currentSymbol + bit.charAt(0);
               tokenType = SunConversion.TokenType.SYMBOL;
               break;
            default:
               tokenType = SunConversion.TokenType.NORMAL;
         }

         if (tokenType != SunConversion.TokenType.WHITESPACE && inWitespace) {
            inWitespace = false;
         }

         if (tokenType != SunConversion.TokenType.SYMBOL && currentSymbol.length() > 0) {
            tokens.add(currentSymbol);
            currentSymbol = "";
         }

         if (tokenType == SunConversion.TokenType.NORMAL) {
            tokens.add(bit);
         }
      }

      if (currentSymbol.length() > 0) {
         tokens.add(currentSymbol);
         currentSymbol = "";
      }

      if (((String)tokens.getFirst()).equals(" ")) {
         tokens.removeFirst();
      }

      return tokens;
   }

   private static enum TokenType {
      WHITESPACE,
      SYMBOL,
      NORMAL;
   }

   private class SunColumnName {
      private final String table;
      private final String column;

      public SunColumnName(ColumnName columnName, String primaryTableName) {
         this((String)columnName.getvalue(), primaryTableName);
      }

      public SunColumnName(String fullName, String primaryTableName) {
         int dot = fullName.indexOf(46);
         if (dot > 0) {
            String t = fullName.substring(0, dot);
            if (primaryTableName.equals(t)) {
               this.table = null;
            } else {
               this.table = t;
            }

            this.column = fullName.substring(dot + 1);
         } else {
            this.table = null;
            this.column = fullName;
         }

      }
   }

   private class EntityData {
      private final Entity entity;
      private final Map<String, Id> ids = new TreeMap();
      private final Map<String, Field> fields = new TreeMap();
      private final Map<String, RelationField> relations = new TreeMap();

      public EntityData(Entity entity) {
         if (entity == null) {
            throw new NullPointerException("entity is null");
         } else {
            this.entity = entity;
            Attributes attributes = entity.getAttributes();
            if (attributes != null) {
               for(Id id : attributes.getId()) {
                  String name = id.getName();
                  this.ids.put(name, id);
                  this.fields.put(name, id);
               }

               for(Basic basic : attributes.getBasic()) {
                  String name = basic.getName();
                  this.fields.put(name, basic);
               }

               for(RelationField relationField : attributes.getOneToOne()) {
                  String name = relationField.getName();
                  this.relations.put(name, relationField);
               }

               for(RelationField relationField : attributes.getOneToMany()) {
                  String name = relationField.getName();
                  this.relations.put(name, relationField);
               }

               for(RelationField relationField : attributes.getManyToOne()) {
                  String name = relationField.getName();
                  this.relations.put(name, relationField);
               }

               for(RelationField relationField : attributes.getManyToMany()) {
                  String name = relationField.getName();
                  this.relations.put(name, relationField);
               }
            }

            for(AttributeOverride attributeOverride : entity.getAttributeOverride()) {
               String name = attributeOverride.getName();
               this.fields.put(name, attributeOverride);
            }

         }
      }

      public boolean hasPkColumnMapping(String column) {
         if (this.ids.size() > 1) {
            return false;
         } else {
            for(Id id : this.ids.values()) {
               if (column.equals(id.getColumn().getName())) {
                  return true;
               }
            }

            return false;
         }
      }
   }
}
