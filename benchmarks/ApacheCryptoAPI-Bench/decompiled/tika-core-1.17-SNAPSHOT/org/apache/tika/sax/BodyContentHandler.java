package org.apache.tika.sax;

import java.io.OutputStream;
import java.io.Writer;
import org.apache.tika.sax.xpath.Matcher;
import org.apache.tika.sax.xpath.MatchingContentHandler;
import org.apache.tika.sax.xpath.XPathParser;
import org.xml.sax.ContentHandler;

public class BodyContentHandler extends ContentHandlerDecorator {
   private static final XPathParser PARSER = new XPathParser("xhtml", "http://www.w3.org/1999/xhtml");
   private static final Matcher MATCHER;

   public BodyContentHandler(ContentHandler handler) {
      super(new MatchingContentHandler(handler, MATCHER));
   }

   public BodyContentHandler(Writer writer) {
      this((ContentHandler)(new WriteOutContentHandler(writer)));
   }

   public BodyContentHandler(OutputStream stream) {
      this((ContentHandler)(new WriteOutContentHandler(stream)));
   }

   public BodyContentHandler(int writeLimit) {
      this((ContentHandler)(new WriteOutContentHandler(writeLimit)));
   }

   public BodyContentHandler() {
      this((ContentHandler)(new WriteOutContentHandler()));
   }

   static {
      MATCHER = PARSER.parse("/xhtml:html/xhtml:body/descendant::node()");
   }
}
