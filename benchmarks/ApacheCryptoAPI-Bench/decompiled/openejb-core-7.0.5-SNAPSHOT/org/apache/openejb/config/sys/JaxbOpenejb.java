package org.apache.openejb.config.sys;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.MarshalException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.ValidationEvent;
import javax.xml.bind.ValidationEventHandler;
import javax.xml.bind.ValidationException;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.transform.sax.SAXSource;
import org.apache.openejb.OpenEJBException;
import org.apache.openejb.config.SystemProperty;
import org.apache.openejb.jee.JAXBContextFactory;
import org.apache.openejb.loader.IO;
import org.apache.openejb.util.Join;
import org.apache.openejb.util.Messages;
import org.apache.openejb.util.Saxs;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.helpers.XMLFilterImpl;

public abstract class JaxbOpenejb {
   public static final ThreadLocal<Set<String>> currentPublicId = new ThreadLocal();
   private static final Map<Class, JAXBContext> jaxbContexts = new HashMap();

   public static <T> T create(Class<T> type) {
      if (type == null) {
         throw new NullPointerException("type is null");
      } else if (type == ConnectionManager.class) {
         return (T)createConnectionManager();
      } else if (type == Connector.class) {
         return (T)createConnector();
      } else if (type == Container.class) {
         return (T)createContainer();
      } else if (type == Deployments.class) {
         return (T)createDeployments();
      } else if (type == JndiProvider.class) {
         return (T)createJndiProvider();
      } else if (type == Openejb.class) {
         return (T)createOpenejb();
      } else if (type == ProxyFactory.class) {
         return (T)createProxyFactory();
      } else if (type == Resource.class) {
         return (T)createResource();
      } else if (type == SecurityService.class) {
         return (T)createSecurityService();
      } else if (type == ServiceProvider.class) {
         return (T)createServiceProvider();
      } else if (type == ServicesJar.class) {
         return (T)createServicesJar();
      } else if (type == TransactionManager.class) {
         return (T)createTransactionManager();
      } else if (type == Tomee.class) {
         return (T)createTomee();
      } else {
         throw new IllegalArgumentException("Unknown type " + type.getName());
      }
   }

   public static <T> T create(String type) {
      if (type == null) {
         throw new NullPointerException("type is null");
      } else if (type.equalsIgnoreCase("ConnectionManager")) {
         return (T)createConnectionManager();
      } else if (type.equalsIgnoreCase("Connector")) {
         return (T)createConnector();
      } else if (type.equalsIgnoreCase("Container")) {
         return (T)createContainer();
      } else if (type.equalsIgnoreCase("Deployments")) {
         return (T)createDeployments();
      } else if (type.equalsIgnoreCase("JndiProvider")) {
         return (T)createJndiProvider();
      } else if (type.equalsIgnoreCase("Openejb")) {
         return (T)createOpenejb();
      } else if (type.equalsIgnoreCase("Tomee")) {
         return (T)createTomee();
      } else if (type.equalsIgnoreCase("ProxyFactory")) {
         return (T)createProxyFactory();
      } else if (type.equalsIgnoreCase("Resource")) {
         return (T)createResource();
      } else if (type.equalsIgnoreCase("SecurityService")) {
         return (T)createSecurityService();
      } else if (type.equalsIgnoreCase("ServiceProvider")) {
         return (T)createServiceProvider();
      } else if (type.equalsIgnoreCase("ServicesJar")) {
         return (T)createServicesJar();
      } else if (type.equalsIgnoreCase("TransactionManager")) {
         return (T)createTransactionManager();
      } else if (type.equalsIgnoreCase("Service")) {
         return (T)createService();
      } else if (type.equalsIgnoreCase("System-Property")) {
         return (T)createSystemProperty();
      } else {
         throw new IllegalArgumentException("Unknown type " + type);
      }
   }

   public static ServicesJar readServicesJar(String providerPath) throws OpenEJBException {
      ClassLoader loader = Thread.currentThread().getContextClassLoader();
      List<URL> sources = new ArrayList();
      String path1 = "META-INF/" + providerPath + "/service-jar.xml";
      String path2 = providerPath.replace('.', '/') + "/service-jar.xml";
      List<String> paths = Arrays.asList(path1, path2);

      for(String p : paths) {
         try {
            Enumeration<URL> resources = loader.getResources(p);
            if (resources != null) {
               sources.addAll(Collections.list(resources));
            }
         } catch (IOException e) {
            throw new OpenEJBException(String.format("Unable to scan for service-jar.xml file: getResource('%s')", p), e);
         }
      }

      if (sources.size() == 0) {
         throw new OpenEJBException("No service-jar.xml files found: searched " + Join.join(" and ", (Collection)paths));
      } else {
         ServicesJar servicesJar = new ServicesJar();

         for(URL url : sources) {
            try {
               InputStream read = IO.read(url);

               try {
                  ServicesJar jar = parseServicesJar(read);
                  servicesJar.getServiceProvider().addAll(jar.getServiceProvider());
               } catch (Exception e) {
                  throw new OpenEJBException("Unable to parse service-jar.xml file for provider " + providerPath + " at " + url, e);
               } finally {
                  IO.close(read);
               }
            } catch (IOException e) {
               throw new OpenEJBException("Unable to read service-jar.xml file for provider " + providerPath + " at " + url, e);
            }
         }

         return servicesJar;
      }
   }

