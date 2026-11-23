package org.apache.tika.sax;

import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.Locale;
import org.xml.sax.ContentHandler;
import org.xml.sax.helpers.DefaultHandler;

public class BasicContentHandlerFactory implements ContentHandlerFactory {
   private final HANDLER_TYPE type;
   private final int writeLimit;

   public static HANDLER_TYPE parseHandlerType(String handlerTypeName, HANDLER_TYPE defaultType) {
      if (handlerTypeName == null) {
         return defaultType;
      } else {
         switch (handlerTypeName.toLowerCase(Locale.ROOT)) {
            case "xml":
               return BasicContentHandlerFactory.HANDLER_TYPE.XML;
            case "text":
               return BasicContentHandlerFactory.HANDLER_TYPE.TEXT;
            case "txt":
               return BasicContentHandlerFactory.HANDLER_TYPE.TEXT;
            case "html":
               return BasicContentHandlerFactory.HANDLER_TYPE.HTML;
            case "body":
               return BasicContentHandlerFactory.HANDLER_TYPE.BODY;
            case "ignore":
               return BasicContentHandlerFactory.HANDLER_TYPE.IGNORE;
            default:
               return defaultType;
         }
      }
   }

   public BasicContentHandlerFactory(HANDLER_TYPE type, int writeLimit) {
      this.type = type;
      this.writeLimit = writeLimit;
   }

   public ContentHandler getNewContentHandler() {
      if (this.type == BasicContentHandlerFactory.HANDLER_TYPE.BODY) {
         return new BodyContentHandler(this.writeLimit);
      } else if (this.type == BasicContentHandlerFactory.HANDLER_TYPE.IGNORE) {
         return new DefaultHandler();
      } else if (this.writeLimit > -1) {
         switch (this.type) {
            case TEXT:
               return new WriteOutContentHandler(new ToTextContentHandler(), this.writeLimit);
            case HTML:
               return new WriteOutContentHandler(new ToHTMLContentHandler(), this.writeLimit);
            case XML:
               return new WriteOutContentHandler(new ToXMLContentHandler(), this.writeLimit);
            default:
               return new WriteOutContentHandler(new ToTextContentHandler(), this.writeLimit);
         }
      } else {
         switch (this.type) {
            case TEXT:
               return new ToTextContentHandler();
            case HTML:
               return new ToHTMLContentHandler();
            case XML:
               return new ToXMLContentHandler();
            default:
               return new ToTextContentHandler();
         }
      }
   }

   public ContentHandler getNewContentHandler(OutputStream os, String encoding) throws UnsupportedEncodingException {
      if (this.type == BasicContentHandlerFactory.HANDLER_TYPE.IGNORE) {
         return new DefaultHandler();
      } else if (this.writeLimit > -1) {
         switch (this.type) {
            case TEXT:
               return new WriteOutContentHandler(new ToTextContentHandler(os, encoding), this.writeLimit);
            case HTML:
               return new WriteOutContentHandler(new ToHTMLContentHandler(os, encoding), this.writeLimit);
            case XML:
               return new WriteOutContentHandler(new ToXMLContentHandler(os, encoding), this.writeLimit);
            case BODY:
               return new WriteOutContentHandler(new BodyContentHandler(new OutputStreamWriter(os, encoding)), this.writeLimit);
            default:
               return new WriteOutContentHandler(new ToTextContentHandler(os, encoding), this.writeLimit);
         }
      } else {
         switch (this.type) {
            case TEXT:
               return new ToTextContentHandler(os, encoding);
            case HTML:
               return new ToHTMLContentHandler(os, encoding);
            case XML:
               return new ToXMLContentHandler(os, encoding);
            case BODY:
               return new BodyContentHandler(new OutputStreamWriter(os, encoding));
            default:
               return new ToTextContentHandler(os, encoding);
         }
      }
   }

   public HANDLER_TYPE getType() {
      return this.type;
   }

   public static enum HANDLER_TYPE {
      BODY,
      IGNORE,
      TEXT,
      HTML,
      XML;
   }
}
