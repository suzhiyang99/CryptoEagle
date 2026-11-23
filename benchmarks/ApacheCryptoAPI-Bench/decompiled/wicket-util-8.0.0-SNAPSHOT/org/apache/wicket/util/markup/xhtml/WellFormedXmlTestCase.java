package org.apache.wicket.util.markup.xhtml;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.junit.Test;
import org.xml.sax.EntityResolver;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

public class WellFormedXmlTestCase {
   private DocumentBuilderFactory factory;
   private static final FileFilter fileFilter = new FileFilter() {
      public boolean accept(File pathname) {
         String path = pathname.getAbsolutePath().replace('\\', '/');
         return !path.contains("/src/test/") && !path.contains("/target/") && !"package.html".equals(pathname.getName()) && (pathname.isDirectory() || pathname.getName().endsWith(".html"));
      }
   };
   private static final ErrorHandler errorHandler = new ErrorHandler() {
      public void warning(SAXParseException exception) throws SAXException {
         throw exception;
      }

      public void error(SAXParseException exception) throws SAXException {
         throw exception;
      }

      public void fatalError(SAXParseException exception) throws SAXException {
         throw exception;
      }
   };
   private static final EntityResolver entityResolver = new EntityResolver() {
      private final Map<String, String> systemIdToUri = new HashMap();

      {
         this.systemIdToUri.put("http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd", "xhtml1-transitional.dtd");
         this.systemIdToUri.put("http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd", "xhtml1-strict.dtd");
         this.systemIdToUri.put("http://www.w3.org/TR/html4/loose.dtd", "xhtml1-transitional.dtd");
         this.systemIdToUri.put("http://www.w3.org/TR/html4/strict.dtd", "xhtml1-strict.dtd");
      }

      public InputSource resolveEntity(String publicId, String systemId) throws SAXException, IOException {
         String uri = (String)this.systemIdToUri.get(systemId);
         return uri != null ? new InputSource(WellFormedXmlTestCase.class.getResource(uri).toString()) : null;
      }
   };

   @Test
   public void markupFiles() {
      this.factory = DocumentBuilderFactory.newInstance();
      this.factory.setNamespaceAware(true);
      File root = (new File("")).getAbsoluteFile();
      this.processDirectory(root);
   }

   private void processDirectory(File dir) {
      for(File f : dir.listFiles(fileFilter)) {
         if (f.isDirectory()) {
            this.processDirectory(f);
         } else {
            this.processFile(f);
         }
      }

   }

   private void processFile(File file) {
      DocumentBuilder builder;
      try {
         builder = this.factory.newDocumentBuilder();
      } catch (ParserConfigurationException e) {
         throw new RuntimeException("Configuration exception while parsing xml markup.", e);
      }

      builder.setEntityResolver(entityResolver);
      builder.setErrorHandler(errorHandler);

      try {
         builder.parse(file);
      } catch (SAXException e) {
         throw new RuntimeException("Parsing xml sax failed, file: " + file, e);
      } catch (IOException e) {
         throw new RuntimeException("Parsing xml io failed, file: " + file, e);
      }
   }
}
