package org.apache.tika.sax;

import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class TeeContentHandler extends DefaultHandler {
   private final ContentHandler[] handlers;

   public TeeContentHandler(ContentHandler... handlers) {
      this.handlers = handlers;
   }

   public void startPrefixMapping(String prefix, String uri) throws SAXException {
      for(ContentHandler handler : this.handlers) {
         handler.startPrefixMapping(prefix, uri);
      }

   }

   public void endPrefixMapping(String prefix) throws SAXException {
      for(ContentHandler handler : this.handlers) {
         handler.endPrefixMapping(prefix);
      }

   }

   public void processingInstruction(String target, String data) throws SAXException {
      for(ContentHandler handler : this.handlers) {
         handler.processingInstruction(target, data);
      }

   }

   public void setDocumentLocator(Locator locator) {
      for(ContentHandler handler : this.handlers) {
         handler.setDocumentLocator(locator);
      }

   }

   public void startDocument() throws SAXException {
      for(ContentHandler handler : this.handlers) {
         handler.startDocument();
      }

   }

   public void endDocument() throws SAXException {
      for(ContentHandler handler : this.handlers) {
         handler.endDocument();
      }

   }

   public void startElement(String uri, String localName, String name, Attributes atts) throws SAXException {
      for(ContentHandler handler : this.handlers) {
         handler.startElement(uri, localName, name, atts);
      }

   }

   public void endElement(String uri, String localName, String name) throws SAXException {
      for(ContentHandler handler : this.handlers) {
         handler.endElement(uri, localName, name);
      }

   }

   public void characters(char[] ch, int start, int length) throws SAXException {
      for(ContentHandler handler : this.handlers) {
         handler.characters(ch, start, length);
      }

   }

   public void ignorableWhitespace(char[] ch, int start, int length) throws SAXException {
      for(ContentHandler handler : this.handlers) {
         handler.ignorableWhitespace(ch, start, length);
      }

   }

   public void skippedEntity(String name) throws SAXException {
      for(ContentHandler handler : this.handlers) {
         handler.skippedEntity(name);
      }

   }
}
