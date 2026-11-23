package org.apache.tika.sax;

import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Serializable;
import java.io.StringWriter;
import java.io.Writer;
import java.nio.charset.Charset;
import java.util.UUID;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;

public class WriteOutContentHandler extends ContentHandlerDecorator {
   private final Serializable tag;
   private final int writeLimit;
   private int writeCount;

   public WriteOutContentHandler(ContentHandler handler, int writeLimit) {
      super(handler);
      this.tag = UUID.randomUUID();
      this.writeCount = 0;
      this.writeLimit = writeLimit;
   }

   public WriteOutContentHandler(Writer writer, int writeLimit) {
      this((ContentHandler)(new ToTextContentHandler(writer)), writeLimit);
   }

   public WriteOutContentHandler(Writer writer) {
      this((Writer)writer, -1);
   }

   public WriteOutContentHandler(OutputStream stream) {
      this((Writer)(new OutputStreamWriter(stream, Charset.defaultCharset())));
   }

   public WriteOutContentHandler(int writeLimit) {
      this((Writer)(new StringWriter()), writeLimit);
   }

   public WriteOutContentHandler() {
      this(100000);
   }

   public void characters(char[] ch, int start, int length) throws SAXException {
      if (this.writeLimit != -1 && this.writeCount + length > this.writeLimit) {
         super.characters(ch, start, this.writeLimit - this.writeCount);
         this.writeCount = this.writeLimit;
         throw new WriteLimitReachedException("Your document contained more than " + this.writeLimit + " characters, and so your requested limit has been reached. To receive the full text of the document, increase your limit. (Text up to the limit is however available).", this.tag);
      } else {
         super.characters(ch, start, length);
         this.writeCount += length;
      }
   }

   public void ignorableWhitespace(char[] ch, int start, int length) throws SAXException {
      if (this.writeLimit != -1 && this.writeCount + length > this.writeLimit) {
         super.ignorableWhitespace(ch, start, this.writeLimit - this.writeCount);
         this.writeCount = this.writeLimit;
         throw new WriteLimitReachedException("Your document contained more than " + this.writeLimit + " characters, and so your requested limit has been reached. To receive the full text of the document, increase your limit. (Text up to the limit is however available).", this.tag);
      } else {
         super.ignorableWhitespace(ch, start, length);
         this.writeCount += length;
      }
   }

   public boolean isWriteLimitReached(Throwable t) {
      if (t instanceof WriteLimitReachedException) {
         return this.tag.equals(((WriteLimitReachedException)t).tag);
      } else {
         return t.getCause() != null && this.isWriteLimitReached(t.getCause());
      }
   }

   private static class WriteLimitReachedException extends SAXException {
      private static final long serialVersionUID = -1850581945459429943L;
      private final Serializable tag;

      public WriteLimitReachedException(String message, Serializable tag) {
         super(message);
         this.tag = tag;
      }
   }
}