   public static ServicesJar parseServicesJar(InputStream in) throws ParserConfigurationException, SAXException, IOException {
      InputSource inputSource = new InputSource(in);
      SAXParser parser = Saxs.namespaceAwareFactory().newSAXParser();
      ServicesJar servicesJar1 = new ServicesJar();
      parser.parse(inputSource, new OpenEJBHandler(servicesJar1));
      return servicesJar1;
   }

   public static Openejb readConfig(String configFile) throws OpenEJBException {
      InputStream in = null;

      Openejb e;
      try {
         if (configFile.startsWith("jar:")) {
            URL url = new URL(configFile);
            in = IO.read(url);
         } else if (configFile.startsWith("file:")) {
            URL url = new URL(configFile);
            in = IO.read(url);
         } else {
            in = IO.read(new File(configFile));
         }

         if (!configFile.endsWith(".json")) {
            e = readConfig(new InputSource(in));
            return e;
         }

         e = (Openejb)JSonConfigReader.read(Openejb.class, in);
      } catch (MalformedURLException e) {
         throw new OpenEJBException("Unable to resolve location " + configFile, e);
      } catch (Exception e) {
         throw new OpenEJBException("Unable to read OpenEJB configuration file at " + configFile, e);
      } finally {
         IO.close(in);
      }

      return e;
   }

   public static Openejb readConfig(InputSource in) throws IOException, SAXException, ParserConfigurationException {
      return SaxOpenejb.parse(in);
   }

   public static void writeConfig(String configFile, Openejb openejb) throws OpenEJBException {
      OutputStream out = null;

      try {
         File file = new File(configFile);
         out = IO.write(file);
         marshal(Openejb.class, openejb, out);
      } catch (IOException e) {
         throw new OpenEJBException(messages().format("conf.1040", configFile, e.getLocalizedMessage()), e);
      } catch (MarshalException e) {
         if (e.getCause() instanceof IOException) {
            throw new OpenEJBException(messages().format("conf.1040", configFile, e.getLocalizedMessage()), e);
         }

         throw new OpenEJBException(messages().format("conf.1050", configFile, e.getLocalizedMessage()), e);
      } catch (ValidationException e) {
         throw new OpenEJBException(messages().format("conf.1060", configFile, e.getLocalizedMessage()), e);
      } catch (JAXBException e) {
         throw new OpenEJBException(e);
      } finally {
         if (out != null) {
            try {
               out.close();
            } catch (Exception var13) {
            }
         }

      }

   }

   private static Messages messages() {
      return new Messages("org.apache.openejb.util.resources");
   }

   public static <T> String marshal(Class<T> type, Object object) throws JAXBException {
      ByteArrayOutputStream baos = new ByteArrayOutputStream();
      marshal(type, object, baos);
      return new String(baos.toByteArray());
   }

   public static <T> void marshal(Class<T> type, Object object, OutputStream out) throws JAXBException {
      JAXBContext jaxbContext = getContext(type);
      Marshaller marshaller = jaxbContext.createMarshaller();
      marshaller.setProperty("jaxb.formatted.output", true);
      marshaller.marshal(object, out);
   }

   public static <T> JAXBContext getContext(Class<T> type) throws JAXBException {
      JAXBContext jaxbContext = (JAXBContext)jaxbContexts.get(type);
      if (jaxbContext == null) {
         Thread thread = Thread.currentThread();
         ClassLoader old = thread.getContextClassLoader();
         thread.setContextClassLoader(JaxbOpenejb.class.getClassLoader());

         try {
            jaxbContext = JAXBContextFactory.newInstance(new Class[]{type});
         } finally {
            thread.setContextClassLoader(old);
         }

         jaxbContexts.put(type, jaxbContext);
      }

      return jaxbContext;
   }

