package org.apache.tika.sax;

import org.xml.sax.SAXException;

public class TaggedSAXException extends SAXException {
   private final Object tag;

   public TaggedSAXException(SAXException original, Object tag) {
      super(original.getMessage(), original);
      this.initCause(original);
      this.tag = tag;
   }

   public Object getTag() {
      return this.tag;
   }

   public SAXException getCause() {
      return (SAXException)super.getCause();
   }
}
