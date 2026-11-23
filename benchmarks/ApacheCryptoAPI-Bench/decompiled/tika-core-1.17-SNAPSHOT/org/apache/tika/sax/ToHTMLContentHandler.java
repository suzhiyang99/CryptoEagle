package org.apache.tika.sax;

import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import org.xml.sax.SAXException;

public class ToHTMLContentHandler extends ToXMLContentHandler {
   private static final Set<String> EMPTY_ELEMENTS = new HashSet(Arrays.asList("area", "base", "basefont", "br", "col", "frame", "hr", "img", "input", "isindex", "link", "meta", "param"));

   public ToHTMLContentHandler(OutputStream stream, String encoding) throws UnsupportedEncodingException {
      super(stream, encoding);
   }

   public ToHTMLContentHandler() {
   }

   public void startDocument() throws SAXException {
   }

   public void endElement(String uri, String localName, String qName) throws SAXException {
      if (this.inStartElement) {
         this.write('>');
         this.inStartElement = false;
         if (EMPTY_ELEMENTS.contains(localName)) {
            this.namespaces.clear();
            return;
         }
      }

      super.endElement(uri, localName, qName);
   }
}