   public static <T> T unmarshal(Class<T> type, InputStream in, boolean filter) throws ParserConfigurationException, SAXException, JAXBException {
      InputSource inputSource = new InputSource(in);
      SAXParser parser = Saxs.namespaceAwareFactory().newSAXParser();
      JAXBContext ctx = getContext(type);
      Unmarshaller unmarshaller = ctx.createUnmarshaller();
      unmarshaller.setEventHandler(new ValidationEventHandler() {
         public boolean handleEvent(ValidationEvent validationEvent) {
            System.out.println(validationEvent);
            return false;
         }
      });
      SAXSource source;
      if (filter) {
         NamespaceFilter xmlFilter = new NamespaceFilter(parser.getXMLReader());
         xmlFilter.setContentHandler(unmarshaller.getUnmarshallerHandler());
         source = new SAXSource(xmlFilter, inputSource);
      } else {
         source = new SAXSource(inputSource);
      }

      currentPublicId.set(new TreeSet());

      Object var12;
      try {
         var12 = unmarshaller.unmarshal(source, type).getValue();
      } finally {
         currentPublicId.set((Object)null);
      }

      return (T)var12;
   }

   public static <T> T unmarshal(Class<T> type, InputStream in) throws ParserConfigurationException, SAXException, JAXBException {
      return (T)unmarshal(type, in, true);
   }

   public static ConnectionManager createConnectionManager() {
      return new ConnectionManager();
   }

   public static Connector createConnector() {
      return new Connector();
   }

   public static Container createContainer() {
      return new Container();
   }

   public static Deployments createDeployments() {
      return new Deployments();
   }

   public static Service createService() {
      return new Service();
   }

   public static SystemProperty createSystemProperty() {
      return new SystemProperty();
   }

   public static JndiProvider createJndiProvider() {
      return new JndiProvider();
   }

   public static Openejb createOpenejb() {
      return new Openejb();
   }

   private static Tomee createTomee() {
      return new Tomee();
   }

   public static ProxyFactory createProxyFactory() {
      return new ProxyFactory();
   }

   public static Resource createResource() {
      return new Resource();
   }

   public static SecurityService createSecurityService() {
      return new SecurityService();
   }

   public static ServiceProvider createServiceProvider() {
      return new ServiceProvider();
   }

   public static ServicesJar createServicesJar() {
      return new ServicesJar();
   }

   public static TransactionManager createTransactionManager() {
      return new TransactionManager();
   }

   public static class NamespaceFilter extends XMLFilterImpl {
      public NamespaceFilter(XMLReader xmlReader) {
         super(xmlReader);
      }

      public InputSource resolveEntity(String publicId, String systemId) throws SAXException, IOException {
         Set<String> publicIds = (Set)JaxbOpenejb.currentPublicId.get();
         if (publicIds != null) {
            publicIds.add(publicId);
         }

         return super.resolveEntity(publicId, systemId);
      }

      public void startElement(String uri, String localName, String qname, Attributes atts) throws SAXException {
         super.startElement("http://www.openejb.org/System/Configuration", localName, qname, atts);
      }
   }

   private static class OpenEJBHandler extends DefaultHandler {
      private final PropertiesAdapter propertiesAdapter = new PropertiesAdapter();
      private final ServicesJar servicesJar;
      private ServiceProvider provider;
      private StringBuilder content;

      public OpenEJBHandler(ServicesJar servicesJar1) {
         this.servicesJar = servicesJar1;
      }

      public void startDocument() throws SAXException {
      }

      public void startElement(String uri, String localName, String qName, Attributes att) throws SAXException {
         if (localName.equals("ServiceProvider")) {
            this.provider = new ServiceProvider();
            this.provider.setId(att.getValue("", "id"));
            this.provider.setService(att.getValue("", "service"));
            this.provider.setFactoryName(att.getValue("", "factory-name"));
            this.provider.setConstructor(att.getValue("", "constructor"));
            this.provider.setClassName(att.getValue("", "class-name"));
            this.provider.setParent(att.getValue("", "parent"));
            String typesString = att.getValue("", "types");
            if (typesString != null) {
               ListAdapter listAdapter = new ListAdapter();
               List<String> types = listAdapter.unmarshal(typesString);
               this.provider.getTypes().addAll(types);
            }

            this.servicesJar.getServiceProvider().add(this.provider);
         }
      }

      public void characters(char[] ch, int start, int length) throws SAXException {
         if (this.content == null) {
            this.content = new StringBuilder();
         }

         this.content.append(ch, start, length);
      }

      public void endElement(String uri, String localName, String qName) throws SAXException {
         if (this.provider != null && this.content != null) {
            try {
               this.provider.getProperties().putAll(this.propertiesAdapter.unmarshal(this.content.toString()));
            } catch (Exception e) {
               throw new SAXException(e);
            }

            this.provider = null;
            this.content = null;
         }
      }
   }
}
