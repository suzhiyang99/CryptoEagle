package org.apache.tika.parser;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.stream.XMLInputFactory;
import javax.xml.transform.Transformer;
import org.apache.tika.exception.TikaException;
import org.apache.tika.utils.XMLReaderUtils;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;
import org.xml.sax.XMLReader;

public class ParseContext implements Serializable {
   private static final long serialVersionUID = -5921436862145826534L;
   private final Map<String, Object> context = new HashMap();

   public <T> void set(Class<T> key, T value) {
      if (value != null) {
         this.context.put(key.getName(), value);
      } else {
         this.context.remove(key.getName());
      }

   }

   public <T> T get(Class<T> key) {
      return (T)this.context.get(key.getName());
   }

   public <T> T get(Class<T> key, T defaultValue) {
      T value = (T)this.get(key);
      return (T)(value != null ? value : defaultValue);
   }

   public XMLReader getXMLReader() throws TikaException {
      XMLReader reader = (XMLReader)this.get(XMLReader.class);
      return reader != null ? reader : XMLReaderUtils.getXMLReader();
   }

   public SAXParser getSAXParser() throws TikaException {
      SAXParser parser = (SAXParser)this.get(SAXParser.class);
      return parser != null ? parser : XMLReaderUtils.getSAXParser();
   }

   public SAXParserFactory getSAXParserFactory() {
      SAXParserFactory factory = (SAXParserFactory)this.get(SAXParserFactory.class);
      if (factory == null) {
         factory = SAXParserFactory.newInstance();
         factory.setNamespaceAware(true);
         factory.setValidating(false);

         try {
            factory.setFeature("http://javax.xml.XMLConstants/feature/secure-processing", true);
         } catch (ParserConfigurationException var3) {
         } catch (SAXNotSupportedException var4) {
         } catch (SAXNotRecognizedException var5) {
         }
      }

      return factory;
   }

   private DocumentBuilderFactory getDocumentBuilderFactory() {
      DocumentBuilderFactory documentBuilderFactory = (DocumentBuilderFactory)this.get(DocumentBuilderFactory.class);
      return documentBuilderFactory != null ? documentBuilderFactory : XMLReaderUtils.getDocumentBuilderFactory();
   }

   public DocumentBuilder getDocumentBuilder() throws TikaException {
      DocumentBuilder documentBuilder = (DocumentBuilder)this.get(DocumentBuilder.class);
      return documentBuilder != null ? documentBuilder : XMLReaderUtils.getDocumentBuilder();
   }

   public XMLInputFactory getXMLInputFactory() {
      XMLInputFactory factory = (XMLInputFactory)this.get(XMLInputFactory.class);
      return factory != null ? factory : XMLReaderUtils.getXMLInputFactory();
   }

   public Transformer getTransformer() throws TikaException {
      Transformer transformer = (Transformer)this.get(Transformer.class);
      return transformer != null ? transformer : XMLReaderUtils.getTransformer();
   }
}
