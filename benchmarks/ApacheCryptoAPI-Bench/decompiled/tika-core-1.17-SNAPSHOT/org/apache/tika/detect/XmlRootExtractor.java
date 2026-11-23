package org.apache.tika.detect;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import javax.xml.namespace.QName;
import javax.xml.parsers.SAXParserFactory;
import org.apache.tika.io.CloseShieldInputStream;
import org.apache.tika.sax.OfflineContentHandler;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.helpers.DefaultHandler;

public class XmlRootExtractor {
   public QName extractRootElement(byte[] data) {
      return this.extractRootElement((InputStream)(new ByteArrayInputStream(data)));
   }

   public QName extractRootElement(InputStream stream) {
      ExtractorHandler handler = new ExtractorHandler();

      try {
         SAXParserFactory factory = SAXParserFactory.newInstance();
         factory.setNamespaceAware(true);
         factory.setValidating(false);

         try {
            factory.setFeature("http://javax.xml.XMLConstants/feature/secure-processing", true);
         } catch (SAXNotRecognizedException var5) {
         }

         factory.newSAXParser().parse(new CloseShieldInputStream(stream), new OfflineContentHandler(handler));
      } catch (Exception var6) {
      }

      return handler.rootElement;
   }

   private static class ExtractorHandler extends DefaultHandler {
      private QName rootElement;

      private ExtractorHandler() {
         this.rootElement = null;
      }

      public void startElement(String uri, String local, String name, Attributes attributes) throws SAXException {
         this.rootElement = new QName(uri, local);
         throw new SAXException("Aborting: root element received");
      }
   }
}
