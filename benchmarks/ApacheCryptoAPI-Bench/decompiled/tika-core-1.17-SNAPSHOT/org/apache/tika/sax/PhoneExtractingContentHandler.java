package org.apache.tika.sax;

import java.util.Arrays;
import org.apache.tika.metadata.Metadata;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class PhoneExtractingContentHandler extends ContentHandlerDecorator {
   private Metadata metadata;
   private static final String PHONE_NUMBERS = "phonenumbers";
   private StringBuilder stringBuilder;

   public PhoneExtractingContentHandler(ContentHandler handler, Metadata metadata) {
      super(handler);
      this.metadata = metadata;
      this.stringBuilder = new StringBuilder();
   }

   protected PhoneExtractingContentHandler() {
      this(new DefaultHandler(), new Metadata());
   }

   public void characters(char[] ch, int start, int length) throws SAXException {
      try {
         String text = new String(Arrays.copyOfRange(ch, start, start + length));
         this.stringBuilder.append(text);
         super.characters(ch, start, length);
      } catch (SAXException e) {
         this.handleException(e);
      }

   }

   public void endDocument() throws SAXException {
      super.endDocument();

      for(String number : CleanPhoneText.extractPhoneNumbers(this.stringBuilder.toString())) {
         this.metadata.add("phonenumbers", number);
      }

   }
}
