package org.apache.openejb.config.sys;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import org.apache.openejb.config.SystemProperty;
import org.apache.openejb.loader.SystemInstance;
import org.apache.openejb.util.Saxs;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

class SaxOpenejb extends StackHandler {
   public static final String HOME_VAR = "$home";
   private final Openejb openejb = new Openejb();

   public static Openejb parse(InputSource source) throws SAXException, ParserConfigurationException, IOException {
      SAXParser parser = Saxs.factory().newSAXParser();
      SaxOpenejb sax = new SaxOpenejb();
      parser.parse(source, sax);
      return sax.openejb;
   }

   public void startDocument() throws SAXException {
      this.push(new Document());
   }

   Openejb getOpenejb() {
      return this.openejb;
   }

   public class Root extends DefaultHandler {
      public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
         switch (localName) {
            case "Container":
               SaxOpenejb.this.push(SaxOpenejb.this.new ContainerElement());
               break;
            case "JndiProvider":
               SaxOpenejb.this.push(SaxOpenejb.this.new JndiProviderElement());
               break;
            case "SecurityService":
               SaxOpenejb.this.push(SaxOpenejb.this.new SecurityServiceElement());
               break;
            case "TransactionManager":
               SaxOpenejb.this.push(SaxOpenejb.this.new TransactionManagerElement());
               break;
            case "ConnectionManager":
               SaxOpenejb.this.push(SaxOpenejb.this.new ConnectionManagerElement());
               break;
            case "ProxyFactory":
               SaxOpenejb.this.push(SaxOpenejb.this.new ProxyFactoryElement());
               break;
            case "Resource":
               SaxOpenejb.this.push(SaxOpenejb.this.new ResourceElement(SaxOpenejb.this.openejb.getResource()));
               break;
            case "Connector":
               SaxOpenejb.this.push(SaxOpenejb.this.new ResourceElement(SaxOpenejb.this.openejb.getResource()));
               break;
            case "Deployments":
               SaxOpenejb.this.push(SaxOpenejb.this.new DeploymentsElement());
               break;
            case "Import":
               SaxOpenejb.this.push(SaxOpenejb.this.new ImportElement());
               break;
            case "Service":
               SaxOpenejb.this.push(SaxOpenejb.this.new DeclaredServiceElement(SaxOpenejb.this.openejb.getServices()));
               break;
            case "System-Property":
               SaxOpenejb.this.push(SaxOpenejb.this.new SystemPropertyElement(SaxOpenejb.this.openejb.getSystemProperties()));
               break;
            default:
               throw new IllegalStateException("Unsupported Element: " + localName);
         }

