package org.apache.openejb.config;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.apache.openejb.OpenEJBException;
import org.apache.openejb.cdi.CompositeBeans;
import org.apache.openejb.config.sys.JSonConfigReader;
import org.apache.openejb.config.sys.JaxbOpenejb;
import org.apache.openejb.config.sys.Resource;
import org.apache.openejb.config.sys.Resources;
import org.apache.openejb.core.ParentClassLoaderFinder;
import org.apache.openejb.jee.ApplicationClient;
import org.apache.openejb.jee.Beans;
import org.apache.openejb.jee.Connector;
import org.apache.openejb.jee.Connector10;
import org.apache.openejb.jee.EjbJar;
import org.apache.openejb.jee.FacesConfig;
import org.apache.openejb.jee.HandlerChains;
import org.apache.openejb.jee.JavaWsdlMapping;
import org.apache.openejb.jee.JaxbJavaee;
import org.apache.openejb.jee.Keyable;
import org.apache.openejb.jee.Listener;
import org.apache.openejb.jee.TldTaglib;
import org.apache.openejb.jee.WebApp;
import org.apache.openejb.jee.WebFragment;
import org.apache.openejb.jee.Webservices;
import org.apache.openejb.jee.bval.ValidationConfigType;
import org.apache.openejb.jee.jpa.EntityMappings;
import org.apache.openejb.jee.jpa.fragment.PersistenceFragment;
import org.apache.openejb.jee.jpa.fragment.PersistenceUnitFragment;
import org.apache.openejb.jee.jpa.unit.JaxbPersistenceFactory;
import org.apache.openejb.jee.jpa.unit.Persistence;
import org.apache.openejb.jee.jpa.unit.PersistenceUnit;
import org.apache.openejb.jee.oejb2.GeronimoEjbJarType;
import org.apache.openejb.jee.oejb2.JaxbOpenejbJar2;
import org.apache.openejb.jee.oejb2.OpenejbJarType;
import org.apache.openejb.jee.oejb3.JaxbOpenejbJar3;
import org.apache.openejb.jee.oejb3.OpenejbJar;
import org.apache.openejb.loader.IO;
import org.apache.openejb.loader.SystemInstance;
import org.apache.openejb.sxc.ApplicationClientXml;
import org.apache.openejb.sxc.EjbJarXml;
import org.apache.openejb.sxc.FacesConfigXml;
import org.apache.openejb.sxc.HandlerChainsXml;
import org.apache.openejb.sxc.TldTaglibXml;
import org.apache.openejb.sxc.WebXml;
import org.apache.openejb.sxc.WebservicesXml;
import org.apache.openejb.util.LengthInputStream;
import org.apache.openejb.util.LogCategory;
import org.apache.openejb.util.Logger;
import org.apache.openejb.util.Saxs;
import org.apache.openejb.util.URLs;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class ReadDescriptors implements DynamicDeployer {
   private static final Logger logger;
   private static final boolean ROOT_URL_FROM_WEBINF;
   public static final TldTaglib SKIP_TAGLIB;

   public AppModule deploy(AppModule appModule) throws OpenEJBException {
      for(EjbModule ejbModule : appModule.getEjbModules()) {
         if (ejbModule.getEjbJar() == null) {
            this.readEjbJar(ejbModule, appModule);
         }

         if (ejbModule.getOpenejbJar() == null) {
            this.readOpenejbJar(ejbModule);
         }

         if (ejbModule.getBeans() == null) {
            this.readBeans(ejbModule);
         }

         this.readValidationConfigType(ejbModule);
         this.readCmpOrm(ejbModule);
         readResourcesXml(ejbModule);
      }

      for(ClientModule clientModule : appModule.getClientModules()) {
         this.readAppClient(clientModule, appModule);
         this.readValidationConfigType(clientModule);
         readResourcesXml(clientModule);
      }

      for(ConnectorModule connectorModule : appModule.getConnectorModules()) {
         this.readConnector(connectorModule, appModule);
         this.readValidationConfigType(connectorModule);
         readResourcesXml(connectorModule);
      }

      for(WebModule webModule : appModule.getWebModules()) {
         this.readWebApp(webModule, appModule);
         this.readValidationConfigType(webModule);
         readResourcesXml(webModule);
      }

      List<Object> persistenceUrls = (List)appModule.getAltDDs().get("persistence.xml");
      if (persistenceUrls != null) {
         for(Object persistenceUrl : persistenceUrls) {
            boolean url = persistenceUrl instanceof URL;
            Source source = getSource(persistenceUrl);
            String moduleName;
            String path;
            String rootUrl;
            if (url) {
               URL pUrl = (URL)persistenceUrl;
               File file = URLs.toFile(pUrl);
               path = file.getAbsolutePath();
               if (file.getName().endsWith("persistence.xml")) {
                  File parentFile = file.getParentFile();
                  String parent = parentFile.getName();
                  if (!parent.equalsIgnoreCase("WEB-INF") && !parent.equalsIgnoreCase("META-INF")) {
                     file = file.getParentFile();
                  } else {
                     file = parentFile.getParentFile();
                  }
               }

               moduleName = file.toURI().toString();
               String tmpRootUrl = moduleName;
               String extForm = pUrl.toExternalForm();
               if (extForm.contains("WEB-INF/classes/META-INF/")) {
                  if (!ROOT_URL_FROM_WEBINF) {
                     tmpRootUrl = extForm.substring(0, extForm.indexOf("/META-INF"));
                  } else {
                     tmpRootUrl = extForm.substring(0, extForm.indexOf("/classes/META-INF"));
                  }
               }

               if (tmpRootUrl.endsWith(".war")) {
                  tmpRootUrl = tmpRootUrl.substring(0, tmpRootUrl.length() - ".war".length());
               }

               rootUrl = tmpRootUrl;
            } else {
               moduleName = "";
               rootUrl = "";
               path = null;
            }

            try {
               Persistence persistence = (Persistence)JaxbPersistenceFactory.getPersistence(Persistence.class, source.get());
               PersistenceModule persistenceModule = new PersistenceModule(appModule, rootUrl, persistence);
               persistenceModule.getWatchedResources().add(moduleName);
               if (url && "file".equals(((URL)persistenceUrl).getProtocol())) {
                  persistenceModule.getWatchedResources().add(path);
               }

               appModule.addPersistenceModule(persistenceModule);
            } catch (Exception e1) {
               DeploymentLoader.logger.error("Unable to load Persistence Unit from EAR: " + appModule.getJarLocation() + ", module: " + moduleName + ". Exception: " + e1.getMessage(), (Throwable)e1);
            }
         }
      }

      List<URL> persistenceFragmentUrls = (List)appModule.getAltDDs().get("persistence-fragment.xml");
      if (persistenceFragmentUrls != null) {
         for(URL persistenceFragmentUrl : persistenceFragmentUrls) {
            try {
               PersistenceFragment persistenceFragment = (PersistenceFragment)JaxbPersistenceFactory.getPersistence(PersistenceFragment.class, persistenceFragmentUrl);

               for(PersistenceUnitFragment fragmentUnit : persistenceFragment.getPersistenceUnitFragment()) {
                  for(PersistenceModule persistenceModule : appModule.getPersistenceModules()) {
                     Persistence persistence = persistenceModule.getPersistence();

                     for(PersistenceUnit unit : persistence.getPersistenceUnit()) {
                        if (fragmentUnit.getName().equals(unit.getName())) {
                           if (!persistenceFragment.getVersion().equals(persistence.getVersion())) {
                              logger.error("persistence unit version and fragment version are different, fragment will be ignored");
                           } else {
                              if ("file".equals(persistenceFragmentUrl.getProtocol())) {
                                 persistenceModule.getWatchedResources().add(URLs.toFile(persistenceFragmentUrl).getAbsolutePath());
                              }

                              for(String clazz : fragmentUnit.getClazz()) {
                                 if (!unit.getClazz().contains(clazz)) {
                                    logger.info("Adding class " + clazz + " to persistence unit " + fragmentUnit.getName());
                                    unit.getClazz().add(clazz);
                                 }
                              }

                              for(String mappingFile : fragmentUnit.getMappingFile()) {
                                 if (!unit.getMappingFile().contains(mappingFile)) {
                                    logger.info("Adding mapping file " + mappingFile + " to persistence unit " + fragmentUnit.getName());
                                    unit.getMappingFile().add(mappingFile);
                                 }
                              }

                              for(String jarFile : fragmentUnit.getJarFile()) {
                                 if (!unit.getJarFile().contains(jarFile)) {
                                    logger.info("Adding jar file " + jarFile + " to persistence unit " + fragmentUnit.getName());
                                    unit.getJarFile().add(jarFile);
                                 }
                              }

                              if (fragmentUnit.isExcludeUnlistedClasses()) {
                                 unit.setExcludeUnlistedClasses(true);
                                 logger.info("Excluding unlisted classes for persistence unit " + fragmentUnit.getName());
                              }
                           }
                        }
                     }
                  }
               }
            } catch (Exception e1) {
               DeploymentLoader.logger.error("Unable to load Persistence Unit Fragment from EAR: " + appModule.getJarLocation() + ", fragment: " + persistenceFragmentUrl.toString() + ". Exception: " + e1.getMessage(), (Throwable)e1);
            }
         }
      }

      return appModule;
   }

   public static void readResourcesXml(Module module) {
      Source url = getSource(module.getAltDDs().get("resources.xml"));
      if (url != null) {
         try {
            Resources openejb = (Resources)JaxbOpenejb.unmarshal(Resources.class, url.get());
            module.initResources(check(openejb));
         } catch (Exception e) {
            logger.warning("can't read " + url.toString() + " to load resources for module " + module.toString(), (Throwable)e);
         }
      }

      url = getSource(module.getAltDDs().get("resources.json"));
      if (url != null) {
         try {
            Resources openejb = (Resources)JSonConfigReader.read(Resources.class, url.get());
            module.initResources(check(openejb));
         } catch (Exception e) {
            logger.warning("can't read " + url.toString() + " to load resources for module " + module.toString(), (Throwable)e);
         }
      }

   }

   public static Resources check(Resources resources) {
      for(Resource resource : resources.getResource()) {
         if (resource.getClassName() != null) {
            try {
               ParentClassLoaderFinder.Helper.get().loadClass(resource.getClassName());
            } catch (Exception var5) {
               Boolean lazySpecified = Boolean.valueOf(resource.getProperties().getProperty("Lazy", "false"));
               resource.getProperties().setProperty("Lazy", "true");
               resource.getProperties().setProperty("UseAppClassLoader", "true");
               if (!lazySpecified) {
                  resource.getProperties().setProperty("InitializeAfterDeployment", "true");
               }
            }
         }
      }

      return resources;
   }

   private void readValidationConfigType(Module module) throws OpenEJBException {
      if (module.getValidationConfig() == null) {
         Source value = getSource(module.getAltDDs().get("validation.xml"));
         if (value != null) {
            try {
               ValidationConfigType validationConfigType = (ValidationConfigType)JaxbOpenejb.unmarshal(ValidationConfigType.class, value.get(), false);
               module.setValidationConfig(validationConfigType);
            } catch (Exception var4) {
               logger.warning("can't read validation.xml to construct a validation factory, it will be ignored");
            }
         }

      }
   }

   private void readOpenejbJar(EjbModule ejbModule) throws OpenEJBException {
      Source source = getSource(ejbModule.getAltDDs().get("openejb-jar.xml"));
      if (source != null) {
         try {
            OpenejbJar openejbJar = ((OpenejbJar)JaxbOpenejbJar3.unmarshal(OpenejbJar.class, source.get())).postRead();
            ejbModule.setOpenejbJar(openejbJar);
         } catch (Exception v3ParsingException) {
            OpenejbJar openejbJar = new OpenejbJar();
            ejbModule.setOpenejbJar(openejbJar);

            try {
               JAXBElement element = (JAXBElement)JaxbOpenejbJar2.unmarshal(OpenejbJarType.class, source.get());
               OpenejbJarType o2 = (OpenejbJarType)element.getValue();
               ejbModule.getAltDDs().put("openejb-jar.xml", o2);
               GeronimoEjbJarType g2 = OpenEjb2Conversion.convertToGeronimoOpenejbXml(o2);
               ejbModule.getAltDDs().put("geronimo-openejb.xml", g2);
            } catch (Exception var15) {
               final Exception v2ParsingException = var15;
               final Exception[] realIssue = new Exception[]{v3ParsingException};

               try {
                  SAXParserFactory factory = Saxs.namespaceAwareFactory();
                  SAXParser parser = factory.newSAXParser();
                  parser.parse(source.get(), new DefaultHandler() {
                     public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
                        if (localName.equals("environment")) {
                           realIssue[0] = v2ParsingException;
                           throw new SAXException("Throw exception to stop parsing");
                        } else if (uri != null) {
                           if (uri.contains("openejb-jar-2.") || uri.contains("geronimo.apache.org/xml/ns")) {
                              realIssue[0] = v2ParsingException;
                              throw new SAXException("Throw exception to stop parsing");
                           }
                        }
                     }
                  });
               } catch (Exception var12) {
               }

               String filePath = "<error: could not be written>";

               try {
                  File tempFile;
                  try {
                     tempFile = File.createTempFile("openejb-jar-", ".xml");
                  } catch (Throwable var13) {
                     File tmp = new File("tmp");
                     if (!tmp.exists() && !tmp.mkdirs()) {
                        throw new IOException("Failed to create local tmp directory: " + tmp.getAbsolutePath());
                     }

                     tempFile = File.createTempFile("openejb-jar-", ".xml", tmp);
                  }

                  try {
                     IO.copy(source.get(), tempFile);
                  } catch (IOException var11) {
                  }

                  filePath = tempFile.getAbsolutePath();
               } catch (IOException var14) {
               }

               Exception e = realIssue[0];
               if (e instanceof SAXException) {
                  throw new OpenEJBException("Cannot parse the openejb-jar.xml. Xml content written to: " + filePath, e);
               }

               if (e instanceof JAXBException) {
                  throw new OpenEJBException("Cannot unmarshall the openejb-jar.xml. Xml content written to: " + filePath, e);
               }

               if (e instanceof IOException) {
                  throw new OpenEJBException("Cannot read the openejb-jar.xml.", e);
               }

               throw new OpenEJBException("Encountered unknown error parsing the openejb-jar.xml.", e);
            }
         }
      }

      Source source1 = getSource(ejbModule.getAltDDs().get("geronimo-openejb.xml"));
      if (source1 != null) {
         try {
            GeronimoEjbJarType geronimoEjbJarType = null;
            Object o = JaxbOpenejbJar2.unmarshal(GeronimoEjbJarType.class, source1.get());
            if (o instanceof GeronimoEjbJarType) {
               geronimoEjbJarType = (GeronimoEjbJarType)o;
            } else if (o instanceof JAXBElement) {
               JAXBElement element = (JAXBElement)o;
               geronimoEjbJarType = (GeronimoEjbJarType)element.getValue();
            }

            if (geronimoEjbJarType != null) {
               Object nested = geronimoEjbJarType.getOpenejbJar();
               if (nested != null && nested instanceof OpenejbJar) {
                  OpenejbJar existingOpenejbJar = ejbModule.getOpenejbJar();
                  if (existingOpenejbJar == null || existingOpenejbJar.getEjbDeploymentCount() <= 0) {
                     OpenejbJar openejbJar = (OpenejbJar)nested;
                     ejbModule.getAltDDs().put("openejb-jar.xml", openejbJar);
                     ejbModule.setOpenejbJar(openejbJar);
                  }
               }

               ejbModule.getAltDDs().put("geronimo-openejb.xml", geronimoEjbJarType);
            }
         } catch (Exception e) {
            throw new OpenEJBException("Failed parsing geronimo-openejb.xml", e);
         }
      }

   }

   private void readAppClient(ClientModule clientModule, AppModule appModule) throws OpenEJBException {
      if (clientModule.getApplicationClient() == null) {
         Object data = clientModule.getAltDDs().get("application-client.xml");
         if (data instanceof ApplicationClient) {
            clientModule.setApplicationClient((ApplicationClient)data);
         } else if (data instanceof URL) {
            URL url = (URL)data;
            ApplicationClient applicationClient = readApplicationClient(url);
            clientModule.setApplicationClient(applicationClient);
         } else if (!clientModule.isEjbModuleGenerated()) {
            DeploymentLoader.logger.debug("No application-client.xml found assuming annotations present: " + appModule.getJarLocation() + ", module: " + clientModule.getModuleId());
            clientModule.setApplicationClient(new ApplicationClient());
         }

      }
   }

   public void readEjbJar(EjbModule ejbModule, AppModule appModule) throws OpenEJBException {
      if (ejbModule.getEjbJar() == null) {
         Source data = getSource(ejbModule.getAltDDs().get("ejb-jar.xml"));
         if (data != null) {
            try {
               EjbJar ejbJar = readEjbJar(data.get());
               ejbModule.setEjbJar(ejbJar);
            } catch (IOException e) {
               throw new OpenEJBException(e);
            }
         } else {
            DeploymentLoader.logger.debug("No ejb-jar.xml found assuming annotated beans present: " + appModule.getJarLocation() + ", module: " + ejbModule.getModuleId());
            ejbModule.setEjbJar(new EjbJar());
         }

      }
   }

   private static void checkDuplicatedByBeansXml(List<String> list, List<String> duplicated) {
      for(String str : list) {
         if (list.indexOf(str) != list.lastIndexOf(str)) {
            duplicated.add(str);
         }
      }

   }

   public static void checkDuplicatedByBeansXml(Beans beans, Beans complete) {
      checkDuplicatedByBeansXml(beans.getAlternativeClasses(), complete.getDuplicatedAlternatives().getClasses());
      checkDuplicatedByBeansXml(beans.getAlternativeStereotypes(), complete.getDuplicatedAlternatives().getStereotypes());
      checkDuplicatedByBeansXml(beans.getDecorators(), complete.getDuplicatedDecorators());
      checkDuplicatedByBeansXml(beans.getInterceptors(), complete.getDuplicatedInterceptors());
   }

   private void readBeans(EjbModule ejbModule) throws OpenEJBException {
      if (ejbModule.getBeans() == null) {
         Object raw = ejbModule.getAltDDs().get("beans.xml");
         Source data = getSource(raw);
         if (data != null) {
            try {
               Beans beans = readBeans(data.get());
               checkDuplicatedByBeansXml(beans, beans);
               if (UrlSource.class.isInstance(data)) {
                  beans.setUri(((UrlSource)UrlSource.class.cast(data)).getUrl().toExternalForm());
               } else {
                  beans.setUri("jar:file://" + ejbModule.getModuleId() + "!/META-INF/beans.xml");
               }

               ejbModule.setBeans(beans);
            } catch (IOException e) {
               throw new OpenEJBException(e);
            }
         } else if (raw instanceof Beans) {
            ejbModule.setBeans((Beans)raw);
         } else if (List.class.isInstance(raw)) {
            CompositeBeans compositeBeans = new CompositeBeans();
            List list = (List)List.class.cast(raw);
            if (!list.isEmpty()) {
               for(Object o : list) {
                  try {
                     UrlSource urlSource = (UrlSource)UrlSource.class.cast(o);
                     mergeBeansXml(compositeBeans, readBeans(urlSource.get()), urlSource.getUrl());
                  } catch (IOException e) {
                     throw new OpenEJBException(e);
                  }
               }

               ejbModule.setBeans(compositeBeans);
            }
         }

      }
   }

   private static Beans mergeBeansXml(CompositeBeans current, Beans beans, URL url) {
      current.mergeClasses(url, beans);
      current.getScan().getExclude().addAll(beans.getScan().getExclude());
      checkDuplicatedByBeansXml(beans, current);
      String beanDiscoveryMode = beans.getBeanDiscoveryMode();
      current.getDiscoveryByUrl().put(url, beanDiscoveryMode == null ? "ALL" : beanDiscoveryMode);
      return current;
   }

   private void readCmpOrm(EjbModule ejbModule) throws OpenEJBException {
      Object data = ejbModule.getAltDDs().get("openejb-cmp-orm.xml");
      if (data != null && !(data instanceof EntityMappings) && data instanceof URL) {
         URL url = (URL)data;

         try {
            EntityMappings entitymappings = (EntityMappings)JaxbJavaee.unmarshalJavaee(EntityMappings.class, IO.read(url));
            ejbModule.getAltDDs().put("openejb-cmp-orm.xml", entitymappings);
         } catch (SAXException e) {
            throw new OpenEJBException("Cannot parse the openejb-cmp-orm.xml file: " + url.toExternalForm(), e);
         } catch (JAXBException e) {
            throw new OpenEJBException("Cannot unmarshall the openejb-cmp-orm.xml file: " + url.toExternalForm(), e);
         } catch (IOException e) {
            throw new OpenEJBException("Cannot read the openejb-cmp-orm.xml file: " + url.toExternalForm(), e);
         } catch (Exception e) {
            throw new OpenEJBException("Encountered unknown error parsing the openejb-cmp-orm.xml file: " + url.toExternalForm(), e);
         }
      }

   }

   private void readConnector(ConnectorModule connectorModule, AppModule appModule) throws OpenEJBException {
      if (connectorModule.getConnector() == null) {
         Object data = connectorModule.getAltDDs().get("ra.xml");
         if (data instanceof Connector) {
            connectorModule.setConnector((Connector)data);
         } else if (data instanceof URL) {
            URL url = (URL)data;
            Connector connector = readConnector(url);
            connectorModule.setConnector(connector);
         } else {
            DeploymentLoader.logger.debug("No ra.xml found assuming annotated beans present: " + appModule.getJarLocation() + ", module: " + connectorModule.getModuleId());
            connectorModule.setConnector(new Connector());
         }

      }
   }

   private void readWebApp(WebModule webModule, AppModule appModule) throws OpenEJBException {
      if (webModule.getWebApp() != null) {
         this.mergeWebFragments(webModule);
      } else {
         Object data = webModule.getAltDDs().get("web.xml");
         if (data instanceof WebApp) {
            webModule.setWebApp((WebApp)data);
         } else if (data instanceof URL) {
            URL url = (URL)data;
            WebApp webApp = readWebApp(url);
            webModule.setWebApp(webApp);
         } else {
            DeploymentLoader.logger.debug("No web.xml found assuming annotated beans present: " + appModule.getJarLocation() + ", module: " + webModule.getModuleId());
            webModule.setWebApp(new WebApp());
         }

         this.mergeWebFragments(webModule);
      }
   }

   private void mergeWebFragments(WebModule webModule) {
      Collection<URL> urls = (Collection)Collection.class.cast(webModule.getAltDDs().get("web-fragment.xml"));
      if (urls != null) {
         for(URL rawUrl : urls) {
            if (rawUrl != null) {
               Source url = getSource(rawUrl);

               try {
                  WebFragment webFragment = (WebFragment)WebFragment.class.cast(JaxbJavaee.unmarshal(WebFragment.class, url.get(), false));
                  mergeOnlyMissingEntries(webModule.getWebApp().getPersistenceContextRefMap(), webFragment.getPersistenceContextRef());
                  mergeOnlyMissingEntries(webModule.getWebApp().getPersistenceUnitRefMap(), webFragment.getPersistenceUnitRef());
                  mergeOnlyMissingEntries(webModule.getWebApp().getMessageDestinationRefMap(), webFragment.getMessageDestinationRef());
                  mergeOnlyMissingEntries(webModule.getWebApp().getDataSourceMap(), webFragment.getDataSource());
                  mergeOnlyMissingEntries(webModule.getWebApp().getJMSConnectionFactoriesMap(), webFragment.getJMSConnectionFactories());
                  mergeOnlyMissingEntries(webModule.getWebApp().getEjbLocalRefMap(), webFragment.getEjbLocalRef());
                  mergeOnlyMissingEntries(webModule.getWebApp().getEjbRefMap(), webFragment.getEjbRef());
                  mergeOnlyMissingEntries(webModule.getWebApp().getServiceRefMap(), webFragment.getServiceRef());
                  mergeOnlyMissingEntries(webModule.getWebApp().getEnvEntryMap(), webFragment.getEnvEntry());
                  mergeOnlyMissingEntries(webModule.getWebApp().getResourceEnvRefMap(), webFragment.getResourceEnvRef());
                  mergeOnlyMissingEntries(webModule.getWebApp().getResourceRefMap(), webFragment.getResourceRef());
               } catch (Exception e) {
                  logger.warning("can't read " + url.toString(), (Throwable)e);
               }
            }
         }
      }

   }

   private static <A extends Keyable<String>> void mergeOnlyMissingEntries(Map<String, A> existing, Collection<A> news) {
      for(A entry : news) {
         String key = (String)entry.getKey();
         if (!existing.containsKey(key)) {
            existing.put(key, entry);
         }
      }

   }

   public static ApplicationClient readApplicationClient(URL url) throws OpenEJBException {
      try {
         ApplicationClient applicationClient = ApplicationClientXml.unmarshal(url);
         return applicationClient;
      } catch (SAXException e) {
         throw new OpenEJBException("Cannot parse the application-client.xml file: " + url.toExternalForm(), e);
      } catch (JAXBException e) {
         throw new OpenEJBException("Cannot unmarshall the application-client.xml file: " + url.toExternalForm(), e);
      } catch (IOException e) {
         throw new OpenEJBException("Cannot read the application-client.xml file: " + url.toExternalForm(), e);
      } catch (Exception e) {
         throw new OpenEJBException("Encountered unknown error parsing the application-client.xml file: " + url.toExternalForm(), e);
      }
   }

   public static EjbJar readEjbJar(InputStream is) throws OpenEJBException {
      try {
         String content = IO.slurp(is);
         if (isEmptyEjbJar(new ByteArrayInputStream(content.getBytes()))) {
            String id = getId(new ByteArrayInputStream(content.getBytes()));
            return new EjbJar(id);
         } else {
            return EjbJarXml.unmarshal(new ByteArrayInputStream(content.getBytes()));
         }
      } catch (SAXException e) {
         throw new OpenEJBException("Cannot parse the ejb-jar.xml", e);
      } catch (IOException e) {
         throw new OpenEJBException("Cannot read the ejb-jar.xml", e);
      } catch (Exception e) {
         throw new OpenEJBException("Encountered error parsing the ejb-jar.xml", e);
      }
   }

   public static Beans readBeans(InputStream inputStream) throws OpenEJBException {
      try {
         String content = IO.slurp(inputStream).trim();
         if (content.length() == 0) {
            Beans beans = new Beans();
            beans.setBeanDiscoveryMode("ALL");
            return beans;
         } else {
            return (Beans)JaxbJavaee.unmarshalJavaee(Beans.class, new ByteArrayInputStream(content.getBytes()));
         }
      } catch (SAXException e) {
         throw new OpenEJBException("Cannot parse the beans.xml", e);
      } catch (JAXBException e) {
         e.printStackTrace();
         throw new OpenEJBException("Cannot unmarshall the beans.xml", e);
      } catch (IOException e) {
         throw new OpenEJBException("Cannot read the beans.xml", e);
      } catch (Exception e) {
         throw new OpenEJBException("Encountered unknown error parsing the beans.xml", e);
      }
   }

   private static boolean isEmptyEjbJar(InputStream is) throws IOException, ParserConfigurationException, SAXException {
      return isEmpty(is, "ejb-jar");
   }

   private static boolean isEmpty(InputStream is, final String rootElement) throws IOException, ParserConfigurationException, SAXException {
      LengthInputStream in = new LengthInputStream(is);
      InputSource inputSource = new InputSource(in);
      Thread thread = Thread.currentThread();
      ClassLoader original = thread.getContextClassLoader();
      thread.setContextClassLoader(Saxs.class.getClassLoader());

      SAXParser parser;
      try {
         parser = Saxs.namespaceAwareFactory().newSAXParser();
      } finally {
         thread.setContextClassLoader(original);
      }

      try {
         parser.parse(inputSource, new DefaultHandler() {
            public void startElement(String uri, String localName, String qName, Attributes att) throws SAXException {
               if (!localName.equals(rootElement)) {
                  throw new SAXException(localName);
               }
            }

            public InputSource resolveEntity(String publicId, String systemId) throws IOException, SAXException {
               return new InputSource(new ByteArrayInputStream(new byte[0]));
            }
         });
         return true;
      } catch (SAXException var11) {
         return in.getLength() == 0L;
      }
   }

   private static String getId(InputStream is) {
      final String[] id = new String[]{null};

      try {
         LengthInputStream in = new LengthInputStream(is);
         InputSource inputSource = new InputSource(in);
         SAXParser parser = Saxs.namespaceAwareFactory().newSAXParser();
         parser.parse(inputSource, new DefaultHandler() {
            public void startElement(String uri, String localName, String qName, Attributes att) throws SAXException {
               id[0] = att.getValue("id");
            }

            public InputSource resolveEntity(String publicId, String systemId) throws IOException, SAXException {
               return new InputSource(new ByteArrayInputStream(new byte[0]));
            }
         });
      } catch (Exception var5) {
      }

      return id[0];
   }

   public static Webservices readWebservices(URL url) throws OpenEJBException {
      try {
         return WebservicesXml.unmarshal(url);
      } catch (SAXException e) {
         throw new OpenEJBException("Cannot parse the webservices.xml file: " + url.toExternalForm(), e);
      } catch (JAXBException e) {
         throw new OpenEJBException("Cannot unmarshall the webservices.xml file: " + url.toExternalForm(), e);
      } catch (IOException e) {
         throw new OpenEJBException("Cannot read the webservices.xml file: " + url.toExternalForm(), e);
      } catch (Exception e) {
         throw new OpenEJBException("Encountered unknown error parsing the webservices.xml file: " + url.toExternalForm(), e);
      }
   }

   public static HandlerChains readHandlerChains(URL url) throws OpenEJBException {
      try {
         return HandlerChainsXml.unmarshal(url);
      } catch (SAXException e) {
         throw new OpenEJBException("Cannot parse the webservices.xml file: " + url.toExternalForm(), e);
      } catch (JAXBException e) {
         throw new OpenEJBException("Cannot unmarshall the webservices.xml file: " + url.toExternalForm(), e);
      } catch (IOException e) {
         throw new OpenEJBException("Cannot read the webservices.xml file: " + url.toExternalForm(), e);
      } catch (Exception e) {
         throw new OpenEJBException("Encountered unknown error parsing the webservices.xml file: " + url.toExternalForm(), e);
      }
   }

   public static JavaWsdlMapping readJaxrpcMapping(URL url) throws OpenEJBException {
      try {
         JavaWsdlMapping wsdlMapping = (JavaWsdlMapping)JaxbJavaee.unmarshalJavaee(JavaWsdlMapping.class, IO.read(url));
         return wsdlMapping;
      } catch (SAXException e) {
         throw new OpenEJBException("Cannot parse the JaxRPC mapping file: " + url.toExternalForm(), e);
      } catch (JAXBException e) {
         throw new OpenEJBException("Cannot unmarshall the JaxRPC mapping file: " + url.toExternalForm(), e);
      } catch (IOException e) {
         throw new OpenEJBException("Cannot read the JaxRPC mapping file: " + url.toExternalForm(), e);
      } catch (Exception e) {
         throw new OpenEJBException("Encountered unknown error parsing the JaxRPC mapping file: " + url.toExternalForm(), e);
      }
   }

   public static Connector readConnector(URL url) throws OpenEJBException {
      Connector connector;
      try {
         connector = (Connector)JaxbJavaee.unmarshalJavaee(Connector.class, IO.read(url));
      } catch (JAXBException e) {
         try {
            Connector10 connector10 = (Connector10)JaxbJavaee.unmarshalJavaee(Connector10.class, IO.read(url));
            connector = Connector.newConnector(connector10);
         } catch (SAXException | ParserConfigurationException var4) {
            throw new OpenEJBException("Cannot parse the ra.xml file: " + url.toExternalForm(), e);
         } catch (JAXBException var5) {
            throw new OpenEJBException("Cannot unmarshall the ra.xml file: " + url.toExternalForm(), e);
         } catch (IOException var6) {
            throw new OpenEJBException("Cannot read the ra.xml file: " + url.toExternalForm(), e);
         }
      } catch (SAXException e) {
         throw new OpenEJBException("Cannot parse the ra.xml file: " + url.toExternalForm(), e);
      } catch (IOException e) {
         throw new OpenEJBException("Cannot read the ra.xml file: " + url.toExternalForm(), e);
      } catch (Exception e) {
         throw new OpenEJBException("Encountered unknown error parsing the ra.xml file: " + url.toExternalForm(), e);
      }

      return connector;
   }

   public static WebApp readWebApp(URL url) throws OpenEJBException {
      try {
         WebApp webApp = WebXml.unmarshal(url);
         return webApp;
      } catch (SAXException e) {
         throw new OpenEJBException("Cannot parse the web.xml file: " + url.toExternalForm(), e);
      } catch (JAXBException e) {
         throw new OpenEJBException("Cannot unmarshall the web.xml file: " + url.toExternalForm(), e);
      } catch (IOException e) {
         throw new OpenEJBException("Cannot read the web.xml file: " + url.toExternalForm(), e);
      } catch (Exception e) {
         throw new OpenEJBException("Encountered unknown error parsing the web.xml file: " + url.toExternalForm(), e);
      }
   }

   public static TldTaglib readTldTaglib(URL url) throws OpenEJBException {
      if (!url.getPath().contains("jstl-1.2.jar") && (!url.getPath().contains("taglibs-standard-") || !url.getPath().contains(".jar!"))) {
         if (url.getPath().contains("myfaces-impl")) {
            TldTaglib taglib = new TldTaglib();
            Listener listener = new Listener();
            listener.setListenerClass("org.apache.myfaces.webapp.StartupServletContextListener");
            taglib.getListener().add(listener);
            return taglib;
         } else {
            try {
               return TldTaglibXml.unmarshal(url);
            } catch (SAXException e) {
               String message = "Cannot parse the JSP tag library definition file: " + url.toExternalForm();
               logger.warning(message);
               logger.debug(message, (Throwable)e);
            } catch (JAXBException e) {
               String message = "Cannot unmarshall the JSP tag library definition file: " + url.toExternalForm();
               logger.warning(message);
               logger.debug(message, (Throwable)e);
            } catch (IOException e) {
               String message = "Cannot read the JSP tag library definition file: " + url.toExternalForm();
               logger.warning(message);
               logger.debug(message, (Throwable)e);
            } catch (Exception e) {
               String message = "Encountered unknown error parsing the JSP tag library definition file: " + url.toExternalForm();
               logger.warning(message);
               logger.debug(message, (Throwable)e);
            }

            return SKIP_TAGLIB;
         }
      } else {
         return SKIP_TAGLIB;
      }
   }

   public static FacesConfig readFacesConfig(URL url) throws OpenEJBException {
      try {
         Source src = getSource(url);
         if (src == null) {
            return new FacesConfig();
         } else {
            String content = IO.slurp(src.get());
            return isEmpty(new ByteArrayInputStream(content.getBytes()), "faces-config") ? new FacesConfig() : FacesConfigXml.unmarshal(new ByteArrayInputStream(content.getBytes()));
         }
      } catch (SAXException e) {
         throw new OpenEJBException("Cannot parse the faces configuration file: " + url.toExternalForm(), e);
      } catch (JAXBException e) {
         throw new OpenEJBException("Cannot unmarshall the faces configuration file: " + url.toExternalForm(), e);
      } catch (IOException e) {
         throw new OpenEJBException("Cannot read the faces configuration file: " + url.toExternalForm(), e);
      } catch (Exception e) {
         throw new OpenEJBException("Encountered unknown error parsing the faces configuration file: " + url.toExternalForm(), e);
      }
   }

   private static Source getSource(Object o) {
      if (o instanceof URL) {
         return new UrlSource((URL)o);
      } else if (o instanceof Source) {
         return (Source)o;
      } else {
         return o instanceof String ? new StringSource((String)o) : null;
      }
   }

   static {
      logger = Logger.getInstance(LogCategory.OPENEJB_STARTUP, ReadDescriptors.class);
      ROOT_URL_FROM_WEBINF = SystemInstance.get().getOptions().get("openejb.jpa.root-url-from-webinf", false);
      SKIP_TAGLIB = new TldTaglib();
   }

   public static class UrlSource implements Source {
      private final URL url;

      public UrlSource(URL url) {
         this.url = url;
      }

      public InputStream get() throws IOException {
         return IO.read(this.url);
      }

      public URL getUrl() {
         return this.url;
      }

      public String toString() {
         return "UrlSource{url=" + this.url + '}';
      }
   }

   public static class StringSource implements Source {
      private final byte[] bytes;
      private final String toString;

      public StringSource(String content) {
         this.toString = content;
         this.bytes = content.getBytes();
      }

      public InputStream get() throws IOException {
         return new ByteArrayInputStream(this.bytes);
      }

      public String toString() {
         return "StringSource{content=" + this.toString + '}';
      }
   }

   public interface Source {
      InputStream get() throws IOException;
   }
}
