package org.apache.tika.sax;

import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

public class SafeContentHandler extends ContentHandlerDecorator {
   private static final char[] REPLACEMENT = new char[]{'�'};
   private final Output charactersOutput = new Output() {
      public void write(char[] ch, int start, int length) throws SAXException {
         SafeContentHandler.super.characters(ch, start, length);
      }
   };
   private final Output ignorableWhitespaceOutput = new Output() {
      public void write(char[] ch, int start, int length) throws SAXException {
         SafeContentHandler.super.ignorableWhitespace(ch, start, length);
      }
   };

   public SafeContentHandler(ContentHandler handler) {
      super(handler);
   }

   private void filter(char[] ch, int start, int length, Output output) throws SAXException {
      int end = start + length;

      int j;
      for(int i = start; i < end; i = j) {
         int c = Character.codePointAt(ch, i, end);
         j = i + Character.charCount(c);
         if (this.isInvalid(c)) {
            if (i > start) {
               output.write(ch, start, i - start);
            }

            this.writeReplacement(output);
            start = j;
         }
      }

      output.write(ch, start, end - start);
   }

   private boolean isInvalid(String value) {
      char[] ch = value.toCharArray();

      int c;
      for(int i = 0; i < ch.length; i += Character.charCount(c)) {
         c = Character.codePointAt(ch, i);
         if (this.isInvalid(c)) {
            return true;
         }
      }

      return false;
   }

   protected boolean isInvalid(int ch) {
      if (ch >= 32) {
         if (ch < 57344) {
            return ch > 55295;
         } else if (ch < 65536) {
            return ch > 65533;
         } else {
            return ch > 1114111;
         }
      } else {
         return ch != 9 && ch != 10 && ch != 13;
      }
   }

   protected void writeReplacement(Output output) throws SAXException {
      output.write(REPLACEMENT, 0, REPLACEMENT.length);
   }

   public void startElement(String uri, String localName, String name, Attributes atts) throws SAXException {
      for(int i = 0; i < atts.getLength(); ++i) {
         if (this.isInvalid(atts.getValue(i))) {
            AttributesImpl filtered = new AttributesImpl();

            for(int j = 0; j < atts.getLength(); ++j) {
               String value = atts.getValue(j);
               if (j >= i && this.isInvalid(value)) {
                  Output buffer = new StringOutput();
                  this.filter(value.toCharArray(), 0, value.length(), buffer);
                  value = buffer.toString();
               }

               filtered.addAttribute(atts.getURI(j), atts.getLocalName(j), atts.getQName(j), atts.getType(j), value);
            }

            atts = filtered;
            break;
         }
      }

      super.startElement(uri, localName, name, atts);
   }

   public void endElement(String uri, String localName, String name) throws SAXException {
      super.endElement(uri, localName, name);
   }

   public void endDocument() throws SAXException {
      super.endDocument();
   }

   public void characters(char[] ch, int start, int length) throws SAXException {
      this.filter(ch, start, length, this.charactersOutput);
   }

   public void ignorableWhitespace(char[] ch, int start, int length) throws SAXException {
      this.filter(ch, start, length, this.ignorableWhitespaceOutput);
   }

   private static class StringOutput implements Output {
      private final StringBuilder builder;

      private StringOutput() {
         this.builder = new StringBuilder();
      }

      public void write(char[] ch, int start, int length) {
         this.builder.append(ch, start, length);
      }

      public String toString() {
         return this.builder.toString();
      }
   }

   protected interface Output {
      void write(char[] var1, int var2, int var3) throws SAXException;
   }
}