         SaxOpenejb.this.get().startElement(uri, localName, qName, attributes);
      }
   }

   private class Document extends DefaultHandler {
      private Document() {
      }

      public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
         if (localName.equals("openejb")) {
            SaxOpenejb.this.push(SaxOpenejb.this.new Root());
         } else {
            if (!localName.equals("tomee")) {
               throw new SAXException("Unsupported Element: " + localName);
            }

            SaxOpenejb.this.push(SaxOpenejb.this.new Root());
         }

         SaxOpenejb.this.checkAttributes(attributes, new String[0]);
      }
   }

   private class DeploymentsElement extends DefaultHandler {
      private final Deployments deployments;

      private DeploymentsElement() {
         this.deployments = new Deployments();
      }

      public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
         this.deployments.setDir(attributes.getValue("dir"));
         this.deployments.setFile(attributes.getValue("jar"));
         this.deployments.setFile(attributes.getValue("file"));
         this.deployments.setAutoDeploy("true".equals(attributes.getValue("autoDeploy")));
         SaxOpenejb.this.checkAttributes(attributes, new String[]{"dir", "jar", "file", "autoDeploy"});
      }

      public void endElement(String uri, String localName, String qName) throws SAXException {
         SaxOpenejb.this.openejb.getDeployments().add(this.deployments);
      }
   }

   public class ContainerElement extends StackHandler.ServiceElement<Container> {
      public ContainerElement() {
         super(new Container());
      }

      public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
         super.startElement(uri, localName, qName, attributes);
         String ctype = attributes.getValue("ctype");
         if (ctype != null) {
            ((Container)this.service).setType(ctype);
         }

      }

      public void endElement(String uri, String localName, String qName) {
         SaxOpenejb.this.openejb.getContainer().add(this.service);
         super.endElement(uri, localName, qName);
      }

      protected List<String> getAttributes() {
         List<String> attributes = new ArrayList(super.getAttributes());
         attributes.add("ctype");
         return attributes;
      }
   }

   public class SecurityServiceElement extends StackHandler.ServiceElement<SecurityService> {
      public SecurityServiceElement() {
         super(new SecurityService());
      }

      public void endElement(String uri, String localName, String qName) {
         SaxOpenejb.this.openejb.setSecurityService(this.service);
         super.endElement(uri, localName, qName);
      }
   }

   public class ConnectionManagerElement extends StackHandler.ServiceElement<ConnectionManager> {
      public ConnectionManagerElement() {
         super(new ConnectionManager());
      }

      public void endElement(String uri, String localName, String qName) {
         SaxOpenejb.this.openejb.setConnectionManager(this.service);
         super.endElement(uri, localName, qName);
      }
   }

   public class ProxyFactoryElement extends StackHandler.ServiceElement<ProxyFactory> {
      public ProxyFactoryElement() {
         super(new ProxyFactory());
      }

      public void endElement(String uri, String localName, String qName) {
         SaxOpenejb.this.openejb.setProxyFactory(this.service);
         super.endElement(uri, localName, qName);
      }
   }

   public class TransactionManagerElement extends StackHandler.ServiceElement<TransactionManager> {
      public TransactionManagerElement() {
         super(new TransactionManager());
      }

      public void endElement(String uri, String localName, String qName) {
         SaxOpenejb.this.openejb.setTransactionManager(this.service);
         super.endElement(uri, localName, qName);
      }
   }

   public class JndiProviderElement extends StackHandler.ServiceElement<JndiProvider> {
      public JndiProviderElement() {
         super(new JndiProvider());
      }

      public void endElement(String uri, String localName, String qName) {
         SaxOpenejb.this.openejb.getJndiProvider().add(this.service);
         super.endElement(uri, localName, qName);
      }
   }

   private class ImportElement extends DefaultHandler {
      private String path;

      private ImportElement() {
      }

      public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
         this.path = attributes.getValue("path");
         SaxOpenejb.this.checkAttributes(attributes, new String[]{"path"});
      }

      public void endElement(String uri, String localName, String qName) throws SAXException {
         if (this.path != null) {
            this.updatePath();

            InputStream is;
            try {
               URL url = new URL(this.path);
               is = url.openStream();
            } catch (MalformedURLException e) {
               File file = new File(this.path);

               try {
                  is = new FileInputStream(file);
               } catch (FileNotFoundException var9) {
                  throw new SAXException("specified path '" + this.path + "' is neither an url nor a file path", e);
               }
            } catch (IOException e) {
               throw new SAXException(e);
            }

            try {
               Openejb importedOpenEJB = SaxOpenejb.parse(new InputSource(is));
               this.merge(SaxOpenejb.this.openejb, importedOpenEJB);
            } catch (IOException | ParserConfigurationException e) {
               throw new SAXException(e);
            }
         }

      }

      private void updatePath() {
         if (this.path != null && this.path.startsWith("$home")) {
            this.path = SystemInstance.get().getHome().getDirectory().getAbsolutePath() + this.path.substring("$home".length());
         }

      }

      private void merge(Openejb openejb, Openejb importedOpenEJB) {
         if (importedOpenEJB.container != null) {
            for(Container container : importedOpenEJB.container) {
               openejb.add(container);
            }
         }

         if (importedOpenEJB.jndiProvider != null) {
            for(JndiProvider jndiProvider : importedOpenEJB.jndiProvider) {
               openejb.add(jndiProvider);
            }
         }

         if (importedOpenEJB.securityService == null && importedOpenEJB.transactionManager == null && importedOpenEJB.connectionManager == null && importedOpenEJB.proxyFactory != null) {
         }

         if (importedOpenEJB.connector != null) {
            for(Connector connector : importedOpenEJB.connector) {
               openejb.add(connector);
            }
         }

         if (importedOpenEJB.resource != null) {
            for(Resource resource : importedOpenEJB.resource) {
               openejb.add(resource);
            }
         }

         if (importedOpenEJB.deployments != null) {
            for(Deployments deployment : importedOpenEJB.deployments) {
               openejb.add(deployment);
            }
         }

         if (importedOpenEJB.systemProperties != null) {
            for(SystemProperty systemProp : importedOpenEJB.systemProperties) {
               openejb.add(systemProp);
            }
         }

      }
   }
}
