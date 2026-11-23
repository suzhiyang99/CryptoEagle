package org.apache.tika.sax;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.metadata.TikaCoreProperties;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

public class XHTMLContentHandler extends SafeContentHandler {
   public static final String XHTML = "http://www.w3.org/1999/xhtml";
   private static final char[] NL = new char[]{'\n'};
   private static final char[] TAB = new char[]{'\t'};
   private static final Set<String> HEAD = unmodifiableSet("title", "link", "base", "meta", "script");
   private static final Set<String> AUTO = unmodifiableSet("head", "frameset");
   private static final Set<String> INDENT = unmodifiableSet("li", "dd", "dt", "td", "th", "frame");
   public static final Set<String> ENDLINE = unmodifiableSet("p", "h1", "h2", "h3", "h4", "h5", "h6", "div", "ul", "ol", "dl", "pre", "hr", "blockquote", "address", "fieldset", "table", "form", "noscript", "li", "dt", "dd", "noframes", "br", "tr", "select", "option", "link", "script");
   private static final Attributes EMPTY_ATTRIBUTES = new AttributesImpl();
   private final Metadata metadata;
   private boolean documentStarted = false;
   private boolean headStarted = false;
   private boolean headEnded = false;
   private boolean useFrameset = false;

   private static Set<String> unmodifiableSet(String... elements) {
      return Collections.unmodifiableSet(new HashSet(Arrays.asList(elements)));
   }

   public XHTMLContentHandler(ContentHandler handler, Metadata metadata) {
      super(handler);
      this.metadata = metadata;
   }

   public void startDocument() throws SAXException {
      if (!this.documentStarted) {
         this.documentStarted = true;
         super.startDocument();
         this.startPrefixMapping("", "http://www.w3.org/1999/xhtml");
      }

   }

   private void lazyStartHead() throws SAXException {
      if (!this.headStarted) {
         this.headStarted = true;
         super.startElement("http://www.w3.org/1999/xhtml", "html", "html", EMPTY_ATTRIBUTES);
         this.newline();
         super.startElement("http://www.w3.org/1999/xhtml", "head", "head", EMPTY_ATTRIBUTES);
         this.newline();
      }

   }

   private void lazyEndHead(boolean isFrameset) throws SAXException {
      this.lazyStartHead();
      if (!this.headEnded) {
         this.headEnded = true;
         this.useFrameset = isFrameset;

         for(String name : this.metadata.names()) {
            if (!name.equals("title")) {
               for(String value : this.metadata.getValues(name)) {
                  if (value != null) {
                     AttributesImpl attributes = new AttributesImpl();
                     attributes.addAttribute("", "name", "name", "CDATA", name);
                     attributes.addAttribute("", "content", "content", "CDATA", value);
                     super.startElement("http://www.w3.org/1999/xhtml", "meta", "meta", attributes);
                     super.endElement("http://www.w3.org/1999/xhtml", "meta", "meta");
                     this.newline();
                  }
               }
            }
         }

         super.startElement("http://www.w3.org/1999/xhtml", "title", "title", EMPTY_ATTRIBUTES);
         String title = this.metadata.get(TikaCoreProperties.TITLE);
         if (title != null && title.length() > 0) {
            char[] titleChars = title.toCharArray();
            super.characters(titleChars, 0, titleChars.length);
         } else {
            super.characters(new char[0], 0, 0);
         }

         super.endElement("http://www.w3.org/1999/xhtml", "title", "title");
         this.newline();
         super.endElement("http://www.w3.org/1999/xhtml", "head", "head");
         this.newline();
         if (this.useFrameset) {
            super.startElement("http://www.w3.org/1999/xhtml", "frameset", "frameset", EMPTY_ATTRIBUTES);
         } else {
            super.startElement("http://www.w3.org/1999/xhtml", "body", "body", EMPTY_ATTRIBUTES);
         }
      }

   }

   public void endDocument() throws SAXException {
      this.lazyEndHead(this.useFrameset);
      if (this.useFrameset) {
         super.endElement("http://www.w3.org/1999/xhtml", "frameset", "frameset");
      } else {
         super.endElement("http://www.w3.org/1999/xhtml", "body", "body");
      }

      super.endElement("http://www.w3.org/1999/xhtml", "html", "html");
      this.endPrefixMapping("");
      super.endDocument();
   }

   public void startElement(String uri, String local, String name, Attributes attributes) throws SAXException {
      if (name.equals("frameset")) {
         this.lazyEndHead(true);
      } else if (!AUTO.contains(name)) {
         if (HEAD.contains(name)) {
            this.lazyStartHead();
         } else {
            this.lazyEndHead(false);
         }

         if ("http://www.w3.org/1999/xhtml".equals(uri) && INDENT.contains(name)) {
            this.ignorableWhitespace(TAB, 0, TAB.length);
         }

         super.startElement(uri, local, name, attributes);
      }

   }

   public void endElement(String uri, String local, String name) throws SAXException {
      if (!AUTO.contains(name)) {
         super.endElement(uri, local, name);
         if ("http://www.w3.org/1999/xhtml".equals(uri) && ENDLINE.contains(name)) {
            this.newline();
         }
      }

   }

   public void characters(char[] ch, int start, int length) throws SAXException {
      this.lazyEndHead(this.useFrameset);
      super.characters(ch, start, length);
   }

   public void startElement(String name) throws SAXException {
      this.startElement("http://www.w3.org/1999/xhtml", name, name, EMPTY_ATTRIBUTES);
   }

   public void startElement(String name, String attribute, String value) throws SAXException {
      AttributesImpl attributes = new AttributesImpl();
      attributes.addAttribute("", attribute, attribute, "CDATA", value);
      this.startElement("http://www.w3.org/1999/xhtml", name, name, attributes);
   }

   public void startElement(String name, AttributesImpl attributes) throws SAXException {
      this.startElement("http://www.w3.org/1999/xhtml", name, name, attributes);
   }

   public void endElement(String name) throws SAXException {
      this.endElement("http://www.w3.org/1999/xhtml", name, name);
   }

   public void characters(String characters) throws SAXException {
      if (characters != null && characters.length() > 0) {
         this.characters(characters.toCharArray(), 0, characters.length());
      }

   }

   public void newline() throws SAXException {
      this.ignorableWhitespace(NL, 0, NL.length);
   }

   public void element(String name, String value) throws SAXException {
      if (value != null && value.length() > 0) {
         this.startElement(name);
         this.characters(value);
         this.endElement(name);
      }

   }
}
