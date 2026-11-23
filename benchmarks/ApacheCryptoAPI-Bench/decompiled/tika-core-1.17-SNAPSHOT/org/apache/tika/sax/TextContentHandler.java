package org.apache.tika.sax;

import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class TextContentHandler extends DefaultHandler {
   private static final char[] SPACE = new char[]{' '};
   private final ContentHandler delegate;
   private final boolean addSpaceBetweenElements;

   public TextContentHandler(ContentHandler delegate) {
      this(delegate, false);
   }

   public TextContentHandler(ContentHandler delegate, boolean addSpaceBetweenElements) {
      this.delegate = delegate;
      this.addSpaceBetweenElements = addSpaceBetweenElements;
   }

   public void setDocumentLocator(Locator locator) {
      this.delegate.setDocumentLocator(locator);
   }

   public void characters(char[] ch, int start, int length) throws SAXException {
      this.delegate.characters(ch, start, length);
   }

   public void ignorableWhitespace(char[] ch, int start, int length) throws SAXException {
      this.delegate.ignorableWhitespace(ch, start, length);
   }

   public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
      if (this.addSpaceBetweenElements) {
         this.delegate.characters(SPACE, 0, SPACE.length);
      }

   }

   public void startDocument() throws SAXException {
      this.delegate.startDocument();
   }

   public void endDocument() throws SAXException {
      this.delegate.endDocument();
   }

   public String toString() {
      return this.delegate.toString();
   }
}
