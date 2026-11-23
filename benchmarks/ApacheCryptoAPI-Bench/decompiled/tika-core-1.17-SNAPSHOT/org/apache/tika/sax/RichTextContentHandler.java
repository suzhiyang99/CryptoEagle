package org.apache.tika.sax;

import java.io.Writer;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

public class RichTextContentHandler extends WriteOutContentHandler {
   public RichTextContentHandler(Writer writer) {
      super(writer);
   }

   public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
      super.startElement(uri, localName, qName, attributes);
      if ("img".equals(localName) && attributes.getValue("alt") != null) {
         String nfo = "[image: " + attributes.getValue("alt") + ']';
         this.characters(nfo.toCharArray(), 0, nfo.length());
      }

      if ("a".equals(localName) && attributes.getValue("name") != null) {
         String nfo = "[bookmark: " + attributes.getValue("name") + ']';
         this.characters(nfo.toCharArray(), 0, nfo.length());
      }

   }
}
