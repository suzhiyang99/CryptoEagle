package org.apache.openejb.config.sys;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import org.apache.openejb.config.SystemProperty;
import org.apache.openejb.util.Join;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class StackHandler extends DefaultHandler {
   private static final boolean DEBUG = Boolean.getBoolean("openejb.sax.debug");
   private final List<DefaultHandler> handlers = new LinkedList();

   protected DefaultHandler get() {
      return (DefaultHandler)this.handlers.get(0);
   }

   protected DefaultHandler pop() {
      return (DefaultHandler)this.handlers.remove(0);
   }

   protected void checkAttributes(Attributes attributes, String... allowed) throws SAXException {
      this.checkAttributes(attributes, Arrays.asList(allowed));
   }

   protected void checkAttributes(Attributes attributes, List<String> allowed) throws SAXException {
      List<String> invalid = new ArrayList();

      for(int i = 0; i < attributes.getLength(); ++i) {
         if (!allowed.contains(attributes.getLocalName(i))) {
            invalid.add(attributes.getLocalName(i));
         }
      }

      if (invalid.size() > 0) {
         throw new SAXException("Unsupported Attribute(s): " + Join.join(", ", (Collection)invalid) + ".  Supported Attributes are: " + Join.join(", ", (Collection)allowed) + ".  If the setting is a configuration property it must be placed inside the element body.");
      }
   }

   protected void push(DefaultHandler handler) {
      if (DEBUG) {
         for(DefaultHandler ignored : this.handlers) {
            System.out.print("  ");
         }

         System.out.println("+ " + handler);
      }

      this.handlers.add(0, handler);
   }

   public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
      if (DEBUG) {
         for(DefaultHandler ignored : this.handlers) {
            System.out.print("  ");
         }

         System.out.println("> " + this.get());
      }

      this.get().startElement(uri, localName, qName, attributes);
   }

   public void endElement(String uri, String localName, String qName) throws SAXException {
      this.get().endElement(uri, localName, qName);
      if (!DEBUG) {
         this.pop();
      } else {
         for(DefaultHandler ignored : this.handlers) {
            System.out.print("  ");
         }

         System.out.println(" - " + this.pop());
      }

   }

   public void characters(char[] ch, int start, int length) throws SAXException {
      this.get().characters(ch, start, length);
   }

   public class Content extends DefaultHandler {
      private StringBuilder characters = new StringBuilder();

      public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
         this.characters = new StringBuilder();
      }

      public void characters(char[] ch, int start, int length) {
         this.characters.append(new String(ch, start, length));
      }

      public void endElement(String uri, String localName, String qName) {
         this.setValue(this.characters.toString());
      }

      public void setValue(String text) {
      }
   }

   public class SystemPropertyElement extends Content {
      private final List<String> allowed = Arrays.asList("name", "value");
      private final SystemProperty built;
      private final List<SystemProperty> list;

      public SystemPropertyElement(List<SystemProperty> systemProperties) {
         this.list = systemProperties;
         this.built = new SystemProperty();
      }

      public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
         super.startElement(uri, localName, qName, attributes);
         this.built.setName(attributes.getValue("name"));
         this.built.setValue(attributes.getValue("value"));
         StackHandler.this.checkAttributes(attributes, this.allowed);
      }

      public void endElement(String uri, String localName, String qName) {
         this.list.add(this.built);
         super.endElement(uri, localName, qName);
      }
   }

   public abstract class ServiceElement<S extends AbstractService> extends Content {
      final S service;

      protected ServiceElement(S service) {
         this.service = service;
      }

      public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
         if (attributes.getValue("type") != null) {
            this.service.setType(attributes.getValue("type"));
         }

         if (attributes.getValue("jar") != null) {
            this.service.setJar(attributes.getValue("jar"));
         }

         if (attributes.getValue("provider") != null) {
            this.service.setProvider(attributes.getValue("provider"));
         }

         if (attributes.getValue("id") != null) {
            this.service.setId(attributes.getValue("id"));
         }

         if (attributes.getValue("class-name") != null) {
            this.service.setClassName(attributes.getValue("class-name"));
         }

         if (attributes.getValue("constructor") != null) {
            this.service.setConstructor(attributes.getValue("constructor"));
         }

         if (attributes.getValue("factory-name") != null) {
            this.service.setFactoryName(attributes.getValue("factory-name"));
         }

         if (attributes.getValue("classpath") != null) {
            this.service.setClasspath(attributes.getValue("classpath"));
         }

         if (attributes.getValue("classpath-api") != null) {
            this.service.setClasspathAPI(attributes.getValue("classpath-api"));
         }

         StackHandler.this.checkAttributes(attributes, this.getAttributes());
      }

      protected List<String> getAttributes() {
         List<String> attributes = new ArrayList();
         attributes.add("type");
         attributes.add("jar");
         attributes.add("provider");
         attributes.add("id");
         attributes.add("class-name");
         attributes.add("constructor");
         attributes.add("factory-name");
         attributes.add("classpath");
         return attributes;
      }

      public void setValue(String text) {
         try {
            this.service.getProperties().putAll((new PropertiesAdapter()).unmarshal(text));
         } catch (Exception e) {
            throw new RuntimeException(e);
         }
      }
   }

   public class ResourceElement extends ServiceElement<Resource> {
      private final Collection<Resource> resources;

      public ResourceElement(Collection<Resource> resources) {
         super(new Resource());
         this.resources = resources;
      }

      public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
         super.startElement(uri, localName, qName, attributes);
         ((Resource)this.service).setJndi(attributes.getValue("jndi"));
         ((Resource)this.service).setPostConstruct(attributes.getValue("post-construct"));
         ((Resource)this.service).setPreDestroy(attributes.getValue("pre-destroy"));
         ((Resource)this.service).setTemplate(attributes.getValue("template"));
         ((Resource)this.service).setPropertiesProvider(attributes.getValue("property-provider"));
         if (((Resource)this.service).getPropertiesProvider() == null) {
            ((Resource)this.service).setPropertiesProvider(attributes.getValue("properties-provider"));
         }

         String aliases = attributes.getValue("aliases");
         if (aliases != null) {
            ((Resource)this.service).getAliases().addAll(Arrays.asList(aliases.split(",")));
         }

         String dependsOn = attributes.getValue("depends-on");
         if (dependsOn != null) {
            ((Resource)this.service).getDependsOn().addAll(Arrays.asList(dependsOn.split(",")));
         }

      }

      public void endElement(String uri, String localName, String qName) {
         this.resources.add(this.service);
         super.endElement(uri, localName, qName);
      }

      protected List<String> getAttributes() {
         List<String> attributes = super.getAttributes();
         attributes.add("jndi");
         attributes.add("aliases");
         attributes.add("properties-provider");
         attributes.add("property-provider");
         attributes.add("depends-on");
         return attributes;
      }
   }

   public class DeclaredServiceElement extends ServiceElement<Service> {
      private final Collection<Service> services;

      public DeclaredServiceElement(Collection<Service> services) {
         super(new Service());
         this.services = services;
      }

      public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
         super.startElement(uri, localName, qName, attributes);
         ((Service)this.service).setClazz(attributes.getValue("class"));
      }

      public void endElement(String uri, String localName, String qName) {
         this.services.add(this.service);
         super.endElement(uri, localName, qName);
      }

      protected List<String> getAttributes() {
         List<String> attributes = super.getAttributes();
         attributes.add("class");
         return attributes;
      }
   }
}
