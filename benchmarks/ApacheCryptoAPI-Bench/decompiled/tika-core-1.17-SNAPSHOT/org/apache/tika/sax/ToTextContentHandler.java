package org.apache.tika.sax;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.nio.charset.Charset;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class ToTextContentHandler extends DefaultHandler {
   private final Writer writer;

   public ToTextContentHandler(Writer writer) {
      this.writer = writer;
   }

   public ToTextContentHandler(OutputStream stream) {
      this((Writer)(new OutputStreamWriter(stream, Charset.defaultCharset())));
   }

   public ToTextContentHandler(OutputStream stream, String encoding) throws UnsupportedEncodingException {
      this((Writer)(new OutputStreamWriter(stream, encoding)));
   }

   public ToTextContentHandler() {
      this((Writer)(new StringWriter()));
   }

   public void characters(char[] ch, int start, int length) throws SAXException {
      try {
         this.writer.write(ch, start, length);
      } catch (IOException e) {
         throw new SAXException("Error writing: " + new String(ch, start, length), e);
      }
   }

   public void ignorableWhitespace(char[] ch, int start, int length) throws SAXException {
      this.characters(ch, start, length);
   }

   public void endDocument() throws SAXException {
      try {
         this.writer.flush();
      } catch (IOException e) {
         throw new SAXException("Error flushing character output", e);
      }
   }

   public String toString() {
      return this.writer.toString();
   }
}
