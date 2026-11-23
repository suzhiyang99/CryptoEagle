package org.apache.tika.sax;

import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;

public class TaggedContentHandler extends ContentHandlerDecorator {
   public TaggedContentHandler(ContentHandler proxy) {
      super(proxy);
   }

   public boolean isCauseOf(SAXException exception) {
      if (exception instanceof TaggedSAXException) {
         TaggedSAXException tagged = (TaggedSAXException)exception;
         return this == tagged.getTag();
      } else {
         return false;
      }
   }

   public void throwIfCauseOf(Exception exception) throws SAXException {
      if (exception instanceof TaggedSAXException) {
         TaggedSAXException tagged = (TaggedSAXException)exception;
         if (this == tagged.getTag()) {
            throw tagged.getCause();
         }
      }

   }

   protected void handleException(SAXException e) throws SAXException {
      throw new TaggedSAXException(e, this);
   }
}
