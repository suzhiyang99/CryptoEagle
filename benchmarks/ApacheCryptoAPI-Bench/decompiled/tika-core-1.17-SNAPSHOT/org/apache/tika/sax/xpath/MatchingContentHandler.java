package org.apache.tika.sax.xpath;

import java.util.LinkedList;
import org.apache.tika.sax.ContentHandlerDecorator;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

public class MatchingContentHandler extends ContentHandlerDecorator {
   private final LinkedList<Matcher> matchers = new LinkedList();
   private Matcher matcher;

   public MatchingContentHandler(ContentHandler delegate, Matcher matcher) {
      super(delegate);
      this.matcher = matcher;
   }

   public void startElement(String uri, String localName, String name, Attributes attributes) throws SAXException {
      this.matchers.addFirst(this.matcher);
      this.matcher = this.matcher.descend(uri, localName);
      AttributesImpl matches = new AttributesImpl();

      for(int i = 0; i < attributes.getLength(); ++i) {
         String attributeURI = attributes.getURI(i);
         String attributeName = attributes.getLocalName(i);
         if (this.matcher.matchesAttribute(attributeURI, attributeName)) {
            matches.addAttribute(attributeURI, attributeName, attributes.getQName(i), attributes.getType(i), attributes.getValue(i));
         }
      }

      if (this.matcher.matchesElement() || matches.getLength() > 0) {
         super.startElement(uri, localName, name, matches);
         if (!this.matcher.matchesElement()) {
            this.matcher = new CompositeMatcher(this.matcher, ElementMatcher.INSTANCE);
         }
      }

   }

   public void endElement(String uri, String localName, String name) throws SAXException {
      if (this.matcher.matchesElement()) {
         super.endElement(uri, localName, name);
      }

      if (!this.matchers.isEmpty()) {
         this.matcher = (Matcher)this.matchers.removeFirst();
      }

   }

   public void characters(char[] ch, int start, int length) throws SAXException {
      if (this.matcher.matchesText()) {
         super.characters(ch, start, length);
      }

   }

   public void ignorableWhitespace(char[] ch, int start, int length) throws SAXException {
      if (this.matcher.matchesText()) {
         super.ignorableWhitespace(ch, start, length);
      }

   }

   public void processingInstruction(String target, String data) {
   }

   public void skippedEntity(String name) throws SAXException {
      if (this.matcher.matchesText()) {
         super.skippedEntity(name);
      }

   }
}
