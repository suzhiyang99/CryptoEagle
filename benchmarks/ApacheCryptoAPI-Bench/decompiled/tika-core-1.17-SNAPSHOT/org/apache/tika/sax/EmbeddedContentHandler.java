package org.apache.tika.sax;

import org.xml.sax.ContentHandler;

public class EmbeddedContentHandler extends ContentHandlerDecorator {
   public EmbeddedContentHandler(ContentHandler handler) {
      super(handler);
   }

   public void startDocument() {
   }

   public void endDocument() {
   }
}
