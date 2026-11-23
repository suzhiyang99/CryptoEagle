package org.apache.openejb.config;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;
import javax.xml.parsers.SAXParser;
import org.apache.openejb.util.Saxs;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public final class ScanUtil {
   private ScanUtil() {
   }

   public static ScanHandler read(URL scanXml) throws IOException {
      try {
         SAXParser parser = Saxs.factory().newSAXParser();
         ScanHandler handler = new ScanHandler();
         parser.parse(new BufferedInputStream(scanXml.openStream()), handler);
         return handler;
      } catch (Exception var3) {
         throw new IOException("can't parse " + scanXml.toExternalForm());
      }
   }

   public static final class ScanHandler extends DefaultHandler {
      private final Set<String> classes = new HashSet();
      private final Set<String> packages = new HashSet();
      private Set<String> current;
      private boolean optimized = true;

      public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
         if (qName.equals("class")) {
            this.current = this.classes;
         } else if (qName.equals("package")) {
            this.current = this.packages;
         } else if (qName.equals("scan")) {
            String optimized = attributes.getValue("optimized");
            this.optimized = optimized == null || Boolean.parseBoolean(optimized);
         }

      }

      public void characters(char[] ch, int start, int length) throws SAXException {
         if (this.current != null) {
            this.current.add(new String(ch, start, length));
         }

      }

      public void endElement(String uri, String localName, String qName) throws SAXException {
         this.current = null;
      }

      public boolean isOptimized() {
         return this.optimized;
      }

      public Set<String> getPackages() {
         return this.packages;
      }

      public Set<String> getClasses() {
         return this.classes;
      }
   }
}
