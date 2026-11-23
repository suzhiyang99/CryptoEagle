package org.apache.tika.sax;

import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;

public class ExpandedTitleContentHandler extends ContentHandlerDecorator {
   private boolean isTitleTagOpen;
   private static final String TITLE_TAG = "TITLE";

   public ExpandedTitleContentHandler() {
   }

   public ExpandedTitleContentHandler(ContentHandler handler) {
      super(handler);
   }

   public void startDocument() throws SAXException {
      super.startDocument();
      this.isTitleTagOpen = false;
   }

   public void startElement(String uri, String localName, String qName, Attributes atts) throws SAXException {
      super.startElement(uri, localName, qName, atts);
      if ("TITLE".equalsIgnoreCase(localName) && "http://www.w3.org/1999/xhtml".equals(uri)) {
         this.isTitleTagOpen = true;
      }

   }

   public void endElement(String uri, String localName, String qName) throws SAXException {
      super.endElement(uri, localName, qName);
      if ("TITLE".equalsIgnoreCase(localName) && "http://www.w3.org/1999/xhtml".equals(uri)) {
         this.isTitleTagOpen = false;
      }

   }

   public void characters(char[] ch, int start, int length) throws SAXException {
      if (this.isTitleTagOpen && length == 0) {
         try {
            super.characters(new char[0], 0, 1);
         } catch (ArrayIndexOutOfBoundsException var5) {
         }
      } else {
         super.characters(ch, start, length);
      }

   }
}
