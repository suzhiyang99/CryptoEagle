package org.apache.tika.utils;

import java.io.IOException;
import java.io.StringReader;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLResolver;
import javax.xml.stream.XMLStreamException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import org.apache.tika.exception.TikaException;
import org.xml.sax.EntityResolver;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;
import org.xml.sax.XMLReader;

public class XMLReaderUtils {
   private static final Logger LOG = Logger.getLogger(XMLReaderUtils.class.getName());
   private static final EntityResolver IGNORING_SAX_ENTITY_RESOLVER = new EntityResolver() {
      public InputSource resolveEntity(String publicId, String systemId) throws SAXException, IOException {
         return new InputSource(new StringReader(""));
      }
   };
   private static final XMLResolver IGNORING_STAX_ENTITY_RESOLVER = new XMLResolver() {
      public Object resolveEntity(String publicID, String systemID, String baseURI, String namespace) throws XMLStreamException {
         return "";
      }
   };

   public static XMLReader getXMLReader() throws TikaException {
      XMLReader reader;
      try {
         reader = getSAXParser().getXMLReader();
      } catch (SAXException e) {
         throw new TikaException("Unable to create an XMLReader", e);
      }

      reader.setEntityResolver(IGNORING_SAX_ENTITY_RESOLVER);
      return reader;
   }

   public static SAXParser getSAXParser() throws TikaException {
      try {
         return getSAXParserFactory().newSAXParser();
      } catch (ParserConfigurationException e) {
         throw new TikaException("Unable to configure a SAX parser", e);
      } catch (SAXException e) {
         throw new TikaException("Unable to create a SAX parser", e);
      }
   }

   public static SAXParserFactory getSAXParserFactory() {
      SAXParserFactory factory = SAXParserFactory.newInstance();
      factory.setNamespaceAware(true);
      factory.setValidating(false);

      try {
         factory.setFeature("http://javax.xml.XMLConstants/feature/secure-processing", true);
      } catch (ParserConfigurationException var2) {
      } catch (SAXNotSupportedException var3) {
      } catch (SAXNotRecognizedException var4) {
      }

      return factory;
   }

   public static DocumentBuilderFactory getDocumentBuilderFactory() {
      DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
      factory.setExpandEntityReferences(false);
      factory.setNamespaceAware(true);
      factory.setValidating(false);
      trySetSAXFeature(factory, "http://javax.xml.XMLConstants/feature/secure-processing", true);
      trySetSAXFeature(factory, "http://xml.org/sax/features/external-general-entities", false);
      trySetSAXFeature(factory, "http://xml.org/sax/features/external-parameter-entities", false);
      trySetSAXFeature(factory, "http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
      trySetSAXFeature(factory, "http://apache.org/xml/features/nonvalidating/load-dtd-grammar", false);
      return factory;
   }

   public static DocumentBuilder getDocumentBuilder() throws TikaException {
      try {
         DocumentBuilderFactory documentBuilderFactory = getDocumentBuilderFactory();
         DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
         documentBuilder.setEntityResolver(IGNORING_SAX_ENTITY_RESOLVER);
         documentBuilder.setErrorHandler((ErrorHandler)null);
         return documentBuilder;
      } catch (ParserConfigurationException e) {
         throw new TikaException("XML parser not available", e);
      }
   }

   public static XMLInputFactory getXMLInputFactory() {
      XMLInputFactory factory = XMLInputFactory.newFactory();
      tryToSetStaxProperty(factory, "javax.xml.stream.isNamespaceAware", true);
      tryToSetStaxProperty(factory, "javax.xml.stream.isValidating", false);
      factory.setXMLResolver(IGNORING_STAX_ENTITY_RESOLVER);
      return factory;
   }

   private static void trySetSAXFeature(DocumentBuilderFactory documentBuilderFactory, String feature, boolean enabled) {
      try {
         documentBuilderFactory.setFeature(feature, enabled);
      } catch (Exception e) {
         LOG.log(Level.WARNING, "SAX Feature unsupported: " + feature, e);
      } catch (AbstractMethodError ame) {
         LOG.log(Level.WARNING, "Cannot set SAX feature because outdated XML parser in classpath: " + feature, ame);
      }

   }

   private static void tryToSetStaxProperty(XMLInputFactory factory, String key, boolean value) {
      try {
         factory.setProperty(key, value);
      } catch (IllegalArgumentException var4) {
      }

   }

   public static Transformer getTransformer() throws TikaException {
      try {
         TransformerFactory transformerFactory = TransformerFactory.newInstance();
         transformerFactory.setFeature("http://javax.xml.XMLConstants/feature/secure-processing", true);
         return transformerFactory.newTransformer();
      } catch (TransformerFactoryConfigurationError | TransformerConfigurationException e) {
         throw new TikaException("Transformer not available", e);
      }
   }
}
